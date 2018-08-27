/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.iac;

public class CardAlreadyUsedException extends Exception {
    public CardAlreadyUsedException(String message) {
        super(message);
    }
}
