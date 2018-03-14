/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;

/**
 * Created by i.semenov on 01.03.2018.
 */
public class SudirPersonData /*extends SudirBaseData*/ {
    private String guid;
    private String firstName;
    private String lastName;
    private String middleName;
    private String mail;
    private String mobile;
    private String phone;

    public SudirPersonData() {

    }

    /*public SudirPersonData(String token) {
        super(token);
    }*/

    public String getMobileCanonical() throws Exception {
        return PhoneNumberCanonicalizator.canonicalize(mobile);
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
