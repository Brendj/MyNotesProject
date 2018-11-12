/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.dtiszn;

import ru.axetta.ecafe.processor.core.persistence.ClientDtisznDiscountInfo;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

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
            list = DAOUtils.getDTISZNDiscountsInfoByClientIdSinceVersion(session, clientDiscountsDTSZNRequest.getIdOfClient(),
                    clientDiscountsDTSZNRequest.getMaxVersion());
        } else {
            list = DAOUtils.getDTISZNDiscountInfoByOrgIdSinceVersion(session, clientDiscountsDTSZNRequest.getOrgOwner(),
                    clientDiscountsDTSZNRequest.getMaxVersion());
        }
        for (ClientDtisznDiscountInfo info : list) {
            if (info != null) {
                ClientDiscountDTSZNItem resItem = new ClientDiscountDTSZNItem(info);
                items.add(resItem);
            }
        }
        result.setItems(items);
        return result;
    }
}
