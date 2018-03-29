/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by i.semenov on 29.03.2018.
 */
public class PreorderLinkOD {
    private Long idOfPreorderLinkOD;
    private String preorderGuid;
    private Long idOfOrg;
    private Long idOfOrder;
    private Long idOfOrderDetail;
    private Long qty;
    private Long price;

    public PreorderLinkOD() {

    }

    public PreorderLinkOD(String guid, OrderDetail orderDetail) {
        this.preorderGuid = guid;
        this.idOfOrg = orderDetail.getCompositeIdOfOrderDetail().getIdOfOrg();
        this.idOfOrder = orderDetail.getIdOfOrder();
        this.idOfOrderDetail = orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail();
        this.qty = orderDetail.getQty();
        this.price = orderDetail.getRPrice();
    }

    public String getPreorderGuid() {
        return preorderGuid;
    }

    public void setPreorderGuid(String preorderGuid) {
        this.preorderGuid = preorderGuid;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getIdOfPreorderLinkOD() {
        return idOfPreorderLinkOD;
    }

    public void setIdOfPreorderLinkOD(Long idOfPreorderLinkOD) {
        this.idOfPreorderLinkOD = idOfPreorderLinkOD;
    }
}
