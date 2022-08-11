package ru.axetta.ecafe.processor.core.zlp.kafka;

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
import ru.axetta.ecafe.processor.core.push.kafka.logging.LoggingListenableFutureCallback;
import ru.axetta.ecafe.processor.core.push.kafka.service.KafkaService;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;

import java.util.UUID;

@Service
public class BenefitKafkaService extends KafkaService {
    private static final Logger logger = LoggerFactory.getLogger(BenefitKafkaService.class);
    public static final String REQUEST_SYSTEM = "ispp";
    public static final String REQUEST_METHOD = "relatedness_checking_2";
    public static final String STATE_SERVICE_VARIETY_CODE_DEFAULT = "77060601";
    public static final String STATE_SERVICE_VARIETY_CODE_PROPERTY = "ecafe.processing.zlp.service.code";

    public BenefitKafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendGuardianshipValidationRequest(ApplicationForFood applicationForFood) {
        try {
            GuardianshipValidationData data = getBenefitData(applicationForFood);
            AbstractPushData request = new GuardianshipValidationRequest(data);
            Message<AbstractPushData> message = MessageBuilder.withPayload(request)
                    .build();
            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate
                    .send(getTopicFromConfig(request), message);
            future.addCallback(new ZlpLoggingListenableFutureCallback(message, data.getApplicationForFood()));
        } catch (Exception e) {
            logger.error(String.format("Error in sendGuardianshipValidationRequest for serviceNumber = %s", applicationForFood.getServiceNumber()), e);
        }
    }

    public GuardianshipValidationData getBenefitData(ApplicationForFood applicationForFood) throws Exception {
        String message = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                .getOriginalMessageFromApplicationForFood(applicationForFood);
        CoordinateMessage coordinateMessage = RuntimeContext.getAppContext().getBean(ETPMVService.class).getCoordinateMessage(message);
        return new GuardianshipValidationData(coordinateMessage, applicationForFood);
    }

    public GuardianshipValidationRequest getGuardianshipValidationRequest(GuardianshipValidationData benefitData) {
        return new GuardianshipValidationRequest(benefitData);
    }

}
