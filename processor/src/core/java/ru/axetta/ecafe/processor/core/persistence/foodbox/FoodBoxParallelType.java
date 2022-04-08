/*
 * Copyright (c) 2022. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.foodbox;


public class FoodBoxParallelType {
    private Long parallelTypeId;
    private Integer parallel;

    public Integer getParallel() {
        return parallel;
    }

    public void setParallel(Integer parallel) {
        this.parallel = parallel;
    }

    public Long getParallelTypeId() {
        return parallelTypeId;
    }

    public void setParallelTypeId(Long parallelTypeId) {
        this.parallelTypeId = parallelTypeId;
    }
}
