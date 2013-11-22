/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 24.10.13
 * Time: 14:43
 */

@XmlRootElement(name = "RegularPaymentList")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegularPaymentList {

    @XmlElement(name = "RegularPayment")
    private List<RegularPaymentInfo> list;

    public List<RegularPaymentInfo> getList() {
        return list;
    }

    public void setList(List<RegularPaymentInfo> list) {
        this.list = list;
    }

    @XmlRootElement(name = "RegularPayment")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RegularPaymentInfo {

        @XmlElement(name = "PaymentAmount")
        private Long paymentAmount;
        @XmlElement(name = "PaymentDate")
        @XmlSchemaType(name = "dateTime")
        private Date paymentDate;
        @XmlElement(name = "ClientBalance")
        private Long clientBalance;
        @XmlElement(name = "ThresholdAmount")
        private Long thresholdAmount;
        @XmlElement(name = "PaymentStatus")
        private String paymentStatus;

        public RegularPaymentInfo() {
        }

        public RegularPaymentInfo(Long paymentAmount, Date paymentDate, Long clientBalance, Long thresholdAmount,
                String paymentStatus) {
            this.paymentAmount = paymentAmount;
            this.paymentDate = paymentDate;
            this.clientBalance = clientBalance;
            this.thresholdAmount = thresholdAmount;
            this.paymentStatus = paymentStatus;
        }

        public Long getPaymentAmount() {
            return paymentAmount;
        }

        public void setPaymentAmount(Long paymentAmount) {
            this.paymentAmount = paymentAmount;
        }

        public Date getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(Date paymentDate) {
            this.paymentDate = paymentDate;
        }

        public Long getClientBalance() {
            return clientBalance;
        }

        public void setClientBalance(Long clientBalance) {
            this.clientBalance = clientBalance;
        }

        public Long getThresholdAmount() {
            return thresholdAmount;
        }

        public void setThresholdAmount(Long thresholdAmount) {
            this.thresholdAmount = thresholdAmount;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
    }
}
