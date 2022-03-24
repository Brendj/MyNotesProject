/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import ru.iteco.cardsync.beans.RunnableBlockCardThreadWrapper;
import ru.iteco.cardsync.enums.ActionType;
import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;
import ru.iteco.cardsync.models.CardActionRequest;
import ru.iteco.cardsync.service.CardActionRequestService;
import ru.iteco.cardsync.service.CardProcessorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class KafkaService {

    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private final ObjectMapper objectMapper;
    private final CardProcessorService cardProcessorService;
    private final CardActionRequestService cardActionRequestService ;

    @Resource(name = "cardBlockingTaskExecutor")
    protected TaskExecutor taskExecutor;

    public KafkaService(ObjectMapper objectMapper, CardProcessorService cardProcessorService,
                        CardActionRequestService cardActionRequestService) {
        this.objectMapper = objectMapper;
        this.cardProcessorService = cardProcessorService;
        this.cardActionRequestService = cardActionRequestService;
    }

    public boolean blockCardAsync(BlockPersonEntranceRequest request, Long offset, Integer partitionId, String message) {
        RunnableBlockCardThreadWrapper wrapper = new RunnableBlockCardThreadWrapper(request, offset, partitionId, message, cardProcessorService, cardActionRequestService);
        taskExecutor.execute(wrapper);
        return true;
    }
    
    @KafkaListener(topics = "#{'${kafka.topic.card}'}")
//        @KafkaListener(topicPartitions = @TopicPartition(topic = "#{'${kafka.topic.card}'}", partitionOffsets = {
//            @PartitionOffset(partition = "0", initialOffset = "0")}))//for tests
    public void meshListener(String message, @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        try {
            BlockPersonEntranceRequest request = objectMapper.readValue(message, BlockPersonEntranceRequest.class);
            log.info(String.format("Offsets corrent: %s", offset));
            blockCardAsync(request, offset, partitionId, message);
        }
        catch (Exception e)
            {
                System.out.println(e);
            }
    }
}
