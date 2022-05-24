package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.math.BigInteger;
import java.util.List;

public class GuardianItem {

    private Long idOfClient;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String snils;
    private String mobile;
    private String address;
    private BigInteger idOfClientGroup;
    private String groupName;

    public GuardianItem() {
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigInteger getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(BigInteger idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
