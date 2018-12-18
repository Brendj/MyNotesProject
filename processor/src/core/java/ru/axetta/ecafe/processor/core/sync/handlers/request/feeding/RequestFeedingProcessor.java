/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

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
        try {
            ResRequestFeedingItem resItem;
            boolean errorFound;
            Long nextVersion = DAOUtils.nextVersionByApplicationForFood(session);
            Long nextHistoryVersion = DAOUtils.nextVersionByApplicationForFoodHistory(session);
            ETPMVService service = RuntimeContext.getAppContext().getBean(ETPMVService.class);
            for (RequestFeedingItem item : requestFeeding.getItems()) {
                errorFound = !item.getResCode().equals(RequestFeedingItem.ERROR_CODE_ALL_OK);
                if (!errorFound) {
                    ApplicationForFood applicationForFood = DAOUtils.findApplicationForFoodByClientIdAndRegDate(session,
                            item.getIdOfClient(), item.getApplicationCreatedDate());
                    Client client = (Client) session.load(Client.class, item.getIdOfClient());
                    if (null == client) {
                        logger.error(String.format("Client with id=%d not found", item.getIdOfClient()));
                        continue;
                    }
                    ApplicationForFoodStatus status = new ApplicationForFoodStatus(ApplicationForFoodState.fromCode(item.getStatus()),
                            ApplicationForFoodDeclineReason.fromInteger(item.getDeclineReason()));
                    if (null == applicationForFood) {
                        try {
                            applicationForFood = DAOUtils
                                    .createApplicationForFood(session, client, item.getDtisznCode(), item.getApplicantPhone(), item.getApplicantName(),
                                            item.getApplicantSecondName(), item.getApplicantSurname(), item.getServNumber(), ApplicationForFoodCreatorType.PORTAL);
                            service.sendStatusAsync(System.currentTimeMillis(), item.getServNumber(),
                                    applicationForFood.getStatus().getApplicationForFoodState(),
                                    applicationForFood.getStatus().getDeclineReason());
                            applicationForFood = DAOUtils.updateApplicationForFoodByServiceNumber(session, item.getServNumber(),
                                    new ApplicationForFoodStatus(ApplicationForFoodState.REGISTERED, null));
                            service.sendStatusAsync(System.currentTimeMillis(), item.getServNumber(),
                                    applicationForFood.getStatus().getApplicationForFoodState(),
                                    applicationForFood.getStatus().getDeclineReason());
                        } catch (Exception e) {
                            logger.error(String.format("Unable to create application for food {idOfClient=%d, DTSZNCode=%d, "
                                    + "applicantPhone=%s, applicantName=%s, applicantSecondName=%s, applicantSurname=%s, serviceNumber=%s}",
                                    item.getIdOfClient(), item.getDtisznCode(), item.getApplicantPhone(), item.getApplicantName(),
                                    item.getApplicantSecondName(), item.getApplicantSurname(), item.getServNumber()), e);
                            continue;
                        }

                    } else {
                        ApplicationForFoodStatus oldStatus = applicationForFood.getStatus();
                        applicationForFood = DAOUtils.updateApplicationForFoodByServiceNumberFullWithVersion(session, item.getServNumber(),
                                client, item.getDtisznCode(), status, item.getApplicantPhone(), item.getApplicantName(), item.getApplicantSecondName(),
                                item.getApplicantSurname(), nextVersion, nextHistoryVersion);
                        if (!oldStatus.equals(status)) {
                            service.sendStatusAsync(System.currentTimeMillis(),
                                    item.getServNumber(), status.getApplicationForFoodState(), status.getDeclineReason());
                        }
                    }

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
                session.flush();
            }
        } catch (Exception e) {
            logger.error("Error saving RequestFeeding", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public RequestFeedingData processData() throws Exception {
        RequestFeedingData result = new RequestFeedingData();
        List<RequestFeedingItem> items = new ArrayList<RequestFeedingItem>();
        RequestFeedingItem resItem;
        List<Long> friendlyOrgIds = DAOUtils.findFriendlyOrgIds(session, requestFeeding.getIdOfOrgOwner());
        List<ApplicationForFood> list =
                DAOUtils.getApplicationsForFoodForOrgsSinceVersion(session, friendlyOrgIds, requestFeeding.getMaxVersion());
        for (ApplicationForFood applicationForFood : list) {
            if (null != applicationForFood) {
                resItem = new RequestFeedingItem(applicationForFood);
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
    }
}
