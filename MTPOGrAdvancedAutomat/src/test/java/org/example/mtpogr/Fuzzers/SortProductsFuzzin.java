package org.example.mtpogr.Fuzzers;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.example.mtpogr.domain.service.purchase.ProductService;
import org.example.mtpogr.domain.service.purchase.SortService;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortProductsFuzzin {


    ProductRepository productRepository;

    SortService sortService;

    @InjectMocks
    ProductService productService;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        sortService = new SortService();
    }

    @FuzzTest
    void SortProductsFuzzin(FuzzedDataProvider data)
    {

        int size = data.consumeInt(0, 100);
        List<Product> products = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            products.add(buildProduct(data.consumeLong()));
        }
        List<Product> sorted = sortService.sortByIdP(products);

        assertEquals(size, sorted.size());
        assertSortedP(sorted);

    }
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
