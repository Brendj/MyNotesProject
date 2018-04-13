/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

public class PreordersReport extends BasicReportForListOrgsJob {
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
    public static final String REPORT_NAME = "Отчет по предварительным заказам";
    public static final String[] TEMPLATE_FILE_NAMES = {"PreordersReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};
    final public static String P_ID_OF_CLIENTS="idOfClients";

    final private static Logger logger = LoggerFactory.getLogger(PreordersReport.class);

    public static class Builder extends BasicReportForListOrgsJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(endTime));
            parameterMap.put("reportName", REPORT_NAME);

            String idOfOrgs = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>();
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String idOfClients = StringUtils.trimToEmpty(reportProperties.getProperty(P_ID_OF_CLIENTS));
            List<String> stringClientsList = Arrays.asList(StringUtils.split(idOfClients, ','));
            List<Long> idOfClientList = new ArrayList<Long>();
            for (String idOfClient : stringClientsList) {
                idOfClientList.add(Long.parseLong(idOfClient));
            }

            JRDataSource dataSource = createDataSource(session, startTime, endTime, idOfOrgList, idOfClientList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new PreordersReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime,
                List<Long> idOfOrgList, List<Long> idOfClientList) throws Exception {
            List<PreorderReportItem> result = new ArrayList<PreorderReportItem>();
            String conditions = (idOfOrgList.size() == 0) ? "" : " and o.idoforg in (:orgs) ";
            if (idOfClientList.size() > 0) {
                conditions += " and c.idofclient in (:clients) ";
            }
            Query query = session.createSQLQuery("SELECT ctg.idofcontragent, ctg.contragentname, o.idoforg, o.shortnameinfoservice, o.address, "
                    + "c.contractid, p.surname, p.firstname, p.secondname, pc.preorderdate, pc.amount as complexAmount, ci.complexname, pc.idofpreordercomplex, "
                    + "md.menudetailname, pmd.amount as menudetailAmount "
                    + "FROM cf_preorder_complex pc INNER JOIN cf_clients c ON c.idofclient = pc.idofclient "
                    + "INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                    + "INNER JOIN cf_orgs o ON o.idoforg = c.idoforg "
                    + "INNER JOIN cf_complexinfo ci ON o.idoforg = ci.idoforg AND ci.menudate = pc.preorderdate AND ci.idofcomplex = pc.armcomplexid "
                    + "INNER JOIN cf_contragents ctg ON o.defaultsupplier = ctg.idofcontragent "
                    + "LEFT JOIN cf_preorder_menudetail pmd ON pc.idofpreordercomplex = pmd.idofpreordercomplex "
                    + "LEFT JOIN cf_menu m ON o.idoforg = m.idoforg AND pmd.preorderdate = m.menudate "
                    + "LEFT JOIN cf_menudetails md ON m.idofmenu = md.idofmenu AND pmd.armidofmenu = md.localidofmenu "
                    + "WHERE (pc.amount > 0 OR pmd.amount > 0) "
                    + "and (pc.preorderdate between :startDate and :endDate "
                    + "or pmd.preorderdate between :startDate and :endDate) "
                    + conditions
                    + "ORDER BY ctg.idofcontragent, o.idoforg, c.contractid, pc.idofpreordercomplex, md.menudetailname");
            query.setParameter("startDate", CalendarUtils.startOfDay(startTime).getTime());
            query.setParameter("endDate", CalendarUtils.endOfDay(endTime).getTime());
            if (idOfOrgList.size() > 0) {
                query.setParameterList("orgs", idOfOrgList);
            }
            if (idOfClientList.size() > 0) {
                query.setParameterList("clients", idOfClientList);
            }
            List list = query.list();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                Long idOfContragent = ((BigInteger) row[0]).longValue();
                String contragentName = (String) row[1];
                Long idOfOrg = ((BigInteger) row[2]).longValue();
                String shortNameInfoService = (String) row[3];
                String address = (String) row[4];
                Long contractId = ((BigInteger) row[5]).longValue();
                String surname = (String) row[6];
                String firstname = (String) row[7];
                String secondname = (String) row[8];
                Date preorderDate = new Date(((BigInteger) row[9]).longValue());
                Integer amountComplex = (Integer) row[10];
                String complexName = (String) row[11];
                Long idOfPreorderComplex = ((BigInteger) row[12]).longValue();
                String menuDetailName = (String) row[13];
                Integer amountMenuDetail = (Integer) row[14];
                PreorderReportItem item = new PreorderReportItem(idOfContragent, contragentName, idOfOrg, shortNameInfoService,
                        address, contractId, surname, firstname, secondname, preorderDate,
                        amountComplex, complexName, idOfPreorderComplex, menuDetailName, amountMenuDetail);
                result.add(item);
            }
            return new JRBeanCollectionDataSource(result);
        }
    }

    public PreordersReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }


    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new PreordersReport();
    }

    public PreordersReport() {
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }
}
