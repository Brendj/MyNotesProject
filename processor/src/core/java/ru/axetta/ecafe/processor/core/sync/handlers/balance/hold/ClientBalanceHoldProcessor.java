/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.persistence.ClientBalanceHold;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.PreOrdersFeeding;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class ClientBalanceHoldProcessor extends AbstractProcessor<PreOrdersFeeding> {

    private final ClientBalanceHoldRequest clientBalanceHoldRequest;

    public ClientBalanceHoldProcessor(Session persistenceSession, ClientBalanceHoldRequest clientBalanceHoldRequest) {
        super(persistenceSession);
        this.clientBalanceHoldRequest = clientBalanceHoldRequest;
    }

    @Override
    public ClientBalanceHoldFeeding process() throws Exception {
        ClientBalanceHoldFeeding result = new ClientBalanceHoldFeeding();
        List<ClientBalanceHoldItem> items = new ArrayList<ClientBalanceHoldItem>();

        List<ClientBalanceHold> list = DAOUtils.getClientBalanceHoldForOrgSinceVersion(session,
                clientBalanceHoldRequest.getOrgOwner(), clientBalanceHoldRequest.getMaxVersion());
        for (ClientBalanceHold clientBalanceHold : list) {
            ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(session, clientBalanceHold);
            items.add(resItem);
        }
        result.setItems(items);
        return result;
    }
}
