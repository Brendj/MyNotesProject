/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdSpecialDateView;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdView;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
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
    @Path(value = "test")
    public Response test() {
        return Response.status(HttpURLConnection.HTTP_OK).entity("good").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "discountComplexList")
    public Response getComplexList() {
        logger.info("Начало работы сервиса сбора данных для ЭЖД");
        ResponseToEZD responseToEZD = new ResponseToEZD();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        //Количество дней для загрузки
        Integer countDayz;
        try {
            countDayz = Integer
                    .valueOf(runtimeContext.getConfigProperties().getProperty("ecafe.processor.ezd.days", "1"));
            if (countDayz == null) {
                countDayz = 1;
            }
        } catch (Exception e) {
            countDayz = 1;
        }
        logger.info(String.format("Сбор на %s дней вперед", countDayz.toString()));

        //Вычисление результата запроса
        try {

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Date date = new Date();
            date = CalendarUtils.addOneDay(date);
            date = CalendarUtils.startOfDay(date);


            logger.info("Старт начала сбора данных по учебному календарю");
            //Загружаем все данные учебного календаря
            List<RequestsEzdSpecialDateView> requestsEzdSpecialDateViews = DAOUtils
                    .getAllDateFromsSpecialDatesForEZD(persistenceSession);
            if (requestsEzdSpecialDateViews == null) {
                requestsEzdSpecialDateViews = new ArrayList<>();
            }
            logger.info(String.format("Всего записей по учебному календарю - %s",
                    String.valueOf(requestsEzdSpecialDateViews.size())));

            logger.info("Старт начала сбора данных по производственному календарю");
            //Загружаем все данные производственного календаря
            List<ProductionCalendar> productionCalendars = DAOUtils
                    .getAllDateFromProdactionCalendarForEZD(persistenceSession);
            if (productionCalendars == null) {
                productionCalendars = new ArrayList<>();
            }
            logger.info(String.format("Всего записей по производственному календарю - %s",
                    String.valueOf(productionCalendars.size())));


            logger.info("Старт начала сбора настроек, полученных с АРМ для организаций");
            //Настройка с АРМ для всех id Org
            Map<Long, Integer> allIdtoSetiings = OrgSettingDAOUtils
                    .getOrgSettingItemByOrgAndType(persistenceSession, null, SETTING_TYPE);
            if (allIdtoSetiings == null) {
                allIdtoSetiings = new HashMap<>();
            }
            logger.info(String.format("Всего настроек с АРМ - %s", String.valueOf(allIdtoSetiings.size())));

            logger.info("Старт сбора данных из вьюхи для отправки в ЭЖД");
            //Получаем все данные для отправки в ЭЖД
            List<RequestsEzdView> requestsEzdViews = DAOUtils
                    .getAllDateFromViewEZD(persistenceSession, null, null, CalendarUtils.startOfDay(date));
            if (requestsEzdViews == null) {
                requestsEzdViews = new ArrayList<>();
            }
            logger.info(String.format("Вьюха вернула записей - %s", String.valueOf(requestsEzdViews.size())));

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            logger.info("Старт формирования ответа для ЭЖД");

            //Хранится последняя группы при принадлежности группы к параллели
            String groupNameComplex = null;

            //Хранится последние guid и орг при вычислении дат
            String curguidDATES = null;
            String curgroupNameDATES = null;

            //Здесь храниться массив возможных дат для текущей комбинации guid + орг
            List<Date> datesForThis = new ArrayList<>();

            boolean badComplex;

            //Здесь хранятся последние сформированные данные по guid, group и дата для ответа в ЭЖД
            String orgGuidResponse = null;
            String groupNameResponse = null;
            String currentDatesResponse = null;

            DiscountComplexOrg discountComplexOrg = null;
            DiscountComplexGroup discountComplexGroup = null;
            DiscountComplexItem discountComplexItem = null;
            ComplexesItem complexesItem;

            //Для экономии памяти
            String thisOrgGuid;
            String thisGroupName;
            Long thisIdOfOrg;
            Integer clas;
            Date curDateDates;
            Integer countwait;
            boolean goodProd;
            boolean goodSpec;
            Date curDate;
            String curDatesString;
            for (RequestsEzdView requestsEzdView : requestsEzdViews) {
                thisOrgGuid = requestsEzdView.getOrgguid();
                thisGroupName = requestsEzdView.getGroupname();
                thisIdOfOrg = requestsEzdView.getIdoforg();
                badComplex = false;

                //Проверка на то, что данный комплекс подходит для данной группы
                String complexname = requestsEzdView.getComplexname();
                if (groupNameComplex == null || !groupNameComplex.equals(thisGroupName)) {
                    groupNameComplex = thisGroupName;
                    try {
                        clas = extractDigits(thisGroupName);
                    } catch (NumberFormatException e) //т.е. в названии группы нет чисел
                    {
                        clas = 0;
                    }
                    if (clas > 0 && clas < 5)//1-4
                    {
                        if (complexname.contains("5-11")) {
                            badComplex = true;
                        }

                    } else {
                        if (clas > 4 && clas < 12)//5-11
                        {
                            if (complexname.contains("1-4")) {
                                badComplex = true;
                            }
                        }
                    }
                }

                //Если комплекс подходит, то ...
                if (!badComplex) {
                    //Если это новое сочетание группы + орг, то находим для них все используемые даты
                    if (curguidDATES == null || curgroupNameDATES == null || !curguidDATES.equals(thisOrgGuid)
                            || !curgroupNameDATES.equals(thisGroupName)) {
                        curguidDATES = thisOrgGuid;
                        curgroupNameDATES = thisGroupName;

                        datesForThis = new ArrayList<>();

                        //Находим даты от одной начальной
                        curDateDates = date;
                        //Сколько дней пропустить
                        countwait = allIdtoSetiings.get(thisIdOfOrg);
                        int countGoodday = 0;

                        do {
                            goodProd = false;
                            goodSpec = false;
                            for (ProductionCalendar productionCalendar : productionCalendars) {
                                if (productionCalendar.getDay().equals(curDateDates)) {
                                    goodProd = true;
                                    break;
                                }
                            }
                            if (!goodProd) {
                                ///ПЕРЕДЕЛАТЬ ДЛЯ УСКОРЕНИЯ Т.К. ВЬЮХА БУДЕТ СОРТИРОВАНА!!!!!
                                for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                                    if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate())
                                            .equals(curDateDates) && requestsEzdSpecialDateView.getIdoforg()
                                            .equals(thisIdOfOrg) && requestsEzdSpecialDateView.getGroupname()
                                            .equals(thisGroupName)) {
                                        goodSpec = true;
                                        break;
                                    }
                                }
                            }
                            if (!goodSpec) {
                                if (countwait == null || countwait == 0) {
                                    datesForThis.add(curDateDates);
                                    countGoodday++;
                                } else {
                                    countwait--;
                                }
                            }
                            curDateDates = CalendarUtils.addOneDay(curDateDates);
                        } while (countGoodday < countDayz);
                    }

                    curDate = CalendarUtils.startOfDay(requestsEzdView.getMenudate());
                    //Если для данной комбинации орг+группа есть такая дата и компелекс подходит для группы, то ..
                    if (datesForThis.contains(curDate)) {
                        //Заполняем ответ

                        //Если организация с такой guid не встречалась раньше, то создаем
                        if (orgGuidResponse == null || !orgGuidResponse.equals(thisOrgGuid)) {
                            discountComplexOrg = new DiscountComplexOrg();
                            discountComplexOrg.setGuid(thisOrgGuid);
                            responseToEZD.getOrg().add(discountComplexOrg);
                            orgGuidResponse = thisOrgGuid;
                            groupNameResponse = null;
                        }

                        //Достаем группы
                        if (groupNameResponse == null || !groupNameResponse.equals(thisGroupName)) {
                            discountComplexGroup = new DiscountComplexGroup();
                            discountComplexGroup.setGroupName(thisGroupName);
                            discountComplexOrg.getGroups().add(discountComplexGroup);
                            groupNameResponse = thisGroupName;
                            currentDatesResponse = null;
                        }


                        curDatesString = curDate.toString();
                        //Достаем дни
                        if (currentDatesResponse == null || !currentDatesResponse.equals(curDatesString)) {
                            discountComplexItem = new DiscountComplexItem();
                            discountComplexItem.setDate(new SimpleDateFormat("dd.MM.yyyy").format(curDate));
                            discountComplexGroup.getDays().add(discountComplexItem);
                            currentDatesResponse = curDatesString;
                        }

                        complexesItem = new ComplexesItem();
                        complexesItem.setComplexname(complexname);
                        complexesItem.setIdofcomplex(requestsEzdView.getIdofcomplex().toString());
                        discountComplexItem.getComplexeslist().add(complexesItem);
                    }
                }
            }

            logger.info("Ответ для ЭЖД успешно сформирован");

            responseToEZD.setErrorCode(Long.toString(ResponseCodes.RC_OK.getCode()));
            responseToEZD.setErrorMessage(ResponseCodes.RC_OK.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseToEZD).build();
        } catch (IllegalArgumentException e) {
            logger.error(String.format("%s", e));
            responseToEZD.setErrorCode(Long.toString(ResponseCodes.RC_INTERNAL_ERROR.getCode()));
            responseToEZD.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(responseToEZD).build();
        } catch (Exception e) {
            logger.error(String.format("%s", e));
            responseToEZD.setErrorCode(Long.toString(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode()));
            responseToEZD.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(responseToEZD).build();
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
            } else {
                return Integer.valueOf(builder.toString());
            }
        }
        return Integer.valueOf(builder.toString());
    }
}
