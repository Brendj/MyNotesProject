/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.nsi;

import generated.nsiws_delta.Item;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 04.07.14
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class NSIDeltaException extends Exception {
    protected Item item;

    public NSIDeltaException() {
    }

    public NSIDeltaException(String message) {
        super(message);
    }

    public NSIDeltaException(String message, Throwable cause) {
        super(message, cause);
    }

    public NSIDeltaException(Throwable cause) {
        super(cause);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}