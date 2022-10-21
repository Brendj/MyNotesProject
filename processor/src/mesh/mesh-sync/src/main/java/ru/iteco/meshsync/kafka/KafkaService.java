package ru.iteco.meshsync.kafka;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.iteco.meshsync.enums.EntityType;
import ru.iteco.meshsync.kafka.dto.EntityChangeEventDTO;
import ru.iteco.meshsync.mesh.service.logic.MeshService;
import ru.iteco.meshsync.models.EntityChanges;

import java.util.Arrays;
import java.util.List;

@Service
public class KafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private static final List<EntityType> trackedEntities = Arrays.asList(
            EntityType.PERSON,
            EntityType.PERSON_EDUCATION,
            EntityType.CATEGORY,
            EntityType.CLASS,
            EntityType.PERSON_AGENT,
            EntityType.PERSON_CONTACT,
            EntityType.PERSON_DOCUMENT
    );

    private final ObjectMapper objectMapper;
    private final MeshService meshService;

    @Value(value = "${kafka.consumer-id}")
    private String consumerId;

    public KafkaService(ObjectMapper objectMapper,
                        MeshService meshService){
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        this.objectMapper = objectMapper;
        this.meshService = meshService;
    }

    @KafkaListener(topics = "#{'${kafka.topic.mesh}'}")
   /*@KafkaListener(topicPartitions = @TopicPartition(topic = "#{'${kafka.topic.mesh}'}",
            partitionOffsets = {
                    @PartitionOffset(partition = "0", initialOffset = "76360875")
            }))//for tests*/
    public void meshListener(String message,
                            @Header(KafkaHeaders.OFFSET) Long offset,
                            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        log.info(String.format("Offset %d, Partition_ID %d, Received JSON: %s",
                offset, partitionId, message));
        EntityChangeEventDTO dto = objectMapper.readValue(message, EntityChangeEventDTO.class);
        if(dto.getUpdated_by().equals(consumerId)){
            log.info("Self-update message, message skipped");
            return;
        }
        commitJson(dto);
    }

    private void commitJson(EntityChangeEventDTO dto) {
        if (dto.getEntity_name() == null || !trackedEntities.contains(dto.getEntity_name())) {
            log.warn("Not tracked entity for updates: " + dto.getEntity_name());
            return;
        }
        EntityChanges entityChanges = EntityChanges.buildFromDTO(dto);
        dto = null;
        try {
            meshService.processMsg(entityChanges);
        } catch (Exception e) {
            log.error("Can't process message: ", e);
        }
    }
}
