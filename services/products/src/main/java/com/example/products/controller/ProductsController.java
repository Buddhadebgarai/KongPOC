package com.example.products.controller;

import com.example.products.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping(path = "api/v1/products")
public class ProductsController {

    private static final Logger log = LoggerFactory.getLogger(ProductsController.class);
    private static final AtomicLong counter = new AtomicLong(1);
    private static final Map<Long, Product> productStore = Collections.synchronizedMap(new LinkedHashMap<>());

    static {
        // Initialize with sample data
        productStore.put(1L, new Product(1L, "Laptop", 999.99, "High-performance laptop"));
        productStore.put(2L, new Product(2L, "Mouse", 29.99, "Wireless mouse"));
        counter.set(3L);
    }

    @GetMapping(path = "/")
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Fetching all products");
        return ResponseEntity.ok(new ArrayList<>(productStore.values()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productStore.get(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping(path = "/")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        log.info("Creating new product: {}", product);
        Long newId = counter.getAndIncrement();
        product.setId(newId);
        productStore.put(newId, product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        log.info("Updating product with id: {}", id);
        if (!productStore.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }
        product.setId(id);
        productStore.put(id, product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        log.info("Deleting product with id: {}", id);
        if (!productStore.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Product not found"));
        }
        productStore.remove(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
