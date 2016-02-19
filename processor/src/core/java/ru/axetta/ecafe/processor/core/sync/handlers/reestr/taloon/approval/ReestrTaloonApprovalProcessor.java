/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfTaloonApproval;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TaloonApproval;
import ru.axetta.ecafe.processor.core.persistence.TaloonCreatedTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class ReestrTaloonApprovalProcessor extends AbstractProcessor<ResReestrTaloonApproval> {

    private static final Logger logger = LoggerFactory.getLogger(ReestrTaloonApprovalProcessor.class);
    private final ReestrTaloonApproval reestrTaloonApproval;
    private final List<ResTaloonApprovalItem> resTaloonApprovalItems;

    public ReestrTaloonApprovalProcessor(Session persistenceSession, ReestrTaloonApproval reestrTaloonApproval) {
        super(persistenceSession);
        this.reestrTaloonApproval = reestrTaloonApproval;
        resTaloonApprovalItems = new ArrayList<ResTaloonApprovalItem>();
    }

    @Override
    public ResReestrTaloonApproval process() throws Exception {
        ResReestrTaloonApproval result = new ResReestrTaloonApproval();
        List<ResTaloonApprovalItem> items = new ArrayList<ResTaloonApprovalItem>();
        try {
            ResTaloonApprovalItem resItem;
            Long nextVersion = DAOUtils.nextVersionByTaloonApproval(session);
            for (TaloonApprovalItem item : reestrTaloonApproval.getItems()) {

                if (item.getResCode().equals(TaloonApprovalItem.ERROR_CODE_ALL_OK)) {
                    CompositeIdOfTaloonApproval compositeId = new CompositeIdOfTaloonApproval(item.getOrgId(), item.getDate(), item.getName());
                    TaloonApproval taloon = DAOUtils.findTaloonApproval(session, compositeId);
                    Integer qty = item.getQty();
                    Long price = item.getPrice();
                    TaloonCreatedTypeEnum createdType = item.getCreatedType();
                    Org orgOwner = (Org)session.load(Org.class, item.getOrgOwnerId());
                    Boolean deletedState = item.getDeletedState();
                    Long taloonNumber = item.getTaloonNumber();

                    if (taloon == null) {
                        taloon = new TaloonApproval(compositeId, qty, price, createdType);
                    }
                    taloon.setQty(qty);
                    taloon.setPrice(price);
                    taloon.setCreatedType(createdType);
                    taloon.setOrgOwner(orgOwner);
                    taloon.setVersion(nextVersion);
                    taloon.setDeletedState(deletedState);
                    taloon.setTaloonNumber(taloonNumber);

                    session.saveOrUpdate(taloon);

                    resItem = new ResTaloonApprovalItem(taloon);
                    resItem.setResultCode(item.getResCode());
                } else {
                    resItem = new ResTaloonApprovalItem();
                    resItem.setOrgId(item.getOrgId());
                    resItem.setDate(item.getDate());
                    resItem.setName(item.getName());
                    resItem.setQty(item.getQty());
                    resItem.setPrice(item.getPrice());
                    resItem.setCreatedType(item.getCreatedType());
                    resItem.setTaloonNumber(item.getTaloonNumber());
                    resItem.setResultCode(item.getResCode());
                    resItem.setErrorMessage(item.getErrorMessage());
                }
                items.add(resItem);
            }
            session.flush();
        }
        catch (Exception e) {
            logger.error("Error saving ReestrTaloonApproval", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public ReestrTaloonApprovalData processData() throws Exception {
        ReestrTaloonApprovalData result = new ReestrTaloonApprovalData();
        List<ResTaloonApprovalItem> items = new ArrayList<ResTaloonApprovalItem>();
        ResTaloonApprovalItem resItem;
        List<TaloonApproval> list = DAOUtils.getTaloonApprovalForOrgSinceVersion(session, reestrTaloonApproval.getIdOfOrgOwner(), reestrTaloonApproval.getMaxVersion());
        for (TaloonApproval taloon : list) {
            resItem = new ResTaloonApprovalItem(taloon);
            items.add(resItem);
        }

        result.setItems(items);
        return result;
    }

    public List<ResTaloonApprovalItem> getResTaloonApprovalItems() {
        return resTaloonApprovalItems;
    }
}
