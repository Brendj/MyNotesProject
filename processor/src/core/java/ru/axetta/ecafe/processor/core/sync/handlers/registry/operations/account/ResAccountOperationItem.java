/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.operations.account;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * User: Shamil
 * Date: 20.02.15
 */
public class ResAccountOperationItem {
    public static final String SYNC_NAME = "RAT";

    public static final int OK = 0;
    public static final int DUPLICATE = 160;
    public static final int ERROR = 999;

    private long idOfOperation;
    private int result;
    private String error;


    public ResAccountOperationItem(long idOfOperation, int result, String error) {
        this.idOfOperation = idOfOperation;
        this.result = result;
        this.error = error;
    }

    public long getIdOfOperation() {
        return idOfOperation;
    }

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SYNC_NAME);
        element.setAttribute("IdOfOperation", Long.toString(this.idOfOperation));
        element.setAttribute("Result", Integer.toString(this.result));
        element.setAttribute("Error", this.error);
        if (null != this.error) {
            element.setAttribute("Error", this.error);
        }
        return element;
    }

    @Override
    public String toString() {
        return "Item{" + "idOfOrder=" + idOfOperation + ", result=" + result + ", error='" + error + '\'' + '}';
    }
}
