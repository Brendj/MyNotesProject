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
            String guidOrg, String groupName, String dateR, String userName, Long idOfComplex, String complexName,
            Integer count) {

        try {
            Date date = new SimpleDateFormat("dd.MM.yyyy").parse(dateR);
            date = CalendarUtils.startOfDay(date);

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
                    if (org.getState() == 0 || org.getType().equals(OrganizationType.SUPPLIER)
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
            Date curDate = new Date();
            boolean flag;

            //2.1.1
            do {
                flag = false;
                for (ProductionCalendar productionCalendar : productionCalendars) {
                    if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(curDate)) {
                        curDate = CalendarUtils.addOneDay(curDate);
                        flag = true;
                    }
                }
            } while (flag);

            //2.1.3
            //Если время больше 11:00, то прибавляем 1 день
            if (new Date().getTime() - CalendarUtils.startOfDay(new Date()).getTime() > TIME_MAX) {
                curDate = CalendarUtils.addOneDay(curDate);
                //2.1.3.1
                do {
                    flag = false;
                    for (ProductionCalendar productionCalendar : productionCalendars) {
                        if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(curDate)) {
                            curDate = CalendarUtils.addOneDay(curDate);
                            flag = true;
                        }
                    }
                } while (flag);
            }

            for (Org org : orgs) {
                Date startDateforCurrOrg = curDate;
                Integer countBadday = allIdtoSetiings.get(org.getIdOfOrg());
                startDateforCurrOrg = CalendarUtils.addDays(startDateforCurrOrg, countBadday);
                //Если дата заявки меньше текущей...
                if (date.getTime() < startDateforCurrOrg.getTime()) {
                    Result result = new Result();
                    result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
                    result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
                    return result;
                }

                flag = false;
                for (ProductionCalendar productionCalendar : productionCalendars) {
                    if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(date)) {
                        flag = true;
                        //2.3.1
                        if (productionCalendar.getFlag() == 2) {
                            Result result = new Result();
                            result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                            result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                            return result;
                        }
                        break;
                    }
                }
                //1.4
                boolean findSpec = false;
                if (flag) {
                    for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                        if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(date)
                                && requestsEzdSpecialDateView.getIdoforg().equals(org.getIdOfOrg()) && (
                                requestsEzdSpecialDateView.getGroupname().equals(groupName)
                                        || requestsEzdSpecialDateView.getGroupname() == null)) {
                            if (requestsEzdSpecialDateView.getIsweekend() == 0) {
                                //2.3.2
                                findSpec = true;
                            } else {
                                //2.3.3
                                Result result = new Result();
                                result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                                result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                                return result;
                            }
                            break;
                        }
                    }
                    if (CalendarUtils.getDayOfWeek(date) == Calendar.SATURDAY) {
                        if (!DAOUtils.getGroupNamesToOrgsByOrgAndGroupName(persistenceSession, org, groupName)
                                .getIsSixDaysWorkWeek()) {
                            Result result = new Result();
                            result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                            result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                            return result;
                        }
                    }
                }
                //2.4
                else {
                    for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                        if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(date)
                                && requestsEzdSpecialDateView.getIdoforg().equals(org.getIdOfOrg()) && (
                                requestsEzdSpecialDateView.getGroupname().equals(groupName)
                                        || requestsEzdSpecialDateView.getGroupname() == null))

                        {
                            if (requestsEzdSpecialDateView.getGroupname().equals(groupName)) {

                                if (requestsEzdSpecialDateView.getIsweekend() == 1) {
                                    Result result = new Result();
                                    result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                                    result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                                    return result;
                                }
                            } else {
                                if (requestsEzdSpecialDateView.getIsweekend() == 1) {
                                    Result result = new Result();
                                    result.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                                    result.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                                    return result;
                                }
                            }
                        }
                    }
                }
                //3.0
                Integer maxVersion = DAOUtils.getMaxVersionForEZD(persistenceSession);
                maxVersion++;
                if (!DAOUtils
                        .findSameRequestFromEZD(persistenceSession, org.getIdOfOrg(), groupName, date, idOfComplex)) {
                    DAOUtils.updateRequestFromEZD(persistenceSession, org.getIdOfOrg(), userName, groupName, date,
                            idOfComplex, count, maxVersion);
                } else {

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
