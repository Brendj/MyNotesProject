/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;



import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.ui.card.CardLockReason;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.*;


@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Controller
public class SmartWatchRestController {
    private Logger logger = LoggerFactory.getLogger(SmartWatchRestController.class);
    private Map<Integer, String> cardState;
    private Long DEFAULT_SMART_WATCH_VALID_TIME = 157766400000L; // 5 year
    private boolean debug;
    private final Integer CARD_TYPE_SMARTWATCH = Arrays.asList(Card.TYPE_NAMES).indexOf("Часы (Mifare)");

    public SmartWatchRestController(){
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
    public Response sendLinkingTokenByMobile(@FormParam(value="mobilePhone") String mobilePhone)throws Exception{
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
            Client client = DAOService.getInstance().getClientByMobilePhone(mobilePhone);
            if(client == null){
                throw new IllegalArgumentException("No clients found for this mobilePhone number: " + mobilePhone);
            }
            if(!isGuardian(session, client)){
                throw new IllegalArgumentException("Client with contractID: " + client.getContractId()
                        + ", found by mobilePhone number: " + mobilePhone
                        + ", is not a guardian");
            }

            String token = DAOService.getInstance().generateLinkingTokenForSmartWatch(session, mobilePhone);
            String message = "Код безопасности: " + token;

            RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                    .sendMessageAsync(client, EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                            new String[]{"linkingToken", message}, new Date());

            token = "";
            message = "";

            transaction.commit();
            transaction = null;

            result.resultCode = ResponseCodes.RC_OK.getCode();
            result.description = "Код активации отправлен по SMS" +
                    (client.hasEmail()? " и по Email" : "");
            return Response.status(HttpURLConnection.HTTP_OK)
                    .entity(result)
                    .build();
        } catch (IllegalArgumentException e){
            logger.error("Can't generate or send token :", e);
            result.resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.description = debug ? e.getMessage() : ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } catch (Exception e){
            logger.error("Can't generate or send token :", e);
            result.resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.description = debug ? e.getMessage() : ResponseCodes.RC_INTERNAL_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value = "getListInfoOfChildrens")
    public Response getListOfChildrenByPhoneAndToken(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token)throws Exception{
        JsonListInfo result = new JsonListInfo();
        Session session = null;
        Transaction transaction = null;
        try{
            if(mobilePhone == null || mobilePhone.isEmpty()){
                throw new IllegalArgumentException("Invalid mobilePhone number: is null or is empty");
            }

            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkAndConvertPhone(mobilePhone);
            if(!isValidPhoneAndToken(session, mobilePhone, token)){
                throw new IllegalArgumentException("Invalid token and mobilePhone number, mobilePhone: " + mobilePhone );
            }
            token = "";

            Client parent = DAOService.getInstance().getClientByMobilePhone(mobilePhone);
            if(parent == null){
                throw new Exception("No clients found for this mobilePhone number: " + mobilePhone
                            + ", but passed the TokenValidator");
            }

            List<JsonChildrenDataInfoItem> items = buildChildrenDataInfoItems(session, parent);
            result.setItems(items);

            transaction.commit();
            transaction = null;

            result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
            result.getResult().description = ResponseCodes.RC_OK.toString();
            return Response.status(HttpURLConnection.HTTP_OK)
                    .entity(result)
                    .build();
        } catch (IllegalArgumentException e){
            logger.error("Can't get List Of Children's", e);
            result.getResult().resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.getResult().description = debug ? e.getMessage() : ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } catch (Exception e){
            logger.error("Can't get List Of Children's", e);
            result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.getResult().description = debug ? e.getMessage() : ResponseCodes.RC_INTERNAL_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
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
            @FormParam(value="trackerActivateTime") Long trackerActivateTime, @FormParam(value="simIccid") String simIccid) throws Exception{
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = inputParamsIsValidOrTrowException(session, trackerId, trackerUid, mobilePhone, token);

            if(contractId == null){
                throw new IllegalArgumentException("ContractID is null");
            }

            Client parent = DAOService.getInstance().getClientByMobilePhone(mobilePhone);
            if(parent == null){
                throw new Exception("No clients found by this mobilePhone number: " + mobilePhone
                        + ", but passed the TokenValidator");
            }
            Client child = DAOUtils.findClientByContractId(session, contractId);
            if(child == null){
                throw new IllegalArgumentException("No clients found by contractID: " + contractId);
            }
            if(!isRelatives(session, parent, child)){
                throw new IllegalArgumentException("Parent (contractID: " + parent.getContractId() + ") and Child (contractID: " + child.getContractId() + ") is not relatives");
            }

            if(childHasAnActiveSmartWatch(session, child)){
                throw new IllegalArgumentException("The client witch contractID: " + child.getContractId() + " has an active SmartWatch");
            }

            Date issueTime = new Date();
            Date validTime = new Date(issueTime.getTime() + this.DEFAULT_SMART_WATCH_VALID_TIME);

            CardManager cardManager = RuntimeContext.getInstance().getCardManager();

            Card card = DAOUtils.findCardByCardNo(session, trackerUid);
            Long idOfCard = null;
            if(card == null) {
                idOfCard = cardManager
                        .createSmartWatchAsCard(session, child.getIdOfClient(), trackerId, Card.ACTIVE_STATE, validTime,
                                Card.ISSUED_LIFE_STATE, null, issueTime, trackerUid, null);
            } else {
                if((card.getClient() == null || card.getClient().equals(child) || card.getState().equals(CardState.BLOCKED.getValue()))
                        && card.getCardType().equals(CARD_TYPE_SMARTWATCH)) {
                    cardManager.updateCard(child.getIdOfClient(), card.getIdOfCard(), card.getCardType(), CardState.ISSUED.getValue(), card.getValidTime(), card.getLifeState(),
                            CardLockReason.OTHER.getDescription(), card.getIssueTime(), card.getExternalId(), null, child.getOrg().getIdOfOrg());
                } else {
                    throw new Exception("Card CardNo: " + card.getCardNo() + " is registered and owned Client contractID: " + card.getClient().getContractId());
                }
                idOfCard = card.getIdOfCard();
            }

            child.setHasActiveSmartWatch(true);

            Date trackerActivateTimeDate = trackerActivateTime == null ? new Date() : new Date(trackerActivateTime);
            SmartWatch watch = DAOUtils.findSmartWatchByTrackerUidAndTrackerId(session, trackerId, trackerUid);
            if(watch == null) {
                DAOUtils.createSmartWatch(session, idOfCard, child.getIdOfClient(), model, color,
                        trackerUid, trackerId, trackerActivateUserId, status, trackerActivateTimeDate, simIccid);
            } else {
                this.updateSmartWatch(watch, session, idOfCard, child.getIdOfClient(), color, model,
                        trackerUid, trackerId, trackerActivateUserId, status, trackerActivateTimeDate, simIccid);
            }

            blockActiveCards(child, idOfCard);
            session.update(child);

            transaction.commit();
            transaction = null;

            result.resultCode = ResponseCodes.RC_OK.getCode();
            result.description = ResponseCodes.RC_OK.toString();
            return Response.status(HttpURLConnection.HTTP_OK)
                    .entity(result)
                    .build();
        } catch (IllegalArgumentException e){
            logger.error("Can't registry SmartWatch ", e);
            result.resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.description = debug ? e.getMessage() : ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } catch (Exception e){
            logger.error("Can't registry SmartWatch ", e);
            result.resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.description = debug ? e.getMessage() : ResponseCodes.RC_INTERNAL_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }


    @POST
    @Path(value = "blockSmartWatch")
    public Response blockSmartWatch(@FormParam(value="mobilePhone") String mobilePhone, @FormParam(value="token") String token,
            @FormParam(value="trackerUid") Long trackerUid, @FormParam(value="trackerID") Long trackerId)throws Exception{
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
            session.update(child);

            transaction.commit();
            transaction = null;

            result.resultCode = ResponseCodes.RC_OK.getCode();
            result.description = ResponseCodes.RC_OK.toString();
            return Response.status(HttpURLConnection.HTTP_OK)
                    .entity(result)
                    .build();
        } catch (IllegalArgumentException e){
            logger.error("Can't block SmartWatch", e);
            result.resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.description = debug ? e.getMessage() : ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } catch (Exception e){
            logger.error("Can't block SmartWatch", e);
            result.resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.description = debug ? e.getMessage() : ResponseCodes.RC_INTERNAL_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value="getEnterEvents")
    public Response getEnterEvents(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="contractId") Long contractId,
            @QueryParam(value="startDate") Long startDateTime, @QueryParam(value="endDate") Long endDateTime){
        JsonEnterEvents result = new JsonEnterEvents();
        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkAndConvertPhone(mobilePhone);
            if(!isValidPhoneAndToken(session, mobilePhone, token)){
                throw new IllegalArgumentException("Invalid token and mobilePhone number, mobilePhone: " + mobilePhone );
            }
            token = "";

            if(contractId == null){
                throw new IllegalArgumentException("ContractID is null");
            }

            Client parent = DAOService.getInstance().getClientByMobilePhone(mobilePhone);
            if(parent == null){
                throw new Exception("No clients found for this mobilePhone number: " + mobilePhone
                        + ", but passed the TokenValidator");
            }

            Client child = DAOUtils.findClientByContractId(session, contractId);
            if(child == null){
                throw new IllegalArgumentException("No clients found by contractID: " + contractId);
            }
            if(!isRelatives(session, parent, child)){
                throw new IllegalArgumentException("Parent (contractID: " + parent.getContractId() + ") and Child (contractID: " + child.getContractId() + ") is not relatives");
            }

            Date startDate;
            Date endDate;

            if(startDateTime == null){
                logger.warn("Start date is Null, set as yesterday");
                startDate = CalendarUtils.startOfDay(CalendarUtils.addDays(new Date(), -1));
            } else {
                startDate = CalendarUtils.startOfDay(new Date(startDateTime));
            }

            if(endDateTime == null){
                logger.warn("End date is Null, set as now");
                endDate = CalendarUtils.endOfDay(new Date());
            } else {
                endDate = CalendarUtils.endOfDay(new Date(endDateTime));
            }

            List<JsonEnterEventItem> items = buildEnterEventItem(session, child, startDate, endDate);
            result.setItems(items);
            result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
            result.getResult().description = ResponseCodes.RC_OK.toString();

            transaction.commit();
            transaction = null;

            return Response.status(HttpURLConnection.HTTP_OK)
                    .entity(result)
                    .build();
        } catch (IllegalArgumentException e){
            logger.error("Can't get EnterEvents ", e);
            result.getResult().resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.getResult().description = debug ? e.getMessage() : ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } catch (Exception e){
            logger.error("Can't get EnterEvents ", e);
            result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.getResult().description = debug ? e.getMessage() : ResponseCodes.RC_INTERNAL_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @GET
    @Path(value="getPurchases")
    public Response getPurchases(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token, @QueryParam(value="contractId") Long contractId,
            @QueryParam(value="startDate") Long startDateTime, @QueryParam(value="endDate") Long endDateTime){
        Session session = null;
        Transaction transaction = null;
        JsonPurchases result = new JsonPurchases();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            mobilePhone = checkAndConvertPhone(mobilePhone);
            if (!isValidPhoneAndToken(session, mobilePhone, token)) {
                throw new IllegalArgumentException("Invalid token and mobilePhone number, mobilePhone: " + mobilePhone);
            }
            token = "";

            if (contractId == null) {
                throw new IllegalArgumentException("ContractID is null");
            }

            Client parent = DAOService.getInstance().getClientByMobilePhone(mobilePhone);
            if (parent == null) {
                throw new Exception("No clients found for this mobilePhone number: " + mobilePhone
                        + ", but passed the TokenValidator");
            }

            Client child = DAOUtils.findClientByContractId(session, contractId);
            if (child == null) {
                throw new IllegalArgumentException("No clients found by contractID: " + contractId);
            }
            if (!isRelatives(session, parent, child)) {
                throw new IllegalArgumentException(
                        "Parent (contractID: " + parent.getContractId() + ") and Child (contractID: " + child.getContractId() + ") is not relatives");
            }

            Date startDate;
            Date endDate;

            if(startDateTime == null){
                logger.warn("Start date is Null, set as yesterday");
                startDate = CalendarUtils.startOfDay(CalendarUtils.addDays(new Date(), -1));
            } else {
                startDate = CalendarUtils.startOfDay(new Date(startDateTime));
            }

            if(endDateTime == null){
                logger.warn("End date is Null, set as now");
                endDate = CalendarUtils.endOfDay(new Date());
            } else {
                endDate = CalendarUtils.endOfDay(new Date(endDateTime));
            }

            List<JsonOrder> items = buildPaymentsInfo(session, child, startDate, endDate);
            result.setItems(items);
            result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
            result.getResult().description = ResponseCodes.RC_OK.toString();

            transaction.commit();
            transaction = null;

            return Response.status(HttpURLConnection.HTTP_OK)
                    .entity(result)
                    .build();
        } catch (IllegalArgumentException e){
            logger.error("Can't get Purchases ", e);
            result.getResult().resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.getResult().description = debug ? e.getMessage() : ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } catch (Exception e){
            logger.error("Can't get Purchases  ", e);
            result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.getResult().description = debug ? e.getMessage() : ResponseCodes.RC_INTERNAL_ERROR.toString();
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build();
        } finally{
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private List<JsonOrder> buildPaymentsInfo(Session session, Client child, Date startDate, Date endDate) throws Exception{
        List<JsonOrder> items = new LinkedList<JsonOrder>();
        List<Order> ordersOfClient = null;

        Criteria criteria = session.createCriteria(Order.class);
        criteria.add(Restrictions.eq("client", child));
        criteria.add(Restrictions.between("orderDate", startDate, endDate));
        criteria.addOrder(org.hibernate.criterion.Order.desc("orderDate"));

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

    private List<JsonEnterEventItem> buildEnterEventItem(Session session, Client child, Date startDate, Date endDate) throws Exception{
        List<JsonEnterEventItem> items = new LinkedList<JsonEnterEventItem>();
        List<Long> cardNoOfOwner = new LinkedList<Long>();
        List<EnterEvent> events = null;

        for(Card card : child.getCards()){
            cardNoOfOwner.add(card.getCardNo());
        }

        Criteria criteria = session.createCriteria(EnterEvent.class);
        Criterion cardNoRestriction = Restrictions.in("idOfCard", cardNoOfOwner);
        Criterion clientRestriction = Restrictions.eq("client", child);
        criteria.add(Restrictions.or(cardNoRestriction, clientRestriction));
        criteria.add(Restrictions.between("evtDateTime", startDate, endDate));
        criteria.addOrder(org.hibernate.criterion.Order.desc("evtDateTime"));

        events = criteria.list();
        if(events == null || events.isEmpty()){
            logger.warn("Not found event for client contractID: " + child.getContractId());
            return Collections.emptyList();
        }

        for(EnterEvent event : events){
            JsonEnterEventItem item = new JsonEnterEventItem();
            item.setDirection(event.getPassDirection());
            item.setEvtDateTime(event.getEvtDateTime());
            if(event.getIdOfCard() != null){
                Card card = findClientCardByCardNo(child.getCards(), event.getIdOfCard());
                if(card != null) {
                    item.setCardType(Card.TYPE_NAMES[card.getCardType()]);
                }
            }
            if(event.getClient() != null){
                item.setClient(child.getPerson().getFullName());
            }
            items.add(item);
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
            String model, Long trackerUid, Long trackerId, Long trackerActivateUserId,
            String status, Date trackerActivateTimeDate, String simIccid) {
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
                            CardLockReason.OTHER.getDescription(), card.getIssueTime(), card.getExternalId());
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
        ClientGuardian clientGuardian = DAOUtils.findClientGuardian(session, child.getIdOfClient(), parent.getIdOfClient());
        return clientGuardian != null;
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

    private List<JsonChildrenDataInfoItem> buildChildrenDataInfoItems(Session session, Client client) throws Exception{
        List<JsonChildrenDataInfoItem> resultList = new LinkedList<JsonChildrenDataInfoItem>();
        try{
            List<ClientGuardian> childrens = DAOUtils.findListOfClientGuardianByIdOfGuardian(session, client.getIdOfClient());
            for(ClientGuardian el : childrens){
                Client child = (Client) session.get(Client.class, el.getIdOfChildren());
                Card card = DAOUtils.getLastCardByClient(session, client);
                JsonChildrenDataInfoItem item = new JsonChildrenDataInfoItem();
                item.setContractID(child.getContractId());
                if(child.getPerson() != null) {
                    item.setFirsName(child.getPerson().getFirstName());
                    item.setSecondName(child.getPerson().getSecondName());
                    item.setSurName(child.getPerson().getSurname());
                }
                item.setGroupName(child.getClientGroup().getGroupName());
                JsonCardInfo info = new JsonCardInfo();
                info.setCardPrintedNo(card.getCardPrintedNo());
                info.setCardNo(card.getCardNo());
                info.setCardType(Card.TYPE_NAMES[card.getCardType()]);
                info.setLifeState(Card.LIFE_STATE_NAMES[card.getLifeState()]);
                info.setState(this.cardState.get(card.getState()));
                item.setCardInfo(info);
                resultList.add(item);
            }
            return resultList;
        }catch (Exception e) {
            logger.error(e.getMessage());
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

    private boolean isGuardian(Session session, Client client) throws Exception {
        try {
            List<ClientGuardian> dataFromDB = DAOUtils.findListOfClientGuardianByIdOfGuardian(session, client.getIdOfClient());
            if (dataFromDB == null || dataFromDB.isEmpty()) {
                logger.warn("No data about Guardians by Clients with contractID: " + client.getContractId());
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("", e);
            throw e;
        }
    }
}
