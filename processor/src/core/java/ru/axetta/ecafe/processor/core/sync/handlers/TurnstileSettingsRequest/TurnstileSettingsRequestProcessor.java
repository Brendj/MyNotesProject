/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.*;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TurnstileSettingsRequestProcessor extends AbstractProcessor<ResTurnstileSettingsRequest> {

    private static final Logger logger = LoggerFactory.getLogger(TurnstileSettingsRequestProcessor.class);
    private final TurnstileSettingsRequest turnstileSettingsRequest;

    public TurnstileSettingsRequestProcessor(Session persistenceSession,
            TurnstileSettingsRequest turnstileSettingsRequest) {
        super(persistenceSession);
        this.turnstileSettingsRequest = turnstileSettingsRequest;
    }

    public ResTurnstileSettingsRequest process() {
        ResTurnstileSettingsRequest result = new ResTurnstileSettingsRequest();

        List<ResTurnstileSettingsRequestItem> items = new ArrayList<ResTurnstileSettingsRequestItem>();
        try {
            ResTurnstileSettingsRequestItem resItem = null;
            boolean errorFound = false;
            Long nextVersion = DAOUtils.nextVersionByTurnstileSettingsRequest(session);
            Long orgOwner = turnstileSettingsRequest.getOrgOwner();

            ru.axetta.ecafe.processor.core.persistence.TurnstileSettings turnstileSettings = DAOUtils
                    .getTurnstileSettingsRequestByOrg(session, orgOwner);
            if (null == turnstileSettings) {
                turnstileSettings = new ru.axetta.ecafe.processor.core.persistence.TurnstileSettings();
                Org org = (Org) session.load(Org.class, orgOwner);
                turnstileSettings.setOrg(org);
            }

            for (TurnstileSettingsRequestItem item : turnstileSettingsRequest.getItems()) {
                String moduleType = item.getType();
                switch (moduleType) {
                    case "TS":
                        errorFound = !item.getResCode().equals(TurnstileSettingsRequestItem.ERROR_CODE_ALL_OK);
                        if (!errorFound) {
                            TurnstileSettingsRequestTSItem tsItem = (TurnstileSettingsRequestTSItem) item;
                            turnstileSettings.setNumOfEntries(tsItem.getNumOfEntries());

                            resItem = new ResTurnstileSettingRequestTSItem(turnstileSettings, tsItem.getResCode());
                        } else {
                            resItem = new ResTurnstileSettingRequestTSItem(item.getResCode(), item.getErrorMessage());
                        }
                        items.add(resItem);
                        break;
                    case "TR":
                        errorFound = !item.getResCode().equals(TurnstileSettingsRequestItem.ERROR_CODE_ALL_OK);
                        if (!errorFound) {
                            TurnstileSettingsRequestTRItem trItem = (TurnstileSettingsRequestTRItem) item;
                            turnstileSettings.setTurnstileId(trItem.getTurnstileId());
                            turnstileSettings.setControllerModel(trItem.getControllerModel());
                            turnstileSettings.setControllerFirmwareVersion(trItem.getControllerFirmwareVersion());
                            turnstileSettings.setIsWorkWithLongIds(trItem.getIsWorkWithLongIds());
                            turnstileSettings.setLastUpdateForTurnstile(trItem.getLastUpdateForTurnstileSetting());

                            resItem = new ResTurnstileSettingsRequestTRItem(turnstileSettings, trItem.getResCode());
                        } else {
                            resItem = new ResTurnstileSettingsRequestTRItem(item.getResCode(), item.getErrorMessage());
                        }
                        items.add(resItem);
                        break;
                }
            }
            turnstileSettings.setVersion(nextVersion);
            session.save(turnstileSettings);
        } catch (Exception e) {
            logger.error("Error saving TurnstileSettingRequest", e);
            return null;
        }
        result.setItems(items);
        return result;
    }
}
