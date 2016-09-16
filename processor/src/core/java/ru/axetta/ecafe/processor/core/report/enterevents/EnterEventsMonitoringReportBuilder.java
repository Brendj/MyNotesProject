/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.enterevents;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AutoReportGenerator;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

public class EnterEventsMonitoringReportBuilder extends BasicReportForAllOrgJob.Builder {

    public EnterEventsMonitoringReportBuilder() {
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        AutoReportGenerator autoReportGenerator = RuntimeContext.getInstance().getAutoReportGenerator();
        String templateFilename =
                autoReportGenerator.getReportsTemplateFilePath() + "EnterEventsMonitoringReport.jasper";

        if (!(new File(templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();

        JRDataSource dataSource = buildDataSource(session, startTime, endTime, parameterMap);

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

        return new EnterEventsMonitoringReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime)
                .setHtmlReport(os.toString("UTF-8"));
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate, Map<String, Object> parameterMap) throws Exception {
        boolean showElectionAreaOnly = Boolean.valueOf(getReportProperties().getProperty("showElectionAreaOnly"));
        boolean showStatus1 = Boolean.valueOf(getReportProperties().getProperty("showStatus1"));
        boolean showStatus2 = Boolean.valueOf(getReportProperties().getProperty("showStatus2"));
        boolean showStatus3 = Boolean.valueOf(getReportProperties().getProperty("showStatus3"));
        boolean showStatus4 = Boolean.valueOf(getReportProperties().getProperty("showStatus4"));
        boolean showStatus5 = Boolean.valueOf(getReportProperties().getProperty("showStatus5"));
        String UIKfilter = getReportProperties().getProperty("UIKfilter");
        String idOfOrgFilter = getReportProperties().getProperty("idOfOrgFilter");
        String addressFilter = getReportProperties().getProperty("addressFilter");
        String orgNameFilter = getReportProperties().getProperty("orgNameFilter");

        List<String> UIKfilters = null;
        List<String> idOfOrgFilters = null;
        List<String> addressFilters = null;
        List<String> orgNameFilters = null;

        if(StringUtils.isNotEmpty(UIKfilter)) {
            UIKfilters = Arrays.asList(UIKfilter.split(","));
        }
        if(StringUtils.isNotEmpty(idOfOrgFilter)) {
            idOfOrgFilters = Arrays.asList(idOfOrgFilter.split(","));
        }
        if(StringUtils.isNotEmpty(addressFilter)) {
            addressFilters = Arrays.asList(addressFilter.split(","));
        }
        if(StringUtils.isNotEmpty(orgNameFilter)) {
            orgNameFilters = Arrays.asList(orgNameFilter.split(","));
        }

        int eventsTotal1 = 0;
        int eventsTotal2 = 0;
        int eventsTotal3 = 0;
        int lastSyncTotal1 = 0;
        int lastSyncTotal2 = 0;
        int lastSyncTotal3 = 0;
        int lastEventTotal1 = 0;
        int lastEventTotal2 = 0;
        int lastEventTotal3 = 0;

        List<EnterEventsMonitoring.EnterEventItem> list = new ArrayList<EnterEventsMonitoring.EnterEventItem>();
        Map<Long, List<EnterEventsMonitoring.EnterEventItem>> enterEventMap = EnterEventsMonitoring.getEnterEventMap();
        for(Long idOfOrg : enterEventMap.keySet()) {
            int count = 0;
            for(EnterEventsMonitoring.EnterEventItem item : enterEventMap.get(idOfOrg)) {
                count++;
                if(showElectionAreaOnly && StringUtils.isEmpty(item.getElectionArea())){
                    continue;
                }
                if(!showStatus1 && item.getColorType() == EnterEventsMonitoring.EnterEventItem.COLOR_GREEN){
                    continue;
                }
                if(!showStatus2 && item.getColorType() == EnterEventsMonitoring.EnterEventItem.COLOR_YELLOW){
                    continue;
                }
                if(!showStatus3 && item.getColorType() == EnterEventsMonitoring.EnterEventItem.COLOR_RED){
                    continue;
                }
                if(!showStatus4 && item.getColorType() == EnterEventsMonitoring.EnterEventItem.COLOR_BLUE){
                    continue;
                }
                if(!showStatus5 && item.getColorType() == EnterEventsMonitoring.EnterEventItem.COLOR_GRAY){
                    continue;
                }
                if(UIKfilters != null && UIKfilters.size() > 0) {
                    boolean contains = false;
                    List<String> uiks = new ArrayList<String>();
                    if(StringUtils.isNotEmpty(item.getElectionArea())){
                        uiks = Arrays.asList(item.getElectionArea().split(", "));
                    }
                    for(String uik : uiks) {
                        if(UIKfilters.contains(uik)) {
                            contains = true;
                            break;
                        }
                    }
                    if(!contains) {
                        continue;
                    }
                }
                if(idOfOrgFilters != null && idOfOrgFilters.size() > 0) {
                    if(!idOfOrgFilters.contains(item.getIdOfOrg().toString())){
                        continue;
                    }
                }
                if(addressFilters != null && addressFilters.size() > 0) {
                    boolean contains = false;
                    for (String s : addressFilters) {
                        if(item.getAddress().toLowerCase().contains(s.toLowerCase())){
                            contains = true;
                            break;
                        }
                    }
                    if(!contains) {
                        continue;
                    }
                }
                if(orgNameFilters != null && orgNameFilters.size() > 0) {
                    boolean contains = false;
                    for (String s : orgNameFilters) {
                        if(item.getOrgShortName().toLowerCase().contains(s.toLowerCase())){
                            contains = true;
                            break;
                        }
                    }
                    if(!contains) {
                        continue;
                    }
                }

                if(item.getColorType() == EnterEventsMonitoring.EnterEventItem.COLOR_GREEN) {
                    eventsTotal1 = eventsTotal1 + item.getEventCount();
                }
                if(item.getColorType() == EnterEventsMonitoring.EnterEventItem.COLOR_YELLOW) {
                    eventsTotal2 = eventsTotal2 + item.getEventCount();
                }
                if(item.getColorType() == EnterEventsMonitoring.EnterEventItem.COLOR_RED) {
                    eventsTotal3 = eventsTotal3 + item.getEventCount();
                }
                if(count == 1) {
                    if (item.getLastSyncColor() == EnterEventsMonitoring.EnterEventItem.COLOR_GREEN) {
                        lastSyncTotal1 = lastSyncTotal1 + 1;
                    }
                    if (item.getLastSyncColor() == EnterEventsMonitoring.EnterEventItem.COLOR_YELLOW) {
                        lastSyncTotal2 = lastSyncTotal2 + 1;
                    }
                    if (item.getLastSyncColor() == EnterEventsMonitoring.EnterEventItem.COLOR_RED) {
                        lastSyncTotal3 = lastSyncTotal3 + 1;
                    }
                    if (item.getLastEventColor() == EnterEventsMonitoring.EnterEventItem.COLOR_GREEN) {
                        lastEventTotal1 = lastEventTotal1 + 1;
                    }
                    if (item.getLastEventColor() == EnterEventsMonitoring.EnterEventItem.COLOR_YELLOW) {
                        lastEventTotal2 = lastEventTotal2 + 1;
                    }
                    if (item.getLastEventColor() == EnterEventsMonitoring.EnterEventItem.COLOR_RED) {
                        lastEventTotal3 = lastEventTotal3 + 1;
                    }
                }
                list.add(item);
            }
        }

        parameterMap.put("eventsTotal1", eventsTotal1);
        parameterMap.put("eventsTotal2", eventsTotal2);
        parameterMap.put("eventsTotal3", eventsTotal3);
        parameterMap.put("lastSyncTotal1", lastSyncTotal1);
        parameterMap.put("lastSyncTotal2", lastSyncTotal2);
        parameterMap.put("lastSyncTotal3", lastSyncTotal3);
        parameterMap.put("lastEventTotal1", lastEventTotal1);
        parameterMap.put("lastEventTotal2", lastEventTotal2);
        parameterMap.put("lastEventTotal3", lastEventTotal3);
        return new JRBeanCollectionDataSource(list);
    }
}
