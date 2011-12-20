/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class Notification {

    private Long idOfNotification;
    private Client client;
    private Date notificationTime;
    private Integer notificationType;
    private String notificationText;

    Notification() {
        // For Hibernate only
    }

    public Notification(Client client, Date notificationTime, int notificationType, String notificationText) {
        this.client = client;
        this.notificationTime = notificationTime;
        this.notificationType = notificationType;
        this.notificationText = notificationText;
    }

    public Long getIdOfNotification() {
        return idOfNotification;
    }

    private void setIdOfNotification(Long idOfNotification) {
        // For Hibernate only
        this.idOfNotification = idOfNotification;
    }

    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        // For Hibernate only
        this.client = client;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    private void setNotificationTime(Date notificationTime) {
        // For Hibernate only
        this.notificationTime = notificationTime;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    private void setNotificationType(Integer notificationType) {
        // For Hibernate
        this.notificationType = notificationType;
    }

    public String getNotificationText() {
        return notificationText;
    }

    private void setNotificationText(String notificationText) {
        // For Hibernate
        this.notificationText = notificationText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        final Notification that = (Notification) o;
        return idOfNotification.equals(that.getIdOfNotification());
    }

    @Override
    public int hashCode() {
        return idOfNotification.hashCode();
    }

    @Override
    public String toString() {
        return "Notification{" + "idOfNotification=" + idOfNotification + ", client=" + client + ", notificationTime="
                + notificationTime + ", notificationType=" + notificationType + ", notificationText='"
                + notificationText + '\'' + '}';
    }
}