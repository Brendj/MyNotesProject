package ru.axetta.ecafe.processor.core.zlp.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.etp.CoordinateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.push.kafka.service.KafkaService;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.PersonBenefitCheckRequest;

@Configuration
@Service
public class BenefitKafkaService {
    private static final Logger logger = LoggerFactory.getLogger(BenefitKafkaService.class);
    public static final String REQUEST_SYSTEM_PROPERTY = "ecafe.processing.zlp.service.request_system";
    public static final String REQUEST_SYSTEM_DEFAULT = "pp";
    public static final String REQUEST_METHOD_GUARDIANSHIP = "relatedness_checking";
    public static final String REQUEST_METHOD_BENEFIT = "active_benefit_categories_getting_request";
    public static final String REQUEST_METHOD_DOC = "passport_validity_checking";
    public static final String STATE_SERVICE_VARIETY_CODE_DEFAULT = "100101";
    public static final String STATE_SERVICE_VARIETY_CODE_PROPERTY = "ecafe.processing.zlp.service.code";

//    public static final String RESPONSE_METHOD_BENEFIT = "active_benefit_categories_getting_response";
//    public static final String RESPONSE_METHOD_DOC = "passport_validity_checking_response";
//    public static final String RESPONSE_METHOD_GUARDIANSHIP = "relatedness_checking_response";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> benefitKafkaTemplate;

    public BenefitKafkaService(KafkaTemplate<String, String> benefitKafkaTemplate) {
        this.benefitKafkaTemplate = benefitKafkaTemplate;
    }

    public void sendBenefitValidationRequest(String serviceNumber) {
        ApplicationForFood applicationForFood = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                .getApplicationForFoodWithDtisznCodes(serviceNumber);
        sendBenefitValidationRequest(applicationForFood);
    }

    private String getTopicFromConfig(AbstractPushData data) throws Exception {
        String topicLink = null;
        if (data instanceof GuardianshipValidationRequest)
            topicLink = AbstractPushData.GUARDIANSHIP_VALIDATION_REQUEST_TOPIC;
        else if (data instanceof PersonBenefitCheckRequest)
            topicLink = AbstractPushData.BENEFIT_VALIDATION_REQUEST_TOPIC;
        else if (data instanceof DocValidationRequest)
            topicLink = AbstractPushData.DOC_VALIDATION_REQUEST_TOPIC;
        String address = RuntimeContext.getInstance().getConfigProperties().getProperty(topicLink, "");
        if (address.equals(""))
            throw new Exception(String.format("Kafka topic not specified, topicLink: %s", topicLink));
        return address;
    }

    public void sendBenefitValidationRequest(ApplicationForFood applicationForFood) {
        try {
            AbstractPushData request = new PersonBenefitCheckRequest(applicationForFood);
            Message<AbstractPushData> message = MessageBuilder.withPayload(request).build();
            String msg = objectMapper.writeValueAsString(request);
            String topic = getTopicFromConfig(request);
            ListenableFuture<SendResult<String, String>> future = benefitKafkaTemplate.send(getTopicFromConfig(request), msg);
            future.addCallback(new ZlpLoggingListenableFutureCallback(message, msg, topic, 2, applicationForFood.getIdOfApplicationForFood(),
                    applicationForFood.getServiceNumber(), null));
        } catch (Exception e) {
            logger.error(String.format("Error in sendBenefitValidationRequest for serviceNumber = %s", applicationForFood.getServiceNumber()), e);
        }
    }

    private Integer getTypeClazz(AbstractPushData data) throws Exception {
        Integer type = null;
        if (data instanceof GuardianshipValidationRequest)
            type = 1;
//        else if (data instanceof PersonBenefitCheckRequest)
//            type = 2;
        else if (data instanceof DocValidationRequest)
            type = 3;
        if (type == null)
            throw new Exception("Kafka topic not specified, topicLink");
        return type;
    }

    public void sendRequest(ApplicationForFood applicationForFood, Class<? extends AbstractPushData> clazz) {
        try {
            RequestValidationData data = getBenefitData(applicationForFood);
            AbstractPushData request = clazz.getDeclaredConstructor(RequestValidationData.class).newInstance(data);
            Message<AbstractPushData> message = MessageBuilder.withPayload(request).build();
            String msg = objectMapper.writeValueAsString(request);
            String topic = getTopicFromConfig(request);
            ListenableFuture<SendResult<String, String>> future = benefitKafkaTemplate.send(topic, msg);
            future.addCallback(new ZlpLoggingListenableFutureCallback(message, msg, topic, getTypeClazz(request), data.getIdOfApplicationForFood(), applicationForFood.getServiceNumber(), null));
        } catch (Exception e) {
            logger.error(String.format("Error in sendRequest for serviceNumber = %s", applicationForFood.getServiceNumber()), e);
        }
    }

    public static RequestValidationData getBenefitData(ApplicationForFood applicationForFood) throws Exception {
        String message = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                .getOriginalMessageFromApplicationForFood(applicationForFood);
        CoordinateMessage coordinateMessage = (CoordinateMessage)RuntimeContext.getAppContext().getBean(ETPMVService.class).getCoordinateMessage(message);
        return new RequestValidationData(coordinateMessage, applicationForFood.getIdOfApplicationForFood());
    }

}
