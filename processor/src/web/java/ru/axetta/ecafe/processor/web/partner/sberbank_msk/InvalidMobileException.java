/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_msk;

public class InvalidMobileException extends Exception {
    public InvalidMobileException(String message) {
        super(message);
    }
}
