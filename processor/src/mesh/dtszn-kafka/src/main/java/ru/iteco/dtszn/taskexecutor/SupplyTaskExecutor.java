/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.taskexecutor;

import ru.iteco.dtszn.kafka.KafkaService;
import ru.iteco.dtszn.models.dto.SupplyMSPOrders;
import ru.iteco.dtszn.service.SupplyMSPService;

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
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class SupplyTaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(SupplyTaskExecutor.class);
    private static final int SAMPLE_SIZE = 50000;

    private final SupplyMSPService supplyMSPService;
    private final CronTrigger supplyCronTrigger;
    private final ThreadPoolTaskScheduler threadPoolSupplyTaskScheduler;
    private final KafkaService kafkaService;

    @Value("${spring.profiles.active}")
    private String activeProfile;

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
                if(activeProfile.contains("dev")){
                    begin = getStartOfPeriod();
                    end = getEndOfPeriod();
                } else {
                    begin = getStartOfPreviousMonth();
                    end = getEndOfPreviousMonth();
                }
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

        private Date getStartOfPreviousMonth() {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        }

        private Date getEndOfPreviousMonth() {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MILLISECOND, -1);
            return calendar.getTime();
        }

        private Date getStartOfPeriod() {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.MINUTE, -5);
            return calendar.getTime();
        }

        private Date getEndOfPeriod() {
            return new Date();
        }
    }
}
