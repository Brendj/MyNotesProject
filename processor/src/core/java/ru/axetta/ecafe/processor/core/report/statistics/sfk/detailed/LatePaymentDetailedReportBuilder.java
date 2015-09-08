/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.detailed;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.hibernate.Session;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.09.15
 * Time: 10:08
 */

public class LatePaymentDetailedReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public LatePaymentDetailedReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        return null;
    }

    private JRDataSource buildDataSource(Session session, Long idOfOrg, Date startTime, Date endTime,
            String latePaymentDaysCountType, String latePaymentByOneDayCountType) {
        return new JRBeanCollectionDataSource(null);
    }
}
