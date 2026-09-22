package com.vantair.api.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Address snapshot embedded into an order. Decoupled from {@link Address} so
 * that editing or deleting a saved address never mutates historical orders.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ShippingAddress {
    private String label;
    private String name;
    private String phone;
    private String line1;
    private String city;
    private String state;
    private String pincode;
}
