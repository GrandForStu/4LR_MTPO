package org.example.mtpogr.domain.service.purchase;

import org.example.mtpogr.domain.statemachine.entity.OrderMtMProduct;
import org.example.mtpogr.domain.statemachine.entity.Product;
import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
import org.springframework.statemachine.config.StateMachineFactory;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
import org.example.mtpogr.domain.statemachine.entity.Order;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;


@Service
public class OrderService {
    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    @Autowired
    private StateMachinePersister<OrderState, OrderEvent, Long> persister;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SortService sortService;

    public OrderState applyEvent(Long orderId, OrderEvent event) throws Exception {

        if (event == null) throw new IllegalArgumentException("Event не может быть null");
        Order order = orderRepository.findById(orderId).orElseThrow();

        // Создаём или восстанавливаем автомат для данного заказа
        StateMachine<OrderState, OrderEvent> stateMachine = stateMachineFactory.getStateMachine();
        persister.restore(stateMachine, orderId);



        // Отправляем событие
        boolean accepted = stateMachine.sendEvent(event);
        if (!accepted) {
            throw new IllegalStateException("Невозможно выполнить " + event + " для заказа " + orderId);
        }

        // Сохраняем новое состояние автомата
        persister.persist(stateMachine, orderId);

        // Обновляем состояние в сущности Order (если нужно)
        order.setState(stateMachine.getState().getId());
        orderRepository.save(order);

        return order.getState();
    }
    public Order createOrder()
    {
        Order order = new Order();
        order.setState(OrderState.NEW);
        return orderRepository.save(order);
    }

    public Order createOrder(List<Long> productIds) {
        Order order = new Order();
        order.setState(OrderState.NEW);
        List<OrderMtMProduct> items = new ArrayList<>();
        for (Long productId : productIds) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + productId));


            OrderMtMProduct item = new OrderMtMProduct();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(1);
            item.setPriceAtOrder(product.getPrice()); // фиксируем цену

            product.setAmount(product.getAmount() - 1); // уменьшаем остаток

            productRepository.save(product);

            items.add(item);}
            return orderRepository.save(order);

    }

    public OrderMtMProduct addProductToOrder(Long orderId, Long productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));

        if (order.getState() != OrderState.NEW) {
            throw new IllegalStateException(
                    "Нельзя изменять состав заказа в состоянии: " + order.getState()
            );
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + productId));


        OrderMtMProduct item = new OrderMtMProduct();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(1);
        item.setPriceAtOrder(product.getPrice());
        product.setAmount(product.getAmount() - 1);

        order.getProducts().add(item);
        orderRepository.save(order);

        return item;
    }
    public void RemoveProductFromOrder(Long orderId, Long OrPrId)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));

        if (order.getState() != OrderState.NEW) {
            throw new IllegalStateException(
                    "Нельзя изменять состав заказа в состоянии: " + order.getState()
            );
        }
        List<OrderMtMProduct> products = order.getProducts();
        for(OrderMtMProduct prod:  order.getProducts())
        {
            if(prod.getId() == OrPrId) { products.remove(prod); break;}
        }
        order.setProducts(products);
        
    }
    public List<Order> getAllSortedById() {
        return sortService.sortById(orderRepository.findAll());
    }
}