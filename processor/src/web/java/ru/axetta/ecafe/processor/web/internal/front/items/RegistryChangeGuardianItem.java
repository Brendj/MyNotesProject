/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created by i.semenov on 21.01.2019.
 */
public class RegistryChangeGuardianItem {
    private String fio;
    private String phone;
    private Boolean isLegalRepresent;
    private String guardianType;

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getLegalRepresent() {
        return isLegalRepresent;
    }

    public void setLegalRepresent(Boolean legalRepresent) {
        isLegalRepresent = legalRepresent;
    }

    public String getGuardianType() {
        return guardianType;
    }

    public void setGuardianType(String guardianType) {
        this.guardianType = guardianType;
    }
}
