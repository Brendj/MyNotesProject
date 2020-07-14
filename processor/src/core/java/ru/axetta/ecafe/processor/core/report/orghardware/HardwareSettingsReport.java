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

    private static final int ADMINISTRATOR = 0;
    private static final int FEEDING = 1;
    private static final int GUARD = 2;
    private static final int INFO = 3;
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
            parameterMap.put("IS_IGNORE_PAGINATION", true);
            parameterMap.put(SELECTED_ADMINISTRATOR_PARAM, showAdministrator);
            parameterMap.put(SELECTED_CASHIER_PARAM, showCashier);
            parameterMap.put(SELECTED_GUARD_PARAM, showGuard);
            parameterMap.put(SELECTED_INFO_RAPAM, showInfo);
            parameterMap.put(SELECTED_TURNSTILES_PARAM, showTurnstiles);

            JRDataSource dataSource = createDataSource(session, selectedStatus, selectedDistrict, idOfOrgList,
                    allFriendlyOrgs, showAdministrator, showCashier, showGuard, showInfo, showTurnstiles);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new HardwareSettingsReport(generateEndTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Integer statusCondition, String selectedDistrict,
                List<Long> idOfOrgList, Boolean allFriendlyOrg, Boolean showAdministrator, Boolean showCashier,
                Boolean showGuard, Boolean showInfo, Boolean showTurnstiles) throws Exception {
            return new JRBeanCollectionDataSource(
                    buildOrgHardwareCollection(idOfOrgList, statusCondition, session, selectedDistrict, allFriendlyOrg,
                            showAdministrator, showCashier, showGuard, showInfo, showTurnstiles));
        }

        public static List<HardwareSettingsReportItem> buildOrgHardwareCollection(List<Long> idOfOrgList,
                Integer statusCondition, Session persistenceSession, String selectedDistricts, Boolean allFriendlyOrgs,
                Boolean showAdministrator, Boolean showCashier, Boolean showGuard, Boolean showInfo,
                Boolean showTurnstiles) throws Exception {
            List<HardwareSettingsReportItem> result = new ArrayList<HardwareSettingsReportItem>();

            Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
            Criteria orgSyncCriteria = persistenceSession.createCriteria(OrgSync.class);
            if (!CollectionUtils.isEmpty(idOfOrgList)) {
                if (allFriendlyOrgs) {
                    SQLQuery subQuery = persistenceSession.createSQLQuery(
                            "select friendlyOrg from cf_friendly_organization where currentOrg in (:idOfOrgs)");
                    subQuery.addScalar("friendlyOrg", LongType.INSTANCE);
                    subQuery.setParameterList("idOfOrgs", idOfOrgList);

                    List<Long> idsOfFriendlyOrg = subQuery.list();
                    orgCriteria.add(Restrictions.in("idOfOrg", idsOfFriendlyOrg));
                    orgSyncCriteria.add(Restrictions.in("idOfOrg",idsOfFriendlyOrg));
                } else {
                    orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
                    orgSyncCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
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

                Criteria hardwareSettingsCriteria = persistenceSession.createCriteria(HardwareSettings.class);
                hardwareSettingsCriteria.add(Restrictions.eq("org.idOfOrg", org.getIdOfOrg()));
                List<HardwareSettings> listOfSettings = hardwareSettingsCriteria.list();
                List<OrgSync> listOfOrgSync = orgSyncCriteria.list();
                OrgSync orgSync = getOrgSyncByOrg(listOfOrgSync, org);

                if (orgSync != null) {
                    for (HardwareSettings settings : listOfSettings) {
                        Set<HardwareSettingsMT> innerList = settings.getModuleTypes();
                        for (HardwareSettingsMT mt : innerList) {
                            if(mt.getInstallStatus() == 0) {
                                continue;
                            }
                            Integer moduleType = mt.getModuleType();
                            switch (moduleType) {
                                case (ADMINISTRATOR):
                                    if (showAdministrator) {
                                        result.add(new HardwareSettingsReportItem(settings, orgSync,
                                                "АРМ администратора ОУ", mt.getReaderName(), mt.getFirmwareVer(), true,
                                                persistenceSession));
                                    } else {
                                        continue;
                                    }
                                    break;
                                case (FEEDING):
                                    if (showCashier) {
                                        result.add(new HardwareSettingsReportItem(settings, orgSync,
                                                "АРМ оператора питания(кассира)", mt.getReaderName(),
                                                mt.getFirmwareVer(), false, persistenceSession));
                                    } else {
                                        continue;
                                    }
                                    break;
                                case (GUARD):
                                    if (showGuard) {
                                        result.add(new HardwareSettingsReportItem(settings, orgSync,
                                                "АРМ контроллера входа(охранника)", mt.getReaderName(),
                                                mt.getFirmwareVer(), false, persistenceSession));
                                    } else {
                                        continue;
                                    }
                                    break;
                                case (INFO):
                                    if (showInfo) {
                                        result.add(new HardwareSettingsReportItem(settings, orgSync, "Инфопанель",
                                                mt.getReaderName(), mt.getFirmwareVer(), false, persistenceSession));
                                    } else {
                                        continue;
                                    }
                                    break;
                            }
                        }
                    }

                    Criteria turnstileCriteria = persistenceSession.createCriteria(TurnstileSettings.class);
                    turnstileCriteria.add(Restrictions.eq("org.idOfOrg", org.getIdOfOrg()));

                    List<TurnstileSettings> turnstileSettingsList = turnstileCriteria.list();
                    for (TurnstileSettings ts : turnstileSettingsList) {
                        if (showTurnstiles) {
                            result.add(new HardwareSettingsReportItem(ts, turnstileSettingsList.size(), orgSync));
                        }
                    }
                }
            }
            return result;
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
