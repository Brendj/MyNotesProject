/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.iteco.dtszn.kafka.dto.SupplyEvent;
import ru.iteco.dtszn.models.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

@Service
public class KafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaStarshipTemplate;

    @Value(value = "${kafka.topic.assign}")
    private String topicAssignName;

    @Value(value = "${kafka.topic.supply}")
    private String topicSupplyName;

    public KafkaService(KafkaTemplate<String, String> kafkaStarshipTemplate) {
        this.kafkaStarshipTemplate = kafkaStarshipTemplate;
    }

    public void sendSupplyMSP(Order order){
        try{
            SupplyEvent event = SupplyEvent.build(order);
            String msg = objectMapper.writeValueAsString(event);
            sendSupply(msg);
        } catch (Exception e){
            log.error(String.format("Can't send Order ID:%s ID OO:%s",
                    order.getCompositeId().getIdOfOrder(), order.getCompositeId().getIdOfOrg()
            ), e);
        }
    }

    private ListenableFuture sendAssign(String message){
        ListenableFuture<SendResult<String, String>> future = kafkaStarshipTemplate.send(this.topicAssignName, message);
        future.addCallback(new LoggingListenableFutureCallback(message));
        return future;
    }

    private ListenableFuture sendSupply(String message){
        ListenableFuture<SendResult<String, String>> future = kafkaStarshipTemplate.send(this.topicSupplyName, message);
        future.addCallback(new LoggingListenableFutureCallback(message));
        return future;
    }
}
