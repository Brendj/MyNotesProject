/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.emias.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.iteco.emias.kafka.Request.PersonExemption;
import ru.iteco.emias.service.EmiasProcessorService;

@Service
public class KafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private final ObjectMapper objectMapper;
    private final EmiasProcessorService emiasProcessorService;

    public KafkaService(ObjectMapper objectMapper, EmiasProcessorService emiasProcessorService) {
        this.objectMapper = objectMapper;
        this.emiasProcessorService = emiasProcessorService;
    }

    @KafkaListener(topics = "#{'${kafka.topic.emias}'}")

//        @KafkaListener(topicPartitions = @TopicPartition(topic = "#{'${kafka.topic.emias}'}", partitionOffsets = {
//            @PartitionOffset(partition = "0", initialOffset = "0")}))//for tests
    public void meshListener(String message, @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        try {
            PersonExemption request = objectMapper.readValue(message, PersonExemption.class);
            emiasProcessorService.processEmiasRequest(request);
        } catch (Exception e)
        {
            log.error("internal error " + e);
        }
    }
}
