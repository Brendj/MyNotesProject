/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddRegistrationCardResponse")
public class AddRegistrationCardResult extends Result {
    @XmlElement(name = "supplierINN")
    private String supplierINN;
    @XmlElement(name = "supplierName")
    private String supplierName;
    @XmlElement(name = "contractId")
    private Long contractId;

    public AddRegistrationCardResult() {

    }

    public String getSupplierINN() {
        return supplierINN;
    }

    public void setSupplierINN(String supplierINN) {
        this.supplierINN = supplierINN;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}
