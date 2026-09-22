package org.example.mtpogr.config;

import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Block;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import javax.swing.*;
import java.util.EnumSet;


@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent>
{
    @Autowired
    OrderRepository orderRepository;
    @Override
    public void configure(final StateMachineStateConfigurer<OrderState,OrderEvent> states) throws Exception
    {
        states
        .withStates()
        .initial(OrderState.NEW)
//        .end(OrderState.DELIVERED)
        .end(OrderState.CANCELLED)
        .states(EnumSet.allOf(OrderState.class));

    }
   @Override
    public void configure(final StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
       transitions
               .withExternal()
               .source(OrderState.NEW)
               .target(OrderState.CONFIRMED)
               .event(OrderEvent.CONFIRM)
               //.action( lockOrder())

            .and()
               .withExternal()
               .source(OrderState.CONFIRMED)
               .target(OrderState.PAID)
               .event(OrderEvent.PAY)
               //.action {}

               .and()
               .withExternal()
               .source(OrderState.PAID)
               .target(OrderState.SHIPPED)
               .event(OrderEvent.SHIP)
               //.action {}

               .and()
               .withExternal()
               .source(OrderState.SHIPPED)
               .target(OrderState.DELIVERED)
               .event(OrderEvent.DELIVER)
               //.action {}

               .and()
               .withExternal()
               .source(OrderState.NEW)
               .target(OrderState.CANCELLED)
               .event(OrderEvent.CANCEL)
               //.action {}

               .and()
               .withExternal()
               .source(OrderState.CONFIRMED)
               .target(OrderState.CANCELLED)
               .event(OrderEvent.CANCEL)
               //.action {}

               .and()
               .withExternal()
               .source(OrderState.PAID)
               .target(OrderState.CANCELLED)
               .event(OrderEvent.CANCEL)
               //.action {}

               .and()
               .withExternal()
               .source(OrderState.SHIPPED)
               .target(OrderState.CANCELLED)
               .event(OrderEvent.CANCEL)
               //.action {}
               .and()
               .withExternal()
               .source(OrderState.DELIVERED)
               .target(OrderState.CANCELLED)
               .event(OrderEvent.CANCEL);
       //.action {}

    }

}
