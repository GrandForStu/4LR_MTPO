package org.example.mtpogr.controller;

import org.example.mtpogr.domain.service.purchase.OrderService;
import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController
{
    @Autowired
    private OrderService orderService;

    @PostMapping("/{orderId}/events")
    public ResponseEntity<OrderState> sendEvent(@PathVariable Long orderId, @RequestParam OrderEvent event) throws Exception {
        OrderState newState = orderService.applyEvent(orderId, event);
        return ResponseEntity.ok(newState);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder() {
        return ResponseEntity.ok(orderService.createOrder());
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersSortedById() {
        return ResponseEntity.ok(orderService.getAllSortedById());
    }
}