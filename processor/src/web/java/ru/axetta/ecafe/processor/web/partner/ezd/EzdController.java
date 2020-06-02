/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdSpecialDateView;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdView;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
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
    private static final long TIME_MAX = 39600000; //11:00

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
    @Path(value = "discountcomplexlist")
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
            Date currentDate = CalendarUtils.startOfDay(date);


            logger.info("Старт начала сбора данных по производственному календарю");
            //Загружаем все данные производственного календаря
            List<ProductionCalendar> productionCalendars = DAOUtils
                    .getAllDateFromProdactionCalendarForFutureDates(persistenceSession);
            if (productionCalendars == null) {
                productionCalendars = new ArrayList<>();
            }
            logger.info(String.format("Всего записей по производственному календарю - %s",
                    String.valueOf(productionCalendars.size())));

            currentDate = getWorkedDateForProdactionCalendar(currentDate, productionCalendars);

            //Если время более 11:00, то на сегодня менять нельзя
            if (new Date().getTime() - CalendarUtils.startOfDay(new Date()).getTime() > TIME_MAX) {
                currentDate = CalendarUtils.addOneDay(currentDate);
                //currentDate = getWorkedDateForProdactionCalendar (currentDate, productionCalendars);
            }

            logger.info("Старт начала сбора данных по учебному календарю");
            //Загружаем все данные учебного календаря
            List<RequestsEzdSpecialDateView> requestsEzdSpecialDateViews = DAOUtils
                    .getAllDateFromsSpecialDatesForEZD(persistenceSession);
            if (requestsEzdSpecialDateViews == null) {
                requestsEzdSpecialDateViews = new ArrayList<>();
            }
            logger.info(String.format("Всего записей по учебному календарю - %s",
                    String.valueOf(requestsEzdSpecialDateViews.size())));

            logger.info("Старт начала сбора настроек, полученных с АРМ для организаций");
            //Настройка с АРМ для всех id Org
            Map<Long, Integer> allIdtoSetiings = OrgSettingDAOUtils
                    .getOrgSettingItemByOrgAndType(persistenceSession, null, SETTING_TYPE);
            if (allIdtoSetiings == null) {
                allIdtoSetiings = new HashMap<>();
            }
            logger.info(String.format("Всего настроек с АРМ - %s", String.valueOf(allIdtoSetiings.size())));

            //Получаем все данные для отправки в ЭЖД
            List<RequestsEzdView> requestsEzdViews = null;
            requestsEzdViews = DAOUtils
                    .getAllDateFromViewEZD(persistenceSession, null, null);
            if (requestsEzdViews == null) {
                requestsEzdViews = new ArrayList<>();
            }

            Map<Long, List<Date>> massCorrectDates = new HashMap<>();

            Long counter = 0L;
            for (RequestsEzdView requestsEzdView : requestsEzdViews) {
                Date startedDate = currentDate;
                String curGroupName = requestsEzdView.getGroupname();
                Long curOrg = requestsEzdView.getIdoforg();
                //Сколько дней пропустить
                Integer countwait = allIdtoSetiings.get(curOrg);

                boolean flag = false;
                do {
                    startedDate = getWorkedDateForProdactionCalendar(startedDate, productionCalendars);
                    Integer weekend = getWeekendFromSpecDates(curGroupName, curOrg, startedDate,
                            requestsEzdSpecialDateViews);
                    if (weekend == null || weekend == 0) {
                        //3.2
                        countwait = countwait - 1;
                        if (countwait == 0) {
                            flag = true;
                        }
                    } else {
                        startedDate = CalendarUtils.addOneDay(startedDate);
                    }
                } while (!flag);
                List<Date> dates = new ArrayList<>();
                dates.add(startedDate);
                massCorrectDates.put(counter, dates);
                counter++;
            }

            for (int i = 0; i < massCorrectDates.size(); i++) {
                List<Date> dates = massCorrectDates.get(i);
                RequestsEzdView requestsEzdView = requestsEzdViews.get(i);

                Date startedDate = CalendarUtils.addOneDay(dates.get(0));
                Integer countMax = countDayz;
                //4.1
                boolean flagend = false;
                do {
                    boolean flag = false;
                    do {
                        flag = false;
                        for (ProductionCalendar productionCalendar : productionCalendars) {
                            if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(startedDate)) {
                                if (productionCalendar.getFlag() == 2) {
                                    flag = true;
                                }
                            }
                        }
                    } while (flag);


                    Integer weekend = getWeekendFromSpecDates(requestsEzdView.getGroupname(),
                            requestsEzdView.getIdoforg(), startedDate, requestsEzdSpecialDateViews);
                    if (weekend == null || weekend == 0) {
                        if (CalendarUtils.getDayOfWeek(startedDate) == Calendar.SATURDAY) {
                            if (DAOReadonlyService.getInstance().isSixWorkWeekOrg(requestsEzdView.getIdoforg())) {
                                dates.add(startedDate);
                                countMax = countMax - 1;
                                if (countMax == 0) {
                                    flagend = true;
                                }
                            } else {
                                startedDate = CalendarUtils.addOneDay(startedDate);
                            }
                        } else {
                            startedDate = CalendarUtils.addOneDay(startedDate);
                        }

                    } else {
                        startedDate = CalendarUtils.addOneDay(startedDate);
                    }

                } while (!flagend);
            }


            ////Хранится последние guid и орг при вычислении дат
            //String curguidDATES = null;
            //String curgroupNameDATES = null;
            //
            ////Здесь храниться массив возможных дат для текущей комбинации guid + орг
            //List<Date> datesForThis = new ArrayList<>();
            //
            //boolean badComplex;
            //
            ////Здесь хранятся последние сформированные данные по guid, group и дата для ответа в ЭЖД
            //String orgGuidResponse = null;
            //String groupNameResponse = null;
            //String currentDatesResponse = null;
            //
            //DiscountComplexOrg discountComplexOrg = null;
            //DiscountComplexGroup discountComplexGroup = null;
            //DiscountComplexItem discountComplexItem = null;
            //ComplexesItem complexesItem;
            //
            ////Для экономии памяти
            //String thisOrgGuid;
            //String thisGroupName;
            //Long thisIdOfOrg;
            //Integer clas;
            //Date curDateDates;
            //Integer countwait;
            //boolean goodSub;
            //boolean goodSpec;
            //Date curDate;
            //String curDatesString;
            //Integer currentCountSpecDate = 0;
            //
            ////Количество частей для загрузки
            //Integer countParts = 10;
            //try {
            //    countParts = Integer
            //            .valueOf(runtimeContext.getConfigProperties().getProperty("ecafe.processor.ezd.parts", "10"));
            //    if (countParts == null) {
            //        countParts = 10;
            //    }
            //} catch (Exception e) {
            //    countParts = 10;
            //}
            //logger.info(String.format("Данные для ЭЖД будут загружены за %s обращения к БД", countParts.toString()));
            //
            //logger.info("Получаем общее количество школ");
            //List<Org> idOfOrgs = DAOUtils.getAllOrgWithGuid(persistenceSession);
            //logger.info(String.format("Общее колличество школ - %s", String.valueOf(idOfOrgs.size())));
            //
            //logger.info(
            //        String.format("Максимальный размер памяти - %s", String.valueOf(Runtime.getRuntime().maxMemory())));
            //Integer count = idOfOrgs.size();
            //Integer sizeofPart = count / countParts;
            //
            //Integer realCountPart;
            //if (count % countParts != 0) {
            //    realCountPart = countParts + 1;
            //} else {
            //    realCountPart = countParts;
            //}
            //
            ////Максимальное количество частей для загрузки
            //Integer maxcountParts = 0;
            //try {
            //    maxcountParts = Integer
            //            .valueOf(runtimeContext.getConfigProperties().getProperty("ecafe.processor.ezd.maxparts", "0"));
            //    if (maxcountParts == null) {
            //        maxcountParts = 0;
            //    }
            //} catch (Exception e) {
            //    maxcountParts = 0;
            //}
            //if (maxcountParts != 0) {
            //    logger.info(String.format("Будет загружено %s части из %s", maxcountParts.toString(), realCountPart));
            //}
            //
            //for (int i = 0; i < realCountPart; i++) {
            //    //см в отдельном документе
            //}

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
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

    private Date getWorkedDateForProdactionCalendar(Date currentDate, List<ProductionCalendar> productionCalendars) {
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
        return currentDate;
    }


    private Integer getWeekendFromSpecDates(String groupName, Long idOrg, Date specDate,
            List<RequestsEzdSpecialDateView> requestsEzdSpecialDateViews) {
        for (Integer k = 0; k < requestsEzdSpecialDateViews.size(); k++) {
            if (CalendarUtils.startOfDay(requestsEzdSpecialDateViews.get(k).getSpecDate()).equals(specDate)
                    && requestsEzdSpecialDateViews.get(k).getIdoforg().equals(idOrg) && (
                    requestsEzdSpecialDateViews.get(k).getGroupname().equals(groupName) || (
                            requestsEzdSpecialDateViews.get(k).getGroupname() == null))) {
                return requestsEzdSpecialDateViews.get(k).getIsweekend();
            }
        }
        //Если элемент по таким данным не найден
        return null;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "requestscomplex")
    public Result requestscomplex(@QueryParam(value = "GuidOrg") String guidOrg,
            @QueryParam(value = "GroupName") String groupName, @QueryParam(value = "Date") String dateR,
            @QueryParam(value = "UserName") String userName, @QueryParam(value = "idOfComplex") Long idOfComplex,
            @QueryParam(value = "complexName") String complexName, @QueryParam(value = "count") Integer count) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            logger.info("Старт начала сбора данных по производственному календарю");
            //Загружаем все данные производственного календаря
            List<ProductionCalendar> productionCalendars = DAOUtils
                    .getAllDateFromProdactionCalendarForFutureDates(persistenceSession);
            if (productionCalendars == null) {
                productionCalendars = new ArrayList<>();
            }
            logger.info(String.format("Всего записей по производственному календарю - %s",
                    String.valueOf(productionCalendars.size())));

            List<Org> orgs = DAOUtils.findOrgsByGuid(persistenceSession, guidOrg);
            List<Long> idOrgs = new ArrayList<>();
            for (Org org : orgs) {
                idOrgs.add(org.getIdOfOrg());
            }

            List<String> groupNames = new ArrayList<>();
            groupNames.add(groupName);
            logger.info("Старт начала сбора данных по учебному календарю");
            //Загружаем все данные учебного календаря
            List<RequestsEzdSpecialDateView> requestsEzdSpecialDateViews = DAOUtils
                    .getDateFromsSpecialDatesForEZD(persistenceSession, groupNames, idOrgs);
            if (requestsEzdSpecialDateViews == null) {
                requestsEzdSpecialDateViews = new ArrayList<>();
            }
            logger.info(String.format("Всего записей по учебному календарю - %s",
                    String.valueOf(requestsEzdSpecialDateViews.size())));

            //Настройка с АРМ для Org
            Map<Long, Integer> allIdtoSetiings = OrgSettingDAOUtils
                    .getOrgSettingItemByOrgAndType(persistenceSession, idOrgs, SETTING_TYPE);
            if (allIdtoSetiings == null) {
                allIdtoSetiings = new HashMap<>();
            }
            Result result = GeneralRequestMetod
                    .requestsComplexForOne(persistenceSession, productionCalendars, requestsEzdSpecialDateViews,
                            allIdtoSetiings, guidOrg, groupName, dateR, userName, idOfComplex, complexName, count);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return result;

        } catch (Exception e) {
            Result result = new Result();
            result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            return result;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
}
