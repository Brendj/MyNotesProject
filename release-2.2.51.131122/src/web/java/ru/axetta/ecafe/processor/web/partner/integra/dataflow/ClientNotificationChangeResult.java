/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 26.02.13
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
public class ClientNotificationChangeResult {

    public Long resultCode;
    public String description;


    public ClientNotificationChangeResult(Long resultCode, String desc) {

        this.resultCode = resultCode;
        this.description = desc;
    }

    public ClientNotificationChangeResult (){}
}
