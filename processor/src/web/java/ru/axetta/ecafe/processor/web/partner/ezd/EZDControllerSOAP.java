/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzd;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdSpecialDateView;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.ProductionCalendar;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import java.text.SimpleDateFormat;
import java.util.*;

@WebService
public class EZDControllerSOAP extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(EZDControllerSOAP.class);
    private static final int SETTING_TYPE = 11001;

    class DateFromArmeForOrg {

        private String groupName;
        private Long idOrd;

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public Long getIdOrd() {
            return idOrd;
        }

        public void setIdOrd(Long idOrd) {
            this.idOrd = idOrd;
        }
    }

    @WebMethod(operationName = "requestscomplex")
    public List<ResponseToEZDResult> requestscomplex(@WebParam(name = "orders") List<ResponseFromEzd> orders) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        List<ResponseToEZDResult> responseToEZDResults = new ArrayList<>();

        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

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

            //Соотношение группы - орг для всех полученных ORG
            List<DateFromArmeForOrg> groupIdOrg = new ArrayList<>();
            String groupName = null;
            String gui = null;
            for (ResponseFromEzd responseFromEzd : orders) {
                String curGuid = responseFromEzd.getGuidOrg();
                if (gui == null || !gui.equals(curGuid)) {
                    gui = curGuid;
                    Long curIdOrg = DAOUtils.findOrgByGuid(persistenceSession, curGuid).getIdOfOrg();
                    String curGroupName = responseFromEzd.getGroupName();
                    if (groupName == null || !groupName.equals(curGroupName)) {
                        groupName = curGroupName;
                        //Создаем доп список с запрещенными датами
                        DateFromArmeForOrg dateFromArmeForOrg = new DateFromArmeForOrg();
                        dateFromArmeForOrg.setIdOrd(curIdOrg);
                        dateFromArmeForOrg.setGroupName(curGroupName);
                        groupIdOrg.add(dateFromArmeForOrg);

                    }
                }
            }

            //Берём текущую дату
            Date date = new Date();
            date = CalendarUtils.addOneDay(date);
            date = CalendarUtils.startOfDay(date);

            Date startDate = date;
            //Собираем доп запрещенные даты для орг + группа на основе инфы с АРМ
            List<RequestsEzdSpecialDateView> dateNotWorksFromARM = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : allIdtoSetiings.entrySet()) {
                Long idOrg = entry.getKey();
                Integer countDays = entry.getValue();

                for (DateFromArmeForOrg dateFromArmeForOrg : groupIdOrg) {
                    if (idOrg.equals(dateFromArmeForOrg.getIdOrd())) {
                        Integer countBadday = countDays;
                        startDate = date;
                        do {
                            boolean goodProd = false;
                            boolean goodSpec = false;
                            for (ProductionCalendar productionCalendar : productionCalendars) {
                                if (productionCalendar.getDay().equals(startDate)) {
                                    goodProd = true;
                                    break;
                                }
                            }
                            if (!goodProd) {
                                ///ПЕРЕДЕЛАТЬ ДЛЯ УСКОРЕНИЯ Т.К. ВЬЮХА БУДЕТ СОРТИРОВАНА!!!!!
                                for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                                    if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate())
                                            .equals(startDate) && requestsEzdSpecialDateView.getIdoforg().equals(idOrg)
                                            && requestsEzdSpecialDateView.getGroupname()
                                            .equals(dateFromArmeForOrg.getGroupName())) {
                                        goodSpec = true;
                                        break;
                                    }
                                }
                            }
                            if (!goodSpec) {
                                if (countBadday != 0) {
                                    RequestsEzdSpecialDateView requestsEzdSpecialDateView = new RequestsEzdSpecialDateView();
                                    requestsEzdSpecialDateView.setIdoforg(idOrg);
                                    requestsEzdSpecialDateView.setGroupname(dateFromArmeForOrg.getGroupName());
                                    requestsEzdSpecialDateView.setSpecDate(startDate);
                                    dateNotWorksFromARM.add(requestsEzdSpecialDateView);
                                    countBadday--;
                                }
                                startDate = CalendarUtils.addOneDay(startDate);
                            }
                        } while (countBadday != 0);
                    }
                }

            }
            //Добавляем дополнительные запрещенные даты, полученные от АРМ
            requestsEzdSpecialDateViews.addAll(dateNotWorksFromARM);

            Integer maxVersion = DAOUtils.getMaxVersionForEZD(persistenceSession);
            for (ResponseFromEzd responseFromEzd : orders) {
                String currentGuid = responseFromEzd.getGuidOrg();
                String currentGroupName = responseFromEzd.getGroupName();
                Date currentDate = new SimpleDateFormat("dd.MM.yy").parse(responseFromEzd.getDate());
                currentDate = CalendarUtils.startOfDay(currentDate);

                Org org = DAOUtils.findOrgByGuid(persistenceSession, currentGuid);
                boolean good = true;
                if (org == null || org.getState() == 0 || org.getType().equals(OrganizationType.SUPPLIER)) {
                    ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
                    responseToEZDResult.setGuidOrg(currentGuid);
                    responseToEZDResult.setGroupName(currentGroupName);
                    responseToEZDResult.setDate(responseFromEzd.getDate());
                    responseToEZDResult.setIdOfComplex(responseFromEzd.getIdOfComplex());
                    responseToEZDResult.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
                    responseToEZDResult.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
                    responseToEZDResults.add(responseToEZDResult);
                    good = false;
                }
                if (DAOUtils.getGroupNamesToOrgsByOrgAndGroupName(persistenceSession, org, currentGroupName) == null) {
                    ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
                    responseToEZDResult.setGuidOrg(currentGuid);
                    responseToEZDResult.setGroupName(currentGroupName);
                    responseToEZDResult.setDate(responseFromEzd.getDate());
                    responseToEZDResult.setIdOfComplex(responseFromEzd.getIdOfComplex());
                    responseToEZDResult.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
                    responseToEZDResult.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
                    responseToEZDResults.add(responseToEZDResult);
                    good = false;
                }
                for (ProductionCalendar productionCalendar : productionCalendars) {
                    if (productionCalendar.getDay().equals(currentDate)) {
                        ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
                        responseToEZDResult.setGuidOrg(currentGuid);
                        responseToEZDResult.setGroupName(currentGroupName);
                        responseToEZDResult.setDate(responseFromEzd.getDate());
                        responseToEZDResult.setIdOfComplex(responseFromEzd.getIdOfComplex());
                        responseToEZDResult.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                        responseToEZDResult.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                        responseToEZDResults.add(responseToEZDResult);
                        good = false;
                    }
                }
                for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {
                    if (CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()).equals(currentDate)
                            && requestsEzdSpecialDateView.getIdoforg().equals(org.getIdOfOrg())
                            && requestsEzdSpecialDateView.getGroupname().equals(currentGroupName)) {
                        ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
                        responseToEZDResult.setGuidOrg(currentGuid);
                        responseToEZDResult.setGroupName(currentGroupName);
                        responseToEZDResult.setDate(responseFromEzd.getDate());
                        responseToEZDResult.setIdOfComplex(responseFromEzd.getIdOfComplex());
                        responseToEZDResult.setErrorCode(ResponseCodes.RC_WRONG_DATA.getCode().toString());
                        responseToEZDResult.setErrorMessage(ResponseCodes.RC_WRONG_DATA.toString());
                        responseToEZDResults.add(responseToEZDResult);
                        good = false;
                    }
                }
                if (good) {
                    maxVersion++;
                    if (!DAOUtils
                            .findSameRequestFromEZD(persistenceSession, org.getIdOfOrg(), currentGroupName, currentDate,
                                    responseFromEzd.getIdOfComplex())) {
                        DAOUtils.updateRequestFromEZD(persistenceSession, org.getIdOfOrg(), currentGroupName,
                                currentDate, responseFromEzd.getIdOfComplex(), responseFromEzd.getCount(), maxVersion);
                    } else {
                        RequestsEzd requestsEzd = new RequestsEzd();

                        requestsEzd.setIdOfOrg(org.getIdOfOrg());
                        requestsEzd.setGroupname(currentGroupName);
                        requestsEzd.setDateappointment(currentDate);
                        requestsEzd.setIdofcomplex(responseFromEzd.getIdOfComplex());
                        requestsEzd.setComplexname(responseFromEzd.getComplexName());
                        requestsEzd.setComplexcount(responseFromEzd.getCount());
                        requestsEzd.setUsername(responseFromEzd.getUserName());
                        requestsEzd.setCreateddate(new Date());
                        requestsEzd.setVersionrecord(maxVersion);
                        persistenceSession.save(requestsEzd);

                        ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
                        responseToEZDResult.setGuidOrg(currentGuid);
                        responseToEZDResult.setGroupName(currentGroupName);
                        responseToEZDResult.setDate(responseFromEzd.getDate());
                        responseToEZDResult.setIdOfComplex(responseFromEzd.getIdOfComplex());
                        responseToEZDResult.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
                        responseToEZDResult.setErrorMessage(ResponseCodes.RC_OK.toString());
                        responseToEZDResults.add(responseToEZDResult);
                    }
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return responseToEZDResults;
        } catch (Exception e)

        {
            responseToEZDResults = new ArrayList<>();
            ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
            responseToEZDResult.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
            responseToEZDResult.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            responseToEZDResults.add(responseToEZDResult);
            return responseToEZDResults;
        }

        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
}



