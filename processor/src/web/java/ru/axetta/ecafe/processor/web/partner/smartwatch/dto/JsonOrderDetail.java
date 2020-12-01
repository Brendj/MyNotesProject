/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch.dto;

public class JsonOrderDetail {
    private String goodName;
    private Long qty;
    private Long rPrice;

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getrPrice() {
        return rPrice;
    }

    public void setrPrice(Long rPrice) {
        this.rPrice = rPrice;
    }
}
