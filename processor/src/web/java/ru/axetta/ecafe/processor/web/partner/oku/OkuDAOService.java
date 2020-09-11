/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.Order;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

@Component
@Scope
public class OkuDAOService {

    private static final Logger logger = LoggerFactory.getLogger(OkuDAOService.class);

    private static List<Long> clientGroupList = new ArrayList<>();
    private static List<String> ageTypeGroupList = new ArrayList<>();

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager emReport;

    @Autowired
    private RuntimeContext runtimeContext;

    public static final String PREDEFINED_GROUPS_PROPERTY = "ecafe.processor.oku.groups";
    public static final String NOT_IN_AGETYPEGROUPS_PROPERTY = "ecafe.processor.oku.not.agetypegroups";

    public static final String PROPERTY_VALUE_SEPARATOR = ",";

    @PostConstruct
    private void init() {
        initPredefinedGroups();
        initAgeTypeGroups();
    }

    private void initPredefinedGroups() {
        String predefinedGroupsRawString = runtimeContext.getConfigProperties().getProperty(PREDEFINED_GROUPS_PROPERTY);
        if (StringUtils.isEmpty(predefinedGroupsRawString)) {
            clientGroupList.add(ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            clientGroupList.add(ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue());
            clientGroupList.add(ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            clientGroupList.add(ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
        } else {
            String[] predefinedGroupsStringArray = StringUtils.split(predefinedGroupsRawString, PROPERTY_VALUE_SEPARATOR);

            for (String predefinedGroupString : predefinedGroupsStringArray) {
                long predefinedGroupLong;
                try {
                    predefinedGroupLong = Long.parseLong(predefinedGroupString);
                } catch (NumberFormatException e) {
                    logger.error(String.format("Unexpected number of group: %s", predefinedGroupString));
                    continue;
                }

                ClientGroup.Predefined predefined = ClientGroup.Predefined.parse(predefinedGroupLong);
                if (predefined == null) {
                    logger.error(String.format("Group with id %s not found", predefinedGroupString));
                    continue;
                }

                clientGroupList.add(predefined.getValue());
            }
        }
    }

    private void initAgeTypeGroups() {
        String ageTypeGroupsRawString = runtimeContext.getConfigProperties().getProperty(NOT_IN_AGETYPEGROUPS_PROPERTY);
        if (StringUtils.isEmpty(ageTypeGroupsRawString))
            return;
        ///
        String[] ageTypeGroupsStringArray = StringUtils.split(ageTypeGroupsRawString, PROPERTY_VALUE_SEPARATOR);

        for (String ageTypeGroupString : ageTypeGroupsStringArray) {
            ageTypeGroupString = ageTypeGroupString.trim().toLowerCase();
            if (!StringUtils.isEmpty(ageTypeGroupString)) {
                ageTypeGroupList.add(ageTypeGroupString);
            } else {
                logger.error(String.format("Incorrect value in %s property", NOT_IN_AGETYPEGROUPS_PROPERTY));
            }
        }
    }

    @Transactional(readOnly = true)
    public ClientData checkClient(Long contractId, String surname) {
        Query query = emReport.createQuery("select c from Client c join c.person p  join c.org o "
                + "where c.contractId = :contractId and lower(p.surname) = :surname and o.participantOP = true "
                + "     and (c.clientGroup.compositeIdOfClientGroup.idOfClientGroup in (:clientGroupList) "
                + "         or ((c.clientGroup.compositeIdOfClientGroup.idOfClientGroup < :clientGroupEmployees) "
                + "             and lower(c.ageTypeGroup) not in (:notInAgeTypeGroupList)))");
        query.setParameter("contractId", contractId);
        query.setParameter("surname", surname.toLowerCase());
        query.setParameter("clientGroupList", clientGroupList);
        query.setParameter("clientGroupEmployees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
        query.setParameter("notInAgeTypeGroupList", ageTypeGroupList);
        query.setMaxResults(1);
        try {
            Client client = (Client) query.getSingleResult();
            return new ClientData(client.getOrg().getIdOfOrg());
        } catch (Exception e) {
            logger.info(String.format("Unable to find client with contractId=%s, surname=%s", contractId, surname));
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void setClientAsUserOP(Long contractId) throws Exception {
        Query query = em.createQuery("update Client c set c.userOP = true where c.contractId = :contractId");
        query.setParameter("contractId", contractId);
        int res = query.executeUpdate();
        if (res != 1) {
            throw new Exception("Client not found");
        }
    }

    @Transactional(readOnly = true)
    public Boolean checkClientByContractId(Long contractId) {
        Query query = emReport.createQuery("select c from Client c join c.person p "
                + "join c.org o where c.contractId = :contractId and c.userOP = true and o.participantOP = true");
        query.setParameter("contractId", contractId);
        query.setMaxResults(1);
        try {
            Client client = (Client) query.getSingleResult();
            return null != client;
        } catch (Exception e) {
            logger.info(String.format("Unable to find client with contractId=%s", contractId));
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Collection<Order> getOrdersByContractIdFromDate(Long contractId, Date orderedFrom) {
        Query query = emReport.createNativeQuery(
                "select distinct o.idoforder, o.idoforg, o.createddate, od.menudetailname as complex_name, a.menudetailname as dish_name, "
                        + "    g.guid, a.menuorigin as dish_menuorigin " + "from cf_orders o "
                        + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg "
                        + "    and menutype between :typeComplexMin and :typeComplexMax "
                        + "join cf_clients c on c.idofclient = o.idofclient "
                        + "join cf_orgs org on org.idoforg = c.idoforg "
                        + "left join cf_goods g on g.idofgood = od.idofgood "
                        + "join (select _od.idoforder, _od.idoforg, _od.menudetailname, _od.menutype, _od.menuorigin "
                        + "    from cf_orderdetails _od "
                        + "    where _od.menutype between :typeComplexItemMin and :typeComplexItemMax "
                        + ") a on a.idoforder = o.idoforder and a.idoforg = o.idoforg and (a.menutype = od.menutype + 100) "
                        + "where c.contractid = :contractId and o.orderdate > :orderedFrom and c.userop = true and org.participantop = true "
                        + "    and o.ordertype in (:orderTypeDefault,:orderTypeUnknown,:orderTypeVending,:orderTypePayPlan,:orderTypeSubscription, "
                        + "         :orderTypeReducedPricePlan, :orderTypeReducedPricePlanReserve) "
                        + "    and o.state = :orderCommittedState "
                        + "union all "
                        + "select distinct o.idoforder, o.idoforg, o.createddate, null as complex_name, od.menudetailname as dish_name, "
                        + "    null as guid, od.menuorigin as dish_menuorigin " + "from cf_orders o "
                        + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg and menutype = :typeDish "
                        + "join cf_clients c on c.idofclient = o.idofclient "
                        + "join cf_orgs org on org.idoforg = c.idoforg "
                        + "where c.contractid = :contractId and o.orderdate > :orderedFrom and c.userop = true and org.participantop = true and "
                        + "    o.ordertype in (:orderTypeDefault,:orderTypeUnknown,:orderTypeVending,:orderTypePayPlan,:orderTypeSubscription, "
                        + "         :orderTypeReducedPricePlan, :orderTypeReducedPricePlanReserve) "
                        + "    and o.state = :orderCommittedState");
        query.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("typeComplexItemMin", OrderDetail.TYPE_COMPLEX_ITEM_MIN);
        query.setParameter("typeComplexItemMax", OrderDetail.TYPE_COMPLEX_ITEM_MAX);
        query.setParameter("contractId", contractId);
        query.setParameter("orderedFrom", orderedFrom.getTime());
        query.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
        query.setParameter("orderTypeDefault", OrderTypeEnumType.UNKNOWN.ordinal());
        query.setParameter("orderTypeUnknown", OrderTypeEnumType.DEFAULT.ordinal());
        query.setParameter("orderTypeVending", OrderTypeEnumType.VENDING.ordinal());
        query.setParameter("orderTypePayPlan", OrderTypeEnumType.PAY_PLAN.ordinal());
        query.setParameter("orderTypeSubscription", OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal());
        query.setParameter("orderTypeReducedPricePlan", OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal());
        query.setParameter("orderTypeReducedPricePlanReserve", OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal());
        query.setParameter("orderCommittedState", ru.axetta.ecafe.processor.core.persistence.Order.STATE_COMMITED);

        List<?> list = query.getResultList();

        HashMap<CompositeIdOfOrder, Order> orderHashMap = new HashMap<>();
        HashMap<CompositeIdOfOrder, HashMap<String, Complex>> complexHashMap = new HashMap<>();
        for (Object o : list) {
            Object[] row = (Object[]) o;
            long idOfOrder = ((BigInteger) row[0]).longValue();
            long idOfOrg = ((BigInteger) row[1]).longValue();
            Date orderDate = new Date(((BigInteger) row[2]).longValue());
            String complexName = (null == row[3]) ? null : ((String) row[3]).replaceAll("^\"|\"$|(?<=\")\"", "");
            String dishName = (null == row[4]) ? null : ((String) row[4]).replaceAll("^\"|\"$|(?<=\")\"", "");
            String complexGuid = (null == row[5]) ? null : ((String) row[5]).replaceAll("^\"|\"$|(?<=\")\"", "");
            String dishMenuOrigin = (null == row[6]) ? "unknown" : OrderDetail.getMenuOriginAsCode((Integer) row[6]);

            CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(idOfOrg, idOfOrder);
            if (!orderHashMap.containsKey(compositeIdOfOrder)) {
                orderHashMap.put(compositeIdOfOrder, new Order(idOfOrder, idOfOrg, orderDate));
            }

            if (StringUtils.isEmpty(complexName)) {
                orderHashMap.get(compositeIdOfOrder).getDishes().add(new Dish(dishName, dishMenuOrigin));
            } else {
                if (!complexHashMap.containsKey(compositeIdOfOrder)) {
                    complexHashMap.put(compositeIdOfOrder, new HashMap<String, Complex>());
                }
                if (!complexHashMap.get(compositeIdOfOrder).containsKey(complexGuid)) {
                    Complex complex = new Complex(complexName);
                    complexHashMap.get(compositeIdOfOrder).put(complexGuid, complex);
                    orderHashMap.get(compositeIdOfOrder).getComplexes().add(complex);
                }
                complexHashMap.get(compositeIdOfOrder).get(complexGuid).getDishList()
                        .add(new Dish(dishName, dishMenuOrigin));
            }
        }
        return orderHashMap.values();
    }

    @Transactional(readOnly = true)
    public Collection<Order> getOrders(Date orderedFrom, Date orderedTo, Integer limit, Integer offset) {
        List<Long> clientIdList = getClients();
        if (clientIdList.isEmpty()) {
            return new ArrayList<>();
        }
        Query query = emReport.createNativeQuery(
                "select distinct a.contractid, a.idoforder, a.idoforg, a.createddate, od.menudetailname as complex_name, "
                        + "    odd.menudetailname as dish_name, g.guid, odd.menuorigin as dish_menuorigin " + "from ( "
                        + "    select o.idoforder, o.idoforg, c.contractid, o.createddate " + "    from cf_orders o "
                        + "    join cf_clients c on o.idofclient = c.idofclient "
                        + "    where c.idofclient in (:clientIdList) and o.createddate between :orderedFrom and :orderedTo "
                        + "        and o.ordertype in (:orderTypeDefault,:orderTypeUnknown,:orderTypeVending,:orderTypePayPlan,:orderTypeSubscription,"
                        + "             :orderTypeReducedPricePlan, :orderTypeReducedPricePlanReserve) "
                        + "        and o.state = :orderCommittedState "
                        + "    limit :_limit " + "    offset :_offset " + ") a "
                        + "left join cf_orderdetails od on a.idoforder = od.idoforder and a.idoforg = od.idoforg "
                        + "    and od.menutype between :typeComplexMin and :typeComplexMax "
                        + "left join cf_goods g on g.idofgood = od.idofgood "
                        + "left join cf_orderdetails odd on odd.idoforder = a.idoforder and odd.idoforg = a.idoforg "
                        + "    and (odd.menutype = od.menutype + 100 or odd.menutype = :typeDish)");
        query.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("orderedFrom", orderedFrom.getTime());
        query.setParameter("orderedTo", orderedTo.getTime());
        query.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);
        query.setParameter("orderTypeDefault", OrderTypeEnumType.UNKNOWN.ordinal());
        query.setParameter("orderTypeUnknown", OrderTypeEnumType.DEFAULT.ordinal());
        query.setParameter("orderTypeVending", OrderTypeEnumType.VENDING.ordinal());
        query.setParameter("orderTypePayPlan", OrderTypeEnumType.PAY_PLAN.ordinal());
        query.setParameter("orderTypeSubscription", OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal());
        query.setParameter("orderTypeReducedPricePlan", OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal());
        query.setParameter("orderTypeReducedPricePlanReserve", OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal());
        query.setParameter("_limit", limit);
        query.setParameter("_offset", offset);
        query.setParameter("clientIdList", clientIdList);
        query.setParameter("orderCommittedState", ru.axetta.ecafe.processor.core.persistence.Order.STATE_COMMITED);

        List<?> list = query.getResultList();

        HashMap<CompositeIdOfOrder, Order> orderHashMap = new HashMap<>();
        HashMap<CompositeIdOfOrder, HashMap<String, Complex>> complexHashMap = new HashMap<>();
        for (Object o : list) {
            Object[] row = (Object[]) o;
            Long contractId = ((BigInteger) row[0]).longValue();
            long idOfOrder = ((BigInteger) row[1]).longValue();
            long idOfOrg = ((BigInteger) row[2]).longValue();
            Date orderDate = new Date(((BigInteger) row[3]).longValue());
            String complexName = (null == row[4]) ? null : ((String) row[4]).replaceAll("^\"|\"$|(?<=\")\"", "");
            String dishName = (null == row[5]) ? null : ((String) row[5]).replaceAll("^\"|\"$|(?<=\")\"", "");
            String complexGuid = (null == row[6]) ? null : ((String) row[6]).replaceAll("^\"|\"$|(?<=\")\"", "");
            String dishMenuOrigin = (null == row[7]) ? "unknown" : OrderDetail.getMenuOriginAsCode((Integer) row[7]);

            CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(idOfOrg, idOfOrder);
            if (!orderHashMap.containsKey(compositeIdOfOrder)) {
                orderHashMap.put(compositeIdOfOrder, new Order(contractId, idOfOrder, idOfOrg, orderDate));
            }

            if (StringUtils.isEmpty(complexName)) {
                orderHashMap.get(compositeIdOfOrder).getDishes().add(new Dish(dishName, dishMenuOrigin));
            } else {
                if (!complexHashMap.containsKey(compositeIdOfOrder)) {
                    complexHashMap.put(compositeIdOfOrder, new HashMap<String, Complex>());
                }
                if (!complexHashMap.get(compositeIdOfOrder).containsKey(complexGuid)) {
                    Complex complex = new Complex(complexName);
                    complexHashMap.get(compositeIdOfOrder).put(complexGuid, complex);
                    orderHashMap.get(compositeIdOfOrder).getComplexes().add(complex);
                }
                complexHashMap.get(compositeIdOfOrder).get(complexGuid).getDishList()
                        .add(new Dish(dishName, dishMenuOrigin));
            }
        }
        return orderHashMap.values();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Long> getClients() {
        Query query = emReport.createQuery("select c.idOfClient from Client c join c.person p "
                + "join c.org o where c.userOP = true and o.participantOP = true");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationInfo(Long idOfOrg) {
        List<Long> friendlyOrgsIdList = new ArrayList<>();
        Query query = emReport.createNativeQuery("select friendlyorg from cf_friendly_organization where currentorg=:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        for (Object o: query.getResultList()) {
            friendlyOrgsIdList.add(((BigInteger)o).longValue());
        }


        query = emReport.createQuery("select o from Org o where o.idOfOrg in (:idOfOrgList) and o.participantOP = true");
        query.setParameter("idOfOrgList", friendlyOrgsIdList);
        List<Org> orgList = query.getResultList();

        List<Organization> resultList = new ArrayList<>();
        for (Org org : orgList) {
            resultList.add(new Organization(org));
        }

        return resultList;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Organization> getOrganizationInfoList(Date updatedFrom, Integer limit, Integer offset) {
        List<Organization> organizationList = new ArrayList<>();
        Query query = emReport.createQuery(
                "select o from Org o where o.participantOP = true and o.updateTime > :updatedFrom");
        query.setMaxResults(limit);
        query.setParameter("updatedFrom", updatedFrom);
        query.setFirstResult(offset);

        List<Org> list = query.getResultList();

        for (Org o : list) {
            organizationList.add(new Organization(o));
        }

        return organizationList;
    }

    public static List<Long> getClientGroupList() {
        return clientGroupList;
    }
}
