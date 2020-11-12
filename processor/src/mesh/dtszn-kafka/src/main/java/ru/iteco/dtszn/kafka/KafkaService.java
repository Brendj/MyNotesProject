/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaStarshipTemplate;

    @Value(value = "${kafka.topic.assign}")
    private String topicAssignName;

    @Value(value = "${kafka.topic.supply}")
    private String topicSupplyName;

    public KafkaService(ObjectMapper objectMapper,
                        KafkaTemplate<String, String> kafkaStarshipTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaStarshipTemplate = kafkaStarshipTemplate;
    }

    public void sendAssign(String message){
        ListenableFuture<SendResult<String, String>> future = kafkaStarshipTemplate.send(this.topicAssignName, message);
        future.addCallback(new LoggingListenableFutureCallback(message));
    }

    public void sendSupply(String message){
        ListenableFuture<SendResult<String, String>> future = kafkaStarshipTemplate.send(this.topicSupplyName, message);
        future.addCallback(new LoggingListenableFutureCallback(message));
    }
}
