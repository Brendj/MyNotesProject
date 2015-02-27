/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.totalServices;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.OrderTypeEnumType;
import ru.axetta.ecafe.processor.core.persistence.utils.DetailedDeviationsWithoutCorpsService;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ClientInfo;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ComplexInfoItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 18.02.15
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class TotalServicesHelper {

    public static DetailedDeviationsWithoutCorpsService benefitService = new DetailedDeviationsWithoutCorpsService();

    public static Set<Long> getBeneficiaryClientsIdSet(Session session, Date startTime, Date endTime, Long idOfOrg) {

        List<PlanOrderItem> planOrderItems = new ArrayList<PlanOrderItem>();

        Date sTt = startTime;
        CalendarUtils.truncateToDayOfMonth(sTt);
        Date eTt = CalendarUtils.addOneDay(startTime);
        CalendarUtils.truncateToDayOfMonth(eTt);

        List<DiscountRule> rulesForOrg = DetailedDeviationsWithoutCorpsService.getDiscountRulesByOrg(session, idOfOrg);

        List<ComplexInfoItem> complexInfoItemListByPlan = DetailedDeviationsWithoutCorpsService
                .loadComplexNameByPlan(session, idOfOrg, startTime, endTime);

        // Платные категории
        List<Long> paidCategories = DetailedDeviationsWithoutCorpsService.loadAllPaydAbleCategories(session);

        // Льготные планы питания
        List<PlanOrderItem> planOrderItemList = getPlanOrderItemList(session, sTt, eTt, idOfOrg, rulesForOrg,
                complexInfoItemListByPlan, paidCategories);

        if (planOrderItemList != null) planOrderItems.addAll(planOrderItemList);

        Date nextDateEndTime = endTime;
        CalendarUtils.truncateToDayOfMonth(nextDateEndTime);

        while (eTt.before(CalendarUtils.truncateToDayOfMonth(nextDateEndTime))) {
            sTt = CalendarUtils.addOneDay(sTt);
            eTt = CalendarUtils.addOneDay(eTt);
            planOrderItemList = getPlanOrderItemList(session, sTt, eTt, idOfOrg, rulesForOrg, complexInfoItemListByPlan,
                    paidCategories);
            if (planOrderItemList != null) planOrderItems.addAll(planOrderItemList);
        }

        Set<Long> idOfBenefitClients = new HashSet<Long>();
        for (PlanOrderItem planOrderItem : planOrderItems) idOfBenefitClients.add(planOrderItem.getIdOfClient());
        return idOfBenefitClients;
    }

    private static String getBenefitOrderTypes() {
        /**
         *  Типы оплат (OrderType) "льготники"
         * **/
        List<OrderTypeEnumType> benefitOrderTypesList = new ArrayList<OrderTypeEnumType>() {{
            add(OrderTypeEnumType.REDUCED_PRICE_PLAN);
            //add(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE); // резерв
            add(OrderTypeEnumType.CORRECTION_TYPE);
        }};
        String benefitOrderTypesString = "";
        for (OrderTypeEnumType type : benefitOrderTypesList) {
            benefitOrderTypesString += ((Integer) type.ordinal()).toString() + ",";
        }
        return benefitOrderTypesString.substring(0, benefitOrderTypesString.length() - 1);
    }

    /**
     * Список учащихся организации
     *
     * @param session
     * @param startTime - todo оставлен для доработки с учетом миграций
     * @param endTime   - todo оставлен для доработки с учетом миграций
     * @param idOfOrg
     * @return
     */
    public static List<ClientInfo> getOrgClientsInfoList(Session session, Date startTime, Date endTime, Long idOfOrg) {
        List<ClientInfo> clientInfoList = new ArrayList<ClientInfo>();

        Query query = session.createSQLQuery(
                "SELECT DISTINCT cl.idofclient, cl.idoforg, (p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fullname, gr.idofclientgroup, gr.groupName, cl.categoriesDiscounts "
                        + "FROM cf_clients cl "
                        + "LEFT JOIN cf_clientgroups gr ON gr.idofclientgroup = cl.idofclientgroup AND gr.idoforg = cl.idoforg "
                        + "LEFT JOIN cf_persons p ON cl.idofperson = p.idofperson "
                        + "LEFT JOIN cf_cards cards ON cards.idofclient = cl.idofclient "
                        + "WHERE cl.idoforg = :idOfOrg AND gr.idOfClientGroup < " + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                        + "AND cards.state = 0");
        query.setParameter("idOfOrg", idOfOrg);

        List result = query.list();

        for (Object resultClient : result) {
            Object[] resultClientItem = (Object[]) resultClient;
            ClientInfo clientInfo = new ClientInfo(((BigInteger) resultClientItem[0]).longValue(),
                    ((BigInteger) resultClientItem[1]).longValue(), (String) resultClientItem[2],
                    ((BigInteger) resultClientItem[3]).longValue(), (String) resultClientItem[4],
                    (String) resultClientItem[5]);
            clientInfoList.add(clientInfo);
        }
        return clientInfoList;
    }

    // Планы питания на учащихся
    public static List<PlanOrderItem> getPlanOrderItemList(Session session, Date startTime, Date endTime, Long idOfOrg,
            List<DiscountRule> rulesForOrg, List<ComplexInfoItem> complexInfoItemListByPlan,
            List<Long> onlyPaidCategories) {
        List<PlanOrderItem> allItems = new ArrayList<PlanOrderItem>();
        // Учащиеся организации
        List<ClientInfo> clientInfoList = getOrgClientsInfoList(session, startTime, endTime, idOfOrg);

        Long a = 0L;
        Long b = 0L;
        Long c = 0L;
        Long d = 0L;

        if (!clientInfoList.isEmpty()) {
            for (ClientInfo clientInfo : clientInfoList) {
                List<Long> categories = benefitService
                        .getClientBenefits(clientInfo.getCategoriesDiscounts(), clientInfo.getGroupName());
                categories.removeAll(onlyPaidCategories);
                List<DiscountRule> rules = benefitService.getClientsRules(rulesForOrg, categories);
                rules = benefitService.getRulesByHighPriority(rules);
                for (DiscountRule rule : rules) {
                    a += 1;
                    String complexMap = rule.getComplexesMap();
                    if (complexMap != null && complexMap.length() > 0) {
                        b += 1;
                    } else if (complexMap != null){
                        c += 1;
                    } else {
                        d += 1;
                    }
                    List<Integer> allComplexesId = getComplexIdListFromString(complexMap);
                    for (Integer complexId : allComplexesId) {

                        String complexName = getComplexNameById(startTime, endTime, complexInfoItemListByPlan, complexId);
                        PlanOrderItem item = new PlanOrderItem(clientInfo.getClientId(), clientInfo.getClientName(),
                                complexId, rule.getIdOfRule(), startTime, clientInfo.getGroupName(), complexName);
                        allItems.add(item);
                        if (rule.getOperationOr()) {
                            break;
                        }
                    }
                }
            }
            return allItems;
        }
        return null;
    }

    private static String getComplexNameById(Date startTime, Date endTime, List<ComplexInfoItem> complexInfoItemListByPlan,
            Integer complexId) {
        String result = "";
        for (ComplexInfoItem complexInfoItem : complexInfoItemListByPlan) {
            if (complexInfoItem.getIdOfComplex().equals(complexId) && complexInfoItem.getMenuDate()
                    .after(startTime) && complexInfoItem.getMenuDate().before(endTime)) {
                result = complexInfoItem.getComplexName();
                break;
            }
        }
        return result;
    }

    private static List<Integer> getComplexIdListFromString(String complexMap) {
        List<Integer> allComplexesId = new ArrayList<Integer>();

        if ((complexMap != null) && (complexMap != "")) {
            String[] complexes = complexMap.split(";");
            for (String complex : complexes) {
                String[] complexItemSplit = complex.split("=");
                if (Integer.parseInt(complexItemSplit[1]) > 0) {
                    allComplexesId.add(Integer.parseInt(complexItemSplit[0]));
                }
            }
        }
        return allComplexesId;
    }
}
