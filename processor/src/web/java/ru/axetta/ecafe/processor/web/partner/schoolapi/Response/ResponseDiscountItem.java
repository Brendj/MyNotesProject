/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ResponseDiscountItem {
    @JsonProperty("Discount")
    private String categoryName;

    @JsonProperty("DiscountId")
    private Long idOfCategoryDiscount;

    @JsonProperty("CategoryType")
    private Integer categoryType;

    @JsonProperty("OrgType")
    private Integer organizationType;

    @JsonProperty("BlockedToChange")
    private Boolean blockedToChange;

    @JsonProperty("CategoryOrgs")
    private List<ResponseDiscountItemCategoryOrg> categoryOrgs;

    public ResponseDiscountItem() {

    }

    public ResponseDiscountItem(CategoryDiscount categoryDiscount) {
        this.categoryName = categoryDiscount.getCategoryName();
        this.idOfCategoryDiscount = categoryDiscount.getIdOfCategoryDiscount();
        this.categoryType = categoryDiscount.getCategoryType().getValue();
        this.organizationType = categoryDiscount.getOrgType();
        this.blockedToChange = categoryDiscount.getBlockedToChange();
        this.categoryOrgs = new ArrayList<>();
    }

    public void addCategoryOrg(String categoryOrgName, Long categoryOrgId) {
        ResponseDiscountItemCategoryOrg item = new ResponseDiscountItemCategoryOrg(categoryOrgName, categoryOrgId);
        categoryOrgs.add(item);
    }

    @Override
    public int hashCode() {
        return idOfCategoryDiscount.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResponseDiscountItem)) return false;
        return idOfCategoryDiscount != null && idOfCategoryDiscount.equals(((ResponseDiscountItem) o).getIdOfCategoryDiscount());
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getIdOfCategoryDiscount() {
        return idOfCategoryDiscount;
    }

    public void setIdOfCategoryDiscount(Long idOfCategoryDiscount) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
    }

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
    }

    public Boolean getBlockedToChange() {
        return blockedToChange;
    }

    public void setBlockedToChange(Boolean blockedToChange) {
        this.blockedToChange = blockedToChange;
    }

    public List<ResponseDiscountItemCategoryOrg> getCategoryOrgs() {
        return categoryOrgs;
    }

    public void setCategoryOrgs(List<ResponseDiscountItemCategoryOrg> categoryOrgs) {
        this.categoryOrgs = categoryOrgs;
    }
}
