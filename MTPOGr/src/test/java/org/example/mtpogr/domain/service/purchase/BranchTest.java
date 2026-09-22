package org.example.mtpogr.domain.service.purchase;

import org.example.mtpogr.domain.statemachine.entity.OrderMtMProduct;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchTest {
    //@Mock
//    OrderController orderController;
    @Mock
    private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    @Mock
    private StateMachinePersister<OrderState, OrderEvent, Long> persister;

    @Mock
    private OrderRepository orderRepository;

    @Spy
    private SortService sortService;

    @Mock
    private StateMachine stateMachine;

    @Mock
    private State state;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<OrderState> captr;

//    @Test
//    void GoodApplyEvent() throws Exception {
//        Order order = new Order();
//        order.setId(1L);
//        when(orderRepository .findById(1L)).thenReturn(Optional.of(order));
//        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
//        when(stateMachine.getState()).thenReturn(state);
//        when(state.getId()).thenReturn(OrderState.CONFIRMED);
//        when(stateMachine.sendEvent(OrderEvent.CONFIRM)).thenReturn(true);
//        //when(persister.restore(stateMachine,1L)).thenReturn(stateMachine);
//
//        OrderState appev = orderService.applyEvent(1L, OrderEvent.CONFIRM);
//
//        assertEquals(appev,OrderState.CONFIRMED);
//        verify(persister).restore(stateMachine,1L);
//        verify(persister).persist(stateMachine,1L);
//        assertEquals(order.getState(),OrderState.CONFIRMED);
//        verify(orderRepository).save(order);
//    }
//
//    @Test
//    void BadApplyEvent() throws Exception{
//        Order order = new Order();
//        order.setId(1L);
//        when(orderRepository .findById(1L)).thenReturn(Optional.of(order));
//        when(stateMachineFactory.getStateMachine()).thenReturn(stateMachine);
//        when(stateMachine.sendEvent(OrderEvent.DELIVER)).thenReturn(false);
//        //when(persister.restore(stateMachine,1L)).thenReturn(stateMachine);
//
//        //OrderState appev = orderService.applyEvent(1L, OrderEvent.DELIVER);
//        assertThrows(IllegalStateException.class, () -> orderService.applyEvent(1L, OrderEvent.DELIVER));
//        //assertNotEquals(order.getState(),OrderState.CONFIRMED);
//        verify(persister).restore(stateMachine,1L);
//
//       // verify(orderRepository).save(order);
//    }

    @Test
    void ThereIsNowhereToApplyEvents() throws Exception{
        Order order = new Order();
        order.setId(2L);
        when(orderRepository.findById(2L)).thenReturn(Optional.empty());
        // OrderState appev = orderService.applyEvent(2L, OrderEvent.CONFIRM);
        assertThrows(NoSuchElementException.class, () -> orderService.applyEvent(2L, OrderEvent.CONFIRM));

    }

    @Test
    void ThereIsNoEventsToApply() throws Exception{
        //when(persister.restore(stateMachine,1L)).thenReturn(stateMachine);

        //OrderState appev = orderService.applyEvent(1L, null);
        assertThrows(IllegalArgumentException.class, () -> orderService.applyEvent(1L, null));
    }



    //ProductService тесты

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

//    @Test
//    void GoodGetById() {
//        Product p = buildProduct(1L, "Мышь", 5000, 20);
//
//        doReturn(Optional.of(p)).when(productRepository).findById(1L);
//
//        Product result = productService.getById(1L);
//
//        assertEquals(p, result);
//    }
//
//    @Test
//    void NotGetById() {
//        when(productRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class, () -> productService.getById(99L));
//    }


    private Product buildProduct(Long id, String name, double price, int amount) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        p.setAmount(amount);
        return p;
    }


    @Test
    void Validupdate() {
        Product existing = buildProduct(1L, "Старое имя", 100, 5);
        Product updated = buildProduct(1L,"Новое имя", 200, 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any())).thenReturn(existing);

        Product result = productService.update(1L, updated);

        assertEquals("Новое имя", existing.getName());
        assertEquals( 200, existing.getPrice());
        verify(productRepository).save(existing);
    }

    @Test
    void ThereIsNothingToUpdate() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> productService.update(99L, buildProduct(99L, "X",1, 1)));
        verify(productRepository, never()).save(any());
    }

    // Branch: товар существует, но новая цена отрицательная
    @Test
    void UpdateButWrongPrice() {
        Product existing = buildProduct(1L, "Ноутбук", 100, 5);
        Product updated = buildProduct(1L, "Ноутбук", -50, 5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(IllegalArgumentException.class, () -> productService.update(1L, updated));
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteExistingProduct() {
        Product p = buildProduct(1L,"Клавиатура", 3000, 7);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    //   @Test
//    void deleteProdctwhichNotExists() {
//        when(productRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class, () -> productService.delete(99L));
//        verify(productRepository, never()).deleteById(any());
//    }


    //sortService
    @Test
    void sortButItsAboutLeft() {
        List<Order> list = new ArrayList<>(List.of(
                buildOrder(5L), buildOrder(3L), buildOrder(1L)
        ));

        List<Order> result = sortService.sortById(list);

        assertEquals(3, result.size());
        assertEquals(List.of(1L, 3L, 5L),
                result.stream().map(Order::getId).toList());
    }



    @Test
    void sortButItsAboutRight() {
        List<Product> list = new ArrayList<>(List.of(
                buildProduct(1L, "A",50,1),
                buildProduct(3L, "B",60,1),
                buildProduct(4L, "C",70,1),
                buildProduct(2L, "D",80,1)
        ));

        List<Product> result = sortService.sortByIdP(list);

        assertEquals(4, result.size());
        assertEquals(List.of(1L, 2L, 3L, 4L),
                result.stream().map(Product::getId).toList());
    }

    @Test
    void sortPButItsAboutLeft() {
        List<Product> list = new ArrayList<>(List.of(
                buildProduct(5L, "A",50,1),
                buildProduct(3L, "B",60,1),
                buildProduct(1L,"C",70,1)
        ));

        List<Product> result = sortService.sortByIdP(list);

        assertEquals(3, result.size());
        assertEquals(List.of(1L, 3L, 5L),
                result.stream().map(Product::getId).toList());
    }



    @Test
    void sortPButItsAboutRight() {
        List<Order> list = new ArrayList<>(List.of(
                buildOrder(1L), buildOrder(3L), buildOrder(4L), buildOrder(2L)
        ));

        List<Order> result = sortService.sortById(list);

        assertEquals(4, result.size());
        assertEquals(List.of(1L, 2L, 3L, 4L),
                result.stream().map(Order::getId).toList());
    }



    private Order buildOrder(Long id) {
        Order o = new Order();
        o.setId(id);
        o.setState(OrderState.NEW);
        return o;
    }

    @Test
    void LockedOrderProductToOrder() {
        Order order = buildOrder(1L);
        Product product = buildProduct(1L,"A",50,1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//    when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//    when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        //stateMachine.sendEvent(OrderEvent.CONFIRM);
        order.setState(OrderState.CONFIRMED);
        assertThrows(IllegalStateException.class, () -> orderService.addProductToOrder(1L,1L));

        verify(orderRepository).findById(1L);
    }

    @Test
    void ValidProductToOrder() {
        Order order = buildOrder(1L);
        Product product = buildProduct(1L,"A",50,1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderMtMProduct item = orderService.addProductToOrder(1L,1L);

        verify(orderRepository).save(order);
        //verify(productRepository).save(product);
        verify(orderRepository).findById(1L);
        assertEquals(50, item.getPriceAtOrder());
        assertSame(order, item.getOrder(),
                "Позиция должна ссылаться на тот заказ, в который добавлялась");

    }

    @Test
    @DisplayName("RemoveProductFromOrder удаляет позицию из заказа в статусе NEW")
    void removeProductremovesItem() {
        Order order = buildOrderWithItem(1L, OrderState.NEW, 42L);
        Product product = buildProduct(41L, "Good", 1.0,1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(41L)).thenReturn(Optional.of(product));
        // Предполагаем, что в заказе есть хотя бы один товар
        orderService.addProductToOrder(1L, 41L );
        assumeTrue(!order.getProducts().isEmpty(), "Для теста нужен заказ с товаром");

        orderService.RemoveProductFromOrder(1L, order.getProducts().getLast().getId());
        orderService.RemoveProductFromOrder(1L, 42L);

        assertTrue(order.getProducts().isEmpty());
    }

    @Test
    @DisplayName("RemoveProductFromOrder бросает исключение если заказ не в статусе NEW")
    void removeProductthrows() {
        Order order = buildOrderWithItem(1L, OrderState.CONFIRMED, 42L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
                () -> orderService.RemoveProductFromOrder(1L, 42L));
    }

    @Test
    @DisplayName("RemoveProductFromOrder бросает исключение если заказ не найден")
    void removeProductbutNoProduct() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> orderService.RemoveProductFromOrder(99L, 1L));
    }

    private Order buildOrderWithItem(Long orderId, OrderState state, Long itemId) {
        Order o = new Order();
        o.setId(orderId);
        o.setState(state);
// Используем реальный объект OrderMtMProduct
        var item = new org.example.mtpogr.domain.statemachine.entity.OrderMtMProduct();
        // Устанавливаем id через рефлексию, т.к. поле генерируется БД
        try {
            var f = item.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(item, itemId);
        } catch (Exception ignored) { }
        item.setOrder(o);

        o.getProducts().add(item);
        return o;
    }



}
