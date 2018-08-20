/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;


import com.sun.istack.NotNull;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.ui.card.CardLockReason;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.*;


@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class SmartWatchRestController {
    private Logger logger = LoggerFactory.getLogger(SmartWatchRestController.class);
    private Map<Integer, String> cardState;
    private Long DEFAULT_SMART_WATCH_VALID_TIME = 157766400000L; // 5 year

    public SmartWatchRestController(){
        this.cardState = new HashMap<Integer, String>();
        for (CardState state : CardState.values()) {
            this.cardState.put(state.getValue(), state.getDescription());
        }
    }

    @POST
    @Path(value = "getTokenByMobile")
    @Transactional(rollbackFor = Exception.class)
    public  Result sendLinkingTokenByMobile(@QueryParam(value="mobilePhone") String mobilePhone)throws Exception{
        Result result = new Result();
        Session session = null;
        try {
            if(mobilePhone == null || mobilePhone.isEmpty()){
                throw new IllegalArgumentException("Invalid mobilePhone number: is null or is empty");
            }
            session = RuntimeContext.getInstance().createPersistenceSession();
            mobilePhone = checkAndConvertPhone(mobilePhone);
            Client client = DAOService.getInstance().getClientByMobilePhone(mobilePhone);
            if(client == null){
                throw new IllegalArgumentException("No clients found for this mobilePhone number: " + mobilePhone);
            }
            if(!isGuardian(session, client)){
                throw new Exception("Client with contractID: " + client.getContractId()
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

            result.resultCode = ResponseCodes.RC_OK.getCode();
            result.description = "Код активации отправлен по SMS" +
                    (client.hasEmail()? " и по Email" : "");
            return result;
        } catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            result.resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.description = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build());
        } catch (Exception e){
            logger.error(e.getMessage());
            result.resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.description = ResponseCodes.RC_INTERNAL_ERROR.toString();
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .entity(result)
                    .build());
        }
    }

    @GET
    @Path(value = "getListInfoOfChildrens")
    @Transactional
    public JsonListInfo getListOfChildrenByPhoneAndToken(@QueryParam(value="mobilePhone") String mobilePhone,
            @QueryParam(value="token") String token)throws Exception{
        JsonListInfo result = new JsonListInfo();
        Session session = null;
        try{
            if(mobilePhone == null || mobilePhone.isEmpty()){
                throw new IllegalArgumentException("Invalid mobilePhone number: is null or is empty");
            }

            session = RuntimeContext.getInstance().createReportPersistenceSession();
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

            List<JsonChildrenDataInfoItem> items = buildItems(session, parent);
            result.setItems(items);

            result.getResult().resultCode = ResponseCodes.RC_OK.getCode();
            result.getResult().description = ResponseCodes.RC_OK.toString();
            return result;
        } catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            result.getResult().resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.getResult().description = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build());
        } catch (Exception e){
            logger.error(e.getMessage());
            result.getResult().resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.getResult().description = ResponseCodes.RC_INTERNAL_ERROR.toString();
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .entity(result)
                    .build());
        }
    }

    @POST
    @Path(value = "registrySmartWatch")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public Result registrySmartWatch(@QueryParam(value="mobilePhone") String mobilePhone, @QueryParam(value="token") String token,
            @NotNull @QueryParam(value="contractId") Long contractId, @QueryParam(value="model") String model, @QueryParam(value="color") String color,
            @NotNull @QueryParam(value="trackerUid") Long trackerUid, @NotNull @QueryParam(value="trackerID") Long trackerId,
            @QueryParam(value="trackerActivateUserId") Long trackerActivateUserId, @QueryParam(value="status") Integer status,
            @QueryParam(value="trackerActivateTime") Long trackerActivateTime, @QueryParam(value="simIccid") String simIccid) throws Exception{
        Result result = new Result();
        Session session = null;
        try {
            if(mobilePhone == null || mobilePhone.isEmpty()){
                throw new IllegalArgumentException("Invalid mobilePhone number: is null or is empty");
            }
            session = RuntimeContext.getInstance().createPersistenceSession();
            mobilePhone = checkAndConvertPhone(mobilePhone);
            if(!isValidPhoneAndToken(session, mobilePhone, token)){
                throw new IllegalArgumentException("Invalid token and mobilePhone number, mobilePhone: " + mobilePhone );
            }
            token = "";

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

            blockActiveCards(child);
            CardManager cardManager = RuntimeContext.getInstance().getCardManager();
            Long idOfCard = cardManager.createSmartWatchAsCard(session, child.getIdOfClient(), trackerId, Card.ACTIVE_STATE,
                    validTime, Card.ISSUED_LIFE_STATE, null, issueTime, trackerUid, null);

            child.setHasActiveSmartWatch(true);
            session.update(child);

            SmartWatch watch = DAOUtils.findSmartWatchByTrackerUidAndTrackerId(session, trackerId, trackerUid);
            if(watch == null) {
                Date trackerActivateTimeDate = null;
                if(trackerActivateTime == null){
                    trackerActivateTimeDate = new Date();
                } else {
                    trackerActivateTimeDate = new Date(trackerActivateTime);
                }
                DAOUtils.createSmartWatch(session, idOfCard, child.getIdOfClient(), model, color,
                        trackerUid, trackerId, trackerActivateUserId, status, trackerActivateTimeDate, simIccid);
            } else {
                this.updateSmartWatch(watch, session, idOfCard, child.getIdOfClient());
            }
            result.resultCode = ResponseCodes.RC_OK.getCode();
            result.description = ResponseCodes.RC_OK.toString();
            return result;
        } catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            result.resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.description = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                                .entity(result)
                                .build());
        } catch (Exception e){
            logger.error(e.getMessage());
            result.resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.description = ResponseCodes.RC_INTERNAL_ERROR.toString();
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .entity(result)
                    .build());
        }
    }

    @POST
    @Path(value = "blockSmartWatch")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public Result blockSmartWatch(@QueryParam(value="mobilePhone") String mobilePhone, @QueryParam(value="token") String token,
            @NotNull @QueryParam(value="trackerUid") Long trackerUid, @NotNull @QueryParam(value="trackerID") Long trackerId)throws Exception{
        Result result = new Result();
        Session session = null;
        try {
            if(mobilePhone == null || mobilePhone.isEmpty()){
                throw new IllegalArgumentException("Invalid mobilePhone number: is null or is empty");
            }
            session = RuntimeContext.getInstance().createPersistenceSession();
            mobilePhone = checkAndConvertPhone(mobilePhone);
            if(!isValidPhoneAndToken(session, mobilePhone, token)){
                throw new IllegalArgumentException("Invalid token and mobilePhone number, mobilePhone: " + mobilePhone );
            }
            token = "";

            Client parent = DAOService.getInstance().getClientByMobilePhone(mobilePhone);
            SmartWatch watch = DAOUtils.findSmartWatchByTrackerUidAndTrackerId(session, trackerId, trackerUid);
            if(watch == null){
                throw new IllegalArgumentException("No SmartWatch found by trackerUid: " + trackerUid + " and trackerId: " + trackerId);
            }

            Client child = (Client) session.get(Client.class, watch.getIdOfClient());

            if(!isRelatives(session, parent, child)){
                throw new IllegalArgumentException("Parent (contractID: " + parent.getContractId() + ") and Child (contractID: " + child.getContractId() + ") is not relatives");
            }

            Card card = (Card) session.get(Card.class, watch.getIdOfCard());

            if(card.getState().equals(CardState.BLOCKED.getValue())){
                throw new IllegalArgumentException("SmartWatch already blocked");
            }

            blockActiveCard(child, card);
            child.setHasActiveSmartWatch(false);
            session.update(child);

            result.resultCode = ResponseCodes.RC_OK.getCode();
            result.description = ResponseCodes.RC_OK.toString();
            return result;
        } catch (IllegalArgumentException e){
            logger.error(e.getMessage());
            result.resultCode = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode();
            result.description = ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString();
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(result)
                    .build());
        } catch (Exception e){
            logger.error(e.getMessage());
            result.resultCode = ResponseCodes.RC_INTERNAL_ERROR.getCode();
            result.description = ResponseCodes.RC_INTERNAL_ERROR.toString();
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .entity(result)
                    .build());
        }
    }

    private void updateSmartWatch(SmartWatch watch, Session session, Long idOfCard, Long idOfClient) throws Exception {
        watch.setIdOfClient(idOfClient);
        watch.setIdOfCard(idOfCard);
        session.update(watch);
    }

    private void blockActiveCard(Client client, Card card) throws Exception {
        if (card.getState() == Card.ACTIVE_STATE) {
            RuntimeContext.getInstance().getCardManager()
                    .updateCard(client.getIdOfClient(), card.getIdOfCard(), card.getCardType(),
                            CardState.BLOCKED.getValue(), card.getValidTime(), card.getLifeState(),
                            CardLockReason.OTHER.getDescription(), card.getIssueTime(), card.getExternalId());
        }
    }

    private void blockActiveCards(Client child) throws Exception{
        Set<Card> cardSet = child.getCards();
        for(Card card : cardSet) {
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

    private List<JsonChildrenDataInfoItem> buildItems(Session session, Client client) throws Exception{
        List<JsonChildrenDataInfoItem> resultList = new LinkedList<JsonChildrenDataInfoItem>();
        try{
            List<ClientGuardian> childrens = DAOUtils.findListOfClientGuardianByIdOfGuardian(session, client.getIdOfClient());
            for(ClientGuardian el : childrens){
                Client child = (Client) session.get(Client.class, el.getIdOfChildren());
                List<Card> cardList = new LinkedList<Card>(child.getCards());
                JsonChildrenDataInfoItem item = new JsonChildrenDataInfoItem();
                item.setContractID(child.getContractId());
                if(child.getPerson() != null) {
                    item.setFirsName(child.getPerson().getFirstName());
                    item.setSecondName(child.getPerson().getSecondName());
                    item.setSurName(child.getPerson().getSurname());
                }
                item.setGroupName(child.getClientGroup().getGroupName());
                for(Card card : cardList){
                    if(Card.TYPE_NAMES[card.getCardType()].equals("Часы (Mifare)")) {
                        JsonSmartWatchInfo info = new JsonSmartWatchInfo();
                            info.setTrackerId(card.getCardPrintedNo());
                            info.setTrackerUid(card.getCardNo());
                            info.setLifeState(Card.LIFE_STATE_NAMES[card.getLifeState()]);
                            info.setState(this.cardState.get(card.getState()));
                            item.getSmartWatchInfoList().add(info);
                    }
                }
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
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
            throw e;
        }
    }

}
