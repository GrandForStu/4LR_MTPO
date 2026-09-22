package org.example.mtpogr.FSM.selfmade;

import org.example.mtpogr.domain.statemachine.event.OrderEvent;
import org.example.mtpogr.domain.statemachine.state.OrderState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.transition.Transition;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MadeByMefrfr {

    @Autowired
    private StateMachineFactory<OrderState,OrderEvent> factory;
    @Test
    void fullMatrixMatchesConfig() throws Exception
    {
        StateMachine<OrderState, OrderEvent> ft = factory.getStateMachine();
        Map<Map<OrderState, OrderEvent>, OrderState> transs = new HashMap<>();
        ft.start();
        for(Transition<OrderState, OrderEvent> tr: ft.getTransitions()){
            transs.put(
                            Map.of(tr.getSource().getId(),tr.getTrigger().getEvent()),
                    tr.getTarget().getId()
            );
    }
        ft.stop();


        for (OrderState from : OrderState.values()) {
            for (OrderEvent event : OrderEvent.values()) {
                StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine();
                sm.getStateMachineAccessor()
                        .doWithAllRegions(access ->
                                access.resetStateMachine(new DefaultStateMachineContext<OrderState, OrderEvent>
                                        (from, null, null, null)));
                sm.start();
                boolean istru = sm.sendEvent(event);
                OrderState expRes = transs.get(Map.of(from,event));
                if (istru) {
                    assertNotNull(expRes, "Событие принято, но перехода нет в конфиге");
                    assertEquals(sm.getState().getId(), expRes);
                    System.out.println("Переход из " + from + " в " + expRes + " через событие " + event + " прошёл успешно");

                } else {
                    assertNull(expRes);
                    System.out.println("Переход из "+ from+ " через событие "+ event+ " невозможен");
                }
                sm.stop();
            }
        }
    }
}
