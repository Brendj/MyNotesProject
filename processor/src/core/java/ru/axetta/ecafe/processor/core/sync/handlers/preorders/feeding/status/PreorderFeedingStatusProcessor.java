/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import ru.axetta.ecafe.processor.core.persistence.PreorderStatus;
import ru.axetta.ecafe.processor.core.persistence.PreorderStatusType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nuc on 18.02.2020.
 */
public class PreorderFeedingStatusProcessor extends AbstractProcessor<ResPreorderFeedingStatus> {
    private static final Logger logger = LoggerFactory.getLogger(PreorderFeedingStatusProcessor.class);
    private final PreorderFeedingStatusRequest preorderFeedingStatusRequest;
    private final List<ResPreorderFeedingStatusItem> resPreorderFeedingStatusItems;

    public PreorderFeedingStatusProcessor(Session persistenceSession, PreorderFeedingStatusRequest preorderFeedingStatusRequest) {
        super(persistenceSession);
        this.preorderFeedingStatusRequest = preorderFeedingStatusRequest;
        resPreorderFeedingStatusItems = new ArrayList<ResPreorderFeedingStatusItem>();
    }

    @Override
    public ResPreorderFeedingStatus process() throws Exception {
        ResPreorderFeedingStatus result = new ResPreorderFeedingStatus();
        List<ResPreorderFeedingStatusItem> items = new ArrayList<ResPreorderFeedingStatusItem>();
        try {
            ResPreorderFeedingStatusItem resItem = null;
            Long nextVersion = DAOUtils.nextVersionByTableWithoutLock(session, "cf_preorder_status");

            for (PreorderFeedingStatusItem item : preorderFeedingStatusRequest.getItems()) {
                boolean isNew = false;
                boolean errorFound = !StringUtils.isEmpty(item.getErrorMessage());
                if (!errorFound) {
                    PreorderStatus preorderStatus = DAOReadonlyService
                            .getInstance().findPreorderStatus(item.getGuid(), item.getDate());
                    if (preorderStatus == null) {
                        preorderStatus = new PreorderStatus();
                        isNew = true;
                    }
                    PreorderStatusType status = item.getStatus() == null ? null : PreorderStatusType.fromInteger(item.getStatus());
                    Boolean storno = item.getStorno() == null ? null : item.getStorno().equals(1);
                    preorderStatus.setGuid(item.getGuid());
                    preorderStatus.setDate(item.getDate());
                    preorderStatus.setStatus(status);
                    preorderStatus.setStorno(storno);
                    preorderStatus.setIdOfOrgOnCreate(item.getOrgOwner());
                    preorderStatus.setDeletedState(item.getDeletedState());
                    preorderStatus.setLastUpdate(new Date());

                    preorderStatus.setVersion(nextVersion);
                    if (isNew)
                        session.save(preorderStatus);
                    else
                        session.merge(preorderStatus);
                }
                resItem = new ResPreorderFeedingStatusItem(item.getGuid(), nextVersion, item.getErrorMessage());
                items.add(resItem);
            }
            session.flush();
        }
        catch (Exception e) {
            logger.error("Error saving ReestrTaloonApproval", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public PreorderFeedingStatusData processData(ResPreorderFeedingStatus resPreorderFeedingStatus) throws Exception {
        PreorderFeedingStatusData result = new PreorderFeedingStatusData();
        List<PreorderFeedingStatusItem> items = new ArrayList<PreorderFeedingStatusItem>();
        PreorderFeedingStatusItem resItem;
        List<PreorderStatus> list = DAOUtils.getPreorderStatusListSinceVersion(session, preorderFeedingStatusRequest.getOrgOwner(), preorderFeedingStatusRequest.getMaxVersion());
        for (PreorderStatus preorderStatus : list) {
            if (existInResitems(preorderStatus, resPreorderFeedingStatus)) continue;
            Integer storno = preorderStatus.getStorno() == null ? null : (preorderStatus.getStorno() ? 1 : 0);
            Integer status = preorderStatus.getStatus() == null ? null : preorderStatus.getStatus().getCode();
            resItem = new PreorderFeedingStatusItem(preorderStatus.getDate(), preorderStatus.getGuid(), status,
                    storno, preorderStatus.getVersion(), preorderStatus.getDeletedState(), preorderStatus.getIdOfOrgOnCreate(), null);
            items.add(resItem);
        }
        result.setItems(items);
        return result;
    }

    private boolean existInResitems(PreorderStatus preorderStatus, ResPreorderFeedingStatus resPreorderFeedingStatus) {
        for (ResPreorderFeedingStatusItem item : resPreorderFeedingStatus.getItems()) {
            if (item.getGuid().equals(preorderStatus.getGuid())) return true;
        }
        return false;
    }
}
