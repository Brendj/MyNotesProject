/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by anvarov on 01.03.2018.
 */
public class SumQtyAndPriceItem {

    private Long sumQty;
    private Long sumPrice;

    public SumQtyAndPriceItem() {
    }

    public SumQtyAndPriceItem(Long sumQty, Long sumPrice) {
        this.sumQty = sumQty;
        this.sumPrice = sumPrice;
    }

    public Long getSumQty() {
        return sumQty;
    }

    public void setSumQty(Long sumQty) {
        this.sumQty = sumQty;
    }

    public Long getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Long sumPrice) {
        this.sumPrice = sumPrice;
    }
}
