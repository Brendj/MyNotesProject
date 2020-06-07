/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.preorder;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.persistence.PreorderMobileGroupOnCreateType;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by nuc on 27.03.2020.
 */
public class PreorderStatsReportBuilder extends BasicReportJob.Builder {
    private final String templateFilename;

    public PreorderStatsReportBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();

        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "PreorderStatsReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        parameterMap.put("startDate", startTime);
        parameterMap.put("endDate", endTime);

        JRDataSource dataSource = buildDataSource(session, startTime, endTime);

        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
        Date generateEndTime = new Date();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JRHtmlExporter exporter = new JRHtmlExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, Boolean.TRUE);
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR_NAME, "./images/");
        exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "/images/");
        exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.FRAMES_AS_NESTED_TABLES, Boolean.FALSE);
        exporter.setParameter(JRHtmlExporterParameter.IS_WRAP_BREAK_WORD, Boolean.TRUE);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
        exporter.exportReport();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();

        return new PreorderStatsReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime).setHtmlReport(os.toString("UTF-8"));
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate) throws Exception {
        Map<Date, PreorderStatsReportItem> map = new TreeMap<>();
        String orgCondition = "";
        List<Long> orgs = new ArrayList<>();
        if (reportProperties.getProperty(PreorderStatsReport.PREORDER_ORGS_PARAM) != null) {
            orgCondition = " and org.preordersenabled = 1";
        } else {
            orgCondition = " and org.idOfOrg in :orgs";
            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            for (String idOfOrg : stringOrgList) {
                orgs.add(Long.parseLong(idOfOrg));
            }
        }

        /*Query query = session.createQuery("select pc from PreorderComplex pc, Org org "
                + "where pc.idOfOrgOnCreate = org.idOfOrg "
                + "and pc.preorderDate between :startDate and :endDate and pc.deletedState = false "
                + orgCondition
                + " order by pc.idOfOrgOnCreate");*/

        Query query = session.createSQLQuery("select pc.preorderDate, "
                + "(select string_agg(mobileGroupOnCreate, ',') from "
                + "(select distinct cast(mobileGroupOnCreate as text) from cf_preorder_menudetail pmd "
                + "where pmd.idofpreordercomplex = pc.idofpreordercomplex and pmd.deletedState = 0) q) as qq "
                + "from cf_preorder_complex pc "
                + "join cf_orgs org on pc.idOfOrgOnCreate = org.IdOfOrg "
                + "where pc.preorderdate between :startDate and :endDate "
                + "and pc.deletedState = 0 "
                + orgCondition);


        query.setParameter("startDate", CalendarUtils.startOfDay(startDate).getTime());
        query.setParameter("endDate", CalendarUtils.endOfDay(endDate).getTime());
        if (getReportProperties().getProperty(PreorderStatsReport.PREORDER_ORGS_PARAM) == null) {
            query.setParameterList("orgs", orgs);
        }
        List<PreorderComplex> list = query.list();
        for (Object o : list) {
            Object[] row = (Object[]) o;
            Date date = new Date(((BigInteger)row[0]).longValue());
            if (row[1] == null) {
                PreorderStatsReportItem item = new PreorderStatsReportItem(date, null);
                addToMap(map, date, item);
            } else {
                String flags = (String) row[1];
                String[] arr = flags.split(",");
                for (String str : arr) {
                    Integer mobileGroup = new Integer(str);
                    PreorderMobileGroupOnCreateType mobileGroupOnCreate = mobileGroup == null ? null : PreorderMobileGroupOnCreateType.fromInteger(mobileGroup);
                    PreorderStatsReportItem item = new PreorderStatsReportItem(date, mobileGroupOnCreate);
                    addToMap(map, date, item);
                }
            }
        }

        List<PreorderStatsReportItem> items = new ArrayList<>();
        for (Date date : map.keySet()) {
            items.add(map.get(date));
        }

        return new JRBeanCollectionDataSource(items);
    }

    private void addToMap(Map<Date, PreorderStatsReportItem> map, Date date, PreorderStatsReportItem item) {
        PreorderStatsReportItem mapItem = map.get(date);
        if (mapItem == null) {
            map.put(date, item);
        } else {
            mapItem.setEmployee(mapItem.getEmployee() + item.getEmployee());
            mapItem.setParents(mapItem.getParents() + item.getParents());
            mapItem.setStudents(mapItem.getStudents() + item.getStudents());
            mapItem.setOthers(mapItem.getOthers() + item.getOthers());
        }
    }
}
