/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class PreOrderFeedingProcessor extends AbstractProcessor<PreOrdersFeeding> {

    private final PreOrdersFeedingRequest preOrdersFeedingRequest;

    public PreOrderFeedingProcessor(Session persistenceSession, PreOrdersFeedingRequest preOrdersFeedingRequest) {
        super(persistenceSession);
        this.preOrdersFeedingRequest = preOrdersFeedingRequest;
    }

    //todo тут доработки
    @Override
    public PreOrdersFeeding process() throws Exception {
        PreOrdersFeeding result = new PreOrdersFeeding();
        List<PreOrdersFeedingItem> items = new ArrayList<PreOrdersFeedingItem>();

        List<PreorderComplex> list;
        if (preOrdersFeedingRequest.getMaxVersion() > 0) {
            list = DAOUtils.getPreorderComplexForOrgSinceVersion(session, preOrdersFeedingRequest.getOrgOwner(),
                    preOrdersFeedingRequest.getMaxVersion());
        } else {
            list = DAOUtils.getPreorderComplexForOrgNewBase(session, preOrdersFeedingRequest.getOrgOwner());
        }
        for (PreorderComplex preorderComplex : list) {
            if (preorderComplex != null) {
                try {
                    PreOrdersFeedingItem resItem = new PreOrdersFeedingItem(session, preorderComplex); //, menuDetailList);
                    items.add(resItem);
                } catch (Exception e) {
                    //todo Есть предзаказ на комплекс или блюдо, но нет самого комплекса или блюда
                }
            }
        }
        result.setItems(items);
        return result;
    }
}
