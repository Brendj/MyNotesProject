package ru.iteco.meshsync.kafka;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.iteco.meshsync.EntityType;
import ru.iteco.meshsync.kafka.dto.EntityChangeEventDTO;
import ru.iteco.meshsync.mesh.service.DAO.EntityChangesService;
import ru.iteco.meshsync.mesh.service.logic.MeshService;
import ru.iteco.meshsync.models.EntityChanges;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class KafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private static final Set<EntityType> trackedEntities = Stream.of(
            EntityType.person,
            EntityType.person_education,
            EntityType.category
    ).collect(Collectors.toSet());

    private final ObjectMapper objectMapper;
    private final MeshService meshService;
    private final EntityChangesService entityChangesService;

    public KafkaService(ObjectMapper objectMapper,
                        MeshService meshService,
                        EntityChangesService entityChangesService){
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        this.objectMapper = objectMapper;
        this.meshService = meshService;
        this.entityChangesService = entityChangesService;
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
        commitJson(dto);
    }

    private void commitJson(EntityChangeEventDTO dto) {
        if(!trackedEntities.contains(dto.getEntity_name())){
            log.warn("Not tracked entity for updates: " + dto.getEntity_name());
            return;
        }
        EntityChanges entityChanges = EntityChanges.buildFromDTO(dto);
        dto = null;
        try {
            boolean process = meshService.processEntityChanges(entityChanges);
        } catch (Exception e){
            log.error("Can't process message: ", e);
        }
    }
}