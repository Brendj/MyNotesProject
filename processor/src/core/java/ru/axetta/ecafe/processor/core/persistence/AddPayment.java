/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 29.11.11
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
public class AddPayment {
   private Long idOfAddPayment;
   private Contragent contragentPayer;
   private Contragent contragentReceiver;
   private Long summa;
   private String comment;
   private Date fromDate;
   private Date toDate;

    public AddPayment() {

    }

    public AddPayment(Long idOfAddPayment, Contragent contragentPayer, Contragent contragentReceiver, Long summa,
            String comment, Date fromDate, Date toDate) {
        this.idOfAddPayment = idOfAddPayment;
        this.contragentPayer = contragentPayer;
        this.contragentReceiver = contragentReceiver;
        this.summa = summa;
        this.comment = comment;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Long getIdOfAddPayment() {
        return idOfAddPayment;
    }

    public void setIdOfAddPayment(Long idOfAddPayment) {
        this.idOfAddPayment = idOfAddPayment;
    }

    public Contragent getContragentPayer() {
        return contragentPayer;
    }

    public void setContragentPayer(Contragent contragentPayer) {
        this.contragentPayer = contragentPayer;
    }

    public Contragent getContragentReceiver() {
        return contragentReceiver;
    }

    public void setContragentReceiver(Contragent contragentReceiver) {
        this.contragentReceiver = contragentReceiver;
    }

    public Long getSumma() {
        return summa;
    }

    public void setSumma(Long summa) {
        this.summa = summa;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AddPayment that = (AddPayment) o;

        if (!idOfAddPayment.equals(that.idOfAddPayment)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfAddPayment.hashCode();
    }

    @Override
    public String toString() {
        return "AddPayment{" + "idOfAddPayment=" + idOfAddPayment + ", contragentPayer=" + contragentPayer
                + ", contragentReceiver=" + contragentReceiver + ", summa=" + summa + ", comment='" + comment
                + '\'' + ", fromDate=" + fromDate + ", toDate=" + toDate + '}';
    }
}
