/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import java.util.List;

/**
 * Created by i.semenov on 23.04.2018.
 */
public class ESIAPersonData {
    private String guid;
    private String firstName;
    private String lastName;
    private String middleName;
    private String mail;
    private String phone;
    private List<ESIADataVO> esiaDataVO;

    public ESIAPersonData() {

    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List getEsiaDataVO() {
        return esiaDataVO;
    }

    public void setEsiaDataVO(List esiaDataVO) {
        this.esiaDataVO = esiaDataVO;
    }
}
