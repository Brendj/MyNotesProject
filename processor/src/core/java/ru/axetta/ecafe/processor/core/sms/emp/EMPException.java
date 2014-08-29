/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 18.07.14
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public class EMPException extends Exception {
    protected int code;
    protected String error;

    public EMPException() {
    }

    public EMPException(String message) {
        super(message);
    }

    public EMPException(String message, Throwable cause) {
        super(message, cause);
    }

    public EMPException(Throwable cause) {
        super(cause);
    }

    public EMPException(String message, Throwable cause, int code, String error) {
        super(message, cause);
        this.code = code;
        this.error = error;
    }

    public EMPException(Throwable cause, int code, String error) {
        super(cause);
        this.code = code;
        this.error = error;
    }

    public EMPException(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }
}
