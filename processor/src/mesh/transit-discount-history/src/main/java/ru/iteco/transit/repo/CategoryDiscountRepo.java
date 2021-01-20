/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.repo;

import ru.iteco.transit.models.CategoryDiscount;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryDiscountRepo extends CrudRepository<CategoryDiscount, Long> {
    List<CategoryDiscount> findAllByIdOfCategoryDiscountIn(List<Long> ids);
}
