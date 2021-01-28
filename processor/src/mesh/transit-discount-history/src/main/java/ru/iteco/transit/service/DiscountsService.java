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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class DiscountsService {
    private static final Logger log = LoggerFactory.getLogger(DiscountsService.class);

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
        Long id = null;
        try {
            id = Long.parseLong(idStr);
        } catch (Exception e){
            log.error(String.format("Get exception when parse ID %s", idStr), e);
            return null;
        }

        return categoryDiscountRepo.findById(id).orElse(null);
    }

    public List<CategoryDiscount> getDiffDiscounts(List<String> newDiscounts, List<String> oldDiscounts) {
        List<String> dif = (List<String>) CollectionUtils.disjunction(newDiscounts, oldDiscounts);
        List<Long> ids = new LinkedList<>();
        for(String idStr : dif){
            if(StringUtils.isNumeric(idStr)){
                Long id = null;
                try {
                    id = Long.parseLong(idStr);
                } catch (Exception ignore){
                    continue;
                }
                ids.add(id);
            }
        }

        return categoryDiscountRepo.findAllByIdOfCategoryDiscountIn(ids);
    }
}
