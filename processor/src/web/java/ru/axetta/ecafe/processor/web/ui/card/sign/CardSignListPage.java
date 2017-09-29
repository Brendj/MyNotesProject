/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card.sign;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CardSign;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 28.09.2017.
 */
@Component
@Scope("session")
public class CardSignListPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(CardSignListPage.class);
    private List<CardSignItem> cards = new ArrayList<CardSignItem>();

    @Override
    public void onShow() throws Exception {
        fill();
    }

    private void fill() {
        Session session = null;
        Transaction transaction = null;
        try {
            getCards().clear();
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(CardSign.class);
            criteria.addOrder(Order.asc("idOfCardSign"));
            List<CardSign> list = criteria.list();
            for (CardSign cardSign : list) {
                CardSignItem item = new CardSignItem(cardSign);
                getCards().add(item);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in cardSign show list page: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public String getPageFilename() {
        return "card/sign/list";
    }

    public List<CardSignItem> getCards() {
        return cards;
    }

    public void setCards(List<CardSignItem> cards) {
        this.cards = cards;
    }
}
