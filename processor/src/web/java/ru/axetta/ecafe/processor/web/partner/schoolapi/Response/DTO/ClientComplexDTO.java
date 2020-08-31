/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import ru.axetta.ecafe.processor.core.persistence.ComplexInfo;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class ClientComplexDTO {
    @JsonIgnore
    public static final String ORDER_PAID = "Оплачен";
    @JsonIgnore
    public static final String ORDER_CANCELLED = "Отменен";

    @JsonProperty("Discount")
    private String discount;
    @JsonProperty("ComplexName")
    private String complexName;
    @JsonProperty("ToPay")
    private Boolean toPay;
    @JsonProperty("MSurname")
    private String mSurname;
    @JsonProperty("MName")
    private String mName;
    @JsonProperty("MSecondName")
    private String mSecondName;
    @JsonProperty("Order")
    private Boolean order;
    @JsonIgnore
    private ComplexInfo complexInfo;
    @JsonIgnore
    private DiscountRule discountRule;

    public ClientComplexDTO(){

    }

    public ClientComplexDTO(String discount, ComplexInfo complexInfo, String mSurname, String mName, String mSecondName, DiscountRule discountRule){
        this.discount = discount;
        this.complexInfo = complexInfo;
        this.complexName = complexInfo.getComplexName();
        this.toPay = false;
        this.order = null;
        this.mSurname = mSurname;
        this.mName = mName;
        this.mSecondName = mSecondName;
        this.discountRule = discountRule;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Boolean getToPay() {
        return toPay;
    }

    public void setToPay(Boolean toPay) {
        this.toPay = toPay;
    }

    public String getmSurname() {
        return mSurname;
    }

    public void setmSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSecondName() {
        return mSecondName;
    }

    public void setmSecondName(String mSecondName) {
        this.mSecondName = mSecondName;
    }

    public Boolean getOrder() {
        return order;
    }

    public void setOrder(Boolean order) {
        this.order = order;
    }

    public ComplexInfo getComplexInfo() {
        return complexInfo;
    }

    public void setComplexInfo(ComplexInfo complexInfo) {
        this.complexInfo = complexInfo;
    }

    public DiscountRule getDiscountRule() { return discountRule; }

    public void setDiscountRule(DiscountRule discountRule) { this.discountRule = discountRule; }
}
