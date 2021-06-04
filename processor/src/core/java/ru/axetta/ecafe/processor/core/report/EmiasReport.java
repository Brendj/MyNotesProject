/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.EMIASbyDay;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
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
import java.text.Format;
import java.text.SimpleDateFormat;
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
    final public static String P_ID_OF_CLIENTS = "idOfClients";
    final public static String P_ALL_FRIENDLY_ORGS = "friendsOrg";

    private final static Logger logger = LoggerFactory.getLogger(EmiasReport.class);
    private static Format formatter = new SimpleDateFormat("dd.MM.yyyy");

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
                    new JRBeanCollectionDataSource(createDataSource(session, startTime, endTime)));
            Date generateEndTime = new Date();
            return new EmiasReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        public List<EmiasItem> createDataSource(Session session, Date startTime, Date endTime) {

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
            String filterPeriod = "";

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

            if (startTime != null)
            {
                filterPeriod += " and ce.dateliberate > " + startTime.getTime() + " ";
            }
            if (endTime != null)
            {
                filterPeriod += " and ce.dateliberate < " + endTime.getTime() + " ";
            }

            Query queryEMIAS = session.createSQLQuery(
                    "select distinct co.idoforg, co.shortnameinfoservice, co.shortaddress, ccg.groupname, cp.firstname, cp.surname, cp.secondname,\n"
                            + "cc.contractid, benefit.benef, ce.dateliberate, ce.idemias, ce.startdateliberate, ce.enddateliberate, "
                            + " CASE "
                            + " WHEN ce.archive = true THEN 'Посещает'       else 'Не посещает' \n"
                            + "END as status, ce.accepteddatetime, cc.idofclient \n" + "from cf_emias ce \n"
                            + "left join cf_clients cc on cc.meshguid = ce.guid\n"
                            + "left join cf_orgs co on cc.idoforg = co.idoforg\n"
                            + "left join cf_clientgroups ccg on ccg.idofclientgroup = cc.idofclientgroup and ccg.idoforg = co.idoforg\n"
                            + "left join cf_persons cp on cp.idofperson = cc.idofperson\n"
                            + "left join (select cc.idofclient, string_agg(ccdd.dtiszndescription, ', ') as benef from cf_clients cc \n"
                            + "left join cf_client_dtiszn_discount_info ccdd on cc.idofclient=ccdd.idofclient group by cc.idofclient) as benefit on benefit.idofclient=cc.idofclient\n"
                            + "where ce.processed = true " + filterOrgs + filterClients + filterPeriod);

            List rListEmias = queryEMIAS.list();
            long counter = 1;
            Map<Long,ArrayList<Date>> clientswithDates = new HashMap<>();
            for (Object o : rListEmias) {
                Object[] row = (Object[]) o;
                Long idoforg = ((BigInteger) row[0]).longValue();
                String shortnameinfoservice = (String) row[1];
                String shortaddress = (String) row[2];
                String groupname = (String) row[3];
                String firstname = (String) row[4];
                String lastname = (String) row[5];
                String middlename = (String) row[6];
                Long contractid = ((BigInteger) row[7]).longValue();
                String benefit = (String) row[8];
                Date dateliberate;
                try
                {
                    dateliberate = new Date(((BigInteger)row[9]).longValue());
                } catch (Exception e)
                {
                    dateliberate = null;
                }
                Long idEmias;
                try
                {
                    idEmias = ((BigInteger) row[10]).longValue();
                } catch (Exception e)
                {
                    idEmias = null;
                }
                Date startdateliberate;
                try
                {
                    startdateliberate = new Date(((BigInteger)row[11]).longValue());
                } catch (Exception e)
                {
                    startdateliberate = null;
                }
                Date enddateliberate;
                try
                {
                    enddateliberate = new Date(((BigInteger)row[12]).longValue());
                } catch (Exception e)
                {
                    enddateliberate = null;
                }
                Date dateArchived = null;
                try
                {
                    if (formatter.format(startdateliberate).equals(formatter.format(enddateliberate)))
                        dateArchived = startdateliberate;
                } catch (Exception e)
                {
                    dateArchived = null;
                }
                String status = (String) row[13];
                Date accepteddatetime;
                try
                {
                    accepteddatetime = new Date(((BigInteger)row[14]).longValue());
                } catch (Exception e)
                {
                    accepteddatetime = null;
                }
                Long idOOfClient = ((BigInteger) row[15]).longValue();
                String acceptedDates = "";
                ///////////////
                if (!clientswithDates.containsKey(idOOfClient)) {
                    //Кладем запись в контейнер
                    List<EMIASbyDay> emiaSbyDays = DAOReadonlyService.getInstance()
                            .getEmiasbyDayForClient(session, null, idOOfClient);
                    ArrayList<Date> dates = new ArrayList<>();
                    for (EMIASbyDay emiaSbyDay : emiaSbyDays) {
                        //Если ребенок ест
                        if (emiaSbyDay.getEat())
                            dates.add(emiaSbyDay.getDate());
                    }
                    clientswithDates.put(idOOfClient, dates);
                }
                //Достаем все даты по текущему клиенту
                for (Date date: clientswithDates.get(idOOfClient)) {
                    if (date.after(startdateliberate) && date.before(enddateliberate)) {
                        acceptedDates += formatter.format(date);
                        acceptedDates += ", ";
                    }
                }
                if (!acceptedDates.isEmpty())
                    acceptedDates = acceptedDates.substring(0, acceptedDates.length()-1);
                ///////////////////////////
                EmiasItem emiasItem = new EmiasItem(counter, idoforg, shortnameinfoservice, shortaddress, groupname,
                        firstname, lastname, middlename, contractid, benefit, idEmias, dateliberate, startdateliberate,
                        enddateliberate, dateArchived, status, acceptedDates, accepteddatetime);
                emiasItems.add(emiasItem);
                counter++;
            }

            //Collections.sort(emiasItems, new Comparator<EmiasItem>() {
            //    public int compare(EmiasItem o1, EmiasItem o2) {
            //        if (o1.getEmiasID().equals(o2.getEmiasID())) {
            //            return 0;
            //        } else if (Long.valueOf(o1.getEmiasID()) < Long.valueOf(o2.getEmiasID())) {
            //            return 1;
            //        } else {
            //            return -1;
            //        }
            //    }
            //});
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
