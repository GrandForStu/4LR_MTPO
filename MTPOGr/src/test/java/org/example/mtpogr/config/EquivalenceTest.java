package org.example.mtpogr.config;

import org.hamcrest.Matcher;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static  org.hamcrest.MatcherAssert.assertThat;
import org.example.mtpogr.domain.service.purchase.OrderService;
import org.example.mtpogr.domain.service.purchase.ProductService;
import org.example.mtpogr.domain.service.purchase.SortService;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import static java.util.Optional.empty;
//import static net.bytebuddy.matcher.ElementMatchers.is;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;
import static java.util.Optional.empty;
@ExtendWith(MockitoExtension.class)
public class EquivalenceTest {

    //Тесты productService

    @Mock
    ProductRepository productRepository;
    @InjectMocks
    ProductService productService;

    @Mock
    OrderRepository orderRepository;
    @InjectMocks
    OrderService orderService;
    @Spy
    SortService sortService;
    @Test
    @DisplayName("Тест на хорошесть товара")
    void ValidCatalog()
    {
        Product product = buildProduct(1L, "Good", 200, 5);
        when(productRepository.save(any())).thenReturn(product);

        Product result = productService.create(product);

        assertNotNull(result);
        verify(productRepository).save(product);

    }

    @Test
    @DisplayName("Тест Хороший но на границах")
    void BoundValidCatalog()
    {

        Product product = buildProduct(1L, "isItGood?", 0, 0);
        when(productRepository.save(any())).thenReturn(product);

        Product result = productService.create(product);

        assertNotNull(result);
        verify(productRepository).save(product);

    }

    @ParameterizedTest
    @CsvSource(value = {"Bad(,null, 1",
            "Bad(, 1,null",
            "null, 40, 1"},nullValues = "null" )
    @DisplayName("Плохой тест null")
    void NullNotValidCatalog(String name, Double price, Integer am)
    {
        //Product product = buildProduct(1L, name, price, am);
        //assertThrows(IllegalArgumentException.class, () -> productService.create(new Product(1L, name, price, am)));
        assertThatThrownBy(() -> productService.create(buildProduct(1L, name, price, am)))
                .isInstanceOfAny(IllegalArgumentException.class, NullPointerException.class);
        verify(productRepository, never()).save(any());
    }
    // "null, Bad(, 40, 1"
    @Test
    @DisplayName("Плохой тест id null")
    void NullIdNotValid()
    {
        Product product = buildProduct(null, "Bad", 40 , 1);
        assertThrows(NullPointerException.class, () -> productService.create(product));
        verify(productRepository, never()).save(any());
    }
    @ParameterizedTest
    @CsvSource({"Bad, -1, 5",
            "Bad,50,-1"})
    @DisplayName("Плохой тест <0")
    void LessThen0NotValidCatalog(String name, double price, int am){
        Product product = buildProduct(1L, name, price , am);
        assertThrows(IllegalArgumentException.class, () -> productService.create(product));
        verify(productRepository, never()).save(any());


    }





    //Тесты OrderService
    @Test
    @DisplayName("Создание заказа хорошее")
    void createValidOrder() {

        Product product = buildProduct(1L, "Ноутбук",5000, 3);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(List.of(1L));

        assertNotNull(result);
        assertEquals(OrderState.NEW, result.getState());
        assertEquals(2, product.getAmount()); // остаток уменьшился
        System.out.println(result.getProducts().size());
        // assertEquals(5000, result.getProducts().getFirst().getPriceAtOrder());
        // assertEquals("Ноутбук", product.getName());

        verify(orderRepository).save(any());
    }
    @Test
    @DisplayName("Создание заказа с продуком но без продуктов")
    void createOrderbutProductIsNoLongerThere() {


        when(productRepository.findById(99L)).thenReturn(empty());

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(List.of(99L)));
        verify(orderRepository, never()).save(any());
    }


    //Сортировочный сервис


    @Test
    @DisplayName("Сортировка пустого списка заказов")
    void nullSort() {
        assertNull(sortService.sortById(null));
    }

    @Test
    @DisplayName("Сортировка пустого каталога")
    void emptySort() {
        List<Product> result = sortService.sortByIdP(new ArrayList<>());
        assertTrue(result.isEmpty());
    }


    @Test
    @DisplayName("Сортровка единственного элемента")
    void OneElementSort() {
        List<Order> list = List.of(buildOrder(5L));
        List<Order> result = sortService.sortById(new ArrayList<>(list));
        List<Product> listp = List.of(buildProduct(5L, "e", 12,2));
        List<Product> resultp = sortService.sortByIdP(new ArrayList<>(listp));
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getId());
        assertEquals(1, resultp.size());
        assertEquals(5L, resultp.get(0).getId());

    }

    //    @Test
