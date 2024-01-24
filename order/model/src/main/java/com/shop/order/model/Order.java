package com.shop.order.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Order code is mandatory!")
    @Size(min = 20, max = 20, message = "Order code must contains 20 symbols!")
    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @Size(max = 32, message = "Order status can't be large than 32 symbols!")
    @Column(name = "status", length = 32)
    private String status;

    @NotNull(message = "Order user id is mandatory!")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Order total price is mandatory!")
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @NotNull(message = "Order created time is mandatory!")
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @NotNull(message = "Order last ime update is mandatory!")
    @Column(name = "last_time_update", nullable = false)
    private LocalDateTime lastTimeUpdate;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "delivery_time")
    private LocalDateTime deliveryTime;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "order_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderUnit> orderUnits = new ArrayList<>();

}
