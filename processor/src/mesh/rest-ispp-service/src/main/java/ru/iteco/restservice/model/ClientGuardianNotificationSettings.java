/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.enums.ClientNotificationSettingType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_client_guardian_notificationsettings")
public class ClientGuardianNotificationSettings {
    @Id
    @Column(name = "idofsetting")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOfSetting;

    @Column(name = "createddate")
    private Long createdDate;

    @Column(name = "notifytype")
    private ClientNotificationSettingType type;

    @ManyToOne
    @JoinColumn(name = "idofclientguardian")
    private ClientGuardian clientGuardian;

    public ClientGuardianNotificationSettings() {
    }

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

    public ClientNotificationSettingType getType() {
        return type;
    }

    public void setType(ClientNotificationSettingType type) {
        this.type = type;
    }

    public ClientGuardian getClientGuardian() {
        return clientGuardian;
    }

    public void setClientGuardian(ClientGuardian clientGuardian) {
        this.clientGuardian = clientGuardian;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientGuardianNotificationSettings that = (ClientGuardianNotificationSettings) o;
        return Objects.equals(idOfSetting, that.idOfSetting);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfSetting);
    }
}
