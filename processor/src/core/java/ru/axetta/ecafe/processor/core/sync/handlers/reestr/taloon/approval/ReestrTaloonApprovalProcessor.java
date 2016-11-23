/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
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
                    Long idOfOrg = item.getOrgId();
                    Long idOfOrgCreated = item.getOrgIdCreated();
                    Date date = item.getDate();
                    String name = item.getName();
                    String goodsGuid = item.getGoodsGuid();
                    if (goodsGuid == null) goodsGuid = "";
                    TaloonApproval taloon = DAOReadonlyService.getInstance().findTaloonApproval(idOfOrg, date, name, goodsGuid);
                    Integer ordersCount = DAOReadonlyService.getInstance().findTaloonApprovalSoldedQty(idOfOrg, date, name, goodsGuid);
                    Integer soldedQty = item.getSoldedQty();
                    if(ordersCount == null || ordersCount == 0 || soldedQty.equals(ordersCount)) {
                        ordersCount = null;
                    } else {
                        soldedQty = ordersCount;
                    }
                    Long price = item.getPrice();
                    Integer requestedQty = item.getRequestedQty();
                    Integer shippedQty = item.getShippedQty();
                    TaloonCreatedTypeEnum createdType = item.getCreatedType();
                    TaloonISPPStatesEnum isppState = item.getIsppState();
                    TaloonPPStatesEnum ppState = item.getPpState();
                    String goodsName = item.getGoodsName();
                    Org orgOwner = (Org)session.load(Org.class, item.getOrgOwnerId());
                    Boolean deletedState = item.getDeletedState();
                    Long taloonNumber = item.getTaloonNumber();

                    if (taloon == null) {
                        taloon = new TaloonApproval(idOfOrg, idOfOrgCreated, date, name, goodsGuid, soldedQty, price, createdType, requestedQty, shippedQty,
                                isppState, ppState, goodsName);
                    }
                    taloon.setSoldedQty(soldedQty);
                    taloon.setRequestedQty(requestedQty);
                    taloon.setShippedQty(shippedQty);
                    taloon.setPrice(price);
                    taloon.setCreatedType(createdType);
                    taloon.setIsppState(isppState);
                    taloon.setPpState(ppState);
                    taloon.setOrgOwner(orgOwner);
                    taloon.setVersion(nextVersion);
                    taloon.setDeletedState(deletedState);
                    taloon.setTaloonNumber(taloonNumber);

                    session.saveOrUpdate(taloon);

                    resItem = new ResTaloonApprovalItem(taloon, ordersCount, item.getResCode());
                } else {
                    resItem = new ResTaloonApprovalItem();
                    resItem.setOrgId(item.getOrgId());
                    resItem.setDate(item.getDate());
                    resItem.setName(item.getName());
                    resItem.setGoodsGuid(item.getGoodsGuid());
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
            if (taloon != null) {
                resItem = new ResTaloonApprovalItem(taloon);
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
    }

    public List<ResTaloonApprovalItem> getResTaloonApprovalItems() {
        return resTaloonApprovalItems;
    }
}
