/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentCompletionReportItem;
import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentDAOService;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 22.01.13
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
public class ContragentCompletionReport extends BasicReportForContragentJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по обороту";
    public static final String[] TEMPLATE_FILE_NAMES = {"ContragentCompletionReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{20, 21};


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {}

    public static class Builder extends BasicReportForContragentJob.Builder{

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            parameterMap.put("startDate", dateFormat.format(startTime));
            parameterMap.put("endDate", dateFormat.format(endTime));
            parameterMap.put("contragentName", contragent.getContragentName());
            parameterMap.put("showNullValues", getReportProperties().getProperty("showNullValues"));

            String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty("idOfOrgList"));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String organizationTypeProperty = getReportProperties().getProperty("organizationTypeModify");
            OrganizationType orgType = null;

            OrganizationType[] organizationTypes = OrganizationType.values();
            for (OrganizationType organizationType : organizationTypes) {
                if (organizationType.toString().equals(organizationTypeProperty)) {
                    orgType = organizationType;
                    break;
                }
            }

            JRDataSource dataSource = createDataSource(session, contragent, startTime, endTime,
                    (Calendar) calendar.clone(), parameterMap, idOfOrgList, orgType);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            return new ContragentCompletionReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, contragent.getIdOfContragent());
        }

        private JRDataSource createDataSource(Session session, Contragent contragent, Date startTime, Date endTime, Calendar clone, Map<String, Object> parameterMap, List<Long> idOfOrgList, OrganizationType organizationType) {
            ContragentDAOService contragentDAOService = new ContragentDAOService();
            contragentDAOService.setSession(session);
            List<ContragentCompletionReportItem> list = new ArrayList<ContragentCompletionReportItem>();
            //list
            //При нулевых значениях, строиться отчет с нулевыми данными
            List<Org> orgItems = new ArrayList<Org>();
            List<Contragent> contragentList = contragentDAOService.getPayAgentContragent();
            /*if (contragent != null) {
                orgItems = contragentDAOService.findDistributionOrganizationByDefaultSupplier(contragent);
            } else {
                orgItems = contragentDAOService.findAllDistributionOrganization();
            }*/

            if (organizationType != null) {
                List<Org> orgList = new ArrayList<Org>();
                for (Org org : orgItems) {
                     if (org.getType().equals(organizationType)) {
                         orgList.add(org);
                     }
                }
                orgItems = orgList;
            }

            if (!orgItems.isEmpty()) {
                if (!CollectionUtils.isEmpty(idOfOrgList)) {

                    // пересорт по типу организации
                    List<Long> orgSortedList = new ArrayList<Long>();
                    for (Org org : orgItems) {
                        for (Long idOfOrg: idOfOrgList) {
                            if (org.getIdOfOrg().equals(idOfOrg)) {
                                orgSortedList.add(idOfOrg);
                                break;
                            }
                        }
                    }

                    idOfOrgList = orgSortedList;

                    for (Long idOrg : idOfOrgList) {
                        Org org1 = contragentDAOService.getOrdByOrgId(idOrg);
                        ContragentCompletionReportItem contragentCompletionReportItem = null;
                        for (int i = 0; i < contragentList.size(); i++) {
                            contragentCompletionReportItem = new ContragentCompletionReportItem(org1.getIdOfOrg(),
                                    org1.getShortName(), org1.getCity(), contragentList.get(i).getContragentName(), 0L, 0);
                            list.add(contragentCompletionReportItem);
                        }
                    }
                } else {
                    for (Org org : orgItems) {
                        ContragentCompletionReportItem contragentCompletionReportItem = null;
                        for (int i = 0; i < contragentList.size(); i++) {
                            contragentCompletionReportItem = new ContragentCompletionReportItem(org.getIdOfOrg(),
                                    org.getShortName(), org.getCity(), contragentList.get(i).getContragentName(), 0L, 0);
                            list.add(contragentCompletionReportItem);
                        }
                        idOfOrgList.add(org.getIdOfOrg());
                    }
                }
            }

            List<ContragentCompletionReportItem> contragentCompletionReportItems = contragentDAOService.generateReportItems(idOfOrgList,
                    startTime, endTime, contragent);
            list.addAll(contragentCompletionReportItems);
            if(parameterMap.get("showNullValues") == "false") {
                list = deleteAllNullPayments(list);
            }
            return new JRBeanCollectionDataSource(list);
        }
    }

    private static List<ContragentCompletionReportItem> deleteAllNullPayments(List<ContragentCompletionReportItem> list){
        List<ContragentCompletionReportItem> newList = new ArrayList<ContragentCompletionReportItem>();
        for(ContragentCompletionReportItem item : list){
            if ((item.getPaySum() > 0L) || (item.getPaymentCount() > 0)){
                newList.add(item);
            }
        }
        return newList;
    }

    @Override
    protected Integer getContragentSelectClass() {
        return Contragent.TSP;
    }

    public ContragentCompletionReport() {}

    public ContragentCompletionReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfContragent) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfContragent);
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public BasicReportForContragentJob createInstance() {
        return new ContragentCompletionReport();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_TODAY;
    }

    private static final Logger logger = LoggerFactory.getLogger(ContragentCompletionReport.class);
}
