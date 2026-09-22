package org.example.mtpogr.FSM;


import nz.ac.waikato.modeljunit.Action;
import nz.ac.waikato.modeljunit.FsmModel;
import org.example.mtpogr.domain.service.purchase.OrderService;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.example.mtpogr.domain.statemachine.state.OrderState;

public class Generator implements FsmModel {
    private final OrderService orderService;
    private Long orderId;
    private final OrderRepository orderRepository; // добавили — нужен для чтения реального состояния

    public Generator(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        reset(false);
    }

    @Override
    public void reset(boolean b) {
        Order order = orderService.createOrder();
        this.orderId = order.getId();
    }

    @Override
    public Object getState() {
        return orderRepository.findById(orderId).orElseThrow().getState();
    }

    @Action
    public void confirm() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.CONFIRM);
    }
    public boolean confirmGuard() {
        return getState() == OrderState.NEW;
    }

    @Action
    public void pay() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.PAY);
    }
    public boolean payGuard() {
        return getState() == OrderState.CONFIRMED;
    }

    @Action
    public void ship() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.SHIP);
    }
    public boolean shipGuard() {
        return getState() == OrderState.PAID;
    }

    @Action
    public void deliver() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.DELIVER);
    }
    public boolean deliverGuard() {
        return getState() == OrderState.SHIPPED;
    }

    @Action
    public void cancel() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.CANCEL);
    }
    public boolean cancelGuard() {
        return getState() == OrderState.NEW ||
                getState() == OrderState.CONFIRMED ||
                getState() == OrderState.PAID ||
                getState() == OrderState.SHIPPED ||
                getState() == OrderState.DELIVERED;
    }
}