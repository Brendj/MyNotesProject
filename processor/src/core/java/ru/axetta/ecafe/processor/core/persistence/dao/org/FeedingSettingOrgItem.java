/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.org;

/**
 * Created by nuc on 16.12.2020.
 */
public class FeedingSettingOrgItem {
    private Long limit;
    private Long discount;
    private Boolean useDiscount;

    public FeedingSettingOrgItem(Long limit, Long discount, Boolean useDiscount) {
        this.limit = limit;
        this.discount = discount;
        this.useDiscount = useDiscount;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Boolean getUseDiscount() {
        return useDiscount;
    }

    public void setUseDiscount(Boolean useDiscount) {
        this.useDiscount = useDiscount;
    }
}
