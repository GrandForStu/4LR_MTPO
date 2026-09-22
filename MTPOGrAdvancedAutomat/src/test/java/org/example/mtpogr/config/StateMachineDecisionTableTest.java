package org.example.mtpogr.config;

import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineDecisionTableTest {

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> StMFactory;

    private StateMachine<OrderState, OrderEvent> StM;

    public void setState(OrderState id) {
        StM.stop();
        StM.getStateMachineAccessor()
                .doWithAllRegions(acs ->
                        acs.resetStateMachine(
                                new DefaultStateMachineContext<>(id, null, null, null)
                        ));
        StM.start();
    }

    @BeforeEach
    void reNew() {
        StM = StMFactory.getStateMachine();
    }

    @ParameterizedTest
    @MethodSource("BadTestsState")
    @DisplayName("Табличка плохих вариантов AdvancedOrder")
    void BadTests(OrderState from, OrderState to) {
        setState(from);

        OrderEvent[] all = {OrderEvent.CONFIRM, OrderEvent.PAY,
                OrderEvent.SHIP, OrderEvent.CANCEL, OrderEvent.DELIVER,
                OrderEvent.TAKE, OrderEvent.RETURN, OrderEvent.PACK,
                OrderEvent.RETURN_CONFIRMED, OrderEvent.ADD_PRODUCT
        };

        for (OrderEvent how : all) {
            if (!from.equals(to)) {
                boolean moved = StM.sendEvent(how);
                if (moved) {
                    assertNotEquals(to, StM.getState().getId());
                    setState(from);
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("GoodTestsState")
    @DisplayName("Табличка вариантов AdvancedOrder")
    void GoodTests(OrderState from, OrderState to, OrderEvent how) {
        setState(from);

        boolean moved = StM.sendEvent(how);

        assertTrue(moved, "Переход " + from + " --" + how + "--> " + to + " не сработал");
        assertEquals(to, StM.getState().getId(), "Ожидалось " + to + " после " + from + " --" + how + "--> " + to
        );
    }

    static Stream<Arguments> GoodTestsState() {
        return Stream.of(
                Arguments.of(OrderState.NEW, OrderState.DRAFT, OrderEvent.ADD_PRODUCT),
                Arguments.of(OrderState.DRAFT, OrderState.CONFIRMED, OrderEvent.CONFIRM),
                Arguments.of(OrderState.DRAFT, OrderState.CANCELLED, OrderEvent.CANCEL),
                Arguments.of(OrderState.CONFIRMED, OrderState.PAID, OrderEvent.PAY),
                Arguments.of(OrderState.CONFIRMED, OrderState.CANCELLED, OrderEvent.CANCEL),
                Arguments.of(OrderState.PAID, OrderState.PACKED, OrderEvent.PACK),
                Arguments.of(OrderState.PAID, OrderState.CANCELLED, OrderEvent.CANCEL),
                Arguments.of(OrderState.PACKED, OrderState.SHIPPED, OrderEvent.SHIP),
                Arguments.of(OrderState.SHIPPED, OrderState.DELIVERED, OrderEvent.DELIVER),
                Arguments.of(OrderState.SHIPPED, OrderState.CANCELLED, OrderEvent.CANCEL),
                Arguments.of(OrderState.DELIVERED, OrderState.COMPLETED, OrderEvent.TAKE),
                Arguments.of(OrderState.DELIVERED, OrderState.SHIPPED_BACK, OrderEvent.CANCEL),
                Arguments.of(OrderState.COMPLETED, OrderState.RETURN_INITIATED, OrderEvent.RETURN),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.SHIPPED_BACK, OrderEvent.RETURN_CONFIRMED),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.CANCELLED, OrderEvent.DELIVER)

        );
    }

    static Stream<Arguments> BadTestsState() {
        return Stream.of(
                Arguments.of(OrderState.NEW, OrderState.CONFIRMED),
                Arguments.of(OrderState.NEW, OrderState.PAID),
                Arguments.of(OrderState.NEW, OrderState.PACKED),
                Arguments.of(OrderState.NEW, OrderState.SHIPPED),
                Arguments.of(OrderState.NEW, OrderState.DELIVERED),
                Arguments.of(OrderState.NEW, OrderState.COMPLETED),
                Arguments.of(OrderState.NEW, OrderState.RETURN_INITIATED),
                Arguments.of(OrderState.NEW, OrderState.SHIPPED_BACK),
                Arguments.of(OrderState.NEW, OrderState.CANCELLED),

                Arguments.of(OrderState.DRAFT, OrderState.NEW),
                Arguments.of(OrderState.DRAFT, OrderState.PAID),
                Arguments.of(OrderState.DRAFT, OrderState.PACKED),
                Arguments.of(OrderState.DRAFT, OrderState.SHIPPED),
                Arguments.of(OrderState.DRAFT, OrderState.DELIVERED),
                Arguments.of(OrderState.DRAFT, OrderState.COMPLETED),
                Arguments.of(OrderState.DRAFT, OrderState.RETURN_INITIATED),
                Arguments.of(OrderState.DRAFT, OrderState.SHIPPED_BACK),

                Arguments.of(OrderState.CONFIRMED, OrderState.NEW),
                Arguments.of(OrderState.CONFIRMED, OrderState.DRAFT),
                Arguments.of(OrderState.CONFIRMED, OrderState.PACKED),
                Arguments.of(OrderState.CONFIRMED, OrderState.SHIPPED),
                Arguments.of(OrderState.CONFIRMED, OrderState.DELIVERED),
                Arguments.of(OrderState.CONFIRMED, OrderState.COMPLETED),
                Arguments.of(OrderState.CONFIRMED, OrderState.RETURN_INITIATED),
                Arguments.of(OrderState.CONFIRMED, OrderState.SHIPPED_BACK),

                Arguments.of(OrderState.PAID, OrderState.NEW),
                Arguments.of(OrderState.PAID, OrderState.DRAFT),
                Arguments.of(OrderState.PAID, OrderState.CONFIRMED),
                Arguments.of(OrderState.PAID, OrderState.SHIPPED),
                Arguments.of(OrderState.PAID, OrderState.DELIVERED),
                Arguments.of(OrderState.PAID, OrderState.COMPLETED),
                Arguments.of(OrderState.PAID, OrderState.RETURN_INITIATED),
                Arguments.of(OrderState.PAID, OrderState.SHIPPED_BACK),

                Arguments.of(OrderState.PACKED, OrderState.NEW),
                Arguments.of(OrderState.PACKED, OrderState.DRAFT),
                Arguments.of(OrderState.PACKED, OrderState.CONFIRMED),
                Arguments.of(OrderState.PACKED, OrderState.PAID),
                Arguments.of(OrderState.PACKED, OrderState.DELIVERED),
                Arguments.of(OrderState.PACKED, OrderState.COMPLETED),
                Arguments.of(OrderState.PACKED, OrderState.RETURN_INITIATED),
                Arguments.of(OrderState.PACKED, OrderState.SHIPPED_BACK),
                Arguments.of(OrderState.PACKED, OrderState.CANCELLED),

                // SHIPPED -> ...
                Arguments.of(OrderState.SHIPPED, OrderState.NEW),
                Arguments.of(OrderState.SHIPPED, OrderState.DRAFT),
                Arguments.of(OrderState.SHIPPED, OrderState.CONFIRMED),
                Arguments.of(OrderState.SHIPPED, OrderState.PAID),
                Arguments.of(OrderState.SHIPPED, OrderState.PACKED),
                Arguments.of(OrderState.SHIPPED, OrderState.COMPLETED),
                Arguments.of(OrderState.SHIPPED, OrderState.RETURN_INITIATED),
                Arguments.of(OrderState.SHIPPED, OrderState.SHIPPED_BACK),

                Arguments.of(OrderState.DELIVERED, OrderState.NEW),
                Arguments.of(OrderState.DELIVERED, OrderState.DRAFT),
                Arguments.of(OrderState.DELIVERED, OrderState.CONFIRMED),
                Arguments.of(OrderState.DELIVERED, OrderState.PAID),
                Arguments.of(OrderState.DELIVERED, OrderState.PACKED),
                Arguments.of(OrderState.DELIVERED, OrderState.SHIPPED),
                Arguments.of(OrderState.DELIVERED, OrderState.RETURN_INITIATED),
                Arguments.of(OrderState.DELIVERED, OrderState.CANCELLED),

                Arguments.of(OrderState.COMPLETED, OrderState.NEW),
                Arguments.of(OrderState.COMPLETED, OrderState.DRAFT),
                Arguments.of(OrderState.COMPLETED, OrderState.CONFIRMED),
                Arguments.of(OrderState.COMPLETED, OrderState.PAID),
                Arguments.of(OrderState.COMPLETED, OrderState.PACKED),
                Arguments.of(OrderState.COMPLETED, OrderState.SHIPPED),
                Arguments.of(OrderState.COMPLETED, OrderState.DELIVERED),
                Arguments.of(OrderState.COMPLETED, OrderState.SHIPPED_BACK),
                Arguments.of(OrderState.COMPLETED, OrderState.CANCELLED),

                Arguments.of(OrderState.RETURN_INITIATED, OrderState.NEW),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.DRAFT),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.CONFIRMED),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.PAID),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.PACKED),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.SHIPPED),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.DELIVERED),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.COMPLETED),
                Arguments.of(OrderState.RETURN_INITIATED, OrderState.CANCELLED),

                Arguments.of(OrderState.SHIPPED_BACK, OrderState.NEW),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.DRAFT),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.CONFIRMED),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.PAID),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.PACKED),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.SHIPPED),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.DELIVERED),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.COMPLETED),
                Arguments.of(OrderState.SHIPPED_BACK, OrderState.RETURN_INITIATED),

                Arguments.of(OrderState.CANCELLED, OrderState.DRAFT),
                Arguments.of(OrderState.CANCELLED, OrderState.CONFIRMED),
                Arguments.of(OrderState.CANCELLED, OrderState.PAID),
                Arguments.of(OrderState.CANCELLED, OrderState.PACKED),
                Arguments.of(OrderState.CANCELLED, OrderState.SHIPPED),
                Arguments.of(OrderState.CANCELLED, OrderState.DELIVERED),
                Arguments.of(OrderState.CANCELLED, OrderState.COMPLETED),
                Arguments.of(OrderState.CANCELLED, OrderState.RETURN_INITIATED),
                Arguments.of(OrderState.CANCELLED, OrderState.SHIPPED_BACK)
        );
    }
}