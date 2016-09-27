/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.02.13
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public class ClientNotificationSettingsItem {
    private Long typeOfNotification;
    private String nameOfNotification;


    public Long getTypeOfNotification() {
        return typeOfNotification;
    }

    public void setTypeOfNotification(Long typeOfNotification) {
        this.typeOfNotification = typeOfNotification;
    }

    public String getNameOfNotification() {
        return nameOfNotification;
    }

    public void setNameOfNotification(String nameOfNotification) {
        this.nameOfNotification = nameOfNotification;
    }

    @Override
    public boolean equals(Object o) {
        return this.typeOfNotification.equals(((ClientNotificationSettingsItem)o).getTypeOfNotification());
    }

    @Override
    public int hashCode() {
        return typeOfNotification.hashCode();
    }
}
