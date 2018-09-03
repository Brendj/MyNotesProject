/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

/**
 * Created by i.semenov on 31.08.2018.
 */
public class MenuDetailNotExistsException extends Exception {
    public MenuDetailNotExistsException(String message) {
        super(message);
    }
}
