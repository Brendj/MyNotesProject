/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.visitordogm;

import ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 12.10.16
 * Time: 10:37
 */
@Component
@Scope("session")
public class VisitorDogmCardGroupPage extends BasicWorkspacePage {

    private CardItem currentCard;

    public CardItem getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(CardItem currentCard) {
        this.currentCard = currentCard;
    }
}
