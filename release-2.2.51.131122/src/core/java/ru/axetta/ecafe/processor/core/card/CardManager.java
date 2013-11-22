/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

import org.hibernate.Session;
import org.hibernate.Transaction;

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

    void createTempCard(Long idOfClient, long cardNo, String cardPrintedNo) throws Exception;

    void updateCard(Long idOfClient, Long idOfCard, int cardType, int state, Date validTime, int lifeState,
            String lockReason, Date issueTime, String externalId) throws Exception;

}
