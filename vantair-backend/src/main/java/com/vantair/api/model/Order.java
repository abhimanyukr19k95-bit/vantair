package com.vantair.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    /** Human-friendly order id, e.g. "VNT12345678". */
    @Id
    private String id;

    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /** Snapshot of the shipping address at order time. */
    @Embedded
    private ShippingAddress address;

    /** "upi" or "cod". */
    private String payment;

    private Integer subtotal;
    private Integer discount;
    private Integer delivery;
    private Integer codFee;
    private Integer total;

    private String couponCode;

    /** Plaintext delivery OTP, only exposed to the owning customer. */
    @JsonIgnore
    private String otp;

    /** Hash the delivery agent validates against. */
    private String otpHash;

    private int otpViews;
    private boolean otpUsed;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.Confirmed;

    private Instant placedAt = Instant.now();
    private Instant deliveredAt;

    private String deliveryPartner;
    private String trackingId;

    @Column(length = 200)
    private String expectedDelivery;

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }
}
