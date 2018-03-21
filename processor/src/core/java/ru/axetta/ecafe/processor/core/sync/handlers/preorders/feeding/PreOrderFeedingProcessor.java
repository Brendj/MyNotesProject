/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding;

import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.persistence.PreorderMenuDetail;
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

    @Override
    public PreOrdersFeeding process() throws Exception {
        PreOrdersFeeding result = new PreOrdersFeeding();
        List<PreOrdersFeedingItem> items = new ArrayList<PreOrdersFeedingItem>();

        List<PreorderComplex> list = DAOUtils.getPreorderComplexForOrgSinceVersion(session,
                preOrdersFeedingRequest.getOrgOwner(), preOrdersFeedingRequest.getMaxVersion());
        for (PreorderComplex preorderComplex : list) {
            if (preorderComplex != null) {
                List<PreorderMenuDetail> menuDetailList =
                        DAOUtils.getPreorderMenuDetailByPreorderComplex(session, preorderComplex);
                PreOrdersFeedingItem resItem = new PreOrdersFeedingItem(preorderComplex, menuDetailList);
                items.add(resItem);
            }
        }
        result.setItems(items);
        return result;
    }
}
