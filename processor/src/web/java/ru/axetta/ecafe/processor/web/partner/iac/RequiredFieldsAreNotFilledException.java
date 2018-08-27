/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.iac;

public class RequiredFieldsAreNotFilledException extends Exception {
    public RequiredFieldsAreNotFilledException(String message) {
        super(message);
    }
}
