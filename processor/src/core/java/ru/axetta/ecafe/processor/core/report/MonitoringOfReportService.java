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

    public List<ReportItem> buildReportItems(Session session, Date startTime, Date endTime, List<Long> idOfOrgList) {
        List<ReportItem> reportItemList = getOrgData(session, idOfOrgList);

        return reportItemList;
    }

    public List<ReportItem> getOrgData(Session session, List<Long> idOfOrgList) {
        List<ReportItem> reportItemList = new ArrayList<ReportItem>();

        for (Long idOfOrg: idOfOrgList) {
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
            reportItem.setStudentsWithMaps("");
            reportItem.setParents(parents(org, session));
            reportItem.setPedagogicalComposition(pedagogicalComposition(org, session));
            reportItem.setOtherEmployees(otherEmloyees(org, session));

            reportItemList.add(reportItem);
        }
        return reportItemList;
    }

    public String parents(Org org, Session session) {

        Query query = session.createSQLQuery("SELECT count(*) FROM cf_clients cfc LEFT JOIN cf_clientgroups cfcl ON cfc.idoforg = cfcl.idoforg "
                + "WHERE cfc.idoforg = :idoforg AND cfcl.groupname LIKE '%Родители%'");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String pedagogicalComposition(Org org, Session session) {

        Query query = session.createSQLQuery("SELECT count(*) FROM cf_clients cfc LEFT JOIN cf_clientgroups cfcl ON cfc.idoforg = cfcl.idoforg "
                + "WHERE cfc.idoforg = :idoforg AND (cfcl.groupname LIKE '%Пед. состав%' OR cfcl.groupname LIKE '%Администрация%') ");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String allPeoples(Org org, Session session) {
        Query query = session.createSQLQuery("SELECT count(*)"
                + "FROM cf_clients cfc LEFT JOIN cf_clientgroups cfcl ON cfc.idoforg = cfcl.idoforg "
                + "WHERE cfc.idoforg = :idoforg AND cfcl.idofclientgroup < 1100000000");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String otherEmloyees(Org org, Session session) {
        Query query = session.createSQLQuery("SELECT count(*)"
                        + "FROM cf_clients cfc LEFT JOIN cf_clientgroups cfcl ON cfc.idoforg = cfcl.idoforg "
                + "WHERE cfc.idoforg = :idoforg AND cfcl.idofclientgroup in (1100000050, 1100000020, 1100000040)");
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

        public ReportItem() {
        }

        public ReportItem(String orgNum, String shortName, String address, String idOfOrg, String code, String district,
                String typeOfBuilding, String introductionQueue, String studentsInDatabase, String studentsWithMaps,
                String parents, String pedagogicalComposition, String otherEmployees) {
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
    }

    public static class MonitoringOfItem {

    }
}
