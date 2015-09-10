/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.detailed;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.09.15
 * Time: 10:24
 */

public class LatePaymentDetailedReportService {

    private static final Logger logger = LoggerFactory.getLogger(LatePaymentDetailedReportService.class);

    DateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");

    public LatePaymentDetailedReportService() {
    }

    public List<LatePaymentDetailedReportModel> getMainData(Session session, Long idOfOrg, Date startDate, Date endDate)
            throws Exception {

        List<LatePaymentDetailedReportModel> latePaymentDetailedReportModelList = new ArrayList<LatePaymentDetailedReportModel>();

        Query query = session.createSQLQuery(
                "SELECT substring(cfo.shortname FROM 'Y*([0-9-]+)') orgnum, cfo.address, to_timestamp( TRUNC(CAST(o.orderdate AS BIGINT) / 1000 )) paymentDate"
                        + " FROM cf_orders o INNER JOIN cf_orgs cfo ON cfo.idoforg = o.idoforg"
                        + " INNER JOIN cf_clients cl ON cl.idofclient = o.idofclient"
                        + " INNER JOIN CF_Clients_CategoryDiscounts cc ON cc.idofclient = cl.idofclient"
                        + " WHERE cast (to_timestamp(o.createddate/1000) AS DATE) <> cast (to_timestamp(o.orderdate/1000) AS DATE)"
                        + " AND o.ordertype IN (4,6) AND o.state = 0 AND o.createddate BETWEEN :startDate AND :endDate"
                        + " AND o.idoforg = :idOfOrg AND cc.idOfCategoryDiscount IN (2, 5, 3, 4, 20, 1, 104, 105, 106, 108, 112, 121, 122, 123, 124)"
                        + " GROUP BY cfo.shortname, cfo.address, to_timestamp( TRUNC(CAST(o.orderdate AS BIGINT) / 1000 )), o.idoforg"
                        + " ORDER BY cfo.shortname, cfo.address, to_timestamp( TRUNC(CAST(o.orderdate AS BIGINT) / 1000 ))");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());

        List resultList = query.list();

        String orgNum;
        String address;
        Date paymentDate;

        for (Object res : resultList) {
            Object[] result = (Object[]) res;

            orgNum = (String) result[0];
            address = (String) result[1];
            paymentDate = (Date) result[2];

            LatePaymentDetailedReportModel latePaymentDetailedReportModel = new LatePaymentDetailedReportModel(orgNum,
                    address, dateFormat.format(paymentDate));

            List<LatePaymentDetailedSubReportModel> latePaymentDetailedSubReportModelList = getExtraData(session,
                    idOfOrg, CalendarUtils.parseDate(latePaymentDetailedReportModel.getPaymentDate()), startDate,
                    endDate);

            latePaymentDetailedReportModel
                    .setLatePaymentDetailedSubReportModelList(latePaymentDetailedSubReportModelList);

            latePaymentDetailedReportModelList.add(latePaymentDetailedReportModel);
        }

        return latePaymentDetailedReportModelList;
    }

    public List<LatePaymentDetailedSubReportModel> getExtraData(Session session, Long idOfOrg, Date paymentDate,
            Date startDate, Date endDate) {

        List<LatePaymentDetailedSubReportModel> latePaymentDetailedSubReportModelList = new ArrayList<LatePaymentDetailedSubReportModel>();

        Query query = session.createSQLQuery(
                "SELECT to_timestamp( TRUNC( CAST(o.createddate AS BIGINT  ) / 1000 )) foodDate, p.surname ||' ' ||p.firstname ||' '|| p.secondname || ' (' || od.menudetailname ||')' client"
                        + " FROM cf_orders o INNER JOIN cf_clients cl ON cl.idofclient = o.idofclient"
                        + " INNER JOIN cf_persons p ON cl.idofperson = p.idofperson"
                        + " INNER JOIN cf_orderdetails od ON od.idoforder = o.idoforder AND od.idoforg = o.idoforg "
                        + "INNER JOIN CF_Clients_CategoryDiscounts cc ON cc.idofclient = cl.idofclient WHERE"
                        + " cast(to_timestamp(o.createddate / 1000)AS DATE) <> :paymentDate AND cast (to_timestamp(o.orderdate / 1000) AS DATE) = :paymentDate "
                        + " AND o.createddate BETWEEN :startDate AND :endDate AND od.menutype BETWEEN '50' AND '99' AND o.ordertype IN (4, 6)"
                        + " AND o.state = 0 AND cc.idOfCategoryDiscount IN (2, 5, 3, 4, 20, 1, 104, 105, 106, 108, 112, 121, 122, 123, 124)"
                        + " AND o.idoforg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("paymentDate", paymentDate);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());

        List resultList = query.list();

        Date foodDate;
        String client;

        for (Object res : resultList) {
            Object[] result = (Object[]) res;

            foodDate = (Date) result[0];
            client = (String) result[1];

            LatePaymentDetailedSubReportModel latePaymentDetailedSubReportModel = new LatePaymentDetailedSubReportModel(
                    dateFormat.format(foodDate), client);


            latePaymentDetailedSubReportModelList.add(latePaymentDetailedSubReportModel);
        }

        return latePaymentDetailedSubReportModelList;
    }
}
