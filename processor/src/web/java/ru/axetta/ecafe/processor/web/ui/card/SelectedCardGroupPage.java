/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card;

import ru.axetta.ecafe.processor.core.card.CardNoFormat;
import ru.axetta.ecafe.processor.core.card.CardPrintedNoFormat;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class SelectedCardGroupPage extends BasicWorkspacePage {

    private String shortName;

    public String getShortName() {
        return shortName;
    }

    public void fill(Session session, Long idOfCard) throws Exception {
        Card card = (Card) session.load(Card.class, idOfCard);
        if (null == card) {
            this.shortName = null;
        } else {
            Long cardPrintedNo = card.getCardPrintedNo();
            if (null != cardPrintedNo) {
                shortName = String.format("%s (%s)", CardNoFormat.format(card.getCardNo()),
                        CardPrintedNoFormat.format(cardPrintedNo));
            } else {
                shortName = CardNoFormat.format(card.getCardNo());
            }
        }
    }

}