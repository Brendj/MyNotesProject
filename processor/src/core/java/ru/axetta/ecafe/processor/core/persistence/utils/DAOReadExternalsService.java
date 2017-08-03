/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .createQuery("select c.client.contractId from Card c where c.cardNo = :cardNo", Long.class);
            query.setParameter("cardNo", cardId);
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

    public boolean existClientPayment(Contragent contragent, PaymentRequest.PaymentRegistry.Payment payment) {
        String additionalCondition = "";
        if (payment.getAddIdOfPayment() == null || !payment.getAddIdOfPayment().startsWith(RNIPLoadPaymentsService.SERVICE_NAME)) {
            additionalCondition = String.format(" and cp.contragent.idOfContragent = %s", contragent.getIdOfContragent());
        }
        Query query = entityManager
                .createQuery("select count(cp.idOfClientPayment) from ClientPayment cp where cp.idOfPayment = :idOfPayment" + additionalCondition);
        query.setParameter("idOfPayment", payment.getIdOfPayment());
        return (Long)query.getSingleResult() > 0;
    }

    /*
    * Поиск клиента по idOfClient или contractId
    * */
    public Client findClient(Long idOfClient, Long contractId) throws Exception {
        if ((idOfClient != null && contractId != null) || (idOfClient == null && contractId == null))
            throw new Exception("Invalid arguments");
        try {
            String query_str = (idOfClient != null ? "select c from Client c where c.idOfClient = :parameter" : "select c from Client c where c.contractId = :parameter");
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
        Query query = entityManager.createQuery("select order from Order order where order.client = :client and order.createTime between "
                + ":startTime and :endTime order by createTime");
        query.setParameter("client", client);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        return query.getResultList();
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

}
