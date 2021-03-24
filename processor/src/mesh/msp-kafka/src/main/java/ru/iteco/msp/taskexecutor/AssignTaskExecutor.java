/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.iteco.msp.enums.AssignOperationType;
import ru.iteco.msp.kafka.KafkaService;
import ru.iteco.msp.kafka.dto.AssignEvent;
import ru.iteco.msp.models.CategoryDiscount;
import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;
import ru.iteco.msp.models.ClientDiscountHistory;
import ru.iteco.msp.service.DiscountsService;
import ru.iteco.msp.service.FileSupportService;

import org.apache.commons.collections4.CollectionUtils;
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
import java.util.Date;
import java.util.List;

@Component
public class AssignTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(AssignTaskExecutor.class);

    private final CronTrigger assignCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolAssignTaskScheduler;
    private final KafkaService kafkaService;
    private final DiscountsService discountsService;
    private final FileSupportService fileSupportService;

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
            DiscountsService discountsService,
            FileSupportService fileSupportService){
        this.assignCronTrigger = assignCronTrigger;
        this.threadPoolAssignTaskScheduler = threadPoolAssignTaskScheduler;
        this.kafkaService = kafkaService;
        this.discountsService = discountsService;
        this.fileSupportService = fileSupportService;
    }

    @PostConstruct
    public void buildRunnableTask(){
        threadPoolAssignTaskScheduler.schedule(new RunnableTask(), assignCronTrigger);
    }

    class RunnableTask implements Runnable {

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
                        type = AssignOperationType.getAssignTypeByOperationType(h.getOperationType());
                        if (AssignOperationType.CHANGE.equals(type)
                                || h.getCategoryDiscount().getCategoryDiscountDTSZN() != null) {
                            info = discountsService
                                    .getLastInfoByClientAndCode(h.getClient(), h.getCategoryDiscount()
                                            .getCategoryDiscountDTSZN().getCode());
                        }

                        AssignEvent event = AssignEvent.build(h.getCategoryDiscount(), h.getClient(), type, info);
                        kafkaService.sendAssign(mapper.writeValueAsString(event));
                    }
                }
            } catch (Exception e) {
                log.error("Critical error in process sending assign MSP-discounts info, task interrupt", e);
            }
        }

        @Transactional(propagation = Propagation.SUPPORTS)
        public void beginPrimalLoad() {
            try {
                Long lastProcessIdOfClient = fileSupportService.getLastProcessIdOfClient();
                Pageable pageable = PageRequest.of(0, sampleSize, Sort.by("idOfClient"));
                List<Client> clientsList;
                if(lastProcessIdOfClient == null) {
                    clientsList = discountsService.getClientsWithMeshGuid(pageable);
                } else {
                    clientsList = discountsService
                            .getClientsWithMeshGuidAndGreaterThenIdOfClient(lastProcessIdOfClient, pageable);
                }
                while (CollectionUtils.isNotEmpty(clientsList)) {
                    for (Client c : clientsList) {
                        List<CategoryDiscount> clientCategoryDiscount = c.getDiscounts();
                        for (CategoryDiscount discount : clientCategoryDiscount) {
                            if (discount.getCategoryType() != 0) {
                                continue;
                            }

                            ClientDTSZNDiscountInfo info = null;
                            if (discount.getCategoryDiscountDTSZN() != null) {
                                info = discountsService
                                        .getLastInfoByClientAndCode(c, discount.getCategoryDiscountDTSZN().getCode());
                            }

                            AssignEvent event = AssignEvent.build(discount, c, AssignOperationType.ADD, info);
                            kafkaService.sendAssign(mapper.writeValueAsString(event));
                        }
                        fileSupportService.writeLastProcessIdOfClient(c.getIdOfClient());
                    }

                    pageable = pageable.next();
                    clientsList = discountsService.getClientsWithMeshGuid(pageable);
                }
                log.info("--Primal load of assign MSP completed--");
            } catch (Exception e) {
                log.error("Critical error in process sending primary discount change history records, task interrupt", e);
            }
        }
    }
}
