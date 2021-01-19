/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import ru.iteco.msp.models.CategoryDiscount;
import ru.iteco.msp.models.DiscountChangeHistory;
import ru.iteco.msp.models.enums.OperationType;
import ru.iteco.msp.service.ClientDiscountHistoryService;
import ru.iteco.msp.service.DiscountsService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class TransitTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(TransitTaskExecutor.class);

    private final CronTrigger transitCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolTransitTaskScheduler;
    private final DiscountsService discountsService;
    private final ClientDiscountHistoryService clientDiscountHistoryService;

    private static final int SAMPLE_SIZE = 50000;

    public TransitTaskExecutor(
            CronTrigger transitCronTrigger,
            ThreadPoolTaskScheduler threadPoolTransitTaskScheduler,
            DiscountsService discountsService,
            ClientDiscountHistoryService clientDiscountHistoryService){
        this.transitCronTrigger = transitCronTrigger;
        this.threadPoolTransitTaskScheduler = threadPoolTransitTaskScheduler;
        this.discountsService = discountsService;
        this.clientDiscountHistoryService = clientDiscountHistoryService;
    }

    @PostConstruct
    public void buildRunnableTask(){
        threadPoolTransitTaskScheduler.schedule(new RunnableTask(), transitCronTrigger);
    }

    class RunnableTask implements Runnable{

        @Override
        @Transactional
        public void run() {
            try {
                int i = 0;

                Pageable pageable = PageRequest.of(i, SAMPLE_SIZE, Sort.by("registrationDate"));
                List<DiscountChangeHistory> discountChangeHistoryList = discountsService
                        .getHistory(pageable);

                while(CollectionUtils.isNotEmpty(discountChangeHistoryList)) {
                    for (DiscountChangeHistory h : discountChangeHistoryList) {
                        if (StringUtils.isEmpty(h.getClient().getMeshGuid()) || (StringUtils.isBlank(h.getCategoriesDiscounts()) && StringUtils
                                .isBlank(h.getOldCategoriesDiscounts()))) {
                            continue;
                        }
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
                                if(!discount.getCategoryType().equals(0)){
                                    continue;
                                }

                                clientDiscountHistoryService.save(discount, h.getClient(), h.getRegistrationDate(),
                                        h.getComment(), type);
                            }
                        } else {
                            List<CategoryDiscount> dif = discountsService.getDiffDiscounts(newDiscounts, oldDiscounts);

                            for (CategoryDiscount d : dif) {
                                if (!d.getCategoryType().equals(0)) {
                                    continue;
                                }
                                type = getOperationType(oldDiscounts, newDiscounts, d.getIdOfCategoryDiscount().toString());

                                clientDiscountHistoryService.save(d, h.getClient(), h.getRegistrationDate(),
                                        h.getComment(), type);
                            }
                        }
                    }

                    i += SAMPLE_SIZE;
                    pageable = PageRequest.of(i, SAMPLE_SIZE, Sort.by("registrationDate"));
                    discountChangeHistoryList = discountsService.getHistory(pageable);
                }
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
}
