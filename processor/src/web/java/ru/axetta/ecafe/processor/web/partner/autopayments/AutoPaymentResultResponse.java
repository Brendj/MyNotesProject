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

@XmlRootElement(name = "AutoPaymentResultResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AutoPaymentResultResponse {


    @XmlElement(name = "idaction")
    private long idaction;
    @XmlElement(name = "errorcode")
    private int errorCode;
    @XmlElement(name = "errortext")
    private String errorDesc = "Ok.";

    public AutoPaymentResultResponse() {
    }

    public AutoPaymentResultResponse(AutoPaymentResultRequest request) {
        this.idaction = request.getIdaction();
    }

    public AutoPaymentResultResponse(long idaction, int errorCode, String errorDesc) {
        this.idaction = idaction;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public long getIdaction() {
        return idaction;
    }

    public void setIdaction(long idaction) {
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
}
