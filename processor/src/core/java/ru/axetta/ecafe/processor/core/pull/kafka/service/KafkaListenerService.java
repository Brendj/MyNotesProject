package ru.axetta.ecafe.processor.core.pull.kafka.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.ActiveBenefitCategoriesGettingResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.RelatednessChecking2Response;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.passport.PassportBySerieNumberValidityCheckingResponse;

@Service
public class KafkaListenerService {

    private final ObjectMapper objectMapper;
    private final KafkaListenerServiceImpl kafkaService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);

    public KafkaListenerService(ObjectMapper objectMapper,
                        KafkaListenerServiceImpl kafkaService) {
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
        try {
            if (data instanceof ActiveBenefitCategoriesGettingResponse) {
                //Обработка информации о льготных категориях
                ApplicationForFood applicationForFood = kafkaService.processingActiveBenefitCategories(data, message);
                if (applicationForFood != null) {
                    Boolean validDoc = applicationForFood.getValidDoc();
                    //Если в заявлении уже был флаг, то запрос на проверку не отправляем
                    if (validDoc == null || !validDoc) {
                        //Отправка запроса на проверку паспорта заявителя
                        RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendRequest(applicationForFood, DocValidationRequest.class);
                    } else
                    {
                        //Исполнение заявления
                        RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).updateApplicationsForFoodKafkaService(applicationForFood);
                    }
                }
            }
            if (data instanceof PassportBySerieNumberValidityCheckingResponse) {
                //Обработка информации о подтверждении паспорта
                ApplicationForFood applicationForFood = kafkaService.processingPassportValidation(data, message);
                if (applicationForFood != null) {
                    Boolean validGuard = applicationForFood.getValidGuardianShip();
                    //Если в заявлении уже был флаг, то запрос на проверку не отправляем
                    if (validGuard == null || !validGuard) {
                        //Отправка запроса на проверку родства
                        RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendRequest(applicationForFood, GuardianshipValidationRequest.class);
                    } else
                    {
                        //Исполнение заявления
                        RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).updateApplicationsForFoodKafkaService(applicationForFood);
                    }
                }
            }
            if (data instanceof RelatednessChecking2Response) {
                //Обработка информации о подтверждении родства
                ApplicationForFood applicationForFood = kafkaService.processingGuardianValidation(data, message);
                if (applicationForFood != null) {
                    //Исполнение заявления
                    RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).updateApplicationsForFoodKafkaService(applicationForFood);
                }
            }
        } catch (Exception e)
        {
            logger.error("Error in parse Response for ZLP");
        }
    }
}
