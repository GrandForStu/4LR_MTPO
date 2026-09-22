package org.example.mtpogr.controller;


import org.example.mtpogr.domain.service.purchase.ProductService;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
//
//    @GetMapping
//    public ResponseEntity<List<Product>> getAll() {
//        return ResponseEntity.ok(productService.getAll());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Product> getById(@PathVariable Long id) {
//        return ResponseEntity.ok(productService.getById(id));
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<Product>> search(@RequestParam String name) {
//        return ResponseEntity.ok(productService.search(name));
//    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.ok(productService.create(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
