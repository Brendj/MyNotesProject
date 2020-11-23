/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;


import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.MigrantsUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.smartwatch.dto.*;
import ru.axetta.ecafe.processor.web.ui.card.CardLockReason;

import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.*;

import static java.lang.Math.abs;


@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Controller
@DependsOn("runtimeContext")
@ApplicationPath("/smartwatch/")
public class SmartWatchRestController extends Application {
    private Logger logger = LoggerFactory.getLogger(SmartWatchRestController.class);
    private Map<Integer, String> cardState;
    private boolean debug;

    private final Integer CARD_TYPE_SMARTWATCH = Arrays.asList(Card.TYPE_NAMES).indexOf("Часы (Mifare)");
    private final Integer DEFAULT_SAMPLE_LIMIT = 10;

    private final String BLOCK_SMART_WATCH = "Блокировка чосов (Mifare)";
    private final String REISSUE_SMART_WATCH = "Выдача чосов (Mifare) новому владельцу";

    private final String IS_BUFFET = StringUtils.join(Arrays.asList(
            OrderTypeEnumType.UNKNOWN.ordinal(),
            OrderTypeEnumType.DEFAULT.ordinal(),
            OrderTypeEnumType.VENDING.ordinal()
    ), ", ");

    private final String IS_REDUCED_PRICE_PLAN_FOOD = StringUtils.join(Arrays.asList(
            OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal(),
            OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal(),
            OrderTypeEnumType.DISCOUNT_PLAN_CHANGE.ordinal()
    ), ", ");

    private final String IS_PAY_PLAN_FOOD = StringUtils.join(Arrays.asList(
            OrderTypeEnumType.PAY_PLAN.ordinal(),
            OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal()
    ), ", ");

    @PostConstruct
    public void init(){
        this.cardState = new HashMap<Integer, String>();
        for (CardState state : CardState.values()) {
            this.cardState.put(state.getValue(), state.getDescription());
        }
        this.debug = this.isDebug();
    }

