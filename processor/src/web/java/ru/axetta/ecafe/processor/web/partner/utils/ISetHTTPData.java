/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.utils;

import javax.xml.ws.handler.MessageContext;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 04.04.16
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
public interface ISetHTTPData {
    //public void setIdOfSystem(String idOfSystem);
    //public void setSsoId(String ssoId);
    //public void setOperationType(String operationType);
    public void setData(MessageContext jaxwsContext);
}
