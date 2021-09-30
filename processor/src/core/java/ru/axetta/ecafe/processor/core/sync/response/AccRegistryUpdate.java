/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.ResultOperation;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.06.14
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class AccRegistryUpdate implements AbstractToElement{
    private Map<Long, AccItem> accItemMap = new HashMap<Long, AccItem>();
    private Date maxTransactionDate; //Дата конечной выборки транзакций на процессинге
    private ResultOperation result = new ResultOperation();

    public Date getMaxTransactionDate() {
        Date currentDate = new Date();
        if(maxTransactionDate==null || maxTransactionDate.getTime()<currentDate.getTime()){
            maxTransactionDate = currentDate;
        }
        return maxTransactionDate;
    }

    public ResultOperation getResult() {
        return result;
    }

    public void setResult(ResultOperation result) {
        this.result = result;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        return toElement(document,CalendarUtils.getDateTimeFormatLocal());
    }


    public Element toElement(Document document, DateFormat timeFormat) throws Exception {
        Element element = document.createElement("AccRegistryUpdate");
        if (this.result != null) {
            XMLUtils.setAttributeIfNotNull(element, "ResCode", result.getCode());
            XMLUtils.setAttributeIfNotNull(element, "ResultMessage", result.getMessage());
        }
        element.setAttribute("Date", timeFormat.format(getMaxTransactionDate()));
        for (AccItem item : this.accItemMap.values()) {
            element.appendChild(item.toElement(document, timeFormat));
        }
        return element;
    }

    public void addAccountTransactionInfoV2(AccountTransactionExtended accountTransaction) {
        final Long idOfClient = accountTransaction.getIdofclient();
        final Client client = DAOReadonlyService.getInstance().findClientById(idOfClient);
        AccItem accItem = accItemMap.get(client.getIdOfClient());
        if(accItem==null){
            accItem = new AccItem();
            accItem.idOfClient = client.getIdOfClient();
            if(client.getSubBalance1()==null){
                accItem.subBalance1 = 0L;
            }else {
                accItem.subBalance1 = client.getSubBalance1();
            }
            accItem.balance = client.getBalance() - accItem.subBalance1;
            accItemMap.put(accItem.idOfClient, accItem);
        }
        final TransactionItem transactionItem = new TransactionItem();
        transactionItem.idOfTransaction = accountTransaction.getIdoftransaction();
        transactionItem.source = accountTransaction.getSource();
        transactionItem.transactionDateTime = accountTransaction.getTransactiondate();
        transactionItem.transactionType = accountTransaction.getSourcetype();
        transactionItem.sumMainBalance = accountTransaction.getTransactionsum();
        transactionItem.sumSubBalance = accountTransaction.getTransactionsubbalance1sum();
        transactionItem.sumComplex = accountTransaction.getComplexsum();
        transactionItem.sumSocDiscount = accountTransaction.getDiscountsum();
        transactionItem.orderType = accountTransaction.getOrdertype();
        accItem.transactionItems.add(transactionItem);
        if(maxTransactionDate==null || maxTransactionDate.getTime()<accountTransaction.getTransactiondate().getTime()){
            maxTransactionDate = accountTransaction.getTransactiondate();
        }
    }

    public void addAccountTransactionInfo(AccountTransaction accountTransaction) {
        final Client client = accountTransaction.getClient();
        AccItem accItem = accItemMap.get(client.getIdOfClient());
        if(accItem==null){
            accItem = new AccItem();
            accItem.idOfClient = client.getIdOfClient();
            if(client.getSubBalance1()==null){
                accItem.subBalance1 = 0L;
            }else {
                accItem.subBalance1 = client.getSubBalance1();
            }
            accItem.balance = client.getBalance() - accItem.subBalance1;
            accItemMap.put(accItem.idOfClient, accItem);
        }
        final TransactionItem transactionItem = new TransactionItem();
        transactionItem.idOfTransaction = accountTransaction.getIdOfTransaction();
        transactionItem.source = accountTransaction.getSource();
        transactionItem.transactionDateTime = accountTransaction.getTransactionTime();
        transactionItem.transactionType = accountTransaction.getSourceType();
        transactionItem.sumMainBalance = accountTransaction.getTransactionSum();
        transactionItem.sumSubBalance = accountTransaction.getTransactionSubBalance1Sum()==null?0L:accountTransaction.getTransactionSubBalance1Sum();
        accItem.transactionItems.add(transactionItem);
        if(maxTransactionDate==null || maxTransactionDate.getTime()<accountTransaction.getTransactionTime().getTime()){
            maxTransactionDate = accountTransaction.getTransactionTime();
        }
    }


    private static class AccItem{
        private long idOfClient; //Идентификатор клиента
        private long balance; //Размер баланса основного счета
        private long subBalance1; //Размер баланса субсчета 1
        private List<TransactionItem> transactionItems = new LinkedList<TransactionItem>(); // список транзакций клиента

        public Element toElement(Document document,DateFormat timeFormat) throws Exception{
            Element element = document.createElement("AI");
            element.setAttribute("IdC", Long.toString(idOfClient));
            element.setAttribute("B", Long.toString(balance));
            element.setAttribute("SB1", Long.toString(subBalance1));
            for (TransactionItem item : this.transactionItems) {
                element.appendChild(item.toElement(document, timeFormat));
            }
            return element;
        }

    }

    private static class TransactionItem{
        private long idOfTransaction;       // Идентификатор транзакции
        private String source;              // Ссылка на основание транзакции (номер клиентского заказа, номер пополнения платежа и т.п.)
        private Date transactionDateTime;   // Дата транзакции
        private int transactionType;        // Тип транзакции
        private long  sumMainBalance;       // Сумма изменения основного баланса
        private long  sumSubBalance;        // Сумма изменения основного баланса
        private long sumComplex;            // Сумма только по комплексам
        private long sumSocDiscount;        // Сумма скидки
        private int orderType;              // Тип заказа

        public Element toElement(Document document, DateFormat timeFormat) throws Exception {
            Element element = document.createElement("TI");
            element.setAttribute("IdT", Long.toString(idOfTransaction));
            element.setAttribute("SrcT", source);
            element.setAttribute("D", timeFormat.format(transactionDateTime));
            element.setAttribute("T", Integer.toString(transactionType));
            element.setAttribute("SM", Long.toString(sumMainBalance));
            element.setAttribute("SSB1", Long.toString(sumSubBalance));
            element.setAttribute("SC", Long.toString(sumComplex));
            element.setAttribute("SocD", Long.toString(sumSocDiscount));
            element.setAttribute("OT", Integer.toString(orderType));
            return element;
        }

    }

}
