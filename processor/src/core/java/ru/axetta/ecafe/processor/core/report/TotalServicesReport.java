/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DetailedDeviationsWithoutCorpsService;
import ru.axetta.ecafe.processor.core.persistence.utils.FriendlyOrganizationsInfoModel;
import ru.axetta.ecafe.processor.core.persistence.utils.OrgUtils;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ClientInfo;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.ComplexInfoItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 29.10.12
 * Time: 14:44
 * Онлайн отчеты -> Свод по услугам
 */
public class TotalServicesReport extends BasicReport {

    private final List<TotalEntry> items;

    public TotalServicesReport() {
        super();
        this.items = Collections.emptyList();
    }

    public TotalServicesReport(Date generateTime, long generateDuration, List<TotalEntry> items) {
        super(generateTime, generateDuration);
        this.items = items;
    }

    public List<TotalEntry> getItems() {
        return items;
    }

    public static class Builder {

        private Session session;
        private Date startDate;
        private Date endDate;
        private Long idOfOrg;
        private Boolean showBuildingDetails;
        // Данные по корпусам ОО
        private Map<Long, TotalEntry> entries;
        // Итоговые данные по ОО
        private TotalEntry totalEntry;
        private List<Long> idOfOrgList;

        public Builder(Session session, Date startDate, Date endDate, Long idOfOrg, Boolean showBuildingDetails) {
            this.session = session;
            this.startDate = startDate;
            this.endDate = endDate;
            this.idOfOrg = idOfOrg;
            this.showBuildingDetails = showBuildingDetails;
            this.entries = new HashMap<Long, TotalEntry>();
            this.totalEntry = initTotalEntry();
            this.idOfOrgList = getFriendlyOrgsIdList(this.session, this.idOfOrg);
        }

        private static Set<Long> getBeneficiaryClientsIdSet(Session session, Date startTime, Date endTime,
                List<Long> idOfOrgList) {
            List<PlanOrderItem> planOrderItems = new ArrayList<PlanOrderItem>();
            Date sTt = startTime;
            CalendarUtils.truncateToDayOfMonth(sTt);
            Date eTt = CalendarUtils.addOneDay(startTime);
            CalendarUtils.truncateToDayOfMonth(eTt);

            List<DiscountRule> rulesForOrg = new ArrayList<DiscountRule>();
            List<ComplexInfoItem> complexInfoItemListByPlan = new ArrayList<ComplexInfoItem>();
            for (Long idOfOrg : idOfOrgList) {
                rulesForOrg.addAll(
                        DetailedDeviationsWithoutCorpsService.getDiscountRulesByOrg(session, idOfOrg));
                complexInfoItemListByPlan.addAll(
                        DetailedDeviationsWithoutCorpsService.loadComplexNameByPlan(session, idOfOrg, startTime,
                                endTime));
            }

            // Платные категории
            List<Long> paidCategories =
                    DetailedDeviationsWithoutCorpsService.loadAllPaydAbleCategories(session);

            // Льготные планы питания
            List<PlanOrderItem> planOrderItemList = getPlanOrderItemList(session, sTt, eTt, idOfOrgList, rulesForOrg,
                    complexInfoItemListByPlan, paidCategories);

            if (planOrderItemList != null) {
                planOrderItems.addAll(planOrderItemList);
            }

            CalendarUtils.truncateToDayOfMonth(endTime);

            while (eTt.before(CalendarUtils.truncateToDayOfMonth(endTime))) {
                sTt = CalendarUtils.addOneDay(sTt);
                eTt = CalendarUtils.addOneDay(eTt);
                planOrderItemList = getPlanOrderItemList(session, sTt, eTt, idOfOrgList, rulesForOrg,
                        complexInfoItemListByPlan, paidCategories);
                if (planOrderItemList != null) {
                    planOrderItems.addAll(planOrderItemList);
                }
            }

            Set<Long> idOfBenefitClients = new HashSet<Long>();
            for (PlanOrderItem planOrderItem : planOrderItems) {
                idOfBenefitClients.add(planOrderItem.getIdOfClient());
            }
            return idOfBenefitClients;
        }

