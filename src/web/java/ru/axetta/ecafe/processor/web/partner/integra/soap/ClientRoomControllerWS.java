/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.ui.PaymentTextUtils;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
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
    private static final Long RC_INTERNAL_ERROR = 100L, RC_OK = 0L;

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
                    data.setDescription("Client not found");
                } else {
                    processor.process(client, data, objectFactory, persistenceSession, persistenceTransaction);
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
    public ClientSummaryExt getSummary(Long contractId) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) {
                ClientSummaryExt clientSummaryExt = objectFactory.createClientSummaryExt();
                clientSummaryExt.setBalance(client.getBalance());
                clientSummaryExt.setOverdraftLimit(client.getLimit());
                clientSummaryExt.setStateOfContract(Client.CONTRACT_STATE_NAMES[client.getContractState()]);
                clientSummaryExt.setExpenditureLimit(client.getExpenditureLimit());
                data.setClientSummaryExt(clientSummaryExt);
            }
        });
        return data.getClientSummaryExt();
    }

    final static int MAX_RECS = 50;

    @Override
    public PurchaseListExt getPurchaseList(Long contractId, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
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
        });
        return data.getPurchaseListExt();
    }

    @Override
    public PaymentList getPaymentList(Long contractId, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
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
        });
        return data.getPaymentList();
    }

    @Override
    public MenuListExt getMenuList(Long contractId, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
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
        });
        return data.getMenuListExt();
    }

    @Override
    public CardList getCardList(Long contractId) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
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
        });
        return data.getCardList();
    }

    @Override
    public EnterEventList getEnterEventList(Long contractId, final Date startDate, final Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                Date nextToEndDate = DateUtils.addDays(endDate, 1);
                Criteria enterEventCriteria = session.createCriteria(EnterEvent.class);
                enterEventCriteria.add(Restrictions.eq("idOfClient", client.getIdOfClient()));
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
        });
        return data.getEnterEventList();
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
}