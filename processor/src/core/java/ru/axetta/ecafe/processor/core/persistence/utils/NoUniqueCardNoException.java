/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

public class NoUniqueCardNoException extends Exception {
    public NoUniqueCardNoException(String message) {
        super(message);
    }
}
