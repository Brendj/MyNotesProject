/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.transit.taskexecutor;

import ru.iteco.transit.models.CategoryDiscount;
import ru.iteco.transit.models.DiscountChangeHistory;
import ru.iteco.transit.models.enums.OperationType;
import ru.iteco.transit.service.ClientDiscountHistoryService;
import ru.iteco.transit.service.DiscountsService;
import ru.iteco.transit.service.FileSupportService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class TransitTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(TransitTaskExecutor.class);

    private final DiscountsService discountsService;
    private final ClientDiscountHistoryService clientDiscountHistoryService;
    private final FileSupportService fileSupportService;

    private static final int SAMPLE_SIZE = 500;

    public TransitTaskExecutor(
            DiscountsService discountsService,
            ClientDiscountHistoryService clientDiscountHistoryService,
            FileSupportService fileSupportService){
        this.discountsService = discountsService;
        this.clientDiscountHistoryService = clientDiscountHistoryService;
        this.fileSupportService = fileSupportService;
    }

    public void run() {
        try {
            Long lastProcessRegistryDate = fileSupportService.getLastProcessRegistryDate();
            List<DiscountChangeHistory> discountChangeHistoryList = null;

            Pageable pageable = PageRequest.of(0, SAMPLE_SIZE, Sort.by("registrationDate"));

            if(lastProcessRegistryDate == null) {
                discountChangeHistoryList = discountsService.getHistory(pageable);
            } else {
                discountChangeHistoryList = discountsService
                        .getHistoryGreatThenRegistryDate(pageable, lastProcessRegistryDate);
            }

            while(CollectionUtils.isNotEmpty(discountChangeHistoryList)) {
                for (DiscountChangeHistory h : discountChangeHistoryList) {
                    if (StringUtils.isEmpty(h.getClient().getMeshGuid()) || (StringUtils.isBlank(h.getCategoriesDiscounts()) && StringUtils
                            .isBlank(h.getOldCategoriesDiscounts()))) {
                        continue;
                    }
                    log.info(String
                            .format("Process history ID: %d Client ID: %d, New discounts: %s, Old discounts: %s",
                                    h.getIdOfDiscountChange(), h.getClient().getIdOfClient(),
                                    h.getCategoriesDiscounts(), h.getOldCategoriesDiscounts()
                            ));
                    List<String> newDiscounts = getSortedSplitList(h.getCategoriesDiscounts());
                    List<String> oldDiscounts = getSortedSplitList(h.getOldCategoriesDiscounts());
                    OperationType type;

                    if (newDiscounts.equals(oldDiscounts)) {
                        if (h.getOldDiscountMode().equals(h.getDiscountMode())) {
                            continue;
                        }
                        type = OperationType.CHANGE;

                        for(String id : newDiscounts){
                            CategoryDiscount discount = discountsService.getDiscountByStrId(id);
                            if(discount == null){
                                log.warn(String.format("No find discounts by ID: %s", id));
                                continue;
                            }
                            clientDiscountHistoryService.save(discount, h.getClient(), h.getRegistrationDate(),
                                    h.getComment(), type);
                        }
                    } else {
                        List<CategoryDiscount> dif = discountsService.getDiffDiscounts(newDiscounts, oldDiscounts);

                        for (CategoryDiscount d : dif) {
                            type = getOperationType(oldDiscounts, newDiscounts, d.getIdOfCategoryDiscount().toString());

                            clientDiscountHistoryService.save(d, h.getClient(), h.getRegistrationDate(),
                                    h.getComment(), type);
                        }
                    }
                    fileSupportService.writeLastProcessRegistryDate(h.getRegistrationDate());
                }
                discountChangeHistoryList = null;
                pageable = pageable.next();
                discountChangeHistoryList = discountsService.getHistory(pageable);
            }
            log.info("Process done");
        } catch (Exception e) {
            log.error("Critical error in process transit category change history to new entity, task interrupt", e);
        }
    }

    private OperationType getOperationType(List<String> oldDiscounts, List<String> newDif,
                                           String idOfCategoryDiscount) {
        if(oldDiscounts.contains(idOfCategoryDiscount) && newDif.contains(idOfCategoryDiscount)){
            return OperationType.CHANGE;
        } else if(oldDiscounts.contains(idOfCategoryDiscount)){
            return OperationType.DELETE;
        } else {
            return OperationType.ADD;
        }
    }

    private List<String> getSortedSplitList(String s) {
        if(StringUtils.isBlank(s)){
            return Collections.emptyList();
        }
        String[] arr = StringUtils.split(s, ",");
        Arrays.sort(arr);
        return Arrays.asList(arr);
    }
}
