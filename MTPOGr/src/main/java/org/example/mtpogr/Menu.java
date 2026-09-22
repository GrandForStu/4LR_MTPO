//package org.example.mtpogr;
//
//import org.example.mtpogr.domain.service.purchase.ProductService;
//import org.example.mtpogr.domain.statemachine.entity.Product;
//import org.example.mtpogr.domain.statemachine.repostory.OrderRepository;
//import org.example.mtpogr.domain.service.purchase.OrderService;
//import org.example.mtpogr.domain.statemachine.action.JsonFileLoader;
//import org.example.mtpogr.domain.statemachine.event.OrderEvent;
//import org.example.mtpogr.domain.statemachine.entity.Order;
//import org.example.mtpogr.domain.statemachine.repostory.ProductRepository;
//import org.example.mtpogr.domain.statemachine.state.OrderState;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.statemachine.StateMachine;
//import org.springframework.statemachine.config.StateMachineFactory;
//import org.springframework.statemachine.persist.StateMachinePersister;
//import org.springframework.stereotype.Component;
//
//import java.nio.file.Path;
//import java.util.*;
//@Component
///**
// * Точка входа в консольное приложение «Система управления заказами».
// */
//public class Menu {
//    @Autowired
//    private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
//
//    @Autowired
//    private StateMachinePersister<OrderState, OrderEvent, Long> persister;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    JsonFileLoader loader;
//
//    @Autowired
//    ProductService productService;
//
//    @Autowired
//    ProductRepository productRepository;
//
//
//    private final Scanner sc = new Scanner(System.in);
//
//
//    public void menu() throws Exception {
//        StateMachine<OrderState, OrderEvent> fsm = stateMachineFactory.getStateMachine();
//        // Загружаем демонстрационные данные
//        //loadDemoData();
//        System.out.println("=== Система управления заказами интернет-магазина ===\n");
//
//        boolean running = true;
//        while (running) {
//            printMainMenu();
//            String choice = sc.nextLine().trim();
//            switch (choice) {
//                case "2" :
//                    ordersMenu();
//                    break;
//                case "1" :
//                    filesMenu();
//                    break;
//                case "0"  :
//                System.out.println("До свидания!");
//                    running = false;
//                break;
//                default   :
//                    System.out.println("Неверный выбор.\n");
//                    break;
//            }
//        }
//    }
//
//    // ------------------------------------------------------------------ //
//    //  Меню                                                                //
//    // ------------------------------------------------------------------ //
//
//    private void printMainMenu() {
//       // System.out.println(" 1. Каталог товаров");
//        System.out.println(" 1. Файлы (JSON)");
//        System.out.println(" 2. Заказы ");
//        System.out.println(" 0. Выход");
//        System.out.print("Ваш выбор: ");
//    }
//
//
//    private void ordersMenu () throws Exception{
//        System.out.println("\n--- Заказы ---");
//        System.out.println("1. Создать заказ");
//        System.out.println("2. Список всех заказов");
//        System.out.println("3. Изменить количество товаров в заказе");
//        System.out.println("4. Закончить формирование заказа");
//        System.out.println("5. Оплатить заказ");
//        System.out.println("6. Отправить заказ");
//        System.out.println("7. Доставить заказ");
//        System.out.println("8. Отменить заказ");
//        System.out.print("Выбор: ");
//        String c = sc.nextLine().trim();
//
//        switch (c) {
//            case "1": createOrderInteractive();
//            break;
//            case "2":
//                for(Order order: orderRepository.findAll())
//            {
//                System.out.print(order.getId() + " "  );
//                System.out.println(order.getState());
//            }
//                break;
//            case "3":
//                Catalogue();
//                break;
//
//            case "4": applyOrderAction(OrderEvent.CONFIRM);
//            break;
//            case "5": applyOrderAction(OrderEvent.PAY);
//                break;
//            case "6": applyOrderAction(OrderEvent.SHIP);
//                break;
//            case "7": applyOrderAction(OrderEvent.DELIVER);
//                break;
//            case "8": applyOrderAction(OrderEvent.CANCEL);
//                break;
//            default : System.out.println("Неверный выбор.");
//                break;
//        }
//        System.out.println();
//    }
//    private void applyOrderAction(OrderEvent ord) throws Exception
//    {
//        System.out.print("Введите ID заказа");
//        String pid = sc.nextLine().trim();
//        Long id = Long.parseLong(pid);
//        orderService.applyEvent(id,ord);
//
//
//    }
//    private void Catalogue()
//    {
//        boolean mew = true;
//
//
//        while(mew)
//        {
//            System.out.println("\n--- Товары ---");
//            System.out.println("0. Добавить товар в каталог товаров");
//            System.out.println("1. Каталог товаров");
//            System.out.println("2. Добавить товар");
//            System.out.println("3. Убрать товар");
//            System.out.println("4. Назад");
//            System.out.print("Выбор: ");
//            String c = sc.nextLine().trim();
//            switch (c)
//            {
//                case "0":
//                    Product product = new Product();
//                    System.out.println("Введите Название товара");
//                    product.setName(sc.nextLine().trim());
//                    System.out.println("Введите Цену товара");
//                    product.setPrice(Double.parseDouble(sc.nextLine().trim()));
//                    System.out.println("Введите Количество товара");
//                    product.setAmount(Integer.parseInt(sc.nextLine().trim()));
//                    productService.create(product);
//                    break;
//                case "1":
//                    for(Product p : productRepository.findAll())
//                    {
//                        System.out.println(p);
//                    }
//                    break;
//                case "2": try{
//                    System.out.println("Введите ID товара");
//                    Long idP = Long.parseLong(sc.nextLine().trim());
//                    System.out.println("Введите ID заказа, в который добавляем заказ");
//                    Long id = Long.parseLong(sc.nextLine().trim());
//                    orderService.addProductToOrder(id, idP);
//                }
//                catch (IllegalArgumentException exception){
//                    System.out.print("Товар не добавлен");}
//                    break;
//                case "3":
//                    System.out.println("Введите ID товара, который удаляем");
//                    Long idP = Long.parseLong(sc.nextLine().trim());
//                    System.out.println("Введите ID заказа, из которого удаляем");
//                    Long id = Long.parseLong(sc.nextLine().trim());
//                    orderService.RemoveProductFromOrder(id, idP);
//                    break;
//
//
//                default:
//                    mew = false;
//                    break;
//            }
//
//        }
//    }
//
//    private void createOrderInteractive() throws Exception {
//
//        Order o = orderService.createOrder();
//        System.out.println("Заказ создан: " + o);
//        o.setId(orderRepository.count());
//    }
//    private void filesMenu() {
//        System.out.println("1. Загрузить список заказов из JSON");
//        System.out.println("2. Сохранить список заказов в JSON");
//        System.out.print("Выбор: ");
//        String c = sc.nextLine().trim();
//        System.out.print("Путь к файлу: ");
//        String Spath = sc.nextLine().trim();
//        Path path = Path.of(Spath);
//        try {
//            if ("1".equals(c)) {
//
//
//                orderRepository.saveAll(loader.loadOrders(path));
//                //service = new OrderService(catalog, repo, fsm);
//                System.out.println("Загружено: " + orderRepository.count() + " заказов"); }
//            else
//            {
//
//                loader.saveOrders(orderRepository.findAll(), path);
//                System.out.println("список заказов сохранён в файле " + path); }
//        } catch (Exception e) {
//            System.out.println("Ошибка: " + e.getMessage());
//        }
//        System.out.println();
//    }
//    }
//
