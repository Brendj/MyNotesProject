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
            ResTaloonApprovalItem resItem = null;
            Long nextVersion = DAOUtils.nextVersionByTaloonApproval(session);
            boolean errorFound = false;
            for (TaloonApprovalItem item : reestrTaloonApproval.getItems()) {

                errorFound = !item.getResCode().equals(TaloonApprovalItem.ERROR_CODE_ALL_OK);
                if (!errorFound) {
                    Long idOfOrg = item.getOrgId();
                    Long idOfOrgCreated = item.getOrgIdCreated();
                    Date date = item.getDate();
                    String name = item.getName();
                    String goodsGuid = item.getGoodsGuid();
                    Long price = item.getPrice();
                    if (goodsGuid == null) goodsGuid = "";
                    TaloonApproval taloon = DAOReadonlyService.getInstance().findTaloonApproval(idOfOrg, date, name,
                            goodsGuid, price);
                    Integer ordersCount = DAOReadonlyService.getInstance().findTaloonApprovalSoldedQty(idOfOrg, date,
                            name, goodsGuid, price);
                    Integer soldedQty = item.getSoldedQty();
                    if(ordersCount == null || ordersCount == 0 || soldedQty.equals(ordersCount)) {
                        ordersCount = null;
                    }
                    else {
                        ordersCount = soldedQty;
                    }

                    Integer requestedQty = item.getRequestedQty();
                    Integer shippedQty = item.getShippedQty();
                    TaloonCreatedTypeEnum createdType = item.getCreatedType();
                    TaloonISPPStatesEnum isppState = item.getIsppState();
                    TaloonPPStatesEnum ppState = item.getPpState();
                    String goodsName = item.getGoodsName();
                    Org orgOwner = (Org)session.load(Org.class, item.getOrgOwnerId());
                    Boolean deletedState = item.getDeletedState();
                    Long complexId = item.getComplexId();
                    Boolean byWebSupplier = item.getByWebSupplier();
                    Long taloonNumber = item.getTaloonNumber();
                    Long versionFromClient = item.getVersion();
                    if ((versionFromClient == null && taloon != null) || (versionFromClient != null && taloon != null
                            && versionFromClient < taloon.getVersion())) {
                        errorFound = true;
                        item.setResCode(TaloonApprovalItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                        item.setErrorMessage("Record version conflict");
                    } else {

                        if (taloon == null) {
                            taloon = new TaloonApproval(idOfOrg, idOfOrgCreated, date, name, goodsGuid, soldedQty,
                                    price, createdType, requestedQty, shippedQty,
                                    isppState, ppState, goodsName);
                            taloon.setRemarks(String.format("Создано в ОО \"%s\" (ид. %s), %3$td.%3$tm.%3$tY %3$tT",
                                    orgOwner.getShortName(), orgOwner.getIdOfOrg(), new Date()));
                        } else {
                            String rem = (taloon.getRemarks() == null ? "-" : taloon.getRemarks());
                            taloon.setRemarks(rem.concat("\n").concat(String
                                    .format("Изменено в ОО \"%s\" (ид. %s), %3$td.%3$tm.%3$tY %3$tT", orgOwner.getShortName(), orgOwner.getIdOfOrg(), new Date())));
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
                        taloon.setComplexId(complexId);
                        taloon.setByWebSupplier(byWebSupplier);

                        session.saveOrUpdate(taloon);

                        resItem = new ResTaloonApprovalItem(taloon, ordersCount, item.getResCode());
                    }
                }
                if (errorFound) {
                    resItem = new ResTaloonApprovalItem();
                    resItem.setOrgId(item.getOrgId());
                    resItem.setDate(item.getDate());
                    resItem.setName(item.getName());
                    resItem.setGoodsGuid(item.getGoodsGuid());
                    resItem.setTaloonNumber(item.getTaloonNumber());
                    resItem.setPrice(item.getPrice());
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
        List<TaloonApproval> list = DAOUtils.getTaloonApprovalForOrgSinceVersion(session,
                reestrTaloonApproval.getIdOfOrgOwner(), reestrTaloonApproval.getMaxVersion());
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
