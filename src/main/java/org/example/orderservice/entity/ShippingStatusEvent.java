package org.example.orderservice.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingStatusEvent {

    private Long orderId;

    private String customerEmail;

    private String status;

}