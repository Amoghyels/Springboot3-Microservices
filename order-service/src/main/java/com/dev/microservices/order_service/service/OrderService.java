package com.dev.microservices.order_service.service;

import com.dev.microservices.order_service.client.InventoryClient;
import com.dev.microservices.order_service.dto.OrderRequest;
import com.dev.microservices.order_service.event.OrderPlacedEvent;
import com.dev.microservices.order_service.model.Order;
import com.dev.microservices.order_service.repository.OrderRepository;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;



@Slf4j

@Service
@RequiredArgsConstructor

public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate <String, OrderPlacedEvent> kafkaTemplate;
    public void placeOrder(OrderRequest orderRequest){
       var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
       log.info("{}",isProductInStock);

       if(isProductInStock){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setPrice(orderRequest.price());
        order.setSkuCode(orderRequest.skuCode());
        order.setQuantity(orderRequest.quantity());
        orderRepository.save(order);

           OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
           orderPlacedEvent.setOrderNumber(order.getOrderNumber());
           orderPlacedEvent.setEmail(orderRequest.userDetails().email());
           orderPlacedEvent.setFirstName(orderRequest.userDetails().firstName());
           orderPlacedEvent.setLastName(orderRequest.userDetails().lastName());
           log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
           kafkaTemplate.send("order-placed",orderPlacedEvent);
           log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-placed", orderPlacedEvent);
       }else{
           throw new RuntimeException("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
       }



    }

}
