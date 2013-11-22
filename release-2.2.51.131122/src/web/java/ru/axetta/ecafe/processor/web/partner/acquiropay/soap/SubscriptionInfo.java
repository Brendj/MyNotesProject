/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.xml.bind.annotation.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 24.10.13
 * Time: 13:02
 */

@XmlRootElement(name = "SubscriptionInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionInfo {

    @XmlElement(name = "RegularPaymentSubscriptionID")
    private Long idOfSubscription;
    @XmlElement(name = "ClientID")
    private String clientID;
    @XmlElement(name = "ClientIDType")
    private Integer clientIDType;
    @XmlElement(name = "ContractID")
    private String contractID;
    @XmlElement(name = "AccountRegion")
    private String accountRegion;
    @XmlElement(name = "LowerLimitAmount")
    private Long lowerLimitAmount;
    @XmlElement(name = "PaymentAmount")
    private Long paymentAmount;
    @XmlElement(name = "Currency")
    private Integer currency;
    @XmlElement(name = "SubscriptionPeriodOfValidity")
    private Integer subscriptionPeriodOfValidity;
    @XmlElement(name = "RegistrationDate")
    @XmlSchemaType(name = "dateTime")
    private Date registrationDate;
    @XmlElement(name = "ValidityDate")
    @XmlSchemaType(name = "dateTime")
    private Date validityDate;
    @XmlElement(name = "DeactivationDate")
    @XmlSchemaType(name = "dateTime")
    private Date deactivationDate;
    @XmlElement(name = "Status")
    private String status;
    @XmlElement(name = "LastPaymentDate")
    @XmlSchemaType(name = "dateTime")
    private Date lastPaymentDate;
    @XmlElement(name = "LastPaymentStatus")
    private String lastPaymentStatus;
    @XmlElement(name = "CardNumber")
    private String cardNumber;
    @XmlElement(name = "CardHolder")
    private String cardHolder;
    @XmlElement(name = "CardExpMonth")
    private Integer expMonth;
    @XmlElement(name = "CardExpYear")
    private Integer expYear;

    public Long getIdOfSubscription() {
        return idOfSubscription;
    }

    public void setIdOfSubscription(Long idOfSubscription) {
        this.idOfSubscription = idOfSubscription;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public Integer getClientIDType() {
        return clientIDType;
    }

    public void setClientIDType(Integer clientIDType) {
        this.clientIDType = clientIDType;
    }

    public String getContractID() {
        return contractID;
    }

    public void setContractID(String contractID) {
        this.contractID = contractID;
    }

    public String getAccountRegion() {
        return accountRegion;
    }

    public void setAccountRegion(String accountRegion) {
        this.accountRegion = accountRegion;
    }

    public Long getLowerLimitAmount() {
        return lowerLimitAmount;
    }

    public void setLowerLimitAmount(Long lowerLimitAmount) {
        this.lowerLimitAmount = lowerLimitAmount;
    }

    public Long getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Long paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Integer getSubscriptionPeriodOfValidity() {
        return subscriptionPeriodOfValidity;
    }

    public void setSubscriptionPeriodOfValidity(Integer subscriptionPeriodOfValidity) {
        this.subscriptionPeriodOfValidity = subscriptionPeriodOfValidity;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(Date validityDate) {
        this.validityDate = validityDate;
    }

    public Date getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Date deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public String getLastPaymentStatus() {
        return lastPaymentStatus;
    }

    public void setLastPaymentStatus(String lastPaymentStatus) {
        this.lastPaymentStatus = lastPaymentStatus;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }
}
