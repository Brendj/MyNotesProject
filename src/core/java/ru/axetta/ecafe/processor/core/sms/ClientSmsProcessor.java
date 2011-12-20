/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.07.2009
 * Time: 10:33:22
 * To change this template use File | Settings | File Templates.
 */
public interface ClientSmsProcessor {

    void registerClientSms(Long idOfClient, String idOfSms, String phone, Integer contentsType, String textContents,
            Date serviceSendTime) throws Exception;

}