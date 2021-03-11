/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.model.OrgDeliveryInfo;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.core.sync.response.AccountTransactionExtended;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    public static final String CARD_NOT_FOUND = "Карта не найдена";
    public static final String SEVERAL_CARDS_FOUND = "Найдено более одной карты";

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public static DAOReadonlyService getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOReadonlyService.class);
    }

    public List<AccountTransactionExtended> getAccountTransactionsForOrgSinceTimeV2(Org org, Date fromDateTime,
            Date toDateTime) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        List<Long> friendlyOrgIds = DAOUtils.findFriendlyOrgIds(session, org.getIdOfOrg());
        if (!OrgRepository.getInstance()
                .transactionsExistByLastProcessSectionDates(friendlyOrgIds, fromDateTime, toDateTime)) {
            return new ArrayList<AccountTransactionExtended>();
        }
        String str_query =
                "select t.idOfTransaction, t.source, t.transactionDate, " + "t.sourceType, t.transactionSum,  "
                        + "coalesce(t.transactionSubBalance1Sum, 0) as transactionSubBalance1Sum, coalesce(sum(dd.qty * dd.rprice), 0) as complexsum, "
                        + "coalesce(sum(dd.socDiscount), 0) as discountsum, coalesce(oo.orderType, 0) as ordertype, t.idOfClient "
                        + "from cf_transactions t left join cf_orders oo on t.idOfTransaction=oo.IdOfTransaction "
                        + "left join cf_orderdetails dd on oo.idOfOrder=dd.idOfOrder and oo.idOfOrg = dd.idOfOrg and dd.menuType between :menuMin and :menuMax "
                        + "where t.idOfOrg in (:orgs) AND t.transactionDate > :trans_begDate AND t.transactionDate <= :trans_endDate "
                        + "group by t.idOfTransaction, t.source, t.transactionDate, t.sourceType, t.transactionSum, t.transactionSubBalance1Sum, oo.orderType, t.idOfClient "
                        + "order by t.idOfClient";

        SQLQuery q = session.createSQLQuery(str_query);
        q.setParameter("trans_begDate", fromDateTime.getTime());
        q.setParameter("trans_endDate", toDateTime.getTime());
        q.setParameterList("orgs", friendlyOrgIds);
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

    public List<Card> getCardsToBlock(Integer daysInactivity) {
        Date date = CalendarUtils.addDays(new Date(), -daysInactivity);
        Query query = entityManager.createNativeQuery("select ca.IdOfCard from cf_cards ca where ca.state = 0 and ca.issuedate < :date and ca.idofclient is not null "
                + "and not exists (select idofcard from cf_card_activity caa where caa.idofcard = ca.idofcard and caa.lastupdate > :date)");
        query.setParameter("date", date.getTime());
        List list = query.getResultList();
        List<Card> result = new ArrayList<Card>();
        for (Object obj : list) {
            Long idOfCard = HibernateUtils.getDbLong(obj);
            Card card = entityManager.find(Card.class, idOfCard);
            result.add(card);
        }
        return result;
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

    public Integer getMenuCountDays(Long idOfOrg) {
        try {
            Org org = entityManager.find(Org.class, idOfOrg);
            return org.getConfigurationProvider().getMenuSyncCountDays();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Long> getClientsIdsWhereHasEnterEvents(long idOfOrg, Date beginDate, Date endDate) {
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery(" SELECT ee.idofclient " + " FROM cf_enterevents ee "
                + " WHERE ee.idoforg = :idOfOrg  AND ee.evtdatetime BETWEEN :beginDate AND :endDate "
                + " AND ee.idofclient IS NOT NULL AND ee.PassDirection IN (:passDirections)");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("beginDate", beginDate.getTime());
        query.setParameter("endDate", endDate.getTime());
        query.setParameterList("passDirections",
                Arrays.asList(EnterEvent.ENTRY, EnterEvent.EXIT, EnterEvent.PASSAGE_RUFUSAL, EnterEvent.RE_ENTRY,
                        EnterEvent.RE_EXIT, EnterEvent.DETECTED_INSIDE, EnterEvent.CHECKED_BY_TEACHER_EXT,
                        EnterEvent.CHECKED_BY_TEACHER_INT, EnterEvent.QUERY_FOR_ENTER, EnterEvent.QUERY_FOR_EXIT));
        query.addScalar("idofclient", StandardBasicTypes.LONG);
        List result = query.list();
        if (result == null) {
            return new ArrayList<Long>();
        }
        return result;
    }

    public SpecialDate findSpecialDate(CompositeIdOfSpecialDate compositeIdOfSpecialDate) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findSpecialDate(session, compositeIdOfSpecialDate);
    }

    public SpecialDate findSpecialDateWithGroup(CompositeIdOfSpecialDate compositeIdOfSpecialDate, Long idOfClientGroup) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findSpecialDateWithGroup(session, compositeIdOfSpecialDate, idOfClientGroup);
    }

    public List<EnterEvent> getEnterEventsByOrgAndGroup(long idOfOrg, String groupName, Date beginDate, Date endDate) {
        Query query = entityManager.createQuery(
                "select e from EnterEvent e inner join e.client c inner join c.clientGroup g "
                        + "where e.org.idOfOrg = :idOfOrg " + "and e.evtDateTime between :beginDate and :endDate" + (
                        StringUtils.isEmpty(groupName) ? "" : " and g.groupName = :groupName"));
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("beginDate", beginDate);
        query.setParameter("endDate", endDate);
        if (!StringUtils.isEmpty(groupName)) {
            query.setParameter("groupName", groupName);
        }
        return query.getResultList();
    }

    public User getUserFromSession() {
        try {
            HttpServletRequest request = SecurityContextAssociationValve.getActiveRequest().getRequest();
            HttpSession httpSession = request.getSession(true);
            Long idOfUser = (Long) httpSession.getAttribute(User.USER_ID_ATTRIBUTE_NAME);
            User user = findUserById(idOfUser);
            return user;
        } catch (Exception e) {
            //logger.info(String.format("Can't retrieve user from current Session. Message: %s", e.getMessage()));
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

    public Client findClientByContractId(Long contractId) throws Exception {
        try {
            Query query = entityManager.createQuery("select c from Client c where c.contractId = :contractId");
            query.setParameter("contractId", contractId);
            return (Client) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
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
        return findClientGuardianByIdIncludeDisabled(session, idOfChildren, idOfGuardian, false);
    }

    public ClientGuardian findClientGuardianByIdIncludeDisabled(Session session, long idOfChildren, long idOfGuardian,
            boolean includeDisabled) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfChildren));
        criteria.add(Restrictions.eq("idOfGuardian", idOfGuardian));
        if (!includeDisabled) {
            criteria.add(Restrictions.ne("deletedState", true));
            criteria.add(Restrictions.eq("disabled", false));
        }
        return (ClientGuardian) criteria.uniqueResult();
    }

    public List<Long> findClientGuardiansByMobile(Long idOfChildren, String mobile) {
        Query query = entityManager.createQuery(
                "select cg.idOfClientGuardian from ClientGuardian cg, Client child, Client guardian where guardian.idOfClient = cg.idOfGuardian "
                        + "and child.idOfClient = cg.idOfChildren " + "and child.idOfClient = :idOfChildren "
                        + "and cg.deletedState = false and cg.disabled = false and guardian.idOfClientGroup not in (:leaving, :deleted) "
                        + "and guardian.mobile = :mobile", Long.class);
        query.setParameter("idOfChildren", idOfChildren);
        query.setParameter("leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        query.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
        query.setParameter("mobile", mobile);
        return query.getResultList();
    }

    public List<Long> findContractsBySsoid(String ssoid) {
        if (ssoid == null || ssoid.trim().equals("") || ssoid.equals(EMPProcessor.SSOID_FAILED_TO_REGISTER) || ssoid
                .equals(EMPProcessor.SSOID_REGISTERED_AND_WAITING_FOR_DATA)) {
            return null;
        }
        Query query = entityManager.createQuery("select c.contractId from Client c where c.ssoid = :ssoid", Long.class);
        query.setParameter("ssoid", ssoid);
        return query.getResultList();
    }

    /**
     * @param guardianId
     * @param clientId
     * @param notifyType
     * @return Возвращает факт того, что у связки клиент-представитель включен хотя бы один из типов уведомления из notifyTypes
     */
    //TODO - надо ли возвращать true, когда все флаги выключены, но forcesend в конфиге выставлена в 1 - ВОПРОС ??
    public Boolean allowedGuardianshipNotification(Long guardianId, Long clientId, Long notifyType) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return ClientManager.allowedGuardianshipNotification(session, guardianId, clientId, notifyType);
    }

    public List<ItemListByGuardMobile> extractClientItemListFromGuardByGuardMobile(String guardMobile) {
        String query = "select client.idOfClient, client.contractId, client.san, 0 as disabled from cf_clients client where client.phone=:guardMobile or client.mobile=:guardMobile";
        Query q = entityManager.createNativeQuery(query, ItemListByGuardMobile.class);
        q.setParameter("guardMobile", guardMobile);
        List<ItemListByGuardMobile> clients = q.getResultList();

        if (clients != null && !clients.isEmpty()) {
            List<ItemListByGuardMobile> clientsCopy = new ArrayList<ItemListByGuardMobile>(clients);
            for (ItemListByGuardMobile item : clientsCopy) {
                Long id = item.getIdOfClient();
                Query q2 = entityManager.createNativeQuery(
                        "select cl.idOfClient, cl.contractid, cl.san, cg.disabled from cf_client_guardian cg inner join cf_clients cl "
                                + "on cl.idOfClient = cg.idOfChildren "
                                + "where cg.idOfGuardian = :idOfGuardian and cg.deletedState = false",
                        ItemListByGuardMobile.class);
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

    /*public List<Long> extractIDFromGuardByGuardMobile(String guardMobile) {
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
    }*/

    public MenuDetail getMenuDetailConstitutionByOrder(Long idOfMenuFromSync, Org orgFromOrder, Date orderDate) {
        Date endDate = CalendarUtils.addOneDay(orderDate);

        Query query = entityManager.createQuery("SELECT cfm " + "FROM MenuDetail cfm left join cfm.menu cm "
                + "WHERE cfm.idOfMenuFromSync = :idofmenufromsync " + "AND cm.org = :idoforg "
                + "AND cm.menuDate between :orderdate and :enddate " + "ORDER BY cfm.idOfMenuDetail DESC");
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

    public PreorderStatus findPreorderStatus(String guid, Date date) {
        Query query = entityManager.createQuery("select ps from PreorderStatus ps where ps.guid = :guid and ps.date = :date");
        query.setParameter("guid", guid);
        query.setParameter("date", date);
        List list = query.getResultList();
        if (list.size() == 0) return null;
        return (PreorderStatus) list.get(0);
    }

    public TaloonApproval findTaloonApproval(Long idOfOrg, Date taloonDate, String taloonName, String goodsGuid,
            Long price) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT taloon from TaloonApproval taloon " + "where taloon.idOfOrg = :idOfOrg "
                            + "and taloon.taloonDate = :taloonDate " + "and taloon.taloonName = :taloonName "
                            + "and taloon.price = :price " + "and taloon.goodsGuid = :goodsGuid");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("taloonDate", taloonDate);
            query.setParameter("taloonName", taloonName);
            query.setParameter("price", price);
            query.setParameter("goodsGuid", goodsGuid);
            return (TaloonApproval) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public TaloonPreorder findTaloonPreorder(Long idOfOrg, Date taloonDate, Long complexId, String goodsGuid,
            Long price) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT taloon from TaloonPreorder taloon " + "where taloon.idOfOrg = :idOfOrg "
                            + "and taloon.taloonDate = :taloonDate " + "and taloon.complexId = :complexId "
                            + "and taloon.price = :price " + "and taloon.goodsGuid = :goodsGuid");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("taloonDate", taloonDate);
            query.setParameter("complexId", complexId);
            query.setParameter("price", price);
            query.setParameter("goodsGuid", goodsGuid);
            return (TaloonPreorder) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public TaloonPreorder findTaloonPreorder(String guid) {
        try {
            Query query = entityManager
                    .createQuery("SELECT taloon from TaloonPreorder taloon " + "where taloon.guid = :guid");
            query.setParameter("guid", guid);
            return (TaloonPreorder) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public ComplexSchedule findComplexSchedule(String guid) {
        try {
            Query query = entityManager
                    .createQuery("SELECT schedule from ComplexSchedule schedule " + "where schedule.guid = :guid");
            query.setParameter("guid", guid);
            return (ComplexSchedule) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Integer findTaloonApprovalSoldedQty(Long idOfOrg, Date taloonDate, String taloonName, String goodsGuid,
            Long price) {
        Date dateEnd = CalendarUtils.addOneDay(taloonDate);
        try {
            String goodsJoin = "";
            String goodsParam = "";
            if (StringUtils.isNotEmpty(goodsGuid)) {
                goodsJoin = "inner join cf_goods g on g.idofgood = od.idofgood ";
                goodsParam = "and g.guid =:goodsGuid ";
            } else {
                goodsParam = "and od.idofgood is null ";
            }
            Query query = entityManager.createNativeQuery("SELECT sum(od.qty) from cf_orderDetails od "
                    + "inner join cf_orders o on od.idOfOrg = o.idOfOrg and od.idOfOrder = o.idOfOrder " + goodsJoin
                    + "where od.socDiscount > 0 and od.rprice = 0 and od.idOfOrg= :idOfOrg "
                    + "and od.menuDetailName = :taloonName " + "and od.MenuType >= :complexMin "
                    + "and od.MenuType <= :complexMax " + "and od.discount =:price "
                    + "and o.createdDate >= :taloonDate " + "and o.createdDate < :dateEnd " + "and o.state = :state "
                    + goodsParam);
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("taloonName", taloonName);
            query.setParameter("price", price);
            query.setParameter("complexMin", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("complexMax", OrderDetail.TYPE_COMPLEX_MAX);
            query.setParameter("taloonDate", taloonDate.getTime());
            query.setParameter("dateEnd", dateEnd.getTime());
            query.setParameter("state", Order.STATE_COMMITED);
            if (StringUtils.isNotEmpty(goodsGuid)) {
                query.setParameter("goodsGuid", goodsGuid);
            }
            Object result = query.getSingleResult();
            return result != null ? ((BigInteger) result).intValue() : 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public Integer findTaloonPreorderSoldQty(Long idOfOrg, Date taloonDate, String complexName, String goodsGuid,
            Long price) {
        Date dateEnd = CalendarUtils.addOneDay(taloonDate);
        try {
            String goodsJoin = "";
            String goodsParam = "";
            if (StringUtils.isNotEmpty(goodsGuid)) {
                goodsJoin = "inner join cf_goods g on g.idofgood = od.idofgood ";
                goodsParam = "and g.guid =:goodsGuid ";
            } else {
                goodsParam = "and od.idofgood is null ";
            }
            Query query = entityManager.createNativeQuery("SELECT sum(od.qty) from cf_orderDetails od "
                    + "inner join cf_orders o on od.idOfOrg = o.idOfOrg and od.idOfOrder = o.idOfOrder " + goodsJoin
                    + "where od.socDiscount > 0 and od.rprice = 0 and od.idOfOrg= :idOfOrg "
                    + "and od.menuDetailName = :complexName " + "and od.MenuType >= :complexMin "
                    + "and od.MenuType <= :complexMax " + "and od.discount =:price "
                    + "and o.createdDate >= :taloonDate " + "and o.createdDate < :dateEnd " + "and o.state = :state "
                    + goodsParam);
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("complexName", complexName);
            query.setParameter("price", price);
            query.setParameter("complexMin", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("complexMax", OrderDetail.TYPE_COMPLEX_MAX);
            query.setParameter("taloonDate", taloonDate.getTime());
            query.setParameter("dateEnd", dateEnd.getTime());
            query.setParameter("state", Order.STATE_COMMITED);
            if (StringUtils.isNotEmpty(goodsGuid)) {
                query.setParameter("goodsGuid", goodsGuid);
            }
            Object result = query.getSingleResult();
            return result != null ? ((BigInteger) result).intValue() : 0;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public List getInfoMessageDetails(Long idOfInfoMessage) {
        //Query query = entityManager.createQuery("select d from InfoMessageDetail d where d.compositeIdOfInfoMessageDetail.idOfInfoMessage = :idOfInfoMessage");
        Session session = entityManager.unwrap(Session.class);
        SQLQuery query = session.createSQLQuery(
                "select d.idoforg as idOfOrg, o.shortname as orgShortName, d.senddate as sendDate "
                        + "from cf_info_message_details d join cf_orgs o on d.idoforg = o.idoforg where d.idofinfomessage = :idOfInfoMessage order by d.idoforg");
        query.setParameter("idOfInfoMessage", idOfInfoMessage);
        query.addScalar("idOfOrg");
        query.addScalar("orgShortName");
        query.addScalar("sendDate");
        query.setResultTransformer(Transformers.aliasToBean(OrgDeliveryInfo.class));
        return query.list();
    }

    public Long getOrgPriceOfSms(Long idOfOrg) {
        try {
            Query query = entityManager
                    .createNativeQuery("select coalesce(o.priceOfSms, 0) from cf_orgs o where o.idOfOrg = :idOfOrg");
            query.setParameter("idOfOrg", idOfOrg);
            Object result = query.getSingleResult();
            return result != null ? ((BigInteger) result).longValue() : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    public boolean orgInFeedingSettingFound(List<Long> orgs, Long idOfSetting) {
        String str_query = "select coalesce(max(idOfSetting), 0) from cf_feeding_settings_orgs fs where fs.idOfOrg in (:idOfOrg)";
        if (idOfSetting != null) {
            str_query += " and idOfSetting <> :idOfSetting";
        }
        Query query = entityManager.createNativeQuery(str_query);
        query.setParameter("idOfOrg", orgs);
        if (idOfSetting != null) {
            query.setParameter("idOfSetting", idOfSetting);
        }
        Object result = query.getSingleResult();
        return result != null && ((BigInteger) result).longValue() > 0 ? true : false;
    }

    public byte[] getCardSignVerifyData(Integer idOfCardSign, Integer signType, boolean isNewType) {
        try {
            Query query;
            if (signType == 0 && isNewType) {
                query = entityManager.createQuery("select cs.privatekeycard from CardSign cs "
                        + "where cs.idOfCardSign = :idOfCardSign and cs.signType = :signType  and (cs.deleted = false or cs.deleted is null)");
            } else {
                query = entityManager.createQuery("select cs.signData from CardSign cs "
                        + "where cs.idOfCardSign = :idOfCardSign and cs.signType = :signType  and (cs.deleted = false or cs.deleted is null)");
            }
            query.setParameter("idOfCardSign", idOfCardSign);
            query.setParameter("signType", signType);
            return (byte[]) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public CardSign getSignInform(Integer manufacturerCode) {
        try {
            Query query = entityManager.createQuery("From CardSign cs "
                    + "where cs.manufacturerCode = :manufacturerCode and (cs.deleted = false or cs.deleted is null)");
            query.setParameter("manufacturerCode", manufacturerCode);
            List<CardSign> allSign = query.getResultList();
            for (CardSign cardSign : allSign) {
                //В таблице обязательно должен быть новый тип поставщиков с нашей ЭЦП
                if (cardSign.getNewtypeprovider()) {
                    return cardSign;
                }
            }
            return allSign.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public Card getCardfromNum(Long cardNo) {
        try {
            Query query = entityManager.createQuery("From Card c " + "where c.cardNo = :cardNo");
            query.setParameter("cardNo", cardNo);
            List<Card> cards = query.getResultList();
            if (cards.isEmpty()) {
                return null;
            }
            return cards.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public HelpRequest findHelpRequest(Long idOfOrg, String guid) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT help from HelpRequest help " + "where help.org.idOfOrg = :idOfOrg "
                            + "and help.guid = :guid ");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("guid", guid);
            return (HelpRequest) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean isHelpRequestGuidExists(String guid) {
        try {
            Query query = entityManager
                    .createQuery("SELECT help.idOfHelpRequest " + "FROM HelpRequest help " + "where help.guid = :guid");
            query.setParameter("guid", guid);
            return (null != query.getSingleResult());
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isHelpRequestNumberExists(String number) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT help.idOfHelpRequest " + "FROM HelpRequest help " + "where help.requestNumber = :number");
            query.setParameter("number", number);
            return (null != query.getSingleResult());
        } catch (Exception e) {
            return false;
        }
    }

    public String getOrgOfficialName(Long idOfOrg) {
        Query q = entityManager.createQuery("select o.officialName from Org o where idOfOrg = :idOfOrg", String.class);
        q.setParameter("idOfOrg", idOfOrg);
        try {
            return (String) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /*public boolean isSixWorkWeek(Long orgId) throws Exception {
        List<ECafeSettings> settings = DAOService.getInstance().geteCafeSettingses(orgId, SettingsIds.SubscriberFeeding, false);
        boolean isSixWorkWeek = false;
        if(!settings.isEmpty()){
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser =
                    (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            isSixWorkWeek = parser.isSixWorkWeek();
        }
        return isSixWorkWeek;
    }*/

    public boolean isSixWorkWeek(Long orgId, String groupName) {
        boolean resultByOrg = false; //isSixWorkWeek(orgId);
        try {
            return (Boolean) entityManager.createQuery(
                    "select distinct gnto.isSixDaysWorkWeek from GroupNamesToOrgs gnto where gnto.idOfOrg = :idOfOrg and gnto.groupName = :groupName")
                    .setParameter("idOfOrg", orgId).setParameter("groupName", groupName).getSingleResult();
        } catch (Exception e) {
            return resultByOrg;
        }
    }

    public String getClientGroupName(Long idOfOrg, Long idOfClientGroup) {
        if (idOfClientGroup != null) {
            Query query = entityManager.createQuery("select cg.groupName from ClientGroup cg "
                    + "where cg.compositeIdOfClientGroup.idOfOrg = :idOfOrg and cg.compositeIdOfClientGroup.idOfClientGroup = :idOfClientGroup");
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("idOfClientGroup", idOfClientGroup);
            try {
                return (String) query.getSingleResult();
            } catch (Exception ignore) {
            }
        }
        return "";
    }

    public Client getClientByCardPrintedNo(Long cardPrintedNo) throws Exception {
        Query query = entityManager.createQuery(
                "select card from Card card join fetch card.client c join fetch c.person p "
                        + "where card.cardPrintedNo = :cardPrintedNo and card.state = :state and card.client is not null");
        query.setParameter("cardPrintedNo", cardPrintedNo);
        query.setParameter("state", Card.ACTIVE_STATE);
        List<Card> list = query.getResultList();
        if (list.size() == 0) {
            throw new Exception(CARD_NOT_FOUND);
        } else if (list.size() > 1) {
            throw new Exception(SEVERAL_CARDS_FOUND);
        }
        return list.get(0).getClient();
    }

    public List<SpecialDate> getSpecialDates(Date startDate, Date endDate, Long idOfOrg) {
        try {
            Query query = entityManager.createQuery(
                    "select sd from SpecialDate sd where sd.date between :startDate and :endDate "
                            + "and sd.idOfOrg = :idOfOrg and sd.deleted = false");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setParameter("idOfOrg", idOfOrg);
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProductionCalendar> getProductionCalendar(Date dateBegin, Date dateEnd) {
        return entityManager
                .createQuery("select pc from ProductionCalendar pc where pc.day between :dateBegin and :dateEnd")
                .setParameter("dateBegin", CalendarUtils.startOfDay(dateBegin))
                .setParameter("dateEnd", CalendarUtils.endOfDay(dateEnd)).getResultList();
    }

    public Long getIdOfPacket(Long idOfOrg) {
        return entityManager.find(OrgSync.class, idOfOrg).getIdOfPacket();
    }

    public List<User> getUserRoles() {
        return entityManager.createQuery(
                "select u from User u where u.isGroup = true and u.deletedState = false order by u.userName")
                .getResultList();
    }

    public Map<String, Map<String, String>> getProductionCalendar() {
        Query query = entityManager.createQuery("select pc from ProductionCalendar pc order by pc.day");
        List<ProductionCalendar> list = query.getResultList();
        Map<String, Map<String, String>> resultMap = new TreeMap<String, Map<String, String>>();
        for (ProductionCalendar pc : list) {
            String str = CalendarUtils.dateToString(pc.getDay());
            String year = str.substring(6, 10);
            String month = str.substring(3, 5);
            String day = str.substring(0, 2);
            Map<String, String> monthMap = resultMap.get(year);
            if (monthMap == null) {
                monthMap = new TreeMap<String, String>();
            }
            String m = monthMap.get(month);
            String star = "";
            if (pc.getFlag().equals(ProductionCalendar.HOLIDAY)) {
                star = "*";
            }
            if (m == null) {
                monthMap.put(month, day + star);
            } else {
                monthMap.put(month, m + "," + day + star);
            }
            resultMap.put(year, monthMap);
        }
        return resultMap;
    }

    public List<EMIAS> getEmiasForMaxVersionAndIdOrg(Long maxVersion, List<Long> orgs) {
        try {
            Query query = entityManager.createQuery("select ce from EMIAS ce where ce.version > :vers");
            query.setParameter("vers", maxVersion);
            List<EMIAS> emias = query.getResultList();
            //Фильтрация по орг
            Iterator<EMIAS> emiasIterator = emias.iterator();
            while (emiasIterator.hasNext()) {
                EMIAS emias1 = emiasIterator.next();//получаем следующий элемент
                Client cl = DAOUtils.findClientByGuid(entityManager, emias1.getGuid());
                if (cl == null) {
                    //Удаляем "удаленных" клиентов
                    emiasIterator.remove();
                    continue;
                }
                if (orgs.indexOf(cl.getOrg().getIdOfOrg()) == -1) {
                    //Удаляем "чужих" клиентов
                    emiasIterator.remove();
                }
            }
            return emias;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean isSixWorkWeekGroup(Long orgId, Long idOfClientGroup) {
        try {
            String groupName = getClientGroupName(orgId, idOfClientGroup);
            return (boolean)entityManager.createQuery("select distinct gnto.isSixDaysWorkWeek from GroupNamesToOrgs gnto where gnto.idOfOrg = :idOfOrg and gnto.groupName = :groupName")
                    .setParameter("idOfOrg", orgId)
                    .setParameter("groupName", groupName)
                    .getSingleResult();
        } catch (Exception e) {
            return false;
        }
    }

    public Set<WtOrgGroup> getOrgGroupsSetFromVersion(Long version, Contragent contragent, Org org) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT gr FROM WtOrgGroup gr " + "WHERE gr.version > :version AND gr.contragent = :contragent AND "
                            + ":org IN elements(gr.orgs)");
            query.setParameter("version", version);
            query.setParameter("contragent", contragent);
            query.setParameter("org", org);
            List<WtOrgGroup> groups = query.getResultList();
            return new HashSet<>(groups);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtCategoryItem> getCategoryItemsSetFromVersion(Long version) {
        try {
            Query query = entityManager
                    .createQuery("SELECT catItem from WtCategoryItem catItem where catItem.version > :version");
            query.setParameter("version", version);
            List<WtCategoryItem> categories = query.getResultList();
            return new HashSet<>(categories);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtTypeOfProductionItem> getTypeProductionsSetFromVersion(Long version) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT typeProd from WtTypeOfProductionItem typeProd where typeProd.version > :version");
            query.setParameter("version", version);
            List<WtTypeOfProductionItem> prodTypes = query.getResultList();
            return new HashSet<>(prodTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtAgeGroupItem> getAgeGroupItemsSetFromVersion(Long version) {
        try {
            Query query = entityManager
                    .createQuery("SELECT ageGroup from WtAgeGroupItem ageGroup where ageGroup.version > :version");
            query.setParameter("version", version);
            List<WtAgeGroupItem> ageGroups = query.getResultList();
            return new HashSet<>(ageGroups);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtDietType> getDietTypesSetFromVersion(Long version) {
        try {
            Query query = entityManager
                    .createQuery("SELECT dietType from WtDietType dietType where dietType.version > :version");
            query.setParameter("version", version);
            List<WtDietType> dietTypes = query.getResultList();
            return new HashSet<>(dietTypes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtComplexGroupItem> getComplexGroupItemsSetFromVersion(Long version) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT complexGroup from WtComplexGroupItem complexGroup where complexGroup.version > :version");
            query.setParameter("version", version);
            List<WtComplexGroupItem> complexGroups = query.getResultList();
            return new HashSet<>(complexGroups);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtGroupItem> getGroupItemsSetFromVersion(Long version) {
        try {
            Query query = entityManager
                    .createQuery("SELECT groupItem from WtGroupItem groupItem where groupItem.version > :version");
            query.setParameter("version", version);
            List<WtGroupItem> groupItems = query.getResultList();
            return new HashSet<>(groupItems);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtDish> getDishesListFromVersion(Long version, Contragent contragent) {
        try {
            Query query = entityManager.createQuery("SELECT dish FROM WtDish dish WHERE dish.version > :version "
                    + "AND dish.contragent = :contragent");
            query.setParameter("version", version);
            query.setParameter("contragent", contragent);
            List<WtDish> dishes = query.getResultList();

            Set<WtDish> result = new HashSet<>();
            for (WtDish dish : dishes) {
                if (dish.getMenuGroupMenus() != null) {
                    result.add(dish);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtMenuGroup> getMenuGroupsSetFromVersion(Long version, Contragent contragent) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT menuGroup from WtMenuGroup menuGroup where menuGroup.version > :version AND "
                            + "menuGroup.contragent = :contragent");
            query.setParameter("version", version);
            query.setParameter("contragent", contragent);
            List<WtMenuGroup> menuGroups = query.getResultList();
            return new HashSet<>(menuGroups);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Set<WtMenu> getOfflineMenusSetFromVersion(Long version, Org org) {
        Set<WtMenu> offlineMenus = new HashSet<>();
        Query queryDeletedMenus = entityManager.createNativeQuery("select idofmenu from cf_wt_org_relation_aud a "
                + "where a.versionofmenu > :version and a.deletestate = 1 "
                + "and (a.idoforg = :idoforg or exists (select * from cf_wt_org_group_relations r where r.idoforggroup = a.idoforggroup and r.idoforg = :idoforg))");
        queryDeletedMenus.setParameter("version", version);
        queryDeletedMenus.setParameter("idoforg", org.getIdOfOrg());
        List list = queryDeletedMenus.getResultList();
        for (Object obj : list) {
            Long idOfMenu = HibernateUtils.getDbLong(obj);
            Query query = entityManager.createQuery("SELECT menu from WtMenu menu "
                    + "LEFT JOIN FETCH menu.wtOrgGroup orgGroup "
                    + "where menu.idOfMenu = :idOfMenu");
            query.setParameter("idOfMenu", idOfMenu);
            offlineMenus.add((WtMenu)query.getSingleResult());
        }
        return offlineMenus;
    }

    public Set<WtMenu> getMenusSetFromVersion(Long version, Contragent contragent, Org org) {
        try {
            Query query = entityManager.createQuery("SELECT menu from WtMenu menu "
                    + "LEFT JOIN FETCH menu.wtOrgGroup orgGroup "
                    + "where menu.version > :version "
                    + "AND menu.contragent = :contragent "
                    + "AND (:org IN elements(menu.orgs) "
                    + "OR :org IN elements(orgGroup.orgs))");
            query.setParameter("version", version);
            query.setParameter("contragent", contragent);
            query.setParameter("org", org);
            List<WtMenu> menus = query.getResultList();
            return new HashSet<>(menus);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<WtComplex> getOfflineComplexesSetFromVersion(Long version, Org org) {
        Set<WtComplex> offlineComplexes = new HashSet<>();
        Query queryDeletedComplexes = entityManager.createNativeQuery("select idofcomplex from cf_wt_org_relation_aud a "
                + "where a.versionofcomplex > :version and a.deletestate = 1 "
                + "and (a.idoforg = :idoforg or exists (select * from cf_wt_org_group_relations r where r.idoforggroup = a.idoforggroup and r.idoforg = :idoforg))");
        queryDeletedComplexes.setParameter("version", version);
        queryDeletedComplexes.setParameter("idoforg", org.getIdOfOrg());
        List list = queryDeletedComplexes.getResultList();
        for (Object obj : list) {
            Long idOfComplex = HibernateUtils.getDbLong(obj);
            Query query = entityManager.createQuery(
                    "SELECT complex from WtComplex complex left join fetch complex.wtComplexesItems items "
                            + "left join fetch complex.orgs orgs "
                            + "left join fetch items.dishes dishes where complex.idOfComplex = :idOfComplex");
            query.setParameter("idOfComplex", idOfComplex);
            offlineComplexes.add((WtComplex)query.getSingleResult());
        }
        return offlineComplexes;
    }

    public Set<WtComplex> getComplexesSetFromVersion(Long version, Contragent contragent, Org org) {
        Set<WtComplex> complexes = new HashSet<>();
        try {
            Query queryOrgs = entityManager.createQuery(
                    "SELECT complex from WtComplex complex left join fetch complex.wtComplexesItems items "
                            + "left join fetch complex.orgs orgs "
                            + "left join fetch items.dishes dishes where complex.version > :version "
                            + "AND complex.contragent = :contragent AND :org IN elements(complex.orgs)");
            queryOrgs.setParameter("version", version);
            queryOrgs.setParameter("contragent", contragent);
            queryOrgs.setParameter("org", org);
            List<WtComplex> complexesOrgs = queryOrgs.getResultList();

            if (complexesOrgs != null) {
                complexes.addAll(complexesOrgs);
            }

            Query queryOrgGroups = entityManager.createQuery(
                    "SELECT complex from WtComplex complex left join fetch complex.wtComplexesItems items "
                            + "left join fetch complex.orgs orgs "
                            + "left join fetch items.dishes dishes where complex.version > :version "
                            + "AND complex.contragent = :contragent AND :org IN elements(complex.wtOrgGroup.orgs)");
            queryOrgGroups.setParameter("version", version);
            queryOrgGroups.setParameter("contragent", contragent);
            queryOrgGroups.setParameter("org", org);
            List<WtComplex> complexesOrgGroups = queryOrgGroups.getResultList();

            if (complexesOrgGroups != null) {
                complexes.addAll(complexesOrgGroups);
            }

            for (WtComplex complex : complexes) {
                Query query = entityManager
                        .createQuery("SELECT item from WtComplexesItem item left join fetch item.dishes dish where item.wtComplex = :complex");
                query.setParameter("complex", complex);
                List<WtComplexesItem> items = query.getResultList();
                if (items != null && items.size() > 0) {
                    complex.setWtComplexesItems(new HashSet<>(items));
                }
            }
            return complexes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<Org> findFriendlyOrgs(Long idOfOrg) {
        Session session = entityManager.unwrap(Session.class);
        List<Long> friendlyOrgIds = DAOUtils.findFriendlyOrgIds(session, idOfOrg);
        Set<Org> result = new HashSet<>();
        for (Long friendlyOrgId : friendlyOrgIds) {
            result.add(DAOService.getInstance().getOrg(friendlyOrgId));
        }
        return result;
    }

    public Contragent findDefaultSupplier(Long idOfOrg) {
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Query query = session
                .createQuery("SELECT defaultSupplier FROM Org org where org.idOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        return (Contragent) query.uniqueResult();
    }

    public List<WtDish> getMenuDishes(WtMenu menu) {
        Session session = entityManager.unwrap(Session.class);
        org.hibernate.Query q = session.createSQLQuery("select d.idofdish "
                + "from cf_wt_dishes d left join cf_wt_menu_group_dish_relationships mgd on d.idofdish=mgd.idOfDish "
                + "left outer join cf_wt_menu_group_relationships mgr on mgd.idOfMenuMenuGroupRelation=mgr.id "
                + "left outer join cf_wt_menu m on mgr.idOfMenu=m.idofmenu where m.idofmenu=:idOfMenu and d.deletestate = 0 and mgr.deletestate = 0");
        q.setParameter("idOfMenu", menu.getIdOfMenu());
        List list = q.list();
        List<Long> ids = new ArrayList<>();
        for (Object obj : list) {
            Long id = HibernateUtils.getDbLong(obj);
            ids.add(id);
        }
        if (ids.size() == 0) return null;
        org.hibernate.Query query = session
                .createQuery("SELECT dish FROM WtDish dish where dish.idOfDish in :list");
        query.setParameterList("list", ids);
        return query.list();
    }

    public Long getMenuGroupIdByMenuAndDishIds (Long menuId, Long dishId) {
        Query query = entityManager.createNativeQuery("SELECT mg.id FROM cf_wt_menu_groups mg "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgr.idofmenugroup = mg.id "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgd ON mgd.idofmenumenugrouprelation = mgr.id "
                + "LEFT JOIN cf_wt_dishes d ON mgd.idofdish = d.idofdish "
                + "LEFT JOIN cf_wt_menu m ON m.idofmenu = mgr.idofmenu "
                + "WHERE m.idofmenu = :idOfMenu AND d.idofdish = :idOfDish "
                + "and mg.deletestate = 0 and m.deletestate = 0 and mgr.deletestate = 0");

        query.setParameter("idOfMenu", menuId);
        query.setParameter("idOfDish", dishId);
        try {
            Object result = query.getSingleResult();
            return ((BigInteger) result).longValue();
        } catch (NoResultException e) {
            return 0L;
        }
    }

    public Boolean isMenuItemAvailable (Long menuId) {
        Query query = entityManager.createNativeQuery("SELECT count(mg.id) FROM cf_wt_menu_groups mg "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgr.idofmenugroup = mg.id "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgd ON mgd.idofmenumenugrouprelation = mgr.id "
                + "LEFT JOIN cf_wt_menu m ON m.idofmenu = mgr.idofmenu "
                + "WHERE m.idofmenu = :idOfMenu AND mgd.idofdish is not null "
                + "and mg.deletestate = 0 and m.deletestate = 0 and mgr.deletestate = 0 group by mgd.idofdish");
        query.setParameter("idOfMenu", menuId);
        List<BigInteger> temp = query.getResultList();
        for(BigInteger o : temp) {
            if (o.intValue() > 1) {
                return false;
            }
        }
        return true;
    }

    public Set<WtComplexExcludeDays> getExcludeDaysSetFromVersion(Long version, Contragent contragent, Org org) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT excludeDays from WtComplexExcludeDays excludeDays "
                            + "LEFT JOIN FETCH excludeDays.complex complex "
                            + "LEFT JOIN FETCH complex.wtOrgGroup orgGroup "
                            + "WHERE excludeDays.version > :version "
                            + "AND complex.contragent = :contragent "
                            + "AND (:org IN elements(complex.orgs) "
                            + "OR :org IN elements(orgGroup.orgs))");
            query.setParameter("version", version);
            query.setParameter("contragent", contragent);
            query.setParameter("org", org);
            List<WtComplexExcludeDays> excludeDays = query.getResultList();
            return new HashSet<>(excludeDays);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public boolean isSixWorkWeekOrg(Long orgId) {
        boolean resultByOrg = false; //isSixWorkWeek(orgId);
        try {
            List<Boolean> list = entityManager.createQuery("select distinct gnto.isSixDaysWorkWeek from GroupNamesToOrgs gnto where gnto.idOfOrg = :idOfOrg")
                    .setParameter("idOfOrg", orgId)
                    .getResultList();
            if (list.contains(Boolean.TRUE))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    public List<WtComplexExcludeDays> getExcludeDaysByWtComplex(WtComplex wtComplex) {
        Query query = entityManager.createQuery("SELECT excludeDays from WtComplexExcludeDays excludeDays "
                + "WHERE excludeDays.complex = :complex "
                + "AND excludeDays.deleteState = 0");
        query.setParameter("complex", wtComplex);
        return query.getResultList();
    }

    public Boolean checkExcludeDays(Date date, List<WtComplexExcludeDays> excludeDays) {
        try {
            for (WtComplexExcludeDays wtExcludeDays : excludeDays) {
                if (wtExcludeDays.getDate().getTime() == date.getTime())
                    return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> getWtMenuGroupByWtDish(Long idOfOrg, WtDish wtDish) {
        Query query = entityManager.createNativeQuery("SELECT mg.name FROM cf_wt_menu_groups mg "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgr.idofmenugroup = mg.id "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgd ON mgd.idofmenumenugrouprelation = mgr.id "
                + "LEFT JOIN cf_wt_dishes d ON mgd.idofdish = d.idofdish "
                + "LEFT JOIN cf_wt_menu m ON m.idofmenu = mgr.idofmenu "
                + "WHERE (m.idoforggroup in (select og.idoforggroup from cf_wt_org_groups og "
                + "join cf_wt_org_group_relations ogr on og.idoforggroup = ogr.idoforggroup where ogr.idoforg = :idOfOrg) "
                + "OR m.idofmenu in (select idofmenu from cf_wt_menu_org mo where mo.idoforg = :idOfOrg))"
                + "and d.idofdish = :idOfDish and mgr.deletestate = 0 ");
        query.setParameter("idOfDish", wtDish.getIdOfDish());
        query.setParameter("idOfOrg", idOfOrg);
        List list = query.getResultList();
        Set<String> result = new HashSet<>();
        if (list.size() == 0) result.add("");
        for (Object obj : list) {
            result.add((String)obj);
        }
        return result;
    }

    public List<WtCategoryItem> getCategoryItemsByWtDish(WtDish wtDish) {
        return entityManager.createQuery("select dish.categoryItems from WtDish dish where dish = :dish")
                .setParameter("dish", wtDish)
                .getResultList();
    }

    public List<WtGroupItem> getGroupItemsByWtDish(WtDish wtDish) {
        return entityManager.createQuery("select dish.groupItems from WtDish dish where dish = :dish")
                .setParameter("dish", wtDish)
                .getResultList();
    }

    public List<Org> getOrgsByWtMenu(WtMenu wtMenu) {
        return entityManager.createQuery("select menu.orgs from WtMenu menu where menu = :menu")
                .setParameter("menu", wtMenu)
                .getResultList();
    }

    public List<WtMenu> getMenuByWtMenuGroup(WtMenuGroup wtMenuGroup) {
        return entityManager.createQuery("select menu from WtMenu menu left join fetch menu.menuGroupMenus mgm "
                + "where mgm.menuGroup = :wtMenuGroup ")
                .setParameter("wtMenuGroup", wtMenuGroup)
                .getResultList();
    }

    public Boolean checkWorkingDay(Date date) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT calend.flag from ProductionCalendar calend WHERE calend.day "
                            + "between :dateBegin and :dateEnd");
            query.setParameter("dateBegin", CalendarUtils.startOfDay(date));
            query.setParameter("dateEnd", CalendarUtils.endOfDay(date));
            List<Integer> res = query.getResultList();
            if (res != null && res.size() > 0) {
                return res.get(0) == 1;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean checkLearningDayByOrgAndClientGroup(Date date, Org org, ClientGroup clientGroup) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT sd.isWeekend from SpecialDate sd "
                            + "WHERE sd.date between :dateBegin and :dateEnd AND sd.deleted = false AND sd.org = :org "
                            + "AND (sd.idOfClientGroup = :idOfClientGroup OR sd.idOfClientGroup IS NULL)");
            query.setParameter("dateBegin", CalendarUtils.startOfDay(date));
            query.setParameter("dateEnd", CalendarUtils.endOfDay(date));
            query.setParameter("org", org);
            query.setParameter("idOfClientGroup", clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            List<Boolean> res = query.getResultList();
            if (res != null && res.size() > 0) {
                return res.get(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean isSixDaysWorkingWeek(Org org, String groupName) {
        try {
            Query query = entityManager.createQuery("SELECT gno.isSixDaysWorkWeek FROM GroupNamesToOrgs gno " +
                    "WHERE gno.idOfOrg = :idOfOrg AND gno.groupName = :groupName");
            query.setParameter("idOfOrg", org.getIdOfOrg());
            query.setParameter("groupName", groupName);
            return (Boolean) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public GoodRequest findGoodRequestByGuid(String guid) {
        try {
            Query query = entityManager.createQuery("SELECT gr from GoodRequest gr where gr.guid = :guid");
            query.setParameter("guid", guid);
            return (GoodRequest) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Staff findStaffByGuid(String guid) {
        try {
            Query query = entityManager.createQuery("SELECT s from Staff s where s.guid = :guid");
            query.setParameter("guid", guid);
            return (Staff) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public GoodRequestPosition findGoodRequestPositionByGuid(String guid) {
        try {
            Query query = entityManager.createQuery("SELECT grp from GoodRequestPosition grp where grp.guid = :guid");
            query.setParameter("guid", guid);
            return (GoodRequestPosition) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
	public boolean isSixWorkWeekOrgAndGroup(Long orgId, String groupName) {
        boolean resultByOrg = false; //isSixWorkWeek(orgId);
        try {
            List<Boolean> list = entityManager.createQuery("select distinct gnto.isSixDaysWorkWeek from GroupNamesToOrgs gnto "
                    + "where gnto.idOfOrg = :idOfOrg and gnto.groupName = :groupname")
                    .setParameter("idOfOrg", orgId).setParameter("groupname", groupName)
                    .getResultList();
            if (list.contains(Boolean.TRUE))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }
}