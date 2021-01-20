/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;

import java.util.*;

public class GoodDateForOrders {
    private static final int SETTING_TYPE = 11001;
    private static final int SETTING_TYPE_SUBSCRIPTION = 10104;

    private static final long TIME_MAX = 43200000; //12:00
    //Проверка доступности даты на изменение при получении заявок на питание из ПАОУ
    public boolean isGoodDate(Session session, Long idOfOrg, Date dateDone, Integer type) {
        try {
            Org org = (Org) session.load(Org.class, idOfOrg);
            if (org != null) {
                //Если функционал проверки даты выключен для данной орг, то считаем что проверка прогла успешно
                if (!org.getGooddatecheck())
                    return true;
            }
            Date currentDate = new Date();
            currentDate = CalendarUtils.startOfDay(currentDate);
            dateDone = CalendarUtils.startOfDay(dateDone);

            List<ProductionCalendar> productionCalendars = DAOUtils
                    .getAllDateFromProdactionCalendarForFutureDates(session);

            //1.1.1.1
            boolean flag;
            do {
                flag = false;
                for (ProductionCalendar productionCalendar : productionCalendars) {
                    if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(currentDate)) {
                        currentDate = CalendarUtils.addOneDay(currentDate);
                        flag = true;
                    }
                }
            } while (flag);
            //1.1.2
            //Если время больше 12:00, то прибавляем 1 день
            if (new Date().getTime() - CalendarUtils.startOfDay(new Date()).getTime() > TIME_MAX) {
                //1.1.3
                currentDate = CalendarUtils.addOneDay(currentDate);
                do {
                    flag = false;
                    for (ProductionCalendar productionCalendar : productionCalendars) {
                        if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(currentDate)) {
                            currentDate = CalendarUtils.addOneDay(currentDate);
                            flag = true;
                        }
                    }
                } while (flag);
            }

            //1.2
            //Настройка с АРМ для Org
            Integer countBadDays = 0;

            if (type == 0) {
                countBadDays = OrgSettingDAOUtils.getSettingItemByOrgAndType(session, idOfOrg, SETTING_TYPE);
            }
            if (type == 1) {
                //т.к. получаем в часах, то переводим в дни с округлением в большую сторону
                countBadDays = OrgSettingDAOUtils
                        .getSettingItemByOrgAndType(session, idOfOrg, SETTING_TYPE_SUBSCRIPTION);
                countBadDays = (int) Math.ceil(((double) countBadDays / (double) 24));
            }
            //currentDate = CalendarUtils.addDays(currentDate, countBadDays);
            List<GroupNamesToOrgs> groupNamesToOrgs = DAOUtils.getAllGroupnamesToOrgsByIdOfOrg(session, idOfOrg);
            ///////////////////////////////////////////////
            Integer countBadDaysCurrent = 0;
            while (countBadDaysCurrent < countBadDays) {
                do {
                    flag = false;
                    for (ProductionCalendar productionCalendar : productionCalendars) {
                        if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(currentDate)) {
                            currentDate = CalendarUtils.addOneDay(currentDate);
                            flag = true;
                        }
                    }
                } while (flag);

                CompositeIdOfSpecialDate compositeId = new CompositeIdOfSpecialDate(idOfOrg,
                        currentDate);
                List<SpecialDate> specialDate;
                try {
                    specialDate = DAOUtils.findSpecialDateForDates(session, compositeId);
                } catch (Exception e) {
                    specialDate = null;
                }

