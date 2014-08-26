/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ClientPasswordRecover;
import ru.axetta.ecafe.processor.core.client.ClientStatsReporter;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.client.RequestWebParam;
import ru.axetta.ecafe.processor.core.daoservices.questionary.QuestionaryService;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.FinancialOpsManager;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig;
import ru.axetta.ecafe.processor.core.partner.integra.IntegraPartnerConfig;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.StateDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Circulation;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Publication;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.questionary.ClientAnswerByQuestionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryStatus;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientGuardSanRebuildService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.SubscriptionFeedingService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ParameterStringUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.ui.PaymentTextUtils;

import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.*;

import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.truncateToDayOfMonth;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 12.12.11
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */

@WebService()
public class ClientRoomControllerWS extends HttpServlet implements ClientRoomController {

    @Resource
    private WebServiceContext context;

    private static final Logger logger = LoggerFactory.getLogger(ClientRoomControllerWS.class);

    private static final Long RC_CLIENT_AUTHORIZATION_FAILED = -101L;
    private static final Long RC_PARTNER_AUTHORIZATION_FAILED = -100L;
    private static final Long RC_OK = 0L;
    private static final Long RC_INTERNAL_ERROR = 100L;
    private static final Long RC_CLIENT_NOT_FOUND = 110L;
    private static final Long RC_SEVERAL_CLIENTS_WERE_FOUND = 120L;
    private static final Long RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS = 130L;
    private static final Long RC_CLIENT_HAS_THIS_SNILS_ALREADY = 140L;
    private static final Long RC_INVALID_DATA = 150L;
    private static final Long RC_NO_CONTACT_DATA = 160L;
    private static final Long RC_CLIENT_FINANCIAL_OPERATION_ERROR = 170L;
    private static final Long RC_SETTINGS_NOT_FOUND = 180L;
    private static final Long RC_SUBSCRIPTION_FEEDING_DUPLICATE = 190L;
    private static final Long RC_LACK_OF_SUBBALANCE1 = 200L;
    private static final Long RC_ERROR_CREATE_SUBSCRIPTION_FEEDING = 210L;
    private static final Long RC_SUBSCRIPTION_FEEDING_NOT_FOUND = 230L;
    private static final Long RC_PROHIBIT_EXIST = 300L;
    private static final Long RC_PROHIBIT_NOT_FOUND = 310L;
    private static final Long RC_PROHIBIT_REMOVED = 320L;

    private static final String RC_OK_DESC = "OK";
    private static final String RC_CLIENT_NOT_FOUND_DESC = "Клиент не найден";
    private static final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC = "По условиям найден более одного клиента";
    private static final String RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC = "У клиента нет СНИЛС опекуна";
    private static final String RC_CLIENT_HAS_THIS_SNILS_ALREADY_DESC = "У клиента уже есть данный СНИЛС опекуна";
    private static final String RC_CLIENT_AUTHORIZATION_FAILED_DESC = "Ошибка авторизации клиента";
    private static final String RC_INTERNAL_ERROR_DESC = "Внутренняя ошибка";
    private static final String RC_NO_CONTACT_DATA_DESC = "У лицевого счета нет контактных данных";
    private static final String RC_DO_NOT_ACCESS_TO_SUB_BALANCE_DESC = "Нет доступа к субсчетам";
    private static final String RC_SUBSCRIPTION_FEEDING_DUPLICATE_DESC = "У клиента уже есть активная подписка на АП.";
    private static final String RC_LACK_OF_SUBBALANCE1_DESC = "У клиента недостаточно средств на субсчете АП";
    private static final String RC_ERROR_CREATE_SUBSCRIPTION_FEEDING_DESC = "Не верная дата активация циклограммы";
    private static final String RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC = "Услуга не подключена";
    private static final String RC_PROHIBIT_EXIST_DESC = "Запрет с данными параметрами уже существует";
    private static final String RC_PROHIBIT_REMOVED_DESC = "Запрет с данными параметрами был удален";
    private static final String RC_PROHIBIT_NOT_FOUND_DESC = "Запрет с данными параметрами не найден";
    private static final int MAX_RECS = 50;

    public static final int CIRCULATION_STATUS_FILTER_ALL = -1, CIRCULATION_STATUS_FILTER_ALL_ON_HANDS = -2;

    static class Processor {

        public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
              Session persistenceSession, Transaction transaction) throws Exception {
        }

