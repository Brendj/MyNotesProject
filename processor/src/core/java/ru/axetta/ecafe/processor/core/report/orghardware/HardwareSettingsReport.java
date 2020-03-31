/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orghardware;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForListOrgsJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HardwareSettingsReport extends BasicReportForListOrgsJob {

    private Logger logger = LoggerFactory.getLogger(HardwareSettingsReport.class);

    public HardwareSettingsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public HardwareSettingsReport() {
    }

    public static class Builder extends BasicReportJob.Builder {

        private static final Integer SERVICED = 1;
        private static final Integer NOT_SERVISED = 2;

        public static final String LIST_OF_ORG_IDS_PARAM = "idOfOrgList";
        public static final String SELECTED_STATUS_PARAM = "selectedStatus";
        public static final String SELECTED_DISTRICT_PARAM = "selectedDistrict";
        public static final String ALL_FRIENDLY_ORGS = "allFriendlyOrgs";

        private String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                    + HardwareSettingsReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            String idOfOrgs = StringUtils.trimToEmpty(getReportProperties().getProperty(LIST_OF_ORG_IDS_PARAM));
            List<String> stringOrgList = Arrays.asList(StringUtils.split(idOfOrgs, ','));
            List<Long> idOfOrgList = new ArrayList<Long>(stringOrgList.size());
            for (String idOfOrg : stringOrgList) {
                idOfOrgList.add(Long.parseLong(idOfOrg));
            }

            String selectedDistrict = getReportProperties().getProperty(SELECTED_DISTRICT_PARAM);
            Integer selectedStatus = Integer.parseInt(getReportProperties().getProperty(SELECTED_STATUS_PARAM));
            Boolean allFriendlyOrgs = Boolean.parseBoolean(getReportProperties().getProperty(ALL_FRIENDLY_ORGS));

            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));

            JRDataSource dataSource = createDataSource(session, selectedStatus, selectedDistrict, idOfOrgList,
                    allFriendlyOrgs);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new HardwareSettingsReport(generateEndTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Integer statusCondition, String selectedDistrict,
                List<Long> idOfOrgList, Boolean allFriendlyOrg) throws Exception {
            return new JRBeanCollectionDataSource(
                    buildOrgHardwareCollection(idOfOrgList, statusCondition, session, selectedDistrict,
                            allFriendlyOrg));
        }

        public static List<HardwareSettingsReportItem> buildOrgHardwareCollection(List<Long> idOfOrgList,
                Integer statusCondition, Session persistenceSession, String selectedDistricts, Boolean allFriendlyOrgs)
                throws Exception {
            List<HardwareSettingsReportItem> result = new ArrayList<HardwareSettingsReportItem>(idOfOrgList.size());

            Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
            if (!CollectionUtils.isEmpty(idOfOrgList)) {
                if (allFriendlyOrgs) {
                    SQLQuery subQuery = persistenceSession.createSQLQuery(
                            "select friendlyOrg from cf_friendly_organization where currentOrg in (:idOfOrgs)");
                    subQuery.addScalar("friendlyOrg", LongType.INSTANCE);
                    subQuery.setParameterList("idOfOrgs", idOfOrgList);

                    List<Long> idsOfFriendlyOrg = subQuery.list();
                    orgCriteria.add(Restrictions.in("idOfOrg", idsOfFriendlyOrg));
                } else {
                    orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
                }
            }

            if (statusCondition.equals(SERVICED)) {
                orgCriteria.add(Restrictions.eq("state", Org.ACTIVE_STATE));
            } else if (statusCondition.equals(NOT_SERVISED)) {
                orgCriteria.add(Restrictions.eq("state", Org.INACTIVE_STATE));
            }

            if (!StringUtils.isEmpty(selectedDistricts)) {
                orgCriteria.add(Restrictions.like("district", selectedDistricts));
            }
            orgCriteria.addOrder(Order.asc("shortName"));
            List<Org> orgs = orgCriteria.list();
            if (orgs.isEmpty()) {
                return Collections.emptyList();
            }

            Criteria hardwareSettingsCriteria = persistenceSession.createCriteria(HardwareSettings.class);
            hardwareSettingsCriteria.add(Restrictions.in("org.idOfOrg", idOfOrgList));
            List<HardwareSettings> listOfSettings = hardwareSettingsCriteria.list();

            Criteria orgSyncCriteria = persistenceSession.createCriteria(OrgSync.class);
            orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            List<OrgSync> listOfOrgSync = orgSyncCriteria.list();



            for (Org org : orgs) {
                OrgSync orgSync = getOrgSyncByOrg(listOfOrgSync, org);
                HardwareSettings hardwareSettings = getSettingByOrg(listOfSettings, org);

                Criteria hardwareSettingsReadersCriteria = persistenceSession.createCriteria(HardwareSettingsReaders.class);
                hardwareSettingsReadersCriteria.add(Restrictions.in("org.idOfOrg", idOfOrgList));
                List<HardwareSettingsReaders> listOfReaders = hardwareSettingsReadersCriteria.list();

                Criteria turnstileSettingsCriteria = persistenceSession.createCriteria(TurnstileSettings.class);
                turnstileSettingsCriteria.add(Restrictions.in("org.idOfOrg", idOfOrgList));
                List<TurnstileSettings> listOfTurnstileSettings = turnstileSettingsCriteria.list();

                Criteria hardwareSettingsMTCriteria = persistenceSession.createCriteria(HardwareSettingsMT.class);
                hardwareSettingsMTCriteria.add(Restrictions.in("org.idOfOrg", idOfOrgList));
                List<HardwareSettingsMT> listOfMT = hardwareSettingsMTCriteria.list();
                HardwareSettingsReaders hardwareSettingsReaders = getReadersByOrg(listOfReaders, org);
                TurnstileSettings turnstileSettings = getTurnstilesByOrg(listOfTurnstileSettings, org);
                HardwareSettingsMT hardwareSettingsMT = getHardwareSettingsMT(listOfMT, org);

                HardwareSettingsReportItem item = new HardwareSettingsReportItem(org, hardwareSettings, hardwareSettingsReaders,
                        orgSync, turnstileSettings, hardwareSettingsMT);

                if (orgSync != null) {
                    item.setDataBaseSize(orgSync.getDatabaseSize());
                    item.setClientVersion(orgSync.getClientVersion());

                }
                result.add(item);
            }
            return result;
        }

        private static HardwareSettings getSettingByOrg(List<HardwareSettings> settings, Org org) {
            if (CollectionUtils.isEmpty(settings)) {
                return null;
            }
            for(HardwareSettings setting : settings){
                if(setting.getOrgsInternal().contains(org)){
                    return setting;
                }
            }
            return null;
        }

        private static OrgSync getOrgSyncByOrg(List<OrgSync> listOfOrgSync, Org org) {
            if (CollectionUtils.isEmpty(listOfOrgSync)) {
                return null;
            }
            for (OrgSync orgSync : listOfOrgSync) {
                if (orgSync.getIdOfOrg().equals(org.getIdOfOrg())) {
                    return orgSync;
                }
            }
            return null;
        }

        private static HardwareSettingsReaders getReadersByOrg(List<HardwareSettingsReaders> readers, Org org) {
            if (CollectionUtils.isEmpty(readers)) {
                return null;
            }
            for (HardwareSettingsReaders hardwareSettingsReaders : readers) {
                if (hardwareSettingsReaders.getOrgsInternal().contains(org)) {
                    return hardwareSettingsReaders;
                }
            }
            return null;
        }

        private static TurnstileSettings getTurnstilesByOrg(List<TurnstileSettings> turnstiles, Org org) {
            if (CollectionUtils.isEmpty(turnstiles)) {
                return null;
            }
            for (TurnstileSettings turnstileSettings : turnstiles) {
                if (turnstileSettings.getOrgsInternal().contains(org)) {
                    return turnstileSettings;
                }
            }
            return null;
        }

        private static HardwareSettingsMT getHardwareSettingsMT(List<HardwareSettingsMT> hardwareSettingsMTS, Org org) {
            if (CollectionUtils.isEmpty(hardwareSettingsMTS)) {
                return null;
            }
            for (HardwareSettingsMT hardwareSettingsMT : hardwareSettingsMTS) {
                if (hardwareSettingsMT.getOrgsInternal().contains(org)) {
                    return hardwareSettingsMT;
                }
            }
            return null;
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new HardwareSettingsReport();
    }

    @Override
    public HardwareSettingsReport.Builder createBuilder(String templateFilename) {
        return new HardwareSettingsReport.Builder(templateFilename);
    }
}
