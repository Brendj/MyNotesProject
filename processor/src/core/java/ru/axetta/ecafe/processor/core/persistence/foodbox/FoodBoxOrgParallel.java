/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.foodbox;

import ru.axetta.ecafe.processor.core.persistence.Org;


public class FoodBoxOrgParallel {
    private Long foodboxparallelId;
    private Org org;
    private Integer parallel;
    private Boolean available;

    public Long getFoodboxparallelId() {
        return foodboxparallelId;
    }

    public void setFoodboxparallelId(Long foodboxparallelId) {
        this.foodboxparallelId = foodboxparallelId;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Integer getParallel() {
        return parallel;
    }

    public void setParallel(Integer parallel) {
        this.parallel = parallel;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
