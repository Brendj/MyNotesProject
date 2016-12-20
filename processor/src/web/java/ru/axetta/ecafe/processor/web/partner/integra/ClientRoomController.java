/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra;

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
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller("ClientRoomController")
@RequestMapping("/client")
@Deprecated // Что это такое и где это используется.
public class ClientRoomController {
    final Logger logger = LoggerFactory.getLogger(ClientRoomController.class);
    final static String XML_VIEW_NAME="xmlView";
    private static final Long RC_CLIENT_NOT_FOUND = 110L;
    private static final Long RC_INTERNAL_ERROR = 100L, RC_OK=0L;

    interface Processor {
        public void process(Client client, Data data, ObjectFactory objectFactory, Session persistenceSession,
                Transaction transaction)
                throws Exception;
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
                if (client==null) {
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

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(method= RequestMethod.GET, value="/{contractId}/summary")
    public ModelAndView getSummary(@PathVariable Long contractId) {
        Data data=new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction) {
                ClientSummary clientSummary = objectFactory.createClientSummary();
                clientSummary.setBalance(client.getBalance());
                clientSummary.setOverdraftLimit(client.getLimit());
                clientSummary.setStateOfContract(Client.CONTRACT_STATE_NAMES[client.getContractState()]);
                data.setClientSummary(clientSummary);
            }
        });
        return new ModelAndView(XML_VIEW_NAME, "object", data);
    }
    
    @RequestMapping(method= RequestMethod.GET, value="/{contractId}/summaryext")
        public ModelAndView getSummaryExt(@PathVariable Long contractId) {
            Data data=new ClientRequest().process(contractId, new Processor() {
                public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction) {
                    ClientSummaryExt clientSummaryExt = objectFactory.createClientSummaryExt();
                    clientSummaryExt.setBalance(client.getBalance());
                    clientSummaryExt.setOverdraftLimit(client.getLimit());
                    clientSummaryExt.setStateOfContract(Client.CONTRACT_STATE_NAMES[client.getContractState()]);
                    clientSummaryExt.setExpenditureLimit(client.getExpenditureLimit());
                    data.setClientSummaryExt(clientSummaryExt);
                }
            });
            return new ModelAndView(XML_VIEW_NAME, "object", data);
        }

    final static int MAX_RECS=50;
    @RequestMapping(method= RequestMethod.GET, value="/{contractId}/purchase/list")
    public ModelAndView getPurchaseList(@PathVariable Long contractId, final @RequestParam("startDate") Date startDate, final @RequestParam("endDate") Date endDate) {
        Data data=new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction)
                    throws Exception {
                int nRecs=0;
                Date nextToEndDate = DateUtils.addDays(endDate, 1);
                Criteria ordersCriteria = session.createCriteria(Order.class);
                ordersCriteria.add(Restrictions.eq("client", client));
                ordersCriteria.add(Restrictions.ge("createTime", startDate));
                ordersCriteria.add(Restrictions.lt("createTime", nextToEndDate));
                ordersCriteria.addOrder(org.hibernate.criterion.Order.asc("createTime"));
                List ordersList = ordersCriteria.list();
                PurchaseList purchaseList = objectFactory.createPurchaseList();
                for (Object o : ordersList) {
                    if (nRecs++>MAX_RECS) break;
                    Order order = (Order)o;
                    Purchase purchase = objectFactory.createPurchase();
                    purchase.setByCard(order.getSumByCard());
                    purchase.setDiscount(order.getSocDiscount() + order.getTrdDiscount());
                    purchase.setDonation(order.getGrantSum());
                    purchase.setSum(order.getRSum());
                    purchase.setByCash(order.getSumByCash());
                    purchase.setIdOfCard(order.getCard().getCardPrintedNo());
                    purchase.setTime(toXmlDateTime(order.getCreateTime()));

                    Set<OrderDetail> orderDetailSet=((Order) o).getOrderDetails();
                    for (OrderDetail od : orderDetailSet) {
                        PurchaseElement purchaseElement = objectFactory.createPurchaseElement();
                        purchaseElement.setAmount(od.getQty());
                        purchaseElement.setName(od.getMenuDetailName());
                        purchaseElement.setSum(od.getRPrice());
                        purchase.getE().add(purchaseElement);
                    }

                    purchaseList.getP().add(purchase);
                }
                data.setPurchaseList(purchaseList);
            }
        });
        return new ModelAndView(XML_VIEW_NAME, "object", data);
    }

    @RequestMapping(method= RequestMethod.GET, value="/{contractId}/purchase/listext")
        public ModelAndView getPurchaseListExt(@PathVariable Long contractId, final @RequestParam("startDate") Date startDate, final @RequestParam("endDate") Date endDate) {
            Data data=new ClientRequest().process(contractId, new Processor() {
                public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction)
                        throws Exception {
                    int nRecs=0;
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
            return new ModelAndView(XML_VIEW_NAME, "object", data);
        }

    @RequestMapping(method= RequestMethod.GET, value="/{contractId}/payment/list")
    public ModelAndView getPaymentList(@PathVariable Long contractId, final @RequestParam("startDate") Date startDate, final @RequestParam("endDate") Date endDate) {
        Data data=new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction)
                throws Exception {
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
                int nRecs=0;
                for (Object o : clientPaymentsList) {
                    if (nRecs++>MAX_RECS) break;
                    ClientPayment cp = (ClientPayment)o;
                    Payment payment = new Payment();
                    payment.setOrigin(PaymentTextUtils.buildTransferInfo(session, cp));
                    payment.setSum(cp.getPaySum());
                    payment.setTime(toXmlDateTime(cp.getCreateTime()));
                    paymentList.getP().add(payment);
                }
                data.setPaymentList(paymentList);
            }
        });
        return new ModelAndView(XML_VIEW_NAME, "object", data);
    }

    @RequestMapping(method= RequestMethod.GET, value="/{contractId}/menu/list")
    public ModelAndView getMenuList(@PathVariable Long contractId, final @RequestParam("startDate") Date startDate, final @RequestParam("endDate") Date endDate) {
        Data data=new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction)
                throws Exception {
                Criteria menuCriteria = session.createCriteria(Menu.class);
                menuCriteria.add(Restrictions.eq("org", client.getOrg()));
                menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
                menuCriteria.add(Restrictions.ge("menuDate", startDate));
                menuCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 1)));

                List menus = menuCriteria.list();
                MenuList menuList = objectFactory.createMenuList();
                int nRecs=0;
                for (Object currObject : menus) {
                    if (nRecs++>MAX_RECS) break;

                    Menu menu = (Menu)currObject;
                    MenuDateItem menuDateItem = objectFactory.createMenuDateItem();
                    menuDateItem.setDate(toXmlDateTime(menu.getMenuDate()));

                    Criteria menuDetailCriteria = session.createCriteria(MenuDetail.class);
                    menuDetailCriteria.add(Restrictions.eq("menu", menu));
                    HibernateUtils.addAscOrder(menuDetailCriteria, "groupName");
                    HibernateUtils.addAscOrder(menuDetailCriteria, "menuDetailName");
                    List menuDetails = menuDetailCriteria.list();

                    for (Object o : menuDetails) {
                        MenuDetail menuDetail = (MenuDetail)o;
                        MenuItem menuItem = objectFactory.createMenuItem();
                        menuItem.setGroup(menuDetail.getGroupName());
                        menuItem.setName(menuDetail.getMenuDetailName());
                        menuItem.setPrice(menuDetail.getPrice());
                        menuDateItem.getE().add(menuItem);
                    }

                    menuList.getM().add(menuDateItem);
                }
                data.setMenuList(menuList);
            }
        });
        return new ModelAndView(XML_VIEW_NAME, "object", data);
    }

    @RequestMapping(method= RequestMethod.GET, value="/{contractId}/menu/listext")
    public ModelAndView getMenuListExt(@PathVariable Long contractId, final @RequestParam("startDate") Date startDate, final @RequestParam("endDate") Date endDate) {
        Data data=new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction)
                throws Exception {
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
        return new ModelAndView(XML_VIEW_NAME, "object", data);
    }
    
    @RequestMapping(method= RequestMethod.GET, value="/{contractId}/card/list")
    public ModelAndView getCardList(@PathVariable Long contractId) {
        Data data=new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction)
                throws Exception {
                Set<Card> cardSet = client.getCards();
                CardList cardList = objectFactory.createCardList();
                for (Card card : cardSet) {
                    CardItem cardItem = objectFactory.createCardItem();
                    cardItem.setState(card.getState());
                    cardItem.setType(card.getCardType());
                    cardItem.setChangeDate(toXmlDateTime(card.getUpdateTime()));
                    cardItem.setCrystalId(card.getCardNo());
                    cardItem.setIdOfCard(card.getCardPrintedNo());
                    cardItem.setLifeState(card.getLifeState());
                    cardItem.setExpiryDate(toXmlDateTime(card.getValidTime()));
                    cardList.getC().add(cardItem);
                }
                data.setCardList(cardList);
            }
        });
        return new ModelAndView(XML_VIEW_NAME, "object", data);
    }

    @RequestMapping(method=RequestMethod.GET, value="/{contractId}/enterevent/list")
    public ModelAndView getEnterEventList(@PathVariable Long contractId, final @RequestParam("startDate") Date startDate, final @RequestParam("endDate") Date endDate) {
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Data data, ObjectFactory objectFactory, Session session, Transaction transaction)
                    throws Exception {
                Date nextToEndDate = DateUtils.addDays(endDate, 1);
                Criteria enterEventCriteria = session.createCriteria(EnterEvent.class);
                enterEventCriteria.add(Restrictions.eq("client", client.getIdOfClient()));
                enterEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
                enterEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
                enterEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));

                Locale locale = new Locale("ru","RU");
                Calendar calendar = Calendar.getInstance(locale);

                List<EnterEvent> enterEvents = enterEventCriteria.list();
                EnterEventList enterEventList = objectFactory.createEnterEventList();
                int nRecs = 0;
                for (EnterEvent enterEvent : enterEvents) {
                    if (nRecs++>MAX_RECS) break;
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
        return new ModelAndView(XML_VIEW_NAME, "object", data);
    }

    XMLGregorianCalendar toXmlDateTime(Date date) throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xc=DatatypeFactory.newInstance().newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),
          DatatypeConstants.FIELD_UNDEFINED);
        xc.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return xc;
    }
}
