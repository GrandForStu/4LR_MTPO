package org.example.mtpogr.FSM.GraphWalk;

import org.example.mtpogr.domain.service.purchase.OrderService;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.BeforeExecution;
import org.graphwalker.java.annotation.GraphWalker;
import org.graphwalker.java.annotation.Vertex;
import org.graphwalker.java.annotation.Edge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
@GraphWalker(value = "random(edgcoverage(100))")
public class GeneratorGraph extends ExecutionContext {

    private final OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Long orderId;



@BeforeExecution
public void setup() {
    Order order = orderService.createOrder();
    this.orderId = order.getId();
    assertNotNull(orderId);
    System.out.println("Создан заказ с ID: " + orderId);
}

    public GeneratorGraph(OrderService orderService) {
        this.orderService = orderService;
    }



    @Vertex
    public void NEW() {
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderState.NEW, order.getState(), "Заказ должен быть в состоянии NEW");
    }

    @Vertex
    public void CONFIRMED() {
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderState.CONFIRMED, order.getState(), "Заказ должен быть в состоянии CONFIRMED");
    }

    @Vertex
    public void PAID() {
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderState.PAID, order.getState(), "Заказ должен быть в состоянии PAID");
    }

    @Vertex
    public void SHIPPED() {
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderState.SHIPPED, order.getState(), "Заказ должен быть в состоянии SHIPPED");
    }

    @Vertex
    public void DELIVERED() {
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderState.DELIVERED, order.getState(), "Заказ должен быть в состоянии DELIVERED");
    }

    @Vertex
    public void CANCELLED() {
        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals(OrderState.CANCELLED, order.getState(), "Заказ должен быть в состоянии CANCELLED");
    }

    @Edge
    public void CONFIRM() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.CONFIRM);
    }

    @Edge
    public void PAY() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.PAY);
    }

    @Edge
    public void SHIP() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.SHIP);
    }

    @Edge
    public void DELIVER() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.DELIVER);
    }

    @Edge
    public void CANCEL() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.CANCEL);
    }
    @Edge
    public void RESET() throws Exception {
    setup();
    }

}
