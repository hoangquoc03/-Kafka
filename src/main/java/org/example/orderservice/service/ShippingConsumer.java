package org.example.orderservice.service;

import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.ShippingStatusEvent;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ShippingConsumer {

    private final OrderRepository repository;

    public ShippingConsumer(OrderRepository repository){

        this.repository = repository;

    }

    @KafkaListener(

            topics="shipping-events",

            groupId="order-group"

    )
    public void receive(

            ShippingStatusEvent event){

        System.out.println(
                "Receive Shipping Event");

        if("DELIVERED".equals(event.getStatus())){

            Order order=

                    repository.findById(

                            event.getOrderId()

                    ).orElseThrow();

            order.setStatus("COMPLETED");

            repository.save(order);

            System.out.println(
                    "Order Completed");

        }

    }

}