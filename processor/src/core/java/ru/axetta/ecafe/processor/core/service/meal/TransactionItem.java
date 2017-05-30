/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import generated.spb.meal.TransactionType;

import ru.axetta.ecafe.processor.core.persistence.AccountTransaction;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Order;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */

public class TransactionItem {

    private String transactionId;
    private Date transactionDate;
    private Long balance;
    private Long amount;
    private String cardName;
    private String foodName;
    private Integer foodAmount;
    private String directionType;

    public TransactionItem(String transactionId, Date transactionDate, Long balance, Long amount, String cardName,
            String foodName, Integer foodAmount, String directionType) {
        this.transactionId = transactionId;
        this.transactionDate = transactionDate;
        this.balance = balance;
        this.amount = amount;
        this.cardName = cardName;
        this.foodName = foodName;
        this.foodAmount = foodAmount;
        this.directionType = directionType;
    }

    public TransactionItem getTransactionItem(AccountTransaction accountTransaction, Order order) {
        return new TransactionItem(accountTransaction.getIdOfTransaction().toString(), accountTransaction.getTransactionTime(),
                accountTransaction.getBalanceAfterTransaction(), accountTransaction.getTransactionSum(),
                Card.TYPE_NAMES[accountTransaction.getCard().getCardType()], "", 1, "expense");
    }

    public static TransactionType getTransactionType(TransactionItem item) throws Exception {
        TransactionType transactionType = new TransactionType();

        transactionType.setTransactionId(item.getTransactionId());
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(item.getTransactionDate());
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        transactionType.setTransactionDate(date);
        transactionType.setBalance(BigDecimal.valueOf(item.getBalance()));
        transactionType.setAmount(BigDecimal.valueOf(item.getAmount()));
        transactionType.setCardName(item.getCardName());
        transactionType.setFoodName(item.getFoodName());
        transactionType.setFoodAmount(BigInteger.valueOf(item.getFoodAmount()));
        transactionType.setDirectionType(item.getDirectionType());

        return transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(Integer foodAmount) {
        this.foodAmount = foodAmount;
    }

    public String getDirectionType() {
        return directionType;
    }

    public void setDirectionType(String directionType) {
        this.directionType = directionType;
    }
}
