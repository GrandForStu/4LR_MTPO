package org.example.mtpogr.Fuzzers;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.example.mtpogr.domain.service.purchase.ProductService;
import org.example.mtpogr.domain.service.purchase.SortService;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class ServiceFuzzTests {

    @Mock

    ProductRepository productRepository;

    SortService sortService;

    @InjectMocks
    ProductService productService;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        sortService = new SortService();
    }




    //Тесты Продукт сервиса


    @FuzzTest
    @DisplayName("тестирование функции обновления товара")
    void ProductUpdateTest(FuzzedDataProvider data)
    {
        Product p1 = buildProduct(data.consumeLong(1L, 10000L));
        productService.create(p1);
        Product p2 = new Product();

        String newName   = data.consumeString(20);
        double newPrice  = Math.abs(data.consumeDouble());
        int newAmount = data.consumeInt(0, 9999);
        if ((newName == null || newName.isBlank()))  newName = "SmthValid";

        p2.setName(newName );
        p2.setPrice(newPrice);
        p2.setAmount(newAmount);

        when(productRepository.findById(p1.getId())).thenReturn(Optional.of(p1));
        when(productRepository.save(p1)).thenReturn(p1);

        productService.update(p1.getId(),p2 );
        assertEquals(p1.getAmount(), p2.getAmount());
        assertEquals(p1.getPrice(), p2.getPrice());
        assertEquals(p1.getName(), p2.getName());

    }










    //Вспомогательные функции
    private Order buildOrder(Long id) {
        Order o = new Order();
        o.setId(id);
        return o;
    }

    private Product buildProduct(Long id) {
        Product p = new Product();
        p.setId(id);
        p.setName("p" + id);
        p.setPrice(1.0);
        p.setAmount(1);
        return p;
    }
    private void assertSorted(List<Order> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).getId() <= list.get(i + 1).getId(),
                    "Нарушен порядок сортировки на позиции " + i
                            + ": " + list.get(i).getId() + " > " + list.get(i + 1).getId());
        }
    }

    private void assertSortedP(List<Product> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            assertTrue(list.get(i).getId() <= list.get(i + 1).getId(),
                    "Нарушен порядок сортировки товаров на позиции " + i
                            + ": " + list.get(i).getId() + " > " + list.get(i + 1).getId());
        }
    }

}
