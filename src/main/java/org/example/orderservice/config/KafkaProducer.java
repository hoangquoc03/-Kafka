package org.example.orderservice.config;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.entity.FlashSaleOrderEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class KafkaProducer {


    private final KafkaTemplate<String, Object> kafkaTemplate;



    private static final String TOPIC =
            "flash-sale-order-topic";



    public void send(
            FlashSaleOrderEvent event
    ){


        kafkaTemplate.send(
                TOPIC,
                event
        );


    }


}