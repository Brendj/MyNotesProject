/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.ui.PaymentTextUtils;
import ru.axetta.ecafe.processor.web.util.EntityManagerUtils;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.servlet.http.HttpServlet;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 12.12.11
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */

@WebService()
public class ClientRoomControllerWS extends HttpServlet implements ClientRoomController {

    final Logger logger = LoggerFactory.getLogger(ClientRoomControllerWS.class);
    private static final Long RC_CLIENT_NOT_FOUND = 110L;
    private static final Long RC_SEVERAL_CLIENTS_WERE_FOUND = 120L;
    private static final Long RC_INTERNAL_ERROR = 100L, RC_OK = 0L;
    private static final Long RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS = 130L;
    private static final Long RC_CLIENT_HAS_THIS_SNILS_ALREADY = 140L;
    private static final Long RC_INVALID_DATA = 150L;
    private static final String RC_OK_DESC="OK";
    private static final String RC_CLIENT_NOT_FOUND_DESC="Клиент не найден";
    private static final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC="По условиям найден более одного клиента";
    private static final String RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC="У клиента нет СНИЛС опекуна";
    private static final String RC_CLIENT_HAS_THIS_SNILS_ALREADY_DESC= "У клиента уже есть данный СНИЛС опекуна";

    interface Processor {

        public void process(Client client, Data data, ObjectFactory objectFactory, Session persistenceSession,
                Transaction transaction) throws Exception;
    }

    class ClientRequest {

        public Data process(Long contractId, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            data.setIdOfContract(contractId);

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                clientCriteria.add(Restrictions.eq("contractId", contractId));
                Client client = (Client) clientCriteria.uniqueResult();
                if (client == null) {
                    data.setResultCode(RC_CLIENT_NOT_FOUND);
                    data.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                } else {
                    processor.process(client, data, objectFactory, persistenceSession, persistenceTransaction);
                    data.setResultCode(RC_OK);
                    data.setDescription(RC_OK_DESC);
                }
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed to process client room controller request", e);
                data.setResultCode(RC_INTERNAL_ERROR);
                data.setDescription(e.toString());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
            return data;
        }

        public Data process(String san, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                clientCriteria.add(Restrictions.ilike("san", san, MatchMode.EXACT));
                List<Client> clients = clientCriteria.list();

                if (clients.isEmpty()) {
                    data.setResultCode(RC_CLIENT_NOT_FOUND);
                    data.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                } else if (clients.size() > 1) {
                    data.setResultCode(RC_SEVERAL_CLIENTS_WERE_FOUND);
                    data.setDescription(RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
                } else {
                    Client client = (Client) clients.get(0);
                    processor.process(client, data, objectFactory, persistenceSession, persistenceTransaction);
                    data.setIdOfContract(client.getContractId());
                    data.setResultCode(RC_OK);
                    data.setDescription("OK");
                }
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed to process client room controller request", e);
                data.setResultCode(RC_INTERNAL_ERROR);
                data.setDescription(e.toString());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
            return data;
        }
    }

