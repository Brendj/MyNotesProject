/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client.items;

import ru.axetta.ecafe.processor.core.persistence.ClientDTISZNDiscountStatus;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientDiscountItem {
    private Long idOfClientDTiSZNDiscountInfo;
    private Long codeDSZN;
    private String descriptionDSZN;
    private Long idOfCategoryDiscount;
    private String categoryDiscountName;

    private Integer discountMode;
    private String printedCategoriesDiscounts;
    private String printedCategoriesDiscountsDSZN;
    private String lastDiscountsUpdate;
    private String dateStart;
    private String dateEnd;
    private String status;
    private Boolean appointedMSP;
    private Boolean active;

    private final DateFormat CLIENT_DISCOUNTS_DATA_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final DateFormat DSZN_DISCOUNTS_DATA_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public ClientDiscountItem(Long idOfCategoryDiscount, String categoryName, Long idOfClientDTiSZNDiscountInfo,
            Long code, String descriptionDSZN, Integer status, Date dateStart, Date dateEnd,
            Date lastDiscountsUpdate, Integer discountMode, Boolean appointedMSP, Boolean active) {
        this.discountMode = discountMode;
        this.idOfCategoryDiscount = idOfCategoryDiscount;
        this.categoryDiscountName = StringUtils.defaultString(categoryName, "-");
        this.idOfClientDTiSZNDiscountInfo = idOfClientDTiSZNDiscountInfo;
        this.codeDSZN = code;
        this.descriptionDSZN = StringUtils.defaultString(descriptionDSZN, "-");
        this.dateStart = dateStart == null ? "-" : DSZN_DISCOUNTS_DATA_FORMAT.format(dateStart);
        this.dateEnd = dateEnd == null ? "-" : DSZN_DISCOUNTS_DATA_FORMAT.format(dateEnd);
        this.lastDiscountsUpdate = lastDiscountsUpdate == null || idOfCategoryDiscount == null ? "-" : CLIENT_DISCOUNTS_DATA_FORMAT.format(lastDiscountsUpdate);
        this.status = status == null ? "-" :
                      status == ClientDTISZNDiscountStatus.NOT_CONFIRMED.getValue() ? ClientDTISZNDiscountStatus.NOT_CONFIRMED.getDescription() :
                      status == ClientDTISZNDiscountStatus.CONFIRMED.getValue() ? ClientDTISZNDiscountStatus.CONFIRMED.getDescription() :
                      status == ClientDTISZNDiscountStatus.NONE.getValue() ? ClientDTISZNDiscountStatus.NONE.getDescription() : "Неизвестный статус";

        printedCategoriesDiscounts = idOfCategoryDiscount == null ? "-" : idOfCategoryDiscount + " - " + categoryName;
        printedCategoriesDiscountsDSZN =  code == null ? "-" : code + " - " + descriptionDSZN;
        this.appointedMSP = appointedMSP;
        this.active = active;
    }

    public Integer getDiscountMode() {
        return discountMode;
    }

    public void setDiscountMode(Integer discountMode) {
        this.discountMode = discountMode;
    }

    public String getPrintedCategoriesDiscounts() {
        return printedCategoriesDiscounts;
    }

    public void setPrintedCategoriesDiscounts(String categoriesDiscounts) {
        this.printedCategoriesDiscounts = categoriesDiscounts;
    }

    public String getPrintedCategoriesDiscountsDSZN() {
        return printedCategoriesDiscountsDSZN;
    }

    public void setPrintedCategoriesDiscountsDSZN(String categoriesDiscountsDSZN) {
        this.printedCategoriesDiscountsDSZN = categoriesDiscountsDSZN;
    }

    public String getLastDiscountsUpdate() {
        return lastDiscountsUpdate;
    }

    public void setLastDiscountsUpdate(String lastDiscountsUpdate) {
        this.lastDiscountsUpdate = lastDiscountsUpdate;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getIdOfClientDTiSZNDiscountInfo() {
        return idOfClientDTiSZNDiscountInfo;
    }

    public Long getIdOfCategoryDiscount() {
        return idOfCategoryDiscount;
    }

    public String getDescriptionDSZN() {
        return descriptionDSZN;
    }

    public void setDescriptionDSZN(String descriptionDSZN) {
        this.descriptionDSZN = descriptionDSZN;
    }

    public Long getCodeDSZN() {
        return codeDSZN;
    }

    public void setCodeDSZN(Long codeDSZN) {
        this.codeDSZN = codeDSZN;
    }

    public String getCategoryDiscountName() {
        return categoryDiscountName;
    }

    public void setCategoryDiscountName(String categoryDiscountName) {
        this.categoryDiscountName = categoryDiscountName;
    }

    public void setIdOfClientDTiSZNDiscountInfo(Long idOfClientDTiSZNDiscountInfo) {
        this.idOfClientDTiSZNDiscountInfo = idOfClientDTiSZNDiscountInfo;
    }

    public void setIdOfCategoryDiscount(Long idOfCategoryDiscount) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
    }

    public Boolean getAppointedMSP() {
        return appointedMSP;
    }

    public void setAppointedMSP(Boolean appointedMSP) {
        this.appointedMSP = appointedMSP;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
