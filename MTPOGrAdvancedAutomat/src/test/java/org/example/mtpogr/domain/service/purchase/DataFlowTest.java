//package org.example.mtpogr.domain.service.purchase;
//
//import org.example.mtpogr.domain.statemachine.entity.OrderMtMProduct;
//import org.example.mtpogr.domain.statemachine.entity.Product;
//import org.example.mtpogr.domain.statemachine.entity.Order;
//import org.example.mtpogr.domain.statemachine.event.OrderEvent;
//import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
//import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
//import org.example.mtpogr.domain.statemachine.state.OrderState;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.statemachine.StateMachine;
//import org.springframework.statemachine.config.StateMachineFactory;
//import org.springframework.statemachine.persist.StateMachinePersister;
//
//import javax.swing.*;
//import javax.swing.plaf.nimbus.State;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertSame;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class DataFlowTest {
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock private ProductRepository productRepository;
//    @Mock private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
//    @Mock private StateMachinePersister<OrderState, OrderEvent, Long> persister;
//    @Mock private StateMachine<OrderState, OrderEvent> stateMachine;
//    @Mock private State state;
//    @Mock private SortService SortService;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    @Captor
//    private ArgumentCaptor<Order> orderCaptor;
//
//
//    @Test
//    void createOrder_productPrice_flowsIntoOrderMtMProduct_asPriceAtOrder() {
//        Product product = buildProduct(1L, "Ноутбук",  75000, 5);
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        Order result = orderService.createOrder(List.of(1L));
//
//        // def product.price=75000 → use item.setPriceAtOrder(75000)
//        OrderMtMProduct item = result.getProducts().get(0);
//        assertEquals( 75000.00, item.getPriceAtOrder(),
//                "Цена из каталога должна быть зафиксирована в позиции заказа");
//    }
//
//    // Поток: def product.Amount → use (Amount - 1) → def newAmount → use product.setAmount(newAmount)
//    //
//    // Остаток уменьшается ровно на 1 при добавлении товара в заказ.
//    @Test
//    void createOrder_productAmount_decrementedByOne_afterOrderCreation() {
//
//        Product product = buildProduct(1L, "Мышь",  1500, 7);
//
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        orderService.createOrder(List.of(1L));
//
//        // def Amount=7 → use 7-1=6 → def newAmount=6 → use product.setAmount(6)
//        assertEquals(6, product.getAmount(),
//                "Остаток должен уменьшиться ровно на 1");
//        verify(productRepository).save(product);
//    }
//
//    // Поток: def productIds=[p1, p2] → цикл по каждому →
//    //        def item1, item2 → use order.Products.add()
//    //
//    // Проверяем, что все товары из входного списка попали в заказ
//    // и у каждого зафиксирована своя цена.
//    @Test
//    void createOrder_multipleProducts_allFlowIntoOrderMtMProducts() {
//
//        Product p1 = buildProduct(1L, "Клавиатура",  3000, 2);
//        Product p2 = buildProduct(2L, "Монитор",  25000, 1);
//
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(p1));
//        when(productRepository.findById(2L)).thenReturn(Optional.of(p2));
//        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        Order result = orderService.createOrder(List.of(1L, 2L));
//
//        // def productIds=[1,2] → def Products=[item1,item2] → use order.setProducts
//        assertEquals(2, result.getProducts().size());
//        assertEquals( 3000,
//                result.getProducts().get(0).getPriceAtOrder());
//        assertEquals( 25000,
//                result.getProducts().get(1).getPriceAtOrder());
//    }
//
//    // Поток: def order (после save) → use return savedOrder
//    //
//    // Метод возвращает именно тот объект, который вернул репозиторий,
//    // а не промежуточную переменную.
//    @Test
//    void createOrder_savedOrder_isReturnedToController() {
//        Order savedOrder = new Order();
//        savedOrder.setId(100L); // репозиторий присвоил id
//        savedOrder.setState(OrderState.NEW);
//
//        when(orderRepository.save(any())).thenReturn(savedOrder);
//
//        Order result = orderService.createOrder(List.of());
//
//        // def savedOrder (из repository.save) → use return
//        assertSame(savedOrder, result,
//                "Должен вернуться именно тот объект, который сохранил репозиторий");
//    }
//
//    // ----------------------------------------------------------------
//    // addProductToOrder() — Data Flow тесты
//    // ----------------------------------------------------------------
//
//    // Поток: def orderId → use findById(orderId) → def order → use order.isLocked()
//    //        → use order.getProducts().add(item) → use orderRepository.save(order)
//    //
//    // orderId проходит через весь метод: сначала для поиска, потом объект
//    // используется для проверки блокировки и финального сохранения.
//    @Test
//    void addProductToOrder_orderId_flowsThroughAllUsages() {
//        Order order = buildOrder(5L, OrderState.NEW);
//        Product product = buildProduct(1L, "Наушники",  5000, 3);
//
//        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        orderService.addProductToOrder(5L, 1L);
//
//        // def orderId=5 → use findById(5) → def order → use isLocked()
//        //              → use order.getProducts().add() → use save(order)
//        verify(orderRepository).findById(5L);
//        verify(orderRepository).save(order); // сохраняется именно тот же объект
//    }
//
//    // Поток: def product.price → use item.setPriceAtOrder(price)
//    //        Аналогично createOrder — цена фиксируется и при addProductToOrder
//    @Test
//    void addProductToOrder_productPrice_fixedAtMomentOfAdding() {
//        Order order = buildOrder(1L, OrderState.NEW);
//        Product product = buildProduct(1L, "Наушники",  8500.50, 2);
//
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        OrderMtMProduct item = orderService.addProductToOrder(1L, 1L);
//
//        // Цена из каталога на этот момент — 8500.50
//        // Если завтра изменится, позиция заказа не должна измениться
//        assertEquals( 8500.50, item.getPriceAtOrder());
//    }
//
//    // Поток: def item → use order.getProducts().add(item)
//    //
//    // Созданная позиция должна быть связана с нужным заказом
//    @Test
//    void addProductToOrder_createdItem_linkedToCorrectOrder() {
//        Order order = buildOrder(1L, OrderState.NEW);
//        Product product = buildProduct(1L, "Кресло",  15000, 4);
//
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        OrderMtMProduct item = orderService.addProductToOrder(1L, 1L);
//
//        // def item → use item.setOrder(order) → item.getOrder() == order
//        assertSame(order, item.getOrder(),
//                "Позиция должна ссылаться на тот заказ, в который добавлялась");
//    }
//
//    private Product buildProduct(Long id, String name, double price, int Amount) {
//        Product p = new Product();
//        p.setId(id);
//        p.setName(name);
//        p.setPrice(price);
//        p.setAmount(Amount);
//        return p;
//    }
//
//    private Order buildOrder(Long id, OrderState state) {
//        Order o = new Order();
//        o.setId(id);
//        o.setState(state);
//
//        return o;
//    }
//}
