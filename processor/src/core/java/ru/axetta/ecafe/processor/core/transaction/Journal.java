/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.transaction;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 25.01.12
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */

public class Journal  {

    private static final Logger logger= LoggerFactory.getLogger(Journal.class);

    private long idOfTransactionJournal;
    private String cartCodeType;

    public Journal(long idOfTransactionJournal, String cartCodeType) {
        this.idOfTransactionJournal = idOfTransactionJournal;
        this.cartCodeType = cartCodeType;
    }

    public long getIdOfTransactionJournal() {
        return idOfTransactionJournal;
    }

    public void setIdOfTransactionJournal(long idOfTransactionJournal) {
        this.idOfTransactionJournal = idOfTransactionJournal;
    }

    public String getCartCodeType() {
        return cartCodeType;
    }

    public void setCartCodeType(String cartCodeType) {
        this.cartCodeType = cartCodeType;
    }
}
