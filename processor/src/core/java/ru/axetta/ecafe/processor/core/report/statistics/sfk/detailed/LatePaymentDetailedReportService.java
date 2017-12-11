/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.detailed;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.09.15
 * Time: 10:24
 */

public class LatePaymentDetailedReportService {

    private static final Logger logger = LoggerFactory.getLogger(LatePaymentDetailedReportService.class);

    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public LatePaymentDetailedReportService() {
    }

    public void getMainData(Session session, Long idOfOrg, Date startDate, Date endDate, Boolean showReserve, Boolean showRecycling,
            List<LatePaymentDetailedReportModel> latePaymentDetailedReportModelList,
            List<LatePaymentDetailedReportModel> recyclingModelList,
            List<LatePaymentDetailedReportModel> changeModelList) throws Exception {

        String cats = "2, 5, 3, 4, 20, 1, 104, 105, 106, 108, 112, 121, 122, 123, 124";
        String orderChange = "";
        if (showReserve) {
            cats += ", 50";
            orderChange = " OR (o.ordertype=11 AND cc.idOfCategoryDiscount is NULL)";
        }

        String orderRecycle = "";
        if (showRecycling) {
            orderRecycle = "OR (cl.idofclientgroup=" + ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue() +
                    " AND o.ordertype=" + OrderTypeEnumType.RECYCLING_RETIONS.ordinal() + ")";
        }


        Query query = session.createSQLQuery(
                "SELECT cfo.shortnameinfoservice as orgnum, cfo.address, cast (to_timestamp(o.orderdate/1000) AS DATE) paymentDate"
                        + " FROM cf_orders o INNER JOIN cf_orgs cfo ON cfo.idoforg = o.idoforg"
                        + " INNER JOIN cf_clients cl ON cl.idofclient = o.idofclient"
                        + " LEFT JOIN CF_Clients_CategoryDiscounts cc ON cc.idofclient = cl.idofclient"
                        + " WHERE cast (to_timestamp(o.createddate/1000) AS DATE) <> cast (to_timestamp(o.orderdate/1000) AS DATE)"
                        + " AND o.state = 0 AND o.createddate BETWEEN :startDate AND :endDate AND o.idoforg = :idOfOrg"
                        + " AND ((cc.idOfCategoryDiscount IN (" + cats + ") AND o.ordertype IN (:order_types)) " + orderRecycle + orderChange + " )"
                        + " GROUP BY cfo.shortnameinfoservice, cfo.address, cast (to_timestamp(o.orderdate/1000) AS DATE), o.idoforg"
                        + " ORDER BY cfo.shortnameinfoservice, cfo.address, cast (to_timestamp(o.orderdate/1000) AS DATE)");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("order_types", getOrderTypes(showReserve));

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
                    address, dateFormat.format(paymentDate), idOfOrg);

            List<LatePaymentDetailedSubReportModel> recyclingElements = new ArrayList<LatePaymentDetailedSubReportModel>();
            List<LatePaymentDetailedSubReportModel> changeElements = new ArrayList<LatePaymentDetailedSubReportModel>();

            List<LatePaymentDetailedSubReportModel> latePaymentDetailedSubReportModelList = getExtraData(session,
                    idOfOrg, CalendarUtils.parseDate(latePaymentDetailedReportModel.getPaymentDate()), startDate,
                    endDate, showReserve, showRecycling, recyclingElements, changeElements);

            latePaymentDetailedReportModel
                    .setLatePaymentDetailedSubReportModelList(latePaymentDetailedSubReportModelList);

            latePaymentDetailedReportModelList.add(latePaymentDetailedReportModel);


            if (!recyclingElements.isEmpty()) {
                LatePaymentDetailedReportModel latePaymentDetailedReportRecyclingModel = new LatePaymentDetailedReportModel(
                        orgNum, address, dateFormat.format(paymentDate), idOfOrg);
                latePaymentDetailedReportRecyclingModel
                        .setLatePaymentDetailedSubReportRecyclingModelList(recyclingElements);
                recyclingModelList.add(latePaymentDetailedReportRecyclingModel);
            }

