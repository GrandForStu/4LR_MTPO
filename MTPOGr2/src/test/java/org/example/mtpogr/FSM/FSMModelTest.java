package org.example.mtpogr.FSM;

import nz.ac.waikato.modeljunit.GreedyTester;
import nz.ac.waikato.modeljunit.Tester;
import nz.ac.waikato.modeljunit.VerboseListener;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionPairCoverage;
import org.example.mtpogr.MtpoGrApplication;
import org.example.mtpogr.domain.service.purchase.OrderService;
import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MtpoGrApplication.class)

class FSMModelTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void GreedyStuff(){
        Generator model = new Generator(orderService, orderRepository);
        Tester tester = new GreedyTester(model);


        tester.addCoverageMetric(new TransitionPairCoverage());
        tester.addCoverageMetric(new ActionCoverage());
        tester.addCoverageMetric(new TransitionCoverage());
        tester.addCoverageMetric(new StateCoverage());

        tester.addListener(new VerboseListener());
        tester.buildGraph();


        tester.generate(20);

           // System.out.println("Покрытие:");
            //for(CoverageMetric m: tester.getCoverageMetrics())
            tester.printCoverage();

    }

}
