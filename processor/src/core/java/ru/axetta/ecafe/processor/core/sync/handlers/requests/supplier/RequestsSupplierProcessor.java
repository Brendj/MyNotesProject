/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.daoservices.DOVersionRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.Staff;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.GoodRequestsChangeAsyncNotificationService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

public class RequestsSupplierProcessor extends AbstractProcessor<ResRequestsSupplier> {

    private static final Logger logger = LoggerFactory.getLogger(RequestsSupplierProcessor.class);
    private final RequestsSupplier requestsSupplier;

    public RequestsSupplierProcessor(Session persistenceSession, RequestsSupplier requestsSupplier) {
        super(persistenceSession);
        this.requestsSupplier = requestsSupplier;
    }

    @Override
    public ResRequestsSupplier process() throws Exception {
        ResRequestsSupplier result = new ResRequestsSupplier();
        List<ResRequestsSupplierItem> items = new ArrayList<>();
        Calendar calendarStart = RuntimeContext.getInstance().getDefaultLocalCalendar(null);
        final Date startDate = calendarStart.getTime();

        try {
            List<DistributedObject> listForNotifications = new LinkedList<>();
            ResRequestsSupplierItem resItem = null;
            boolean errorFound = false;

            Long nextVersion = DOVersionRepository.updateClassVersion("GoodRequest", session);
            Long nextPositionVersion = DOVersionRepository.updateClassVersion("GoodRequestPosition", session);

            for (RequestsSupplierItem item : requestsSupplier.getItems()) {
                errorFound = !item.getResCode().equals(RequestsSupplierItem.ERROR_CODE_ALL_OK);

                if (!errorFound) {
                    Long idOfOrg = item.getOrgId();
                    Date doneDate = item.getDoneDate();

                    List<RequestsSupplierDetail> requestsSupplierDetailList = item.getRequestsSupplierDetailList();
                    boolean goodDate = true;
                    if (requestsSupplierDetailList != null && requestsSupplierDetailList.size() > 0) {
                        Integer fType = -1;
                        for (RequestsSupplierDetail detail : requestsSupplierDetailList) {
                            if (detail.getfType().equals(RequestsSupplierDetailTypeEnum.REQUEST_TYPE_GENERAL) ||
                                detail.getfType().equals(RequestsSupplierDetailTypeEnum.REQUEST_TYPE_DISCOUNT) ||
                                detail.getfType().equals(RequestsSupplierDetailTypeEnum.REQUEST_TYPE_PAID))
                                fType = 0;
                            if (detail.getfType().equals(RequestsSupplierDetailTypeEnum.REQUEST_TYPE_SUBSCRIPTION))
                                fType = 1;
                            if (fType != -1) {
                                if (!isGoodDate(session, idOfOrg, doneDate, fType))
                                    goodDate = false;
                            }
                            if (!goodDate)
                                break;
                        }
                    }
                    if (!goodDate)
                    {
                        errorFound = true;
                        item.setResCode(RequestsSupplierItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                        item.setErrorMessage("CANT_CHANGE_GRP_ON_DATE");
                    }
                    else {
                        String guid = item.getGuid();
                        String number = item.getNumber();
                        Date dateOfGoodsRequest = item.getDateOfGoodsRequest();
                        RequestsSupplierTypeEnum type = item.getType();
                        String staffGuid = item.getStaffGuid();
                        Boolean deletedState = item.getDeletedState();

                        Long versionFromClient = item.getVersion();

                        GoodRequest goodRequest = DAOReadonlyService.getInstance().findGoodRequestByGuid(guid);

                        if ((versionFromClient == null && goodRequest != null) || (versionFromClient != null
                                && goodRequest != null && versionFromClient < goodRequest.getGlobalVersion())) {
                            errorFound = true;
                            item.setResCode(RequestsSupplierItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                            item.setErrorMessage("GoodRequest versions conflict");
                        } else {
                            if (goodRequest != null) {
                                goodRequest.setLastUpdate(new Date(System.currentTimeMillis()));
                            }
                            if (goodRequest == null) {
                                goodRequest = item.getGoodRequest();
                                goodRequest.setGlobalVersionOnCreate(nextVersion);
                                goodRequest.setCreatedDate(new Date(System.currentTimeMillis()));
                            }

                            Staff staff = DAOReadonlyService.getInstance().findStaffByGuid(staffGuid);
                            if (staff == null) {
                                item.setResCode(RequestsSupplierItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                                item.setErrorMessage("No such a staff record");
                            }

                            goodRequest.setOrgOwner(idOfOrg);
                            goodRequest.setNumber(number);
                            goodRequest.setGlobalVersion(nextVersion);
                            goodRequest.setDeletedState(deletedState);
                            goodRequest.setDoneDate(doneDate);
                            goodRequest.setDateOfGoodsRequest(dateOfGoodsRequest);
                            goodRequest.setState(DocumentState.FOLLOW);
                            goodRequest.setRequestType(type.ordinal());
                            goodRequest.setStaff(staff);
                            goodRequest.setGuidOfStaff(staffGuid);
                            goodRequest.setSendAll(SendToAssociatedOrgs.SendToMain);

                            session.saveOrUpdate(goodRequest);

                            if (requestsSupplierDetailList != null && requestsSupplierDetailList.size() > 0) {
                                for (RequestsSupplierDetail detail : requestsSupplierDetailList) {

                                    Long detailVersionFromClient = detail.getVersion();
                                    String detailGuid = detail.getGuid();

                                    GoodRequestPosition goodRequestPosition = DAOReadonlyService.getInstance().findGoodRequestPositionByGuid(detailGuid);

                                    if ((detailVersionFromClient == null && goodRequestPosition != null) || (
                                            detailVersionFromClient != null && goodRequestPosition != null
                                                    && detailVersionFromClient < goodRequestPosition.getGlobalVersion())) {
                                        errorFound = true;
                                        item.setResCode(RequestsSupplierItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                                        item.setErrorMessage("GoodRequestPosition versions conflict");
                                    } else {
                                        if (goodRequestPosition != null) {
                                            final Long lastTotalCount = goodRequestPosition.getTotalCount();
                                            final Long lastDailySampleCount = goodRequestPosition.getDailySampleCount();
                                            final Long lastTempClientsCount = goodRequestPosition.getTempClientsCount();
                                            goodRequestPosition.setLastTotalCount(lastTotalCount);
                                            goodRequestPosition.setLastDailySampleCount(lastDailySampleCount);
                                            goodRequestPosition.setLastTempClientsCount(lastTempClientsCount);
                                            goodRequestPosition.setLastUpdate(new Date(System.currentTimeMillis()));
                                            goodRequestPosition.setNotified(false);
                                        }
                                        if (goodRequestPosition == null) {
                                            goodRequestPosition = detail.getGoodRequestPosition();
                                            goodRequestPosition.setGlobalVersionOnCreate(nextPositionVersion);
                                            goodRequestPosition.setCreatedDate(new Date(System.currentTimeMillis()));
                                        }

                                        goodRequestPosition.setOrgOwner(idOfOrg);
                                        goodRequestPosition.setGoodRequest(goodRequest);
                                        if (detail.getIdOfComplex() != null) {
                                            goodRequestPosition.setComplexId(detail.getIdOfComplex().intValue());
                                        }
                                        goodRequestPosition.setIdOfDish(detail.getIdOfDish());
                                        goodRequestPosition.setFeedingType(detail.getfType().ordinal());
                                        goodRequestPosition.setNetWeight(0L);
                                        goodRequestPosition.setUnitsScale(UnitScale.UNITS);
                                        goodRequestPosition.setTotalCount(detail.getTotalCount().longValue());
                                        if (detail.getdProbeCount() != null) {
                                            goodRequestPosition.setDailySampleCount(detail.getdProbeCount().longValue());
                                        }
                                        if (detail.getTempClientsCount() != null) {
                                            goodRequestPosition.setTempClientsCount(detail.getTempClientsCount().longValue());
                                        }
                                        goodRequestPosition.setGlobalVersion(nextPositionVersion);
                                        goodRequestPosition.setDeletedState(detail.getDeletedState());

                                        session.saveOrUpdate(goodRequestPosition);

                                        listForNotifications.add(goodRequestPosition);
                                    }
                                }
                            }
                            resItem = new ResRequestsSupplierItem(goodRequest.getGuid(), goodRequest.getGlobalVersion());
                        }
                    }
                }
                if (errorFound) {
                    resItem = new ResRequestsSupplierItem(item.getGuid(), item.getVersion());
                }

                resItem.setResultCode(item.getResCode());
                resItem.setErrorMessage(item.getErrorMessage());
                items.add(resItem);
            }
            session.flush();
            // Рассылка уведомлений
            notifyOrgsAboutChangeGoodRequests(startDate, listForNotifications);

        } catch (Exception e) {
            logger.error("Error by saving RequestsSupplier", e);
            return null;
        }

        result.setItems(items);
        return result;
    }

    private void notifyOrgsAboutChangeGoodRequests(Date startDate, List<DistributedObject> listForNotifications) {

        Calendar calendarEnd = RuntimeContext.getInstance().getDefaultLocalCalendar(null);
        final Date lastCreateOrUpdateDate = calendarEnd.getTime();
        calendarEnd.add(Calendar.MINUTE, 1);
        final Date endGenerateTime = calendarEnd.getTime();

        // разослать уведомления всем организациям, чьи позиции изменились
        HashMap<Long, List<String>> mapPositions = new HashMap<>();
        for (DistributedObject position : listForNotifications) {
            Long orgOwner = position.getOrgOwner();
            if (!mapPositions.containsKey(orgOwner)) {
                mapPositions.put(orgOwner, new ArrayList<String>());
            }
            mapPositions.get(orgOwner).add(position.getGuid());
        }
        GoodRequestsChangeAsyncNotificationService notificationService = GoodRequestsChangeAsyncNotificationService
                .getInstance();
        for (Long orgOwner : mapPositions.keySet()) {
            List<String> guids = mapPositions.get(orgOwner);
            notificationService.notifyOrg(orgOwner, startDate, endGenerateTime, lastCreateOrUpdateDate, guids, true);
        }
    }

    public RequestsSupplierData processData() throws Exception {
        RequestsSupplierData result = new RequestsSupplierData();
        List<ResRequestsSupplierItem> items = new ArrayList<>();
        ResRequestsSupplierItem resItem;
        // Для уменьшения числа записей отбираем только заявки, у позиций которых не заполнен товар
        List<GoodRequest> list = DAOUtils.getGoodRequestForOrgSinceVersionWithDishes(session,
                requestsSupplier.getIdOfOrgOwner(), requestsSupplier.getMaxVersion());
        for (GoodRequest goodRequest : list) {
            if (goodRequest != null) {
                resItem = new ResRequestsSupplierItem(goodRequest);
                List<GoodRequestPosition> details = DAOUtils.getGoodRequestPositionsByGoodRequest(session, goodRequest);
                if (!details.isEmpty()) {
                    resItem.addDetails(details);
                }
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
    }

    public RequestsSupplier getRequestsSupplier() {
        return requestsSupplier;
    }


}
