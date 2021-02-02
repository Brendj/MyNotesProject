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
import java.util.Calendar;
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
                end = getEndOfMonth();
                begin = getBeginOfMonth();

                Integer allRows = supplyMSPService.countOrders(begin, end);

                if(allRows.equals(0)){
                    log.warn("No Orders, skipped");
                    return;
                }
                Pageable pageable = PageRequest.of(0, sampleSize, Sort.by("createdDate"));
                List<SupplyMSPOrders> orderList = supplyMSPService.getDiscountOrders(begin, end, pageable);

                while (CollectionUtils.isNotEmpty(orderList)){
                    for(SupplyMSPOrders o : orderList){
                        kafkaService.sendSupplyMSP(o);
                    }
                    orderList.clear();
                    orderList = null;

                    pageable = pageable.next();
                    orderList = supplyMSPService.getDiscountOrders(begin, end, pageable);
                }
            } catch (Exception e) {
                log.error("Critical error in process sending supply MSP info, task interrupt", e);
            }
        }

        Date getBeginOfMonth(){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, -1);
            return calendar.getTime();
        }

        Date getEndOfMonth(){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MILLISECOND, -1);
            return calendar.getTime();
        }
    }
}
