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
import java.util.*;

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

            //Если guid задан, то получаем данные по школам
            List <Org> orgs = new ArrayList<>();
            if (guidOrg != null){
                //Получаем все школы с данным guid
                orgs = DAOUtils.findOrgsByGuid(persistenceSession, guidOrg);
            }
            //Получаем все данные для отправки в ЭЖД
            List<RequestsEzdView> requestsEzdViews = DAOUtils
                    .getAllDateFromViewEZD(persistenceSession, orgs,
                            groupName, CalendarUtils.startOfDay(date));

            List<ConsolidationDadas> consolidationDadas = new ArrayList<>();

            //Получаем все id школ, которые есть в таблице
            List<Long> orgIds = new ArrayList<>();
            for (RequestsEzdView requestsEzdView : requestsEzdViews) {
                Long value = requestsEzdView.getIdoforg();
                if(orgIds.indexOf(value) == -1)
                {
                    orgIds.add(value);
                }
                ConsolidationDadas consolidationDadas1 = new ConsolidationDadas();
                consolidationDadas1.setIdOrg(value);
                consolidationDadas1.setGroupName(requestsEzdView.getGroupname());
                consolidationDadas.add(consolidationDadas1);
            }
            //Настройка с АРМ для всех id Org
            Map<Long, Integer> allIdtoSetiings = OrgSettingDAOUtils
                    .getOrgSettingItemByOrgAndType(persistenceSession, orgIds, SETTING_TYPE);

            for (ConsolidationDadas consolidationDadas1 : consolidationDadas) {

                Integer offset = allIdtoSetiings.get(consolidationDadas1.getIdOrg());
                if (offset != null) {
                    consolidationDadas1.setDatebefore(CalendarUtils.addDays(date, offset));
                } else {
                    consolidationDadas1.setDatebefore(date);
                }
                //Учебный календарь
                consolidationDadas1.setDateafter(getSpecialDate(persistenceSession, consolidationDadas1.getDatebefore(),
                        consolidationDadas1.getGroupName()));
            }


            int counter = 0;
            Long currentOrgId = null;
            String currentGroupName = null;
            String currentDates = null;
            DiscountComplexOrg discountComplexOrg = null;
            DiscountComplexGroup discountComplexGroup = null;
            DiscountComplexItem discountComplexItem = null;


            for (RequestsEzdView requestsEzdView : requestsEzdViews) {

                Long curOrgId = requestsEzdView.getIdoforg();

                //Получаем нужную дату
                date = consolidationDadas.get(counter).getDateafter();

                counter++;

                Date curDate = requestsEzdView.getMenudate();
                //Если полученная индивидуальная дата попадает под нужный диапазон дат, то
                if (curDate.getTime() > CalendarUtils.startOfDay(date).getTime() &&
                        curDate.getTime() < CalendarUtils.endOfDay(CalendarUtils.addDays(date, COUNT_DAYS)).getTime() ) {
                    //Заполняем ответ

                    //Если организация с такой id не встречалась раньше, то создаем
                    if (currentOrgId == null || currentOrgId != curOrgId)
                    {
                        discountComplexOrg = new DiscountComplexOrg();
                        discountComplexOrg.setIdOrg(curOrgId);
                        responseDiscountComplex.getOrg().add(discountComplexOrg);
                        currentOrgId = curOrgId;
                        currentGroupName = null;
                    }

                    //Достаем группы
                    if (currentGroupName == null || !currentGroupName.equals(requestsEzdView.getGroupname()))
                    {
                        discountComplexGroup = new DiscountComplexGroup();
                        discountComplexGroup.setGroupName(requestsEzdView.getGroupname());
                        discountComplexOrg.getGroups().add(discountComplexGroup);
                        currentGroupName = requestsEzdView.getGroupname();
                        currentDates = null;
                    }


                    boolean stateForDate = false;
                    String curDates = CalendarUtils.startOfDay(curDate).toString();
                    //Достаем дни
                    if (currentDates == null || !currentDates.equals(curDates))
                    {
                        discountComplexItem = new DiscountComplexItem();
                        stateForDate = getStateforDate(curDate);
                        discountComplexItem.setState(String.valueOf(stateForDate));
                        discountComplexItem.setDate(curDates);
                        discountComplexGroup.getDays().add(discountComplexItem);
                        currentDates = curDates;
                    }


                    if (!stateForDate) {
                        ComplexesItem complexesItem = new ComplexesItem();
                        complexesItem.setComplexname(requestsEzdView.getComplexname());
                        complexesItem.setIdofcomplex(requestsEzdView.getIdofcomplex().toString());
                        discountComplexItem.getComplexeslist().add(complexesItem);
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
