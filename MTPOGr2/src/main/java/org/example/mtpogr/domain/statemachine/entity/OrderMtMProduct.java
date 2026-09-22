package org.example.mtpogr.domain.statemachine.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderMtMProduct
{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(optional = false)
        @JoinColumn(name = "order_id")
        @JsonBackReference
        // @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        private Order orders;            // к какому заказу относится

        @ManyToOne(optional = false)
        @JoinColumn(name = "product_id")

        private Product Catalog;        // какой товар

        @Column(nullable = false)
        private int quantity;       // сколько штук

        @Column(nullable = false)
        private double priceAtOrder; // цена на момент заказа (фиксируем!)

        public Long getId() { return id; }

        public Order getOrder() { return orders; }
        public void setOrder(Order order) { this.orders = order; }

        public Product getCatalog() { return Catalog; }
        public void setCatalog(Product catalog) { this.Catalog = catalog; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public double getPriceAtOrder() { return priceAtOrder; }
        public void setPriceAtOrder(double priceAtOrder) { this.priceAtOrder = priceAtOrder; }

}
