/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 07.10.15
 * Time: 16:06
 * To change this template use File | Settings | File Templates.
 */
public interface IRNIPMessageToLog {

    public static final int MESSAGE_OUT = 1, MESSAGE_IN = 2;
    public void LogPacket(String message, int message_type) throws Exception;

}
