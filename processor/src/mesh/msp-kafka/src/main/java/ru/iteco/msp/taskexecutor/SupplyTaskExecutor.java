/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import ru.iteco.msp.kafka.KafkaService;
import ru.iteco.msp.models.dto.SupplyMSPOrders;
import ru.iteco.msp.service.SupplyMSPService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Component
public class SupplyTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(SupplyTaskExecutor.class);
    private static final int SAMPLE_SIZE = 50000;

    private final SupplyMSPService supplyMSPService;
    private final CronTrigger supplyCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolSupplyTaskScheduler;
    private final KafkaService kafkaService;

    public SupplyTaskExecutor(
            CronTrigger supplyCronTrigger,
            ThreadPoolTaskScheduler threadPoolSupplyTaskScheduler,
            SupplyMSPService supplyMSPService,
            KafkaService kafkaService){
        this.supplyCronTrigger = supplyCronTrigger;
        this.threadPoolSupplyTaskScheduler = threadPoolSupplyTaskScheduler;
        this.supplyMSPService = supplyMSPService;
        this.kafkaService = kafkaService;
    }

    @PostConstruct
    public void buildRunnableTask(){
        threadPoolSupplyTaskScheduler.schedule(new RunnableTask(), supplyCronTrigger);
    }

    class RunnableTask implements Runnable {

        @Override
        public void run() {
            Date begin;
            Date end;
            try {
                end = new Date();
                Date next = supplyCronTrigger.nextExecutionTime(new SimpleTriggerContext());
                long delta = next.getTime() - end.getTime();
                begin = new Date(end.getTime() - delta);
                next = null;

                Integer allRows = supplyMSPService.countOrders(begin, end);

                if(allRows.equals(0)){
                    log.warn("No Orders, skipped");
                    return;
                }

                for(int i = 0; i < allRows; i += SAMPLE_SIZE){
                    Pageable pageable = PageRequest.of(i, SAMPLE_SIZE, Sort.by("createdDate"));

                    List<SupplyMSPOrders> orderList = supplyMSPService.getDiscountOrders(begin, end, pageable);

                    for(SupplyMSPOrders o : orderList){
                        kafkaService.sendSupplyMSP(o);
                    }
                    orderList.clear();
                    orderList = null;
                }
            } catch (Exception e) {
                log.error("Critical error in process sending supply MSP info, task interrupt", e);
            }
        }
    }
}
