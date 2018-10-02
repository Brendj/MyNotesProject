/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientBalanceHoldService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

public class ClientBalanceHoldProcessor extends AbstractProcessor<ClientBalanceHoldFeeding> {

    private final ClientBalanceHoldRequest clientBalanceHoldRequest;
    private final ClientBalanceHoldData clientBalanceHoldData;

    public ClientBalanceHoldProcessor(Session persistenceSession, ClientBalanceHoldRequest clientBalanceHoldRequest, ClientBalanceHoldData clientBalanceHoldData) {
        super(persistenceSession);
        this.clientBalanceHoldRequest = clientBalanceHoldRequest;
        this.clientBalanceHoldData = clientBalanceHoldData;
    }

    @Override
    public ClientBalanceHoldFeeding process() throws Exception {
        ClientBalanceHoldFeeding result = new ClientBalanceHoldFeeding();
        List<ClientBalanceHoldItem> items = new ArrayList<ClientBalanceHoldItem>();

        List<ClientBalanceHold> list = DAOUtils.getClientBalanceHoldForOrgSinceVersion(session,
                clientBalanceHoldRequest.getOrgOwner(), clientBalanceHoldRequest.getMaxVersion());
        for (ClientBalanceHold clientBalanceHold : list) {
            ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(clientBalanceHold);
            items.add(resItem);
        }
        result.setItems(items);
        return result;
    }

    public ResClientBalanceHoldData processData() {
        ResClientBalanceHoldData result = new ResClientBalanceHoldData();
        List<ClientBalanceHoldItem> items = new ArrayList<ClientBalanceHoldItem>();

        for (ClientBalanceHoldItem item : clientBalanceHoldData.getItems()) {
            if (!StringUtils.isEmpty(item.getErrorMessage())) {
                ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(item.getGuid(), 100, item.getErrorMessage());
                items.add(resItem);
                continue;
            }
            Criteria criteria = session.createCriteria(ClientBalanceHold.class);
            criteria.add(Restrictions.eq("guid", item.getGuid()));
            ClientBalanceHold clientBalanceHold = (ClientBalanceHold)criteria.uniqueResult();
            if (clientBalanceHold == null) {
                //Для нового объекта присваиваем все поля
                try {
                    Client client = (Client) session.get(Client.class, item.getIdOfClient());
                    Client declarer = (item.getIdOfDeclarer() == null) ? null : (Client) session.get(Client.class, item.getIdOfDeclarer());
                    Org oldOrg = (Org) session.get(Org.class, item.getIdOfOldOrg());
                    Org newOrg = (item.getIdOfNewOrg() == null) ? null : (Org) session.get(Org.class, item.getIdOfNewOrg());
                    Contragent oldContragent = oldOrg.getDefaultSupplier();
                    Contragent newContragent = (newOrg == null) ? null : newOrg.getDefaultSupplier();
                    ClientBalanceHoldCreateStatus createStatus = ClientBalanceHoldCreateStatus.fromInteger(item.getCreateStatus());
                    ClientBalanceHoldRequestStatus requestStatus = ClientBalanceHoldRequestStatus.fromInteger(item.getRequestStatus());
                    RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class).holdClientBalance(client, declarer, oldOrg,
                            newOrg, oldContragent, newContragent, createStatus, requestStatus, item.getPhoneOfDeclarer());
                } catch (Exception e) {
                    ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(item.getGuid(), 101, "Error in parsing required entities by values");
                    items.add(resItem);
                    continue;
                }
            } else {
                //Если объект найден в БД, то меняем только статусы и версию
                Long nextVersion = DAOUtils.nextVersionByClientBalanceHold(session);
                clientBalanceHold.setVersion(nextVersion);
                clientBalanceHold.setCreateStatus(ClientBalanceHoldCreateStatus.fromInteger(item.getCreateStatus()));
                clientBalanceHold.setRequestStatus(ClientBalanceHoldRequestStatus.fromInteger(item.getRequestStatus()));
                clientBalanceHold.setPhoneOfDeclarer(item.getPhoneOfDeclarer());
                session.update(clientBalanceHold);
            }
            ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(item.getGuid(), 0, null);
            items.add(resItem);
        }

        result.setItems(items);
        return result;
    }
}
