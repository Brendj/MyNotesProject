/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.ResTurnstileSettingsRequestItem;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.TurnstileSettingsRequestItem;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.TurnstileSettingsRequestTRItem;
import ru.axetta.ecafe.processor.core.sync.handlers.TurnstileSettingsRequest.items.TurnstileSettingsRequestTSItem;

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
            Integer numOfEntries = null;
            StringBuilder errorMessage = new StringBuilder();
            int status = 1;

            ru.axetta.ecafe.processor.core.persistence.TurnstileSettings turnstileSettings = null;

            for (List<TurnstileSettingsRequestItem> sectionItem : turnstileSettingsRequest.getSectionItem()) {
                for (TurnstileSettingsRequestItem item : sectionItem) {
                    String moduleType = item.getType();

                    errorFound = !item.getResCode().equals(TurnstileSettingsRequestItem.ERROR_CODE_ALL_OK);
                    switch (moduleType) {
                        case "TS":
                            if (!errorFound) {
                                TurnstileSettingsRequestTSItem tsItem = (TurnstileSettingsRequestTSItem) item;
                                numOfEntries = tsItem.getNumOfEntries();
                            } else {
                                errorMessage.append("Section TS not found ");
                                status = 0;
                            }
                            break;
                        case "TR":
                            if (!errorFound) {
                                TurnstileSettingsRequestTRItem trItem = (TurnstileSettingsRequestTRItem) item;
                                turnstileSettings = DAOUtils.getTurnstileSettingsRequestByOrgAndId(session, orgOwner,
                                        trItem.getTurnstileId());
                                if (null == turnstileSettings) {
                                    turnstileSettings = new ru.axetta.ecafe.processor.core.persistence.TurnstileSettings();
                                    Org org = (Org) session.load(Org.class, orgOwner);
                                    turnstileSettings.setOrg(org);
                                }
                                turnstileSettings.setTurnstileId(trItem.getTurnstileId());
                                turnstileSettings.setControllerModel(trItem.getControllerModel());
                                turnstileSettings.setControllerFirmwareVersion(trItem.getControllerFirmwareVersion());
                                turnstileSettings.setIsReadsLongIdsIncorrectly(trItem.getIsReadsLongIdsIncorrectly());
                                turnstileSettings.setLastUpdateForTurnstile(trItem.getLastUpdateForTurnstileSetting());
                                turnstileSettings.setNumOfEntries(numOfEntries);
                                turnstileSettings.setVersion(nextVersion);
                                session.save(turnstileSettings);
                            } else {
                                errorMessage.append("Section TR not found ");
                                status = 0;
                            }
                            break;
                    }
                }
            }

            items.add(new ResTurnstileSettingsRequestItem(status, errorMessage.toString()));
        } catch (Exception e) {
            logger.error("Error saving TurnstileSettingRequest", e);
            return null;
        }
        result.setItems(items);
        return result;
    }
}
