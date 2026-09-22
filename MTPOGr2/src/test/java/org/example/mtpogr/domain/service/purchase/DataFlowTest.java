package org.example.mtpogr.domain.service.purchase;

import org.example.mtpogr.domain.statemachine.entity.OrderMtMProduct;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataFlowTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;

    @Mock private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
    @Mock private StateMachinePersister<OrderState, OrderEvent, Long> persister;
    @Mock private StateMachine<OrderState, OrderEvent> stateMachine;
    @Mock
    private org.springframework.statemachine.state.State<OrderState, OrderEvent> state;
    @Mock private SortService SortService;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<Order> orderCaptor;



    // Поток: def product.price -> use Product.setPriceAtOrder(price)
    //
    // Цена фиксируется в момент создания позиции заказа.
    // Если цена товара изменится позже — это не должно влиять на заказ.
    @Test
    void createOrderPriceNotChanging() {

        Product product = buildProduct(1L, "Ноутбук",75000.00, 5);


        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(List.of(1L));

        // def product.price=75000 -> use Product.setPriceAtOrder(75000)
        OrderMtMProduct Product = result.getProducts().get(0);
        assertEquals(75000.00, Product.getPriceAtOrder(),
                "Цена из каталога должна быть зафиксирована в позиции заказа");
    }

    @Test
    void createOrderWithManyProducts() {

        Product p1 = buildProduct(1L, "Клавиатура", 3000.0, 2);
        Product p2 = buildProduct(2L, "Монитор", 25000.0, 1);


        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(List.of(1L, 2L));

        // def productIds=[1,2] -> def Products=[Product1,Product2] -> use order.setProducts
        assertEquals(2, result.getProducts().size());
        assertEquals(3000.0,
                result.getProducts().get(0).getPriceAtOrder());
        assertEquals(25000.0,
                result.getProducts().get(1).getPriceAtOrder());
    }

    // Поток: def order (после save) -> use return savedOrder
    //
    // Метод возвращает именно тот объект, который вернул репозиторий,
    // а не промежуточную переменную.
    @Test
    void createOrderExactlyThatOne() {

        Order savedOrder = new Order();
        savedOrder.setId(100L); // репозиторий присвоил id
        savedOrder.setState(OrderState.NEW);

        when(orderRepository.save(any())).thenReturn(savedOrder);

        Order result = orderService.createOrder(List.of());

        // def savedOrder (из repository.save) -> use return
        assertSame(savedOrder, result,
                "Должен вернуться именно тот объект, который сохранил репозиторий");
    }

    // Поток: def event -> use stateMachine.sendEvent(event) -> def accepted
    //        -> use if(!accepted) -> ветка false: throw
    //        -> ветка true: continue
    //
    // Заменяет два Branch-теста одним потоком с двумя путями
    @Test
    void applyEventGoodPath() throws Exception {
        Order order = buildOrder(1L, OrderState.NEW, false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.sendEvent(OrderEvent.CONFIRM)).thenReturn(true);
        //  when(stateMachine.getState()).thenReturn(state);
        when(stateMachine.getState()).thenReturn(state);
        when(state.getId()).thenReturn(OrderState.CONFIRMED);

        // def event=CONFIRM -> use sendEvent(CONFIRM) -> def accepted=true
        // -> use !accepted = false -> не бросаем исключение -> продолжаем
        assertDoesNotThrow(() -> orderService.applyEvent(1L, OrderEvent.CONFIRM));
        verify(persister).persist(stateMachine, 1L); // поток дошёл до persist
    }

    @Test
    void applyEventBadPAth() throws Exception {
        Order order = buildOrder(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.sendEvent(OrderEvent.PAY)).thenReturn(false);

        // def event=PAY -> use sendEvent(PAY) -> def accepted=false
        // -> use !accepted = true -> бросаем IllegalStateException
        assertThrows(IllegalStateException.class,
                () -> orderService.applyEvent(1L, OrderEvent.PAY));
        verify(persister, never()).persist(any(), any()); // поток прерван
    }

    // Поток: def stateMachine.state (после события) ->
    //        use order.setState(state) -> use orderRepository.save(order)
    //        -> use return order.getState()
    //
    // Новое состояние автомата проходит в order и возвращается вызывающему.
    @Test
    void applyEventWayOfNewState() throws Exception {
        Order order = buildOrder(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
        when(stateMachine.sendEvent(OrderEvent.CONFIRM)).thenReturn(true);
        when(stateMachine.getState()).thenReturn(state);
        when(state.getId()).thenReturn(OrderState.CONFIRMED);
        // when(((org.springframework.statemachine.state.State<?, ?>) state).getId()).thenReturn(OrderState.CONFIRMED);
        when(orderRepository.save(any())).thenAnswer(inv -> {
            // имитируем сохранение — объект возвращается как есть
            return inv.getArgument(0);
        });

        OrderState result = orderService.applyEvent(1L, OrderEvent.CONFIRM);

        // def state=CONFIRMED (из автомата) ->
        // use order.setState(CONFIRMED) -> use return order.getState()
        assertEquals(OrderState.CONFIRMED, order.getState(),
                "Состояние должно записаться в объект заказа");
        assertEquals(OrderState.CONFIRMED, result,
                "Метод должен вернуть новое состояние");
    }


    private Product buildProduct(Long id, String name, double price, int Amount) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        p.setAmount(Amount);
        return p;
    }

    private Order buildOrder(Long id, OrderState state, boolean locked) {
        Order o = new Order();
        o.setId(id);
        o.setState(state);
        return o;
    }

    @ExtendWith(MockitoExtension.class)

    @InjectMocks
    private ProductService productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;


    // Поток: def updated.name -> use existing.setName() -> use save(existing)
    //
    // Проверяем, что имя из updated реально перезаписывает имя existing,
    // и в репозиторий уходит именно обновлённый объект.
    @Test
    void updateName() {
        Product existing = buildProduct("Старое название", 1000, 5);
        Product updated = buildProduct("Новое название", 1000, 5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        productService.update(1L, updated);

        // def updated.name="Новое название" -> use existing.setName("Новое название")
        // -> use save(existing) — проверяем что save получил обновлённый объект
        verify(productRepository).save(productCaptor.capture());
        assertEquals("Новое название", productCaptor.getValue().getName());
    }

    // Поток: def updated.price -> use existing.setPrice() -> use validateProduct()
    //
    // Новая цена проходит в existing, потом через валидацию.
    // Проверяем оба конца потока: цена записалась И прошла валидацию (save вызван).
    @Test
    void updateThePrice() {
        Product existing = buildProduct("Товар", 500, 3);
        Product updated = buildProduct("Товар", 999.99, 3);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        productService.update(1L, updated);

        verify(productRepository).save(productCaptor.capture());
        // def updated.price=999.99 -> use existing.setPrice(999.99) -> validateProduct -> save
        assertEquals(999.99, productCaptor.getValue().getPrice());
    }

    // Поток: def updated.price (отрицательная) -> use existing.setPrice()
    //        -> use validateProduct() -> выброс исключения -> save НЕ вызван
    //
    // Поток обрывается на валидации — данные не доходят до репозитория.
//    @Test
//    void update_invalidPrice_flowBreaksAtValidation_saveNotCalled() {
//        Product existing = buildProduct("Товар", 500, 3);
//        Product updated = buildProduct("Товар", -1, 3);
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
//
//        // def updated.price=-1 -> use existing.setPrice(-1)
//        // -> use validateProduct(existing) -> исключение, поток обрывается
//        assertThrows(IllegalArgumentException.class,
//                () -> productService.update(1L, updated));
//        verify(productRepository, never()).save(any());
//    }

    // Поток: def updated.Amount -> use existing.setAmount() -> use save(existing)
    //
    // Остаток перезаписывается и сохраняется.
    @Test
    void updateAmount() {
        Product existing = buildProduct("Товар", 100, 0);
        Product updated = buildProduct("Товар", 100, 50);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        productService.update(1L, updated);

        verify(productRepository).save(productCaptor.capture());
        // def updated.Amount=50 -> use existing.setAmount(50) -> save
        assertEquals(50, productCaptor.getValue().getAmount());
    }

    // Поток: def existing (результат getById) -> use save(existing)
    //
    // В репозиторий должен уйти именно тот объект, который был получен из БД,
    // а не копия и не объект updated.
    @Test
    void updateBDGotExactlyOneObject() {
        Product existing = buildProduct("Товар", 100, 5);
        Product updated = buildProduct("Товар", 200, 10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        productService.update(1L, updated);

        // def existing (из findById) -> поля обновляются на месте -> use save(existing)
        // save должен получить именно existing, а не updated
        verify(productRepository).save(same(existing));
    }


    // Проверяем, что один и тот же объект проходит валидацию и сохранение,
    // и он же возвращается из метода.
    @Test
    void createOrderWitchisReturns() {
        Product input = buildProduct("Принтер", 12000, 3);
        Product saved = buildProduct("Принтер", 12000, 3);
        saved.setId(10L); // репозиторий присвоил id

        when(productRepository.save(input)).thenReturn(saved);

        Product result = productService.create(input);

        // def input -> use validateProduct(input) -> use save(input)
        // -> def saved -> use return saved
        verify(productRepository).save(same(input)); // именно input, не копия
        assertSame(saved, result, "Должен вернуться объект из репозитория");
    }

    // Вспомогательный метод
    private Product buildProduct(String name, double price, Integer Amount) {
        Product p = new Product();
        p.setId(1L);
        p.setName(name);
        p.setPrice(price);
        p.setAmount(Amount);
        return p;
    }



    private final SortService sortService = new SortService();


    @Test
    void sortByIdButresultContainsSameObjects() {
        Order o1 = buildOrder(2L);
        Order o2 = buildOrder(1L);
        List<Order> list = new ArrayList<>(List.of(o1, o2));

        List<Order> result = sortService.sortById(list);

        // def result -> use return — проверяем identity (assertSame), не equality
        assertSame(o2, result.get(0), "Первый элемент должен быть тем же объектом o2");
        assertSame(o1, result.get(1), "Второй элемент должен быть тем же объектом o1");



        Product p1 = buildProduct(2L);
        Product p2 = buildProduct(1L);
        List<Product> listP = new ArrayList<>(List.of(p1, p2));

        List<Product> resultP = sortService.sortByIdP(listP);

        // def result -> use return — проверяем identity (assertSame), не equality
        assertSame(p2, resultP.get(0), "Первый элемент должен быть тем же объектом o2");
        assertSame(p1, resultP.get(1), "Второй элемент должен быть тем же объектом o1");


    }

    // Вспомогательный метод
    private Order buildOrder(Long id) {
        Order o = new Order();
        o.setId(id);
        o.setState(OrderState.NEW);
        return o;
    }
    private Product buildProduct(Long id) {
        Product p = new Product();
        p.setId(id);
        p.setName("test_" + id.toString());
        p.setPrice(1.0);
        p.setAmount(1);
        return p;
    }
}