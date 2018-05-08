/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.cardoperator;

import ru.axetta.ecafe.processor.core.logic.CardManagerProcessor;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("session")
@Component
public class CardRegistrationConfirm extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(CardRegistrationConfirm.class);
    private String message;

    public String getMessage() {
        return message;
    }

    public CardRegistrationConfirm(){
        message = null;
    }

    public void prepareADialogue(String cardType) {
        message = "Внимание! За предоставленную услугу с лицевого счета клиента будет списана сумма в размере "
                + (cardType.equals("Mifare")?
                CardManagerProcessor.PRICE_OF_MIFARE / 100 + " руб.":CardManagerProcessor.PRICE_OF_MIFARE_BRACELET / 100 + " руб.")
                + " за карту " + cardType;
    }
}
