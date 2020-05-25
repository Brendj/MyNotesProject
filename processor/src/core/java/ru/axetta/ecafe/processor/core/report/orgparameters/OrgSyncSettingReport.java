/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orgparameters;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ContentType;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSetting;
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

public class OrgSyncSettingReport extends BasicReportForListOrgsJob {
    private static final Logger logger = LoggerFactory.getLogger(OrgSyncSettingReport.class);
    private static OrgSyncSettingReport instance;
    private final Builder builder = createBuilder(null);

    private static final List<ContentType> validContentType = Arrays.asList(
            ContentType.FULL_SYNC,
            ContentType.BALANCES_AND_ENTEREVENTS,
            ContentType.ORGSETTINGS,
            ContentType.CLIENTS_DATA,
            ContentType.MENU,
            ContentType.PHOTOS,
            ContentType.SUPPORT_SERVICE,
            ContentType.LIBRARY
    );
    public static final Integer ALL_TYPES = -1;

    @Override
    public BasicReportForAllOrgJob createInstance() {
        if(instance == null){
            instance = new OrgSyncSettingReport();
        }
        return instance;
    }

    public static OrgSyncSettingReport getInstance() {
        if(instance == null){
            instance = new OrgSyncSettingReport();
        }
        return instance;
    }

    private OrgSyncSettingReport(){

    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return templateFilename == null ? new Builder() : new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getTemplateFilename() {
        return templateFilename;
    }

    @Override
    public void setTemplateFilename(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    public Builder getBuilder() {
        return builder;
    }

    public static class Builder extends BasicReportJob.Builder {
        private String templateFilename;

        public static final String LIST_OF_ORG_IDS_PARAM = "idOfOrgList";
        public static final String SELECTED_DISTRICT_PARAM = "selectedDistrict";
        public static final String SELECTED_CONTENT_TYPE = "selectedContentType";
        public static final String ALL_FRIENDLY_ORGS = "allFriendlyOrgs";

        public Builder(String templateFilename){
            this.templateFilename = templateFilename;
        }

        public Builder(){
            this.templateFilename = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath()
                    + OrgSyncSettingReport.class.getSimpleName() + ".jasper";
        }

        public List<OrgSyncSettingReportItem> buildOrgSettingCollection(List<Long> idOfOrgList, Session persistenceSession,
                String selectedDistricts, Boolean allFriendlyOrgs, Integer selectedContentType) throws Exception {
            List<OrgSyncSettingReportItem> result = new ArrayList<>(idOfOrgList.size());

            Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
            if(!CollectionUtils.isEmpty(idOfOrgList)) {
                if(allFriendlyOrgs){
                    SQLQuery subQuery = persistenceSession.createSQLQuery("select friendlyOrg from cf_friendly_organization where currentOrg in (:idOfOrgs)");
                    subQuery.addScalar("friendlyOrg", LongType.INSTANCE);
                    subQuery.setParameterList("idOfOrgs", idOfOrgList);

                    List<Long> idsOfFriendlyOrg = subQuery.list();
                    orgCriteria.add(Restrictions.in("idOfOrg", idsOfFriendlyOrg));
                } else {
                    orgCriteria.add(Restrictions.in("idOfOrg", idOfOrgList));
                }
            }

            if(StringUtils.isNotEmpty(selectedDistricts)){
                orgCriteria.add(Restrictions.like("district", selectedDistricts));
            }
            orgCriteria.addOrder(Order.asc("shortName"));
            List<Org> orgs = orgCriteria.list();
            if(orgs.isEmpty()){
                return Collections.emptyList();
            }

            Criteria syncSettingCriteria  = persistenceSession.createCriteria(SyncSetting.class);
            syncSettingCriteria.createAlias("org", "org");
            if(!CollectionUtils.isEmpty(orgs)) {
                for (Org org : orgs) {
                    idOfOrgList.add(org.getIdOfOrg());
                }
            }
            if(selectedContentType.equals(ALL_TYPES)) {
                syncSettingCriteria.add(Restrictions.in("contentType", validContentType));
            } else {
                syncSettingCriteria.add(Restrictions.eq("contentType", ContentType.getContentTypeByCode(selectedContentType)));
            }
            syncSettingCriteria.add(Restrictions.in("org.idOfOrg", idOfOrgList));
            List<SyncSetting> settings = syncSettingCriteria.list();

            for(Org org : orgs){
                List<SyncSetting> settingsOfOrg = getListOfSettingsByOrg(settings, org);
                OrgSyncSettingReportItem item = new OrgSyncSettingReportItem(org, settingsOfOrg);
                result.add(item);
            }
            return result;
        }

        private List<SyncSetting> getListOfSettingsByOrg(List<SyncSetting> settings, Org org) {
            List<SyncSetting> result = new LinkedList<>();
            for(SyncSetting setting : settings){
                if(setting.getOrg().equals(org)){
                    result.add(setting);
                }
            }
            return result;
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
            Boolean allFriendlyOrgs = Boolean.parseBoolean(getReportProperties().getProperty(ALL_FRIENDLY_ORGS));
            Integer selectedContentType = Integer.valueOf(getReportProperties().getProperty(SELECTED_CONTENT_TYPE));

            parameterMap.put("startDate", CalendarUtils.dateShortToStringFullYear(startTime));

            JRDataSource dataSource = createDataSource(session, selectedDistrict, selectedContentType,
                    idOfOrgList, allFriendlyOrgs);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();
            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new OrgSettingsReport(generateEndTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, String selectedDistrict,
                Integer selectedContentType, List<Long> idOfOrgList, Boolean allFriendlyOrg) throws Exception {
            return new JRBeanCollectionDataSource(
                    buildOrgSettingCollection(idOfOrgList, session, selectedDistrict, allFriendlyOrg, selectedContentType)
            );
        }
    }
}