//    @DisplayName("Сортировка сортированного списка")
//    void ListAlreadySorted() {
//        List<Order> list = new ArrayList<>(List.of(
//                buildOrder(1L), buildOrder(2L), buildOrder(3L)
//        ));
//
//        List<Order> result = sortService.sortById(list);
//
//        assertEquals(List.of(1L, 2L, 3L),
//                result.stream().map(Order::getId).toList());
//
//        List<Product> listP = new ArrayList<>(List.of(
//                buildProduct(1L), buildProduct(2L), buildProduct(3L)
//        ));
//
//        List<Product> resultP = sortService.sortByIdP(listP);
//
//        assertEquals(List.of(1L, 2L, 3L),
//                resultP.stream().map(Product::getId).toList());
//    }
//
//    @Test
//    @DisplayName("Хорошая сортировка")
//    void ClassicSorting() {
//        List<Order> list = new ArrayList<>(List.of(
//                buildOrder(4L), buildOrder(1L), buildOrder(3L), buildOrder(2L)
//        ));
//
//        List<Order> result = sortService.sortById(list);
//
//        assertEquals(List.of(1L, 2L, 3L, 4L),
//                result.stream().map(Order::getId).toList());
//
//        List<Product> listP = new ArrayList<>(List.of(
//                buildProduct(4L), buildProduct(1L), buildProduct(3L), buildProduct(2L)
//        ));
//
//        List<Product> resultP = sortService.sortByIdP(listP);
//
//        assertEquals(List.of(1L, 2L, 3L, 4L),
//                resultP.stream().map(Product::getId).toList());
//    }
    @Test
    @DisplayName("getAllSortedById возвращает заказы в порядке возрастания ID")
    void GoodGetAllSortedById() {
        List<Order> unsorted = new ArrayList<>(List.of(
                buildOrder(3L), buildOrder(1L), buildOrder(2L)
        ));
        when(orderRepository.findAll()).thenReturn(unsorted);

        List<Order> result = orderService.getAllSortedById();

        // Hamcrest: contains c точным порядком
        assertThat(result.stream().map(Order::getId).toList(),
                contains(1L, 2L, 3L));

        // Проверяем, что sortService.sortById был вызван (spy позволяет это)
        verify(sortService).sortById(unsorted);
    }
//    @Test
//    @DisplayName("getAllSortedById возвращает заказы в порядке возрастания ID")
//    void GoodSortByIdP() {
//        List<Product> unsorted = new ArrayList<>(List.of(
//                buildProduct(3L,"fr",100,1),
//                buildProduct(1L, "Sc",100,2),
//                buildProduct(2L, "Thr",100,3)
//        ));
//       // when(productRepository.findAll()).thenReturn(unsorted);
//
//        List<Product> result = sortService.sortByIdP(unsorted);
//
//        // Hamcrest: contains c точным порядком
//        assertThat(result.stream().map(Product::getId).toList(),
//                contains(1L, 2L, 3L));
//
//        // Проверяем, что sortService.sortById был вызван (spy позволяет это)
//        verify(sortService).sortByIdP(unsorted);
//    }

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "2, 2",
            "5, 5"
    })
    @DisplayName("Хорошая сортировка разных размеров")
    void sortByIdWithCorrectSize(int inputSize, int expectedSize) {
        List<Order> list = new ArrayList<>();
        for (int i = inputSize; i >= 1; i--) {
            list.add(buildOrder((long) i));
        }

        List<Order> result = sortService.sortById(list);

        assertThat(result, hasSize(expectedSize));
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1).getId() <= result.get(i).getId());
        }
    }

    //продукт сервис

    @Test
    @DisplayName("getCatalog возвращает все товары из репозитория")
    void GoodGetCatalog() {
        List<Product> catalog = List.of(
                buildProduct(1L, "Мышь",    500, 10),
                buildProduct(2L, "Клавиатура", 1500, 5)
        );
        when(productRepository.findAll()).thenReturn(catalog);

        List<Product> result = productService.getCatalog();

        assertThat (result, hasSize(2));
        assertThat(result, contains(catalog.get(0), catalog.get(1)));
    }

    @Test
    @DisplayName("getCatalog возвращает пустой список если каталог пуст")
    void getCatalogButEmpty() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> result = productService.getCatalog();


        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findBinary находит существующий товар")
    void findBinaryReturnsProduct() {
        List<Product> catalog = new ArrayList<>(List.of(
                buildProduct(1L, "A", 100, 1),
                buildProduct(2L, "B", 200, 2),
                buildProduct(3L, "C", 300, 3)
        ));
        when(productRepository.findAll()).thenReturn(catalog);

        Product result = productService.findBinary(2L);

        assumeTrue(catalog.size() > 0, "Каталог должен быть непустым для этого теста");

        assertThat(result, notNullValue()); //тут мог бы бытьь матчер
        assertThat(result, hasProperty("name", is("B")));
    }

    @Test
    @DisplayName("findBinary возвращает null если ID не найден")
    void findBinaryButNoIds() {
        when(productRepository.findAll()).thenReturn(List.of(
                buildProduct(1L, "A", 100, 1)
        ));

        Product result = productService.findBinary(99L);

        assertNull(result);
    }

