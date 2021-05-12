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

import java.util.Date;
import java.util.List;

@Service
public class SupplyService {
    private final static Logger log = LoggerFactory.getLogger(SupplyService.class);

    private final KafkaService kafkaService;
    private final SupplyMSPService supplyMSPService;

    private static final Long DELTA_MONTH = 2595600000L;

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
        try {
            ReportingDate reportingDate = new ReportingDate();
            reportingDate = reportingDate.getNext();
            do {
                sendSupplyEvents(reportingDate.getBeginPeriod(), reportingDate.getEndPeriod(), sampleSize);
                reportingDate = reportingDate.getNext();
            } while (reportingDate != null);
            log.info("--Data sending completed--");
        } catch (Exception e) {
            log.error("Critical error in process sending supply MSP info, task interrupt", e);
        }
    }

    @Async
    public void runFromController(Date begin, Date end, Integer sampleSize){
        try{
            if(begin.after(end)){
                throw new IllegalArgumentException("Begin date after end date");
            }
            if((end.getTime() - begin.getTime()) > DELTA_MONTH){
                throw new IllegalArgumentException("Too long period");
            }

            ReportingDate reportingDate = new ReportingDate(begin, end);
            do {
                sendSupplyEvents(reportingDate.getBeginPeriod(), reportingDate.getEndPeriod(), sampleSize);
                reportingDate = reportingDate.getNext();
            } while (reportingDate != null);
            log.info("--Data sending completed--");
        } catch (Exception e) {
            log.error("Critical error in process sending supply MSP info, task interrupt", e);
        }
    }
}
