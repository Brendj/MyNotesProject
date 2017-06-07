/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

/**
 * Created by i.semenov on 07.06.2017.
 */
public abstract class SummaryDownloadBaseService {
    @PersistenceContext(unitName = "reportsPU")
    protected EntityManager entityManager;

    protected abstract String getNode();
    protected abstract void run(Date startDate, Date endDate);

    public boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty(getNode());
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void run() {
        if (!isOn()) {
            return;
        }
        Date prevDay = CalendarUtils.addDays(new Date(System.currentTimeMillis()), -1);
        Date endDate = CalendarUtils.endOfDay(prevDay);
        Date startDate = CalendarUtils.truncateToDayOfMonth(prevDay);
        run(startDate, endDate);
    }
}
