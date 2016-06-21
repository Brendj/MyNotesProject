/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.persistence.Org;
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
    final private Session session;

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
            if(!MigrantsUtils.getLastResolutionForMigrant(session, migrant).equals(1)){
                continue;
            }
            MigrantItem migrantItem = new MigrantItem(migrant, startTime, endTime, isOutcome);
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

    public static class MigrantItem implements Comparable<MigrantItem> {
        private Long idOfOrg;
        private String orgShortName;
        private String orgAddress;
        private Long contractId;
        private String name;
        private String groupName;
        private String startDate;
        private Boolean gtStartDate;
        private String endDate;
        private Boolean gtEndDate;
        private Long idOfOrg2;
        private String orgShortName2;
        private String orgAddress2;

        public MigrantItem(Migrant migrant, Date startTime, Date endTime, boolean isOutcome) {
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
            contractId = migrant.getClientMigrate().getContractId();
            name = migrant.getClientMigrate().getPerson().getFullName();
            groupName = migrant.getClientMigrate().getClientGroup().getGroupName();
            startDate = CalendarUtils.dateShortToStringFullYear(migrant.getVisitStartDate());
            gtStartDate = startTime.after(migrant.getVisitStartDate());
            endDate = CalendarUtils.dateShortToStringFullYear(migrant.getVisitEndDate());
            gtEndDate = migrant.getVisitEndDate().after(endTime);
            idOfOrg2 = org2.getIdOfOrg();
            orgShortName2 = org2.getShortName();
            orgAddress2 = org2.getAddress();
        }

        @Override
        public int compareTo(MigrantItem o) {
            int result = idOfOrg.compareTo(o.getIdOfOrg());
            if(result == 0){
                result = idOfOrg2.compareTo(o.getIdOfOrg2());
            }
            if(result == 0) {
                if (groupName.contains("-") && o.getGroupName().contains("-")) {
                    result = groupName.split("-")[0].compareTo(o.getGroupName().split("-")[0]);
                    if (result == 0) {
                        result = groupName.split("-")[1].compareTo(o.getGroupName().split("-")[1]);
                    }
                } else {
                    result = groupName.compareTo(o.getName());
                }
            }
            if(result == 0){
                result = name.compareTo(o.getName());
            }
            return result;
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

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public Boolean getGtStartDate() {
            return gtStartDate;
        }

        public void setGtStartDate(Boolean gtStartDate) {
            this.gtStartDate = gtStartDate;
        }

        public Boolean getGtEndDate() {
            return gtEndDate;
        }

        public void setGtEndDate(Boolean gtEndDate) {
            this.gtEndDate = gtEndDate;
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
    }

}
