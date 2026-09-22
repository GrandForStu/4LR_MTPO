package org.example.mtpogr.config;

import jakarta.persistence.criteria.From;
import org.example.mtpogr.domain.service.purchase.OrderService;
import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()//classes = StateMachineConfig.class)
class StateMachineDecisionTableTest {
    public void setState(OrderState id)
    {
        StM.stop();
        StM.getStateMachineAccessor()
                .doWithAllRegions(
                        acs -> {
                acs.resetStateMachine(new DefaultStateMachineContext<>(id,null,null,null));
        });
        StM.start();
    }
    @Autowired
    private StateMachineFactory<OrderState,OrderEvent> StMFactory;

    private StateMachine<OrderState,OrderEvent> StM;

    @BeforeEach
    void reNew(){
        StM= StMFactory.getStateMachine();
    }

    @ParameterizedTest
    @MethodSource("BadTestsState")


    @DisplayName("Табличка плохих вариантов")
    void BadTests(OrderState from, OrderState to) {
        setState(from);
        OrderEvent[] all = {OrderEvent.CONFIRM, OrderEvent.PAY,
                OrderEvent.SHIP, OrderEvent.CANCEL, OrderEvent.DELIVER};
        for (OrderEvent how : all) {
            if(!from.equals(to)){
                boolean moved = StM.sendEvent(how);
                if (moved) {
                    assertNotEquals(to, StM.getState().getId());
                    setState(from);
                }}
        }
    }
    @ParameterizedTest
    @MethodSource("GoodTestsState")

    @DisplayName("Табличка вариантов")
    void GoodTests(OrderState from, OrderState to, OrderEvent how)
    {
        setState(from);
        boolean Tr = StM.sendEvent(how);

        Assertions.assertTrue(Tr);
        Assertions.assertEquals(StM.getState().getId(),to);
    }

    static Stream<Arguments> GoodTestsState()
    {
        return Stream.of(
                Arguments.of(OrderState.NEW, OrderState.CANCELLED, OrderEvent.CANCEL),
                Arguments.of(OrderState.NEW, OrderState.CONFIRMED, OrderEvent.CONFIRM),
                Arguments.of(OrderState.CONFIRMED, OrderState.PAID, OrderEvent.PAY),
                Arguments.of(OrderState.PAID, OrderState.SHIPPED, OrderEvent.SHIP),
                Arguments.of(OrderState.SHIPPED, OrderState.DELIVERED, OrderEvent.DELIVER),
                Arguments.of(OrderState.CONFIRMED, OrderState.CANCELLED, OrderEvent.CANCEL),
                Arguments.of(OrderState.PAID, OrderState.CANCELLED, OrderEvent.CANCEL),
                Arguments.of(OrderState.SHIPPED, OrderState.CANCELLED, OrderEvent.CANCEL),
                Arguments.of(OrderState.DELIVERED, OrderState.CANCELLED, OrderEvent.CANCEL)



        );
        // StM.
    }
    static Stream<Arguments> BadTestsState()
    {
        return Stream.of(
                Arguments.of(OrderState.CANCELLED, OrderState.CANCELLED),
                Arguments.of(OrderState.NEW, OrderState.NEW),
                Arguments.of(OrderState.NEW, OrderState.PAID),
                Arguments.of(OrderState.NEW, OrderState.SHIPPED),
                Arguments.of(OrderState.NEW, OrderState.DELIVERED),
                Arguments.of(OrderState.CONFIRMED, OrderState.NEW),
                Arguments.of(OrderState.CONFIRMED, OrderState.CONFIRMED),
                Arguments.of(OrderState.CONFIRMED, OrderState.SHIPPED),
                Arguments.of(OrderState.CONFIRMED, OrderState.DELIVERED),
                Arguments.of(OrderState.PAID, OrderState.NEW),
                Arguments.of(OrderState.PAID, OrderState.CONFIRMED),
                Arguments.of(OrderState.PAID, OrderState.PAID),
                Arguments.of(OrderState.PAID, OrderState.DELIVERED),
                Arguments.of(OrderState.SHIPPED, OrderState.NEW),
                Arguments.of(OrderState.SHIPPED, OrderState.CONFIRMED),
                Arguments.of(OrderState.SHIPPED, OrderState.SHIPPED),
                Arguments.of(OrderState.SHIPPED, OrderState.CONFIRMED),
                Arguments.of(OrderState.DELIVERED, OrderState.NEW),
                Arguments.of(OrderState.DELIVERED, OrderState.CONFIRMED),
                Arguments.of(OrderState.DELIVERED, OrderState.SHIPPED),
                Arguments.of(OrderState.DELIVERED, OrderState.DELIVERED),
                Arguments.of(OrderState.CANCELLED, OrderState.NEW),
                Arguments.of(OrderState.CANCELLED, OrderState.PAID),
                Arguments.of(OrderState.CANCELLED, OrderState.CONFIRMED),
                Arguments.of(OrderState.CANCELLED, OrderState.SHIPPED),
                Arguments.of(OrderState.CANCELLED, OrderState.DELIVERED)

        );
        // StM.
    }



}
