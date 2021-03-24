/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo;

import ru.iteco.msp.models.CategoryDiscount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDiscountRepo extends JpaRepository<CategoryDiscount, Long> {
    List<CategoryDiscount> findAllByIdOfCategoryDiscountIn(List<Long> ids);
}
