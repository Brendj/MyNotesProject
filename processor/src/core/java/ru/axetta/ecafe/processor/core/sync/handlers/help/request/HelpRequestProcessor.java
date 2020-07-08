/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.help.request;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HelpRequestProcessor extends AbstractProcessor<ResHelpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HelpRequestProcessor.class);
    private final HelpRequest helpRequest;

    public HelpRequestProcessor(Session persistenceSession, HelpRequest helpRequest) {
        super(persistenceSession);
        this.helpRequest = helpRequest;
    }

    @Override
    public ResHelpRequest process() {
        ResHelpRequest result = new ResHelpRequest();
        List<ResHelpRequestItem> items = new ArrayList<ResHelpRequestItem>();
        try {
            ResHelpRequestItem resItem = null;
            boolean errorFound = false;
            Long nextVersion = helpRequest.getMaxVersion();
            for (HelpRequestItem item : helpRequest.getItems()) {
                errorFound = !item.getResCode().equals(HelpRequestItem.ERROR_CODE_ALL_OK);
                if (!errorFound) {
                    ru.axetta.ecafe.processor.core.persistence.HelpRequest helpRequest = DAOUtils.getHelpRequestForOrgByGuid(session, item.getOrgId(), item.getGuid());

                    Org org = (Org)session.load(Org.class, item.getOrgId());//DAOReadonlyService.getInstance().findOrg(item.getOrgId());
                    if (null == helpRequest) {
                        helpRequest = new ru.axetta.ecafe.processor.core.persistence.HelpRequest(item.getRequestDate(), item.getTheme(), item.getMessage(), item.getDeclarer(),
                                item.getPhone(), item.getRequestState(), item.getNumber(), org, item.getGuid());
                        helpRequest.setVersion(nextVersion);
                        session.save(helpRequest);
                    } else {
                        helpRequest.setRequestDate(item.getRequestDate());
                        helpRequest.setTheme(item.getTheme());
                        helpRequest.setMessage(item.getMessage());
                        helpRequest.setDeclarer(item.getDeclarer());
                        helpRequest.setPhone(item.getPhone());
                        helpRequest.setStatus(item.getRequestState());
                        helpRequest.setRequestNumber(item.getNumber());
                        helpRequest.setOrg(org);
                        helpRequest.setGuid(item.getGuid());
                        helpRequest.setRequestUpdateDate(new Date());
                        helpRequest.setVersion(nextVersion);
                        session.update(helpRequest);
                    }

                    resItem = new ResHelpRequestItem(helpRequest, item.getResCode());
                } else {
                    resItem = new ResHelpRequestItem();
                    resItem.setGuid(item.getGuid());
                    resItem.setResultCode(item.getResCode());
                    resItem.setErrorMessage(item.getErrorMessage());
                }
                items.add(resItem);
                session.flush();
            }
        }
        catch (Exception e) {
            logger.error("Error saving HelpRequest", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public HelpRequestData processData() throws Exception {
        HelpRequestData result = new HelpRequestData();
        List<ResHelpRequestItem> items = new ArrayList<ResHelpRequestItem>();
        ResHelpRequestItem resItem;
        List<ru.axetta.ecafe.processor.core.persistence.HelpRequest> list =
                DAOUtils.getHelpRequestsForOrgSinceVersion(session, helpRequest.getIdOfOrgOwner(), helpRequest.getMaxVersion());
        for (ru.axetta.ecafe.processor.core.persistence.HelpRequest helpRequest : list) {
            if (helpRequest != null) {
                resItem = new ResHelpRequestItem(helpRequest);
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
    }
}
