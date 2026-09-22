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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ProductFinderFuz {
    @Mock

    ProductRepository productRepository;

    @Spy
    SortService sortService = new SortService();

    @InjectMocks
    ProductService productService;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

    }

    @FuzzTest(maxExecutions = 5000)
    @DisplayName("фаз тесты метода поиска")
    void ProductFinderFuz(FuzzedDataProvider data)
    {
        int size = data.consumeInt(0, 100);
        Long id = data.consumeLong(1L, 127L);
        Long ids;
        boolean cont = false;
        List<Product> products = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            ids = data.consumeLong(1L, 200L);
            products.add(buildProduct(ids));
            if(ids == id) cont = true;
        }
        if (!cont) products.add(buildProduct(id));

        when(productRepository.findAll()).thenReturn(products);

        Product simple = productService.findSimple(id);
        Product binary = productService.findBinary(id);

        assertNotNull(simple, "findSimple() вернул null для id=" + id);
        assertNotNull(binary, "findBinary() вернул null для id=" + id);
        assertEquals(simple.getId(), id);
        assertEquals(binary.getId(), id);
//        assertEquals(productService.findSimple(id), productService.findBinary(id));

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
}
