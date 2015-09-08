/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.latepayment;

import ru.axetta.ecafe.processor.core.persistence.LatePaymentByOneDayCountType;
import ru.axetta.ecafe.processor.core.persistence.LatePaymentDaysCountType;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

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
            Date startDate, Date endDate, String latePaymentDaysCountType, String latePaymentByOneDayCountType) {

        List<LatePaymentReportModelByDays> latePaymentReportModelsByDaysList = new ArrayList<LatePaymentReportModelByDays>();

        List<LatePaymentReportModel> latePaymentReportModels = new ArrayList<LatePaymentReportModel>();

        LatePaymentReportModelByDays  latePaymentReportModelByDays;

        Query query = session.createSQLQuery("SELECT substring(cfo.shortname FROM 'Y*([0-9-]+)') orgname, cfo.address, "
                + "count(DISTINCT cl.idOfClient)benefitcount, "
                + "count(DISTINCT cast(to_timestamp(o.createddate / 1000) AS DATE)) daycount, "
                + "count(DISTINCT o.idoforder) feedcount, cast (to_timestamp(o.createddate/1000) AS DATE) "
                + "FROM cf_orders o INNER JOIN cf_clients cl ON cl.idoforg = o.idoforg "
                + "INNER JOIN CF_Clients_CategoryDiscounts cc ON cc.idofclient = cl.idofclient "
                + "INNER JOIN cf_orgs cfo ON cfo.idoforg = o.idoforg "
                + "WHERE cast(to_timestamp(o.createddate / 1000)AS DATE) <> cast(to_timestamp(o.orderdate / 1000)AS DATE)"
                + "AND o.ordertype = 4 AND o.state = 0 AND o.createddate BETWEEN :startDate AND :endDate "
                + "AND cc.idOfCategoryDiscount IN (2, 5, 3, 4, 20, 1, 104, 105, 106, 108, 112, 121, 122, 123, 124) "
                + "AND cl.idofclientgroup < 1100000000 AND cl.idoforg IN (:idOfOrgList)"
                + "GROUP BY o.idoforg, cfo.shortname, cfo.address, cast (to_timestamp(o.createddate/1000) AS DATE) "
                + "ORDER BY o.idoforg, cfo.shortname, cfo.address");
        query.setParameterList("idOfOrgList", idOfOrgList);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());

        List resultList = query.list();

        String orgName;
        String address;
        Long benefitCount;
        Long dayCount;
        Long feedCount;
        Date date;


        //Уникальные номера Организаций
        SortedSet<String> latePaymentReportModelSet = new TreeSet<String>();


        for (Object res : resultList) {

            Object[] result = (Object[]) res;

            orgName = (String) result[0];
            address = (String) result[1];
            benefitCount = ((BigInteger) result[2]).longValue();
            dayCount = ((BigInteger) result[3]).longValue();
            feedCount = ((BigInteger) result[4]).longValue();
            date = (Date) result[5];

            latePaymentReportModelByDays = new LatePaymentReportModelByDays(orgName, address, benefitCount, dayCount,
                    feedCount, date);

            if (LatePaymentByOneDayCountType.MORE_FIVE.toString().equals(latePaymentByOneDayCountType)) {
                if (latePaymentReportModelByDays.getFeedcount() > 5) {
                    latePaymentReportModelsByDaysList.add(latePaymentReportModelByDays);
                    latePaymentReportModelSet.add(latePaymentReportModelByDays.getOrgname());
                }
            } else {
                latePaymentReportModelsByDaysList.add(latePaymentReportModelByDays);
                latePaymentReportModelSet.add(latePaymentReportModelByDays.getOrgname());
            }
        }


        Long rowNum = 0L;
        for (String orgNum: latePaymentReportModelSet) {

            Long sumDayCount = 0L;
            Long sumFeedCount = 0L;

            rowNum++;

            LatePaymentReportModel latePaymentReportModel = null;

            for (LatePaymentReportModelByDays latePaymentReportModelByDay: latePaymentReportModelsByDaysList) {
                if (latePaymentReportModelByDay.getOrgname().equals(orgNum)) {

                    sumDayCount += latePaymentReportModelByDay.getDaycount();
                    sumFeedCount += latePaymentReportModelByDay.getFeedcount();

                    latePaymentReportModel = new LatePaymentReportModel(rowNum, latePaymentReportModelByDay.getOrgname(),
                            latePaymentReportModelByDay.getAddress(), latePaymentReportModelByDay.getBenefitcount(),
                            sumDayCount, sumFeedCount);
                }
            }

            if (LatePaymentDaysCountType.MORE_TEN.toString().equals(latePaymentDaysCountType)) {
                if (latePaymentReportModel.getDaycount() > 10) {
                    latePaymentReportModels.add(latePaymentReportModel);
                }
            } else {
                latePaymentReportModels.add(latePaymentReportModel);
            }
        }

        return latePaymentReportModels;
    }
}
