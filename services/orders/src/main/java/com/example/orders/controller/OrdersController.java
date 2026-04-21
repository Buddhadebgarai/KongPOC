package com.example.orders.controller;

import com.example.orders.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping(path = "api/v1/orders")
public class OrdersController {

    private static final Logger log = LoggerFactory.getLogger(OrdersController.class);
    private static final AtomicLong counter = new AtomicLong(1);
    private static final Map<Long, Order> orderStore = Collections.synchronizedMap(new LinkedHashMap<>());

    static {
        // Initialize with sample data
        orderStore.put(1L, new Order(1L, 1L, 1L, 2, "PENDING"));
        orderStore.put(2L, new Order(2L, 2L, 2L, 1, "COMPLETED"));
        counter.set(3L);
    }

    @GetMapping(path = "/")
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("Fetching all orders");
        return ResponseEntity.ok(new ArrayList<>(orderStore.values()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        log.info("Fetching order with id: {}", id);
        Order order = orderStore.get(id);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Order not found"));
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping(path = "/")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        log.info("Creating new order: {}", order);
        Long newId = counter.getAndIncrement();
        order.setId(newId);
        orderStore.put(newId, order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        log.info("Updating order with id: {}", id);
        if (!orderStore.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Order not found"));
        }
        order.setId(id);
        orderStore.put(id, order);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        log.info("Deleting order with id: {}", id);
        if (!orderStore.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Order not found"));
        }
        orderStore.remove(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
