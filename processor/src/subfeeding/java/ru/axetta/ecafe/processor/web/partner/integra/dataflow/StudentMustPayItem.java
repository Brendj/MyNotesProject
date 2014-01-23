/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 06.08.12
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StudentMustPayItem")

public class StudentMustPayItem {


    @XmlAttribute(name = "FirstName")
    protected String firstName;
    @XmlAttribute(name = "Surname")
    protected String surname;
    @XmlAttribute(name = "SecondName")
    protected String secondName;
    @XmlAttribute(name = "Balance")
    protected Long balance;
    @XmlAttribute(name = "CreateTime")
    protected Date createTime;
    @XmlAttribute(name = "PaySum")
    protected Long paySum;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getPaySum() {
        return paySum;
    }

    public void setPaySum(Long paySum) {
        this.paySum = paySum;
    }
}
