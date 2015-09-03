/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.report.statistics.sfk.LatePaymentReportModel;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.08.15
 * Time: 13:39
 */

public class LatePaymentReportService {

    private static final Logger logger = LoggerFactory.getLogger(LatePaymentReportService.class);

    public LatePaymentReportService() {
    }

    // Запрос подсчета количества льготников по организации и подсчет дней-порций (Не своевременной оплаты)
    public static List<LatePaymentReportModel> getCountOfBeneficiariesByOrg(Session session, List<Long> idOfOrgList,
            Date startDate, Date endDate) {

        List<LatePaymentReportModel> latePaymentReportModels = new ArrayList<LatePaymentReportModel>();
        LatePaymentReportModel latePaymentReportModel;

        Query query = session.createSQLQuery(
                "SELECT row_number() OVER (ORDER BY o.idoforg, cfo.shortname, cfo.address) num,"
                        + " substring(cfo.shortname FROM 'Y*([0-9-]+)') orgname, " + "cfo.address, "
                        + "count(DISTINCT cl.idOfClient)benefitcount, "
                        + "count(DISTINCT cast(to_timestamp(o.createddate / 1000) AS DATE)) daycount, "
                        + "count(DISTINCT o.idoforder) feedcount " + "FROM cf_orders o "
                        + "INNER JOIN cf_clients cl ON cl.idoforg = o.idoforg "
                        + "INNER JOIN CF_Clients_CategoryDiscounts cc ON cc.idofclient = cl.idofclient "
                        + "INNER JOIN cf_orgs cfo ON cfo.idoforg = o.idoforg "
                        + "WHERE cast(to_timestamp(o.createddate / 1000)AS DATE) <> cast(to_timestamp(o.orderdate / 1000)AS DATE)"
                        + "AND o.ordertype = 4 AND o.state = 0 AND o.createddate "
                        + "BETWEEN :startDate AND :endDate "
                        + "AND cc.idOfCategoryDiscount IN (2, 5, 3, 4, 20, 1, 104, 105, 106, 108, 112, 121, 122, 123, 124) "
                        + "AND cl.idofclientgroup < 1100000000 " + "AND cl.idoforg IN (:idOfOrgList)"
                        + "GROUP BY o.idoforg, cfo.shortname, cfo.address "
                        + "ORDER BY o.idoforg, cfo.shortname, cfo.address");
        query.setParameterList("idOfOrgList", idOfOrgList);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());

        Object[] result = (Object[]) query.uniqueResult();

        Long rowNum;
        String orgName;
        String address;
        Long benefitCount;
        Long dayCount;
        Long feedCount;

        rowNum = ((BigInteger) result[0]).longValue();
        orgName = (String) result[1];
        address = (String) result[2];
        benefitCount = ((BigInteger) result[3]).longValue();
        dayCount = ((BigInteger) result[4]).longValue();
        feedCount = ((BigInteger) result[5]).longValue();

        latePaymentReportModel = new LatePaymentReportModel(rowNum, orgName, address, benefitCount, dayCount, feedCount);

        latePaymentReportModels.add(latePaymentReportModel);

        return latePaymentReportModels;
    }
}
