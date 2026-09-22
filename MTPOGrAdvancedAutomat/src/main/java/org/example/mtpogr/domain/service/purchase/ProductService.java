package org.example.mtpogr.domain.service.purchase;

import org.example.mtpogr.domain.statemachine.entity.Product;
import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SortService sortService;

    public List<Product> getCatalog() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
    }

    public Product create(Product product) {
        validateProduct(product);
        return productRepository.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = getById(id);
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setAmount(updated.getAmount());
        validateProduct(existing);
        return productRepository.save(existing);
    }

    public void delete(Long id) {
        getById(id); // убедимся что товар существует
        productRepository.deleteById(id);
    }

    public Product findBinary(Long id)
    {
        //Product required = new Product();
        List<Product> Catalog = productRepository.findAll();
        Catalog = sortService.sortByIdP(Catalog);

        int low = 0, high = Catalog.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = id.compareTo(Catalog.get(mid).getId());
            if (cmp == 0)      return Catalog.get(mid);
            else if (cmp < 0)  high = mid - 1;
            else               low  = mid + 1;
        }

        return null;
    }

    public Product findSimple(Long id)
    {
        List<Product> Catalog = productRepository.findAll();
        for(Product requred: Catalog)
            if(requred.getId() == id)
                return  requred;
        return null;
    }

//    public List<Product> search(String name) {
//        return productRepository.findByNameContainingIgnoreCase(name);
//    }

    private void validateProduct(Product p) {
        if (p.getId().describeConstable().isEmpty() || p.getId() == null)
        {
            throw new IllegalArgumentException("Id не может быть пустым");
        }
        if (p.getPrice() < 0) {
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
//        if (p.getPrice()) {
//            throw new IllegalArgumentException("Цена не может быть отрицательной");
//        }
        if (p.getName() == null || p.getName().isBlank()) {
            throw new IllegalArgumentException("Название товара не может быть пустым");
        }
    }
}
