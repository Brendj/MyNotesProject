/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdSpecialDateView;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.ProductionCalendar;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.RecoverableService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.ezd.ResponseFromEzd;
import ru.axetta.ecafe.processor.web.partner.ezd.Result;
import ru.axetta.ecafe.processor.web.partner.ezd.ResponseCodes;

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
public class EZDController extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(EZDController.class);
    private static final int SETTING_TYPE = 11001;

    @WebMethod(operationName = "dataFromEZD")
    public Result setDataFromEZD ( @WebParam(name = "orders") List<ResponseFromEzd> orders) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Result result = new Result();

        try
        {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            logger.info("Старт начала сбора данных по учебному календарю");
            //Загружаем все данные учебного календаря
            List<RequestsEzdSpecialDateView> requestsEzdSpecialDateViews = DAOUtils.getAllDateFromsSpecialDatesForEZD(persistenceSession);
            if (requestsEzdSpecialDateViews == null)
                requestsEzdSpecialDateViews = new ArrayList<>();
            logger.info(String.format("Всего записей по учебному календарю - %s", String.valueOf(requestsEzdSpecialDateViews.size())));

            logger.info("Старт начала сбора данных по производственному календарю");
            //Загружаем все данные производственного календаря
            List <ProductionCalendar> productionCalendars = DAOUtils.getAllDateFromProdactionCalendarForEZD(persistenceSession);
            if (productionCalendars == null)
                productionCalendars = new ArrayList<>();
            logger.info(String.format("Всего записей по производственному календарю - %s", String.valueOf(productionCalendars.size())));


            logger.info("Старт начала сбора настроек, полученных с АРМ для организаций");
            //Настройка с АРМ для всех id Org
            Map<Long, Integer> allIdtoSetiings = OrgSettingDAOUtils
                    .getOrgSettingItemByOrgAndType(persistenceSession, null, SETTING_TYPE);
            if (allIdtoSetiings == null)
                allIdtoSetiings = new HashMap<>();
            logger.info(String.format("Всего настроек с АРМ - %s", String.valueOf(allIdtoSetiings.size())));


            //Берём текущую дату
            Date date = new Date();
            date = CalendarUtils.addOneDay(date);
            date = CalendarUtils.startOfDay(date);

            Date startDate = date;
            List<RequestsEzdSpecialDateView> dateNotWorksFromARM = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : allIdtoSetiings.entrySet()) {
                Long idOrg = entry.getKey();
                Integer countDays = entry.getValue();
                for (RequestsEzdSpecialDateView requestsEzdSpecialDateView : requestsEzdSpecialDateViews) {

                }




                Integer countGoodday = 0;
                while (countGoodday < entry.getValue())
                {

                }




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
                                    .equals(startDate) && requestsEzdSpecialDateView.getIdoforg()
                                    .equals(entry.getKey())) {
                                //Создаем доп список с запрещенными датами
                                RequestsEzdSpecialDateView requestsEzdSpecialDateView1 = new RequestsEzdSpecialDateView();
                                requestsEzdSpecialDateView1.setIdoforg(entry.getKey());
                                requestsEzdSpecialDateView1.setGroupname(requestsEzdSpecialDateView.getGroupname());
                                requestsEzdSpecialDateView1.setSpecDate(CalendarUtils.startOfDay(requestsEzdSpecialDateView.getSpecDate()));
                                dateNotWorksFromARM.add(requestsEzdSpecialDateView1);
                                goodSpec = true;
                            }
                        }
                    }
                    if (!goodSpec) {
                        countGoodday++;
                    }
                    startDate = CalendarUtils.addOneDay(startDate);
                } while (countGoodday < entry.getValue());


                System.out.println("ID =  " + entry.getKey() + " День недели = " + entry.getValue());
            }




            for (ResponseFromEzd responseFromEzd: orders)
            {
                String currentGuid = responseFromEzd.getGuidOrg();
                String currentGroupName = responseFromEzd.getGroupName();
                Date currentDate = new SimpleDateFormat("dd.MM.yy").parse(responseFromEzd.getDate());


                Org org = DAOUtils.findOrgByGuid(persistenceSession, currentGuid);
                if (org == null || org.getState() == 0 || org.getType().equals(OrganizationType.SUPPLIER))
                {
                    result.setErrorCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
                    result.setErrorMessage(ResponseCodes.RC_INTERNAL_ERROR.toString());
                    return result;
                }
                if (DAOUtils.getGroupNamesToOrgsByOrgAndGroupName(persistenceSession, org, currentGroupName) == null)
                {
                    result.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
                    result.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
                    return result;
                }




            }


        } catch (Exception e)
        {

        }


    }
}
