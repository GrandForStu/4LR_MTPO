package org.example.mtpogr.FSM.GraphWalk;

import org.example.mtpogr.domain.service.purchase.OrderService;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.java.annotation.BeforeExecution;
import org.graphwalker.java.annotation.Edge;
//import org.graphwalker.java.annotation.Guard;
import org.graphwalker.java.annotation.Vertex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class GeneratorGraph extends ExecutionContext {

    private final OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Long orderId;
    @BeforeExecution
    public void setup() {
        Order order = orderService.createOrder();
        this.orderId = order.getId();
        assertNotNull(orderId, "ID заказа не должен быть null после создания");
        System.out.println("Создан заказ с ID: " + orderId);
    }

    public GeneratorGraph(OrderService orderService) {
        this.orderService = orderService;
    }
    OrderState expectedState;

    @Vertex
    public void NEW() {
        verifyState(OrderState.NEW);
    }

    @Vertex
    public void DRAFT() {
        verifyState(OrderState.DRAFT);
    }

    @Vertex
    public void CONFIRMED() {
        verifyState(OrderState.CONFIRMED);
    }

    @Vertex
    public void PAID() {
        verifyState(OrderState.PAID);
    }

    @Vertex
    public void PACKED() {
        verifyState(OrderState.PACKED);
    }

    @Vertex
    public void SHIPPED() {
        verifyState(OrderState.SHIPPED);
    }

    @Vertex
    public void DELIVERED() {
        verifyState(OrderState.DELIVERED);
    }

    @Vertex
    public void COMPLETED() {
        verifyState(OrderState.COMPLETED);
    }

    @Vertex
    public void RETURN_INITIATED() {
        verifyState(OrderState.RETURN_INITIATED);
    }

    @Vertex
    public void SHIPPED_BACK() {
        verifyState(OrderState.SHIPPED_BACK);
    }

    @Vertex
    public void CANCELLED() {
        verifyState(OrderState.CANCELLED);
    }


    @Edge
    public void ADD_PRODUCT() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.ADD_PRODUCT);
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
    public void PACK() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.PACK);
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
    public void TAKE() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.TAKE);
    }

    @Edge
    public void RETURN() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.RETURN);
    }

    @Edge
    public void RETURN_CONFIRMED() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.RETURN_CONFIRMED);
    }

    @Edge
    public void CANCEL() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.CANCEL);
    }

    @Edge
    public void RESET() throws Exception {
        setup();
    }

    private void verifyState(OrderState expected) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AssertionError());
        assertEquals(expected, order.getState());

    }
}