/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo.assign;

import ru.iteco.msp.models.CategoryDiscountDTSZN;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryDiscountDTSZNRepo extends JpaRepository<CategoryDiscountDTSZN, Long> {
    CategoryDiscountDTSZN findByCode(Integer code);
}
