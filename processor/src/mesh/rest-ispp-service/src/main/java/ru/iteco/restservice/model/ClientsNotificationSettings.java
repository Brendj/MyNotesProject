/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.ClientNotificationSettingType;

import javax.persistence.*;
import java.util.Objects;

@Table
@Entity(name = "cf_clientsnotificationsettings")
public class ClientsNotificationSettings {
    @Id
    @Column(name = "idofsetting")
    private Long idOfSetting;

    @Column(name = "createddate")
    private Long createdDate;

    @ManyToOne
    @JoinColumn(name = "idofclient", insertable = false, updatable = false)
    private Client client;

    @Column(name = "notifytype")
    private ClientNotificationSettingType type;

    public Long getIdOfSetting() {
        return idOfSetting;
    }

    public void setIdOfSetting(Long idOfSetting) {
        this.idOfSetting = idOfSetting;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientNotificationSettingType getType() {
        return type;
    }

    public void setType(ClientNotificationSettingType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientsNotificationSettings that = (ClientsNotificationSettings) o;
        return Objects.equals(idOfSetting, that.idOfSetting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfSetting);
    }
}
