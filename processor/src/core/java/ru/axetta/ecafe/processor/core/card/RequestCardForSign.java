/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

import java.util.Calendar;

public class RequestCardForSign {
    private long uid;   //Физический номер
    private short typeId;  //тип носителя, 1 байт
    private long printed_no;  //Номер на идентификатора
    private Calendar issuedate;  //Дата выпуска
    private short memSize; //Более или менее 128 байт

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public short getTypeId() {
        return typeId;
    }

    public void setTypeId(short typeId) {
        this.typeId = typeId;
    }

    public long getPrinted_no() {
        return printed_no;
    }

    public void setPrinted_no(long printed_no) {
        this.printed_no = printed_no;
    }

    public Calendar getIssuedate() {
        return issuedate;
    }

    public void setIssuedate(Calendar issuedate) {
        this.issuedate = issuedate;
    }


    public short getMemSize() {
        return memSize;
    }

    public void setMemSize(short memSize) {
        this.memSize = memSize;
    }
}
