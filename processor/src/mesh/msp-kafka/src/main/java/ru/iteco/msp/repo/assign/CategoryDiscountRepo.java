/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo.assign;

import ru.iteco.msp.models.CategoryDiscount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDiscountRepo extends JpaRepository<CategoryDiscount, Long> {
    List<CategoryDiscount> findAllByIdOfCategoryDiscountIn(List<Long> ids);
}
