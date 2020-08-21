/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.service.CardProcessorService;

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
    private final CardProcessorService cardProcessorService;

    public KafkaService(ObjectMapper objectMapper, CardProcessorService cardProcessorService) {
        this.objectMapper = objectMapper;
        this.cardProcessorService = cardProcessorService;
    }

//    @KafkaListener(topics = "#{'${kafka.topic.card}'}")
    @KafkaListener(topicPartitions = @TopicPartition(topic = "#{'${kafka.topic.card}'}", partitionOffsets = {
            @PartitionOffset(partition = "0", initialOffset = "5695579")}))//for tests
    public void meshListener(String message, @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        log.info(String.format("Offset %d, Partition_ID %d, Received JSON: %s", offset, partitionId, message));
        BlockPersonEntranceRequest request = objectMapper.readValue(message, BlockPersonEntranceRequest.class);
        commitJson(request);
    }

    private void commitJson(BlockPersonEntranceRequest request) {
        switch (request.getAction()) {
            case block:
                cardProcessorService.processBlockRequest(request);
                break;
            case unblock:
                cardProcessorService.processUnblockRequest(request);
        }
    }
}
