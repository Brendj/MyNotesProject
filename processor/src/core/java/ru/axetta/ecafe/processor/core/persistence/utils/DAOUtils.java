/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.sync.handlers.org.owners.OrgOwner;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.10.2010
 * Time: 18:28:59
 * To change this template use File | Settings | File Templates.
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

    public static List findClientsBySan (Session persistenceSession, String san) {
        org.hibernate.Query query = persistenceSession.createSQLQuery("select CF_Clients.IdOfClient "
                                                                    + "from CF_Clients "
                                                                    + "where CF_Clients.san like :san");
        query.setParameter("san", san);
        List clientList = query.list();
        return clientList;
    }

    public static List findClientsByContract (Session persistenceSession, Long idOfContract) {
        org.hibernate.Query query = persistenceSession.createSQLQuery("select distinct CF_Clients.IdOfClient "
                                                                    + "from CF_Clients  "
                                                                    + "where CF_Clients.IdOfContract=:contractId");
        query.setParameter("contractId", idOfContract);
        List clientList = query.list();
        return clientList;
    }

    public static GoodsBasicBasket findBasicGood(Session persistenceSession, String guidOfBasicGood) {
        Criteria criteria = persistenceSession.createCriteria(GoodsBasicBasket.class);
        criteria.add(Restrictions.eq("guid",guidOfBasicGood));
        return (GoodsBasicBasket) criteria.uniqueResult();
    }

    public static Client getClientReference(Session persistenceSession, long idOfClient) throws Exception {
        return (Client) persistenceSession.load(Client.class, idOfClient);
    }

    @SuppressWarnings("unchecked")
    public static Client findClientByContractId(Session persistenceSession, long contractId) throws Exception {
        Criteria contractCriteria = persistenceSession.createCriteria(Client.class);
        contractCriteria.add(Restrictions.eq("contractId", contractId));
        List<Client> resultList = (List<Client>) contractCriteria.list();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public static Client findClientByGuid(EntityManager em, String guid) {
        javax.persistence.Query q = em.createQuery("from Client where clientGUID=:guid");
        q.setParameter("guid", guid);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return ((Client)l.get(0));
    }

    public static Long getClientIdByGuid(EntityManager em, String guid) {
        javax.persistence.Query q = em.createQuery("select idOfClient from Client where clientGUID=:guid");
        q.setParameter("guid", guid);
        List l = q.getResultList();
        if (l.size()==0) return null;
        return ((Long)l.get(0));
    }

    /* TODO: Добавить в условие выборки исключение клиентов из групп Выбывшие и Удаленные (ECAFE-629) */
    public static List findNewerClients(Session persistenceSession, Collection<Org> organizations, long clientRegistryVersion)
            throws Exception {
        Query query = persistenceSession.createQuery("from Client cl where (cl.idOfClientGroup is null or cl.idOfClientGroup !=1100000060 or cl.idOfClientGroup !=1100000070)  and cl.org in :orgs and clientRegistryVersion>:version");
        query.setParameter("version", clientRegistryVersion);
        query.setParameterList("orgs", organizations);
        return query.list();
    }

    /* TODO: Добавить в условие выборки исключение клиентов из групп Выбывшие и Удаленные (ECAFE-629) */
    public static List findNewerClients(Session persistenceSession, Org organization, long clientRegistryVersion)
            throws Exception {
        Query query = persistenceSession.createQuery("from Client cl where (cl.idOfClientGroup is null or cl.idOfClientGroup !=1100000060 or cl.idOfClientGroup !=1100000070)  and cl.org in :orgs and clientRegistryVersion>:version");
        query.setParameter("version", clientRegistryVersion);
        query.setParameter("orgs", organization);
        return query.list();
    }

    public static Org findOrg(Session persistenceSession, long idOfOrg) throws Exception {
        return (Org) persistenceSession.get(Org.class, idOfOrg);
    }

    public static Order findOrder(Session persistenceSession, CompositeIdOfOrder compositeIdOfOrder) throws Exception {
        return (Order) persistenceSession.get(Order.class, compositeIdOfOrder);
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
        return (Card) criteria.uniqueResult();
    }

    public static CardTemp findCardTempByCardNo(Session persistenceSession, long cardNo) throws Exception {
        Criteria criteria = persistenceSession.createCriteria(CardTemp.class);
        criteria.add(Restrictions.eq("cardNo", cardNo));
        return (CardTemp) criteria.uniqueResult();
    }

    //public static Client findClientByCardNo(EntityManager em, long cardNo) throws Exception {
    //    javax.persistence.Query q = em.createQuery("from Card where cardNo=:cardNo");
    //    q.setParameter("cardNo", cardNo);
    //    List l = q.getResultList();
    //    if (l.size()==0) return null;
    //    return ((Card)l.get(0)).getClient();
    //}

    public static Client findClientByContractId(EntityManager em, long cardNo) throws Exception {
        return findClientByContractId((Session)em.getDelegate(), cardNo);
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

    public static List findClientPayments(Session persistenceSession, Contragent contragent, String idOfPayment)
            throws Exception {
        Criteria criteria = persistenceSession.createCriteria(ClientPayment.class);
        criteria.add(Restrictions.eq("contragent", contragent));
        criteria.add(Restrictions.eq("idOfPayment", idOfPayment));
        return criteria.list();
    }

    public static boolean existClientPayment(Session persistenceSession, Contragent contragent, String idOfPayment)
            throws Exception {
        return !findClientPayments(persistenceSession, contragent, idOfPayment).isEmpty();
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
        return (Menu) criteria.uniqueResult();
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

    public static List<Org> findFriendlyOrgs (EntityManager em, Org organization) throws Exception {
        javax.persistence.Query query = em.createQuery("select fo.idOfOrg from Org org join org.friendlyOrg fo where org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg", organization.getIdOfOrg());
        if (query.getResultList().isEmpty()) return new ArrayList<Org> ();
        List <Long> orgs = (List <Long>)query.getResultList();
        List <Org> res = new ArrayList <Org> ();
        for (Long idoforg : orgs) {
            if (idoforg.longValue() == organization.getIdOfOrg().longValue()) {
                continue;
            }
            res.add(DAOService.getInstance().getOrg((Long) idoforg));
        }
        return res;
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

    public static boolean existClient(Session persistenceSession, Org organization, String firstName, String surname,
            String secondName) throws Exception {
        Query query = persistenceSession.createQuery(
                "select 1 from Client client where (client.org = ?) and (upper(client.person.surname) = ?) and"
                        + "(upper(client.person.firstName) = ?) and (upper(client.person.secondName) = ?)");
        query.setParameter(0, organization);
        query.setParameter(1, StringUtils.upperCase(surname));
        query.setParameter(2, StringUtils.upperCase(firstName));
        query.setParameter(3, StringUtils.upperCase(secondName));
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    public static boolean existClient(Session persistenceSession, Org organization, String firstName, String surname)
            throws Exception {
        Query query = persistenceSession.createQuery(
                "select 1 from Client client where (client.org = ?) and (upper(client.person.surname) = ?) and"
                        + "(upper(client.person.firstName) = ?)");
        query.setParameter(0, organization);
        query.setParameter(1, StringUtils.upperCase(surname));
        query.setParameter(2, StringUtils.upperCase(firstName));
        query.setMaxResults(1);
        return !query.list().isEmpty();
    }

    public static Long findClientByFullName(EntityManager em, Org organization, String surname, String firstName, String secondName)
            throws Exception {


        javax.persistence.Query q = em.createNativeQuery("select cf_clients.idofclient "+
                "from cf_clients "+
                "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "+
                "where trim(upper(cf_persons.Surname))=:surname and trim(upper(cf_persons.FirstName))=:firstName and trim(upper(cf_persons.SecondName))=:secondName and "+
                "(cf_clients.idoforg in (select cf_friendly_organization.friendlyorg from cf_friendly_organization where cf_friendly_organization.currentorg=:org))");
        q.setParameter("org", organization.getIdOfOrg());
        q.setParameter("surname", StringUtils.upperCase(surname).trim());
        q.setParameter("firstName", StringUtils.upperCase(firstName).trim());
        q.setParameter("secondName", StringUtils.upperCase(secondName).trim());
        q.setMaxResults(2);
        List res = q.getResultList();
        if (res.isEmpty()) return null;
        if (res.size()==2) return -1L;
        return ((Number)res.get(0)).longValue();


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

    public static long updateClientRegistryVersion(Session persistenceSession) throws Exception {
        Registry registry = (Registry) persistenceSession.get(Registry.class, Registry.THE_ONLY_INSTANCE_ID);
        registry.setClientRegistryVersion(registry.getClientRegistryVersion() + 1);
        persistenceSession.update(registry);
        return registry.getClientRegistryVersion();
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

    public static void changeClientBalance(Session session, Long idOfClient, long sum) {
        Query q=session.createQuery("UPDATE Client SET balance=balance+? WHERE idOfClient=?");
        q.setLong(0, sum);
        q.setLong(1, idOfClient);
        q.executeUpdate();
    }

    public static void deleteAssortmentForDate(Session persistenceSession, Org organization, Date menuDate) {
        Date endDate = DateUtils.addDays(menuDate, 1);
        Query q = persistenceSession.createQuery("DELETE FROM Assortment WHERE org=:org AND beginDate>=:fromDate AND beginDate<=:endDate");
        q.setParameter("org", organization);
        q.setParameter("fromDate", menuDate);
        q.setParameter("endDate", endDate);
        q.executeUpdate();
    }

    public static void deleteComplexInfoForDate(Session persistenceSession, Org organization, Date menuDate) {
        Date endDate = DateUtils.addDays(menuDate, 1);
        Query q = persistenceSession.createQuery("DELETE FROM ComplexInfo WHERE org=:org AND menuDate>=:fromDate AND menuDate<=:endDate");
        q.setParameter("org", organization);
        q.setParameter("fromDate", menuDate);
        q.setParameter("endDate", endDate);
        q.executeUpdate();
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

    @SuppressWarnings("unchecked")
    public static List<AccountTransaction> getAccountTransactionsForOrgSinceTime(Session persistenceSession, Long idOfOrg,
            Date fromDateTime, Date toDateTime, int sourceType) {
        Query query = persistenceSession.createQuery("select at from AccountTransaction at "
                + " where at.transactionTime>=:sinceTime and at.transactionTime<:tillTime and at.sourceType=:sourceType and (at.org.idOfOrg=:idOfOrg or at.org.idOfOrg in (select fo.idOfOrg from Org org join org.friendlyOrg fo where org.idOfOrg=:idOfOrg))");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("sinceTime", fromDateTime);
        query.setParameter("tillTime", toDateTime);
        query.setParameter("sourceType", sourceType);
        return (List<AccountTransaction>)query.list();
    }

    @SuppressWarnings("unchecked")
    public static List<AccountTransaction> getAccountTransactionsForOrgSinceTime(Session persistenceSession, Set<Long> idOfOrgs,
            Date fromDateTime, Date toDateTime, int sourceType) {
        Query query = persistenceSession.createQuery("select at from AccountTransaction at, Client c "
                + "where at.transactionTime>=:sinceTime and at.transactionTime<:tillTime and at.sourceType=:sourceType and at.client=c and c.org.idOfOrg in (:idOfOrg)");
        query.setParameterList("idOfOrg", idOfOrgs);
        query.setParameter("sinceTime", fromDateTime);
        query.setParameter("tillTime", toDateTime);
        query.setParameter("sourceType", sourceType);
        return (List<AccountTransaction>)query.list();
    }

    public static List getClientsByOrgList(Session session, Set<Long> orgSet){
        String idOfOrgs = orgSet.toString().replaceAll("[^0-9,]", "");
        Query query = session.createQuery("from Client cl where cl.org.idOfOrg in ("+idOfOrgs+")");
        return query.list();
    }

    @SuppressWarnings("unchecked")
    public static List<Object[]> getClientsAndCardsForOrg(Session persistenceSession, Long idOfOrg) {
        Query query = persistenceSession.createQuery(
                "select cl, card from Card card, Client cl where card.client=cl and cl.org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        return (List<Object[]>)query.list();
    }


    @SuppressWarnings("unchecked")
    public static List<Object[]> getClientsAndCardsForOrgs(Session persistenceSession, Set<Long> idOfOrgs) {

        List<ClientGroup> deletingAndLeavingClientGroup = new ArrayList<ClientGroup>();

        /* раскомментировать TODO: Добавить в условие выборки исключение клиентов из групп Выбывшие и Удаленные (ECAFE-629) */
        //Set<CompositeIdOfClientGroup> clientGroups = new HashSet<CompositeIdOfClientGroup>();
        //for (Long idOfOrg: idOfOrgs){
        //    CompositeIdOfClientGroup deletedClientGroup = new CompositeIdOfClientGroup(idOfOrg,ClientGroup.Predefined.CLIENT_DELETED.getValue());
        //    CompositeIdOfClientGroup leavingClientGroup = new CompositeIdOfClientGroup(idOfOrg,ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        //    clientGroups.add(deletedClientGroup);
        //    clientGroups.add(leavingClientGroup);
        //}
        //Criteria clientGroupCriteria = persistenceSession.createCriteria(ClientGroup.class);
        //clientGroupCriteria.add(Restrictions.in("compositeIdOfClientGroup",clientGroups));
        //deletingAndLeavingClientGroup = clientGroupCriteria.list();

        String sql;
        if (deletingAndLeavingClientGroup.isEmpty()){
            sql = "select cl, card from Card card, Client cl where card.client=cl and cl.org.idOfOrg in (:idOfOrg)";
        } else {
            sql = "select cl, card from Card card, Client cl where card.client=cl and cl.org.idOfOrg in (:idOfOrg) and not (cl.clientGroup in (:deletingAndLeavingClientGroup))";
        }

        Query query = persistenceSession.createQuery(sql);
        query.setParameterList("idOfOrg", idOfOrgs);
        if (!deletingAndLeavingClientGroup.isEmpty()){
            query.setParameterList("deletingAndLeavingClientGroup", deletingAndLeavingClientGroup);
        }
        return (List<Object[]>)query.list();
    }

    @SuppressWarnings("unchecked")
    public static List<Object[]> getClientsAndCardsForOrganization(Session persistenceSession, Long idOfOrg) {
        Query query = persistenceSession.createQuery("select cl, card from Card card, Client cl where card.client=cl and (cl.org.idOfOrg in (select fo.idOfOrg from Org org join org.friendlyOrg fo where org.idOfOrg=:idOfOrg) or (cl.idOfOrg=:idOfOrg))");
        query.setParameter("idOfOrg", idOfOrg);
        return (List<Object[]>)query.list();
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

    @SuppressWarnings("unchecked")
    public static List<Contragent> getContragentsWithClassIds(EntityManager em, Integer[] classId) {
        List<Integer> classIds = Arrays.asList(classId);
        javax.persistence.Query q = em.createQuery("from Contragent where classId in (:classIds)");
        q.setParameter("classIds", classIds);
        return (List<Contragent>)q.getResultList();
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

    public static List listDiscountRules(EntityManager em) {
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

    public static ClientGroup createClientGroup(Session persistenceSession, Long idOfOrg, ClientGroup.Predefined predefined) {
        Long idOfClientGroup =null;
        CompositeIdOfClientGroup compositeIdOfClientGroup = new CompositeIdOfClientGroup(idOfOrg,predefined.getValue());
        ClientGroup clientGroup = new ClientGroup(compositeIdOfClientGroup, predefined.getNameOfGroup());
        persistenceSession.save(clientGroup);
        return clientGroup;
    }

    public static ClientGroup createClientGroup(Session persistenceSession, Long idOfOrg, String clientGroupName) {
        ClientGroup.Predefined predefined = ClientGroup.Predefined.parse(clientGroupName);
        Long idOfClientGroup =null;
        /* если группа предопредлелена */
        if(predefined!=null){
            idOfClientGroup = predefined.getValue();
        } else {
            /* иначе это класс */
            Query q = persistenceSession.createQuery("select max(compositeIdOfClientGroup.idOfClientGroup) from ClientGroup where compositeIdOfClientGroup.idOfOrg=:idOfOrg and compositeIdOfClientGroup.idOfClientGroup<:idOfClientGroup");
            q.setParameter("idOfOrg", idOfOrg);
            q.setParameter("idOfClientGroup",1100000000L);
            List l = q.list();
            Long compositeIdOfClientGroup;
            if(l==null || l.isEmpty() || l.get(0)==null){
                compositeIdOfClientGroup = ClientGroup.Predefined.CLIENT_STUDENTS_CLASS_BEGIN.getValue();
            } else {
                compositeIdOfClientGroup =  (Long)l.get(0);
            }
            idOfClientGroup = compositeIdOfClientGroup + 1 ;
        }
        CompositeIdOfClientGroup compositeIdOfClientGroup = new CompositeIdOfClientGroup(idOfOrg,idOfClientGroup);
        ClientGroup clientGroup = new ClientGroup(compositeIdOfClientGroup, clientGroupName);
        persistenceSession.save(clientGroup);
        return clientGroup;
    }

    public static ClientGroup createNewClientGroup(Session persistenceSession, Long idOfOrg, String clientGroupName) {
        CompositeIdOfClientGroup compositeIdOfClientGroup = new CompositeIdOfClientGroup(idOfOrg,
                DAOUtils.getIdForTemporaryClientGroup(persistenceSession, idOfOrg));
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

    /*
    public static Set<Long> getFriendlyOrg(Session persistenceSession, Long idOfOrg) {
        Query query = persistenceSession.createQuery("select org.friendlyOrg from Org org where org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        List forg = query.list();
        Set<Long> result = new HashSet<Long>(forg.size());
        for (Object object: forg){
            result.add(((Org) object).getIdOfOrg());
        }
        return result;
    } */

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

    public static void removeContractLinkFromOrgs(EntityManager entityManager, Contract entity) {
        javax.persistence.Query q = entityManager.createQuery("update Org set contract=null where contract=:contract");
        q.setParameter("contract", entity);
        q.executeUpdate();
    }

    public static List<Object[]> getClientPaymentsDataForPeriod(EntityManager em, Date dtFrom, Date dtTo, Contragent caReceiver) {
        if (caReceiver==null) {
            javax.persistence.TypedQuery<Object[]> q = em.createQuery("select c.contractId, cp.createTime, cp.paySum, cp.idOfPayment from ClientPayment cp, AccountTransaction at, Client c where at=cp.transaction and at.client=c and cp.createTime>=:dtFrom and cp.createTime<:dtTo", Object[].class);
            q.setParameter("dtFrom", dtFrom);
            q.setParameter("dtTo", dtTo);
            return q.getResultList();
        } else {
            javax.persistence.TypedQuery<Object[]> q = em.createQuery("select c.contractId, cp.createTime, cp.paySum, cp.idOfPayment from ClientPayment cp, AccountTransaction at, Client c where at=cp.transaction and at.client=c and cp.createTime>=:dtFrom and cp.createTime<:dtTo and cp.contragentReceiver=:caReceiver", Object[].class);
            q.setParameter("dtFrom", dtFrom);
            q.setParameter("dtTo", dtTo);
            q.setParameter("caReceiver", caReceiver);
            return q.getResultList();
        }
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
    public static <T extends DistributedObject> T findDistributedObjectByRefGUID(Class<T> clazz, Session session, String guid){
        //Criteria criteria = session.createCriteria(DistributedObject.class);
        //criteria.add(Restrictions.eq("guid",guid));
        //return (DistributedObject) criteria.uniqueResult();
        String sql = "from "+clazz.getClass()+" where guid=:guid";
        Query query = session.createQuery(sql);
        query.setParameter("guid", guid);
        List list = query.list();
        if(list==null || list.isEmpty()){
            return null;
        } else {
            return (T) list.get(0);
        }
    }

    public static List<Long> getListIdOfOrgList(Session session, Long idOfOrg){
        //List<Long> resultList = new ArrayList<Long>();
        //Query query = session.createQuery("select idOfDestOrg from MenuExchangeRule where idOfSourceOrg=:idOfOrg");
        //query.setParameter("idOfOrg",idOfOrg);
        //List list = query.list();
        //if(!(list==null || list.isEmpty())){
        //    for (Object object: list){
        //        resultList.add((Long) object);
        //    }
        //}
        //query = session.createQuery("select idOfSourceOrg from MenuExchangeRule where idOfDestOrg=:idOfOrg");
        //query.setParameter("idOfOrg",idOfOrg);
        //list = query.list();
        //if(!(list==null || list.isEmpty())){
        //    for (Object object: list){
        //        resultList.add((Long) object);
        //    }
        //}
        List<Long> resultList = new ArrayList<Long>();
        Query query = session.createQuery("select idOfDestOrg from MenuExchangeRule where idOfSourceOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        List<Long> list = (List<Long>)query.list();
        resultList.addAll(list);
        query = session.createQuery("select idOfSourceOrg from MenuExchangeRule where idOfDestOrg=:idOfOrg");
        query.setParameter("idOfOrg",idOfOrg);
        list = (List<Long>)query.list();
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

    public static void updateClientVersionAndRemoteAddressByOrg(Session persistenceSession,Long idOfOrg, String clientVersion,
            String remoteAddress) {
        Query query = persistenceSession.createQuery("update Org set remoteAddress=:remoteAddress, clientVersion=:clientVersion where idOfOrg=:idOfOrg");
        query.setParameter("remoteAddress", remoteAddress);
        query.setParameter("clientVersion", clientVersion);
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
        final Query query = persistenceSession.createQuery("from Client cl where not(cl.org in :friendlyOrg) and cl.idOfClient in :errorClientIds");
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

    public static CardTempOperation findTempCartOperation(Session session, Long idOfOperation, Org org) {
        final Query query = session.createQuery("select cto from CardTempOperation cto right join cto.org organization where organization.idOfOrg=:idOfOrg and cto.localId=:localId");
        query.setParameter("idOfOrg", org.getIdOfOrg());
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
    public static List<CardTempOperation> getRegistrTempCardOperationByOrg(Session session, Long idOfOrg) {
        Query query = session.createQuery("from CardTempOperation oper where oper.org.id = :idOfOrg and oper.operationType=0");
        query.setParameter("idOfOrg", idOfOrg);
        return query.list();
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
}
