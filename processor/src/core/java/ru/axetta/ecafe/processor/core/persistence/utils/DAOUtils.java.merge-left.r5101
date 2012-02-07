/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.util.DigitalSignatureUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.10.2010
 * Time: 18:28:59
 * To change this template use File | Settings | File Templates.
 */
public class DAOUtils {

    private static final Logger logger = LoggerFactory.getLogger(DAOUtils.class);

    private DAOUtils() {

    }

    public static Contragent findContragent(Session persistenceSession, long idOfContragent) throws Exception {
        return (Contragent) persistenceSession.get(Contragent.class, idOfContragent);
    }

    public static Client findClient(Session persistenceSession, long idOfClient) throws Exception {
        return (Client) persistenceSession.get(Client.class, idOfClient);
    }

    public static Client getClientReference(Session persistenceSession, long idOfClient) throws Exception {
        return (Client) persistenceSession.load(Client.class, idOfClient);
    }

    public static Client findClientByContractId(Session persistenceSession, long contractId) throws Exception {
        Criteria clientWithSameContractCriteria = persistenceSession.createCriteria(Client.class);
        clientWithSameContractCriteria.add(Restrictions.eq("contractId", contractId));
        return (Client) clientWithSameContractCriteria.uniqueResult();
    }

    public static List findNewerClients(Session persistenceSession, Org organization, long clientRegistryVersion)
            throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Client.class);
        criteria.add(Restrictions.eq("org", organization));
        criteria.add(Restrictions.gt("clientRegistryVersion", clientRegistryVersion));
        return criteria.list();
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
        Query query = persistenceSession.createQuery("from MenuExchange where compositeIdOfMenuExchange.idOfOrg=:idOfSourceOrg AND ((compositeIdOfMenuExchange.menuDate>=:startDate AND compositeIdOfMenuExchange.menuDate<=:endDate) OR (compositeIdOfMenuExchange.menuDate=:nullDate))");
        query.setParameter("idOfSourceOrg", idOfSourceOrg);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("nullDate", new Date(0));
        return (List<MenuExchange>)query.list();
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
        return (MenuExchange)l.get(0);
    }

    public static void updateMenuExchangeLink(Session persistenceSession, Long idOfSourceOrg, Long idOfDestOrg) {
        Query query = persistenceSession.createQuery("delete from MenuExchangeRule rule where rule.idOfDestOrg=?");
        query.setParameter(0, idOfDestOrg);
        query.executeUpdate();
        ////
        if (idOfSourceOrg != null && idOfDestOrg != null) {
            persistenceSession.save(new MenuExchangeRule(idOfSourceOrg, idOfDestOrg));
        }
    }

    public static boolean isOrgMenuExchangeSource(Session persistenceSession, Long idOfOrg) {
        Query query = persistenceSession
                .createQuery("select 1 from MenuExchangeRule rule where rule.idOfSourceOrg = ?");
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
        Query query = persistenceSession.createQuery("select at from AccountTransaction at, Client c "
                + "where at.transactionTime>:sinceTime and at.transactionTime<=:tillTime and at.sourceType=:sourceType and at.client=c and c.org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        query.setParameter("sinceTime", fromDateTime);
        query.setParameter("tillTime", toDateTime);
        query.setParameter("sourceType", sourceType);
        return (List<AccountTransaction>)query.list();
    }

    @SuppressWarnings("unchecked")
    public static List<Object[]> getClientsAndCardsForOrg(Session persistenceSession, Long idOfOrg) {
        Query query = persistenceSession.createQuery("select cl, card from Card card, Client cl where card.client=cl and cl.org.idOfOrg=:idOfOrg");
        query.setParameter("idOfOrg", idOfOrg);
        return (List<Object[]>)query.list();
    }

    public static EnterEvent findEnterEvent(Session persistenceSession, CompositeIdOfEnterEvent compositeIdOfEnterEvent) throws Exception {
        return (EnterEvent) persistenceSession.get(EnterEvent.class, compositeIdOfEnterEvent);
    }

    public static Publication findPublication(Session persistenceSession, CompositeIdOfPublication compositeIdOfPublication) throws Exception {
        return (Publication) persistenceSession.get(Publication.class, compositeIdOfPublication);
    }

    public static Circulation findCirculation(Session persistenceSession, CompositeIdOfCirculation compositeIdOfCirculation) throws Exception {
        return (Circulation) persistenceSession.get(Circulation.class, compositeIdOfCirculation);
    }

    public static boolean existContragentWithClass(Session persistenceSession, Integer classId)
            throws Exception {
        Criteria criteria = persistenceSession.createCriteria(Contragent.class);
        criteria.add(Restrictions.eq("classId", classId));
        return !criteria.list().isEmpty();
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
}
