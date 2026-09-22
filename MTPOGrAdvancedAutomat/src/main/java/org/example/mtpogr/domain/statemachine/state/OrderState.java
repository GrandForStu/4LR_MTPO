package org.example.mtpogr.domain.statemachine.state;

public enum OrderState {
    NEW,DRAFT, CONFIRMED, PAID, PACKED, SHIPPED,
    DELIVERED,COMPLETED, RETURN_INITIATED, SHIPPED_BACK ,
    CANCELLED
}