/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 21.06.16
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class UserChangeGrantsException extends Exception {
    public UserChangeGrantsException() {
    }

    public UserChangeGrantsException(String message) {
        super(message);
    }

    public UserChangeGrantsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserChangeGrantsException(Throwable cause) {
        super(cause);
    }
}
