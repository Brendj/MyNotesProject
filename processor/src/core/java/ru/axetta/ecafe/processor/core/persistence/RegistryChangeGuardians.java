/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 18.08.16
 * Time: 15:23
 */

public class RegistryChangeGuardians {

    private Long idOfRegistryGuardian;
    private String familyName;
    private String firstName;
    private String secondName;
    private String relationship;
    private String phoneNumber;
    private String emailAddress;
    private Date createdDate;
    protected Boolean applied;
    private Boolean legal_representative;
    private String ssoid;
    private String guid;
    /**
     * Ссылка на таблицу поставщики Разногласий.
     */
    private RegistryChange registryChange;

    public RegistryChangeGuardians() {
    }

    public RegistryChangeGuardians(String familyName, String firstName, String secondName, String relationship, String phoneNumber,
            String emailAddress, Date createdDate, RegistryChange registryChange, Boolean applied, Boolean legal_representative, String ssoid, String guid) {
        this.familyName = familyName;
        this.firstName = firstName;
        this.secondName = secondName;
        this.relationship = ClientGuardianRelationType.getRelationshipExtended(relationship);
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.createdDate = createdDate;
        this.registryChange = registryChange;
        this.applied = applied;
        this.legal_representative = legal_representative;
        this.ssoid = ssoid;
        this.guid = guid;
    }

    public Long getIdOfRegistryGuardian() {
        return idOfRegistryGuardian;
    }

    public void setIdOfRegistryGuardian(Long idOfRegistryGuardian) {
        this.idOfRegistryGuardian = idOfRegistryGuardian;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public RegistryChange getRegistryChange() {
        return registryChange;
    }

    public void setRegistryChange(RegistryChange registryChange) {
        this.registryChange = registryChange;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getApplied() {
        return applied;
    }

    public void setApplied(Boolean applied) {
        this.applied = applied;
    }

    public Boolean getLegal_representative() {
        return legal_representative;
    }

    public Integer getIntegerRepresentative() {
        if (legal_representative == null) {
            return ClientGuardianRepresentType.UNKNOWN.getCode();
        } else {
            return legal_representative ? 1 : 0;
        }
    }

    public void setLegal_representative(Boolean legal_representative) {
        this.legal_representative = legal_representative;
    }

    public String getSsoid() {
        return ssoid;
    }

    public void setSsoid(String ssoid) {
        this.ssoid = ssoid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guis) {
        this.guid = guis;
    }
}
