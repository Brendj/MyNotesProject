package ru.axetta.ecafe.processor.core.pull.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.ActiveBenefitCategoriesGettingResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.RelatednessChecking2Response;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.passport.PassportBySerieNumberValidityCheckingResponse;

@Service
public class KafkaService {

    private final ObjectMapper objectMapper;
    private final KafkaServiceImpl kafkaService;

    public KafkaService(ObjectMapper objectMapper,
                        KafkaServiceImpl kafkaService) {
        this.objectMapper = objectMapper;
        this.kafkaService = kafkaService;
    }

    @KafkaListener(topics = {AbstractPullData.BENEFIT_VALIDATION_RESPONSE_TOPIC,
                             AbstractPullData.GUARDIANSHIP_VALIDATION_RESPONSE_TOPIC,
                             AbstractPullData.DOC_VALIDATION_RESPONSE_TOPIC})
//        @KafkaListener(topicPartitions = @TopicPartition(topic = "#{'${kafka.topic.card}'}", partitionOffsets = {
//            @PartitionOffset(partition = "0", initialOffset = "0")}))//for tests
    public void MezvedListener(String message, @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        AbstractPullData responseData = objectMapper.readValue(message, AbstractPullData.class);
        parseResponseMessage(responseData, message);
    }


    @Async
    public void parseResponseMessage(AbstractPullData data, String message) {
        if (data instanceof ActiveBenefitCategoriesGettingResponse)
        {
            //Обработка информации о льготных категориях
            kafkaService.processingActiveBenefitCategories(data, message);
        }
        if (data instanceof PassportBySerieNumberValidityCheckingResponse)
        {
            //Обработка информации о подтверждении паспорта
            kafkaService.processingPassportValidation(data, message);
        }
        if (data instanceof RelatednessChecking2Response)
        {
            //Обработка информации о подтверждении родства
            kafkaService.processingGuardianValidation(data, message);
        }
    }
}
