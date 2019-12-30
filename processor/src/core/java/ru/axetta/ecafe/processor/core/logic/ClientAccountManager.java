/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;

import java.util.Date;

public class ClientAccountManager {


    /**
     * списание средств при получении заказа АП и заказа платного плана
     * (списание должно проходить сначала с субсчета АП, в случае если сумма на счете АП не достаточна -
     * то списывать суб счет до 0, не достающий остаток списывать с основного счета)
     *
     * @param session
     * @param client
     * @param card
     * @param transactionSum
     * @param source
     * @param sourceType
     * @param transactionTime
     * @return
     */

    public static AccountTransaction checkBalanceAndProcessAccountTransaction(Session session, Client client, Card card,
          long transactionSum, String source, int sourceType, Date transactionTime, Long idOfSourceOrg, Long orderId) throws Exception {
        AccountTransaction accountTransaction = new AccountTransaction(client, card, client.getContractId(), transactionSum, source,
              sourceType, transactionTime);
        //accountTransaction.setOrg(client.getOrg());
        Org o = (Org)session.load(Org.class, idOfSourceOrg);
        accountTransaction.setOrg(o);
        final Long sum = client.getSubBalance(1);
        // смотрм будущий остаток на счете
        final long diff = sum+transactionSum;
        if(diff<=0){
            // если субсчет уходит в минус то загоняем его в 0
            // остальное списываем с основоного
            accountTransaction.setSubBalance1BeforeTransaction(sum);
            accountTransaction.setTransactionSubBalance1Sum(transactionSum-diff);
            accountTransaction.setBalanceAfterTransaction(client.getBalance() + transactionSum);
            session.save(accountTransaction);
            DAOUtils.changeClientBalance(session, client, transactionSum, transactionTime, orderId);
            //client.addBalanceNotForSave(transactionSum);
            DAOUtils.changeClientSubBalance(session, client.getIdOfClient(), transactionSum-diff, 1, client.getSubBalanceIsNull(1));
            client.addSubBalanceNotForSave(transactionSum-diff, 1);
        } else {
            // на субсчете хватает средства списываем с АП
            accountTransaction.setSubBalance1BeforeTransaction(sum);
            accountTransaction.setTransactionSubBalance1Sum(transactionSum);
            accountTransaction.setBalanceAfterTransaction(client.getBalance() + transactionSum);
            session.save(accountTransaction);
            DAOUtils.changeClientBalance(session, client, transactionSum, transactionTime, orderId);
            //client.addBalanceNotForSave(transactionSum);
            DAOUtils.changeClientSubBalance(session, client.getIdOfClient(), transactionSum, 1, client.getSubBalanceIsNull(1));
            client.addSubBalanceNotForSave(transactionSum, 1);
        }
        return accountTransaction;
    }

    public static AccountTransaction processAccountTransaction(Session session, Client client, Card card, long transactionSum,
            String source, int sourceType, Long idOfSourceOrg, Date transactionTime, Long orderID) throws Exception {
        AccountTransaction accountTransaction = new AccountTransaction(client, card, client.getContractId(), transactionSum, source,
                sourceType, transactionTime);
        if (idOfSourceOrg != null) {
            Org o = (Org)session.load(Org.class, idOfSourceOrg);
            accountTransaction.setOrg(o);
        } else {
            accountTransaction.setOrg(client.getOrg());
        }
        accountTransaction.setBalanceAfterTransaction(client.getBalance() + transactionSum);
        session.save(accountTransaction);
        DAOUtils.changeClientBalance(session, client, transactionSum, transactionTime, orderID);
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
        accountTransaction.setBalanceAfterTransaction(client.getBalance() + transactionSum);
        session.save(accountTransaction);
        DAOUtils.changeClientBalance(session, client, transactionSum, transactionTime, null);
        //client.addBalanceNotForSave(transactionSum);
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
                -transaction.getTransactionSum(), Long.toString(transaction.getIdOfTransaction()),
                AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE, transactionTime);
        cancelTransaction.setOrg(transaction.getOrg());
        if(transaction.getTransactionSubBalance1Sum()!=null){
            cancelTransaction.setSubBalance1BeforeTransaction(client.getSubBalance(1));
            cancelTransaction.setTransactionSubBalance1Sum(-transaction.getTransactionSubBalance1Sum());
        }
        cancelTransaction.setBalanceAfterTransaction(client.getBalance() + (-transaction.getTransactionSum()));
        session.save(cancelTransaction);
        DAOUtils.changeClientBalance(session, client, -transaction.getTransactionSum(), transactionTime, null);
        if(transaction.getTransactionSubBalance1Sum()!=null){
            DAOUtils.changeClientSubBalance1(session, client.getIdOfClient(), -transaction.getTransactionSubBalance1Sum(), false);
        }
        return cancelTransaction;
    }

    /*public static AccountTransaction cancelAccountTransaction(Session session, AccountTransaction transaction,
            Date transactionTime, Integer subBalance) throws Exception {
        final Client client = transaction.getClient();
        final Long subBalanceNum = client.getContractId() * 100 + subBalance;
        String source = Long.toString(transaction.getIdOfTransaction());
        Long transactionSum = transaction.getTransactionSum();
        AccountTransaction cancelTransaction = new AccountTransaction(client, null, subBalanceNum, -transactionSum, source,
                    AccountTransaction.CANCEL_TRANSACTION_SOURCE_TYPE, transactionTime);
        cancelTransaction.setOrg(transaction.getOrg());
        cancelTransaction.setBalanceAfterTransaction(client.getBalance() + (-transactionSum));
        session.save(cancelTransaction);

        DAOUtils.changeClientBalance(session, client.getIdOfClient(), -transactionSum, client.getOrg().getIdOfOrg(), transactionTime);
        if(subBalance>0){
            DAOUtils.changeClientSubBalance(session, client.getIdOfClient(), transactionSum, subBalance, client.getSubBalanceIsNull(subBalance));
            client.addSubBalanceNotForSave(transactionSum, subBalance);
        }
        return cancelTransaction;
    }*/
}
