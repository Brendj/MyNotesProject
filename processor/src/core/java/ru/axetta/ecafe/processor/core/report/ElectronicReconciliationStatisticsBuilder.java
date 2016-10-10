/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DeviationPaymentNewItem;

import org.hibernate.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.10.16
 * Time: 12:26
 */

public class ElectronicReconciliationStatisticsBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public ElectronicReconciliationStatisticsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public ElectronicReconciliationStatisticsBuilder() {
        templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                + ElectronicReconciliationStatisticsReport.class.getSimpleName() + ".jasper";
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }
        return null;
    }

    private JRDataSource buildDataSource(Session session, Date startTime, Date endTime) {
        //Результирующий лист по которому строиться отчет
        List<ElectronicReconciliationStatisticsItem> electronicReconciliationStatisticsItemList = new ArrayList<ElectronicReconciliationStatisticsItem>();

        return new JRBeanCollectionDataSource(electronicReconciliationStatisticsItemList);
    }
}
