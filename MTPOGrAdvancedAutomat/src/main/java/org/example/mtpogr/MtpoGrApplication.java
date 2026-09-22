//package org.example.mtpogr;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.CommandLineRunner;
//
//@SpringBootApplication
//public class MtpoGrApplication implements CommandLineRunner {
//    private final Menu menu;
//
//    public MtpoGrApplication(Menu menu) {
//        this.menu = menu;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(MtpoGrApplication.class, args);
//    }
//
//    @Override
//    public void run(String... args) {
//        try {
//            menu.menu();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
package org.example.mtpogr;//package org.example.mtpogr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
@SpringBootApplication
public class MtpoGrApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtpoGrApplication.class, args);
    }


}
//@SpringBootApplication
//public class MtpoGrApplication implements CommandLineRunner {
//    private final Menu menu;
//
//    public MtpoGrApplication(Menu menu) {
//        this.menu = menu;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(MtpoGrApplication.class, args);
//    }
//
//    @Override
//    @Profile("!test")
//    public void run(String... args) {
//
//        try {
//            menu.menu();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
////import org.springframework.boot.SpringApplication;
////import org.springframework.boot.autoconfigure.SpringBootApplication;
////import org.springframework.boot.CommandLineRunner;
//
////@SpringBootApplication
////public class MtpoGrApplication{
////    public static void main(String[] args) {
////        SpringApplication.run(MtpoGrApplication.class, args);
////    }
////
////    private final Menu menu;
////
////    public MtpoGrApplication(Menu menu) {
////        this.menu = menu;
////    }}
//
////    public static void main(String[] args) {
////        SpringApplication.run(MtpoGrApplication.class, args);
////    }
////
////    @Override
////    public void run(String... args) {
////        try {
////            menu.menu();
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////    }
