package org.example.mtpogr.FSM.GraphWalk;


import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.ReachedVertex;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.machine.SimpleMachine;
import org.graphwalker.java.test.TestBuilder;
import org.graphwalker.java.test.TestExecutor;
import org.graphwalker.java.test.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GWTest {

    @Autowired
    private GeneratorGraph orderTestContext;

    @Test
    public void runGraphWalkerTest() throws Exception {
        Path modelPath = Paths.get("C:\\Java\\subJava\\MTPOGrAdvancedAutomat\\src\\test\\java\\resourses\\AdvancedOrder.json");

        TestBuilder testExecutor = new TestBuilder()
                .addContext(orderTestContext, modelPath,
                        new RandomPath(new EdgeCoverage(100))
                );

        Result result = testExecutor.execute();

        assertTrue(result.hasErrors() == false);
        System.out.println("Все тесты GraphWalker пройдены!");

        PrintStream fileOut = new PrintStream(new FileOutputStream("graphwalker.log"));
        System.setOut(fileOut);
        System.out.println("Все тесты GraphWalker пройдены!");
        System.out.println("Всего шагов: " + result.getResults());
        System.out.println("Ошибок: " + result.getErrors().size());}
}


























//import org.example.mtpogr.domain.service.purchase.OrderService;
//import org.graphwalker.core.condition.EdgeCoverage;
//import org.graphwalker.core.generator.RandomPath;
//import org.graphwalker.core.machine.SimpleMachine;
//import org.graphwalker.core.model.Model;
//import org.graphwalker.io.factory.json.JsonContext;
//import org.graphwalker.io.factory.json.JsonContextFactory;
//import org.graphwalker.java.test.TestBuilder;
//import org.graphwalker.java.test.TestExecutor;
//import org.graphwalker.java.test.Result;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import static jdk.internal.net.http.RequestPublishers.FilePublisher.create;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//public class GWTest{
//
//    @Autowired
//    private OrderService orderService;
//
//   @Test
////    public void testGraphWalker() throws Exception {
////        // 1. Загружаем модель из JSON-файла
////        Model model = new JsonContext().setModel( )
////                .create(Paths.get("src/test/resources/order_model.json"));
////
////        // 2. Создаём контекст (наш раннер)
////        GeneratorGraph context = new GeneratorGraph(orderService);
////
////        // 3. Настраиваем генератор путей (Random с 100% покрытием рёбер)
////        RandomPath pathGenerator = new RandomPath(new EdgeCoverage(100));
////
////        // 4. Связываем модель, контекст и генератор
////        context.setModel(model);
////        context.setPathGenerator(pathGenerator);
////
////        // 5. Создаём машину и запускаем
////        SimpleMachine machine = new SimpleMachine(context);
////        while (machine.hasNextStep()) {
////            machine.getNextStep();
////        }
////    }
////    }
//        Path modelPath = Paths.get("C:\\Users\\Администратор\\Downloads\\test(1).json");
//
//        TestBuilder testBuilder = new TestBuilder()
//                .addContext(orderTestContext)
//                .setModel(modelPath)
//                .build();
//
//        Result result = testExecutor.execute();
//        assertTrue(result.hasErrors() == false, "Тесты GraphWalker завершились с ошибками");
//        System.out.println("Все тесты GraphWalker пройдены!");
//    }
//}
