/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orgparameters;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.FeedingSetting;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrgSync;
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

public class OrgSettingsReport extends BasicReportForListOrgsJob {
    private Logger logger = LoggerFactory.getLogger(OrgSettingsReport.class);

    public OrgSettingsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public OrgSettingsReport(){

    }

    public static class Builder extends BasicReportJob.Builder {

        private static final Integer SERVICED  = 1;
        private static final Integer NOT_SERVISED = 2;

        public static final String LIST_OF_ORG_IDS_PARAM = "idOfOrgList";
        public static final String SELECTED_DISTRICT_PARAM = "selectedDistrict";
        public static final String SELECTED_STATUS_PARAM = "selectedStatus";
        public static final String SHOW_REQUISITE = "showRequisite";
        public static final String SHOW_FEEDING_SETTINGS = "showFeedingSettings";
        public static final String SHOW_CARD_SETTINGS = "showCardSettings";
        public static final String SHOW_OTHER_SETTINGS = "showOtherSetting";
        public static final String ALL_FRIENDLY_ORGS = "allFriendlyOrgs";

        private String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                    + OrgSettingsReport.class.getSimpleName() + ".jasper";
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

            String selectedDistrinct = getReportProperties().getProperty(SELECTED_DISTRICT_PARAM);
            Integer selectedStatus = Integer.parseInt(getReportProperties().getProperty(SELECTED_STATUS_PARAM));
            Boolean showRequisite = Boolean.parseBoolean(getReportProperties().getProperty(SHOW_REQUISITE));
            Boolean showFeedingSettings = Boolean.parseBoolean(getReportProperties().getProperty(SHOW_FEEDING_SETTINGS));
            Boolean showCardSettings = Boolean.parseBoolean(getReportProperties().getProperty(SHOW_CARD_SETTINGS));
            Boolean showOtherSetting = Boolean.parseBoolean(getReportProperties().getProperty(SHOW_OTHER_SETTINGS));
            Boolean allFriendlyOrgs = Boolean.parseBoolean(getReportProperties().getProperty(ALL_FRIENDLY_ORGS));

            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));
            parameterMap.put(SHOW_REQUISITE, showRequisite);
            parameterMap.put(SHOW_FEEDING_SETTINGS, showFeedingSettings);
            parameterMap.put(SHOW_CARD_SETTINGS, showCardSettings);
            parameterMap.put(SHOW_OTHER_SETTINGS, showOtherSetting);

            JRDataSource dataSource = createDataSource(session, selectedDistrinct, selectedStatus,
                    idOfOrgList, allFriendlyOrgs);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new OrgSettingsReport(generateEndTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, String selectedDistrict,
                Integer selectedStatus, List<Long> idOfOrgList, Boolean allFriendlyOrg) throws Exception {
            return new JRBeanCollectionDataSource(buildOrgSettingCollection(idOfOrgList, selectedStatus, session,
                    selectedDistrict, allFriendlyOrg));
        }


        public static List<OrgSettingsReportItem> buildOrgSettingCollection(List<Long> idOfOrgList, Integer statusCondition,
                Session persistenceSession, String selectedDistricts, Boolean allFriendlyOrgs)
                throws Exception {
            List<OrgSettingsReportItem> result = new ArrayList<OrgSettingsReportItem>(idOfOrgList.size());

            Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
            if(!CollectionUtils.isEmpty(idOfOrgList)) {
                if(allFriendlyOrgs){
                    SQLQuery subQuery = persistenceSession.createSQLQuery("select friendlyOrg from cf_friendly_organization where currentOrg in (:idOfOrgs)");
                    subQuery.addScalar("friendlyOrg", LongType.INSTANCE);
                    subQuery.setParameterList("idOfOrgs", idOfOrgList);

                    List<Long> idsOfFriendlyOrg = subQuery.list();
                    if(CollectionUtils.isEmpty(idsOfFriendlyOrg)){
                        idsOfFriendlyOrg = idOfOrgList;
                    }
                    orgCriteria.add(Restrictions.in("idOfOrg", idsOfFriendlyOrg));
                } else {
                    orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
                }
            }

            if(statusCondition.equals(SERVICED)){
                orgCriteria.add(Restrictions.eq("state", Org.ACTIVE_STATE));
            } else if(statusCondition.equals(NOT_SERVISED)){
                orgCriteria.add(Restrictions.eq("state", Org.INACTIVE_STATE));
            }

            if(!StringUtils.isEmpty(selectedDistricts)){
                orgCriteria.add(Restrictions.like("district", selectedDistricts));
            }
            orgCriteria.addOrder(Order.asc("shortName"));
            List<Org> orgs = orgCriteria.list();
            if(orgs.isEmpty()){
                return Collections.emptyList();
            }

            Criteria feedSettingCriteria  = persistenceSession.createCriteria(FeedingSetting.class);
            feedSettingCriteria.createAlias("orgsInternal", "orgs");
            if(CollectionUtils.isEmpty(idOfOrgList)) {
                for (Org org : orgs) {
                    idOfOrgList.add(org.getIdOfOrg());
                }
            }
            feedSettingCriteria.add(Restrictions.in("orgs.idOfOrg", idOfOrgList));
            List<FeedingSetting> settings = feedSettingCriteria.list();

            Criteria orgSyncCriteria = persistenceSession.createCriteria(OrgSync.class);
            orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            List<OrgSync> listOfOrgSync = orgSyncCriteria.list();

            for(Org org : orgs){
                FeedingSetting setting = getSettingByOrg(settings, org);
                OrgSync orgSync = getOrgSyncByOrg(listOfOrgSync, org);
                OrgSettingsReportItem item = new OrgSettingsReportItem(org, setting);
                if(orgSync != null){
                    item.setArmVersionNumber(orgSync.getClientVersion());
                }
                result.add(item);
            }
            return result;
        }
    }

    private static FeedingSetting getSettingByOrg(List<FeedingSetting> settings, Org org){
        if(CollectionUtils.isEmpty(settings)){
            return null;
        }
        for(FeedingSetting setting : settings){
            if(setting.getOrgsInternal().contains(org)){
                return setting;
            }
        }
        return null;
    }

    private static OrgSync getOrgSyncByOrg(List<OrgSync> listOfOrgSync, Org org) {
        if(CollectionUtils.isEmpty(listOfOrgSync)){
            return null;
        }
        for(OrgSync orgSync : listOfOrgSync){
            if(orgSync.getIdOfOrg().equals(org.getIdOfOrg())){
                return orgSync;
            }
        }
        return null;
    }

    @Override
    public Logger getLogger(){
        return logger;
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new OrgSettingsReport();
    }

    @Override
    public OrgSettingsReport.Builder createBuilder(String templateFilename) {
        return new OrgSettingsReport.Builder(templateFilename);
    }
}
