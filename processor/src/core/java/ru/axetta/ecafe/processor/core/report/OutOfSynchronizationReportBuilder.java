/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.01.16
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class OutOfSynchronizationReportBuilder extends BasicReportForAllOrgJob.Builder {

    private final String templateFilename;

    public OutOfSynchronizationReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename = autoReportGenerator.getReportsTemplateFilePath()
                + "OutOfSynchronizationReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        parameterMap.put("startDate", CalendarUtils.dateToString(startTime));

        JRDataSource dataSource = buildDataSource(session, startTime, endTime);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();

        return new OutOfSynchronizationReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session, Date startTime, Date endTime) {
        //Результирующий лист по которому строиться отчет
        List<OutOfSynchronizationItem> outOfSynchronizationReportList = new ArrayList<OutOfSynchronizationItem>();



        return  null;
    }
}
