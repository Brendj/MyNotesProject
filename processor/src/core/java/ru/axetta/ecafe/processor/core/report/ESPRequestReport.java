/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.service.cardblock.CardBlockUnblockService;
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
 * Created by voinov on 22.06.21.
 */
public class ESPRequestReport extends BasicReportForMainBuildingOrgJob {
    final public static String P_ALL_FRIENDLY_ORGS = "friendsOrg";
    final public static String P_CARD_STATUS = "cardStatus";

    private final static Logger logger = LoggerFactory.getLogger(ESPRequestReport.class);


    public ESPRequestReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
    }

    public ESPRequestReport() {

    }

    public enum CardStateType {
        ALL("Все"), UNBLOCK("Только разблокированные"), BLOCK("Только заблокированные");
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        private CardStateType(String description) {
            this.description = description;
        }
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new ESPRequestReport();
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
            return new ESPRequestReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, null);
        }

        public List<ESPRequestItem> createDataSource(Session session, Date startTime, Date endTime) {

            List<ESPRequestItem> espRequestItems = new ArrayList<>();
            Org orgLoad;
            try {
                orgLoad = (Org) session.load(Org.class, Long.parseLong(
                        StringUtils.trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG))));
            } catch (Exception e) {
                orgLoad = null;
            }

            Boolean allFriendlyOrgs = Boolean
                    .parseBoolean(StringUtils.trimToEmpty(reportProperties.getProperty(P_ALL_FRIENDLY_ORGS)));

            String filterOrgs = "";
            if (orgLoad != null) {
                if (allFriendlyOrgs) {
                    for (Org org : orgLoad.getFriendlyOrg()) {
                        filterOrgs += "'" + org.getIdOfOrg() + "',";
                    }
                    filterOrgs = filterOrgs.substring(0, filterOrgs.length() - 1);
                } else {
                    filterOrgs = "'" + orgLoad.getIdOfOrg() + "'";
                }
                filterOrgs = " co.idoforg in (" + filterOrgs + ") ";
            }
            String filterPeriod = " cer.createdate > " + startTime.getTime() + " and cer.createdate < " + endTime.getTime() ;
            String fullfilter = "";
            if (!filterOrgs.isEmpty())
            {
                fullfilter = filterOrgs + " and " + filterPeriod;
            } else
            {
                fullfilter = filterPeriod;
            }

            //Это разблокирование всего
            Query queryESPrequest = session.createSQLQuery("select cer.createdate, cer.updatedate, cer.status, "
                    + "cer.numberrequest, cer.idoforg,\n"
                    + "co.shortaddress, co.shortname, cer.topic, cp.surname, cp.firstname, cp.secondname, "
                    + "cc.mobile from cf_esp_request  cer\n"
                    + "left join cf_orgs co on co.idoforg = cer.idoforg\n"
                    + "left join cf_clients cc on cc.idofclient = cer.idofclient\n"
                    + "left join cf_persons cp on cp.idofperson=cc.idofperson "
                    + "where " + fullfilter);

            List rListESP = queryESPrequest.list();
            for (Object o : rListESP) {
                Object[] row = (Object[]) o;
                Date createDate;
                try {
                    createDate = new Date(((BigInteger) row[0]).longValue());
                } catch (Exception e) {
                    createDate = null;
                }
                Date updatedate;
                try {
                    updatedate = new Date(((BigInteger) row[1]).longValue());
                } catch (Exception e) {
                    updatedate = null;
                }
                String status = (String) row[2];
                String numberrequest = (String) row[3];
                Long idoforg = ((BigInteger) row[4]).longValue();
                String shortaddress = (String) row[5];
                String shortname = (String) row[6];
                String topic = (String) row[7];
                String fio = (String) row[8] + " " + (String) row[9] + " " + (String) row[10];
                String mobile = (String) row[11];

                ESPRequestItem espRequestItem = new ESPRequestItem(createDate, updatedate, status, numberrequest,
                idoforg, shortaddress, shortname, topic, fio, mobile);
                espRequestItems.add(espRequestItem);

            }

            Collections.sort(espRequestItems, new Comparator<ESPRequestItem>() {
                public int compare(ESPRequestItem o1, ESPRequestItem o2) {
                    if (o1.getCreateDate().equals(o2.getCreateDate())) {
                        return 0;
                    } else if (o1.getCreateDate().getTime() < o2.getCreateDate().getTime()) {
                        return 1;
                    } else {
                        return -1;
                    }
                }
            });
            return espRequestItems;
        }
    }
}
