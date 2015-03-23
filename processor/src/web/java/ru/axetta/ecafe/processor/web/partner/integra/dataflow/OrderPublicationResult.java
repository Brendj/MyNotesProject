/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

public class OrderPublicationResult extends Result {
    public Long id = null;

    public OrderPublicationResult(Long resultCode, String desc) {
        super(resultCode, desc);
        id = null;
    }

    public OrderPublicationResult() {
    }
}
