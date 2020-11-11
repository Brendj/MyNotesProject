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
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class KafkaService {

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaStarshipTemplate;

    @Value(value = "${kafka.topic.dtszn}")
    private String topicName;

    public KafkaService(ObjectMapper objectMapper,
                        KafkaTemplate<String, String> kafkaStarshipTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaStarshipTemplate = kafkaStarshipTemplate;
    }

    public void send(String message){
        ListenableFuture<SendResult<String, String>> future = kafkaStarshipTemplate.send(this.topicName, message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
                        .offset() + "]");
            }

            @Override
            public void onFailure(Throwable e) {
                log.error("Unable to send message=[" + message + "] due to : ", e);
            }
        });
    }
}
