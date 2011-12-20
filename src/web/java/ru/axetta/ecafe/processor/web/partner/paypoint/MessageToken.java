/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.paypoint;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 30.09.2010
 * Time: 15:02:17
 * To change this template use File | Settings | File Templates.
 */
public class MessageToken {

    private final String param;
    private final String value;

    public MessageToken(String param, String value) {
        this.param = param;
        this.value = value;
    }

    public String getParam() {
        return param;
    }

    public String getValue() {
        return value;
    }
}
