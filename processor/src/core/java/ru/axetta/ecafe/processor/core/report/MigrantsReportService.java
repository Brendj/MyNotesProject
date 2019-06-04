/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.06.16
 * Time: 12:08
 */

public class MigrantsReportService {
    private final Session session;
    private static final String NO_DATA = "-";

    private static final String[] groupNamesInOrgVisit = {"Обучающиеся других ОО",              //0
                                                          "Родители обучающихся других ОО",     //1
                                                          "Сотрудники других ОО",               //2
                                                          "Выбывшие",                           //3
                                                          "Неизвестно"};                        //4

    public MigrantsReportService(Session session) {
        this.session = session;
    }

    public List<ReportItem> buildReportItems(Date startTime, Date endTime, String migrantsTypes, List<Long> idOfOrgList,
            Boolean showAllMigrants, Integer periodType){
        List<ReportItem> reportItemList = new ArrayList<ReportItem>();
        ReportItem reportItem = new ReportItem(new ArrayList<MigrantItem>(), new ArrayList<MigrantItem>());
        if(showAllMigrants || periodType.equals(MigrantsReport.PERIOD_TYPE_VISIT)) {
            if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.ALL.getName())) {
                reportItem.getOutcomeList().addAll(buildMigrantItems(startTime, endTime,
                        buildOutcomeMigrants(session, idOfOrgList, startTime, endTime, showAllMigrants),
                        true));
                reportItem.getIncomeList().addAll(buildMigrantItems(startTime, endTime,
                        buildIncomeMigrants(session, idOfOrgList, startTime, endTime, showAllMigrants),
                        false));
                reportItemList.add(reportItem);
            } else if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.OUTCOME.getName())) {
                reportItem.getOutcomeList().addAll(buildMigrantItems(startTime, endTime,
                        buildOutcomeMigrants(session, idOfOrgList, startTime, endTime, showAllMigrants),
                        true));
                reportItemList.add(reportItem);
            } else if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.INCOME.getName())) {
                reportItem.getIncomeList().addAll(buildMigrantItems(startTime, endTime,
                        buildIncomeMigrants(session, idOfOrgList, startTime, endTime, showAllMigrants),
                        false));
                reportItemList.add(reportItem);
            }
        } else if(periodType.equals(MigrantsReport.PERIOD_TYPE_CHANGED)){
            if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.ALL.getName())) {
                reportItem.getOutcomeList().addAll(getOutcomeMigrantsForOrgsByResolutionDate(session, idOfOrgList, startTime, endTime));
                reportItem.getIncomeList().addAll(getIncomeMigrantsForOrgsByResolutionDate(session, idOfOrgList, startTime, endTime));
                reportItemList.add(reportItem);
            } else if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.OUTCOME.getName())) {
                reportItem.getOutcomeList().addAll(getOutcomeMigrantsForOrgsByResolutionDate(session, idOfOrgList, startTime, endTime));
                reportItemList.add(reportItem);
            } else if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.INCOME.getName())) {
                reportItem.getIncomeList().addAll(getIncomeMigrantsForOrgsByResolutionDate(session, idOfOrgList, startTime, endTime));
                reportItemList.add(reportItem);
            }
        }
        return reportItemList;
    }

    private List<MigrantItem> getIncomeMigrantsForOrgsByResolutionDate(Session session, List<Long> idOfOrgList, Date startTime, Date endTime) {
        SQLQuery query = session.createSQLQuery(
                     "SELECT orgVisit.idoforg                                               AS idOfOrg,\n"
                        + "       orgVisit.shortname                                           AS orgShortName,\n"
                        + "       orgVisit.address                                             AS orgAddress,\n"
                        + "       orgReg.idoforg                                               AS idOfOrg2,\n"
                        + "       orgReg.shortname                                             AS orgShortName2,\n"
                        + "       orgReg.address                                               AS orgAddress2,\n"
                        + getQueryMainPart()
                        + "WHERE m.idoforgvisit IN (:idOfOrgs)\n"
                        + "  AND lastResol.resolutiondatetime BETWEEN :startDate AND :endDate");

        query
                .setParameter("resComferm", VisitReqResolutionHist.RES_CONFIRMED)
                .setParameter("clientStudentBegin", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue())
                .setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue())
                .setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue())
                .setParameter("startDate", startTime.getTime())
                .setParameter("endDate", endTime.getTime())
                .setParameterList("idOfOrgs", idOfOrgList);

        setScalarsForQuery(query);
        query.setResultTransformer(Transformers.aliasToBean(MigrantItem.class));

        return query.list();
    }

    private List<MigrantItem> getOutcomeMigrantsForOrgsByResolutionDate(Session session, List<Long> idOfOrgList, Date startTime, Date endTime) {
        SQLQuery query = session.createSQLQuery(
                "SELECT orgReg.idoforg                                              AS idOfOrg,\n"
                + "       orgReg.shortname                                             AS orgShortName,\n"
                + "       orgReg.address                                               AS orgAddress,\n"
                + "       orgVisit.idoforg                                             AS idOfOrg2,\n"
                + "       orgVisit.shortname                                           AS orgShortName2,\n"
                + "       orgVisit.address                                             AS orgAddress2,\n"
                + getQueryMainPart()
                + "WHERE m.idoforgregistry IN (:idOfOrgs)\n"
                + "  AND lastResol.resolutiondatetime BETWEEN :startDate AND :endDate");

        query
                .setParameter("resComferm", VisitReqResolutionHist.RES_CONFIRMED)
                .setParameter("clientStudentBegin", ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue())
                .setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue())
                .setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue())
                .setParameter("startDate", startTime.getTime())
                .setParameter("endDate", endTime.getTime())
                .setParameterList("idOfOrgs", idOfOrgList);

        setScalarsForQuery(query);
        query.setResultTransformer(Transformers.aliasToBean(MigrantItem.class));

        return query.list();
    }

    private String getQueryMainPart(){
        return  "       m.requestnumber                                              AS number,\n"
                + "       CASE\n"
                + "           WHEN firstResol.resolutionCause is null then '-'\n"
                + "           ELSE firstResol.resolutionCause\n"
                + "           END                                                      AS resolutionCause,\n"
                + "       CASE\n"
                + "           WHEN firstResol.contactinfo is null then '-'\n"
                + "           ELSE firstResol.contactinfo\n"
                + "           END                                                      AS contactInfo,\n"
                + "       c.contractid                                                 AS contractId,\n"
                + "       (p.surname || ' ' || p.firstname || ' ' || p.secondname)     AS name,\n"
                + ""
                + "       CASE\n"
                + "           WHEN cg.groupname is NULL then 'Неизвестно'\n"
                + "           ELSE cg.groupname\n"
                + "           END                                                      AS groupName,\n"
                + "       CASE\n"
                + "           WHEN lastResol.resolution = :resComferm THEN\n"
                + "               CASE\n"
                + "                   WHEN cg.idofclientgroup is NULL then 'Неизвестно'\n"
                + "                   WHEN cg.idofclientgroup BETWEEN :clientStudentBegin AND :employees - 1 THEN 'Обучающиеся других ОО'\n"
                + "                   WHEN cg.idofclientgroup = :parents THEN 'Родители'\n"
                + "                   WHEN cg.idofclientgroup BETWEEN :employees AND :parents - 1 THEN 'Сотрудники других ОО'\n"
                + "                   ELSE 'Неизвестно'\n"
                + "                   END\n"
                + "           WHEN v.resolution IS NOT NULL THEN 'Выбывшие'\n"
                + "           ELSE '-'\n"
                + "           END                                                      AS groupNameInOrgVisit,\n"
                + "       to_char(to_timestamp(m.visitstartdate / 1000), 'dd.MM.yyyy') AS startDate,\n"
                + "       (:startDate > m.visitstartdate)                              AS gtStartDate,\n"
                + "       to_char(to_timestamp(m.visitenddate / 1000), 'dd.MM.yyyy')   AS endDate,\n"
                + "       (m.visitenddate > :endDate)                                  AS gtEndDate,\n"
                + " CASE\n"
                + "           WHEN lastResol.resolution = 0 THEN 'Создана'\n"
                + "           WHEN lastResol.resolution = 1 THEN 'Подтверждена'\n"
                + "           WHEN lastResol.resolution = 2 THEN 'Отклонена и сдана в архив'\n"
                + "           WHEN lastResol.resolution = 3 THEN 'Аннулирована и сдана в архив'\n"
                + "           WHEN lastResol.resolution = 4 OR lastResol.resolution = 5 THEN 'Сдана в архив по истечению срока действия'\n"
                + "           END                                                      AS resolution "
                + "FROM cf_migrants AS m\n"
                + "         JOIN cf_orgs AS orgReg ON m.idoforgregistry = orgReg.idoforg\n"
                + "         JOIN cf_orgs AS orgVisit ON m.idoforgvisit = orgVisit.idoforg\n"
                + "         JOIN cf_clients AS c ON m.idofclientmigrate = c.idofclient\n"
                + "         JOIN cf_clientgroups AS cg ON c.idoforg = cg.idoforg AND c.idofclientgroup = cg.idofclientgroup\n"
                + "         JOIN cf_persons AS p ON c.idofperson = p.idofperson\n"
                + "         LEFT JOIN cf_visitreqresolutionhist AS v\n"
                + "                   ON m.idoforgregistry = v.idoforgregistry AND m.idofrequest = v.idofrequest and v.resolution = :resComferm\n"
                + "         JOIN (SELECT DISTINCT ON (idofrequest, idoforgregistry) *\n"
                + "               FROM cf_visitreqresolutionhist\n"
                + "               ORDER BY idofrequest, idoforgregistry, resolutiondatetime DESC) AS lastResol\n"
                + "              ON m.idofrequest = lastResol.idofrequest AND m.idoforgregistry = lastResol.idoforgregistry\n"
                + "         JOIN (SELECT DISTINCT ON (idofrequest, idoforgregistry) *\n"
                + "               FROM cf_visitreqresolutionhist\n"
                + "               ORDER BY idofrequest, idoforgregistry, resolutiondatetime) AS firstResol\n"
                + "              ON m.idofrequest = firstResol.idofrequest AND m.idoforgregistry = firstResol.idoforgregistry\n";
    }

    private void setScalarsForQuery(SQLQuery query) {
        query
                .addScalar("idOfOrg", LongType.INSTANCE)
                .addScalar("orgShortName", StringType.INSTANCE)
                .addScalar("orgAddress", StringType.INSTANCE)
                .addScalar("idOfOrg2", LongType.INSTANCE)
                .addScalar("orgShortName2", StringType.INSTANCE)
                .addScalar("orgAddress2", StringType.INSTANCE)
                .addScalar("number", StringType.INSTANCE)
                .addScalar("resolutionCause", StringType.INSTANCE)
                .addScalar("contactInfo", StringType.INSTANCE)
                .addScalar("contractId", LongType.INSTANCE)
                .addScalar("name", StringType.INSTANCE)
                .addScalar("groupName", StringType.INSTANCE)
                .addScalar("resolutionCause", StringType.INSTANCE)
                .addScalar("groupNameInOrgVisit", StringType.INSTANCE)
                .addScalar("startDate", StringType.INSTANCE)
                .addScalar("gtStartDate", BooleanType.INSTANCE)
                .addScalar("endDate", StringType.INSTANCE)
                .addScalar("gtEndDate", BooleanType.INSTANCE)
                .addScalar("resolution", StringType.INSTANCE);
    }

    private List<Migrant> buildOutcomeMigrants(Session session, List<Long> idOfOrgList, Date startTime, Date endTime, Boolean showAllMigrants) {
        List<Migrant> result;
        if(showAllMigrants) {
            result =  MigrantsUtils.getOutcomeMigrantsForOrgsWithoutDate(session, idOfOrgList);
        } else {
            result = MigrantsUtils.getOutcomeMigrantsForOrgsByDate(session, idOfOrgList, startTime, endTime);
        }
        return result;
    }

    private List<Migrant> buildIncomeMigrants(Session session, List<Long> idOfOrgList, Date startTime, Date endTime, Boolean showAllMigrants) {
        List<Migrant> result;
        if(showAllMigrants) {
            result =  MigrantsUtils.getIncomeMigrantsForOrgsWithoutDate(session, idOfOrgList);
        } else {
            result = MigrantsUtils.getIncomeMigrantsForOrgsByDate(session, idOfOrgList, startTime, endTime);
        }
        return result;
    }

    private List<MigrantItem> buildMigrantItems(Date startTime, Date endTime, List<Migrant> migrants, boolean isOutcome){
        List<MigrantItem> result = new ArrayList<MigrantItem>();
        for(Migrant migrant : migrants){
            MigrantItem migrantItem = new MigrantItem(migrant, startTime, endTime,
                    MigrantsUtils.getResolutionsForMigrant(session, migrant), isOutcome);
            result.add(migrantItem);
        }
        return result;
    }

    public static class ReportItem {
        private List<MigrantItem> outcomeList;
        private List<MigrantItem> incomeList;

        public ReportItem(List<MigrantItem> outcomeList, List<MigrantItem> incomeList) {
            this.outcomeList = outcomeList;
            this.incomeList = incomeList;
        }

        public List<MigrantItem> getOutcomeList() {
            return outcomeList;
        }

        public void setOutcomeList(List<MigrantItem> outcomeList) {
            this.outcomeList = outcomeList;
        }

        public List<MigrantItem> getIncomeList() {
            return incomeList;
        }

        public void setIncomeList(List<MigrantItem> incomeList) {
            this.incomeList = incomeList;
        }
    }

    public static class MigrantItem {
        private Long idOfOrg;
        private String orgShortName;
        private String orgAddress;
        private Long idOfOrg2;
        private String orgShortName2;
        private String orgAddress2;
        private String number;
        private String resolutionCause;
        private String contactInfo;
        private Long contractId;
        private String name;
        private String groupName;
        private String groupNameInOrgVisit;
        private String startDate;
        private Boolean gtStartDate;
        private String endDate;
        private Boolean gtEndDate;
        private String resolution;

        public MigrantItem(){

        }

        public MigrantItem(Migrant migrant, Date startTime, Date endTime,
                List<VisitReqResolutionHist> resolutions, boolean isOutcome) {
            Org org;
            Org org2;
            if(isOutcome){
                org = migrant.getOrgRegistry();
                org2 = migrant.getOrgVisit();
            } else {
                org = migrant.getOrgVisit();
                org2 = migrant.getOrgRegistry();
            }
            idOfOrg = org.getIdOfOrg();
            orgShortName = org.getShortName();
            orgAddress = org.getAddress();
            idOfOrg2 = org2.getIdOfOrg();
            orgShortName2 = org2.getShortName();
            orgAddress2 = org2.getAddress();
            number = migrant.getRequestNumber();
            if(!CollectionUtils.isEmpty(resolutions)) {
                resolutionCause =
                        resolutions.get(0).getResolutionCause() != null ? resolutions.get(0).getResolutionCause() : NO_DATA;
                contactInfo = resolutions.get(0).getContactInfo() != null ? resolutions.get(0).getContactInfo() : NO_DATA;
                resolution = MigrantsUtils.getResolutionString(resolutions.get(resolutions.size() - 1).getResolution());
            } else {
                resolutionCause = NO_DATA;
                contactInfo = NO_DATA;
                resolution = NO_DATA;
            }
            contractId = migrant.getClientMigrate().getContractId();
            name = migrant.getClientMigrate().getPerson().getFullName();
            groupName = getGroupNameByClient(migrant.getClientMigrate());
            groupNameInOrgVisit = getGroupNameForOrgVisitByClient(migrant.getClientMigrate(),
                    resolutions);
            startDate = CalendarUtils.dateShortToStringFullYear(migrant.getVisitStartDate());
            gtStartDate = startTime.after(migrant.getVisitStartDate());
            endDate = CalendarUtils.dateShortToStringFullYear(migrant.getVisitEndDate());
            gtEndDate = migrant.getVisitEndDate().after(endTime);
        }

        private static String getGroupNameForOrgVisitByClient(Client client, List<VisitReqResolutionHist> resolutions){
            if(CollectionUtils.isEmpty(resolutions)) return NO_DATA;
            List<Integer> resolInts = new ArrayList<Integer>();
            for(VisitReqResolutionHist v : resolutions){
                resolInts.add(v.getResolution());
            }
            if(resolInts.get(resolInts.size() - 1).equals(VisitReqResolutionHist.RES_CONFIRMED)){
                if(client.getClientGroup() != null) {
                    Long idOfClientGroup = client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup();
                    if (idOfClientGroup >= ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue()
                            && idOfClientGroup < ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()) {
                        return MigrantsReportService.groupNamesInOrgVisit[0];
                    }
                    if (idOfClientGroup.equals(ClientGroup.Predefined.CLIENT_PARENTS.getValue())) {
                        return MigrantsReportService.groupNamesInOrgVisit[1];
                    }
                    if (idOfClientGroup >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            && idOfClientGroup < ClientGroup.Predefined.CLIENT_PARENTS.getValue()) {
                        return MigrantsReportService.groupNamesInOrgVisit[2];
                    }
                }
                return MigrantsReportService.groupNamesInOrgVisit[4];
            }
            if(resolInts.contains(VisitReqResolutionHist.RES_CONFIRMED)){
                return MigrantsReportService.groupNamesInOrgVisit[3];
            }
            return NO_DATA;
        }

        private static String getGroupNameByClient(Client client){
            if(client.getClientGroup() != null) {
                return client.getClientGroup().getGroupName();
            }
            return MigrantsReportService.groupNamesInOrgVisit[4];
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getOrgShortName() {
            return orgShortName;
        }

        public void setOrgShortName(String orgShortName) {
            this.orgShortName = orgShortName;
        }

        public String getOrgAddress() {
            return orgAddress;
        }

        public void setOrgAddress(String orgAddress) {
            this.orgAddress = orgAddress;
        }

        public Long getIdOfOrg2() {
            return idOfOrg2;
        }

        public void setIdOfOrg2(Long idOfOrg2) {
            this.idOfOrg2 = idOfOrg2;
        }

        public String getOrgShortName2() {
            return orgShortName2;
        }

        public void setOrgShortName2(String orgShortName2) {
            this.orgShortName2 = orgShortName2;
        }

        public String getOrgAddress2() {
            return orgAddress2;
        }

        public void setOrgAddress2(String orgAddress2) {
            this.orgAddress2 = orgAddress2;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getResolutionCause() {
            return resolutionCause;
        }

        public void setResolutionCause(String resolutionCause) {
            this.resolutionCause = resolutionCause;
        }

        public String getResolution() {
            return resolution;
        }

        public void setResolution(String resolution) {
            this.resolution = resolution;
        }

        public String getContactInfo() {
            return contactInfo;
        }

        public void setContactInfo(String contactInfo) {
            this.contactInfo = contactInfo;
        }

        public Long getContractId() {
            return contractId;
        }

        public void setContractId(Long contractId) {
            this.contractId = contractId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getGroupNameInOrgVisit() {
            return groupNameInOrgVisit;
        }

        public void setGroupNameInOrgVisit(String groupNameInOrgVisit) {
            this.groupNameInOrgVisit = groupNameInOrgVisit;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public Boolean getGtStartDate() {
            return gtStartDate;
        }

        public void setGtStartDate(Boolean gtStartDate) {
            this.gtStartDate = gtStartDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Boolean getGtEndDate() {
            return gtEndDate;
        }

        public void setGtEndDate(Boolean gtEndDate) {
            this.gtEndDate = gtEndDate;
        }
    }

}
