/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

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
            for (RequestFeedingItem item : requestFeeding.getItems()) {
                errorFound = !item.getResCode().equals(RequestFeedingItem.ERROR_CODE_ALL_OK);
                if (!errorFound) {
                    ApplicationForFood applicationForFood = DAOUtils.findApplicationForFoodByClientIdAndRegDate(session,
                            item.getIdOfClient(), item.getApplicationCreatedDate());
                    Client client = (Client) session.load(Client.class, item.getIdOfClient());
                    if (null == client) {
                        throw new Exception(String.format("Client with id=%d not found", item.getIdOfClient()));
                    }
                    ApplicationForFoodStatus status = new ApplicationForFoodStatus(ApplicationForFoodState.fromCode(item.getStatus()),
                            ApplicationForFoodDeclineReason.fromInteger(item.getDeclineReason()));
                    if (null == applicationForFood) {
                        applicationForFood = new ApplicationForFood(client, item.getDtisznCode(), status, item.getApplicantPhone(),
                                item.getApplicantName(), item.getApplicantSecondName(), item.getApplicantSurname(),
                                item.getServNumber(), item.getCreatorType(), item.getIdOfDocOrder(), item.getDocOrderDate(), nextVersion);
                        session.save(applicationForFood);
                    } else {
                        applicationForFood.setClient(client);
                        applicationForFood.setDtisznCode(item.getDtisznCode());
                        applicationForFood.setStatus(status);
                        applicationForFood.setMobile(item.getApplicantPhone());
                        applicationForFood.setApplicantName(item.getApplicantName());
                        applicationForFood.setApplicantSecondName(item.getApplicantSecondName());
                        applicationForFood.setApplicantSurname(item.getApplicantSurname());
                        applicationForFood.setLastUpdate(new Date());
                        applicationForFood.setVersion(nextVersion);
                        session.update(applicationForFood);
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
        List<ApplicationForFood> list =
                DAOUtils.getApplicationsForFoodForOrgSinceVersion(session, requestFeeding.getIdOfOrgOwner(), requestFeeding.getMaxVersion());
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
