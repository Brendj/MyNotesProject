/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.kafka;

import ru.iteco.msp.models.dto.SupplyMSPOrders;
import ru.iteco.msp.service.SupplyMSPService;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SupplyService {
    private final static Logger log = LoggerFactory.getLogger(SupplyService.class);

    private final KafkaService kafkaService;
    private final SupplyMSPService supplyMSPService;

    private static final Long DELTA_MONTH = 2595600000L;
    private static final int THREAD_POOL_SIZE = 3;

    private volatile ReportingDate reportingDate = null;

    public SupplyService(KafkaService kafkaService,
            SupplyMSPService supplyMSPService) {
        this.kafkaService = kafkaService;
        this.supplyMSPService = supplyMSPService;
    }

    private void sendSupplyEvents(Date begin, Date end, Integer sampleSize) {
        Pageable pageable = PageRequest.of(0, sampleSize);
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
    }

    public void runFromTaskExecutor(Integer sampleSize){
        runTask(null, null, sampleSize);
    }

    @Async
    public void runFromController(Date begin, Date end, Integer sampleSize) {
        if (begin.after(end)) {
            throw new IllegalArgumentException("Begin date after end date");
        }
        if ((end.getTime() - begin.getTime()) > DELTA_MONTH) {
            throw new IllegalArgumentException("Too long period");
        }

        runTask(begin, end, sampleSize);
    }

    private void runTask(Date begin, Date end, Integer sampleSize){
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            if(begin == null || end == null) {
                reportingDate = new ReportingDate();
            } else {
                reportingDate = new ReportingDate(begin, end);
            }

            List<Callable<Boolean>> tasks = Arrays.asList(
                    new RunnableTask(sampleSize),
                    new RunnableTask(sampleSize),
                    new RunnableTask(sampleSize)
            );

            executorService.invokeAll(tasks);
            executorService.shutdown();

            log.info("--Data sending completed--");
        } catch (Exception e) {
            log.error("Critical error in process sending supply MSP info, task interrupt", e);
        }
        finally {
            reportingDate = null;
        }
    }

    synchronized void setNext(){
        reportingDate = reportingDate.getNext();
    }

    class RunnableTask implements Callable<Boolean> {
        private final Integer sampleSize;

        public RunnableTask(Integer sampleSize) {
            this.sampleSize = sampleSize;
        }

        @Override
        public Boolean call() {
            try {
                Date begin = null;
                Date end = null;
                do {
                    synchronized (reportingDate) {
                        begin = reportingDate.getBeginPeriod();
                        end = reportingDate.getEndPeriod();
                    }
                    sendSupplyEvents(begin, end, sampleSize);
                    setNext();
                } while (reportingDate != null);
                return true;
            } catch (Exception e){
                log.error("Exception in task ", e);
                return false;
            }
        }
    }
}