//    @ParameterizedTest
//    @ValueSource(longs = {1L, 2L, 3L, 4L, 5L})
//    @DisplayName("findBinary находит каждый товар из каталога")
//    void findBinaryEachOneIsFound(long id) {
//        List<Product> catalog = new ArrayList<>();
//        for (long i = 1; i <= 5; i++) {
//            catalog.add(buildProduct(i, "Item" + i, i * 100, (int)i));
//        }
//        when(productRepository.findAll()).thenReturn(catalog);
//
//        Product result = productService.findBinary(id);
//
//        assertNotNull(result);
//        assertEquals(id, result.getId());
//    }


//    @Test
//    @DisplayName("findSimple находит существующий товар линейным поиском")
//    void findSimpleReturnsProduct() {
//        List<Product> catalog = new ArrayList<>(List.of(
//                buildProduct(10L, "X", 999, 3),
//                buildProduct(20L, "Y", 1999, 1)
//        ));
//        when(productRepository.findAll()).thenReturn(catalog);
//
//        Product result = productService.findSimple(20L);
//
//        assertNotNull(result);
//        assertEquals("Y", result.getName());
//    }

    @Test
    @DisplayName("findSimple возвращает null если ID отсутствует")
    void findSimpleReturnsNull() {
        when(productRepository.findAll()).thenReturn(List.of(
                buildProduct(5L, "Z", 50, 1)
        ));

        Product result = productService.findSimple(999L);

        assertNull(result);
    }

    @Test
    @DisplayName("findBinary и findSimple дают одинаковый результат (сравнение алгоритмов)")
    void findsHaveSameResults() {
        List<Product> catalog = new ArrayList<>(List.of(
                buildProduct(1L, "A", 100, 1),
                buildProduct(3L, "C", 300, 2),
                buildProduct(7L, "G", 700, 3)
        ));
        // два вызова к репозиторию
        when(productRepository.findAll()).thenReturn(catalog);
        Product binary = productService.findBinary(3L);

        when(productRepository.findAll()).thenReturn(catalog);
        Product linear = productService.findSimple(3L);

        // Assumption: оба метода должны быть реализованы; если хотя бы один null — пропускаем
        assumeFalse(binary == null && linear == null,
                "Оба результата null — методы, вероятно, не реализованы");

        assertEquals(binary, linear, "Бинарный и линейный поиск должны находить один и тот же объект");
    }

    private Product buildProduct(Long id, String name, double price, int amount) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        p.setAmount(amount);
        return p;
    }
    private Product buildProduct(Long id) {
        Product p = new Product();
        p.setId(id);
        p.setName("test_" + id.toString());
        p.setPrice(1.0);
        p.setAmount(1);
        return p;
    }

    private Order buildOrder(Long id) {
        Order o = new Order();
        o.setId(id);
        o.setState(OrderState.NEW);
        return o;}
}


