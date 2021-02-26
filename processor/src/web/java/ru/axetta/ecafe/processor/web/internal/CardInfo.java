/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import java.util.List;

public class CardInfo {
    private List<CardInfoItem> items;

    public List<CardInfoItem> getItems() {
        return items;
    }

    public void setItems(List<CardInfoItem> items) {
        this.items = items;
    }
}
