/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

/**
 * Created by i.semenov on 21.03.2018.
 */
public class PreorderAccessDeniedException extends Exception {
    public PreorderAccessDeniedException() {
    }

    public PreorderAccessDeniedException(String message) {
        super(message);
    }
}
