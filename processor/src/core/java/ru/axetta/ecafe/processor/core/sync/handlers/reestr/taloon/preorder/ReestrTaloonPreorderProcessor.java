/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 16.12.19
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class ReestrTaloonPreorderProcessor extends AbstractProcessor<ResReestrTaloonPreorder> {

    private static final Logger logger = LoggerFactory.getLogger(
            ReestrTaloonPreorderProcessor.class);
    private final ReestrTaloonPreorder reestrTaloonPreorder;
    private final List<ResTaloonPreorderItem> resTaloonPreorderItems;
    private final SessionFactory persistenceSessionFactory;

    public ReestrTaloonPreorderProcessor(Session persistenceSession, ReestrTaloonPreorder reestrTaloonPreorder,
                                         SessionFactory persistenceSessionFactory) {
        super(persistenceSession);
        this.reestrTaloonPreorder = reestrTaloonPreorder;
        resTaloonPreorderItems = new ArrayList<>();
        this.persistenceSessionFactory = persistenceSessionFactory;
    }

    @Override
    public ResReestrTaloonPreorder process() throws Exception {
        ResReestrTaloonPreorder result = new ResReestrTaloonPreorder();
        List<ResTaloonPreorderItem> items = new ArrayList<>();
        try {
            ResTaloonPreorderItem resItem = null;
            Long nextVersion = DAOUtils.nextVersionByTaloonPreorder(session);
            boolean errorFound = false;
            for (TaloonPreorderItem item : reestrTaloonPreorder.getItems()) {
                Session persistenceSession = null;
                Transaction persistenceTransaction = null;
                try {
                    persistenceSession = persistenceSessionFactory.openSession();
                    persistenceTransaction = persistenceSession.beginTransaction();
                    errorFound = !item.getResCode().equals(TaloonPreorderItem.ERROR_CODE_ALL_OK);
                    if (!errorFound) {
                        String guid = item.getGuid();
                        if (guid == null) guid = "";
                        Long idOfOrg = item.getOrgId();
                        Long idOfOrgCreated = item.getOrgIdCreated();
                        Date date = item.getDate();
                        Long complexId = item.getComplexId();
                        String complexName = item.getComplexName();
                        String goodsGuid = item.getGoodsGuid();
                        Long price = item.getPrice();
                        if (goodsGuid == null) goodsGuid = "";
                        TaloonPreorder taloon = DAOReadonlyService.getInstance().findTaloonPreorder(guid);
                        Integer ordersCount = DAOReadonlyService.getInstance().findTaloonPreorderSoldQty(idOfOrg, date, complexName, goodsGuid, price);
                        Integer soldQty = item.getSoldQty();
                        if (ordersCount == null || ordersCount == 0 || soldQty.equals(ordersCount)) {
                            ordersCount = null;
                        } else {
                            ordersCount = soldQty;
                        }

                        Integer requestedQty = item.getRequestedQty();
                        // по умолчанию Отгрузка заполняется значением из поля Заказ ИСПП
                        Integer shippedQty = item.getShippedQty() == null || item.getShippedQty() == 0 ?
                                item.getRequestedQty() : item.getShippedQty();
                        Integer reservedQty = item.getReservedQty();
                        Integer blockedQty = item.getBlockedQty();
                        TaloonCreatedTypeEnum createdType = item.getCreatedType();
                        TaloonISPPStatesEnum isppState = item.getIsppState();
                        TaloonPPStatesEnum ppState = item.getPpState();
                        String goodsName = item.getGoodsName();
                        String comments = item.getComments();
                        Org orgOwner = (Org) persistenceSession.load(Org.class, item.getOrgOwnerId());
                        Boolean deletedState = item.getDeletedState();
                        Boolean byWebSupplier = item.getByWebSupplier();
                        Long idOfDish = item.getIdOfDish();
                        Long taloonNumber = item.getTaloonNumber();
                        Long versionFromClient = item.getVersion();
                        if ((versionFromClient == null && taloon != null) || (versionFromClient != null && taloon != null && versionFromClient < taloon.getVersion())) {
                            errorFound = true;
                            item.setResCode(TaloonPreorderItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                            item.setErrorMessage("Record version conflict");
                        } else {

                            if (taloon == null) {
                                taloon = new TaloonPreorder(guid, idOfOrg, date, complexId, complexName, goodsName, goodsGuid, idOfOrgCreated,
                                        soldQty, requestedQty, shippedQty, reservedQty, blockedQty, price, createdType,
                                        isppState, ppState, comments);
                                taloon.setRemarks(String.format("Создано в ОО \"%s\" (ид. %s), %3$td.%3$tm.%3$tY %3$tT",
                                        orgOwner.getShortName(), orgOwner.getIdOfOrg(), new Date()));
                            } else {
                                String rem = (taloon.getRemarks() == null ? "-" : taloon.getRemarks());
                                taloon.setRemarks(rem.concat("\n").concat(String
                                        .format("Изменено в ОО \"%s\" (ид. %s), %3$td.%3$tm.%3$tY %3$tT", orgOwner.getShortName(), orgOwner.getIdOfOrg(), new Date())));
                            }
                            taloon.setSoldQty(soldQty);
                            taloon.setRequestedQty(requestedQty);
                            taloon.setShippedQty(shippedQty);
                            taloon.setReservedQty(reservedQty);
                            taloon.setBlockedQty(blockedQty);
                            taloon.setPrice(price);
                            taloon.setCreatedType(createdType);
                            taloon.setIsppState(isppState);
                            taloon.setPpState(ppState);
                            taloon.setOrgOwner(orgOwner);
                            taloon.setVersion(nextVersion);
                            taloon.setDeletedState(deletedState);
                            taloon.setByWebSupplier(byWebSupplier);
                            taloon.setTaloonNumber(taloonNumber);
                            taloon.setComments(comments);
                            taloon.setComplexId(complexId);
                            taloon.setIdOfDish(idOfDish);

                            persistenceSession.saveOrUpdate(taloon);

                            resItem = new ResTaloonPreorderItem(taloon, ordersCount, item.getResCode());
                        }
                    }
                    if (errorFound) {
                        resItem = new ResTaloonPreorderItem();
                        resItem.setGuid(item.getGuid());
                        resItem.setOrgId(item.getOrgId());
                        resItem.setDate(item.getDate());
                        resItem.setComplexId(item.getComplexId());
                        resItem.setComplexName(item.getComplexName());
                        resItem.setGoodsGuid(item.getGoodsGuid());
                        resItem.setTaloonNumber(item.getTaloonNumber());
                        resItem.setPrice(item.getPrice());
                        resItem.setResultCode(item.getResCode());
                        resItem.setErrorMessage(item.getErrorMessage());
                    }
                    persistenceSession.flush();
                    persistenceTransaction.commit();
                    items.add(resItem);
                    persistenceTransaction = null;
                } catch (Exception ignore) {
                    logger.error("Error saving TaloonPreorder");
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, logger);
                    HibernateUtils.close(persistenceSession, logger);
                }
            }
        } catch (Exception e) {
            logger.error("Error saving ReestrTaloonPreorder", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public ReestrTaloonPreorderData processData() throws Exception {
        ReestrTaloonPreorderData result = new ReestrTaloonPreorderData();
        List<ResTaloonPreorderItem> items = new ArrayList<>();
        ResTaloonPreorderItem resItem;
        List<TaloonPreorder> list = DAOUtils.getTaloonPreorderForOrgSinceVersion(session, reestrTaloonPreorder.getIdOfOrgOwner(), reestrTaloonPreorder
                .getMaxVersion());
        for (TaloonPreorder taloon : list) {
            if (taloon != null) {
                resItem = new ResTaloonPreorderItem(taloon);
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
    }

    public List<ResTaloonPreorderItem> getResTaloonPreorderItems() {
        return resTaloonPreorderItems;
    }
}
