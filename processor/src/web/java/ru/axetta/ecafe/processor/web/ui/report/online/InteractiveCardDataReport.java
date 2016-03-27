/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: T800
 * Date: 25.03.16
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public class InteractiveCardDataReport extends BasicReportForAllOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(InteractiveCardDataReport.class);
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");

    private Date reportDate;

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return null;
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}

