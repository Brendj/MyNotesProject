/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.service;

import ru.iteco.transit.models.CategoryDiscount;
import ru.iteco.transit.models.DiscountChangeHistory;
import ru.iteco.transit.repo.CategoryDiscountRepo;
import ru.iteco.transit.repo.DiscountChangeHistoryRepo;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class DiscountsService {
    private final DiscountChangeHistoryRepo discountChangeHistoryRepo;
    private final CategoryDiscountRepo categoryDiscountRepo;

    public DiscountsService(
            DiscountChangeHistoryRepo discountChangeHistoryRepo,
            CategoryDiscountRepo categoryDiscountRepo){
        this.discountChangeHistoryRepo = discountChangeHistoryRepo;
        this.categoryDiscountRepo = categoryDiscountRepo;
    }

    public List<DiscountChangeHistory> getHistory(Pageable pageable) {
        return discountChangeHistoryRepo.getAll(pageable);
    }

    public CategoryDiscount getDiscountByStrId(String idStr){
        Long id = Long.parseLong(idStr);
        return categoryDiscountRepo.findById(id).orElse(null);
    }

    public List<CategoryDiscount> getDiffDiscounts(List<String> newDiscounts, List<String> oldDiscounts) {
        List<String> dif = (List<String>) CollectionUtils.disjunction(newDiscounts, oldDiscounts);
        List<Long> ids = new LinkedList<>();
        for(String idStr : dif){
            if(StringUtils.isNumeric(idStr)){
                Long id = Long.parseLong(idStr);
                ids.add(id);
            }
        }

        return categoryDiscountRepo.findAllByIdOfCategoryDiscountIn(ids);
    }
}
