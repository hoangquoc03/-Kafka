package org.example.orderservice.service;

import org.example.orderservice.entity.Order;
import org.example.orderservice.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository repository;

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public OrderService(OrderRepository repository,
                        KafkaTemplate<String,Object> kafkaTemplate){

        this.repository=repository;

        this.kafkaTemplate=kafkaTemplate;

    }

    public Order createOrder(Order order){

        Order saved=repository.save(order);

        OrderCreatedEvent event=new OrderCreatedEvent(

                saved.getId(),
                saved.getCustomerName(),
                saved.getEmail(),
                saved.getProductName(),
                saved.getQuantity()

        );

        kafkaTemplate.send("order-events",event);

        System.out.println("Event sent");

        return saved;

    }

}