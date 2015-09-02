/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.report.statistics.sfk.LatePaymentReportModel;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Set;

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

    // Запрос подсчета количества льготников по дружественным организациям
    public Long getCountOfBeneficiariesByFriendlyOrgs(Set<Org> friendlyOrganizationsSet) {
        return null;
    }

    // Запрос подсчета количества льготников по организации и подсчет дней-порций (Не своевременной оплаты)
    public static LatePaymentReportModel getCountOfBeneficiariesByOrg(Session session, Long idOfOrg) {

        LatePaymentReportModel latePaymentReportModel;

        Query query = session.createSQLQuery(
                "SELECT row_number() OVER(ORDER BY cl.idoforg, cfo.shortname, cfo.address) AS num,"
                        + " substring(cfo.shortname FROM '\\d+') AS orgname, cfo.address, "
                        + " count(cl.idOfClient) AS benefitcount FROM CF_Clients_CategoryDiscounts cc, cf_clients cl "
                        + " LEFT JOIN cf_orgs  cfo ON cfo.idoforg = cl.idoforg "
                        + " WHERE cl.idOfClient = cc.idOfClient AND idOfCategoryDiscount>=0 AND cl.idoforg = :idOfOrg "
                        + " GROUP BY cl.idoforg, cfo.shortname, cfo.address "
                        + " ORDER BY cl.idoforg, cfo.shortname, cfo.address");
        query.setParameter("idOfOrg", idOfOrg);

        Object[] result = (Object[]) query.uniqueResult();

        Long rowNum;
        String orgName;
        String address;
        Long benefitCount;

        rowNum = ((BigInteger) result[0]).longValue();

        if (result[1] != null) {
            orgName = (String) result[1];
        } else {
            orgName = "";
        }

        address = (String) result[2];

        benefitCount = ((BigInteger) result[3]).longValue();

        latePaymentReportModel = new LatePaymentReportModel(rowNum, orgName, address, benefitCount, 0L, 0L);

        return latePaymentReportModel;
    }
}
