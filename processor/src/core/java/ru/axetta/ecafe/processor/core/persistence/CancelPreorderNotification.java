/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 30.05.16
 * Time: 12:04
 */

public class CancelPreorderNotification {
    private Long ifofcancelpreordernotifications;
    private Client client;
    private String typename;
    private Date preorderdate;
    private String textmessage;

    public Long getIfofcancelpreordernotifications() {
        return ifofcancelpreordernotifications;
    }

    public void setIfofcancelpreordernotifications(Long ifofcancelpreordernotifications) {
        this.ifofcancelpreordernotifications = ifofcancelpreordernotifications;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public Date getPreorderdate() {
        return preorderdate;
    }

    public void setPreorderdate(Date preorderdate) {
        this.preorderdate = preorderdate;
    }

    public String getTextmessage() {
        return textmessage;
    }

    public void setTextmessage(String textmessage) {
        this.textmessage = textmessage;
    }
}
