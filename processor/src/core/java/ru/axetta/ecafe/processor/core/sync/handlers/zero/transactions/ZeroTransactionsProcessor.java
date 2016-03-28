/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.zero.transactions;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfZeroTransaction;
import ru.axetta.ecafe.processor.core.persistence.ZeroTransaction;
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
 * Date: 28.03.16
 * Time: 13:31
 * To change this template use File | Settings | File Templates.
 */
public class ZeroTransactionsProcessor extends AbstractProcessor<ResZeroTransactions> {
    private static final Logger logger = LoggerFactory.getLogger(ZeroTransactionsProcessor.class);
    private final ZeroTransactions zeroTransactions;
    private final List<ResZeroTransactionItem> resZeroTransactionItems;

    public ZeroTransactionsProcessor(Session persistenceSession, ZeroTransactions zeroTransactions) {
        super(persistenceSession);
        this.zeroTransactions = zeroTransactions;
        resZeroTransactionItems = new ArrayList<ResZeroTransactionItem>();
    }

    @Override
    public ResZeroTransactions process() throws Exception {
        ResZeroTransactions result = new ResZeroTransactions();
        List<ResZeroTransactionItem> items = new ArrayList<ResZeroTransactionItem>();
        try {
            ResZeroTransactionItem resItem;
            Long nextVersion = DAOUtils.nextVersionByZeroTransaction(session);
            for (ZeroTransactionItem item : zeroTransactions.getItems()) {

                if (item.getResCode().equals(ZeroTransactionItem.ERROR_CODE_ALL_OK)) {
                    CompositeIdOfZeroTransaction compositeId = new CompositeIdOfZeroTransaction(item.getIdOfOrg(), item.getDate(), item.getIdOfCriteria());
                    ZeroTransaction zt = DAOUtils.findZeroTransaction(session, compositeId);
                    Integer targetLevel = item.getTargetLevel();
                    Integer actualLevel = item.getActualLevel();
                    Integer criteriaLevel = item.getCriteriaLevel();
                    Integer idOfReason = item.getIdOfReason();
                    String comment = item.getComment();

                    if (zt == null) {
                        zt = new ZeroTransaction(compositeId, targetLevel, actualLevel, criteriaLevel, idOfReason, comment);
                    }
                    zt.setTargetLevel(targetLevel);
                    zt.setActualLevel(actualLevel);
                    zt.setCriteriaLevel(criteriaLevel);
                    zt.setComment(comment);
                    zt.setVersion(nextVersion);

                    session.saveOrUpdate(zt);

                    resItem = new ResZeroTransactionItem(zt);
                    resItem.setResultCode(item.getResCode());
                } else {
                    resItem = new ResZeroTransactionItem();
                    resItem.setOrgId(item.getIdOfOrg());
                    resItem.setDate(item.getDate());
                    resItem.setTargetLevel(item.getTargetLevel());
                    resItem.setActualLevel(item.getActualLevel());
                    resItem.setCriteriaLevel(item.getCriteriaLevel());
                    resItem.setIdOfReason(item.getIdOfReason());
                    resItem.setComment(item.getComment());
                    resItem.setResultCode(item.getResCode());
                    resItem.setErrorMessage(item.getErrorMessage());
                }
                items.add(resItem);
            }
            session.flush();
        }
        catch (Exception e) {
            logger.error("Error saving ZeroTransactions", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public ZeroTransactionData processData() throws Exception {
        ZeroTransactionData result = new ZeroTransactionData();
        List<ResZeroTransactionItem> items = new ArrayList<ResZeroTransactionItem>();
        ResZeroTransactionItem resItem;
        List<ZeroTransaction> list = DAOUtils.getZeroTransactionsForOrgSinceVersion(session,
                zeroTransactions.getIdOfOrgOwner(), zeroTransactions.getMaxVersion());
        for (ZeroTransaction zt : list) {
            resItem = new ResZeroTransactionItem(zt);
            items.add(resItem);
        }

        result.setItems(items);
        return result;
    }

    public List<ResZeroTransactionItem> getResZeroTransactionItems() {
        return resZeroTransactionItems;
    }
}
