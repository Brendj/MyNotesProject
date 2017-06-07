/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.summary;

import ru.axetta.ecafe.processor.core.service.SummaryCardsMSRService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by i.semenov on 07.06.2017.
 */
public class SummaryCardsMSRServlet extends SummaryBaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(SummaryCardsMSRServlet.class);
    private static final String SUMMARY_REPORT_DAY = "summaryDate";

    protected String getUserNameSettingName() {
        return SummaryCardsMSRService.USER;
    }

    protected String getUserPasswordSettingName(){
        return SummaryCardsMSRService.PASSWORD;
    }

    protected String getFolderSettingName() {
        return SummaryCardsMSRService.FOLDER_PROPERTY;
    }

    protected Logger getLogger() {
        return logger;
    }

    protected String getFileBaseName() {
        return SUMMARY_REPORT_DAY;
    }

}
