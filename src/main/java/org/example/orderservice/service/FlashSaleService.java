package org.example.orderservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.orderservice.config.KafkaProducer;
import org.example.orderservice.entity.FlashSaleOrderEvent;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;



@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleService {


    private final RedissonClient redisson;


    private final RedisTemplate<String, Object> redisTemplate;


    private final KafkaProducer kafkaProducer;



    public boolean buy(
            Long productId,
            Long userId
    ){


        String lockKey =
                "lock:flash-sale:" + productId;



        RLock lock =
                redisson.getLock(lockKey);



        boolean locked = false;



        try {


            // lấy distributed lock

            locked =
                    lock.tryLock(
                            5,
                            10,
                            TimeUnit.SECONDS
                    );



            if(!locked){

                log.warn(
                        "Product {} is busy",
                        productId
                );


                throw new RuntimeException(
                        "Too many buyers"
                );

            }



            /*
             * 1. Trừ tồn kho Redis
             */

            String stockKey =
                    "flash_sale:stock:" + productId;



            Long stock =
                    redisTemplate
                            .opsForValue()
                            .decrement(stockKey);



            log.info(
                    "Product {} remaining stock {}",
                    productId,
                    stock
            );



            /*
             * 2. Hết hàng
             */

            if(stock == null || stock < 0){


                redisTemplate
                        .opsForValue()
                        .increment(stockKey);



                log.info(
                        "Product {} out of stock",
                        productId
                );



                return false;

            }




            /*
             * 3. Tạo Kafka Event
             */


            FlashSaleOrderEvent event =
                    new FlashSaleOrderEvent();


            event.setUserId(userId);

            event.setProductId(productId);

            event.setCreatedAt(
                    LocalDateTime.now()
            );



            /*
             * 4. Gửi Kafka
             */


            kafkaProducer.send(event);



            log.info(
                    "Flash sale success user={} product={}",
                    userId,
                    productId
            );



            return true;



        }
        catch(Exception e){


            log.error(
                    "Flash sale failed",
                    e
            );


            throw new RuntimeException(
                    "Flash sale failed",
                    e
            );

        }
        finally {



            /*
             * Chỉ unlock khi thread này đang giữ lock
             */

            if(
                    locked
                            && lock.isHeldByCurrentThread()
            ){

                lock.unlock();

                log.info(
                        "Release lock {}",
                        lockKey
                );

            }


        }


    }


}