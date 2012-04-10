/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;

import java.util.Date;

public class ClientAccountManager {

    public static AccountTransaction processAccountTransaction(Session session, Client client, Card card, long transactionSum,
            String source, int sourceType, Date transactionTime) throws Exception {
        AccountTransaction accountTransaction = new AccountTransaction(client, card, transactionSum, source,
                sourceType, transactionTime);
        session.save(accountTransaction);
        DAOUtils.changeClientBalance(session, client.getIdOfClient(), transactionSum);
        client.addBalanceNotForSave(transactionSum);
        return accountTransaction;
    }

    public static AccountTransaction cancelAccountTransaction(Session session, AccountTransaction transaction,
            Date transactionTime) throws Exception {
        AccountTransaction cancelTransaction = new AccountTransaction(transaction.getClient(), null,
                -transaction.getTransactionSum(), ""+transaction.getIdOfTransaction(),
                AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE, transactionTime);
        session.save(cancelTransaction);
        DAOUtils.changeClientBalance(session, transaction.getClient().getIdOfClient(), -transaction.getTransactionSum());
        return cancelTransaction;
    }
}
