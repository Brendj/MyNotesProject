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
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.foodbox.*;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxPreorder.FoodBoxPreorderNew;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxPreorder.FoodBoxPreorderNewItem;
import ru.axetta.ecafe.processor.core.sync.handlers.foodBox.FoodBoxPreorder.FoodBoxPreorderNewItemItem;
import ru.axetta.ecafe.processor.core.sync.response.AccountTransactionExtended;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ExternalSystemStats;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.axetta.ecafe.processor.core.utils.RequestUtils;

import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
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

    public Long getContractIdByCardNo(long lCardId) throws Exception {
        Client client = DAOUtils.findClientByCardNo(entityManager, lCardId);
        if (client != null) {
            return client.getContractId();
        }
        return null;
    }

    public List<Card> getActiveCardsWithOverdueValidDate(Date now) {
        Query query = entityManager.createQuery(
                "FROM Card " +
                  " WHERE state = 0 and validTime < :date and client is not null");
        query.setParameter("date", now);

        return query.getResultList();
    }

    public Org findOrg(Long idOfOrg) {
        return entityManager.find(Org.class, idOfOrg);
    }

    public List<Org> findOrgs(List<Long> ids){
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findOrgs(session, ids);
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
            HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
            HttpSession httpSession = request.getSession(true);
            Long idOfUser = (Long) httpSession.getAttribute(User.USER_ID_ATTRIBUTE_NAME);
            return findUserById(idOfUser);
        } catch (Exception e) {
            return null;
        }
    }

    public User findUserById(long idOfUser) throws Exception {
        return entityManager.find(User.class, idOfUser);
    }

    public Order findOrder(Long idOfOrg, Long idOfOrder) {
        return entityManager.find(Order.class, new CompositeIdOfOrder(idOfOrg, idOfOrder));
    }

    public Client findClientById(long idOfClient) {
        return entityManager.find(Client.class, idOfClient);
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

    public List<EMIAS> getEmiasForMaxVersionAndIdOrg(Long maxVersion, List<Long> idOfOrgs) {
        try {
            Session session = entityManager.unwrap(Session.class);
            org.hibernate.Query q = session.createSQLQuery("select ce.id, ce.guid, ce.idEventEMIAS,"
                    + " ce.typeEventEMIAS, ce.dateLiberate, ce.startDateLiberate, ce.endDateLiberate, "
                    + " ce.createDate, ce.updateDate, ce.accepted, ce.deletedemiasid, ce.version, "
                    + " ce.kafka, ce.archive, ce.hazard_level_id, ce.processed, ce.accepteddatetime from cf_emias ce "
                    + " left join cf_clients cc on cc.meshguid = ce.guid "
                    + " where cc.idofclient is not null and cc.idoforg in (:orgs) and ce.version > :vers and (ce.kafka is null or ce.kafka = false)").addEntity(EMIAS.class);
            q.setParameterList("orgs", idOfOrgs);
            q.setParameter("vers", maxVersion);
            return q.list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<EMIAS> getExemptionVisitingForMaxVersionAndIdOrg(Long maxVersion, List<Long> idOfOrgs) {
        try {
            Session session = entityManager.unwrap(Session.class);
            org.hibernate.Query q = session.createSQLQuery("select ce.id, ce.guid, ce.idEventEMIAS, ce.typeEventEMIAS, ce.dateLiberate, ce.startDateLiberate, ce.endDateLiberate,  ce.createDate, ce.updateDate, ce.accepted, ce.deletedemiasid, ce.version,  ce.kafka, ce.archive, ce.hazard_level_id, ce.idemias, ce.processed, ce.accepteddatetime   from cf_emias ce "
                    + " left join cf_clients cc on cc.meshguid = ce.guid "
                    + " where cc.idofclient is not null and cc.idoforg in (:orgs) and ce.version > :vers and ce.kafka=true and ce.processed = true").addEntity(EMIAS.class);
            q.setParameterList("orgs", idOfOrgs);
            q.setParameter("vers", maxVersion);
            return q.list();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<EMIAS> getEmiasbyClient(Session session, Client client) {
        try {
            Criteria criteria = session.createCriteria(EMIAS.class);
            criteria.add(Restrictions.eq("guid", client.getMeshGUID()));
            criteria.add(Restrictions.eq("kafka", true));
            criteria.add(Restrictions.eq("processed", true));
            criteria.add(Restrictions.ne("archive", true));
            return criteria.list();
        } catch (Exception e) {
            return new ArrayList<EMIAS>();
        }
    }

    public List<EMIASbyDay> getEmiasbyDayForClient(Session session, Client client, Long idOfClient) {
        try {
            Criteria criteria = session.createCriteria(EMIASbyDay.class);
            if (idOfClient == null)
                criteria.add(Restrictions.eq("idOfClient", client.getIdOfClient()));
            if (client == null)
                criteria.add(Restrictions.eq("idOfClient", idOfClient));
            return criteria.list();
        } catch (Exception e) {
            return new ArrayList<EMIASbyDay>();
        }
    }

    public List<EMIASbyDay> getEmiasbyDayForOrgs(Long maxVersion, List<Long> idforgs) {
        return entityManager.createQuery("select distinct embd from EMIASbyDay embd where embd.version>:maxVersion and"
                + " embd.idOfOrg in :idforgs")
                .setParameter("idforgs", idforgs).setParameter("maxVersion", maxVersion)
                .getResultList();
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

    public Set<WtOrgGroup> getOfflineOrgGroupsSetFromVersion(Long version, Org org) {
        Set<WtOrgGroup> offlineOrgGroups = new HashSet<>();
        Query queryDeletedOrgGroups = entityManager.createNativeQuery("select idoforggroup from cf_wt_org_relation_aud a "
                + "where a.versionoforggroup > :version and a.deletestate = 1 and a.idoforggroup is not null "
                + "and a.idofcomplex is null and a.idofmenu is null "
                + "and a.idoforg = :idoforg");
        queryDeletedOrgGroups.setParameter("version", version);
        queryDeletedOrgGroups.setParameter("idoforg", org.getIdOfOrg());
        List list = queryDeletedOrgGroups.getResultList();
        for (Object obj : list) {
            Long idOfOrgGroup = HibernateUtils.getDbLong(obj);
            Query query = entityManager.createQuery(
                    "SELECT orgGroup from WtOrgGroup orgGroup left join fetch orgGroup.orgs items "
                            + "where orgGroup.idOfOrgGroup = :idOfOrgGroup");
            query.setParameter("idOfOrgGroup", idOfOrgGroup);
            offlineOrgGroups.add((WtOrgGroup) query.getSingleResult());
        }
        return offlineOrgGroups;
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
                + "where a.versionofmenu > :version and a.deletestate = 1 and a.idofmenu is not null "
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
                + "where a.versionofcomplex > :version and a.deletestate = 1 and a.idofcomplex is not null "
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
            result.add(entityManager.find(Org.class, friendlyOrgId));
        }
        return result;
    }

    public List<Long> findFriendlyOrgsIds(Long idOfOrg) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findFriendlyOrgIds(session, idOfOrg);
    }

    public Set<Long> findFriendlyOrgsIdsAsSet(Long idOfOrg) {
        Session session = entityManager.unwrap(Session.class);
        return new HashSet<>(DAOUtils.findFriendlyOrgIds(session, idOfOrg));
    }

    public Boolean isOrgFriendly(Long targetOrgId, Long testOrgId) {
        Org testOrg = entityManager.find(Org.class, testOrgId);
        Set<Org> set = testOrg.getFriendlyOrg();
        for (Org o : set) {
            if (targetOrgId.equals(o.getIdOfOrg())) {
                return true;
            }
        }
        return false;
    }

    public Contragent findDefaultSupplier(Long idOfOrg) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findDefaultSupplier(session, idOfOrg);
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

    public Map<Long, Set<String>> getWtMenuGroupsForAllDishes(Long idOfOrg) {
        Map<Long, Set<String>> result = new HashMap<>();
        Query query = entityManager.createNativeQuery("SELECT mg.name, d.idofdish FROM cf_wt_menu_groups mg "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgr.idofmenugroup = mg.id "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgd ON mgd.idofmenumenugrouprelation = mgr.id "
                + "LEFT JOIN cf_wt_dishes d ON mgd.idofdish = d.idofdish "
                + "LEFT JOIN cf_wt_menu m ON m.idofmenu = mgr.idofmenu "
                + "WHERE (m.idoforggroup in (select og.idoforggroup from cf_wt_org_groups og "
                + "join cf_wt_org_group_relations ogr on og.idoforggroup = ogr.idoforggroup where ogr.idoforg = :idOfOrg) "
                + "OR m.idofmenu in (select idofmenu from cf_wt_menu_org mo where mo.idoforg = :idOfOrg))"
                + "and mgr.deletestate = 0 and d.idofdish is not null");
        query.setParameter("idOfOrg", idOfOrg);
        List list = query.getResultList();
        for (Object o : list) {
            Object[] row = (Object[]) o;
            String dishName = (String) row[0];
            Long idOfDish = ((BigInteger)row[1]).longValue();
            Set<String> set = result.get(idOfDish);
            if (set == null) set = new HashSet<String>();
            set.add(dishName);
            result.put(idOfDish, set);
        }
        return result;
    }

    public Set<String> getWtMenuGroupByWtDish(Long idOfOrg, Long idOfDish) {
        Query query = entityManager.createNativeQuery("SELECT mg.name FROM cf_wt_menu_groups mg "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgr.idofmenugroup = mg.id "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgd ON mgd.idofmenumenugrouprelation = mgr.id "
                + "LEFT JOIN cf_wt_dishes d ON mgd.idofdish = d.idofdish "
                + "LEFT JOIN cf_wt_menu m ON m.idofmenu = mgr.idofmenu "
                + "WHERE (m.idoforggroup in (select og.idoforggroup from cf_wt_org_groups og "
                + "join cf_wt_org_group_relations ogr on og.idoforggroup = ogr.idoforggroup where ogr.idoforg = :idOfOrg) "
                + "OR m.idofmenu in (select idofmenu from cf_wt_menu_org mo where mo.idoforg = :idOfOrg))"
                + "and d.idofdish = :idOfDish and mgr.deletestate = 0 ");
        query.setParameter("idOfDish", idOfDish);
        query.setParameter("idOfOrg", idOfOrg);
        List list = query.getResultList();
        Set<String> result = new HashSet<>();
        if (list.size() == 0) result.add("");
        for (Object obj : list) {
            result.add((String)obj);
        }
        return result;
    }

    public List<WtCategoryItem> getCategoryItemsByWtDish(Long idOfDish) {
        return entityManager.createQuery("select dish.categoryItems from WtDish dish where dish.idOfDish = :idOfDish")
                .setParameter("idOfDish", idOfDish)
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

    public List<WtDish> getWtDishesByOrgandDate(Org org, Date date) {
        Query query = entityManager.createNativeQuery("SELECT distinct d.idofdish, m.begindate, m.enddate FROM cf_wt_menu_groups mg "
                + "LEFT JOIN cf_wt_menu_group_relationships mgr ON mgr.idofmenugroup = mg.id "
                + "LEFT JOIN cf_wt_menu_group_dish_relationships mgd ON mgd.idofmenumenugrouprelation = mgr.id "
                + "LEFT JOIN cf_wt_dishes d ON mgd.idofdish = d.idofdish "
                + "LEFT JOIN cf_wt_menu m ON m.idofmenu = mgr.idofmenu "
                + "WHERE (m.idoforggroup in (select og.idoforggroup from cf_wt_org_groups og "
                + "join cf_wt_org_group_relations ogr on og.idoforggroup = ogr.idoforggroup where ogr.idoforg = :idOfOrg) "
                + "OR m.idofmenu in (select idofmenu from cf_wt_menu_org mo where mo.idoforg = :idOfOrg))"
                + "and mgr.deletestate = 0 and d.idofdish is not null " +
                "  and d.idofdish in (select cfa.idofdish from cf_foodbox_available cfa where " +
                " cfa.availableqty > 0 and cfa.idoforg = :idOfOrg)");
        query.setParameter("idOfOrg", org.getIdOfOrg());
        List list = query.getResultList();
        List<WtDish> dishes = new ArrayList<>();
        if (date != null) {
            for (Object o : list) {
                Object[] row = (Object[]) o;
                Long startDate = ((Timestamp) row[1]).getTime();
                Long endDate = ((Timestamp) row[2]).getTime();
                if (date.getTime() >= startDate && date.getTime() <= endDate)
                    dishes.add(entityManager.find(WtDish.class, ((BigInteger) row[0]).longValue()));
            }
        }
        else
        {
            for (Object o : list) {
                Object[] row = (Object[]) o;
                dishes.add(entityManager.find(WtDish.class, ((BigInteger) row[0]).longValue()));
            }
        }
        return dishes;
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

    public List<CategoryOrg> getAllWtCategoryOrgs(List<WtDiscountRule> wtDiscountRules) {
        return entityManager.createQuery("select distinct rule.categoryOrgs from WtDiscountRule rule where rule in :discountRules")
                .setParameter("discountRules", wtDiscountRules)
                .getResultList();
    }

    public List<CategoryDiscount> getAllWtCategoryDiscounts(List<WtDiscountRule> wtDiscountRules) {
        return entityManager.createQuery("select distinct rule.categoryDiscounts from WtDiscountRule rule where rule in :discountRules")
                .setParameter("discountRules", wtDiscountRules)
                .getResultList();
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

    public Long getClientGroupByClientId(Long idOfClient) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(Client.class);
        criteria.add(Restrictions.eq("idOfClient", idOfClient));
        Client client = (Client) criteria.uniqueResult();
        return client.getIdOfClientGroup();
    }

    public List<CategoryDiscount> getCategoryDiscountList() {
        TypedQuery<CategoryDiscount> q = entityManager
                .createQuery("from CategoryDiscount order by idOfCategoryDiscount", CategoryDiscount.class);
        return q.getResultList();
    }

    public List<CategoryDiscount> getCategoryDiscountListByCategoryName(String categoryName) {
        String strQuery = "from CategoryDiscount where lower(categoryName) = lower('" + categoryName
                + "') order by idOfCategoryDiscount";
        TypedQuery<CategoryDiscount> q = entityManager.createQuery(strQuery, CategoryDiscount.class);
        return q.getResultList();
    }

    public List<CategoryDiscountDSZN> getCategoryDiscountDSZNList() {
        TypedQuery<CategoryDiscountDSZN> q = entityManager
                .createQuery("from CategoryDiscountDSZN where deleted = false order by code",
                        CategoryDiscountDSZN.class);
        return q.getResultList();
    }

    public List<Contragent> getContragentsWithClassIds(List<Integer> classIds) {
        TypedQuery<Contragent> q = entityManager
                .createQuery("from Contragent where classId in (:classIds)", Contragent.class);
        q.setParameter("classIds", classIds);
        return q.getResultList();
    }

    public User findUserByUserName(String userName) throws Exception {
        javax.persistence.Query q = entityManager.createQuery("from User where userName=:userName");
        q.setParameter("userName", userName);
        return (User) q.getSingleResult();
    }

    public Boolean isMenuExchange(Long idOfOrg) {
        TypedQuery<Long> query = entityManager
                .createQuery("select idOfSourceOrg from MenuExchangeRule where idOfSourceOrg = :idOfSourceOrg",
                        Long.class);
        query.setParameter("idOfSourceOrg", idOfOrg);
        List<Long> list = query.getResultList();
        return !list.isEmpty();
    }

    public Contragent getContragentByName(String name) {
        TypedQuery<Contragent> query = entityManager
                .createQuery("from Contragent where contragentName=:name", Contragent.class);
        query.setParameter("name", name);
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public <T> T findDistributedObjectByRefGUID(Class<T> clazz, String guid) {
        TypedQuery<T> query = entityManager
                .createQuery("from " + clazz.getSimpleName() + " where guid='" + guid + "'", clazz);
        List<T> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public ConfigurationProvider getConfigurationProvider(Long idOfConfigurationProvider) throws Exception {
        return entityManager.find(ConfigurationProvider.class, idOfConfigurationProvider);
    }

    public Long getContractIdByTempCardNoAndCheckValidDate(long lCardId, int days) throws Exception {
        /* так как в поле хранится дата на 00:00 ночи текущего дня вычтем из текущего дня 24 часа в милисекудах */
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(CardTemp.class);
        criteria.createAlias("client", "cl");
        criteria.add(Restrictions.eq("cardNo", lCardId));
        if (days > 0) {
            Date currentDay = CalendarUtils.truncateToDayOfMonth(new Date());
            currentDay = CalendarUtils.addDays(currentDay, -days);
            criteria.add(Restrictions.ge("validDate", currentDay));
        }
        criteria.setProjection(Projections.property("cl.contractId"));
        criteria.setMaxResults(1);
        return (Long) criteria.uniqueResult();
    }

    public Client getClientByContractId(long contractId) {
        return DAOUtils.findClientByContractId(entityManager, contractId);
    }

    public List<Client> findClientsBySan(String san) {
        return DAOUtils.findClientsBySan(entityManager, san);
    }

    public List<TechnologicalMapProduct> getTechnologicalMapProducts(TechnologicalMap technologicalMap) {
        TypedQuery<TechnologicalMapProduct> query = entityManager
                .createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap",
                        TechnologicalMapProduct.class);
        query.setParameter("technologicalMap", technologicalMap);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByTechnologicalMapGroup(
            TechnologicalMapGroup technologicalMapGroup) {
        TypedQuery<TechnologicalMap> query = entityManager
                .createQuery("from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup",
                        TechnologicalMap.class);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public Org findOrById(long idOfOrg) {
        return entityManager.find(Org.class, idOfOrg);
    }

    public List<Long> findFriendlyOrgsIds(long idOfOrg) {
        List<Long> ids = new LinkedList<>();
        Session session = entityManager.unwrap(Session.class);
        Org org = (Org) session.load(Org.class, idOfOrg);
        for (Org org1 : org.getFriendlyOrg()) {
            ids.add(org1.getIdOfOrg());
        }
        return ids;
    }

    public ReportInfo findReportInfoById(long idOfReportInfo) {
        return entityManager.find(ReportInfo.class, idOfReportInfo);
    }

    public Client findClientById(Long idOfClient) {
        return entityManager.find(Client.class, idOfClient);
    }

    public String getClientFullNameById(Long idOfClient) {
        Client client = findClientById(idOfClient);
        if (client != null) {
            return client.getPerson().getFullName();
        } else {
            return null;
        }
    }

    public boolean doesClientBelongToFriendlyOrgs(Long orgId, Long idOfClient) throws Exception {
        Org org = entityManager.find(Org.class, orgId);
        if (org == null) {
            throw new Exception("Организация не найдена: " + orgId);
        }
        Client cl = entityManager.find(Client.class, idOfClient);
        if (cl == null) {
            throw new Exception("Клиент не найден: " + idOfClient);
        }
        if (cl.getOrg().getIdOfOrg().equals(orgId)) {
            return true;
        }
        Set<Org> friendlyOrgs = org.getFriendlyOrg();
        for (Org o : friendlyOrgs) {
            if (cl.getOrg().getIdOfOrg().equals(o.getIdOfOrg())) {
                return true;
            }
        }
        return false;
    }

    public List<Client> findClientsByMobilePhone(String mobilePhone) {
        TypedQuery<Client> query = entityManager.createQuery("from Client where mobile=:mobile", Client.class);
        query.setParameter("mobile", mobilePhone);
        return query.getResultList();
    }

    public List<String> getReportHandleRuleNames() {
        TypedQuery<String> query = entityManager
                .createQuery("select ruleName from ReportHandleRule order by ruleName", String.class);
        return query.getResultList();
    }

    public long getStatClientsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientsWithMobile() {
        Query q = entityManager
                .createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE mobile IS NOT NULL AND mobile<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientsWithEmail() {
        Query q = entityManager
                .createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE email IS NOT NULL AND email<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatUniqueClientsWithPaymentTransaction() {
        Query q = entityManager.createNativeQuery(
                "SELECT count(DISTINCT idofclient) FROM cf_clientpayments cp, cf_transactions t WHERE cp.idoftransaction=t.idoftransaction");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatUniqueClientsWithEnterEvent() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(DISTINCT idOfClient) FROM cf_enterevents");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatClientPaymentsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clientpayments");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getStatOrdersCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_orders");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getEnterEventsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_enterevents");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getClientSmsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clientsms");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsWithPurchaseOfFoodPayPurchaseReducedPriceMeals() {
        Query q = entityManager.createNativeQuery(
                "SELECT count(DISTINCT idofclient) FROM cf_orders o, cf_orderdetails od WHERE o.idoforder=od.idoforder AND o.idoforg=od.idoforg ");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsWithPurchaseOfMealBenefits() {
        Query q = entityManager.createNativeQuery(
                "SELECT count(DISTINCT idofclient) FROM cf_orders o, cf_orderdetails od WHERE o.idoforder=od.idoforder AND o.idoforg=od.idoforg AND od.menutype>50");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long callClientsPayPowerPurchase() {
        Query q = entityManager.createNativeQuery(
                "SELECT count(DISTINCT idofclient) FROM cf_orders o, cf_orderdetails od WHERE o.idoforder=od.idoforder AND o.idoforg=od.idoforg AND od.menutype=0");
        return Long.parseLong("" + q.getSingleResult());
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getStatPaymentsByContragents(Date fromDate, Date toDate) {
        Query q = entityManager.createNativeQuery(
                "SELECT contragentName, paymentMethod, AVG(paysum), SUM(paysum), COUNT(*) FROM cf_clientpayments cp JOIN cf_contragents cc ON cp.idOfContragent=cc.idOfContragent WHERE cp.createdDate>=:fromDate AND cp.createdDate<=:toDate GROUP BY contragentName, paymentMethod ORDER BY contragentName, paymentMethod");
        q.setParameter("fromDate", fromDate.getTime());
        q.setParameter("toDate", toDate.getTime());
        return (List<Object[]>) q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getMonitoringPayLastTransactionStats() {
        Query q = entityManager.createNativeQuery(
                "SELECT cp.idOfContragent, cc.contragentName, MAX(cp.createddate) FROM cf_clientpayments cp, cf_contragents cc WHERE cp.idOfContragent=cc.idOfContragent AND cc.classid=1 GROUP BY cp.idOfContragent, cc.contragentName");
        return (List<Object[]>) q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getMonitoringPayDayTransactionsStats(Date fromDate, Date toDate) {
        Query q = entityManager.createNativeQuery(
                "SELECT cp.idOfContragent, cc.contragentName, COUNT(*) FROM cf_clientpayments cp, cf_contragents cc WHERE cp.idOfContragent=cc.idOfContragent AND cc.classid=1 AND cp.createdDate>=:fromDate AND cp.createdDate<:toDate GROUP BY cp.idOfContragent, cc.contragentName");
        q.setParameter("fromDate", fromDate.getTime());
        q.setParameter("toDate", toDate.getTime());
        return (List<Object[]>) q.getResultList();
    }

    public List<Contragent> getContragentsList() {
        return getContragentsList(null);
    }

    @Transactional
    public List<Contragent> getContragentsListFromOrders() {
        String q = "SELECT distinct c from Contragent c, Order o WHERE o.contragent=c";
        TypedQuery<Contragent> query = entityManager.createQuery(q, Contragent.class);
        List<Contragent> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }


    public List<Contragent> getContragentsList(Integer classId) {
        String q = "from Contragent";
        if (classId != null) {
            q += " c WHERE c.classId=:classId order by idOfContragent";
        }
        TypedQuery<Contragent> query = entityManager.createQuery(q, Contragent.class);
        if (classId != null) {
            query.setParameter("classId", classId);
        }
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    public Contragent getContragentByBIC(String bic) {
        TypedQuery<Contragent> query = entityManager
                .createQuery("from Contragent where bic=:bic and classId=:classId", Contragent.class);
        query.setParameter("bic", bic);
        query.setParameter("classId", Contragent.PAY_AGENT);
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public Contragent getRNIPContragent() {
        TypedQuery<Contragent> query = entityManager
                .createQuery("from Contragent where remarks=:remarks", Contragent.class);
        query.setParameter("remarks", "RNIP_DEFAULT");
        List<Contragent> result = query.getResultList();

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public Org getOrgByGuid(String guid) {
        javax.persistence.Query q = entityManager.createQuery("from Org where guid=:guid");
        q.setParameter("guid", guid);
        List l = q.getResultList();
        if (l.size() == 0) {
            return null;
        }
        return ((Org) l.get(0));
    }

    public List<Org> findOrgByRegistryIdOrGuid(Long registryId, String guid) {
        javax.persistence.Query q = entityManager
                .createQuery("from Org where guid=:guid or uniqueaddressid=:registryId");
        q.setParameter("guid", guid);
        q.setParameter("registryId", registryId);
        return q.getResultList();
    }

    public Org findOrgByRegistryDataByMainField(Long uniqueAddressId, String field_name, Object field_value, String inn,
            Long unom, Long unad, Boolean skipThirdPart) {
        //Новый алгоритм поиска организации в нашей БД по данным от реестров. Сраниваем в 3 этапа по разным наборам полей
        entityManager.setFlushMode(FlushModeType.COMMIT);
        javax.persistence.Query q;
        if (uniqueAddressId != null) {
            q = entityManager.createQuery(String.format(
                    "from Org o left join fetch o.officialPerson p where o.%s=:parameter and o.uniqueAddressId=:uniqueAddressId",
                    field_name));
            q.setParameter("parameter", field_value);
            q.setParameter("uniqueAddressId", uniqueAddressId);
            List<Org> orgs = q.getResultList();   //1 этап
            if (orgs != null && orgs.size() > 0) {
                return orgs.get(0);
            } else {
                q = entityManager.createQuery(
                        "from Org o left join fetch o.officialPerson p where o.INN=:inn and o.uniqueAddressId=:uniqueAddressId");
                q.setParameter("inn", inn);
                q.setParameter("uniqueAddressId", uniqueAddressId);
                List<Org> orgs2 = q.getResultList();  //2 этап
                if (orgs2 != null && orgs2.size() > 0) {
                    return orgs2.get(0);
                }
            }
        }
        if (null != skipThirdPart && skipThirdPart && (null == unom || null == unad)) {
            return null;
        }
        q = entityManager
                .createQuery("from Org o left join fetch o.officialPerson p where o.btiUnom=:unom and o.btiUnad=:unad");
        q.setParameter("unom", unom);
        q.setParameter("unad", unad);
        List<Org> orgs3 = q.getResultList();   //3 этап
        if (orgs3 != null && orgs3.size() > 0) {
            return orgs3.get(0);
        } else {
            return null;
        }
    }

    public Org findOrgByRegistryData(Long uniqueAddressId, String guid, String inn, Long unom, Long unad,
            Boolean skipThirdPart) {
        return findOrgByRegistryDataByMainField(uniqueAddressId, "guid", guid, inn, unom, unad, skipThirdPart);
    }

    public List<Org> findOrgsByNSIId(Long nsiId) {
        Query q = entityManager.createQuery("select org from Org org where org.orgIdFromNsi = :nsiId");
        q.setParameter("nsiId", nsiId);
        return q.getResultList();
    }

    public List<Org> findOrgsByGuidAddressINNOrNumber(String guid, String address, String inn, String number) {
        String queryStr;
        if (StringUtils.isEmpty(address)) {
            queryStr = "from Org where 1=0";
        } else {
            queryStr = "from Org where address = :address";
        }

        if (!StringUtils.isEmpty(guid)) {
            queryStr += " or guid = :guid";
        }
        if (!StringUtils.isEmpty(inn)) {
            queryStr += " or INN = :inn";
        }
        if (!StringUtils.isEmpty(number)) {
            queryStr += " or shortName like :number";
        }
        javax.persistence.Query q = entityManager.createQuery(queryStr);
        if (!StringUtils.isEmpty(address)) {
            q.setParameter("address", address);
        }
        if (!StringUtils.isEmpty(guid)) {
            q.setParameter("guid", guid);
        }
        if (!StringUtils.isEmpty(inn)) {
            q.setParameter("inn", inn);
        }
        if (!StringUtils.isEmpty(number)) {
            q.setParameter("number", number);
        }
        return q.getResultList();
    }

    public Client getClientByGuid(String guid) {
        return DAOUtils.findClientByGuid(entityManager, guid);
    }

    public Client getClientByMeshGuid(String guid) {
        return DAOUtils.findClientByMeshGuid(entityManager.unwrap(Session.class), guid);
    }

    public Client getClientByMobilePhone(String mobile) {
        return DAOUtils.getClientByMobilePhone(entityManager, mobile);
    }

    public List<Client> getClientsListByMobilePhone(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return Collections.EMPTY_LIST;
        }
        List<Client> res = DAOUtils.getClientsListByMobilePhone(entityManager, mobile);
        if (CollectionUtils.isEmpty(res)) {
            return Collections.EMPTY_LIST;
        }
        return res;
    }

    public ReportHandleRule getReportHandleRule(long idOfReportHandleRule) {
        try {
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(ReportHandleRule.class);
            criteria.add(Restrictions.eq("idOfReportHandleRule", idOfReportHandleRule));
            return (ReportHandleRule) criteria.uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    public String getReportHandlerType(long idOfReportHandleRule) {
        try {
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(ReportHandleRule.class);
            criteria.add(Restrictions.eq("idOfReportHandleRule", idOfReportHandleRule));
            ReportHandleRule handleRule = (ReportHandleRule) criteria.uniqueResult();
            return handleRule.findType(session);
        } catch (Exception e) {
            return "";
        }
    }

    public List<ReportHandleRule> getReportHandlerRules(boolean manualAllowed) {
        try {
            Criteria reportRulesCriteria = ReportHandleRule
                    .createAllReportRulesCriteria(manualAllowed, (Session) entityManager.getDelegate());
            return (List<ReportHandleRule>) reportRulesCriteria.list();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

    public List<RuleCondition> getReportHandlerRules(Long ruleId) {
        TypedQuery<RuleCondition> query = entityManager
                .createQuery("from RuleCondition where idOfRuleCondition=:handler", RuleCondition.class);
        query.setParameter("handler", ruleId);
        return query.getResultList();
    }

    public Contragent getContragentById(Long idOfContragent) throws Exception {
        return DAOUtils.findContragent((Session) entityManager.getDelegate(), idOfContragent);
    }

    public Contract getContractById(Long idOfContract) throws Exception {
        return DAOUtils.findContract((Session) entityManager.getDelegate(), idOfContract);
    }

    public String getContractNameById(Long idOfContract) throws Exception {
        Contract contract = DAOUtils.findContract((Session) entityManager.getDelegate(), idOfContract);
        return contract.getContractNumber();
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProvidersList() {
        return entityManager.createQuery("from ConfigurationProvider order by id", ConfigurationProvider.class)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<ConfigurationProvider> findConfigurationProvidersList(String filter) {
        return entityManager.createQuery(
                "from ConfigurationProvider where UPPER(name) like '%" + filter.toUpperCase() + "%' order by id",
                ConfigurationProvider.class).getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(Boolean deletedStatusSelected) {
        TypedQuery<GoodGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from GoodGroup order by globalId", GoodGroup.class);
        } else {
            query = entityManager
                    .createQuery("from GoodGroup where deletedState=false order by globalId", GoodGroup.class);
        }
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<GoodGroup> query;
        if (deletedStatusSelected) {
            query = entityManager
                    .createQuery("from GoodGroup where orgOwner in :orgOwners order by globalId", GoodGroup.class);
        } else {
            query = entityManager
                    .createQuery("from GoodGroup where orgOwner in :orgOwners and deletedState=false order by globalId",
                            GoodGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(String filter) {
        TypedQuery<GoodGroup> query = entityManager.createQuery(
                "from GoodGroup where UPPER(nameOfGoodsGroup) like '%" + filter.toUpperCase()
                        + "%' and deletedState=false order by globalId", GoodGroup.class);
        return query.getResultList();
    }

    public List<GoodGroup> findGoodGroupBySuplifier(List<Long> orgOwners, String filter) {
        TypedQuery<GoodGroup> query = entityManager.createQuery(
                "from GoodGroup where UPPER(nameOfGoodsGroup) like '%" + filter.toUpperCase()
                        + "%' and orgOwner in :orgOwners and deletedState=false  order by globalId", GoodGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<Good> findGoodsByGoodGroup(GoodGroup goodGroup, Boolean deletedStatusSelected) {
        TypedQuery<Good> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from Good where goodGroup=:goodGroup order by globalId", Good.class);
        } else {
            query = entityManager
                    .createQuery("from Good where goodGroup=:goodGroup and deletedState=false order by globalId",
                            Good.class);
        }
        query.setParameter("goodGroup", goodGroup);
        return query.getResultList();
    }

    public List<Good> findGoodsByGoodGroup(GoodGroup goodGroup, List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<Good> query;
        if (deletedStatusSelected) {
            query = entityManager
                    .createQuery("from Good where goodGroup=:goodGroup and orgOwner in :orgOwner order by globalId",
                            Good.class);
        } else {
            query = entityManager.createQuery(
                    "from Good where goodGroup=:goodGroup and orgOwner in :orgOwner and deletedState=false order by globalId",
                    Good.class);
        }
        query.setParameter("goodGroup", goodGroup);
        query.setParameter("orgOwner", orgOwners);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Good> findGoods(ConfigurationProvider configurationProvider, GoodGroup goodGroup, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(Good.class);
        if (goodGroup != null) {
            criteria.add(Restrictions.eq("goodGroup", goodGroup));
        }
        if (orgOwners != null && !orgOwners.isEmpty()) {
            criteria.add(Restrictions.in("orgOwner", orgOwners));
        }
        if (configurationProvider != null) {
            criteria.add(
                    Restrictions.eq("idOfConfigurationProvider", configurationProvider.getIdOfConfigurationProvider()));
        }
        if (deletedStatusSelected != null && !deletedStatusSelected) {
            criteria.add(Restrictions.eq("deletedState", false));
        }
        return criteria.list();
    }

    public List<GoodsBasicBasket> findGoodsBasicBasket() {
        TypedQuery<GoodsBasicBasket> query = entityManager
                .createQuery("from GoodsBasicBasket order by idOfBasicGood", GoodsBasicBasket.class);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",
                    ProductGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",
                    ProductGroup.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",
                    ProductGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from ProductGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",
                    ProductGroup.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from ProductGroup where orgOwner in :orgOwners order by globalId",
                    ProductGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from ProductGroup where orgOwner in :orgOwners and deletedState=false order by globalId",
                    ProductGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(String filter) {
        TypedQuery<ProductGroup> query = entityManager.createQuery(
                "from ProductGroup where UPPER(nameOfGroup) like '%" + filter.toUpperCase()
                        + "%' and deletedState=false order by globalId", ProductGroup.class);
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<ProductGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from ProductGroup order by globalId", ProductGroup.class);
        } else {
            query = entityManager
                    .createQuery("from ProductGroup where deletedState=false order by globalId", ProductGroup.class);
        }
        return query.getResultList();
    }

    public List<ProductGroup> findProductGroupByConfigurationProvider(List<Long> orgOwners, String filter) {
        TypedQuery<ProductGroup> query = entityManager.createQuery(
                "from ProductGroup where UPPER(nameOfGroup) like '%" + filter.toUpperCase()
                        + "%' and orgOwner in :orgOwners and deletedState=false order by globalId", ProductGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Product> findProductByConfigurationProvider(ProductGroup pg, Long confProviderId, Boolean deleteSt,
            List<Long> orgOwners, String productName) {
        Session session = (Session) entityManager.getDelegate();
        Criteria cr = session.createCriteria(Product.class);
        Conjunction conj = Restrictions.conjunction();
        if (pg != null) {
            conj.add(Restrictions.eq("productGroup", pg));
        }
        if (confProviderId != null) {
            conj.add(Restrictions.eq("idOfConfigurationProvider", confProviderId));
        }
        if (deleteSt != null && !deleteSt) {
            conj.add(Restrictions.eq("deletedState", false));
        }
        if (orgOwners != null && !orgOwners.isEmpty()) {
            conj.add(Restrictions.in("orgOwner", orgOwners));
        }
        if (productName != null && !productName.isEmpty()) {
            conj.add(Restrictions.ilike("productName", productName, MatchMode.ANYWHERE));
        }
        cr.add(conj).addOrder(org.hibernate.criterion.Order.asc("globalId"));
        return (List<Product>) cr.list();
    }

        public List<Product> findProductByConfigurationProvider (List < Long > orgOwners, String productName){
            return findProductByConfigurationProvider(orgOwners, false, productName);
    }

        public List<Product> findProductByConfigurationProvider (List < Long > orgOwners, Boolean
        deletedStatusSelected, String productName){
            return findProductByConfigurationProvider(null, null, deletedStatusSelected, orgOwners, productName);
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",
                    TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",
                    TechnologicalMapGroup.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",
                    TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMapGroup.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if (deletedStatusSelected) {
            query = entityManager
                    .createQuery("from TechnologicalMapGroup order by globalId", TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMapGroup where deletedState=false order by globalId",
                    TechnologicalMapGroup.class);
        }
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMapGroup> query;
        if (deletedStatusSelected) {
            query = entityManager
                    .createQuery("from TechnologicalMapGroup where orgOwner in :orgOwners order by globalId",
                            TechnologicalMapGroup.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMapGroup where orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMapGroup.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(String filter) {
        TypedQuery<TechnologicalMapGroup> query = entityManager.createQuery(
                "from TechnologicalMapGroup where UPPER(nameOfGroup) like '%" + filter.toUpperCase()
                        + "%' and deletedState=false order by globalId", TechnologicalMapGroup.class);
        return query.getResultList();
    }

    public List<TechnologicalMapGroup> findTechnologicalMapGroupByConfigurationProvider(List<Long> orgOwners,
            String filter) {
        TypedQuery<TechnologicalMapGroup> query = entityManager.createQuery(
                "from TechnologicalMapGroup where UPPER(nameOfGroup) like '%" + filter.toUpperCase()
                        + "%' and orgOwner in :orgOwners and deletedState=false order by globalId",
                TechnologicalMapGroup.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<Product> findProduct(ProductGroup productGroup, ConfigurationProvider provider, String filter,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findProduct(session, productGroup, provider, filter, orgOwners, deletedStatusSelected);
    }


    public List<GoodGroup> findGoodGroup(ConfigurationProvider provider, String filter, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findGoodGroup(session, provider, filter, orgOwners, deletedStatusSelected);
    }

    public Long countProductsByProductGroup(ProductGroup currentProductGroup) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.countProductByProductGroup(session, currentProductGroup);
    }

    public List<Product> findProductByProductGroup(ProductGroup currentProductGroup) {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findProductByProductGroup(session, currentProductGroup);
    }

    public List<TechnologicalMapProduct> findTechnologicalMapProductByTechnologicalMap(Long idOfTechnologicalMap) {
        String sql = "from TechnologicalMapProduct where technologicalMap.globalId=:idOfTechnologicalMap and deletedState=false order by globalId";
        TypedQuery<TechnologicalMapProduct> query = entityManager.createQuery(sql, TechnologicalMapProduct.class);
        query.setParameter("idOfTechnologicalMap", idOfTechnologicalMap);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from TechnologicalMap order by globalId", TechnologicalMap.class);
        } else {
            query = entityManager.createQuery("from TechnologicalMap where deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(Long idOfConfigurationProvider,
            List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery("from TechnologicalMap where orgOwner in :orgOwners order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(
            TechnologicalMapGroup technologicalMapGroup, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(
            TechnologicalMapGroup technologicalMapGroup, Long idOfConfigurationProvider,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(
            TechnologicalMapGroup technologicalMapGroup, Long idOfConfigurationProvider, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and idOfConfigurationProvider=:idOfConfigurationProvider and orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("idOfConfigurationProvider", idOfConfigurationProvider);
        query.setParameter("orgOwners", orgOwners);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(
            TechnologicalMapGroup technologicalMapGroup, List<Long> orgOwners, Boolean deletedStatusSelected) {
        TypedQuery<TechnologicalMap> query;
        if (deletedStatusSelected) {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and orgOwner in :orgOwners order by globalId",
                    TechnologicalMap.class);
        } else {
            query = entityManager.createQuery(
                    "from TechnologicalMap where technologicalMapGroup=:technologicalMapGroup and orgOwner in :orgOwners and deletedState=false order by globalId",
                    TechnologicalMap.class);
        }
        query.setParameter("orgOwners", orgOwners);
        query.setParameter("technologicalMapGroup", technologicalMapGroup);
        return query.getResultList();
    }


    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(String filter) {
        TypedQuery<TechnologicalMap> query = entityManager.createQuery(
                "from TechnologicalMap where UPPER(nameOfTechnologicalMap) like '%" + filter.toUpperCase()
                        + "%' and deletedState=false order by globalId", TechnologicalMap.class);
        return query.getResultList();
    }

    public List<TechnologicalMap> findTechnologicalMapByConfigurationProvider(List<Long> orgOwners, String filter) {
        TypedQuery<TechnologicalMap> query = entityManager.createQuery(
                "from TechnologicalMap where UPPER(nameOfTechnologicalMap) like '%" + filter.toUpperCase()
                        + "%' and orgOwner in :orgOwners and deletedState=false order by globalId",
                TechnologicalMap.class);
        query.setParameter("orgOwners", orgOwners);
        return query.getResultList();
    }

    public List<ComplexRole> findComplexRoles() {
        return entityManager.createQuery("from ComplexRole order by idOfRole", ComplexRole.class).getResultList();
    }

    public List<CategoryDiscount> getCategoryDiscountListWithIds(List<Long> idOfCategoryList) {
        TypedQuery<CategoryDiscount> q = entityManager
                .createQuery("from CategoryDiscount where idOfCategoryDiscount in (:idOfCategoryList)",
                        CategoryDiscount.class);
        q.setParameter("idOfCategoryList", idOfCategoryList);
        return q.getResultList();
    }

    public List<CategoryOrg> getCategoryOrgWithIds(List<Long> idOfCategoryOrgList) {
        TypedQuery<CategoryOrg> q = entityManager
                .createQuery("from CategoryOrg where idOfCategoryOrg in (:idOfCategoryOrgList)", CategoryOrg.class);
        q.setParameter("idOfCategoryOrgList", idOfCategoryOrgList);
        return q.getResultList();
    }

    public List<CategoryOrg> getCategoryOrgByCategoryName(String categoryName) {
        TypedQuery<CategoryOrg> q = entityManager
                .createQuery("from CategoryOrg where lower(categoryName) = lower(:categoryName)", CategoryOrg.class);
        q.setParameter("categoryName", categoryName);
        return q.getResultList();
    }

    public List<Org> findOrgsByConfigurationProvider(ConfigurationProvider currentConfigurationProvider) {
        TypedQuery<Org> query = entityManager
                .createQuery("from Org where configurationProvider=:configurationProvider", Org.class);
        query.setParameter("configurationProvider", currentConfigurationProvider);
        return query.getResultList();
    }

    public Good getGood(Long globalId) {
        return entityManager.find(Good.class, globalId);
    }

    public List<Client> findClientsForOrgAndFriendly(Long idOfOrg, boolean lazyLoadInit) throws Exception {
        List<Client> cl = DAOUtils.findClientsForOrgAndFriendly(entityManager, entityManager.find(Org.class, idOfOrg));
        if (lazyLoadInit) {
            for (Client c : cl) {
                if (c == null) {
                    continue;
                }
                Person p = c.getPerson();
                if (p != null) {
                    p.getSurname();
                }
                ClientGroup clg = c.getClientGroup();
                if (clg != null) {
                    clg.getGroupName();
                }
            }
        }
        return cl;
    }

    public Set<Org> getFriendlyOrgs(Long idOfOrg) {
        Org org = entityManager.find(Org.class, idOfOrg);
        return org.getFriendlyOrg();
    }

    public List<RegistryChange> getLastRegistryChanges_WithFullFIO(long idOfOrg, long revisionDate, Integer actionFilter,
            String lastName, String firstName, String patronymic, String className) throws Exception {
        if (revisionDate < 1L) {
            revisionDate = getLastRegistryChangeUpdate(idOfOrg, className);
        }
        if (revisionDate < 1) {
            return Collections.EMPTY_LIST;
        }
        String nameStatement = "";
        if (!StringUtils.isEmpty(lastName)) {
            String filter = lastName.trim().toLowerCase().replaceAll(" ", "");
            nameStatement += " and lower(surname) like lower('%" + filter + "%') ";
        }
        if (!StringUtils.isEmpty(firstName)) {
            String filter = firstName.trim().toLowerCase().replaceAll(" ", "");
            nameStatement += " and lower(firstname) like lower('%" + filter + "%') ";
        }
        if (!StringUtils.isEmpty(patronymic)) {
            String filter = patronymic.trim().toLowerCase().replaceAll(" ", "");
            nameStatement += " and lower(secondname) like lower('%" + filter + "%') ";
        }

        String actionStatement = "";
        if (actionFilter != null && actionFilter > 0) {
            actionStatement = " and operation=:operation ";
        }
        String q = "from " + className + " where idOfOrg=:idOfOrg and createDate=:lastUpdate" + nameStatement
                + actionStatement + " order by groupName, surname, firstName, secondName";
        //TypedQuery<RegistryChange> query = entityManager.createQuery(q, RegistryChange.class);
        Query query = entityManager.createQuery(q);
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("lastUpdate", revisionDate);
        if (actionFilter != null && actionFilter > 0) {
            query.setParameter("operation", actionFilter);
        }
        return query.getResultList();
    }

    public List<RegistryChange> getLastRegistryChanges(long idOfOrg, long revisionDate, Integer actionFilter,
            String nameFilter, String className) throws Exception {
        return getLastRegistryChanges_WithFullFIO(idOfOrg, revisionDate, actionFilter,
                nameFilter, null, null, className);
    }

    public long getLastRegistryChangeUpdate(long idOfOrg, String className) throws Exception {
        String tableName = className.equals("RegistryChange") ? "cf_registrychange" : "cf_registrychange_employee";
        Query q = entityManager
                .createNativeQuery("SELECT max(createDate) FROM " + tableName + " WHERE idOfOrg=:idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        Object res = q.getSingleResult();
        return Long.parseLong("" + (res == null || res.toString().length() < 1 ? 0 : res.toString()));
    }

    public List<Object[]> getRegistryChangeRevisions(long idOfOrg, String className) throws Exception {
        Query query = entityManager.createQuery(
                "select distinct createDate, type from " + className + " where idOfOrg=:idOfOrg order by createDate desc");
        query.setParameter("idOfOrg", idOfOrg);
        return query.getResultList();
    }

    public List<RegistryChangeError> getRegistryChangeErrors(long idOfOrg) throws Exception {
        String orgClause = "";
        if (idOfOrg > -1) {
            orgClause = "where idOfOrg=:idOfOrg";
        }
        TypedQuery<RegistryChangeError> query = entityManager
                .createQuery("from RegistryChangeError " + orgClause + " order by createDate desc",
                        RegistryChangeError.class);
        if (idOfOrg > -1) {
            query.setParameter("idOfOrg", idOfOrg);
        }
        return query.getResultList();
    }

    public Long getMainRegistryByItemId(Long idOfItem) {
        Session session = (Session) entityManager.getDelegate();
        try {
            OrgRegistryChangeItem item = (OrgRegistryChangeItem) session.load(OrgRegistryChangeItem.class, idOfItem);
            return item.getOrgRegistryChange().getIdOfOrgRegistryChange();
        } catch (Exception e) {
            logger.error("Error retrieving MainRegistryChange by OrgRegistryChangeItem ID", e);
            return null;
        }
    }

    public List<String> getCurrentRepositoryReportNames() {
        TypedQuery<String> query = entityManager
                .createQuery("select distinct ruleName from ReportInfo order by ruleName", String.class);
        return query.getResultList();
    }

    public List<BigInteger> getCleanupRepositoryReportsByDate() throws Exception {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery("SELECT idofreportinfo " + "FROM cf_reportinfo "
                + "LEFT JOIN cf_reporthandlerules ON cf_reportinfo.rulename=cf_reporthandlerules.rulename "
                + "WHERE cf_reporthandlerules.storageperiod<>-1 AND "
                + "      (cf_reporthandlerules.storageperiod=0 OR "
                + "       createddate<EXTRACT(EPOCH FROM now())*1000-cf_reporthandlerules.storageperiod)");
        List<BigInteger> list = (List<BigInteger>) q.list();
        return list;
    }

    public Contragent findContragentByClient(Long clientContractId) {
        TypedQuery<Contragent> query = entityManager.createQuery(
                        "select o.defaultSupplier from Org o join o.clientsInternal cl \n"
                                + "where cl.contractId = :contractId", Contragent.class)
                .setParameter("contractId", clientContractId);
        List<Contragent> res = query.getResultList();
        return res.isEmpty() ? null : res.get(0);
    }

    public long getComplexPrice(long idoforg, int complex) {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery(
                "SELECT currentprice, max(menudate) AS d " + "FROM cf_complexinfo "
                        + "WHERE idoforg=:idoforg AND idofcomplex=:complex " + "GROUP BY currentprice "
                        + "ORDER BY d DESC");
        q.setLong("idoforg", idoforg);
        q.setInteger("complex", complex);
        List res = q.list();
        for (Object o : res) {
            Object entry[] = (Object[]) o;
            if (entry[0] == null) {
                return 0L;
            }
            return ((BigInteger) entry[0]).longValue();
        }
        return 0L;
    }

    public ComplexInfo getComplexInfo(Client client, Integer idOfComplex, Date date) {
        Query query = entityManager.createQuery("select ci from ComplexInfo ci where ci.org.idOfOrg = :idOfOrg "
                + "and ci.idOfComplex = :idOfComplex and ci.menuDate between :startDate and :endDate");
        query.setParameter("idOfOrg", client.getOrg().getIdOfOrg());
        query.setParameter("idOfComplex", idOfComplex);
        query.setParameter("startDate", CalendarUtils.startOfDay(date));
        query.setParameter("endDate", CalendarUtils.endOfDay(date));
        try {
            return (ComplexInfo)query.getSingleResult();
        } catch (Exception e) {
            logger.error(String.format("Cant find complexInfo idOfComplex=%s, date=%s, idOfClient=%s", idOfComplex, date.getTime(), client.getIdOfClient()), e);
            return null;
        }
    }

    public String[] getDistricts() {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session
                .createSQLQuery("SELECT DISTINCT district FROM cf_orgs WHERE district<>'' ORDER BY district");
        List<String> list = (List<String>) q.list();
        return list.toArray(new String[list.size()]);
    }

    public Map<Long, String> getUserOrgses(Long userId, UserNotificationType type) {
        Session session = entityManager.unwrap(Session.class);
        Map<Long, String> map = new HashMap<Long, String>();
        Criteria criteria = session.createCriteria(UserOrgs.class);
        criteria.add(Restrictions.eq("user.idOfUser", userId));
        criteria.add(Restrictions.eq("userNotificationType", type));
        List<UserOrgs> list = criteria.list();
        for (UserOrgs userOrgs : list) {
            final Org org = userOrgs.getOrg();
            map.put(org.getIdOfOrg(), org.getShortName());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public boolean existsOrgByIdAndTags(Long idOfOrg, String tag) {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        criteria.add(Restrictions.ilike("tag", tag, MatchMode.ANYWHERE));
        List<Org> list = criteria.list();
        return CollectionUtils.isNotEmpty(list);
    }

    public List<String> getRegions() {
        Session session = (Session) entityManager.getDelegate();
        return DAOUtils.getRegions(session);
    }

    public List<Org> getOrgsByDefaultSupplier(Contragent supplier) {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createQuery("from Org where defaultSupplier = :supplier");
        q.setParameter("supplier", supplier);
        return (List<Org>) q.list();
    }

    public List<Client> getNotBindedEMPClients(int clientsPerPackage) {
        String q = "from Client where (ssoid is null or ssoid='') and (mobile is not null and mobile<>'')";//and clientGUID<>''";
        TypedQuery<Client> query = entityManager.createQuery(q, Client.class);
        query.setMaxResults(clientsPerPackage);
        return query.getResultList();
    }

    public Person getPersonByClient(Client client) {
        Query query = entityManager.createQuery("select c.person from Client c where c = :client");
        query.setParameter("client", client);
        List<Person> list = query.getResultList();
        return list.get(0);
    }

    public long getNotBindedEMPClientsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE ssoid IS null AND mobile<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getBindedEMPClientsCount() {
        Query q = entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM cf_clients WHERE ssoid IS NOT null AND ssoid<>'-1' AND mobile<>''");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getBindWaitingEMPClients() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE ssoid='-1'");
        return Long.parseLong("" + q.getSingleResult());
    }

    public long getBindEMPErrorsCount() {
        Query q = entityManager.createNativeQuery("SELECT COUNT(*) FROM cf_clients WHERE ssoid LIKE 'E:%'");
        return Long.parseLong("" + q.getSingleResult());
    }

    public ExternalSystemStats getAllPreviousStatsForExternalSystem(String systemName, String instance) {
        Query q = entityManager.createNativeQuery(
                "SELECT CreateDate, StatisticId, StatisticValue " + "FROM cf_external_system_stats "
                        + "WHERE CreateDate=(SELECT max(CreateDate) FROM cf_external_system_stats WHERE SystemName=:systemName AND Instance=:instance);");
        q.setParameter("systemName", systemName);
        q.setParameter("instance", instance);
        List res = q.getResultList();
        ExternalSystemStats result = null;
        for (Object o : res) {
            Object entry[] = (Object[]) o;
            BigInteger createDate = (BigInteger) entry[0];
            Integer typeId = (Integer) entry[1];
            BigDecimal value = (BigDecimal) entry[2];
            if (result == null) {
                result = new ExternalSystemStats(new Date(createDate.longValue()), systemName, instance);
            }
            result.setValue(typeId.intValue(), value.doubleValue());
        }
        if (result == null) {
            result = new ExternalSystemStats(new Date(System.currentTimeMillis()), systemName, instance);
        }
        return result;
    }

    public OrgRegistryChange getOrgRegistryChange(long idOfOrgRegistryChange) {
        return DAOUtils.getOrgRegistryChange((Session) entityManager.unwrap(Session.class), idOfOrgRegistryChange);
    }

    public String getPersonNameByOrg(Org org) {
        Query query = entityManager.createNativeQuery(
                "SELECT (p.surname || ' ' || p.firstname || ' ' || p.secondname) AS fullname FROM cf_orgs cfo LEFT JOIN cf_persons p ON cfo.idofofficialperson = p.idofperson WHERE cfo.idoforg = :idOfOrg");
        query.setParameter("idOfOrg", org.getIdOfOrg());
        return (String) query.getSingleResult();
    }

    public List<ClientPayment> findClientPaymentsByPaymentId(Contragent contragent, String idOfPayment) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        return DAOUtils.findClientPaymentsForCorrectionOperation(session, contragent, idOfPayment);
    }

    public Boolean isCancelPaymentExists(String idOfPayment) throws Exception {
        Session session = entityManager.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ClientPayment.class);
        criteria.add(Restrictions.eq("idOfPayment", idOfPayment));
        criteria.addOrder(org.hibernate.criterion.Order.desc("createTime"));
        return !criteria.list().isEmpty();
    }

    public List getClientBalanceInfosWithMigrations(String where, String where2, Date begDate, Date endDate, String clientWhere, List<Long> idOfOrgList) {
        //Находим клиентов с историей перемещений между ОО или по группам
        String str_query = "select c.idofclient, h.idoforg from cf_clientmigrationhistory h join cf_clients c on h.idofclient = c.idofclient "
                + "join cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup AND c.idoforg = g.idoforg "
                + "where exists (select * from cf_clientmigrationhistory hhh where hhh.idofclient = h.idofclient and hhh.registrationdate between :begDate and :endDate) and "
                + "h.registrationdate = (select max(registrationdate) from cf_clientmigrationhistory hh where hh.idofclient = h.idofclient and hh.registrationdate < :begDate) "
                + "and h.idoforg in (" + where + ") " + where2
                + " union "
                + "select c.idofclient, h.idoforg from cf_clientgroup_migrationhistory h join cf_clients c on h.idofclient = c.idofclient "
                + "join cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup AND c.idoforg = g.idoforg "
                + "where exists (select * from cf_clientgroup_migrationhistory hhh where hhh.idofclient = h.idofclient and hhh.registrationdate between :begDate and :endDate) and "
                + "h.registrationdate = (select max(registrationdate) from cf_clientgroup_migrationhistory hh where hh.idofclient = h.idofclient and hh.registrationdate < :begDate) "
                + "and h.idoforg in (" + where + ") " + where2;
        Query q = entityManager.createNativeQuery(str_query);
        q.setParameter("begDate", begDate.getTime());
        q.setParameter("endDate", endDate.getTime());
        List list =  q.getResultList();
        Map<Long, Long> clients = new HashMap();
        for (Object obj : list) {
            //Определяем, находился ли клиент в одной из нужных организаций на дату отчета
            Object[] row = (Object[]) obj;
            Long idOfClient = HibernateUtils.getDbLong(row[0]);
            Long idOfOrg = HibernateUtils.getDbLong(row[1]);
            if (idOfOrgList.contains(idOfOrg)) clients.put(idOfClient, idOfOrg);
        }
        str_query = "SELECT :idOfClient, (select o.shortname from cf_orgs o where idoforg = :idOfOrg) as shortname, g.groupname as groupname, c.contractId, p.surname as surname, p.firstname, p.secondname, c.limits, c.balance, "
                + "coalesce((SELECT sum(t.transactionsum) FROM cf_transactions t WHERE t.idofclient = c.idofclient AND t.transactionDate >= :begDate AND t.transactionDate <= :endDate), 0), "
                + "(SELECT min(t.transactiondate) FROM cf_transactions t WHERE t.idofclient = c.idofclient AND t.transactionDate > :begDate), "
                + ":idOfOrg as idoforg "
                + "FROM cf_clients c INNER JOIN cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup AND c.idoforg = g.idoforg "
                + where2 + " JOIN cf_persons p ON c.idofperson = p.idofperson WHERE c.idofclient = :idOfClient";
        List result = new ArrayList();
        for (Map.Entry<Long, Long> entry : clients.entrySet()) {
            q = entityManager.createNativeQuery(str_query);
            q.setParameter("begDate", begDate.getTime());
            q.setParameter("endDate", endDate.getTime());
            q.setParameter("idOfClient", entry.getKey());
            q.setParameter("idOfOrg", entry.getValue());
            result.addAll(q.getResultList());
        }
        return result;
    }

    public List getClientBalanceInfosWithoutMigrations(String where, String where2, Date begDate, Date endDate, String clientWhere) {
        String str_query =
                "SELECT c.idofclient, o.shortname as shortname, g.groupname as groupname, c.contractId, p.surname as surname, p.firstname, p.secondname, c.limits, c.balance, "
                        + "coalesce((SELECT sum(t.transactionsum) FROM cf_transactions t WHERE t.idofclient = c.idofclient AND t.transactionDate >= :begDate AND t.transactionDate <= :endDate), 0), "
                        + "(SELECT min(t.transactiondate) FROM cf_transactions t WHERE t.idofclient = c.idofclient AND t.transactionDate > :begDate), "
                        + "o.idoforg as idoforg "
                        + "FROM cf_clients c INNER JOIN cf_orgs o ON c.idoforg = o.idoforg INNER JOIN cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup AND c.idoforg = g.idoforg "
                        + where2 + " JOIN cf_persons p ON c.idofperson = p.idofperson WHERE c.idoforg in(" + where
                        + ") " + clientWhere
                        + " and c.idofclient not in (select idofclient from cf_clientmigrationhistory where registrationdate between :begDate and :endDate and (idoforg in("
                        + where + ") or idofoldorg in (" + where + "))) "
                        + "and c.idofclient not in (select idofclient from cf_clientgroup_migrationhistory where registrationdate between :begDate and :endDate and idoforg in("
                        + where + "))";
        Query q = entityManager.createNativeQuery(str_query);
        q.setParameter("begDate", begDate.getTime());
        q.setParameter("endDate", endDate.getTime());
        return q.getResultList();
    }

    public List getClientBalanceInfos(String where, String where2, Date begDate, Date endDate, String clientWhere) {
        String str_query =
                "SELECT c.idofclient, o.shortname as shortname, g.groupname as groupname, c.contractId, p.surname as surname, p.firstname, p.secondname, c.limits, c.balance, "
                        + "coalesce((SELECT sum(t.transactionsum) FROM cf_transactions t WHERE t.idofclient = c.idofclient AND t.transactionDate >= :begDate AND t.transactionDate <= :endDate), 0), "
                        + "(SELECT min(t.transactiondate) FROM cf_transactions t WHERE t.idofclient = c.idofclient AND t.transactionDate > :begDate), "
                        + "o.idoforg as idoforg "
                        + "FROM cf_clients c INNER JOIN cf_orgs o ON c.idoforg = o.idoforg INNER JOIN cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup AND c.idoforg = g.idoforg "
                        + where2 + " JOIN cf_persons p ON c.idofperson = p.idofperson WHERE c.idoforg in(" + where
                        + ") " + clientWhere
                        + " and c.idofclient not in (select idofclient from cf_clientmigrationhistory where registrationdate between :begDate and :endDate and idoforg in("
                        + where + ")) " + "union "
                        + "SELECT c.idofclient, shortname as shortname, h.oldgroupname as groupname, c.contractId, p.surname as surname, p.firstname, p.secondname, c.limits, c.balance, "
                        + "coalesce((SELECT sum(t.transactionsum) FROM cf_transactions t WHERE t.idofclient = h.idofclient AND t.transactionDate >= :begDate AND t.transactionDate <= :endDate), 0), "
                        + "(SELECT min(t.transactiondate) FROM cf_transactions t "
                        + "WHERE t.idofclient = c.idofclient AND t.transactionDate > :begDate), "
                        + "h.idofoldorg as idoforg "
                        + "FROM cf_clients c INNER JOIN cf_clientmigrationhistory h ON c.idofclient = h.idofclient "
                        + "JOIN cf_persons p ON c.idofperson = p.idofperson JOIN cf_orgs o ON h.idofoldorg = o.idoforg "
                        + "WHERE h.idofoldorg in(" + where + ") " + clientWhere
                        + " and h.registrationdate between :begDate and :endDate "
                        + "ORDER BY shortname, groupname, surname";
        Query q = entityManager.createNativeQuery(str_query);
        q.setParameter("begDate", begDate.getTime());
        q.setParameter("endDate", endDate.getTime());
        return q.getResultList();
    }

    public String getMenuSourceOrgName(Long idOfOrg) {
        Session session = (Session) entityManager.unwrap(Session.class);
        Long id = DAOUtils.findMenuExchangeSourceOrg(session, idOfOrg);
        if (id != null) {
            Org org = (Org) session.load(Org.class, id);
            return org.getOfficialName();
        }
        return null;
    }

    public String getDefaultSupplierNameByOrg(Long idOfOrg) {
        Session session = entityManager.unwrap(Session.class);
        Contragent cc = DAOUtils.findDefaultSupplier(session, idOfOrg);
        if (cc != null) {
            return cc.getContragentName();
        } else {
            return null;
        }
    }

    public String getConfigurationProviderNameByOrg(Long idOfOrg) {
        Org o = entityManager.find(Org.class, idOfOrg);
        if(o == null){
            return null;
        }
        ConfigurationProvider prov = o.getConfigurationProvider();
        if (prov != null) {
            return prov.getName();
        }
        return null;
    }

    private String getOnlineOptionValue(int option) throws Exception {
        String str_query = "select optiontext from cf_options where idofoption = :idofoption";
        Query q = entityManager.createNativeQuery(str_query);
        q.setParameter("idofoption", option);
        List list = q.getResultList();
        if (list.size() == 0) {
            throw new Exception(String.format("Option id=%s not found", option));
        }
        return (String) list.get(0);
    }

    public Integer getWtDaysForbid() {
        try {
            return new Integer(getOnlineOptionValue(Option.OPTION_WT_DAYS_FORBID));
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public String getReviseLastDate() {
        try {
            return getOnlineOptionValue(Option.OPTION_REVISE_LAST_DATE);
        } catch (Exception e) {
            return "";
        }
    }

    @Transactional
    public String getLastCountBlocked() {
        try {
            return getOnlineOptionValue(Option.OPTION_LAST_COUNT_CARD_BLOCK);
        } catch (Exception e) {
            return "";
        }
    }

    @Transactional
    public String getDeletedLastedDateMenu() {
        try {
            return getOnlineOptionValue(Option.OPTION_LAST_DELATED_DATE_MENU);
        } catch (Exception e) {
            return "";
        }
    }

    @Transactional
    public String getLastProcessedWtComplex() {
        try {
            return getOnlineOptionValue(Option.OPTION_LAST_PROCESSED_WT_COMPLEX);
        } catch (Exception e) {
            return "";
        }
    }

    @Transactional
    public Boolean isSverkaEnabled() {
        try {
            String option = getOnlineOptionValue(Option.OPTION_SVERKA_ENABLED);
            return option.equals("1");
        } catch (Exception e) {
            logger.error("Can't get sverka permission value", e);
            return true;
        }
    }

    public SpecialDate getSpecialCalendarByDate(Date date) {
        Query query = entityManager.createQuery("select sd from SpecialDate sd where sd.date = :day");
        query.setParameter("day", date);
        try {
            return (SpecialDate) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<CardSign> findCardsignByManufactureCodeForNewTypeProvider(Integer manufactureCode) {
        Query query = entityManager.createQuery(
                "select cs from CardSign cs where cs.manufacturerCode = :manufactureCode "
                        + "and cs.newtypeprovider = true and (cs.deleted = false or cs.deleted is null)");
        query.setParameter("manufactureCode", manufactureCode);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<EMIAS> findEMIASbyClientandBeetwenDates(Client client, Date startDate, Date endDate) {
        Query query = entityManager.createQuery("select em from EMIAS em where em.guid = :guid "
                + "and em.dateLiberate between :begDate and :endDate and em.kafka<>true");
        query.setParameter("guid", client.getClientGUID());
        query.setParameter("begDate", startDate);
        query.setParameter("endDate", endDate);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    public ExternalEvent getExternalEvent(Client client, String orgCode, String orgName, ExternalEventType evtType,
            Date evtDateTime, ExternalEventStatus evtStatus) {
        Query query = entityManager.createQuery("select ee from ExternalEvent ee where ee.client = :client "
                + "and ee.evtType = :evtType and ee.evtDateTime = :evtDateTime and ee.evtStatus = :evtStatus "
                + "and ee.orgCode = :orgCode and ee.orgName = :orgName");
        query.setParameter("client", client);
        query.setParameter("evtType", evtType);
        query.setParameter("evtDateTime", evtDateTime);
        query.setParameter("evtStatus", evtStatus);
        query.setParameter("orgCode", orgCode);
        query.setParameter("orgName", orgName);
        try {
            return (ExternalEvent) query.getResultList().get(0);
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<WtComplexGroupItem> getWtComplexGroupList() {
        TypedQuery<WtComplexGroupItem> q = entityManager
                .createQuery("from WtComplexGroupItem order by idOfComplexGroupItem", WtComplexGroupItem.class);
        return q.getResultList();
    }

    public List<WtAgeGroupItem> getWtAgeGroupList() {
        TypedQuery<WtAgeGroupItem> q = entityManager
                .createQuery("from WtAgeGroupItem order by idOfAgeGroupItem", WtAgeGroupItem.class);
        return q.getResultList();
    }

    public List<WtDietType> getWtDietTypeList() {
        TypedQuery<WtDietType> q = entityManager
                .createQuery("from WtDietType order by idOfDietType", WtDietType.class);
        return q.getResultList();
    }

    public Long getWtComplexGroupIdByDescription(String description) {
        try {
            return (Long) entityManager.createQuery("select cg.idOfComplexGroupItem from WtComplexGroupItem cg"
                    + " where lower(cg.description) like '%" + description + "%'").getSingleResult();
        } catch (Exception e) {
            logger.error("Exception in getWtComplexGroupIdByDescription", e);
            return 0L;
        }
    }

    public Long getWtAgeGroupIdByDescription(String description) {
        try {
            return (Long) entityManager.createQuery("select ag.idOfAgeGroupItem from WtAgeGroupItem ag"
                    + " where lower(ag.description) like '%" + description + "%'").getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public List<CategoryDiscount> getCategoryDiscountListByWtRule(WtDiscountRule wtRule) {
        Query query = entityManager.createQuery("select cd from CategoryDiscount cd "
                + "inner join fetch cd.wtDiscountRules where cd.deletedState = false "
                + "and :wtRule in elements(cd.wtDiscountRules)");
        query.setParameter("wtRule", wtRule);
        return query.getResultList();
    }

    public List<WtComplex> getWtComplexesList() {
        TypedQuery<WtComplex> q = entityManager
                .createQuery("select wc from WtComplex wc left join fetch wc.wtComplexGroupItem complexItem "
                                + "left join fetch wc.wtAgeGroupItem ageItem "
                                + "left join fetch wc.wtDietType dietType "
                                + "left join fetch wc.contragent contragent "
                                + "where wc.deleteState = 0 order by wc.idOfComplex",
                        WtComplex.class);
        return q.getResultList();
    }

    public List<WtComplex> getWtComplexListByFilter(List<Long> wtComplexGroupIds, List<Long> wtAgeGroupIds, Long wtDietTypeId,
            List<Long> contragentIds, List<Long> orgIds, WtDiscountRule wtRule) {
        Query query = entityManager.createNativeQuery("select distinct wc.idofcomplex from cf_wt_complexes wc "
                + "left join cf_wt_org_group_relations wogr on wc.idoforggroup = wogr.idoforggroup "
                + "left join cf_wt_complexes_org wco on wco.idofcomplex = wc.idofcomplex "
                + "left join cf_wt_discountrules_complexes drc on drc.idofcomplex = wc.idofcomplex "
                + (wtRule == null ? "where wc.deleteState = 0" : "where drc.idofrule = :idOfRule")
                + (wtComplexGroupIds.size() == 0 ? "" : " and wc.idofcomplexgroupitem in (:wtComplexGroupIds)")
                + (wtAgeGroupIds.size() == 0 ? "" : " and wc.idofagegroupitem in (:wtAgeGroupIds)")
                + (wtDietTypeId == 0 ? "" : " and wc.idofdiettype = :wtDietTypeId")
                + (contragentIds.size() == 0 ? "" : " and wc.idofcontragent in (:contragentIds)")
                + (orgIds.size() == 0 ? "" : " and (wco.idoforg in (:orgIds) or wogr.idoforg in (:orgIds))"));
        if (wtComplexGroupIds.size() > 0) {
            query.setParameter("wtComplexGroupIds", wtComplexGroupIds);
        }
        if (wtAgeGroupIds.size() > 0) {
            query.setParameter("wtAgeGroupIds", wtAgeGroupIds);
        }
        if (wtDietTypeId > 0) {
            query.setParameter("wtDietTypeId", wtDietTypeId);
        }
        if (contragentIds.size() > 0) {
            query.setParameter("contragentIds", contragentIds);
        }
        if (orgIds.size() > 0) {
            query.setParameter("orgIds", orgIds);
        }
        if (wtRule != null) {
            query.setParameter("idOfRule", wtRule.getIdOfRule());
        }
        List list = query.getResultList();
        List<WtComplex> result = new ArrayList<>();
        for (Object obj : list) {
            Long idOfComplex = ((BigInteger) obj).longValue();
            result.add(getWtComplexById(idOfComplex));
        }
        return result;
    }

    public List<Contragent> contragentsListByUser(Long idOfUser) {
        Query q = entityManager.createQuery("select c from User u inner join u.contragents c"
                + " where u.idOfUser = :idOfUser order by c.contragentName");

        q.setParameter("idOfUser", idOfUser);
        return q.getResultList();
    }

    public Long getMeshIdByOrg(long idOfOrg) {
        Query query = entityManager.createNativeQuery("select organizationidfromnsi from cf_orgs where IdOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        Object obj = query.getSingleResult();
        return obj == null ? null : ((BigInteger)obj).longValue();
    }

    public WtComplex getWtComplexById(Long idOfComplex) {
        Query query = entityManager.createQuery("select wc from WtComplex wc "
                + "left join fetch wc.wtComplexGroupItem complexItem "
                + "left join fetch wc.wtAgeGroupItem ageItem "
                + "left join fetch wc.wtDietType dietType "
                + "left join fetch wc.contragent contragent "
                + "where wc.idOfComplex = :idOfComplex");
        query.setParameter("idOfComplex", idOfComplex);
        try {
            return (WtComplex) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List getWtComplexsByComplexes(List<PreorderComplex> preorderComplexs) {
        if (preorderComplexs == null || preorderComplexs.isEmpty())
            return new ArrayList();
        List <Long> preorderComplexIds = new ArrayList<>();
        for (PreorderComplex preorderComplex: preorderComplexs)
        {
            preorderComplexIds.add(preorderComplex.getIdOfPreorderComplex());
        }
        Query query = entityManager.createNativeQuery("select distinct cpc.idofpreordercomplex, cwdt.description from cf_preorder_complex cpc "
                + "LEFT JOIN cf_wt_complexes cwt on cwt.idofcomplex = cpc.armcomplexid "
                + "left join cf_wt_diet_type cwdt on cwt.idofdiettype = cwdt.idofdiettype "
                + "where cpc.idofpreordercomplex in (:preorderComplexIds) and cwdt.idofdiettype is not null");
        query.setParameter("preorderComplexIds", preorderComplexIds);
        return  query.getResultList();
    }

    public List getWtComplexsByRegular(List<RegularPreorder> regularPreorders) {
        if (regularPreorders == null || regularPreorders.isEmpty())
            return new ArrayList();
        List <Long> regularPreordersIds = new ArrayList<>();
        for (RegularPreorder regularPreorder: regularPreorders)
        {
            regularPreordersIds.add(regularPreorder.getIdOfRegularPreorder());
        }
        Query query = entityManager.createNativeQuery("select distinct crp.idofregularpreorder, cwdt.description "
                + "from cf_regular_preorders crp "
                + "LEFT JOIN cf_preorder_complex cpc on crp.idofregularpreorder = cpc.idofregularpreorder "
                + "LEFT JOIN cf_wt_complexes cwt on cwt.idofcomplex = cpc.armcomplexid "
                + "left join cf_wt_diet_type cwdt on cwt.idofdiettype = cwdt.idofdiettype "
                + "where crp.idofregularpreorder in (:regularPreordersIds) and cwdt.idofdiettype is not null");
        query.setParameter("regularPreordersIds", regularPreordersIds);
        return  query.getResultList();
    }

    public WtDish getWtDishById(Long idOfDish) {
        Query query = entityManager.createQuery("SELECT dish FROM WtDish dish "
                + "where dish.idOfDish = :idOfDish");
        query.setParameter("idOfDish", idOfDish);
        try {
            return (WtDish) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<WtDish> getWtDish() {
        Query q = entityManager.createQuery("SELECT dish FROM WtDish dish");
        return q.getResultList();
    }

    //Список союзов организаций, кужа входит данная организация
    public List<Long> getOrgGroupsbyOrgForWEBARM(Long idOforg) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery(" select idoforggroup from cf_wt_org_group_relations "
                + "  where idoforg = " + idOforg);
        List<BigInteger> list = (List<BigInteger>) q.list();
        List<Long> result = new ArrayList<>(list.size());
        for (BigInteger value: list)
        {
            result.add(value.longValue());
        }
        return result;
    }

    public List<Long> getComplexesByOrgForWEBARM(Long idOforg) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        org.hibernate.Query q = session.createSQLQuery(" select idofcomplex from cf_wt_complexes_org where idoforg = "
                + idOforg);
        List<BigInteger> list = (List<BigInteger>) q.list();
        List<Long> result = new ArrayList<>();
        for (BigInteger value: list)
        {
            result.add(value.longValue());
        }
        return result;
    }

    public List getComplexesByGroupForWEBARM(List<Long> idOfgroups) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        String groupString = "";
        for (Long groupid: idOfgroups)
        {
            groupString = groupString + "'" + groupid.toString() + "',";
        }
        groupString = groupString.substring(0,groupString.length()-1);
        org.hibernate.Query q = session.createSQLQuery("select idofcomplex, name, begindate, enddate, idofagegroupitem from cf_wt_complexes "
                + "where idofcomplexgroupitem in (1,3) and deletestate=0 and idoforggroup in (" + groupString + ")");
        return q.list();
    }

    public List getComplexesByComplexForWEBARM(List<Long> idOfComplexes) throws Exception {
        Session session = (Session) entityManager.getDelegate();
        String idOfComplexString = "";
        for (Long idOfComplex: idOfComplexes)
        {
            idOfComplexString = idOfComplexString + "'" + idOfComplex.toString() + "',";
        }
        idOfComplexString = idOfComplexString.substring(0,idOfComplexString.length()-1);
        org.hibernate.Query q = session.createSQLQuery("select idofcomplex, name, begindate, enddate, idofagegroupitem from cf_wt_complexes "
                + "where idofcomplexgroupitem in (1,3) and deletestate=0 and idofcomplex in (" + idOfComplexString + ")");
        return q.list();
    }

    public List<WtComplex> getComplexesByWtDiscountRule(WtDiscountRule discountRule) {
        return DAOUtils.getComplexesByWtDiscountRule(entityManager, discountRule);
    }

    public CodeMSP findCodeNSPByCode(Integer code) {
        if(code == null){
            return null;
        }
        Session session = (Session) entityManager.getDelegate();

        Criteria criteria = session.createCriteria(CodeMSP.class);
        criteria.add(Restrictions.eq("code", code));

        return (CodeMSP) criteria.uniqueResult();
    }

    public List<CategoryDiscount> getCategoryDiscountListNotDeletedTypeDiscount() {
        TypedQuery<CategoryDiscount> q = entityManager
                .createQuery("from CategoryDiscount where deletedState = false"
                                + " and categoryType = 0 "
                                + " and idOfCategoryDiscount >= 0"
                                + " order by categoryName",
                        CategoryDiscount.class);
        return q.getResultList();
    }

    public List<Client> getClientsBySoid(String ssoid) {
        return DAOUtils.getClientsBySsoid(entityManager, ssoid);
    }

    public List<WtGroupItem> getMapTypeFoods() {
        Query q = entityManager.createQuery("SELECT wtGroup from WtGroupItem wtGroup");
        return q.getResultList();
    }

    public List<WtComplexGroupItem> getTypeComplexFood() {
        Query q = entityManager.createQuery("SELECT wtComplex from WtComplexGroupItem wtComplex");
        return q.getResultList();
    }

    public List<WtDietType> getMapDiet() {
        Query q = entityManager.createQuery("SELECT wtDiet from WtDietType wtDiet");
        return q.getResultList();
    }

    public List<WtAgeGroupItem> getAgeGroups() {
        Query q = entityManager.createQuery("SELECT wtAge FROM WtAgeGroupItem wtAge");
        return q.getResultList();
    }

    public MeshClass getMeshClassByUID(String uid){
        try {
            Query q = entityManager.createQuery("SELECT c FROM MeshClass AS c WHERE c.uid = :uid");
            q.setParameter("uid", uid);
            return (MeshClass) q.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    public Long getMaxVersionOfFoodBoxPreorderAvailable(Org org){
        try {
            Query q = entityManager.createQuery("SELECT MAX(c.version) FROM FoodBoxPreorderAvailable AS c WHERE c.org = :org");
            q.setParameter("org", org);
            return (Long) q.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    public FoodBoxPreorderAvailable getFoodBoxPreorderAvailable(Org org, Long idOfDish){
        try {
            Query q = entityManager.createQuery("SELECT c FROM FoodBoxPreorderAvailable AS c WHERE c.org = :org and c.idOfDish= :idOfDish");
            q.setParameter("org", org);
            q.setParameter("idOfDish", idOfDish);
            return (FoodBoxPreorderAvailable) q.getSingleResult();
        } catch (NoResultException e){
            return null;
        }
    }

    public Long getMaxVersionOfFoodBoxPreorder(){
        try {
            Query q = entityManager.createQuery("SELECT MAX(c.version) FROM FoodBoxPreorder AS c");
            Long maxV = (Long) q.getSingleResult();
            if (maxV == null)
                maxV = 0L;
            return maxV;
        } catch (NoResultException e){
            return 0L;
        }
    }

    public Set<FoodBoxPreorder> getFoodBoxPreordersFromVersion(Long version, Org org) {
        try {
            Query query = entityManager.createQuery("SELECT fb from FoodBoxPreorder fb "
                    + "where fb.version > :version "
                    + "AND fb.org = :org");
            query.setParameter("version", version);
            query.setParameter("org", org);
            List<FoodBoxPreorder> foodBoxPreorders = query.getResultList();
            return new HashSet<>(foodBoxPreorders);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Integer getFoodBoxPreordersUnallocated(Org org) {
        try {
            Query query = entityManager.createQuery("SELECT fb from FoodBoxPreorder fb "
                    + "where fb.org = :org and (fb.located is null or fb.located = false)");
            query.setParameter("org", org);
            List<FoodBoxPreorder> foodBoxPreorders = query.getResultList();
            return foodBoxPreorders.size();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FoodBoxPreorder getFoodBoxPreorderByExternalId(String externalId) {
        try {
            Query query = entityManager.createQuery("SELECT fb from FoodBoxPreorder fb "
                    + "where fb.idFoodBoxExternal = :idFoodBoxExternal ");
            query.setParameter("idFoodBoxExternal", externalId);
            return (FoodBoxPreorder)query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FoodBoxPreorder> getActiveFoodBoxPreorderForClient(Client client) {
        try {
            Query query = entityManager.createQuery("SELECT fb from FoodBoxPreorder fb "
                    + "where fb.client = :client "
                    + "and fb.state between :new and :loaded");
            query.setParameter("client", client);
            query.setParameter("new", FoodBoxStateTypeEnum.NEW);
            query.setParameter("loaded", FoodBoxStateTypeEnum.LOADED);
            List<FoodBoxPreorder> foodBoxPreorders = query.getResultList();
            return foodBoxPreorders;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FoodBoxPreorder> getFoodBoxPreorderByForClient(Client client, Date from, Date to) {
        try {
            Query query = entityManager.createQuery("SELECT fb from FoodBoxPreorder fb "
                    + "where fb.client = :client "
                    + "and fb.createDate between :from and :to");
            query.setParameter("client", client);
            query.setParameter("from", from);
            query.setParameter("to", to);
            List<FoodBoxPreorder> foodBoxPreorders = query.getResultList();
            return foodBoxPreorders;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<FoodBoxPreorderDish> getFoodBoxPreordersDishes(FoodBoxPreorder foodBoxPreorder) {
        try {
            Query query = entityManager.createQuery("SELECT fbd from FoodBoxPreorderDish fbd "
                    + "where fbd.foodBoxPreorder = :foodBoxPreorder ");
            query.setParameter("foodBoxPreorder", foodBoxPreorder);
            List<FoodBoxPreorderDish> foodBoxPreorderDishes = query.getResultList();
            return new HashSet<>(foodBoxPreorderDishes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public FoodBoxPreorderNew getFoodBoxPreorders(Long version, Org org)
    {
        Set<FoodBoxPreorder> foodBoxPreorders = DAOReadonlyService.getInstance().getFoodBoxPreordersFromVersion(version, org);
        FoodBoxPreorderNew foodBoxPreorderNew = new FoodBoxPreorderNew();
        for (FoodBoxPreorder foodBoxPreorder: foodBoxPreorders)
        {
            if (!(foodBoxPreorder.getState().equals(FoodBoxStateTypeEnum.EXECUTED) || foodBoxPreorder.getState().equals(FoodBoxStateTypeEnum.CANCELED))) {
                FoodBoxPreorderNewItem foodBoxPreorderNewItem = new FoodBoxPreorderNewItem(foodBoxPreorder.getIdFoodBoxPreorder(), foodBoxPreorder.getState(), foodBoxPreorder.getClient().getIdOfClient(), foodBoxPreorder.getCreateDate(), foodBoxPreorder.getVersion());
                Set<FoodBoxPreorderDish> foodBoxPreorderDishes = DAOReadonlyService.getInstance().getFoodBoxPreordersDishes(foodBoxPreorder);
                for (FoodBoxPreorderDish foodBoxPreorderDish : foodBoxPreorderDishes) {
                    FoodBoxPreorderNewItemItem foodBoxPreorderNewItemItem = new FoodBoxPreorderNewItemItem();
                    foodBoxPreorderNewItemItem.setIdOfDish(foodBoxPreorderDish.getIdOfDish());
                    foodBoxPreorderNewItemItem.setPrice(foodBoxPreorderDish.getPrice());
                    foodBoxPreorderNewItemItem.setQty(foodBoxPreorderDish.getQty());
                    foodBoxPreorderNewItem.getItems().add(foodBoxPreorderNewItemItem);
                }
                foodBoxPreorderNew.getItems().add(foodBoxPreorderNewItem);
            }
        }
        return foodBoxPreorderNew;
    }

    public FoodBoxPreorder findFoodBoxPreorderById(long idOfFoodBoxPreorder) {
        return entityManager.find(FoodBoxPreorder.class, idOfFoodBoxPreorder);
    }

    public Set<FoodBoxCells> getFoodBoxCellsByOrg(Org org) {
        try {
            Query query = entityManager.createQuery("SELECT fbc from FoodBoxCells fbc "
                    + "where fbc.org = :org ");
            query.setParameter("org", org);
            List<FoodBoxCells> foodBoxCells = query.getResultList();
            return new HashSet<>(foodBoxCells);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public FoodBoxCells getFoodBoxCellsByOrgAndFoodBoxId(Org org, Long foodBoxId) {
        try {
            Query query = entityManager.createQuery("SELECT fbc from FoodBoxCells fbc "
                    + "where fbc.org = :org and fbc.fbId = :foodBoxId");
            query.setParameter("org", org);
            query.setParameter("foodBoxId", foodBoxId.intValue());
            FoodBoxCells foodBoxCells = (FoodBoxCells)query.getSingleResult();
            return foodBoxCells;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}