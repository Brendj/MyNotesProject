/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CryptoSign;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdMenuView;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdSpecialDateView;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdView;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

@Path(value = "")
@Controller
public class EzdController {

    private Logger logger = LoggerFactory.getLogger(EzdController.class);
    private static final int SETTING_TYPE = 11001;
    private static final long TIME_MAX = 39600000; //11:00
    private static final int SERT_NUM_QR =  65536;
    private static final int SIZE_DATE =  37;
    private static final int TIME_ACTIVE_QR =  300000;
    public static final String KEY_FOR_QR = "ecafe.processor.ezd.qr.key";
    public static final String GROUPS_FOR_QR = "ecafe.processor.ezd.qr.groups";
    public static final String GROUPS_FOR_QR_INVERSE = "ecafe.processor.ezd.qr.groups.inverse";
    public static final String BETWEEN_GROUPS_FOR_QR = "ecafe.processor.ezd.qr.betweengroups";
    public static final String BETWEEN_GROUPS_FOR_QR_INVERSE = "ecafe.processor.ezd.qr.betweengroups.inverse";
    public static final String EGEGROUP_QR_MASK = "ecafe.processor.ezd.qr.agetypegroups.mask";
    public static final String EGEGROUP_QR_MASK_INVERSE = "ecafe.processor.ezd.qr.agetypegroups.mask.inverse";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "discountcomplexlist")
    public Response getComplexList(@Context HttpServletRequest request) {
        String type;
        try
        {
            type = request.getParameterMap().get("type")[0];
        }
        catch (Exception e)
        {
            type = "full";
        }
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
            persistenceSession = runtimeContext.createExternalServicesPersistenceSession();
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
            requestsEzdViews = DAOUtils.getAllDateFromViewEZD(persistenceSession, null, null);
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
                if (countwait != null) {
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
                        }
                        startedDate = CalendarUtils.addOneDay(startedDate);
                    } while (!flag);
                }
                List<Date> dates = new ArrayList<>();
                dates.add(startedDate);
                massCorrectDates.put(counter, dates);
                counter++;
            }

            for (int i = 0; i < massCorrectDates.size(); i++) {
                List<Date> dates = massCorrectDates.get((long) i);
                RequestsEzdView requestsEzdView = requestsEzdViews.get(i);

                Date startedDate = CalendarUtils.addOneDay(dates.get(0));
                Integer countMax = countDayz;
                //4.1
                boolean flagend = false;
                boolean flag;
                ProductionCalendar productionCalendarSaved = null;
                boolean flag2;

                do {
                    do {
                        flag = false;
                        flag2 = false;
                        for (ProductionCalendar productionCalendar : productionCalendars) {
                            if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(startedDate)) {
                                flag = true;
                                productionCalendarSaved = productionCalendar;
                                break;
                            }
                        }
                        //4.1.1
                        if (flag) {
                            if (productionCalendarSaved.getFlag() == 2) {
                                startedDate = CalendarUtils.addOneDay(startedDate);
                                flag2 = true;
                            }

                            if (!flag2) {
                                Integer week = getWeekendFromSpecDates(requestsEzdView.getGroupname(),
                                        requestsEzdView.getIdoforg(), startedDate, requestsEzdSpecialDateViews);
                                if (week != null && week == 0) {
                                    dates.add(startedDate);
                                    countMax = countMax - 1;
                                    if (countMax == 0) {
                                        flagend = true;
                                    }
                                } else {
                                    flag2 = true;
                                }
                                if (week == null && CalendarUtils.getDayOfWeek(startedDate) == Calendar.SATURDAY) {
                                    if (DAOReadonlyService.getInstance()
                                            .isSixWorkWeekOrgAndGroup(requestsEzdView.getIdoforg(), requestsEzdView.getGroupname())) {
                                        dates.add(startedDate);
                                        countMax = countMax - 1;
                                        if (countMax == 0) {
                                            flagend = true;
                                        }
                                    } else {
                                        flag2 = true;
                                    }
                                }
                            }
                        } else {
                            //4.2
                            Integer week = getWeekendFromSpecDates(requestsEzdView.getGroupname(),
                                    requestsEzdView.getIdoforg(), startedDate, requestsEzdSpecialDateViews);
                            if (week == null || week == 0) {
                                dates.add(startedDate);
                                countMax = countMax - 1;
                                if (countMax == 0) {
                                    flagend = true;
                                }
                            } else {
                                flag2 = true;
                            }
                        }
                        startedDate = CalendarUtils.addOneDay(startedDate);
                    } while (flag2 && !flagend);
                } while (!flagend);
            }
            //Подготовка окончена, далее сбор самого меню
            List<RequestsEzdMenuView> allMenuForEZD = null;
            allMenuForEZD = DAOUtils.getAllMenuForEZD(persistenceSession, null);
            if (allMenuForEZD == null) {
                allMenuForEZD = new ArrayList<>();
            }
            Integer counter1 = 0;
            List<RequestsEzdMenuView> menuforCurrentOrg = new ArrayList<>();
            Long idCurrentOrg = null;
            Long nameCurrentGroup = null;
            boolean goodComplex;
            Integer clas;
            //Здесь хранятся последние сформированные данные по guid, group и дата для ответа в ЭЖД
            String orgGuidResponse = null;
            String groupNameResponse = null;
            String currentDatesResponse = null;
            DiscountComplexOrg discountComplexOrg = null;
            DiscountComplexGroup discountComplexGroup = null;
            DiscountComplexItem discountComplexItem = null;
            String curDatesString;
            ComplexesItem complexesItem;
            List complexes = null;
            for (RequestsEzdView requestsEzdView : requestsEzdViews) {
                String curGroupName = requestsEzdView.getGroupname();
                Long ekisid = requestsEzdView.getEkisid();
                String guid = requestsEzdView.getOrgguid();
                Long idOforg = requestsEzdView.getIdoforg();
                List<Date> dates = massCorrectDates.get((long) counter1);
                counter1++;
                if (!requestsEzdView.getUsewebarm() && (type.equals("full") || type.equals("noWebArm"))) {
                    //Заполняем список меню для текущей организации
                    if (menuforCurrentOrg.isEmpty() || (idCurrentOrg != null && !idCurrentOrg
                            .equals(requestsEzdView.getIdoforg()))) {
                        menuforCurrentOrg.clear();
                        idCurrentOrg = requestsEzdView.getIdoforg();
                        for (RequestsEzdMenuView requestsEzdMenuView : allMenuForEZD) {
                            if (requestsEzdMenuView.getIdOforg().equals(requestsEzdView.getIdoforg())) {
                                menuforCurrentOrg.add(requestsEzdMenuView);
                            }
                        }
                    }

                    for (RequestsEzdMenuView requestsEzdMenuView : menuforCurrentOrg) {
                        //Если такая дата подходит для группы + орг
                        if (dates.contains(CalendarUtils.startOfDay(requestsEzdMenuView.getMenuDate()))) {
                            //Проверка на то, что данный комплекс подходит для данной группы
                            String complexname = requestsEzdMenuView.getComplexname();
                            goodComplex = false;
                            try {
                                clas = extractDigits(curGroupName);
                            } catch (NumberFormatException e) //т.е. в названии группы нет чисел
                            {
                                clas = 0;
                            }
                            if (clas > 0 && clas < 5)//1-4
                            {
                                if (complexname.contains("1-4")) {
                                    goodComplex = true;
                                }

                            } else {
                                if (clas > 4 && clas < 12)//5-11
                                {
                                    if (complexname.contains("5-")) {
                                        goodComplex = true;
                                    }
                                }
                            }

                            //Если комплекс подходит, то ...
                            if (goodComplex) {
                                //Заполняем ответ

                                //Если организация с такой guid не встречалась раньше, то создаем
                                if (orgGuidResponse == null || !orgGuidResponse.equals(guid)) {
                                    discountComplexOrg = new DiscountComplexOrg();
                                    discountComplexOrg.setGuid(guid);
                                    discountComplexOrg.setEkisid(ekisid);
                                    responseToEZD.getOrg().add(discountComplexOrg);
                                    orgGuidResponse = guid;
                                    groupNameResponse = null;
                                }

                                //Достаем группы
                                if (groupNameResponse == null || !groupNameResponse.equals(curGroupName)) {
                                    discountComplexGroup = new DiscountComplexGroup();
                                    discountComplexGroup.setGroupName(curGroupName);
                                    discountComplexOrg.getGroups().add(discountComplexGroup);
                                    groupNameResponse = curGroupName;
                                    currentDatesResponse = null;
                                }


                                curDatesString = requestsEzdMenuView.getMenuDate().toString();
                                //Достаем дни
                                if (currentDatesResponse == null || !currentDatesResponse.equals(curDatesString)) {
                                    discountComplexItem = new DiscountComplexItem();
                                    discountComplexItem.setDate(new SimpleDateFormat("dd.MM.yyyy")
                                            .format(requestsEzdMenuView.getMenuDate()));
                                    discountComplexGroup.getDays().add(discountComplexItem);
                                    currentDatesResponse = curDatesString;
                                }

                                complexesItem = new ComplexesItem();
                                complexesItem.setComplexname(complexname);
                                complexesItem.setIdofcomplex(requestsEzdMenuView.getIdofcomplex().toString());
                                discountComplexItem.getComplexeslist().add(complexesItem);
                            }
                        }
                    }
                } else {
                    if ((type.equals("full") || type.equals("WebArm"))) {
                        //Часть по web-арм
                        if (idCurrentOrg == null || !idCurrentOrg.equals(idOforg)) {
                            idCurrentOrg = idOforg;
                            complexes = null;
                            List<Long> groupsOrgs = DAOService.getInstance().getOrgGroupsbyOrgForWEBARM(idOforg);
                            if (groupsOrgs.isEmpty()) {
                                //7.2
                                List<Long> complexesTemp = DAOService.getInstance().getComplexesByOrgForWEBARM(idOforg);
                                if (!complexesTemp.isEmpty()) {
                                    //7.4
                                    complexes = DAOService.getInstance().getComplexesByComplexForWEBARM(complexesTemp);
                                }
                            } else {
                                //7.3
                                complexes = DAOService.getInstance().getComplexesByGroupForWEBARM(groupsOrgs);
                            }
                        }

                        if (complexes != null) {
                            Map<Date, List<Object[]>> conteiner = new TreeMap<>();
                            for (Object complex : complexes) {
                                //7.5
                                Object[] obj = (Object[]) complex;
                                Date startDate = (Date) (obj[2]);
                                Date endDate = (Date) (obj[3]);
                                Long idofagegroupitem = ((BigInteger) (obj[4])).longValue();
                                for (Date date1 : dates) {
                                    if (date1.getTime() > startDate.getTime() && date1.getTime() < endDate.getTime()) {
                                        goodComplex = false;
                                        try {
                                            clas = extractDigits(curGroupName);
                                        } catch (NumberFormatException e) //т.е. в названии группы нет чисел
                                        {
                                            clas = 0;
                                        }
                                        if (clas > 0 && clas < 5)//1-4
                                        {
                                            if (idofagegroupitem == 3 || idofagegroupitem == 7) {
                                                goodComplex = true;
                                            }

                                        } else {
                                            if (clas > 4 && clas < 12)//5-11
                                            {
                                                if (idofagegroupitem == 4 || idofagegroupitem == 7) {
                                                    goodComplex = true;
                                                }
                                            }
                                        }
                                        if (goodComplex) {
                                            List<Object[]> objects = conteiner.get(date1);
                                            if (objects == null) {
                                                objects = new ArrayList<>();
                                            }
                                            objects.add(obj);
                                            conteiner.put(date1, objects);
                                        }
                                    }
                                }
                            }
                            for (Map.Entry<Date, List<Object[]>> entry : conteiner.entrySet()) {
                                Date date1 = entry.getKey();
                                List<Object[]> complexs = entry.getValue();
                                for (Object[] obj : complexs) {
                                    Long idComplex = ((BigInteger) (obj[0])).longValue();
                                    String complexName = (String) (obj[1]);
                                    //Заполняем ответ
                                    //Если организация с такой guid не встречалась раньше, то создаем
                                    if (orgGuidResponse == null || !orgGuidResponse.equals(guid)) {
                                        discountComplexOrg = new DiscountComplexOrg();
                                        discountComplexOrg.setGuid(guid);
                                        discountComplexOrg.setEkisid(ekisid);
                                        responseToEZD.getOrg().add(discountComplexOrg);
                                        orgGuidResponse = guid;
                                        groupNameResponse = null;
                                    }

                                    //Достаем группы
                                    if (groupNameResponse == null || !groupNameResponse.equals(curGroupName)) {
                                        discountComplexGroup = new DiscountComplexGroup();
                                        discountComplexGroup.setGroupName(curGroupName);
                                        discountComplexOrg.getGroups().add(discountComplexGroup);
                                        groupNameResponse = curGroupName;
                                        currentDatesResponse = null;
                                    }


                                    curDatesString = date1.toString();
                                    //Достаем дни
                                    if (currentDatesResponse == null || !currentDatesResponse.equals(curDatesString)) {
                                        discountComplexItem = new DiscountComplexItem();
                                        discountComplexItem.setDate(new SimpleDateFormat("dd.MM.yyyy").format(date1));
                                        discountComplexGroup.getDays().add(discountComplexItem);
                                        currentDatesResponse = curDatesString;
                                    }

                                    complexesItem = new ComplexesItem();
                                    complexesItem.setComplexname(complexName);
                                    complexesItem.setIdofcomplex(idComplex.toString());
                                    discountComplexItem.getComplexeslist().add(complexesItem);
                                }
                            }
                        }
                    }
                }
            }
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
                    break;
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

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "getQRCode")
    public Response getQRCode(@Context HttpServletRequest request) {
        logger.info("Старт генерации QR кода");
        Result result = new Result();
        String securityKey = "";
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if (header.toLowerCase().equals("key"))
                {
                    securityKey = request.getHeader(header);
                    break;
                }
            }
        }
        String meshGuid="";
        try {
            for (String key : request.getParameterMap().keySet()) {
                String tmpString = key.toLowerCase();
                if (!tmpString.equals("meshguid")) {
                    continue;
                }
                meshGuid = request.getParameterMap().get(key)[0];
            }
        } catch (Exception e)
        {
            meshGuid="";
        }
        if (securityKey.isEmpty() || meshGuid.isEmpty()) {
            logger.error("Отсутствуют обязательные параметры");
            result.setErrorCode(ResponseCodes.RC_PARAM_NOT_FOUND.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_PARAM_NOT_FOUND.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }
        logger.info("MeshGuid: " + meshGuid);
        //Контроль безопасности
        if (!validateAccess(securityKey)) {
            logger.error("Неверный ключ доступа");
            result.setErrorCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_WRONG_KEY.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = DAOUtils.findClientByMeshGuid(session, meshGuid);
            if (client==null)
            {
                logger.error("Клиент не найден");
                result.setErrorCode(ResponseCodes.RC_CLIENT_NOT_FOUND.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_CLIENT_NOT_FOUND.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
            String groups = RuntimeContext.getInstance().getConfigProperties().getProperty(GROUPS_FOR_QR, "");
            String groupsInverse = RuntimeContext.getInstance().getConfigProperties().getProperty(GROUPS_FOR_QR_INVERSE, "");
            String betweenGroups = RuntimeContext.getInstance().getConfigProperties().getProperty(BETWEEN_GROUPS_FOR_QR, "");
            String betweenGroupsInverse = RuntimeContext.getInstance().getConfigProperties().getProperty(BETWEEN_GROUPS_FOR_QR_INVERSE, "");
            String ageGroupMask = RuntimeContext.getInstance().getConfigProperties().getProperty(EGEGROUP_QR_MASK, "");
            String ageGroupMaskInverse = RuntimeContext.getInstance().getConfigProperties().getProperty(EGEGROUP_QR_MASK_INVERSE, "");

            if (groups.isEmpty() || groupsInverse.isEmpty() || ageGroupMask.isEmpty() || ageGroupMaskInverse.isEmpty())
            {
                logger.error("Конфигурация не задана");
                result.setErrorCode(ResponseCodes.RC_NO_CONFIG.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_NO_CONFIG.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }

            boolean erroringroup = true;
            boolean errorinbetweengroup = true;
            ClientGroup.Predefined predefined = null;
            if (client.getIdOfClientGroup() != null) {
                //Получаем предопределенную группу
                predefined = ClientGroup.Predefined.parse(client.getIdOfClientGroup());
                //Проверка списка групп
                String[] idClientsGroup = groups.split(",");
                for (String idClientGroup : idClientsGroup) {
                    if (client.getIdOfClientGroup().equals(Long.valueOf(idClientGroup)))
                    {
                        erroringroup=false;
                        break;
                    }
                }
                try {
                    //Проверка диапазона групп
                    String[] idClientsGroupBetween = betweenGroups.split(",");
                    if (idClientsGroupBetween[0].equals("infinity"))
                    {
                        if (idClientsGroupBetween[1].equals("infinity"))
                        {
                            errorinbetweengroup = false;
                        }
                        else
                        {
                            if (client.getIdOfClientGroup() < Long.parseLong(idClientsGroupBetween[1])) {
                                errorinbetweengroup = false;
                            }
                        }
                    } else
                    {
                        if (idClientsGroupBetween[1].equals("infinity"))
                        {
                            if (client.getIdOfClientGroup() > Long.parseLong(idClientsGroupBetween[0])) {
                                errorinbetweengroup = false;
                            }
                        }
                        else
                        {
                            if (client.getIdOfClientGroup() > Long.parseLong(idClientsGroupBetween[0])
                                    && client.getIdOfClientGroup() < Long.parseLong(idClientsGroupBetween[1])) {
                                errorinbetweengroup = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Внутренняя ошибка сервиса");
                    result.setErrorCode(ResponseCodes.RC_SERVER_ERROR.getCode().toString());
                    result.setErrorMessage(ResponseCodes.RC_SERVER_ERROR.toString());
                    return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
                }
            }
            if (Boolean.parseBoolean(groupsInverse))
            {
                erroringroup = !erroringroup;
            }
            if (Boolean.parseBoolean(betweenGroupsInverse))
            {
                errorinbetweengroup = !errorinbetweengroup;
            }

            boolean erroringage = false;

            if (predefined == null) {
                erroringage = true;
                //Проверка маски группы только для НЕ предопределенных групп
                if (client.getAgeTypeGroup() != null) {
                    if (client.getAgeTypeGroup().toLowerCase().contains(ageGroupMask.toLowerCase())) {
                        erroringage = false;
                    }
                }
                if (Boolean.parseBoolean(ageGroupMaskInverse)) {
                    erroringage = !erroringage;
                }
            }


            if ((erroringroup && errorinbetweengroup) || erroringage)
            {
                logger.error("Для клиентов вашей категории пользование данным сервисом не предусмотрено");
                result.setErrorCode(ResponseCodes.RC_BAD_CATEGORY.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_BAD_CATEGORY.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
            if (client.getCards().isEmpty())
            {
                logger.error("На текущий момент доступ в здание невозможен по причине отсутствия активного электронного идентификатора");
                result.setErrorCode(ResponseCodes.RC_NO_CARD.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_NO_CARD.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }

            boolean activecard = false;
            for (Card card: client.getCards())
            {
                if (card.isActive())
                {
                    activecard = true;
                    break;
                }
            }
            if (!activecard)
            {
                logger.error("На текущий момент доступ в здание невозможен по причине заблокированного электронного идентификатора");
                result.setErrorCode(ResponseCodes.RC_NO_ACTIVE_CARD.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_NO_ACTIVE_CARD.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
            //////////////////
            //Получаем ключи или генерируем их
            CardSign cardSign = DAOReadonlyService.getInstance().getSignInform(SERT_NUM_QR);
            if (cardSign == null)
                cardSign = generatorKey();
            if (cardSign == null)
            {
                logger.error("Внутренняя ошибка при генерации ключей для QR-кодов");
                result.setErrorCode(ResponseCodes.RC_SERVER_ERROR.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_SERVER_ERROR.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
            Date startDate = new Date();
            Date endDate = new Date(startDate.getTime()+TIME_ACTIVE_QR);
            //Собираем, что подписываем
            byte[] lenght = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(SIZE_DATE).array();//1 байт
            byte[] clientId = ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(client.getIdOfClient()).array();//4 байта
            byte[] qrcode = asBytes (UUID.randomUUID());//16 байт
            byte[] codeCreator = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(1).array();//4 байта
            byte[] dateStart =  ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt((int) (startDate.getTime()/1000)).array();//4 байта
            byte[] dateEnd =  ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt((int) (endDate.getTime()/1000)).array();//4 байта
            byte[] sert = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(cardSign.getIdOfCardSign()).array();//4 байта
            //Здесь сформируется конечный вариант для подписания
            byte[] qr_data = new byte[37];
            System.arraycopy(lenght, 3, qr_data, 0, 1);
            System.arraycopy(clientId, 4, qr_data, 1, 4);
            System.arraycopy(qrcode, 0, qr_data, 5, 16);
            System.arraycopy(codeCreator, 0, qr_data, 21, 4);
            System.arraycopy(dateStart, 0, qr_data, 25, 4);
            System.arraycopy(dateEnd, 0, qr_data, 29, 4);
            System.arraycopy(sert, 0, qr_data, 33, 4);

            try {
                //Достаем приватный ключ для подписи
                PrivateKey pk = CryptoSign.loadPrivKey(cardSign.getPrivatekeycard());
                //Подписывание
                byte[] sign = CryptoSign.sign(qr_data, pk);

                //Сохраняем сгенерированный код
                ClientEnterQR clientEnterQR = new ClientEnterQR(client, qr_data, startDate, endDate, new Date());
                DAOService.getInstance().saveQRinfo(clientEnterQR);

                //Переводим в DER кодировку
                StringBuffer res = new StringBuffer(2*(qr_data.length + sign.length));
                for(int i=0; i < qr_data.length; i++)
                    res.append(String.format("%02X", qr_data[i]));
                for(int i=0; i< sign.length; i++)
                    res.append(String.format("%02X", sign[i]));

                //Формируем ответ
                ResponseToEZDQR responseToEZDQR = new ResponseToEZDQR();
                responseToEZDQR.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
                responseToEZDQR.setErrorMessage(ResponseCodes.RC_OK.toString());
                responseToEZDQR.setMeshguid(meshGuid);
                responseToEZDQR.setQr(res.toString());
                responseToEZDQR.setDateFrom(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startDate));
                responseToEZDQR.setDateTo(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endDate));
                return Response.status(HttpURLConnection.HTTP_OK).entity(responseToEZDQR).build();
            } catch (Exception e)
            {
                logger.error("Error in QR generate service: ", e);
                result.setErrorCode(ResponseCodes.RC_SERVER_ERROR.getCode().toString());
                result.setErrorMessage(ResponseCodes.RC_SERVER_ERROR.toString());
                return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при генерации ключей для QR кодов\": ", e);
            result.setErrorCode(ResponseCodes.RC_SERVER_ERROR.getCode().toString());
            result.setErrorMessage(ResponseCodes.RC_SERVER_ERROR.toString());
            return Response.status(HttpURLConnection.HTTP_OK).entity(result).build();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private boolean validateAccess(String key) {
        String keyinternal = RuntimeContext.getInstance().getConfigProperties().getProperty(KEY_FOR_QR, "");
        if (!key.isEmpty() && key.equals(keyinternal))
            return true;
        return false;
    }

    private CardSign generatorKey()
    {
        Session session = null;
        Transaction transaction = null;
        try {
            KeyPair pair = CryptoSign.keyPairGen();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            CardSign cardSign = new CardSign();
            cardSign.setIdOfCardSign(SERT_NUM_QR);
            //тип ECDSA
            cardSign.setSignType(1);
            cardSign.setManufacturerName("QR EZD");
            cardSign.setManufacturerCode(SERT_NUM_QR);
            cardSign.setSignData(publicKey.getEncoded());
            cardSign.setPrivatekeycard(privateKey.getEncoded());
            cardSign.setNewtypeprovider(true);
            session.save(cardSign);
            transaction.commit();
            transaction = null;
            logger.info("Созданы приватный и открытый ключи для QR кодов");
            return cardSign;
        } catch (Exception e) {
            logger.error("Ошибка при генерации ключей для QR кодов\": ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return null;
    }

    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
