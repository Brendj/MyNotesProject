/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.iteco.msp.enums.AssignOperationType;
import ru.iteco.msp.kafka.KafkaService;
import ru.iteco.msp.kafka.dto.AssignEvent;
import ru.iteco.msp.models.CategoryDiscount;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;
import ru.iteco.msp.models.ClientDiscountHistory;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class AssignTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(AssignTaskExecutor.class);

    private final CronTrigger assignCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolAssignTaskScheduler;
    private final KafkaService kafkaService;
    private final DiscountsService discountsService;

    @Value(value = "${kafka.task.execution.assign.samplesize}")
    private Integer sampleSize;

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
        @Transactional
        public void run() {
            Date begin;
            Date end;
            AssignOperationType type;
            ClientDTSZNDiscountInfo info = null;

            try {
                if (runPrimalLoad && !alreadyLoaded) {
                    beginPrimalLoad();
                    alreadyLoaded = true;
                } else {
                    end = new Date();
                    Date next = assignCronTrigger.nextExecutionTime(new SimpleTriggerContext());
                    long delta = next.getTime() - end.getTime();
                    begin = new Date(end.getTime() - delta);

                    List<ClientDiscountHistory> discountChangeHistoryList = discountsService.getNewHistoryByTime(begin);

                    for (ClientDiscountHistory h : discountChangeHistoryList) {
                        if (StringUtils.isEmpty(h.getClient().getMeshGuid())) {
                            continue;
                        }
                        type = AssignOperationType.getAssignTypeByOperationType(h.getOperationType());
                        if(AssignOperationType.CHANGE.equals(type) || h.getCategoryDiscount().getCategoryDiscountDTSZN() != null){
                            info = discountsService.getChangedDiscounts(
                                    h.getCategoryDiscount().getCategoryDiscountDTSZN().getCode(), h.getClient(),
                                    takeThreeMinute(h.getRegistryDate()), addThreeMinute(h.getRegistryDate())
                            );
                        }

                        AssignEvent event = AssignEvent.build(h.getCategoryDiscount(), h.getClient(), type, info);
                        kafkaService.sendAssign(mapper.writeValueAsString(event));
                    }
                }
            } catch (Exception e) {
                log.error("Critical error in process sending assign MSP-discounts info, task interrupt", e);
            }
        }

        private Date takeThreeMinute(Long time) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.MINUTE, -3);
            return calendar.getTime();
        }

        private Date addThreeMinute(Long time) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.MINUTE, 3);
            return calendar.getTime();
        }

        @Transactional(propagation = Propagation.SUPPORTS)
        public void beginPrimalLoad() {
            try {
                Pageable pageable = PageRequest.of(0, sampleSize, Sort.by("idOfClient").and(Sort.by("registrationDate").descending()));
                List<DiscountChangeHistory> discountChangeHistoryList = discountsService.getDistinctHistoryByClient(pageable);
                while (CollectionUtils.isNotEmpty(discountChangeHistoryList)) {
                    for(DiscountChangeHistory h : discountChangeHistoryList){
                        if (StringUtils.isEmpty(h.getClient().getMeshGuid()) ||
                                (StringUtils.isBlank(h.getCategoriesDiscounts()) && StringUtils.isBlank(h.getOldCategoriesDiscounts()))) {
                            continue;
                        }
                        List<String> discounts = getSortedSplitList(h.getCategoriesDiscounts());
                        for(String categoryId : discounts){
                            CategoryDiscount discount = discountsService.getDiscountByStrId(categoryId);
                            if(discount == null){
                                log.warn("Not exist discount by ID:" + categoryId);
                                continue;
                            }
                            if(discount.getCategoryType() != 0){
                                continue;
                            }

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

                    pageable = pageable.next();
                    discountChangeHistoryList = discountsService.getDistinctHistoryByClient(pageable);
                }
            } catch (Exception e) {
                log.error("Critical error in process sending primary discount change history records, task interrupt", e);
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
