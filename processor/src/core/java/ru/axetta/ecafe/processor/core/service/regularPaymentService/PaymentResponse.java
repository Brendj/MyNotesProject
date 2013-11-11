/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 21.10.13
 * Time: 13:24
 * Объект, хранящий параметры response- и callback-ответов на запросы к системе Acquiropay.
 */

public class PaymentResponse {

    private int statusCode;
    private String paymentId;
    private String status;
    private String extendedStatus;
    private Date dateTime;
    private String cf;
    private String cf2;
    private String cf3;
    private String panMask;
    private String cardHolder;
    private String expYear;
    private String expMonth;
    private String sign;
    private String authCode;
    private String rrn;
    private String errorDescription;

    public PaymentResponse() {

    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExtendedStatus() {
        return extendedStatus;
    }

    public void setExtendedStatus(String extendedStatus) {
        this.extendedStatus = extendedStatus;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public String getCf2() {
        return cf2;
    }

    public void setCf2(String cf2) {
        this.cf2 = cf2;
    }

    public String getCf3() {
        return cf3;
    }

    public void setCf3(String cf3) {
        this.cf3 = cf3;
    }

    public String getPanMask() {
        return panMask;
    }

    public void setPanMask(String panMask) {
        this.panMask = panMask;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getExpYear() {
        return expYear;
    }

    public void setExpYear(String expYear) {
        this.expYear = expYear;
    }

    public String getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(String expMonth) {
        this.expMonth = expMonth;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("statusCode=").append(statusCode);
        if (paymentId != null) {
            sb.append(", paymentId=").append(paymentId);
        }
        if (status != null) {
            sb.append(", status=").append(status);
        }
        if (extendedStatus != null) {
            sb.append(", extendedStatus=").append(extendedStatus);
        }
        if (dateTime != null) {
            sb.append(", dateTime=").append(dateTime);
        }
        if (cf != null) {
            sb.append(", cf=").append(cf);
        }
        if (cf2 != null) {
            sb.append(", cf2=").append(cf2);
        }
        if (cf3 != null) {
            sb.append(", cf3=").append(cf3);
        }
        if (panMask != null) {
            sb.append(", panMask=").append(panMask);
        }
        if (cardHolder != null) {
            sb.append(", cardHolder=").append(cardHolder);
        }
        if (expYear != null) {
            sb.append(", expYear=").append(expYear);
        }
        if (expMonth != null) {
            sb.append(", expMonth=").append(expMonth);
        }
        if (sign != null) {
            sb.append(", sign=").append(sign);
        }
        if (authCode != null) {
            sb.append(", authCode=").append(authCode);
        }
        if (rrn != null) {
            sb.append(", rrn=").append(rrn);
        }
        if (errorDescription != null) {
            sb.append(", errorDescription=").append(errorDescription);
        }
        return sb.append("}").toString();
    }
}
