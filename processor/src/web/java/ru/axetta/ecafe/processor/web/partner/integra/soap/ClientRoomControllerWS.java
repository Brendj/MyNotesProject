/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ClientPasswordRecover;
import ru.axetta.ecafe.processor.core.client.ClientStatsReporter;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.client.RequestWebParam;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.daoservices.DOVersionRepository;
import ru.axetta.ecafe.processor.core.daoservices.questionary.QuestionaryService;
import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.FinancialOpsManager;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig;
import ru.axetta.ecafe.processor.core.partner.integra.IntegraPartnerConfig;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.Menu;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDao;
import ru.axetta.ecafe.processor.core.persistence.dao.enterevents.EnterEventsRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.StateDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeedingType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Circulation;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.OrderPublication;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Publication;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.questionary.ClientAnswerByQuestionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryStatus;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryType;
import ru.axetta.ecafe.processor.core.persistence.service.enterevents.EnterEventsService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientGuardSanRebuildService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.service.SubscriptionFeedingService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.core.sync.SectionType;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ParameterStringUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.org.OrgSummary;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.org.OrgSummaryResult;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors.VisitorsSummary;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors.VisitorsSummaryList;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors.VisitorsSummaryResult;
import ru.axetta.ecafe.processor.web.partner.utils.HTTPData;
import ru.axetta.ecafe.processor.web.partner.utils.HTTPDataHandler;
import ru.axetta.ecafe.processor.web.ui.PaymentTextUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.hibernate.*;
import org.hibernate.criterion.*;
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
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

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
    private static final Long RC_SUBSCRIPTION_FEEDING_ACTIVATED = 330L;
    private static final Long RC_PUBLICATION_NOT_AVAILABLE = 340L;
    private static final Long RC_ORDER_PUBLICATION_NOT_FOUND = 350L;
    private static final Long RC_ORDER_PUBLICATION_CANT_BE_DELETED = 360L;
    private static final Long RC_ORDER_PUBLICATION_ALREADY_EXISTS = 370L;
    private static final Long RC_CLIENT_IS_NOT_WARD_OF_GUARDIAN = 380L;
    private static final Long RC_ORG_HOLDER_NOT_FOUND = 390L;
    private static final Long RC_CLIENT_GUID_NOT_FOUND = 400L;
    private static final Long RC_CLIENT_DOES_NOT_HAVE_PHOTO = 500L;
    private static final Long RC_IMAGE_SIZE_NOT_FOUND = 510L;
    private static final Long RC_IMAGE_NOT_VALIDATED = 520L;
    private static final Long RC_IMAGE_NOT_SAVED = 530L;
    private static final Long RC_IMAGE_NOT_DELETED = 540L;
    private static final Long RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO = 550L;
    private static final Long RC_CLIENT_PHOTO_UNDER_REGISTRY = 560L;
    private static final Long RC_CLIENT_GUARDIAN_NOT_FOUND = 570L;
    private static final Long RC_INVALID_OPERATION_VARIABLE_FEEDING = 580L;
    private static final Long RC_ERROR_CREATE_VARIABLE_FEEDING = 590L;
    private static final Long RC_ERROR_NOT_ALL_DAYS_FILLED_VARIABLE_FEEDING = 600L;


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
    private static final String RC_ERROR_CREATE_SUBSCRIPTION_FEEDING_DESC = "Неверная дата активация циклограммы";
    private static final String RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC = "Услуга не подключена";
    private static final String RC_PROHIBIT_EXIST_DESC = "Запрет с данными параметрами уже существует";
    private static final String RC_PROHIBIT_REMOVED_DESC = "Запрет с данными параметрами был удален";
    private static final String RC_PROHIBIT_NOT_FOUND_DESC = "Запрет с данными параметрами не найден";
    private static final String RC_PUBLICATION_NOT_AVAILABLE_DESC = "Книга не найдена или нет свободных экземпляров";
    private static final String RC_ORDER_PUBLICATION_NOT_FOUND_DESC = "Заказ не найден";
    private static final String RC_ORDER_PUBLICATION_CANT_BE_DELETED_DESC = "Заказ не может быть удален";
    private static final String RC_ORDER_PUBLICATION_ALREADY_EXISTS_DESC = "Заказ на выбранную книгу уже существует";
    private static final String RC_CLIENT_IS_NOT_WARD_OF_GUARDIAN_DESC = "Клиент не найден или не является опекаемым для данного опекуна";
    private static final String RC_ORG_HOLDER_NOT_FOUND_DESC = "Не найдена организация - держатель экземпляра";
    private static final String RC_CLIENT_GUID_NOT_FOUND_DESC = "GUID клиента не найден";
    private static final String RC_IMAGE_NOT_SAVED_DESC = "Не удалось сохранить фото";
    private static final String RC_IMAGE_NOT_DELETED_DESC = "Не удалось удалить фото";
    private static final String RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO_DESC = "У клиента нет неподтвержденного фото";
    private static final String RC_CLIENT_PHOTO_UNDER_REGISTRY_DESC = "Фото клиента в процессе сверки";
    private static final String RC_CLIENT_GUARDIAN_NOT_FOUND_DESC = "Связка клиент-представитель не найдена";
    private static final String RC_INVALID_OPERATION_VARIABLE_FEEDING_DESC = "Подписку на вариативное питание приостановить нельзя";
    private static final String RC_ERROR_CREATE_VARIABLE_FEEDING_DESC = "В рамках данного вида питания можно выбрать только один вариант комплекса каждого вида рациона (завтрак, обед)";
    private static final String RC_ERROR_NOT_ALL_DAYS_FILLED_VARIABLE_FEEDING_DESC = "В рамках данного вида питания должен быть выбран один вариант комплекса каждого вида рациона (завтрак, обед) на каждый день циклограммы в пределах недели";
    private static final int MAX_RECS = 50;
    private static final int MAX_RECS_getPurchaseList = 500;
    private static final int MAX_RECS_getEventsList = 1000;

    private static final String COMMENT_MPGU_CREATE = "{Создано на mos.ru %s пользователем с номером телефона %s)}";

    private static final List<SectionType> typesForSummary = new ArrayList<SectionType>(Arrays.asList(SectionType.ACC_INC_REGISTRY,
            SectionType.ACCOUNT_OPERATIONS_REGISTRY, SectionType.ACCOUNTS_REGISTRY, SectionType.ORGANIZATIONS_STRUCTURE, SectionType.CLIENT_REGISTRY));

    public static final int CIRCULATION_STATUS_FILTER_ALL = -1, CIRCULATION_STATUS_FILTER_ALL_ON_HANDS = -2;

    //private final Long[] orgs_VP_pilot = getVPOrgsList();

    private static final String QUERY_PUBLICATION_LIST =
            "select result.*, org.shortname from " +
            "(select fq.IdOfPublication, fq.Author, fq.Title, fq.Title2, fq.PublicationDate, fq.Publisher, fq.instancesAmount, count(ins.IdOfInstance) as instancesAvailable, fq.owner " +
                    "from (select pub.IdOfPublication, pub.Author, pub.Title, pub.Title2, pub.PublicationDate, pub.Publisher, " +
                    "count(ins.IdOfInstance) as instancesAmount, ins.OrgOwner as owner " +
                    "from cf_publications pub inner join cf_instances ins on pub.IdOfPublication = ins.IdOfPublication " +
                    "where ins.OrgOwner in (select friendlyorg from cf_friendly_organization where currentorg = :org) " +
                    "CONDITION " +
                    "group by pub.IdOfPublication, ins.OrgOwner order by Author limit :limit offset :offset) fq inner join " +
                    "cf_instances ins on fq.IdOfPublication = ins.IdOfPublication " +
                    "where ins.OrgOwner = fq.owner and not exists (select IdOfCirculation from cf_circulations cir inner join cf_issuable iss on cir.IdOfIssuable = iss.IdOfIssuable " +
                    "where iss.IdOfInstance = ins.IdOfInstance and cir.RealRefundDate is null) " +
                    "group by fq.IdOfPublication, fq.Author, fq.Title, fq.Title2, fq.PublicationDate, fq.Publisher, fq.instancesAmount, fq.owner " +
                    "order by fq.Author) result inner join cf_orgs org on result.owner=org.IdOfOrg";

    private static final String QUERY_PUBLICATION_LIST_COUNT = "select count(*) from (select distinct pub.IdOfPublication, ins.orgOwner " +
            "from cf_publications pub inner join cf_instances ins on pub.IdOfPublication = ins.IdOfPublication inner join cf_issuable iss on ins.IdOfInstance = iss.IdOfInstance " +
            "where ins.OrgOwner in (select friendlyorg from cf_friendly_organization where currentorg = :org) and not exists (select IdOfCirculation from cf_circulations cir where cir.IdOfIssuable = iss.IdOfIssuable and cir.RealRefundDate is null) ";

    private static final String QUERY_PUBLICATION_LIST_COUNT_TAIL = ") result";//alias for inner select

    private final Set<Date> eeManualCleared = new HashSet<Date>();

    static class Processor {

        public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
              Session persistenceSession, Transaction transaction) throws Exception {
        }

        public void process(Org org, Data data, ObjectFactory objectFactory, Session persistenceSession,
              Transaction transaction) throws Exception {
        }

        public void process(Client client, Data data, ObjectFactory objectFactory, Session persistenceSession) throws Exception {
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
// коментарии
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

        public Data process(Client client, Session session, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            try {
                processor.process(client, data, objectFactory, session);
            } catch (Exception e) {
                logger.error("Failed to process client room controller request", e);
                data.setResultCode(RC_INTERNAL_ERROR);
                data.setDescription(e.toString());
            }
            return data;
        }

        public Data process(Long contractId, Processor processor) {
            return process(contractId, processor, null);
        }

        public Data process(Long contractId, Processor processor, HTTPDataHandler handler) {

            Boolean enableSubBalanceOperation = RuntimeContext.getInstance()
                    .getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
            if (enableSubBalanceOperation) {
                String contractIdStr = String.valueOf(contractId);
                int len = contractIdStr.length();
                if (ContractIdGenerator.luhnTest(contractIdStr) || len < 2 || RuntimeContext.RegistryType.isSpb()) {
                    return process(contractId, CLIENT_ID_INTERNALID, processor, handler);
                } else {
                    return process(contractId, CLIENT_SUB_ID, processor, handler);
                }
            } else {
                return process(contractId, CLIENT_ID_INTERNALID, processor, handler);
            }
        }

        public Data process(Object id, int clientIdType, Processor processor, HTTPDataHandler handler) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            Integer subBalanceNum = 0;
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createExternalServicesPersistenceSession();
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
                        clientCriteria.add(
                                Restrictions.eq("contractId", Long.parseLong(subBalanceNumber.substring(0, len - 2))));
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
                        if (handler != null) {
                            handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), new Date(System.currentTimeMillis()), handler.getData().getSsoId(),
                                    client.getIdOfClient(), handler.getData().getOperationType());
                        }
                    } catch (NullPointerException e) {
                        data.setResultCode(RC_CLIENT_NOT_FOUND);
                        data.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                    }
                }
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
        HTTPData data1 = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data1);
        authenticateRequest(null, handler);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processSummary(client, data, objectFactory, session);
                  }
              }, handler);

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    @Override
    public ClientSummaryResult getSummaryByTypedId(String id, int idType) {
        HTTPData data1 = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data1);
        authenticateRequest(null, handler);

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
        }, handler);

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
        /*порог снижения баланса для отправки уведомления*/
        clientSummaryExt.setThresholdBalanceNotify(client.getBalanceToNotify());
        /* ФИО Клиента */
        clientSummaryExt.setFirstName(client.getPerson().getFirstName());
        clientSummaryExt.setLastName(client.getPerson().getSurname());
        clientSummaryExt.setMiddleName(client.getPerson().getSecondName());
        /* Флаги увидомлений клиента (Истина/ложь)*/
        clientSummaryExt.setNotifyViaEmail(client.isNotifyViaEmail());
        clientSummaryExt.setNotifyViaSMS(client.isNotifyViaSMS());
        clientSummaryExt.setNotifyViaPUSH(client.isNotifyViaPUSH());
        /* контактный телефон и емайл адрес электронной почты */
        clientSummaryExt.setMobilePhone(client.getMobile());
        clientSummaryExt.setEmail(client.getEmail());
        clientSummaryExt.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance().getLastProcessSectionsDate(client.getOrg().getIdOfOrg(),
                typesForSummary)));
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
        /* Группа к которой относится клиент (Наименование класса ученика) */
        if (client.getClientGroup() == null) {
            clientSummaryExt.setGrade(null);
        } else {
            clientSummaryExt.setGrade(client.getClientGroup().getGroupName());
        }
        /* Краткое наименование Учебного учреждения для сервиса информирования*/
        clientSummaryExt.setOfficialName(client.getOrg().getShortNameInfoService());
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

        clientSummaryExt.setOrgId(client.getOrg().getIdOfOrg());
        clientSummaryExt.setOrgType(client.getOrg().getType());

        clientSummaryExt.setLastConfirmMobile(toXmlDateTime(client.getLastConfirmMobile()));

        data.setClientSummaryExt(clientSummaryExt);
    }

    @Override
    public PhotoURLResult getPhotoURL(Long contractId, int size, boolean isNew) {
        PhotoURLResult result = new PhotoURLResult();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    return result;
                }
                getPhotoUrl(size, result, client, ImageUtils.findClientPhoto(session, client.getIdOfClient()), isNew);
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    private void getPhotoUrl(int size, PhotoURLResult result, Client client, ClientPhoto photo, boolean isNew) {
        try {
            result.URL = ImageUtils.getPhotoURL(client, photo, size, isNew);
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            result.status = ImageUtils.getPhotoStatus(photo);
        } catch (ImageUtils.NoPhotoException e) {
            result.URL = ImageUtils.getDefaultImageURL();
            result.resultCode = RC_CLIENT_DOES_NOT_HAVE_PHOTO;
            result.description = e.getMessage();
        } catch (ImageUtils.NoNewPhotoException e){
            result.resultCode = RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO;
            result.description = RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO_DESC;
        } catch (ImageUtils.NoSuchImageSizeException e){
            result.resultCode = RC_IMAGE_SIZE_NOT_FOUND;
            result.description = e.getMessage();
        }
    }

    @Override
    public PhotoURLResult uploadPhoto(Long contractId, Long guardianContractId, Image photo, int size) {
        PhotoURLResult result = new PhotoURLResult();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    return result;
                }
                Client guardian = findClient(session, guardianContractId, null, result);
                if (guardian == null) {
                    return result;
                }
                ClientPhoto clientPhoto = ImageUtils.findClientPhoto(session, client.getIdOfClient());
                if (clientPhoto != null) {
                    ImageUtils.saveImage(client, clientPhoto, photo, true);
                    clientPhoto.setIsNew(true);
                    clientPhoto.setIsCanceled(false);
                } else {
                    String imageName = ImageUtils.saveImage(client.getContractId(), client.getIdOfClient(), photo, true);
                    clientPhoto = new ClientPhoto(client.getIdOfClient(), guardian, imageName, true);
                }
                session.saveOrUpdate(clientPhoto);
                transaction.commit();
                transaction = null;
                getPhotoUrl(size, result, client, clientPhoto, true);
            } catch (IOException e){
                logger.error(RC_IMAGE_NOT_SAVED_DESC + ": " + e.getMessage(), e);
                result.resultCode = RC_IMAGE_NOT_SAVED;
                result.description = RC_IMAGE_NOT_SAVED_DESC + ": " + e.getMessage();
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (ImageUtils.ImageUtilsException e){
            result.resultCode = RC_IMAGE_NOT_VALIDATED;
            result.description = e.getMessage();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public Result deleteNewPhoto(Long contractId) {
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    return result;
                }
                ClientPhoto photo = ImageUtils.findClientPhoto(session, client.getIdOfClient());
                if (photo != null && photo.getIsNew()) {
                    boolean deleted = ImageUtils.deleteImage(client.getContractId(), client.getIdOfClient(),
                            photo.getName(), true);
                    if(!deleted){
                        result.resultCode = RC_IMAGE_NOT_DELETED;
                        result.description = RC_IMAGE_NOT_DELETED_DESC;
                    } else {
                        result.resultCode = RC_OK;
                        result.description = RC_OK_DESC;
                        photo.setIsNew(false);
                        session.update(photo);
                    }
                } else {
                    result.resultCode = RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO;
                    result.description = RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO_DESC;
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public PurchaseListResult getPurchaseList(Long contractId, final Date startDate, final Date endDate, final Short mode) {

        Long clientContractId = contractId;
        String contractIdstr = String.valueOf(contractId);
        if (ContractIdGenerator.luhnTest(contractIdstr) || RuntimeContext.RegistryType.isSpb()) {
            clientContractId = contractId;
        } else {
            int len = contractIdstr.length();
            if (len > 2 && ContractIdGenerator.luhnTest(contractIdstr.substring(0, len - 2))) {
                clientContractId = Long.parseLong(contractIdstr.substring(0, len - 2));
            }
        }

        HTTPData httpData = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(httpData);
        authenticateRequest(clientContractId, handler);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                  Session session, Transaction transaction) throws Exception {
                if (subBalanceNum.equals(0)) {
                    processPurchaseList(client, data, objectFactory, session, endDate, startDate, null,mode);
                }
                if (subBalanceNum.equals(1)) {
                    processPurchaseList(client, data, objectFactory, session, endDate, startDate,
                          OrderTypeEnumType.SUBSCRIPTION_FEEDING, mode);
                }
            }
        }, handler);
        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();

        return purchaseListResult;
    }

    @Override
    public PurchaseListResult getPurchaseList(String san, final Date startDate, final Date endDate, final Short mode) {
        HTTPData httpData = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(httpData);
        authenticateRequest(null, handler);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processPurchaseList(client, data, objectFactory, session, endDate, startDate, null,mode);
                  }
              }, handler);

        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();
        return purchaseListResult;
    }

    @Override
    public PurchaseListResult getPurchaseSubscriptionFeedingList(String san, final Date startDate, final Date endDate, final Short mode) {
        HTTPData httpData = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(httpData);
        authenticateRequest(null, handler);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processPurchaseList(client, data, objectFactory, session, endDate, startDate,
                            OrderTypeEnumType.SUBSCRIPTION_FEEDING, mode);
                  }
              }, handler);

        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();
        return purchaseListResult;
    }

    private void processPurchaseList(Client client, Data data, ObjectFactory objectFactory, Session session,
          Date endDate, Date startDate, OrderTypeEnumType orderType, Short mode) throws DatatypeConfigurationException {
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
            if (nRecs++ > MAX_RECS_getPurchaseList) {
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
            purchaseExt.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance().getLastProcessSectionsDate(order.getOrg().getIdOfOrg(),
                    SectionType.PAYMENT_REGISTRY)));
            if (order.getCard() == null) {
                purchaseExt.setIdOfCard(null);
            } else {
                purchaseExt.setIdOfCard(order.getCard().getIdOfCard());
            }
            //было так: purchaseExt.setIdOfCard(order.getCard().getCardPrintedNo());
            purchaseExt.setTime(toXmlDateTime(order.getCreateTime()));
            if (mode!=null && mode == 1){
                purchaseExt.setState(order.getState());
            }
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
    public PurchaseListWithDetailsResult getPurchaseListWithDetails(Long contractId, final Date startDate,
            final Date endDate, final Short mode) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(null, handler);
        ObjectFactory objectFactory = new ObjectFactory();
        return processPurchaseListWithDetails(contractId, objectFactory, startDate, endDate, mode, handler);
    }

    private PurchaseListWithDetailsResult processPurchaseListWithDetails(Long contractId, ObjectFactory objectFactory,
            Date startDate, Date endDate, Short mode, HTTPDataHandler handler) {
        PurchaseListWithDetailsResult result = new PurchaseListWithDetailsResult();
        PurchaseListWithDetailsExt purchaseListWithDetailsExt = objectFactory.createPurchaseListWithDetailsExt();
        try {
            Client client = DAOReadExternalsService.getInstance().findClient(null, contractId);

            if (client == null) {
                return result;
            }
            handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), new Date(System.currentTimeMillis()),
                    handler.getData().getSsoId(), client.getIdOfClient(), handler.getData().getOperationType());

            int nRecs = 0;
            Date nextToEndDate = DateUtils.addDays(endDate, 1);
            List<Order> ordersList = DAOReadExternalsService.getInstance().getClientOrdersByPeriod(client, startDate, nextToEndDate);
            if (ordersList.size() == 0) {
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
                result.purchaseListWithDetailsExt = purchaseListWithDetailsExt;
                return result;
            }
            List<OrderDetail> detailsList = DAOReadExternalsService.getInstance().getOrderDetailsByOrders(ordersList);

            Set<Long> orderOrgIds = getOrgsByOrders(ordersList);
            Set<Long> menuIds = getIdOfMenusByOrderDetails(detailsList);
            List<MenuDetail> menuDetails = DAOReadExternalsService.getInstance().getMenuDetailsByOrderDetails(orderOrgIds, menuIds, startDate, endDate);

            Map<Long, Date> lastProcessMap = new HashMap<Long, Date>();
            for (Object o : ordersList) {
                if (nRecs++ > MAX_RECS_getPurchaseList) {
                    break;
                }
                Order order = (Order) o;
                PurchaseWithDetailsExt purchaseWithDetailsExt = objectFactory.createPurchaseWithDetailsExt();
                purchaseWithDetailsExt.setByCard(order.getSumByCard());
                purchaseWithDetailsExt.setSocDiscount(order.getSocDiscount());
                purchaseWithDetailsExt.setTrdDiscount(order.getTrdDiscount());
                purchaseWithDetailsExt.setDonation(order.getGrantSum());
                purchaseWithDetailsExt.setSum(order.getRSum());
                purchaseWithDetailsExt.setByCash(order.getSumByCash());
                purchaseWithDetailsExt.setLastUpdateDate(toXmlDateTime(getLastPaymentRegistryDate(order.getOrg().getIdOfOrg(), lastProcessMap)));
                if (order.getCard() == null) {
                    purchaseWithDetailsExt.setIdOfCard(null);
                } else {
                    purchaseWithDetailsExt.setIdOfCard(order.getCard().getIdOfCard());
                }
                purchaseWithDetailsExt.setTime(toXmlDateTime(order.getCreateTime()));
                if (mode!=null && mode == 1){
                    purchaseWithDetailsExt.setState(order.getState());
                }
                for (OrderDetail od : findDetailsByOrder(order, detailsList)) {
                    PurchaseWithDetailsElementExt purchaseWithDetailsElementExt = objectFactory.createPurchaseWithDetailsElementExt();
                    purchaseWithDetailsElementExt.setIdOfOrderDetail(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                    purchaseWithDetailsElementExt.setAmount(od.getQty());
                    purchaseWithDetailsElementExt.setName(od.getMenuDetailName());
                    purchaseWithDetailsElementExt.setSum(od.getRPrice());
                    purchaseWithDetailsElementExt.setMenuType(od.getMenuType());
                    purchaseWithDetailsElementExt.setLastUpdateDate(toXmlDateTime(getLastPaymentRegistryDate(order.getOrg().getIdOfOrg(), lastProcessMap)));
                    if (od.isComplex()) {
                        purchaseWithDetailsElementExt.setType(1);
                    } else if (od.isComplexItem()) {
                        purchaseWithDetailsElementExt.setType(2);
                    } else {
                        purchaseWithDetailsElementExt.setType(0);
                    }

                    if (od.getIdOfMenuFromSync() != null) {

                        MenuDetail menuDetail = findMenuDetailByOrderDetail(od.getIdOfMenuFromSync(), menuDetails);

                        if (menuDetail != null) {
                            purchaseWithDetailsElementExt.setPrice(menuDetail.getPrice());
                            purchaseWithDetailsElementExt.setCalories(menuDetail.getCalories());
                            purchaseWithDetailsElementExt.setOutput(menuDetail.getMenuDetailOutput());
                            purchaseWithDetailsElementExt.setVitB1(menuDetail.getVitB1());
                            purchaseWithDetailsElementExt.setVitB2(menuDetail.getVitB2());
                            purchaseWithDetailsElementExt.setVitPp(menuDetail.getVitPp());
                            purchaseWithDetailsElementExt.setVitC(menuDetail.getVitC());
                            purchaseWithDetailsElementExt.setVitA(menuDetail.getVitA());
                            purchaseWithDetailsElementExt.setVitE(menuDetail.getVitE());
                            purchaseWithDetailsElementExt.setMinCa(menuDetail.getMinCa());
                            purchaseWithDetailsElementExt.setMinP(menuDetail.getMinP());
                            purchaseWithDetailsElementExt.setMinMg(menuDetail.getMinMg());
                            purchaseWithDetailsElementExt.setMinFe(menuDetail.getMinFe());
                            purchaseWithDetailsElementExt.setProtein(menuDetail.getProtein());
                            purchaseWithDetailsElementExt.setFat(menuDetail.getFat());
                            purchaseWithDetailsElementExt.setCarbohydrates(menuDetail.getCarbohydrates());
                        }
                    }

                    purchaseWithDetailsExt.getE().add(purchaseWithDetailsElementExt);
                }

                purchaseListWithDetailsExt.getP().add(purchaseWithDetailsExt);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        result.purchaseListWithDetailsExt = purchaseListWithDetailsExt;

        return result;
    }

    private Date getLastPaymentRegistryDate(Long idOfOrg, Map<Long, Date> map) {
        if (!map.containsKey(idOfOrg))
            map.put(idOfOrg, OrgRepository.getInstance().getLastProcessSectionsDate(idOfOrg, SectionType.PAYMENT_REGISTRY));
        return map.get(idOfOrg);
    }

    private List<OrderDetail> findDetailsByOrder(Order order, List<OrderDetail> details) {
        List<OrderDetail> list = new ArrayList<OrderDetail>();
        for (OrderDetail detail : details) {
            if (detail.getIdOfOrder().equals(order.getCompositeIdOfOrder().getIdOfOrder())
                    && detail.getCompositeIdOfOrderDetail().getIdOfOrg().equals(order.getCompositeIdOfOrder().getIdOfOrg())) {
                list.add(detail);
            }
        }
        return list;
    }

    private MenuDetail findMenuDetailByOrderDetail(Long idOfMenuFromSync,  List<MenuDetail> menuDetails) {
        for (MenuDetail detail : menuDetails) {
            if (idOfMenuFromSync.equals(detail.getIdOfMenuFromSync())) return detail;
        }
        return null;
    }

    private Set<Long> getOrgsByOrders(List<Order> ordersList) {
        Set set = new HashSet<Long>();
        for (Order order : ordersList) {
            set.add(order.getCompositeIdOfOrder().getIdOfOrg());
        }
        return set;
    }

    private Set<Long> getIdOfMenusByOrderDetails(List<OrderDetail> detailsList) {
        Set set = new HashSet<Long>();
        for (OrderDetail detail : detailsList) {
            set.add(detail.getIdOfMenuFromSync());
        }
        return set;
    }

    @Override
    public PaymentListResult getPaymentList(Long contractId, final Date startDate, final Date endDate) {

        Long clientContractId = contractId;
        String contractIdstr = String.valueOf(contractId);
        if (ContractIdGenerator.luhnTest(contractIdstr) || RuntimeContext.RegistryType.isSpb()) {
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
                processPaymentList(session, client, subBalanceNum, data, objectFactory, endDate, startDate);
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
                      processPaymentList(session, client, subBalanceNum, data, objectFactory, endDate, startDate);
                  }
              }, null);

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
                      processPaymentList(session, client, 1, data, objectFactory, endDate, startDate);
                  }
              }, null);

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    private void processPaymentList(Session session, Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
          Date endDate, Date startDate) throws Exception {
        List clientPaymentsList = DAOReadExternalsService.getInstance().getPaymentsList(client, subBalanceNum, endDate, startDate);
        PaymentList paymentList = objectFactory.createPaymentList();
        int nRecs = 0;
        for (Object o : clientPaymentsList) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            ClientPayment cp = (ClientPayment) o;
            Payment payment = new Payment();
            payment.setOrigin(PaymentTextUtils.buildTransferInfo(session, cp));
            payment.setSum(cp.getPaySum());
            payment.setTime(toXmlDateTime(cp.getCreateTime()));
            paymentList.getP().add(payment);
        }
        data.setPaymentList(paymentList);
    }

    @Override
    public MenuListResult getMenuList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
              .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                  public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                        Session session, Transaction transaction) throws Exception {
                      processMenuFirstDay(client.getOrg(), data, objectFactory, session, startDate, endDate);
                  }
              }, null);

        MenuListResult menuListResult = new MenuListResult();
        if (data.getMenuListExt() != null) {
            Collections.sort(data.getMenuListExt().getM());
        }
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    @Override
    public MenuListResult getMenuFirstDay(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processMenuFirstDay(client.getOrg(), data, objectFactory, session, startDate, endDate);
            }
        });

        MenuListResult menuListResult = new MenuListResult();
        if (data.getMenuListExt() != null) {
            Collections.sort(data.getMenuListExt().getM());
        }
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    private void processMenuFirstDay(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {

        List<Menu> menuByOneDayList = getMenuByOneDay(session, startDate, org);

        if (menuByOneDayList != null) {
            if (menuByOneDayList.size() > 1) {
                List<Menu> menuList = new ArrayList<Menu>();
                for (Menu menuItem : menuByOneDayList) {
                    if (menuItem.getMenuDetails().isEmpty() || menuItem.getMenuDetails().size() < 30) {
                        menuList.add(menuItem);
                    }
                }
                if (menuList.get(0).getMenuDetails().isEmpty() || menuList.get(0).getMenuDetails().size() < 30) {
                    processMenuByMaxIdOfMenu(session, startDate, endDate, objectFactory, org, data);
                } else {
                    processMenuList(org, data, objectFactory, session, startDate, endDate);
                }
            } else {
                processMenuList(org, data, objectFactory, session, startDate, endDate);
            }
        } else {
            processMenuByMaxIdOfMenu(session, startDate, endDate, objectFactory, org, data);
        }
    }

    private List<Menu> getMenuByOneDay(Session session, Date startDate, Org org) {
        Date endDate = CalendarUtils.addOneDay(startDate);

        Criteria menuByDayCriteria = session.createCriteria(Menu.class);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuByDayCriteria.add(Restrictions.eq("org", org));
        menuByDayCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuByDayCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        menuByDayCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));

        return (List<Menu>) menuByDayCriteria.list();
    }

    private void processMenuByMaxIdOfMenu(Session session, Date startDate, Date endDate, ObjectFactory objectFactory,
            Org org, Data data) throws DatatypeConfigurationException {
        Criteria menuMaxIdCriteria = session.createCriteria(MenuDetail.class);
        menuMaxIdCriteria.createAlias("menu", "m", JoinType.LEFT_OUTER_JOIN);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuMaxIdCriteria.add(Restrictions.eq("m.org", org));
        menuMaxIdCriteria.add(Restrictions.eq("m.menuSource", Menu.ORG_MENU_SOURCE));
        menuMaxIdCriteria.add(Restrictions.lt("m.menuDate", fromCal.getTime()));
        menuMaxIdCriteria.add(Restrictions.like("menuPath", "%уфет%"));
        menuMaxIdCriteria.setProjection(Projections.max("m.idOfMenu"));

        Long menuMaxId = (Long) menuMaxIdCriteria.uniqueResult();

        List menus = new ArrayList();

        if (menuMaxId != null) {
        Criteria menuCriteria = session.createCriteria(Menu.class);
        menuCriteria.add(Restrictions.eq("org", org));
        menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuCriteria.add(Restrictions.eq("idOfMenu", menuMaxId));

        Menu menu = (Menu) menuCriteria.uniqueResult();
        menu.setMenuDate(startDate);


        menus.add(menu);

        startDate = CalendarUtils.addOneDay(startDate);
        }
        Criteria menuByDayCriteria = session.createCriteria(Menu.class);
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuByDayCriteria.add(Restrictions.eq("org", org));
        menuByDayCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuByDayCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        menuByDayCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));

        List menusRemaining = menuByDayCriteria.list();
        menus.addAll(menusRemaining);
        generateMenuDetail(objectFactory, menus, session, data);
    }

    private void generateMenuDetail(ObjectFactory objectFactory, List menus, Session session, Data data)
            throws DatatypeConfigurationException {

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
            menuDetailCriteria.add(Restrictions.eq("availableNow", 1));
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
                menuItemExt.setVitB2(menuDetail.getVitB2());
                menuItemExt.setVitPp(menuDetail.getVitPp());
                menuItemExt.setVitC(menuDetail.getVitC());
                menuItemExt.setVitA(menuDetail.getVitA());
                menuItemExt.setVitE(menuDetail.getVitE());
                menuItemExt.setMinCa(menuDetail.getMinCa());
                menuItemExt.setMinP(menuDetail.getMinP());
                menuItemExt.setMinMg(menuDetail.getMinMg());
                menuItemExt.setMinFe(menuDetail.getMinFe());
                menuItemExt.setOutput(menuDetail.getMenuDetailOutput());
                menuItemExt.setAvailableNow(menuDetail.getAvailableNow());
                menuItemExt.setProtein(menuDetail.getProtein());
                menuItemExt.setCarbohydrates(menuDetail.getCarbohydrates());
                menuItemExt.setFat(menuDetail.getFat());
                menuDateItemExt.getE().add(menuItemExt);
            }

            menuListExt.getM().add(menuDateItemExt);
        }
        data.setMenuListExt(menuListExt);
    }

    @Override
    public MenuListWithComplexesResult getMenuListWithComplexes(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(null);
        ObjectFactory objectFactory = new ObjectFactory();
        return processMenuListWithComplexes(contractId, startDate, endDate, objectFactory);
    }

    private MenuListWithComplexesResult processMenuListWithComplexes(Long contractId, Date startDate, Date endDate, ObjectFactory objectFactory) {
        Session session = null;
        Transaction transaction = null;
        MenuListWithComplexesResult result = new MenuListWithComplexesResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = findClientByContractId(session, contractId, result);

            if (client == null) {
                return result;
            }

            Org org = client.getOrg();

            DAOService daoService = DAOService.getInstance();

            Criteria criteria = session.createCriteria(ComplexInfo.class);
            criteria.add(Restrictions.eq("org", org));
            criteria.add(Restrictions.gt("menuDate", startDate));
            criteria.add(Restrictions.lt("menuDate", endDate));

            List<ComplexInfo> complexInfoList = criteria.list();

            List<MenuWithComplexesExt> list = new ArrayList<MenuWithComplexesExt>();
            for (ComplexInfo ci : complexInfoList) {
                List<MenuItemExt> menuItemExtList = getMenuItemsExt(objectFactory, ci.getIdOfComplexInfo());
                MenuWithComplexesExt menuWithComplexesExt = new MenuWithComplexesExt(ci);
                menuWithComplexesExt.setMenuItemExtList(menuItemExtList);
                list.add(menuWithComplexesExt);
            }
            result.getMenuWithComplexesList().setList(list);

        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        return result;
    }

    @Override
    public MenuListResult getMenuListByOrg(@WebParam(name = "orgId") Long orgId, final Date startDate,
          final Date endDate) {
        authenticateRequest(null);

        Data data = new OrgRequest().process(orgId, new Processor() {
            public void process(Org org, Data data, ObjectFactory objectFactory, Session session,
                  Transaction transaction) throws Exception {
                processMenuFirstDay(org, data, objectFactory, session, startDate, endDate);
            }
        });

        MenuListResult menuListResult = new MenuListResult();
        if (data.getMenuListExt() != null) {
            Collections.sort(data.getMenuListExt().getM());
        }
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

    private void processMenuFirstDayWithProhibitions(Client client, Data data, ObjectFactory objectFactory,
            Session session, Date startDate, Date endDate) throws DatatypeConfigurationException {
        List<Menu> menuByOneDay = getMenuByOneDay(session, startDate, client.getOrg());

        if (menuByOneDay != null) {
            if (menuByOneDay.size() > 1) {
                List<Menu> menuList = new ArrayList<Menu>();
                for (Menu menuItem : menuByOneDay) {
                    if (menuItem.getMenuDetails().isEmpty() || menuItem.getMenuDetails().size() < 30) {
                        menuList.add(menuItem);
                    }
                }
                if (menuList.get(0).getMenuDetails().isEmpty() || menuList.get(0).getMenuDetails().size() < 30) {
                    processMenuByMaxIdOfMenuWithProhibitions(client, data, objectFactory, session, startDate, endDate);
                } else {
                    processMenuListWithProhibitions(client, data, objectFactory, session, startDate, endDate);
                }
            } else {
                processMenuListWithProhibitions(client, data, objectFactory, session, startDate, endDate);
            }
        } else {
            processMenuByMaxIdOfMenuWithProhibitions(client, data, objectFactory, session, startDate, endDate);
        }
    }

    private void processMenuByMaxIdOfMenuWithProhibitions(Client client, Data data, ObjectFactory objectFactory,
            Session session, Date startDate, Date endDate) throws DatatypeConfigurationException {
        Criteria menuMaxIdCriteria = session.createCriteria(MenuDetail.class);
        menuMaxIdCriteria.createAlias("menu", "m", JoinType.LEFT_OUTER_JOIN);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuMaxIdCriteria.add(Restrictions.eq("m.org", client.getOrg()));
        menuMaxIdCriteria.add(Restrictions.eq("m.menuSource", Menu.ORG_MENU_SOURCE));
        menuMaxIdCriteria.add(Restrictions.lt("m.menuDate", fromCal.getTime()));
        menuMaxIdCriteria.add(Restrictions.like("menuPath", "%уфет%"));
        menuMaxIdCriteria.setProjection(Projections.max("m.idOfMenu"));

        Long menuMaxId = (Long) menuMaxIdCriteria.uniqueResult();

        List menus = new ArrayList();

        if (menuMaxId != null) {
            Criteria menuCriteria = session.createCriteria(Menu.class);
            menuCriteria.add(Restrictions.eq("org", client.getOrg()));
            menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
            menuCriteria.add(Restrictions.eq("idOfMenu", menuMaxId));

            Menu menu = (Menu) menuCriteria.uniqueResult();
            menu.setMenuDate(startDate);


            menus.add(menu);

            startDate = CalendarUtils.addOneDay(startDate);
        }
        Criteria menuByDayCriteria = session.createCriteria(Menu.class);
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuByDayCriteria.add(Restrictions.eq("org", client.getOrg()));
        menuByDayCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuByDayCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        menuByDayCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));

        List menusRemaining = menuByDayCriteria.list();
        menus.addAll(menusRemaining);
        generateMenuDetailWithProhibitions(session, client, objectFactory, menus, data);
    }

    private void generateMenuDetailWithProhibitions(Session session, Client client, ObjectFactory objectFactory, List menus, Data data)
            throws DatatypeConfigurationException {
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
            //   menuDetailCriteria.add(Restrictions.sqlRestriction("{alias}.menupath !~ '^\\[\\d*\\]'"));
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
                menuItemExt.setOutput(menuDetail.getMenuDetailOutput());
                menuItemExt.setAvailableNow(menuDetail.getAvailableNow());
                menuItemExt.setProtein(menuDetail.getProtein());
                menuItemExt.setCarbohydrates(menuDetail.getCarbohydrates());
                menuItemExt.setFat(menuDetail.getFat());

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
            //menuDetailCriteria.add(Restrictions.sqlRestriction("{alias}.menupath !~ '^\\[\\d*\\]'"));
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
                menuItemExt.setOutput(menuDetail.getMenuDetailOutput());
                menuItemExt.setAvailableNow(menuDetail.getAvailableNow());
                menuItemExt.setProtein(menuDetail.getProtein());
                menuItemExt.setCarbohydrates(menuDetail.getCarbohydrates());
                menuItemExt.setFat(menuDetail.getFat());

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
            menuDetailCriteria.add(Restrictions.eq("availableNow", 1));
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
                menuItemExt.setVitB2(menuDetail.getVitB2());
                menuItemExt.setVitPp(menuDetail.getVitPp());
                menuItemExt.setVitC(menuDetail.getVitC());
                menuItemExt.setVitA(menuDetail.getVitA());
                menuItemExt.setVitE(menuDetail.getVitE());
                menuItemExt.setMinCa(menuDetail.getMinCa());
                menuItemExt.setMinP(menuDetail.getMinP());
                menuItemExt.setMinMg(menuDetail.getMinMg());
                menuItemExt.setMinFe(menuDetail.getMinFe());
                menuItemExt.setOutput(menuDetail.getMenuDetailOutput());
                menuItemExt.setAvailableNow(menuDetail.getAvailableNow());
                menuItemExt.setProtein(menuDetail.getProtein());
                menuItemExt.setCarbohydrates(menuDetail.getCarbohydrates());
                menuItemExt.setFat(menuDetail.getFat());
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
              }, null);

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
    public EnterEventListResult getNEnterEventList(@WebParam(name = "orgId") long orgId,@WebParam(name = "startDate") final Date startDate,
            @WebParam(name = "N") final int n) {
        Data data = null;
        EnterEventListResult enterEventListResult = new EnterEventListResult();
        try {
            data = processNEnterEventList( orgId,startDate, n);
            enterEventListResult.enterEventList = data.getEnterEventList();
            enterEventListResult.resultCode = data.getResultCode();
            enterEventListResult.description = data.getDescription();
        } catch (Exception e) {
            e.printStackTrace();
            enterEventListResult.resultCode = RC_INTERNAL_ERROR;
            enterEventListResult.description = e.getMessage().toString();
        }
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
              }, null);

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
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            EnterEventWithRepItem enterEventWithRepItem = objectFactory.createEnterEventWithRepItem();
            enterEventWithRepItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventWithRepItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventWithRepItem.setEnterName(enterEvent.getEnterName());
            enterEventWithRepItem.setDirection(enterEvent.getPassDirection());
            enterEventWithRepItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            enterEventWithRepItem.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance().getLastProcessSectionsDate(enterEvent.getOrg().getIdOfOrg(),
                    SectionType.ENTER_EVENTS)));

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
            final Long checkerId = enterEvent.getChildPassCheckerId();
            if (checkerId != null) {
                Client checker = DAOUtils.findClient(session, checkerId);
                enterEventWithRepItem.setChildPassCheckerContractId(checker.getContractId());
                enterEventWithRepItem.setChildPassChecker(checker.getPerson().getFullName());
            }
            enterEventWithRepList.getE().add(enterEventWithRepItem);
        }
        data.setEnterEventWithRepList(enterEventWithRepList);
    }

    private void processEnterEventList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate, boolean byGuardian) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        /*Запрос на получение данных из таблицы EnterEvents*/
        Criteria enterEventCriteria = session.createCriteria(EnterEvent.class);
        enterEventCriteria.add(byGuardian ? Restrictions.eq("guardianId", client.getIdOfClient())
                : Restrictions.eq("client", client));
        enterEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        enterEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        enterEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));
        /* -- Запрос на получение данных из таблицы EnterEvents -- */

        /*Запрос на получение данных из таблицы EnterEventsManual*/
        Criteria manualEventCriteria = session.createCriteria(EnterEventManual.class);
        manualEventCriteria.add(Restrictions.eq("idOfClient", client.getIdOfClient()));
        manualEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        manualEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        manualEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));
        List<EnterEventManual> manualEvents = manualEventCriteria.list();
        /* -- Запрос на получение данных из таблицы EnterEventsManual -- */

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        List<EnterEvent> enterEvents = enterEventCriteria.list();
        EnterEventList enterEventList = objectFactory.createEnterEventList();
        int nRecs = 0;

        Map<Long, Date> lastUpdatePrecessSectionDate = new HashMap<Long, Date>();
        Long localIdOfOrg;
        Date localDate;
        Map<Long, String> guardianSan = new HashMap<Long, String>();
        Long localGuardianId;
        String localSan;
        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            localIdOfOrg = enterEvent.getOrg().getIdOfOrg();
            localDate = lastUpdatePrecessSectionDate.get(localIdOfOrg);
            if (localDate == null) {
                lastUpdatePrecessSectionDate.put(localIdOfOrg,
                        OrgRepository.getInstance().getLastProcessSectionsDate(localIdOfOrg, SectionType.ENTER_EVENTS));
            }
            localGuardianId = enterEvent.getGuardianId();
            if (localGuardianId != null) {
                localSan = guardianSan.get(localGuardianId);
                if (localSan == null) {
                    guardianSan.put(localGuardianId, DAOUtils.extractSanFromClient(session, localGuardianId));
                }
            }
        }

        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            EnterEventItem enterEventItem = objectFactory.createEnterEventItem();
            enterEventItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setEnterName(enterEvent.getEnterName());
            enterEventItem.setDirection(enterEvent.getPassDirection());
            enterEventItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            enterEventItem.setLastUpdateDate(toXmlDateTime(lastUpdatePrecessSectionDate.get(enterEvent.getOrg().getIdOfOrg())));
            final Long guardianId = enterEvent.getGuardianId();
            if (guardianId != null) {
                enterEventItem.setGuardianSan(guardianSan.get(guardianId));
            }
            enterEventList.getE().add(enterEventItem);
        }

        for (EnterEventManual manualEvent : manualEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            EnterEventItem enterEventItem = objectFactory.createEnterEventItem();
            enterEventItem.setDateTime(toXmlDateTime(manualEvent.getEvtDateTime()));
            calendar.setTime(manualEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setEnterName(manualEvent.getEnterName());
            enterEventItem.setDirection(EnterEvent.CHECKED_BY_TEACHER_EXT);
            enterEventItem.setTemporaryCard(0);
            enterEventItem.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance().getLastProcessSectionsDate(manualEvent.getIdOfOrg(),
                    SectionType.ENTER_EVENTS)));
            enterEventList.getE().add(enterEventItem);
        }
        data.setEnterEventList(enterEventList);
    }

    private Data processNEnterEventList(long orgId,Date date, int n) throws Exception {

        Data data = new Data();
        data.setResultCode(RC_OK);
        data.setDescription(RC_OK_DESC);

        List<EnterEvent> lastNEnterEvent = EnterEventsRepository.getInstance().findLastNEnterEvent(orgId, date, n);

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        EnterEventList enterEventList =new EnterEventList();
        for (EnterEvent enterEvent : lastNEnterEvent) {
            EnterEventItem enterEventItem = new EnterEventItem();
            enterEventItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setEnterName(enterEvent.getEnterName());
            enterEventItem.setDirection(enterEvent.getPassDirection());
            enterEventItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            enterEventItem.setPassWithGuardian(enterEvent.getGuardianId());
            enterEventItem.setLastUpdateDate(toXmlDateTime(
                    OrgRepository.getInstance().getLastProcessSectionsDate(enterEvent.getOrg().getIdOfOrg(),
                    SectionType.ENTER_EVENTS)));
            final Long guardianId = enterEvent.getGuardianId();
            if (guardianId != null) {
                enterEventItem.setGuardianSan(ClientDao.getInstance().extractSanFromClient(guardianId));
            }
            enterEventList.getE().add(enterEventItem);

            if (enterEvent.getClient()!= null){
                enterEventItem.setIdOfClient(enterEvent.getClient().getIdOfClient());
            }
            enterEventItem.setIdOfCard(enterEvent.getIdOfCard());
            enterEventItem.setTurnstileAddr(enterEvent.getTurnstileAddr());
            enterEventItem.setVisitorFullName(enterEvent.getVisitorFullName());
        }
        data.setEnterEventList(enterEventList);

        return data;
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

    public ClientsWithResultCode getClientsByGuardMobile(String mobile, Session session) {

        ClientsWithResultCode data = new ClientsWithResultCode();
        try {
            Map<Long, ClientCreatedFromType> idOfClients = extractIDFromGuardByGuardMobile(Client.checkAndConvertMobile(mobile), session);
            if (idOfClients.isEmpty()) {
                data.resultCode = RC_CLIENT_NOT_FOUND;
                data.description = "Клиент не найден";
            } else {
                Map<Client, ClientCreatedFromType> map = new HashMap<Client, ClientCreatedFromType>();
                for (Map.Entry<Long, ClientCreatedFromType> entry : idOfClients.entrySet()) {
                    map.put(DAOUtils.findClient(session, entry.getKey()), entry.getValue());
                    //data.setClients(findListOfClientsByListOfIds(idOfClients, session));
                }
                data.setClients(map);
                data.resultCode = RC_OK;
                data.description = "OK";
            }
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.toString();
        }
        return data;
    }

    public Map<Long, ClientCreatedFromType> extractIDFromGuardByGuardMobile(String guardMobile, Session session) {
        Map<Long, ClientCreatedFromType> result = new HashMap<Long, ClientCreatedFromType>();
        String query = "select client.idOfClient from cf_clients client where client.phone=:guardMobile or client.mobile=:guardMobile"; //все клиенты с номером телефона
        Query q = session.createSQLQuery(query);
        q.setParameter("guardMobile", guardMobile);
        List<BigInteger> clients = q.list();

        if (clients != null && !clients.isEmpty()){
            for(BigInteger id : clients){
                Long londId = id.longValue();
                Query q2 = session.createQuery("select cg from ClientGuardian cg " +
                        "where cg.idOfGuardian = :idOfGuardian and cg.deletedState = false");  //все дети текущего клиента
                q2.setParameter("idOfGuardian", londId);
                List<ClientGuardian> list = q2.list();
                if (list != null && list.size() > 0) {
                    for (ClientGuardian cg : list) {
                        if (!cg.isDisabled()) {
                            result.put(cg.getIdOfChildren(), cg.getCreatedFrom());
                        }
                    }
                } else {
                    result.put(londId, ClientCreatedFromType.DEFAULT);
                }
            }
        }

        return result;
    }

    public List<Client> findListOfClientsByListOfIds(List<Long> idsOfClient, Session session) throws Exception {
        try {
            Query query = session.createQuery("from Client c where c.idOfClient in (:list)");
            query.setParameterList("list", idsOfClient);
            return query.list();
        } catch(Exception e) {
            return null;
        }
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
    public Long getContractIdByCardNo(@WebParam(name = "cardId") String cardId, @WebParam(name = "mode") int mode) {
        authenticateRequest(null);

        long lCardId = Long.parseLong(cardId);
        Long contractId = null;
        try {
            if (mode == 0) {
                contractId = getContractIdByCardNoInternal_OLDWAY(lCardId);
            }
            if (mode == 1) {
                contractId = getContractIdByCardNoInternal_NEWWAY(lCardId);
            }
        } catch (Exception e) {
            logger.error("ClientRoomController failed", e);
        }
        return contractId;
    }

    private Long getContractIdByCardNoInternal_OLDWAY(long cardId) throws Exception {
        Long contractId = DAOService.getInstance().getContractIdByCardNo(cardId);
        if (contractId == null) {
            int days = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_TEMP_CARD_VALID_DAYS);
            contractId = DAOService.getInstance().getContractIdByTempCardNoAndCheckValidDate(cardId, days);
        }
        return contractId;
    }

    private Long getContractIdByCardNoInternal_NEWWAY(long cardId) throws Exception {
        Long contractId = null;
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Card card = DAOUtils.findCardByCardNo(session, cardId);
            if (card.getState() == Card.ACTIVE_STATE) {
                contractId = card.getClient().getContractId();
            }
        }
        finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return contractId;
    }

    @Override
    public ClientSummaryExt[] getSummaryByGuardSan(String guardSan) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(null, handler);
        Date date = new Date(System.currentTimeMillis());
        //authenticateRequest(null);

        ClientsData cd = getClientsByGuardSan(guardSan);
        LinkedList<ClientSummaryExt> clientSummaries = new LinkedList<ClientSummaryExt>();
        if (cd != null && cd.clientList != null) {
            for (ClientItem ci : cd.clientList.getClients()) {
                ClientSummaryResult cs = getSummary(ci.getContractId());
                if (cs.clientSummary != null) {
                    clientSummaries.add(cs.clientSummary);
                    Long idOfClient = DAOService.getInstance().getClientByContractId(cs.clientSummary.getContractId()).getIdOfClient();
                    handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date, handler.getData().getSsoId(),
                            idOfClient, handler.getData().getOperationType());
                }
            }
        }
        return clientSummaries.toArray(new ClientSummaryExt[clientSummaries.size()]);
    }

    @Override
    public ClientSummaryExtListResult getSummaryByGuardMobile(String guardMobile) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(null, handler);
        Date date = new Date(System.currentTimeMillis());

        Session session = null;
        try {
            LinkedList<ClientSummaryExt> clientSummaries = new LinkedList<ClientSummaryExt>();
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            ClientsWithResultCode cd = getClientsByGuardMobile(guardMobile, session);

            if (cd != null && cd.getClients() != null) {
                for (Map.Entry<Client, ClientCreatedFromType> entry : cd.getClients().entrySet()) {
                    Data dataProcess = new ClientRequest().process(entry.getKey(), session, new Processor() {
                        public void process(Client client, Data dataProcess, ObjectFactory objectFactory,
                                Session session) throws Exception {
                            processSummary(client, dataProcess, objectFactory, session);
                        }
                    });
                    ClientSummaryResult cs = new ClientSummaryResult();
                    if (!entry.getValue().equals(ClientCreatedFromType.DEFAULT)) {
                        dataProcess.getClientSummaryExt().setGuardianCreatedWhere(entry.getValue().getValue());
                    }
                    cs.clientSummary = dataProcess.getClientSummaryExt();
                    cs.resultCode = dataProcess.getResultCode();
                    cs.description = dataProcess.getDescription();
                    if (cs.clientSummary != null) {
                        clientSummaries.add(cs.clientSummary);
                        handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date, handler.getData().getSsoId(),
                                entry.getKey().getIdOfClient(), handler.getData().getOperationType());
                    }
                }
            }

            ClientSummaryExtListResult clientSummaryExtListResult = new ClientSummaryExtListResult();
            clientSummaryExtListResult.clientSummary = clientSummaries;
            clientSummaryExtListResult.resultCode = cd.resultCode;
            clientSummaryExtListResult.description = cd.description;

            return clientSummaryExtListResult;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public ClientRepresentativesResult getClientRepresentatives(String contractId) {
        Long contractIdLong = Long.valueOf(contractId);
        authenticateRequest(contractIdLong);

        Data data = new ClientRequest().process(contractIdLong, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processClientRepresentativeList(client, data, objectFactory, session);
            }
        });

        ClientRepresentativesResult clientRepresentativesResult = new ClientRepresentativesResult();
        clientRepresentativesResult.clientRepresentativesList = data.getClientRepresentativesList();
        clientRepresentativesResult.resultCode = RC_OK;
        clientRepresentativesResult.description = RC_OK_DESC;

       return clientRepresentativesResult;
    }

    private void processClientRepresentativeList(Client client, Data data, ObjectFactory objectFactory, Session session) {
        try {
            Criteria criteria  = session.createCriteria(ClientGuardian.class);
            criteria.add(Restrictions.eq("idOfChildren", client.getIdOfClient()));
            criteria.add(Restrictions.eq("disabled", false));
            criteria.add(Restrictions.eq("deletedState", false));
            List guardiansResults = criteria.list();

            ClientRepresentativesList clientRepresentativesList = new ClientRepresentativesList();

            for (Object o: guardiansResults) {
                ClientGuardian clientGuardian = (ClientGuardian) o;
                Client cl = DAOUtils.findClient(session, clientGuardian.getIdOfGuardian());
                if ((cl != null) && (!cl.isDontShowToExternal())) {
                    ClientRepresentative clientRepresentative = objectFactory.creteClientRepresentative();
                    clientRepresentative.setId(cl.getContractId());
                    clientRepresentative.setName(cl.getPerson().getSurnameAndFirstLetters());
                    clientRepresentative.setEmail(cl.getEmail());
                    clientRepresentative.setMobile(cl.getMobile());
                    clientRepresentative.setNotifyviaemail(cl.isNotifyViaEmail());
                    clientRepresentative.setNotifyviapush(cl.isNotifyViaPUSH());
                    if (!clientGuardian.getCreatedFrom().equals(ClientCreatedFromType.DEFAULT)) {
                        clientRepresentative.setCreatedWhere(clientGuardian.getCreatedFrom().getValue());
                        clientRepresentative.setIdOfOrg(cl.getOrg().getIdOfOrg());
                        clientRepresentative.setOrgShortName(cl.getOrg().getShortName());
                    }

                    clientRepresentativesList.getRep().add(clientRepresentative);
                }
            }

            data.setClientRepresentativesList(clientRepresentativesList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result enableNotificationBySMS(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "state") boolean state) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        List<Long> contracts = getContracts(contractId, handler);
        if (!DAOService.getInstance().enableClientNotificationBySMS(contracts, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        } else {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            for (Long cId : contracts) {
                processor.updateNotificationParams(cId);
            }
        }
        return r;
    }

    private List<Long> getContracts(Long contractId, HTTPDataHandler handler) {
        List<Long> contracts = new ArrayList<Long>();
        contracts.add(contractId);
        try {
            String ssoid = handler.getData().getSsoId();
            List<Long> contractIds = DAOReadonlyService.getInstance().findContractsBySsoid(ssoid);
            for (Long cId : contractIds) {
                contracts.add(cId);
            }
        } catch (Exception ignore) {}
        return contracts;
    }

    @Override
    public Result enableNotificationByPUSH(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "state") boolean state) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        List<Long> contracts = getContracts(contractId, handler);
        if (!DAOService.getInstance().enableClientNotificationByPUSH(contracts, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        } else {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            for (Long cId : contracts) {
                processor.updateNotificationParams(cId);
            }
        }
        return r;
    }

    @Override
    public Result enableNotificationByEmail(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "state") boolean state) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        List<Long> contracts = getContracts(contractId, handler);
        if (!DAOService.getInstance().enableClientNotificationByEmail(contracts, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        } else {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            for (Long cId : contracts) {
                processor.updateNotificationParams(cId);
            }
        }
        return r;
    }

    @Override
    public Result changeMobilePhone(@WebParam(name = "contractId") Long contractId,
          @WebParam(name = "mobilePhone") String mobilePhone, @WebParam(name = "dateConfirm") Date dateConfirm) {
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
        if (!DAOService.getInstance().setClientMobilePhone(contractId, mobilePhone, dateConfirm)) {
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
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);
        Date date = new Date(System.currentTimeMillis());
        //authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);
        if (limit < 0) {
            r = new Result(RC_INVALID_DATA, "Лимит не может быть меньше нуля");
            return r;
        }
        if (!DAOService.getInstance().setClientExpenditureLimit(contractId, limit)) {
            r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
        } else {
            Long idOfClient = DAOService.getInstance().getClientByContractId(contractId).getIdOfClient();
            handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date, handler.getData().getSsoId(),
                    idOfClient, handler.getData().getOperationType());
        }
        return r;
    }

    @Override
    public Result changeThresholdBalanceNotify(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "threshold") long threshold) {
        authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);
        if (threshold < 0) {
            r = new Result(RC_INVALID_DATA, "Значение порога не может быть меньше нуля");
            return r;
        }
        if (!DAOService.getInstance().setClientBalanceToNotify(contractId, threshold)) {
            r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
        }
        return r;
    }

    @Override
    public PublicationListResult getPublicationListSimple(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "searchCondition") String searchCondition, @WebParam(name = "limit") int limit,
            @WebParam(name = "offset") int offset) {

        authenticateRequest(contractId);

        final String fSearchCondition = searchCondition;
        final int fLimit = limit;
        final int fOffset = offset;
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processPublicationListSimple(client, data, objectFactory, session, fSearchCondition, fLimit, fOffset);
            }
        });

        PublicationListResult pubListResult = new PublicationListResult();
        pubListResult.publicationList = data.getPublicationItemList();
        pubListResult.resultCode = data.getResultCode();
        pubListResult.description = data.getDescription();
        pubListResult.amountForCondition = data.getAmountForCondition();
        return pubListResult;
    }

    private void processPublicationListSimple(Client client, Data data, ObjectFactory objectFactory, Session session,
            String searchCondition, int limit, int offset) throws DatatypeConfigurationException {

        if (searchCondition.isEmpty()) {
            data.setPublicationItemList(new PublicationItemList());
            data.setAmountForCondition(0);
            return;
        }
        String str_condition = " and (upper(pub.Author) like :condition_like or upper(pub.Title) like :condition_like or upper(pub.Title2) like :condition_like " +
                "or pub.PublicationDate = :condition_eq " +
                "or upper(pub.Publisher) like :condition_like or pub.ISBN like :condition_isbn) ";

        Long org = client.getOrg().getIdOfOrg();

        StringBuilder bquery = new StringBuilder();
        bquery.append(QUERY_PUBLICATION_LIST);

        SQLQuery query = session.createSQLQuery(bquery.toString().replaceAll("CONDITION", str_condition));
        query.setParameter("org", org);
        query.setParameter("condition_like", "%" + searchCondition.toUpperCase() + "%");
        query.setParameter("condition_eq", searchCondition);
        query.setParameter("condition_isbn", searchCondition.replaceAll("-", "") + "%");
        query.setParameter("limit", limit);
        query.setParameter("offset", offset);

        data.setPublicationItemList(getPublicationListItem(objectFactory, query));

        bquery.setLength(0);
        bquery.append(QUERY_PUBLICATION_LIST_COUNT);
        bquery.append(str_condition);
        bquery.append(QUERY_PUBLICATION_LIST_COUNT_TAIL);
        query = session.createSQLQuery(bquery.toString());
        query.setParameter("org", org);
        query.setParameter("condition_like", "%" + searchCondition.toUpperCase() + "%");
        query.setParameter("condition_eq", searchCondition);
        query.setParameter("condition_isbn", searchCondition.replaceAll("-", "") + "%");
        data.setAmountForCondition(((BigInteger)query.uniqueResult()).intValue());
    }

    @Override
    public PublicationListResult getPublicationListAdvanced(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "author") String author, @WebParam(name = "title") String title,
            @WebParam(name = "title2") String title2, @WebParam(name = "publicationDate") String publicationDate,
            @WebParam(name = "publisher") String publisher, @WebParam(name = "isbn") String isbn,
            @WebParam(name="limit") int limit, @WebParam(name="offset") int offset) {

        authenticateRequest(contractId);

        final String fAuthor = author;
        final String fTitle = title;
        final String fTitle2 = title2;
        final String fPublicationDate = publicationDate;
        final String fPublisher = publisher;
        final String fISBN = isbn;
        final int fLimit = limit;
        final int fOffset = offset;
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processPublicationListAdvanced(client, data, objectFactory, session, fAuthor, fTitle, fTitle2,
                        fPublicationDate, fPublisher, fISBN, fLimit, fOffset);
            }
        });

        PublicationListResult pubListResult = new PublicationListResult();
        pubListResult.publicationList = data.getPublicationItemList();
        pubListResult.resultCode = data.getResultCode();
        pubListResult.description = data.getDescription();
        pubListResult.amountForCondition = data.getAmountForCondition();
        return pubListResult;
    }

    private void processPublicationListAdvanced(Client client, Data data, ObjectFactory objectFactory, Session session,
            String author, String title, String title2, String publicationDate, String publisher, String isbn,
            int limit, int offset) throws DatatypeConfigurationException {

        if (author.isEmpty() && title.isEmpty() && title2.isEmpty() && publicationDate.isEmpty() && publisher.isEmpty() && isbn.isEmpty()) {
            data.setPublicationItemList(new PublicationItemList());
            data.setAmountForCondition(0);
            return;
        }
        Long org = client.getOrg().getIdOfOrg();

        StringBuilder bquery = new StringBuilder();
        bquery.append(QUERY_PUBLICATION_LIST);
        String str_condition = getConditionsForPublicationListAdvanced(author, title, title2, publicationDate, publisher, isbn);

        SQLQuery query = session.createSQLQuery(bquery.toString().replaceAll("CONDITION", str_condition));

        setUserParametersForPublicationList(query, author, title, title2, publicationDate, publisher, isbn);

        query.setParameter("org", org);
        query.setParameter("limit", limit);
        query.setParameter("offset", offset);

        data.setPublicationItemList(getPublicationListItem(objectFactory, query));

        bquery.setLength(0);
        bquery.append(QUERY_PUBLICATION_LIST_COUNT);
        bquery.append(str_condition);
        bquery.append(QUERY_PUBLICATION_LIST_COUNT_TAIL);

        query = session.createSQLQuery(bquery.toString());
        query.setParameter("org", org);
        setUserParametersForPublicationList(query, author, title, title2, publicationDate, publisher, isbn);
        data.setAmountForCondition(((BigInteger)query.uniqueResult()).intValue());
    }

    private String getConditionsForPublicationListAdvanced(String author, String title, String title2,
            String publicationDate, String publisher, String isbn) {
        StringBuilder builder = new StringBuilder();
        if (author != null && !author.isEmpty()) {
            builder.append(" and upper(pub.Author) like :author ");
        }
        if (title != null && !title.isEmpty()) {
            builder.append(" and upper(pub.Title) like :title ");
        }
        if (title2 != null && !title2.isEmpty()) {
            builder.append(" and upper(pub.Title2) like :title2 ");
        }
        if (publicationDate != null && !publicationDate.isEmpty()) {
            builder.append(" and pub.PublicationDate = :publicationDate ");
        }
        if (publisher != null && !publisher.isEmpty()) {
            builder.append(" and upper(pub.Publisher) like :publisher ");
        }
        if (isbn != null && !isbn.isEmpty()) {
            builder.append(" and pub.ISBN like :isbn ");
        }
        return builder.toString();
    }

    private void setUserParametersForPublicationList(SQLQuery query, String author, String title, String title2,
            String publicationDate, String publisher, String isbn) {
        if (author != null && !author.isEmpty()) {
            query.setParameter("author", "%" + author.toUpperCase() + "%");
        }
        if (title != null && !title.isEmpty()) {
            query.setParameter("title", "%" + title.toUpperCase() + "%");
        }
        if (title2 != null && !title2.isEmpty()) {
            query.setParameter("title2", "%" + title2.toUpperCase() + "%");
        }
        if (publicationDate != null && !publicationDate.isEmpty()) {
            query.setParameter("publicationDate", publicationDate);
        }
        if (publisher != null && !publisher.isEmpty()) {
            query.setParameter("publisher", "%" + publisher.toUpperCase() + "%");
        }
        if (isbn != null && !isbn.isEmpty()) {
            query.setParameter("isbn", isbn.replaceAll("-", "") + "%");
        }
    }

    private PublicationItemList getPublicationListItem(ObjectFactory objectFactory, SQLQuery query) {
        List list = query.list();

        PublicationItemList puList = objectFactory.createPublicationItemList();
        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            Object[] objs = (Object[])iterator.next();

            PublicationInstancesItem pu = new PublicationInstancesItem();

            pu.setInstancesAmount(((BigInteger)objs[6]).intValue());
            pu.setInstancesAvailable(((BigInteger)objs[7]).intValue());
            pu.setOrgHolder(objs[9].toString());
            pu.setOrgHolderId(((BigInteger)objs[8]).longValue());
            PublicationItem pi = new PublicationItem();
            pi.setAuthor(objs[1].toString());
            pi.setPublisher(objs[5].toString());
            pi.setTitle(objs[2].toString());
            pi.setTitle2(objs[3].toString());
            pi.setPublicationDate(objs[4].toString());
            pi.setPublicationId(((BigInteger)objs[0]).longValue());
            pu.setPublication(pi);

            puList.getC().add(pu);
        }
        return puList;
    }

    @Override
    public OrderPublicationResult orderPublication(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "publicationId") Long publicationId, @WebParam(name = "orgHolderId") Long orgHolderId) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        OrderPublicationResult result = new OrderPublicationResult();

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

                Publication publication = findPublicationByPublicationId(session, publicationId);
                if (publication == null) {
                    result.resultCode = RC_PUBLICATION_NOT_AVAILABLE;
                    result.description = RC_PUBLICATION_NOT_AVAILABLE_DESC;
                    return result;
                }

                Org orgHolder = DAOUtils.findOrg(session, orgHolderId);
                if (orgHolder == null) {
                    result.resultCode = RC_ORG_HOLDER_NOT_FOUND;
                    result.description = RC_ORG_HOLDER_NOT_FOUND_DESC;
                    return result;
                }

                Criteria orderCriteria = session.createCriteria(OrderPublication.class);
                orderCriteria.add(Restrictions.eq("client", client));
                orderCriteria.add(Restrictions.eq("publication", publication));
                orderCriteria.add(Restrictions.eq("deletedState", false));
                orderCriteria.add(Restrictions.eq("orgOwner", orgHolderId));

                if (!orderCriteria.list().isEmpty()) {
                    result.resultCode = RC_ORDER_PUBLICATION_ALREADY_EXISTS;
                    result.description = RC_ORDER_PUBLICATION_ALREADY_EXISTS_DESC;
                    return result;
                }

                final String ORDER_PUBLICATION = "OrderPublication";
                final String bquery = "select count(ins.IdOfInstance) " +
                        "from cf_instances ins inner join cf_issuable iss on ins.IdOfInstance = iss.IdOfInstance " +
                        "where ins.OrgOwner = :org and ins.IdOfPublication = :pub and not exists " +
                        "(select IdOfCirculation from cf_circulations cir where cir.IdOfIssuable = iss.IdOfIssuable and cir.RealRefundDate is null) ";
                SQLQuery query = session.createSQLQuery(bquery.toString());
                query.setParameter("org", orgHolderId);
                query.setParameter("pub", publicationId);
                Long insCount = ((BigInteger)query.uniqueResult()).longValue();
                if (insCount > 0) {
                    Long maxVersion = DOVersionRepository.updateClassVersion(ORDER_PUBLICATION, session);
                    OrderPublication orderPublication = new OrderPublication();
                    orderPublication.setGlobalVersion(maxVersion);
                    orderPublication.setGlobalVersionOnCreate(maxVersion);
                    orderPublication.setCreatedDate(new Date());
                    orderPublication.setDeletedState(false);
                    orderPublication.setIdOfClient(client.getIdOfClient());
                    orderPublication.setOrgOwner(orgHolderId);
                    orderPublication.setSendAll(SendToAssociatedOrgs.DontSend);

                    orderPublication.setPublication(publication);
                    orderPublication.setClient(client);
                    session.persist(orderPublication);
                    result.id = orderPublication.getGlobalId();
                    result.resultCode = RC_OK;
                    result.description = RC_OK_DESC;
                }
                else {
                    result.resultCode = RC_PUBLICATION_NOT_AVAILABLE;
                    result.description = RC_PUBLICATION_NOT_AVAILABLE_DESC;
                }
                transaction.commit();
                transaction = null;
            }
            finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    private Publication findPublicationByPublicationId(Session session, long publicationId) {
        Criteria publicationCriteria = session.createCriteria(Publication.class);
        publicationCriteria.add(Restrictions.eq("globalId", publicationId));
        List<Publication> resultList = (List<Publication>) publicationCriteria.list();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public OrderPublicationListResult getOrderPublicationList(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processOrderPublicationList(client, data, objectFactory, session);
            }
        });

        OrderPublicationListResult orderListResult = new OrderPublicationListResult();
        orderListResult.orderPublicationList = data.getOrderPublicationItemList();
        orderListResult.resultCode = data.getResultCode();
        orderListResult.description = data.getDescription();
        return orderListResult;
    }

    private void processOrderPublicationList(Client client, Data data, ObjectFactory objectFactory, Session session)
            throws DatatypeConfigurationException {
        Criteria orderCriteria = session.createCriteria(OrderPublication.class);
        orderCriteria.add(Restrictions.eq("client", client));
        orderCriteria.add(Restrictions.eq("deletedState", false));
        orderCriteria.addOrder(org.hibernate.criterion.Order.asc("createdDate"));

        List<OrderPublication> orderList = orderCriteria.list();

        HashMap<Long, String> friendlyOrgNames = getFriendlyOrgNames(client);

        OrderPublicationItemList orList = objectFactory.createOrderPublicationItemList();
        for (OrderPublication c : orderList) {
            OrderPublicationItem oi = new OrderPublicationItem();
            oi.setOrderDate(toXmlDateTime(c.getCreatedDate()));
            oi.setOrderId(c.getGlobalId());
            oi.setOrderStatus(c.getStatus());
            oi.setOrgHolder(StringUtils.defaultString(friendlyOrgNames.get(c.getOrgOwner())));
            Publication p = c.getPublication();
            if (p != null) {
                PublicationItem pi = new PublicationItem();
                pi.setAuthor(p.getAuthor());
                pi.setPublisher(p.getPublisher());
                pi.setTitle(p.getTitle());
                pi.setTitle2(p.getTitle2());
                pi.setPublicationDate(p.getPublicationdate());
                pi.setPublicationId(p.getGlobalId());
                oi.setPublication(pi);
            }
            orList.getC().add(oi);
        }
        data.setOrderPublicationItemList(orList);
    }

    private HashMap<Long, String> getFriendlyOrgNames(Client client) {
        Set<Org> frienlyOrgs = client.getOrg().getFriendlyOrg();
        HashMap<Long, String> frienlyOrgNames = new HashMap<Long, String>();
        for(Org org : frienlyOrgs) {
            frienlyOrgNames.put(org.getIdOfOrg(), org.getShortName());
        }
        return frienlyOrgNames;
    }

    @Override
    public OrderPublicationDeleteResult deleteOrderPublication(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "orderId") Long orderId) {
        authenticateRequest(contractId);
        OrderPublicationDeleteResult result = new OrderPublicationDeleteResult();
        Session session = null;
        Transaction transaction = null;
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
                Criteria orderCriteria = session.createCriteria(OrderPublication.class);
                orderCriteria.add(Restrictions.eq("globalId", orderId));
                OrderPublication order = (OrderPublication) orderCriteria.uniqueResult();
                if (order == null) {
                    result.resultCode = RC_ORDER_PUBLICATION_NOT_FOUND;
                    result.description = RC_ORDER_PUBLICATION_NOT_FOUND_DESC;
                    return result;
                }
                else {
                    if (order.getStatus() != null && !order.getStatus().isEmpty())
                    {
                        result.resultCode = RC_ORDER_PUBLICATION_CANT_BE_DELETED;
                        result.description = RC_ORDER_PUBLICATION_CANT_BE_DELETED_DESC;
                        return result;
                    }
                    order.setDeletedState(true);
                    session.save(order);
                    transaction.commit();
                    transaction = null;
                }
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public ClassRegisterEventListByGUIDResult putClassRegisterEventListByGUID(ClassRegisterEventListByGUID registerEventList) {
        authenticateRequest(null);

        return сlassRegisterEventListByGUID(registerEventList);

    }

    private ClassRegisterEventListByGUIDResult сlassRegisterEventListByGUID(ClassRegisterEventListByGUID registerEventList) {
        ClassRegisterEventListByGUIDResult result = new ClassRegisterEventListByGUIDResult();
        ClassRegisterEventListByGUIDResultItem result_list = new ClassRegisterEventListByGUIDResultItem();
        result_list.classRegisterEventListByGUIDResultItem = new ArrayList<ClassRegisterEventByGUIDItem>();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            //удаляем события за предыдущие дни
            Date redtime = new Date(System.currentTimeMillis());
            redtime = CalendarUtils.calculateTodayStart(new GregorianCalendar(), redtime);
            if (!eeManualCleared.contains(redtime)) {
                Query query = persistenceSession.createQuery("delete from EnterEventManual where evtDateTime <= :evtDateTime");
                query.setParameter("evtDateTime", redtime);
                Integer cnt = query.executeUpdate();
                eeManualCleared.add(redtime);
            }

            for(ClassRegisterEventByGUID event : registerEventList.registerEvent) {
                ClassRegisterEventByGUIDItem it = processClassRegisterEvent(persistenceSession, event);
                result_list.classRegisterEventListByGUIDResultItem.add(it);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            result.classRegisterEventListByGUIDResult = result_list;

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceSession.close();
        }
        catch (Exception e) {
            logger.error("Internal error in putClassRegisterEventListByGUID", e);
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return result;
    }

    private ClassRegisterEventByGUIDItem processClassRegisterEvent(Session persistenceSession, ClassRegisterEventByGUID event) {
        ClassRegisterEventByGUIDItem item = new ClassRegisterEventByGUIDItem();
        item.guid = event.guid;

        try {
            Criteria criteria = persistenceSession.createCriteria(Client.class);
            criteria.add(Restrictions.eq("clientGUID", event.guid));
            List<Client> clientList = criteria.list();
            persistenceSession.clear();
            if (clientList == null || clientList.size() == 0) {
                logger.warn(String.format("Client with guid=%s not found to save manual enter event", event.guid));
                throw new IllegalArgumentException(String.format("Клиент с guid=%s не найден", event.guid));
            }
            Client client = clientList.get(0);
            List<String> pList = new ArrayList<String>();
            pList.add(event.guid);
            Boolean doGenerateEvent = true;
            try {
                EnterEventStatusListResult res = processEnterEventStatusList(pList);
                //doGenerateEvent = !processEnterEventStatusList(pList).enterEventStatusList.getC().get(0).getInside();
                doGenerateEvent = !res.enterEventStatusList.getC().get(0).getInside();
                if (!doGenerateEvent) {
                    Date lastEventDate = res.enterEventStatusList.getC().get(0).getLastEnterEventDateTime().toGregorianCalendar().getTime();
                    Date dayOfLastEventDate = CalendarUtils.calculateTodayStart(new GregorianCalendar(), lastEventDate);
                    Date dayOfNewEventDate = CalendarUtils.calculateTodayStart(new GregorianCalendar(), event.evtDateTime);
                    if (!dayOfLastEventDate.equals(dayOfNewEventDate) && dayOfLastEventDate.before(dayOfNewEventDate)) {
                        doGenerateEvent = true; //Если последнее событие по клиенту было не в этот день, а ренее, то новое событие присутствия надо генерировать
                    }
                }
            }
            catch (Exception doGenerateIsTrue) {}

            if (doGenerateEvent) {
                EnterEventManual newEvent = new EnterEventManual();
                newEvent.setEnterName(event.evtName);
                newEvent.setEvtDateTime(event.evtDateTime);
                newEvent.setIdOfClient(client.getIdOfClient());
                newEvent.setIdOfOrg(client.getOrg().getIdOfOrg());
                persistenceSession.save(newEvent);
            }

            item.state = 0;

        }
        catch (IllegalArgumentException e) {
            item.state = 100; //Клиент не найден
        }
        catch (Exception e) {
            logger.error("Internal error in putClassRegisterEventListByGUID", e);
            item.state = 101; //Internal Error
        }
        return item;
    }

    @Override
    public EnterEventStatusListResult getEnterEventStatusListByGUID(List<String> guids) {
        authenticateRequest(null);

        return processEnterEventStatusList(guids);

    }

    private EnterEventStatusListResult processEnterEventStatusList(List<String> guids) {
        EnterEventStatusListResult result = new EnterEventStatusListResult();
        EnterEventStatusItemList result_list = new EnterEventStatusItemList();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            String where_in = "'" + StringUtils.join(guids, "', '") + "'";
            String query_str = String.format("select cli.clientGuid, ee.passDirection, to_timestamp(ee.evtdatetime / 1000) from cf_enterevents ee join cf_clients cli " +
                    "on (ee.idofclient = cli.idofclient and (ee.evtdatetime = (select evtdatetime from cf_enterevents " +
                    "where idofclient = cli.idofclient order by evtdatetime desc limit 1))) " +
                    "where cli.clientguid in (%s)", where_in);
            org.hibernate.Query q = persistenceSession.createSQLQuery(query_str);
            List resultList = q.list();
            for (Object entry : resultList) {
                Object record[] = (Object[]) entry;
                String guid            = (String) record[0];
                Integer passDirection  = (Integer) record[1];
                Date evtDate           = record[2] == null ? null : new Date(((Timestamp) record[2]).getTime());
                Boolean inside = false;
                switch(passDirection) {
                    case EnterEvent.ENTRY:
                    case EnterEvent.DETECTED_INSIDE:
                    case EnterEvent.RE_ENTRY:
                    case EnterEvent.CHECKED_BY_TEACHER_EXT:
                    case EnterEvent.CHECKED_BY_TEACHER_INT:
                        inside = true;
                        break;
                }
                /*if (inside) {
                    Date dayOfLastEventDate = CalendarUtils.calculateTodayStart(new GregorianCalendar(), evtDate);
                    Date todayDate = CalendarUtils.calculateTodayStart(new GregorianCalendar(), new Date(System.currentTimeMillis()));
                    if (!dayOfLastEventDate.equals(todayDate) && dayOfLastEventDate.before(todayDate)) {
                        inside = false; //Если последнее событие по клиенту было не сегодня, то считаем, что его нет в здании
                    }
                }*/
                EnterEventStatusItem result_item = new EnterEventStatusItem();
                result_item.setGuid(guid);
                result_item.setInside(inside);
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(evtDate);
                XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                result_item.setLastEnterEventDateTime(date2);
                result_item.setLastEnterEventDirection(passDirection);
                result_list.getC().add(result_item);
            }

            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            result.enterEventStatusList = result_list;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = e.toString();
            result.enterEventStatusList = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public Result setGuardianshipDisabled(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardMobile") String guardMobile,
            @WebParam(name = "value") Boolean value) {
        authenticateRequest(contractId);
        return processSetGuardianship(contractId, guardMobile, value);
    }

    private Result processSetGuardianship(Long contractId, String guardMobile, Boolean value) {
        Result result = new Result();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);

            List<ClientGuardianItem> guardians = ClientManager.loadGuardiansByClient(session, client.getIdOfClient());
            boolean guardianWithMobileFound = false;
            for (ClientGuardianItem item : guardians) {
                Client guardian = (Client)session.get(Client.class, item.getIdOfClient());
                if (guardian != null && guardian.getMobile().equals(guardMobile)) {
                    guardianWithMobileFound = true;
                    Criteria criteria = session.createCriteria(ClientGuardian.class);
                    criteria.add(Restrictions.eq("idOfChildren", client.getIdOfClient()));
                    criteria.add(Restrictions.eq("idOfGuardian", item.getIdOfClient()));
                    ClientGuardian cg = (ClientGuardian)criteria.uniqueResult();
                    cg.setDisabled(value);
                    cg.setVersion(getClientGuardiansResultVersion(session));
                    session.persist(cg);
                }
            }
            if (value) {
                if (client.getMobile().equals(guardMobile)) {
                    client.setMobile("");
                    logger.info("class : ClientRoomControllerWS, method : processSetGuardianship line : 4790, idOfClient : " + client.getIdOfClient() + " mobile : " + client.getMobile());
                    session.persist(client);
                }
            } else {
                if (!guardianWithMobileFound) {
                    if (client.getMobile() == null || client.getMobile().isEmpty()) {
                        client.setMobile(guardMobile);
                        logger.info("class : ClientRoomControllerWS, method : processSetGuardianship line : 4797, idOfClient : " + client.getIdOfClient() + " mobile : " + client.getMobile());
                        session.persist(client);
                    } else {
                        throw new IllegalArgumentException(String.format("Невозможно активировать опекунскую связь между клиентом " +
                                "с л/с %s и представителем с телефоном %s", contractId, guardMobile));
                    }
                }
            }

            session.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        }
        catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = e.getMessage();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }

        return result;
    }

    private Long getClientGuardiansResultVersion(Session session) {
        Long version;
        try {
            version = ClientManager.generateNewClientGuardianVersion(session);
        } catch (Exception ex) {
            logger.error("Failed get max client guardians version, ", ex);
            version = 0L;
        }
        return version;
    }

    @Override
    public Result clearMobileByContractId(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "phone") String phone) {
        authenticateRequest(contractId);
        return processClearMobile(contractId, phone);
    }

    private Result processClearMobile(Long contractId, String phone) {
        Result result = new Result();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session session = null;
        Transaction persistenceTransaction = null;
        try {
            session = runtimeContext.createPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            if (contractId == null) {
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }
            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }
            String currentMobile = client.getMobile();

            if (phone.equals(currentMobile)) {
                client.setMobile("");
                logger.debug("class : ClientRoomControllerWS, method : processClearMobile line : 4860, idOfClient : " + client.getIdOfClient() + " mobile : " + client.getMobile());
                session.persist(client);
                logger.info(String.format("Очищен номер телефона %s у клиента с ContractId=%s", phone, client.getContractId()));
            }

            List<ClientGuardianItem> guardians = ClientManager.loadGuardiansByClient(session, client.getIdOfClient());
            for (ClientGuardianItem item : guardians) {
                Client guardian = DAOUtils.findClientByContractId(session, item.getContractId());
                if (phone.equals(guardian.getMobile())) {
                    guardian.setMobile("");
                    logger.debug("class : ClientRoomControllerWS, method : processClearMobile line : 4870, idOfClient : " + guardian.getIdOfClient() + " mobile : " + guardian.getMobile());
                    session.persist(guardian);
                    logger.info(String.format("Очищен номер телефона %s у клиента с ContractId=%s", phone, guardian.getContractId()));
                }
            }
            session.flush();
            persistenceTransaction.commit();
            session.close();
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = e.toString();
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        return result;
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
                pi.setPublicationId(p.getGlobalId());
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

            if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH_BY_PASSWORD) {
                if(client.hasEncryptedPasswordSHA1(token)){
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
                            new String[]{"linkingToken", linkingToken.getToken()}, new Date());
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
                        new String[]{"linkingToken", codes}, new Date());
            result.resultCode = RC_OK;
            result.description = "Код активации отправлен по SMS для " + clientList.size() + " л/с";
            return result;
        } catch (Exception e) {
            logger.error("Failed to send linking token", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    IntegraPartnerConfig.LinkConfig authenticateRequest(Long contractId, HTTPDataHandler handler) throws Error {
        /*if (RuntimeContext.getInstance().isTestMode()){
            return null;
        }*/
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
        //данные для логирования запроса
        if (handler != null) {
            handler.setData(jaxwsContext);
        }
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
            Client client = null;
            try {
                client = DAOReadExternalsService.getInstance().findClient(null, contractId);
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

    IntegraPartnerConfig.LinkConfig authenticateRequest(Long contractId) throws Error {
        return authenticateRequest(contractId, null);
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

        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        runtimeContext = RuntimeContext.getInstance();
        ClientPaymentOrderProcessor clientPaymentOrderProcessor = runtimeContext.getClientPaymentOrderProcessor();
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (orderStatus < ClientPaymentOrder.ORDER_STATUS_TRANSFER_ACCEPTED) {
                clientPaymentOrderProcessor.changePaymentOrderStatus(idOfClient, idOfClientPaymentOrder, orderStatus);
            } else {
                ClientPaymentOrder clientPaymentOrder = DAOUtils.getClientPaymentOrderReference(persistenceSession, idOfClientPaymentOrder);
                clientPaymentOrderProcessor.changePaymentOrderStatus(clientPaymentOrder.getContragent().getIdOfContragent(), idOfClientPaymentOrder, orderStatus,
                        clientPaymentOrder.getContragentSum(), idOfClientPaymentOrder.toString(), "changePaymentOrderStatus/".concat(idOfClientPaymentOrder.toString()));
            }
            persistenceSession.clear();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
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
            if (!daoService.setClientMobilePhone(contractId, mobilePhone, null)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //enableNotificationBySms
            List<Long> list = new ArrayList<Long>();
            list.add(contractId);
            if (!daoService.enableClientNotificationBySMS(list, smsNotificationState)) {
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
            for (ClientGuardianNotificationSetting.Predefined predef : ClientGuardianNotificationSetting.Predefined.values()) {
                if (predef.getValue().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
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

    @Override
    public ClientNotificationSettingsResult getClientGuardianNotificationSettings(@WebParam(name = "childContractId") Long childContractId,
            @WebParam(name = "guardianMobile") String guardianMobile) {
        authenticateRequest(childContractId);

        ClientNotificationSettingsResult res = new ClientNotificationSettingsResult(RC_OK, RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;

        Set<ClientNotificationSettingsItem> set = new HashSet<ClientNotificationSettingsItem>();
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            Long childId = DAOReadonlyService.getInstance().getClientIdByContract(childContractId);

            if (childId == null) {
                res.setResultCode(RC_CLIENT_NOT_FOUND);
                res.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                return res;
            }

            List<Long> clientGuardians = DAOReadonlyService.getInstance().findClientGuardiansByMobile(childId, Client.checkAndConvertMobile(guardianMobile));
            if (clientGuardians == null || clientGuardians.size() == 0) {
                return getClientNotificationSettings(childContractId);
                /*res.setResultCode(RC_CLIENT_GUARDIAN_NOT_FOUND);
                res.setDescription(RC_CLIENT_GUARDIAN_NOT_FOUND_DESC);
                return res;*/
            }

            boolean clientHasCustomNotificationSettings = false;
            for (Long id : clientGuardians) {
                ClientGuardian clientGuardian = (ClientGuardian)persistenceSession.get(ClientGuardian.class, id);
                for (ClientGuardianNotificationSetting setting : clientGuardian.getNotificationSettings()) {
                    if (setting.getNotifyType().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                        clientHasCustomNotificationSettings = true;
                        continue;
                    }
                    ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                    it.setTypeOfNotification(setting.getNotifyType());
                    it.setNameOfNotification(
                            ClientGuardianNotificationSetting.Predefined.parse(setting.getNotifyType()).getName());
                    set.add(it);
                }
                // если у клиента нет записи изменения настроек - выдаем настройки по умолчанию
                if (!clientHasCustomNotificationSettings) {
                    for (ClientGuardianNotificationSetting.Predefined predefined : ClientGuardianNotificationSetting.Predefined
                            .values()) {
                        if (predefined.isEnabledAtDefault()) {
                            ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                            it.setTypeOfNotification(predefined.getValue());
                            it.setNameOfNotification(predefined.getName());
                            set.add(it);
                        }
                    }
                }
            }
            List<ClientNotificationSettingsItem> list = new ArrayList<ClientNotificationSettingsItem>();
            for (ClientNotificationSettingsItem item : set) {
                list.add(item);
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

            List<Client> guardians = ClientManager.findGuardiansByClient(persistenceSession, client.getIdOfClient(), null);
            if (guardians.size() > 0) {
                retrieveNotificationsForClientGuardian(persistenceSession, list, client, guardians);
            } else {
                //----------------------------ниже старый код
                Set<ClientNotificationSettingsItem> set = new HashSet<ClientNotificationSettingsItem>();
                boolean clientHasCustomNotificationSettings = false;
                for (ClientNotificationSetting setting : client.getNotificationSettings()) {
                    if (setting.getNotifyType().equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                        clientHasCustomNotificationSettings = true;
                        continue;
                    }
                    ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                    it.setTypeOfNotification(setting.getNotifyType());
                    it.setNameOfNotification(ClientNotificationSetting.Predefined.parse(setting.getNotifyType()).getName());
                    set.add(it);
                }
                // если у клиента нет записи изменения настроек - выдаем настройки по умолчанию
                if (!clientHasCustomNotificationSettings) {
                    for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined.values()) {
                        if (predefined.isEnabledAtDefault()) {
                            ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                            it.setTypeOfNotification(predefined.getValue());
                            it.setNameOfNotification(predefined.getName());
                            set.add(it);
                        }
                    }
                }
                for (ClientNotificationSettingsItem item : set) {
                    list.add(item);
                }
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

    private void retrieveNotificationsForClientGuardian(Session session, List<ClientNotificationSettingsItem> list, Client client, List<Client> guardians) {
        for (ClientGuardianNotificationSetting.Predefined pd : ClientGuardianNotificationSetting.Predefined.values()) {
            if (pd.getValue().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            boolean found = false;
            for (Client guardian : guardians) {
                found = false;
                ClientGuardian clientGuardian = DAOReadonlyService.getInstance()
                        .findClientGuardianById(session, client.getIdOfClient(), guardian.getIdOfClient());
                if (clientGuardian == null) { continue; }
                for (ClientGuardianNotificationSetting setting : clientGuardian.getNotificationSettings()) {
                    if (pd.getValue().equals(setting.getNotifyType())) {
                        found = true;
                        break;
                    }
                }
                if (!found) break;
            }
            if (!found) {
                continue;
            } else {
                ClientNotificationSettingsItem item = new ClientNotificationSettingsItem();
                item.setNameOfNotification(pd.getName());
                item.setTypeOfNotification(pd.getValue());
                list.add(item);
            }
        }

    }

    @Override
    public ClientNotificationChangeResult setClientGuardianNotificationSettings(@WebParam(name = "childContractId") Long childContractId,
            @WebParam(name = "guardianMobile") String guardianMobile,
            @WebParam(name = "notificationType") List<Long> notificationTypes) {
        authenticateRequest(childContractId);

        ClientNotificationChangeResult res = new ClientNotificationChangeResult(RC_OK, RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long childId = DAOReadonlyService.getInstance().getClientIdByContract(childContractId);
            if (childId == null) {
                res.resultCode = RC_CLIENT_NOT_FOUND;
                res.description = RC_CLIENT_NOT_FOUND_DESC;
                return res;
            }

            List<Long> guardianIds = DAOReadonlyService.getInstance().findClientGuardiansByMobile(childId, Client.checkAndConvertMobile(guardianMobile));

            if (guardianIds == null || guardianIds.size() == 0) {
                //Если нет представителей, то работаем по старому алгоритму (плюс удаляем из переданного списка новые типы уведомелений)
                boolean order_types_removed = false;
                boolean old_order_type_exists = false;
                if (notificationTypes != null) {
                    for (Iterator<Long> iterator = notificationTypes.iterator(); iterator.hasNext(); ) {
                        Long type = iterator.next();
                        if (type.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_FREE.getValue())
                                || type.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_PAY.getValue())) {
                            iterator.remove();
                            order_types_removed = true;
                        }
                        if (type.equals(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_BAR.getValue())) {
                            old_order_type_exists = true;
                        }
                    }
                    if (!old_order_type_exists && order_types_removed) {
                        notificationTypes.add(ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_ORDERS_BAR.getValue());
                    }
                }
                return setClientNotificationSettings(childContractId, notificationTypes); //Если нет представителей, работаем по старому алгоритму
            }
            for (Long id : guardianIds) {
                ClientGuardian clientGuardian = (ClientGuardian) persistenceSession.get(ClientGuardian.class, id);
                processNotificationsForClientGuardian(notificationTypes, clientGuardian);
                persistenceSession.update(clientGuardian);
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

    private void processNotificationsForClientGuardian(List<Long> notificationTypes, ClientGuardian clientGuardian) throws Exception {
        boolean pdFound;
        List<NotificationSettingItem> notificationItems = new ArrayList<NotificationSettingItem>();
        for (ClientGuardianNotificationSetting.Predefined pd : ClientGuardianNotificationSetting.Predefined.values()) {
            if (pd.getValue().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            pdFound = false;
            for (Long notificationType : notificationTypes) {
                if (notificationType.equals(pd.getValue())) {
                    pdFound = true;
                    break;
                }
            }
            NotificationSettingItem item = new NotificationSettingItem(pd, pdFound);
            notificationItems.add(item);
        }

        ClientManager.attachNotifications(clientGuardian, notificationItems);
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
            List<Client> guardians = ClientManager.findGuardiansByClient(persistenceSession, client.getIdOfClient(), null);
            for (Client guardian : guardians) {
                ClientGuardian clientGuardian = DAOReadonlyService.getInstance()
                        .findClientGuardianById(persistenceSession, client.getIdOfClient(), guardian.getIdOfClient());
                if (clientGuardian == null) { continue; }
                processNotificationsForClientGuardian(notificationTypes, clientGuardian);
                persistenceSession.update(clientGuardian);
            }
            for (ClientNotificationSetting.Predefined pd : ClientNotificationSetting.Predefined.values()) {
                ClientNotificationSetting cns = new ClientNotificationSetting(client, pd.getValue());
                if ((notificationTypes!=null && notificationTypes.contains(pd.getValue())) || pd.getValue()
                      .equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                    client.getNotificationSettings().add(cns);
                } else {
                    client.getNotificationSettings().remove(cns);
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
    public ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return findComplexesWithSubFeeding(contractId, null, type);
    }

    @Override
    public ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "san") String san,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return findComplexesWithSubFeeding(null, san, type);
    }

    public ComplexInfoResult findComplexesWithSubFeeding(Long contractId, String san, Integer type) {
        Session session = null;
        Transaction transaction = null;
        ComplexInfoResult result = new ComplexInfoResult();
        try {
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                return result;
            }
            Org org = client.getOrg();

            boolean isParent = false;
            if (client.getClientGroup() != null) {
                final String groupName = client.getClientGroup().getGroupName();
                isParent = client.getIdOfClientGroup() >= ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES || ClientGroup.predefinedGroupNames().contains(groupName);
            }
            boolean vp = (type == null ? false : type.equals(SubscriptionFeedingType.VARIABLE_TYPE.ordinal()));
            /*boolean vp = false;
            if (ArrayUtils.contains(getVPOrgsList(), org.getIdOfOrg())) {
                vp = true;
            }*/
            List<ComplexInfo> complexInfoList = DAOReadExternalsService.getInstance().findComplexesWithSubFeeding(org, isParent, vp);
            List<ComplexInfoExt> list = new ArrayList<ComplexInfoExt>();
            result.getComplexInfoList().setList(list);
            ObjectFactory objectFactory = new ObjectFactory();
            for (ComplexInfo ci : complexInfoList) {
                List<MenuItemExt> menuItemExtList = getMenuItemsExt(objectFactory, ci.getIdOfComplexInfo());
                ComplexInfoExt complexInfoExt = new ComplexInfoExt(ci);
                complexInfoExt.setMenuItemExtList(menuItemExtList);
                list.add(complexInfoExt);
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

    private List<MenuItemExt> getMenuItemsExt (ObjectFactory objectFactory, Long idOfComplexInfo) {
        List<MenuItemExt> menuItemExtList = new ArrayList<MenuItemExt>();
        List<MenuDetail> menuDetails = DAOReadExternalsService.getInstance().getMenuDetailsByIdOfComplexInfo(idOfComplexInfo);
        for (MenuDetail menuDetail : menuDetails) {
            MenuItemExt menuItemExt = objectFactory.createMenuItemExt();
            menuItemExt.setGroup(menuDetail.getGroupName());
            menuItemExt.setName(menuDetail.getMenuDetailName());
            menuItemExt.setPrice(menuDetail.getPrice());
            menuItemExt.setCalories(menuDetail.getCalories());
            menuItemExt.setVitB1(menuDetail.getVitB1());
            menuItemExt.setVitB2(menuDetail.getVitB2());
            menuItemExt.setVitPp(menuDetail.getVitPp());
            menuItemExt.setVitC(menuDetail.getVitC());
            menuItemExt.setVitA(menuDetail.getVitA());
            menuItemExt.setVitE(menuDetail.getVitE());
            menuItemExt.setMinCa(menuDetail.getMinCa());
            menuItemExt.setMinP(menuDetail.getMinP());
            menuItemExt.setMinMg(menuDetail.getMinMg());
            menuItemExt.setMinFe(menuDetail.getMinFe());
            menuItemExt.setOutput(menuDetail.getMenuDetailOutput());
            menuItemExt.setAvailableNow(menuDetail.getAvailableNow());
            menuItemExt.setProtein(menuDetail.getProtein());
            menuItemExt.setCarbohydrates(menuDetail.getCarbohydrates());
            menuItemExt.setFat(menuDetail.getFat());

            menuItemExtList.add(menuItemExt);
        }
        return menuItemExtList;
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
                        "Отсутствуют настройки абонементного питания для организации " + clientOrg.getShortNameInfoService();
                return result;
            }
            if (list.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = "Организация имеет более одной настройки " + clientOrg.getShortNameInfoService();
                return result;
            }
            ECafeSettings settings = (ECafeSettings) list.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                    .getSplitSettingValue();
            SubscriptionFeedingSettingExt settingExt = new SubscriptionFeedingSettingExt();
            settingExt.setDayDeActivate(parser.getDayDeActivate());
            int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = 0;
            if (hoursForbidChange < 24) {
                dayForbidChange++;
            } else {
                dayForbidChange = (hoursForbidChange % 24 == 0 ? hoursForbidChange / 24 : hoursForbidChange / 24 + 1);
            }
            settingExt.setDayForbidChange(dayForbidChange);
            settingExt.setDayRequest(parser.getDayRequest());
            settingExt.setEnableFeeding(parser.isEnableFeeding());
            settingExt.setSixWorkWeek(parser.isSixWorkWeek());
            settingExt.setDaysToForbidChangeInPos(parser.getDaysToForbidChangeInPos());
            settingExt.setDayCreateVP(parser.getDayCreateVP());
            settingExt.setHoursForbidVP(parser.getHoursForbidVP());
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
                                client.getOrg().getShortNameInfoService(), idOfOrg);
                return result;
            }
            if (list.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String.format("Организация имеет более одной настройки %s (IdOfOrg = %s)",
                        client.getOrg().getShortNameInfoService(), idOfOrg);
                return result;
            }
            ECafeSettings settings = (ECafeSettings) list.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                    .getSplitSettingValue();
            SubscriptionFeedingSettingExt settingExt = new SubscriptionFeedingSettingExt();
            settingExt.setDayDeActivate(parser.getDayDeActivate());
            int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = 0;
            if (hoursForbidChange < 24) {
                dayForbidChange++;
            } else {
                dayForbidChange = (hoursForbidChange % 24 == 0 ? hoursForbidChange / 24 : hoursForbidChange / 24 + 1);
            }
            settingExt.setDayForbidChange(dayForbidChange);
            settingExt.setDayRequest(parser.getDayRequest());
            settingExt.setEnableFeeding(parser.isEnableFeeding());
            settingExt.setSixWorkWeek(parser.isSixWorkWeek());
            settingExt.setDaysToForbidChangeInPos(parser.getDaysToForbidChangeInPos());
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
          @WebParam(name = "currentDay") Date currentDay, @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return getCurrentSubscriptionFeeding(contractId, null, currentDay, type);
    }

    @Override
    public SubscriptionFeedingResult getCurrentSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "currentDay") Date currentDay, @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return getCurrentSubscriptionFeeding(null, san, currentDay, type);
    }

    private SubscriptionFeedingResult getCurrentSubscriptionFeeding(Long contractId, String san,  Date currentDay, Integer type) {
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
            SubscriptionFeeding sf = service.getCurrentSubscriptionFeedingByClientToDay(session, client, currentDay, type);
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

/*    //@Override
    public SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(
          @WebParam(name = "contractId") Long contractId, @WebParam(name = "startDate") Date startDate,
          @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(contractId);
        return getSubscriptionFeedingHistoryList(contractId, null, startDate, endDate);
    }*/

/*    //@Override
    public SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(null);
        return getSubscriptionFeedingHistoryList(null, san, startDate, endDate);
    }*/

/*    private SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(Long contractId, String san, Date startDate,
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
    }*/

    @Override
    public SubscriptionFeedingJournalResult getSubscriptionFeedingJournal(Long contractId, Date startDate, Date endDate) {
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingJournalResult result = new SubscriptionFeedingJournalResult();

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
            List<SubscriptionFeeding> subscriptionFeedings = subscriptionFeedingService.findSubscriptionFeedingByClient(client, startDate, endDate);
            for (SubscriptionFeeding subscriptionFeeding: subscriptionFeedings) {
                result.subscriptionFeedingJournalListExt.getS().add(new SubscriptionFeedingJournalExt(subscriptionFeeding));
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
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
          @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return getCycleDiagramHistoryList(contractId, null, startDate, endDate, type);
    }

    @Override
    public CycleDiagramList getCycleDiagramHistoryList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
          @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return getCycleDiagramHistoryList(null, san, startDate, endDate, type);
    }

    private CycleDiagramList getCycleDiagramHistoryList(Long contractId, String san, Date startDate, Date endDate, Integer type) {
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
            List<CycleDiagram> cycleDiagrams = service.findCycleDiagramsByClient(client, startDate, endDate, type);
            for (CycleDiagram cycleDiagram : cycleDiagrams) {
                result.cycleDiagramListExt.getC().add(new CycleDiagramOut(cycleDiagram));
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
    public CycleDiagramList getCycleDiagramList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return getCycleDiagramList(contractId, null, type);
    }

    @Override
    public CycleDiagramList getCycleDiagramList(@WebParam(name = "san") String san,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return getCycleDiagramList(null, san, type);
    }

    private CycleDiagramList getCycleDiagramList(Long contractId, String san, Integer type) {
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
            List<CycleDiagram> cycleDiagrams = service.findCycleDiagramsByClient(client, type);
            for (CycleDiagram cycleDiagram : cycleDiagrams) {
                result.cycleDiagramListExt.getC().add(new CycleDiagramOut(cycleDiagram));
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

/*    @Override
    public Result activateSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram) {
        authenticateRequest(contractId);
        return activateSubscriptionFeeding(contractId, null, cycleDiagram);
    }*/

    @Override
    public Result activateCurrentSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "dateActivateSubscription") Date dateActivateSubscription) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        DateFormat df = CalendarUtils.getDateFormatLocal();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                    .geteCafeSettingses(client.getOrg().getIdOfOrg(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                        .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                                client.getOrg().getShortNameInfoService(), client.getOrg().getIdOfOrg());
                return result;
            }
            Date dateActivateSubs = CalendarUtils.truncateToDayOfMonth(dateActivateSubscription);
            SubscriptionFeeding subscriptionFeeding = SubscriptionFeedingService
                    .getCurrentSubscriptionFeedingByClientToDay(session, client, dateActivateSubs, null);
            if (subscriptionFeeding == null) {
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
                return result;
            }
            if (subscriptionFeeding.getDateActivateSubscription() == null) {
                ECafeSettings cafeSettings = settings.get(0);
                SubscriberFeedingSettingSettingValue parser;
                parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
                Date dayForbid = subscriptionFeeding.getFirstDateCanChangeRegister(parser);
                if (dateActivateSubscription.getTime() < dayForbid.getTime()) {
                    result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                    result.description = "Неверная дата активация подписки (" + dateActivateSubscription.toString() + " < " + dayForbid.toString() + ")";
                    return result;
                }
                subscriptionFeeding.setDateActivateSubscription(dateActivateSubscription);
                session.save(subscriptionFeeding);
                transaction.commit();

                result.resultCode = RC_OK;
                result.description = String.format("Подписка успешно активирована, начнет действовать после " + df.format(dateActivateSubscription));
                return result;
            } else {
                result.resultCode = RC_SUBSCRIPTION_FEEDING_DUPLICATE;
                result.description = String.format("Подписка уже активирована (дата ее активации " + df.format(subscriptionFeeding.getDateActivateSubscription()) + ")");
                return result;
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

/*    @Override
    public Result activateSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram) {
        authenticateRequest(null);
        return activateSubscriptionFeeding(null, san, cycleDiagram);
    }*/

    /*private Result activateSubscriptionFeeding(Long contractId, String san, CycleDiagramExt cycleDiagram) {
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

            //Date dayForbid = GetFirstCanChangeSF(settings);

            final int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = (hoursForbidChange %24==0? hoursForbidChange /24: hoursForbidChange /24+1);
            Date dayForbid = CalendarUtils.addDays(currentDate, dayForbidChange);
            if(dayForbid.getHours()>=12){
                dayForbid = CalendarUtils.addOneDay(currentDate);
            }
            Date activateDate = cycleDiagram.getDateActivationDiagram();
            if(activateDate.getTime()<dayForbid.getTime()){
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Неверная дата активация подписки";
                return result;
            }

            SubscriptionFeeding sf = new SubscriptionFeeding();
            sf.setCreatedDate(new Date());
            sf.fill(subscriptionFeeding);
            sf.setDateActivateSubscription(activateDate);
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
    }*/

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
                  SubscriptionFeedingService.getCurrentSubscriptionFeedingByClientToDay(session, client, suspendDate, null);
            if (subscriptionFeeding.getFeedingType().equals(SubscriptionFeedingType.VARIABLE_TYPE)) {
                result.resultCode = RC_INVALID_OPERATION_VARIABLE_FEEDING;
                result.description = RC_INVALID_OPERATION_VARIABLE_FEEDING_DESC;
                return result;
            }

            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                  .geteCafeSettingses(subscriptionFeeding.getOrgOwner(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                            client.getOrg().getShortNameInfoService(), subscriptionFeeding.getOrgOwner());
                return result;
            }
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser;
            parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            Date dayForbid = subscriptionFeeding.getFirstDateCanChangeRegister(parser, new Date());
            if(suspendDate.getTime() < dayForbid.getTime()){
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Неверная дата приостановки подписки";
                return result;
            }
            subscriptionFeeding.setLastDatePauseSubscription(suspendDate);
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
                  SubscriptionFeedingService.getCurrentSubscriptionFeedingByClientToDay(session, client, activateDate, null);
            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                  .geteCafeSettingses(subscriptionFeeding.getOrgOwner(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                            client.getOrg().getShortNameInfoService(), client.getOrg().getIdOfOrg());
                return result;
            }
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser;
            parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            Date dayForbid = subscriptionFeeding.getFirstDateCanChangeRegister(parser, new Date());
            if(activateDate.getTime() < dayForbid.getTime()){
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Неправильная дата возобновления подписки";
                return result;
            }
            Date currentDate = new Date();
            if (subscriptionFeeding.getDateActivateSubscription() == null
                    && subscriptionFeeding.getLastDatePauseSubscription() == null
                    && subscriptionFeeding.getWasSuspended() == false) {

                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Ошибка возобновления подписки в статусе \"Создана\"";
                return result;
            }
            if (subscriptionFeeding.getDateActivateSubscription() != null
                    && subscriptionFeeding.getLastDatePauseSubscription() == null
                    && subscriptionFeeding.getWasSuspended() == false
                    && subscriptionFeeding.getDateActivateSubscription().getTime() > currentDate.getTime()) {
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Ошибка возобновления подписки в статусе \"Ожидает активации\"";
                return result;
            }
            if (subscriptionFeeding.getDateActivateSubscription() != null
                    && subscriptionFeeding.getLastDatePauseSubscription() == null
                    && subscriptionFeeding.getWasSuspended() == false
                    && subscriptionFeeding.getDateActivateSubscription().getTime() <= currentDate.getTime()) {
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Ошибка возобновления подписки в статусе \"Активна\"";
                return result;
            }
            SubscriptionFeeding sf = new SubscriptionFeeding();
            sf.setCreatedDate(new Date());
            sf.setLastUpdate(new Date());
            sf.fill(subscriptionFeeding);
            sf.setDateActivateSubscription(activateDate);
            sf.setLastDatePauseSubscription(null);
            sf.setWasSuspended(false);
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
                  SubscriptionFeedingService.getCurrentSubscriptionFeedingByClientToDay(session, client, new Date(), null);
            if(subscriptionFeeding==null){
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
                return result;
            }
            DAOService daoService = DAOService.getInstance();
            subscriptionFeeding.setWasSuspended(false);
            subscriptionFeeding.setLastDatePauseSubscription(null);
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
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram, @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return putCycleDiagram(contractId, null, cycleDiagram, type);
    }

    @Override
    public Result putCycleDiagram(@WebParam(name = "san") String san,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram, @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return putCycleDiagram(null, san, cycleDiagram, type);
    }

    private Result putCycleDiagram(Long contractId, String san, CycleDiagramExt cycleDiagram, Integer type) {
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
                                clientOrg.getShortNameInfoService(), idOfOrg);
                return result;
            }
            if (settingsList.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                        .format("Организация имеет более одной настройки %s (IdOfOrg = %s)", clientOrg.getShortNameInfoService(),
                                idOfOrg);
                return result;
            }
            ECafeSettings settings = (ECafeSettings) settingsList.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                    .getSplitSettingValue();
            Date dayForbid = SubscriptionFeeding.getFirstDateCanChangeRegister(parser, new Date());
            if (cycleDiagram.getDateActivationDiagram().getTime() < dayForbid.getTime()) {
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(CycleDiagram.class);
            criteria.add(Restrictions.eq("client", client));
            criteria.add(Restrictions.eq("dateActivationDiagram", cycleDiagram.getDateActivationDiagram()));
            List list = criteria.list();
            SubscriptionFeedingService sfService = SubscriptionFeedingService.getInstance();
            boolean vp = (type == null ? false : type.equals(SubscriptionFeedingType.VARIABLE_TYPE.ordinal()));
            /* boolean vp = false;
            if (ArrayUtils.contains(getVPOrgsList(), clientOrg.getIdOfOrg())) {
                vp = true;
            }*/
            if (vp) {
                boolean sixWorkWeek = parser.isSixWorkWeek();
                if (!allDaysWithData(sixWorkWeek, cycleDiagram)) {
                    result.resultCode = RC_ERROR_NOT_ALL_DAYS_FILLED_VARIABLE_FEEDING;
                    result.description = RC_ERROR_NOT_ALL_DAYS_FILLED_VARIABLE_FEEDING_DESC;
                    return result;
                }
                boolean allOk = true;
                String complexesByDayOfWeek;
                for (int i = 1; i < 8; i++) {
                    complexesByDayOfWeek = getCycleDiagramValueByDayOfWeek(i, cycleDiagram);
                    if (complexesByDayOfWeek == null) continue;
                    String[] complexesByDayArray = complexesByDayOfWeek.split("\\|"); // разделитель в виде верт. черты - несколько недель
                    for (String complexesByDay : complexesByDayArray) {
                        allOk = allowCreateCycleDiagramByComplexesInDay(complexesByDay, sfService, clientOrg,
                                cycleDiagram.getDateActivationDiagram()); //для каждой недели проверка на допустимое сочетание комплексов
                        if (!allOk)
                            break;
                    }
                    if (!allOk) break;
                }
                if (!allOk) {
                    result.resultCode = RC_ERROR_CREATE_VARIABLE_FEEDING;
                    result.description = RC_ERROR_CREATE_VARIABLE_FEEDING_DESC;
                    return result;
                }
            }
            List<ComplexInfo> availableComplexes = sfService.findComplexesWithSubFeeding(clientOrg, vp);
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
                diagram.setMonday(cycleDiagram.getMonday());
                diagram.setMondayPrice(sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                diagram.setTuesday(cycleDiagram.getTuesday());
                diagram.setTuesdayPrice(sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                diagram.setWednesday(cycleDiagram.getWednesday());
                diagram.setWednesdayPrice(sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                diagram.setThursday(cycleDiagram.getThursday());
                diagram.setThursdayPrice(sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                diagram.setFriday(cycleDiagram.getFriday());
                diagram.setFridayPrice(sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                diagram.setSaturday(cycleDiagram.getSaturday());
                diagram.setSaturdayPrice(sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                diagram.setSunday("");
                diagram.setSundayPrice("0");
                diagram.setStaff(null);
                diagram.setFeedingType(vp ? SubscriptionFeedingType.VARIABLE_TYPE : SubscriptionFeedingType.ABON_TYPE);
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
                    diagram.setMonday(cycleDiagram.getMonday());
                    diagram.setMondayPrice(sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                    diagram.setTuesday(cycleDiagram.getTuesday());
                    diagram.setTuesdayPrice(sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                    diagram.setWednesday(cycleDiagram.getWednesday());
                    diagram.setWednesdayPrice(sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                    diagram.setThursday(cycleDiagram.getThursday());
                    diagram.setThursdayPrice(sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                    diagram.setFriday(cycleDiagram.getFriday());
                    diagram.setFridayPrice(sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                    diagram.setSaturday(cycleDiagram.getSaturday());
                    diagram.setSaturdayPrice(sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                    diagram.setSunday("");
                    diagram.setSundayPrice("0");
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

    private boolean allDaysWithData(boolean sixWorkWeek, CycleDiagramExt cycleDiagram) {
        int countInMonday = countSubstring(cycleDiagram.getMonday(), '|');
        return !(StringUtils.isEmpty(cycleDiagram.getMonday()) || StringUtils.isEmpty(cycleDiagram.getTuesday())
                || StringUtils.isEmpty(cycleDiagram.getWednesday()) || StringUtils.isEmpty(cycleDiagram.getThursday())
                || StringUtils.isEmpty(cycleDiagram.getFriday()) || (StringUtils.isEmpty(cycleDiagram.getSaturday()) && sixWorkWeek))
                &&
                (countInMonday == countSubstring(cycleDiagram.getTuesday(), '|')
                && countInMonday == countSubstring(cycleDiagram.getWednesday(), '|')
                && countInMonday == countSubstring(cycleDiagram.getThursday(), '|')
                && countInMonday == countSubstring(cycleDiagram.getFriday(), '|')
                && (sixWorkWeek ? countInMonday == countSubstring(cycleDiagram.getSaturday(), '|') : true));
    }

    private int countSubstring(String source, char search) {
        int result = 0;
        try {
            for (char ch : source.toCharArray()) {
                if (search == ch)
                    result++;
            }
        } catch(Exception ignore) {
            return 0;
        }
        return result;
    }

    private String getCycleDiagramValueByDayOfWeek(int day, CycleDiagramExt cycleDiagram) {
        switch (day) {
            case 1: return cycleDiagram.getMonday();
            case 2: return cycleDiagram.getTuesday();
            case 3: return cycleDiagram.getWednesday();
            case 4: return cycleDiagram.getThursday();
            case 5: return cycleDiagram.getFriday();
            case 6: return cycleDiagram.getSaturday();
            case 7: return cycleDiagram.getSunday();
            default: return null;
        }
    }

    private boolean allowCreateCycleDiagramByComplexesInDay(String complexesByDay, SubscriptionFeedingService sfService,
            Org org, Date date) {
        List<Integer> complexIds = new ArrayList<Integer>();
        for (String s : complexesByDay.split(";")) {
            complexIds.add(Integer.parseInt(s));
        }
        return complexIds.size() == 1 || sfService.allowCreateByRootComplexes(org, complexIds, date);
    }

    @Override
    public MenuListWithProhibitionsResult getMenuListWithProhibitions(Long contractId, final Date startDate,
            final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processMenuFirstDayWithProhibitions(client, data, objectFactory, session, startDate, endDate);
            }
        });
        MenuListWithProhibitionsResult menuListWithProhibitionsResult = new MenuListWithProhibitionsResult();
        if (data.getMenuListExt() != null) {
            Collections.sort(data.getMenuListExt().getM());
        }
        menuListWithProhibitionsResult.menuList = data.getMenuListExt();
        menuListWithProhibitionsResult.resultCode = data.getResultCode();
        menuListWithProhibitionsResult.description = data.getDescription();
        return menuListWithProhibitionsResult;
    }

    //возврат id
    //ProhibitionResult
    @Override
    public ProhibitionsResult addProhibition(Long contractId, String filterText, Integer filterType) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);
        Date date = new Date(System.currentTimeMillis());
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
                handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date,
                        handler.getData().getSsoId(), client.getIdOfClient(), handler.getData().getOperationType());

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
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);
        Date date = new Date(System.currentTimeMillis());
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

                handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date, handler.getData().getSsoId(),
                        client.getIdOfClient(), handler.getData().getOperationType());

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

    @Override
    public OrgSummaryResult getOrgSummary(@WebParam(name = "orgId") Long orgId) {
        OrgSummaryResult result = new OrgSummaryResult();
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            Org org = DAOUtils.findOrg(session, orgId);
            if (org != null){
                result.orgSummary = new OrgSummary(org);
            }else {
                result.notFound();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.internalError();
        }
        return result;
    }

    @Override
    public VisitorsSummaryResult getVisitorsSummary() {
        return getVisitorsSummaryByDate(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public VisitorsSummaryResult getVisitorsSummaryByDate(Long datetime) {
        VisitorsSummaryResult result = new VisitorsSummaryResult();
        Session session = null;
        result.orgsList = new VisitorsSummaryList();

        EnterEventsService enterEventsService = (EnterEventsService) RuntimeContext.getAppContext()
                .getBean(EnterEventsService.class);
        List<DAOEnterEventSummaryModel> dataClients = new ArrayList<DAOEnterEventSummaryModel>();
        List<DAOEnterEventSummaryModel> dataOthers = new ArrayList<DAOEnterEventSummaryModel>();
        List<DAOEnterEventSummaryModel> dataVisitors = new ArrayList<DAOEnterEventSummaryModel>();
        try{
            dataClients = enterEventsService.getEnterEventsSummaryNotEmptyClient(datetime);
            dataOthers = enterEventsService.getEnterEventsSummaryEmptyClient(datetime);
            dataVisitors = enterEventsService.getEnterEventsSummaryVisitors(datetime);
        } catch (Exception i) {
            result.resultCode = ResultConst.CODE_INTERNAL_ERROR;
            result.description = ResultConst.DESCR_INTERNAL_ERROR;
        }

        Map<Long,VisitorsSummary> visitorsSummaryList = new HashMap<Long, VisitorsSummary>();

        parseVisitorsSummary(visitorsSummaryList,dataClients);
        parseVisitorsSummary(visitorsSummaryList,dataOthers);
        parseVisitorsSummary(visitorsSummaryList,dataVisitors);

        //visitorsSummaryList.addAll( parseVisitorsSummary(dataOthers) );

        result.orgsList.org = new LinkedList<VisitorsSummary>(visitorsSummaryList.values());
        result.orgsList.orgCount = visitorsSummaryList.values().size();
        if (result.orgsList.org.size() == 0 ){
            result.description = ResultConst.DESCR_NOT_FOUND;
            result.resultCode = ResultConst.CODE_NOT_FOUND;
        }
        return result;
    }

    private static Map<Long,VisitorsSummary> parseVisitorsSummary (Map<Long,VisitorsSummary> visitorsSummaryList,List<DAOEnterEventSummaryModel> data){
        VisitorsSummary visitorsSummary = null;
        for (DAOEnterEventSummaryModel model : data) {
            if( visitorsSummary == null || !model.getIdOfOrg().equals(visitorsSummary.id)){
                visitorsSummary = visitorsSummaryList.get(model.getIdOfOrg());
                if(visitorsSummary == null) {
                    visitorsSummary = new VisitorsSummary();
                    visitorsSummary.id = model.getIdOfOrg();
                    visitorsSummaryList.put(visitorsSummary.id,visitorsSummary);
                }
            }
            if(model.getIdOfClient() == null){
                if (model.getIdOfVisitor() != null ) {
                    if ((model.getPassDirection() != 1) && (model.getPassDirection() != 7)){
                            visitorsSummary.others3++;
                    }
                } else {
                    if (model.getEventCode() == 112) {
                        visitorsSummary.cardless++;
                    }
                    if((model.getPassDirection() == 1) || (model.getPassDirection() == 7)){
                        visitorsSummary.exitsCardless++;
                    }
                }
            }

            if (model.getIdOfClient() != null) {
                visitorsSummary.studentsTotal++;
                if ( (model.getPassDirection() != 1) && (model.getPassDirection() != 7) ) {
                    if (model.getIdOfClientGroup() != null) {
                        if (model.getIdOfClientGroup() < ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() ) {
                            visitorsSummary.studentsInside++;
                        } else if ((model.getIdOfClientGroup() >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue())
                                && (model.getIdOfClientGroup() < ClientGroup.Predefined.CLIENT_PARENTS.getValue())) {
                            visitorsSummary.employee++;
                        } else if(model.getIdOfClientGroup()>= ClientGroup.Predefined.CLIENT_PARENTS.getValue()){
                            visitorsSummary.others1++;
                        }
                    } else {
                        visitorsSummary.others2++;
                    }
                }
            }
        }
      // visitorsSummaryList.put(visitorsSummary.id,visitorsSummary);
        return visitorsSummaryList;
    }

    @Override
    public ClientGuidResult getClientGuidByContractId(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        ClientGuidResult result = new ClientGuidResult();
        try {
            DAOService daoService = DAOService.getInstance();
            Client client = daoService.getClientByContractId(contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            if (client.getClientGUID() == null) {
                result.resultCode = RC_CLIENT_GUID_NOT_FOUND;
                result.description = RC_CLIENT_GUID_NOT_FOUND_DESC;
                return result;
            }
            result.clientGUID = client.getClientGUID();
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
    public Result addGuardian(@WebParam(name = "firstName") String firstName,
            @WebParam(name = "secondName") String secondName, @WebParam(name = "surname") String surname,
            @WebParam(name = "mobile") String mobile, @WebParam(name = "gender") Integer gender,
            @WebParam(name = "childContractId") Long childContractId, @WebParam(name = "creatorMobile") String creatorMobile) {

        authenticateRequest(null);

        String mobilePhone = Client.checkAndConvertMobile(mobile);
        if (StringUtils.isEmpty(firstName) || StringUtils.isEmpty(surname) || StringUtils.isEmpty(mobilePhone)
                || gender == null || childContractId == null) {
            return new Result(RC_INVALID_DATA, "Не заполнены обязательные поля");
        }
        if (StringUtils.isEmpty(mobilePhone)) {
            return new Result(RC_INVALID_DATA, "Неверный формат мобильного телефона");
        }

        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
            String remark = String.format(COMMENT_MPGU_CREATE, dateString, creatorMobile);
            Client client = findClient(session, childContractId, null, result);
            if (client == null) {
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }

            Org org = client.getOrg();
            int count = 0;
            Client guardian = null;
            if (secondName == null) secondName = "";
            //List<Client> guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient());
            List<Client> exClients = DAOUtils.findClientsByFIO(session, org.getFriendlyOrg(), firstName, surname, secondName, mobilePhone);
            for (Client cl : exClients) {
                if (cl.getClientGroup() == null
                    || cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_DELETED.getValue())
                        || cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue())) {
                        continue;
                }
                /*if (guardians.contains(cl)) {
                    return new Result(RC_INVALID_DATA, "Клиент уже зарегистрирован");
                }*/
                count++;
                guardian = cl;
            }
            if (count > 1) {
                return new Result(RC_SEVERAL_CLIENTS_WERE_FOUND, RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
            }
            if (guardian == null) {
                guardian = ClientManager.createGuardianTransactionFree(session, firstName, secondName,
                        surname, mobile, remark, gender, org);
            }

            ClientGuardian clientGuardian = DAOUtils.findClientGuardian(session, client.getIdOfClient(), guardian.getIdOfClient());
            if (clientGuardian == null) {
                ClientManager.createClientGuardianInfoTransactionFree(session, guardian, null, false, client.getIdOfClient(), ClientCreatedFromType.MPGU);
            } else if (clientGuardian.getDeletedState() || clientGuardian.isDisabled()){
                Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                clientGuardian.restore(newGuardiansVersions);
                clientGuardian.setCreatedFrom(ClientCreatedFromType.MPGU);
                session.update(clientGuardian);
            } else {
                return new Result(RC_INVALID_DATA, "Клиент уже зарегистрирован");
            }

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    /*private void testClientsForGuardianMethods(List<Client> clients) throws IllegalArgumentException {
        Long idO = null;
        for (Client c : clients) {
            if (idO == null) idO = c.getOrg().getIdOfOrg();
            if (!idO.equals(c.getOrg().getIdOfOrg())) {
                throw new IllegalArgumentException("Л/с опекаемых должны принадлежать одной организации");
            }
        }
    }*/

    /*Пока выключаем метод, т.к. не используется
    @Override
    public Result changeGuardian(@WebParam(name = "contractId") Long contractId, @WebParam(name = "firstName") String firstName,
            @WebParam(name = "secondName") String secondName, @WebParam(name = "surname") String surname,
            @WebParam(name = "gender") Integer gender, @WebParam(name = "contracts") ListOfContracts contracts) {
        authenticateRequest(contractId);

        if (StringUtils.isEmpty(firstName) || StringUtils.isEmpty(surname) || contracts == null
                || gender == null || CollectionUtils.isEmpty(contracts.getContractIds())) {
            return new Result(RC_INVALID_DATA, "Не заполнены обязательные поля");
        }

        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();

                Client client = findClientByContractId(session, contractId, result);
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }

                String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
                String remarkChange = String.format(COMMENT_MPGU_CHANGE, dateString);

                List<Client> clients = DAOUtils.findClientsByListOfContractId(session, contracts.getContractIds()); //список клиентов-опекаемых из входного параметра
                testClientsForGuardianMethods(clients);
                List<Client> children = ClientManager.findChildsByClient(session, client.getIdOfClient(), true);

                boolean found;
                for (Client clPar : clients) { //clPar - клиенты-опекаемые с л/с из входного параметра
                    found = false;
                    for (Client clDB : children) {  //clDB - клиенты-опекаемых, уже существующие у представителя client
                        if (clPar.equals(clDB)) {
                            found = true;
                            ClientGuardian cg = DAOReadonlyService.getInstance().findClientGuardianByIdIncludeDisabled(session, clPar.getIdOfClient(), client.getIdOfClient(), true);
                            if (cg.getDeletedState() || cg.isDisabled()) {
                                Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                                cg.restore(newGuardiansVersions);
                                session.update(cg);
                            }
                            break;
                        }
                    }
                    if (!found) {
                        //создание связки
                        ClientManager.createClientGuardianInfoTransactionFree(session, client, null, false, clPar.getIdOfClient());
                        children.add(clPar);
                    }
                }
                for (Client clDB : children) {
                    found = false;
                    for (Client clPar : clients) {
                        if (clDB.equals(clPar)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ClientGuardian cg = DAOReadonlyService.getInstance().findClientGuardianById(session, clDB.getIdOfClient(), client.getIdOfClient());
                        if (cg != null) {
                            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                            cg.delete(newGuardiansVersions);
                            session.update(cg);
                        }
                    }
                }
                FieldProcessor.Config modifyConfig = new ClientManager.ClientFieldConfigForUpdate();
                modifyConfig.setValue(ClientManager.FieldId.SURNAME, surname);
                modifyConfig.setValue(ClientManager.FieldId.NAME, firstName);
                modifyConfig.setValue(ClientManager.FieldId.SECONDNAME, secondName);
                modifyConfig.setValue(ClientManager.FieldId.GENDER, gender);
                ClientManager.modifyClientTransactionFree((ClientManager.ClientFieldConfigForUpdate) modifyConfig,
                        client.getOrg(), remarkChange, client, session, true);

                transaction.commit();
                transaction = null;
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
            }  catch (IllegalArgumentException e) {
                return new Result(RC_INVALID_DATA, e.getMessage());
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }

        return result;
    }*/

    @Override
    public Result removeGuardian(@WebParam(name = "guardianContractId") Long guardianContractId,
            @WebParam(name = "childContractId") Long childContractId) {
        authenticateRequest(guardianContractId);

        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client child = findClientByContractId(session, childContractId, result);
            Client guardian = findClientByContractId(session, guardianContractId, result);


            if (guardian == null || child == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            ClientGuardian cg = DAOReadonlyService.getInstance().findClientGuardianById(session, child.getIdOfClient(), guardian.getIdOfClient());
            if (cg == null || !cg.getCreatedFrom().equals(ClientCreatedFromType.MPGU)) {
                result.resultCode = RC_INVALID_DATA;
                result.description = "Представитель не найден или был создан в другой системе. Удаление невозможно";
                return result;
            }

            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
            cg.setDisabled(true);
            cg.setVersion(newGuardiansVersions);
            session.update(cg);

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

        return result;
    }

    @Override
    public MuseumEnterInfo getMuseumEnterInfo(@WebParam(name = "cardId") String cardId, @WebParam(name = "museumName") String museumName) {
        authenticateRequest(null);
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            long lCardId = Long.parseLong(cardId);
            Card card = DAOUtils.findCardByCardNo(session, lCardId);
            Client client = (card == null ? null : card.getClient());
            if (client == null) {
                return new MuseumEnterInfo(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC, "", MuseumEnterInfo.MUSEUM_ENTER_TYPE_PAY, "");
            }

            String orgShortName = client.getOrg().getShortNameInfoService();
            String guid = client.getClientGUID();
            Integer enterType = MuseumEnterInfo.MUSEUM_ENTER_TYPE_PAY;
            Date currentDate = new Date();
            boolean freeType = (
                    (card.getState() == CardState.ISSUED.getValue() || card.getState() == CardState.TEMPISSUED.getValue())
                    && card.getValidTime().after(currentDate) &&
                            !(client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup() >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            && client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup() <= ClientGroup.Predefined.CLIENT_DELETED.getValue())
            );
            if (freeType) {
                enterType = MuseumEnterInfo.MUSEUM_ENTER_TYPE_FREE;
            }
            return new MuseumEnterInfo(RC_OK, RC_OK_DESC, orgShortName, enterType, guid);
        } catch (Exception e) {
            return new MuseumEnterInfo(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC, "", MuseumEnterInfo.MUSEUM_ENTER_TYPE_PAY, "");
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private Long[] getVPOrgsList() {
        List<Long> result = new ArrayList<Long>();
        String[] strs = RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.vp.pilot.orgs", "697, 748, 1830, 1831, 1832, 2499").split(",");
        for (String str : strs) {
            result.add(Long.parseLong(str.trim()));
        }
        return result.toArray(new Long[result.size()]);
    }
}
