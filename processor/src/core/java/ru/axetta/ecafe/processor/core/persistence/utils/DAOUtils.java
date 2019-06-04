/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.logic.ProcessorUtils;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.payment.PaymentRequest;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.PreOrderFeedingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSetting;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingGroup;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.sync.SectionType;
import ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data.InteractiveReportDataItem;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwner;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.sync.response.OrgFilesItem;
import ru.axetta.ecafe.processor.core.utils.*;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PublicKey;
import java.text.DateFormat;
import java.util.*;

import static ru.axetta.ecafe.processor.core.logic.ClientManager.findGuardiansByClient;
import static ru.axetta.ecafe.processor.core.service.PreorderRequestsReportService.PREORDER_COMMENT;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.10.2010
 * Time: 18:28:59
 */
public class DAOUtils {

    private static final Logger logger = LoggerFactory.getLogger(DAOUtils.class);

    private DAOUtils() {}

    public static List<OrgOwner> getOrgSourceByMenuExchangeRule(Session session, Long idOfOrg, Boolean source) throws Exception{
        Criteria menuExchangeRuleCriteria = session.createCriteria(MenuExchangeRule.class);
        if(source){
            menuExchangeRuleCriteria.add(Restrictions.eq("idOfDestOrg", idOfOrg));
        } else {
            menuExchangeRuleCriteria.add(Restrictions.eq("idOfSourceOrg", idOfOrg));
        }
        List list = menuExchangeRuleCriteria.list();
        List<OrgOwner> orgOwnerList = new ArrayList<OrgOwner>(list.size());
        for (Object object: list){
            MenuExchangeRule menuExchangeRule = (MenuExchangeRule) object;
            Org org = null;
            if(source){
                org = (Org) session.get(Org.class, menuExchangeRule.getIdOfSourceOrg());
            } else {
                org = (Org) session.get(Org.class, menuExchangeRule.getIdOfDestOrg());
            }
            orgOwnerList.add(new OrgOwner(org.getIdOfOrg(), org.getShortName(), org.getOfficialName(), source));
        }
        return orgOwnerList;
    }

    public static Contragent findContragent(Session persistenceSession, long idOfContragent) throws Exception {
        return (Contragent) persistenceSession.get(Contragent.class, idOfContragent);
    }

    public static Contract findContract(Session persistenceSession, long idOfContract) throws Exception {
        return (Contract) persistenceSession.load(Contract.class, idOfContract);
    }

    public static Client findClient(Session persistenceSession, long idOfClient) throws Exception {
        return (Client) persistenceSession.get(Client.class, idOfClient);
    }

    @SuppressWarnings("unchecked")
    public static List<Client> findClients(Session session, List<Long> clientIds){
        if(clientIds.size() == 0) {
            return new ArrayList<Client>();
        }
        Criteria criteria = session.createCriteria(Client.class);
        criteria.add(Restrictions.in("idOfClient", clientIds));
        return criteria.list();
    }

    public static List findClientsBySan (Session persistenceSession, String san) {
        org.hibernate.Query query = persistenceSession.createSQLQuery("select CF_Clients.IdOfClient "
                                                                    + "from CF_Clients "
                                                                    + "where CF_Clients.san like :san");
        query.setParameter("san", san);
        List clientList = query.list();
        return clientList;
    }

    public static List findClientsByContract(Session persistenceSession, Long contractId) {
        org.hibernate.Query query = persistenceSession
                .createSQLQuery("select CF_Clients.IdOfClient from CF_Clients where CF_Clients.contractId=:contractId");
        query.setParameter("contractId", contractId);
        List clientList = query.list();
        return clientList;
    }

    public static Client getClientReference(Session persistenceSession, long idOfClient) throws Exception {
        return (Client) persistenceSession.load(Client.class, idOfClient);
    }

    @SuppressWarnings("unchecked")
    public static Client findClientByContractId(Session persistenceSession, long contractId) {
        Criteria contractCriteria = persistenceSession.createCriteria(Client.class);
        contractCriteria.add(Restrictions.eq("contractId", contractId));
        List<Client> resultList = (List<Client>) contractCriteria.list();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public static List<Client> findClientsByListOfContractId(Session persistenceSession, List<Long> contractIds) {
        Criteria contractCriteria = persistenceSession.createCriteria(Client.class);
        contractCriteria.add(Restrictions.in("contractId", contractIds));
        return (List<Client>) contractCriteria.list();
    }

    @SuppressWarnings("unchecked")
    public static Client findClientByMobile(Session session, String mobile) {
        Criteria mobileCriteria = session.createCriteria(Client.class);
        mobileCriteria.add(Restrictions.eq("mobile", mobile));
        mobileCriteria.add(Restrictions.ge("idOfClientGroup", 1100000000L));
        mobileCriteria.add(Restrictions.ne("idOfClientGroup", 1100000060L));
        mobileCriteria.add(Restrictions.ne("idOfClientGroup", 1100000070L));
        List<Client> resultList = (List<Client>) mobileCriteria.list();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @SuppressWarnings("unchecked")
    public static Client findClientByGuid(Session persistenceSession, String guid) {
        Criteria criteria = persistenceSession.createCriteria(Client.class);
        criteria.add(Restrictions.eq("clientGUID", guid));
        List<Client> resultList = (List<Client>) criteria.list();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @SuppressWarnings("unchecked")
    public static List<Client> findClientBySan(Session persistenceSession, String san) {
        Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
        clientCriteria.add(Restrictions.ilike("san", san, MatchMode.EXACT));
        return clientCriteria.list();
    }

    public static Client findClientByGuid(EntityManager em, String guid) {
        if(guid == null || guid.isEmpty()){
            return null;
        }
        javax.persistence.Query q = em.createQuery("from Client where clientGUID=:guid");
        q.setParameter("guid", guid);
        q.setMaxResults(1);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return ((Client)l.get(0));
    }

    @SuppressWarnings("unchecked")
    public static List<Client> findClientsByGuids(EntityManager em, List<String> guids) {
        if(guids.size() == 0){
            return new ArrayList<Client>();
        }
        javax.persistence.Query q = em.createQuery("from Client where clientGUID in :guids");
        q.setParameter("guids", guids);
        List<Client> result = (List<Client>) q.getResultList();
        return result != null ? result : new ArrayList<Client>();
    }

    public static Client getClientByMobilePhone(EntityManager em, String mobile) {
        /*javax.persistence.Query q = em.createQuery("from Client where mobile=:mobile");
        q.setParameter("mobile", mobile);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return ((Client)l.get(0));*/
        List l = getClientsListByMobilePhone(em, mobile);
        if (l.size()==0) return null;
        return ((Client)l.get(0));
    }

    public static List getClientsListByMobilePhone(EntityManager em, String mobile) {
        javax.persistence.Query q = em.createQuery("from Client where mobile=:mobile");
        q.setParameter("mobile", mobile);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return l;
    }

    public static Long getClientIdByGuid(EntityManager em, String guid) {
        return getClientIdByGuid(em, guid, false);
    }

    public static Long getClientIdByGuid(EntityManager em, String guid, boolean excludeLeaved) {
        String sql = "select idOfClient from Client where clientGUID=:guid";
        if(excludeLeaved) {
            sql += " and idOfClientGroup<:leavedGroup ";
        }
        javax.persistence.Query q = em.createQuery(sql);
        q.setParameter("guid", guid);
        if(excludeLeaved) {
            q.setParameter("leavedGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        }
        List l = q.getResultList();
        if (l.size()==0) return null;
        return ((Long)l.get(0));
    }

    /* TODO: Добавить в условие выборки исключение клиентов из групп Выбывшие и Удаленные (ECAFE-629) */
    @SuppressWarnings("unchecked")
    public static List<Client> findNewerClients(Session session, Collection<Org> orgs, long clientRegistryVersion) {
        //Query query = session.createQuery(
        //        "from Client cl where (cl.idOfClientGroup not in (:cg) or cl.idOfClientGroup is null) and cl.org in (:orgs) and clientRegistryVersion > :version")
        //        .setParameterList("cg", new Long[]{
        //                ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
        //                ClientGroup.Predefined.CLIENT_DELETED.getValue()})
        //        .setParameter("version", clientRegistryVersion).setParameterList("orgs", orgs);
        //return (List<Client>) query.list();
        Query query = session.createQuery(
                "from Client cl where cl.org in (:orgs) and clientRegistryVersion > :version")
                .setParameter("version", clientRegistryVersion).setParameterList("orgs", orgs);
        return (List<Client>) query.list();
    }

    public static boolean wasSuspendedLastSubscriptionFeedingByClient(Session session, long idOfClient){
        Query query = session.createQuery("from SubscriptionFeeding where idOfClient=:idOfClient and dateDeactivateService>=:currentDate order by dateDeactivateService desc");
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("currentDate", new Date());
        query.setMaxResults(1);
        SubscriptionFeeding subscriptionFeeding = (SubscriptionFeeding) query.uniqueResult();
        if(subscriptionFeeding==null) return false;
        return subscriptionFeeding.getWasSuspended();
    }

    public static String extractSanFromClient(Session session, long idOfClient){
        Criteria criteria = session.createCriteria(Client.class);
        criteria.add(Restrictions.eq("idOfClient", idOfClient));
        criteria.setProjection(Projections.property("san"));
        return (String) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public static List<Long> findActiveClientsId(Session session, List<Org> orgList) {
        //Query query = session.createQuery(
        //        "select cl.idOfClient from Client cl where cl.org in (:orgList) and (cl.idOfClientGroup not in (:cg) or cl.idOfClientGroup is null)")
        //        .setParameterList("orgList", orgList)
        //        .setParameterList("cg", Arrays.asList(ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
        //                ClientGroup.Predefined.CLIENT_DELETED.getValue()));
        //return (List<Long>) query.list();
        List<Long> group = Arrays.asList(ClientGroup.Predefined.CLIENT_LEAVING.getValue(),ClientGroup.Predefined.CLIENT_DELETED.getValue());
        Criteria activeClientCriteria = session.createCriteria(Client.class);
        activeClientCriteria.add(Restrictions.in("org", orgList));
        activeClientCriteria.add(
                Restrictions.or(
                        Restrictions.not(Restrictions.in("idOfClientGroup",group)),
                        Restrictions.isNull("idOfClientGroup"))
        );
        activeClientCriteria.setProjection(Property.forName("idOfClient"));
        return activeClientCriteria.list();
    }

    public static Client findClientByCardNo(EntityManager em, long cardNo) throws Exception {
        javax.persistence.Query q = em.createQuery("from Card where cardNo=:cardNo");
        q.setParameter("cardNo", cardNo);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return ((Card)l.get(0)).getClient();
    }

    public static Client findClientByContractId(EntityManager em, long cardNo) {
        return findClientByContractId(em.unwrap(Session.class), cardNo);
    }

    public static List<Client> findClientsBySan(EntityManager em, String san) {
        return findClientBySan(em.unwrap(Session.class), san);
    }

    public static List<GoodBasicBasketPrice> findGoodBasicBasketPrice(Session persistenceSession, GoodsBasicBasket basicBasket, Long idOfOrg) {
        Criteria criteria = persistenceSession.createCriteria(GoodBasicBasketPrice.class);
        criteria.add(Restrictions.eq("goodsBasicBasket", basicBasket));
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        criteria.add(Restrictions.ne("deletedState", true));
        criteria.addOrder(org.hibernate.criterion.Order.desc("globalVersion"));
        criteria.setMaxResults(1); //возвращаем максимальную по номеру версии запись, если записей несколько
        return criteria.list();
    }

    public static GoodsBasicBasket findBasicGood(Session persistenceSession, String guidOfBasicGood) {
        Criteria criteria = persistenceSession.createCriteria(GoodsBasicBasket.class);
        criteria.add(Restrictions.eq("guid",guidOfBasicGood));
        return (GoodsBasicBasket) criteria.uniqueResult();
    }

    public static GoodBBMenuPrice findBBMenuPrice(Session persistenceSession, Long idOfBasicGood,
            Long idOfConfigurationProvider, Date menuDate, String menuDetailName) {
        Criteria criteria = persistenceSession.createCriteria(GoodBBMenuPrice.class);
        criteria.add(Restrictions.eq("idOfBasicGood", idOfBasicGood));
        criteria.add(Restrictions.eq("idOfConfigurationProvider", idOfConfigurationProvider));
        criteria.add(Restrictions.eq("menuDate", menuDate));
        criteria.add(Restrictions.eq("menuDetailName", menuDetailName));
        return (GoodBBMenuPrice) criteria.uniqueResult();
    }

    public static Long getOrgConfigurationProvider(Session session, Long idOfOrg) {
        Query query = session.createSQLQuery("select coalesce(IdOfConfigurationProvider, 0) as id from cf_orgs where IdOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        return ((BigInteger)query.uniqueResult()).longValue();
    }

    public static List<String> getRegions(Session session) {
        Query q = session.createSQLQuery("select distinct district from cf_orgs where trim(both ' ' from district)<>''");
        return (List<String>) q.list();
    }

    public static Org findOrg(Session persistenceSession, long idOfOrg) throws Exception {
        return (Org) persistenceSession.get(Org.class, idOfOrg);
    }

    public static Org findOrgWithOfficialPerson(Session persistenceSession, long idOfOrg) throws Exception {
        Query query = persistenceSession.createQuery("select o from Org o left join fetch o.officialPerson where o.idOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        return (Org)query.uniqueResult();
    }

    /*public static Org findOrgWithPessimisticLock(Session persistenceSession, long idOfOrg) throws Exception {
        Query query = persistenceSession.createQuery(
                "from Org o where o.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        query.setLockMode("o", LockMode.PESSIMISTIC_WRITE);
        List res = query.list();
        if(res != null && res.size() > 0) {
            return (Org) res.get(0);
        }
        return null;
    }*/

    public static Org findOrgByShortname(Session session, String shortname) {
        Query query = session.createQuery(
                "from Org o where o.shortName=:shortName");
        query.setParameter("shortName", shortname);
        List res = query.list();
        if(res != null && res.size() > 0) {
            return (Org) res.get(0);
        }
        return null;
    }

    public static Org findOrgByOrgNumber(Session session, String orgNumber) {
        String sql = "from Org o where o.shortName like '%" + orgNumber + "%'";
        Query query = session.createQuery(sql);
        //query.setParameter("shortName", orgNumber);
        List res = query.list();
        if(res != null && res.size() > 0) {
            return (Org) res.get(0);
        }
        return null;
    }

    public static boolean isNotPlannedOrgExists(Session session, String shortName, long additionalIdBuilding) {
        Query q = session.createSQLQuery("select 1 from cf_not_planned_orgs where shortName=:shortName and additionalIdBuilding=:additionalIdBuilding");
        q.setParameter("shortName", shortName);
        q.setParameter("additionalIdBuilding", additionalIdBuilding);
        List res = q.list();
        if(res != null && res.size() > 0) {
            return true;
        }
        return false;
    }

    /*
    * Обновляет орг. Ставит признак mainbuilding = 0
    * */
    public static int orgMainBuildingUnset(Session session, long idOfOrg)  {
        Query q = session.createSQLQuery("update cf_orgs set MainBuilding = 0 where idOfOrg = :idOfOrg").setParameter("idOfOrg",idOfOrg);
        return q.executeUpdate();
    }

    public static Order findOrder(Session persistenceSession, Long idOfOrg, Long idOfOrder) throws Exception {
        return findOrder(persistenceSession, new CompositeIdOfOrder(idOfOrg, idOfOrder));
    }

    public static Order findOrder(Session persistenceSession, CompositeIdOfOrder compositeIdOfOrder) throws Exception {
        return (Order) persistenceSession.get(Order.class, compositeIdOfOrder);
    }

    public static ZeroTransaction findZeroTransaction(Session persistenceSession, CompositeIdOfZeroTransaction compositeIdOfZeroTransaction) throws Exception {
        return (ZeroTransaction) persistenceSession.get(ZeroTransaction.class, compositeIdOfZeroTransaction);
    }

    public static SpecialDate findSpecialDate(Session persistenceSession, CompositeIdOfSpecialDate compositeIdOfSpecialDate) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(SpecialDate.class);
        criteria.add(Restrictions.eq("idOfOrg", compositeIdOfSpecialDate.getIdOfOrg()));
        criteria.add(Restrictions.eq("date", compositeIdOfSpecialDate.getDate()));
        criteria.add(Restrictions.isNull("idOfClientGroup"));
        return (SpecialDate)criteria.uniqueResult();
    }

    public static SpecialDate findSpecialDateWithGroup(Session persistenceSession,
            CompositeIdOfSpecialDate compositeIdOfSpecialDate, Long idOfClientGroup) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(SpecialDate.class);
        criteria.add(Restrictions.eq("idOfOrg", compositeIdOfSpecialDate.getIdOfOrg()));
        criteria.add(Restrictions.eq("date", compositeIdOfSpecialDate.getDate()));
        criteria.add(Restrictions.eq("idOfClientGroup", idOfClientGroup));
        return (SpecialDate)criteria.uniqueResult();
    }

    public static LastProcessSectionsDates findLastProcessSectionsDate(Session persistenceSession,
            CompositeIdOfLastProcessSectionsDates compositeIdOfLastProcessSectionsDates) throws Exception {
        return (LastProcessSectionsDates) persistenceSession.get(LastProcessSectionsDates.class, compositeIdOfLastProcessSectionsDates);
    }

    public static OrderDetail findOrderDetail(Session persistenceSession,
            CompositeIdOfOrderDetail compositeIdOfOrderDetail) throws Exception {
        return (OrderDetail) persistenceSession.get(OrderDetail.class, compositeIdOfOrderDetail);
    }

    public static Org getOrgReference(Session persistenceSession, long idOfOrg) throws Exception {
        return (Org) persistenceSession.load(Org.class, idOfOrg);
    }

    public static Long getIdOfOrg(Session persistenceSession, long idOfOrg) throws Exception {
        Query query = persistenceSession.createQuery("select idOfOrg from Org where idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        return (Long) query.uniqueResult();
    }

    public static ClientPaymentOrder getClientPaymentOrderReference(Session persistenceSession,
            Long idOfClientPaymentOrder) throws Exception {
        return (ClientPaymentOrder) persistenceSession.load(ClientPaymentOrder.class, idOfClientPaymentOrder);
    }

    public static Card getCardReference(Session persistenceSession, long idOfCard) throws Exception {
        return (Card) persistenceSession.get(Card.class, idOfCard);
    }

    public static Card findCardByCardNo(Session persistenceSession, long cardNo) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Card.class);
        criteria.add(Restrictions.eq("cardNo", cardNo));
        criteria.addOrder(org.hibernate.criterion.Order.desc("updateTime"));
        criteria.setMaxResults(1);
        return (Card) criteria.uniqueResult();
    }

    public static NewCard findNewCardByCardNo(Session persistenceSession, long cardNo) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(NewCard.class);
        criteria.add(Restrictions.eq("cardNo", cardNo));
        return (NewCard) criteria.uniqueResult();
    }

    public static CardTemp findCardTempByCardNo(Session persistenceSession, long cardNo) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(CardTemp.class);
        criteria.add(Restrictions.eq("cardNo", cardNo));
        return (CardTemp) criteria.uniqueResult();
    }

    public static User findUser(Session persistenceSession, long idOfUser) throws Exception {
        return (User) persistenceSession.get(User.class, idOfUser);
    }

    public static User findUser(Session persistenceSession, String userName) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(User.class);
        criteria.add(Restrictions.eq("userName", userName));
        return (User)criteria.uniqueResult();
    }

    public static User getUserReference(Session persistenceSession, long idOfUser) throws Exception {
        return (User) persistenceSession.load(User.class, idOfUser);
    }

    public static ContragentClientAccount findContragentClientAccount(Session persistenceSession,
            CompositeIdOfContragentClientAccount compositeIdOfContragentClientAccount) throws Exception {
        return (ContragentClientAccount) persistenceSession
                .get(ContragentClientAccount.class, compositeIdOfContragentClientAccount);
    }

    /*public static List findClientPayments(Session persistenceSession, Contragent contragent, String idOfPayment)
        throws Exception {
        Criteria criteria = persistenceSession.createCriteria(ClientPayment.class);
        criteria.add(Restrictions.eq("contragent", contragent));
        criteria.add(Restrictions.eq("idOfPayment", idOfPayment));
        return criteria.list();
    }*/

    public static List findClientPayments(Session persistenceSession, Contragent contragent, PaymentRequest.PaymentRegistry.Payment payment)
            throws Exception {
        Criteria criteria = persistenceSession.createCriteria(ClientPayment.class);
        if (payment.getAddIdOfPayment() == null || !payment.getAddIdOfPayment().startsWith(RNIPLoadPaymentsService.SERVICE_NAME)) {
            criteria.add(Restrictions.eq("contragent", contragent));
        }
        criteria.add(Restrictions.eq("idOfPayment", payment.getIdOfPayment()));
        criteria.addOrder(org.hibernate.criterion.Order.desc("createTime"));
        return criteria.list();
    }

    public static List findClientPaymentsForCorrectionOperation(Session persistenceSession, Contragent contragent, String idOfPayment)
            throws Exception {
        Criteria criteria = persistenceSession.createCriteria(ClientPayment.class);
        criteria.add(Restrictions.like("idOfPayment", idOfPayment, MatchMode.START)); //включаем в поиск не только совпадения idOfPayment, но и корректировки
        criteria.add(Restrictions.not(Restrictions.like("idOfPayment", idOfPayment + ClientPayment.CANCEL_SUBSTRING, MatchMode.START))); //но не включаем отмены
        //criteria.add(Restrictions.like("addIdOfPayment", RNIPLoadPaymentsService.SERVICE_NAME, MatchMode.START)); - убираем сравнение строки по началу на "РНИП..."
        criteria.addOrder(org.hibernate.criterion.Order.desc("createTime"));
        return criteria.list();
    }

    public static boolean existClientPayment(Session persistenceSession, Contragent contragent, PaymentRequest.PaymentRegistry.Payment payment)
            throws Exception {
        return !findClientPayments(persistenceSession, contragent, payment).isEmpty();
    }

    public static ClientGroup findClientGroup(Session persistenceSession,
            CompositeIdOfClientGroup compositeIdOfClientGroup) throws Exception {
        return (ClientGroup) persistenceSession.get(ClientGroup.class, compositeIdOfClientGroup);
    }

    /**
     * производит выборку Группы клиента по номеру организации и имени группы
     * игнорируя регистр имени группы
     * @since  2012-03-06
     * @param persistenceSession ссылка на сессию
     * @param idOfOrg идентификатор организации
     * @param groupName имя группы
     * @return null если таблица пуста, сущность ClientGroup
     */
    public static ClientGroup findClientGroupByGroupNameAndIdOfOrg(Session persistenceSession,Long idOfOrg, String groupName) throws Exception{
        Criteria clientGroupCriteria = persistenceSession.createCriteria(ClientGroup.class);
        List l = clientGroupCriteria.add(
                Restrictions.and(
                        Restrictions.eq("groupName", groupName).ignoreCase(),
                        Restrictions.eq("org.idOfOrg",idOfOrg)
                )
        ).list();
        if (l.size()>0) return (ClientGroup)l.get(0);
        return null;
    }

    public static ClientGroup findClientGroupByGroupNameAndIdOfOrgNotIgnoreCase(Session persistenceSession,Long idOfOrg, String groupName) throws Exception{
        Criteria clientGroupCriteria = persistenceSession.createCriteria(ClientGroup.class);
        List l = clientGroupCriteria.add(
                Restrictions.and(
                        Restrictions.eq("groupName", groupName),
                        Restrictions.eq("org.idOfOrg",idOfOrg)
                )
        ).list();
        if (l.size()>0) return (ClientGroup)l.get(0);
        return null;
    }

    /**
     * производит выборку Группы клиента по номеру организации и имени группы
     * игнорируя регистр имени группы
     * @since  2012-03-06
     * @param persistenceSession ссылка на сессию
     * @param idOfOrg идентификатор организации
     * @param idOfClientGroup имя группы
     * @return null если таблица пуста, сущность ClientGroup
     */
    public static ClientGroup findClientGroupByIdOfClientGroupAndIdOfOrg(Session persistenceSession,Long idOfOrg, Long idOfClientGroup) throws Exception{
        Criteria clientGroupCriteria = persistenceSession.createCriteria(ClientGroup.class);
        List l = clientGroupCriteria.add(
                Restrictions.and(
                        Restrictions.eq("compositeIdOfClientGroup.idOfClientGroup", idOfClientGroup),
                        Restrictions.eq("org.idOfOrg",idOfOrg)
                )
        ).list();
        if (l.size()>0) return (ClientGroup)l.get(0);
        return null;
    }

    public static SyncHistory getSyncHistoryReference(Session persistenceSession, long idOfSync) throws Exception {
        return (SyncHistory) persistenceSession.load(SyncHistory.class, idOfSync);
    }

    public static Menu findMenu(Session persistenceSession, Org organization, int menuSource, Date menuDate)
            throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Menu.class);
        criteria.add(Restrictions.eq("org", organization));
        criteria.add(Restrictions.eq("menuSource", menuSource));
        criteria.add(Restrictions.eq("menuDate", menuDate));
        //return (Menu) criteria.uniqueResult();
        List list = criteria.list();
        if(list==null || list.isEmpty()){
            return null;
        } else {
            Menu returnMenu = null;
            Long max = -1L;
            for (Object obj: list){
                Menu menu = (Menu) obj;
                if(menu.getIdOfMenu()>max) returnMenu = menu;
            }
            return returnMenu;
        }

    }

