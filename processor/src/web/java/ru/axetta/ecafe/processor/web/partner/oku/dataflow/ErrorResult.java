/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku.dataflow;

import java.net.HttpURLConnection;

public class ErrorResult implements IResponseEntity {
    private Integer code;
    private String message;

    public ErrorResult(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResult() {

    }

    public static ErrorResult notFound() {
        return new ErrorResult(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
    }

    public static ErrorResult badRequest() {
        return new ErrorResult(HttpURLConnection.HTTP_BAD_REQUEST, "Bad Request");
    }

    public static ErrorResult unauthorized() {
        return new ErrorResult(HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
