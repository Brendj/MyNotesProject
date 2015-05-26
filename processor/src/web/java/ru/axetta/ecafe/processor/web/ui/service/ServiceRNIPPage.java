/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * User: Shamil
 * Date: 21.05.15
 * Time: 18:41
 */
@Component
@Scope("session")
public class ServiceRNIPPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRNIPPage.class);

    private Date startDate = new Date();
    private Date endDate = new Date();



    public void run(){
        endDate = CalendarUtils.endOfDay(endDate);
        logger.error("Manual launch RNIPService startDate:" + startDate.toString() + ", endDate:" + endDate.toString());

        RNIPLoadPaymentsService rnipLoadPaymentsService = RNIPLoadPaymentsService.getInstance();
        rnipLoadPaymentsService.run(startDate,endDate);
    }

    public String getPageFilename() {
        return "service/rnip_service";
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
