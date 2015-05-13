/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import java.io.Serializable;

/**
 * User: shamil
 * Date: 13.05.15
 * Time: 11:17
 */
public class ResponseItem implements Serializable {

    public static final int OK = 0;
    public static final String OK_MESSAGE = "Ok.";
    public int code;
    public String message;

    public ResponseItem() {
        code = OK;
        message = OK_MESSAGE;
    }

    public ResponseItem(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
