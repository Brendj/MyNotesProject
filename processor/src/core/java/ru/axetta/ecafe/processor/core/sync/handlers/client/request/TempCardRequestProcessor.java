/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.client.request;

import org.hibernate.Session;
import ru.axetta.ecafe.processor.core.persistence.CardTempOperation;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.07.13
 * Time: 12:43
 * To change this template use File | Settings | File Templates.
 */
public class TempCardRequestProcessor extends AbstractProcessor<TempCardOperationData> {

    private final Long idOfOrg;

    public TempCardRequestProcessor(Session session, Long idOfOrg) {
        super(session);
        this.idOfOrg = idOfOrg;
    }

    @Override
    public TempCardOperationData process() throws Exception {
        List<TempCardOperationElement> tempCardOperationElements = new LinkedList<>();
        List<CardTempOperation> cardTempList = DAOUtils.getRegistrationTempCardOperationByOrg(session, idOfOrg);
        for (CardTempOperation operation: cardTempList){
            tempCardOperationElements.add(new TempCardOperationElement(operation));
            Long cartNo = operation.getCardTemp().getCardNo();
            CardTempOperation cardTempOperation;
            cardTempOperation = DAOUtils.getLastTempCardOperationByOrgAndCartNo(session, idOfOrg, cartNo);
            if(cardTempOperation!=null && !operation.equals(cardTempOperation)){
                tempCardOperationElements.add(new TempCardOperationElement(cardTempOperation));
            }
        }
        return new TempCardOperationData(tempCardOperationElements);
    }
}
