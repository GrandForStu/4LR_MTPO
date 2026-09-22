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
    private final OrderRepository orderRepository;
    private Long orderId;

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
    public void addProduct() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.ADD_PRODUCT);
    }
    public boolean addProductGuard() {
        return getState() == OrderState.NEW;
    }

    @Action
    public void confirm() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.CONFIRM);
    }
    public boolean confirmGuard() {
        return getState() == OrderState.DRAFT;
    }

    @Action
    public void pay() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.PAY);
    }
    public boolean payGuard() {
        return getState() == OrderState.CONFIRMED;
    }

    @Action
    public void pack() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.PACK);
    }
    public boolean packGuard() {
        return getState() == OrderState.PAID;
    }

    @Action
    public void ship() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.SHIP);
    }
    public boolean shipGuard() {
        return getState() == OrderState.PACKED;
    }

    @Action
    public void deliver() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.DELIVER);
    }
    public boolean deliverGuard() {
        return getState() == OrderState.SHIPPED;
    }

    @Action
    public void take() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.TAKE);
    }
    public boolean takeGuard() {
        return getState() == OrderState.DELIVERED;
    }

    @Action
    public void returnOrder() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.RETURN);
    }
    public boolean returnOrderGuard() {
        return getState() == OrderState.COMPLETED;
    }

    @Action
    public void returnConfirmed() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.RETURN_CONFIRMED);
    }
    public boolean returnConfirmedGuard() {
        return getState() == OrderState.RETURN_INITIATED;
    }

    @Action
    public void deliverReturn() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.DELIVER);
    }
    public boolean deliverReturnGuard() {
        return getState() == OrderState.SHIPPED_BACK;
    }

    // CANCEL – может быть вызван из многих состояний
    @Action
    public void cancel() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.CANCEL);
    }
    public boolean cancelGuard() {
        return getState() == OrderState.DRAFT ||
                getState() == OrderState.CONFIRMED ||
                getState() == OrderState.PAID ||
                getState() == OrderState.SHIPPED ||
                getState() == OrderState.DELIVERED;
           }

    @Action
    public void cancelFromDelivered() throws Exception {
        orderService.applyEvent(orderId, OrderEvent.CANCEL); // или отдельное событие
    }
    public boolean cancelFromDeliveredGuard() {
        return getState() == OrderState.DELIVERED;
    }


    public boolean resetGuard() {
        return getState() == OrderState.CANCELLED;
    }
}