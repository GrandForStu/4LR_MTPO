package org.example.mtpogr.domain.statemachine.persist;

import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStateMachinePersist implements StateMachinePersist<OrderState, OrderEvent, Long> {

    private final Map<Long, StateMachineContext<OrderState, OrderEvent>> contexts = new HashMap<>();

    @Override
    public void write(StateMachineContext<OrderState, OrderEvent> context, Long id) throws Exception {
        contexts.put(id, context);
    }

    @Override
    public StateMachineContext<OrderState, OrderEvent> read(Long id) throws Exception {
        return contexts.get(id);
    }
}



//import org.example.mtpogr.domain.statemachine.event.OrderEvent
//import org.example.mtpogr.domain.statemachine.state.OrderState
//import org.springframework.statemachine.StateMachineContext;
//import org.springframework.statemachine.persist.StateMachinePersister;
//import org.springframework.statemachine.persist.DefaultStateMachinePersister;
////import org.springframework.statemachine.persist.InMemoryStateMachinePersist;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.statemachine.StateMachineContext;
//import org.springframework.statemachine.StateMachinePersist;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class InMemoryStateMachinePersist implements StateMachinePersist<OrderState, OrderEvent, Long> {
//
//    private final Map<Long, StateMachineContext<OrderState, OrderEvent>> contexts = new HashMap<>();
//
//    @Override
//    public void write(StateMachineContext<OrderState, OrderEvent> context, Long id) throws Exception {
//        contexts.put(id, context);
//    }
//
//    @Override
//    public StateMachineContext<OrderState, OrderEvent> read(Long id) throws Exception {
//        return contexts.get(id);
//    }
//}
//@Configuration
//public class StateMachinePersistenceConfig {
//
//    @Bean
//    public InMemoryStateMachinePersist<OrderState, OrderEvent, Long> inMemoryPersist() {
//        return new InMemoryStateMachinePersist<>();
//    }
//
//    @Bean
//    public StateMachinePersister<OrderState, OrderEvent, Long> persister(
//            InMemoryStateMachinePersist<OrderState, OrderEvent, Long> persist) {
//        return new DefaultStateMachinePersister<>(persist);
//    }
//}