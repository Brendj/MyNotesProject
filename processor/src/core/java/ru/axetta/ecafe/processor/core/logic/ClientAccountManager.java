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
        AccountTransaction accountTransaction = new AccountTransaction(client, card, client.getContractId(), transactionSum, source,
                sourceType, transactionTime);
        accountTransaction.setOrg(client.getOrg());
        session.save(accountTransaction);
        DAOUtils.changeClientBalance(session, client.getIdOfClient(), transactionSum);
        client.addBalanceNotForSave(transactionSum);
        return accountTransaction;
    }

    public static AccountTransaction processAccountTransaction(Session session, Client client, Card card, long transactionSum,
            String source, int sourceType, Date transactionTime, Integer subBalance) throws Exception {
        final Long subBalanceNum = client.getContractId() * 100 + subBalance;
        AccountTransaction accountTransaction = new AccountTransaction(client, card, subBalanceNum, transactionSum, source,
                sourceType, transactionTime);
        accountTransaction.setOrg(client.getOrg());
        if(subBalance>0){
            Long sum = client.getSubBalance(subBalance);
            if(sum ==null) sum=0L;
            accountTransaction.setSubBalance1BeforeTransaction(sum);
            accountTransaction.setTransactionSubBalance1Sum(transactionSum);
        }
        session.save(accountTransaction);
        DAOUtils.changeClientBalance(session, client.getIdOfClient(), transactionSum);
        client.addBalanceNotForSave(transactionSum);
        if(subBalance>0){
            DAOUtils.changeClientSubBalance(session, client.getIdOfClient(), transactionSum, subBalance, client.getSubBalanceIsNull(subBalance));
            client.addSubBalanceNotForSave(transactionSum, subBalance);
        }
        return accountTransaction;
    }

    public static AccountTransaction cancelAccountTransaction(Session session, AccountTransaction transaction,
            Date transactionTime) throws Exception {
        final Client client = transaction.getClient();
        AccountTransaction cancelTransaction = new AccountTransaction(client, null, client.getContractId(),
                -transaction.getTransactionSum(), ""+transaction.getIdOfTransaction(),
                AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE, transactionTime);
        cancelTransaction.setOrg(transaction.getOrg());
        session.save(cancelTransaction);
        DAOUtils.changeClientBalance(session, client.getIdOfClient(), -transaction.getTransactionSum());
        return cancelTransaction;
    }

    public static AccountTransaction cancelAccountTransaction(Session session, AccountTransaction transaction,
            Date transactionTime, Integer subBalance) throws Exception {
        final Client client = transaction.getClient();
        final Long subBalanceNum = client.getContractId() * 100 + subBalance;
        String source = Long.toString(transaction.getIdOfTransaction());
        Long transactionSum = transaction.getTransactionSum();
        AccountTransaction cancelTransaction = new AccountTransaction(client, null, subBalanceNum, -transactionSum, source,
                    AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE, transactionTime);
        cancelTransaction.setOrg(transaction.getOrg());
        session.save(cancelTransaction);

        DAOUtils.changeClientBalance(session, client.getIdOfClient(), -transactionSum);
        if(subBalance>0){
            DAOUtils.changeClientSubBalance(session, client.getIdOfClient(), transactionSum, subBalance, client.getSubBalanceIsNull(subBalance));
            client.addSubBalanceNotForSave(transactionSum, subBalance);
        }
        return cancelTransaction;
    }
}
