/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.01.14
 * Time: 10:15
 * To change this template use File | Settings | File Templates.
 */
public class ResultOperation {
    private static final int SUCCESS_CODE = 0;
    private Integer code;
    private String message;

    public ResultOperation() {
        this(SUCCESS_CODE, null);
    }

    public ResultOperation(Integer code, String resultMessage) {
        this.code = code;
        this.message = resultMessage;
    }

    public Integer getCode() {
        return code;
    }

    public boolean isSuccess() {
        return code == SUCCESS_CODE;
    }

    public String getMessage() {
        return message;
    }
}
