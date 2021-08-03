/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplexesItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDish;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtMenuGroup;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.*;

/***
 *
 * Класс для получения данных, используемых во взаимодействии с платежными контрагентами, ПГУ и МП
 * Отдельный слейв БД для минимизации времени отклика вызовов внешних сервисов.
 * Не использовать для получения данных в методах синхронизации и внутренних сервисов!
 *
 ***/
@Component
@Scope("singleton")
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class DAOReadExternalsService {
    private final static Logger logger = LoggerFactory.getLogger(DAOReadExternalsService.class);

    @PersistenceContext(unitName = "externalServicesPU")
    private EntityManager entityManager;

    public static DAOReadExternalsService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOReadExternalsService.class);
    }

    public String getContragentPublicKeyString(Long idOfContragent) throws Exception {
        try {
            Query query = entityManager
                .createQuery("select c.publicKey from Contragent c where c.idOfContragent = :idOfContragent",
                        String.class);
            query.setParameter("idOfContragent", idOfContragent);
            return (String) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NullPointerException("Unknown contragent with id == "+idOfContragent);
        }
    }

    public Long getContractIdByCardNo(long cardId) {
        try {
            Query query = entityManager
                .createQuery("select c.client.contractId from Card c where c.cardNo = :cardNo order by c.updateTime desc", Long.class);
            query.setParameter("cardNo", cardId);
            query.setMaxResults(1);
            return (Long) query.getSingleResult();
        } catch (Exception ignore) {
            return null;
        }
    }

    public Contragent findContragent(Long idOfContragent) {
        try {
            Query query = entityManager
                    .createQuery("select c from Contragent c where c.idOfContragent = :idOfContragent", Contragent.class)
                    .setParameter("idOfContragent", idOfContragent);
            return (Contragent) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Contragent findContragentByClient(Long clientContractId) {
        try {
            Query query = entityManager
                    .createQuery("select c.org.defaultSupplier from Client c where c.contractId = :contractId", Contragent.class)
                    .setParameter("contractId", clientContractId);
            return (Contragent) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Long existingClientPayment(Contragent contragent, PaymentRequest.PaymentRegistry.Payment payment) {
        String additionalCondition = "";
        if (payment.getAddIdOfPayment() == null || !payment.getAddIdOfPayment().startsWith(RNIPLoadPaymentsService.SERVICE_NAME)) {
            additionalCondition = String.format(" and cp.contragent.idOfContragent = %s", contragent.getIdOfContragent());
        }
        Query query = entityManager
                .createQuery("select cp.idOfClientPayment from ClientPayment cp where cp.idOfPayment = :idOfPayment" + additionalCondition);
        query.setParameter("idOfPayment", payment.getIdOfPayment());
        List<Long> list = query.getResultList();
        if (list.size() == 0) return null;
        return list.get(0);
    }

    /*
    * Поиск клиента по idOfClient или contractId
    * */
    public Client findClient(Long idOfClient, Long contractId) throws Exception {
        if ((idOfClient != null && contractId != null) || (idOfClient == null && contractId == null))
            throw new Exception("Invalid arguments");
        try {
            String query_str = (idOfClient != null ?
                    "select c from Client c join fetch c.org o join fetch o.defaultSupplier where c.idOfClient = :parameter"
                    : "select c from Client c join fetch c.org o join fetch o.defaultSupplier where c.contractId = :parameter");
            Query query = entityManager.createQuery(query_str);
            query.setParameter("parameter", idOfClient != null ? idOfClient : contractId);
            return (Client) query.getSingleResult();
        } catch(Exception e) {
            return null;
        }
    }

    public ContragentClientAccount findContragentClientAccount(CompositeIdOfContragentClientAccount compositeIdOfContragentClientAccount) {
        return entityManager.find(ContragentClientAccount.class, compositeIdOfContragentClientAccount);
    }

    public Org findOrg(Long idOfOrg) {
        return entityManager.find(Org.class, idOfOrg);
    }

    public Contragent getOrgDefaultSupplier(Long idOfOrg) {
        try {
            return entityManager.find(Contragent.class, findOrg(idOfOrg).getDefaultSupplier().getIdOfContragent());
        } catch (Exception e) {
            return null;
        }
    }

    public Client refreshClient(Client client) {
        return entityManager.merge(client);
    }

    public Person findPerson(Long idOfPerson) {
        return entityManager.find(Person.class, idOfPerson);
    }

    public List<Client> findGuardiansByClient(Long idOfChildren, Long idOfGuardian) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return ClientManager.findGuardiansByClient(session, idOfChildren, idOfGuardian);
    }

    public Boolean allowedGuardianshipNotification(Long guardianId, Long clientId, Long notifyType) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return ClientManager.allowedGuardianshipNotification(session, guardianId, clientId, notifyType);
    }

    public List<Order> getClientOrdersByPeriod(Client client, Date startTime, Date endTime) {
        /*Query query = entityManager.createQuery("select order from Order order where order.client = :client and order.createTime between "
                + ":startTime and :endTime order by createTime");
        query.setParameter("client", client);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        return query.getResultList();*/
        Query query = entityManager.createNativeQuery("SELECT idoforg, idoforder FROM (SELECT idoforg, idoforder, createddate FROM CF_Orders order0_ WHERE order0_.IdOfClient = :client) qq "
                + "WHERE (cast(CreatedDate AS NUMERIC) BETWEEN :startTime AND :endTime)");
        query.setParameter("client", client.getIdOfClient());
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        List list = query.getResultList();

        List<Order> result = new ArrayList<>();
        if (list.size() == 0) return result;

        List<CompositeIdOfOrder> list2 = new ArrayList<>();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            long idOfOrg = HibernateUtils.getDbLong(row[0]);
            long idOfOrder = HibernateUtils.getDbLong(row[1]);
            list2.add(new CompositeIdOfOrder(idOfOrg, idOfOrder));
        }
        Query q = entityManager.createQuery("select order from Order order where order.compositeIdOfOrder in :list order by createTime");
        q.setParameter("list", list2);
        return q.getResultList();
    }

    public List<OrderDetail> getOrderDetailsByOrders(List<Order> orders) {
        Query query = entityManager.createQuery("select detail from OrderDetail detail where detail.order in :orders");
        query.setParameter("orders", orders);
        return query.getResultList();
    }

    public List<MenuDetail> getMenuDetailsByOrderDetails(Set<Long> orgIds, Set<Long> menuIds, Date startDate, Date endDate) {
        Date sDate = CalendarUtils.truncateToDayOfMonth(startDate);
        Date eDate = CalendarUtils.addOneDay(CalendarUtils.truncateToDayOfMonth(endDate));

        Query query = entityManager.createQuery("SELECT cfm FROM MenuDetail cfm left join cfm.menu cm "
                + "WHERE cfm.idOfMenuFromSync in :idOfMenus AND cm.org.idOfOrg in :orgIds "
                + "AND cm.menuDate between :startDate and :endDate ORDER BY cfm.idOfMenuDetail DESC");
        query.setParameter("idOfMenus", menuIds);
        query.setParameter("orgIds", orgIds);
        query.setParameter("startDate", sDate);
        query.setParameter("endDate", eDate);

        return query.getResultList();
    }

    public Set<WtDish> getWtDishesByOrderDetails(List<OrderDetail> detailsList, Date startDate, Date endDate) {
        Set<Long> dishIds = new HashSet<>();
        for (OrderDetail od : detailsList) {
            if (od.getIdOfDish() != null) {
                dishIds.add(od.getIdOfDish());
            }
        }
        if (dishIds.size() > 0) {
            Query query = entityManager.createQuery(
                    "SELECT dish FROM WtDish dish WHERE dish.idOfDish in :dishIds AND dish.deleteState = 0 " +
                            "AND ((dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding >= :endDate) "
                            + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding >= :endDate) "
                            + "OR (dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
                            + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL))");
            query.setParameter("dishIds", dishIds);
            query.setParameter("startDate", startDate, TemporalType.DATE);
            query.setParameter("endDate", endDate, TemporalType.DATE);
            List<WtDish> res = query.getResultList();
            if (res != null && res.size() > 0) {
                return new HashSet<>(res);
            }
        }
        return null;
    }

    public List getPaymentsList(Client client, Integer subBalanceNum, Date endDate, Date startDate) {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        /*Query query = entityManager.createQuery("select cp from ClientPayment cp inner join cp.transaction tr where cp.payType = :payType and "
                + "cp.createTime >= :startDate and cp.createTime < :endDate and tr.client.idOfClient = :idOfClient order by cp.createTime asc");
        query.setParameter("payType", subBalanceNum != null && subBalanceNum.equals(1) ? ClientPayment.CLIENT_TO_SUB_ACCOUNT_PAYMENT : ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", nextToEndDate);
        query.setParameter("idOfClient", client.getIdOfClient());
        return query.getResultList();*/
        Query query = entityManager.createNativeQuery("select cp.idofclientpayment, cp.paysum, cp.createddate, cp.paymentmethod, cp.addpaymentmethod, cp.addidofpayment, cg.contragentname "
                + "from CF_ClientPayments cp join CF_Transactions tt on cp.IdOfTransaction=tt.IdOfTransaction "
                + "left join cf_contragents cg on cp.idofcontragent = cg.idofcontragent "
                + "where cp.PayType = :payType and cast(cp.CreatedDate as numeric) >= :startDate and cast(cp.CreatedDate as numeric) < :endDate and tt.IdOfClient = :idOfClient");
        query.setParameter("payType", subBalanceNum != null && subBalanceNum.equals(1) ? ClientPayment.CLIENT_TO_SUB_ACCOUNT_PAYMENT : ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", nextToEndDate.getTime());
        query.setParameter("idOfClient", client.getIdOfClient());
        return query.getResultList();
    }

    public List<ComplexInfo> findComplexesWithSubFeeding(Org org, Boolean isParent, boolean vp) {
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
        if (vp) {
            sql = "select distinct ci from ComplexInfo ci "
                    + " where ci.org = :org and usedVariableFeeding = 1 "
                    + " and menuDate >= :startDate and menuDate < :endDate "
                    + " and ci.idOfComplex in :idOfComplex";
        } else {
            sql = "select distinct ci from ComplexInfo ci "
                    + " where ci.org = :org and usedSubscriptionFeeding = 1 "
                    + " and menuDate >= :startDate and menuDate < :endDate "
                    + " and ci.idOfComplex in :idOfComplex";
        }
        Date endDate = today;
        Integer addDays = org.getConfigurationProvider().getMenuSyncCountDays();
        if (addDays == null) addDays = 7;
        endDate = CalendarUtils.addDays(endDate, addDays);

        today = CalendarUtils.addDays(today, -7); //todo в будущем нужна настройка на сдвиг начальной даты

        TypedQuery<ComplexInfo> query = entityManager.createQuery(sql,
                ComplexInfo.class).setParameter("org", org).setParameter("startDate", today)
                .setParameter("endDate", endDate).setParameter("idOfComplex", idOfComplex);
        List<ComplexInfo> res = query.getResultList();
        return res;
    }

    public List<MenuDetail> getMenuDetailsByIdOfComplexInfo(Long idOfComplexInfo) {
        Query query = entityManager.createQuery("select md from ComplexInfoDetail cid join cid.menuDetail md where cid.complexInfo.idOfComplexInfo = :idOfComplexInfo")
                .setParameter("idOfComplexInfo", idOfComplexInfo);
        return query.getResultList();
    }

    public int isClientInside(Session session, long idOfClient) {
        int result = 0;
        Date beginDate = CalendarUtils.truncateToDayOfMonth(new Date());
        Date endDate = CalendarUtils.addOneDay(beginDate);
        SQLQuery query = session.createSQLQuery("SELECT ee.passDirection " +
                " FROM cf_enterevents ee " +
                " WHERE ee.idofclient = :idOfClient  AND ee.evtdatetime BETWEEN :beginDate AND :endDate " +
                " order by ee.EvtDateTime desc limit 1");
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("beginDate", beginDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        List<Integer> passDirections = Arrays.asList(EnterEvent.ENTRY, EnterEvent.RE_ENTRY, EnterEvent.DIRECTION_ENTER,
                EnterEvent.DETECTED_INSIDE, EnterEvent.CHECKED_BY_TEACHER_EXT, EnterEvent.CHECKED_BY_TEACHER_INT, EnterEvent.QUERY_FOR_ENTER);
        query.addScalar("passDirection", StandardBasicTypes.INTEGER);
        Integer pd = (Integer)query.uniqueResult();
        if (pd != null) {
            if (passDirections.contains(pd)) result = 1;
        }
        return result; //0 - по событиям проходов нет в ОО, 1 - находится в ОО
    }

    public List<WtDish> getWtDishesByWtComplex(WtComplex wtComplex) {
        Query query = entityManager.createQuery(
                "SELECT DISTINCT dish FROM WtDish dish LEFT JOIN dish.complexItems complexItems "
                        + "LEFT JOIN complexItems.wtComplex complex "
                        + "WHERE complex = :complex "
                        + "AND dish.deleteState = 0");
        query.setParameter("complex", wtComplex);
        return query.getResultList();
    }

    public List<WtDish> getWtDishesByComplexAndDates(WtComplex complex, Date startDate, Date endDate) {
        Query query = entityManager.createQuery("SELECT DISTINCT dish FROM WtDish dish "
                + "LEFT JOIN dish.complexItems complexItems "
                + "LEFT JOIN complexItems.wtComplex complex "
                + "WHERE complex = :complex "
                + "AND dish.deleteState = 0 "
                + "AND ((dish.dateOfBeginMenuIncluding < :startDate AND dish.dateOfEndMenuIncluding > :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding > :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding < :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL))");
        query.setParameter("complex", complex);
        query.setParameter("startDate", startDate, TemporalType.TIMESTAMP);
        query.setParameter("endDate", endDate, TemporalType.TIMESTAMP);
        return (List<WtDish>) query.getResultList();
    }

    public Set<Long> getDishesRepeatable(WtComplex wtComplex) {
        Set<Long> result = new HashSet<>();
        if (!wtComplex.getComposite()) return result;
        Query query = entityManager.createNativeQuery("select t.idofdish from cf_wt_complexes_dishes_repeatable t "
                + "where t.idofcomplex = :idOfComplex");
        query.setParameter("idOfComplex", wtComplex.getIdOfComplex());
        List list = query.getResultList();
        for (Object entry : list) {
            Long id = ((BigInteger)entry).longValue ();
            result.add(id);
        }
        return result;
    }

    public List<WtDish> getWtDishesByComplexItemAndDates(WtComplexesItem complexItem, Date startDate, Date endDate) {
        Query query = entityManager.createQuery("SELECT DISTINCT dish FROM WtDish dish join dish.complexItems complex "
                + "WHERE complex = :complexItem "
                + "AND dish.deleteState = 0 "
                + "AND ((dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding >= :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding >= :endDate) "
                + "OR (dish.dateOfBeginMenuIncluding <= :startDate AND dish.dateOfEndMenuIncluding IS NULL) "
                + "OR (dish.dateOfBeginMenuIncluding IS NULL AND dish.dateOfEndMenuIncluding IS NULL))");
        query.setParameter("complexItem", complexItem);
        query.setParameter("startDate", startDate, TemporalType.DATE);
        query.setParameter("endDate", endDate, TemporalType.DATE);
        return (List<WtDish>) query.getResultList();
    }

    public WtMenuGroup getWtMenuGroupByWtDish(WtDish wtDish) {
        Query query = entityManager.createQuery(
                "SELECT menuGroup FROM WtMenuGroup menuGroup "
                        + "LEFT JOIN menuGroup.menuGroupMenus menuGroupMenus "
                        + "WHERE :wtDish IN ELEMENTS(menuGroupMenus.dishes) "
                        + "AND menuGroup.deleteState = 0");
        query.setParameter("wtDish", wtDish);
        List<WtMenuGroup> res = query.getResultList();
        return res.size() > 0 ? res.get(0) : null;
    }
}
