/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 16:22:37
 * To change this template use File | Settings | File Templates.
 */
public class InvalidRequestException extends RuntimeException {

    private final PayPointRequest request;

    public InvalidRequestException(PayPointRequest request) {
        super(String.format("Unsupported request: %s", request.toString()));
        this.request = request;
    }

    public InvalidRequestException(String message) {
        super(message);
        this.request = null;
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
        this.request = null;
    }

    public InvalidRequestException(Throwable cause) {
        super(cause);
        this.request = null;
    }

    public PayPointRequest getRequest() {
        return request;
    }
}
