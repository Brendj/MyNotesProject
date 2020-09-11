/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by nuc on 22.06.2020.
 */
public class ResponseDiscountItemCategoryOrg {
    @JsonProperty("CategoryOrgName")
    private String categoryOrgName;

    @JsonProperty("OrgId")
    private Long categoryOrgId;

    public ResponseDiscountItemCategoryOrg(String categoryOrgName, Long categoryOrgId) {
        this.categoryOrgName = categoryOrgName;
        this.categoryOrgId = categoryOrgId;
    }

    public String getCategoryOrgName() {
        return categoryOrgName;
    }

    public void setCategoryOrgName(String categoryOrgName) {
        this.categoryOrgName = categoryOrgName;
    }

    public Long getCategoryOrgId() {
        return categoryOrgId;
    }

    public void setCategoryOrgId(Long categoryOrgId) {
        this.categoryOrgId = categoryOrgId;
    }
}
