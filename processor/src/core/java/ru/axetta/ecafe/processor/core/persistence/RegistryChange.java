/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 03.10.13
 * Time: 14:51
 * To change this template use File | Settings | File Templates.
 */
public class RegistryChange {
    protected Long idOfOrg;
    protected Long idOfMigrateOrgTo;
    protected Long idOfMigrateOrgFrom;
    protected Long createDate;
    protected Long idOfRegistryChange;
    protected String clientGUID;
    protected String firstName;
    protected String secondName;
    protected String surname;
    protected String groupName;
    protected String firstNameFrom;
    protected String secondNameFrom;
    protected String surnameFrom;
    protected String groupNameFrom;
    protected String error;
    protected Long idOfClient;
    protected Integer operation;
    protected String notificationId;
    protected Integer type;
    protected Boolean applied;

    protected Integer gender;
    protected Long birthDate;
    protected Integer genderFrom;
    protected Long birthDateFrom;

    protected Boolean checkBenefits;
    protected String benefitDSZN;
    protected String newDiscounts;
    protected String benefitDSZNFrom;
    protected String oldDiscounts;

    protected Integer guardiansCount;
    protected Set<RegistryChangeGuardians> registryChangeGuardiansSet;

    protected String ageTypeGroup;

    protected String ageTypeGroupFrom;

    public static final int FULL_COMPARISON = 1;
    public static final int CHANGES_UPDATE = 2;

    public RegistryChange() {
    }

    public RegistryChange(Long idOfOrg, Long idOfMigrateOrgTo, Long idOfMigrateOrgFrom, Long createDate,
            Long idOfRegistryChange, String clientGUID, String firstName, String secondName, String surname,
            String groupName, String firstNameFrom, String secondNameFrom, String surnameFrom, String groupNameFrom,
            Long idOfClient, Integer operation, Integer type, Boolean applied, String error, String notificationId,
            Integer gender, Long birthDate, Boolean checkBenefits, String benefitDSZN, String newDiscounts,
            String benefitDSZNFrom, String oldDiscounts, Integer genderFrom, Long birthDateFrom, String ageTypeGroup, String ageTypeGroupFrom) {
        this.idOfOrg = idOfOrg;
        this.idOfMigrateOrgTo = idOfMigrateOrgTo;
        this.idOfMigrateOrgFrom = idOfMigrateOrgFrom;
        this.createDate = createDate;
        this.idOfRegistryChange = idOfRegistryChange;
        this.clientGUID = clientGUID;
        this.firstName = firstName;
        this.secondName = secondName;
        this.surname = surname;
        this.groupName = groupName;
        this.firstNameFrom = firstNameFrom;
        this.secondNameFrom = secondNameFrom;
        this.surnameFrom = surnameFrom;
        this.groupNameFrom = groupNameFrom;
        this.idOfClient = idOfClient;
        this.operation = operation;
        this.type = type;
        this.applied = applied;
        this.error = error;
        this.notificationId = notificationId;
        this.gender = gender;
        this.birthDate = birthDate;
        this.genderFrom = genderFrom;
        this.birthDateFrom = birthDateFrom;
        this.checkBenefits = checkBenefits;
        this.benefitDSZN = benefitDSZN;
        this.newDiscounts = newDiscounts;
        this.benefitDSZNFrom = benefitDSZNFrom;
        this.oldDiscounts = oldDiscounts;
        this.ageTypeGroup = ageTypeGroup;
        this.ageTypeGroupFrom = ageTypeGroupFrom;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfMigrateOrgTo() {
        return idOfMigrateOrgTo;
    }

    public void setIdOfMigrateOrgTo(Long idOfMigrateOrgTo) {
        this.idOfMigrateOrgTo = idOfMigrateOrgTo;
    }

    public Long getIdOfMigrateOrgFrom() {
        return idOfMigrateOrgFrom;
    }

    public void setIdOfMigrateOrgFrom(Long idOfMigrateOrgFrom) {
        this.idOfMigrateOrgFrom = idOfMigrateOrgFrom;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getIdOfRegistryChange() {
        return idOfRegistryChange;
    }

    public void setIdOfRegistryChange(Long idOfRegistryChange) {
        this.idOfRegistryChange = idOfRegistryChange;
    }

    public String getClientGUID() {
        return clientGUID;
    }

    public void setClientGUID(String clientGUID) {
        this.clientGUID = clientGUID;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFirstNameFrom() {
        return firstNameFrom;
    }

    public void setFirstNameFrom(String firstNameFrom) {
        this.firstNameFrom = firstNameFrom;
    }

    public String getSecondNameFrom() {
        return secondNameFrom;
    }

    public void setSecondNameFrom(String secondNameFrom) {
        this.secondNameFrom = secondNameFrom;
    }

    public String getSurnameFrom() {
        return surnameFrom;
    }

    public void setSurnameFrom(String surnameFrom) {
        this.surnameFrom = surnameFrom;
    }

    public String getGroupNameFrom() {
        return groupNameFrom;
    }

    public void setGroupNameFrom(String groupNameFrom) {
        this.groupNameFrom = groupNameFrom;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Boolean getApplied() {
        return applied;
    }

    public void setApplied(Boolean applied) {
        this.applied = applied;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Long getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Long birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getGenderFrom() {
        return genderFrom;
    }

    public void setGenderFrom(Integer genderFrom) {
        this.genderFrom = genderFrom;
    }

    public Long getBirthDateFrom() {
        return birthDateFrom;
    }

    public void setBirthDateFrom(Long birthDateFrom) {
        this.birthDateFrom = birthDateFrom;
    }

    public Boolean getCheckBenefits() {
        return checkBenefits;
    }

    public Boolean getCheckBenefitsSafe() {
        return (null == checkBenefits) ? false : checkBenefits;
    }

    public void setCheckBenefits(Boolean checkBenefits) {
        this.checkBenefits = checkBenefits;
    }

    public String getBenefitDSZN() {
        return benefitDSZN;
    }

    public void setBenefitDSZN(String benefitDSZN) {
        this.benefitDSZN = benefitDSZN;
    }

    public String getNewDiscounts() {
        return newDiscounts;
    }

    public void setNewDiscounts(String newDiscounts) {
        this.newDiscounts = newDiscounts;
    }

    public String getBenefitDSZNFrom() {
        return benefitDSZNFrom;
    }

    public void setBenefitDSZNFrom(String benefitDSZNFrom) {
        this.benefitDSZNFrom = benefitDSZNFrom;
    }

    public String getOldDiscounts() {
        return oldDiscounts;
    }

    public void setOldDiscounts(String oldDiscounts) {
        this.oldDiscounts = oldDiscounts;
    }

    public Integer getGuardiansCount() {
        return guardiansCount;
    }

    public void setGuardiansCount(Integer guardiansCount) {
        this.guardiansCount = guardiansCount;
    }

    public Set<RegistryChangeGuardians> getRegistryChangeGuardiansSet() {
        return registryChangeGuardiansSet;
    }

    public void setRegistryChangeGuardiansSet(Set<RegistryChangeGuardians> registryChangeGuardiansSet) {
        this.registryChangeGuardiansSet = registryChangeGuardiansSet;
    }

    public String getAgeTypeGroup() {
        return ageTypeGroup;
    }

    public void setAgeTypeGroup(String ageTypeGroup) {
        this.ageTypeGroup = ageTypeGroup;
    }

    public String getAgeTypeGroupFrom() {
        return ageTypeGroupFrom;
    }

    public void setAgeTypeGroupFrom(String ageTypeGroupFrom) {
        this.ageTypeGroupFrom = ageTypeGroupFrom;
    }
}
