package org.example.mtpogr.domain.statemachine.persist;

import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import org.example.mtpogr.domain.statemachine.persist.InMemoryStateMachinePersist;

@Configuration
public class StateMachinePersistenceConfig {

    @Bean
    public InMemoryStateMachinePersist inMemoryPersist() {
        return new InMemoryStateMachinePersist();
    }

    @Bean
    public StateMachinePersister<OrderState, OrderEvent, Long> persister(
            InMemoryStateMachinePersist persist) {
        return new DefaultStateMachinePersister(persist);
    }
}