        // Планы питания на учащихся
        private static List<PlanOrderItem> getPlanOrderItemList(Session session, Date startTime, Date endTime,
                List<Long> idOfOrgList, List<DiscountRule> rulesForOrg, List<ComplexInfoItem> complexInfoItemListByPlan,
                List<Long> onlyPaidCategories) {
            List<PlanOrderItem> allItems = new ArrayList<PlanOrderItem>();
            // Учащиеся организации
            List<ClientInfo> clientInfoList = getOrgClientsInfoList(session, startTime, endTime, idOfOrgList);

            if (!clientInfoList.isEmpty()) {
                for (ClientInfo clientInfo : clientInfoList) {
                    List<Long> categories =
                            DetailedDeviationsWithoutCorpsService
                                .getClientBenefits(clientInfo.getCategoriesDiscounts(), clientInfo.getGroupName());
                    categories.removeAll(onlyPaidCategories);
                    List<DiscountRule> rules =
                            DetailedDeviationsWithoutCorpsService
                                .getClientsRules(rulesForOrg, categories);
                    rules =
                            DetailedDeviationsWithoutCorpsService.getRulesByHighPriority(rules);
                    for (DiscountRule rule : rules) {
                        String complexMap = rule.getComplexesMap();
                        List<Integer> allComplexesId = getComplexIdListFromString(complexMap);
                        for (Integer complexId : allComplexesId) {
                            String complexName = getComplexNameById(startTime, endTime, complexInfoItemListByPlan,
                                    complexId);
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

        /**
         * Список учащихся организации
         *
         * @param session
         * @param startTime   - оставлен для доработки с учетом миграций
         * @param endTime     - оставлен для доработки с учетом миграций
         * @param idOfOrgList
         * @return
         */
        private static List<ClientInfo> getOrgClientsInfoList(Session session, Date startTime, Date endTime,
                List<Long> idOfOrgList) {
            List<ClientInfo> clientInfoList = new ArrayList<ClientInfo>();

            Query query = session.createSQLQuery("SELECT \n" +
                    "DISTINCT cl.idofclient, \n" +
                    "cl.idoforg, \n" +
                    "(p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fullname, \n" +
                    "gr.idofclientgroup, \n" +
                    "gr.groupName, \n" +
                    "cl.categoriesDiscounts\n " +
                    "FROM " +
                    "cf_clients cl " +
                    "LEFT JOIN cf_clientgroups gr ON gr.idofclientgroup = cl.idofclientgroup AND gr.idoforg = cl.idoforg \n"
                    +
                    "LEFT JOIN cf_persons p ON cl.idofperson = p.idofperson \n" +
                    "LEFT JOIN cf_cards cards ON cards.idofclient = cl.idofclient \n" +
                    "WHERE " +
                    "cl.idoforg IN (:idOfOrgList) \n" +
                    "AND gr.idOfClientGroup < " + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " \n" +
                    "AND cards.state = 0;");
            query.setParameterList("idOfOrgList", idOfOrgList);

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

        private static List<Integer> getComplexIdListFromString(String complexMap) {
            List<Integer> allComplexesId = new ArrayList<Integer>();

            if ((complexMap != null) && (!complexMap.equals(""))) {
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

        private static String getComplexNameById(Date startTime, Date endTime,
                List<ComplexInfoItem> complexInfoItemListByPlan, Integer complexId) {
            String result = "";
            for (ComplexInfoItem complexInfoItem : complexInfoItemListByPlan) {
                if (complexInfoItem.getIdOfComplex().equals(complexId) && complexInfoItem.getMenuDate().after(startTime)
                        && complexInfoItem.getMenuDate().before(endTime)) {
                    result = complexInfoItem.getComplexName();
                    break;
                }
            }
            return result;
        }

        private static List<Long> getFriendlyOrgsIdList(Session session, Long idOfOrg) {
            List<Long> idOfOrgList = new ArrayList<Long>();
            idOfOrgList.add(idOfOrg);
            Set<Long> idOfOrgSet = new HashSet<Long>();
            Set<FriendlyOrganizationsInfoModel> organizationsInfoModelSet = OrgUtils
                    .getMainBuildingAndFriendlyOrgsList(session, idOfOrgList);
            for (FriendlyOrganizationsInfoModel org : organizationsInfoModelSet) {
                idOfOrgSet.add(org.getIdOfOrg());
                Set<Org> friends = org.getFriendlyOrganizationsSet();
                if (friends != null) {
                    for (Org friend : friends) {
                        idOfOrgSet.add(friend.getIdOfOrg());
                    }
                }
            }
            idOfOrgList.clear();
            idOfOrgList.addAll(idOfOrgSet);
            return idOfOrgList;
        }

        private static List<OrderTypeEnumType> getBenefitOrderTypes() {
            // Типы оплат (OrderType) льготных планов питания
            return new ArrayList<OrderTypeEnumType>() {{
                add(OrderTypeEnumType.REDUCED_PRICE_PLAN);
                add(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE);
                add(OrderTypeEnumType.WATER_ACCOUNTING);
            }};
        }

        private static List<OrderTypeEnumType> getPaidOrderTypes() {
            // Типы оплат (OrderType) платных планов питания
            return new ArrayList<OrderTypeEnumType>() {{
                add(OrderTypeEnumType.DEFAULT);
                add(OrderTypeEnumType.PAY_PLAN);
                add(OrderTypeEnumType.SUBSCRIPTION_FEEDING);
            }};
        }

        private static List<OrderTypeEnumType> getDefaultOrderType() {
            return new ArrayList<OrderTypeEnumType>() {{
                add(OrderTypeEnumType.DEFAULT);
            }};
        }

        private static List<OrderTypeEnumType> getCommonOrderTypes() {
            List<OrderTypeEnumType> benefitOrderTypesList = new ArrayList<OrderTypeEnumType>();
            benefitOrderTypesList.addAll(getPaidOrderTypes());
            benefitOrderTypesList.addAll(getBenefitOrderTypes());
            return benefitOrderTypesList;
        }

        public TotalServicesReport build() throws Exception {
            Date generateTime = new Date();
            // Инициализация структуры данных
            initData();
            // Получение общего количества учащихся
            loadTotalClientsCount();
            // Получение количества получающих льготное питание
            loadBeneficiaryStudentsCount();
            // Получение количества событий прохода через турникет за период
            loadCurrentClientsCount();
            // Получение количества учеников получивших льготное питание
            loadBenefitClientsGotFeed();
            // Получение количества учеников их чужих школ, получивших льготное питание
            loadBenefitClientsOtherOrgsGotFeed();
            // Получение количества получивших платное комплексное питание
            loadPaidClientsGotFeed();
            // Получение количества клиентов получивших платное питание в буфете
            loadSnackClientsGotFeed();
            // Получение количества клиентов получивших питание
            loadClientsGotFeed();

            List<TotalEntry> result = new LinkedList<TotalEntry>();
            if (showBuildingDetails) {
                result.addAll(entries.values());
            }
            result.add(totalEntry);

            return new TotalServicesReport(generateTime, new Date().getTime() - generateTime.getTime(), result);
        }

        private TotalEntry initTotalEntry() {
            Org org = (Org) session.load(Org.class, idOfOrg);
            String mainBuildingName = org.getShortName();
            if (mainBuildingName.indexOf("-") >= 0)
                mainBuildingName = mainBuildingName.substring(0, mainBuildingName.indexOf("-"));
            if (showBuildingDetails)
                mainBuildingName = "Итого по " + mainBuildingName;
            return new TotalEntry(mainBuildingName, idOfOrg);
        }

        private void initData() {
            Criteria criteria = session.createCriteria(Org.class);
            criteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            criteria.add(Restrictions.eq("state", 1));
            criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("idOfOrg"))
                    .add(Projections.groupProperty("shortName")));
            if (showBuildingDetails) {
                List resultList = criteria.list();
                for (Object result : resultList) {
                    Object e[] = (Object[]) result;
                    Long id = (Long) e[0];
                    String shortName = ((String) e[1]).trim();
                    TotalEntry item = new TotalEntry(shortName);
                    entries.put(id, item);
                }
            }
        }

        private void loadTotalClientsCount() {
            Criteria criteria = session.createCriteria(Client.class);
            criteria.createAlias("org", "o");
            criteria.add(Restrictions.in("o.idOfOrg", idOfOrgList));
            criteria.add(Restrictions.eq("o.state", 1));
            criteria.add(Restrictions.lt("idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()));
            criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("o.idOfOrg"))
                    .add(Projections.countDistinct("idOfClient")));
            if (showBuildingDetails) {
                List resultList = criteria.list();
                for (Object result : resultList) {
                    Object e[] = (Object[]) result;
                    Long id = (Long) e[0];
                    TotalEntry item = entries.get(id);
                    item.setTotalClientsCount((Long) e[1]);
                    entries.put(id, item);
                }
            }
            criteria.setProjection(Projections.countDistinct("idOfClient"));
            List resultList = criteria.list();
            totalEntry.setTotalClientsCount((Long) resultList.get(0));
        }

        private void loadBeneficiaryStudentsCount() {

            /** todo следует использовать
             *  ClientDao clientDao = new ClientDao();
             *  clientDao.findAllBeneficiaryStudentsCount();
             */
            if (showBuildingDetails) {
                for (Long idOfOrg : entries.keySet()) {
                    entries.get(idOfOrg).setPlanBenefitClientsCount(
                            (long) getBeneficiaryClientsIdSet(session, startDate, endDate, Arrays.asList(idOfOrg))
                                    .size());
                }
            }
            totalEntry.setPlanBenefitClientsCount(
                    (long) getBeneficiaryClientsIdSet(session, startDate, endDate, idOfOrgList).size());
        }

        private void loadCurrentClientsCount() {

            //String currentClientsCountForBuilingsPQ = "select cf_enterevents.idoforg, count(distinct cf_enterevents.idofclient) " +
            //        "from cf_orgs " +
            //        "left join cf_clients on cf_clients.idoforg=cf_orgs.idoforg " +
            //        "left join cf_enterevents on cf_enterevents.idofclient=cf_clients.idofclient and cf_enterevents.idoforg in "
            //        + orgConditionIn +
            //        "where cf_orgs.state=1 " + " and cf_clients.idOfClientGroup<" + ClientGroup.Predefined
            //        .CLIENT_EMPLOYEES.getValue() + " AND " + orgConditionWithCFORGS +
            //        " AND cf_enterevents.evtdatetime>=" + startDate.getTime() +
            //        " AND cf_enterevents.evtdatetime<" + endDate.getTime() +
            //        " group by cf_enterevents.idoforg";

            Criteria criteria = session.createCriteria(EnterEvent.class);
            criteria.createAlias("client", "cl");
            criteria.createAlias("org", "o");
            criteria.add(Restrictions.in("o.idOfOrg", idOfOrgList));
            criteria.add(Restrictions.eq("o.state", 1));
            criteria.add(Restrictions.lt("cl.idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()));
            criteria.add(Restrictions.between("evtDateTime", startDate, endDate));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("o.idOfOrg"))
                    .add(Projections.countDistinct("cl.idOfClient")));
            if (showBuildingDetails) {
                List resultList = criteria.list();
                for (Object result : resultList) {
                    Object e[] = (Object[]) result;
                    entries.get(e[0]).setCurrentClientsCount((Long) e[1]);
                }
            }
            criteria.setProjection(Projections.countDistinct("cl.idOfClient"));
            List resultList = criteria.list();
            totalEntry.setCurrentClientsCount((Long) resultList.get(0));
        }

        private void loadBenefitClientsGotFeed() {

            Criteria criteria = session.createCriteria(OrderDetail.class);
            criteria.createAlias("order", "o");
            criteria.createAlias("order.client", "cl");
            criteria.add(Restrictions.between("menuType", OrderDetail.TYPE_COMPLEX_MIN, OrderDetail.TYPE_COMPLEX_MAX));
            criteria.add(Restrictions.eq("o.state", 0));
            criteria.add(Restrictions.in("o.orderType", getBenefitOrderTypes()));
            criteria.add(Restrictions.lt("cl.idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()));
            criteria.add(Restrictions.between("o.createTime", startDate, endDate));
            criteria.add(Restrictions.in("o.org.idOfOrg", idOfOrgList));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("o.org.idOfOrg"))
                    .add(Projections.countDistinct("cl.idOfClient")));
            if (showBuildingDetails) {
                List resultList = criteria.list();
                for (Object result : resultList) {
                    Object e[] = (Object[]) result;
                    entries.get(e[0]).setRealBenefitClientsCount((Long) e[1]);
                }
            }
            criteria.setProjection(Projections.countDistinct("cl.idOfClient"));
            List resultList = criteria.list();
            totalEntry.setRealBenefitClientsCount((Long) resultList.get(0));
        }

        private void loadBenefitClientsOtherOrgsGotFeed() {

            Criteria criteria = session.createCriteria(OrderDetail.class);
            criteria.createAlias("order", "o");
            criteria.createAlias("order.client", "cl");
            criteria.add(Restrictions.between("menuType", OrderDetail.TYPE_COMPLEX_MIN, OrderDetail.TYPE_COMPLEX_MAX));
            criteria.add(Restrictions.eq("o.state", 0));
            criteria.add(Restrictions.in("o.orderType", getBenefitOrderTypes()));
            criteria.add(Restrictions.lt("cl.idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()));
            criteria.add(Restrictions.between("o.createTime", startDate, endDate));
            criteria.add(Restrictions.in("o.org.idOfOrg", idOfOrgList));
            criteria.add(Restrictions.not(Restrictions.in("cl.org.idOfOrg", idOfOrgList)));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("o.org.idOfOrg"))
                    .add(Projections.countDistinct("cl.idOfClient")));
            if (showBuildingDetails) {
                List resultList = criteria.list();
                for (Object result : resultList) {
                    Object e[] = (Object[]) result;
                    entries.get(e[0]).setRealBenefitClientsOtherOrgsCount((Long) e[1]);
                }
            }
            criteria.setProjection(Projections.countDistinct("cl.idOfClient"));
            List resultList = criteria.list();
            totalEntry.setRealBenefitClientsOtherOrgsCount((Long) resultList.get(0));
        }

        private void loadPaidClientsGotFeed() {

            //String realPayedClientsCountForBuilingsPQ =
            //        "SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) " + " FROM cf_orders  "
            //                + " LEFT JOIN cf_orderdetails ON cf_orders.idoforder = cf_orderdetails.idoforder and cf_orders.idoforg = cf_orderdetails.idoforg "
            //                + " LEFT JOIN cf_clients ON cf_clients.idofclient = cf_orders.idofclient " + " WHERE "
            //                + "     cf_orderdetails.menutype between " + OrderDetail.TYPE_COMPLEX_MIN + " and "
            //                + OrderDetail.TYPE_COMPLEX_MAX + "     AND cf_orders.state = 0 "
            //                + "     AND cf_orders.ordertype in (1, 3, 7) " + "     AND cf_clients.idOfClientGroup < "
            //                + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
            //                + "     AND cf_orders.createddate between " + startDate.getTime() + " and " + endDate
            //                .getTime() + "     AND " + orgConditionWithCFORDERS + "group by cf_orders.idoforg";

            Criteria criteria = session.createCriteria(OrderDetail.class);
            criteria.createAlias("order", "o");
            criteria.createAlias("order.client", "cl");
            criteria.add(Restrictions.between("menuType", OrderDetail.TYPE_COMPLEX_MIN, OrderDetail.TYPE_COMPLEX_MAX));
            criteria.add(Restrictions.eq("o.state", 0));
            criteria.add(Restrictions.in("o.orderType", getPaidOrderTypes()));
            criteria.add(Restrictions.lt("cl.idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()));
            criteria.add(Restrictions.between("o.createTime", startDate, endDate));
            criteria.add(Restrictions.in("o.org.idOfOrg", idOfOrgList));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("o.org.idOfOrg"))
                    .add(Projections.countDistinct("cl.idOfClient")));
            if (showBuildingDetails) {
                List resultList = criteria.list();
                for (Object result : resultList) {
                    Object e[] = (Object[]) result;
                    entries.get(e[0]).setRealPaidClientsCount((Long) e[1]);
                }
            }
            criteria.setProjection(Projections.countDistinct("cl.idOfClient"));
            List resultList = criteria.list();
            totalEntry.setRealPaidClientsCount((Long) resultList.get(0));
        }

        private void loadSnackClientsGotFeed() {

            //String realSnackPayedClientsCountForBuilingsPQ =
            //"SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) " + " FROM cf_orders "
            //+ " LEFT JOIN cf_orderdetails ON cf_orders.idoforder = cf_orderdetails.idoforder and cf_orders.idoforg = cf_orderdetails.idoforg "
            //+ " LEFT JOIN cf_clients ON cf_clients.idofclient = cf_orders.idofclient " + " WHERE "
            //+ "     cf_orderdetails.menutype = " + OrderDetail.TYPE_DISH_ITEM
            //+ "     AND cf_orders.state = 0 " + "     AND cf_orders.ordertype in (1) "
            //+ "     AND cf_clients.idOfClientGroup < " + ClientGroup.Predefined.CLIENT_EMPLOYEES
            //.getValue() + "     AND cf_orders.createddate between " + startDate.getTime() + " and "
            //+ endDate.getTime() + "     AND " + orgConditionWithCFORDERS + "group by cf_orders.idoforg";

            Criteria criteria = session.createCriteria(OrderDetail.class);
            criteria.createAlias("order", "o");
            criteria.createAlias("order.client", "cl");
            criteria.add(Restrictions.eq("menuType", OrderDetail.TYPE_DISH_ITEM));
            criteria.add(Restrictions.eq("o.state", 0));
            criteria.add(Restrictions.in("o.orderType", getDefaultOrderType()));
            criteria.add(Restrictions.lt("cl.idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()));
            criteria.add(Restrictions.between("o.createTime", startDate, endDate));
            criteria.add(Restrictions.in("o.org.idOfOrg", idOfOrgList));
            criteria.setProjection(Projections.projectionList()
                            .add(Projections.groupProperty("o.org.idOfOrg"))
                            .add(Projections.countDistinct("cl.idOfClient")));
            if (showBuildingDetails) {
                List resultList = criteria.list();
                for (Object result : resultList) {
                    Object e[] = (Object[]) result;
                    entries.get(e[0]).setRealSnackPaidClientsCount((Long) e[1]);
                }
            }
            criteria.setProjection(Projections.countDistinct("cl.idOfClient"));
            List resultList = criteria.list();
            totalEntry.setRealSnackPaidClientsCount((Long) resultList.get(0));
        }

        private void loadClientsGotFeed() {

            //String uniqueClientsCountForBuilingsPQ =
            //        "SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) " + " FROM cf_orders "
            //                + " LEFT JOIN cf_orderdetails ON cf_orders.idoforder = cf_orderdetails.idoforder and cf_orders.idoforg = cf_orderdetails.idoforg "
            //                + " LEFT JOIN cf_clients ON cf_clients.idofclient = cf_orders.idofclient " + " WHERE "
            //                + "     (cf_orderdetails.menutype = " + OrderDetail.TYPE_DISH_ITEM
            //                + "     OR cf_orderdetails.menutype between " + OrderDetail.TYPE_COMPLEX_MIN + " and "
            //                + OrderDetail.TYPE_COMPLEX_MAX + ")" + "     AND cf_orders.state = 0 "
            //                + "     AND cf_orders.ordertype in (1, 3, 4, 6, 7) "
            //                + "     AND cf_clients.idOfClientGroup < " + ClientGroup.Predefined.CLIENT_EMPLOYEES
            //                .getValue() + "     AND cf_orders.createddate between " + startDate.getTime() + " and "
            //                + endDate.getTime() + "     AND " + orgConditionWithCFORDERS + "group by cf_orders.idoforg";

            Criteria criteria = session.createCriteria(OrderDetail.class);
            criteria.createAlias("order", "o");
            criteria.createAlias("order.client", "cl");
            criteria.add(Restrictions.or(
                            Restrictions.eq("menuType", OrderDetail.TYPE_DISH_ITEM),
                            Restrictions.between("menuType", OrderDetail.TYPE_COMPLEX_MIN, OrderDetail.TYPE_COMPLEX_MAX)));
            criteria.add(Restrictions.eq("o.state", 0));
            criteria.add(Restrictions.in("o.orderType", getCommonOrderTypes()));
            criteria.add(Restrictions.lt("cl.idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()));
            criteria.add(Restrictions.between("o.createTime", startDate, endDate));
            criteria.add(Restrictions.in("o.org.idOfOrg", idOfOrgList));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.groupProperty("o.org.idOfOrg"))
                    .add(Projections.countDistinct("cl.idOfClient")));
            if (showBuildingDetails) {
                List resultList = criteria.list();
                for (Object result : resultList) {
                    Object e[] = (Object[]) result;
                    entries.get(e[0]).setUniqueClientsCount((Long) e[1]);
                }
            }
            criteria.setProjection(Projections.countDistinct("cl.idOfClient"));
            List resultList = criteria.list();
            totalEntry.setUniqueClientsCount((Long) resultList.get(0));
        }
    }

    public static class TotalEntry {

        private String shortName = null;                    // Название организации
        private Long idOfOrg = null;
        private Long totalClientsCount = 0L;
        private Long planBenefitClientsCount = 0L;
        private String per_planBenefitClientsCount = "0,00 %";
        private Long currentClientsCount = 0L;
        private String per_currentClientsCount = "0,00 %";
        private Long realBenefitClientsCount = 0L;
        private String per_realBenefitClientsCount = "0,00 %";
        private Long realBenefitClientsOtherOrgsCount = 0L;
        private Long realPaidClientsCount = 0L;
        private String per_realPaidClientsCount = "0,00 %";
        private Long realSnackPaidClientsCount = 0L;
        private String per_realSnackPaidClientsCount = "0,00 %";
        private Long uniqueClientsCount = 0L;
        private String per_uniqueClientsCount = "0,00 %";
        private Map<String, Object> data;

        public TotalEntry(String shortName) {
            this.shortName = shortName;
        }

        public TotalEntry(String shortName, Long idOfOrg) {
            this.shortName = shortName;
            this.idOfOrg = idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public Long getTotalClientsCount() {
            return totalClientsCount;
        }

        public void setTotalClientsCount(Long totalClientsCount) {
            this.totalClientsCount = totalClientsCount;
            setPer_planBenefitClientsCount();
            setPer_currentClientsCount();
            setPer_realBenefitClientsCount();
            setPer_realPaidClientsCount();
            setPer_realSnackPaidClientsCount();
            setPer_uniqueClientsCount();
        }

        public Long getPlanBenefitClientsCount() {
            return planBenefitClientsCount;
        }

        public void setPlanBenefitClientsCount(Long planBenefitClientsCount) {
            this.planBenefitClientsCount = planBenefitClientsCount;
            setPer_planBenefitClientsCount();
        }

        public Long getCurrentClientsCount() {
            return currentClientsCount;
        }

        public void setCurrentClientsCount(Long currentClientsCount) {
            this.currentClientsCount = currentClientsCount;
            setPer_currentClientsCount();
        }

        public Long getRealBenefitClientsCount() {
            return realBenefitClientsCount;
        }

        public void setRealBenefitClientsCount(Long realBenefitClientsCount) {
            this.realBenefitClientsCount = realBenefitClientsCount;
            setPer_realBenefitClientsCount();
        }

        public Long getRealPaidClientsCount() {
            return realPaidClientsCount;
        }

        public void setRealPaidClientsCount(Long realPaidClientsCount) {
            this.realPaidClientsCount = realPaidClientsCount;
            setPer_realPaidClientsCount();
        }

        public Long getRealSnackPaidClientsCount() {
            return realSnackPaidClientsCount;
        }

        public void setRealSnackPaidClientsCount(Long realSnackPaidClientsCount) {
            this.realSnackPaidClientsCount = realSnackPaidClientsCount;
            setPer_realSnackPaidClientsCount();
        }

        public Long getUniqueClientsCount() {
            return uniqueClientsCount;
        }

        public void setUniqueClientsCount(Long uniqueClientsCount) {
            this.uniqueClientsCount = uniqueClientsCount;
            setPer_uniqueClientsCount();
        }

        public String getPer_planBenefitClientsCount() {
            return per_planBenefitClientsCount;
        }

        private void setPer_planBenefitClientsCount() {
            this.per_planBenefitClientsCount =
                    new BigDecimal(totalClientsCount == 0 ? 0 : planBenefitClientsCount.doubleValue() / totalClientsCount * 100).
                            setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%";
        }

        public String getPer_currentClientsCount() {
            return per_currentClientsCount;
        }

        private void setPer_currentClientsCount() {
            this.per_currentClientsCount =
                    new BigDecimal(totalClientsCount == 0 ? 0 : currentClientsCount.doubleValue() / totalClientsCount * 100).
                            setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%";
        }

        public String getPer_realBenefitClientsCount() {
            return per_realBenefitClientsCount;
        }

        private void setPer_realBenefitClientsCount() {
            this.per_realBenefitClientsCount =
                    new BigDecimal(totalClientsCount == 0 ? 0 : realBenefitClientsCount.doubleValue() / totalClientsCount * 100).
                            setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%";
        }

        public String getPer_realPaidClientsCount() {
            return per_realPaidClientsCount;
        }

        private void setPer_realPaidClientsCount() {
            this.per_realPaidClientsCount =
                    new BigDecimal(totalClientsCount == 0 ? 0 : realPaidClientsCount.doubleValue() / totalClientsCount * 100).
                            setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%";
        }

        public String getPer_realSnackPaidClientsCount() {
            return per_realSnackPaidClientsCount;
        }

        private void setPer_realSnackPaidClientsCount() {
            this.per_realSnackPaidClientsCount =
                    new BigDecimal(totalClientsCount == 0 ? 0 : realSnackPaidClientsCount.doubleValue() / totalClientsCount * 100).
                            setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%";
        }

        public String getPer_uniqueClientsCount() {
            return per_uniqueClientsCount;
        }

        private void setPer_uniqueClientsCount() {
            this.per_uniqueClientsCount =
                    new BigDecimal(totalClientsCount == 0 ? 0 : uniqueClientsCount.doubleValue() / totalClientsCount * 100).
                            setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%";
        }

        public Long getRealBenefitClientsOtherOrgsCount() {
            return realBenefitClientsOtherOrgsCount;
        }

        public void setRealBenefitClientsOtherOrgsCount(Long realBenefitClientsOtherOrgsCount) {
            this.realBenefitClientsOtherOrgsCount = realBenefitClientsOtherOrgsCount;
        }
    }
}