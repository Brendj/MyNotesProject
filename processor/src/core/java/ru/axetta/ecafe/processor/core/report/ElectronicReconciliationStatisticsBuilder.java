/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.10.16
 * Time: 12:26
 */

public class ElectronicReconciliationStatisticsBuilder extends BasicReportForAllOrgJob.Builder {

    Long idOfContragent = -1L;

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

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        parameterMap.put("endTime", CalendarUtils.dateToString(endTime));

        if (contragent != null) {
            parameterMap.put("contragentName", contragent.getContragentName());
            idOfContragent = contragent.getIdOfContragent();
        } else {
            throw new Exception("Поставщик не указан.");
        }

        String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));
        List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
        // Список организаций
        List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
        for (String idOfOrg : stringOrgList) {
            idOfOrgList.add(Long.parseLong(idOfOrg));
        }

       /* Object region = reportProperties.getProperty("region");
        if (region == null) {
            throw new IllegalArgumentException("Не указан Округ");
        }*/

        JRDataSource dataSource = buildDataSource(session, startTime, endTime, idOfContragent, idOfOrgList);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

        Date generateEndTime = new Date();
        final long generationDuration = generateEndTime.getTime() - generateBeginTime.getTime();
        return new ElectronicReconciliationStatisticsReport(generateBeginTime, generationDuration, jasperPrint,
                startTime, endTime, idOfContragent);
    }

    private JRDataSource buildDataSource(Session session, Date startTime, Date endTime, Long idOfContragent,
            List<Long> idOfOrgList) {
        //Результирующий лист по которому строиться отчет
        List<ElectronicReconciliationStatisticsItem> electronicReconciliationStatisticsItemList = new ArrayList<ElectronicReconciliationStatisticsItem>();

        return new JRBeanCollectionDataSource(electronicReconciliationStatisticsItemList);
    }
}
