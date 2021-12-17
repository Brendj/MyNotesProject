/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

public class CardResponseItem extends ResponseItem {
    public Long idOfCard;
    public Integer transitionState;

    public CardResponseItem() {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.idOfCard = null;
        this.transitionState = null;
    }

    public CardResponseItem(Long idOfCard, Integer transitionState) {
        this.code = OK;
        this.message = OK_MESSAGE;
        this.idOfCard = idOfCard;
        this.transitionState = transitionState;
    }

    public CardResponseItem(int code, String message) {
        this.code = code;
        this.message = message;
        this.idOfCard = null;
        this.transitionState = null;
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

    public static class CardAlreadyExistSecondRegisterAllowed extends Exception {
        public CardAlreadyExistSecondRegisterAllowed(String message) {
            super(message);
        }
    }

    public static class LongCardNoNotSpecified extends Exception {
        public LongCardNoNotSpecified(String message) {
            super(message);
        }
    }
}
