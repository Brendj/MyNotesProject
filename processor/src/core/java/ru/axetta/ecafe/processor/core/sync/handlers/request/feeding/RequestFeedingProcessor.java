/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestFeedingProcessor extends AbstractProcessor<ResRequestFeeding> {

    private static final Logger logger = LoggerFactory.getLogger(RequestFeedingProcessor.class);
    private final RequestFeeding requestFeeding;

    public RequestFeedingProcessor(Session session, RequestFeeding requestFeeding) {
        super(session);
        this.requestFeeding = requestFeeding;
    }

    @Override
    public ResRequestFeeding process() {
        ResRequestFeeding result = new ResRequestFeeding();
        List<ResRequestFeedingItem> items = new ArrayList<ResRequestFeedingItem>();
        List<ResRequestFeedingETPStatuses> etpStatuses = new ArrayList<ResRequestFeedingETPStatuses>();
        try {
            ResRequestFeedingItem resItem;
            boolean errorFound;
            Long nextVersion = DAOUtils.nextVersionByApplicationForFood(session);
            Long nextHistoryVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);
            for (RequestFeedingItem item : requestFeeding.getItems()) {
                errorFound = !item.getResCode().equals(RequestFeedingItem.ERROR_CODE_ALL_OK);
                if (!errorFound) {
                    ApplicationForFood applicationForFood = DAOUtils
                            .findApplicationForFoodByClientIdAndRegDate(session, item.getIdOfClient(),
                                    item.getApplicationCreatedDate());
                    Client client = (Client) session.load(Client.class, item.getIdOfClient());
                    if (null == client) {
                        logger.error(String.format("Client with id=%d not found", item.getIdOfClient()));
                        continue;
                    }

                    ApplicationForFoodStatus status = new ApplicationForFoodStatus(
                            ApplicationForFoodState.fromCode(item.getStatus()),
                            ApplicationForFoodDeclineReason.fromInteger(item.getDeclineReason()));
                    if (null == applicationForFood) {
                        try {
                            applicationForFood = DAOUtils
                                    .createApplicationForFood(session, client, item.getDtisznCode(),
                                            item.getApplicantPhone(), item.getApplicantName(),
                                            item.getApplicantSecondName(), item.getApplicantSurname(),
                                            item.getServNumber(), ApplicationForFoodCreatorType.PORTAL);
                            etpStatuses.add(new ResRequestFeedingETPStatuses(applicationForFood,
                                    applicationForFood.getStatus()));
                            ApplicationForFoodStatus st = new ApplicationForFoodStatus(
                                    ApplicationForFoodState.REGISTERED, null);
                            applicationForFood = DAOUtils
                                    .updateApplicationForFoodByServiceNumber(session, item.getServNumber(), st);
                            etpStatuses.add(new ResRequestFeedingETPStatuses(applicationForFood, st));
                        } catch (Exception e) {
                            logger.error(String.format(
                                    "Unable to create application for food {idOfClient=%d, DTSZNCode=%d, "
                                            + "applicantPhone=%s, applicantName=%s, applicantSecondName=%s, applicantSurname=%s, serviceNumber=%s}",
                                    item.getIdOfClient(), item.getDtisznCode(), item.getApplicantPhone(),
                                    item.getApplicantName(), item.getApplicantSecondName(), item.getApplicantSurname(),
                                    item.getServNumber()), e);
                            resItem = new ResRequestFeedingItem();
                            resItem.setCode(RequestFeedingItem.ERROR_CODE_INTERNAL_ERROR);
                            resItem.setError("Error in processing entity: " + e.getMessage());
                            items.add(resItem);
                            continue;
                        }

                    } else {
                        try {
                            ApplicationForFoodStatus oldStatus = applicationForFood.getStatus();
                            if (!oldStatus.equals(status) && applicationForFood.getDtisznCode() == null && status
                                    .getApplicationForFoodState().equals(ApplicationForFoodState.OK)) {
                                //если Иное и новый статус 1075, то искусственно создаем статус 1052
                                DAOUtils.addApplicationForFoodHistoryWithVersionIfNotExist(session, applicationForFood,
                                        new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING, null),
                                        nextHistoryVersion);
                                etpStatuses.add(new ResRequestFeedingETPStatuses(applicationForFood,
                                        new ApplicationForFoodStatus(ApplicationForFoodState.RESULT_PROCESSING,
                                                status.getDeclineReason())));
                                if (CalendarUtils.betweenDate(new Date(), item.getOtherDiscountStartDate(), item.getOtherDiscountEndDate())) {
                                    DiscountManager.addOtherDiscountForClient(session, client);
                                }
                            }

                            applicationForFood = DAOUtils
                                    .updateApplicationForFoodByServiceNumberFullWithVersion(session,
                                            item.getServNumber(), client, item.getDtisznCode(), status,
                                            item.getApplicantPhone(), item.getApplicantName(),
                                            item.getApplicantSecondName(), item.getApplicantSurname(), nextVersion,
                                            nextHistoryVersion, item.getDocOrderDate(), item.getIdOfDocOrder());
                            if (!oldStatus.equals(status)) {
                                etpStatuses.add(new ResRequestFeedingETPStatuses(applicationForFood,
                                        new ApplicationForFoodStatus(status.getApplicationForFoodState(),
                                                status.getDeclineReason())));
                            }
                        } catch (ApplicationForFoorStatusExistsException e) {
                            logger.error("Error in processing entity: " + e.getMessage());
                            resItem = new ResRequestFeedingItem(applicationForFood, item.getResCode());
                            items.add(resItem);
                            continue;
                        } catch (Exception e) {
                            resItem = new ResRequestFeedingItem();
                            resItem.setCode(RequestFeedingItem.ERROR_CODE_INTERNAL_ERROR);
                            resItem.setError("Error in processing entity: " + e.getMessage());
                            items.add(resItem);
                            continue;
                        }
                    }

                    saveOtherDiscount(session, item, client, applicationForFood);

                    resItem = new ResRequestFeedingItem(applicationForFood, item.getResCode());
                } else {
                    resItem = new ResRequestFeedingItem();
                    resItem.setApplicationForFeedingNumber(item.getApplicationForFeedingNumber());
                    resItem.setRegDate(item.getApplicationCreatedDate());
                    resItem.setIdOfClient(item.getIdOfClient());
                    resItem.setApplicantPhone(item.getApplicantPhone());
                    resItem.setCode(item.getResCode());
                    resItem.setError(item.getErrorMessage());
                }
                items.add(resItem);
            }
        } catch (Exception e) {
            logger.error("Error saving RequestFeeding", e);
            return null;
        }
        result.setItems(items);
        result.setStatuses(etpStatuses);
        return result;
    }

    public RequestFeedingData processData() throws Exception {
        RequestFeedingData result = new RequestFeedingData();
        List<RequestFeedingItem> items = new ArrayList<RequestFeedingItem>();
        List<Long> friendlyOrgIds = DAOUtils.findFriendlyOrgIds(session, requestFeeding.getIdOfOrgOwner());
        List<ApplicationForFood> list = DAOUtils
                .getApplicationsForFoodForOrgsSinceVersion(session, friendlyOrgIds, requestFeeding.getMaxVersion());
        for (ApplicationForFood applicationForFood : list) {
            if (null != applicationForFood) {
                ApplicationForFoodHistory history = DAOUtils
                        .getLastApplicationForFoodHistory(session, applicationForFood);
                RequestFeedingItem resItem = null;
                if (null == applicationForFood.getDtisznCode()) { //Тип льготы - Иное
                    ClientDtisznDiscountInfo discountInfo = DAOUtils
                            .getDTISZNDiscountInfoByClientAndCode(session, applicationForFood.getClient(),
                                    DTSZNDiscountsReviseService.OTHER_DISCOUNT_CODE);
                    if (null != discountInfo) {
                        resItem = new RequestFeedingItem(applicationForFood, history.getCreatedDate(), discountInfo);
                    }
                }

                if (null == resItem) {
                    resItem = new RequestFeedingItem(applicationForFood, history.getCreatedDate());
                }
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
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
                    DTSZNDiscountsReviseService.DATA_SOURCE_TYPE_MARKER_ARM, clientDTISZNDiscountVersion, new Date());
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
