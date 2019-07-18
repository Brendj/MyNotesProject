/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfOrder;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.ClientData;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.Complex;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.Dish;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.Order;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Scope("singleton")
public class OkuDAOService {

    private static final Logger logger = LoggerFactory.getLogger(OkuDAOService.class);

    private static List<Long> clientGroupList = new ArrayList<>();

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @PersistenceContext(unitName = "reportsPU")
    private EntityManager emReport;

    @PostConstruct
    private void init() {
        clientGroupList.add(ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
    }

    @Transactional(readOnly = true)
    public ClientData checkClient(Long contractId, String surname) {
        Query query = emReport.createQuery(
                "select c from Client c join c.person p where c.contractId = :contractId and lower(p.surname) = :surname");
        query.setParameter("contractId", contractId);
        query.setParameter("surname", surname.toLowerCase());
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
        Query query = emReport.createQuery(
                "select c from Client c join c.person p where c.contractId = :contractId and c.userOP = true");
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
                "select o.idoforder, o.idoforg, o.createddate, od.menudetailname as complex_name, a.menudetailname as dish_name, g.guid "
                 + "from cf_orders o "
                 + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg and menutype between :typeComplexMin and :typeComplexMax "
                 + "join cf_clients c on c.idofclient = o.idofclient "
                 + "left join cf_goods g on g.idofgood = od.idofgood "
                 + "join (select _od.idoforder, _od.idoforg, _od.menudetailname, _od.menutype "
                 + "    from cf_orderdetails _od "
                 + "    where _od.menutype between :typeComplexItemMin and :typeComplexItemMax "
                 + ") a on a.idoforder = o.idoforder and a.idoforg = o.idoforg and (a.menutype = od.menutype + 100) "
                 + "where c.contractid = :contractId and o.orderdate > :orderedFrom "
                 + "union all "
                 + "select o.idoforder, o.idoforg, o.createddate, null as complex_name, od.menudetailname as dish_name, null as guid "
                 + "from cf_orders o "
                 + "join cf_orderdetails od on od.idoforder = o.idoforder and od.idoforg = o.idoforg and menutype = :typeDish "
                 + "join cf_clients c on c.idofclient = o.idofclient "
                 + "where c.contractid = :contractId and o.orderdate > :orderedFrom");
        query.setParameter("typeComplexMin", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("typeComplexMax", OrderDetail.TYPE_COMPLEX_MAX);
        query.setParameter("typeComplexItemMin", OrderDetail.TYPE_COMPLEX_ITEM_MIN);
        query.setParameter("typeComplexItemMax", OrderDetail.TYPE_COMPLEX_ITEM_MAX);
        query.setParameter("contractId", contractId);
        query.setParameter("orderedFrom", orderedFrom.getTime());
        query.setParameter("typeDish", OrderDetail.TYPE_DISH_ITEM);

        List list = query.getResultList();

        HashMap<CompositeIdOfOrder, Order> orderHashMap = new HashMap<>();
        HashMap<CompositeIdOfOrder, HashMap<String, Complex>> complexHashMap = new HashMap<>();
        for (Object o : list) {
            Object[] row = (Object[]) o;
            Long idOfOrder = ((BigInteger) row[0]).longValue();
            Long idOfOrg = ((BigInteger) row[1]).longValue();
            Date orderDate = new Date(((BigInteger) row[2]).longValue());
            String complexName = (null == row[3]) ? null : ((String) row[3]).replaceAll("^\"|\"$|(?<=\")\"", "");
            String dishName = (null == row[4]) ? null : ((String) row[4]).replaceAll("^\"|\"$|(?<=\")\"", "");
            String complexGuid = (null == row[5]) ? null : ((String) row[5]).replaceAll("^\"|\"$|(?<=\")\"", "");

            CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(idOfOrg, idOfOrder);
            if (!orderHashMap.containsKey(compositeIdOfOrder)) {
                orderHashMap.put(compositeIdOfOrder, new Order(idOfOrder, idOfOrg, orderDate));
            }

            if (StringUtils.isEmpty(complexName)) {
                orderHashMap.get(compositeIdOfOrder).getComposition().add(new Dish(dishName));
            } else {
                if (!complexHashMap.containsKey(compositeIdOfOrder)) {
                    complexHashMap.put(compositeIdOfOrder, new HashMap<String, Complex>());
                }
                if (!complexHashMap.get(compositeIdOfOrder).containsKey(complexGuid)) {
                    Complex complex = new Complex(complexName, complexGuid);
                    complexHashMap.get(compositeIdOfOrder).put(complexGuid, complex);
                    orderHashMap.get(compositeIdOfOrder).getComposition().add(complex);
                }
                complexHashMap.get(compositeIdOfOrder).get(complexGuid).getDishList().add(new Dish(dishName));
            }
        }
        return orderHashMap.values();
    }

    public static List<Long> getClientGroupList() {
        return clientGroupList;
    }
}