        public void process(Org org, Data data, ObjectFactory objectFactory, Session persistenceSession,
              Transaction transaction) throws Exception {
        }
    }

    @Override
    public Result transferBalance(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "fromSub") Integer fromSub, @WebParam(name = "toSub") Integer toSub,
          @WebParam(name = "amount") Long amount) {

        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        Boolean enableSubBalanceOperation = RuntimeContext.getInstance()
              .getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
        if (enableSubBalanceOperation) {

            Client client;
            final DAOService instance = DAOService.getInstance();
            try {
                client = instance.getClientByContractId(contractId);
            } catch (Exception e) {
                logger.error("INTERNAL ERROR", e);
                r.resultCode = RC_INTERNAL_ERROR;
                r.description = RC_INTERNAL_ERROR_DESC;
                return r;
            }
            if (client == null) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
                return r;
            }

            try {
                FinancialOpsManager financialOpsManager = RuntimeContext.getAppContext()
                      .getBean(FinancialOpsManager.class);
                financialOpsManager.createSubAccountTransfer(client, fromSub, toSub, amount);
            } catch (FinancialOpsManager.AccountTransactionException ate) {
                r.resultCode = RC_CLIENT_FINANCIAL_OPERATION_ERROR;
                r.description = ate.getMessage();
                logger.error("Failed to process client room controller request", ate);
            } catch (Exception e) {
                r.resultCode = RC_INTERNAL_ERROR;
                r.description = RC_INTERNAL_ERROR_DESC;
                logger.error("Failed to process client room controller request", e);
            }
        } else {
            r.resultCode = RC_CLIENT_FINANCIAL_OPERATION_ERROR;
            r.description = RC_DO_NOT_ACCESS_TO_SUB_BALANCE_DESC;
        }

        return r;
    }

    @Override
    public Result transferBalance(@WebParam(name = "contractId") String san,
          @WebParam(name = "fromSub") Integer fromSub, @WebParam(name = "toSub") Integer toSub,
          @WebParam(name = "amount") Long amount) {

        authenticateRequest(null);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        Boolean enableSubBalanceOperation = RuntimeContext.getInstance()
              .getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
        if (enableSubBalanceOperation) {
            List<Client> clients = new ArrayList<Client>();
            final DAOService instance = DAOService.getInstance();
            try {
                clients = instance.findClientsBySan(san);
            } catch (Exception e) {
                logger.error("INTERNAL ERROR", e);
                r.resultCode = RC_INTERNAL_ERROR;
                r.description = RC_INTERNAL_ERROR_DESC;
                return r;
            }
            if (clients.size() > 1) {
                r.resultCode = RC_SEVERAL_CLIENTS_WERE_FOUND;
                r.description = RC_SEVERAL_CLIENTS_WERE_FOUND_DESC;
                return r;
            }
            if (clients.isEmpty() || clients.get(0) == null) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
                return r;
            }
            Client client = clients.get(0);
            try {
                FinancialOpsManager financialOpsManager = RuntimeContext.getAppContext()
                      .getBean(FinancialOpsManager.class);
                financialOpsManager.createSubAccountTransfer(client, fromSub, toSub, amount);
            } catch (FinancialOpsManager.AccountTransactionException ate) {
                r.resultCode = RC_CLIENT_FINANCIAL_OPERATION_ERROR;
                r.description = ate.getMessage();
                logger.error("Failed to process client room controller request", ate);
            } catch (Exception e) {
                r.resultCode = RC_INTERNAL_ERROR;
                r.description = RC_INTERNAL_ERROR_DESC;
                logger.error("Failed to process client room controller request", e);
            }
        } else {
            r.resultCode = RC_CLIENT_FINANCIAL_OPERATION_ERROR;
            r.description = RC_DO_NOT_ACCESS_TO_SUB_BALANCE_DESC;
        }

        return r;
    }

    @Override
    public ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByOrg(Long orgId) {
        return getListOfComplaintBookEntriesByCriteria(orgId, null);
    }

    @Override
    public ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByClient(Long contractId) {
        return getListOfComplaintBookEntriesByCriteria(null, contractId);
    }

    private ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByCriteria(Long orgId, Long contractId) {
        authenticateRequest(null);

        ListOfComplaintBookEntriesResult result = new ListOfComplaintBookEntriesResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Set<Long> setIdOfOrgs = null;
            Client client = null;
            if ((orgId != null) && (contractId == null)) {
                setIdOfOrgs = new HashSet<Long>();
                Org org = (Org) persistenceSession.load(Org.class, orgId);
                if (org == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Организация с указанным идентификатором не найдена";
                    return result;
                }
                setIdOfOrgs.add(orgId);
                List<Long> longList = DAOUtils.getListIdOfOrgList(persistenceSession, org.getIdOfOrg());
                setIdOfOrgs.addAll(longList);
                for (Org friendlyOrg : org.getFriendlyOrg()) {
                    setIdOfOrgs.add(friendlyOrg.getIdOfOrg());
                }
            } else if ((orgId == null) && (contractId != null)) {
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                clientCriteria.add(Restrictions.eq("contractId", contractId));
                client = (Client) clientCriteria.uniqueResult();
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }
            } else {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = RC_INTERNAL_ERROR_DESC;
                return result;
            }

            ListOfComplaintBookEntries listOfComplaintBookEntries = new ListOfComplaintBookEntries();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria bookCriteria = persistenceSession.createCriteria(GoodComplaintBook.class);
            if (setIdOfOrgs != null) {
                bookCriteria.add(Restrictions.in("orgOwner", setIdOfOrgs));
            } else {
                bookCriteria.add(Restrictions.eq("client", client));
            }
            List bookEntries = bookCriteria.list();
            if (!bookEntries.isEmpty()) {
                for (Object entry : bookEntries) {
                    GoodComplaintBook goodComplaintBook = (GoodComplaintBook) entry;
                    ListOfComplaintBookEntriesExt listOfComplaintBookEntriesExt = objectFactory
                          .createListOfComplaintBookEntriesExt();

                    listOfComplaintBookEntriesExt.setContractId(goodComplaintBook.getClient().getContractId());
                    listOfComplaintBookEntriesExt.setGuid(goodComplaintBook.getGuid());
                    listOfComplaintBookEntriesExt.setDeletedState(goodComplaintBook.getDeletedState());
                    listOfComplaintBookEntriesExt
                          .setCreatedDate(getXMLGregorianCalendarByDate(goodComplaintBook.getCreatedDate()));
                    listOfComplaintBookEntriesExt.setOrgOwner(goodComplaintBook.getOrgOwner());
                    listOfComplaintBookEntriesExt.setGuidOfGood(goodComplaintBook.getGood().getGuid());
                    listOfComplaintBookEntriesExt.setNameOfGood(goodComplaintBook.getGood().getNameOfGood());

                    ListOfComplaintIterations listOfComplaintIterations = new ListOfComplaintIterations();
                    listOfComplaintBookEntriesExt.getIterations().add(listOfComplaintIterations);

                    Criteria iterationCriteria = persistenceSession.createCriteria(GoodComplaintIterations.class);
                    iterationCriteria.add(Restrictions.eq("complaint", goodComplaintBook));
                    List iterations = iterationCriteria.list();
                    if (!iterations.isEmpty()) {
                        for (Object iterObject : iterations) {
                            GoodComplaintIterations iteration = (GoodComplaintIterations) iterObject;
                            ListOfComplaintIterationsExt listOfComplaintIterationsExt = objectFactory
                                  .createListOfComplaintIterationsExt();

                            listOfComplaintIterationsExt.setIterationNumber(iteration.getIterationNumber());
                            listOfComplaintIterationsExt
                                  .setIterationStatus(iteration.getGoodComplaintIterationStatus().getStatusNumber());
                            listOfComplaintIterationsExt.setProblemDescription(iteration.getProblemDescription());
                            listOfComplaintIterationsExt.setConclusion(iteration.getConclusion());
                            listOfComplaintIterationsExt.setGuid(iteration.getGuid());
                            listOfComplaintIterationsExt.setDeletedState(iteration.getDeletedState());
                            listOfComplaintIterationsExt
                                  .setCreatedDate(getXMLGregorianCalendarByDate(iteration.getCreatedDate()));
                            listOfComplaintIterationsExt.setOrgOwner(iteration.getOrgOwner());

                            ListOfComplaintOrders listOfComplaintOrders = new ListOfComplaintOrders();
                            listOfComplaintIterationsExt.getOrders().add(listOfComplaintOrders);

                            Criteria orderCriteria = persistenceSession.createCriteria(GoodComplaintOrders.class);
                            orderCriteria.add(Restrictions.eq("complaintIteration", iteration));
                            List orders = orderCriteria.list();
                            for (Object orderObject : orders) {
                                GoodComplaintOrders order = (GoodComplaintOrders) orderObject;
                                ListOfComplaintOrdersExt listOfComplaintOrdersExt = objectFactory
                                      .createListOfComplaintOrdersExt();

                                OrderDetail orderDetail = order.getOrderDetail();
                                listOfComplaintOrdersExt.setIdOfOrderDetail(
                                      orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                                listOfComplaintOrdersExt.setMenuDetailName(orderDetail.getMenuDetailName());
                                listOfComplaintOrdersExt.setDateOfOrder(
                                      getXMLGregorianCalendarByDate(order.getOrderDetail().getOrder().getCreateTime()));
                                listOfComplaintOrdersExt.setGuid(order.getGuid());
                                listOfComplaintOrdersExt
                                      .setCreatedDate(getXMLGregorianCalendarByDate(order.getCreatedDate()));
                                listOfComplaintOrdersExt.setDeletedState(order.getDeletedState());
                                listOfComplaintOrdersExt.setOrgOwner(order.getOrgOwner());

                                listOfComplaintOrders.getO().add(listOfComplaintOrdersExt);
                            }

                            ListOfComplaintCauses listOfComplaintCauses = new ListOfComplaintCauses();
                            listOfComplaintIterationsExt.getCauses().add(listOfComplaintCauses);

                            Criteria causeCriteria = persistenceSession.createCriteria(GoodComplaintCauses.class);
                            causeCriteria.add(Restrictions.eq("complaintIteration", iteration));
                            List causes = causeCriteria.list();
                            for (Object causeObject : causes) {
                                GoodComplaintCauses cause = (GoodComplaintCauses) causeObject;
                                ListOfComplaintCausesExt listOfComplaintCausesExt = objectFactory
                                      .createListOfComplaintCausesExt();

                                listOfComplaintCausesExt.setCause(cause.getCause().getCauseNumber());
                                listOfComplaintCausesExt.setCauseDescription(cause.getTitle());
                                listOfComplaintCausesExt.setGuid(cause.getGuid());
                                listOfComplaintCausesExt
                                      .setCreatedDate(getXMLGregorianCalendarByDate(cause.getCreatedDate()));
                                listOfComplaintCausesExt.setDeletedState(cause.getDeletedState());
                                listOfComplaintCausesExt.setOrgOwner(cause.getOrgOwner());

                                listOfComplaintCauses.getC().add(listOfComplaintCausesExt);
                            }

                            listOfComplaintIterations.getI().add(listOfComplaintIterationsExt);
                        }
                    }

                    listOfComplaintBookEntries.getE().add(listOfComplaintBookEntriesExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.listOfComplaintBookEntries = listOfComplaintBookEntries;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public IdResult openComplaint(Long contractId, Long orderOrgId, List<Long> orderDetailIdList,
          List<Integer> causeNumberList, String description) {
        authenticateRequest(null);

        IdResult result = new IdResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

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
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            Criteria orderOrgCriteria = persistenceSession.createCriteria(Org.class);
            orderOrgCriteria.add(Restrictions.eq("idOfOrg", orderOrgId));
            Org orderOrg = (Org) orderOrgCriteria.uniqueResult();
            if (orderOrg == null) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = "Организации с указанным идентификатором не существует";
                return result;
            }

            // Проверяем, ссылаются ли все элементы списка деталей заказов на один и тот же товар,
            // и запоминаем этот товар
            if ((orderDetailIdList == null) || orderDetailIdList.isEmpty()) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = "Список деталей заказов не может быть пустым";
                return result;
            }
            Good problematicGood = null;
            List<GoodComplaintOrders> goodComplaintOrdersList = new ArrayList<GoodComplaintOrders>(
                  orderDetailIdList.size());
            for (Long idOfOrderDetail : orderDetailIdList) {
                CompositeIdOfOrderDetail compositeIdOfOrderDetail = new CompositeIdOfOrderDetail(orderOrg.getIdOfOrg(),
                      idOfOrderDetail);
                OrderDetail orderDetail = DAOUtils.findOrderDetail(persistenceSession, compositeIdOfOrderDetail);
                if (orderDetail == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description =
                          "Не найден элемент деталей заказов с указанным идентификатором и номером " + "организации";
                    return result;
                }

                // Проверка, существует ли Заказ, совершенный указанным клиентом и содержащий данную деталь заказа
                CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(client.getOrg().getIdOfOrg(),
                      orderDetail.getIdOfOrder());
                Criteria orderCriteria = persistenceSession.createCriteria(Order.class);
                orderCriteria.add(Restrictions.eq("compositeIdOfOrder", compositeIdOfOrder));
                Order order = (Order) orderCriteria.uniqueResult();
                if ((order == null) || !order.getClient().equals(client)) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Указанный клиент не совершал данного заказа";
                    return result;
                }

                Good goodFromOrder = orderDetail.getGood();
                if (goodFromOrder == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "У переданного элемента списка деталей заказов с идентификатором " +
                          +orderDetail.getIdOfOrder() + " не указана ссылка на товар";
                    return result;
                }

                if (problematicGood == null) {
                    problematicGood = goodFromOrder;
                } else if (!goodFromOrder.equals(problematicGood)) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description =
                          "Требуется передавать список деталей заказа, " + "ссылающиеся на один и тот же товар";
                    return result;
                }

                GoodComplaintOrders goodComplaintOrders = new GoodComplaintOrders();
                goodComplaintOrders.setOrderOrg(orderOrg);
                goodComplaintOrders.setOrderDetail(orderDetail);
                fillDisctributedObjectsCommonDetails(goodComplaintOrders, client.getOrg());

                goodComplaintOrdersList.add(goodComplaintOrders);
            }

            // Проверяем, подавалась ли ранее указанным клиентом жалоба на данный товар
            Criteria goodComplaintBookCriteria = persistenceSession.createCriteria(GoodComplaintBook.class);
            goodComplaintBookCriteria.add(Restrictions.eq("client", client));
            goodComplaintBookCriteria.add(Restrictions.eq("good", problematicGood));
            GoodComplaintBook goodComplaintBook = (GoodComplaintBook) goodComplaintBookCriteria.uniqueResult();

            int newIterationNumber;
            if (goodComplaintBook == null) {
                goodComplaintBook = new GoodComplaintBook();
                goodComplaintBook.setClient(client);
                goodComplaintBook.setGood(problematicGood);
                fillDisctributedObjectsCommonDetails(goodComplaintBook, client.getOrg());

                persistenceSession.save(goodComplaintBook);

                newIterationNumber = 0;
            } else {
                // Нахождение последней итерации по жалобе указанного клиента на данный товар
                // и проверка, было ли вынесено заключение по текущей итерации, т.к. до вынесения заключенися
                // переоткрывать жалобу запрещено
                Criteria iterationCriteria = persistenceSession.createCriteria(GoodComplaintIterations.class);
                iterationCriteria.add(Restrictions.eq("complaint", goodComplaintBook));
                List iterationObjects = iterationCriteria.list();
                GoodComplaintIterations lastIteration = null;
                for (Object iterationObject : iterationObjects) {
                    GoodComplaintIterations currentIteration = (GoodComplaintIterations) iterationObject;
                    if ((null == lastIteration) || (currentIteration.getIterationNumber() > lastIteration
                          .getIterationNumber())) {
                        lastIteration = currentIteration;
                    }
                }
                if (lastIteration == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = RC_INTERNAL_ERROR_DESC;
                    return result;
                }
                if (!GoodComplaintIterationStatus.conclusion.equals(lastIteration.getGoodComplaintIterationStatus())) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "По жалобе с указанным идентификатором еще не было вынесено заключения";
                    return result;
                }
                newIterationNumber = lastIteration.getIterationNumber() + 1;
            }

            // Выяснили, какой номер итерации назначать новой итерации
            GoodComplaintIterations newIteration = new GoodComplaintIterations();
            newIteration.setComplaint(goodComplaintBook);
            newIteration.setIterationNumber(newIterationNumber);
            newIteration.setProblemDescription(description);
            newIteration.setGoodComplaintIterationStatus(GoodComplaintIterationStatus.creation);
            fillDisctributedObjectsCommonDetails(newIteration, client.getOrg());
            persistenceSession.save(newIteration);

            // Получили сохраненную итерацию, следовательно, можно привязать к этой итерации
            // элементы деталей заказов из списка, полученного ранее, и сохранить их
            for (GoodComplaintOrders order : goodComplaintOrdersList) {
                order.setComplaintIteration(newIteration);
                persistenceSession.save(order);
            }

            // Осталось записать причины подачи жалобы
            if (causeNumberList == null) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = "Не указаны причины подачи жалобы";
                return result;
            }
            List<Integer> parsedCauseNumberList = new ArrayList<Integer>();
            for (Integer causeNumber : causeNumberList) {
                if (parsedCauseNumberList.contains(causeNumber)) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Список причин подачи жалобы должен содержать уникальные значения";
                    return result;
                } else {
                    parsedCauseNumberList.add(causeNumber);
                }
                GoodComplaintPossibleCauses cause = GoodComplaintPossibleCauses.getCauseByNumberNullSafe(causeNumber);
                if (cause == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description =
                          "Номер причины подачи жалобы " + causeNumber + " не определен в списке возможных причин";
                    return result;
                }
                GoodComplaintCauses goodComplaintCauses = new GoodComplaintCauses();
                goodComplaintCauses.setCause(cause);
                goodComplaintCauses.setComplaintIteration(newIteration);
                fillDisctributedObjectsCommonDetails(goodComplaintCauses, client.getOrg());
                persistenceSession.save(goodComplaintCauses);
            }

            result.id = goodComplaintBook.getGlobalId();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return result;
    }

    private void fillDisctributedObjectsCommonDetails(DistributedObject distributedObject, Org org) {
        distributedObject.setOrgOwner(org.getIdOfOrg());
        distributedObject.setCreatedDate(new Date());
        distributedObject.setDeletedState(false);
        distributedObject.setSendAll(SendToAssociatedOrgs.SendToMain);
        distributedObject.setGlobalVersion(
              DAOService.getInstance().updateVersionByDistributedObjects(distributedObject.getClass().getSimpleName()));
    }

    @Override
    public Result changeComplaintStatusToConsideration(Long complaintId) {
        return changeComplaintStatus(complaintId, GoodComplaintIterationStatus.consideration, null);
    }

    @Override
    public Result changeComplaintStatusToInvestigation(Long complaintId) {
        return changeComplaintStatus(complaintId, GoodComplaintIterationStatus.investigation, null);
    }

    @Override
    public Result giveConclusionOnComplaint(Long complaintId, String conclusion) {
        return changeComplaintStatus(complaintId, GoodComplaintIterationStatus.conclusion, conclusion);
    }

    private Result changeComplaintStatus(Long complaintId, GoodComplaintIterationStatus status, String conclusion) {
        authenticateRequest(null);

        Result result = new Result();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria complaintCriteria = persistenceSession.createCriteria(GoodComplaintBook.class);
            complaintCriteria.add(Restrictions.eq("globalId", complaintId));
            GoodComplaintBook goodComplaintBook = (GoodComplaintBook) complaintCriteria.uniqueResult();
            if (goodComplaintBook == null) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = "Жалобы с указанным идентификатором не существует";
                return result;
            }

            Criteria iterationCriteria = persistenceSession.createCriteria(GoodComplaintIterations.class);
            iterationCriteria.add(Restrictions.eq("complaint", goodComplaintBook));
            List iterationObjects = iterationCriteria.list();
            GoodComplaintIterations iteration = null;
            for (Object iterationObject : iterationObjects) {
                GoodComplaintIterations goodComplaintIterations = (GoodComplaintIterations) iterationObject;
                if ((null == iteration) || (goodComplaintIterations.getIterationNumber() > iteration
                      .getIterationNumber())) {
                    iteration = goodComplaintIterations;
                }
            }
            if (iteration == null) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = RC_INTERNAL_ERROR_DESC;
                return result;
            }

            if (status.getStatusNumber() - iteration.getGoodComplaintIterationStatus().getStatusNumber() != 1) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description =
                      "Нельзя назначать итерации со статусом " + iteration.getGoodComplaintIterationStatus().getTitle()
                            + " статус " + status.getTitle();
                return result;
            }
            iteration.setGoodComplaintIterationStatus(status);
            if (conclusion != null) {
                iteration.setConclusion(conclusion);
            }
            persistenceSession.save(iteration);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return result;
    }

    @Override
    public ListOfProductsResult getListOfProducts(Long orgId) {
        authenticateRequest(null);

        ListOfProductsResult result = new ListOfProductsResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Set<Long> setIdOfOrgs = new HashSet<Long>();
            if (orgId != null) {
                Org org = (Org) persistenceSession.load(Org.class, orgId);
                if (org == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Организация не найдена";
                    return result;
                }
                setIdOfOrgs.add(orgId);
                List<Long> longList = DAOUtils.getListIdOfOrgList(persistenceSession, org.getIdOfOrg());
                setIdOfOrgs.addAll(longList);
            }

            ListOfProductGroups listOfProductGroups = new ListOfProductGroups();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria productGroupCriteria = persistenceSession.createCriteria(ProductGroup.class);
            productGroupCriteria.add(Restrictions.in("orgOwner", setIdOfOrgs));
            List groupObjects = productGroupCriteria.list();
            if (!groupObjects.isEmpty()) {
                for (Object groupObject : groupObjects) {
                    ProductGroup productGroup = (ProductGroup) groupObject;
                    ListOfProductGroupsExt listOfProductGroupsExt = objectFactory.createListOfProductGroupsExt();
                    listOfProductGroupsExt.setNameOfGroup(productGroup.getNameOfGroup());
                    listOfProductGroupsExt.setClassificationCode(productGroup.getClassificationCode());
                    listOfProductGroupsExt.setDeletedState(productGroup.getDeletedState());
                    listOfProductGroupsExt.setGuid(productGroup.getGuid());
                    listOfProductGroupsExt.setOrgOwner(productGroup.getOrgOwner());
                    listOfProductGroupsExt.setCreatedDate(getXMLGregorianCalendarByDate(productGroup.getCreatedDate()));
                    ListOfProducts listOfProducts = new ListOfProducts();
                    listOfProductGroupsExt.getProducts().add(listOfProducts);
                    Criteria productCriteria = persistenceSession.createCriteria(Product.class);
                    productCriteria.add(Restrictions.eq("productGroup", productGroup));
                    productCriteria.add(Restrictions.in("orgOwner", setIdOfOrgs));
                    List objects = productCriteria.list();
                    if (!objects.isEmpty()) {
                        for (Object object : objects) {
                            Product product = (Product) object;
                            ListOfProductsExt listOfProductsExt = objectFactory.createListOfProductsExt();
                            listOfProductsExt.setCode(product.getCode());
                            listOfProductsExt.setOkpCode(product.getOkpCode());
                            listOfProductsExt.setClassificationCode(product.getClassificationCode());
                            listOfProductsExt.setDeletedState(product.getDeletedState());
                            listOfProductsExt.setGuid(product.getGuid());
                            listOfProductsExt.setProductName(product.getProductName());
                            listOfProductsExt.setFullName(product.getFullName());
                            listOfProductsExt.setDensity(product.getDensity());
                            listOfProductsExt.setOrgOwner(product.getOrgOwner());
                            listOfProductsExt.setCreatedDate(getXMLGregorianCalendarByDate(product.getCreatedDate()));

                            listOfProducts.getP().add(listOfProductsExt);
                        }
                    }

                    listOfProductGroups.getPG().add(listOfProductGroupsExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.listOfProductGroups = listOfProductGroups;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public ListOfGoodsResult getListOfGoods(Long orgId) {
        authenticateRequest(null);

        ListOfGoodsResult result = new ListOfGoodsResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = null;
            if (orgId != null) {
                Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
                orgCriteria.add(Restrictions.eq("idOfOrg", orgId));
                org = (Org) orgCriteria.uniqueResult();
                if (org == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Организация не найдена";
                    return result;
                }
            }

            ListOfGoodGroups listOfGoodGroups = new ListOfGoodGroups();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria goodGroupCriteria = persistenceSession.createCriteria(GoodGroup.class);
            List groupObjects = goodGroupCriteria.list();
            if (!groupObjects.isEmpty()) {
                for (Object groupObject : groupObjects) {
                    GoodGroup goodGroup = (GoodGroup) groupObject;
                    ListOfGoodGroupsExt listOfGoodGroupsExt = objectFactory.createListOfGoodGroupsExt();
                    listOfGoodGroupsExt.setNameOfGoodsGroup(goodGroup.getNameOfGoodsGroup());
                    listOfGoodGroupsExt.setDeletedState(goodGroup.getDeletedState());
                    listOfGoodGroupsExt.setGuid(goodGroup.getGuid());
                    listOfGoodGroupsExt.setOrgOwner(goodGroup.getOrgOwner());
                    listOfGoodGroupsExt.setCreatedDate(getXMLGregorianCalendarByDate(goodGroup.getCreatedDate()));

                    ListOfGoods listOfGoods = new ListOfGoods();
                    listOfGoodGroupsExt.getGoods().add(listOfGoods);

                    Criteria goodCriteria = persistenceSession.createCriteria(Good.class);
                    goodCriteria.add(Restrictions.eq("goodGroup", goodGroup));
                    if (org != null) {
                        List<Long> menuExchangeRuleList = DAOUtils
                              .getListIdOfOrgList(persistenceSession, org.getIdOfOrg());
                        StringBuffer sqlRestriction = new StringBuffer();
                        for (Long idOfProvider : menuExchangeRuleList) {
                            sqlRestriction.append("orgOwner=");
                            sqlRestriction.append(idOfProvider);
                            sqlRestriction.append(" or ");
                        }
                        sqlRestriction.append("orgOwner=");
                        sqlRestriction.append(org.getIdOfOrg());
                        goodCriteria.add(Restrictions.sqlRestriction(sqlRestriction.toString()));
                    }
                    List objects = goodCriteria.list();
                    if (!objects.isEmpty()) {
                        for (Object object : objects) {
                            Good good = (Good) object;
                            ListOfGoodsExt listOfGoodsExt = objectFactory.createListOfGoodsExt();
                            listOfGoodsExt.setGoodsCode(good.getGoodsCode());
                            listOfGoodsExt.setGuid(good.getGuid());
                            listOfGoodsExt.setDeletedState(good.getDeletedState());
                            listOfGoodsExt.setOrgOwner(good.getOrgOwner());
                            listOfGoodsExt.setNameOfGood(good.getNameOfGood());
                            listOfGoodsExt.setFullName(good.getFullName());
                            listOfGoodsExt.setUnitsScale(good.getUnitsScale().ordinal());
                            listOfGoodsExt.setNetWeight(good.getNetWeight());
                            listOfGoodsExt.setLifetime(good.getLifeTime());
                            listOfGoodsExt.setMargin(good.getMargin());
                            listOfGoodsExt.setCreatedDate(getXMLGregorianCalendarByDate(good.getCreatedDate()));

                            listOfGoods.getG().add(listOfGoodsExt);
                        }
                    }

                    listOfGoodGroups.getGG().add(listOfGoodGroupsExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.listOfGoodGroups = listOfGoodGroups;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;


    }

    @Override
    public ProhibitionsListResult getDishProhibitionsList(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(null);

        ProhibitionsListResult prohibitionsListResult = new ProhibitionsListResult();
        prohibitionsListResult.resultCode = RC_OK;
        prohibitionsListResult.description = RC_OK_DESC;

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
                prohibitionsListResult.resultCode = RC_CLIENT_NOT_FOUND;
                prohibitionsListResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return prohibitionsListResult;
            }

            ProhibitionsList prohibitionsList = new ProhibitionsList();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            prohibitionCriteria.add(Restrictions.eq("client", client));
            List objects = prohibitionCriteria.list();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    Prohibition prohibition = (Prohibition) object;
                    ProhibitionsListExt prohibitionsListExt = objectFactory.createProhibitionsListExt();
                    prohibitionsListExt.setGuid(prohibition.getGuid());
                    prohibitionsListExt.setDeletedState(prohibition.getDeletedState());
                    prohibitionsListExt.setCreatedDate(getXMLGregorianCalendarByDate(prohibition.getCreatedDate()));
                    prohibitionsListExt.setContactId(client.getContractId());
                    Product bannedProduct = prohibition.getProduct();
                    ProductGroup bannedProductGroup = prohibition.getProductGroup();
                    Good bannedGood = prohibition.getGood();
                    GoodGroup bannedGoodGroup = prohibition.getGoodGroup();
                    if (bannedProduct != null) {
                        prohibitionsListExt.setGuidOfProducts(bannedProduct.getGuid());
                    } else if (bannedProductGroup != null) {
                        prohibitionsListExt.setGuidOfProductGroups(bannedProductGroup.getGuid());
                    } else if (bannedGood != null) {
                        prohibitionsListExt.setGuidOfGood(bannedGood.getGuid());
                    } else if (bannedGoodGroup != null) {
                        prohibitionsListExt.setGuidOfGoodsGroup(bannedGoodGroup.getGuid());
                    }

                    ProhibitionExclusionsList exclusionsList = new ProhibitionExclusionsList();
                    prohibitionsListExt.getExclusions().add(exclusionsList);

                    Criteria exclusionCriteria = persistenceSession.createCriteria(ProhibitionExclusion.class);
                    exclusionCriteria.add(Restrictions.eq("prohibition", prohibition));
                    List exclusionObjects = exclusionCriteria.list();
                    if (!exclusionObjects.isEmpty()) {
                        for (Object exclusionObject : exclusionObjects) {
                            ProhibitionExclusion exclusion = (ProhibitionExclusion) exclusionObject;
                            ProhibitionExclusionsListExt prohibitionExclusionsListExt = objectFactory
                                  .createProhibitionExclusionsListExt();
                            prohibitionExclusionsListExt.setGuid(exclusion.getGuid());
                            prohibitionExclusionsListExt.setDeletedState(exclusion.getDeletedState());
                            prohibitionExclusionsListExt
                                  .setCreatedDate(getXMLGregorianCalendarByDate(exclusion.getCreatedDate()));
                            Good excludedGood = exclusion.getGood();
                            GoodGroup excludedGoodGroup = exclusion.getGoodsGroup();
                            if (excludedGood != null) {
                                prohibitionExclusionsListExt.setGuidOfGood(excludedGood.getGuid());
                            } else if (excludedGoodGroup != null) {
                                prohibitionExclusionsListExt.setGuidOfGoodsGroup(excludedGoodGroup.getGuid());
                            }

                            exclusionsList.getE().add(prohibitionExclusionsListExt);
                        }
                    }

                    prohibitionsList.getC().add(prohibitionsListExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            prohibitionsListResult.prohibitionsList = prohibitionsList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            prohibitionsListResult.resultCode = RC_INTERNAL_ERROR;
            prohibitionsListResult.description = RC_INTERNAL_ERROR_DESC;
        }
        return prohibitionsListResult;
    }

    private XMLGregorianCalendar getXMLGregorianCalendarByDate(Date date) throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        return xmlCalendar;
    }

    @Override
    public IdResult setProhibitionOnProduct(Long orgId, Long contractId, Long idOfProduct, Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, Product.class, idOfProduct);
        } else {
            return setProhibitionOnObject(orgId, contractId, Product.class, idOfProduct);
        }
    }

    @Override
    public IdResult setProhibitionOnProductGroup(Long orgId, Long contractId, Long idOfProductGroup,
          Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, ProductGroup.class, idOfProductGroup);
        } else {
            return setProhibitionOnObject(orgId, contractId, ProductGroup.class, idOfProductGroup);
        }
    }

    @Override
    public IdResult setProhibitionOnGood(Long orgId, Long contractId, Long idOfGood, Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, Good.class, idOfGood);
        } else {
            return setProhibitionOnObject(orgId, contractId, Good.class, idOfGood);
        }
    }

    @Override
    public IdResult setProhibitionOnGoodGroup(Long orgId, Long contractId, Long idOfGoodGroup, Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, GoodGroup.class, idOfGoodGroup);
        } else {
            return setProhibitionOnObject(orgId, contractId, GoodGroup.class, idOfGoodGroup);
        }
    }

    private IdResult deteteProhibitionOnObject(Long orgId, Long contractId, Class objectClass, Long idOfObject) {
        authenticateRequest(null);
        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
            orgCriteria.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCriteria.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                idResult.resultCode = RC_CLIENT_NOT_FOUND;
                idResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return idResult;
            }

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            if (objectClass.equals(Product.class)) {
                prohibitionCriteria.add(Restrictions.eq("product.globalId", idOfObject));
            } else if (objectClass.equals(ProductGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("productGroup.globalId", idOfObject));
            } else if (objectClass.equals(Good.class)) {
                prohibitionCriteria.add(Restrictions.eq("good.globalId", idOfObject));
            } else if (objectClass.equals(GoodGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("goodGroup.globalId", idOfObject));
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            Prohibition prohibition = (Prohibition) prohibitionCriteria.uniqueResult();
            prohibition.setDeletedState(true);
            prohibition.setGlobalVersion(
                  DAOService.getInstance().updateVersionByDistributedObjects(Prohibition.class.getSimpleName()));
            persistenceSession.save(prohibition);
            idResult.id = prohibition.getGlobalId();
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    private IdResult setProhibitionOnObject(Long orgId, Long contractId, Class objectClass, Long idOfObject) {
        authenticateRequest(null);

        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCritera = persistenceSession.createCriteria(Org.class);
            orgCritera.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCritera.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                idResult.resultCode = RC_CLIENT_NOT_FOUND;
                idResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return idResult;
            }

            Prohibition prohibition = new Prohibition();
            prohibition.setClient(client);
            prohibition.setOrgOwner(org.getIdOfOrg());
            prohibition.setCreatedDate(new Date());
            prohibition.setDeletedState(false);
            prohibition.setSendAll(SendToAssociatedOrgs.SendToSelf);
            Criteria objectCriteria = persistenceSession.createCriteria(objectClass);
            objectCriteria.add(Restrictions.eq("globalId", idOfObject));
            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            if (objectClass.equals(Product.class)) {
                prohibitionCriteria.add(Restrictions.eq("product.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if (p == null) {
                    Product product = (Product) objectCriteria.uniqueResult();
                    if (product == null) {
                        idResult.resultCode = RC_INTERNAL_ERROR;
                        idResult.description = "Продукта с указанным id не существует";
                        return idResult;
                    }
                    prohibition.setProduct(product);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else if (objectClass.equals(ProductGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("productGroup.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if (p == null) {
                    ProductGroup productGroup = (ProductGroup) objectCriteria.uniqueResult();
                    if (productGroup == null) {
                        idResult.resultCode = RC_INTERNAL_ERROR;
                        idResult.description = "Группы продуктов с указанным id не существует";
                        return idResult;
                    }
                    prohibition.setProductGroup(productGroup);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else if (objectClass.equals(Good.class)) {
                prohibitionCriteria.add(Restrictions.eq("good.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if (p == null) {
                    Good good = (Good) objectCriteria.uniqueResult();
                    if (good == null) {
                        idResult.resultCode = RC_INTERNAL_ERROR;
                        idResult.description = "Товара с указанным id не существует";
                        return idResult;
                    }
                    prohibition.setGood(good);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }

            } else if (objectClass.equals(GoodGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("goodGroup.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if (p == null) {
                    GoodGroup goodGroup = (GoodGroup) objectCriteria.uniqueResult();
                    if (goodGroup == null) {
                        idResult.resultCode = RC_INTERNAL_ERROR;
                        idResult.description = "Группы товаров с указанным id не существует";
                        return idResult;
                    }
                    prohibition.setGoodGroup(goodGroup);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            prohibition.setGlobalVersion(
                  DAOService.getInstance().updateVersionByDistributedObjects(Prohibition.class.getSimpleName()));
            persistenceSession.save(prohibition);

            idResult.id = prohibition.getGlobalId();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    @Override
    public IdResult excludeGoodFromProhibition(Long orgId, Long idOfProhibition, Long idOfGood) {
        return excludeObjectFromProhibition(orgId, idOfProhibition, Good.class, idOfGood);
    }

    @Override
    public IdResult excludeGoodGroupFromProhibition(Long orgId, Long idOfProhibition, Long idOfGoodGroup) {
        return excludeObjectFromProhibition(orgId, idOfProhibition, GoodGroup.class, idOfGoodGroup);
    }

    private IdResult excludeObjectFromProhibition(Long orgId, Long idOfProhibition, Class objectClass,
          Long idOfObject) {
        authenticateRequest(null);

        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCritera = persistenceSession.createCriteria(Org.class);
            orgCritera.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCritera.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            prohibitionCriteria.add(Restrictions.eq("globalId", idOfProhibition));
            Prohibition prohibition = (Prohibition) prohibitionCriteria.uniqueResult();
            if (prohibition == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Запрет с указанным id не найден";
                return idResult;
            }

            ProhibitionExclusion exclusion = new ProhibitionExclusion();
            exclusion.setProhibition(prohibition);
            exclusion.setOrgOwner(org.getIdOfOrg());
            exclusion.setCreatedDate(new Date());
            exclusion.setDeletedState(false);
            exclusion.setSendAll(SendToAssociatedOrgs.SendToSelf);
            Criteria objectCriteria = persistenceSession.createCriteria(objectClass);
            objectCriteria.add(Restrictions.eq("globalId", idOfObject));
            if (objectClass.equals(Good.class)) {
                Good good = (Good) objectCriteria.uniqueResult();
                if (good == null) {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Товар с указанным id не найден";
                    return idResult;
                }
                exclusion.setGood(good);
            } else if (objectClass.equals(GoodGroup.class)) {
                GoodGroup goodGroup = (GoodGroup) objectCriteria.uniqueResult();
                if (goodGroup == null) {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Группа товаров с указанным id не найдена";
                    return idResult;
                }
                exclusion.setGoodsGroup(goodGroup);
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            exclusion.setGlobalVersion(DAOService.getInstance()
                  .updateVersionByDistributedObjects(ProhibitionExclusion.class.getSimpleName()));
            persistenceSession.save(exclusion);

            idResult.id = exclusion.getGlobalId();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    @Override
    public ClassStudentListResult getStudentListByIdOfClientGroup(Long idOfClientGroup) {
        authenticateRequest(null);

        ClassStudentListResult classStudentListResult = new ClassStudentListResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("idOfClientGroup", idOfClientGroup));
            List objects = clientCriteria.list();
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            ClassStudentList classStudentList = new ClassStudentList();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    Client client = (Client) object;
                    ClientSummaryExt clientSummaryExt = objectFactory.createClientSummaryExt();
                    clientSummaryExt.setContractId(client.getContractId());
                    clientSummaryExt.setFirstName(client.getPerson().getFirstName());
                    clientSummaryExt.setLastName(client.getPerson().getSurname());
                    clientSummaryExt.setMiddleName(client.getPerson().getSecondName());
                    classStudentList.getC().add(clientSummaryExt);
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            classStudentListResult.classStudentList = classStudentList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return classStudentListResult;
    }

    @Override
    public ClientGroupListResult getGroupListByOrg(Long idOfOrg) {
        authenticateRequest(null);

        ClientGroupListResult clientGroupListResult = new ClientGroupListResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientGroupCriteria = persistenceSession.createCriteria(ClientGroup.class);
            clientGroupCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));

            List objects = clientGroupCriteria.list();
            ClientGroupList clientGroupList = new ClientGroupList();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    ClientGroup clientGroup = (ClientGroup) object;
                    ClientGroupItem clientGroupItem = new ClientGroupItem();
                    clientGroupItem.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    clientGroupItem.setGroupName(clientGroup.getGroupName());
                    clientGroupList.getG().add(clientGroupItem);
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            clientGroupListResult.clientGroupList = clientGroupList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientGroupListResult;
    }

    class ClientRequest {

        final static int CLIENT_ID_INTERNALID = 0, CLIENT_ID_SAN = 1, CLIENT_ID_EXTERNAL_ID = 2, CLIENT_ID_GUID = 3,
              CLIENT_SUB_ID = 4;

        public Data process(Long contractId, Processor processor) {

            Boolean enableSubBalanceOperation = RuntimeContext.getInstance()
                  .getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
            if (enableSubBalanceOperation) {
                String contractIdStr = String.valueOf(contractId);
                int len = contractIdStr.length();
                if (ContractIdGenerator.luhnTest(contractIdStr) || len < 2) {
                    return process(contractId, CLIENT_ID_INTERNALID, processor);
                } else {
                    return process(contractId, CLIENT_SUB_ID, processor);
                }
            } else {
                return process(contractId, CLIENT_ID_INTERNALID, processor);
            }
        }

        public Data process(Object id, int clientIdType, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            Integer subBalanceNum = 0;
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                if (clientIdType == CLIENT_ID_INTERNALID) {
                    clientCriteria.add(Restrictions.eq("contractId", (Long) id));
                } else if (clientIdType == CLIENT_ID_SAN) {
                    clientCriteria.add(Restrictions.ilike("san", (String) id, MatchMode.EXACT));
                } else if (clientIdType == CLIENT_ID_EXTERNAL_ID) {
                    clientCriteria.add(Restrictions.eq("externalId", (Long) id));
                } else if (clientIdType == CLIENT_ID_GUID) {
                    clientCriteria.add(Restrictions.eq("clientGUID", (String) id));
                } else if (clientIdType == CLIENT_SUB_ID) {
                    String subBalanceNumber = id.toString();
                    int len = subBalanceNumber.length();
                    if (ContractIdGenerator.luhnTest(subBalanceNumber.substring(0, len - 2))) {
                        subBalanceNum = Integer.parseInt(subBalanceNumber.substring(len - 2));
                        clientCriteria.add(Restrictions
                              .eq("contractId", Long.parseLong(subBalanceNumber.substring(0, len - 2))));
                    } else {
                        clientCriteria.add(Restrictions.eq("contractId", (Long) id));
                    }
                }

                List<Client> clients = clientCriteria.list();

                if (clients.isEmpty()) {
                    data.setResultCode(RC_CLIENT_NOT_FOUND);
                    data.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                } else if (clients.size() > 1) {
                    data.setResultCode(RC_SEVERAL_CLIENTS_WERE_FOUND);
                    data.setDescription(RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
                } else {
                    Client client = (Client) clients.get(0);
                    try {
                        client.getSubBalance(subBalanceNum);
                        processor.process(client, subBalanceNum, data, objectFactory, persistenceSession,
                              persistenceTransaction);
                        data.setIdOfContract(client.getContractId());
                        data.setResultCode(RC_OK);
                        data.setDescription(RC_OK_DESC);
                    } catch (NullPointerException e) {
                        data.setResultCode(RC_CLIENT_NOT_FOUND);
                        data.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                    }
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

    class OrgRequest {

        public Data process(long orgId, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria criteria = persistenceSession.createCriteria(Org.class);
                criteria.add(Restrictions.eq("idOfOrg", orgId));
                List<Org> orgs = criteria.list();

                if (orgs.isEmpty()) {
                    data.setResultCode(RC_INVALID_DATA);
                    data.setDescription("Организация не найдена");
                } else {
                    Org org = (Org) orgs.get(0);
                    processor.process(org, data, objectFactory, persistenceSession, persistenceTransaction);
                    data.setIdOfContract(null);
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
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
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
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
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
    public ClientSummaryResult getSummaryByTypedId(String id, int idType) {
        authenticateRequest(null);

        Object idVal = null;
        if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_INTERNALID) {
            idVal = Long.parseLong(id);
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_EXTERNAL_ID) {
            idVal = Long.parseLong(id);
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN) {
            idVal = id;
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_GUID) {
            idVal = id;
        } else {
            return new ClientSummaryResult(null, RC_INVALID_DATA, "idType invalid");
        }

        Data data = new ClientRequest().process(idVal, idType, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
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
        /* Номер контракта */
        clientSummaryExt.setContractId(client.getContractId());
        /* дата заключения контракта */
        clientSummaryExt.setDateOfContract(toXmlDateTime(client.getContractTime()));
        /* Текущий баланс клиента */
        clientSummaryExt.setBalance(client.getBalance());
        /* Баланс субсчета АП клиента */
        final Long subBalance1 = client.getSubBalance1() == null ? 0L : client.getSubBalance1();
        clientSummaryExt.setSubBalance1(subBalance1);
        /* Баланс основного счета клиента */
        clientSummaryExt.setSubBalance0(client.getBalance() - subBalance1);
        /* лимит овердрафта */
        clientSummaryExt.setOverdraftLimit(client.getLimit());
        /* Статус контракта (Текстовое значение) */
        clientSummaryExt.setStateOfContract(Client.CONTRACT_STATE_NAMES[client.getContractState()]);
        /*ограничения дневных затрат за день*/
        clientSummaryExt.setExpenditureLimit(client.getExpenditureLimit());
        /* ФИО Клиента */
        clientSummaryExt.setFirstName(client.getPerson().getFirstName());
        clientSummaryExt.setLastName(client.getPerson().getSurname());
        clientSummaryExt.setMiddleName(client.getPerson().getSecondName());
        /* Флаги увидомлений клиента (Истина/ложь)*/
        clientSummaryExt.setNotifyViaEmail(client.isNotifyViaEmail());
        clientSummaryExt.setNotifyViaSMS(client.isNotifyViaSMS());
        /* контактный телефон и емайл адрес электронной почты */
        clientSummaryExt.setMobilePhone(client.getMobile());
        clientSummaryExt.setEmail(client.getEmail());
        Contragent defaultMerchant = client.getOrg().getDefaultSupplier();
        if (defaultMerchant != null) {
            clientSummaryExt.setDefaultMerchantId(defaultMerchant.getIdOfContragent());
            clientSummaryExt
                  .setDefaultMerchantInfo(ParameterStringUtils.extractParameters("TSP.", defaultMerchant.getRemarks()));
        }
        EnterEvent ee = DAOUtils.getLastEnterEvent(session, client);
        if (ee != null) {
            clientSummaryExt.setLastEnterEventCode(ee.getPassDirection());
            clientSummaryExt.setLastEnterEventTime(toXmlDateTime(ee.getEvtDateTime()));
        }
        /* Группа к которой относится клиент (Наименование класса учиника) */
        if (client.getClientGroup() == null) {
            clientSummaryExt.setGrade(null);
        } else {
            clientSummaryExt.setGrade(client.getClientGroup().getGroupName());
        }
        /* Официальное наименование Учебного учереждения */
        clientSummaryExt.setOfficialName(client.getOrg().getOfficialName());
        // Новые параметры:
        String phone = client.getPhone();
        if (phone != null) {
            clientSummaryExt.setPhone(phone);
        }

        String address = client.getAddress();
        if (address != null) {
            clientSummaryExt.setAddress(address);
        }


        clientSummaryExt.setLimit(client.getLimit());

        Integer freePayCount = client.getFreePayCount();
        if (freePayCount != null) {
            clientSummaryExt.setFreePayCount(client.getFreePayCount());
        }

        Integer freePayMaxCount = client.getFreePayMaxCount();
        if (freePayMaxCount != null) {
            clientSummaryExt.setFreePayMaxCount(client.getFreePayMaxCount());
        }

        Date lastFreePayTime = client.getLastFreePayTime();

        if (lastFreePayTime != null) {
            GregorianCalendar greLastFreePayTime = new GregorianCalendar();
            greLastFreePayTime.setTime(lastFreePayTime);
            XMLGregorianCalendar xmlLastFreePayTime = DatatypeFactory.newInstance()
                  .newXMLGregorianCalendar(greLastFreePayTime);

            clientSummaryExt.setLastFreePayTime(xmlLastFreePayTime);
        }


        clientSummaryExt.setDiscountMode(client.getDiscountMode());


        data.setClientSummaryExt(clientSummaryExt);
    }

    @Override
    public PurchaseListResult getPurchaseList(Long contractId, final Date startDate, final Date endDate) {

        Long clientContractId = contractId;
        String contractIdstr = String.valueOf(contractId);
        if (ContractIdGenerator.luhnTest(contractIdstr)) {
            clientContractId = contractId;
        } else {
            int len = contractIdstr.length();
            if (len > 2 && ContractIdGenerator.luhnTest(contractIdstr.substring(0, len - 2))) {
                clientContractId = Long.parseLong(contractIdstr.substring(0, len - 2));
            }
        }

        authenticateRequest(clientContractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                if (subBalanceNum.equals(0)) {
                    processPurchaseList(client, data, objectFactory, session, endDate, startDate, null);
                }
                if (subBalanceNum.equals(1)) {
                    processPurchaseList(client, data, objectFactory, session, endDate, startDate,
                          OrderTypeEnumType.SUBSCRIPTION_FEEDING);
                }
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
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processPurchaseList(client, data, objectFactory, session, endDate, startDate, null);
                  }
              });

        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();
        return purchaseListResult;
    }

    @Override
    public PurchaseListResult getPurchaseSubscriptionFeedingList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processPurchaseList(client, data, objectFactory, session, endDate, startDate,
                            OrderTypeEnumType.SUBSCRIPTION_FEEDING);
                  }
              });

        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();
        return purchaseListResult;
    }

    private void processPurchaseList(Client client, Data data, ObjectFactory objectFactory, Session session,
          Date endDate, Date startDate, OrderTypeEnumType orderType) throws DatatypeConfigurationException {
        int nRecs = 0;
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria ordersCriteria = session.createCriteria(Order.class);
        ordersCriteria.add(Restrictions.eq("client", client));
        ordersCriteria.add(Restrictions.ge("createTime", startDate));
        ordersCriteria.add(Restrictions.lt("createTime", nextToEndDate));
        if (orderType != null) {
            ordersCriteria.add(Restrictions.eq("orderType", orderType));
        }
        ordersCriteria.addOrder(org.hibernate.criterion.Order.asc("createTime"));
        List ordersList = ordersCriteria.list();
        PurchaseListExt purchaseListExt = objectFactory.createPurchaseListExt();
        for (Object o : ordersList) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            Order order = (Order) o;
            PurchaseExt purchaseExt = objectFactory.createPurchaseExt();
            purchaseExt.setByCard(order.getSumByCard());
            purchaseExt.setSocDiscount(order.getSocDiscount());
            purchaseExt.setTrdDiscount(order.getTrdDiscount());
            purchaseExt.setDonation(order.getGrantSum());
            purchaseExt.setSum(order.getRSum());
            purchaseExt.setByCash(order.getSumByCash());
            if (order.getCard() == null) {
                purchaseExt.setIdOfCard(null);
            } else {
                purchaseExt.setIdOfCard(order.getCard().getIdOfCard());
            }
            //было так: purchaseExt.setIdOfCard(order.getCard().getCardPrintedNo());
            purchaseExt.setTime(toXmlDateTime(order.getCreateTime()));

            Set<OrderDetail> orderDetailSet = ((Order) o).getOrderDetails();
            for (OrderDetail od : orderDetailSet) {
                PurchaseElementExt purchaseElementExt = objectFactory.createPurchaseElementExt();
                purchaseElementExt.setIdOfOrderDetail(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                purchaseElementExt.setAmount(od.getQty());
                purchaseElementExt.setName(od.getMenuDetailName());
                purchaseElementExt.setSum(od.getRPrice());
                purchaseElementExt.setMenuType(od.getMenuType());
                if (od.isComplex()) {
                    purchaseElementExt.setType(1);
                } else if (od.isComplexItem()) {
                    purchaseElementExt.setType(2);
                } else {
                    purchaseElementExt.setType(0);
                }
                purchaseExt.getE().add(purchaseElementExt);
            }

            purchaseListExt.getP().add(purchaseExt);
        }
        data.setPurchaseListExt(purchaseListExt);
    }

    @Override
    public PaymentListResult getPaymentList(Long contractId, final Date startDate, final Date endDate) {

        Long clientContractId = contractId;
        String contractIdstr = String.valueOf(contractId);
        if (ContractIdGenerator.luhnTest(contractIdstr)) {
            clientContractId = contractId;
        } else {
            int len = contractIdstr.length();
            if (len > 2 && ContractIdGenerator.luhnTest(contractIdstr.substring(0, len - 2))) {
                clientContractId = Long.parseLong(contractIdstr.substring(0, len - 2));
            }
        }

        authenticateRequest(clientContractId);
        //authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                processPaymentList(client, subBalanceNum, data, objectFactory, session, endDate, startDate);
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
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processPaymentList(client, subBalanceNum, data, objectFactory, session, endDate, startDate);
                  }
              });

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    @Override
    public PaymentListResult getPaymentSubscriptionFeedingList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processPaymentList(client, 1, data, objectFactory, session, endDate, startDate);
                  }
              });

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    private void processPaymentList(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
          Session session, Date endDate, Date startDate) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria clientPaymentsCriteria = session.createCriteria(ClientPayment.class);
        if (subBalanceNum != null && subBalanceNum.equals(1)) {
            clientPaymentsCriteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_SUB_ACCOUNT_PAYMENT));
        } else {
            clientPaymentsCriteria.add(Restrictions.eq("payType", ClientPayment.CLIENT_TO_ACCOUNT_PAYMENT));
        }
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
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                processMenuList(client.getOrg(), data, objectFactory, session, startDate, endDate);
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
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processMenuList(client.getOrg(), data, objectFactory, session, startDate, endDate);
                  }
              });

        MenuListResult menuListResult = new MenuListResult();
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    @Override
    public MenuListResult getMenuListByOrg(@WebParam(name = "orgId") Long orgId, final Date startDate,
          final Date endDate) {
        authenticateRequest(null);

        Data data = new OrgRequest().process(orgId, new Processor() {
            public void process(Org org, Data data, ObjectFactory objectFactory, Session session,
                  Transaction transaction) throws Exception {
                processMenuList(org, data, objectFactory, session, startDate, endDate);
            }
        });

        MenuListResult menuListResult = new MenuListResult();
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }


    @Override
    public ComplexListResult getComplexList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                processComplexList(client.getOrg(), data, objectFactory, session, startDate, endDate);
            }
        });

        ComplexListResult complexListResult = new ComplexListResult();
        complexListResult.complexDateList = data.getComplexDateList();
        complexListResult.resultCode = data.getResultCode();
        complexListResult.description = data.getDescription();
        return complexListResult;
    }

    private void processMenuListWithProhibitions(Client client, Data data, ObjectFactory objectFactory, Session session,
          Date startDate, Date endDate) throws DatatypeConfigurationException {

        Map<String, Long> ProhibitByFilter = new HashMap<String, Long>();
        Map<String, Long> ProhibitByName = new HashMap<String, Long>();
        Map<String, Long> ProhibitByGroup = new HashMap<String, Long>();

        Criteria prohibitionsCriteria = session.createCriteria(ProhibitionMenu.class);
        prohibitionsCriteria.add(Restrictions.eq("client", client));
        prohibitionsCriteria.add(Restrictions.eq("deletedState", false));

        List prohibitions = prohibitionsCriteria.list();
        for (Object prohibitObj : prohibitions) {
            ProhibitionMenu prohibition = (ProhibitionMenu) prohibitObj;

            switch (prohibition.getProhibitionFilterType()) {
                case PROHIBITION_BY_FILTER:
                    ProhibitByFilter.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GOODS_NAME:
                    ProhibitByName.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GROUP_NAME:
                    ProhibitByGroup.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
            }
        }

        Criteria menuCriteria = session.createCriteria(Menu.class);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuCriteria.add(Restrictions.eq("org", client.getOrg()));
        menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        menuCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));

        List menus = menuCriteria.list();
        MenuListExt menuListExt = objectFactory.createMenuListExt();
        int nRecs = 0;
        for (Object currObject : menus) {
            if (nRecs++ > MAX_RECS) {
                break;
            }

            Menu menu = (Menu) currObject;
            MenuDateItemExt menuDateItemExt = objectFactory.createMenuDateItemExt();
            menuDateItemExt.setDate(toXmlDateTime(menu.getMenuDate()));

            Criteria menuDetailCriteria = session.createCriteria(MenuDetail.class);
            menuDetailCriteria.add(Restrictions.eq("menu", menu));
            menuDetailCriteria.add(Restrictions.sqlRestriction("{alias}.menupath !~ '^\\[\\d*\\]'"));
            HibernateUtils.addAscOrder(menuDetailCriteria, "groupName");
            HibernateUtils.addAscOrder(menuDetailCriteria, "menuDetailName");
            List menuDetails = menuDetailCriteria.list();
            for (Object o : menuDetails) {
                MenuDetail menuDetail = (MenuDetail) o;

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

                if (ProhibitByGroup.containsKey(menuDetail.getGroupName())) {
                    menuItemExt.setIdOfProhibition(ProhibitByGroup.get(menuDetail.getGroupName()));
                } else {
                    if (ProhibitByName.containsKey(menuDetail.getMenuDetailName())) {
                        menuItemExt.setIdOfProhibition(ProhibitByName.get(menuDetail.getMenuDetailName()));
                    } else {
                        //пробегаться в цикле.
                        for (String filter : ProhibitByFilter.keySet()) {
                            if (menuDetail.getMenuDetailName().indexOf(filter) != -1) {
                                menuItemExt.setIdOfProhibition(ProhibitByFilter.get(filter));
                            }
                        }
                    }
                }
                menuDateItemExt.getE().add(menuItemExt);
            }
            menuListExt.getM().add(menuDateItemExt);
        }
        data.setMenuListExt(menuListExt);
    }

    private void processMenuList(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
          Date endDate) throws DatatypeConfigurationException {
        Criteria menuCriteria = session.createCriteria(Menu.class);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuCriteria.add(Restrictions.eq("org", org));
        menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        menuCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));
        //menuCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 1)));

        List menus = menuCriteria.list();
        MenuListExt menuListExt = objectFactory.createMenuListExt();
        int nRecs = 0;
        for (Object currObject : menus) {
            if (nRecs++ > MAX_RECS) {
                break;
            }

            Menu menu = (Menu) currObject;
            MenuDateItemExt menuDateItemExt = objectFactory.createMenuDateItemExt();
            menuDateItemExt.setDate(toXmlDateTime(menu.getMenuDate()));

            Criteria menuDetailCriteria = session.createCriteria(MenuDetail.class);
            menuDetailCriteria.add(Restrictions.eq("menu", menu));
            HibernateUtils.addAscOrder(menuDetailCriteria, "groupName");
            HibernateUtils.addAscOrder(menuDetailCriteria, "menuDetailName");
            List menuDetails = menuDetailCriteria.list();

            for (Object o : menuDetails) {
                MenuDetail menuDetail = (MenuDetail) o;
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

    private void processComplexList(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
          Date endDate) throws DatatypeConfigurationException {


        Criteria complexCriteria = session.createCriteria(ComplexInfo.class);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        complexCriteria.add(Restrictions.eq("org", org));

        complexCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        complexCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));

        //complexCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 1)));

        List<ComplexInfo> complexes = complexCriteria.list();


        ArrayList<ArrayList<ComplexInfo>> sortedComplexes = new ArrayList<ArrayList<ComplexInfo>>();

        Date currDate = null;
        ArrayList<ComplexInfo> currComplexListWithSameDate = new ArrayList<ComplexInfo>();

        for (Object complexObject : complexes) {

            ComplexInfo currComplex = (ComplexInfo) complexObject;

            if (currDate == null) {
                currComplexListWithSameDate.add(currComplex);
                currDate = currComplex.getMenuDate();
                continue;
            }

            if (currComplex.getMenuDate().equals(currDate)) {
                currComplexListWithSameDate.add(currComplex);

            } else {

                ArrayList<ComplexInfo> newComplexes = new ArrayList<ComplexInfo>();
                newComplexes.addAll(currComplexListWithSameDate);

                sortedComplexes.add(newComplexes);

                currComplexListWithSameDate = new ArrayList<ComplexInfo>();
                currComplexListWithSameDate.add(currComplex);
                currDate = currComplex.getMenuDate();

            }


        }


        currDate = null;
        ComplexDateList complexDateList = new ComplexDateList();


        for (ArrayList<ComplexInfo> complexesWithSameDate : sortedComplexes) {

            ComplexDate complexDate = new ComplexDate();

            // boolean emptyComplexDate=true;

            // ComplexInfo currComplex=complexesWithSameDate.get(0);

            //currDate=currComplex.getMenuDate();


            // for(Object complexObject:complexes){
            // ArrayList<ArrayList<ComplexInfoDetail>> complexDetailsWithSameDate =new
            // ArrayList<ArrayList<ComplexInfoDetail>>();

            for (ComplexInfo complexInfo : complexesWithSameDate) {

                Complex complex = new Complex();

                Criteria complexDetailsCriteria = session.createCriteria(ComplexInfoDetail.class);
                complexDetailsCriteria.add(Restrictions.eq("complexInfo", complexInfo));


                List<ComplexInfoDetail> complexDetails = complexDetailsCriteria.list();

                if (!complexDetails.isEmpty()) {

                    for (ComplexInfoDetail complexInfoDetail : complexDetails) {
                        ComplexDetail complexDetail = new ComplexDetail();
                        complexDetail.setName(complexInfoDetail.getMenuDetail().getMenuDetailName());
                        complex.getE().add(complexDetail);
                        complex.setName(complexInfoDetail.getComplexInfo().getComplexName());
                    }

                    complexDate.getE().add(complex);
                    complexDate.setDate(toXmlDateTime(complexInfo.getMenuDate()));


                    // emptyComplexDate=false;
                    logger.info("complexName: " + complexInfo.getComplexName());

                    //  ArrayList<ComplexInfoDetail>complexDetailList=new ArrayList<ComplexInfoDetail>();
                    // complexDetailList.addAll(complexDetails);
                    // complexDetailsWithSameDate.add(complexDetailList);

                }


            }

            if (!complexDate.getE().isEmpty()) {

                complexDateList.getE().add(complexDate);
            }

        }


        data.setComplexDateList(complexDateList);

    }


    @Override
    public CardListResult getCardList(Long contractId) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
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
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
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
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                processEnterEventList(client, data, objectFactory, session, endDate, startDate, false);
            }
        });

        EnterEventListResult enterEventListResult = new EnterEventListResult();
        enterEventListResult.enterEventList = data.getEnterEventList();
        enterEventListResult.resultCode = data.getResultCode();
        enterEventListResult.description = data.getDescription();
        return enterEventListResult;
    }

    @Override
    public EnterEventWithRepListResult getEnterEventWithRepList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processEnterEventWithRepList(client, data, objectFactory, session, endDate, startDate, false);
            }
        });

        EnterEventWithRepListResult enterEventWithRepListResult = new EnterEventWithRepListResult();
        enterEventWithRepListResult.enterEventWithRepList = data.getEnterEventWithRepList();
        enterEventWithRepListResult.resultCode = data.getResultCode();
        enterEventWithRepListResult.description = data.getDescription();
        return enterEventWithRepListResult;
    }

    @Override
    public EnterEventListResult getEnterEventListByGuardian(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                processEnterEventList(client, data, objectFactory, session, endDate, startDate, true);
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
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processEnterEventList(client, data, objectFactory, session, endDate, startDate, false);
                  }
              });

        EnterEventListResult enterEventListResult = new EnterEventListResult();
        enterEventListResult.enterEventList = data.getEnterEventList();
        enterEventListResult.resultCode = data.getResultCode();
        enterEventListResult.description = data.getDescription();
        return enterEventListResult;
    }

    private void processEnterEventWithRepList(Client client, Data data, ObjectFactory objectFactory, Session session,
          Date endDate, Date startDate, boolean byGuardian) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria enterEventWithRepCriteria = session.createCriteria(EnterEvent.class);
        enterEventWithRepCriteria.add(byGuardian ? Restrictions.eq("guardianId", client.getIdOfClient())
              : Restrictions.eq("client", client));
        enterEventWithRepCriteria.add(Restrictions.ge("evtDateTime", startDate));
        enterEventWithRepCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        enterEventWithRepCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        List<EnterEvent> enterEvents = enterEventWithRepCriteria.list();
        EnterEventWithRepList enterEventWithRepList = objectFactory.createEnterEventWithRepList();
        int nRecs = 0;
        Map<Long, Client> guardianMap = new HashMap<Long, Client>();
        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            EnterEventWithRepItem enterEventWithRepItem = objectFactory.createEnterEventWithRepItem();
            enterEventWithRepItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventWithRepItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventWithRepItem.setEnterName(enterEvent.getEnterName());
            enterEventWithRepItem.setDirection(enterEvent.getPassDirection());
            enterEventWithRepItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);

            final Long guardianId = enterEvent.getGuardianId();
            if (guardianId != null) {
                Client guardian = guardianMap.get(guardianId);
                if (guardian == null){
                    guardian = DAOUtils.findClient(session, guardianId);
                    guardianMap.put(guardianId, guardian);
                }
                enterEventWithRepItem.setRepId(guardian.getContractId());
                enterEventWithRepItem.setRepName(guardian.getPerson().getFullName());
                //enterEventItem.setGuardianSan(guardian.getSan());
                enterEventWithRepItem.setGuardianSan(DAOUtils.extractSanFromClient(session, guardianId));
            }
            enterEventWithRepList.getE().add(enterEventWithRepItem);
        }
        data.setEnterEventWithRepList(enterEventWithRepList);
    }

    private void processEnterEventList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate, boolean byGuardian) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria enterEventCriteria = session.createCriteria(EnterEvent.class);
        enterEventCriteria.add(byGuardian ? Restrictions.eq("guardianId", client.getIdOfClient())
                : Restrictions.eq("client", client));
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
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setEnterName(enterEvent.getEnterName());
            enterEventItem.setDirection(enterEvent.getPassDirection());
            enterEventItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            final Long guardianId = enterEvent.getGuardianId();
            if (guardianId != null) {
                //Client guardian = DAOUtils.findClient(session, guardianId);
                //enterEventItem.setGuardianSan(guardian.getSan());
                enterEventItem.setGuardianSan(DAOUtils.extractSanFromClient(session, guardianId));
            }
            enterEventList.getE().add(enterEventItem);
        }
        data.setEnterEventList(enterEventList);
    }

    private Integer translateDayOfWeek(int i) {
        if (i == Calendar.MONDAY) {
            return 0;
        } else if (i == Calendar.TUESDAY) {
            return 1;
        } else if (i == Calendar.WEDNESDAY) {
            return 2;
        } else if (i == Calendar.THURSDAY) {
            return 3;
        } else if (i == Calendar.FRIDAY) {
            return 4;
        } else if (i == Calendar.SATURDAY) {
            return 5;
        } else if (i == Calendar.SUNDAY) {
            return 6;
        }
        return -1;
    }

    @Override
    public ClientsData getClientsByGuardSan(String guardSan) {
        authenticateRequest(null);
        ClientsData data = new ClientsData();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            List<Long> idOfClients = DAOUtils.extractIDFromGuardSanByGuardSan(persistenceSession, guardSan);

            data.clientList = new ClientList();
            for (Long idOfClient : idOfClients) {
                Client cl = DAOUtils.findClient(persistenceSession, idOfClient);
                ClientItem clientItem = new ClientItem();
                clientItem.setContractId(cl.getContractId());
                clientItem.setSan(cl.getSan());
                data.clientList.getClients().add(clientItem);
            }
            data.resultCode = RC_OK;
            data.description = "OK";
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



    public ClientsData getClientsByGuardMobile(String mobile) {
        authenticateRequest(null);
        ClientsData data = new ClientsData();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            List<Long> idOfClients = DAOUtils.extractIDFromGuardByGuardMobile(persistenceSession,
                    Client.checkAndConvertMobile(mobile));
            data.clientList = new ClientList();
            for (Long idOfClient : idOfClients) {
                Client cl = DAOUtils.findClient(persistenceSession, idOfClient);
                ClientItem clientItem = new ClientItem();
                clientItem.setContractId(cl.getContractId());
                clientItem.setSan(cl.getSan());
                data.clientList.getClients().add(clientItem);
            }
            data.resultCode = RC_OK;
            data.description = "OK";
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
        guardSan = ClientGuardSanRebuildService.clearGuardSan(guardSan);
        authenticateRequest(null);

        AttachGuardSanResult data = new AttachGuardSanResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List clientList = DAOUtils.findClientsBySan(persistenceSession, san);
            workClientSan(persistenceSession, guardSan, data, clientList);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (persistenceTransaction != null) {
                persistenceTransaction.rollback();
            }
            if (persistenceSession != null) {
                persistenceSession.close();
            }
        }
        return data;
    }

    @Override
    public AttachGuardSanResult attachGuardSan(Long contractId, String guardSan) {
        guardSan = ClientGuardSanRebuildService.clearGuardSan(guardSan);
        authenticateRequest(null);

        AttachGuardSanResult data = new AttachGuardSanResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List clientList = DAOUtils.findClientsByContract(persistenceSession, contractId);
            workClientSan(persistenceSession, guardSan, data, clientList);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (persistenceTransaction != null) {
                persistenceTransaction.rollback();
            }
            if (persistenceSession != null) {
                persistenceSession.close();
            }
        }
        return data;
    }

    @Override
    public DetachGuardSanResult detachGuardSan(String san, String guardSan) {
        guardSan = ClientGuardSanRebuildService.clearGuardSan(guardSan);
        authenticateRequest(null);

        DetachGuardSanResult data = new DetachGuardSanResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List clientList = DAOUtils.findClientsBySan(persistenceSession, san);
            workClientSan(persistenceSession, guardSan, data, clientList);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (persistenceTransaction != null) {
                persistenceTransaction.rollback();
            }
            if (persistenceSession != null) {
                persistenceSession.close();
            }
        }

        return data;
    }

    @Override
    public DetachGuardSanResult detachGuardSan(Long contractId, String guardSan) {
        guardSan = ClientGuardSanRebuildService.clearGuardSan(guardSan);
        authenticateRequest(null);

        DetachGuardSanResult data = new DetachGuardSanResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List clientList = DAOUtils.findClientsByContract(persistenceSession, contractId);
            workClientSan(persistenceSession, guardSan, data, clientList);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.getMessage();
        } finally {
            if (persistenceTransaction != null) {
                persistenceTransaction.rollback();
            }
            if (persistenceSession != null) {
                persistenceSession.close();
            }
        }

        return data;
    }

    private void workClientSan(Session persistenceSession, String guardSan, Result data, List clientList) {
        guardSan = ClientGuardSanRebuildService.clearGuardSan(guardSan);
        if (guardSan.length() < 1) {
            data.resultCode = RC_INVALID_DATA;
            data.description = "Неверно указан СНИЛС: " + guardSan;
            return;
        }


        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        try {
            parseWorkClientSan(persistenceSession, guardSan, data, clientList);
        } catch (Exception e) {
            data.resultCode = RC_INVALID_DATA;
            data.description = RC_INTERNAL_ERROR_DESC;
            return;
        }
    }

    public void parseWorkClientSan(Session persistenceSession, String guardSan, Result data, List clientList) {
        if (clientList.size() == 0) {
            data.resultCode = RC_CLIENT_NOT_FOUND;
            data.description = RC_CLIENT_NOT_FOUND_DESC;
        } /*else if (clientList.size() > 1) {
            data.resultCode = RC_SEVERAL_CLIENTS_WERE_FOUND;
            data.description = RC_SEVERAL_CLIENTS_WERE_FOUND_DESC;
        }*/ else {
            Long idOfClient = ((BigInteger) clientList.get(0)).longValue();
            Client cl = null;
            Set<GuardSan> guardSans = Collections.EMPTY_SET;
            //String clientGuardSan = (String) clientObject[1];
            try {
                cl = DAOUtils.findClient(persistenceSession, idOfClient);
                guardSans = cl.getGuardSan();
            } catch (Exception e) {
                data.resultCode = RC_INTERNAL_ERROR;
                data.description = RC_INTERNAL_ERROR_DESC;
                logger.error("Failed to insert SNILS using WS", e);
                return;
            }


            //  Проверка наличия опекуна среди текущих
            boolean exists = false;
            for (GuardSan gSan : guardSans) {
                if (gSan.getGuardSan().equals(guardSan)) {
                    exists = true;
                    break;
                }
            }

            if (data instanceof AttachGuardSanResult) {
                //  Если надо прикрепить опекуна
                try {
                    if (exists) {
                        //  Если уже опекун уже существует, то ничего не делаем
                        data.resultCode = RC_CLIENT_HAS_THIS_SNILS_ALREADY;
                        data.description = RC_CLIENT_HAS_THIS_SNILS_ALREADY_DESC;
                    } else {
                        //  Иначе, добавляем его и обновляем все сущности в БД
                        GuardSan newGuardSan = new GuardSan(cl, guardSan);
                        newGuardSan.setGuardSan(guardSan);
                        persistenceSession.save(newGuardSan);
                        /*guardSans.add(newGuardSan);
                        cl.setGuardSan(guardSans);
                        session.update(cl);*/
                        data.resultCode = RC_OK;
                        data.description = "Ok";
                    }
                } catch (Exception e) {
                    data.resultCode = RC_INTERNAL_ERROR;
                    data.description = RC_INTERNAL_ERROR_DESC;
                    logger.error("Failed to insert guard SNILS using WS", e);
                    return;
                }
            } else if (data instanceof DetachGuardSanResult) {
                //  Если надо его открепить
                try {
                    if (!exists) {
                        //  Если опекун уже не существует, ничего не делаем
                        data.resultCode = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS;
                        data.description = RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS_DESC;
                    } else {
                        for (GuardSan gSan : guardSans) {
                            if (gSan.getGuardSan().equals(guardSan)) {
                                guardSans.remove(gSan);
                                break;
                            }
                        }
                        cl.setGuardSan(guardSans);
                        persistenceSession.update(cl);
                        data.resultCode = RC_OK;
                        data.description = "Ok";
                    }
                } catch (Exception e) {
                    data.resultCode = RC_INTERNAL_ERROR;
                    data.description = RC_INTERNAL_ERROR_DESC;
                    logger.error("Failed to remove guard SNILS using WS", e);
                    return;
                }
            } else {
                data.resultCode = RC_INTERNAL_ERROR;
                data.description = RC_INTERNAL_ERROR_DESC;
                logger.error("Try to execute unknown operation " + data.getClass());
            }
        }
    }

    private boolean isGuardSanExists(String guardSan, String clientGuardSans) {
        String[] guardSans = clientGuardSans.split(";");
        for (String gs : guardSans) {
            if (gs.equals(guardSan)) {
                return true;
            }
        }
        return false;
    }

    XMLGregorianCalendar toXmlDateTime(Date date) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
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
        authenticateRequest(null);

        long lCardId = Long.parseLong(cardId);
        Long contractId = null;
        try {
            contractId = DAOService.getInstance().getContractIdByCardNo(lCardId);
            if (contractId == null) {
                int days = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_TEMP_CARD_VALID_DAYS);
                contractId = DAOService.getInstance().getContractIdByTempCardNoAndCheckValidDate(lCardId, days);
            }
        } catch (Exception e) {
            logger.error("ClientRoomController failed", e);
        }
        return contractId;
    }

    @Override
    public ClientSummaryExt[] getSummaryByGuardSan(String guardSan) {
        authenticateRequest(null);

        ClientsData cd = getClientsByGuardSan(guardSan);
        LinkedList<ClientSummaryExt> clientSummaries = new LinkedList<ClientSummaryExt>();
        if (cd != null && cd.clientList != null) {
            for (ClientItem ci : cd.clientList.getClients()) {
                ClientSummaryResult cs = getSummary(ci.getContractId());
                if (cs.clientSummary != null) {
                    clientSummaries.add(cs.clientSummary);
                }
            }
        }
        return clientSummaries.toArray(new ClientSummaryExt[clientSummaries.size()]);
    }

    @Override
    public ClientSummaryExtListResult getSummaryByGuardMobile(String guardMobile) {
        authenticateRequest(null);

        ClientsData cd = getClientsByGuardMobile(guardMobile);
        LinkedList<ClientSummaryExt> clientSummaries = new LinkedList<ClientSummaryExt>();
        if (cd != null && cd.clientList != null) {
            for (ClientItem ci : cd.clientList.getClients()) {
                ClientSummaryResult cs = getSummary(ci.getContractId());
                if (cs.clientSummary != null) {
                    clientSummaries.add(cs.clientSummary);
                }
            }
        }

        ClientSummaryExtListResult clientSummaryExtListResult = new ClientSummaryExtListResult();
        clientSummaryExtListResult.clientSummary = clientSummaries;
        clientSummaryExtListResult.resultCode = 0l;

        return clientSummaryExtListResult;
    }

    @Override
    public ClientRepresentativesResult getClientRepresentatives(@WebParam(name = "contractId") String contractId) {
        Long contractIdLong = Long.valueOf(contractId);
        authenticateRequest(contractIdLong);

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        List<Client> representativeList = null;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();

            representativeList = ClientManager.loadGuardiansByChildren(persistenceSession, contractIdLong);

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (representativeList == null || representativeList.isEmpty()) {
            return null;
        }
        List<ClientRepresentatives> clientRepresentativesList = new ArrayList<ClientRepresentatives>();

        for (Client aRepresentativeList : representativeList) {
            ClientRepresentatives clientRepresentative = new ClientRepresentatives();
            clientRepresentative.setId(aRepresentativeList.getContractId());
            clientRepresentative.setName(aRepresentativeList.getPerson().getFullName());
            clientRepresentativesList.add(clientRepresentative);
        }

        ClientRepresentativesResult clientRepresentativesResult = new ClientRepresentativesResult();
        clientRepresentativesResult.clientRepresentativesList = clientRepresentativesList;
        clientRepresentativesResult.resultCode = 0l;
        return clientRepresentativesResult;
    }

    @Override
    public Result enableNotificationBySMS(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "state") boolean state) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().enableClientNotificationBySMS(contractId, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result enableNotificationByEmail(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "state") boolean state) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().enableClientNotificationByEmail(contractId, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeMobilePhone(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "mobilePhone") String mobilePhone) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
        if (mobilePhone == null) {
            r.resultCode = RC_INVALID_DATA;
            r.description = "Неверный формат телефона";
            return r;
        }
        if (!DAOService.getInstance().setClientMobilePhone(contractId, mobilePhone)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeEmail(@WebParam(name = "contractId") Long contractId, @WebParam(name = "email") String email) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().setClientEmail(contractId, email)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeExpenditureLimit(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "limit") long limit) {
        authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);
        if (limit < 0) {
            r = new Result(RC_INVALID_DATA, "Лимит не может быть меньше нуля");
            return r;
        }
        if (!DAOService.getInstance().setClientExpenditureLimit(contractId, limit)) {
            r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
        }
        return r;
    }


    @Override
    public CirculationListResult getCirculationList(@WebParam(name = "contractId") Long contractId, int state) {
        authenticateRequest(contractId);

        final int fState = state;
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                processCirculationList(client, data, objectFactory, session, fState);
            }
        });

        CirculationListResult circListResult = new CirculationListResult();
        circListResult.circulationList = data.getCirculationItemList();
        circListResult.resultCode = data.getResultCode();
        circListResult.description = data.getDescription();
        return circListResult;
    }

    private void processCirculationList(Client client, Data data, ObjectFactory objectFactory, Session session,
          int state) throws DatatypeConfigurationException {
        Criteria circulationCriteria = session.createCriteria(Circulation.class);
        circulationCriteria.add(Restrictions.eq("client", client));
        if (state == CIRCULATION_STATUS_FILTER_ALL_ON_HANDS) {
            circulationCriteria.add(Restrictions
                  .or(Restrictions.eq("status", Circulation.EXTENDED), Restrictions.eq("status", Circulation.ISSUED)));
        } else if (state != CIRCULATION_STATUS_FILTER_ALL) {
            circulationCriteria.add(Restrictions.eq("status", state));
        }
        circulationCriteria.addOrder(org.hibernate.criterion.Order.desc("issuanceDate"));

        List<Circulation> circulationList = circulationCriteria.list();

        CirculationItemList ciList = objectFactory.createCirculationItemList();
        for (Circulation c : circulationList) {
            CirculationItem ci = new CirculationItem();
            ci.setIssuanceDate(toXmlDateTime(c.getIssuanceDate()));
            ci.setStatus(c.getStatus());
            ci.setRealRefundDate(toXmlDateTime(c.getRealRefundDate()));
            ci.setRefundDate(toXmlDateTime(c.getRefundDate()));
            Publication p = c.getIssuable().getInstance().getPublication();
            if (p != null) {
                PublicationItem pi = new PublicationItem();
                pi.setAuthor(p.getAuthor());
                pi.setPublisher(p.getPublisher());
                pi.setTitle(p.getTitle());
                pi.setTitle2(p.getTitle2());
                pi.setPublicationDate(p.getPublicationdate());
                ci.setPublication(pi);
            }
            ciList.getC().add(ci);
        }
        data.setCirculationItemList(ciList);
    }

    @Override
    public Result authorizeClient(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "token") String token) {
        IntegraPartnerConfig.LinkConfig partnerLinkConfig = null;
        //logger.info("init authorizeClient");
        partnerLinkConfig = authenticateRequest(null);
        if (logger.isDebugEnabled()) {
            logger.debug("begin authorizeClient");
        }
        try {

            DAOService daoService = DAOService.getInstance();
            //logger.info("begin get Client");
            Client client = daoService.getClientByContractId(contractId);
            //logger.info("find client");
            if (client == null) {
                //logger.info("find client == null");
                if (logger.isDebugEnabled()) {
                    logger.debug("Client not found");
                }
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }
            //logger.info("find client != null");
            boolean authorized = false;
            if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH_BY_NAME) {
                //logger.info("MD5");
                String fullNameUpCase = client.getPerson().getFullName().replaceAll("\\s", "").toUpperCase();
                fullNameUpCase = fullNameUpCase + "Nb37wwZWufB";
                byte[] bytesOfMessage = fullNameUpCase.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash = md.digest(bytesOfMessage);
                BigInteger bigInt = new BigInteger(1, hash);
                //String md5HashString = bigInt.toString(16);
                String md5HashString = String.format("%0" + (hash.length << 1) + "X", bigInt);
                if (logger.isDebugEnabled()) {
                    logger.info("token    md5: " + token.toUpperCase());
                    logger.info("generate md5: " + md5HashString.toUpperCase());
                }
                if (md5HashString.toUpperCase().compareTo(token.toUpperCase()) == 0) {
                    authorized = true;
                    if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
                        daoService
                              .addIntegraPartnerAccessPermissionToClient(client.getIdOfClient(), partnerLinkConfig.id);
                    }
                }
            }
            if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH_BY_MOBILE) {
                String key = CryptoUtils.MD5(client.getMobile());
                if (key.equalsIgnoreCase(token)) {
                    authorized = true;
                }
            }
            if (client.hasEncryptedPasswordSHA1(token)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("hasEncryptedPassword");
                }
                authorized = true;
                if (!authorized
                      && partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
                    daoService.addIntegraPartnerAccessPermissionToClient(client.getIdOfClient(), partnerLinkConfig.id);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("authorized" + String.valueOf(authorized));
            }
            if (authorized) {
                return new Result(RC_OK, RC_OK_DESC);
            } else {
                return new Result(RC_CLIENT_AUTHORIZATION_FAILED, RC_CLIENT_AUTHORIZATION_FAILED_DESC);
            }
        } catch (Exception e) {
            logger.error("Failed to authorized client", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    @Override
    public ActivateLinkingTokenResult activateLinkingToken(String linkingToken) {
        authenticateRequest(null);

        ActivateLinkingTokenResult result = new ActivateLinkingTokenResult();
        try {

            DAOService daoService = DAOService.getInstance();

            Client client = daoService.findAndDeleteLinkingToken(linkingToken);
            if (client == null) {
                result.resultCode = RC_INVALID_DATA;
                result.description = "Код активации не найден";
            } else {
                result.contractId = client.getContractId();
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
            }
        } catch (Exception e) {
            logger.error("Failed to activate linking token: " + linkingToken, e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public GenerateLinkingTokenResult generateLinkingToken(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        GenerateLinkingTokenResult result = new GenerateLinkingTokenResult();
        try {
            DAOService daoService = DAOService.getInstance();
            Client client = daoService.getClientByContractId(contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            LinkingToken linkingToken = daoService.generateLinkingToken(client);
            result.linkingToken = linkingToken.getToken();
            result.contractId = contractId;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Failed to generate linking token", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public Result sendLinkingTokenByContractId(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        try {
            Result result = new Result();
            DAOService daoService = DAOService.getInstance();
            Client client = daoService.getClientByContractId(contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            LinkingToken linkingToken = daoService.generateLinkingToken(client);
            String info = "";
            if (client.hasEmail()) {
                info += "e-mail";
            }
            if (client.hasMobile()) {
                if (info.length() > 0) {
                    info += ", ";
                }
                info += "SMS";
            }
            if (info.length() == 0) {
                result.resultCode = RC_NO_CONTACT_DATA;
                result.description = RC_NO_CONTACT_DATA_DESC;
            } else {
                RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                      .sendMessageAsync(client, EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                            new String[]{"linkingToken", linkingToken.getToken()});
                result.resultCode = RC_OK;
                result.description = "Код активации отправлен по " + info;
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to send linking token", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    @Override
    public Result sendLinkingTokenByMobile(@WebParam(name = "mobilePhone") String mobilePhone) {
        authenticateRequest(null);
        Result result = new Result();

        try {
            mobilePhone = Client.checkAndConvertMobile(mobilePhone);
            if (mobilePhone == null) {
                result.resultCode = RC_INVALID_DATA;
                result.description = "Неверный формат телефона";
                return result;
            }

            DAOService daoService = DAOService.getInstance();
            List<Client> clientList = daoService.findClientsByMobilePhone(mobilePhone);
            if (clientList.size() == 0) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            String codes = "";
            for (Client cl : clientList) {
                LinkingToken linkingToken = daoService.generateLinkingToken(cl);
                if (codes.length() > 0) {
                    codes += ", ";
                }
                codes += linkingToken.getToken();
            }
            RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                  .sendMessageAsync(clientList.get(0), EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                        new String[]{"linkingToken", codes});
            result.resultCode = RC_OK;
            result.description = "Код активации отправлен по SMS для " + clientList.size() + " л/с";
            return result;
        } catch (Exception e) {
            logger.error("Failed to send linking token", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    IntegraPartnerConfig.LinkConfig authenticateRequest(Long contractId) throws Error {
        MessageContext jaxwsContext = context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) jaxwsContext.get(SOAPMessageContext.SERVLET_REQUEST);
        String clientAddress = request.getRemoteAddr();
        ////
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        X509Certificate[] certificates = (X509Certificate[]) request
              .getAttribute("javax.servlet.request.X509Certificate");
        ////
        IntegraPartnerConfig.LinkConfig linkConfig = null;
        String DNs = "";
        if (certificates != null && certificates.length > 0) {
            for (int n = 0; n < certificates.length; ++n) {
                String dn = certificates[0].getSubjectDN().getName();
                linkConfig = runtimeContext.getIntegraPartnerConfig().getLinkConfigByCertDN(dn);
                if (linkConfig != null) {
                    break;
                }
                DNs += dn + ";";
            }
        }
        /////
        // пробуем по имени и паролю
        if (linkConfig == null) {
            AuthorizationPolicy authorizationPolicy = (AuthorizationPolicy) jaxwsContext
                  .get("org.apache.cxf.configuration.security.AuthorizationPolicy");
            if (authorizationPolicy != null && authorizationPolicy.getUserName() != null) {
                linkConfig = runtimeContext.getIntegraPartnerConfig()
                      .getLinkConfigWithAuthTypeBasicMatching(authorizationPolicy.getUserName(),
                            authorizationPolicy.getPassword());
            }
        }
        /////
        if (linkConfig == null) {
            linkConfig = runtimeContext.getIntegraPartnerConfig()
                  .getLinkConfigWithAuthTypeNoneAndMatchingAddress(clientAddress);
        } else {
            // check remote addr
            if (!linkConfig.matchAddress(clientAddress)) {
                throw new Error("Integra partner auth failed: remote address does not match: " + clientAddress
                      + " for link config: " + linkConfig.id + "; request: ip=" + clientAddress + "; ssl DNs=" + DNs);
            }
        }
        /////
        if (linkConfig == null) {
            throw new Error(
                  "Integra partner auth failed: link config not found: ip=" + clientAddress + "; ssl DNs=" + DNs);
        }
        /////
        if (contractId != null && linkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
            DAOService daoService = DAOService.getInstance();
            Client client = null;
            try {
                client = daoService.getClientByContractId(contractId);
            } catch (Throwable e) {
            }
            if (client == null) {
                throw new Error("Integra partner auth failed: client not found: contractId=" + contractId + "; ip="
                      + clientAddress + "; ssl DNs=" + DNs);
            }

            if (!client.hasIntegraPartnerAccessPermission(linkConfig.id)) {
                throw new Error("Integra partner auth failed: access prohibited for client: contractId=" + contractId
                      + ", authorize client first; ip=" + clientAddress + "; ssl DNs=" + DNs);
            }
        }
        return linkConfig;
    }

    @Override
    public Result changePassword(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "base64passwordHash") String base64passwordHash) {

        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().setClientPassword(contractId, base64passwordHash)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public SendResult sendPasswordRecoverURLFromEmail(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "request") RequestWebParam request) {
        ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();
        SendResult sr = new SendResult();
        sr.resultCode = RC_OK;
        sr.description = RC_OK_DESC;
        try {
            int succeeded = clientPasswordRecover.sendPasswordRecoverURLFromEmail(contractId, request);
            sr.recoverStatus = succeeded;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            sr.resultCode = RC_INTERNAL_ERROR;
            sr.description = RC_INTERNAL_ERROR_DESC;


        }
        return sr;
    }

    @Override
    public CheckPasswordResult checkPasswordRestoreRequest(@WebParam(name = "request") RequestWebParam request) {
        ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();
        CheckPasswordResult cpr = new CheckPasswordResult();
        cpr.resultCode = RC_OK;
        cpr.description = RC_OK_DESC;
        try {
            boolean succeeded = clientPasswordRecover.checkPasswordRestoreRequest(request);
            cpr.succeeded = succeeded;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            cpr.resultCode = RC_INTERNAL_ERROR;
            cpr.description = RC_INTERNAL_ERROR_DESC;

        }
        return cpr;
    }

    @Override
    public IdResult getIdOfClient(@WebParam(name = "contractId") Long contractId) {
        Long idOfClient = null;


        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;


        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            idOfClient = client.getIdOfClient();


            r.id = idOfClient;
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return r;

    }

    public IdResult getIdOfContragent(@WebParam(name = "contragentName") String contragentName) {


        Long idOfContragent = null;

        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;

        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();


            Criteria contragentCriteria = persistenceSession.createCriteria(Contragent.class);
            contragentCriteria.add(Restrictions.eq("contragentName", contragentName));
            Contragent contragent = (Contragent) contragentCriteria.uniqueResult();
            idOfContragent = contragent.getIdOfContragent();

            r.id = idOfContragent;

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return r;
    }

    @Override
    public IdResult createPaymentOrder(@WebParam(name = "idOfClient") Long idOfClient,
          @WebParam(name = "idOfContragent") Long idOfContragent, @WebParam(name = "paymentMethod") int paymentMethod,
          @WebParam(name = "copecksAmount") Long copecksAmount, @WebParam(name = "contragentSum") Long contragentSum) {
        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;


        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();
            Long idOfClientPaymentOrder = runtimeContext.getClientPaymentOrderProcessor()
                  .createPaymentOrder(idOfClient, idOfContragent, paymentMethod, copecksAmount, contragentSum);
            r.id = idOfClientPaymentOrder;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;

        }
        return r;
    }


    @Override
    public Result changePaymentOrderStatus(@WebParam(name = "idOfClient") Long idOfClient,
          @WebParam(name = "idOfClientPaymentOrder") Long idOfClientPaymentOrder,
          @WebParam(name = "orderStatus") int orderStatus) {

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        RuntimeContext runtimeContext = null;

        runtimeContext = RuntimeContext.getInstance();
        ClientPaymentOrderProcessor clientPaymentOrderProcessor = runtimeContext.getClientPaymentOrderProcessor();
        try {
            clientPaymentOrderProcessor.changePaymentOrderStatus(idOfClient, idOfClientPaymentOrder,
                  ClientPaymentOrder.ORDER_STATUS_CANCELLED);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        }
        return r;

    }

    @Override
    public RBKMoneyConfigResult getRBKMoneyConfig() {
        RuntimeContext runtimeContext = null;
        RBKMoneyConfigResult r = new RBKMoneyConfigResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        RBKMoneyConfig rbkMoneyConfig = runtimeContext.getPartnerRbkMoneyConfig();
        RBKMoneyConfigExt rbkMoneyConfigExt = new RBKMoneyConfigExt();
        rbkMoneyConfigExt.setContragentName(rbkMoneyConfig.getContragentName());
        rbkMoneyConfigExt.setEshopId(rbkMoneyConfig.getEshopId());
        rbkMoneyConfigExt.setPurchaseUri(rbkMoneyConfig.getPurchaseUri().toString());
        rbkMoneyConfigExt.setRate(rbkMoneyConfig.getRate());
        rbkMoneyConfigExt.setSecretKey(rbkMoneyConfig.getSecretKey());
        rbkMoneyConfigExt.setServiceName(rbkMoneyConfig.getServiceName());
        rbkMoneyConfigExt.setShow(rbkMoneyConfig.getShow());
        r.rbkConfig = rbkMoneyConfigExt;
        return r;

    }

    @Override
    public ChronopayConfigResult getChronopayConfig() {
        RuntimeContext runtimeContext = null;
        ChronopayConfigResult r = new ChronopayConfigResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        ChronopayConfig chronopayConfig = runtimeContext.getPartnerChronopayConfig();
        ChronopayConfigExt chronopayConfigExt = new ChronopayConfigExt();
        chronopayConfigExt.setCallbackUrl(chronopayConfig.getCallbackUrl());
        chronopayConfigExt.setContragentName(chronopayConfig.getContragentName());
        chronopayConfigExt.setIp(chronopayConfig.getIp());
        chronopayConfigExt.setPurchaseUri(chronopayConfig.getPurchaseUri());
        chronopayConfigExt.setRate(chronopayConfig.getRate());
        chronopayConfigExt.setSharedSec(chronopayConfig.getSharedSec());
        chronopayConfigExt.setShow(chronopayConfig.getShow());

        r.chronopayConfig = chronopayConfigExt;
        return r;

    }

    @Override
    public ClientSmsListResult getClientSmsList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(contractId);

        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        ClientSmsListResult r = new ClientSmsListResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();

            Date nextToEndDate = DateUtils.addDays(endDate, 1);


            Criteria clientSmsCriteria = persistenceSession.createCriteria(ClientSms.class);
            clientSmsCriteria.add(Restrictions.ge("serviceSendTime", startDate));
            clientSmsCriteria.add(Restrictions.lt("serviceSendTime", nextToEndDate));
            clientSmsCriteria.add(Restrictions.eq("client", client));
            List clientSmsList = clientSmsCriteria.list();
            ClientSmsList clientSmsListR = new ClientSmsList();

            for (Object clientSmsObject : clientSmsList) {
                ClientSms clientSms = (ClientSms) clientSmsObject;
                AccountTransaction accountTransaction = clientSms.getTransaction();

                Sms sms = new Sms();

                sms.setDeliveryStatus(clientSms.getDeliveryStatus());
                sms.setPrice(clientSms.getPrice());

                sms.setContentsType(clientSms.getContentsType());

                GregorianCalendar greSendTime = new GregorianCalendar();
                greSendTime.setTime(clientSms.getServiceSendTime());
                XMLGregorianCalendar xmlSendTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(greSendTime);

                sms.setServiceSendTime(xmlSendTime);

                Long transactionSum = 0L;
                if (null != accountTransaction) {
                    transactionSum = accountTransaction.getTransactionSum();
                }

                sms.setTransactionSum(transactionSum);

                if (null != accountTransaction) {
                    Card card = accountTransaction.getCard();
                    if (null != card) {

                        sms.setCardNo(card.getCardNo());
                    }
                }
                clientSmsListR.getS().add(sms);


            }
            r.clientSmsList = clientSmsListR;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;

        }


        return r;
    }


    @Override
    public BanksData getBanks() {

        BanksData bd = new BanksData();
        bd.resultCode = RC_OK;
        bd.description = RC_OK_DESC;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        BanksList bankItemList = new BanksList();
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria banksCriteria = persistenceSession.createCriteria(Bank.class);

            List<Bank> banksList = (List<Bank>) banksCriteria.list();
            //List<BankItem>banks=new ArrayList<BankItem>();
            for (Bank bank : banksList) {
                BankItem bankItem = new BankItem();
                bankItem.setEnrollmentType(bank.getEnrollmentType());
                bankItem.setLogoUrl(bank.getLogoUrl());
                bankItem.setMinRate(bank.getMinRate());
                bankItem.setName(bank.getName());
                bankItem.setTerminalsUrl(bank.getTerminalsUrl());
                bankItem.setRate(bank.getRate());
                bankItem.setIdOfBank(bank.getIdOfBank());

                bankItemList.getBanks().add(bankItem);
            }

            bd.banksList = bankItemList;

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;


        } catch (Exception e) {
            bd.resultCode = RC_INTERNAL_ERROR;
            bd.description = RC_INTERNAL_ERROR_DESC;


            logger.error(e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return bd;

    }

    @Override
    public Result changePersonalInfo(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "limit") Long limit, @WebParam(name = "address") String address,
          @WebParam(name = "phone") String phone, @WebParam(name = "mobilePhone") String mobilePhone,
          @WebParam(name = "email") String email,
          @WebParam(name = "smsNotificationState") boolean smsNotificationState) {

        authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);

        try {
            DAOService daoService = DAOService.getInstance();


            //change limit
            if (limit < 0) {
                r = new Result(RC_INVALID_DATA, "Лимит не может быть меньше нуля");
                return r;
            }
            if (!daoService.setClientExpenditureLimit(contractId, limit)) {
                r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }

            //change email
            if (!daoService.setClientEmail(contractId, email)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //change mobile phone
            mobilePhone = Client.checkAndConvertMobile(mobilePhone);
            if (mobilePhone == null) {
                r.resultCode = RC_INVALID_DATA;
                r.description = "Неверный формат телефона";
                return r;
            }
            if (!daoService.setClientMobilePhone(contractId, mobilePhone)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //enableNotificationBySms
            if (!daoService.enableClientNotificationBySMS(contractId, smsNotificationState)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }
            //change phone
            if (!daoService.setClientPhone(contractId, phone)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //change address
            if (!daoService.setClientAddress(contractId, address)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

        } catch (Exception e) {
            logger.error("error in changePersonalInfo: ", e);
        }
        return r;
    }

    @WebMethod(operationName = "getHiddenPages")
    public HiddenPagesResult getHiddenPages() {
        RuntimeContext runtimeContext = null;
        HiddenPagesResult r = new HiddenPagesResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        String hiddenPages = runtimeContext
              .getPropertiesValue(RuntimeContext.PARAM_NAME_HIDDEN_PAGES_IN_CLIENT_ROOM, "");
        r.hiddenPages = hiddenPages;
        return r;
    }


    @Override
    public QuestionaryResultList getActiveMenuQuestions(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "currentDate")
          final Date currentDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                processQuestionaryList(client, data, objectFactory, session, currentDate, QuestionaryType.MENU);
            }
        });

        QuestionaryResultList questionaryResultList = new QuestionaryResultList();
        questionaryResultList.questionaryList = data.getQuestionaryList();
        questionaryResultList.resultCode = data.getResultCode();
        questionaryResultList.description = data.getDescription();
        return questionaryResultList;
    }

    @Override
    public Result setAnswerFromQuestion(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "IdOfAnswer") Long idOfAnswer) {
        authenticateRequest(contractId);
        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            QuestionaryService questionaryService = new QuestionaryService();
            questionaryService.registrationAnswerByClient(persistenceSession, contractId, idOfAnswer);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
            logger.error(e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return r;
    }

    private void processQuestionaryList(Client client, Data data, ObjectFactory objectFactory, Session session,
          Date currentDate, QuestionaryType type) {
        DetachedCriteria orgCriteria = DetachedCriteria.forClass(Client.class).createAlias("org", "o", JoinType.NONE)
              .setProjection(Property.forName("org.idOfOrg"))
              .add(Restrictions.eq("contractId", client.getContractId()));
        Criteria questionaryCriteria = session.createCriteria(Questionary.class);
        questionaryCriteria.add(Restrictions.eq("status", QuestionaryStatus.START));
        questionaryCriteria.add(Restrictions.eq("questionaryType", type));
        questionaryCriteria.createAlias("orgs", "org", JoinType.INNER_JOIN);
        questionaryCriteria.add(Property.forName("org.idOfOrg").eq(orgCriteria));
        questionaryCriteria.add(Restrictions.ge("viewDate", currentDate));
        questionaryCriteria.addOrder(org.hibernate.criterion.Order.asc("viewDate"));
        questionaryCriteria.setMaxResults(24);

        List<Questionary> questionaries = questionaryCriteria.list();
        QuestionaryList questionaryList = objectFactory.createQuestionaryList();

        for (Questionary questionary : questionaries) {
            Criteria criteria = session.createCriteria(ClientAnswerByQuestionary.class);
            criteria.add(Restrictions.in("answer", questionary.getAnswers()));
            criteria.add(Restrictions.eq("client", client));
            List<ClientAnswerByQuestionary> list = (List<ClientAnswerByQuestionary>) criteria.list();
            QuestionaryItem questionaryItem = new QuestionaryItem(questionary);
            if (!list.isEmpty()) {
                questionaryItem.setCheckedAnswer(list.get(0).getAnswer().getIdOfAnswer());
            }
            questionaryItem.addAnswers(questionary.getAnswers());
            questionaryList.getQ().add(questionaryItem);
        }
        data.setQuestionaryList(questionaryList);
    }


    @Override
    public ClientNotificationSettingsResult getClientNotificationTypes() {

        ClientNotificationSettingsResult res = new ClientNotificationSettingsResult(RC_OK, RC_OK_DESC);

        List<ClientNotificationSettingsItem> list = new ArrayList<ClientNotificationSettingsItem>();
        try {
            for (ClientNotificationSetting.Predefined predef : ClientNotificationSetting.Predefined.values()) {
                if (predef.getValue().equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                    continue;
                }
                ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                it.setTypeOfNotification(predef.getValue());
                it.setNameOfNotification(predef.getName());
                list.add(it);
            }
            res.setSettings(list);
        } catch (Exception e) {
            res.setResultCode(RC_INTERNAL_ERROR);
            res.setDescription(RC_INTERNAL_ERROR_DESC);
            logger.error(e.getMessage(), e);
        }
        return res;
    }


    @Override @SuppressWarnings("unchecked")
    public ClientNotificationSettingsResult getClientNotificationSettings(
          @WebParam(name = "contractId") Long contractId) {

        authenticateRequest(contractId);

        ClientNotificationSettingsResult res = new ClientNotificationSettingsResult(RC_OK, RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;

        List<ClientNotificationSettingsItem> list = new ArrayList<ClientNotificationSettingsItem>();
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                res.setResultCode(RC_CLIENT_NOT_FOUND);
                res.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                return res;
            }

            for (ClientNotificationSetting setting : client.getNotificationSettings()) {
                if (setting.getNotifyType()
                      .equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                    continue;
                }
                ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                it.setTypeOfNotification(setting.getNotifyType());
                it.setNameOfNotification(ClientNotificationSetting.Predefined.parse(setting.getNotifyType()).getName());
                list.add(it);
            }
            res.setSettings(list);
        } catch (Exception e) {
            res.setResultCode(RC_INTERNAL_ERROR);
            res.setDescription(RC_INTERNAL_ERROR_DESC);
            logger.error(e.getMessage(), e);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
        return res;
    }


    @Override
    public ClientNotificationChangeResult setClientNotificationSettings(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "notificationType") List<Long> notificationTypes) {
        authenticateRequest(contractId);

        ClientNotificationChangeResult res = new ClientNotificationChangeResult(RC_OK, RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                res.resultCode = RC_CLIENT_NOT_FOUND;
                res.description = RC_CLIENT_NOT_FOUND_DESC;
                return res;
            }
            if (notificationTypes != null) {
                for (ClientNotificationSetting.Predefined pd : ClientNotificationSetting.Predefined.values()) {
                    ClientNotificationSetting cns = new ClientNotificationSetting(client, pd.getValue());
                    if (notificationTypes.contains(pd.getValue()) || pd.getValue()
                          .equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                        client.getNotificationSettings().add(cns);
                    } else {
                        client.getNotificationSettings().remove(cns);
                    }
                }
            }
            persistenceTransaction.commit();
        } catch (Exception e) {
            res.resultCode = RC_INTERNAL_ERROR;
            res.description = RC_INTERNAL_ERROR_DESC;
            logger.error(e.getMessage(), e);
            HibernateUtils.rollback(persistenceTransaction, logger);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
        return res;
    }

    @Override
    public ClientConfirmPaymentData getStudentsByCanNotConfirmPayment(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientConfirmPaymentData studentsConfirmPaymentData = new ClientConfirmPaymentData();
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client teacher = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (teacher == null) {
                studentsConfirmPaymentData.resultCode = RC_CLIENT_NOT_FOUND;
                studentsConfirmPaymentData.description = RC_CLIENT_NOT_FOUND_DESC;
                studentsConfirmPaymentData.studentsConfirmPaymentList = new StudentsConfirmPaymentList();
            } else {
                List<StudentMustPayItem> studentMustPayItemList = new ArrayList<StudentMustPayItem>();
                if (teacher.getCanConfirmGroupPayment()) {
                    List students = DAOUtils
                          .fetchStudentsByCanNotConfirmPayment(persistenceSession, teacher.getIdOfClient());
                    Long idOfClient = null;
                    Long sum = 0L;
                    for (Object object : students) {
                        Object[] student = (Object[]) object;
                        Long balance = Long.valueOf(String.valueOf(student[3]));
                        Long paySum = Long.valueOf(String.valueOf(student[4]));
                        Long currentIdOfClient = Long.valueOf(String.valueOf(student[6]));
                        if (idOfClient == null || (!idOfClient.equals(currentIdOfClient))) {
                            idOfClient = currentIdOfClient;
                            sum = 0L;
                        }
                        if (balance + sum < 0 && idOfClient.equals(currentIdOfClient)) {
                            sum += paySum;
                            StudentMustPayItem mustPayItem = new StudentMustPayItem();
                            mustPayItem.setFirstName(String.valueOf(student[0]));
                            mustPayItem.setSurname(String.valueOf(student[1]));
                            mustPayItem.setSecondName(String.valueOf(student[2]));
                            mustPayItem.setBalance(balance);
                            mustPayItem.setCreateTime((Date) student[5]);
                            mustPayItem.setPaySum(paySum);
                            studentMustPayItemList.add(mustPayItem);
                        }
                    }
                    studentsConfirmPaymentData.resultCode = RC_OK;
                    studentsConfirmPaymentData.description = RC_OK_DESC;
                    studentsConfirmPaymentData.studentsConfirmPaymentList = new StudentsConfirmPaymentList(
                          studentMustPayItemList);
                } else {
                    List students = DAOUtils.fetchTeacherByDoConfirmPayment(persistenceSession);
                    Long idOfClient = null;
                    Long sum = 0L;

                    for (Object object : students) {
                        Object[] student = (Object[]) object;
                        if (Long.valueOf(String.valueOf(student[7])).equals(teacher.getIdOfClient())) {
                            Long balance = Long.valueOf(String.valueOf(student[3]));
                            Long paySum = Long.valueOf(String.valueOf(student[4]));
                            Long currentIdOfClient = Long.valueOf(String.valueOf(student[6]));
                            if (idOfClient == null || (!idOfClient.equals(currentIdOfClient))) {
                                idOfClient = currentIdOfClient;
                                sum = 0L;
                            }
                            if (balance + sum < 0 && idOfClient.equals(currentIdOfClient)) {
                                sum += paySum;
                                StudentMustPayItem mustPayItem = new StudentMustPayItem();
                                Person person = teacher.getPerson();
                                mustPayItem.setFirstName(person.getFirstName());
                                mustPayItem.setSurname(person.getSurname());
                                mustPayItem.setSecondName(person.getSecondName());
                                mustPayItem.setBalance(balance);
                                mustPayItem.setCreateTime((Date) student[5]);
                                mustPayItem.setPaySum(paySum);
                                studentMustPayItemList.add(mustPayItem);
                            }
                        }
                    }
                }
                studentsConfirmPaymentData.resultCode = RC_OK;
                studentsConfirmPaymentData.description = RC_OK_DESC;
                studentsConfirmPaymentData.studentsConfirmPaymentList = new StudentsConfirmPaymentList(
                      studentMustPayItemList);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to Students By Can Not Confirm Payment settings", e);
            studentsConfirmPaymentData.resultCode = RC_INTERNAL_ERROR;
            studentsConfirmPaymentData.description = RC_INTERNAL_ERROR_DESC;
            studentsConfirmPaymentData.studentsConfirmPaymentList = new StudentsConfirmPaymentList();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        return studentsConfirmPaymentData;
    }

    @Override
    public ClientStatsResult getClientStats(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
          @WebParam(name = "type") int type) {
        authenticateRequest(contractId);

        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        ClientStatsResult r = new ClientStatsResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = findClientByContractId(persistenceSession, contractId, r);
            if (client == null) {
                return r;
            }

            r.stats = RuntimeContext.getAppContext().getBean(ClientStatsReporter.class)
                  .getStatsForClient(client, startDate, endDate);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;

        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }


        return r;
    }

    @Override
    public ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        return findComplexesWithSubFeeding(contractId, null);
    }

    @Override
    public ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "san") String san) {
        authenticateRequest(null);
        return findComplexesWithSubFeeding(null, san);
    }

    public ComplexInfoResult findComplexesWithSubFeeding(Long contractId, String san) {
        Session session = null;
        Transaction transaction = null;
        ComplexInfoResult result = new ComplexInfoResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                return result;
            }
            Org org = client.getOrg();
            transaction.commit();
            SubscriptionFeedingService sfService = RuntimeContext.getAppContext()
                  .getBean(SubscriptionFeedingService.class);
            final String groupName = client.getClientGroup().getGroupName();
            final boolean isParent = client.getIdOfClientGroup() >= ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES
                  || ClientGroup.predefinedGroupNames().contains(groupName);
            List<ComplexInfo> complexInfoList = sfService.findComplexesWithSubFeeding(org, isParent);
            List<ComplexInfoExt> list = new ArrayList<ComplexInfoExt>();
            result.getComplexInfoList().setList(list);
            for (ComplexInfo ci : complexInfoList) {
                list.add(new ComplexInfoExt(ci));
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private <T extends Result> Client findClient(Session session, Long contractId, String san, final T res)
          throws Exception {
        if (contractId != null) {
            return findClientByContractId(session, contractId, res);
        } else if (san != null) {
            return findClientBySan(session, san, res);
        } else {
            return null;
        }
    }

    private <T extends Result> Client findClientByContractId(Session session, Long contractId, final T res)
          throws Exception {
        Client client = DAOUtils.findClientByContractId(session, contractId);
        if (client == null) {
            res.resultCode = RC_CLIENT_NOT_FOUND;
            res.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return client;
    }

    private <T extends Result> Client findClientBySan(Session session, String san, final T res) throws Exception {
        List<Client> clientList = DAOUtils.findClientBySan(session, san);
        if (clientList.isEmpty()) {
            res.resultCode = RC_CLIENT_NOT_FOUND;
            res.description = RC_CLIENT_NOT_FOUND_DESC;
            return null;
        }
        if (clientList.size() > 1) {
            res.resultCode = RC_SEVERAL_CLIENTS_WERE_FOUND;
            res.description = RC_SEVERAL_CLIENTS_WERE_FOUND_DESC;
            return null;
        }
        return clientList.get(0);
    }


    @Override
    public TransferSubBalanceListResult getTransferSubBalanceList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        TransferSubBalanceListResult result = new TransferSubBalanceListResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(SubAccountTransfer.class);
            criteria.add(Restrictions.eq("clientTransfer", client));
            criteria.add(Restrictions.between("createTime", startDate, endDate));
            criteria.setMaxResults(MAX_RECS);
            List list = criteria.list();
            List<TransferSubBalanceExt> t = result.transferSubBalanceListExt.getT();
            for (Object obj : list) {
                SubAccountTransfer subAccountTransfer = (SubAccountTransfer) obj;
                TransferSubBalanceExt transferSubBalance = new TransferSubBalanceExt();
                transferSubBalance.setCreateTime(subAccountTransfer.getCreateTime());
                transferSubBalance.setBalanceBenefactor(subAccountTransfer.getBalanceBenefactor());
                transferSubBalance.setBalanceBeneficiary(subAccountTransfer.getBalanceBeneficiary());
                transferSubBalance.setTransferSum(subAccountTransfer.getTransferSum());
                t.add(transferSubBalance);
            }
            transaction.commit();
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public TransferSubBalanceListResult getTransferSubBalanceList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(null);
        Session session = null;
        Transaction transaction = null;
        TransferSubBalanceListResult result = new TransferSubBalanceListResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, null, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(SubAccountTransfer.class);
            criteria.add(Restrictions.eq("clientTransfer", client));
            criteria.add(Restrictions.between("createTime", startDate, endDate));
            criteria.setMaxResults(MAX_RECS);
            List list = criteria.list();
            List<TransferSubBalanceExt> t = result.transferSubBalanceListExt.getT();
            for (Object obj : list) {
                SubAccountTransfer subAccountTransfer = (SubAccountTransfer) obj;
                TransferSubBalanceExt transferSubBalance = new TransferSubBalanceExt();
                transferSubBalance.setCreateTime(subAccountTransfer.getCreateTime());
                transferSubBalance.setBalanceBenefactor(subAccountTransfer.getBalanceBenefactor());
                transferSubBalance.setBalanceBeneficiary(subAccountTransfer.getBalanceBeneficiary());
                transferSubBalance.setTransferSum(subAccountTransfer.getTransferSum());
                t.add(transferSubBalance);
            }
            transaction.commit();
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }


    @Override
    public SubscriptionFeedingSettingResult getSubscriptionFeedingSetting(
          @WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingSettingResult result = new SubscriptionFeedingSettingResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(ECafeSettings.class);
            final Org clientOrg = client.getOrg();
            final Long idOfOrg = clientOrg.getIdOfOrg();
            criteria.add(Restrictions.eq("orgOwner", idOfOrg));
            criteria.add(Restrictions.eq("settingsId", SettingsIds.SubscriberFeeding));
            criteria.add(Restrictions.eq("deletedState", false));
            List list = criteria.list();
            transaction.commit();
            if (list == null || list.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description =
                      "Отсутствуют настройки абонементного питания для организации " + clientOrg.getShortName();
                return result;
            }
            if (list.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = "Организация имеет более одной настройки " + clientOrg.getShortName();
                return result;
            }
            ECafeSettings settings = (ECafeSettings) list.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                  .getSplitSettingValue();
            SubscriptionFeedingSettingExt settingExt = new SubscriptionFeedingSettingExt();
            settingExt.setDayDeActivate(parser.getDayDeActivate());
            Date currentDay = new Date();
            final int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = (hoursForbidChange % 24 == 0 ? hoursForbidChange / 24 : hoursForbidChange / 24 + 1);
            Date dayForbid = CalendarUtils.addDays(currentDay, dayForbidChange);
            if (dayForbid.getHours() >= 12) {
                dayForbidChange++;
            }
            settingExt.setDayForbidChange(dayForbidChange);
            settingExt.setDayRequest(parser.getDayRequest());
            settingExt.setEnableFeeding(parser.isEnableFeeding());
            result.subscriptionFeedingSettingExt = settingExt;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public SubscriptionFeedingSettingResult getSubscriptionFeedingSetting(@WebParam(name = "san") String san) {
        authenticateRequest(null);
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingSettingResult result = new SubscriptionFeedingSettingResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, null, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(ECafeSettings.class);
            final Long idOfOrg = client.getOrg().getIdOfOrg();
            criteria.add(Restrictions.eq("orgOwner", idOfOrg));
            criteria.add(Restrictions.eq("settingsId", SettingsIds.SubscriberFeeding));
            List list = criteria.list();
            transaction.commit();
            if (list == null || list.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                            client.getOrg().getShortName(), idOfOrg);
                return result;
            }
            if (list.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String.format("Организация имеет более одной настройки %s (IdOfOrg = %s)",
                      client.getOrg().getShortName(), idOfOrg);
                return result;
            }
            ECafeSettings settings = (ECafeSettings) list.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                  .getSplitSettingValue();
            SubscriptionFeedingSettingExt settingExt = new SubscriptionFeedingSettingExt();
            settingExt.setDayDeActivate(parser.getDayDeActivate());
            Date currentDay = new Date();
            final int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = (hoursForbidChange % 24 == 0 ? hoursForbidChange / 24 : hoursForbidChange / 24 + 1);
            Date dayForbid = CalendarUtils.addDays(currentDay, dayForbidChange);
            if (dayForbid.getHours() >= 12) {
                dayForbidChange++;
            }
            settingExt.setDayForbidChange(dayForbidChange);
            //settingExt.setDayForbidChange(parser.getDayForbidChange());
            settingExt.setDayRequest(parser.getDayRequest());
            settingExt.setEnableFeeding(parser.isEnableFeeding());
            result.subscriptionFeedingSettingExt = settingExt;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public SubscriptionFeedingResult getCurrentSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "currentDay") Date currentDay) {
        authenticateRequest(contractId);
        return getCurrentSubscriptionFeeding(contractId, null, currentDay);
    }

    @Override
    public SubscriptionFeedingResult getCurrentSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "currentDay") Date currentDay) {
        authenticateRequest(null);
        return getCurrentSubscriptionFeeding(null, san, currentDay);
    }

    private SubscriptionFeedingResult getCurrentSubscriptionFeeding(Long contractId, String san,  Date currentDay) {
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingResult result = new SubscriptionFeedingResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            transaction.commit();
            SubscriptionFeedingService service = SubscriptionFeedingService.getInstance();
            SubscriptionFeeding sf = service.getCurrentSubscriptionFeedingByClientToDay(client, currentDay);
            if(sf==null){
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
            } else {
                result.setSubscriptionFeedingExt(new SubscriptionFeedingExt(sf));
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
            }
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }

        return result;
    }

    @Override
    public SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(
          @WebParam(name = "contractId") Long contractId, @WebParam(name = "startDate") Date startDate,
          @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(contractId);
        return getSubscriptionFeedingHistoryList(contractId, null, startDate, endDate);
    }

    @Override
    public SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(null);
        return getSubscriptionFeedingHistoryList(null, san, startDate, endDate);
    }

    private SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(Long contractId, String san, Date startDate,
          Date endDate) {
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingListResult result = new SubscriptionFeedingListResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            transaction.commit();
            SubscriptionFeedingService subscriptionFeedingService = SubscriptionFeedingService.getInstance();
            List<SubscriptionFeeding> subscriptionFeedings = subscriptionFeedingService
                  .findSubscriptionFeedingByClient(client, startDate, endDate);
            for (SubscriptionFeeding subscriptionFeeding : subscriptionFeedings) {
                result.subscriptionFeedingListExt.getS().add(new SubscriptionFeedingExt(subscriptionFeeding));
            }

            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }

        return result;
    }

    @Override
    public CycleDiagramList getCycleDiagramHistoryList(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(contractId);
        return getCycleDiagramHistoryList(contractId, null, startDate, endDate);
    }

    @Override
    public CycleDiagramList getCycleDiagramHistoryList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(null);
        return getCycleDiagramHistoryList(null, san, startDate, endDate);
    }

    private CycleDiagramList getCycleDiagramHistoryList(Long contractId, String san, Date startDate, Date endDate) {
        Session session = null;
        Transaction transaction = null;
        CycleDiagramList result = new CycleDiagramList();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            SubscriptionFeedingService service = SubscriptionFeedingService.getInstance();
            List<CycleDiagram> cycleDiagrams = service.findCycleDiagramsByClient(client, startDate, endDate);
            for (CycleDiagram cycleDiagram : cycleDiagrams) {
                result.cycleDiagramListExt.getC().add(new CycleDiagramExt(cycleDiagram));
            }
            transaction.commit();
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public CycleDiagramList getCycleDiagramList(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        return getCycleDiagramList(contractId, null);
    }

    @Override
    public CycleDiagramList getCycleDiagramList(@WebParam(name = "san") String san) {
        authenticateRequest(null);
        return getCycleDiagramList(null, san);
    }

    private CycleDiagramList getCycleDiagramList(Long contractId, String san) {
        Session session = null;
        Transaction transaction = null;
        CycleDiagramList result = new CycleDiagramList();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            transaction.commit();
            SubscriptionFeedingService service = SubscriptionFeedingService.getInstance();
            List<CycleDiagram> cycleDiagrams = service.findCycleDiagramsByClient(client);
            for (CycleDiagram cycleDiagram : cycleDiagrams) {
                result.cycleDiagramListExt.getC().add(new CycleDiagramExt(cycleDiagram));
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result activateSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram) {
        authenticateRequest(contractId);
        return activateSubscriptionFeeding(contractId, null, cycleDiagram);
    }

    @Override
    public Result activateSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram) {
        authenticateRequest(null);
        return activateSubscriptionFeeding(null, san, cycleDiagram);
    }

    private Result activateSubscriptionFeeding(Long contractId, String san, CycleDiagramExt cycleDiagram) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Org clientOrg = client.getOrg();
            Date currentDate = new Date();
            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                  .geteCafeSettingses(client.getOrg().getIdOfOrg(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                            client.getOrg().getShortName(), client.getOrg().getIdOfOrg());
                return result;
            }
            SubscriptionFeeding subscriptionFeeding =
                  SubscriptionFeedingService.getCurrentSubscriptionFeedingByClientToDay(session, client, currentDate);
            if(subscriptionFeeding==null){
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
                return result;
            }
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser;
            parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();

            final int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = (hoursForbidChange %24==0? hoursForbidChange /24: hoursForbidChange /24+1);
            Date dayForbid = CalendarUtils.addDays(currentDate, dayForbidChange);
            if(dayForbid.getHours()>=12){
                dayForbid = CalendarUtils.addOneDay(currentDate);
            }
            Date activateDate = cycleDiagram.getDateActivationDiagram();
            if(activateDate.getTime()<dayForbid.getTime()){
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Не верная дата активация подписки";
                return result;
            }

            SubscriptionFeeding sf = new SubscriptionFeeding();
            sf.fill(subscriptionFeeding);
            sf.setDateActivateService(activateDate);
            sf.setStaff(null);
            sf.setDeletedState(false);
            Long sfVersion = daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName());
            sf.setGlobalVersionOnCreate(sfVersion);
            sf.setGlobalVersion(sfVersion);
            Criteria criteria = session.createCriteria(CycleDiagram.class);
            criteria.add(Restrictions.eq("client", client));
            criteria.add(Restrictions.eq("dateActivationDiagram", cycleDiagram.getDateActivationDiagram()));
            List list = criteria.list();
            SubscriptionFeedingService sfService = SubscriptionFeedingService.getInstance();
            if (list.isEmpty()) {
                // создаем новую
                CycleDiagram diagram = new CycleDiagram();
                diagram.setCreatedDate(new Date());
                diagram.setClient(client);
                diagram.setOrgOwner(clientOrg.getIdOfOrg());
                diagram.setIdOfClient(client.getIdOfClient());
                diagram.setDateActivationDiagram(cycleDiagram.getDateActivationDiagram());
                diagram.setStateDiagram(StateDiagram.WAIT);
                diagram.setDeletedState(false);
                diagram.setSendAll(SendToAssociatedOrgs.SendToSelf);
                Long version = DAOService.getInstance()
                      .updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
                diagram.setGlobalVersionOnCreate(version);
                diagram.setGlobalVersion(version);
                List<ComplexInfo> availableComplexes = sfService.findComplexesWithSubFeeding(clientOrg);
                diagram.setMonday(cycleDiagram.getMonday());
                diagram.setMondayPrice(sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                diagram.setTuesday(cycleDiagram.getTuesday());
                diagram.setTuesdayPrice(sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                diagram.setWednesday(cycleDiagram.getWednesday());
                diagram.setWednesdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                diagram.setThursday(cycleDiagram.getThursday());
                diagram.setThursdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                diagram.setFriday(diagram.getFriday());
                diagram.setFridayPrice(sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                diagram.setSaturday(cycleDiagram.getSaturday());
                diagram.setSaturdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                diagram.setSunday("");
                diagram.setSundayPrice(0L);
                diagram.setStaff(null);
                session.save(diagram);
            } else {
                // изменяем те что есть
                for (Object obj : list) {
                    CycleDiagram diagram = (CycleDiagram) obj;
                    diagram.setDateActivationDiagram(cycleDiagram.getDateActivationDiagram());
                    diagram.setStateDiagram(StateDiagram.WAIT);
                    diagram.setDeletedState(false);
                    diagram.setSendAll(SendToAssociatedOrgs.SendToSelf);
                    Long version = DAOService.getInstance()
                          .updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
                    //diagram.setGlobalVersionOnCreate(version);
                    diagram.setGlobalVersion(version);
                    List<ComplexInfo> availableComplexes = sfService.findComplexesWithSubFeeding(clientOrg);
                    diagram.setMonday(cycleDiagram.getMonday());
                    diagram.setMondayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                    diagram.setTuesday(cycleDiagram.getTuesday());
                    diagram.setTuesdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                    diagram.setWednesday(cycleDiagram.getWednesday());
                    diagram.setWednesdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                    diagram.setThursday(cycleDiagram.getThursday());
                    diagram.setThursdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                    diagram.setFriday(cycleDiagram.getFriday());
                    diagram.setFridayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                    diagram.setSaturday(cycleDiagram.getSaturday());
                    diagram.setSaturdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                    diagram.setSunday("");
                    diagram.setSundayPrice(0L);
                    session.save(diagram);
                }
            }
            session.save(sf);
            transaction.commit();
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result suspendSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "suspendDate") Date suspendDate) {
        authenticateRequest(contractId);
        return suspendSubscriptionFeeding(contractId, null, suspendDate);
    }

    @Override
    public Result suspendSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "suspendDate") Date suspendDate) {
        authenticateRequest(null);
        return suspendSubscriptionFeeding(null, san, suspendDate);
    }

    private Result suspendSubscriptionFeeding(Long contractId, String san, Date suspendDate) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            SubscriptionFeeding subscriptionFeeding =
                  SubscriptionFeedingService.getCurrentSubscriptionFeedingByClientToDay(session, client, suspendDate);

            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                  .geteCafeSettingses(subscriptionFeeding.getOrgOwner(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                            client.getOrg().getShortName(), subscriptionFeeding.getOrgOwner());
                return result;
            }
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser;
            parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            Date date = new Date();

            final int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = (hoursForbidChange %24==0? hoursForbidChange /24: hoursForbidChange /24+1);
            Date dayForbid = CalendarUtils.addDays(date, dayForbidChange);
            if(dayForbid.getHours()>=12){
                dayForbid = CalendarUtils.addOneDay(date);
            }
            if(suspendDate.getTime()<dayForbid.getTime()){
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Не верная дата приостановки подписки";
                return result;
            }
            subscriptionFeeding.setLastDatePauseService(suspendDate);
            subscriptionFeeding.setWasSuspended(true);
            subscriptionFeeding.setGlobalVersion(daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
            subscriptionFeeding.setLastUpdate(new Date());
            session.saveOrUpdate(subscriptionFeeding);
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result reopenSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "activateDate") Date activateDate) {
        authenticateRequest(contractId);
        return reopenSubscriptionFeeding(contractId, null, activateDate);
    }

    @Override
    public Result reopenSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "activateDate") Date activateDate) {
        authenticateRequest(null);
        return reopenSubscriptionFeeding(null, san, activateDate);
    }

    private Result reopenSubscriptionFeeding(Long contractId, String san, Date activateDate) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            SubscriptionFeeding subscriptionFeeding =
                  SubscriptionFeedingService.getCurrentSubscriptionFeedingByClientToDay(session, client, activateDate);
            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                  .geteCafeSettingses(subscriptionFeeding.getOrgOwner(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                            client.getOrg().getShortName(), client.getOrg().getIdOfOrg());
                return result;
            }
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser;
            parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            Date date = new Date();

            final int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = (hoursForbidChange %24==0? hoursForbidChange /24: hoursForbidChange /24+1);
            Date dayForbid = CalendarUtils.addDays(date, dayForbidChange);
            if(dayForbid.getHours()>=12){
                dayForbid = CalendarUtils.addOneDay(date);
            }
            if(activateDate.getTime()<dayForbid.getTime()){
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Не верная дата активация циклограммы";
                return result;
            }
            SubscriptionFeeding sf = new SubscriptionFeeding();
            sf.setCreatedDate(new Date());
            sf.setLastUpdate(new Date());
            sf.fill(subscriptionFeeding);
            sf.setDateActivateService(activateDate);
            sf.setLastDatePauseService(null);
            sf.setStaff(null);
            sf.setDeletedState(false);
            Long version = daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName());
            sf.setGlobalVersionOnCreate(version);
            sf.setGlobalVersion(version);
            session.persist(sf);
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result cancelSubscriptionFeeding(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        return cancelSubscriptionFeeding(contractId, null);
    }

    @Override
    public Result cancelSubscriptionFeeding(@WebParam(name = "san") String san) {
        authenticateRequest(null);
        return cancelSubscriptionFeeding(null, san);
    }

    private Result cancelSubscriptionFeeding(Long contractId, String san) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            SubscriptionFeeding subscriptionFeeding =
                  SubscriptionFeedingService.getCurrentSubscriptionFeedingByClientToDay(session, client, new Date());
            if(subscriptionFeeding==null){
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
                return result;
            }
            DAOService daoService = DAOService.getInstance();
            subscriptionFeeding.setWasSuspended(false);
            subscriptionFeeding.setLastDatePauseService(null);
            subscriptionFeeding.setGlobalVersion(daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
            subscriptionFeeding.setLastUpdate(new Date());
            session.saveOrUpdate(subscriptionFeeding);
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result putCycleDiagram(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram) {
        authenticateRequest(contractId);
        return putCycleDiagram(contractId, null, cycleDiagram);
    }

    @Override
    public Result putCycleDiagram(@WebParam(name = "san") String san,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram) {
        authenticateRequest(null);
        return putCycleDiagram(null, san, cycleDiagram);
    }

    private Result putCycleDiagram(Long contractId, String san, CycleDiagramExt cycleDiagram) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria settingCriteria = session.createCriteria(ECafeSettings.class);
            final Org clientOrg = client.getOrg();
            final Long idOfOrg = clientOrg.getIdOfOrg();
            settingCriteria.add(Restrictions.eq("orgOwner", idOfOrg));
            settingCriteria.add(Restrictions.eq("settingsId", SettingsIds.SubscriberFeeding));
            List settingsList = settingCriteria.list();
            if (settingsList.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                            clientOrg.getShortName(), idOfOrg);
                return result;
            }
            if (settingsList.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Организация имеет более одной настройки %s (IdOfOrg = %s)", clientOrg.getShortName(),
                            idOfOrg);
                return result;
            }
            ECafeSettings settings = (ECafeSettings) settingsList.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                  .getSplitSettingValue();
            Date date = new Date();

            final int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = (hoursForbidChange %24==0? hoursForbidChange /24: hoursForbidChange /24+1);
            Date dayForbid = CalendarUtils.addDays(date, dayForbidChange);
            if(dayForbid.getHours()>=12){
                dayForbid = CalendarUtils.addOneDay(date);
            }
            if(cycleDiagram.getDateActivationDiagram().getTime()<dayForbid.getTime()){
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Не верная дата активации циклограммы";
                return result;
            }
            Criteria criteria = session.createCriteria(CycleDiagram.class);
            criteria.add(Restrictions.eq("client", client));
            criteria.add(Restrictions.eq("dateActivationDiagram", cycleDiagram.getDateActivationDiagram()));
            List list = criteria.list();
            SubscriptionFeedingService sfService = SubscriptionFeedingService.getInstance();
            if (list.isEmpty()) {
                // создаем новую
                CycleDiagram diagram = new CycleDiagram();
                diagram.setCreatedDate(new Date());
                diagram.setClient(client);
                diagram.setOrgOwner(clientOrg.getIdOfOrg());
                diagram.setIdOfClient(client.getIdOfClient());
                diagram.setDateActivationDiagram(cycleDiagram.getDateActivationDiagram());
                diagram.setStateDiagram(StateDiagram.WAIT);
                diagram.setDeletedState(false);
                diagram.setSendAll(SendToAssociatedOrgs.SendToSelf);
                Long version = DAOService.getInstance()
                      .updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
                diagram.setGlobalVersionOnCreate(version);
                diagram.setGlobalVersion(version);
                List<ComplexInfo> availableComplexes = sfService.findComplexesWithSubFeeding(clientOrg);
                diagram.setMonday(cycleDiagram.getMonday());
                diagram.setMondayPrice(sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                diagram.setTuesday(cycleDiagram.getTuesday());
                diagram.setTuesdayPrice(sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                diagram.setWednesday(cycleDiagram.getWednesday());
                diagram.setWednesdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                diagram.setThursday(cycleDiagram.getThursday());
                diagram.setThursdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                diagram.setFriday(diagram.getFriday());
                diagram.setFridayPrice(sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                diagram.setSaturday(cycleDiagram.getSaturday());
                diagram.setSaturdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                diagram.setSunday("");
                diagram.setSundayPrice(0L);
                diagram.setStaff(null);
                session.save(diagram);
            } else {
                // изменяем те что есть
                for (Object obj : list) {
                    CycleDiagram diagram = (CycleDiagram) obj;
                    diagram.setDateActivationDiagram(cycleDiagram.getDateActivationDiagram());
                    diagram.setStateDiagram(StateDiagram.WAIT);
                    diagram.setDeletedState(false);
                    diagram.setSendAll(SendToAssociatedOrgs.SendToSelf);
                    Long version = DAOService.getInstance()
                          .updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
                    //diagram.setGlobalVersionOnCreate(version);
                    diagram.setGlobalVersion(version);
                    List<ComplexInfo> availableComplexes = sfService.findComplexesWithSubFeeding(clientOrg);
                    diagram.setMonday(cycleDiagram.getMonday());
                    diagram.setMondayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                    diagram.setTuesday(cycleDiagram.getTuesday());
                    diagram.setTuesdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                    diagram.setWednesday(cycleDiagram.getWednesday());
                    diagram.setWednesdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                    diagram.setThursday(cycleDiagram.getThursday());
                    diagram.setThursdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                    diagram.setFriday(cycleDiagram.getFriday());
                    diagram.setFridayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                    diagram.setSaturday(cycleDiagram.getSaturday());
                    diagram.setSaturdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                    diagram.setSunday("");
                    diagram.setSundayPrice(0L);
                    session.save(diagram);
                }
            }
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public MenuListWithProhibitionsResult getMenuListWithProhibitions(Long contractId, final Date startDate,
          final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                processMenuListWithProhibitions(client, data, objectFactory, session, startDate, endDate);
            }
        });
        MenuListWithProhibitionsResult menuListWithProhibitionsResult = new MenuListWithProhibitionsResult();
        menuListWithProhibitionsResult.menuList = data.getMenuListExt();
        menuListWithProhibitionsResult.resultCode = data.getResultCode();
        menuListWithProhibitionsResult.description = data.getDescription();
        return menuListWithProhibitionsResult;
    }

    //возврат id
    //ProhibitionResult
    @Override
    public ProhibitionsResult addProhibition(Long contractId, String filterText, Integer filterType) {
        Session session = null;
        Transaction transaction = null;
        ProhibitionsResult result = new ProhibitionsResult();
        try {
            try {
                Date currentDate = new Date();
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }

                long maxVersion = DAOUtils.nextVersionByProhibitionsMenu(session);

                ProhibitionMenu prohibitionMenu = null;

                Criteria prohibitionsCriteria = session.createCriteria(ProhibitionMenu.class);
                prohibitionsCriteria.add(Restrictions.eq("client", client));
                prohibitionsCriteria.add(Restrictions.eq("filterText", filterText));
                final ProhibitionFilterType typeBuId = ProhibitionFilterType.getTypeBuId(filterType);
                prohibitionsCriteria.add(Restrictions.eq("prohibitionFilterType", typeBuId));
                prohibitionMenu = (ProhibitionMenu) prohibitionsCriteria.uniqueResult();

                if (prohibitionMenu != null) {
                    if (prohibitionMenu.getDeletedState()) {
                        prohibitionMenu.setDeletedState(false);
                        prohibitionMenu.setVersion(maxVersion);
                        prohibitionMenu.setUpdateDate(new Date());
                        result.prohibitionId = prohibitionMenu.getIdOfProhibitions();
                        session.update(prohibitionMenu);
                    } else {
                        result.prohibitionId = prohibitionMenu.getIdOfProhibitions();
                        result.resultCode = RC_PROHIBIT_EXIST;
                        result.description = RC_PROHIBIT_EXIST_DESC;
                        return result;
                    }
                } else {
                    prohibitionMenu = new ProhibitionMenu();
                    prohibitionMenu.setVersion(maxVersion);
                    prohibitionMenu.setClient(client);
                    prohibitionMenu.setCreateDate(currentDate);
                    prohibitionMenu.setFilterText(filterText);
                    prohibitionMenu.setProhibitionFilterType(typeBuId);
                    prohibitionMenu.setDeletedState(false);
                    session.save(prohibitionMenu);
                    result.prohibitionId = prohibitionMenu.getIdOfProhibitions();
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    //возврат result
    @Override
    public ProhibitionsResult removeProhibition(Long contractId, Long prohibitionId) {
        Session session = null;
        Transaction transaction = null;
        ProhibitionsResult result = new ProhibitionsResult();
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }
                long maxVersion = DAOUtils.nextVersionByProhibitionsMenu(session);
                ProhibitionMenu prohibitionMenu = null;

                if (prohibitionId != null) {
                    Criteria prohibitionsCriteria = session.createCriteria(ProhibitionMenu.class);
                    prohibitionsCriteria.add(Restrictions.eq("idOfProhibitions", prohibitionId));
                    prohibitionsCriteria.add(Restrictions.eq("client", client));
                    prohibitionMenu = (ProhibitionMenu) prohibitionsCriteria.uniqueResult();

                    if (prohibitionMenu != null) {
                        if (prohibitionMenu.getDeletedState()) {
                            result.resultCode = RC_PROHIBIT_REMOVED;
                            result.description = RC_PROHIBIT_REMOVED_DESC;
                            return result;
                        }
                        prohibitionMenu.setVersion(maxVersion);
                        prohibitionMenu.setDeletedState(true);
                        prohibitionMenu.setUpdateDate(new Date());
                        session.update(prohibitionMenu);
                    } else {
                        result.resultCode = RC_PROHIBIT_NOT_FOUND;
                        result.description = RC_PROHIBIT_NOT_FOUND_DESC;
                        return result;
                    }
                } else {
                    result.resultCode = RC_PROHIBIT_NOT_FOUND;
                    result.description = RC_PROHIBIT_NOT_FOUND_DESC;
                    return result;
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }
}