    public static MenuDetail findMenuDetail(Session persistenceSession, Menu menu, String menuDetailName)
            throws Exception {
        Criteria criteria = persistenceSession.createCriteria(MenuDetail.class);
        criteria.add(Restrictions.eq("menu", menu));
        criteria.add(Restrictions.eq("menuDetailName", menuDetailName));
        return (MenuDetail) criteria.uniqueResult();
    }


    public static List findMenusBetweenDates(Session persistenceSession, Org organization, int menuSource,
            Date startDate, Date endDate) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Menu.class);
        criteria.add(Restrictions.eq("org", organization));
        criteria.add(Restrictions.eq("menuSource", menuSource));
        criteria.add(Restrictions.ge("menuDate", startDate));
        criteria.add(Restrictions.lt("menuDate", endDate));
        return criteria.list();
    }

    public static DiaryClass findDiaryClass(Session persistenceSession, CompositeIdOfDiaryClass compositeIdOfDiaryClass)
            throws Exception {
        return (DiaryClass) persistenceSession.get(DiaryClass.class, compositeIdOfDiaryClass);
    }

    public static DiaryTimesheet findDiaryTimesheet(Session persistenceSession,
            CompositeIdOfDiaryTimesheet compositeIdOfDiaryTimesheet) throws Exception {
        return (DiaryTimesheet) persistenceSession.get(DiaryTimesheet.class, compositeIdOfDiaryTimesheet);
    }

    public static DiaryValue findDiaryValue(Session persistenceSession, CompositeIdOfDiaryValue compositeIdOfDiaryValue)
            throws Exception {
        return (DiaryValue) persistenceSession.get(DiaryValue.class, compositeIdOfDiaryValue);
    }


    public static List <Client> findClientsForOrgAndFriendly (EntityManager em, Org parentOrg, List <Org> friendlyOrgs) throws Exception {
        String orgsClause = " where client.org = :org0";
        for (int i=0; i<friendlyOrgs.size(); i++) {
            if (orgsClause.length() > 0) {
                orgsClause += " or ";
            }
            orgsClause += "client.org = :org" + (i + 1);
        }

        javax.persistence.Query query = em.createQuery(
                "from Client client " + orgsClause);
        query.setParameter("org0", parentOrg);
        for (int i=0; i<friendlyOrgs.size(); i++) {
            query.setParameter("org" + (i + 1), friendlyOrgs.get(i));
        }
        if (query.getResultList().isEmpty()) return Collections.emptyList();
        List <Client> cls = (List <Client>)query.getResultList();
        return cls;
    }


    public static List <Client> findClientsForOrgAndFriendly (EntityManager em, Org organization) throws Exception {
        List <Org> orgs = findFriendlyOrgs (em, organization);
        return findClientsForOrgAndFriendly (em, organization, orgs);
    }

    public static List <Client> findClientsWithoutPredefinedForOrgAndFriendly (EntityManager em, Org organization) throws Exception {
        List <Org> orgs = findFriendlyOrgs (em, organization);
        //return findClientsForOrgAndFriendly (em, organization, orgs);

        String orgsClause = " where (client.org = :org0 ";
        for (int i=0; i < orgs.size(); i++) {
            if (orgsClause.length() > 0) {
                orgsClause += " or ";
            }
            orgsClause += "client.org = :org" + (i + 1);
        }
        orgsClause += ") " + " and (not (client.idOfClientGroup >= " +
                ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and client.idOfClientGroup < " +
                ClientGroup.Predefined.CLIENT_LEAVING.getValue() + ") or client.idOfClientGroup is null)";

        javax.persistence.Query query = em.createQuery(
                "from Client client " + orgsClause);
        query.setParameter("org0", organization);
        for (int i=0; i < orgs.size(); i++) {
            query.setParameter("org" + (i + 1), orgs.get(i));
        }
        if (query.getResultList().isEmpty()) return Collections.emptyList();
        List <Client> cls = (List <Client>)query.getResultList();
        return cls;
    }

    public static List <Client> findClientsWithoutPredefinedForOrg(EntityManager em, Org organization) throws Exception {
        String orgsClause = " where (client.org = :org ";
        orgsClause += ") " + " and not (client.idOfClientGroup >= " +
                ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and client.idOfClientGroup < " +
                ClientGroup.Predefined.CLIENT_LEAVING.getValue() + ")";

        javax.persistence.Query query = em.createQuery(
                "from Client client " + orgsClause);
        query.setParameter("org", organization);
        if (query.getResultList().isEmpty()) return Collections.emptyList();
        List <Client> cls = (List <Client>)query.getResultList();
        return cls;
    }

    public static List<Long> findFriendlyOrgIds(Session session, Long orgId) {
        Query query = session
                .createSQLQuery("select friendlyorg from cf_friendly_organization where currentorg=:idOfOrg")
                .setParameter("idOfOrg", orgId);
        List<Long> result = new ArrayList<Long>();
        for (Object o: query.list()) {
            result.add(((BigInteger)o).longValue());
        }
        return result;
    }
    public static List<Org> findFriendlyOrgs(EntityManager em, Org organization) throws Exception {
        List<Long> orgIds = findFriendlyOrgIds((Session) em.getDelegate(), organization.getIdOfOrg());
        List<Org> res = new ArrayList<Org>();
        for (Long idoforg : orgIds) {
            if (idoforg.equals(organization)) {
                continue;
            }
            res.add(DAOService.getInstance().getOrg(idoforg));
        }
        return res;
    }
    //находит только корпуса, за исключением текущего
    public static List<Org> findFriendlyOrgs(Session session, long organization) throws Exception {
        List<Long> orgIds = findFriendlyOrgIds(session, organization);
        List<Org> res = new ArrayList<Org>();
        for (Long idoforg : orgIds) {
            if (idoforg.equals(organization)) {
                continue;
            }
            res.add(DAOService.getInstance().getOrg(idoforg));
        }
        return res;
    }
    //Находит все включая текущую.
    public static List<Org> findAllFriendlyOrgs(Session session, long organization) throws Exception {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.in("idOfOrg", findFriendlyOrgIds(session, organization)));
        List<Org> result = criteria.list();
        return result !=null ? result : new ArrayList<Org>();

    }

    public static List<Org> getOrgsByStatusSinceVersion(Session session, Integer state, long version) throws Exception {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("state", state));
        criteria.add(Restrictions.ne("type", OrganizationType.SUPPLIER));
        criteria.add(Restrictions.gt("orgStructureVersion", version));
        return criteria.list();
    }

    public static List<Org> getOrgsSinceVersion(Session session, long version) throws Exception {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.ne("type", OrganizationType.SUPPLIER));
        criteria.add(Restrictions.gt("orgStructureVersion", version));
        return criteria.list();
    }

    public static List<InfoMessage> getInfoMessagesSinceVersion(Session session, long idOfOrg, long version) throws Exception {
        Query query = session.createQuery("select m from InfoMessage m join m.infoMessageDetails d "
                + "where d.compositeIdOfInfoMessageDetail.idOfOrg = :idOfOrg and m.version > :version");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("version", version);
        return query.list();
    }

    public static void setSendDateInfoMessage(Session session, Long idOfInfoMessage, Long idOfOrg) {
        Query query = session.createQuery("update InfoMessageDetail set sendDate = :date "
                + "where compositeIdOfInfoMessageDetail.idOfOrg = :idOfOrg and compositeIdOfInfoMessageDetail.idOfInfoMessage = :idOfInfoMessage");
        query.setParameter("date", new Date());
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("idOfInfoMessage", idOfInfoMessage);
        query.executeUpdate();
    }

    public static Boolean allOrgRegistryChangeItemsApplied(Session session, Long idOfOrgRegistryChange) {
        Query q = session.createQuery("from OrgRegistryChangeItem where mainRegistry=:idOfOrgRegistryChange and applied=false");
        q.setParameter("idOfOrgRegistryChange", idOfOrgRegistryChange);
        List res = q.list();
        if (res != null && res.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    public static OrgRegistryChange getOrgRegistryChange(Session session, long idOfOrgRegistryChange) {
        Query q = session.createQuery("from OrgRegistryChange where idOfOrgRegistryChange=:idOfOrgRegistryChange");
        q.setParameter("idOfOrgRegistryChange", idOfOrgRegistryChange);
        List res = q.list();
        if(res == null || res.size() < 1) {
            return null;
        }
        return (OrgRegistryChange) res.get(0);
    }

    public static OrgRegistryChangeItem getOrgRegistryChangeItem(Session session, long idOfOrgRegistryChangeItem) {
        Query q = session.createQuery("from OrgRegistryChangeItem where idOfOrgRegistryChangeItem=:idOfOrgRegistryChangeItem");
        q.setParameter("idOfOrgRegistryChangeItem", idOfOrgRegistryChangeItem);
        List res = q.list();
        if(res == null || res.size() < 1) {
            return null;
        }
        return (OrgRegistryChangeItem) res.get(0);
    }


    public static List findClients(Session persistenceSession, Org organization, String firstName, String surname,
            String secondName) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Client.class);
        criteria.add(Restrictions.eq("org", organization));
        criteria.createCriteria("person");
        criteria.add(Restrictions.eq("firstName", firstName).ignoreCase());
        criteria.add(Restrictions.eq("surname", surname).ignoreCase());
        criteria.add(Restrictions.eq("secondName", secondName).ignoreCase());
        return criteria.list();
    }

    public static List<Client> findClientsByFIO(Session persistenceSession, Set<Org> orgs, String firstName, String surname,
            String secondName, String mobile) throws Exception {
        String secondNameCondition = StringUtils.isEmpty(secondName) ? "" :" and (upper(client.person.secondName) = :secondname) ";
        //String createdFromCondition = createdFrom == null ? "" : " and (client.createdFrom = :createdfrom) ";
        Query query = persistenceSession.createQuery(
                "select client from Client client where client.org in (:org) and (upper(client.person.surname) = :surname) and"
                        + "(upper(client.person.firstName) = :firstname) " + secondNameCondition + " and mobile = :mobile order by client.contractTime desc");
        query.setParameterList("org", orgs);
        query.setParameter("surname", StringUtils.upperCase(surname));
        query.setParameter("firstname", StringUtils.upperCase(firstName));
        if (!secondNameCondition.equals(""))
            query.setParameter("secondname", StringUtils.upperCase(secondName));
        query.setParameter("mobile", mobile);
        return query.list();
    }

    /* Возвращаем ClientGuardian без учета признаков удаления и неактивности связки */
    public static ClientGuardian findClientGuardian(Session session, long idOfChildren, long idOfGuardian) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", idOfChildren));
        criteria.add(Restrictions.eq("idOfGuardian", idOfGuardian));
        return (ClientGuardian)criteria.uniqueResult();
    }

    public static boolean existClient(Session persistenceSession, Org organization, String firstName, String secondName, String surname)
            throws Exception {
        String secondNameCondition = StringUtils.isEmpty(secondName) ? "" : " and (upper(client.person.secondName) = ?) ";
        Query query = persistenceSession.createQuery(
                "select 1 from Client client where (client.org = ?) and (upper(client.person.surname) = ?) and"
                        + "(upper(client.person.firstName) = ?)" + secondNameCondition);
        query.setParameter(0, organization);
        query.setParameter(1, StringUtils.upperCase(surname));
        query.setParameter(2, StringUtils.upperCase(firstName));
        if (secondName != null) {
            query.setParameter(3, StringUtils.upperCase(secondName));
        }
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    @SuppressWarnings("unchecked")
    public static List<Object[]> findClientByFullName(EntityManager em, Org organization, String surname, String firstName, String secondName, boolean dismissPredefinedGroups)
            throws Exception {


        String predefinedGroups = "";
        if (dismissPredefinedGroups) {
            //  Проверяем не попадает ли в CLIENT_EMPLOYEES, а так же в ВЫБЫВШИЕ за предыдущие года
            /*predefinedGroups = " (cf_clients.idofclientgroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() +
                    " or (cf_clients.idofclientgroup=" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() +
                    " ) ) and ";*/
            predefinedGroups = " (cf_clients.idofclientgroup<=" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() +
                    " ) and ";
            // and EXTRACT(year from date (to_timestamp(cf_clients.lastupdate / 1000))) = EXTRACT(year from date (current_timestamp))
            //predefinedGroups = " cf_clients.idofclientgroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and ";
        }


        javax.persistence.Query q = em.createNativeQuery("select cf_clients.idofclient, cf_persons.Surname||' '||cf_persons.FirstName||' '||cf_persons.SecondName "+
                "from cf_clients "+
                "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "+
                "where trim(upper(cf_persons.Surname))=:surname and trim(upper(cf_persons.FirstName))=:firstName and trim(upper(cf_persons.SecondName))=:secondName and "+
                predefinedGroups +
                "(cf_clients.idoforg in (select cf_friendly_organization.friendlyorg from cf_friendly_organization where cf_friendly_organization.currentorg=:org))");
        q.setParameter("org", organization.getIdOfOrg());
        q.setParameter("surname", StringUtils.upperCase(surname).trim());
        q.setParameter("firstName", StringUtils.upperCase(firstName).trim());
        q.setParameter("secondName", StringUtils.upperCase(secondName).trim());
        q.setMaxResults(2);
        List res = q.getResultList();
        if (res.isEmpty()) return null;
        return (List<Object[]>)res;
        //if (res.size()==2) return -1L;
        //return ((Number)res.get(0)).longValue();


        /*javax.persistence.Query query = entityManager.createQuery(
               "select idOfClient from Client client where (client.org = :org) and "
               + "(trim(upper(client.person.surname)) = :surname) and "
               + "(trim(upper(client.person.firstName)) = :firstName) and (trim(upper(client.person.secondName)) = :secondName)");
       query.setParameter("org", organization);
       query.setParameter("surname", StringUtils.upperCase(surname).trim());
       query.setParameter("firstName", StringUtils.upperCase(firstName).trim());
       query.setParameter("secondName", StringUtils.upperCase(secondName).trim());
       query.setMaxResults(2);
       if (query.getResultList().isEmpty()) return findClientByFullNameInFriendlyOrgs (entityManager, organization, surname, firstName, secondName);
       if (query.getResultList().size()==2) return -1L;
       return (Long)query.getResultList().get(0);*/
    }

    @SuppressWarnings("unchecked")
    public static List<Object[]> findClientByFullNameFuzzy(EntityManager em, Org organization, String surname, String firstName, String secondName, boolean dismissPredefinedGroups)
            throws Exception {

        String predefinedGroups = "";
        if (dismissPredefinedGroups) {
            //  Проверяем не попадает ли в CLIENT_EMPLOYEES, а так же в ВЫБЫВШИЕ за предыдущие года
            /*predefinedGroups = " (cf_clients.idofclientgroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() +
                    " or (cf_clients.idofclientgroup=" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() +
                    " ) ) and ";*/
            predefinedGroups = " (cf_clients.idofclientgroup<=" + ClientGroup.Predefined.CLIENT_LEAVING.getValue() +
                    " ) and ";
            // and EXTRACT(year from date (to_timestamp(cf_clients.lastupdate / 1000))) = EXTRACT(year from date (current_timestamp))
            //predefinedGroups = " cf_clients.idofclientgroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " and ";
        }
        String fio=((surname==null?"":surname)+(firstName==null?"":firstName)+(secondName==null?"":secondName)).toLowerCase().replaceAll(" ", "");
        javax.persistence.Query q = em.createNativeQuery("select cf_clients.idofclient, cf_persons.Surname||' '||cf_persons.FirstName||' '||cf_persons.SecondName "+
                "from cf_clients "+
                "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "+
                "where (cf_clients.idoforg in (select cf_friendly_organization.friendlyorg from cf_friendly_organization where cf_friendly_organization.currentorg=:org)) and "+
                predefinedGroups +
                "(levenshtein(:fio, trim(lower(cf_persons.Surname))||trim(lower(cf_persons.FirstName))||trim(lower(cf_persons.SecondName)))<3 or "+
                "(trim(lower(cf_persons.Surname))=:surname and trim(lower(cf_persons.FirstName))=:firstName and (length(cf_persons.SecondName)=0 or length(:secondName)=0))) "+
                " order by levenshtein(:fio, trim(lower(cf_persons.Surname))||trim(lower(cf_persons.FirstName))||trim(lower(cf_persons.SecondName)))");
        q.setParameter("org", organization.getIdOfOrg());
        q.setParameter("surname", StringUtils.lowerCase(surname).trim());
        q.setParameter("firstName", StringUtils.lowerCase(firstName).trim());
        q.setParameter("fio", fio);
        q.setParameter("secondName", StringUtils.upperCase(secondName).trim());
        q.setMaxResults(1);
        List res = q.getResultList();
        if (res.isEmpty()) return null;
        //if (res.size()==2) return -1L;
        return (List<Object[]>)res;
        //return ((Number)((Object[])res.get(0))[0]).longValue();


        /*javax.persistence.Query query = entityManager.createQuery(
               "select idOfClient from Client client where (client.org = :org) and "
               + "(trim(upper(client.person.surname)) = :surname) and "
               + "(trim(upper(client.person.firstName)) = :firstName) and (trim(upper(client.person.secondName)) = :secondName)");
       query.setParameter("org", organization);
       query.setParameter("surname", StringUtils.upperCase(surname).trim());
       query.setParameter("firstName", StringUtils.upperCase(firstName).trim());
       query.setParameter("secondName", StringUtils.upperCase(secondName).trim());
       query.setMaxResults(2);
       if (query.getResultList().isEmpty()) return findClientByFullNameInFriendlyOrgs (entityManager, organization, surname, firstName, secondName);
       if (query.getResultList().size()==2) return -1L;
       return (Long)query.getResultList().get(0);*/
    }

    public static Long findClientByFullNameInFriendlyOrgs(EntityManager em, Org organization, String surname, String firstName, String secondName)
            throws Exception {
        javax.persistence.Query query = em.createQuery(
                "select idOfClient from Client client where (client.org = :org or client.org.idOfOrg in (select fo.idOfOrg from Org org join org.friendlyOrg fo where org.idOfOrg=client.org.idOfOrg)) and "
                        + "(trim(upper(client.person.surname)) = :surname) and "
                        + "(trim(upper(client.person.firstName)) = :firstName) and (trim(upper(client.person.secondName)) = :secondName)");
        query.setParameter("org", organization);
        query.setParameter("surname", StringUtils.upperCase(surname).trim());
        query.setParameter("firstName", StringUtils.upperCase(firstName).trim());
        query.setParameter("secondName", StringUtils.upperCase(secondName).trim());
        query.setMaxResults(2);
        if (query.getResultList().isEmpty()) return null;
        if (query.getResultList().size()==2) return -1L;
        return ((Number)query.getResultList().get(0)).longValue();
    }

    public static boolean existCard(Session persistenceSession, long cardPrintedNo) throws Exception {
        Query query = persistenceSession.createQuery("select 1 from Card card where card.cardPrintedNo = ?");
        query.setParameter(0, cardPrintedNo);
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    public static boolean existNewCard(Session persistenceSession, long cardPrintedNo) throws Exception {
        Query query = persistenceSession.createQuery("select 1 from NewCard card where card.cardPrintedNo = ?");
        query.setParameter(0, cardPrintedNo);
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    public static boolean existVisitorDogm(Session persistenceSession, String passportNumber,
            String driverLicenceNumber, String warTicketNumber) throws Exception {
        boolean passport = StringUtils.isNotEmpty(passportNumber) && existVisitorDogmPassport(persistenceSession, passportNumber);
        boolean driverLicence = StringUtils.isNotEmpty(driverLicenceNumber) && existVisitorDogmDriverLicence(persistenceSession, driverLicenceNumber);
        boolean warTicket = StringUtils.isNotEmpty(warTicketNumber) && existVisitorDogmWarTicket(persistenceSession, warTicketNumber);
        return passport || driverLicence || warTicket;
    }

    public static boolean existVisitorDogmPassport(Session persistenceSession, String passportNumber) throws Exception {
        Query query = persistenceSession.createQuery("select 1 from Visitor visitor "
                + "where visitor.passportNumber = ? and visitor.visitorType = ?");
        query.setParameter(0, passportNumber);
        query.setParameter(1, Visitor.VISITORDOGM_TYPE);
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    public static boolean existVisitorDogmDriverLicence(Session persistenceSession, String driverLicenceNumber) throws Exception {
        Query query = persistenceSession.createQuery("select 1 from Visitor visitor "
                + "where visitor.driverLicenceNumber = ? and visitor.visitorType = ?");
        query.setParameter(0, driverLicenceNumber);
        query.setParameter(1, Visitor.VISITORDOGM_TYPE);
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    public static boolean existVisitorDogmWarTicket(Session persistenceSession, String warTicketNumber) throws Exception {
        Query query = persistenceSession.createQuery("select 1 from Visitor visitor "
                + "where visitor.warTicketNumber = ? and visitor.visitorType = ?");
        query.setParameter(0, warTicketNumber);
        query.setParameter(1, Visitor.VISITORDOGM_TYPE);
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    public static long updateClientRegistryVersion(Session persistenceSession) throws Exception {
        return updateClientRegistryVersionWithPessimisticLock();
    }

    public static long updateClientRegistryVersionWithPessimisticLock() throws Exception {
        return updateClientRegistryVersionWithPessimisticLock(1);
    }

    public static long updateClientRegistryVersionWithPessimisticLock(int count) throws Exception {
        Transaction transaction = null;
        Session session = RuntimeContext.getInstance().createPersistenceSession();
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from Registry r where r.idOfRegistry=:idOfRegistry");
            query.setParameter("idOfRegistry", Registry.THE_ONLY_INSTANCE_ID);
            query.setLockMode("r", LockMode.PESSIMISTIC_WRITE);
            Registry registry = (Registry)query.uniqueResult();
            Long result = registry.getClientRegistryVersion() + 1;
            registry.setClientRegistryVersion(registry.getClientRegistryVersion() + count);
            session.update(registry);
            transaction.commit();
            transaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public static long updateOrgLastContractIdWithPessimisticLock(Long idOfOrg, int count) throws Exception {
        Transaction transaction = null;
        Session session = RuntimeContext.getInstance().createPersistenceSession();
        try {
            transaction = session.beginTransaction();
            Query query = session.createQuery("from OrgContractId r where r.idOfOrg=:idOfOrg");
            query.setParameter("idOfOrg", idOfOrg);
            query.setLockMode("r", LockMode.PESSIMISTIC_WRITE);
            OrgContractId orgContractId = (OrgContractId)query.uniqueResult();
            Long result = orgContractId.getLastClientContractId() + 1;
            orgContractId.setLastClientContractId(orgContractId.getLastClientContractId() + count);
            session.update(orgContractId);
            transaction.commit();
            transaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public static Long findMenuExchangeSourceOrg(Session persistenceSession, Long idOfOrg) {
        Criteria criteria = persistenceSession.createCriteria(MenuExchangeRule.class);
        criteria.add(Restrictions.eq("idOfDestOrg", idOfOrg));
        MenuExchangeRule rule = (MenuExchangeRule) criteria.uniqueResult();
        if (rule == null) {
            return null;
        }
        return rule.getIdOfSourceOrg();
    }

    @SuppressWarnings("unchecked")
    public static List<Long> findMenuExchangeDestOrg(Session persistenceSession, Long idOfOrg) {
        Criteria criteria = persistenceSession.createCriteria(MenuExchangeRule.class);
        criteria.add(Restrictions.eq("idOfSourceOrg", idOfOrg));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.distinct(Projections.property("idOfDestOrg")))
        );
        return criteria.list();
        //MenuExchangeRule rule = (MenuExchangeRule) criteria.uniqueResult();
        //if (rule == null) {
        //    return null;
        //}
        //return rule.getIdOfSourceOrg();
    }

    @SuppressWarnings("unchecked")
    public static List<MenuExchange> findMenuExchangeDataBetweenDatesIncludingSettings(Session persistenceSession, Long idOfSourceOrg, Date startDate,
            Date endDate) {
        // Settings record has date = 0
        Query query = persistenceSession.createQuery("from MenuExchange where compositeIdOfMenuExchange.idOfOrg=:idOfSourceOrg AND ((compositeIdOfMenuExchange.menuDate>=:startDate AND compositeIdOfMenuExchange.menuDate<=:endDate) OR (compositeIdOfMenuExchange.menuDate=:nullDate))");
        query.setParameter("idOfSourceOrg", idOfSourceOrg);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("nullDate", new Date(0));
        List<MenuExchange> mEList = (List<MenuExchange>)query.list();
        return mEList;
        //Criteria criteria = persistenceSession.createCriteria(MenuExchange.class);
        //criteria.add(Restrictions.eq("compositeIdOfMenuExchange.idOfOrg", idOfSourceOrg));
        //criteria.add(Restrictions.ge("compositeIdOfMenuExchange.menuDate", startDate));
        //criteria.add(Restrictions.lt("compositeIdOfMenuExchange.menuDate", endDate));
        //return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public static MenuExchange findMenuExchangeBeforeDateByEqFlag(Session persistenceSession, Long idOfSourceOrg, Date startDate, int flag) {
        Query query = persistenceSession.createQuery("from MenuExchange where compositeIdOfMenuExchange.idOfOrg=:idOfSourceOrg AND compositeIdOfMenuExchange.menuDate<=:startDate AND flags=:flags ORDER BY compositeIdOfMenuExchange.menuDate DESC");
        query.setParameter("idOfSourceOrg", idOfSourceOrg);
        query.setParameter("startDate", startDate);
        query.setParameter("flags", flag);
        query.setMaxResults(1);
        List l=query.list();
        if (l.isEmpty()) return null;
        MenuExchange mE = (MenuExchange)l.get(0);
        return mE;
    }

    public static void updateMenuExchangeLink(Session persistenceSession, Long idOfSourceOrg, Long idOfDestOrg) {
        Query query = persistenceSession.createQuery(
                "delete from MenuExchangeRule discountrule where discountrule.idOfDestOrg=?");
        query.setParameter(0, idOfDestOrg);
        query.executeUpdate();
        ////
        if (idOfSourceOrg != null && idOfDestOrg != null) {
            persistenceSession.save(new MenuExchangeRule(idOfSourceOrg, idOfDestOrg));
        }
    }

    public static boolean isOrgMenuExchangeSource(Session persistenceSession, Long idOfOrg) {
        Query query = persistenceSession
                .createQuery("select 1 from MenuExchangeRule discountrule where discountrule.idOfSourceOrg = ?");
        query.setParameter(0, idOfOrg);
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    public static void changeClientBalance(Session session, Client client, long sum, Date transactionDate) {
        Query q = session.createQuery("UPDATE Client SET balance = balance + :charge WHERE idOfClient = :id")
                .setParameter("charge", sum)
                .setParameter("id", client.getIdOfClient());
        q.executeUpdate();
        client.addBalanceNotForSave(sum);
        RuntimeContext.getAppContext().getBean(ProcessorUtils.class).saveLastProcessSectionCustomDateTransactionFree(
                session, client.getOrg().getIdOfOrg(), SectionType.LAST_TRANSACTION, transactionDate);
        if ((client.getBalanceToNotify() != null) && (client.getBalance() < client.getBalanceToNotify())) {
            sendNotificationLowBalance(session, client, transactionDate);
        }
    }

    private static void sendNotificationLowBalance(Session session, Client client, Date transactionDate) {
        try {
            String[] values = generateLowBalanceNotificationParams(session, client, transactionDate);
            RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                .sendNotificationAsync(client, null, EventNotificationService.NOTIFICATION_LOW_BALANCE,
                        values, transactionDate);
            List<Client> guardians = findGuardiansByClient(session, client.getIdOfClient(), null);

            if (!(guardians == null || guardians.isEmpty())) {
                for (Client destGuardian : guardians) {
                    if (DAOReadonlyService.getInstance().allowedGuardianshipNotification(destGuardian.getIdOfClient(),
                            client.getIdOfClient(),
                            ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_LOW_BALANCE.getValue())) {
                        RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                            .sendNotificationAsync(destGuardian, client, EventNotificationService.NOTIFICATION_LOW_BALANCE,
                                    values, transactionDate);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(String.format("Can't send notification low balance. Client id=%s", client.getIdOfClient()), e);
        }
    }

    private static String[] generateLowBalanceNotificationParams(Session session, Client client, Date trDate) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
        String empTime = df.format(trDate);
        Criteria criteria = session.createCriteria(Person.class);
        criteria.add(Restrictions.eq("idOfPerson", client.getPerson().getIdOfPerson()));
        Person person = (Person)criteria.uniqueResult();
        return new String[]{
                "balance", CurrencyStringUtils.copecksToRubles(client.getBalance()), "contractId",
                ContractIdFormat.format(client.getContractId()), "surname", person.getSurname(),
                "firstName", person.getFirstName(), "empTime", empTime,
                EventNotificationService.PARAM_BALANCE_TO_NOTIFY, CurrencyStringUtils.copecksToRubles(client.getBalanceToNotify())};
    }

    /**
     *
     * @param session
     * @param idOfClient
     * @param sum
     * @param addOrNew
     */
    // TODO: при добавленее нового поля субсчета необходио добавлять в логику
    public static void changeClientSubBalance1(Session session, Long idOfClient, long sum, boolean addOrNew) {
        final String queryAddString = "UPDATE Client SET subBalance1=subBalance1+? WHERE idOfClient=?";
        final String queryNewString = "UPDATE Client SET subBalance1=? WHERE idOfClient=?";
        final String queryString = addOrNew?queryNewString:queryAddString;
        Query q=session.createQuery(queryString);
        q.setLong(0, sum);
        q.setLong(1, idOfClient);
        q.executeUpdate();
    }

    // TODO: при добавленее нового поля субсчета необходио добавлять в логику
    public static void changeClientSubBalance(Session session, Long idOfClient, long sum, int subBalanceNum, boolean addOrNew) {
        final String queryAddString = String.format("UPDATE Client SET subBalance%d=subBalance%d+? WHERE idOfClient=?", subBalanceNum, subBalanceNum);
        final String queryNewString = String.format("UPDATE Client SET subBalance%d=? WHERE idOfClient=?", subBalanceNum);
        final String queryString = addOrNew?queryNewString:queryAddString;
        Query q=session.createQuery(queryString);
        q.setLong(0, sum);
        q.setLong(1, idOfClient);
        q.executeUpdate();
    }

    public static void deleteAssortmentForDate(Session persistenceSession, Org organization, Date menuDate) {
        Date endDate = DateUtils.addDays(menuDate, 1);
        endDate = CalendarUtils.addSeconds(endDate, -1);
        Query q = persistenceSession.createQuery("DELETE FROM Assortment WHERE org=:org AND beginDate>=:fromDate AND beginDate<=:endDate");
        q.setParameter("org", organization);
        q.setParameter("fromDate", menuDate);
        q.setParameter("endDate", endDate);
        q.executeUpdate();
    }

    public static void deleteComplexInfoForDate(Session persistenceSession, Org organization, Date menuDate) {
        Date endDate = DateUtils.addDays(menuDate, 1);
        endDate = CalendarUtils.addSeconds(endDate, -1);
        Query q = persistenceSession.createQuery("DELETE FROM ComplexInfo WHERE org=:org AND menuDate>=:fromDate AND menuDate<=:endDate");
        q.setParameter("org", organization);
        q.setParameter("fromDate", menuDate);
        q.setParameter("endDate", endDate);
        q.executeUpdate();
    }

    /*public static void deletePreordersByComplexInfo(Session session, Long idOfComplexInfo, Long version) {
        Query q = session.createQuery("update PreorderComplex pc set amount = 0, deletedState = true, complexInfo = null, version = :version "
                + "where pc.complexInfo.idOfComplexInfo = :idOfComplexInfo");
        q.setParameter("idOfComplexInfo", idOfComplexInfo);
        q.setParameter("version", version);
        q.executeUpdate();
    }

    public static void deleteComplexInfoDetailsByComplexInfo(Session session, Long idOfComplexInfo) {
        Query q = session.createQuery("delete from ComplexInfoDetail "
                + "where complexInfo.idOfComplexInfo = :idOfComplexInfo");
        q.setParameter("idOfComplexInfo", idOfComplexInfo);
        q.executeUpdate();
    }*/

    public static List<ComplexInfo> getComplexInfoForDate(Session session, Org organization, Date menuDate) {
        Date endDate = DateUtils.addDays(menuDate, 1);
        endDate = CalendarUtils.addSeconds(endDate, -1);
        Query q = session.createQuery("select ci from ComplexInfo ci WHERE ci.org=:org AND ci.menuDate>=:fromDate AND ci.menuDate<=:endDate");
        q.setParameter("org", organization);
        q.setParameter("fromDate", menuDate);
        q.setParameter("endDate", endDate);
        return q.list();
    }

    public static MenuDetail findMenuDetailByLocalId(Session persistenceSession, Menu menu, Long localIdOfMenu) {
        Query q = persistenceSession.createQuery("FROM MenuDetail WHERE menu=:menu AND localIdOfMenu=:localIdOfMenu");
        q.setParameter("menu", menu);
        q.setParameter("localIdOfMenu", localIdOfMenu);
        return (MenuDetail)q.uniqueResult();
    }

    public static MenuDetail findMenuDetailByPathAndPrice(Session persistenceSession, Menu menu, String path, Long price) {
        String sql = "";
        if (price != null) {
            sql = "FROM MenuDetail WHERE menu=:menu AND menupath=:menupath AND price=:price";
        } else {
            sql ="FROM MenuDetail WHERE menu=:menu AND menupath=:menupath";
        }
        Query q = persistenceSession.createQuery(sql);
        q.setParameter("menu", menu);
        q.setParameter("menupath", path);
        if (price != null) {
            q.setParameter("price", price);
        }
        return q.list() == null || q.list().size() < 1 ? null : (MenuDetail)q.list().get(0);//.uniqueResult();
    }

    public static PublicKey getContragentPublicKey(RuntimeContext runtimeContext, Long idOfContragent) throws Exception {
        PublicKey publicKey;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            // Start data model transaction
            persistenceTransaction = persistenceSession.beginTransaction();
            // Find given org
            Contragent c = (Contragent) persistenceSession.get(Contragent.class, idOfContragent);
            if (null == c) {
                throw new NullPointerException("Unknown contragent with id == "+idOfContragent);
            }
            publicKey = DigitalSignatureUtils.convertToPublicKey(c.getPublicKey());
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return publicKey;
    }

    /**
     * Возвращает список транзакций произведенных в интревале времени, для конкретной организации
     * и с укзазанием спика необходимого типа транзакций (если список пуст то не будет оганичеваться по данному критерию)
     * @param persistenceSession ссылка на сессию
     * @param org ссылка на организацию
     * @param fromDateTime время с которого учитывается >
     * @param toDateTime время до которого учитывается <=
     * @param sourceType список типов транзакций если путо не будет учитываться в криетрии
     * @return возвращается список транзакций клиентов
     */
    @SuppressWarnings("unchecked")
    public static List<AccountTransaction> getAccountTransactionsForOrgSinceTime(Session persistenceSession, Org org,
            Date fromDateTime, Date toDateTime, List<Integer> sourceType) {
        Criteria criteria = persistenceSession.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.in("org", org.getFriendlyOrg()));
        criteria.add(Restrictions.gt("transactionTime", fromDateTime)); // >
        criteria.add(Restrictions.le("transactionTime", toDateTime));   // <=
        if(!CollectionUtils.isEmpty(sourceType)) {
            criteria.add(Restrictions.in("sourceType", sourceType));
        }
        return criteria.list();
    }

    /**
     * Возвращает список данных синхронизации укзанного типа за интервал времени для указанной организации
     *
     * @param persistenceSession ссылка на сессию
     * @param idOfOrg            id организации
     * @param fromDateTime       время с которого учитывается >
     * @param toDateTime         время до которого учитывается <=
     * @param syncType           список типов транзакций если путо не будет учитываться в криетрии
     * @return возвращается список транзакций клиентов
     */
    @SuppressWarnings("unchecked")
    public static List<SyncHistoryCalc> getSyncHistoryCalc(Session persistenceSession, Long idOfOrg, Date fromDateTime,
            Date toDateTime) {
        Criteria criteria = persistenceSession.createCriteria(SyncHistoryCalc.class);
        if (idOfOrg != null) {
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        }
        criteria.add(Restrictions.ge("syncDay", fromDateTime)); // >=
        criteria.add(Restrictions.le("syncDay", toDateTime));   // <=
        return criteria.list();
    }

    /**
     * Возвращает список транзакций произведенных в интревале времени, для конкретной организации
     * и с укзазанием спика необходимого типа транзакций (если список пуст то не будет оганичеваться по данному критерию)
     * @param persistenceSession ссылка на сессию
     * @param org ссылка на организацию
     * @param fromDateTime время с которого учитывается >
     * @param toDateTime время до которого учитывается <=
     * @return возвращается список транзакций клиентов
     */
    /*@SuppressWarnings("unchecked")
    public static List<AccountTransaction> getAccountTransactionsForOrgSinceTime(Session persistenceSession, Org org,
            Date fromDateTime, Date toDateTime) {
        Criteria criteria = persistenceSession.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.in("org", org.getFriendlyOrg()));
        criteria.add(Restrictions.gt("transactionTime", fromDateTime)); // >
        criteria.add(Restrictions.le("transactionTime", toDateTime));   // <=
        HibernateUtils.addAscOrder(criteria, "client.idOfClient");
        return criteria.list();
    }

    public static List<AccountTransactionExtended> getAccountTransactionsForOrgSinceTimeV2(Session persistenceSession, Org org,
            Date fromDateTime, Date toDateTime) {
        String str_query = "select t.idOfTransaction, t.source, t.transactionDate, " +
                "t.sourceType, t.transactionSum,  " +
                "coalesce(t.transactionSubBalance1Sum, 0) as transactionSubBalance1Sum, coalesce(query.complexsum, 0) as complexsum, " +
                "coalesce(query.discountsum, 0) as discountsum, coalesce(query.orderType, 0) as ordertype, t.idOfClient " +
                "from cf_transactions t left join " +
                "(select coalesce(sum(dd.qty * dd.rprice), 0) as complexsum, coalesce(sum(dd.socDiscount), 0) as discountsum, oo.orderType, oo.idOfTransaction " +
                "from cf_orders oo join cf_orderdetails dd on oo.idOfOrder = dd.idOfOrder and oo.idOfOrg = dd.idOfOrg " +
                "where oo.createdDate > :orders_begDate AND oo.createddate <= :orders_endDate AND oo.idOfOrg in (:orgs) " +
                "AND dd.idOfOrg in (:orgs) AND dd.menuType between :menuMin and :menuMax " +
                "group by oo.orderType, oo.idOfTransaction) as query " +
                "on t.idOfTransaction = query.idOfTransaction " +
                "where t.idOfOrg in (:orgs) AND t.transactionDate > :trans_begDate AND t.transactionDate <= :trans_endDate " +
                "order by t.idOfClient";
        SQLQuery q = persistenceSession.createSQLQuery(str_query);
        // заказы будем искать за последние 24 часа от времени запроса
        q.setParameter("orders_begDate", CalendarUtils.addDays(toDateTime,-1).getTime());
        q.setParameter("orders_endDate",toDateTime.getTime());
        // транзакции будем искать строго от запрашиваемого времени
        q.setParameter("trans_begDate", fromDateTime.getTime());
        q.setParameter("trans_endDate", toDateTime.getTime());
        q.setParameterList("orgs", org.getFriendlyOrg());
        q.setParameter("menuMin", OrderDetail.TYPE_COMPLEX_MIN);
        q.setParameter("menuMax", OrderDetail.TYPE_COMPLEX_MAX);
        q.setResultTransformer(Transformers.aliasToBean(AccountTransactionExtended.class));
        q.addScalar("idoftransaction").addScalar("source").addScalar("transactiondate").addScalar("sourcetype").addScalar("transactionsum").addScalar("transactionsubbalance1sum")
                .addScalar("complexsum", StandardBasicTypes.BIG_DECIMAL).addScalar("discountsum", StandardBasicTypes.BIG_DECIMAL).addScalar("ordertype")
                .addScalar("idofclient");
        return q.list();
    }*/

    @SuppressWarnings("unchecked")
    public static List<Card> getClientsAndCardsForOrgs(Session persistenceSession, Set<Long> idOfOrgs, List<Long> clientIds) {
        Criteria clientCardsCriteria = persistenceSession.createCriteria(Card.class);
        clientCardsCriteria.createCriteria("client","cl", JoinType.LEFT_OUTER_JOIN);
        if (clientIds!=null && !clientIds.isEmpty()) {
            clientCardsCriteria.add(Restrictions.in("cl.idOfClient", clientIds));
        }
        clientCardsCriteria.createCriteria("cl.org","o", JoinType.LEFT_OUTER_JOIN);
        clientCardsCriteria.add(Restrictions.in("o.idOfOrg", idOfOrgs));
        return clientCardsCriteria.list();
    }

    public static EnterEvent findEnterEvent(Session persistenceSession, CompositeIdOfEnterEvent compositeIdOfEnterEvent) throws Exception {
        return (EnterEvent) persistenceSession.get(EnterEvent.class, compositeIdOfEnterEvent);
    }

    public static boolean existContragentWithClass(Session persistenceSession, Integer classId)
            throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", classId));
        return !criteria.list().isEmpty();
    }

    public static boolean existOrder(Session persistenceSession, long idOfOrg, long idOfOrder)
            throws Exception {
        Query q = persistenceSession.createQuery(
                "select count(*) from Order o where o.id.idOfOrg=:idOfOrg and o.id.idOfOrder=:idOfOrder");
        q.setParameter("idOfOrg", idOfOrg);
        q.setParameter("idOfOrder", idOfOrder);
        return ((Long)q.uniqueResult())!=0;
    }

    public static boolean existEnterEvent(Session persistenceSession, long idOfOrg, long idOfEnterEvent)
            throws Exception {
        Query q = persistenceSession.createQuery(
                "select count(*) from EnterEvent ee where ee.id.idOfOrg=:idOfOrg and ee.id.idOfEnterEvent=:idOfEnterEvent");
        q.setParameter("idOfOrg", idOfOrg);
        q.setParameter("idOfEnterEvent", idOfEnterEvent);
        return ((Long)q.uniqueResult())!=0;
    }

    public static CategoryDiscount getCategoryDiscount(EntityManager em, Long categoryId) {
        javax.persistence.Query q = em.createQuery("from CategoryDiscount where idOfCategoryDiscount = :idOfCategoryDiscount");
        q.setParameter("idOfCategoryDiscount", categoryId);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return (CategoryDiscount)l.get(0);
    }

    public static void clearGoodComplaintIterationStatus(EntityManager em) {
        javax.persistence.Query q = em.createQuery("delete from GoodComplaintIterationStatus");
        q.executeUpdate();
    }

    public static void clearGoodComplaintPossibleCauses(EntityManager em) {
        javax.persistence.Query q = em.createQuery("delete from GoodComplaintPossibleCauses");
        q.executeUpdate();
    }

    public static boolean getOptionValueBool(Session session, long nOption, boolean defaultValue) {
        String v =getOptionValue(session, nOption, defaultValue?"1":"0");
        return v.equals("1") || v.compareToIgnoreCase("true")==0;
    }
    public static String getOptionValue(EntityManager em, long nOption, String defaultValue) {
        return getOptionValue((Session)em.getDelegate(),  nOption, defaultValue);
    }
   /* public static String getOptionValue(Session session, long nOption, String defaultValue) {
        Query q = session.createQuery("from Option where idOfOption=:nOption");
        q.setParameter("nOption", nOption);
        List l = q.list();
        String v;
        if (l.size()==0) v = defaultValue;
        else v=((Option)l.get(0)).getOptionText();
        return v;
    }*/

    public static String getOptionValue(Session session, long nOption, String defaultValue) {
        Query q = session.createQuery("from Option where idOfOption=:nOption");
        q.setParameter("nOption", nOption);
        List l = q.list();
        String v;
        if (l.size()==0) return defaultValue;
        v=((Option)l.get(0)).getOptionText();
        if(v==null) return defaultValue;
        return v;
    }
    public static void setOptionValue(Session session, long nOption, String value) {
        session.saveOrUpdate(new Option(nOption, value));
    }
    public static void setOptionValue(EntityManager em, long nOption, String value) {
        setOptionValue((Session)em.getDelegate(), nOption, value);
    }

    public static int deleteFromTransactionJournal(EntityManager entityManager, long maxIdOfTransactionJournal) {
        javax.persistence.Query q = entityManager.createQuery("delete from TransactionJournal where idOfTransactionJournal<=:maxId");
        q.setParameter("maxId", maxIdOfTransactionJournal);
        return q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public static List<TransactionJournal> fetchTransactionJournalRecs(EntityManager entityManager,
            int maxRecordsInBatch) {
        javax.persistence.Query q = entityManager.createQuery("from TransactionJournal order by idOfTransactionJournal asc");
        q.setMaxResults(maxRecordsInBatch);
        return (List<TransactionJournal>)q.getResultList();
    }

    public static List<Bank> getBanks(EntityManager entityManager){
        javax.persistence.Query q = entityManager.createQuery("from Bank");
        return (List<Bank>)q.getResultList();

    }

    /**
     * производит выборку всех значений по таблице CategoryOrg и
     * выдает все значения в данной таблице с учетом сортировке по идентификатору
     * @author Kadyrov Damir
     * @since  2012-02-08
     * @param entityManager менеджер сущностей
     * @return null если таблица пуста, List<CategoryOrg>
     */
     public static List<CategoryOrg> findCategoryOrg(EntityManager entityManager) {
        javax.persistence.Query q = entityManager.createQuery("from CategoryOrg order by idOfCategoryOrg asc");
        return (List<CategoryOrg>)q.getResultList();
    }

    /**
     * производит выборку органичиваясь данным множеством идентификаторов по таблице Orgs и
     * выдает все значения в данной таблице с учетом сортировке по идентификатору
     * @author Kadyrov Damir
     * @since  2012-02-08
     * @param entityManager менеджер сущностей
     * @param idOfOrgList список идентификаторов организаций
     * @return null если таблица пуста, List<Org>
     */
    public static List<Org> findOrgs(EntityManager entityManager, List<Long> idOfOrgList) {
        javax.persistence.Query q = entityManager.createQuery("from Org where idOfOrg in :curId order by idOfOrg asc");
        q.setParameter("curId", idOfOrgList);
        return (List<Org>) q.getResultList();
    }

    /**
     * производит выборку по идентификатору в таблице CategoryOrg
     * @author Kadyrov Damir
     * @since  2012-02-08
     * @param entityManager менеджер сущностей
     * @param selectedIdOfCategoryOrg идентификатор искаемой организации
     * @return null если таблица пуста, CategoryOrg
     */
    public static CategoryOrg findCategoryOrgById(EntityManager entityManager, Long selectedIdOfCategoryOrg) {
        javax.persistence.Query q = entityManager.createQuery("from CategoryOrg where idOfCategoryOrg=:curId");
        q.setParameter("curId", selectedIdOfCategoryOrg);
        return (CategoryOrg) q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public static List<CategoryDiscount> getCategoryDiscountList(EntityManager entityManager) {
        javax.persistence.Query q = entityManager.createQuery("from CategoryDiscount order by idOfCategoryDiscount");
        return (List<CategoryDiscount>)q.getResultList();
    }

    public static CategoryDiscount findCategoryDiscountById(EntityManager entityManager, long idOfCategoryDiscount) {
        javax.persistence.Query q = entityManager.createQuery("from CategoryDiscount where idOfCategoryDiscount=:idOfCategoryDiscount");
        q.setParameter("idOfCategoryDiscount", idOfCategoryDiscount);
        return (CategoryDiscount)q.getSingleResult();
    }

    public static CategoryDiscountDSZN findCategoryDiscountDSZNById(EntityManager entityManager, int idOfCategoryDiscountDSZN) {
        javax.persistence.Query q = entityManager.createQuery("from CategoryDiscountDSZN where idOfCategoryDiscountDSZN=:idOfCategoryDiscountDSZN");
        q.setParameter("idOfCategoryDiscountDSZN", idOfCategoryDiscountDSZN);
        return (CategoryDiscountDSZN)q.getSingleResult();
    }

    public static CategoryDiscountDSZN findCategoryDiscountDSZNByCode(EntityManager entityManager, int code) {
        javax.persistence.Query q = entityManager.createQuery("from CategoryDiscountDSZN where code=:code");
        q.setParameter("code", code);
        CategoryDiscountDSZN result;
        try {
            result = (CategoryDiscountDSZN) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return result;
    }

    public static long nextVersionByCategoryDiscountDSZN(EntityManager entityManager){
        long version = 0L;
        javax.persistence.Query query = entityManager.createNativeQuery("select cd.version from CF_CategoryDiscounts_DSZN as cd "
                + "order by cd.version desc limit 1 for update");
        try {
            Object o = query.getSingleResult();
            if(o!=null){
                version = Long.valueOf(o.toString())+1;
            }
        } catch (NoResultException ignore) {}
        return version;
    }

    public static void deleteCategoryDiscount(Session session, long id) {
        //TODO: разобораться надо ли это с учетом ввода таблицы связи
        CategoryDiscount categoryDiscount = (CategoryDiscount) session.load(CategoryDiscount.class, id);
        Criteria clientCriteria = session.createCriteria(Client.class);
        Criterion exp1 = Restrictions.or(
            Restrictions.like("categoriesDiscounts", categoryDiscount.getIdOfCategoryDiscount() + "", MatchMode.EXACT),
            Restrictions.like("categoriesDiscounts", categoryDiscount.getIdOfCategoryDiscount() + ",",
                MatchMode.START));
        Criterion exp2 = Restrictions.or(
            Restrictions.like("categoriesDiscounts", "," + categoryDiscount.getIdOfCategoryDiscount(),
                MatchMode.END),
            Restrictions.like("categoriesDiscounts", "," + categoryDiscount.getIdOfCategoryDiscount() + ",",
                MatchMode.ANYWHERE));
        Criterion expression = Restrictions.or(exp1, exp2);
        clientCriteria.add(expression);
        List<Client> clients = clientCriteria.list();
        for (Client client : clients) {
            String categoriesDiscounts = client.getCategoriesDiscounts();
            if (categoriesDiscounts.contains("," + id + ","))
                categoriesDiscounts = categoriesDiscounts.replace("," + id + ",", ",");
            else if (categoriesDiscounts.startsWith(id + ","))
                categoriesDiscounts = categoriesDiscounts.substring((id + ",").length());
            else if (categoriesDiscounts.endsWith("," + id))
                categoriesDiscounts = categoriesDiscounts.substring(0, categoriesDiscounts.length() - ("," + id).length());
            else
                categoriesDiscounts = categoriesDiscounts.replace("" + id, "");
            client.setCategoriesDiscounts(categoriesDiscounts);
            session.save(client);
        }

        session.delete(categoryDiscount);
    }

    public static void deleteCategoryDiscountDSZN(Session session, Long id, Long nextVersion) {
        CategoryDiscountDSZN categoryDiscountDSZN = (CategoryDiscountDSZN) session.load(CategoryDiscountDSZN.class, id.intValue());
        categoryDiscountDSZN.setDeleted(true);
        categoryDiscountDSZN.setVersion(nextVersion);
        categoryDiscountDSZN.setCategoryDiscount(null);
        session.save(categoryDiscountDSZN);
    }

    @SuppressWarnings("unchecked")
    public static List<Org> getOrgsByIdList(EntityManager entityManager, List<Long> idOfOrgList) {
        if (idOfOrgList.isEmpty()) return new LinkedList<Org>();
        javax.persistence.Query q = entityManager.createQuery("from Org where idOfOrg in :idOfOrgs");
        q.setParameter("idOfOrgs", idOfOrgList);
        return (List<Org>)q.getResultList();
    }

    public static Map<Long, Org> getOrgsByIdList(Session session, List<Long> idOfOrgList) {
        Map<Long, Org> orgMap = new HashMap<Long, Org>(0);
        if (idOfOrgList.isEmpty()) return orgMap;
        String idOfOrgs=idOfOrgList.toString().replaceAll("[^0-9,]","");
        Query query = session.createQuery("from Org where idOfOrg in ("+idOfOrgs+")");
        for(Object entity: query.list()){
            Org org = (Org) entity;
            orgMap.put(org.getIdOfOrg(),org);
        }
        return orgMap;
    }

    public static long getCategoryDiscountMaxId(EntityManager em) {
        javax.persistence.Query q = em.createQuery("select max(idOfCategoryDiscount) from CategoryDiscount");
        return (Long)(q.getSingleResult());
    }

    public static List<DiscountRule> listDiscountRules(EntityManager em) {
        javax.persistence.Query q = em.createQuery("from DiscountRule order by priority, idOfRule asc");
        return q.getResultList();
    }

    public static List getCategoryDiscountListWithIds(EntityManager em, List<Long> idOfCategoryList) {
        javax.persistence.Query q = em.createQuery("from CategoryDiscount where idOfCategoryDiscount in (:idOfCategoryList)");
        q.setParameter("idOfCategoryList", idOfCategoryList);
        return q.getResultList();
    }

    public static List getCategoryOrgWithIds(EntityManager em, List<Long> idOfCategoryOrgList) {
        javax.persistence.Query q = em.createQuery("from CategoryOrg where idOfCategoryOrg in (:idOfCategoryOrgList)");
        q.setParameter("idOfCategoryOrgList", idOfCategoryOrgList);
        return q.getResultList();
    }

    public static List getCategoryOrgWithIds(Session session, List<Long> idOfCategoryOrgList) {
        String idOfCategoryOrgs=idOfCategoryOrgList.toString().replaceAll("[^0-9,]", "");
        Query query = session.createQuery("from CategoryOrg where idOfCategoryOrg in ("+idOfCategoryOrgs+")");
        return query.list();
    }

    public static EnterEvent getLastEnterEvent(Session session, Client client) {
        Query q = session.createQuery("from EnterEvent where client=:client order by evtDateTime desc");
        q.setMaxResults(1);
        q.setParameter("client", client);
        List l = q.list();
        if (l.size()>0) return (EnterEvent)l.get(0);
        return null;
    }

    public static long getIdForTemporaryClientGroup(Session session, Long idOfOrg) {
        Query q = session.createQuery("select min(compositeIdOfClientGroup.idOfClientGroup) from ClientGroup where compositeIdOfClientGroup.idOfOrg=:idOfOrg");
        q.setParameter("idOfOrg", idOfOrg);
        List l = q.list();
        long minId=ClientGroup.TEMPORARY_GROUP_MAX_ID;
        if (l.size()>0 && l.get(0)!=null) {
            long id = (Long)l.get(0);
            if (id<minId) minId = id;
        }
        return minId-1;
    }

    public static boolean removeEmptyClientGroupByName(Session persistenceSession, Long idOfOrg, String groupName) throws Exception {
        Query q = persistenceSession.createSQLQuery("delete from cf_clientgroups where idoforg=:idoforg and groupname=:groupname");
        q.setLong("idoforg", idOfOrg);
        q.setString("groupname", groupName);
        return q.executeUpdate() > 0;
    }

    public static ClientGroup createClientGroup(Session persistenceSession, Long idOfOrg, ClientGroup.Predefined predefined) {
        Long idOfClientGroup =null;
        CompositeIdOfClientGroup compositeIdOfClientGroup = new CompositeIdOfClientGroup(idOfOrg,predefined.getValue());
        ClientGroup clientGroup = new ClientGroup(compositeIdOfClientGroup, predefined.getNameOfGroup());
        persistenceSession.save(clientGroup);
        return clientGroup;
    }

    public static ClientGroup createClientGroup(Session persistenceSession, Long idOfOrg, String clientGroupName) {
        ClientGroup.Predefined predefined = ClientGroup.Predefined.parse(clientGroupName);
        Long idOfClientGroup;
        /* если группа предопредлелена */
        if(predefined!=null){
            idOfClientGroup = predefined.getValue();
        } else {
            /* иначе это класс */
            Query q = persistenceSession.createQuery("select max(compositeIdOfClientGroup.idOfClientGroup) from ClientGroup where compositeIdOfClientGroup.idOfOrg=:idOfOrg and compositeIdOfClientGroup.idOfClientGroup<:idOfClientGroup");
            q.setParameter("idOfOrg", idOfOrg);
            q.setParameter("idOfClientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            List l = q.list();
            Long res = l.isEmpty() || l.get(0) == null ? ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue()
                    : (Long) l.get(0);
            idOfClientGroup = res + 1;
            }
        CompositeIdOfClientGroup compositeIdOfClientGroup = new CompositeIdOfClientGroup(idOfOrg,idOfClientGroup);
        ClientGroup clientGroup = new ClientGroup(compositeIdOfClientGroup, clientGroupName);
        persistenceSession.save(clientGroup);
        return clientGroup;
    }

    public static Contragent findContragentByClass(Session session, int classId) {
        // Оператор
        Criteria criteria = session.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", classId));
        return (Contragent) criteria.uniqueResult();
    }

    public static Good findGoodByGuid(Session session, String guidOfGood) {
        Criteria criteria = session.createCriteria(Good.class);
        criteria.add(Restrictions.eq("guid", guidOfGood));
        return (Good) criteria.uniqueResult();
    }

    public static void savePreorderGuidFromOrderDetail(Session session, String guid, OrderDetail orderDetail, boolean cancelOrder) {
        if (!cancelOrder) {
            PreorderLinkOD linkOD = new PreorderLinkOD(guid, orderDetail);
            session.save(linkOD);
        }
        Criteria criteria = session.createCriteria(PreorderComplex.class);
        criteria.add(Restrictions.eq("guid", guid));
        PreorderComplex preorderComplex = (PreorderComplex)criteria.uniqueResult();
        if (preorderComplex != null) {
            Long sum = orderDetail.getQty() * orderDetail.getRPrice();
            Long qty = orderDetail.getQty();
            if (cancelOrder) {
                sum = -sum;
                qty = -qty;
            }
            preorderComplex.setUsedSum(preorderComplex.getUsedSum() + sum);
            preorderComplex.setUsedAmount(preorderComplex.getUsedAmount() + qty);
            session.update(preorderComplex);
        }
    }

    public static void changeClientGroupNotifyViaSMS(Session session, boolean notifyViaSMS, List<Long> clientsId)
            throws Exception {
        org.hibernate.Query q = session.createQuery(
                "update Client set notifyViaSMS = :notifyViaSMS, clientRegistryVersion=:clientRegistryVersion where idOfClient in :clientsId");
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
        q.setLong("clientRegistryVersion", clientRegistryVersion);
        q.setBoolean("notifyViaSMS", notifyViaSMS);
        q.setParameterList("clientsId", clientsId);
        if (q.executeUpdate() != clientsId.size())
            throw new Exception("Ошибка при изменении параметров SMS уведомления");
    }

    public static void changeClientGroupNotifyViaPUSH(Session session, boolean notifyViaPUSH, List<Long> clientsId)
            throws Exception {
        org.hibernate.Query q = session.createQuery(
                "update Client set notifyViaPUSH = :notifyViaPUSH, clientRegistryVersion=:clientRegistryVersion where idOfClient in :clientsId");
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
        q.setLong("clientRegistryVersion", clientRegistryVersion);
        q.setBoolean("notifyViaPUSH", notifyViaPUSH);
        q.setParameterList("clientsId", clientsId);
        if (q.executeUpdate() != clientsId.size())
            throw new Exception("Ошибка при изменении параметров PUSH уведомления");
    }

    public static void changeReadOnlyNotifyViaSMS(Session session, boolean readOnlyNotifyViaSMS, List<Long> clientsId)
            throws Exception {
        org.hibernate.Query q = session.createQuery(
                "update Client set readOnlyNotifyViaSMS = :readOnlyNotifyViaSMS, clientRegistryVersion=:clientRegistryVersion where idOfClient in :clientsId");
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
        q.setLong("clientRegistryVersion", clientRegistryVersion);
        q.setBoolean("readOnlyNotifyViaSMS", readOnlyNotifyViaSMS);
        q.setParameterList("clientsId", clientsId);
        if (q.executeUpdate() != clientsId.size())
            throw new Exception("Ошибка при изменении параметров SMS уведомления");
    }

    public static long getNextVersion(Session session) {
        SQLQuery query = session.createSQLQuery("SELECT nextval('version')");
        long version = ((BigInteger) query.uniqueResult()).longValue();
        return version;
    }

    public static List getClientGroupsByIdOfOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(ClientGroup.class);
        criteria.add(Restrictions.eq("compositeIdOfClientGroup.idOfOrg", idOfOrg));
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public static List<Org> getAllOrgWithGuid(EntityManager em) {
        javax.persistence.Query q = em.createQuery("from Org where guid is not null");
        return (List<Org>)q.getResultList();
    }

    public static List<Org> findOrgsWithContract(EntityManager entityManager, Contract contract) {
        TypedQuery<Org> query = entityManager.createQuery("from Org where contract=:contract", Org.class);
        query.setParameter("contract", contract);
        return query.getResultList();
    }

    public static void removeContractLinkFromOrg(EntityManager entityManager, Contract contract, Long idOfOrg) {
        Session session = entityManager.unwrap(Session.class);
        contract.setContractOrgHistory(session, idOfOrg);
        javax.persistence.Query q = entityManager.createQuery("update Org set contract=null where idOfOrg=:idOfOrg and contract=:contract");
        q.setParameter("idOfOrg", idOfOrg);
        q.setParameter("contract", contract);
        q.executeUpdate();
    }

    public static void removeContractLinkFromOrgs(EntityManager entityManager, Contract entity) {
        Session session = entityManager.unwrap(Session.class);
        Criteria  criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("contract", entity));
        List<Org> list = criteria.list();
        for (Org org : list) {
            entity.setContractOrgHistory(session, org.getIdOfOrg());
        }

        javax.persistence.Query q = entityManager.createQuery("update Org set contract=null where contract=:contract");
        q.setParameter("contract", entity);
        q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public static List<Object[]> getClientPaymentsDataForPeriod(EntityManager em, Date dtFrom, Date dtTo,
            Contragent caReceiver, Contragent contragent) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ClientPayment.class).createAlias("transaction", "at")
                .createAlias("at.client", "c").add(Restrictions.ge("createTime", dtFrom))
                .add(Restrictions.lt("createTime", dtTo));
        if (caReceiver != null) {
            criteria.add(Restrictions.eq("contragentReceiver", caReceiver));
        }
        if (contragent != null) {
            criteria.add(Restrictions.eq("contragent", contragent));
        }
        criteria.setProjection(Projections.projectionList().add(Projections.property("c.contractId"))
                .add(Projections.property("createTime")).add(Projections.property("paySum"))
                .add(Projections.property("idOfPayment")));
        return (List<Object[]>) criteria.list();
    }

    public static Object[] getClientPaymentById(EntityManager em, String paymentId, Contragent contragent) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ClientPayment.class).createAlias("transaction", "at")
                .createAlias("at.client", "c").add(Restrictions.eq("idOfPayment", paymentId));
        if (contragent != null) {
            criteria.add(Restrictions.eq("contragent", contragent));
        }
        criteria.setProjection(Projections.projectionList().add(Projections.property("c.contractId"))
                .add(Projections.property("createTime")).add(Projections.property("paySum"))
                .add(Projections.property("idOfPayment")));
        List<Object[]> res = (List<Object[]>) criteria.list();
        if(res.size() < 1) {
            return null;
        }
        return res.get(0);
        //
    }

    public static Object[] getClientPaymentByIdSpecial(EntityManager em, String paymentId, Contragent contragent) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ClientPayment.class).createAlias("transaction", "at")
                .createAlias("at.client", "c").add(Restrictions.ilike("idOfPayment", paymentId, MatchMode.END));
        if (contragent != null) {
            criteria.add(Restrictions.eq("contragent", contragent));
        }
        criteria.setProjection(Projections.projectionList().add(Projections.property("c.contractId"))
                .add(Projections.property("createTime")).add(Projections.property("paySum"))
                .add(Projections.property("idOfPayment")));
        List<Object[]> res = (List<Object[]>) criteria.list();
        if(res.size() < 1) {
            return null;
        }
        return res.get(0);
        //
    }

    public static Long getBlockedCardsCount(EntityManager em) {
        javax.persistence.Query q = em.createQuery("select count(*) from Card where state<>:activeState");
        q.setParameter("activeState", Card.ACTIVE_STATE);
        return (Long)q.getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public static List<AccountTransfer> getAccountTransfersForClient(Session session, Client client, Date startTime,
            Date endTime) {
        org.hibernate.Query q = session.createQuery(
                "from AccountTransfer where (clientBenefactor=:client or clientBeneficiary=:client) and createTime>=:startTime and createTime<=:endTime");
        q.setLong("startTime", startTime.getTime());
        q.setLong("endTime", endTime.getTime());
        q.setParameter("client", client);
        return (List<AccountTransfer>)q.list();
    }
    
    @SuppressWarnings("unchecked")
    public static List<AccountRefund> getAccountRefundsForClient(Session session, Client client, Date startTime,
            Date endTime) {
        org.hibernate.Query q = session.createQuery(
                "from AccountRefund where (client=:client) and createTime>=:startTime and createTime<=:endTime");
        q.setLong("startTime", startTime.getTime());
        q.setLong("endTime", endTime.getTime());
        q.setParameter("client", client);
        return (List<AccountRefund>)q.list();
    }


    public static Client findClientByRefGUID(Session session, String guid){
        Criteria criteria = session.createCriteria(Client.class);
        criteria.add(Restrictions.eq("clientGUID",guid));
        return (Client) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public static <T extends DistributedObject> T findDistributedObjectByRefGUID(Class<T> clazz, Session session, String guid) throws DistributedObjectException{
        DistributedObject refDistributedObject = null;
        try {
            refDistributedObject = clazz.newInstance();
            Criteria criteria = session.createCriteria(clazz);
            criteria.add(Restrictions.eq("guid",guid));
            refDistributedObject.createProjections(criteria);
            criteria.setResultTransformer(Transformers.aliasToBean(clazz));
            criteria.setMaxResults(1);
            return (T) criteria.uniqueResult();
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Long> getListIdOfOrgList(Session session, Long idOfOrg){
        List<Long> resultList = new ArrayList<Long>();
        Query query = session.createQuery("select idOfDestOrg from MenuExchangeRule where idOfSourceOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        List<Long> list = (List<Long>) query.list();
        resultList.addAll(list);
        query = session.createQuery("select idOfSourceOrg from MenuExchangeRule where idOfDestOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        list = (List<Long>) query.list();
        resultList.addAll(list);
        return resultList;
    }

    public static Long getClientGroup(Session persistenceSession, Long idOfClient, Long idOfOrg) {
        Query q = persistenceSession.createQuery(
                "select idOfClientGroup from Client where idOfClient=:idOfClient and org.idOfOrg=:idOfOrg");
        q.setParameter("idOfClient", idOfClient);
        q.setParameter("idOfOrg", idOfOrg);
        try {
            return (Long)q.uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static void updateClientVersionAndRemoteAddressByOrg(Session persistenceSession, Long idOfOrg, String clientVersion,
            String remoteAddress, String sqlServerVersion) {
        Query query = persistenceSession.createQuery(
                 "update OrgSync "
                 + " set remoteAddress=:remoteAddress, clientVersion=:clientVersion, sqlserverversion = :sqlServerVersion "
                 + " where idOfOrg=:idOfOrg ");
        query.setParameter("remoteAddress", remoteAddress);
        query.setParameter("clientVersion", clientVersion);
        query.setParameter("sqlServerVersion", sqlServerVersion);
        query.setParameter("idOfOrg", idOfOrg);
        query.executeUpdate();
    }

    /* Венуть список идентификаторов корпусов организации */
    public static Set<Long> getIdOfFriendlyOrg(Session persistenceSession, Long idOfOrg){
        Query query = persistenceSession.createQuery("select fo.idOfOrg from Org org join org.friendlyOrg fo where org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        List list = query.list();
        Set<Long> idOfFriendlyOrgSet = new TreeSet<Long>();
        for (Object object: list){
            idOfFriendlyOrgSet.add((Long) object);
        }
        return idOfFriendlyOrgSet;
    }

    /* получаем список всех клиентов у которых guid пустой */
    public static List<Long> extractIdFromClientsByGUIDIsNull(EntityManager entityManager) {
        TypedQuery<Long> query = entityManager.createQuery("select idOfClient from Client client where client.clientGUID is null or client.clientGUID=''", Long.class);
        //query.setMaxResults(2000);
        return query.getResultList();
    }

    public static boolean isNextGradeTransfer(Session session, Long idOfOrg) {
        Query query = session.createQuery("select org.nextGradeParam from Org org where org.idOfOrg = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        Boolean f = (Boolean) query.uniqueResult();
        return f == null ? false : f;
    }

    public static void disableNextGradeTransfer(Session session, Long idOfOrg) {
        Query query = session.createQuery("update Org o set o.nextGradeParam = false where id = :idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        query.executeUpdate();
    }

    public static boolean isFullSyncByOrg(Session session, Long idOfOrg) {
        Query query = session.createQuery("select org.fullSyncParam from Org org where org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        Boolean f = (Boolean) query.uniqueResult();
        if(f== null) return false;
        else return f;
    }

    public static void falseFullSyncByOrg(Session session, Long idOfOrg) {
        Query query = session.createQuery("update Org set fullSyncParam=0 where id=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        query.executeUpdate();
    }

    public static List<Client> fetchErrorClientsWithOutFriendlyOrg(final Session persistenceSession,final Set<Org> friendlyOrg,
            final List<Long> errorClientIds) {
        final Query query = persistenceSession.createQuery("from Client cl where not(cl.org in (:friendlyOrg)) and cl.idOfClient in (:errorClientIds)");
        query.setParameterList("friendlyOrg", friendlyOrg);
        query.setParameterList("errorClientIds", errorClientIds);
        return query.list();
    }

    public static Client checkClientBindOrg(final Session session,final Long idOfClient,final Long idOfOrg) {
        final Query query = session.createQuery("select friendly.idOfOrg from Org o left join o.friendlyOrg friendly where o.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        final List<Long> idOfOrgList = query.list();
        Query clientQuery = session.createQuery("select cl from Client cl right join cl.org o where cl.idOfClient=:idOfClient and o.idOfOrg in (:idOfOrgList)");
        clientQuery.setParameter("idOfClient", idOfClient);
        clientQuery.setParameterList("idOfOrgList", idOfOrgList);
        return (Client) clientQuery.uniqueResult();
    }

    public static CardTempOperation findTempCartOperation(Session session, Long idOfOperation, Long idOfOrg) {
        final Query query = session.createQuery("select cto from CardTempOperation cto right join cto.org organization where organization.idOfOrg=:idOfOrg and cto.localId=:localId");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("localId", idOfOperation);
        return (CardTempOperation) query.uniqueResult();
    }

    public static Visitor existVisitor(Session session, Long idOfVisitor) {
        Query clientQuery = session.createQuery("select vi from Visitor vi where vi.idOfVisitor=:idOfVisitor");
        clientQuery.setParameter("idOfVisitor", idOfVisitor);
        return (Visitor) clientQuery.uniqueResult();
    }

    public static void removeGuardSan (Session session, Client client) {
        //  Очищаем cf_client_guardsan
        org.hibernate.Query remove = session.createSQLQuery("delete from CF_GuardSan where idofclient=:idofclient");
        remove.setLong("idofclient", client.getIdOfClient());
        remove.executeUpdate();
    }

    public static void clearGuardSanTable(Session session) {
        org.hibernate.Query clear = session.createSQLQuery("delete from CF_GuardSan");
        clear.executeUpdate();
    }

    public static Map<Long, String> getClientGuardSan_Old (Session session) {
        Map<Long, String> data = new HashMap<Long, String>();
        org.hibernate.Query select = session.createSQLQuery("select idofclient, guardsan from CF_Clients where guardsan<>'' order by idofclient");
        List resultList = select.list();
        for (Object entry : resultList) {
            Object e[] = (Object[]) entry;
            long idOfClient= ((BigInteger) e[0]).longValue();
            String guardSan = e[1].toString ();
            data.put(idOfClient, guardSan);
        }
        return data;
    }


    // TODO: воспользоваться диклоративными пособами генерации запроса и на выходи получать только TempCardOperationItem
    public static CardTempOperation getLastTempCardOperationByOrgAndCartNo(Session session, Long idOfOrg,Long cardNo) {
        Query query = session.createQuery(
                "select operation from CardTempOperation operation left join operation.cardTemp card  where operation.org.idOfOrg=:idOfOrg and card.cardNo=:cardNo order by operation.operationDate desc");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("cardNo",cardNo);
        List list = query.list();
        if(list==null || list.isEmpty()){
            return null;
        }else {
            return (CardTempOperation) list.get(0);
        }
    }

    public static Visitor findVisitorById(Session session, Long idOfVisitor) {
        Criteria criteria = session.createCriteria(Visitor.class);
        criteria.add(Restrictions.eq("idOfVisitor",idOfVisitor));
        return (Visitor) criteria.uniqueResult();
    }

    public static CardTemp findCardTempByVisitorAndCardNo(Session session, Visitor visitor, Long cardNo) {
        Criteria criteria = session.createCriteria(CardTemp.class);
        criteria.add(Restrictions.eq("visitor", visitor));
        criteria.add(Restrictions.eq("cardNo", cardNo));
        return (CardTemp) criteria.uniqueResult();
    }

    public static List fetchStudentsByCanNotConfirmPayment(Session persistenceSession, Long idOfClient) {
        Criteria criteria = persistenceSession.createCriteria(Order.class);
        criteria.add(Restrictions.eq("confirmerId",idOfClient));
        criteria.createCriteria("client","student", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.sqlRestriction("{alias}.balance + {alias}.limits < 0"));
        criteria.createAlias("student.person","person", JoinType.LEFT_OUTER_JOIN);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("person.firstName"), "firstName")
                .add(Projections.property("person.surname"), "surname")
                .add(Projections.property("person.secondName"), "secondName")
                .add(Projections.property("student.balance"), "balance")
                .add(Projections.property("RSum"), "rSum")
                .add(Projections.property("createTime"), "createTime")
                .add(Projections.property("student.idOfClient"), "idOfClient")
        );
        return criteria.list();
    }

    public static List fetchTeacherByDoConfirmPayment(Session persistenceSession) {
        Criteria criteria = persistenceSession.createCriteria(Order.class);
        criteria.add(Restrictions.isNotNull("confirmerId"));
        criteria.createCriteria("client","student", JoinType.LEFT_OUTER_JOIN)
                .add(Restrictions.sqlRestriction("{alias}.balance + {alias}.limits < 0"));
        criteria.createAlias("student.person","person", JoinType.LEFT_OUTER_JOIN);
        criteria.setProjection(Projections.projectionList()
                .add(Projections.property("person.firstName"), "firstName")
                .add(Projections.property("person.surname"), "surname")
                .add(Projections.property("person.secondName"), "secondName")
                .add(Projections.property("student.balance"), "balance")
                .add(Projections.property("RSum"), "rSum")
                .add(Projections.property("createTime"), "createTime")
                .add(Projections.property("student.idOfClient"), "idOfClient")
                .add(Projections.property("confirmerId"), "confirmerId")
        );
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    public static Client findTeacherPaymentForStudents(Session persistenceSession, Long contractId) {
        Criteria criteriaCanConfirmGroupPayment = persistenceSession.createCriteria(Client.class);
        criteriaCanConfirmGroupPayment.add(Restrictions.eq("canConfirmGroupPayment", true));
        criteriaCanConfirmGroupPayment.add(Restrictions.eq("contractId", contractId));
        List<Client> clients = criteriaCanConfirmGroupPayment.list();
        if(clients==null || clients.isEmpty()){
            return null;
        } else {
            return clients.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<CardTempOperation> getRegistrationTempCardOperationByOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(CardTempOperation.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("operationType", CardOperationStation.REGISTRATION));
        return criteria.list();
    }

    public static String extraxtORGNFromOrgByIdOfOrg(Session session, long idOfOrg) {
        Query query = session.createQuery("select o.OGRN from Org o where o.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        List list = query.list();
        if(list==null || list.isEmpty()){
            return "";
        } else {
            return (String) list.get(0);
        }
    }

    public static List<Long> extractIDFromGuardSanByGuardSan(Session persistenceSession, String guardSan) {
        Query q = persistenceSession.createQuery("select gs.client.idOfClient from GuardSan gs where gs.guardSan=:guardSan");
        q.setParameter("guardSan", guardSan);
        return q.list();
    }

    /*
    * Находит детей представителя или ребенка, у которого указан телефонный номер
    * */
    public static List<Long> extractIDFromGuardByGuardMobile(Session persistenceSession, String guardMobile) {
        //String query = "select client.idOfClient from Client client where (client.phone=:guardMobile or client.mobile=:guardMobile) "
        //        + "and not exists (select idOfClientGuardian from ClientGuardian where idOfChildren = Client.idOfClient)";
        String query = "select client.idOfClient from Client client where client.phone=:guardMobile or client.mobile=:guardMobile";
        Query q = persistenceSession.createQuery(query);
        q.setParameter("guardMobile", guardMobile);
        List<Long> clients = q.list();

        if (clients != null && !clients.isEmpty()){
            List<Long> clientsCopy = new ArrayList<Long>(clients);
            for(Long id : clientsCopy){
                Criteria criteria = persistenceSession.createCriteria(ClientGuardian.class);
                criteria.add(Restrictions.eq("idOfGuardian", id));
                criteria.add(Restrictions.eq("deletedState", false));
                //criteria.add(Restrictions.eq("disabled", false));
                List<ClientGuardian> list = criteria.list();
                if (list != null && list.size() > 0) {
                    for (ClientGuardian cg : list) {
                        clients.add(cg.getIdOfChildren());
                    }
                    clients.remove(id);
                }
            }
        }

        Set<Long> tempSet = new HashSet<Long>();
        tempSet.addAll(clients);
        clients.clear();
        clients.addAll(tempSet);
        return clients;
    }

    public static List<Good> getAllGoods (Session persistenceSession) {
        return getAllGoods (persistenceSession, null);
    }

    public static List<Good> getAllGoods (Session persistenceSession, Long idofgoodsgroup) {
        String sql = "from Good good";
        if (idofgoodsgroup != null && idofgoodsgroup != Long.MIN_VALUE) {
            sql += " where good.goodGroup.globalId=:idofgoodsgroup";
        }
        Query query = persistenceSession.createQuery(sql);
        if (idofgoodsgroup != null && idofgoodsgroup != Long.MIN_VALUE) {
            query.setLong("idofgoodsgroup", idofgoodsgroup);
        }
        List list = query.list();
        return list;
    }

    public static int clearFriendlyOrgByOrg(Session session, Long idOfOrg) {
        Query query = session.createSQLQuery("DELETE FROM cf_friendly_organization WHERE (currentorg=:idOfOrg or friendlyorg=:idOfOrg) and not (currentorg=friendlyorg)");
        query.setParameter("idOfOrg",idOfOrg);
        return query.executeUpdate();
    }

    public static HashSet<Org> findOrgById(Session session, List<Long> idOfOrgList) {
        Query query = session.createQuery("from Org org where org.id in :id");
        query.setParameterList("id", idOfOrgList);
        return new HashSet<Org>(query.list());
    }

    @SuppressWarnings("unchecked")
    public static List<Object[]> getClientInOrgTimes(EntityManager em, long idOfClient, Date from, Date to) {
        javax.persistence.Query q = em.createNativeQuery(
                "select date_trunc('day', to_timestamp(evtdatetime/1000)), (max(evtdatetime)-min(evtdatetime))/1000  from cf_enterevents where idofclient=:idOfClient and evtdatetime>=:fromTime and evtdatetime<=:toTime group by date_trunc('day', to_timestamp(evtdatetime/1000)) order by 1");
        q.setParameter("idOfClient", idOfClient);
        q.setParameter("fromTime", from.getTime());
        q.setParameter("toTime", to.getTime());
        return (List<Object[]>)q.getResultList();
    }

		public static int extractCardTypeByCartNo(Session session, Long cardNo) {
        Query query = session.createQuery("select visitorType from CardTemp where cardNo=:cardNo");
        query.setParameter("cardNo", cardNo);
        return (Integer) query.uniqueResult();
    }

    public static void createSyncHistoryException(Session session, long idOfOrg, SyncHistory history, String s) throws Exception {
        Org org = DAOUtils.getOrgReference(session, idOfOrg);
        SyncHistoryException syncHistoryException = new SyncHistoryException(org, history, s);
        session.save(syncHistoryException);
    }


    public static boolean isCommodityAccountingByOrg(Session session, Long idOfOrg){
        Query query = session.createQuery("select org.commodityAccounting from Org org where org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        Boolean f = (Boolean) query.uniqueResult();
        if(f == null) return false;
        else return f;
    }

    public static Boolean isSupplierByOrg(Session session, Long idOfOrg) {
        Query query = session.createQuery("select org.refectoryType from Org org where org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        int refectoryType = (Integer) query.uniqueResult();
        //{"Сырьевая столовая" - 0, "Столовая-доготовочная" - 1,
        // "Буфет-раздаточная" - 2, "Комбинат питания" - 3}
        return refectoryType==3;
    }

    @SuppressWarnings("unchecked")
    public static List<Product> findProductByProductGroup(Session session, ProductGroup currentProductGroup) {
        Criteria productCriteria = session.createCriteria(Product.class);
        productCriteria.add(Restrictions.eq("productGroup", currentProductGroup));
        return productCriteria.list();
    }

    public static Long countProductByProductGroup(Session session, ProductGroup currentProductGroup) {
        Criteria productCriteria = session.createCriteria(Product.class);
        productCriteria.add(Restrictions.eq("productGroup", currentProductGroup));
        productCriteria.setProjection(Projections.count("globalId"));
        return (Long) productCriteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public static List<GoodGroup> findGoodGroup(Session session, ConfigurationProvider provider, String nameFilter, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        Criteria goodGroupCriteria = session.createCriteria(GoodGroup.class);
        if(provider!=null){
            goodGroupCriteria.add(Restrictions.eq("idOfConfigurationProvider", provider.getIdOfConfigurationProvider()));
        }
        if(!StringUtils.isEmpty(nameFilter)){
            goodGroupCriteria.add(Restrictions.ilike("nameOfGoodsGroup", nameFilter, MatchMode.ANYWHERE));
        }
        if(orgOwners!=null && !orgOwners.isEmpty()){
            goodGroupCriteria.add(Restrictions.in("orgOwner", orgOwners));
        }
        if(deletedStatusSelected!=null && !deletedStatusSelected){
            goodGroupCriteria.add(Restrictions.eq("deletedState",false));
        }
        return goodGroupCriteria.list();
    }

    @SuppressWarnings("unchecked")
    public static List<Product> findProduct(Session session, ProductGroup productGroup, ConfigurationProvider provider, String nameFilter, List<Long> orgOwners,
            Boolean deletedStatusSelected) {
        Criteria productCriteria = session.createCriteria(Product.class);
        if(productGroup!=null){
            productCriteria.add(Restrictions.eq("productGroup", productGroup));
        }
        if(provider!=null){
            productCriteria.add(Restrictions.eq("idOfConfigurationProvider", provider.getIdOfConfigurationProvider()));
        }
        if(!StringUtils.isEmpty(nameFilter)){
            productCriteria.add(Restrictions.ilike("productName", nameFilter, MatchMode.ANYWHERE));
        }
        if(!(orgOwners==null || orgOwners.isEmpty())){
            productCriteria.add(Restrictions.in("orgOwner", orgOwners));
        }
        if(deletedStatusSelected!=null && !deletedStatusSelected){
            productCriteria.add(Restrictions.eq("deletedState",false));
        }
        return productCriteria.list();
    }
    
    public static List<String> getDiscountRuleSubcategories(Session session) {
        org.hibernate.Query q = session.createSQLQuery(
                "select distinct subcategory from cf_discountrules where subcategory<>''");
        return (List<String>)q.list();
    }

    /**
     * Получение настроек по отчтеам пользователя
     * @param persistenceSession ссылка на сессию с бд
     * @param currentUser ссылка на текущего пользователя
     * @param numberOfReport тип отчтеа отпределен в классе UserReportSetting как контстанты
     * @return возвращается настройки если есть, либо пустой объект
     * @throws Exception
     */
    public static Properties extractPropertiesByUserReportSetting(Session persistenceSession, User currentUser,
            Integer numberOfReport) throws Exception {
        Properties properties = new Properties();
        Criteria criteria = persistenceSession.createCriteria(UserReportSetting.class);
        criteria.add(Restrictions.eq("user", currentUser));
        criteria.add(Restrictions.eq("numberOfReport", numberOfReport));
        Object obj = criteria.uniqueResult();
        if(obj!=null){
            UserReportSetting setting = (UserReportSetting) obj;
            properties.load(new StringReader(setting.getSettings()));
        }
        return properties;
    }

    /**
     * Создание или обновление параметров отчета
     * @param persistenceSession ссылка на сессию бд
     * @param currentUser ссылка на текущего пользователя
     * @param numberOfReport тип отчтеа отпределен в классе UserReportSetting как контстанты
     * @param properties настройки отчета
     */
    public static void saveReportSettings(Session persistenceSession, User currentUser,
            Integer numberOfReport, Properties properties) {
        Criteria criteria = persistenceSession.createCriteria(UserReportSetting.class);
        criteria.add(Restrictions.eq("user", currentUser));
        criteria.add(Restrictions.eq("numberOfReport", numberOfReport));
        Object obj = criteria.uniqueResult();
        StringWriter writer = new StringWriter();
        properties.list(new PrintWriter(writer));
        if(obj==null){
            UserReportSetting reportSetting = new UserReportSetting();
            reportSetting.setUser(currentUser);
            reportSetting.setNumberOfReport(numberOfReport);
            reportSetting.setSettings(writer.getBuffer().toString());
            persistenceSession.save(reportSetting);
        } else {
            UserReportSetting reportSetting = (UserReportSetting) obj;
            reportSetting.setSettings(writer.getBuffer().toString());
            persistenceSession.save(reportSetting);
        }
    }

    public static TechnologicalMapGroup findTechnologicalMapGroupByTechnologicalMap(Session session,
            TechnologicalMap technologicalMap) {
        session.refresh(technologicalMap);
        TechnologicalMapGroup technologicalMapGroup = technologicalMap.getTechnologicalMapGroup();
        technologicalMapGroup.getNameOfGroup();
        return technologicalMapGroup;
    }

    @SuppressWarnings("unchecked")
    public static List<ProhibitionMenu> getProhibitionMenuForOrgSinceVersion(Session session, Org org, long version){
        Criteria criteria = session.createCriteria(ProhibitionMenu.class);
        if(org.getFriendlyOrg().isEmpty()){
            criteria.createCriteria("client").add(Restrictions.eq("org", org));
        } else {
            criteria.createCriteria("client").add(Restrictions.in("org", org.getFriendlyOrg()));
        }
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static long nextVersionByProhibitionsMenu(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select max(proh.version) from cf_Prohibitions as proh");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByClientBalanceHold(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select cbh.version from cf_clientbalance_hold as cbh order by cbh.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByConfigurationProvider(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select max(prov.version) from cf_provider_configurations as prov");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByTaloonApproval(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select t.version from cf_taloon_approval as t order by t.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByComplexSchedule(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select s.version from cf_complex_schedules as s order by s.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByZeroTransaction(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select t.version from cf_zerotransactions as t order by t.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByGroupNameToOrg(Session session) {
        long version = 0L;
        Query query = session.createSQLQuery(
                "SELECT g.version FROM cf_GroupNames_To_Orgs AS g ORDER BY g.version DESC LIMIT 1 FOR UPDATE");
        Object o = query.uniqueResult();
        if (o != null) {
            version = Long.valueOf(o.toString()) + 1;
        }
        return version;
    }

    public static long nextVersionBySpecialDate(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select sd.version from cf_specialdates as sd order by sd.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByMenusCalendar(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select sd.version from cf_menus_calendar as sd order by sd.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByClientPhoto(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select cp.version from cf_clientphoto as cp where cp.version is not null order by cp.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByOrgStucture(Session session){
        long version = 0L;
        Query query = session.createSQLQuery(
                "select o.orgStructureVersion from cf_orgs as o order by o.orgStructureVersion desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByClientgroupManager(Session session) {
        long version = 0L;
        Criteria criteria = session.createCriteria(ClientGroupManager.class);
        criteria.setProjection(Projections.max("version"));
        Object result = criteria.uniqueResult();
        if (result != null) {
            Long currentMaxVersion = (Long) result;
            version = currentMaxVersion + 1;
        }
        return version;
    }

    public static long nextVersionByInfoMessage(Session session){
        long version = 0L;
        Query query = session.createSQLQuery(
                "select m.version from cf_info_messages as m order by m.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByCardRequest(Session session){
        long version = 0L;
        Query query = session.createSQLQuery(
                "select m.version from cf_card_requests as m order by m.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static boolean cardRequestExists(Session session, Client client) {
        //ищем актуальные заявки по клиенту (не удаленные, и по которым не была выдана карта)
        Query query = session.createQuery("select cr.idOfCardRequest from CardRequest cr "
                + "where cr.client.idOfClient = :client and cr.deletedState = false and cr.cardIssueDate is null");
        query.setParameter("client", client.getIdOfClient());
        List res = query.list();
        return res.size() > 0;
    }

    public static String getCardRequestString(Session session, Client client) {
        Query query = session.createQuery("select cr from CardRequest cr where cr.client.idOfClient = :client and cr.deletedState = false order by cr.createdDate desc");
        query.setMaxResults(1);
        query.setParameter("client", client.getIdOfClient());
        CardRequest cardRequest = null;
        String result = "";
        try {
            cardRequest = (CardRequest)query.uniqueResult();
            if (cardRequest != null) {
                result = "Заказ карты";
                if (cardRequest.getCardIssueDate() != null) {
                    result += ". " + String.format("Карта выдана %s", CalendarUtils.dateShortToString(cardRequest.getCardIssueDate()));
                }
            }
        } catch (Exception notFound) {}
        return result;
    }

    public static void disableCardRequest(Session session, Long idOfClient) {
        Query query = session.createQuery("select cr from CardRequest cr where cr.client.idOfClient = :client and cr.deletedState = false order by cr.createdDate desc");
        query.setMaxResults(1);
        query.setParameter("client", idOfClient);
        try {
            CardRequest cardRequest = (CardRequest)query.uniqueResult();
            Long nextVersion = nextVersionByCardRequest(session);
            cardRequest.setDeletedState(true);
            cardRequest.setVersion(nextVersion);
            session.update(cardRequest);
        } catch (Exception notFound) {}
    }

    public static long nextVersionByExternalEvent(Session session){
        long version = 0L;
        Query query = session.createSQLQuery(
                "select e.version from cf_externalevents as e order by e.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static long nextVersionByPreorderComplex(Session session){
        long version = 0L;
        Query query = session.createSQLQuery(
                "select e.version from cf_preorder_complex as e order by e.version desc limit 1");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static List<TaloonApproval> getTaloonApprovalForOrgSinceVersion(Session session, Long idOfOrg, long version) throws Exception {
        //Org org = (Org)session.load(Org.class, idOfOrg);
        List<Org> orgs = findAllFriendlyOrgs(session, idOfOrg);
        Criteria criteria = session.createCriteria(TaloonApproval.class);
        criteria.add(Restrictions.in("org", orgs));
        criteria.add(Restrictions.gt("version", version));
        //criteria.add(Restrictions.eq("deletedState", false));
        return criteria.list();
    }

    public static List<ComplexSchedule> getComplexSchedulesForOrgSinceVersion(Session session, Long idOfOrg, long version) throws Exception {
        List<Long> orgIds = findFriendlyOrgIds(session, idOfOrg);
        Criteria criteria = session.createCriteria(ComplexSchedule.class);
        criteria.add(Restrictions.in("idOfOrg", orgIds));
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static List<GroupNamesToOrgs> getGroupNamesToOrgsForOrgSinceVersion(Session session,Long idOfOrg,long version) throws Exception {
        Query query = session
                .createQuery("select g from GroupNamesToOrgs g where g.idOfMainOrg=:orgId and g.version>:version");
        query.setParameter("orgId", idOfOrg);
        query.setParameter("version", version);
        return query.list();
    }

    public static List<ZeroTransaction> getZeroTransactionsForOrgSinceVersion(Session session, Long idOfOrg, long version) throws Exception {
        Org org = (Org)session.load(Org.class, idOfOrg);
        Criteria criteria = session.createCriteria(ZeroTransaction.class);
        criteria.add(Restrictions.eq("org", org));
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static List<CardRequest> getCardRequestsForOrgSinceVersion(Session session, List<Long> idOfOrgs, long version) throws Exception {
        Query query = session.createQuery("select cr from CardRequest cr join cr.client cl where cl.org.idOfOrg in (:idOfOrgs) and cr.version > :version");
        query.setParameterList("idOfOrgs", idOfOrgs);
        query.setParameter("version", version);
        return query.list();
    }

    public static List<SpecialDate> getSpecialDatesForOrgSinceVersion(Session session, Long idOfOrg, long version) throws Exception {
        Org org = (Org)session.load(Org.class, idOfOrg);
        Criteria criteria = session.createCriteria(SpecialDate.class);
        criteria.add(Restrictions.eq("org", org));
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static List<SpecialDate> getSpecialDatesForFriendlyOrgsSinceVersion(Session session, Long idOfOrg, long version) throws Exception {
        List<Org> orgs = findAllFriendlyOrgs(session, idOfOrg);
        Criteria criteria = session.createCriteria(SpecialDate.class);
        criteria.add(Restrictions.in("org", orgs));
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static List<ClientPhoto> getClientPhotosForFriendlyOrgsSinceVersion(Session session, Long idOfOrg,
            long version, int limit) throws Exception {
        Org org1 = (Org) session.load(Org.class, idOfOrg);
        DetachedCriteria subCriteria = DetachedCriteria.forClass(Client.class);
        subCriteria.createAlias("org", "o");
        subCriteria.add(Restrictions.in("o.idOfOrg", OrgUtils.getFriendlyOrgIds(org1)));
        subCriteria.setProjection(Property.forName("idOfClient"));
        Criteria criteria = session.createCriteria(ClientPhoto.class);
        criteria.add(Property.forName("idOfClient").in(subCriteria));
        criteria.add(Restrictions.gt("version", version));
        criteria.addOrder(org.hibernate.criterion.Order.asc("version"));
        criteria.setMaxResults(limit);
        return criteria.list();
    }

    public static LastProcessSectionsDates getLastProcessSectionsDate(Session session, Long idOfOrg, SectionType sectionType) throws Exception {
        List<SectionType> sectionTypes = new ArrayList<SectionType>();
        sectionTypes.add(sectionType);
        return getLastProcessSectionsDate(session, idOfOrg, sectionTypes);
    }

    public static LastProcessSectionsDates getLastProcessSectionsDate(Session session, Long idOfOrg, List<SectionType> sectionTypes) throws Exception {
        List<Org> orgs = findAllFriendlyOrgs(session, idOfOrg);
        List<Integer> types = new ArrayList<Integer>();
        for(SectionType sectionType : sectionTypes){
            types.add(sectionType.getType());
        }
        Criteria criteria = session.createCriteria(LastProcessSectionsDates.class);
        criteria.add(Restrictions.in("org", orgs));
        if(!types.isEmpty()){
            criteria.add(Restrictions.in("type", types));
        }
        criteria.addOrder(org.hibernate.criterion.Order.desc("date"));
        return (LastProcessSectionsDates) criteria.list().get(0);
    }

    public static List<Accessory> getAccessories(Session session, Long idOfSourceOrg) {
        String q = "from Accessory a where a.idOfSourceOrg=:idOfSourceOrg";
        org.hibernate.Query query = session.createQuery(q);
        query.setParameter("idOfSourceOrg", idOfSourceOrg);
        return (List<Accessory>)query.list();
    }

    public static OrgInventory getOrgInventory(Session session, Long idOfOrg) {
        String q = "from OrgInventory a where a.idOfOrg=:idOfOrg";
        org.hibernate.Query query = session.createQuery(q);
        query.setParameter("idOfOrg", idOfOrg);
        return (OrgInventory) query.uniqueResult();
    }

    public static List<Long> complementIdOfOrgSet(Session session, List<Long> idOfOrgList) {
        Set<Long> idOfOrgSet = new HashSet<Long>();
        Set<FriendlyOrganizationsInfoModel> organizationsInfoModelSet = OrgUtils.getMainBuildingAndFriendlyOrgsList(
                session, idOfOrgList);
        for (FriendlyOrganizationsInfoModel org: organizationsInfoModelSet) {
            idOfOrgSet.add(org.getIdOfOrg());
            Set<Org> friends = org.getFriendlyOrganizationsSet();
            if (friends != null) {
                for (Org friend: friends) {
                    idOfOrgSet.add(friend.getIdOfOrg());
                }
            }
        }
        List<Long> resultlist = new ArrayList<Long>();
        resultlist.addAll(idOfOrgSet);
        return resultlist;
    }


    public static Org findByAdditionalId(Session session, long additionalIdBuildingId){
        return (Org) session.createQuery("from Org where uniqueAddressId =:additionalIdBuildingId")
                .setParameter("additionalIdBuildingId", additionalIdBuildingId)
                .uniqueResult();
    }


    public static Org findByBtiUnom(Session session, long btiUnom){
        /*return (Org) session.createQuery("from Org where btiUnom =:btiUnom")
                .setParameter("btiUnom", btiUnom)
                .uniqueResult();*/
        Query query = session.createQuery(
                "from Org where btiUnom =:btiUnom");
        query.setParameter("btiUnom", btiUnom);
        List res = query.list();
        if(res != null && res.size() > 0) {
            return (Org) res.get(0);
        }
        return null;
    }

    public static List<InteractiveReportDataItem> getInteractiveReportDatas(Session session, Long idOfOrg) {
        Query query = session.createSQLQuery(
                "SELECT cfi.idofrecord, cfi.value FROM cf_interactive_report_data cfi  WHERE cfi.idoforg =:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);

        List res = query.list();

        if (res != null && res.size() > 0) {
            List<InteractiveReportDataItem> itemList = new ArrayList<InteractiveReportDataItem>();
            for (Object o : res) {
                Object[] result = (Object[]) o;
                InteractiveReportDataItem inter = new InteractiveReportDataItem((String) result[1],
                        ((BigInteger) result[0]).longValue());
                itemList.add(inter);
            }
            return itemList;
        }
        return null;
    }

    public static InteractiveReportDataEntity findInteractiveDataReport(Session session,
            CompositeIdOfInteractiveReportData compositeIdOfInteractiveReportData) {
        return (InteractiveReportDataEntity) session
                .get(InteractiveReportDataEntity.class, compositeIdOfInteractiveReportData);
    }

    public static void deleteInteractiveReportDataEntity(Session session,
            InteractiveReportDataEntity interactiveReportDataEntity) {
        session.delete(interactiveReportDataEntity);
        session.flush();
    }

    public static List<GroupNamesToOrgs> getAllGroupnamesToOrgsByIdOfOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(GroupNamesToOrgs.class);
        criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("idOfMainOrg", idOfOrg));

        List<GroupNamesToOrgs> result = criteria.list();

        return result;
    }

    public static List<GroupNamesToOrgs> getAllGroupnamesToOrgsByIdOfMainOrg(Session session, Long idOfMainOrg) {
        Criteria criteria = session.createCriteria(GroupNamesToOrgs.class);
        criteria.add(Restrictions.eq("idOfMainOrg", idOfMainOrg));

        List<GroupNamesToOrgs> result = criteria.list();

        return result;
    }

    public static void setMainBuildingGroupnamesToOrgs(Session session, Long idOfOrg) {
        List<GroupNamesToOrgs> groupNamesToOrgsList = getAllGroupnamesToOrgsByIdOfOrg(session, idOfOrg);

        if (!groupNamesToOrgsList.isEmpty()) {
            for (GroupNamesToOrgs groupNamesToOrg : groupNamesToOrgsList) {
                groupNamesToOrg.setMainBuilding(1);
                groupNamesToOrg.setIdOfMainOrg(idOfOrg);
                groupNamesToOrg.setVersion(nextVersionByGroupNameToOrg(session));
                session.update(groupNamesToOrg);
            }
        }
    }

    public static void setMainBuildingGroupnamesToOrgsByIdOfOrg(Session session, Long idOfOrg, Long idOfMainOrg) {
        List<GroupNamesToOrgs> groupNamesToOrgsList = getAllGroupnamesToOrgsByIdOfMainOrg(session, idOfOrg);

        if (!groupNamesToOrgsList.isEmpty()) {
            for (GroupNamesToOrgs groupNamesToOrg : groupNamesToOrgsList) {
                groupNamesToOrg.setMainBuilding(1);
                groupNamesToOrg.setIdOfMainOrg(idOfMainOrg);
                groupNamesToOrg.setVersion(nextVersionByGroupNameToOrg(session));
                session.update(groupNamesToOrg);
                session.flush();
            }
        }
    }

    public static void groupNamesToOrgsMainBuildingUnset(Session session, Long idOfMainOrg) {
        List<GroupNamesToOrgs> groupNamesToOrgsList = getAllGroupnamesToOrgsByIdOfMainOrg(session, idOfMainOrg);

        if (!groupNamesToOrgsList.isEmpty()) {
            for (GroupNamesToOrgs groupNamesToOrg : groupNamesToOrgsList) {
                groupNamesToOrg.setIdOfMainOrg(null);
                groupNamesToOrg.setVersion(nextVersionByGroupNameToOrg(session));
                session.update(groupNamesToOrg);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static GroupNamesToOrgs getAllGroupnamesToOrgsByIdOfMainOrgAndGroupName(Session session, Long idOfOrg,
            String groupName) {

        Org o = (Org) session.load(Org.class, idOfOrg);

        Long idOfMainOrg = null;

        for (Org org1 : o.getFriendlyOrg()) {
            if (org1.isMainBuilding()) {
                idOfMainOrg = org1.getIdOfOrg();
            }
        }

        if (idOfMainOrg == null) {
            idOfMainOrg = o.getIdOfOrg();
        }

        Query query = session.createQuery(" FROM GroupNamesToOrgs WHERE idOfMainOrg = :idOfMainOrg "
                                          + " AND LOWER(REPLACE(groupName, ' ', '')) LIKE REPLACE(:groupName, ' ', '')");
        query.setParameter("idOfMainOrg", idOfMainOrg);
        query.setParameter("groupName", groupName.toLowerCase());
        
        List<GroupNamesToOrgs> list = (List<GroupNamesToOrgs>) query.list();
        GroupNamesToOrgs groupNamesToOrgs = null;

        for (GroupNamesToOrgs namesToOrgs: list) {
            if (namesToOrgs.getIsMiddleGroup() == null || namesToOrgs.getIsMiddleGroup() == false) {
                groupNamesToOrgs = namesToOrgs;
            }
        }

        return groupNamesToOrgs;
    }

    //Промежуточная группа
    public static void deleteByParentGroupName(Session session, String parentGroupName, Long idOfMainOrg) {
        Query q = session.createQuery(
                "DELETE FROM GroupNamesToOrgs WHERE parentGroupName =:parentGroupName AND idOfMainOrg =:idOfMainOrg AND isMiddleGroup = true");
        q.setParameter("parentGroupName", parentGroupName);
        q.setParameter("idOfMainOrg", idOfMainOrg);
        q.executeUpdate();
    }

    //Конфигурация провайдера
    public static ConfigurationProvider findConfigurationProviderByName(Session session, String nameOfGood) {
        Criteria criteria = session.createCriteria(ConfigurationProvider.class);
        criteria.add(Restrictions.eq("name", nameOfGood));

        ConfigurationProvider configurationProvider = (ConfigurationProvider) criteria.uniqueResult();

        return configurationProvider;
    }

    public static List<Org> findOrgs(Session session, List<Long> orgIds){
        if(orgIds.size() == 0) {
            return new ArrayList<Org>();
        }
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.in("idOfOrg", orgIds));
        return criteria.list();
    }

    public static List<OrgFile> findOrgFiles(Session session, List<Org> orgs){
        if(orgs == null || orgs.isEmpty()) {
            return new ArrayList<OrgFile>();
        }
        Criteria criteria = session.createCriteria(OrgFile.class);
        criteria.add(Restrictions.in("orgOwner", orgs));
        List<OrgFile> result = criteria.list();
        return result != null ? result : new ArrayList<OrgFile>();
    }

    public static List<OrgFile> getOrgFilesForFriendlyOrgs(Session session, Long idOfOrg,
            List<OrgFilesItem> idsOfOrgFile) throws Exception {
        Org org1 = (Org) session.load(Org.class, idOfOrg);
        Criteria criteria = session.createCriteria(OrgFile.class);
        criteria.add(Property.forName("orgOwner").in(org1.getFriendlyOrg()));
        if (null != idsOfOrgFile && !idsOfOrgFile.isEmpty()) {
            Junction conditionGroup = Restrictions.disjunction();
            for (OrgFilesItem i : idsOfOrgFile) {
                Org org2 = (Org) session.load(Org.class, i.getIdOfOrg());
                conditionGroup.add(Restrictions.and(Restrictions.eq("idOfOrgFile", i.getIdOfOrgFile()),
                        Restrictions.eq("orgOwner", org2)));
            }
            criteria.add(conditionGroup);
        }
        return criteria.list();
    }

    public static long nextVersionByHelpRequests(Session session){
        long version = 0L;
        Query query = session.createSQLQuery("select r.version from cf_helprequests as r order by r.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o!=null){
            version = Long.valueOf(o.toString())+1;
        }
        return version;
    }

    public static List<HelpRequest> getHelpRequestsForOrgSinceVersion(Session session, Long idOfOrg, long version) throws Exception {
        Criteria criteria = session.createCriteria(HelpRequest.class);
        Org org = (Org) session.load(Org.class, idOfOrg);
        criteria.add(Restrictions.eq("org", org));
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static HelpRequest getHelpRequestForOrgByGuid(Session session, Long idOfOrg, String guid) throws Exception {
        Criteria criteria = session.createCriteria(HelpRequest.class);
        Org org = (Org) session.load(Org.class, idOfOrg);
        criteria.add(Restrictions.eq("org", org));
        criteria.add(Restrictions.eq("guid", guid));
        return (HelpRequest) criteria.uniqueResult();
    }

    public static List<Org> getOrgByInnAndUnom(Session session, String inn, Integer unom) {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("INN", inn));
        criteria.add(Restrictions.eq("btiUnom", Long.valueOf(unom)));
        return criteria.list();
    }

    public static List<PreorderComplex> getPreorderComplexForOrgSinceVersion(Session session,
            long orgOwner, long version) throws Exception {
        Query query = session.createQuery("select pc from PreorderComplex pc "
                + "where pc.version > :version and (pc.idOfOrgOnCreate = :idOfOrg or (pc.idOfOrgOnCreate is null and pc.client.org.idOfOrg = :idOfOrg))");
        query.setParameter("version", version);
        query.setParameter("idOfOrg", orgOwner);
        return query.list();
    }

    public static List<PreorderMenuDetail> getPreorderMenuDetailByPreorderComplex(Session session,
            PreorderComplex complex) throws Exception {
        Criteria criteria = session.createCriteria(PreorderMenuDetail.class);
        criteria.add(Restrictions.eq("preorderComplex.idOfPreorderComplex", complex.getIdOfPreorderComplex()));
        return criteria.list();
    }

    public static ComplexInfo getComplexInfoByPreorderComplex(Session session, PreorderComplex preorderComplex) {
        Criteria criteria = session.createCriteria(ComplexInfo.class);
        criteria.add(Restrictions.eq("org.idOfOrg", preorderComplex.getClient().getOrg().getIdOfOrg()));
        criteria.add(Restrictions.eq("menuDate", preorderComplex.getPreorderDate()));
        criteria.add(Restrictions.eq("idOfComplex", preorderComplex.getArmComplexId()));
        ComplexInfo complexInfo = (ComplexInfo)criteria.uniqueResult();
        return complexInfo;
    }

    public static String getPreorderComplexName(Session session, PreorderComplex preorderComplex) {
        ComplexInfo ci =  getComplexInfoByPreorderComplex(session, preorderComplex);
        return ci == null ? "" : ci.getComplexName();
    }

    public static MenuDetail getPreorderMenuDetail(Session session, PreorderMenuDetail preorderMenuDetail) {
        Criteria criteria = session.createCriteria(MenuDetail.class);
        criteria.createAlias("menu", "m");
        criteria.add(Restrictions.eq("m.org.idOfOrg", preorderMenuDetail.getClient().getOrg().getIdOfOrg()));
        criteria.add(Restrictions.eq("m.menuDate", preorderMenuDetail.getPreorderDate()));
        criteria.add(Restrictions.eq("localIdOfMenu", preorderMenuDetail.getArmIdOfMenu()));
        MenuDetail menuDetail = (MenuDetail)criteria.uniqueResult();
        return menuDetail;
    }


    public static String getPreorderMenuDetailName(Session session, PreorderMenuDetail preorderMenuDetail) {
        MenuDetail menuDetail = getPreorderMenuDetail(session, preorderMenuDetail);
        return menuDetail == null ? "" : menuDetail.getMenuDetailName();
    }

    public static Integer getPreorderFeedingForbiddenDays(Long idOfOrg) {
        Integer forbiddenDays = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria criteria = persistenceSession.createCriteria(ECafeSettings.class);
            criteria.add(Restrictions.eq("orgOwner", idOfOrg));
            criteria.add(Restrictions.eq("settingsId", SettingsIds.PreOrderFeeding));
            criteria.add(Restrictions.eq("deletedState", false));
            List list = criteria.list();
            if (list == null || list.isEmpty()) {
                //logger.error("Отсутствуют настройки предзаказанного питания для организации с id=" + idOfOrg);
                return PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
            }
            if (list.size() > 1) {
                logger.error("Организация имеет более одной настройки id OO=" + idOfOrg);
                return PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
            }
            ECafeSettings settings = (ECafeSettings) list.get(0);
            PreOrderFeedingSettingValue parser = (PreOrderFeedingSettingValue) settings.getSplitSettingValue();
            forbiddenDays = parser.getForbiddenDaysCount();

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(String.format("Can't get preorders feeding forbidden days value. IdOfOrg = %d", idOfOrg), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        if (forbiddenDays == null) {
            forbiddenDays = PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
        }
        return forbiddenDays;
    }

    public static List<MenusCalendar> getMenusCalendarForOrgSinceVersion(Session session, Long version, Long idOfOrg) {
        try {
            Criteria criteria = session.createCriteria(MenuExchangeRule.class);
            criteria.add(Restrictions.eq("idOfDestOrg", idOfOrg));
            MenuExchangeRule menuExchangeRule = (MenuExchangeRule) criteria.uniqueResult();
            if (menuExchangeRule == null)
                return new ArrayList<MenusCalendar>();

            Query query = session.createQuery(
                    "select mc from MenusCalendar mc where mc.version > :version and mc.org.idOfOrg = :idOfOrg");
            query.setParameter("version", version);
            query.setParameter("idOfOrg", menuExchangeRule.getIdOfSourceOrg());
            return query.list();
        } catch (NonUniqueResultException ne) {
            return new ArrayList<MenusCalendar>();
        }
    }

    public static ClientDtisznDiscountInfo getDTISZNDiscountInfoByClientAndCode(Session session, Client client, Long code) {
        Criteria criteria = session.createCriteria(ClientDtisznDiscountInfo.class);
        criteria.add(Restrictions.eq("client", client));
        criteria.add(Restrictions.eq("dtisznCode", code));
        return (ClientDtisznDiscountInfo) criteria.uniqueResult();
    }

    public static List<ClientDtisznDiscountInfo> getDTISZNDiscountsInfoByClient(Session session, Client client) {
        Criteria criteria = session.createCriteria(ClientDtisznDiscountInfo.class);
        criteria.add(Restrictions.eq("client", client));
        return criteria.list();
    }

    public static List<MenusCalendarDate> getMenusCalendarDateItems(Session session, Long idOfMenusCalendar) {
        Criteria criteria = session.createCriteria(MenusCalendarDate.class);
        criteria.add(Restrictions.eq("menusCalendar.idOfMenusCalendar", idOfMenusCalendar));
        return criteria.list();
    }

    public static MenusCalendar getMenusCalendarForOrgByGuid(Session session, Long idOfOrg, String guid) throws Exception {
        Criteria criteria = session.createCriteria(MenusCalendar.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("guid", guid));
        return (MenusCalendar) criteria.uniqueResult();
    }

    public static MenusCalendarDate getMenusCalendarDate(Session session, Long idOfMenusCalendar, Date date) {
        Criteria criteria = session.createCriteria(MenusCalendarDate.class);
        criteria.add(Restrictions.eq("menusCalendar.idOfMenusCalendar", idOfMenusCalendar));
        criteria.add(Restrictions.eq("date", date));
        return (MenusCalendarDate)criteria.uniqueResult();
    }

    public static Integer getPreorderFeedingForbiddenDays(Client client) {
        if (client == null) {
            return PreorderComplex.DEFAULT_FORBIDDEN_DAYS;
        }
        return getPreorderFeedingForbiddenDays(client.getOrg().getIdOfOrg());
    }

    public static Card getLastCardByClient(Session persistenceSession, Client client){
        Criteria criteria = persistenceSession.createCriteria(Card.class);
        criteria.add(Restrictions.eq("client", client)).addOrder(org.hibernate.criterion.Order.desc("createTime"));
        criteria.setMaxResults(1);
        return (Card) criteria.uniqueResult();
    }

    public static List<Card> getAllCardByClient(Session session, Client client) {
        Criteria criteria = session.createCriteria(Card.class);
        criteria.add(Restrictions.eq("client", client)).addOrder(org.hibernate.criterion.Order.desc("createTime"));
        return criteria.list();
    }

    public static List<User> getUsersByIds(Session session, List<Long> idOfUserList) {
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.in("idOfUser", idOfUserList));
        criteria.addOrder(org.hibernate.criterion.Order.asc("idOfUser"));
        return criteria.list();
    }

    public static List<String> getAllDistinctDepartments(Session session){
        Query sql = session.createSQLQuery("select distinct department from cf_users where department is not null and department not like ''");
        return sql.list();
    }

    public static Staff getAdminStaffFromOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(Staff.class);
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        criteria.add(Restrictions.eq("idOfRole", Staff.Roles.ADMIN.getIdOfRole()));
        List list = criteria.list();
        if (null != list && !list.isEmpty())
            return (Staff) list.get(0);
        return null;
    }

    public static Long getNextGoodRequestNumberForOrgPerDay(Session session, Long idOfOrg, Date date) {
        Date startDate = CalendarUtils.truncateToDayOfMonth(date);
        Date endDate = CalendarUtils.addSeconds(CalendarUtils.addOneDay(startDate), -1);
        Query query = session.createQuery("select count(*) from GoodRequest where orgOwner=:orgOwner and createdDate between :startDate and :endDate");
        query.setParameter("orgOwner", idOfOrg);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return (Long)query.uniqueResult() + 1L;
    }

    public static List<GoodRequestPosition> getGoodRequestPositionsFromGoodRequest(Session session, GoodRequest request) {
        Criteria criteria = session.createCriteria(GoodRequestPosition.class);
        criteria.add(Restrictions.eq("goodRequest", request));
        return criteria.list();
    }

    public static GoodRequest findGoodRequestByPreorderInfo(Session session, Long idOfOrg, Date createdDate) {
        Criteria criteria = session.createCriteria(GoodRequest.class);
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        criteria.add(Restrictions.eq("doneDate", createdDate));
        List list = criteria.list();
        if (!list.isEmpty())
            return (GoodRequest) list.get(0);
        return null;
    }

    public static OrgSync getOrgSyncForOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(OrgSync.class);
        criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        return (OrgSync) criteria.uniqueResult();
    }

    public static List<GoodRequest> getPreorderGoodRequestsByDate(Session session, Date startDate, Date endDate) {
        Criteria criteria = session.createCriteria(GoodRequest.class);
        criteria.add(Restrictions.eq("comment", PREORDER_COMMENT));
        criteria.add(Restrictions.between("doneDate", startDate, endDate));
        return criteria.list();
    }

    public static List<Object[]> getNumberAllUsersInOrg(Session session, String ogrn, Date eventDate)throws Exception{
        try {
            Long idoforg = getOrgByOGRN(session, ogrn);

            List<BigInteger> noValidClients = new ArrayList<BigInteger>();
            noValidClients.add(BigInteger.valueOf(ClientGroup.Predefined.CLIENT_LEAVING.getValue()));
            noValidClients.add(BigInteger.valueOf(ClientGroup.Predefined.CLIENT_DELETED.getValue()));

            List<Integer> enterPassDirection = new ArrayList<Integer>();
            enterPassDirection.add(EnterEvent.ENTRY);
            enterPassDirection.add(EnterEvent.RE_ENTRY);
            enterPassDirection.add(EnterEvent.DETECTED_INSIDE);
            enterPassDirection.add(EnterEvent.CHECKED_BY_TEACHER_EXT);
            enterPassDirection.add(EnterEvent.CHECKED_BY_TEACHER_INT);

            List<BigInteger> idOfActiveDetectedClients = new LinkedList<BigInteger>();

            List<Object[]> latestEvents = getDetectedClientnFromBeginningDayOfOrg(session, idoforg, eventDate);
            if (latestEvents == null || latestEvents.isEmpty()) {
                throw new Exception("No detect users in Org ID: " + idoforg + " ORGN: " + ogrn);
            }

            for (Object[] row : latestEvents) {
                BigInteger idofDetectClient = (BigInteger) row[0];
                Integer passDirection = (Integer) row[1];
                if (!enterPassDirection.contains(passDirection)) {
                    continue;
                }
                idOfActiveDetectedClients.add(idofDetectClient);
            }

            if (idOfActiveDetectedClients.isEmpty()) {
                throw new Exception("No detect users in Org ID: " + idoforg + " ORGN: " + ogrn);
            }

            Query query = session.createSQLQuery(
                    "select gr.groupname, count(distinct cl.idofclient) "
                            + " from cf_clientgroups gr "
                            + " left join cf_clients cl on gr.idofclientgroup = cl.idofclientgroup and gr.idoforg = cl.idoforg "
                            + " where cl.idofclient in (:listOfIdClients) "
                            + " and gr.idofclientgroup not in (:noValidClients) "
                            + " group by gr.groupname "
                            + " order by gr.groupname ");
            query.setParameterList("listOfIdClients", idOfActiveDetectedClients);
            query.setParameterList("noValidClients", noValidClients);
            List result = query.list();
            if (result == null) {
                throw new Exception("Query return empty list for Org ID " + idoforg + " OGRN: " + ogrn);
            }
            return result;
        } catch (IllegalArgumentException e){
            throw e;
        } catch (Exception e){
            logger.error("Can't find users in Org: " + e.getMessage());
            return new ArrayList<Object[]>();
        }
    }

    private static List<Object[]> getDetectedClientnFromBeginningDayOfOrg(Session session, Long org, Date eventDate) {
        Query query = session.createSQLQuery("select ee.idofclient, ee.PassDirection from cf_EnterEvents ee "
                + " join ( select e.idofclient, max(e.evtDateTime) as evtDateTime from cf_EnterEvents e where e.evtDateTime between :startDate and :eventDate and e.idoforg = :idoforg group by e.idofclient ) q "
                + " on ee.idofclient = q.idofclient and ee.evtDateTime = q.evtDateTime ");
        query.setParameter("idoforg", org);
        query.setParameter("startDate", CalendarUtils.startOfDay(eventDate).getTime());
        query.setParameter("eventDate", eventDate.getTime());
        return query.list();
    }


    private static Long getOrgByOGRN(Session session, String ogrn) {
        if(ogrn == null || ogrn.isEmpty()){
            throw new IllegalArgumentException("Not correct OGRN");
        }
        Query query = session.createSQLQuery("select idoforg from cf_orgs "
                                        + " where ogrn like :OGRN ");
        query.setParameter("OGRN", ogrn);
        query.setMaxResults(1);
        BigInteger idoforg = (BigInteger) query.uniqueResult();
        if(idoforg == null){
            throw new IllegalArgumentException("No find Org by OGRN " + ogrn);
        }
        return idoforg.longValue();
    }

	public static Long getAllPreordersPriceByClient(Session session, Long idOfClient, Date startDate, Date endDate,
            Long idOfPreorderComplex, Long idOfPreorderMenudetail) {
        String complexCondition = "", menudetailCondition = "";
        if (null != idOfPreorderComplex) {
            complexCondition = " AND pc.idofpreordercomplex <> :idOfPreorderComplex ";
        }
        if (null != idOfPreorderMenudetail) {
            menudetailCondition = " AND pmd.idofpreordermenudetail <> :idOfPreorderMenudetail ";
        }
        Query query = session.createSQLQuery(
             "SELECT sum(a.totalprice) AS totalprice "
              + "FROM ( "
              + "   SELECT sum(pc.complexprice * pc.amount) AS totalprice "
              + "   FROM cf_preorder_complex pc "
              + "   WHERE pc.amount > 0 AND coalesce(pc.deletedstate=0,false) AND pc.idofclient = :idOfClient AND "
              + "       pc.preorderdate BETWEEN :startDate AND :endDate "
              + complexCondition
              + "   UNION ALL "
              + "   SELECT sum(pmd.menudetailprice * pmd.amount) AS totalprice "
              + "   FROM cf_preorder_menudetail pmd "
              + "   WHERE pmd.amount > 0 AND coalesce(pmd.deletedstate=0,false) AND pmd.idofclient = :idOfClient AND "
              + "       pmd.preorderdate BETWEEN :startDate AND :endDate "
              + menudetailCondition
              + ") a");

        query.setParameter("idOfClient", idOfClient);
        query.setParameter("startDate", startDate.getTime());
        query.setParameter("endDate", endDate.getTime());

        if (!complexCondition.isEmpty()) {
            query.setParameter("idOfPreorderComplex", idOfPreorderComplex);
        }

        if (!menudetailCondition.isEmpty()) {
            query.setParameter("idOfPreorderMenudetail", idOfPreorderMenudetail);
        }

        Object value = query.uniqueResult();

        return (null == value) ? 0 : ((BigDecimal) value).longValue();
    }

    public static void createGroupNamesToOrg(Session session, Org org, Long version, String groupName) {
        Long idOfMainOrg = null;
        try{
            for (Org friendlyOrg : org.getFriendlyOrg()) {
                if (friendlyOrg.isMainBuilding()) {
                    idOfMainOrg = friendlyOrg.getIdOfOrg();
                }
            }
            if(idOfMainOrg == null){
                idOfMainOrg = org.getIdOfOrg();
            }
            GroupNamesToOrgs groupNamesToOrgs = new GroupNamesToOrgs();
            groupNamesToOrgs.setIdOfOrg(org.getIdOfOrg());
            groupNamesToOrgs.setMainBuilding(1);
            groupNamesToOrgs.setGroupName(groupName);
            groupNamesToOrgs.setIdOfMainOrg(idOfMainOrg);
            groupNamesToOrgs.setVersion(version);
            session.save(groupNamesToOrgs);
        } catch (Exception e) {
            logger.error("Save GroupNamesToOrgs to database error:" + e.getMessage());
        }
    }

    public static void createEnterEventsSendInfo(EnterEvent enterEvent, Session session) {
        try {
            if(!EnterEventSendInfo.VALID_ENTER_CODES.contains(enterEvent.getPassDirection()) &&
                    !EnterEventSendInfo.VALID_EXIT_CODES.contains(enterEvent.getPassDirection())){
                logger.debug("EnterEvent record EVT Time: " + enterEvent.getEvtDateTime()
                        + " ORG: " + enterEvent.getOrg().getIdOfOrg()
                        + " have PassDirection: " + enterEvent.getPassDirection()
                );
                return;
            }
            Card card = enterEvent.getIdOfCard() == null ? null : findCardByCardNo(session, enterEvent.getIdOfCard());
            EnterEventSendInfo enterEventSendInfo = new EnterEventSendInfo();
            enterEventSendInfo.setCompositeIdOfEnterEventSendInfo(
                    new CompositeIdOfEnterEventSendInfo(
                            enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent(),
                            enterEvent.getCompositeIdOfEnterEvent().getIdOfOrg()
                            )
            );
            enterEventSendInfo.setClient(enterEvent.getClient());
            enterEventSendInfo.setCard(card);
            enterEventSendInfo.setDirectionType(enterEvent.getPassDirection());
            enterEventSendInfo.setEvtDateTime(enterEvent.getEvtDateTime());
            enterEventSendInfo.setOrg(enterEvent.getOrg());
            enterEventSendInfo.setSendToExternal(false);
            enterEventSendInfo.setResponseCode(false);
            enterEventSendInfo.setEnterEvent(enterEvent);
            session.save(enterEventSendInfo);
        } catch (Exception e){
            logger.error("Save EnterEventSendInfo to database error: ", e);
        }
    }

    public static String findClientGUIDByCardNo(Session session, Long cardNo) {
        try {
            Query query = session.createSQLQuery(
                            " select c.clientguid from cf_clients c "
                            + " join cf_cards crd on crd.idofclient = c.idofclient "
                            + " where crd.cardno = :cardNo ");
            query.setParameter("cardNo", cardNo);
            query.setMaxResults(1);
            return (String) query.uniqueResult();
        } catch (Exception e){
            logger.error("Can't find client GUID by CardNo: " + e.getMessage());
            return null;
        }
    }

    public static Long findCardNoByClientGUID(Session session, String GUID) {
        try {
            Query query = session.createSQLQuery(
                            " select crd.cardno from cf_cards crd "
                            + " join cf_clients c on c.idofclient = crd.idofcard "
                            + " where c.clientguid like :guid and crd.state = 0 "
                            + " order by crd.createddate desc ");
            query.setParameter("guid", GUID);
            query.setMaxResults(1);
            BigInteger buffer = (BigInteger) query.uniqueResult();
            if(buffer == null) {
                return null;
            }
            return  buffer.longValue();
        } catch (Exception e){
            logger.error("Can't find CardNo by client GUID: " + e.getMessage());
            return  null;
        }
    }

    public static void updateEnterEventsSendInfo(Session session, Long idofEnterEvent, Long idofOrg,
            Boolean responseCode, Boolean sendToExternal) throws Exception {
        Query query = session.createSQLQuery(
                        " UPDATE cf_EnterEvents_Send_Info "
                         + " SET sendToExternal = :sendToExternal, responseCode = :responseCode "
                         + " WHERE idoforg = :idofOrg and idofenterevent = :idofEnterEvent "
                )
                .setParameter("sendToExternal", sendToExternal? 1:0)
                .setParameter("responseCode", responseCode? 1:0)
                .setParameter("idofOrg", idofOrg)
                .setParameter("idofEnterEvent", idofEnterEvent);
        query.executeUpdate();
    }


    public static LinkingTokenForSmartWatch findLinkingTokenForSmartWatch(Session session, String phone, String token) {
        Criteria criteria = session.createCriteria(LinkingTokenForSmartWatch.class);
        criteria
                .add(Restrictions.like("token", token))
                .add(Restrictions.like("phoneNumber", phone));
        return (LinkingTokenForSmartWatch) criteria.uniqueResult();
    }

    public static List<ClientGuardian> findListOfClientGuardianByIdOfGuardian(Session session, Long idOfClient) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfGuardian", idOfClient));
        criteria.add(Restrictions.eq("deletedState", false));
        criteria.add(Restrictions.eq("disabled", false));
        return criteria.list();
    }

    public static Card findSmartWatchAsCardByCardNoAndCardPrintedNo(Session session, Long trackerIdAsCardPrintedNo, Long trackerUidAsCardNo,
            Integer cardType) {
        Criteria criteria = session.createCriteria(Card.class);
        criteria
                .add(Restrictions.eq("cardNo", trackerUidAsCardNo))
                .add(Restrictions.eq("cardPrintedNo", trackerIdAsCardPrintedNo))
                .add(Restrictions.eq("cardType", cardType));
        return (Card) criteria.uniqueResult();
    }

    public static Long createSmartWatch(Session session, Long idOfCard, Long idOfClient, String model, String color,
            Long trackerUid, Long trackerId, Long trackerActivateUserId, String status, Date trackerActivateTime,
            String simIccid) {
        try {
            SmartWatch watch = new SmartWatch();
            watch.setIdOfCard(idOfCard);
            watch.setIdOfClient(idOfClient);
            watch.setTrackerId(trackerId);
            watch.setTrackerUid(trackerUid);
            watch.setModel(model);
            watch.setColor(color);
            watch.setTrackerActivateUserId(trackerActivateUserId);
            watch.setStatus(status);
            watch.setTrackerActivateTime(trackerActivateTime);
            watch.setSimIccid(simIccid);
            session.save(watch);
            return watch.getIdOfSmartWatch();
        } catch (Exception e){
            logger.error("Can't create SmartWatch Entity: " + e.getMessage());
            return null;
        }
    }

    public static SmartWatch findSmartWatchByTrackerUidAndTrackerId(Session session, Long trackerId, Long trackerUid) throws Exception{
        Criteria criteria = session.createCriteria(SmartWatch.class);
        criteria
                .add(Restrictions.eq("trackerUid", trackerUid))
                .add(Restrictions.eq("trackerId", trackerId));
        return (SmartWatch) criteria.uniqueResult();
    }

    public static Org findOrgByGuid(Session session, String guid) {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.add(Restrictions.eq("guid", guid));
        return (Org) criteria.uniqueResult();
    }

    public static Card findCardByCardNoAndOrg(Session persistenceSession, long cardNo, long idOfOrg) {
        Criteria criteria = persistenceSession.createCriteria(Card.class);
        criteria.add(Restrictions.eq("cardNo", cardNo));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.addOrder(org.hibernate.criterion.Order.desc("updateTime"));
        criteria.setMaxResults(1);
        return (Card) criteria.uniqueResult();
    }

    public static Card findCardByCardNoAndIdOfFriendlyOrg(Session session, Long cardNo, Long idOfOrg){
        Integer state = CardState.ISSUED.getValue();
        Query query = session.createSQLQuery(" select cr.idofcard from cf_cards cr "
                + " left join (select friendlyorg from cf_friendly_organization where currentorg = :friendlyOrg)as q on cr.idoforg = q.friendlyorg "
                + " where state = :lifeState and cr.cardno = :cardNo");
        query
                .setParameter("friendlyOrg", idOfOrg)
                .setParameter("lifeState", state)
                .setParameter("cardNo", cardNo);
        BigInteger idOfCard = (BigInteger) query.uniqueResult();
        return (Card) session.get(Card.class, idOfCard.longValue());
    }

    public static Boolean findClientByCardNoAndHeHaveActiveSW(Session session, Long cardNo, Long idOfOrg){
        Integer state = CardState.ISSUED.getValue();
        try {
            Query query = session.createSQLQuery(" select c.hasactiveSmartWatch from cf_cards cr "
                    + " left join (select friendlyorg from cf_friendly_organization where currentorg = :friendlyOrg)as q on cr.idoforg = q.friendlyorg "
                    + " join cf_clients c on cr.idofclient = c.idofclient "
                    + " where state = :lifeState and cr.cardno = :cardNo ");
            query
                    .setParameter("friendlyOrg", idOfOrg)
                    .setParameter("lifeState", state)
                    .setParameter("cardNo", cardNo);
            Integer hasActiveSmartWatch = (Integer) query.uniqueResult();
            return hasActiveSmartWatch != null && hasActiveSmartWatch.equals(1);
        } catch (Exception e){
            logger.error("", e);
            return false;
        }
    }

    public static List<Client> findClientsByOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(Client.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        return criteria.list();
    }

    public static List<Card> getAllCardByClientAndCardState(Session session, Client client, CardState state) {
        Criteria criteria = session.createCriteria(Card.class);
        criteria.add(Restrictions.eq("client", client));
        criteria.add(Restrictions.eq("state", state.getValue()));
        criteria.addOrder(org.hibernate.criterion.Order.desc("createTime"));
        return criteria.list();
    }

    public static List<String> getDishesByPreorderComplexId(Session session, Long idOfPreorderComplex) {
        Criteria criteria = session.createCriteria(PreorderMenuDetail.class);
        criteria.add(Restrictions.eq("preorderComplex.idOfPreorderComplex", idOfPreorderComplex));
        criteria.addOrder(org.hibernate.criterion.Order.asc("menuDetailName"));
        criteria.setProjection(Projections.projectionList().add(Projections.property("menuDetailName")));
        return criteria.list();
    }

    public static ApplicationForFood getLastApplicationForFoodByClientGuid(Session session, String clientGuid) {
        Criteria criteria = session.createCriteria(ApplicationForFood.class);
        criteria.createAlias("client", "c");
        criteria.add(Restrictions.eq("c.clientGUID", clientGuid));
        criteria.add(Restrictions.eq("archived", Boolean.FALSE));
        criteria.addOrder(org.hibernate.criterion.Order.desc("lastUpdate"));
        criteria.setMaxResults(1);
        return (ApplicationForFood) criteria.uniqueResult();
    }

    public static ApplicationForFood createApplicationForFood(Session session, Client client, Long dtisznCode, String mobile,
            String guardianName, String guardianSecondName, String guardianSurname, String serviceNumber,
            ApplicationForFoodCreatorType creatorType) {
        Long applicationForFoodVersion = nextVersionByApplicationForFood(session);
        Long historyVersion = nextVersionByApplicationForFoodHistory(session);
        return createApplicationForFoodWithVersion(session, client, dtisznCode, mobile, guardianName, guardianSecondName,
                guardianSurname, serviceNumber, creatorType, applicationForFoodVersion, historyVersion);
    }

    public static ApplicationForFood createApplicationForFoodWithVersion(Session session, Client client, Long dtisznCode, String mobile,
            String guardianName, String guardianSecondName, String guardianSurname, String serviceNumber,
            ApplicationForFoodCreatorType creatorType, Long version, Long historyVersion) {
        ApplicationForFood applicationForFood = new ApplicationForFood(client, dtisznCode,
                new ApplicationForFoodStatus(ApplicationForFoodState.TRY_TO_REGISTER, null),
                mobile, guardianName, guardianSecondName, guardianSurname, serviceNumber, creatorType, null,
                null, version);
        session.save(applicationForFood);

        addApplicationForFoodHistoryWithVersion(session, applicationForFood,
                new ApplicationForFoodStatus(ApplicationForFoodState.TRY_TO_REGISTER, null), historyVersion);

        return applicationForFood;
    }

    public static ApplicationForFood updateApplicationForFood(Session session, Client client, ApplicationForFoodStatus status) {
        ApplicationForFood applicationForFood = findActiveApplicationForFoodByClient(session, client);
        if (null == applicationForFood)
            return null;

        Long applicationForFoodVersion = nextVersionByApplicationForFood(session);
        applicationForFood.setStatus(status);
        applicationForFood.setVersion(applicationForFoodVersion);
        applicationForFood.setLastUpdate(new Date());
        session.update(applicationForFood);

        addApplicationForFoodHistory(session, applicationForFood, status);
        return applicationForFood;
    }

    public static void updateApplicationForFoodSendToAISContingentOnly(Session session, ApplicationForFood applicationForFood, Long version) {
        applicationForFood.setSendToAISContingent(true);
        applicationForFood.setVersion(version);
        applicationForFood.setLastUpdate(new Date());
        session.update(applicationForFood);
    }

    public static ApplicationForFood updateApplicationForFoodWithSendToAISContingent(Session session, ApplicationForFood applicationForFood,
            ApplicationForFoodStatus status, Long version, Long historyVersion) throws Exception {
        applicationForFood.setSendToAISContingent(true);
        return updateApplicationForFoodWithVersionHistorySafe(session, applicationForFood, status, version, historyVersion);
    }

    public static ApplicationForFood updateApplicationForFoodWithVersion(Session session, ApplicationForFood applicationForFood,
            ApplicationForFoodStatus status, Long version, Long historyVersion) {
        applicationForFood.setStatus(status);
        applicationForFood.setVersion(version);
        applicationForFood.setLastUpdate(new Date());
        session.update(applicationForFood);

        addApplicationForFoodHistoryWithVersion(session, applicationForFood, status, historyVersion);
        return applicationForFood;
    }

    public static ApplicationForFood updateApplicationForFoodWithVersionHistorySafe(Session session, ApplicationForFood applicationForFood,
            ApplicationForFoodStatus status, Long version, Long historyVersion) throws Exception {
        applicationForFood.setStatus(status);
        applicationForFood.setVersion(version);
        applicationForFood.setLastUpdate(new Date());
        session.update(applicationForFood);

        addApplicationForFoodHistoryWithVersionIfNotExist(session, applicationForFood, status, historyVersion);
        return applicationForFood;
    }

    public static ApplicationForFood  updateApplicationForFoodByServiceNumber(Session persistenceSession, String serviceNumber,
            ApplicationForFoodStatus status) {
        ApplicationForFood applicationForFood = findApplicationForFoodByServiceNumber(persistenceSession, serviceNumber);
        if(applicationForFood == null){
            logger.warn("Can't find ApplicationForFood by serviceNumber = " + serviceNumber);
            return null;
        }
        Long applicationForFoodVersion = nextVersionByApplicationForFood(persistenceSession);
        applicationForFood.setStatus(status);
        applicationForFood.setVersion(applicationForFoodVersion);
        applicationForFood.setLastUpdate(new Date());
        persistenceSession.update(applicationForFood);

        addApplicationForFoodHistoryIfNotExist(persistenceSession, applicationForFood, status);
        return applicationForFood;
    }

    public static void addApplicationForFoodHistory(Session session, ApplicationForFood applicationForFood, ApplicationForFoodStatus status) {
        Long applicationForFoodHistoryVersion = nextVersionByApplicationForFoodHistory(session);
        addApplicationForFoodHistoryWithVersion(session, applicationForFood, status, applicationForFoodHistoryVersion);
    }

    public static void addApplicationForFoodHistoryWithVersion(Session session, ApplicationForFood applicationForFood,
            ApplicationForFoodStatus status, Long version) {
        ApplicationForFoodHistory applicationForFoodHistory = new ApplicationForFoodHistory(applicationForFood,
                status,null, version);
        session.save(applicationForFoodHistory);
    }

    public static void addApplicationForFoodHistoryWithVersionIfNotExist(Session session, ApplicationForFood applicationForFood, ApplicationForFoodStatus status, Long version) throws Exception {
        ApplicationForFoodHistory applicationForFoodHistory;
        applicationForFoodHistory = findApplicationForFoodHistoryByStatusAndapplicationForFood(session, applicationForFood, status);
        if(applicationForFoodHistory != null){
            String errorString = String.format("Exist applicationForFoodHistory state = %d for ApplicationForFood: clientContractID= %d , serviceNumber= %s ",
                    applicationForFoodHistory.getStatus().getApplicationForFoodState().getCode(),
                    applicationForFood.getClient().getContractId(), applicationForFood.getServiceNumber());
            throw new Exception(errorString);
        }
        addApplicationForFoodHistoryWithVersion(session, applicationForFood, status, version);
    }

    public static void addApplicationForFoodHistoryIfNotExist(Session session, ApplicationForFood applicationForFood, ApplicationForFoodStatus status) {
        ApplicationForFoodHistory applicationForFoodHistory = null;
        try {
            applicationForFoodHistory = findApplicationForFoodHistoryByStatusAndapplicationForFood(session, applicationForFood, status);
            if(applicationForFoodHistory != null){
                logger.warn(String.format("Exist applicationForFoodHistory state = %d for ApplicationForFood: clientContractID= %d , serviceNumber= %s, "
                        , applicationForFoodHistory.getStatus().getApplicationForFoodState().getCode(),
                        applicationForFood.getClient().getContractId(), applicationForFood.getServiceNumber()));
                return;
            }
            Long applicationForFoodHistoryVersion = nextVersionByApplicationForFoodHistory(session);
            applicationForFoodHistory = new ApplicationForFoodHistory(applicationForFood,
                    status, null, applicationForFoodHistoryVersion);
            session.save(applicationForFoodHistory);
        } catch (Exception e){
            logger.error("", e);
        }
    }

    private static ApplicationForFoodHistory findApplicationForFoodHistoryByStatusAndapplicationForFood(Session session, ApplicationForFood applicationForFood,
            ApplicationForFoodStatus status) {
        Criteria criteria = session.createCriteria(ApplicationForFoodHistory.class);
        criteria.add(Restrictions.eq("status", status))
                .add(Restrictions.eq("applicationForFood", applicationForFood));
        return (ApplicationForFoodHistory) criteria.uniqueResult();
    }

    public static ApplicationForFood findActiveApplicationForFoodByClient(Session session, Client client) {
        Criteria criteria = session.createCriteria(ApplicationForFood.class);
        criteria.add(Restrictions.eq("client", client));
        criteria.add(Restrictions.ne("status",
                new ApplicationForFoodStatus(ApplicationForFoodState.DENIED, ApplicationForFoodDeclineReason.NO_DOCS)));
        criteria.add(Restrictions.ne("status",
                new ApplicationForFoodStatus(ApplicationForFoodState.DENIED, ApplicationForFoodDeclineReason.NO_APPROVAL)));
        criteria.add(Restrictions.ne("status",
                new ApplicationForFoodStatus(ApplicationForFoodState.DENIED, ApplicationForFoodDeclineReason.INFORMATION_CONFLICT)));
        criteria.add(Restrictions.or(Restrictions.isNull("archived"), Restrictions.eq("archived", false)));
        criteria.setMaxResults(1);
        return (ApplicationForFood) criteria.uniqueResult();
    }

    public static long nextVersionByApplicationForFood(Session session){
        long version = 0L;
        Query query = session
                .createSQLQuery("select apf.version from cf_applications_for_food as apf order by apf.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o != null){
            version = Long.valueOf(o.toString()) + 1;
        }
        return version;
    }

    public static List<ApplicationForFood> getApplicationForFoodListByOrgs(Session session, List<Long> idOfOrgs,
            ApplicationForFoodStatus status, Long benefit, List<Long> idOfClientList, String number) {
        String condition = "where 1=1 ";
        condition += (idOfOrgs.size() == 0 ? "" : "and a.client.org.idOfOrg in :idOfOrgs");
        condition += status == null ? "" : " and a.status = :status";
        condition += benefit == null ? "" : (benefit.equals(0L) ? " and a.dtisznCode is null" : " and a.dtisznCode = :code");
        condition += (idOfClientList.size() == 0) ? "" : " and a.client.idOfClient in :idOfClientList";
        condition += (StringUtils.isEmpty(number)) ? "" : " and a.serviceNumber like :number";
        Query query = session.createQuery("select a from ApplicationForFood a " + condition + " order by a.createdDate, a.serviceNumber");
        if (idOfOrgs.size() > 0) query.setParameterList("idOfOrgs", idOfOrgs);
        if (status != null) query.setParameter("status", status);
        if (benefit != null && benefit > 0L) query.setParameter("code", benefit);
        if (idOfClientList.size() > 0) query.setParameterList("idOfClientList", idOfClientList);
        if (!StringUtils.isEmpty(number)) query.setParameter("number", number.length() == 30 ? number : "%" + ETPMVService.ISPP_ID + getProperNumber(number) + "%");
        return query.list();
    }

    private static String getProperNumber(String number) {
        while (number.length() < 7) number = "0" + number;
        return number;
    }

    public static List<ApplicationForFood> getApplicationForFoodListByClient(Session session, Long idOfClient) {
        Query query = session.createQuery("select a from ApplicationForFood a where a.client.idOfClient = :idOfClient order by a.serviceNumber");
        query.setParameter("idOfClient", idOfClient);
        return query.list();
    }

    public static long nextVersionByApplicationForFoodHistory(Session session){
        long version = 0L;
        Query query = session
                .createSQLQuery("select apfh.version from cf_applications_for_food_history as apfh order by apfh.version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o != null){
            version = Long.valueOf(o.toString()) + 1;
        }
        return version;
    }

    public static List<ApplicationForFoodHistory> getHistoryByApplicationForFood(Session session, ApplicationForFood applicationForFood) {
        Query query = session.createQuery("select h from ApplicationForFoodHistory h where h.applicationForFood = :app order by h.createdDate");
        query.setParameter("app", applicationForFood);
        return query.list();
    }

    public static ApplicationForFood findApplicationForFoodByClientIdAndRegDate(Session session, Long idOfClient, Date regDate) {
        Criteria criteria = session.createCriteria(ApplicationForFood.class);
        criteria.add(Restrictions.eq("client.idOfClient", idOfClient));
        criteria.add(Restrictions.eq("createdDate", regDate));
        return (ApplicationForFood) criteria.uniqueResult();
    }

    public static ApplicationForFood findApplicationForFoodByServiceNumber(Session persistenceSession, String serviceNumber) {
        Criteria criteria = persistenceSession.createCriteria(ApplicationForFood.class);
        criteria.add(Restrictions.like("serviceNumber", serviceNumber));
        return (ApplicationForFood) criteria.uniqueResult();
    }

    public static List<ApplicationForFood> getApplicationsForFoodForOrgsSinceVersion(Session session, List<Long> idOfOrgs,
            long version) throws Exception {
        Criteria criteria = session.createCriteria(ApplicationForFood.class);
        criteria.createAlias("client", "c");
        criteria.createAlias("c.org", "o");
        criteria.add(Restrictions.in("o.idOfOrg", idOfOrgs));
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static Card getLastCreatedActiveCardByClient(Session session, Client client) {
        try {
            Criteria criteria = session.createCriteria(Card.class);
            criteria.add(Restrictions.eq("client", client))
                    .add(Restrictions.eq("state", CardState.ISSUED.getValue()))
                    .addOrder(org.hibernate.criterion.Order.desc("createTime"))
                    .setMaxResults(1);
            return (Card) criteria.uniqueResult();
        } catch (Exception e){
            logger.error("Can't get last active card by client with contractID: " + client.getContractId(), e);
            return  null;
        }
    }

    public static List<ClientDtisznDiscountInfo> getDTISZNDiscountsInfoByClientIdSinceVersion(Session session, Long idOfClient,
            Long version) {
        Criteria criteria = session.createCriteria(ClientDtisznDiscountInfo.class);
        criteria.add(Restrictions.eq("client.idOfClient", idOfClient));
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static List<ClientDtisznDiscountInfo> getDTISZNDiscountInfoByOrgIdSinceVersion(Session session, Long idOfOrg,
            Long version) {
        List<Long> friendlyOrgIds = findFriendlyOrgIds(session, idOfOrg);
        Criteria criteria = session.createCriteria(ClientDtisznDiscountInfo.class);
        criteria.createAlias("client", "c");
        criteria.add(Restrictions.in("c.org.idOfOrg", friendlyOrgIds));
        criteria.add(Restrictions.gt("version", version));
        return criteria.list();
    }

    public static List<ApplicationForFood> getApplicationForFoodListByStatus(Session session, ApplicationForFoodStatus status, Boolean isOthers) {
        Criteria criteria = session.createCriteria(ApplicationForFood.class);
        criteria.add(Restrictions.eq("status", status));
        criteria.add(Restrictions.eq("archived", false));
        if (isOthers)
            criteria.add(Restrictions.isNull("dtisznCode"));
        return criteria.list();
    }

    public static CategoryDiscountDSZN findCategoryDiscountDSZNByETPCode(EntityManager entityManager, Long ETPCode) {
        javax.persistence.Query q = entityManager.createQuery("from CategoryDiscountDSZN where ETPCode=:ETPCode");
        q.setParameter("ETPCode", ETPCode);
        CategoryDiscountDSZN result;
        try {
            result = (CategoryDiscountDSZN) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return result;
    }

    public static List<CategoryDiscountDSZN> getCategoryDiscountDSZNList(Session session, Boolean withEtp) {
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        if (withEtp) {
            criteria.add(Restrictions.isNotNull("ETPCode"));
        }
        criteria.addOrder(org.hibernate.criterion.Order.asc("idOfCategoryDiscountDSZN"));
        return criteria.list();
    }

    public static List<CategoryDiscountDSZN> getCategoryDiscountDSZNForReportList(Session session) {
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        criteria.add(Restrictions.eq("deleted", false));
        criteria.addOrder(org.hibernate.criterion.Order.asc("code"));
        return criteria.list();
    }

    public static ApplicationForFood  updateApplicationForFoodByServiceNumberFullWithVersion(Session persistenceSession, String serviceNumber,
            Client client, Long dtisznCode, ApplicationForFoodStatus status, String mobile, String applicantName, String applicantSecondName,
            String applicantSurname, Long version, Long historyVersion) throws Exception {
        ApplicationForFood applicationForFood = findApplicationForFoodByServiceNumber(persistenceSession, serviceNumber);
        if(applicationForFood == null){
            logger.warn("Can't find ApplicationForFood by serviceNumber = " + serviceNumber);
            return null;
        }
        applicationForFood.setClient(client);
        applicationForFood.setDtisznCode(dtisznCode);
        applicationForFood.setStatus(status);
        applicationForFood.setMobile(mobile);
        applicationForFood.setApplicantName(applicantName);
        applicationForFood.setApplicantSecondName(applicantSecondName);
        applicationForFood.setApplicantSurname(applicantSurname);
        applicationForFood.setVersion(version);
        applicationForFood.setLastUpdate(new Date());
        persistenceSession.update(applicationForFood);

        addApplicationForFoodHistoryWithVersionIfNotExist(persistenceSession, applicationForFood, status, historyVersion);
        return applicationForFood;
    }

    public static long nextVersionByClientDTISZNDiscountInfo(Session session) {
        long version = 0L;
        Query query = session
                .createSQLQuery("select version from cf_client_dtiszn_discount_info order by version desc limit 1 for update");
        Object o = query.uniqueResult();
        if(o != null){
            version = Long.valueOf(o.toString()) + 1;
        }
        return version;
    }

    public static CategoryDiscountDSZN getCategoryDiscountDSZNByDSZNCode(Session session, Long dsznCode) {
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        criteria.add(Restrictions.eq("code", dsznCode.intValue()));
        return (CategoryDiscountDSZN) criteria.uniqueResult();
    }

    public static ECafeSettings getECafeSettingByIdOfOrgAndSettingId(Session session, Long idOfOrg, SettingsIds id) {
        Criteria criteria = session.createCriteria(ECafeSettings.class);
        criteria.add(Restrictions.eq("orgOwner", idOfOrg));
        criteria.add(Restrictions.eq("settingsId", id));
        criteria.add(Restrictions.eq("deletedState", false));
        List list = criteria.list();
        if (list.isEmpty()) {
            return null;
        }
        return (ECafeSettings) list.get(0);
    }

    public static GroupNamesToOrgs getGroupNamesToOrgsByOrgAndGroupName(Session session, Org org, String groupName) {

        Long idOfMainOrg = null;
        for (Org o : org.getFriendlyOrg()) {
            if (o.isMainBuilding()) {
                idOfMainOrg = o.getIdOfOrg();
                break;
            }
        }

        Criteria criteria = session.createCriteria(GroupNamesToOrgs.class);
        criteria.add(Restrictions.eq("idOfOrg", org.getIdOfOrg()));
        if (null != idOfMainOrg) {
            criteria.add(Restrictions.eq("idOfMainOrg", idOfMainOrg));
        }
        criteria.add(Restrictions.eq("groupName", groupName));
        return (GroupNamesToOrgs) criteria.uniqueResult();
    }

    public static List<Long> getUniqueClientIdFromClientDTISZNDiscountInfoSinceDate(Session session, Date date) {
        List<Long> clientGroupList = new LinkedList<Long>();
        clientGroupList.add(ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_DELETED.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_OTHER_ORG.getValue());
        Query query = session.createQuery("select distinct client.idOfClient from ClientDtisznDiscountInfo "
                + "where lastUpdate >= :date and client.clientGroup.compositeIdOfClientGroup.idOfClientGroup not in (:clientGroups)");
        query.setParameter("date", date);
        query.setParameterList("clientGroups", clientGroupList);
        return query.list();
    }

    public static List<Long> getUniqueClientIdFromClientDTISZNDiscountInfo(Session session) {
        List<Long> clientGroupList = new LinkedList<Long>();
        clientGroupList.add(ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_DELETED.getValue());
        clientGroupList.add(ClientGroup.Predefined.CLIENT_OTHER_ORG.getValue());
        Query query = session.createQuery("select distinct client.idOfClient from ClientDtisznDiscountInfo "
                + "where client.clientGroup.compositeIdOfClientGroup.idOfClientGroup not in (:clientGroups)");
        query.setParameterList("clientGroups", clientGroupList);
        return query.list();
    }

    public static List<CategoryDiscountDSZN> getCategoryDiscountDSZNByCategoryDiscountCode(Session session, Long idOfCategoryDiscount) {
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        criteria.add(Restrictions.eq("categoryDiscount.idOfCategoryDiscount", idOfCategoryDiscount));
        return criteria.list();
    }

    public static List<ClientDtisznDiscountInfo> getDTISZNDiscountInfoByClientAndCode(Session session, Client client, List<Long> codeList) {
        Criteria criteria = session.createCriteria(ClientDtisznDiscountInfo.class);
        criteria.add(Restrictions.eq("client", client));
        criteria.add(Restrictions.in("dtisznCode", codeList));
        return criteria.list();
    }

    // код льготы иное
    public static Long getOtherDiscountCode(Session session) {
        Query query = session.createQuery("select dszn.categoryDiscount.idOfCategoryDiscount from CategoryDiscountDSZN dszn where code=0");
        return (Long) query.uniqueResult();
    }

    public static Person getPersonByClientId(Session session, Long idOfClient) {
        Query query = session.createQuery("select p from Client c join c.person p where c.idOfClient = :idOfClient");
        query.setParameter("idOfClient", idOfClient);
        return (Person) query.uniqueResult();
    }

    public static List<Client> findClientsByMobileAndGroupNamesIgnoreLeavingDeletedDisplaced(Session session, String mobile,
            List<String> groupNameList) {
        Criteria mobileCriteria = session.createCriteria(Client.class);
        mobileCriteria.createAlias("clientGroup", "cg");
        mobileCriteria.add(Restrictions.eq("mobile", mobile));
        mobileCriteria.add(Restrictions.ne("idOfClientGroup", ClientGroup.Predefined.CLIENT_LEAVING.getValue()));
        mobileCriteria.add(Restrictions.ne("idOfClientGroup", ClientGroup.Predefined.CLIENT_DELETED.getValue()));
        mobileCriteria.add(Restrictions.ne("idOfClientGroup", ClientGroup.Predefined.CLIENT_DISPLACED.getValue()));
        mobileCriteria.add(Restrictions.in("cg.groupName", groupNameList));
        return (List<Client>) mobileCriteria.list();
    }

    public static Client findClientByMobileAndGroupNamesIgnoreLeavingDeletedDisplaced(Session session, String mobile,
            List<String> groupNameList) {
        List<Client> clientList = findClientsByMobileAndGroupNamesIgnoreLeavingDeletedDisplaced(session, mobile, groupNameList);
        return clientList.isEmpty() ? null : clientList.get(0);
    }

    public static Boolean isFriendlyOrganizations(Session session, Org org1, Org org2) {
        List<Long> friendlyOrgIdList = findFriendlyOrgIds(session, org1.getIdOfOrg());
        for (Long id : friendlyOrgIdList) {
            if (id.equals(org2.getIdOfOrg())) {
                return true;
            }
        }
        return false;
    }

    public static Integer countActiveCardByIdOfClient(Long idOfClient, Session persistenceSession) {
        Query query = persistenceSession.createSQLQuery(" select cast(count(idofcard) as integer) as numbOfActiveCard"
                + " from cf_cards "
                + " where state = :activeState and idofclient = :idOfClient"
        );
        query.setParameter("idOfClient", idOfClient);
        query.setParameter("activeState", CardState.ISSUED.getValue());
        return (Integer) query.uniqueResult();
    }

    public static ApplicationForFoodHistory getLastApplicationForFoodHistory(Session session, ApplicationForFood applicationForFood) {
        Criteria criteria = session.createCriteria(ApplicationForFoodHistory.class);
        criteria.add(Restrictions.eq("applicationForFood", applicationForFood));
        criteria.addOrder(org.hibernate.criterion.Order.desc("createdDate"));
        criteria.setMaxResults(1);
        return (ApplicationForFoodHistory) criteria.uniqueResult();
    }

    public static List<KznClientsStatistic> getKznClientStatisticByOrgs(Session session, List<Long> idOfOrgList) {
        Criteria criteria = session.createCriteria(KznClientsStatistic.class);
        if (!idOfOrgList.isEmpty())
            criteria.add(Restrictions.in("org.idOfOrg", idOfOrgList));
        return criteria.list();
    }

    public static Boolean deleteFromKznClientStatisticByOrgId(Session session, Long idOfOrg) {
        Query query = session.createQuery(
                "delete from KznClientsStatistic statistic where idOfOrg=:id");
        query.setParameter("id", idOfOrg);
        return query.executeUpdate() > 0;
    }

    public static KznClientsStatistic getKznClientStatisticByOrg(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(KznClientsStatistic.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        return (KznClientsStatistic) criteria.uniqueResult();
    }

    public static ClientGroup findKznEmployeeGroupByOrgId(Session session, Long idOfOrg) {
        Criteria criteria = session.createCriteria(ClientGroup.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("groupName", "Сотрудники"));
        return (ClientGroup) criteria.uniqueResult();
    }

    public static List<String> getAllDistinctDepartmentsFromOrgs(Session session) {
        SQLQuery query = session.createSQLQuery("select distinct district from cf_orgs where district is not null and district not like ''");
        return query.list();
    }

    public static  Long getLastVersionOfOrgSettings(Session session){
        SQLQuery query = session.createSQLQuery("SELECT MAX(version) FROM CF_OrgSettings");
        return DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(query.uniqueResult());
    }

    public static  Long getLastVersionOfOrgSettingsItem(Session session){
        SQLQuery query = session.createSQLQuery("SELECT MAX(version) FROM CF_OrgSettings_Items");
        return DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(query.uniqueResult());
    }

    public static OrgSetting getOrgSettingByOrgAndType(Session session, Long idOfOrg, Integer settingGroupId) {
        Criteria criteria = session.createCriteria(OrgSetting.class);
        criteria.add(Restrictions.eq("idOfOrg", idOfOrg))
                .add(Restrictions.eq("settingGroup", OrgSettingGroup.getGroupById(settingGroupId)));
        return (OrgSetting) criteria.uniqueResult();
    }

    public static Contragent getContragentbyClientId(Session persistenceSession, Long clientId) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Contragent.class);
        criteria.createAlias("orgsInternal", "orgs");
        criteria.createAlias("orgs.clientsInternal", "client");
        criteria.add(Restrictions.eq("client.idOfClient", clientId));
        return (Contragent) criteria.uniqueResult();
    }
}
