/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzd;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdSpecialDateView;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.ProductionCalendar;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class GeneralRequestMetod {
    private static Logger logger = LoggerFactory.getLogger(GeneralRequestMetod.class);
    private static final long TIME_MAX = 39600000; //11:00

    public static Result requestsComplexForOne(Session persistenceSession, List<ProductionCalendar> productionCalendars,
                                               List<RequestsEzdSpecialDateView> requestsEzdSpecialDateViews, Map<Long, Integer> allIdtoSetiings,
                                               String guidOrg, String groupName, String dateR,
                                               String userName, Long idOfComplex,
                                               String complexName, Integer count) {
        try {
            Date date = new SimpleDateFormat("dd.MM.yyyy").parse(dateR);
            date = CalendarUtils.startOfDay(date);

            Date curDate = new Date();
            //Если дата заявки сегодняшняя, но время больше 11:00...
            if (CalendarUtils.startOfDay(curDate).getTime() == date.getTime() && (curDate.getTime() - date.getTime()
                    > TIME_MAX)) {
                Result result = new Result();
                result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
                return result;
            }
            //Если дата заявки меньше текущей...
            if (date.getTime() < curDate.getTime()) {
                Result result = new Result();
                result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
                return result;
            }

            List<Org> orgs = DAOUtils.findOrgsByGuid(persistenceSession, guidOrg);
            //Если организаций c таким guid не найдена, то ...
            if (orgs.isEmpty()) {
                Result result = new Result();
                result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
                return result;
            } else {
                //Удаляем все организации, неподходящие по фильтру и у которых нет нужной группы
                Iterator<Org> it = orgs.iterator();
                while (it.hasNext()) {
                    Org org = it.next();
                    if (org.getState().equals(0) || org.getType().equals(OrganizationType.SUPPLIER)
                            || DAOUtils.getGroupNamesToOrgsByOrgAndGroupName(persistenceSession, org, groupName) == null
                            || !org.getPreorderlp()) {
                        it.remove();
                    }
                }
            }
            //Если организаций не осталось....
            if (orgs.isEmpty()) {
                Result result = new Result();
                result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
                return result;
            }
            Integer maxVersion = DAOUtils.getMaxVersionForEZD(persistenceSession);
            for (Org org : orgs) {
                Date startDate = new Date();
                //startDate = CalendarUtils.addOneDay(startDate);
                startDate = CalendarUtils.startOfDay(startDate);

                Integer countBadday = allIdtoSetiings.get(org.getIdOfOrg());
                //Собираем доп запрещенные даты для орг + группа на основе инфы с АРМ
                List<RequestsEzdSpecialDateView> dateNotWorksFromARM = new ArrayList<>();
                if (countBadday != null) {
                    do {
                        boolean goodProd = false;
                        boolean goodSpec = false;
                        //Проверка по производственному календарю
                        for (ProductionCalendar productionCalendar : productionCalendars) {
                            if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(startDate)) {
                                goodProd = true;
                                //Проверка не является ли эта дата рабочей субботой....
                                boolean findDate = false;
                                for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                                    if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(startDate) && requestsEzdSpecialDateView.getIdoforg()
                                            .equals(org.getIdOfOrg()) && requestsEzdSpecialDateView.getGroupname().equals(groupName)) {
                                        findDate = true;
                                        if (requestsEzdSpecialDateView.getIsweekend() == 0) {
                                            goodProd = false;
                                        } else {
                                            goodSpec = true;
                                        }
                                        break;
                                    }
                                }
                                if (!findDate) {
                                    int day = CalendarUtils.getDayOfWeek(startDate);
                                    if (day == Calendar.SATURDAY) {
                                        boolean flag = DAOReadonlyService.getInstance()
                                                .isSixWorkWeek(org.getIdOfOrg(), groupName);
                                        if (flag) {
                                            goodProd = false;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                        //Проверка по учебному календарю
                        if (!goodSpec && !goodProd) {
                            for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                                if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(startDate)
                                        && requestsEzdSpecialDateView.getIdoforg().equals(org.getIdOfOrg()) && requestsEzdSpecialDateView.getGroupname().equals(groupName)
                                        && requestsEzdSpecialDateView.getIsweekend() == 1) {
                                    goodSpec = true;
                                    break;
                                }
                            }
                        }
                        //Если день рабочий, то...
                        if (!goodSpec && !goodProd) {
                            if (countBadday != 0) {
                                RequestsEzdSpecialDateView requestsEzdSpecialDateView = new RequestsEzdSpecialDateView();
                                requestsEzdSpecialDateView.setSpecDate(startDate);
                                dateNotWorksFromARM.add(requestsEzdSpecialDateView);
                                countBadday--;
                            }
                        }
                        startDate = CalendarUtils.addOneDay(startDate);
                    } while (countBadday != 0);
                }

                maxVersion++;
                if (!DAOUtils.findSameRequestFromEZD(persistenceSession, org.getIdOfOrg(), groupName, date, idOfComplex)) {
                    DAOUtils.updateRequestFromEZD(persistenceSession, org.getIdOfOrg(), userName, groupName, date,
                            idOfComplex, count, maxVersion);
                } else {
                    if (date.getTime() < new Date().getTime()) {
                        Result result = new Result();
                        result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                        result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                        return result;
                    }
                    //Проверка по производственному календарю
                    boolean goodProd = false;
                    for (ProductionCalendar productionCalendar : productionCalendars) {
                        if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(date)) {
                            goodProd = true;
                            //Проверка не является ли эта дата рабочей субботой....
                            for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                                if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(date) && requestsEzdSpecialDateView.getIdoforg().equals(org.getIdOfOrg())
                                        && requestsEzdSpecialDateView.getGroupname().equals(groupName)) {
                                    if (requestsEzdSpecialDateView.getIsweekend() == 0) {
                                        goodProd = false;
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if (goodProd) {
                        Result result = new Result();
                        result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                        result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                        return result;
                    }
                    //Проверяем по учебному календарю
                    for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                        if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(date) && requestsEzdSpecialDateView.getIdoforg().equals(org.getIdOfOrg())
                                && requestsEzdSpecialDateView.getGroupname().equals(groupName)
                                && requestsEzdSpecialDateView.getIsweekend() == 1) {
                            Result result = new Result();
                            result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                            result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                            return result;
                        }
                    }
                    //Проверяем дополнительные запрещенные даты, полученные от АРМ
                    for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : dateNotWorksFromARM) {
                        if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(date)) {
                            Result result = new Result();
                            result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                            result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                            return result;
                        }
                    }
                    DAOService.getInstance().applyHaveNewLPForOrg(org.getIdOfOrg(), true);

                    RequestsEzd requestsEzd = new RequestsEzd();
                    requestsEzd.setIdOfOrg(org.getIdOfOrg());
                    requestsEzd.setGroupname(groupName);
                    requestsEzd.setDateappointment(date);
                    requestsEzd.setIdofcomplex(idOfComplex);
                    requestsEzd.setComplexname(complexName);
                    requestsEzd.setComplexcount(count);
                    requestsEzd.setUsername(userName);
                    requestsEzd.setCreateddate(new Date());
                    requestsEzd.setVersionrecord(maxVersion);
                    requestsEzd.setGuid(UUID.randomUUID().toString());
                    persistenceSession.save(requestsEzd);
                }
            }
            Result result = new Result();
            result.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_OK.toString());
            return result;
        } catch (Exception e) {
            Result result = new Result();
            result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return result;
        }
    }
}
