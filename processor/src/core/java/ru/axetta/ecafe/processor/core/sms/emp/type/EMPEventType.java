/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.08.14
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public interface EMPEventType {
    public int getPreviousId();

    public int getId();

    public int getType();

    public String getName();

    public String getText();

    public Map<String, String> getParameters();

    public int getStream();

    public void parse(Client client, Map<String, Object> additionalParams);

    public String getSsoid();

    public Long getMsisdn();

    public String buildText();

    public long getTime();

    public void setTime(long time);
}