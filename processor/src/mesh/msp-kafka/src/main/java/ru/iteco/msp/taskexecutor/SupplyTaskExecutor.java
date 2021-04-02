/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.taskexecutor;

import ru.iteco.msp.kafka.KafkaService;
import ru.iteco.msp.models.dto.SupplyMSPOrders;
import ru.iteco.msp.service.SupplyMSPService;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Component
public class SupplyTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(SupplyTaskExecutor.class);

    private final SupplyMSPService supplyMSPService;
    private final CronTrigger supplyCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolSupplyTaskScheduler;
    private final KafkaService kafkaService;

    @Value(value = "${kafka.task.execution.supply.samplesize}")
    private Integer sampleSize;

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
                ReportingDate reportingDate = new ReportingDate();

                do {
                    end = reportingDate.getEndPeriod();
                    begin = reportingDate.getBeginPeriod();

                    Pageable pageable = PageRequest.of(0, sampleSize, Sort.by("createdDate"));
                    List<SupplyMSPOrders> orderList = supplyMSPService.getDiscountOrders(begin, end, pageable);

                    while (CollectionUtils.isNotEmpty(orderList)) {
                        for (SupplyMSPOrders o : orderList) {
                            kafkaService.sendSupplyMSP(o);
                        }
                        orderList.clear();
                        orderList = null;

                        pageable = pageable.next();
                        orderList = supplyMSPService.getDiscountOrders(begin, end, pageable);
                    }
                    reportingDate = reportingDate.getNext();
                } while (reportingDate != null);

            } catch (Exception e) {
                log.error("Critical error in process sending supply MSP info, task interrupt", e);
            }
        }
    }
}
