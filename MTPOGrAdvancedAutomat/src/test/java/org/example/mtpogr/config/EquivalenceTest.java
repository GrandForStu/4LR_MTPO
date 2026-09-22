//package org.example.mtpogr.config;
//
//import org.example.mtpogr.domain.service.purchase.OrderService;
//import org.example.mtpogr.domain.service.purchase.ProductService;
//import org.example.mtpogr.domain.service.purchase.SortService;
//import org.example.mtpogr.domain.statemachine.entity.Order;
//import org.example.mtpogr.domain.statemachine.entity.Product;
//import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
//import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
//import org.example.mtpogr.domain.statemachine.state.OrderState;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class EquivalenceTest {
//
//    //Тесты productService
//
//    @Mock
//    ProductRepository productRepository;
//    @InjectMocks
//    ProductService productService;
//
//    @Mock
//    OrderRepository orderRepository;
//    @InjectMocks
//    OrderService orderService;
//
//    @Test
//    @DisplayName("Тест на хорошесть товара")
//    void ValidCatalog()
//    {
//        Product product = buildProduct(1L, "Good", 200, 5);
//        when(productRepository.save(any())).thenReturn(product);
//
//        Product result = productService.create(product);
//
//        assertNotNull(result);
//        verify(productRepository).save(product);
//
//    }
//
//    @Test
//    @DisplayName("Тест Хороший но на границах")
//    void BoundValidCatalog()
//    {
//
//        Product product = buildProduct(1L, "isItGood?", 0, 0);
//        when(productRepository.save(any())).thenReturn(product);
//
//        Product result = productService.create(product);
//
//        assertNotNull(result);
//        verify(productRepository).save(product);
//
//    }
//
//    @ParameterizedTest
//    @CsvSource({"1L, Bad(, null, 1",
//            "1L, Bad(, 1, null",
//             "1L, null, 40, 1",
//            "null, Bad(, 40, 1"}  )
//    @DisplayName("Плохой тест null")
//    void NullNotValidCatalog(Long id, String name, double price, int am)
//    {
//        Product product = buildProduct(id, name, price , am);
//        assertThrows(IllegalArgumentException.class, () -> productService.create(product));
//        verify(productRepository, never()).save(any());
//
//    }
//
//    @ParameterizedTest
//    @CsvSource({"Bad, -1, 5",
//            "Bad,50,-1"})
//    @DisplayName("Плохой тест <0")
//    void LessThen0NotValidCatalog(Long id, String name, double price, int am){
//        Product product = buildProduct(id, name, price , am);
//        assertThrows(IllegalArgumentException.class, () -> productService.create(product));
//        verify(productRepository, never()).save(any());
//
//
//    }
//
//
//    private Product buildProduct(Long id, String name, double price, int amount) {
//        Product p = new Product();
//        p.setId(id);
//        p.setName(name);
//        p.setPrice(price);
//        p.setAmount(amount);
//        return p;
//    }
//
//
//    //Тесты OrderService
//    @Test
//    void createValidOrder() {
//
//        Product product = buildProduct(1L, "Ноутбук",5000, 3);
//
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
//
//        Order result = orderService.createOrder(List.of(1L));
//
//        assertNotNull(result);
//        assertEquals(OrderState.NEW, result.getState());
//        assertEquals(2, product.getAmount()); // остаток уменьшился
//        verify(orderRepository).save(any());
//    }
//    @Test
//    void createOrderbutProductIsNoLongerThere() {
//
//
//        when(productRepository.findById(99L)).thenReturn(Optional.empty());
//
//        assertThrows(IllegalArgumentException.class,
//                () -> orderService.createOrder(List.of(99L)));
//        verify(orderRepository, never()).save(any());
//    }
//
//
//    //Сортировочный сервис
//    @Autowired
//    SortService sortService;
//
//    @Test
//    void nullSort() {
//        assertNull(sortService.sortById(null));
//    }
//
//    // EP: пустой список → возвращается пустой список
//    @Test
//    void emptySort() {
//        List<Order> result = sortService.sortById(new ArrayList<>());
//        assertTrue(result.isEmpty());
//    }
//
//    // EP: один элемент → возвращается тот же список без изменений
//    @Test
//    void OneElementSort() {
//        List<Order> list = List.of(buildOrder(5L));
//        List<Order> result = sortService.sortById(new ArrayList<>(list));
//
//        assertEquals(1, result.size());
//        assertEquals(5L, result.get(0).getId());
//    }
//
//    // EP: список уже отсортирован → порядок не меняется
//    @Test
//    void ListAlreadySorted() {
//        List<Order> list = new ArrayList<>(List.of(
//                buildOrder(1L), buildOrder(2L), buildOrder(3L)
//        ));
//
//        List<Order> result = sortService.sortById(list);
//
//        assertEquals(List.of(1L, 2L, 3L),
//                result.stream().map(Order::getId).toList());
//    }
//
//    // EP: случайный порядок → сортируется правильно
//    @Test
//    void ClassicSorting() {
//        List<Order> list = new ArrayList<>(List.of(
//                buildOrder(4L), buildOrder(1L), buildOrder(3L), buildOrder(2L)
//        ));
//
//        List<Order> result = sortService.sortById(list);
//
//        assertEquals(List.of(1L, 2L, 3L, 4L),
//                result.stream().map(Order::getId).toList());
//    }
//
//    private Order buildOrder(Long id) {
//        Order o = new Order();
//        o.setId(id);
//        o.setState(OrderState.NEW);
//        return o;}
//}
