/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 10.02.2010
 * Time: 11:21:46
 * To change this template use File | Settings | File Templates.
 */
public class AccessDiniedException extends RuntimeException {

    public AccessDiniedException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public AccessDiniedException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public AccessDiniedException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public AccessDiniedException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
