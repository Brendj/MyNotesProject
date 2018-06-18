/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.persistence.MenusCalendar;
import ru.axetta.ecafe.processor.core.sync.BaseItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 14.06.2018.
 */
public class MenusCalendarItem extends BaseItem {
    private Long idOfMenusCalendar;
    private String guid;
    private Long idOfOrg;
    private Long idOfMenu;
    private Date startDate;
    private Date endDate;
    private Boolean sixWorkDays;
    private Long version;
    private Boolean deletedState;
    private List<MenusCalendarDateItem> items;
    private String errorMessage;

    public MenusCalendarItem() {

    }

    public MenusCalendarItem(MenusCalendar menusCalendar, List<MenusCalendarDateItem> items) {
        this.guid = menusCalendar.getGuid();
        this.idOfOrg = menusCalendar.getOrg().getIdOfOrg();
        this.idOfMenu = menusCalendar.getIdOfMenu();
        this.startDate = menusCalendar.getStartDate();
        this.endDate = menusCalendar.getEndDate();
        this.sixWorkDays = menusCalendar.getSixWorkDays();
        this.version = menusCalendar.getVersion();
        this.deletedState = menusCalendar.getDeletedState();
        this.items = items;
    }

    public MenusCalendarItem(String guid, Long idOfOrg, Long idOfMenu, Date startDate, Date endDate,
            Boolean sixWorkDays, Long version, Boolean deletedState, List<MenusCalendarDateItem> items, String errorMessage) {
        this.guid = guid;
        this.idOfOrg = idOfOrg;
        this.idOfMenu = idOfMenu;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sixWorkDays = sixWorkDays;
        this.version = version;
        this.deletedState = deletedState;
        this.items = items;
        this.errorMessage = errorMessage;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "OrgId", idOfOrg);
        XMLUtils.setAttributeIfNotNull(element, "IdOfMenu", idOfMenu);
        XMLUtils.setAttributeIfNotNull(element, "SDate", CalendarUtils.dateShortToStringFullYear(startDate));
        XMLUtils.setAttributeIfNotNull(element, "EDate", CalendarUtils.dateShortToStringFullYear(endDate));
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        XMLUtils.setAttributeIfNotNull(element, "SixWorkDays", sixWorkDays);
        if (deletedState) {
            XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        }
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        return element;
    }

    public static MenusCalendarItem build(Node itemNode) {
        String guid = null;
        Long idOfOrg = null;
        Long idOfMenu = null;
        Date startDate = null;
        Date endDate = null;
        Boolean sixWorkDays = null;
        Long version = null;
        Boolean deletedState = null;
        StringBuilder errorMessage = new StringBuilder();
        List<MenusCalendarDateItem> items = new ArrayList<MenusCalendarDateItem>();

        guid = XMLUtils.getAttributeValue(itemNode, "Guid");
        if (null == guid || StringUtils.isEmpty(guid)) {
            errorMessage.append("Attribute Guid not found");
        }
        idOfOrg = getOrgId(itemNode, errorMessage);
        String str = XMLUtils.getAttributeValue(itemNode, "IdOfMenu");
        idOfMenu = Long.parseLong(str);
        startDate = getDateValue(itemNode, "SDate", errorMessage);
        endDate = getDateValue(itemNode, "EDate", errorMessage);
        str = XMLUtils.getAttributeValue(itemNode, "SixWorkDays");
        sixWorkDays = str == null ? false : str.equals("1");
        str = XMLUtils.getAttributeValue(itemNode, "D");
        deletedState = str == null ? false : str.equals("1");
        str = XMLUtils.getAttributeValue(itemNode, "V");
        version = Long.parseLong(str);
        Node dateNode = itemNode.getFirstChild();
        while (null != dateNode) {
            if (Node.ELEMENT_NODE == dateNode.getNodeType() && dateNode.getNodeName().equals("CDI")) {
                MenusCalendarDateItem dateItem = MenusCalendarDateItem.build(dateNode);
                items.add(dateItem);
            }
            dateNode = dateNode.getNextSibling();
        }
        return new MenusCalendarItem(guid, idOfOrg, idOfMenu, startDate, endDate, sixWorkDays, version, deletedState, items, errorMessage.toString());
    }

    public Long getIdOfMenusCalendar() {
        return idOfMenusCalendar;
    }

    public void setIdOfMenusCalendar(Long idOfMenusCalendar) {
        this.idOfMenusCalendar = idOfMenusCalendar;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    public void setIdOfMenu(Long idOfMenu) {
        this.idOfMenu = idOfMenu;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getSixWorkDays() {
        return sixWorkDays;
    }

    public void setSixWorkDays(Boolean sixWorkDays) {
        this.sixWorkDays = sixWorkDays;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public List<MenusCalendarDateItem> getItems() {
        return items;
    }

    public void setItems(List<MenusCalendarDateItem> items) {
        this.items = items;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
