package org.example.mtpogr.domain.statemachine.entity;

import jakarta.persistence.*;
import org.example.mtpogr.domain.statemachine.state.OrderState;

import java.util.ArrayList;
import java.util.List;

//import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderState state;


    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderMtMProduct> products = new ArrayList<>();


    public void setState(OrderState id) {
        this.state = id;
    }
    public OrderState getState() {
        return state;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public List<OrderMtMProduct> getProducts() { return products; }
    public void setProducts(List<OrderMtMProduct> products) { this.products = products; }

    // другие поля: customer, totalAmount, createdAt...

    // геттеры и сеттеры
}