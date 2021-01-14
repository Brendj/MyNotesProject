/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.iteco.msp.kafka.KafkaService;
import ru.iteco.msp.kafka.dto.AssignEvent;
import ru.iteco.msp.models.CategoryDiscount;
import ru.iteco.msp.models.CategoryDiscountDTSZN;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;
import ru.iteco.msp.models.DiscountChangeHistory;
import ru.iteco.msp.service.DiscountsService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class AssignTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(AssignTaskExecutor.class);
    private static final int SAMPLE_SIZE = 100000;

    private final CronTrigger assignCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolAssignTaskScheduler;
    private final KafkaService kafkaService;
    private final DiscountsService discountsService;

    private final ObjectMapper mapper = new ObjectMapper();

    private boolean alreadyLoaded = false;

    @Value(value = "${msp.run.primary.load}")
    private Boolean runPrimalLoad;

    public AssignTaskExecutor(
            CronTrigger assignCronTrigger,
            ThreadPoolTaskScheduler threadPoolAssignTaskScheduler,
            KafkaService kafkaService,
            DiscountsService discountsService){
        this.assignCronTrigger = assignCronTrigger;
        this.threadPoolAssignTaskScheduler = threadPoolAssignTaskScheduler;
        this.kafkaService = kafkaService;
        this.discountsService = discountsService;
    }

    @PostConstruct
    public void buildRunnableTask(){
        threadPoolAssignTaskScheduler.schedule(new RunnableTask(), assignCronTrigger);
    }

    class RunnableTask implements Runnable{

        @Override
        public void run() {
            Date begin;
            Date end;
            AssignOperationType type;
            try {
                if(runPrimalLoad && !alreadyLoaded){
                    beginPrimalLoad();
                    alreadyLoaded = true;
                } else {
                    end = new Date();
                    Date next = assignCronTrigger.nextExecutionTime(new SimpleTriggerContext());
                    long delta = next.getTime() - end.getTime();
                    begin = new Date(end.getTime() - delta);

                    List<DiscountChangeHistory> discountChangeHistoryList = discountsService.getHistoryByTime(begin);

                    for (DiscountChangeHistory h : discountChangeHistoryList) {
                        if (StringUtils.isEmpty(h.getClient().getMeshGuid()) || (StringUtils.isBlank(h.getCategoriesDiscounts()) && StringUtils
                                .isBlank(h.getOldCategoriesDiscounts()))) {
                            continue;
                        }
                        List<String> newDiscounts = getSortedSplitList(h.getCategoriesDiscounts());
                        List<String> oldDiscounts = getSortedSplitList(h.getOldCategoriesDiscounts());

                        if (newDiscounts.equals(oldDiscounts)) {
                            if (h.getOldDiscountMode().equals(h.getDiscountMode())) {
                                continue;
                            }
                            type = AssignOperationType.CHANGE;

                            List<ClientDTSZNDiscountInfo> changedDiscounts = discountsService
                                    .getChangedDiscounts(h.getClient(), begin, end);
                            for (ClientDTSZNDiscountInfo info : changedDiscounts) {
                                CategoryDiscountDTSZN categoryDiscountDTSZN = discountsService.getCategoryDiscountDTSZNByCode(info.getDTISZNCode());
                                AssignEvent event = AssignEvent
                                        .build(categoryDiscountDTSZN.getCategoryDiscount(), h.getClient(), type, info);

                                kafkaService.sendAssign(mapper.writeValueAsString(event));
                            }
                        } else {
                            List<CategoryDiscount> dif = discountsService.getDiffDiscounts(newDiscounts, oldDiscounts);

                            for (CategoryDiscount d : dif) {
                                if (!d.getCategoryType().equals(0)) {
                                    continue;
                                }
                                type = getOperationType(oldDiscounts, newDiscounts, d.getIdOfCategoryDiscount().toString());

                                ClientDTSZNDiscountInfo info = null;
                                if (d.getCategoryDiscountDTSZN() != null) {
                                    info = discountsService.getLastInfoByClientAndCode(h.getClient(),
                                            d.getCategoryDiscountDTSZN().getCode());
                                }
                                AssignEvent event = AssignEvent.build(d, h.getClient(), type, info);

                                kafkaService.sendAssign(mapper.writeValueAsString(event));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Critical error in process sending assign MSP-discounts info, task interrupt", e);
            }
        }

        private void beginPrimalLoad() {
            int i = 0;
            try {
                Pageable pageable = PageRequest.of(i, SAMPLE_SIZE, Sort.by("client", "registrationDate"));
                List<DiscountChangeHistory> discountChangeHistoryList = discountsService.getDistinctHistoryByClient(pageable);
                while (CollectionUtils.isNotEmpty(discountChangeHistoryList)) {
                    for(DiscountChangeHistory h : discountChangeHistoryList){
                        if (StringUtils.isEmpty(h.getClient().getMeshGuid()) || (StringUtils.isBlank(h.getCategoriesDiscounts()) && StringUtils
                                .isBlank(h.getOldCategoriesDiscounts()))) {
                            continue;
                        }
                        List<String> discounts = getSortedSplitList(h.getCategoriesDiscounts());
                        for(String categoryId : discounts){
                            CategoryDiscount discount = discountsService.getDiscountByStrId(categoryId);

                            ClientDTSZNDiscountInfo info = null;
                            if (discount.getCategoryDiscountDTSZN() != null) {
                                info = discountsService.getLastInfoByClientAndCode(h.getClient(),
                                        discount.getCategoryDiscountDTSZN().getCode());
                            }

                            AssignEvent event = AssignEvent.build(discount, h.getClient(), AssignOperationType.ADD,
                                    info);
                            kafkaService.sendAssign(mapper.writeValueAsString(event));
                        }

                    }

                    i += SAMPLE_SIZE;
                    pageable = PageRequest.of(i, SAMPLE_SIZE, Sort.by("client").and(Sort.by("registrationDate").descending()));
                    discountChangeHistoryList = discountsService.getDistinctHistoryByClient(pageable);
                }
            } catch (Exception e) {
                log.error("Critical error in process sending primary discount change history records, task interrupt", e);
            }
        }

        private AssignOperationType getOperationType(List<String> oldDiscounts, List<String> newDif,
                                                     String idOfCategoryDiscount) {
            if(oldDiscounts.contains(idOfCategoryDiscount)){
                return AssignOperationType.DELETE;
            } else {
                return AssignOperationType.ADD;
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
