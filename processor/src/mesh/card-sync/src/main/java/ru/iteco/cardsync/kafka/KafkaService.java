/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private final ObjectMapper objectMapper;

    public KafkaService(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{'${kafka.topic.card}'}")
   /*@KafkaListener(topicPartitions = @TopicPartition(topic = "#{'${kafka.topic.card}'}",
            partitionOffsets = {
                    @PartitionOffset(partition = "0", initialOffset = "0")
            }))//for tests*/
    public void meshListener(String message,
            @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        log.info(String.format("Offset %d, Partition_ID %d, Received JSON: %s",
                offset, partitionId, message));
        //EntityChangeEventDTO dto = objectMapper.readValue(message, EntityChangeEventDTO.class);
        commitJson();
    }

    private void commitJson() {
        try {

        } catch (Exception e){
            log.error("Can't process message: ", e);
        }
    }
}
