/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

public class CardApplicationReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Отчет по заявлениям на карту";
    public static final String[] TEMPLATE_FILE_NAMES = {"EscortAccessDOUReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};
    final public static String P_ID_OF_CLIENTS="idOfClients";
    final public static String P_ENABLE_DATE_FILTER="enableDateFilter";

    final private static Logger logger = LoggerFactory.getLogger(CardApplicationReport.class);

    public static class Builder extends BasicReportForOrgJob.Builder {

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
            parameterMap.put("SUBREPORT_DIR", RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath());

            String idOfOrgString = StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            Long idOfOrg = Long.parseLong(idOfOrgString);

            Org org = (Org) session.load(Org.class, idOfOrg);
            if (null != org) {
                parameterMap.put("shortName", org.getShortNameInfoService());
            } else {
                parameterMap.put("shortName", "не указано");
            }

            String idOfClients = StringUtils.trimToEmpty(reportProperties.getProperty(P_ID_OF_CLIENTS));
            List<String> stringClientsList = Arrays.asList(StringUtils.split(idOfClients, ','));
            List<Long> idOfClientList = new ArrayList<Long>();
            List<String> clientFIOList = new LinkedList<String>();
            for (String idOfClient : stringClientsList) {
                Long idOfClientLong = Long.parseLong(idOfClient);
                idOfClientList.add(idOfClientLong);
                Person person = DAOUtils.getPersonByClientId(session, idOfClientLong);
                if (null != person) {
                    clientFIOList.add(person.getFullName());
                }
            }
            parameterMap.put("clientName", StringUtils.join(clientFIOList, ", "));

            Boolean enableDateFilter = Boolean.parseBoolean(reportProperties.getProperty(P_ENABLE_DATE_FILTER));

            JRDataSource dataSource = createDataSource(session, !enableDateFilter ? null : startTime, !enableDateFilter ? null : endTime, idOfOrg, idOfClientList, parameterMap);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);
            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();
            return new CardApplicationReport(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, Long idOfOrg,
                List<Long> idOfClientList, Map<String, Object> parameterMap) throws Exception {
            Date maxDate = null;
            Date minDate = null;

            List<ReportItem> reportItemList = new LinkedList<ReportItem>();
            String conditions = "";
            if (idOfClientList.size() > 0) {
                conditions += " and c.idofclient in (:clients) ";
            }

            if (null != startTime && null != endTime) {
                conditions += " and cr.createddate between :startDate and :endDate ";
            }

            Query query = session.createSQLQuery(
                    "select cr.createddate, p_grd.surname || ' ' || p_grd.firstname || ' ' || p_grd.secondname as grd_name, "
                     + "    p.surname || ' ' || p.firstname || ' ' || p.secondname as client_name, "
                     + "    cast (c_grd.passportseries as text) || cast (c_grd.passportnumber as text) as passport, cr.typecard, ca.cardtype, ca.state, "
                     + "    ca.issuedate, "
                     + "    (select count(*) as count from cf_cards card where card.idofclient in "
                     + "        (select idofchildren from cf_client_guardian where idofguardian = c_grd.idofclient union select c_grd.idofclient) and card.cardtype = :type) as cards_count "
                     + "from cf_card_requests cr "
                     + "join cf_clients c_grd on c_grd.idofclient = cr.idofclient "
                     + "join cf_persons p_grd on p_grd.idofperson = c_grd.idofperson "
                     + "join cf_client_guardian cg on cg.idofguardian = c_grd.idofclient "
                     + "join cf_clients c on c.idofclient = cg.idofchildren "
                     + "join cf_persons p on c.idofperson = p.idofperson "
                     + "left join cf_cards ca on ca.idofclient = c.idofclient and ca.state = :state "
                     + "where c.idoforg = :idOfOrg"
                     + conditions);
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("type", 1);  // Москвенок
            query.setParameter("state", Card.ACTIVE_STATE);
            if (idOfClientList.size() > 0) {
                query.setParameterList("clients", idOfClientList);
            }

            if (null != startTime && null != endTime) {
                query.setParameter("startDate", startTime.getTime());
                query.setParameter("endDate", endTime.getTime());
            }
            List list = query.list();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                Date applicationDate =  new Date(((BigInteger) row[0]).longValue());
                String guardianName = (String) row[1];
                String clientName = (String) row[2];
                String passport = (String) row[3];
                Integer requestCardTypeNum = (Integer) row[4];
                String requestCardType = requestCardTypeNum == null ? "" : Card.TYPE_NAMES[requestCardTypeNum];
                Integer currentCardTypeNum = (Integer) row[5];
                String currentCardType = currentCardTypeNum == null ? "" : Card.TYPE_NAMES[currentCardTypeNum];
                Integer currentCardStateNum = (Integer) row[6];
                String currentCardState = currentCardStateNum == null ? "" : Card.STATE_NAMES[currentCardStateNum];
                Date issueDate = new Date(((BigInteger) row[7]).longValue());
                Integer cardsCount = ((BigInteger) row[8]).intValue();

                reportItemList.add(new ReportItem(applicationDate, clientName, guardianName, passport, requestCardType,
                        currentCardType, currentCardState, issueDate, cardsCount));

                if (null == startTime && null == endTime) {
                    if (null == maxDate || maxDate.getTime() < applicationDate.getTime()) {
                        maxDate = applicationDate;
                    }

                    if (null == minDate || minDate.getTime() > applicationDate.getTime()) {
                        minDate = applicationDate;
                    }
                }
            }

            if (null != minDate) {
                parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(minDate));
            }
            if (null != maxDate) {
                parameterMap.put("endDate", CalendarUtils.dateShortToStringFullYear(maxDate));
            }
            return new JRBeanCollectionDataSource(reportItemList);
        }

        public static class ReportItem {
            private Date applicationDate;
            private String clientName;
            private String guardianName;
            private String passport;
            private String requestCardType;
            private String currentCardType;
            private String currentCardState;
            private Date issueDate;
            private Integer cardsCount;

            public ReportItem(Date applicationDate, String clientName, String guardianName, String passport, String requestCardType,
                    String currentCardType, String currentCardState, Date issueDate, Integer cardsCount) {
                this.applicationDate = applicationDate;
                this.clientName = clientName;
                this.guardianName = guardianName;
                this.passport = passport;
                this.requestCardType = requestCardType;
                this.currentCardType = currentCardType;
                this.currentCardState = currentCardState;
                this.issueDate = issueDate;
                this.cardsCount = cardsCount;
            }

            public Date getApplicationDate() {
                return applicationDate;
            }

            public void setApplicationDate(Date applicationDate) {
                this.applicationDate = applicationDate;
            }

            public String getClientName() {
                return clientName;
            }

            public void setClientName(String clientName) {
                this.clientName = clientName;
            }

            public String getGuardianName() {
                return guardianName;
            }

            public void setGuardianName(String guardianName) {
                this.guardianName = guardianName;
            }

            public String getPassport() {
                return passport;
            }

            public void setPassport(String passport) {
                this.passport = passport;
            }

            public String getRequestCardType() {
                return requestCardType;
            }

            public void setRequestCardType(String requestCardType) {
                this.requestCardType = requestCardType;
            }

            public String getCurrentCardType() {
                return currentCardType;
            }

            public void setCurrentCardType(String currentCardType) {
                this.currentCardType = currentCardType;
            }

            public String getCurrentCardState() {
                return currentCardState;
            }

            public void setCurrentCardState(String currentCardState) {
                this.currentCardState = currentCardState;
            }

            public Date getIssueDate() {
                return issueDate;
            }

            public void setIssueDate(Date issueDate) {
                this.issueDate = issueDate;
            }

            public Integer getCardsCount() {
                return cardsCount;
            }

            public void setCardsCount(Integer cardsCount) {
                this.cardsCount = cardsCount;
            }
        }
    }

    public CardApplicationReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }


    @Override
    public BasicReportForOrgJob createInstance() {
        return new CardApplicationReport();
    }

    public CardApplicationReport() {
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
