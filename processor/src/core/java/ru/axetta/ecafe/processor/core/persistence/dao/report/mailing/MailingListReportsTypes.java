/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.report.mailing;

/**
 * Created by Akmukov on 16.02.2016.
 */
public enum MailingListReportsTypes {
    NUTRITION (0),
    VISITS(1),
    SOME_LIST_1(2),
    SOME_LIST_2(3),
    UNKNOWN(-1);

    private final int code;

    MailingListReportsTypes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MailingListReportsTypes getByCode(int code) {
        for (MailingListReportsTypes type: MailingListReportsTypes.values()){
            if (type.getCode() == code){
                return type;
            }
        }
        return UNKNOWN;
    }

}
