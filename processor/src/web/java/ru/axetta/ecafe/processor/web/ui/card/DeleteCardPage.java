/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class DeleteCardPage extends BasicWorkspacePage {

    public void deleteAt(Session session, Long idOfCard) throws HibernateException {
        // Заблокировано
        //Card card = (Card) session.load(Card.class, idOfCard);
        //session.delete(card);
    }

}