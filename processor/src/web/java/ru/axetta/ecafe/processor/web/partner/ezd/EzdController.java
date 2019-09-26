/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdView;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingItem;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.fpsapi.*;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path(value = "")
@Controller
public class EzdController {

    private Logger logger = LoggerFactory.getLogger(EzdController.class);
    private static final int SETTING_TYPE = 11001;
    private static final int THRESHOLD_VALUE = 39600000;//39600000 - Это время в миллесекундах между началом для (00:00) и 11:00
    private static final int COUNT_DAYS = 5;//Количество дней для загрузки
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "discountComplexList")
    public Response getComplexList(@QueryParam(value="GuidOrg") String guidOrg,
             @QueryParam(value="GroupName") String groupName) throws Exception {
        ResponseDiscountComplex responseDiscountComplex = new ResponseDiscountComplex();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Вычисление результата запроса
        try {

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Date date = new Date();

            //До 11:00 ???
            Date startDay = CalendarUtils.startOfDay(date);
            Long currentTime = date.getTime() - startDay.getTime();
            if (currentTime >= THRESHOLD_VALUE)
            {
                date = CalendarUtils.addOneDay(date);
            }


            //Производственный календарь
            date = getProdaction(date);

            //Сохраняем общую начальную дату
            Date currentDate = date;

            //Если guid не задан, то получаем данные по всем школам
            List<BigInteger> orgIds = new ArrayList<>();
            if (guidOrg == null)
                //Получаем id всех школ
                orgIds = DAOUtils.getOrgsAllId(persistenceSession);
            else {
                List <Org> orgs = DAOUtils.findOrgsByGuid(persistenceSession, guidOrg);
                for (Org org: orgs)
                {
                    orgIds.add(BigInteger.valueOf(org.getIdOfOrg()));
                }
            }

            for (BigInteger orgid: orgIds) {
                //Настройка с АРМ
                date = currentDate;
                Integer countDays = OrgSettingDAOUtils
                        .getOrgSettingItemByOrgAndType(persistenceSession, orgid.longValue(), SETTING_TYPE);
                if (countDays != null) {
                    date = CalendarUtils.addDays(date, countDays);
                }


                List<GroupNamesToOrgs> groupNamesToOrgs = DAOUtils.findGroupsForOrg(persistenceSession, orgid.longValue(), groupName);
                //Сохраняем общую начальную дату
                Date currentDateSchool = date;

                for (GroupNamesToOrgs groupNamesToOrgs1: groupNamesToOrgs) {

                    date = currentDateSchool;

                    //Учебный календарь
                    date = getSpecialDate(persistenceSession, date, groupNamesToOrgs1.getGroupName());

                    //Получаем все данные для одной школы и одной группы для отправки в ЭЖД
                    List<RequestsEzdView> requestsEzdViews = DAOUtils
                            .getAllDateFromViewEZD(persistenceSession, orgid.longValue(),
                                    groupNamesToOrgs1.getGroupName(), CalendarUtils.startOfDay(date),
                                    CalendarUtils.endOfDay(CalendarUtils.addDays(date, COUNT_DAYS)));
                    for (RequestsEzdView requestsEzdView : requestsEzdViews) {
                        DiscountComplexOrg discountComplexOrg = null;
                        for (DiscountComplexOrg discountComplexOrgset : responseDiscountComplex.getOrg()) {
                            if (discountComplexOrgset.getIdOrg().equals(requestsEzdView.getIdoforg())) {
                                discountComplexOrg = discountComplexOrgset;
                                break;
                            }
                        }
                        //Если организация с такой id не встречалась раньше, то создаем
                        if (discountComplexOrg == null) {
                            discountComplexOrg = new DiscountComplexOrg();
                            discountComplexOrg.setIdOrg(requestsEzdView.getIdoforg());
                            responseDiscountComplex.getOrg().add(discountComplexOrg);
                        }

                        //Достаем группы
                        DiscountComplexGroup discountComplexGroup = null;
                        for (DiscountComplexGroup discountComplexGroupset : discountComplexOrg.getGroups()) {
                            if (discountComplexGroupset.getGroupName().equals(requestsEzdView.getGroupname())) {
                                discountComplexGroup = discountComplexGroupset;
                                break;
                            }
                        }
                        //Если такой группы не было, то создаем
                        if (discountComplexGroup == null) {
                            discountComplexGroup = new DiscountComplexGroup();
                            discountComplexGroup.setGroupName(requestsEzdView.getGroupname());
                            discountComplexOrg.getGroups().add(discountComplexGroup);
                        }


                        boolean stateForDate = false;
                        DiscountComplexItem discountComplexItem = null;
                        //Достаем дни
                        for (DiscountComplexItem discountComplexItemset : discountComplexGroup.getDays()) {
                            if (discountComplexItemset.getDate()
                                    .equals(CalendarUtils.startOfDay(requestsEzdView.getMenudate()).toString())) {
                                discountComplexItem = discountComplexItemset;
                                stateForDate = Boolean.valueOf(discountComplexItem.getState());
                                break;
                            }
                        }
                        //Если такой даты не было, то создаем
                        if (discountComplexItem == null) {
                            discountComplexItem = new DiscountComplexItem();
                            stateForDate = getStateforDate(requestsEzdView.getMenudate());
                            discountComplexItem.setState(String.valueOf(stateForDate));
                            discountComplexItem.setDate(CalendarUtils.startOfDay(requestsEzdView.getMenudate()).toString());
                            discountComplexGroup.getDays().add(discountComplexItem);
                        }


                        if (!stateForDate) {
                            ComplexesItem complexesItem = new ComplexesItem();
                            complexesItem.setComplexname(requestsEzdView.getComplexname());
                            complexesItem.setIdofcomplex(requestsEzdView.getIdofcomplex().toString());
                            discountComplexItem.getComplexeslist().add(complexesItem);
                        }
                    }
                }
            }


            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscountComplex.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
            responseDiscountComplex.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscountComplex).build();
        } catch (IllegalArgumentException e) {
            logger.error (String.format ("%s", e));
            responseDiscountComplex.setErrorCode(Long.toString(ResponseCodes.RC_INTERNAL_ERROR.getCode()));
            responseDiscountComplex.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseDiscountComplex).build();
        } catch (Exception e) {
            logger.error (String.format ("%s", e));
            responseDiscountComplex.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseDiscountComplex.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseDiscountComplex).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private Date getProdaction (Date date)
    {
        //Прибавляем по одному дню, пока не выйдем из нерабочих дней по производственному календарю
        while (DAOService.getInstance().getProductionCalendarByDate(date) != null)
        {
            date = CalendarUtils.addOneDay(date);
        }
        return date;
    }

    private Date getSpecialDate (Session persistenceSession, Date date, String groupName) throws Exception {
        //Проверяем по учебному календарю
        SpecialDate specialDate = DAOService.getInstance().getSpecialCalendarByDate(date);
        if (specialDate != null && specialDate.getIsWeekend())
        {
            Long idOfOrg = specialDate.getIdOfOrg();
            if (idOfOrg != null)
            {
                Org orgCalender = (Org) persistenceSession.load(Org.class, idOfOrg);
                if (orgCalender.getClientGroups() == null)
                {
                    date = CalendarUtils.addOneDay(date);
                }
                else {
                    if (DAOUtils.findClientGroupByGroupNameAndIdOfOrg(persistenceSession, idOfOrg, groupName)
                            != null) {
                        date = CalendarUtils.addOneDay(date);
                    }
                }
            }
        }
        return date;
    }

    private boolean getStateforDate (Date date)
    {
        if (DAOService.getInstance().getProductionCalendarByDate(date) != null)
        {
            return true;
        }
        else
        {
            SpecialDate specialDate = DAOService.getInstance().getSpecialCalendarByDate(date);
            if (specialDate != null && specialDate.getIsWeekend())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
