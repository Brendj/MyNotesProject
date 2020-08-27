/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by nuc on 15.06.2020.
 */
public class FPClientDiscounts {
    @JsonProperty("DiscountId")
    private Long discountId;

    @JsonProperty("DiscountName")
    private String discountName;

    public FPClientDiscounts(CategoryDiscount cd) {
        this.discountId = cd.getIdOfCategoryDiscount();
        this.discountName = cd.getCategoryName();
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }
}
