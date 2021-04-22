/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RequestFeedingProcessor extends AbstractProcessor<ResRequestFeeding> {

    private static final Logger logger = LoggerFactory.getLogger(RequestFeedingProcessor.class);
    private final RequestFeeding requestFeeding;

    public RequestFeedingProcessor(Session session, RequestFeeding requestFeeding) {
        super(session);
        this.requestFeeding = requestFeeding;
    }

    @Override
    public ResRequestFeeding process()
    {
        List<ResRequestFeedingItem> resItems = new ArrayList<>();
        List<ResRequestFeedingETPStatuses> etpStatuses = new ArrayList<>();
        try
        {
            long nextVersion = DAOUtils.nextVersionByApplicationForFood(session);
            long nextHistoryVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);

            for (RequestFeedingItem item : requestFeeding.getItems())
            {
                if (item.getResCode().equals(RequestFeedingItem.ERROR_CODE_ALL_OK) == false)
                {
                    resItems.add(createErrorItem(item));
                    continue;
                }
                Client client = (Client) session.load(Client.class, item.getIdOfClient());
                if (client == null)
                {
                    logger.error(String.format("Client with id=%d not found", item.getIdOfClient()));
                    continue;
                }

                Map.Entry<ResRequestFeedingItem, List<ResRequestFeedingETPStatuses>> result = proceedOneItem(item, nextVersion, nextHistoryVersion, client);
                resItems.add(result.getKey());
                etpStatuses.addAll(result.getValue());
            }
        }
        catch (Exception e)
        {
            logger.error("Error saving RequestFeeding", e);
            return null;
        }
        return new ResRequestFeeding(resItems, etpStatuses);
    }

    public Map.Entry<ResRequestFeedingItem, List<ResRequestFeedingETPStatuses>> proceedOneItem(RequestFeedingItem item, long nextVersion, long nextHistoryVersion, Client client)
    {
        List<ResRequestFeedingETPStatuses> etpStatuses = new ArrayList<>();

        ApplicationForFood applicationForFood = DAOUtils.findApplicationForFoodByClientIdAndRegDate(session, item.getIdOfClient(), item.getApplicationCreatedDate());

        ApplicationForFoodStatus newStatus = new ApplicationForFoodStatus(ApplicationForFoodState.fromCode(item.getStatus()), ApplicationForFoodDeclineReason.fromInteger(item.getDeclineReason()));

        if (applicationForFood == null)
        {
            try
            {
                applicationForFood = DAOUtils.createApplicationForFood(session, client, item.getDtisznCode(), item.getApplicantPhone(), item.getApplicantName(), item.getApplicantSecondName(), item.getApplicantSurname(), item.getServNumber(), ApplicationForFoodCreatorType.PORTAL);
                etpStatuses.add(new ResRequestFeedingETPStatuses(applicationForFood, applicationForFood.getStatus()));
                ApplicationForFoodStatus st = new ApplicationForFoodStatus(ApplicationForFoodState.REGISTERED, null);
                applicationForFood = DAOUtils.updateApplicationForFoodByServiceNumber(session, item.getServNumber(), st);
                etpStatuses.add(new ResRequestFeedingETPStatuses(applicationForFood, st));
            }
            catch (Exception e)
            {
                logger.error(String.format("Unable to create application for food {idOfClient=%d, DTSZNCode=%d, applicantPhone=%s, applicantName=%s, applicantSecondName=%s, applicantSurname=%s, serviceNumber=%s}", item.getIdOfClient(), item.getDtisznCode(), item.getApplicantPhone(), item.getApplicantName(), item.getApplicantSecondName(), item.getApplicantSurname(), item.getServNumber()), e);
                return new AbstractMap.SimpleEntry<>(ResRequestFeedingItem.error(RequestFeedingItem.ERROR_CODE_INTERNAL_ERROR, "Error in processing entity: " + e.getMessage()), etpStatuses);
            }
        }
        else
        {
            try
            {
                ApplicationForFoodStatus oldStatus = applicationForFood.getStatus();
                if (applicationForFood.getDtisznCode() == null && newStatus.getApplicationForFoodState().equals(ApplicationForFoodState.OK) && !oldStatus.equals(newStatus) )
                {
                    //если Иное и новый статус 1075, то искусственно создаем статус 1052
                    DAOUtils.addApplicationForFoodHistoryWithVersionIfNotExist(session, applicationForFood, new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING, null), nextHistoryVersion);
                    etpStatuses.add(new ResRequestFeedingETPStatuses(applicationForFood, new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING, newStatus.getDeclineReason())));
                    if (CalendarUtils.betweenDate(new Date(), item.getOtherDiscountStartDate(), item.getOtherDiscountEndDate())) DiscountManager.addOtherDiscountForClient(session, client);
                }

                applicationForFood = DAOUtils.updateApplicationForFoodByServiceNumberFullWithVersion(session, item.getServNumber(), client, item.getDtisznCode(), newStatus, item.getApplicantPhone(), item.getApplicantName(), item.getApplicantSecondName(), item.getApplicantSurname(), nextVersion, nextHistoryVersion);
                if (!oldStatus.equals(newStatus)) etpStatuses.add(new ResRequestFeedingETPStatuses(applicationForFood, new ApplicationForFoodStatus(newStatus.getApplicationForFoodState(), newStatus.getDeclineReason())));
            }
            catch (ApplicationForFoorStatusExistsException e)
            {
                logger.error("Error in processing entity: " + e.getMessage());
                return new AbstractMap.SimpleEntry<>(new ResRequestFeedingItem(applicationForFood, item.getResCode()), etpStatuses);
            }
            catch (Exception e)
            {
                return new AbstractMap.SimpleEntry<>(ResRequestFeedingItem.error(RequestFeedingItem.ERROR_CODE_INTERNAL_ERROR, "Error in processing entity: " + e.getMessage()), etpStatuses);
            }
        }
        saveOtherDiscount(session, item, client, applicationForFood);
        ResRequestFeedingItem resItem = new ResRequestFeedingItem(applicationForFood, item.getResCode());
        return new AbstractMap.SimpleEntry<>(resItem, etpStatuses);
    }

    public void processETPStatuses(List<ResRequestFeedingETPStatuses> etpStatuses) throws Exception {
        long time = System.currentTimeMillis() - RuntimeContext.getAppContext().getBean(ETPMVService.class).getPauseValue();
        for (ResRequestFeedingETPStatuses etpStatus : etpStatuses) {
            RuntimeContext.getAppContext().getBean(ETPMVService.class)
                    .sendStatusAsync(time, etpStatus.getApplicationForFood().getServiceNumber(),
                            etpStatus.getStatus().getApplicationForFoodState(),
                            etpStatus.getStatus().getDeclineReason());
            time += RuntimeContext.getAppContext().getBean(ETPMVService.class).getPauseValue();
        }
    }

    private ResRequestFeedingItem createErrorItem(RequestFeedingItem item) {
        ResRequestFeedingItem errorItem = new ResRequestFeedingItem();
        errorItem.setApplicationForFeedingNumber(item.getApplicationForFeedingNumber());
        errorItem.setRegDate(item.getApplicationCreatedDate());
        errorItem.setIdOfClient(item.getIdOfClient());
        errorItem.setApplicantPhone(item.getApplicantPhone());
        errorItem.setCode(item.getResCode());
        errorItem.setError(item.getErrorMessage());
        return errorItem;
    }

    public RequestFeedingData processData() throws Exception {
        List<RequestFeedingItem> requestFeedingItems = new ArrayList<>();
        List<Long> friendlyOrgIds = DAOUtils.findFriendlyOrgIds(session, requestFeeding.getIdOfOrgOwner());
        List<ApplicationForFood> applicationsForFood = DAOUtils.getApplicationsForFoodForOrgsSinceVersion(session, friendlyOrgIds, requestFeeding.getMaxVersion());

        for (ApplicationForFood applicationForFood : applicationsForFood) {
            if (applicationForFood == null) continue;

            ApplicationForFoodHistory history = DAOUtils.getLastApplicationForFoodHistory(session, applicationForFood);

            RequestFeedingItem requestFeedingItem = null;
            if (applicationForFood.getDtisznCode() == null) { //Тип льготы - Иное
                ClientDtisznDiscountInfo discountInfo = DAOUtils.getDTISZNDiscountInfoByClientAndCode(session, applicationForFood.getClient(), DTSZNDiscountsReviseService.OTHER_DISCOUNT_CODE);
                if (discountInfo != null) requestFeedingItem = new RequestFeedingItem(applicationForFood, history.getCreatedDate(), discountInfo);
            }

            if (requestFeedingItem == null) requestFeedingItem = new RequestFeedingItem(applicationForFood, history.getCreatedDate());
            requestFeedingItems.add(requestFeedingItem);
        }

        return new RequestFeedingData(requestFeedingItems);
    }

    private void saveOtherDiscount(Session session, RequestFeedingItem item, Client client, ApplicationForFood applicationForFood) {
        if (null != item.getDtisznCode() && (null == item.getOtherDiscountStartDate() || null == item
                .getOtherDiscountEndDate()) || null == item.getOtherDiscountStartDate() || null == item
                .getOtherDiscountEndDate()) {
            return;
        }
        ClientDtisznDiscountInfo discountInfo = DAOUtils
                .getDTISZNDiscountInfoByClientAndCode(session, client, DTSZNDiscountsReviseService.OTHER_DISCOUNT_CODE);
        if (null == discountInfo) {
            Long clientDTISZNDiscountVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);
            discountInfo = new ClientDtisznDiscountInfo(client, DTSZNDiscountsReviseService.OTHER_DISCOUNT_CODE,
                    DTSZNDiscountsReviseService.OTHER_DISCOUNT_DESCRIPTION, ClientDTISZNDiscountStatus.CONFIRMED,
                    item.getOtherDiscountStartDate(), item.getOtherDiscountEndDate(), new Date(),
                    DTSZNDiscountsReviseService.DATA_SOURCE_TYPE_MARKER_ARM, clientDTISZNDiscountVersion);
            session.save(discountInfo);
        } else {
            if (discountInfo.getArchived() || !discountInfo.getStatus().equals(ClientDTISZNDiscountStatus.CONFIRMED)
                    || !discountInfo.getDateStart().equals(item.getOtherDiscountStartDate()) || !discountInfo
                    .getDateEnd().equals(item.getOtherDiscountEndDate())) {
                DiscountManager.ClientDtisznDiscountInfoBuilder builder = new DiscountManager.ClientDtisznDiscountInfoBuilder(discountInfo);
                builder.withArchived(false);
                builder.withStatus(ClientDTISZNDiscountStatus.CONFIRMED);
                builder.withDateStart(item.getOtherDiscountStartDate());
                builder.withDateEnd(item.getOtherDiscountEndDate());
                builder.save(session);
            }
        }
        applicationForFood.setDiscountDateStart(item.getOtherDiscountStartDate());
        applicationForFood.setDiscountDateEnd(item.getOtherDiscountEndDate());
        session.update(applicationForFood);
    }
}
