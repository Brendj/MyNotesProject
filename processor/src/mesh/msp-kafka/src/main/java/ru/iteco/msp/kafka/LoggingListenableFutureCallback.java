/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.SendResult;
import org.springframework.lang.NonNull;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class LoggingListenableFutureCallback implements ListenableFutureCallback<SendResult<String, String>> {
    private final Logger log = LoggerFactory.getLogger(LoggingListenableFutureCallback.class);
    private final String message;

    public LoggingListenableFutureCallback(String message){
        this.message = message;
    }

    @Override
    public void onSuccess(@NonNull SendResult<String, String> result) {
        log.info("Send message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "] Key: " + result.getProducerRecord().key());
    }

    @Override
    public void onFailure(Throwable e) {
        log.error("Unable to send message=[" + message + "] due to : ", e);
    }
}
