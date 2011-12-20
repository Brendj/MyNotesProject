/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.rbkmoney;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.12.2009
 * Time: 11:44:57
 * To change this template use File | Settings | File Templates.
 */
public class ProtocolRequest {

    private final String eshopId;
    private final String paymentId;
    private final Long orderId;
    private final String eshopAccount;
    private final String serviceName;
    private final Double recipientAmount;
    private final String recipientCurrency;
    private final int paymentStatus;
    private final String userName;
    private final String userEmail;
    private final Date paymentData;
    private final String secretKey;
    private final String hash;

    public ProtocolRequest(HttpServletRequest request) throws Exception {
        DecimalFormat doubleNumberFormat = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        doubleNumberFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        doubleNumberFormat.applyPattern("########0.00");

        TimeZone sourceTimeZone = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-DD HH:MM:SS");
        timeFormat.setTimeZone(sourceTimeZone);

        this.eshopId = request.getParameter("eshopId");
        this.paymentId = request.getParameter("paymentId");
        this.orderId = Long.valueOf(request.getParameter("orderId"));
        this.eshopAccount = request.getParameter("eshopAccount");
        this.serviceName = request.getParameter("serviceName");
        this.recipientAmount = doubleNumberFormat.parse(request.getParameter("recipientAmount")).doubleValue();
        this.recipientCurrency = request.getParameter("recipientCurrency");
        this.paymentStatus = Integer.valueOf(request.getParameter("paymentStatus"));
        this.userName = request.getParameter("userName");
        this.userEmail = request.getParameter("userEmail");
        this.paymentData = timeFormat.parse(request.getParameter("paymentData"));
        this.secretKey = request.getParameter("secretKey");
        this.hash = request.getParameter("hash");
    }

    public String getEshopId() {
        return eshopId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getEshopAccount() {
        return eshopAccount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Double getRecipientAmount() {
        return recipientAmount;
    }

    public String getRecipientCurrency() {
        return recipientCurrency;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Date getPaymentData() {
        return paymentData;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "ProtocolRequest{" + "eshopId='" + eshopId + '\'' + ", paymentId='" + paymentId + '\'' + ", orderId="
                + orderId + ", eshopAccount='" + eshopAccount + '\'' + ", serviceName='" + serviceName + '\''
                + ", recipientAmount=" + recipientAmount + ", recipientCurrency='" + recipientCurrency + '\''
                + ", paymentStatus=" + paymentStatus + ", userName='" + userName + '\'' + ", userEmail='" + userEmail
                + '\'' + ", paymentData=" + paymentData + ", secretKey='" + secretKey + '\'' + ", hash='" + hash + '\''
                + '}';
    }
}
