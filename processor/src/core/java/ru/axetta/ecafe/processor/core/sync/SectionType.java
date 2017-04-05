/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 07.07.16
 * Time: 10:37
 */
public enum  SectionType {
    PAYMENT_REGISTRY(1),
    ORGANIZATIONS_STRUCTURE(2),
    ACC_REGISTRY(3),
    ACC_INC_REGISTRY(4),
    ACCOUNT_OPERATIONS_REGISTRY(5),
    ACCOUNTS_REGISTRY(6),
    MENU(7),
    PROHIBITIONS_MENU(8),
    CATEGORIES_DISCOUNTS_RULES(9),
    CORRECTING_NUMBERS_ORDERS_REGISTRY(10),
    CLIENT_REGISTRY(11),
    DIARY(12),
    ENTER_EVENTS(13),
    DIRECTIVES(14),
    GOODS_BASIC_BASKET_DATA(15),
    ORG_OWNER_DATA(16),
    TEMP_CARDS_OPERATIONS(17),
    REESTR_TALOON_APPROVAL(18),
    ZERO_TRANSACTIONS(19),
    SPECIAL_DATES(20),
    MIGRANTS(21),
    RO(22),
    LAST_TRANSACTION(23),
    INFO_MESSAGE(24);

    private int type;

    private SectionType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
