/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//В донном класе будет содержаться конкретный массив отдаваемых дней для конмбинации орг + групп
public class DataOfDates {
    private String guid;
    private String groupName;
    private List<Date> dates = new ArrayList<>();

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Date> getDates() {
        return dates;
    }

    public void setDates(List<Date> dates) {
        this.dates = dates;
    }
}
