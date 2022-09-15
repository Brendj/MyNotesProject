package ru.axetta.ecafe.processor.core.zlp.kafka;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.PersonBenefitCheckRequest;

public class ZlpLoggingListenableFutureCallback implements ListenableFutureCallback<SendResult<String, String>> {
    private final Logger log = LoggerFactory.getLogger(ZlpLoggingListenableFutureCallback.class);
    private final Message<AbstractPushData> message;
    private final String jsonMessage;
    private final Integer type;
    private final Long idOfApplicationForFood;
    private final String serviceNumber;
    private final String topic;
    private final AppMezhvedErrorSendKafka appMezhvedErrorSendKafka;

    public ZlpLoggingListenableFutureCallback(Message<AbstractPushData> message, String jsonMessage, String topic,
                                              Integer type, Long idOfApplicationForFood, String serviceNumber,
                                              AppMezhvedErrorSendKafka appMezhvedErrorSendKafka) {
        this.message = message;
        this.jsonMessage = jsonMessage;
        this.idOfApplicationForFood = idOfApplicationForFood;
        this.type = type;
        this.serviceNumber = serviceNumber;
        this.topic = topic;
        this.appMezhvedErrorSendKafka = appMezhvedErrorSendKafka;
    }

    @Override
    public void onSuccess(SendResult<String, String> result) {
        if (result == null) {
            onFailure(new NullPointerException("SendResult is null"));
            return;
        }
        if (appMezhvedErrorSendKafka != null)
        {
            //удаляем запись из таблицы повторной отправки
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).updateMezhvedKafkaError(appMezhvedErrorSendKafka, true);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveMezhvedRequest(message.getPayload(), jsonMessage, idOfApplicationForFood);
        log.info("Send kafka message: " + jsonMessage + ", Partition: " + result.getRecordMetadata().partition());
        try {
            ApplicationForFoodStatus status = getApplicationForFoodStatus();
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                    .updateApplicationForFoodWithStatus(idOfApplicationForFood, status);
            RuntimeContext.getAppContext().getBean(ETPMVService.class)
                    .sendStatus(System.currentTimeMillis(), serviceNumber, status.getApplicationForFoodState());
        } catch (Exception e) {
            log.error("Error in sendRequestForGuardianshipValidation when sending status: ", e);
        }
    }

    @Override
    public void onFailure(@NonNull Throwable e) {
        log.error(String.format("Failed to send message to kafka: %s", message), e);
        if (appMezhvedErrorSendKafka != null)
        {
            //Изменяем дату повторной отправки
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).updateMezhvedKafkaError(appMezhvedErrorSendKafka, false);
        }
        else {
            //Сохраняем сообщение в таблицу вместе с причиной ошибки доставки
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                    .saveMezvedKafkaError(jsonMessage, topic, type, e.getMessage(), idOfApplicationForFood);
        }
    }



    private ApplicationForFoodStatus getApplicationForFoodStatus() throws Exception {
        if (message.getPayload() instanceof GuardianshipValidationRequest) {
            return new ApplicationForFoodStatus(ApplicationForFoodState.GUARDIANSHIP_VALIDITY_REQUEST_SENDED);
        }
        if (message.getPayload() instanceof PersonBenefitCheckRequest) {
            return new ApplicationForFoodStatus(ApplicationForFoodState.BENEFITS_VALIDITY_REQUEST_SENDED);
        }
        if (message.getPayload() instanceof DocValidationRequest) {
            return new ApplicationForFoodStatus(ApplicationForFoodState.DOC_VALIDITY_REQUEST_SENDED);
        }

        throw new Exception("Unknown message type");
    }

    private String requestToJsonString(AbstractPushData request) {
        ObjectMapper objectMapper = new ObjectMapper();
        String res;
        try {
            res = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            res = "Cannot deserialize payload to string";
        }
        return res;
    }
}
