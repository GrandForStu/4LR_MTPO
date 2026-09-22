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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GWTest {

    @Autowired
    private GeneratorGraph orderTestContext;

    @Test
    public void runGraphWalkerTest() throws Exception {
        Path modelPath = Paths.get("C:\\Java\\subJava\\MTPOGr\\src\\test\\java\\resourses\\test(1).json");

        TestBuilder testExecutor = new TestBuilder()
                .addContext(orderTestContext, modelPath,
                        new RandomPath(new EdgeCoverage(100))
                );

        Result result = testExecutor.execute();

        assertFalse(result.hasErrors());
        System.out.println("Все тесты GraphWalker пройдены!");

        PrintStream fileOut = new PrintStream(new FileOutputStream("graphwalker.log"));
        System.setOut(fileOut);
        System.out.println("Все тесты GraphWalker пройдены!");
        System.out.println("Всего шагов: " + result.getResults());
        System.out.println("Ошибок: " + result.getErrors().size());}
}




















