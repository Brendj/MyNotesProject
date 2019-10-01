/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdSpecialDateView;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdView;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.*;

@Path(value = "")
@Controller
public class EzdController {

    private Logger logger = LoggerFactory.getLogger(EzdController.class);
    private static final int SETTING_TYPE = 11001;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "discountComplexList")
    public Response getComplexList() {
        ResponseDiscountComplex responseDiscountComplex = new ResponseDiscountComplex();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        //Количество дней для загрузки
        Integer countDayz;
        try {
            countDayz = Integer.valueOf(runtimeContext.getConfigProperties().getProperty("ecafe.processor.ezd.days", "1"));
            if (countDayz == null)
                countDayz = 1;
        }catch (Exception e)
        {
            countDayz = 1;
        }
        //Вычисление результата запроса
        try {

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Date date = new Date();


            date = CalendarUtils.addOneDay(date);
            date = CalendarUtils.startOfDay(date);
            //Загружаем все данные учебного календаря
            List<RequestsEzdSpecialDateView> requestsEzdSpecialDateViews = DAOUtils.getAllDateFromsSpecialDatesForEZD(persistenceSession);

            //Загружаем все данные производственного календаря
            List <ProductionCalendar> productionCalendars = DAOUtils.getAllDateFromProdactionCalendarForEZD(persistenceSession);

            //Настройка с АРМ для всех id Org
            Map<Long, Integer> allIdtoSetiings = OrgSettingDAOUtils
                    .getOrgSettingItemByOrgAndType(persistenceSession, null, SETTING_TYPE);

            //Получаем все данные для отправки в ЭЖД
            List<RequestsEzdView> requestsEzdViews = DAOUtils
                    .getAllDateFromViewEZD(persistenceSession, null,
                            null, CalendarUtils.startOfDay(date));


            //Составление сводной информации для всех организаций и групп
            List<DataOfDates> dataOfDates = new ArrayList<>();
            String curguid = null;
            String curgroupName = null;
            for (RequestsEzdView requestsEzdView : requestsEzdViews) {
                String curOrgGuid = requestsEzdView.getOrgguid();
                String groupName = requestsEzdView.getGroupname();

                if (curguid == null || curgroupName == null || !curOrgGuid.equals(curguid)
                        || !groupName.equals(curgroupName))
                {
                    curguid = curOrgGuid;
                    curgroupName = groupName;
                    DataOfDates dataOfDates1 = new DataOfDates();
                    dataOfDates1.setGroupName(groupName);
                    dataOfDates1.setGuid(curOrgGuid);
                    //Находим даты
                    Date curDate = date;
                    //Сколько дней пропустить
                    Integer countwait = allIdtoSetiings.get(requestsEzdView.getIdoforg());
                    boolean goodProd;
                    boolean goodSpec;
                    int countGoodday = 0;

                    do{
                        goodProd = false;
                        goodSpec = false;
                        for (ProductionCalendar productionCalendar: productionCalendars)
                        {
                            if (productionCalendar.getDay().equals(curDate))
                            {
                                goodProd = true;
                                break;
                            }
                        }
                        if (!goodProd)
                        {
                            for (RequestsEzdSpecialDateView requestsEzdSpecialDateView: requestsEzdSpecialDateViews)
                            {
                                if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(curDate))
                                    goodSpec = true;
                            }
                        }
                        if (!goodProd && !goodSpec)
                        {
                            if (countwait == null || countwait == 0)
                            {
                                dataOfDates1.getDates().add(curDate);
                                countGoodday++;
                            }
                            else
                                countwait--;
                        }
                        curDate = CalendarUtils.addOneDay(curDate);
                    } while (countGoodday < countDayz);
                    dataOfDates.add(dataOfDates1);
                }
            }

            String currentOrgGuid = null;
            String currentGroupName = null;
            String currentDates = null;
            DiscountComplexOrg discountComplexOrg = null;
            DiscountComplexGroup discountComplexGroup = null;
            DiscountComplexItem discountComplexItem = null;
            List<Date> datesForThis = new ArrayList<>();
            Integer clas;
            int counter = 0;
            boolean badComplex = false;
            for (RequestsEzdView requestsEzdView : requestsEzdViews) {

                String curOrgGuid = requestsEzdView.getOrgguid();
                String groupName = requestsEzdView.getGroupname();

                //Проверка на то, что данный комплекс подходит для данной группы
                String complexname = requestsEzdView.getComplexname();
                if (currentGroupName == null || !currentGroupName.equals(requestsEzdView.getGroupname()))
                {
                    try {
                        clas = extractDigits(groupName);
                    } catch (NumberFormatException e) //т.е. в названии группы нет чисел
                    {
                        clas = 0;
                    }
                    if (clas>0 && clas<5)//1-4
                    {
                        if (complexname.contains("5-11"))
                        {
                            badComplex = true;
                        }

                    } else
                    {
                        if (clas>4 && clas<12)//5-11
                        {
                            if (complexname.contains("1-4"))
                            {
                                badComplex = true;
                            }
                        }
                    }
                }


                //Текущая комбинация орг+группа не совпадает с предыщей, то ...
                if (curOrgGuid == null || !curOrgGuid.equals(currentOrgGuid) ||
                        currentGroupName == null || !currentGroupName.equals(requestsEzdView.getGroupname())) {
                    datesForThis = dataOfDates.get(counter).getDates();
                    counter++;
                }

                Date curDate = CalendarUtils.startOfDay(requestsEzdView.getMenudate());
                //Если для данной комбинации орг+группа есть такая дата и компелекс подходит для группы, то ..
                if (datesForThis.contains(curDate) && !badComplex)
                {
                    //Заполняем ответ

                    //Если организация с такой guid не встречалась раньше, то создаем
                    if (curOrgGuid == null || !curOrgGuid.equals(currentOrgGuid))
                    {
                        discountComplexOrg = new DiscountComplexOrg();
                        discountComplexOrg.setGuid(curOrgGuid);
                        responseDiscountComplex.getOrg().add(discountComplexOrg);
                        currentOrgGuid = curOrgGuid;
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
                    String curDates = curDate.toString();
                    //Достаем дни
                    if (currentDates == null || !currentDates.equals(curDates))
                    {
                        discountComplexItem = new DiscountComplexItem();
                        stateForDate = getStateforDate(curDate);
                        discountComplexItem.setState(String.valueOf(stateForDate));
                        discountComplexItem.setDate(new SimpleDateFormat("dd.MM.yyyy").format(curDate));
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

    public Integer extractDigits(String src) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            }
            else
            {
                return Integer.valueOf(builder.toString());
            }
        }
        return Integer.valueOf(builder.toString());
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
