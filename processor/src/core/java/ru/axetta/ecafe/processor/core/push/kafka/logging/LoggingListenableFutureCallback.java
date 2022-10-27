package ru.axetta.ecafe.processor.core.push.kafka.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;

public class LoggingListenableFutureCallback implements ListenableFutureCallback<SendResult<String, Object>> {
    private final Logger log = LoggerFactory.getLogger(LoggingListenableFutureCallback.class);
    private final Message<AbstractPushData> message;

    public LoggingListenableFutureCallback(Message<AbstractPushData> message) {
        this.message = message;
    }

    @Override
    public void onSuccess(SendResult<String, Object> result) {
        if (result == null) {
            onFailure(new NullPointerException("SendResult is null"));
            return;
        }
        log.info("Send kafka message: " + message + ", Partition: " + result.getRecordMetadata().partition());
    }

    @Override
    public void onFailure(@NonNull Throwable e) {
        log.error(String.format("Failed to send message to kafka: %s", message), e);
    }
}
