package ru.iteco.nsisync.kafka;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.iteco.nsisync.error.UnknownCatalogException;
import ru.iteco.nsisync.nsi.dto.CatalogInfoDTO;
import ru.iteco.nsisync.nsi.dto.ChangesDTO;
import ru.iteco.nsisync.nsi.service.CatalogService;
import ru.iteco.nsisync.nsi.service.OrganizationRegistryService;

@Service
public class KafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    private final OrganizationRegistryService organizationRegistryService;
    private final CatalogService catalogService;
    private final ObjectMapper objectMapper;

    public KafkaService(OrganizationRegistryService organizationRegistryService,
                        CatalogService catalogService,
                        ObjectMapper objectMapper){
        this.organizationRegistryService = organizationRegistryService;
        this.catalogService = catalogService;
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{'${kafka.topic.nsi}'}")
   /*@KafkaListener(topicPartitions = @TopicPartition(topic = "#{'${kafka.topic.nsi}'}",
            partitionOffsets = {
                    @PartitionOffset(partition = "0", initialOffset = "1241109")
            }))//for tests*/
    public void nsiListener(String message,
                            @Header(KafkaHeaders.OFFSET) Long offset,
                            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        log.info(String.format("Offset %d, Partition_ID %d, Received JSON: %s",
                offset, partitionId, message));
        CatalogInfoDTO dto = objectMapper.readValue(message, CatalogInfoDTO.class);
        commitJson(dto);
    }

    private void commitJson(CatalogInfoDTO message) throws Exception {
        try {
            ChangesDTO catalogChangeData = message.getData().getCatalog().getData();
            switch (message.getCatalogId()) {
                case LEGAL_REPRESENT:
                    catalogService.processLegalRepresent(catalogChangeData);
                    break;
                case CONTACT_TYPE:
                    catalogService.processContractType(catalogChangeData);
                    break;
                case GENDER:
                    catalogService.processGender(catalogChangeData);
                    break;
                case PARALLELS:
                    catalogService.processParallels(catalogChangeData);
                    break;
                case ADMIN_DISTRICT:
                    catalogService.processAdminDistrict(catalogChangeData);
                    break;
                case ORGANIZATION_REGISTRY:
                    organizationRegistryService.processOrganizationRegistry(catalogChangeData);
                    break;
                case CITY_AREAS:
                    catalogService.processCityAreas(catalogChangeData);
                    break;
                case EDUCATION_LEVEL:
                    catalogService.processEducationLevel(catalogChangeData);
                    break;
                case TRAINING_FORM:
                    catalogService.processTrainingForm(catalogChangeData);
                    break;
                default:
                    throw new UnknownCatalogException("Unknown catalog with Name: " + message.getData().getCatalog().getTechnical_name());
            }
        } catch (UnknownCatalogException e) {
            log.warn(String.format("Message ID: %d, Except: %s", message.getId(), e.getMessage()));
        } catch (Exception e) {
            log.error("Can't process message ID: " + message.getId(), e);
            throw e;
        }
    }
}
