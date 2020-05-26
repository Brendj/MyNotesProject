/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orghardware;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgSync;
import ru.axetta.ecafe.processor.core.persistence.TurnstileSettings;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportForListOrgsJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
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

    private HardwareSettingsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public HardwareSettingsReport() {
    }

    public static class Builder extends BasicReportJob.Builder {

        private static final Integer SERVICED = 1;
        private static final Integer NOT_SERVICED = 2;

        public static final String LIST_OF_ORG_IDS_PARAM = "idOfOrgList";
        public static final String SELECTED_STATUS_PARAM = "selectedStatus";
        public static final String SELECTED_DISTRICT_PARAM = "selectedDistrict";
        public static final String SELECTED_ADMINISTRATOR_PARAM = "selectedAdministrator";
        public static final String SELECTED_CASHIER_PARAM = "selectedCashier";
        public static final String SELECTED_GUARD_PARAM = "selectedGuard";
        public static final String SELECTED_INFO_RAPAM = "selectedInfo";
        public static final String SELECTED_TURNSTILES_PARAM = "selectedTurnstiles";
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
            Boolean showAdministrator = Boolean
                    .parseBoolean(getReportProperties().getProperty(SELECTED_ADMINISTRATOR_PARAM));
            Boolean showCashier = Boolean.parseBoolean(getReportProperties().getProperty(SELECTED_CASHIER_PARAM));
            Boolean showGuard = Boolean.parseBoolean(getReportProperties().getProperty(SELECTED_GUARD_PARAM));
            Boolean showInfo = Boolean.parseBoolean(getReportProperties().getProperty(SELECTED_INFO_RAPAM));
            Boolean showTurnstiles = Boolean.parseBoolean(getReportProperties().getProperty(SELECTED_TURNSTILES_PARAM));

            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put(SELECTED_ADMINISTRATOR_PARAM, showAdministrator);
            parameterMap.put(SELECTED_CASHIER_PARAM, showCashier);
            parameterMap.put(SELECTED_GUARD_PARAM, showGuard);
            parameterMap.put(SELECTED_INFO_RAPAM, showInfo);
            parameterMap.put(SELECTED_TURNSTILES_PARAM, showTurnstiles);


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
            List<HardwareSettingsReportItem> result = new ArrayList<HardwareSettingsReportItem>();

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
            } else if (statusCondition.equals(NOT_SERVICED)) {
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

            for (Org org : orgs) {

                List<HardwareSettingsReportItem> tempList = new ArrayList<HardwareSettingsReportItem>();

                Criteria hardwareSettingsCriteria = persistenceSession.createCriteria(HardwareSettings.class);
                hardwareSettingsCriteria.add(Restrictions.eq("org.idOfOrg", org.getIdOfOrg()));
                List<HardwareSettings> listOfSettings = hardwareSettingsCriteria.list();

                Criteria orgSyncCriteria = persistenceSession.createCriteria(OrgSync.class);
                orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
                List<OrgSync> listOfOrgSync = orgSyncCriteria.list();

                OrgSync orgSync = getOrgSyncByOrg(listOfOrgSync, org);

                for (HardwareSettings settings : listOfSettings) {
                    Query query = persistenceSession.createSQLQuery(
                            "select hs.iphost, hs.dotnetver, hs.osver, hs.ramsize, hs.cpuhost, "
                                    + "(select r1.readername as readernameou from cf_hardware_settings_readers r1 where r1.idofhardwaresetting = hs.idofhardwaresetting and r1.idoforg = hs.idoforg and r1.usedbymodule = 0), "
                                    + "(select r1.firmwarever as firmwareversionou from cf_hardware_settings_readers r1 where r1.idofhardwaresetting = hs.idofhardwaresetting and r1.idoforg = hs.idoforg and r1.usedbymodule = 0), "
                                    + "(select r1.readername as readernamefeeding from  cf_hardware_settings_readers r1 where r1.idofhardwaresetting = hs.idofhardwaresetting and r1.idoforg = hs.idoforg and r1.usedbymodule = 1), "
                                    + "(select r1.firmwarever as firmwareversionfeeding from  cf_hardware_settings_readers r1 where r1.idofhardwaresetting = hs.idofhardwaresetting and r1.idoforg = hs.idoforg and r1.usedbymodule = 1), "
                                    + "(select r1.readername as readernameguard from cf_hardware_settings_readers r1 where r1.idofhardwaresetting = hs.idofhardwaresetting and r1.idoforg = hs.idoforg and r1.usedbymodule = 2), "
                                    + "(select r1.firmwarever as firmwareversionguard from cf_hardware_settings_readers r1 where r1.idofhardwaresetting = hs.idofhardwaresetting and r1.idoforg = hs.idoforg and r1.usedbymodule = 2), "
                                    + "(select r1.readername as readernameinfo from cf_hardware_settings_readers r1 where r1.idofhardwaresetting = hs.idofhardwaresetting and r1.idoforg = hs.idoforg and r1.usedbymodule = 3), "
                                    + "(select r1.firmwarever as firmwareversioninfo  from cf_hardware_settings_readers r1 where r1.idofhardwaresetting = hs.idofhardwaresetting and r1.idoforg = hs.idoforg and r1.usedbymodule = 3) "
                                    + " from cf_hardware_settings hs "
                                    + "where hs.idoforg = :idOfOrg and hs.idofhardwaresetting = :idOfHardwareSetting ");

                    query.setParameter("idOfOrg", settings.getOrg().getIdOfOrg());
                    query.setParameter("idOfHardwareSetting",
                            settings.getCompositeIdOfHardwareSettings().getIdOfHardwareSetting());

                    List list = query.list();

                    for (Object o : list) {
                        Object[] values = (Object[]) o;
                        if (orgSync != null) {
                            HardwareSettingsReportItem item = new HardwareSettingsReportItem(orgSync, true);

                            item.setRemoteAddressOU((String) values[0]);
                            item.setRemoteAddressFeeding((String) values[0]);
                            item.setRemoteAddressGuard((String) values[0]);
                            item.setRemoteAddressInfo((String) values[0]);

                            item.setDotNetVersionOU((String) values[1]);
                            item.setDotNetVersionFeeding((String) values[1]);
                            item.setDotNetVersionGuard((String) values[1]);
                            item.setDotNetVersionInfo((String) values[1]);

                            item.setOsVersionOU((String) values[2]);
                            item.setOsVersionFeeding((String) values[2]);
                            item.setOsVersionGuard((String) values[2]);
                            item.setOsVersionInfo((String) values[2]);

                            item.setRamSizeOU((String) values[3]);
                            item.setRamSizeFeeding((String) values[3]);
                            item.setRamSizeGuard((String) values[3]);
                            item.setRamSizeInfo((String) values[3]);

                            item.setCpuVersionOU((String) values[4]);
                            item.setCpuVersionFeeding((String) values[4]);
                            item.setCpuVersionGuard((String) values[4]);
                            item.setCpuVersionInfo((String) values[4]);

                            item.setReaderNameOU((String) values[5]);
                            if (values[6] != null) {
                                item.setFirmwareVersionOU((String) values[6]);
                            }

                            item.setReaderNameFeeding((String) values[7]);
                            if (values[8] != null) {
                                item.setFirmwareVersionFeeding((String) values[8]);
                            }

                            item.setReaderNameGuard((String) values[9]);
                            if (values[10] != null) {
                                item.setFirmwareVersionGuard((String) values[10]);
                            }
                            item.setRemoteAddressInfo((String) values[11]);
                            if (values[12] != null) {
                                item.setFirmwareVersionInfo((String) values[12]);
                            }
                            result.add(item);
                        }
                    }
                }

                Criteria turnstileCriteria = persistenceSession.createCriteria(TurnstileSettings.class);
                turnstileCriteria.add(Restrictions.eq("org.idOfOrg", org.getIdOfOrg()));

                List<HardwareSettingsReportItem> newTurnstile = new ArrayList<HardwareSettingsReportItem>();

                List<TurnstileSettings> turnstileSettingsList = turnstileCriteria.list();
                if (!turnstileSettingsList.isEmpty()) {
                    if (listOfSettings.size() < turnstileSettingsList.size()) {
                        for (int i = 0; i < turnstileSettingsList.size(); i++) {
                            if (i < listOfSettings.size()) {
                                HardwareSettingsReportItem item = result.get(i);
                                item.setTurnstileId(turnstileSettingsList.get(i).getTurnstileId());
                                item.setNumOfEntries(turnstileSettingsList.get(i).getNumOfEntries());
                                item.setNumOfTurnstile(turnstileSettingsList.size());
                                item.setControllerModel(turnstileSettingsList.get(i).getControllerModel());
                                item.setControllerFirmwareVersion(
                                        turnstileSettingsList.get(i).getControllerFirmwareVersion());
                                item.setIsWorkWithLongIds(turnstileSettingsList.get(i).getIsReadsLongIdsIncorrectly());
                                item.setTimeCoefficient(turnstileSettingsList.get(i).getTimeCoefficient());
                            } else {
                                HardwareSettingsReportItem tempItem = new HardwareSettingsReportItem(orgSync, false);
                                tempItem.setTurnstileId(turnstileSettingsList.get(i).getTurnstileId());
                                tempItem.setNumOfEntries(turnstileSettingsList.get(i).getNumOfEntries());
                                tempItem.setNumOfTurnstile(turnstileSettingsList.size());
                                tempItem.setControllerModel(turnstileSettingsList.get(i).getControllerModel());
                                tempItem.setControllerFirmwareVersion(
                                        turnstileSettingsList.get(i).getControllerFirmwareVersion());
                                tempItem.setIsWorkWithLongIds(
                                        turnstileSettingsList.get(i).getIsReadsLongIdsIncorrectly());
                                tempItem.setTimeCoefficient(turnstileSettingsList.get(i).getTimeCoefficient());
                                newTurnstile.add(tempItem);
                            }
                        }
                    }

                    if (listOfSettings.size() >= turnstileSettingsList.size()) {
                        for (int i = 0; i < listOfSettings.size(); i++) {
                            if (i == turnstileSettingsList.size()) {
                                break;
                            } else {
                                HardwareSettingsReportItem item = result.get(i);
                                item.setTurnstileId(turnstileSettingsList.get(i).getTurnstileId());
                                item.setNumOfEntries(turnstileSettingsList.get(i).getNumOfEntries());
                                item.setNumOfTurnstile(turnstileSettingsList.size());
                                item.setControllerModel(turnstileSettingsList.get(i).getControllerModel());
                                item.setControllerFirmwareVersion(
                                        turnstileSettingsList.get(i).getControllerFirmwareVersion());
                                item.setIsWorkWithLongIds(turnstileSettingsList.get(i).getIsReadsLongIdsIncorrectly());
                            }
                        }
                    }
                    result.addAll(newTurnstile);
                }
            }
            return result;
        }


        private static HardwareSettings getSettingByOrg(List<HardwareSettings> settings, Org org) {
            if (CollectionUtils.isEmpty(settings)) {
                return null;
            }
            for (HardwareSettings setting : settings) {
                Set<Org> setOrg = setting.getOrg().getFriendlyOrg();
                if (setOrg.contains(org)) {
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
