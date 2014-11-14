/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.model.feedingandvisit;

import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.PlanOrderItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

/**
 * User: shamil
 * Date: 06.10.14
 * Time: 13:55
 */
public class Row {

    private Long clientId; // ид клиента или ид rule если total field
    private String name;
    private Long idOfOrg;
    private String orgName;
    private Integer day;

    public static final String ENTRY_DEFAULT = "H";
    public static final String ENTRY_PAID = "X";
    private String entry = ENTRY_DEFAULT;
    private Long enter = null;
    private Long exit = null;

    private String groupname;
    private boolean totalRow = false; // запись относиться к итого
    private int totalCount = 0;


    private Integer color = 0; //  0 не оплачен . 1 оплачен
    public static final int COLOR_PAID = 1;   //оплачен
    public static final int COLOR_NOT_PAID = 0; // не оплачен

    public Row() {
    }

    public Row(String name, Integer day) {
        this.name = name;
        this.day = day;
    }

    public Row(Long clientId, String name, Integer day, String groupname) {
        this.clientId = clientId;
        this.name = name;
        this.day = day;
        this.groupname = groupname;
    }
    public Row(Long clientId, String orgName, String name, Integer day, String groupname) {
        this.clientId = clientId;
        this.orgName = orgName;
        this.name = name;
        this.day = day;
        this.groupname = groupname;
    }    public Row(Long clientId, long idOfOrg, String orgName, String name, Integer day, String groupname) {
        this.clientId = clientId;
        this.idOfOrg = idOfOrg;
        this.orgName = orgName;
        this.name = name;
        this.day = day;
        this.groupname = groupname;
    }

    public Row(Long clientId, String name, Integer day, String entry, Integer color) {
        this.clientId = clientId;
        this.name = name;
        this.day = day;
        this.entry = entry;
        this.color = color;
    }

    public Row(DAOEnterEventSummaryModel model) {
        this.clientId = model.getIdOfClient();
        this.name = model.getVisitorFullName();
        this.day = CalendarUtils.getDayOfMonth(model.getEvtDateTime());
        this.groupname = model.getGroupName();
        switch (model.getPassDirection()) {
            case 0:
            case 6:
                enter = model.getEvtDateTime();
                break;

            case 1:
            case 7:
                exit = model.getEvtDateTime();
                break;
        }
    }



    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getEntry() {
        if (totalRow) {
            return "" + totalCount;
        } else if (enter == null && exit == null) {
            return color == 0 ? ENTRY_DEFAULT : ENTRY_PAID;
        }

        return "" + ((enter != null) ? CalendarUtils.timeToString(enter) : "...") + " - " + ((exit != null)
                ? CalendarUtils.timeToString(exit) : "...");
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public void update(DAOEnterEventSummaryModel model) {
        if (day != CalendarUtils.getDayOfMonth(model.getEvtDateTime())) {
            return;
        }
        switch (model.getPassDirection()) {
            case 0:
            case 6:
                if ((enter == null) || (enter > model.getEvtDateTime())) {
                    enter = model.getEvtDateTime();
                }
                break;

            case 1:
            case 7:
                if ((exit == null) || (exit < model.getEvtDateTime())) {
                    exit = model.getEvtDateTime();
                }
                break;
        }
    }

    public void incrementcount() {
        totalCount++;
    }

    public void decrementcount() {
        totalCount--;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public boolean isTotalRow() {
        return totalRow;
    }

    public void setTotalRow(boolean totalRow) {
        this.totalRow = totalRow;
    }

    public void update(PlanOrderItem item) {
        setColor(COLOR_PAID);
    }

    public void updateTotal(OrderItem item) {
        totalCount += item.getQty();
    }

    public void update(OrderItem item) {
        setColor(COLOR_PAID);
    }
}
