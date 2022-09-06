package ru.axetta.ecafe.processor.core.zlp.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import generated.etp.CoordinateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import ru.axetta.ecafe.processor.core.zlp.kafka.request.BenefitValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;

@Service("BenefitKafkaService")
public class BenefitKafkaService extends KafkaService {
    private static final Logger logger = LoggerFactory.getLogger(BenefitKafkaService.class);
    public static final String REQUEST_SYSTEM_PROPERTY = "ecafe.processing.zlp.service.request_system";
    public static final String REQUEST_SYSTEM_DEFAULT = "ispp";
    public static final String REQUEST_METHOD_GUARDIANSHIP = "relatedness_checking_2";
    public static final String REQUEST_METHOD_BENEFIT = "active_benefit_categories_getting_request";
    public static final String REQUEST_METHOD_DOC = "passport_by_serie_number_validity_checking_request";
    public static final String STATE_SERVICE_VARIETY_CODE_DEFAULT = "77060601";
    public static final String STATE_SERVICE_VARIETY_CODE_PROPERTY = "ecafe.processing.zlp.service.code";

    public static final String RESPONSE_METHOD_BENEFIT = "active_benefit_categories_getting_response";
    public static final String RESPONSE_METHOD_DOC = "passport_by_serie_number_validity_checking_response";
    public static final String RESPONSE_METHOD_GUARDIANSHIP = "relatedness_checking_2_response";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BenefitKafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendBenefitValidationRequest(String serviceNumber) {
        ApplicationForFood applicationForFood = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                .findApplicationForFoodByServiceNumber(serviceNumber);
        sendRequest(applicationForFood, BenefitValidationRequest.class);
    }

    /*public void sendGuardianshipValidationRequest(ApplicationForFood applicationForFood) {
        try {
            RequestValidationData data = getBenefitData(applicationForFood);
            AbstractPushData request = new GuardianshipValidationRequest(data);
            Message<AbstractPushData> message = MessageBuilder.withPayload(request).build();
            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate
                    .send(getTopicFromConfig(request), message);
            future.addCallback(new ZlpLoggingListenableFutureCallback(message, data.getApplicationForFood()));
        } catch (Exception e) {
            logger.error(String.format("Error in sendGuardianshipValidationRequest for serviceNumber = %s", applicationForFood.getServiceNumber()), e);
        }
    }*/

    public void sendRequest(ApplicationForFood applicationForFood, Class<? extends AbstractPushData> clazz) {
        try {
            RequestValidationData data = getBenefitData(applicationForFood);
            AbstractPushData request = clazz.getDeclaredConstructor(RequestValidationData.class).newInstance(data);
            Message<AbstractPushData> message = MessageBuilder.withPayload(request).build();
            String msg = objectMapper.writeValueAsString(request);
            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(getTopicFromConfig(request), msg);
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

    public GuardianshipValidationRequest getGuardianshipValidationRequest(RequestValidationData benefitData) {
        return new GuardianshipValidationRequest(benefitData);
    }

}
