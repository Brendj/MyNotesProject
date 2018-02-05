/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;

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

    public List<ReportItem> buildReportItems(Date startTime, Date endTime, String migrantsTypes, List<Long> idOfOrgList){
        List<ReportItem> reportItemList = new ArrayList<ReportItem>();
        if(migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.ALL.getName())){
            ReportItem reportItem = new ReportItem(new ArrayList<MigrantItem>(), new ArrayList<MigrantItem>());
            reportItem.getOutcomeList().addAll(buildMigrantItems(startTime, endTime,
                    MigrantsUtils.getOutcomeMigrantsForOrgsByDate(session, idOfOrgList, startTime, endTime), true));
            reportItem.getIncomeList().addAll(buildMigrantItems(startTime, endTime,
                    MigrantsUtils.getIncomeMigrantsForOrgsByDate(session, idOfOrgList, startTime, endTime), false));
            reportItemList.add(reportItem);
        }
        if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.OUTCOME.getName())) {
            ReportItem reportItem = new ReportItem(new ArrayList<MigrantItem>(), new ArrayList<MigrantItem>());
            reportItem.getOutcomeList().addAll(buildMigrantItems(startTime, endTime,
                    MigrantsUtils.getOutcomeMigrantsForOrgsByDate(session, idOfOrgList, startTime, endTime), true));
            reportItemList.add(reportItem);
        }
        if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.INCOME.getName())){
            ReportItem reportItem = new ReportItem(new ArrayList<MigrantItem>(), new ArrayList<MigrantItem>());
            reportItem.getIncomeList().addAll(buildMigrantItems(startTime, endTime,
                    MigrantsUtils.getIncomeMigrantsForOrgsByDate(session, idOfOrgList, startTime, endTime), false));
            reportItemList.add(reportItem);
        }
        return reportItemList;
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
            Long numberLong = migrant.getCompositeIdOfMigrant().getIdOfRequest()/10L;
            number = migrant.getRequestNumber();
            resolutionCause = resolutions.get(0).getResolutionCause() != null ? resolutions.get(0).getResolutionCause() : NO_DATA;
            contactInfo = resolutions.get(0).getContactInfo() != null ? resolutions.get(0).getContactInfo() : NO_DATA;
            contractId = migrant.getClientMigrate().getContractId();
            name = migrant.getClientMigrate().getPerson().getFullName();
            groupName = getGroupNameByClient(migrant.getClientMigrate());
            groupNameInOrgVisit = getGroupNameForOrgVisitByClient(migrant.getClientMigrate(),
                    resolutions);
            startDate = CalendarUtils.dateShortToStringFullYear(migrant.getVisitStartDate());
            gtStartDate = startTime.after(migrant.getVisitStartDate());
            endDate = CalendarUtils.dateShortToStringFullYear(migrant.getVisitEndDate());
            gtEndDate = migrant.getVisitEndDate().after(endTime);
            resolution = MigrantsUtils.getResolutionString(resolutions.get(resolutions.size() - 1).getResolution());
        }

        private static String getGroupNameForOrgVisitByClient(Client client, List<VisitReqResolutionHist> resolutions){
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
