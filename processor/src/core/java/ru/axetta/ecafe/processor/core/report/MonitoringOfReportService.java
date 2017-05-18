/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by almaz anvarov on 04.05.2017.
 */
public class MonitoringOfReportService {

    public List<ReportItem> buildReportItems(Session session, Date startTime, List<Long> idOfOrgList) {
        List<ReportItem> reportItemList = getOrgData(session, idOfOrgList);

        return reportItemList;
    }

    public List<ReportItem> getOrgData(Session session, List<Long> idOfOrgList) {
        List<ReportItem> reportItemList = new ArrayList<ReportItem>();

        for (Long idOfOrg : idOfOrgList) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            ReportItem reportItem = new ReportItem();
            reportItem.setOrgNum(org.getOrgNumberInName());
            reportItem.setShortName(org.getShortName());
            reportItem.setAddress(org.getAddress());
            reportItem.setIdOfOrg(String.valueOf(org.getIdOfOrg()));
            reportItem.setCode(String.valueOf(org.getUniqueAddressId()));
            reportItem.setDistrict(org.getDistrict());
            reportItem.setTypeOfBuilding(org.getType().toString());
            reportItem.setIntroductionQueue(org.getIntroductionQueue());

            reportItem.setStudentsInDatabase(allPeoples(org, session));
            reportItem.setStudentsWithMaps(studentsWithMaps(org, session));
            reportItem.setParents(parents(org, session));
            reportItem.setPedagogicalComposition(pedagogicalComposition(org, session));
            reportItem.setOtherEmployees(otherEmloyees(org, session));

            reportItemList.add(reportItem);
        }
        return reportItemList;
    }

    public String parents(Org org, Session session) {

        Query query = session.createSQLQuery(
                "SELECT count(DISTINCT(cfc.idofclient)) FROM cf_clients cfc WHERE cfc.idoforg = :idoforg AND cfc.IdOfClientGroup = 1100000030");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String pedagogicalComposition(Org org, Session session) {

        Query query = session.createSQLQuery(
                "SELECT count(DISTINCT(cfc.idofclient)) FROM cf_clients cfc "
                        + "WHERE cfc.idoforg = :idoforg AND cfc.IdOfClientGroup in (1100000000, 1100000010)");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String allPeoples(Org org, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT(cfc.idofclient)) "
                + "FROM cf_clients cfc WHERE cfc.idoforg = :idoforg AND cfc.idofclientgroup < 1100000000");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String otherEmloyees(Org org, Session session) {
        Query query = session.createSQLQuery(
                "SELECT count(DISTINCT(cfc.idofclient)) FROM cf_clients cfc "
                        + "WHERE cfc.idoforg = :idoforg AND cfc.idofclientgroup IN (1100000050, 1100000020, 1100000040)");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String studentsWithMaps(Org org, Session session) {
        Query query = session.createSQLQuery(
                "SELECT count(DISTINCT(cfca.idofclient ))  FROM cf_clients cfc LEFT JOIN cf_cards cfca ON cfc.idofclient = cfca.idofclient "
                        + " WHERE cfc.idoforg = :idoforg AND cfc.idofclientgroup < 1100000000");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public static class ReportItem {

        public String orgNum;
        public String shortName;
        public String address;
        public String idOfOrg;
        public String code;
        public String district;
        public String typeOfBuilding;
        public String introductionQueue;
        public String studentsInDatabase;
        public String studentsWithMaps;
        public String parents;
        public String pedagogicalComposition;
        public String otherEmployees;

        public MonitoringOfItem monitoringOfItems;

        public ReportItem() {
        }

        public ReportItem(String orgNum, String shortName, String address, String idOfOrg, String code, String district,
                String typeOfBuilding, String introductionQueue, String studentsInDatabase, String studentsWithMaps,
                String parents, String pedagogicalComposition, String otherEmployees,
                MonitoringOfItem monitoringOfItems) {
            this.orgNum = orgNum;
            this.shortName = shortName;
            this.address = address;
            this.idOfOrg = idOfOrg;
            this.code = code;
            this.district = district;
            this.typeOfBuilding = typeOfBuilding;
            this.introductionQueue = introductionQueue;
            this.studentsInDatabase = studentsInDatabase;
            this.studentsWithMaps = studentsWithMaps;
            this.parents = parents;
            this.pedagogicalComposition = pedagogicalComposition;
            this.otherEmployees = otherEmployees;
            this.monitoringOfItems = monitoringOfItems;
        }

        public String getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(String orgNum) {
            this.orgNum = orgNum;
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

        public String getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(String idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getTypeOfBuilding() {
            return typeOfBuilding;
        }

        public void setTypeOfBuilding(String typeOfBuilding) {
            this.typeOfBuilding = typeOfBuilding;
        }

        public String getIntroductionQueue() {
            return introductionQueue;
        }

        public void setIntroductionQueue(String introductionQueue) {
            this.introductionQueue = introductionQueue;
        }

        public String getStudentsInDatabase() {
            return studentsInDatabase;
        }

        public void setStudentsInDatabase(String studentsInDatabase) {
            this.studentsInDatabase = studentsInDatabase;
        }

        public String getStudentsWithMaps() {
            return studentsWithMaps;
        }

        public void setStudentsWithMaps(String studentsWithMaps) {
            this.studentsWithMaps = studentsWithMaps;
        }

        public String getParents() {
            return parents;
        }

        public void setParents(String parents) {
            this.parents = parents;
        }

        public String getPedagogicalComposition() {
            return pedagogicalComposition;
        }

        public void setPedagogicalComposition(String pedagogicalComposition) {
            this.pedagogicalComposition = pedagogicalComposition;
        }

        public String getOtherEmployees() {
            return otherEmployees;
        }

        public void setOtherEmployees(String otherEmployees) {
            this.otherEmployees = otherEmployees;
        }

        public MonitoringOfItem getMonitoringOfItems() {
            return monitoringOfItems;
        }

        public void setMonitoringOfItems(MonitoringOfItem monitoringOfItems) {
            this.monitoringOfItems = monitoringOfItems;
        }
    }

    public static class MonitoringOfItem {

        private Date sDate;
        private Long numberOfPassesStudents;
        private Long numberOfPassesEmployees;
        private Long numberOfPassesGuardians;
        private Long summaryOfPasses;

        private Long numberOfLgotnoe;
        private Long numberOfReserve;
        private Long numberOfBuffetStudent;
        private Long numberOfBuffetGuardians;
        private Long numberOfSubFeedStudents;
        private Long numberOfSubFeedGuardians;
        private Long numberOfPaidStudents;
        private Long numberOfPaidGuardians;

        public MonitoringOfItem() {
        }

        public MonitoringOfItem(Date sDate, Long numberOfPassesStudents, Long numberOfPassesEmployees,
                Long numberOfPassesGuardians, Long summaryOfPasses, Long numberOfLgotnoe, Long numberOfReserve,
                Long numberOfBuffetStudent, Long numberOfBuffetGuardians, Long numberOfSubFeedStudents,
                Long numberOfSubFeedGuardians, Long numberOfPaidStudents, Long numberOfPaidGuardians) {
            this.sDate = sDate;
            this.numberOfPassesStudents = numberOfPassesStudents;
            this.numberOfPassesEmployees = numberOfPassesEmployees;
            this.numberOfPassesGuardians = numberOfPassesGuardians;
            this.summaryOfPasses = summaryOfPasses;
            this.numberOfLgotnoe = numberOfLgotnoe;
            this.numberOfReserve = numberOfReserve;
            this.numberOfBuffetStudent = numberOfBuffetStudent;
            this.numberOfBuffetGuardians = numberOfBuffetGuardians;
            this.numberOfSubFeedStudents = numberOfSubFeedStudents;
            this.numberOfSubFeedGuardians = numberOfSubFeedGuardians;
            this.numberOfPaidStudents = numberOfPaidStudents;
            this.numberOfPaidGuardians = numberOfPaidGuardians;
        }

        public Date getsDate() {
            return sDate;
        }

        public void setsDate(Date sDate) {
            this.sDate = sDate;
        }

        public Long getNumberOfPassesStudents() {
            return numberOfPassesStudents;
        }

        public void setNumberOfPassesStudents(Long numberOfPassesStudents) {
            this.numberOfPassesStudents = numberOfPassesStudents;
        }

        public Long getNumberOfPassesEmployees() {
            return numberOfPassesEmployees;
        }

        public void setNumberOfPassesEmployees(Long numberOfPassesEmployees) {
            this.numberOfPassesEmployees = numberOfPassesEmployees;
        }

        public Long getNumberOfPassesGuardians() {
            return numberOfPassesGuardians;
        }

        public void setNumberOfPassesGuardians(Long numberOfPassesGuardians) {
            this.numberOfPassesGuardians = numberOfPassesGuardians;
        }

        public Long getSummaryOfPasses() {
            return summaryOfPasses;
        }

        public void setSummaryOfPasses(Long summaryOfPasses) {
            this.summaryOfPasses = summaryOfPasses;
        }

        public Long getNumberOfLgotnoe() {
            return numberOfLgotnoe;
        }

        public void setNumberOfLgotnoe(Long numberOfLgotnoe) {
            this.numberOfLgotnoe = numberOfLgotnoe;
        }

        public Long getNumberOfReserve() {
            return numberOfReserve;
        }

        public void setNumberOfReserve(Long numberOfReserve) {
            this.numberOfReserve = numberOfReserve;
        }

        public Long getNumberOfBuffetStudent() {
            return numberOfBuffetStudent;
        }

        public void setNumberOfBuffetStudent(Long numberOfBuffetStudent) {
            this.numberOfBuffetStudent = numberOfBuffetStudent;
        }

        public Long getNumberOfBuffetGuardians() {
            return numberOfBuffetGuardians;
        }

        public void setNumberOfBuffetGuardians(Long numberOfBuffetGuardians) {
            this.numberOfBuffetGuardians = numberOfBuffetGuardians;
        }

        public Long getNumberOfSubFeedStudents() {
            return numberOfSubFeedStudents;
        }

        public void setNumberOfSubFeedStudents(Long numberOfSubFeedStudents) {
            this.numberOfSubFeedStudents = numberOfSubFeedStudents;
        }

        public Long getNumberOfSubFeedGuardians() {
            return numberOfSubFeedGuardians;
        }

        public void setNumberOfSubFeedGuardians(Long numberOfSubFeedGuardians) {
            this.numberOfSubFeedGuardians = numberOfSubFeedGuardians;
        }

        public Long getNumberOfPaidStudents() {
            return numberOfPaidStudents;
        }

        public void setNumberOfPaidStudents(Long numberOfPaidStudents) {
            this.numberOfPaidStudents = numberOfPaidStudents;
        }

        public Long getNumberOfPaidGuardians() {
            return numberOfPaidGuardians;
        }

        public void setNumberOfPaidGuardians(Long numberOfPaidGuardians) {
            this.numberOfPaidGuardians = numberOfPaidGuardians;
        }
    }
}