    private boolean isDebug() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext
                .getConfigProperties().getProperty("ecafe.processor.geoplaner.restcontroller.debug", "false");
        return Boolean.parseBoolean(reqInstance);
    }

    @POST
    @Path(value = "getTokenByMobile")
    public Response sendLinkingTokenByMobile(@FormParam(value="mobilePhone") String mobilePhone)throws Exception {
        logger.info(String.format("getTokenByMobile: Try create new LinkingToken for guardianPhone: %s", mobilePhone));

        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            if(mobilePhone == null || mobilePhone.isEmpty()){
                throw new IllegalArgumentException("Invalid mobilePhone number: is null or is empty");
            }
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkAndConvertPhone(mobilePhone);
            List<Client> clients = DAOService.getInstance().getClientsListByMobilePhone(mobilePhone);
            if(clients.isEmpty()){
                throw new IllegalArgumentException("No clients found for this mobilePhone number: " + mobilePhone);
            }
            if(!isGuardian(session, clients)){
                throw new IllegalArgumentException("Client found by mobilePhone number: " + mobilePhone
                        + ", is not a guardian");
            }

            String token = DAOService.getInstance().generateLinkingTokenForSmartWatch(session, mobilePhone);
            Client client = clients.get(0);
            RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                    .sendMessageAsync(client, EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                            new String[]{"linkingToken", token}, new Date());

            transaction.commit();
            transaction = null;

            result.resultCode = ResponseCodes.RC_OK.getCode();
            result.description = "Код активации отправлен по SMS" +
                    (client.hasEmail()? " и по Email" : "");

            logger.info(String.format("getTokenByMobile: Processing completed successfully, token %s send by async method for guardianPhone: %s",
                    token, mobilePhone)
            );

            token = "";
            return Response.status(HttpURLConnection.HTTP_OK)
                    .entity(result)
                    .build();
        } catch (IllegalArgumentException e){
            logger.error(String.format("getTokenByMobile: Can't generate or send token for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getTokenByMobile: Can't generate or send token for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value = "getListInfoOfChildrens")
    public Response getListOfChildrenByPhoneAndToken(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token)throws Exception {
        JsonListInfo result = new JsonListInfo();
        Session session = null;
        Transaction transaction = null;
        try{
            if(mobilePhone == null || mobilePhone.isEmpty()){
                throw new IllegalArgumentException("Invalid mobilePhone number: is null or is empty");
            }

            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            token = "";

            List<Client> parents = findParentsByMobile(mobilePhone);

            List<JsonChildrenDataInfoItem> items = buildChildrenDataInfoItems(session, parents);
            result.setItems(items);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getListInfoOfChildrens: Can't get List Of Children's for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getListInfoOfChildrens: Can't get List Of Children's for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @POST
    @Path(value = "registrySmartWatch")
    public Response registrySmartWatch(@FormParam(value="mobilePhone") String mobilePhone, @FormParam(value="token") String token,
            @FormParam(value="contractId") Long contractId, @FormParam(value="model") String model, @FormParam(value="color") String color,
            @FormParam(value="trackerUid") Long trackerUid, @FormParam(value="trackerID") Long trackerId,
            @FormParam(value="trackerActivateUserId") Long trackerActivateUserId, @FormParam(value="status") String status,
            @FormParam(value="trackerActivateTime") Long trackerActivateTime, @FormParam(value="simIccid") String simIccid,
            @HeaderParam(value="vendorId") Long vendorId) throws Exception {
        logger.info(String.format("Try registry SmartWatch for Phone: %s", mobilePhone));
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = inputParamsIsValidOrTrowException(session, trackerId, trackerUid, mobilePhone, token);

            Client child = findClientWithCheckOnParent(session, mobilePhone, contractId);

            if(childHasAnActiveSmartWatch(session, child)){
                throw new IllegalArgumentException("The client witch contractID: " + child.getContractId()
                        + " has an active SmartWatch");
            }

            SmartWatchVendorManager manager = RuntimeContext.getAppContext().getBean(SmartWatchVendorManager.class);
            SmartWatchVendor vendor = manager.getVendorById(vendorId);

            Date issueTime = new Date();
            Date validTime = CalendarUtils.addYear(issueTime, 5); // Карта действительна с момента выдачи/передачи новому лицу + 5 лет

            CardManager cardManager = RuntimeContext.getInstance().getCardManager();

            Card card = DAOUtils.findCardByCardNo(session, trackerUid);
            Long idOfCard = null;
            if(card == null) {
                idOfCard = cardManager
                        .createSmartWatchAsCard(session, child.getIdOfClient(), trackerId, Card.ACTIVE_STATE, validTime,
                                Card.ISSUED_LIFE_STATE, null, issueTime, trackerUid, null, vendor.getCardSignCertNum());
            } else {
                if((card.getClient() == null || card.getState().equals(CardState.BLOCKED.getValue()))
                        && card.getCardType().equals(CARD_TYPE_SMARTWATCH)) {
                    cardManager.updateCard(child.getIdOfClient(), card.getIdOfCard(), card.getCardType(), CardState.ISSUED.getValue(), validTime,
                            card.getLifeState(), "", issueTime, card.getExternalId(), null,
                            child.getOrg().getIdOfOrg(), REISSUE_SMART_WATCH);
                } else {
                    throw new Exception("Card CardNo: " + card.getCardNo()
                            + " is registered and owned Client contractID: "
                            + card.getClient().getContractId()
                            + " (tried to register on the client with contractID: " + child.getContractId() + " )");
                }
                idOfCard = card.getIdOfCard();
            }

            child.setHasActiveSmartWatch(true);
            child.setVendor(vendor);

            Date trackerActivateTimeDate = trackerActivateTime == null ? new Date() : new Date(trackerActivateTime);
            SmartWatch watch = DAOUtils.findSmartWatchByTrackerUidAndTrackerId(session, trackerId, trackerUid);
            if(watch == null) {
                DAOUtils.createSmartWatch(session, idOfCard, child.getIdOfClient(), model, color,
                        trackerUid, trackerId, trackerActivateUserId, status, trackerActivateTimeDate, simIccid, vendor);
            } else {
                this.updateSmartWatch(watch, session, idOfCard, child.getIdOfClient(), color, model,
                        trackerUid, trackerId, trackerActivateUserId, status, trackerActivateTimeDate, simIccid, vendor);
            }

            blockActiveCards(child, idOfCard);
            session.update(child);

            transaction.commit();
            transaction = null;

            logger.info(String.format("registrySmartWatch: Processing completed successfully for guardianPhone: %s", mobilePhone));
            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("registrySmartWatch: Can't registry SmartWatch for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("registrySmartWatch: Can't registry SmartWatch for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @POST
    @Path(value = "blockSmartWatch")
    public Response blockSmartWatch(@FormParam(value="mobilePhone") String mobilePhone, @FormParam(value="token") String token,
            @FormParam(value="trackerUid") Long trackerUid, @FormParam(value="trackerID") Long trackerId)throws Exception{
        logger.info(String.format("Try block SmartWatch for guardianPhone: %s", mobilePhone));

        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = inputParamsIsValidOrTrowException(session, trackerId, trackerUid, mobilePhone, token);

            Client parent = DAOService.getInstance().getClientByMobilePhone(mobilePhone);
            Card card = DAOUtils.findSmartWatchAsCardByCardNoAndCardPrintedNo(session, trackerId, trackerUid,
                    CARD_TYPE_SMARTWATCH);
            if(card == null){
                throw new Exception("No SmartWatch as card found by trackerUid: " + trackerUid + " and trackerId: " + trackerId);
            }

            Client child = card.getClient();
            if(!isRelatives(session, parent, child)){
                throw new IllegalArgumentException("Parent (contractID: " + parent.getContractId() + ") and Child (contractID: " + child.getContractId() + ") is not relatives");
            }

            if(card.getState().equals(CardState.BLOCKED.getValue())){
                throw new IllegalArgumentException("SmartWatch already blocked");
            }

            blockActiveCard(child, card);
            child.setHasActiveSmartWatch(false);
            child.setVendor(null);
            session.update(child);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("blockSmartWatch: Can't block SmartWatch for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("blockSmartWatch: Can't block SmartWatch for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private Client findClientWithCheckOnParent(Session session, String mobilePhone, Long contractId) throws Exception {
        if(contractId == null){
            throw new IllegalArgumentException("ContractID is null");
        }
        List<Client> parents = findParentsByMobile(mobilePhone);

        Client child = DAOUtils.findClientByContractId(session, contractId);
        if(child == null){
            throw new IllegalArgumentException("No clients found by contractID: " + contractId);
        }
        if(!isRelatives(session, parents, child)){
            throw new IllegalArgumentException(String.format("Not found parent for child with contractId=%s", contractId));
        }
        return child;
    }

    @GET
    @Path(value="getEnterEvents")
    public Response getEnterEvents(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="contractId") Long contractId,
            @QueryParam(value="startDate") Long startDateTime, @QueryParam(value="endDate") Long endDateTime,
            @QueryParam(value="limit") Integer limit){
        JsonEnterEvents result = new JsonEnterEvents();
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            Client child = findClientWithCheckOnParent(session, mobilePhone, contractId);

            if(startDateTime == null){
                logger.warn("Start date is Null, set as now");
                startDateTime = new Date().getTime();
            }
            if(endDateTime != null && endDateTime >= startDateTime){
                endDateTime = null;
            }

            List<JsonEnterEventItem> items = buildEnterEventItem(session, child, startDateTime, endDateTime, limit);
            result.setItems(items);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getEnterEvents: Can't get EnterEvents for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getEnterEvents: Can't get EnterEvents for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Deprecated
    @GET
    @Path(value="getPurchasesOld")
    public Response getPurchasesOld(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="contractId") Long contractId,
            @QueryParam(value="startDate") Long startDateTime, @QueryParam(value="endDate") Long endDateTime,
            @QueryParam(value="limit") Integer limit){
        Session session = null;
        Transaction transaction = null;
        JsonPurchases result = new JsonPurchases();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            Client child = findClientWithCheckOnParent(session, mobilePhone, contractId);

            Date startDate;
            Date endDate = null;

            if(startDateTime == null){
                logger.warn("Start date is Null, set as now");
                startDate = new Date();
            } else {
                startDate = new Date(startDateTime);
            }

            if(endDateTime != null && endDateTime < startDate.getTime()){
                endDate = new Date(endDateTime);
            }

            List<JsonOrder> items = buildPaymentsInfo(session, child, startDate, endDate, limit);
            result.setItems(items);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getPurchasesOld: Can't get Purchases for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getPurchasesOld: Can't get Purchases for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value="getPurchases")
    public Response getPurchases(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="contractId") Long contractId,
            @QueryParam(value="startDate") Long startDateTime, @QueryParam(value="endDate") Long endDateTime,
            @QueryParam(value="limit") Integer limit, @QueryParam(value="transactionType") Integer transactionType){
        Session session = null;
        Transaction transaction = null;
        JsonTransactions result = new JsonTransactions();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            Client child = findClientWithCheckOnParent(session, mobilePhone, contractId);

            Date startDate;
            Date endDate = null;

            if(startDateTime == null){
                logger.warn("Start date is Null, set as now");
                startDate = new Date();
            } else {
                startDate = new Date(startDateTime);
            }

            if(endDateTime != null && endDateTime < startDate.getTime()){
                endDate = new Date(endDateTime);
            }

            List<JsonTransaction> items = buildTransactionsInfo(session, child, startDate, endDate, limit, transactionType);
            result.setItems(items);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getPurchases: Can't get Transactions for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getPurchases: Can't get Transactions for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value="getPurchaseDetail")
    public Response getPurchaseDetail(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="idOfTransaction") Long idOfTransaction){
        Session session = null;
        Transaction transaction = null;
        JsonPurchaseDetail result = new JsonPurchaseDetail();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            token = "";

            List<JsonPurchaseDetailItem> items = buildPurchaseDetailInfo(session, idOfTransaction);
            result.setItems(items);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getPurchaseDetail: Can't get PurchaseDetail for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getPurchaseDetail: Can't get PurchaseDetail for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value="getPurchasedComplexes")
    public Response getPurchasedComplexes(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="contractId") Long contractId,
            @QueryParam(value="startDate") Long startDateTime, @QueryParam(value="endDate") Long endDateTime,
            @QueryParam(value="limit") Integer limit){
        Session session = null;
        Transaction transaction = null;
        JsonPurchaseDetail result = new JsonPurchaseDetail();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            Client child = findClientWithCheckOnParent(session, mobilePhone, contractId);

            Date startDate;
            Date endDate = null;

            if(startDateTime == null){
                logger.warn("Start date is Null, set as now");
                startDate = new Date();
            } else {
                startDate = new Date(startDateTime);
            }

            if(endDateTime != null && endDateTime < startDate.getTime()){
                endDate = new Date(endDateTime);
            }

            List<JsonPurchaseDetailItem> items = buildPurchaseComplexInfo(session, child, startDate, endDate, limit);
            result.setItems(items);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getPurchasedComplexes: Can't get PurchasedComplexes for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getPurchasedComplexes: Can't get PurchasedComplexes for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value="getBalance")
    public Response getBalance(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="contractId") Long contractId) {
        JsonBalance result = new JsonBalance();
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            Client child = findClientWithCheckOnParent(session, mobilePhone, contractId);

            JsonBalanceInfo info = buildBalanceInfo(session, child);
            result.setBalanceInfo(info);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getBalance: Can't get Balance for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getBalance: Can't get Balance for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value="getBalanceOperations")
    public Response getBalanceOperations(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="contractId") Long contractId,
            @QueryParam(value="startDate") Long startDateTime, @QueryParam(value="endDate") Long endDateTime){
        Session session = null;
        Transaction transaction = null;
        JsonBalanceOperations result = new JsonBalanceOperations();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            Client child = findClientWithCheckOnParent(session, mobilePhone, contractId);

            Date startDate;
            Date endDate;

            if(startDateTime == null){
                logger.warn("Start date is Null, set as now");
                startDate = new Date();
            } else {
                startDate = new Date(startDateTime);
            }

            if(endDateTime != null && endDateTime < startDate.getTime()){
                endDate = new Date(endDateTime);
            } else {
                endDate = new Date();
            }

            List<JsonBalanceOperationsItem> items = buildBalanceOperations(session, child, startDate, endDate);
            result.setItems(items);

            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getBalanceOperations: Can't get BalanceOperations for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getBalanceOperations: Can't get BalanceOperations for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private List<JsonBalanceOperationsItem> buildBalanceOperations(Session session, Client child, Date startDate, Date endDate) {
        SQLQuery query = session.createSQLQuery(
                " select o.createdDate as orderDate, " + SmartWatchTransactionTypes.IS_DESCRIPTION_OF_CASH.ordinal() + " as transactionType, "
                + SmartWatchOrderType.BUFFET.ordinal() + " as ordertype, t.idOfTransaction, "
                + " o.rSum, cast(NULL as bigint) as date, NULL as complexName, o.state, \n"
                + " string_agg(od.menudetailname, ',') as goodsNames, string_agg(cast(od.qty as varchar), ',') as qty, string_agg(cast(od.rprice as varchar), ',') as rPrices \n"
                + " from cf_orders o\n"
                + " left join cf_transactions t on t.idoftransaction = o.idoftransaction and t.idoforg = o.idoforg\n"
                + " join cf_orderdetails od on od.idoforg = o.idoforg and od.idoforder = o.idoforder\n"
                + " join cf_clients c on o.idofclient = c.idofclient\n"
                + " where o.createddate between :endDate and :startDate and o.ordertype in ( " + IS_BUFFET + " )\n"
                + " and c.idOfClient = :idOfClient\n"
                + " group by 1, 2, 3, 4, 5, 6, 7, 8\n"
                + " union\n"
                + " select o.createdDate as orderDate, " + SmartWatchTransactionTypes.IS_DESCRIPTION_OF_CASH.ordinal() + " as transactionType,\n"
                + " case \n"
                + " when o.ordertype in ( " + IS_REDUCED_PRICE_PLAN_FOOD + " ) then " + SmartWatchOrderType.REDUCED_PRICE_PLAN_FOOD.ordinal()
                + " else " + SmartWatchOrderType.PAY_PLAN_FOOD.ordinal()
                + " end as orderType, cast(NULL as bigint) as idOfTransaction,\n"
                + " case \n"
                + "  when o.ordertype in ( " + IS_REDUCED_PRICE_PLAN_FOOD + " ) then NULL \n"
                + "  else o.rSum \n"
                + " end as rSum, \n"
                + " o.orderDate as date, q.complexName, o.state, NULL as goodsNames, NULL as qty, NULL as rPrices \n"
                + " from cf_orders o \n"
                + " left join cf_transactions t on t.idoftransaction = o.idoftransaction and t.idoforg = o.idoforg\n"
                + " join cf_clients c on o.idofclient = c.idofclient\n"
                + " left join (select idoforg, idoforder, menudetailname as complexname \n"
                + "           from cf_orderdetails where menutype between 50 and 99 ) as q on q.idoforder = o.idoforder and q.idoforg = o.idoforg \n"
                + " where o.createddate between :endDate and :startDate and o.ordertype in ( " + IS_REDUCED_PRICE_PLAN_FOOD + ", " + IS_PAY_PLAN_FOOD + " ) "
                + " and c.idOfClient = :idOfClient\n"
                + " group by 1, 2, 3, 4, 5, 6, 7, 8\n"
                + " union \n"
                + " select t.transactiondate orderDate,\n"
                + " case when t.transactionsum < 0 then " + SmartWatchTransactionTypes.IS_DESCRIPTION_OF_CASH.ordinal()
                + " else " + SmartWatchTransactionTypes.IS_REPLENISHMENT.ordinal()
                + " end as transactionType,\n"
                + SmartWatchOrderType.BUFFET.ordinal() + " as orderType, \n"
                + " t.idOfTransaction, abs(t.transactionsum) as rSum, cast(NULL as bigint) as date, \n"
                + " NULL as complexName, 0 as state, \n"
                + " NULL as goodsNames, NULL as qty, NULL as rPrices "
                + " from cf_transactions t\n"
                + " join cf_clients c on t.idofclient = c.idofclient\n"
                + " left join cf_orders o on t.idoftransaction = o.idoftransaction and t.idoforg = o.idoforg\n"
                + " where t.transactiondate between :endDate and :startDate and o.idoforder is NULL \n"
                + " and c.idOfClient = :idOfClient\n"
                + " order by orderType, orderDate" );

        query.setParameter("endDate", endDate.getTime())
             .setParameter("startDate", startDate.getTime())
             .setParameter("idOfClient", child.getIdOfClient());

        query.addScalar("orderDate", new LongType())
             .addScalar("transactionType", new IntegerType())
             .addScalar("orderType", new IntegerType())
             .addScalar("idOfTransaction", new LongType())
             .addScalar("rSum", new LongType())
             .addScalar("date", new LongType())
             .addScalar("complexName", new StringType())
             .addScalar("goodsNames", new StringType())
             .addScalar("qty", new StringType())
             .addScalar("rPrices", new StringType())
             .addScalar("state", new IntegerType());

        query.setResultTransformer(Transformers.aliasToBean(JsonBalanceOperationsItem.class));

        List<JsonBalanceOperationsItem> result = query.list();
        for(JsonBalanceOperationsItem item : result){
            if(item.getOrderType().equals(SmartWatchOrderType.BUFFET.ordinal()) && item.orderDetailsInfoInfoIsNotNull()){
                String[] splitedGoodNames = item.getGoodsNames().split(",");
                String[] splitedQty = item.getQty().split(",");
                String[] splitedPrice = item.getrPrices().split(",");
                
                for(int i = 0; i < splitedGoodNames.length; i++){
                    JsonOrderDetail orderDetail = new JsonOrderDetail();
                    orderDetail.setGoodName(splitedGoodNames[i]);
                    orderDetail.setrPrice(Long.parseLong(splitedPrice[i]));
                    orderDetail.setQty(Long.parseLong(splitedQty[i]));
                    item.getOrderDetails().add(orderDetail);
                }
            }
        }
        return result;
    }

    @GET
    @Path(value="getLocations")
    public Response getLocations(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token) {
        JsonLocations result = new JsonLocations();
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkMobilePhone(session, mobilePhone, token);
            token = "";

            List<Client> parents = findParentsByMobile(mobilePhone);

            List<Client> children = findChildrenByGuardians(session, parents);

            for(Client child : children) {
                JsonLocationsInfo locations = buildLocations(session, child);
                result.getLocations().add(locations);
            }
            transaction.commit();
            transaction = null;

            return resultOK(result);
        } catch (IllegalArgumentException e){
            logger.error(String.format("getLocations: Can't get Locations for guardianPhone %s", mobilePhone), e);
            return resultBadArgs(result, e);
        } catch (Exception e){
            logger.error(String.format("getLocations: Can't get Locations for guardianPhone %s", mobilePhone), e);
            return resultException(result, e);
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private List<Client> findChildrenByGuardians(Session session, List<Client> parents) {
        List<ClientGuardian> listOfClientGuardianByIdOfGuardian = DAOUtils.findListOfClientGuardianByIdOfGuardian(session, parents);
        if(CollectionUtils.isEmpty(listOfClientGuardianByIdOfGuardian)){
            return Collections.emptyList();
        }
        List<Client> children = new LinkedList<Client>();
        for(ClientGuardian relation : listOfClientGuardianByIdOfGuardian){
            Client child = (Client) session.get(Client.class, relation.getIdOfChildren());
            if(child != null){
                children.add(child);
            }
        }
        return children;
    }

    private JsonLocationsInfo buildLocations(Session session, Client child) throws Exception {
        Org mainOrg = child.getOrg();
        Integer typeOfMainOrg = mainOrg.getType().getCode();
        JsonLocationsInfo info = new JsonLocationsInfo(mainOrg.getShortName(), mainOrg.getShortAddress());
        info.setFio(child.getPerson().getFullName());
        info.setContractId(child.getContractId());
        info.setHasActiveSmartWatch(child.clientHasActiveSmartWatch());

        if(typeOfMainOrg.equals(OrganizationType.PROFESSIONAL.getCode()) || typeOfMainOrg.equals(OrganizationType.SCHOOL.getCode())) {
            for (Org fo : mainOrg.getFriendlyOrg()) {
                if (fo.getIdOfOrg().equals(mainOrg.getIdOfOrg()) || !fo.getType().getCode().equals(typeOfMainOrg)) {
                    continue;
                }
                OrgInformation friendlyOrgInfo = new OrgInformation(fo.getShortName(), fo.getShortAddress());
                info.getFriendlyOrgInfo().add(friendlyOrgInfo);
            }
        }

        List<Migrant> clientMigrants = MigrantsUtils.getActiveMigrantsByIdOfClient(session, child.getIdOfClient());
        for(Migrant m : clientMigrants){
            MigrantInfo migrantInfo = new MigrantInfo(m);
            info.getMigrants().add(migrantInfo);
        }

        return info;
    }

    private JsonBalanceInfo buildBalanceInfo(Session session, Client child) throws Exception {
        JsonBalanceInfo info = new JsonBalanceInfo();
        if(child.getBalance() != null){
            info.setTotalBalance(child.getBalance());
        }
        //TODO Подсчет резерва на предзаказы и возвраты
        return info;
    }

    private List<JsonOrder> buildPaymentsInfo(Session session, Client child, Date startDate,
            Date endDate, Integer limit) throws Exception{
        List<JsonOrder> items = new LinkedList<JsonOrder>();
        List<Order> ordersOfClient = null;
        Criterion timeRestriction = endDate == null ?
                Restrictions.le("orderDate", startDate) : Restrictions.between("orderDate", startDate, endDate);

        if(limit == null || limit <= 0){
            limit = DEFAULT_SAMPLE_LIMIT;
        }

        Criteria criteria = session.createCriteria(Order.class);
        criteria.add(Restrictions.eq("client", child))
                .add(timeRestriction)
                .addOrder(org.hibernate.criterion.Order.desc("orderDate"))
                .setMaxResults(limit);

        ordersOfClient = criteria.list();

        if(ordersOfClient == null || ordersOfClient.isEmpty()){
            logger.warn("No found Orders of Client contractID: " + child.getContractId());
            return Collections.emptyList();
        }

        for(Order o: ordersOfClient){
            JsonOrder item = new JsonOrder();
            item.setOrderDate(o.getOrderDate());
            if(o.getTransaction() != null){
                item.setTransactionType(o.getTransaction().getSourceTypeAsString());
            }
            item.setOrderType(o.getOrderType().toString());
            item.setrSum(o.getRSum());
            item.setSocDiscount(o.getSocDiscount());
            item.setTrdDiscount(o.getTrdDiscount());
            item.setGrantSum(o.getGrantSum());
            item.setSumByCard(o.getSumByCard());
            item.setSumByCash(o.getSumByCash());
            if(o.getCard() != null){
                item.setCardType(Card.TYPE_NAMES[o.getCard().getCardType()]);
            }
            if(o.getClient() != null){
                item.setClient(o.getClient().getPerson().getFullName());
            }
            for(OrderDetail detail : o.getOrderDetails()){
                JsonOrderDetail jsonOrderDetail = new JsonOrderDetail();
                jsonOrderDetail.setGoodName(detail.getMenuDetailName());
                jsonOrderDetail.setrPrice(detail.getRPrice());
                jsonOrderDetail.setQty(detail.getQty());
                item.getOrderDetails().add(jsonOrderDetail);
            }
            items.add(item);
        }
        return items;
    }

    private List<JsonEnterEventItem> buildEnterEventItem(Session session, Client child, Long startDate, Long endDate,
            Integer limit) throws Exception {
        List<JsonEnterEventItem> items = new LinkedList<JsonEnterEventItem>();
        List<EnterEventsItem> events = null;
        String timeConditional = endDate == null ? " ee.evtDateTime <= :startDate " : " ee.evtDateTime BETWEEN :endDate AND :startDate ";

        if(limit == null || limit <= 0){
            limit = DEFAULT_SAMPLE_LIMIT;
        }

        SQLQuery query = session.createSQLQuery("SELECT ee.passDirection, ee.evtDateTime, ee.idOfClient, ee.idOfCard, o.shortNameInfoService, o.shortAddress "
                + " FROM cf_enterevents AS ee "
                + " JOIN cf_orgs AS o ON o.idOfOrg = ee.idOfOrg "
                + " WHERE " + timeConditional
                + " AND ee.idofclient = :idOfChild "
                + " ORDER BY 2 DESC "
                + " LIMIT :limit");
        query
                .addScalar("passDirection", new IntegerType())
                .addScalar("evtDateTime", new LongType())
                .addScalar("idOfClient", new LongType())
                .addScalar("idOfCard", new LongType())
                .addScalar("ShortNameInfoService", new StringType())
                .addScalar("ShortAddress", new StringType())

                .setParameter("startDate", startDate)
                .setParameter("idOfChild", child.getIdOfClient())
                .setParameter("limit", limit)
                .setResultTransformer(Transformers.aliasToBean(EnterEventsItem.class));

        if(endDate != null){
            query.setParameter("endDate", endDate);
        }
        events = query.list();

        if(events == null || events.isEmpty()){
            logger.warn("Not found event for client contractID: " + child.getContractId());
            return Collections.emptyList();
        }

        for(EnterEventsItem event : events){
            JsonEnterEventItem item = new JsonEnterEventItem();
            item.setDirection(event.getPassDirection());
            item.setEvtDateTime(event.getEvtDateTime());
            if(event.getIdOfCard() != null){
                Card card = findClientCardByCardNo(child.getCards(), event.getIdOfCard());
                if(card != null) {
                    item.setCardType(Card.TYPE_NAMES[card.getCardType()]);
                }
            }
            if(event.getIdOfClient() != null){
                item.setClient(child.getPerson().getFullName());
            }
            item.setShortNameInfoService(event.getShortNameInfoService());
            item.setAddress(event.getShortAddress());

            items.add(item);
        }
        return items;
    }

    private List<JsonTransaction> buildTransactionsInfo(Session session, Client child, Date startDate, Date endDate,
            Integer limit, Integer transactionType) throws Exception{
        List<JsonTransaction> items = new LinkedList<JsonTransaction>();
        List<AccountTransaction> accountTransactionList = null;
        Criterion timeRestriction = endDate == null ?
                Restrictions.le("transactionTime", startDate) : Restrictions.between("transactionTime", endDate, startDate);

        if(limit == null || limit <= 0){
            limit = DEFAULT_SAMPLE_LIMIT;
        }

        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.eq("client", child))
                .add(timeRestriction)
                .addOrder(org.hibernate.criterion.Order.desc("transactionTime"))
                .setMaxResults(limit);

        if(transactionType.equals(SmartWatchTransactionTypes.IS_REPLENISHMENT.ordinal())){
            criteria.add(Restrictions.gt("transactionSum", 0L));
        } else if (transactionType.equals(SmartWatchTransactionTypes.IS_DESCRIPTION_OF_CASH.ordinal())){
            criteria.add(Restrictions.lt("transactionSum", 0L));
        }

        accountTransactionList = criteria.list();

        if(accountTransactionList == null || accountTransactionList.isEmpty()){
            logger.warn("No transactions was found for client contractID: " + child.getContractId());
            return Collections.emptyList();
        }

        for(AccountTransaction at: accountTransactionList){
            JsonTransaction jsonTransaction = new JsonTransaction();
            jsonTransaction.setIdOfTransaction(at.getIdOfTransaction());
            jsonTransaction.setOrderDate(at.getTransactionTime());
            jsonTransaction.setTransactionSum(abs(at.getTransactionSum()));
            if(at.getTransactionSum() < 0) {
                jsonTransaction.setTransactionType(SmartWatchTransactionTypes.IS_DESCRIPTION_OF_CASH.ordinal());
            } else {
                jsonTransaction.setTransactionType(SmartWatchTransactionTypes.IS_REPLENISHMENT.ordinal());
            }
            items.add(jsonTransaction);
        }
        return items;
    }

    private List<JsonPurchaseDetailItem> buildPurchaseDetailInfo(Session session, Long idOfTransaction) throws Exception{
        List<JsonPurchaseDetailItem> items = new LinkedList<JsonPurchaseDetailItem>();

        AccountTransaction accountTransaction = (AccountTransaction) session
                .load(AccountTransaction.class, idOfTransaction);

        if(accountTransaction == null){
            logger.warn("No transaction was found with idOfTransaction: " + idOfTransaction);
            return Collections.emptyList();
        }

        for(Order order: accountTransaction.getOrders()){
            items.add(fillPurchaseDetailItem(order));
        }
        return items;
    }

    private List<JsonPurchaseDetailItem> buildPurchaseComplexInfo(Session session, Client child, Date startDate, Date endDate, Integer limit) throws Exception{
        List<JsonPurchaseDetailItem> items = new LinkedList<JsonPurchaseDetailItem>();
        List<Order> ordersOfClient = null;
        Criterion timeRestriction = endDate == null ?
                Restrictions.le("orderDate", startDate) : Restrictions.between("orderDate", endDate, startDate);

        if(limit == null || limit <= 0){
            limit = DEFAULT_SAMPLE_LIMIT;
        }

        Criteria criteria = session.createCriteria(Order.class);
        List<OrderTypeEnumType> complexTypeList = new ArrayList<OrderTypeEnumType>();
        complexTypeList.add(OrderTypeEnumType.PAY_PLAN);
        complexTypeList.add(OrderTypeEnumType.REDUCED_PRICE_PLAN);
        complexTypeList.add(OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE);
        complexTypeList.add(OrderTypeEnumType.SUBSCRIPTION_FEEDING);
        complexTypeList.add(OrderTypeEnumType.DISCOUNT_PLAN_CHANGE);
        criteria.add(Restrictions.in("orderType", complexTypeList));
        criteria.add(Restrictions.eq("client", child))
                .add(timeRestriction)
                .addOrder(org.hibernate.criterion.Order.desc("orderDate"))
                .setMaxResults(limit);

        ordersOfClient = criteria.list();

        if(ordersOfClient == null || ordersOfClient.isEmpty()){
            logger.warn("No found Orders of Client contractID: " + child.getContractId());
            return Collections.emptyList();
        }

        for(Order o: ordersOfClient){
            items.add(fillPurchaseDetailItem(o));
        }
        return items;
    }

    private Card findClientCardByCardNo(Set<Card> cards, Long idOfCard) {
        if(cards == null){
            return null;
        }
        for(Card card : cards){
            if(card.getCardNo().equals(idOfCard)){
                return card;
            }
        }
        return null;
    }

    private String inputParamsIsValidOrTrowException(Session session, Long trackerId, Long trackerUid,
            String mobilePhone, String token) throws Exception{
        if(trackerUid == null || trackerId == null){
            throw new IllegalArgumentException("TrackerUID or trackerID is null");
        }
        mobilePhone = checkAndConvertPhone(mobilePhone);
        if(mobilePhone == null || mobilePhone.isEmpty()){
            throw new IllegalArgumentException("Invalid mobilePhone number: is null or is empty");
        }
        if(!isValidPhoneAndToken(session, mobilePhone, token)){
            throw new IllegalArgumentException("Invalid token and mobilePhone number, mobilePhone: " + mobilePhone );
        }
        token = "";
        return  mobilePhone;
    }

    private void updateSmartWatch(SmartWatch watch, Session session, Long idOfCard, Long idOfClient, String color,
            String model, Long trackerUid, Long trackerId, Long trackerActivateUserId, String status, Date trackerActivateTimeDate, String simIccid,
            SmartWatchVendor vendor) {
        try {
            watch.setIdOfCard(idOfCard);
            watch.setIdOfClient(idOfClient);
            watch.setTrackerId(trackerId);
            watch.setTrackerUid(trackerUid);
            watch.setModel(model);
            watch.setColor(color);
            watch.setTrackerActivateUserId(trackerActivateUserId);
            watch.setStatus(status);
            watch.setTrackerActivateTime(trackerActivateTimeDate);
            watch.setSimIccid(simIccid);
            watch.setVendor(vendor);
            session.update(watch);
        } catch (Exception e) {
            logger.error("Can't update SmartWatch with ID " + watch.getIdOfSmartWatch() + " : ", e);
        }
    }

    private void blockActiveCard(Client client, Card card) throws Exception {
        if (card.getState() == Card.ACTIVE_STATE) {
            RuntimeContext.getInstance().getCardManager()
                    .updateCard(client.getIdOfClient(), card.getIdOfCard(), card.getCardType(),
                            CardState.BLOCKED.getValue(), card.getValidTime(), card.getLifeState(),
                            CardLockReason.OTHER.getDescription(), card.getIssueTime(), card.getExternalId(),
                            null, card.getOrg().getIdOfOrg(), BLOCK_SMART_WATCH);
        }
    }

    private void blockActiveCards(Client child, Long idofCardAsSmartWatch) throws Exception{
        Set<Card> cardSet = child.getCards();
        for(Card card : cardSet) {
            if(card.getIdOfCard().equals(idofCardAsSmartWatch)){
                continue;
            }
            this.blockActiveCard(child, card);
        }
    }

    private boolean isRelatives(Session session, Client parent, Client child) throws Exception {
        return DAOUtils.findClientGuardian(session, child.getIdOfClient(), parent.getIdOfClient()) != null;
    }

    private boolean isRelatives(Session session, List<Client> parents, Client child) throws Exception {
        for (Client parent : parents) {
            boolean guardianFound = isRelatives(session, parent, child);
            if (guardianFound) return true;
        }
        return false;
    }

    private boolean childHasAnActiveSmartWatch(Session session, Client child) throws Exception {
        List<Card> cards = new LinkedList<Card>(child.getCards());
        for(Card card : cards){
            if(Card.TYPE_NAMES[card.getCardType()].equals("Часы (Mifare)")
                    && !(card.getState().equals(CardState.BLOCKED.getValue())) || card.getState().equals(CardState.TEMPBLOCKED.getValue())){
                SmartWatch watch = DAOUtils.findSmartWatchByTrackerUidAndTrackerId(session, card.getCardPrintedNo(), card.getCardNo());
                if(watch == null){
                    logger.warn("Client (contractID: " + child.getContractId() + ") have active card types \"SmartWatch\", but no SmartWatch Entity");
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private List<JsonChildrenDataInfoItem> buildChildrenDataInfoItems(Session session, List<Client> guardians) throws Exception{
        List<JsonChildrenDataInfoItem> resultList = new LinkedList<JsonChildrenDataInfoItem>();
        Set<Client> uniqueChildren = new HashSet<>();
        try{
            List<ClientGuardian> childes = DAOUtils.findListOfClientGuardianByIdOfGuardian(session, guardians);
            for(ClientGuardian el : childes){
                Client child = (Client) session.get(Client.class, el.getIdOfChildren());
                if (uniqueChildren.contains(child)) continue;
                uniqueChildren.add(child);
                JsonChildrenDataInfoItem item = new JsonChildrenDataInfoItem();

                item.setContractID(child.getContractId());
                if(child.getPerson() != null) {
                    item.setFirsName(child.getPerson().getFirstName());
                    item.setSecondName(child.getPerson().getSecondName());
                    item.setSurName(child.getPerson().getSurname());
                }
                item.setGroupName(child.getClientGroup().getGroupName());

                Query query = session.createQuery("FROM Card card"
                        + " WHERE card.client = :client AND (card.state = :activeState "
                        + " OR card.updateTime = (SELECT MAX(updateTime) "
                        + " FROM Card card "
                        + " WHERE card.client = :client AND card.cardType = :smartWatchType))");
                query.setParameter("client", child)
                     .setParameter("activeState", CardState.ISSUED.getValue())
                     .setParameter("smartWatchType", CARD_TYPE_SMARTWATCH);

                List<Card> cards = query.list();

                for(Card card : cards) {
                    if (card != null) {
                        JsonCardInfo info = new JsonCardInfo();
                        info.setCardPrintedNo(card.getCardPrintedNo());
                        info.setCardNo(card.getCardNo());
                        info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
                        info.setLifeState(Card.LIFE_STATE_NAMES[card.getLifeState()]);
                        info.setState(this.cardState.get(card.getState()));
                        item.getCardInfo().add(info);
                    }
                }
                resultList.add(item);
            }
            return resultList;
        }catch (Exception e) {
            logger.error("", e);
            throw e;
        }
    }

    private boolean isValidPhoneAndToken(Session session, String mobilePhone, String token) throws Exception {
        try {
            LinkingTokenForSmartWatch linkingToken = DAOUtils.findLinkingTokenForSmartWatch(session, mobilePhone, token);
            if (linkingToken == null) {
                logger.warn("There are no matches of the \"mobilePhone number - code\" pair");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        }
    }

    private String checkAndConvertPhone(String mobilePhone) throws Exception{
        if (mobilePhone == null || mobilePhone.length() == 0) {
            logger.warn("Invalid mobilePhone number: is null or is empty");
            return null;
        }
        mobilePhone = mobilePhone.replaceAll("[+ \\-()]", "");
        if (mobilePhone.startsWith("8")) {
            mobilePhone = "7" + mobilePhone.substring(1);
        }
        if (mobilePhone.length() == 10) {
            mobilePhone = "7" + mobilePhone;
        } else if (mobilePhone.length() != 11) {
            logger.warn("Invalid mobilePhone number: length of mobilePhone number must have 11 characters");
            return null;
        }
        return mobilePhone;
    }

    private boolean isGuardian(Session session, List<Client> clients) throws Exception {
        try {
            List<ClientGuardian> dataFromDB = DAOUtils.findListOfClientGuardianByIdOfGuardian(session, clients);
            if (dataFromDB == null || dataFromDB.isEmpty()) {
                logger.warn("No data about Guardians by Clients with contractID: ");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        }
    }

    private <T extends IJsonBase> Response resultBadArgs(T result, IllegalArgumentException e) {
        result.getResult().resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
        result.getResult().description = debug ? e.getMessage() : ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
        return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                .entity(result)
                .build();
    }

    private Response resultBadArgs(Result result, IllegalArgumentException e) {
        result.resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
        result.description = debug ? e.getMessage() : ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
        return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                .entity(result)
                .build();
    }

    private <T extends IJsonBase> Response resultException(T result, Exception e) {
        result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
        result.getResult().description = debug ? e.getMessage() : ResponseCodes.RC_INTERNAL_ERROR.toString();
        return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .entity(result)
                .build();
    }

    private Response resultException(Result result, Exception e) {
        result.resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
        result.description = debug ? e.getMessage() : ResponseCodes.RC_INTERNAL_ERROR.toString();
        return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .entity(result)
                .build();
    }

    private <T extends IJsonBase> Response resultOK(T result) {
        result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
        result.getResult().description = ResponseCodes.RC_OK.toString();
        return Response.status(HttpURLConnection.HTTP_OK)
                .entity(result)
                .build();
    }

    private Response resultOK(Result result) {
        result.resultCode = ResponseCodes.RC_OK.getCode();
        result.description = ResponseCodes.RC_OK.toString();
        return Response.status(HttpURLConnection.HTTP_OK)
                .entity(result)
                .build();
    }

    private String checkMobilePhone(Session session, String mobilePhone, String token) throws Exception {
        mobilePhone = checkAndConvertPhone(mobilePhone);
        if(!isValidPhoneAndToken(session, mobilePhone, token)){
            throw new IllegalArgumentException("Invalid token and mobilePhone number, mobilePhone: " + mobilePhone );
        }
        return mobilePhone;
    }

    private List<Client> findParentsByMobile(String mobilePhone) throws Exception {
        List<Client> parents = DAOService.getInstance().getClientsListByMobilePhone(mobilePhone);
        if(parents.isEmpty()){
            throw new Exception("No clients found for this mobilePhone number: " + mobilePhone
                    + ", but passed the TokenValidator");
        }
        return parents;
    }

    private JsonPurchaseDetailItem fillPurchaseDetailItem(Order order) {
        JsonPurchaseDetailItem detailItem = new JsonPurchaseDetailItem();
        detailItem.setOrderDate(order.getOrderDate());
        detailItem.setOrderType(order.getOrderType().toString());
        detailItem.setrSum(order.getRSum());
        detailItem.setSocDiscount(order.getSocDiscount());
        detailItem.setTrdDiscount(order.getTrdDiscount());
        detailItem.setGrantSum(order.getGrantSum());
        detailItem.setSumByCard(order.getSumByCard());
        detailItem.setSumByCash(order.getSumByCash());
        for (OrderDetail orderDetail : order.getOrderDetails()){
            JsonOrderDetail jsonOrderDetail = new JsonOrderDetail();
            jsonOrderDetail.setGoodName(orderDetail.getMenuDetailName());
            jsonOrderDetail.setrPrice(orderDetail.getRPrice());
            jsonOrderDetail.setQty(orderDetail.getQty());
            detailItem.getOrderDetails().add(jsonOrderDetail);
        }
        return detailItem;
    }
}
