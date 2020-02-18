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

                boolean errorFound = !StringUtils.isEmpty(item.getErrorMessage());
                PreorderStatus preorderStatus;
                if (!errorFound) {
                    preorderStatus = DAOReadonlyService
                            .getInstance().findPreorderStatus(item.getGuid(), item.getDate());
                    Long versionFromClient = item.getVersion();
                    if ((versionFromClient == null && preorderStatus != null) || (versionFromClient != null && preorderStatus != null && versionFromClient < preorderStatus.getVersion())) {
                        item.setErrorMessage("Record version conflict");
                    } else {
                        if (preorderStatus == null)
                            preorderStatus = new PreorderStatus();
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
                        session.saveOrUpdate(preorderStatus);
                    }

                }
                resItem = new ResPreorderFeedingStatusItem(item.getGuid(), item.getVersion(), item.getErrorMessage());
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

    public ReestrTaloonApprovalData processData() throws Exception {
        ReestrTaloonApprovalData result = new ReestrTaloonApprovalData();
        List<ResTaloonApprovalItem> items = new ArrayList<ResTaloonApprovalItem>();
        ResTaloonApprovalItem resItem;
        List<TaloonApproval> list = DAOUtils.getTaloonApprovalForOrgSinceVersion(session, reestrTaloonApproval.getIdOfOrgOwner(), reestrTaloonApproval.getMaxVersion());
        for (TaloonApproval taloon : list) {
            if (taloon != null) {
                resItem = new ResTaloonApprovalItem(taloon);
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
    }
}
