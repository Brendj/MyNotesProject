/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.meal;

import generated.spb.meal.MealData;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */

public class MealDataItem {

    private String organizationUid;
    private String studentUid;
    private String cardUid;
    private List<TransactionItem> transactionItems;

    public static MealData getMealData(MealDataItem item) throws Exception {
        MealData mealData = new MealData();

        MealData.IdentityInfo identityInfo = new MealData.IdentityInfo();
        identityInfo.setOrganizationUid(item.getOrganizationUid());
        identityInfo.setStudentUid(item.getStudentUid());
        identityInfo.setCardUid(item.getCardUid());
        mealData.setIdentityInfo(identityInfo);

        for (TransactionItem transactionItem : item.getTransactionItems()) {
            mealData.getTransaction().add(TransactionItem.getTransactionType(transactionItem));
        }

        return mealData;
    }

    public MealDataItem(String organizationUid, String studentUid, String cardUid,
            TransactionItem transactionItem) {
        this.organizationUid = organizationUid;
        this.studentUid = studentUid;
        this.cardUid = cardUid;
        this.transactionItems = new ArrayList<TransactionItem>();
        this.getTransactionItems().add(transactionItem);
    }

    public String getOrganizationUid() {
        return organizationUid;
    }

    public void setOrganizationUid(String organizationUid) {
        this.organizationUid = organizationUid;
    }

    public String getStudentUid() {
        return studentUid;
    }

    public void setStudentUid(String studentUid) {
        this.studentUid = studentUid;
    }

    public String getCardUid() {
        return cardUid;
    }

    public void setCardUid(String cardUid) {
        this.cardUid = cardUid;
    }

    public List<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    public void setTransactionItems(List<TransactionItem> transactionItems) {
        this.transactionItems = transactionItems;
    }
}
