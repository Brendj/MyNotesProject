/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 12.02.16
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class ServiceTemporaryUnavailableException extends Exception {

    public ServiceTemporaryUnavailableException(String message) {
        super(message);
    }

}
