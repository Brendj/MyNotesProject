/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto;

import java.io.Serializable;

public class CreateOrUpdateGuardianRequest implements Serializable
{
    private long childClientId;
    private long guardianClientId;
    private Integer rights;
    private Integer relationType;
    private Boolean isEnabledInformationSupport;

    public long getChildClientId() {
        return childClientId;
    }
    public void setChildClientId(long idOfClient) {
        this.childClientId = idOfClient;
    }

    public long getGuardianClientId() { return guardianClientId; }
    public void setGuardianClientId(long idOfGuardian) {
        this.guardianClientId = idOfGuardian;
    }

    public Integer getRights() {
        return rights;
    }
    public void setRights(Integer rights) {
        this.rights = rights;
    }

    public Integer getRelationType() {
        return relationType;
    }
    public void setRelationType(Integer reletaionType) {
        this.relationType = reletaionType;
    }

    public Boolean getIsEnabledInformationSupport() {
        return isEnabledInformationSupport;
    }
    public void setIsEnabledInformationSupport(Boolean isEnabledInformationSupport) { this.isEnabledInformationSupport = isEnabledInformationSupport; }
}