    @Override
    public ClientSummaryResult getSummary(Long contractId) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processSummary(client, data, objectFactory, session);
            }
        });

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    @Override
    public ClientSummaryResult getSummary(String san) {
        Data data = new ClientRequest().process(san, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processSummary(client, data, objectFactory, session);
            }
        });

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    private void processSummary(Client client, Data data, ObjectFactory objectFactory, Session session)
            throws DatatypeConfigurationException {
        ClientSummaryExt clientSummaryExt = objectFactory.createClientSummaryExt();
        clientSummaryExt.setContractId(client.getContractId());
        clientSummaryExt.setDateOfContract(toXmlDateTime(client.getContractTime()));
        clientSummaryExt.setBalance(client.getBalance());
        clientSummaryExt.setOverdraftLimit(client.getLimit());
        clientSummaryExt.setStateOfContract(Client.CONTRACT_STATE_NAMES[client.getContractState()]);
        clientSummaryExt.setExpenditureLimit(client.getExpenditureLimit());
        clientSummaryExt.setFirstName(client.getPerson().getFirstName());
        clientSummaryExt.setNotifyViaEmail(client.isNotifyViaEmail());
        clientSummaryExt.setNotifyViaSMS(client.isNotifyViaSMS());
        clientSummaryExt.setMobilePhone(client.getMobile());
        clientSummaryExt.setEmail(client.getEmail());
        EnterEvent ee = DAOUtils.getLastEnterEvent(session, client);
        if (ee!=null) {
            clientSummaryExt.setLastEnterEventCode(ee.getEventCode());
            clientSummaryExt.setLastEnterEventTime(toXmlDateTime(ee.getEvtDateTime()));
        }

        if (client.getClientGroup() == null)
            clientSummaryExt.setGrade(null);
        else
            clientSummaryExt.setGrade(client.getClientGroup().getGroupName());
        clientSummaryExt.setOfficialName(client.getOrg().getOfficialName());
        data.setClientSummaryExt(clientSummaryExt);
    }

    final static int MAX_RECS = 50;

    @Override
    public PurchaseListResult getPurchaseList(Long contractId, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processPurchaseList(client, data, objectFactory, session, endDate, startDate);
            }
        });
        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();

        return purchaseListResult;
    }

    @Override
    public PurchaseListResult getPurchaseList(String san, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(san, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processPurchaseList(client, data, objectFactory, session, endDate, startDate);
            }
        });

        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();
        return purchaseListResult;
    }

    private void processPurchaseList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate) throws DatatypeConfigurationException {
        int nRecs = 0;
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria ordersCriteria = session.createCriteria(Order.class);
        ordersCriteria.add(Restrictions.eq("client", client));
        ordersCriteria.add(Restrictions.ge("createTime", startDate));
        ordersCriteria.add(Restrictions.lt("createTime", nextToEndDate));
        ordersCriteria.addOrder(org.hibernate.criterion.Order.asc("createTime"));
        List ordersList = ordersCriteria.list();
        PurchaseListExt purchaseListExt = objectFactory.createPurchaseListExt();
        for (Object o : ordersList) {
            if (nRecs++>MAX_RECS) break;
            Order order = (Order)o;
            PurchaseExt purchaseExt = objectFactory.createPurchaseExt();
            purchaseExt.setByCard(order.getSumByCard());
            purchaseExt.setSocDiscount(order.getSocDiscount());
            purchaseExt.setTrdDiscount(order.getTrdDiscount());
            purchaseExt.setDonation(order.getGrantSum());
            purchaseExt.setSum(order.getRSum());
            purchaseExt.setByCash(order.getSumByCash());
            if (order.getCard() == null)
                purchaseExt.setIdOfCard(null);
            else
                purchaseExt.setIdOfCard(order.getCard().getCardPrintedNo());
            purchaseExt.setTime(toXmlDateTime(order.getCreateTime()));

            Set<OrderDetail> orderDetailSet=((Order) o).getOrderDetails();
            for (OrderDetail od : orderDetailSet) {
                PurchaseElementExt purchaseElementExt = objectFactory.createPurchaseElementExt();
                purchaseElementExt.setAmount(od.getQty());
                purchaseElementExt.setName(od.getMenuDetailName());
                purchaseElementExt.setSum(od.getRPrice());
                purchaseExt.getE().add(purchaseElementExt);
            }

            purchaseListExt.getP().add(purchaseExt);
        }
        data.setPurchaseListExt(purchaseListExt);
    }

    @Override
    public PaymentListResult getPaymentList(Long contractId, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processPaymentList(client, data, objectFactory, session, endDate, startDate);
            }
        });

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    @Override
    public PaymentListResult getPaymentList(String san, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(san, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processPaymentList(client, data, objectFactory, session, endDate, startDate);
            }
        });

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    private void processPaymentList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria clientPaymentsCriteria = session.createCriteria(ClientPayment.class);
        clientPaymentsCriteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
        clientPaymentsCriteria.addOrder(org.hibernate.criterion.Order.asc("createTime"));
        clientPaymentsCriteria.add(Restrictions.ge("createTime", startDate));
        clientPaymentsCriteria.add(Restrictions.lt("createTime", nextToEndDate));
        clientPaymentsCriteria = clientPaymentsCriteria.createCriteria("transaction");
        clientPaymentsCriteria.add(Restrictions.eq("client", client));
        List clientPaymentsList = clientPaymentsCriteria.list();
        PaymentList paymentList = objectFactory.createPaymentList();
        int nRecs = 0;
        for (Object o : clientPaymentsList) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            ClientPayment cp = (ClientPayment) o;
            Payment payment = new Payment();
            payment.setOrigin(PaymentTextUtils.buildTransferInfo(cp));
            payment.setSum(cp.getPaySum());
            payment.setTime(toXmlDateTime(cp.getCreateTime()));
            paymentList.getP().add(payment);
        }
        data.setPaymentList(paymentList);
    }

    @Override
    public MenuListResult getMenuList(Long contractId, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processMenuList(client, data, objectFactory, session, startDate, endDate);
            }
        });

        MenuListResult menuListResult = new MenuListResult();
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    @Override
    public MenuListResult getMenuList(String san, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(san, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processMenuList(client, data, objectFactory, session, startDate, endDate);
            }
        });

        MenuListResult menuListResult = new MenuListResult();
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    private void processMenuList(Client client, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {
        Criteria menuCriteria = session.createCriteria(Menu.class);
        menuCriteria.add(Restrictions.eq("org", client.getOrg()));
        menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuCriteria.add(Restrictions.ge("menuDate", startDate));
        menuCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 1)));

        List menus = menuCriteria.list();
        MenuListExt menuListExt = objectFactory.createMenuListExt();
        int nRecs=0;
        for (Object currObject : menus) {
            if (nRecs++>MAX_RECS) break;

            Menu menu = (Menu)currObject;
            MenuDateItemExt menuDateItemExt = objectFactory.createMenuDateItemExt();
            menuDateItemExt.setDate(toXmlDateTime(menu.getMenuDate()));

            Criteria menuDetailCriteria = session.createCriteria(MenuDetail.class);
            menuDetailCriteria.add(Restrictions.eq("menu", menu));
            HibernateUtils.addAscOrder(menuDetailCriteria, "groupName");
            HibernateUtils.addAscOrder(menuDetailCriteria, "menuDetailName");
            List menuDetails = menuDetailCriteria.list();

            for (Object o : menuDetails) {
                MenuDetail menuDetail = (MenuDetail)o;
                MenuItemExt menuItemExt = objectFactory.createMenuItemExt();
                menuItemExt.setGroup(menuDetail.getGroupName());
                menuItemExt.setName(menuDetail.getMenuDetailName());
                menuItemExt.setPrice(menuDetail.getPrice());
                menuItemExt.setCalories(menuDetail.getCalories());
                menuItemExt.setVitB1(menuDetail.getVitB1());
                menuItemExt.setVitC(menuDetail.getVitC());
                menuItemExt.setVitA(menuDetail.getVitA());
                menuItemExt.setVitE(menuDetail.getVitE());
                menuItemExt.setMinCa(menuDetail.getMinCa());
                menuItemExt.setMinP(menuDetail.getMinP());
                menuItemExt.setMinMg(menuDetail.getMinMg());
                menuItemExt.setMinFe(menuDetail.getMinFe());
                menuDateItemExt.getE().add(menuItemExt);
            }

            menuListExt.getM().add(menuDateItemExt);
        }
        data.setMenuListExt(menuListExt);
    }

    @Override
    public CardListResult getCardList(Long contractId) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processCardList(client, data, objectFactory);
            }
        });
        
        CardListResult cardListResult = new CardListResult();
        cardListResult.cardList = data.getCardList();
        cardListResult.resultCode = data.getResultCode();
        cardListResult.description = data.getDescription();
        return cardListResult;
    }

    @Override
    public CardListResult getCardList(String san) {
        Data data = new ClientRequest().process(san, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processCardList(client, data, objectFactory);
            }
        });

        CardListResult cardListResult = new CardListResult();
        cardListResult.cardList = data.getCardList();
        cardListResult.resultCode = data.getResultCode();
        cardListResult.description = data.getDescription();
        return cardListResult;
    }

    private void processCardList(Client client, Data data, ObjectFactory objectFactory)
            throws DatatypeConfigurationException {
        Set<Card> cardSet = client.getCards();
        CardList cardList = objectFactory.createCardList();
        for (Card card : cardSet) {
            CardItem cardItem = objectFactory.createCardItem();
            cardItem.setState(card.getState());
            cardItem.setType(card.getCardType());
            cardItem.setChangeDate(toXmlDateTime(card.getUpdateTime()));
            cardItem.setCrystalId(card.getCardNo());
            cardItem.setIdOfCard(card.getIdOfCard());
            cardItem.setLifeState(card.getLifeState());
            cardItem.setExpiryDate(toXmlDateTime(card.getValidTime()));
            cardList.getC().add(cardItem);
        }
        data.setCardList(cardList);
    }

    @Override
    public EnterEventListResult getEnterEventList(Long contractId, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processEnterEventList(client, data, objectFactory, session, endDate, startDate);
            }
        });

        EnterEventListResult enterEventListResult = new EnterEventListResult();
        enterEventListResult.enterEventList = data.getEnterEventList();
        enterEventListResult.resultCode = data.getResultCode();
        enterEventListResult.description = data.getDescription();
        return enterEventListResult;
    }

    @Override
    public EnterEventListResult getEnterEventList(String san, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(san, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processEnterEventList(client, data, objectFactory, session, endDate, startDate);
            }
        });

        EnterEventListResult enterEventListResult = new EnterEventListResult();
        enterEventListResult.enterEventList = data.getEnterEventList();
        enterEventListResult.resultCode = data.getResultCode();
        enterEventListResult.description = data.getDescription();
        return enterEventListResult;
    }

    private void processEnterEventList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate) throws DatatypeConfigurationException {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria enterEventCriteria = session.createCriteria(EnterEvent.class);
        enterEventCriteria.add(Restrictions.eq("client", client));
        enterEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        enterEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        enterEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        List<EnterEvent> enterEvents = enterEventCriteria.list();
        EnterEventList enterEventList = objectFactory.createEnterEventList();
        int nRecs = 0;
        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            EnterEventItem enterEventItem = objectFactory.createEnterEventItem();
            enterEventItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventItem.setDay(calendar.get(Calendar.DAY_OF_WEEK) - 1);
            enterEventItem.setEnterName(enterEvent.getEnterName());
            enterEventItem.setDirection(enterEvent.getPassDirection());
            enterEventItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            enterEventList.getE().add(enterEventItem);
        }
        data.setEnterEventList(enterEventList);
    }

    @Override
    public ClientsData getClientsByGuardSan(String guardSan) {
        ClientsData data = new ClientsData();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);

            Criterion exp1 = Restrictions.or(Restrictions.ilike("guardSan", guardSan, MatchMode.EXACT),
                    Restrictions.ilike("guardSan", guardSan + ";", MatchMode.START));
            Criterion exp2 = Restrictions.or(Restrictions.like("guardSan", ";" + guardSan, MatchMode.END),
                    Restrictions.like("guardSan", ";" + guardSan + ";", MatchMode.ANYWHERE));
            Criterion expression = Restrictions.or(exp1, exp2);
            clientCriteria.add(expression);

            List<Client> clients = clientCriteria.list();

            if (clients.isEmpty()) {
                data.resultCode = RC_CLIENT_NOT_FOUND;
                data.description = RC_CLIENT_NOT_FOUND_DESC;
            } else {
                for (Client client : clients) {
                    ClientItem clientItem = new ClientItem();
                    clientItem.setContractId(client.getContractId());
                    clientItem.setSan(client.getSan());
                    data.clientList = new ClientList();
                    data.clientList.getClients().add(clientItem);
                }
                data.resultCode = RC_OK;
                data.description = "OK";
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.toString();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return data;
    }

    @Override
    public AttachGuardSanResult attachGuardSan(String san, String guardSan) {
        AttachGuardSanResult data = new AttachGuardSanResult();
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = EntityManagerUtils.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNativeQuery(
                    "select c.IdOfClient, c.GuardSan from CF_Clients c where c.san like :san");
            query.setParameter("san", san);
            List clientList = query.getResultList();
            workClientSan(entityManager, guardSan, data, clientList);
            entityTransaction.commit();
            entityTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (entityTransaction != null)
                entityTransaction.rollback();
            if (entityManager != null)
                entityManager.close();
        }
        return data;
    }

    @Override
    public AttachGuardSanResult attachGuardSan(Long contractId, String guardSan) {
        AttachGuardSanResult data = new AttachGuardSanResult();
        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = EntityManagerUtils.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNativeQuery(
                    "select c.IdOfClient, c.GuardSan from CF_Clients c where c.contractId = :contractId");
            query.setParameter("contractId", contractId);
            List clientList = query.getResultList();
            workClientSan(entityManager, guardSan, data, clientList);
            entityTransaction.commit();
            entityTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (entityTransaction != null)
                entityTransaction.rollback();
            if (entityManager != null)
                entityManager.close();
        }
        return data;
    }

    @Override
    public DetachGuardSanResult detachGuardSan(String san, String guardSan) {
        DetachGuardSanResult data = new DetachGuardSanResult();

        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = EntityManagerUtils.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNativeQuery(
                    "select c.IdOfClient, c.GuardSan from CF_Clients c where c.san like :san");
            query.setParameter("san", san);
            List clientList = query.getResultList();
            workClientSan(entityManager, guardSan, data, clientList);
            entityTransaction.commit();
            entityTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (entityTransaction != null)
                entityTransaction.rollback();
            if (entityManager != null)
                entityManager.close();
        }

        return data;
    }

    @Override
    public DetachGuardSanResult detachGuardSan(Long contractId, String guardSan) {
        DetachGuardSanResult data = new DetachGuardSanResult();

        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;
        try {
            entityManager = EntityManagerUtils.createEntityManager();
            entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            Query query = entityManager.createNativeQuery(
                    "select c.IdOfClient, c.GuardSan from CF_Clients c where c.contractId = :contractId");
            query.setParameter("contractId", contractId);
            List clientList = query.getResultList();
            workClientSan(entityManager, guardSan, data, clientList);
            entityTransaction.commit();
            entityTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (entityTransaction != null)
                entityTransaction.rollback();
            if (entityManager != null)
                entityManager.close();
        }

        return data;
    }

    private void workClientSan(EntityManager entityManager, String guardSan, Result data, List clientList) {
        if (clientList.size() == 0) {
            data.resultCode = RC_CLIENT_NOT_FOUND;
            data.description =RC_CLIENT_NOT_FOUND_DESC;
        } else if (clientList.size() > 1) {
            data.resultCode = RC_SEVERAL_CLIENTS_WERE_FOUND;
            data.description = RC_SEVERAL_CLIENTS_WERE_FOUND_DESC;
        } else {
            Object[] clientObject = (Object[]) clientList.get(0);
            Long idOfClient = ((BigInteger) clientObject[0]).longValue();
            String clientGuardSan = (String) clientObject[1];
            if (clientGuardSan == null) {
                if (data instanceof AttachGuardSanResult) {
                    Query query = entityManager.createNativeQuery("update CF_Clients set GuardSan = :guardSan where IdOfClient = :idOfClient");
                    query.setParameter("guardSan", guardSan);
                    query.setParameter("idOfClient", idOfClient);
                    query.executeUpdate();
                    data.resultCode = RC_OK;
                    data.description = "Ok";
                } else if (data instanceof DetachGuardSanResult) {
                    data.resultCode = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS;
                    data.description = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC;
                }
            }
            else {
                if (data instanceof AttachGuardSanResult) {
                    if (isGuardSanExists(guardSan, clientGuardSan)) {
                        data.resultCode = RC_CLIENT_HAS_THIS_SNILS_ALREADY;
                        data.description = RC_CLIENT_HAS_THIS_SNILS_ALREADY_DESC;
                    } else {
                        String gs = "";
                        if (clientGuardSan.endsWith(";"))
                            gs = clientGuardSan + guardSan;
                        else
                            gs = clientGuardSan + ";" + guardSan;
                        Query query = entityManager.createNativeQuery("update CF_Clients set GuardSan = :guardSan where IdOfClient = :idOfClient");
                        query.setParameter("guardSan", gs);
                        query.setParameter("idOfClient", idOfClient);
                        query.executeUpdate();
                        data.resultCode = RC_OK;
                        data.description = "Ok";
                    }
                } else if (data instanceof DetachGuardSanResult) {
                    if (!isGuardSanExists(guardSan, clientGuardSan)) {
                        data.resultCode = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS;
                        data.description =RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC;
                    } else {
                        if (clientGuardSan.contains(";" + guardSan + ";"))
                            clientGuardSan = clientGuardSan.replace(";" + guardSan + ";", ";");
                        else if (clientGuardSan.startsWith(guardSan + ";"))
                            clientGuardSan = clientGuardSan.substring((guardSan + ";").length());
                        else if (clientGuardSan.endsWith(";" + guardSan))
                            clientGuardSan = clientGuardSan.substring(0, clientGuardSan.length() - (";" + guardSan).length());
                        else
                            clientGuardSan = clientGuardSan.replace(guardSan, "");
                        Query query = entityManager.createNativeQuery("update CF_Clients set GuardSan = :guardSan where IdOfClient = :idOfClient");
                        query.setParameter("guardSan", clientGuardSan);
                        query.setParameter("idOfClient", idOfClient);
                        query.executeUpdate();
                        data.resultCode = RC_OK;
                        data.description = "Ok";
                    }
                }
            }
        }
    }


    private boolean isGuardSanExists(String guardSan, String clientGuardSans) {
        String[] guardSans = clientGuardSans.split(";");
        for (String gs : guardSans)
            if (gs.equals(guardSan))
                return true;
        return false;
    }

    XMLGregorianCalendar toXmlDateTime(Date date) throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xc = DatatypeFactory.newInstance()
                .newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        xc.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return xc;
    }

    @Override
    public Long getContractIdByCardNo(@WebParam(name = "cardId") String cardId) {
        long lCardId = Long.parseLong(cardId);
        try {
            return DAOService.getInstance().getContractIdByCardNo(lCardId);
        } catch (Exception e) {
            logger.error("ClientRoomController failed", e);
            return null;
        }
    }

    @Override
    public ClientSummaryExt[] getSummaryByGuardSan(String guardSan) {
        ClientsData cd = getClientsByGuardSan(guardSan);
        LinkedList<ClientSummaryExt> clientSummaries = new LinkedList<ClientSummaryExt>();
        if (cd!=null && cd.clientList!=null) {
            for (ClientItem ci : cd.clientList.getClients()) {
                ClientSummaryResult cs = getSummary(ci.getContractId());
                if (cs.clientSummary!=null) {
                    clientSummaries.add(cs.clientSummary);
                }
            }
        }
        return clientSummaries.toArray(new ClientSummaryExt[0]);
    }

    @Override
    public Result enableNotificationBySMS(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "state") boolean state) {
        Result r = new Result();
        r.resultCode=RC_OK;
        r.description=RC_OK_DESC;
        if (!DAOService.getInstance().enableClientNotificationBySMS(contractId, state)) {
            r.resultCode=RC_CLIENT_NOT_FOUND;
            r.description=RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result enableNotificationByEmail(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "state") boolean state) {
        Result r = new Result();
        r.resultCode=RC_OK;
        r.description=RC_OK_DESC;
        if (!DAOService.getInstance().enableClientNotificationByEmail(contractId, state)) {
            r.resultCode=RC_CLIENT_NOT_FOUND;
            r.description=RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeMobilePhone(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "mobilePhone") String mobilePhone) {
        Result r = new Result();
        r.resultCode=RC_OK;
        r.description=RC_OK_DESC;
        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
        if (mobilePhone==null) {
            r.resultCode=RC_INVALID_DATA;
            r.description="Неверный формат телефона";
            return r;
        }
        if (!DAOService.getInstance().setClientMobilePhone(contractId, mobilePhone)) {
            r.resultCode=RC_CLIENT_NOT_FOUND;
            r.description=RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeEmail(@WebParam(name = "contractId") Long contractId, @WebParam(name = "email") String email) {
        Result r = new Result();
        r.resultCode=RC_OK;
        r.description=RC_OK_DESC;
        if (!DAOService.getInstance().setClientEmail(contractId, email)) {
            r.resultCode=RC_CLIENT_NOT_FOUND;
            r.description=RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeExpenditureLimit(@WebParam(name = "contractId") Long contractId, @WebParam(name = "limit") long limit) {
        Result r = new Result(RC_OK, RC_OK_DESC);
        if (limit<0) {
            r = new Result(RC_INVALID_DATA, "Лимит не может быть меньше нуля");
            return r;
        }
        if (!DAOService.getInstance().setClientExpenditureLimit(contractId, limit)) {
            r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
        }
        return r;
    }
}