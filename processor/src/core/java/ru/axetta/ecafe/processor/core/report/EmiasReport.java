/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.service.CardBlockUnblockService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by voinov on 15.05.21.
 */
public class EmiasReport extends BasicReportForMainBuildingOrgJob {

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
    public static final String REPORT_NAME = "Детализированный отчет по посещению";
    public static final String[] TEMPLATE_FILE_NAMES = {"BlockUnblockCard.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-46, -47, -48};
    final public static String P_ID_OF_CLIENTS = "idOfClients";
    final public static String P_ALL_FRIENDLY_ORGS = "friendsOrg";

    private final static Logger logger = LoggerFactory.getLogger(EmiasReport.class);


    public EmiasReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
    }

    public EmiasReport() {

    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new EmiasReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();

            startTime = CalendarUtils.roundToBeginOfDay(startTime);
            endTime = CalendarUtils.endOfDay(endTime);
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    new JRBeanCollectionDataSource(createDataSource(session)));
            Date generateEndTime = new Date();
            return new EmiasReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        public List<EmiasItem> createDataSource(Session session) {

            List<EmiasItem> emiasItems = new ArrayList<>();
            Org orgLoad;
            try {
                orgLoad = (Org) session.load(Org.class, Long.parseLong(
                        StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG))));
            } catch (Exception e) {
                orgLoad = null;
            }

            List<Long> idOfClients = parseStringAsLongList(P_ID_OF_CLIENTS);
            Boolean allFriendlyOrgs = Boolean
                    .parseBoolean(StringUtils.trimToEmpty(reportProperties.getProperty(P_ALL_FRIENDLY_ORGS)));

            String filterOrgs = "";
            String filterClients = "";
            if (orgLoad != null) {
                if (allFriendlyOrgs) {
                    for (Org org : orgLoad.getFriendlyOrg()) {
                        filterOrgs += "'" + org.getIdOfOrg() + "',";
                    }
                    filterOrgs = filterOrgs.substring(0, filterOrgs.length() - 1);
                } else {
                    filterOrgs = "'" + orgLoad.getIdOfOrg() + "'";
                }
                filterOrgs = " and cc.idoforg in (" + filterOrgs + ") ";
            }
            if (idOfClients != null && !idOfClients.isEmpty()) {
                for (Long idClient : idOfClients) {
                    filterClients += "'" + idClient + "',";
                }
                filterClients = filterClients.substring(0, filterClients.length() - 1);
                filterClients = " and cc.idofclient in (" + filterClients + ") ";
            }

            Query queryEMIAS = session.createSQLQuery(
                    "select ce.id, cp.firstname, cp.surname, cp.secondname, ce.dateliberate, ce.startdateliberate, ce.enddateliberate, ce.accepted\n"
                            + "from cf_emias ce \n" + "left join cf_clients cc on cc.meshguid = ce.guid\n"
                            + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
                            + "where ce.processed = true " + filterOrgs + filterClients);

            List rListEmias = queryEMIAS.list();
            for (Object o : rListEmias) {
                Object[] row = (Object[]) o;
                Long idEmias = ((BigInteger) row[0]).longValue();
                String firstname = (String) row[1];
                String lastname = (String) row[2];
                String middlename = (String) row[3];
                Date dateliberate = new Date(((BigInteger)row[4]).longValue());
                Date startdateliberate = new Date(((BigInteger)row[5]).longValue());
                Date enddateliberate = new Date(((BigInteger)row[6]).longValue());
                Boolean accepted = (Boolean) row[7];
                if (accepted == null)
                    accepted = false;
                EmiasItem emiasItem = new EmiasItem(idEmias, firstname, lastname, middlename,
                        dateliberate, startdateliberate, enddateliberate, accepted);
                emiasItems.add(emiasItem);

            }

            Collections.sort(emiasItems, new Comparator<EmiasItem>() {
                public int compare(EmiasItem o1, EmiasItem o2) {
                    if (o1.getEmiasID().equals(o2.getEmiasID())) {
                        return 0;
                    } else if (Long.valueOf(o1.getEmiasID()) < Long.valueOf(o2.getEmiasID())) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            return emiasItems;
        }

        private List<Long> parseStringAsLongList(String propertyName) {
            String propertyValueString = reportProperties.getProperty(propertyName);
            String[] propertyValueArray = StringUtils.split(propertyValueString, ',');
            List<Long> propertyValueList = new ArrayList<Long>();
            for (String propertyValue : propertyValueArray) {
                try {
                    propertyValueList.add(Long.parseLong(propertyValue));
                } catch (NumberFormatException e) {
                    logger.error(String.format("Unable to parse propertyValue: property = %s, value = %s", propertyName,
                            propertyValue), e);
                }
            }
            return propertyValueList;
        }
    }
}
