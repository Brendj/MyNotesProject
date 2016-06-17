/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Migrant;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;

import java.util.*;

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
            ReportItem reportItem = new ReportItem(new ArrayList<OrgItem>(), new ArrayList<OrgItem>());
            reportItem.getOutcomeList().addAll(buildOutcomeOrgItems(startTime, endTime, idOfOrgList));
            reportItem.getIncomeList().addAll(buildIncomeOrgItems(startTime, endTime, idOfOrgList));
            reportItemList.add(reportItem);
        }
        if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.OUTCOME.getName())) {
            ReportItem reportItem = new ReportItem(new ArrayList<OrgItem>(), new ArrayList<OrgItem>());
            reportItem.getOutcomeList().addAll(buildOutcomeOrgItems(startTime, endTime, idOfOrgList));
            reportItemList.add(reportItem);
        }
        if (migrantsTypes.equals(MigrantsUtils.MigrantsEnumType.INCOME.getName())){
            ReportItem reportItem = new ReportItem(new ArrayList<OrgItem>(), new ArrayList<OrgItem>());
            reportItem.getIncomeList().addAll(buildIncomeOrgItems(startTime, endTime, idOfOrgList));
            reportItemList.add(reportItem);
        }
        return reportItemList;
    }

    private List<OrgItem> buildOutcomeOrgItems(Date startTime, Date endTime, List<Long> idOfOrgList){
        Map<Long,OrgItem> items = new HashMap<Long, OrgItem>();
        List<Migrant> outcomeMigrants = MigrantsUtils.getOutcomeMigrantsForOrgsByDate(session, idOfOrgList, startTime, endTime);
        for(Migrant migrant : outcomeMigrants){
            if(!MigrantsUtils.getLastResolutionForMigrant(session, migrant).equals(1)){
                continue;
            }
            Long idOfOrg = migrant.getOrgRegistry().getIdOfOrg();
            Date startDate = migrant.getVisitStartDate();
            Date endDate = migrant.getVisitEndDate();
            MigrantItem migrantItem = new MigrantItem(migrant.getClientMigrate().getContractId(),
                    migrant.getClientMigrate().getPerson().getFullName(), migrant.getClientMigrate().getClientGroup().getGroupName(),
                    CalendarUtils.dateShortToStringFullYear(startDate), startTime.after(startDate),
                    CalendarUtils.dateShortToStringFullYear(endDate), endDate.after(endTime),
                    migrant.getOrgVisit().getIdOfOrg(), migrant.getOrgVisit().getShortName(),
                    migrant.getOrgVisit().getAddress());
            if(items.get(idOfOrg) != null){
                items.get(idOfOrg).getMigrantItems().add(migrantItem);
            } else {
                items.put(idOfOrg, new OrgItem(idOfOrg, migrant.getOrgRegistry().getShortName(),
                        migrant.getOrgRegistry().getAddress(), new ArrayList<MigrantItem>(Arrays.asList(migrantItem))));
            }
        }
        List<OrgItem> result = new ArrayList<OrgItem>(items.values());
        Collections.sort(result);
        for(OrgItem o : result){
            Collections.sort(o.getMigrantItems());
        }
        return result;
    }

    private List<OrgItem> buildIncomeOrgItems(Date startTime, Date endTime, List<Long> idOfOrgList){
        Map<Long,OrgItem> items = new HashMap<Long, OrgItem>();
        List<Migrant> incomeMigrants = MigrantsUtils.getIncomeMigrantsForOrgsByDate(session, idOfOrgList, startTime, endTime);
        for(Migrant migrant : incomeMigrants){
            if(!MigrantsUtils.getLastResolutionForMigrant(session, migrant).equals(1)){
                continue;
            }
            Long idOfOrg = migrant.getOrgVisit().getIdOfOrg();
            Date startDate = migrant.getVisitStartDate();
            Date endDate = migrant.getVisitEndDate();
            MigrantItem migrantItem = new MigrantItem(migrant.getClientMigrate().getContractId(),
                    migrant.getClientMigrate().getPerson().getFullName(), migrant.getClientMigrate().getClientGroup().getGroupName(),
                    CalendarUtils.dateShortToStringFullYear(startDate), startTime.after(startDate),
                    CalendarUtils.dateShortToStringFullYear(endDate), endDate.after(endTime),
                    migrant.getOrgRegistry().getIdOfOrg(), migrant.getOrgRegistry().getShortName(),
                    migrant.getOrgRegistry().getAddress());
            if(items.get(idOfOrg) != null){
                items.get(idOfOrg).getMigrantItems().add(migrantItem);
            } else {
                items.put(idOfOrg, new OrgItem(idOfOrg, migrant.getOrgVisit().getShortName(),
                        migrant.getOrgVisit().getAddress(), new ArrayList<MigrantItem>(Arrays.asList(migrantItem))));
            }
        }
        List<OrgItem> result = new ArrayList<OrgItem>(items.values());
        Collections.sort(result);
        for(OrgItem o : result){
            Collections.sort(o.getMigrantItems());
        }
        return result;
    }

    public static class ReportItem {
        private List<OrgItem> outcomeList;
        private List<OrgItem> incomeList;

        public ReportItem(List<OrgItem> outcomeList, List<OrgItem> incomeList) {
            this.outcomeList = outcomeList;
            this.incomeList = incomeList;
        }

        public List<OrgItem> getOutcomeList() {
            return outcomeList;
        }

        public void setOutcomeList(List<OrgItem> outcomeList) {
            this.outcomeList = outcomeList;
        }

        public List<OrgItem> getIncomeList() {
            return incomeList;
        }

        public void setIncomeList(List<OrgItem> incomeList) {
            this.incomeList = incomeList;
        }
    }

    public static class OrgItem implements Comparable<OrgItem> {
        private Long id;
        private String shortName;
        private String address;
        private List<MigrantItem> migrantItems;

        public OrgItem(Long id, String shortName, String address, List<MigrantItem> migrantItems) {
            this.id = id;
            this.shortName = shortName;
            this.address = address;
            this.migrantItems = migrantItems;
        }

        @Override
        public int compareTo(OrgItem o) {
            return id.compareTo(o.getId());
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<MigrantItem> getMigrantItems() {
            return migrantItems;
        }

        public void setMigrantItems(List<MigrantItem> migrantItems) {
            this.migrantItems = migrantItems;
        }
    }

    public static class MigrantItem implements Comparable<MigrantItem> {
        private Long contractId;
        private String name;
        private String groupName;
        private String startDate;
        private Boolean gtStartDate;
        private String endDate;
        private Boolean gtEndDate;
        private Long idOfOrg;
        private String orgShortName;
        private String orgAddress;

        public MigrantItem(Long contractId, String name, String groupName, String startDate, Boolean gtStartDate,
                String endDate, Boolean gtEndDate, Long idOfOrg, String orgShortName, String orgAddress) {
            this.contractId = contractId;
            this.name = name;
            this.groupName = groupName;
            this.startDate = startDate;
            this.gtStartDate = gtStartDate;
            this.endDate = endDate;
            this.gtEndDate = gtEndDate;
            this.idOfOrg = idOfOrg;
            this.orgShortName = orgShortName;
            this.orgAddress = orgAddress;
        }

        @Override
        public int compareTo(MigrantItem o) {
            int result = idOfOrg.compareTo(o.getIdOfOrg());
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
    }

}
