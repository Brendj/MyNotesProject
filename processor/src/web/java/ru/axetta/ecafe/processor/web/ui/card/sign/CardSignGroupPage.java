/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card.sign;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 14.08.13
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class CardSignGroupPage extends BasicWorkspacePage {

    private CardSignItem currentCard;

    public CardSignItem getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(CardSignItem currentCard) {
        this.currentCard = currentCard;
    }
}
