package org.example.orderservice.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlashSaleOrderEvent {


    private Long userId;


    private Long productId;


    private LocalDateTime createdAt;


}