                if (!specialDate.isEmpty()) {
                    //1.5.2 - 1.5.3
                    boolean flag1 = true;
                    List<String> groupNames = new ArrayList<>();
                    for (SpecialDate specialDate1 : specialDate) {
                        if (!specialDate1.getIsWeekend())
                        //Если работает хотя бы одна группа, то орг работает
                        {
                            flag1 = false;
                            break;
                        }
                        groupNames.add(DAOReadonlyService.getInstance().getClientGroupName(idOfOrg, specialDate1.getIdOfClientGroup()));
                    }
                    Iterator<GroupNamesToOrgs> groupNamesToOrgsIterator = groupNamesToOrgs.iterator();
                    while(groupNamesToOrgsIterator.hasNext()) {
                        GroupNamesToOrgs groupNamesToOrgs1 = groupNamesToOrgsIterator.next();//получаем следующий элемент
                        if (groupNames.contains(groupNamesToOrgs1.getGroupName())) {
                            //Удаляем группы, для которых есть SpecialDate
                            groupNamesToOrgsIterator.remove();
                        }
                    }
                    if (!groupNamesToOrgs.isEmpty()) {
                        //Значит не все группы есть в SpecialDate
                        flag1 = false;
                    }
                    currentDate = CalendarUtils.addOneDay(currentDate);
                    if (!flag1)
                        countBadDaysCurrent++;
                } else {
                    //1.5.4
                    if (CalendarUtils.getDayOfWeek(currentDate) == Calendar.SATURDAY) {
                        currentDate = CalendarUtils.addOneDay(currentDate);
                        if (!DAOReadonlyService.getInstance().isSixWorkWeekOrg(idOfOrg)) {
                            countBadDaysCurrent++;
                        }
                        continue;
                    }
                    currentDate = CalendarUtils.addOneDay(currentDate);
                    countBadDaysCurrent++;
                }
            }
            ///////////////////////////////////////////////

            //1.2.1
            do {
                flag = false;
                for (ProductionCalendar productionCalendar : productionCalendars) {
                    if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(currentDate)) {
                        currentDate = CalendarUtils.addOneDay(currentDate);
                        flag = true;
                    }
                }
            } while (flag);

            //1.3.1
            if (dateDone.getTime() < currentDate.getTime()) {
                return false;
            }

            //1.4 - 1.5
            flag = false;
            for (ProductionCalendar productionCalendar : productionCalendars) {
                if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(dateDone)) {
                    flag = true;
                    break;
                }
            }

            //1.4
            if (!flag) {
                CompositeIdOfSpecialDate compositeId = new CompositeIdOfSpecialDate(idOfOrg,
                        dateDone);
                List<SpecialDate> specialDate;
                try {
                    specialDate = DAOUtils.findSpecialDateForDates(session, compositeId);
                } catch (Exception e) {
                    specialDate = null;
                }
                //1.4.1
                List<String> groupNames = new ArrayList<>();
                for (SpecialDate specialDate1 : specialDate) {
                    if (!specialDate1.getIsWeekend())
                    //1.7
                    {
                        return true;
                    }
                    groupNames.add(DAOReadonlyService.getInstance().getClientGroupName(idOfOrg, specialDate1.getIdOfClientGroup()));
                }
                //Если не найдены даты, то день хороший
                if (specialDate.isEmpty())
                    return true;
                else
                {
                    Iterator<GroupNamesToOrgs> groupNamesToOrgsIterator = groupNamesToOrgs.iterator();
                    while(groupNamesToOrgsIterator.hasNext()) {
                        GroupNamesToOrgs groupNamesToOrgs1 = groupNamesToOrgsIterator.next();//получаем следующий элемент
                        if (groupNames.contains(groupNamesToOrgs1.getGroupName())) {
                            //Удаляем группы, для которых есть SpecialDate
                            groupNamesToOrgsIterator.remove();
                        }
                    }
                    if (groupNamesToOrgs.isEmpty()) {
                        //Значит у всех групп в ОО выходной
                        return false;
                    }
                    else{
                        //Значит не все группы есть в SpecialDate
                        return true;
                    }
                }

                //1.4.2
                //boolean groupWeekend = true;
                //boolean orgWeekend = true;
                //boolean wasOrg = false;
                //boolean wasGroup = false;
                //for (SpecialDate specialDate1: specialDate)
                //{
                //    if (specialDate1.getIdOfClientGroup() != null)
                //    {
                //        wasGroup = true;
                //        if (!specialDate1.getIsWeekend())
                //        {
                //            groupWeekend = false;
                //        }
                //    }
                //    if (specialDate1.getIdOfClientGroup() == null)
                //    {
                //        wasOrg = true;
                //        if (!specialDate1.getIsWeekend())
                //        {
                //            orgWeekend = false;
                //        }
                //    }
                //}
                //if ((groupWeekend && wasGroup) || (orgWeekend && wasOrg))
                //    //1.6
                //    return false;
                ////1.7
                //return true;
            }
            //1.5
            else {
                //1.5.1
                for (ProductionCalendar productionCalendar : productionCalendars) {
                    if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(dateDone)) {
                        if (productionCalendar.getFlag() == 2)
                        //1.6
                        {
                            return false;
                        }
                    }
                }

                CompositeIdOfSpecialDate compositeId = new CompositeIdOfSpecialDate(idOfOrg,
                        dateDone);
                List<SpecialDate> specialDate;
                try {
                    specialDate = DAOUtils.findSpecialDateForDates(session, compositeId);
                } catch (Exception e) {
                    specialDate = null;
                }
                List<String> groupNames = new ArrayList<>();
                if (!specialDate.isEmpty()) {
                    //1.5.2 - 1.5.3
                    for (SpecialDate specialDate1 : specialDate) {
                        if (!specialDate1.getIsWeekend())
                        //1.7
                        {
                            return true;
                        }
                        groupNames.add(DAOReadonlyService.getInstance().getClientGroupName(idOfOrg, specialDate1.getIdOfClientGroup()));
                    }
                    Iterator<GroupNamesToOrgs> groupNamesToOrgsIterator = groupNamesToOrgs.iterator();
                    while(groupNamesToOrgsIterator.hasNext()) {
                        GroupNamesToOrgs groupNamesToOrgs1 = groupNamesToOrgsIterator.next();//получаем следующий элемент
                        if (groupNames.contains(groupNamesToOrgs1.getGroupName())) {
                            //Удаляем группы, для которых есть SpecialDate
                            groupNamesToOrgsIterator.remove();
                        }
                    }
                    if (groupNamesToOrgs.isEmpty()) {
                        //Значит у всех групп в ОО выходной
                        return false;
                    }
                    else{
                        //Значит не все группы есть в SpecialDate
                        return true;
                    }
                } else {
                    //1.5.4
                    if (CalendarUtils.getDayOfWeek(dateDone) == Calendar.SATURDAY) {
                        if (DAOReadonlyService.getInstance().isSixWorkWeekOrg(idOfOrg)) {
                            //1.7
                            return true;
                        } else {
                            //1.6
                            return false;
                        }
                    }
                    //1.6
                    return false;
                }
            }
        } catch (Exception e) {
            return true;
        }
    }

}
