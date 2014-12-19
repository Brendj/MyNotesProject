/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.autopayments;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: Shamil Sungatov
 * Date: 15.12.14
 */

@XmlRootElement(name = "AutoPaymentResultRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class AutoPaymentResultRequest {


    @XmlElement(name = "idaction")
    private Long idaction;
    @XmlElement(name = "errorcode")
    private int errorCode;
    @XmlElement(name = "errortext")
    private String errorDesc;
    @XmlElement(name = "realamount")
    private Long realAmount;

    public AutoPaymentResultRequest() {
    }

    public AutoPaymentResultRequest(long idaction, int errorCode, String errorDesc, long realAmount) {
        this.idaction = idaction;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public Long getIdaction() {
        return idaction;
    }

    public void setIdaction(Long idaction) {
        this.idaction = idaction;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public Long getRealAmount() {
        return realAmount;
    }

    public void setRealAmount(Long realAmount) {
        this.realAmount = realAmount;
    }
}
