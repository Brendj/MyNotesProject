/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.persistence.CardTransitionState;

public class CardResponseItem extends ResponseItem {
    public Long idOfCard;
    public CardTransitionState transitionState;

    public CardResponseItem() {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.idOfCard = null;
        this.transitionState = CardTransitionState.OWN;
    }

    public CardResponseItem(Long idOfCard, CardTransitionState transitionState) {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.idOfCard = idOfCard;
        this.transitionState = transitionState;
    }

    public CardResponseItem(int code, String message) {
        this.code = code;
        this.message = message;
        this.idOfCard = null;
        this.transitionState = CardTransitionState.OWN;
    }

    public static class CardAlreadyExist extends Exception {
        public CardAlreadyExist(String message) {
            super(message);
        }
    }

    public static class CardAlreadyExistInYourOrg extends Exception {
        public CardAlreadyExistInYourOrg(String message) {
            super(message);
        }
    }
}
