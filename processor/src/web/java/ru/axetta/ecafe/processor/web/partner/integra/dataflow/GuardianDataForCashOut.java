/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by i.semenov on 09.10.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuardianDataForCashOut")
public class GuardianDataForCashOut {
    @XmlElement(name = "declarerInn")
    private String declarerInn;

    @XmlElement(name = "declarerAccount")
    private String declarerAccount;

    @XmlElement(name = "declarerBank")
    private String declarerBank;

    @XmlElement(name = "declarerBik")
    private String declarerBik;

    @XmlElement(name = "declarerCorrAccount")
    private String declarerCorrAccount;

    public String getDeclarerInn() {
        return declarerInn;
    }

    public void setDeclarerInn(String declarerInn) {
        this.declarerInn = declarerInn;
    }

    public String getDeclarerAccount() {
        return declarerAccount;
    }

    public void setDeclarerAccount(String declarerAccount) {
        this.declarerAccount = declarerAccount;
    }

    public String getDeclarerBank() {
        return declarerBank;
    }

    public void setDeclarerBank(String declarerBank) {
        this.declarerBank = declarerBank;
    }

    public String getDeclarerBik() {
        return declarerBik;
    }

    public void setDeclarerBik(String declarerBik) {
        this.declarerBik = declarerBik;
    }

    public String getDeclarerCorrAccount() {
        return declarerCorrAccount;
    }

    public void setDeclarerCorrAccount(String declarerCorrAccount) {
        this.declarerCorrAccount = declarerCorrAccount;
    }
}
