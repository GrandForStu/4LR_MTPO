# Research on the effectiveness of model-based (FSM) test generation  compared to manual scenario writing

## Authors and Contributors

The main contributors *Dmitry Zhevatchenko*, student of SPbPU ICSC.

The advisor and contributor *Vladimir A. Parkhomenko*, Senior Lecturer of SPbPU ICSC.

## Introduction

This is a research project for comparing four Test Case Prioritization (TCP) algorithms — **Total Coverage**, **Additional Coverage**, **MOTCP (Ranked NSGA-II)**, and **CAP (Cluster-based Adaptive Prioritization)** — on real-world defects from the **BugsInPy** benchmark.

The project is completed during the preparation of Dmitry Zhevatchenko under *Testing of Software* at SPbPU Institute of Computer Science and Cybersecurity (SPbPU ICSC).

## License

MIT License.

Input datasets used in this repository remain under the original licenses specified by their respective authors and sources:

- **BugsInPy** — see its repository for license details.

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