            if (!changeElements.isEmpty()) {
                LatePaymentDetailedReportModel latePaymentDetailedReportChangeModel = new LatePaymentDetailedReportModel(
                        orgNum, address, dateFormat.format(paymentDate), idOfOrg);
                latePaymentDetailedReportChangeModel.setLatePaymentDetailedSubReportChangeModelList(changeElements);
                changeModelList.add(latePaymentDetailedReportChangeModel);
            }
        }
    }

    private List<Integer> getOrderTypes(Boolean showReserve) {
        List<Integer> order_types = new ArrayList<Integer>();
        order_types.add(OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal());
        order_types.add(OrderTypeEnumType.CORRECTION_TYPE.ordinal());
        if (showReserve) {
            order_types.add(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal());
            order_types.add(OrderTypeEnumType.DISCOUNT_PLAN_CHANGE.ordinal());
        }
        return order_types;
    }

    public List<LatePaymentDetailedSubReportModel> getExtraData(Session session, Long idOfOrg, Date paymentDate,
            Date startDate, Date endDate, Boolean showReserve, Boolean showRecycling,
            List<LatePaymentDetailedSubReportModel> recyclingElements,
            List<LatePaymentDetailedSubReportModel> changeElements) {

        String cats = "2, 5, 3, 4, 20, 1, 104, 105, 106, 108, 112, 121, 122, 123, 124";
        if (showReserve) {
            cats += ", 50";
        }
        String orderTypeCondition = " and ((o.ordertype in (:order_types) and cc.idOfCategoryDiscount IN (" + cats + "))" +
                ((showRecycling) ? "OR (cl.idofclientgroup = " + ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue()
                        + " AND o.ordertype=" + OrderTypeEnumType.RECYCLING_RETIONS.ordinal() + ") " : " ") +
                ((showReserve) ? "OR (o.ordertype=" + OrderTypeEnumType.DISCOUNT_PLAN_CHANGE.ordinal() + " AND cc.idOfCategoryDiscount is NULL)" : " ") +
                ") ";

        List<LatePaymentDetailedSubReportModel> latePaymentDetailedSubReportModelList = new ArrayList<LatePaymentDetailedSubReportModel>();

        Query query = session.createSQLQuery(
                "SELECT cast (to_timestamp(o.createddate/1000) as date) foodDate, "
                        + " p.surname ||' ' ||p.firstname ||' '|| p.secondname || ' (' || od.menudetailname ||')' client"
                        + " , coalesce(cg.GroupName, '') as groupName, o.ordertype, o.idofclient, od.menutype "
                        + " FROM cf_orders o INNER JOIN cf_clients cl ON cl.idofclient = o.idofclient"
                        + " INNER JOIN cf_persons p ON cl.idofperson = p.idofperson"
                        + " INNER JOIN cf_orderdetails od ON od.idoforder = o.idoforder AND od.idoforg = o.idoforg"
                        + " LEFT JOIN CF_Clients_CategoryDiscounts cc ON cc.idofclient = cl.idofclient"
                        + " left join CF_ClientGroups cg on cl.idoforg = cg.IdOfOrg and cl.IdOfClientGroup = cg.IdOfClientGroup"
                        + " WHERE cast(to_timestamp(o.createddate / 1000)AS DATE) <> :paymentDate AND cast (to_timestamp(o.orderdate / 1000) AS DATE) = :paymentDate "
                        + " AND o.createddate BETWEEN :startDate AND :endDate AND od.menutype BETWEEN '50' AND '99'"
                        + " AND o.state = 0 AND o.idoforg = :idOfOrg " + orderTypeCondition
                        + " GROUP BY o.createddate, p.surname, p.firstname, p.secondname, cg.groupname, od.menudetailname, o.ordertype, o.ordertype, o.idofclient, "
                        + " od.menutype "
                        + " ORDER BY foodDate, groupname, client");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("paymentDate", CalendarUtils.truncateToDayOfMonth(paymentDate));
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("order_types", getOrderTypes(showReserve));

        List resultList = query.list();

        Date foodDate;
        String client;
        String groupName;
        Integer orderType;
        Long idOfClient;
        Integer menuType;

        for (Object res : resultList) {
            Object[] result = (Object[]) res;

            foodDate = (Date) result[0];
            client = (String) result[1];
            groupName = (String) result[2];
            orderType = (Integer) result[3];
            idOfClient = ((BigInteger) result[4]).longValue();
            menuType = (Integer) result[5];

            LatePaymentDetailedSubReportModel latePaymentDetailedSubReportModel = new LatePaymentDetailedSubReportModel(
                    idOfClient, dateFormat.format(foodDate), client, groupName, menuType);

            if (OrderTypeEnumType.CORRECTION_TYPE.ordinal() == orderType ||
                    OrderTypeEnumType.RECYCLING_RETIONS.ordinal() == orderType) {
                recyclingElements.add(latePaymentDetailedSubReportModel);
            } else if (OrderTypeEnumType.DISCOUNT_PLAN_CHANGE.ordinal() == orderType ||
                    OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal() == orderType) {
                changeElements.add(latePaymentDetailedSubReportModel);
            } else {
                latePaymentDetailedSubReportModelList.add(latePaymentDetailedSubReportModel);
            }
        }

        return latePaymentDetailedSubReportModelList;
    }
}
