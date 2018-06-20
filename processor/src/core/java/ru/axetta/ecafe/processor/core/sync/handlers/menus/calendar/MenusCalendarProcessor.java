/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.menus.calendar;

import ru.axetta.ecafe.processor.core.persistence.MenusCalendar;
import ru.axetta.ecafe.processor.core.persistence.MenusCalendarDate;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by i.semenov on 14.06.2018.
 */
public class MenusCalendarProcessor extends AbstractProcessor<ResMenusCalendar> {
    private static final Logger logger = LoggerFactory.getLogger(MenusCalendarProcessor.class);
    private MenusCalendarSupplierRequest menusCalendarSupplierRequest;
    private MenusCalendarRequest menusCalendarRequest;

    public MenusCalendarProcessor(Session persistenceSession, MenusCalendarSupplierRequest menusCalendarSupplierRequest) {
        super(persistenceSession);
        this.menusCalendarSupplierRequest = menusCalendarSupplierRequest;
    }

    public MenusCalendarProcessor(Session persistenceSession, MenusCalendarRequest menusCalendarRequest) {
        super(persistenceSession);
        this.menusCalendarRequest = menusCalendarRequest;
    }

    @Override
    public ResMenusCalendar process() {
        ResMenusCalendar result = new ResMenusCalendar();
        List<ResMenusCalendarItem> items = new ArrayList<ResMenusCalendarItem>();
        Map<Long, Org> orgs  = new HashMap<Long, Org>();
        try {
            Long nextVersion = DAOUtils.nextVersionByMenusCalendar(session);
            for (MenusCalendarItem item : menusCalendarSupplierRequest.getItems()) {
                if (!StringUtils.isEmpty(item.getErrorMessage())) {
                    ResMenusCalendarItem resItem = new ResMenusCalendarItem();
                    resItem.setGuid(item.getGuid());
                    resItem.setResultCode(1);
                    resItem.setErrorMessage(item.getErrorMessage());
                    items.add(resItem);
                } else {
                    MenusCalendar menusCalendar = DAOUtils.getMenusCalendarForOrgByGuid(session, item.getIdOfOrg(), item.getGuid());
                    Org org = orgs.get(item.getIdOfOrg());
                    if (org == null) {
                        org = DAOUtils.findOrg(session, item.getIdOfOrg());
                        orgs.put(item.getIdOfOrg(), org);
                    }
                    if (menusCalendar == null) {
                        menusCalendar = new MenusCalendar(item.getGuid(), org, item.getGuidOfMenu(), item.getStartDate(),
                                item.getEndDate(), item.getSixWorkDays(), nextVersion, item.getDeletedState());
                        session.save(menusCalendar);
                    } else {
                        menusCalendar.setGuid(item.getGuid());
                        menusCalendar.setGuidOfMenu(item.getGuidOfMenu());
                        menusCalendar.setStartDate(item.getStartDate());
                        menusCalendar.setEndDate(item.getEndDate());
                        menusCalendar.setSixWorkDays(item.getSixWorkDays());
                        menusCalendar.setVersion(nextVersion);
                        menusCalendar.setDeletedState(item.getDeletedState());
                        menusCalendar.setLastUpdate(new Date());
                        session.update(menusCalendar);
                    }

                    for (MenusCalendarDateItem dateItem : item.getItems()) {
                        MenusCalendarDate menusCalendarDate = DAOUtils
                                .getMenusCalendarDate(session, menusCalendar.getIdOfMenusCalendar(), dateItem.getDate());
                        if (menusCalendarDate == null) {
                            menusCalendarDate = new MenusCalendarDate();
                        }
                        menusCalendarDate.setDate(dateItem.getDate());
                        menusCalendarDate.setComment(dateItem.getComment());
                        menusCalendarDate.setIsWeekend(dateItem.getWeekend());
                        menusCalendarDate.setMenusCalendar(menusCalendar);
                        session.saveOrUpdate(menusCalendarDate);
                    }
                    ResMenusCalendarItem resItem = new ResMenusCalendarItem();
                    resItem.setGuid(item.getGuid());
                    resItem.setResultCode(0);
                    resItem.setErrorMessage("OK");
                    items.add(resItem);
                }
            }
        } catch (Exception e) {
            logger.error("Error saving MenusCalendar", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public MenusCalendarData processData() {
        MenusCalendarData result = new MenusCalendarData();
        List<MenusCalendarItem> items = new ArrayList<MenusCalendarItem>();
        List<MenusCalendar> list = DAOUtils.getMenusCalendarForOrgSinceVersion(session, menusCalendarRequest.getMaxVersion(), menusCalendarRequest.getIdOfOrgOwner());
        for (MenusCalendar menusCalendar : list) {
            List<MenusCalendarDate> menusCalendarDates = DAOUtils.getMenusCalendarDateItems(session, menusCalendar.getIdOfMenusCalendar());
            List<MenusCalendarDateItem> dateItems = new ArrayList<MenusCalendarDateItem>();
            for (MenusCalendarDate menusCalendarDate : menusCalendarDates) {
                MenusCalendarDateItem dateItem = new MenusCalendarDateItem(menusCalendarDate);
                dateItems.add(dateItem);
            }
            MenusCalendarItem item = new MenusCalendarItem(menusCalendar, dateItems);
            items.add(item);
        }
        result.setItems(items);
        return result;
    }
}

