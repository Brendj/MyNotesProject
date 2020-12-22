/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.service;

import ru.iteco.msp.models.*;
import ru.iteco.msp.repo.CategoryDiscountDTSZNRepo;
import ru.iteco.msp.repo.CategoryDiscountRepo;
import ru.iteco.msp.repo.ClientDTSZNDiscountInfoRepo;
import ru.iteco.msp.repo.DiscountChangeHistoryRepo;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
public class DiscountsService {
    private final DiscountChangeHistoryRepo discountChangeHistoryRepo;
    private final ClientDTSZNDiscountInfoRepo clientDTSZNDiscountInfoRepo;
    private final CategoryDiscountRepo categoryDiscountRepo;
    private final CategoryDiscountDTSZNRepo categoryDiscountDTSZNRepo;

    public DiscountsService(
            DiscountChangeHistoryRepo discountChangeHistoryRepo,
            ClientDTSZNDiscountInfoRepo clientDTSZNDiscountInfoRepo,
            CategoryDiscountRepo categoryDiscountRepo,
            CategoryDiscountDTSZNRepo categoryDiscountDTSZNRepo){
        this.discountChangeHistoryRepo = discountChangeHistoryRepo;
        this.clientDTSZNDiscountInfoRepo = clientDTSZNDiscountInfoRepo;
        this.categoryDiscountRepo = categoryDiscountRepo;
        this.categoryDiscountDTSZNRepo = categoryDiscountDTSZNRepo;
    }

    public List<ClientDTSZNDiscountInfo> getChangedDiscounts(Client client, Date begin, Date end) {
        return clientDTSZNDiscountInfoRepo.findAllByClientAndLastUpdate(client, begin.getTime(), end.getTime());
    }

    public List<DiscountChangeHistory> getHistoryByTime(Date end) {
        return discountChangeHistoryRepo.getAllByRegistrationDateGreaterThanEqual(end.getTime());
    }

    public List<CategoryDiscount> getDiffDiscounts(List<String> first, List<String> second) {
        List<String> dif = (List<String>) CollectionUtils.disjunction(first, second);
        List<Long> ids = new LinkedList<>();
        for(String idStr : dif){
            if(!StringUtils.isNumeric(idStr)){
                Long id = Long.parseLong(idStr);
                ids.add(id);
            }
        }

        return categoryDiscountRepo.findAllByIdOfCategoryDiscountIn(ids);
    }

    public CategoryDiscountDTSZN getCategoryDiscountDTSZNByCode(Long dtisznCode) {
        return categoryDiscountDTSZNRepo.findByCode(dtisznCode.intValue());
    }

    public ClientDTSZNDiscountInfo getLastInfoByClientAndCode(Client client, Integer code) {
        return clientDTSZNDiscountInfoRepo.findFirstByDTISZNCodeAndClientOrderByLastUpdateDesc(code.longValue(), client);
    }
}
