# Research on the effectiveness of model-based (FSM) test generation  compared to manual scenario writing

## Authors and Contributors

The main contributor *Dmitry Zhevatchenko*, student of SPbPU ICSC.

The advisor and contributor *Vladimir A. Parkhomenko*, Senior Lecturer of SPbPU ICSC.

## Introduction

This is a research project for comparing four approaches to testing a Finite State Machine (FSM) of an e-commerce order lifecycle — **manual decision-table testing**, **GraphWalker**, **ModelJUnit with GreedyTester**, and a **full-matrix introspection method** — with respect to Transition Coverage,Transition-Pair and overage of unspecified (invalid) transitions

The study is conducted on a Spring Boot application with an order state machine implemented via Spring State Machine. The base model contains 6 states and 5 events (9 valid transitions, 21 invalid pairs); the extended AdvancedOrder model contains 11 states and 11 events (16 valid transitions, 105 invalid pairs).

The project is completed during the preparation of Dmitry Zhevatchenko under *Testing of Software* at SPbPU Institute of Computer Science and Cybersecurity (SPbPU ICSC).


## License

MIT License.

Input datasets used in this repository remain under the original licenses specified by their respective authors and sources:

- **GraphWalker models** (JSON files exported from GraphWalker Studio) — covered by the GraphWalker project license.
- **ModelJUnit** library — covered by the ModelJUnit project license.
- **Jazzer** — covered by the Apache 2.0 license.

## Warranty

The developed software is in progress. Authors give no warranty.

## References

- **GraphWalker** — Model-Based Testing tool. URL: https://graphwalker.github.io/
- **ModelJUnit** — Model-Based Testing tool for Java. URL: https://sourceforge.net/projects/modeljunit/
- **JUnit 5** — JUnit 5 User Guide. URL: https://junit.org/junit5/docs/current/user-guide/
- **Mockito** — Mockito Documentation. URL: https://site.mockito.org/
- **Jazzer** — Coverage-Guided, In-Process Fuzzer for the JVM. URL: https://github.com/CodeIntelligenceTesting/jazzer
- **Spring State Machine** — Reference Documentation. URL: https://docs.spring.io/spring-statemachine/docs/current/reference/

## How to Run

### Prerequisites

- Java 21+ (project verified on OpenJDK 21.0.10)
- Gradle (via wrapper `./gradlew`, no separate installation required)

Before running any tests, especially fuzzers, set the environment variables:

**Windows (PowerShell):**
```powershell
$env:JAVA_HOME = "C:\Users\<user>\.jdk\ms-21.0.10"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```
Run continuous fuzzing: ./gradlew fuzz -DfuzzClass=ProductFinderFuz
