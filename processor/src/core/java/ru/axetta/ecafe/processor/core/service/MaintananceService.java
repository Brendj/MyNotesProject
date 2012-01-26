/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@Component
@Scope("singleton")
public class MaintananceService {
    Logger logger = LoggerFactory.getLogger(MaintananceService.class);

    Date lastCleanDate;
    Integer maintananceHour;

    @Resource
    RuntimeContext runtimeContext;

    public void run() {
        if (maintananceHour==null) {
            maintananceHour = runtimeContext.getPropertiesValue(RuntimeContext.PARAM_NAME_DB_MAINTANANCE_HOUR, 22);
        }
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)==maintananceHour && (lastCleanDate==null || System.currentTimeMillis()-lastCleanDate.getTime()>12*60*60*1000)) {
            logger.info("Starting DB maintanance procedures...");
            lastCleanDate = new Date();
        }
    }
}
