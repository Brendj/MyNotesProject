/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.core.sync.response.AccountTransactionExtended;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.jboss.as.web.security.SecurityContextAssociationValve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 12.05.16
 * Time: 15:50
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DAOReadonlyService {
    private final static Logger logger = LoggerFactory.getLogger(DAOReadonlyService.class);

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public static DAOReadonlyService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOReadonlyService.class);
    }

    public List<AccountTransactionExtended> getAccountTransactionsForOrgSinceTimeV2(Org org,
            Date fromDateTime, Date toDateTime) throws Exception {
        String str_query = "select t.idOfTransaction, t.source, t.transactionDate, " +
                "t.sourceType, t.transactionSum,  " +
                "coalesce(t.transactionSubBalance1Sum, 0) as transactionSubBalance1Sum, coalesce(query.complexsum, 0) as complexsum, " +
                "coalesce(query.discountsum, 0) as discountsum, coalesce(query.orderType, 0) as ordertype, t.idOfClient " +
                "from cf_transactions t left join " +
                "(select coalesce(sum(dd.qty * dd.rprice), 0) as complexsum, coalesce(sum(dd.socDiscount), 0) as discountsum,"
                + " oo.orderType, oo.idOfTransaction " +
                "from cf_orders oo join cf_orderdetails dd on oo.idOfOrder = dd.idOfOrder and oo.idOfOrg = dd.idOfOrg " +
                "where oo.createdDate > :orders_begDate AND oo.createddate <= :orders_endDate AND oo.idOfOrg in (:orgs) " +
                "AND dd.idOfOrg in (:orgs) AND dd.menuType between :menuMin and :menuMax " +
                "group by oo.orderType, oo.idOfTransaction) as query " +
                "on t.idOfTransaction = query.idOfTransaction " +
                "where t.idOfOrg in (:orgs) AND t.transactionDate > :trans_begDate AND t.transactionDate <= :trans_endDate " +
                "order by t.idOfClient";
        Session session = entityManager.unwrap(Session.class);
        //org = (Org) session.merge(org);
        SQLQuery q = session.createSQLQuery(str_query);
        // заказы будем искать за последние 24 часа от времени запроса
        q.setParameter("orders_begDate", CalendarUtils.addDays(toDateTime, -1).getTime());
        q.setParameter("orders_endDate",toDateTime.getTime());
        // транзакции будем искать строго от запрашиваемого времени
        q.setParameter("trans_begDate", fromDateTime.getTime());
        q.setParameter("trans_endDate", toDateTime.getTime());
        q.setParameterList("orgs",DAOUtils.findFriendlyOrgIds(session, org.getIdOfOrg()));
        q.setParameter("menuMin", OrderDetail.TYPE_COMPLEX_MIN);
        q.setParameter("menuMax", OrderDetail.TYPE_COMPLEX_MAX);
        q.setResultTransformer(Transformers.aliasToBean(AccountTransactionExtended.class));
        q.addScalar("idoftransaction").addScalar("source").addScalar("transactiondate").addScalar("sourcetype")
                .addScalar("transactionsum").addScalar("transactionsubbalance1sum")
                .addScalar("complexsum", StandardBasicTypes.BIG_DECIMAL)
                .addScalar("discountsum", StandardBasicTypes.BIG_DECIMAL).addScalar("ordertype")
                .addScalar("idofclient");
        return q.list();
    }

    public Org findOrg(Long idOfOrg) throws Exception {
        Org org = entityManager.find(Org.class, idOfOrg);
        if (null == org) {
            final String message = String.format("Unknown org with IdOfOrg == %s", idOfOrg);
            logger.error(message);
            throw new NullPointerException(message);
        }
        return org;
    }

    public List<Long> getClientsIdsWhereHasEnterEvents(long idOfOrg, Date beginDate, Date endDate) {
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery(" SELECT ee.idofclient " +
                " FROM cf_enterevents ee " +
                " WHERE ee.idoforg = :idOfOrg  AND ee.evtdatetime BETWEEN :beginDate AND :endDate " +
                " AND ee.idofclient IS NOT NULL AND ee.PassDirection IN (:passDirections)");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("beginDate", beginDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("passDirections",
                Arrays.asList(EnterEvent.ENTRY, EnterEvent.EXIT, EnterEvent.PASSAGE_RUFUSAL, EnterEvent.RE_ENTRY,
                        EnterEvent.RE_EXIT, EnterEvent.DETECTED_INSIDE, EnterEvent.CHECKED_BY_TEACHER_EXT, EnterEvent.CHECKED_BY_TEACHER_INT));
        query.addScalar("idofclient", StandardBasicTypes.LONG);
        List result = query.list();
        if (result == null)
            return new ArrayList<Long>();
        return result;
    }

    public User getUserFromSession() {
        try {
            HttpServletRequest request = SecurityContextAssociationValve.getActiveRequest().getRequest();
            HttpSession httpSession = request.getSession(true);
            Long idOfUser = (Long)httpSession.getAttribute(User.USER_ID_ATTRIBUTE_NAME);
            User user = findUserById(idOfUser);
            return user;
        } catch (Exception e) {
            //logger.error(String.format("Can't retrieve user from current Session. Message: %s", e.getMessage()));
            return null;
        }
    }

    public User findUserById(long idOfUser) throws Exception {
        User user = entityManager.find(User.class, idOfUser);
        return user;
    }

    public Order findOrder(Long idOfOrg, Long idOfOrder) {
        Order order = entityManager.find(Order.class, new CompositeIdOfOrder(idOfOrg, idOfOrder));
        return order;
    }

    public Client findClientById(long idOfClient) throws Exception {
        Client client = entityManager.find(Client.class, idOfClient);
        return client;
    }

    public Long getClientIdByContract(Long contractId) {
        try {
            Query query = entityManager
                    .createQuery("select c.idOfClient from Client c where c.contractId = :contractId", Long.class);
            query.setParameter("contractId", contractId);
            return (Long) query.getSingleResult();
        } catch (Exception e) {
            logger.error("error retrieving ifOfClient from contractId", e);
            return null;
        }
    }

    public ClientGuardian findClientGuardianById(Session session, long idOfChildren, long idOfGuardian) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfChildren));
        criteria.add(Restrictions.eq("idOfGuardian", idOfGuardian));
        criteria.add(Restrictions.ne("deletedState", true));
        criteria.add(Restrictions.eq("disabled", false));
        return (ClientGuardian)criteria.uniqueResult();
    }

    public List<Long> findClientGuardiansByMobile(Long idOfChildren, String mobile) {
        Query query = entityManager.createQuery("select cg.idOfClientGuardian from ClientGuardian cg, Client child, Client guardian where guardian.idOfClient = cg.idOfGuardian " +
                "and child.idOfClient = cg.idOfChildren " +
                "and child.idOfClient = :idOfChildren " +
                "and cg.deletedState = false and cg.disabled = false and guardian.idOfClientGroup not in (:leaving, :deleted) " +
                "and guardian.mobile = :mobile", Long.class);
        query.setParameter("idOfChildren", idOfChildren);
        query.setParameter("leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        query.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
        query.setParameter("mobile", mobile);
        return query.getResultList();
    }

    public List<Long> findContractsBySsoid(String ssoid) {
        if (ssoid == null || ssoid.trim().equals("") || ssoid.equals(EMPProcessor.SSOID_FAILED_TO_REGISTER)
                || ssoid.equals(EMPProcessor.SSOID_REGISTERED_AND_WAITING_FOR_DATA)) {
            return null;
        }
        Query query = entityManager.createQuery("select c.contractId from Client c where c.ssoid = :ssoid", Long.class);
        query.setParameter("ssoid", ssoid);
        return query.getResultList();
    }

    /**
     *
     * @param guardianId
     * @param clientId
     * @param notifyType
     * @return Возвращает факт того, что у связки клиент-представитель включен хотя бы один из типов уведомления из notifyTypes
     */
    //TODO - надо ли возвращать true, когда все флаги выключены, но forcesend в конфиге выставлена в 1 - ВОПРОС ??
    public Boolean allowedGuardianshipNotification(Long guardianId, Long clientId, Long notifyType) throws Exception {
        ClientGuardianNotificationSetting.Predefined predefined = ClientGuardianNotificationSetting.Predefined.parse(notifyType);
        if (predefined == null) {
            return true;
        }
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Query query = session
                .createSQLQuery("select notifyType from cf_client_guardian_notificationsettings n " +
                        "where idOfClientGuardian = (select idOfClientGuardian from cf_client_guardian cg " +
                        "where cg.disabled = 0 and cg.IdOfChildren = :idOfChildren and cg.IdOfGuardian = :idOfGuardian and cg.deletedState = false)");
        query.setParameter("idOfChildren", clientId);
        query.setParameter("idOfGuardian", guardianId);
        List resultList = query.list();
        if (resultList.size() < 1 && predefined.isEnabledAtDefault()) {
            return true;
        }
        for (Object o : resultList) {
            BigInteger bi = (BigInteger) o;
            if (bi.longValue() == predefined.getValue()) {
                return true;
            }
        }
        return false;
    }

    public List<ItemListByGuardMobile> extractClientItemListFromGuardByGuardMobile(String guardMobile) {
        String query = "select client.idOfClient, client.contractId, client.san, 0 as disabled from cf_clients client where client.phone=:guardMobile or client.mobile=:guardMobile";
        Query q = entityManager.createNativeQuery(query, ItemListByGuardMobile.class);
        q.setParameter("guardMobile", guardMobile);
        List<ItemListByGuardMobile> clients = q.getResultList();

        if (clients != null && !clients.isEmpty()){
            List<ItemListByGuardMobile> clientsCopy = new ArrayList<ItemListByGuardMobile>(clients);
            for (ItemListByGuardMobile item : clientsCopy) {
                Long id = item.getIdOfClient();
                Query q2 = entityManager.createNativeQuery("select cl.idOfClient, cl.contractid, cl.san, cg.disabled from cf_client_guardian cg inner join cf_clients cl " +
                        "on cl.idOfClient = cg.idOfChildren " +
                        "where cg.idOfGuardian = :idOfGuardian and cg.deletedState = false", ItemListByGuardMobile.class);
                q2.setParameter("idOfGuardian", id);
                List<ItemListByGuardMobile> list = q2.getResultList();
                if (list != null && list.size() > 0) {
                    for (ItemListByGuardMobile cg : list) {
                        if (cg.getDisabled().equals(1)) {
                            clients.add(cg);
                        }
                    }
                    clients.remove(item);
                }
            }
        }

        Set<ItemListByGuardMobile> tempSet = new HashSet<ItemListByGuardMobile>();
        tempSet.addAll(clients);
        clients.clear();
        clients.addAll(tempSet);
        return clients;
    }

    public List<Long> extractIDFromGuardByGuardMobile(String guardMobile) {
        Set<Long> result = new HashSet<Long>();
        String query = "select client.idOfClient from Client client where client.phone=:guardMobile or client.mobile=:guardMobile"; //все клиенты с номером телефона
        Query q = entityManager.createQuery(query, Long.class);
        q.setParameter("guardMobile", guardMobile);
        List<Long> clients = q.getResultList();

        if (clients != null && !clients.isEmpty()){
            for(Long id : clients){
                Query q2 = entityManager.createQuery("select cg from ClientGuardian cg " +
                        "where cg.idOfGuardian = :idOfGuardian and cg.deletedState = false", ClientGuardian.class);  //все дети текущего клиента
                q2.setParameter("idOfGuardian", id);
                List<ClientGuardian> list = q2.getResultList();
                if (list != null && list.size() > 0) {
                    for (ClientGuardian cg : list) {
                        if (!cg.isDisabled()) {
                            result.add(cg.getIdOfChildren());
                        }
                    }
                } else {
                    result.add(id);
                }
            }
        }

        return new ArrayList<Long>(result);
    }

    public MenuDetail getMenuDetailConstitutionByOrder(Long idOfMenuFromSync, Org orgFromOrder, Date orderDate) {
        Date endDate  = CalendarUtils.addOneDay(orderDate);

        Query query = entityManager.createQuery(
                "SELECT cfm "
                        + "FROM MenuDetail cfm left join cfm.menu cm "
                        + "WHERE cfm.idOfMenuFromSync = :idofmenufromsync "
                        + "AND cm.org = :idoforg "
                        + "AND cm.menuDate between :orderdate and :enddate "
                        + "ORDER BY cfm.idOfMenuDetail DESC");
        query.setParameter("idofmenufromsync", idOfMenuFromSync);
        query.setParameter("idoforg", orgFromOrder);
        query.setParameter("orderdate", orderDate);
        query.setParameter("enddate", endDate);
        query.setMaxResults(1);

        try {
            MenuDetail menuDetail = (MenuDetail) query.getSingleResult();
            return menuDetail;
        } catch (Exception e) {
            return null;
        }
    }

    public TaloonApproval findTaloonApproval(Long idOfOrg, Date taloonDate, String taloonName, String goodsGuid) {
        try {
            Query query = entityManager.createQuery("SELECT taloon from TaloonApproval taloon "
                + "where taloon.idOfOrg = :idOfOrg "
                + "and taloon.taloonDate = :taloonDate "
                + "and taloon.taloonName = :taloonName "
                + "and taloon.goodsGuid = :goodsGuid");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("taloonDate", taloonDate);
            query.setParameter("taloonName", taloonName);
            query.setParameter("goodsGuid", goodsGuid);
            return (TaloonApproval)query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer findTaloonApprovalSoldedQty(Long idOfOrg, Date taloonDate, String taloonName, String goodsGuid) {
        Date dateEnd = CalendarUtils.addOneDay(taloonDate);
        try {
            String goodsJoin = "";
            String goodsParam = "";
            if(StringUtils.isNotEmpty(goodsGuid)) {
                goodsJoin = "inner join cf_goods g on g.idofgood = od.idofgood ";
                goodsParam = "and g.guid =:goodsGuid ";
            } else {
                goodsParam = "and od.idofgood is null ";
            }
            Query query = entityManager.createNativeQuery("SELECT sum(od.qty) from cf_orderDetails od "
                    + "inner join cf_orders o on od.idOfOrg = o.idOfOrg and od.idOfOrder = o.idOfOrder " + goodsJoin
                    + "where od.idOfOrg= :idOfOrg "
                    + "and od.menuDetailName = :taloonName "
                    + "and od.MenuType >= :complexMin "
                    + "and od.MenuType <= :complexMax "
                    + "and o.orderDate >= :taloonDate "
                    + "and o.orderDate < :dateEnd "
                    + goodsParam);
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("taloonName", taloonName);
            query.setParameter("complexMin", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("complexMax", OrderDetail.TYPE_COMPLEX_MAX);
            query.setParameter("taloonDate", taloonDate.getTime());
            query.setParameter("dateEnd", dateEnd.getTime());
            if(StringUtils.isNotEmpty(goodsGuid)) {
                query.setParameter("goodsGuid", goodsGuid);
            }
            Object result = query.getSingleResult();
            return result != null ? ((BigInteger) result).intValue() : 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public List getPaymentsList(Client client, Integer subBalanceNum, Date endDate, Date startDate) {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Query query = entityManager.createQuery("select cp from ClientPayment cp inner join cp.transaction tr where cp.payType = :payType and "
                + "cp.createTime >= :startDate and cp.createTime < :endDate and tr.client.idOfClient = :idOfClient order by cp.createTime asc");
        query.setParameter("payType", subBalanceNum != null && subBalanceNum.equals(1) ? ClientPayment.CLIENT_TO_SUB_ACCOUNT_PAYMENT : ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", nextToEndDate);
        query.setParameter("idOfClient", client.getIdOfClient());
        return query.getResultList();
    }

    public List<ComplexInfo> findComplexesWithSubFeeding(Org org, Boolean isParent) {
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());
        Set<Integer> idOfComplex = new HashSet<Integer>(DiscountRule.COMPLEX_COUNT);
        for (int i=0; i< DiscountRule.COMPLEX_COUNT; i++){
            idOfComplex.add(i);
        }
        if(!isParent){
            Session session = entityManager.unwrap(Session.class);
            Criteria criteria = session.createCriteria(ComplexRole.class);
            String arrayOfFilterText = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_ARRAY_OF_FILTER_TEXT);
            for (String filter : arrayOfFilterText.split(";")){
                criteria.add(Restrictions.ilike("extendRoleName", filter, MatchMode.ANYWHERE));
            }
            criteria.setProjection(Projections.property("idOfRole"));
            List list = criteria.list();
            for (Object obj: list){
                idOfComplex.remove(Integer.valueOf(obj.toString()));
            }
        }
        final String sql;
        sql = "select distinct ci from ComplexInfo ci "
                + " where ci.org = :org and usedSubscriptionFeeding = 1 "
                + " and menuDate >= :startDate and menuDate < :endDate "
                + " and ci.idOfComplex in :idOfComplex";
        Date endDate = today;
        endDate = CalendarUtils.addDays(endDate, 7);
        TypedQuery<ComplexInfo> query = entityManager.createQuery(sql,
                ComplexInfo.class).setParameter("org", org).setParameter("startDate", today)
                .setParameter("endDate", endDate).setParameter("idOfComplex", idOfComplex);
        List<ComplexInfo> res = query.getResultList();
        return res;
    }

}