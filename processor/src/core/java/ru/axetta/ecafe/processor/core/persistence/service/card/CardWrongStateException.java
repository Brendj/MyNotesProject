/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.card;

public class CardWrongStateException extends Exception {
    public CardWrongStateException(String message) {
        super(message);
    }
}