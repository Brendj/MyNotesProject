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
import ru.axetta.ecafe.processor.core.push.model.BalanceData;
import ru.axetta.ecafe.processor.core.push.model.BenefitData;
import ru.axetta.ecafe.processor.core.push.model.EnterEventData;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.BenefitValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;

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

    public static final String RESPONSE_METHOD_BENEFIT = "active_benefit_categories_getting_response";
    public static final String RESPONSE_METHOD_DOC = "passport_validity_checking_response";
    public static final String RESPONSE_METHOD_GUARDIANSHIP = "relatedness_checking_response";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> benefitKafkaTemplate;

    public BenefitKafkaService(KafkaTemplate<String, String> benefitKafkaTemplate) {
        this.benefitKafkaTemplate = benefitKafkaTemplate;
    }

    public void sendBenefitValidationRequest(String serviceNumber) {
        ApplicationForFood applicationForFood = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                .findApplicationForFoodByServiceNumber(serviceNumber);
        sendRequest(applicationForFood, BenefitValidationRequest.class);
    }

    private String getTopicFromConfig(AbstractPushData data) throws Exception {
        String topicLink = null;
        if (data instanceof GuardianshipValidationRequest)
            topicLink = AbstractPushData.GUARDIANSHIP_VALIDATION_REQUEST_TOPIC;
        else if (data instanceof BenefitValidationRequest)
            topicLink = AbstractPushData.BENEFIT_VALIDATION_REQUEST_TOPIC;
        else if (data instanceof DocValidationRequest)
            topicLink = AbstractPushData.DOC_VALIDATION_REQUEST_TOPIC;
        String address = RuntimeContext.getInstance().getConfigProperties().getProperty(topicLink, "");
        if (address.equals(""))
            throw new Exception(String.format("Kafka topic not specified, topicLink: %s", topicLink));
        return address;
    }

    public void sendRequest(ApplicationForFood applicationForFood, Class<? extends AbstractPushData> clazz) {
        try {
            RequestValidationData data = getBenefitData(applicationForFood);
            AbstractPushData request = clazz.getDeclaredConstructor(RequestValidationData.class).newInstance(data);
            Message<AbstractPushData> message = MessageBuilder.withPayload(request).build();
            String msg = objectMapper.writeValueAsString(request);
            ListenableFuture<SendResult<String, String>> future = benefitKafkaTemplate.send(getTopicFromConfig(request), msg);
            future.addCallback(new ZlpLoggingListenableFutureCallback(message, msg, data.getIdOfApplicationForFood(), applicationForFood.getServiceNumber()));
        } catch (Exception e) {
            logger.error(String.format("Error in sendGuardianshipValidationRequest for serviceNumber = %s", applicationForFood.getServiceNumber()), e);
        }
    }

    public RequestValidationData getBenefitData(ApplicationForFood applicationForFood) throws Exception {
        String message = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                .getOriginalMessageFromApplicationForFood(applicationForFood);
        CoordinateMessage coordinateMessage = (CoordinateMessage)RuntimeContext.getAppContext().getBean(ETPMVService.class).getCoordinateMessage(message);
        return new RequestValidationData(coordinateMessage, applicationForFood.getIdOfApplicationForFood());
    }

}
