/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

import ru.axetta.ecafe.processor.core.persistence.User;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.jws.soap.SOAPBinding;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 24.03.2010
 * Time: 13:30:21
 * To change this template use File | Settings | File Templates.
 */
public interface CardManager {

    Long createCard(Session persistenceSession, Transaction persistenceTransaction, Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo) throws Exception;

    Long createCard(Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo) throws Exception;

    Long createCard(Long idOfClient, long cardNo, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, Long cardPrintedNo, User user) throws Exception;;

    void createTempCard(Long idOfOrg, long cardNo, String cardPrintedNo) throws Exception;

    void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId) throws Exception;

    void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId, User user) throws Exception;

    void changeCardOwner(Long idOfClient, Long cardNo, Date changeTime, Date validTime) throws Exception;

    void changeCardOwner(Long idOfClient, Long cardNo, Date changeTime, Date validTime, User cardOperatorUser) throws Exception;

    Long createNewCard(Session persistenceSession, Transaction persistenceTransaction, long cardNo, Long cardPrintedNo, Integer cardType) throws Exception;

    Long createNewCard(long cardNo, Long cardPrintedNo, Integer cardType) throws Exception;

    NewCardItem getNewCardPrintedNo(Session persistenceSession, Transaction persistenceTransaction, long cardNo) throws Exception;

    NewCardItem getNewCardPrintedNo(long cardNo) throws Exception;

    class NewCardItem{
        private Long cardPrintedNo;
        private Integer cardType;

        public NewCardItem(Long cardPrintedNo, Integer cardType) {
            this.cardPrintedNo = cardPrintedNo;
            this.cardType = cardType;
        }

        public Long getCardPrintedNo() {
            return cardPrintedNo;
        }

        public void setCardPrintedNo(Long cardPrintedNo) {
            this.cardPrintedNo = cardPrintedNo;
        }

        public Integer getCardType() {
            return cardType;
        }

        public void setCardType(Integer cardType) {
            this.cardType = cardType;
        }
    }

}
