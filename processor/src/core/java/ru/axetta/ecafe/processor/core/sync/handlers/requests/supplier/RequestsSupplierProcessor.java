/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DocumentState;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestsSupplierProcessor extends AbstractProcessor<ResRequestsSupplier> {

    private static final Logger logger = LoggerFactory.getLogger(RequestsSupplierProcessor.class);
    private final RequestsSupplier requestsSupplier;
    //private final List<ResRequestsSupplierItem> resRequestsSupplierItems;

    public RequestsSupplierProcessor(Session persistenceSession, RequestsSupplier requestsSupplier) {
        super(persistenceSession);
        this.requestsSupplier = requestsSupplier;
        //resRequestsSupplierItems = new ArrayList<>();
    }

    @Override
    public ResRequestsSupplier process() throws Exception {
        ResRequestsSupplier result = new ResRequestsSupplier();
        List<ResRequestsSupplierItem> items = new ArrayList<>();

        try {
            ResRequestsSupplierItem resItem = null;
            Long nextVersion = DAOUtils.nextVersionByGoodRequest(session);
            boolean errorFound = false;

            for (RequestsSupplierItem item : requestsSupplier.getItems()) {
                errorFound = !item.getResCode().equals(RequestsSupplierItem.ERROR_CODE_ALL_OK);
                //if (!errorFound) {

                    Long idOfOrg = item.getOrgId();
                    String guid = item.getGuid();

                    String number = item.getNumber();
                    Date doneDate = item.getDoneDate();
                    RequestsSupplierTypeEnum type = item.getType();
                    String staffGuid = item.getStaffGuid();
                    Boolean deletedState = item.getDeletedState();
                    Long versionFromClient = item.getVersion();

                    GoodRequest goodRequest = DAOReadonlyService.getInstance().findGoodRequestByGuid(guid);

                    if ((versionFromClient == null && goodRequest != null) || (versionFromClient != null &&
                            goodRequest != null && versionFromClient < goodRequest.getGlobalVersion())) {
                        errorFound = true;
                        item.setResCode(RequestsSupplierItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                        item.setErrorMessage("Record versions conflict");
                    } else {
                        if (goodRequest == null) {
                            goodRequest = item.getGoodRequest();
                        }

                        goodRequest.setOrgOwner(idOfOrg);
                        goodRequest.setNumber(number);
                        goodRequest.setCreatedDate(new Date(System.currentTimeMillis()));
                        goodRequest.setDoneDate(doneDate);
                        goodRequest.setState(DocumentState.FOLLOW);
                        goodRequest.setRequestType(type.ordinal());
                        goodRequest.setGuidOfStaff(staffGuid);
                        goodRequest.setSendAll(SendToAssociatedOrgs.SendToMain);

                        session.saveOrUpdate(goodRequest);

                        List<RequestsSupplierDetail> requestsSupplierDetailList = item.getRequestsSupplierDetailList();
                        if (requestsSupplierDetailList != null && requestsSupplierDetailList.size() > 0) {
                            for (RequestsSupplierDetail detail : requestsSupplierDetailList) {
                                Long detailVersionFromClient = detail.getVersion();

                                GoodRequestPosition goodRequestPosition = DAOReadonlyService.getInstance()
                                        .findGoodRequestPositionByGuid(guid);

                                if ((detailVersionFromClient == null && goodRequestPosition != null) ||
                                        (detailVersionFromClient != null && goodRequestPosition != null &&
                                                detailVersionFromClient < goodRequestPosition.getGlobalVersion())) {
                                    errorFound = true;
                                    item.setResCode(RequestsSupplierItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                                    item.setErrorMessage("Record versions conflict");
                                } else if (goodRequestPosition == null) {
                                    goodRequestPosition = detail.getGoodRequestPosition();
                                }

                                goodRequestPosition.setOrgOwner(idOfOrg);
                                goodRequestPosition.setGoodRequest(goodRequest);
                                if (detail.getIdOfComplex() != null) {
                                    goodRequestPosition.setComplexId(detail.getIdOfComplex().intValue());
                                }
                                // idOfDish
                                // fType
                                goodRequestPosition.setNetWeight(0L);
                                goodRequestPosition.setCreatedDate(new Date(System.currentTimeMillis()));
                                goodRequestPosition.setTotalCount(detail.getTotalCount().longValue());
                                if (detail.getdProbeCount() != null) {
                                    goodRequestPosition.setLastDailySampleCount(detail.getdProbeCount().longValue());
                                }
                                if (detail.getTempClientsCount() != null) {
                                    goodRequestPosition.setTempClientsCount(detail.getTempClientsCount().longValue());
                                }
                            }
                        }
                    }

                resItem = new ResRequestsSupplierItem();
                resItem.setGuid(guid);
                resItem.setVersion(nextVersion);
                resItem.setResultCode(item.getResCode());
                resItem.setErrorMessage(item.getErrorMessage());

                items.add(resItem);
                //}
            }
            session.flush();

        } catch (Exception e) {
            logger.error("Error by saving RequestsSupplier", e);
            return null;
        }

        result.setItems(items);
        return result;
    }

    public RequestsSupplierData processData() throws Exception {
        RequestsSupplierData result = new RequestsSupplierData();
        List<ResRequestsSupplierItem> items = new ArrayList<>();
        ResRequestsSupplierItem resItem;
        List<GoodRequest> list = DAOUtils.getGoodRequestForOrgSinceVersion(session,
                requestsSupplier.getIdOfOrgOwner(), requestsSupplier.getMaxVersion());
        for (GoodRequest goodRequest : list) {
            if (goodRequest != null) {
                resItem = new ResRequestsSupplierItem(goodRequest);
                List<GoodRequestPosition> details = DAOUtils.getGoodRequestPositionsFromGoodRequest(session, goodRequest);
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

    //public List<ResRequestsSupplierItem> getResRequestsSupplierItems() {
    //    return resRequestsSupplierItems;
    //}
}
