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
import ru.axetta.ecafe.processor.core.proactive.kafka.model.response.PersonBenefitCategoryChanges;
import ru.axetta.ecafe.processor.core.proactive.service.PersonBenefitCategoryService;
import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.ActiveBenefitCategoriesGettingResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.GuardianResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.passport.PassportResponse;

@Service
public class KafkaListenerService {

    private final ObjectMapper objectMapper;
    private final KafkaListenerServiceImpl kafkaService;
    private final PersonBenefitCategoryService personBenefitCategoryService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);

    public KafkaListenerService(ObjectMapper objectMapper,
                                KafkaListenerServiceImpl kafkaService,
                                PersonBenefitCategoryService personBenefitCategoryService) throws Exception {
        this.objectMapper = objectMapper;
        this.kafkaService = kafkaService;
        this.personBenefitCategoryService = personBenefitCategoryService;
    }

    @KafkaListener(topics = {AbstractPullData.BENEFIT_VALIDATION_RESPONSE_TOPIC,
            AbstractPullData.GUARDIANSHIP_VALIDATION_RESPONSE_TOPIC,
            AbstractPullData.DOC_VALIDATION_RESPONSE_TOPIC})
//        @KafkaListener(topicPartitions = @TopicPartition(topic = "#{'${kafka.topic.card}'}", partitionOffsets = {
//            @PartitionOffset(partition = "0", initialOffset = "0")}))//for tests
    public void MezvedListener(String message, @Header(KafkaHeaders.OFFSET) Long offset,
                               @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        logger.info(String.format("Response from kafka. Offset = %s, partitionId = %s, message = %s", offset, partitionId, message));
        AbstractPullData responseData = (AbstractPullData) objectMapper.readValue(message, getMessageType(message));
        RuntimeContext.getAppContext().getBean(KafkaListenerService.class).parseResponseMessage(responseData, message);
    }

    @KafkaListener(topics = {AbstractPullData.BENEFIT_RESPONSE_TOPIC})
    public void MosBenefitListener(String message, @Header(KafkaHeaders.OFFSET) Long offset,
                                    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partitionId) throws Exception {
        logger.info(String.format("Response from kafka. Offset = %s, partitionId = %s, message = %s", offset, partitionId, message));
        PersonBenefitCategoryChanges responseData = objectMapper.readValue(message, PersonBenefitCategoryChanges.class);
        personBenefitCategoryService.parseResponseMessage(responseData);
    }

    private Class<?> getMessageType(String message) throws Exception {
        if (message.contains("benefit_categories")) return ActiveBenefitCategoriesGettingResponse.class;
        if (message.contains("passport_validity_checking_response")) return PassportResponse.class;
        if (message.contains("relatedness_checking_response")) return GuardianResponse.class;
        throw new Exception("Unknown message type");
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
                    } else {
                        Boolean validGuardianship = applicationForFood.getValidGuardianShip();
                        if (validGuardianship == null || !validGuardianship) {
                            RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendRequest(applicationForFood, GuardianshipValidationRequest.class);
                        } else {
                            //Исполнение заявления
                            RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).updateApplicationsForFoodKafkaService(applicationForFood);
                        }
                    }
                }
            }
            if (data instanceof PassportResponse) {
                //Обработка информации о подтверждении паспорта
                ApplicationForFood applicationForFood = kafkaService.processingPassportValidation(data, message);
                if (applicationForFood != null) {
                    Boolean validGuard = applicationForFood.getValidGuardianShip();
                    //Если в заявлении уже был флаг, то запрос на проверку не отправляем
                    if (validGuard == null || !validGuard) {
                        //Отправка запроса на проверку родства
                        RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendRequest(applicationForFood, GuardianshipValidationRequest.class);
                    } else {
                        //Исполнение заявления
                        RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).updateApplicationsForFoodKafkaService(applicationForFood);
                    }
                }
            }
            if (data instanceof GuardianResponse) {
                //Обработка информации о подтверждении родства
                ApplicationForFood applicationForFood = kafkaService.processingGuardianValidation(data, message);
                if (applicationForFood != null) {
                    //Исполнение заявления
                    RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).updateApplicationsForFoodKafkaService(applicationForFood);
                }
            }
        } catch (Exception e) {
            logger.error("Error in parse Response for ZLP");
        }
    }
}
