/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 17.11.11
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
public class CurrentPosition {
    private long idOfPosition;
    private Contragent idOfContragentDebtor;
    private Contragent idOfContragentCreditor;
    private long summa;

    public CurrentPosition() {
        // For Hibernate only
    }

    public CurrentPosition(long idOfPosition, Contragent idOfContragentDebtor, Contragent idOfContragentCreditor,
            long summa) {
        this.idOfPosition = idOfPosition;
        this.idOfContragentDebtor = idOfContragentDebtor;
        this.idOfContragentCreditor = idOfContragentCreditor;
        this.summa = summa;
    }

    public long getIdOfPosition() {
        return idOfPosition;
    }

    public void setIdOfPosition(long idOfPosition) {
        this.idOfPosition = idOfPosition;
    }

    public Contragent getIdOfContragentDebtor() {
        return idOfContragentDebtor;
    }

    public void setIdOfContragentDebtor(Contragent idOfContragentDebtor) {
        this.idOfContragentDebtor = idOfContragentDebtor;
    }

    public Contragent getIdOfContragentCreditor() {
        return idOfContragentCreditor;
    }

    public void setIdOfContragentCreditor(Contragent idOfContragentCreditor) {
        this.idOfContragentCreditor = idOfContragentCreditor;
    }

    public long getSumma() {
        return summa;
    }

    public void setSumma(long summa) {
        this.summa = summa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CurrentPosition that = (CurrentPosition) o;

        if (idOfPosition != that.idOfPosition) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfPosition ^ (idOfPosition >>> 32));
    }

    @Override
    public String toString() {
        return "CurrentPosition{" + "idOfPosition=" + idOfPosition + ", idOfContragentDebtor=" + idOfContragentDebtor
                + ", idOfContragentCreditor=" + idOfContragentCreditor + ", summa=" + summa + '}';
    }
}
