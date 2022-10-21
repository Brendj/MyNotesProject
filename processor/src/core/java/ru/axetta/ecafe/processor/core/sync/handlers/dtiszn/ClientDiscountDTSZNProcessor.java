/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.dtiszn;

import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientDtisznDiscountInfo;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;

import java.util.*;

public class ClientDiscountDTSZNProcessor extends AbstractProcessor<ClientDiscountDTSZN> {

    private final ClientDiscountsDTSZNRequest clientDiscountsDTSZNRequest;

    public ClientDiscountDTSZNProcessor(Session persistenceSession, ClientDiscountsDTSZNRequest clientDiscountsDTSZNRequest) {
        super(persistenceSession);
        this.clientDiscountsDTSZNRequest = clientDiscountsDTSZNRequest;
    }

    @Override
    public ClientDiscountDTSZN process() throws Exception {
        ClientDiscountDTSZN result = new ClientDiscountDTSZN();
        List<ClientDiscountDTSZNItem> items = new ArrayList<ClientDiscountDTSZNItem>();

        List<ClientDtisznDiscountInfo> list = null;
        if (null != clientDiscountsDTSZNRequest.getIdOfClient()) {
            //list = DAOUtils.getDTISZNDiscountsInfoByClientIdSinceVersion(session, clientDiscountsDTSZNRequest.getIdOfClient(),
            //        clientDiscountsDTSZNRequest.getMaxVersion());
            Client client = session.load(Client.class, clientDiscountsDTSZNRequest.getIdOfClient());
            list = Arrays.asList(DiscountManager.getAppointedClientDtisznDiscount(client));
        } else {
            List<ClientDtisznDiscountInfo> listDiscounts
                    = DAOUtils.getDTISZNDiscountInfoByOrgIdSinceVersion(session, clientDiscountsDTSZNRequest.getOrgOwner(),
                    clientDiscountsDTSZNRequest.getMaxVersion(), false);
            Set<Client> set = getClientsByDiscounts(listDiscounts);
            list = new ArrayList<>();
            for (Client client : set) {
                list.add(DiscountManager.getAppointedClientDtisznDiscount(client));
            }
        }
        for (ClientDtisznDiscountInfo info : list) {
            if (info != null && info.getVersion() > clientDiscountsDTSZNRequest.getMaxVersion()) {
                ClientDiscountDTSZNItem resItem = new ClientDiscountDTSZNItem(info);
                items.add(resItem);
            }
        }
        List<ClientDtisznDiscountInfo> archivedList = DAOUtils.getDTISZNDiscountInfoByOrgIdSinceVersion(session, clientDiscountsDTSZNRequest.getOrgOwner(),
                clientDiscountsDTSZNRequest.getMaxVersion(), true);
        for (ClientDtisznDiscountInfo info : archivedList) {
            if (info != null) {
                ClientDiscountDTSZNItem resItem = new ClientDiscountDTSZNItem(info);
                items.add(resItem);
            }
        }
        result.setItems(items);
        return result;
    }

    private Set<Client> getClientsByDiscounts(List<ClientDtisznDiscountInfo> list) {
        Set<Client> clients = new HashSet<>();
        for (ClientDtisznDiscountInfo info : list) {
            clients.add(info.getClient());
        }
        return clients;
    }
}
