package org.example.mtpogr.config;

import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void configure(final StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states
                .withStates()
                .initial(OrderState.NEW)
                .end(OrderState.CANCELLED)
                .states(EnumSet.allOf(OrderState.class));
    }

    @Override
    public void configure(final StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
                // NEW -> DRAFT
                .withExternal()
                .source(OrderState.NEW)
                .target(OrderState.DRAFT)
                .event(OrderEvent.ADD_PRODUCT)
                .and()
                // DRAFT -> CONFIRMED
                .withExternal()
                .source(OrderState.DRAFT)
                .target(OrderState.CONFIRMED)
                .event(OrderEvent.CONFIRM)
                .and()
                // DRAFT -> CANCELLED
                .withExternal()
                .source(OrderState.DRAFT)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .and()
                // CONFIRMED -> PAID
                .withExternal()
                .source(OrderState.CONFIRMED)
                .target(OrderState.PAID)
                .event(OrderEvent.PAY)
                .and()
                // CONFIRMED -> CANCELLED
                .withExternal()
                .source(OrderState.CONFIRMED)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .and()
                // PAID -> PACKED
                .withExternal()
                .source(OrderState.PAID)
                .target(OrderState.PACKED)
                .event(OrderEvent.PACK)
                .and()
                // PAID -> CANCELLED
                .withExternal()
                .source(OrderState.PAID)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .and()
                // PACKED -> SHIPPED
                .withExternal()
                .source(OrderState.PACKED)
                .target(OrderState.SHIPPED)
                .event(OrderEvent.SHIP)
                .and()
                // SHIPPED -> DELIVERED
                .withExternal()
                .source(OrderState.SHIPPED)
                .target(OrderState.DELIVERED)
                .event(OrderEvent.DELIVER)
                .and()
                // SHIPPED -> CANCELLED
                .withExternal()
                .source(OrderState.SHIPPED)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL)
                .and()
                // DELIVERED -> COMPLETED (получение товара)
                .withExternal()
                .source(OrderState.DELIVERED)
                .target(OrderState.COMPLETED)
                .event(OrderEvent.TAKE)
                .and()
                // DELIVERED -> SHIPPED_BACK (отказ от товара после доставки)
                .withExternal()
                .source(OrderState.DELIVERED)
                .target(OrderState.SHIPPED_BACK)
                .event(OrderEvent.CANCEL)
                .and()
                // COMPLETED -> RETURN_INITIATED
                .withExternal()
                .source(OrderState.COMPLETED)
                .target(OrderState.RETURN_INITIATED)
                .event(OrderEvent.RETURN)
                .and()
                // RETURN_INITIATED -> SHIPPED_BACK
                .withExternal()
                .source(OrderState.RETURN_INITIATED)
                .target(OrderState.SHIPPED_BACK)
                .event(OrderEvent.RETURN_CONFIRMED)
                .and()
                // SHIPPED_BACK -> CANCELLED
                .withExternal()
                .source(OrderState.SHIPPED_BACK)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.DELIVER);

                // CANCELLED -> NEW (административный сброс)

    }
}