package org.example.mtpogr.domain.statemachine.entity;

import jakarta.persistence.*;
import org.springframework.security.core.parameters.P;
//import org.springframework.data.annotation.Id;

@Entity
@Table(name = "Catalog")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer amount;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer stock) { this.amount = stock; }
}
