/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.03.14
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class ClientAlreadyExistException extends Exception {

    public ClientAlreadyExistException() {
    }

    public ClientAlreadyExistException(String message) {
        super(message);
    }

    public ClientAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
