/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Set;

public enum ClientDiscountHistoryOperationTypeEnum {
    ADD, CHANGE, DELETE;

    public static ClientDiscountHistoryOperationTypeEnum getType(Set<CategoryDiscount> oldDiscounts,
            Set<CategoryDiscount> newDiscounts, CategoryDiscount discount) {
        if(oldDiscounts.contains(discount) && newDiscounts.contains(discount)){
            return CHANGE;
        } else if(newDiscounts.contains(discount)){
            return ADD;
        } else {
            return DELETE;
        }
    }
}
