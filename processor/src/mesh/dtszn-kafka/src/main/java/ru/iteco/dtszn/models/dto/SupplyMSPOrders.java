/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.models.dto;

public class SupplyMSPOrders {
    private Long idOfOrg;
    private Long idOfOrder;
    private String meshGUID;
    private Integer code;
    private String dtsznCodes;
    private String categoryName;
    private Long orderDate;
    private Long rSum;
    private Long organizationId;
    private String details;

    public SupplyMSPOrders() {
    }

    public SupplyMSPOrders(Long idOfOrg, Long idOfOrder, String meshGUID, Integer code, String dtsznCodes,
                           String categoryName, Long orderDate, Long rSum, Long organizationId, String details) {
        this.idOfOrg = idOfOrg;
        this.idOfOrder = idOfOrder;
        this.meshGUID = meshGUID;
        this.code = code;
        this.dtsznCodes = dtsznCodes;
        this.categoryName = categoryName;
        this.orderDate = orderDate;
        this.rSum = rSum;
        this.organizationId = organizationId;
        this.details = details;
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

    public String getMeshGUID() {
        return meshGUID;
    }

    public void setMeshGUID(String meshGUID) {
        this.meshGUID = meshGUID;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDtsznCodes() {
        return dtsznCodes;
    }

    public void setDtsznCodes(String dtsznCodes) {
        this.dtsznCodes = dtsznCodes;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Long orderDate) {
        this.orderDate = orderDate;
    }

    public Long getrSum() {
        return rSum;
    }

    public void setrSum(Long rSum) {
        this.rSum = rSum;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
