/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.card.request;

import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */
public class CardRequestsProcessor extends AbstractProcessor<ResCardRequests> {
    private static final Logger logger = LoggerFactory.getLogger(CardRequestsProcessor.class);
    private final CardRequests cardRequests;
    private final List<ResCardRequestItem> resCardRequestItems;

    public CardRequestsProcessor(Session persistenceSession, CardRequests cardRequests) {
        super(persistenceSession);
        this.cardRequests = cardRequests;
        resCardRequestItems = new ArrayList<ResCardRequestItem>();
    }

    @Override
    public ResCardRequests process() throws Exception {
        return null; //не принимаем данные от Арма
    }

    public CardRequestsData processData() throws Exception {
        CardRequestsData result = new CardRequestsData();
        /*
        todo заглушка пока
         */
        result.setItems(new ArrayList<ResCardRequestItem>());
        return result;
    }

    public List<ResCardRequestItem> getResCardRequestItems() {
        return resCardRequestItems;
    }
}
