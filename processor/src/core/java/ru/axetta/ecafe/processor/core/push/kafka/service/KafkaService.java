package ru.axetta.ecafe.processor.core.push.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.push.kafka.logging.LoggingListenableFutureCallback;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.push.model.BalanceData;
import ru.axetta.ecafe.processor.core.push.model.BenefitData;
import ru.axetta.ecafe.processor.core.push.model.EnterEventData;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.BenefitValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;

import java.util.UUID;

@Primary
@Configuration
@Service("KafkaService")
public class KafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaService.class);
    public static final String MESH_KAFKA_ENABLE_PROPERTY = "ecafe.processing.mesh.kafka.enable";
    protected final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendMessage(AbstractPushData data) {
//        try {
        Message<AbstractPushData> message = MessageBuilder.withPayload(data)
                .setHeader("correlationId", UUID.randomUUID())
                .setHeader("createdAt", System.currentTimeMillis())
                .build();
//            ListenableFuture<SendResult<String, Object>> future = kafkaTemplate
//                    .send(getTopicFromConfig(data), message);
//            future.addCallback(new LoggingListenableFutureCallback(message));
//        } catch (Exception e) {
//            log.error(String.format("Failed to send message to kafka: %s", data.toString()), e);
//        }
        log.info("Kafka message: " + message);
    }

    protected String getTopicFromConfig(AbstractPushData data) throws Exception {
        String topicLink = null;
        if (data instanceof BalanceData)
            topicLink = AbstractPushData.BALANCE_TOPIC;
        else if (data instanceof EnterEventData)
            topicLink = AbstractPushData.ENTRANCE_TOPIC;
        else if (data instanceof BenefitData)
            topicLink = AbstractPushData.BENEFIT_TOPIC;
        else if (data instanceof GuardianshipValidationRequest)
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
}