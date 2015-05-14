/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * User: shamil
 * Date: 30.04.15
 * Time: 11:02
 */
public class ResCardsOperationsRegistryItem {

    public static final String SYNC_NAME = "RCO";

    public static final int OK= 0;
    public static final String OK_MESSAGE = null;
    public static final int ERROR_CARD_NOT_FOUND = 150;
    public static final String ERROR_CARD_NOT_FOUND_MESSAGE = "Карта не найдена";
    public static final int ERROR_CLIENT_NOT_FOUND = 155;
    public static final String ERROR_CLIENT_NOT_FOUND_MESSAGE = "Карта не найдена";
    public static final int ERROR_DUPLICATE = 160;
    public static final String ERROR_DUPLICATE_MESSAGE = "Такая карта уже существует";
    public static final int ERROR = 170;
    public static final String ERROR_MESSAGE = "ERROR";

    private long idOfOperation;
    private int result;
    private String error; // 1024


    public ResCardsOperationsRegistryItem(long idOfOperation, int result, String error) {
        this.idOfOperation = idOfOperation;
        this.result = result;
        this.error = error;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SYNC_NAME);
        element.setAttribute("IdOfOperation", Long.toString(this.idOfOperation));
        element.setAttribute("Result", Integer.toString(this.result));
        element.setAttribute("Error", this.error);
        return element;
    }
}
