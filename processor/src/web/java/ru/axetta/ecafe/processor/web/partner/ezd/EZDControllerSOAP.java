/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.EZD.RequestsEzdSpecialDateView;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.ProductionCalendar;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.OrgSettingDAOUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import java.util.*;

@WebService
public class EZDControllerSOAP extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(EZDControllerSOAP.class);
    private static final int SETTING_TYPE = 11001;

    @WebMethod(operationName = "requestscomplex")
    public List<ResponseToEZDResult> requestscomplex(@WebParam(name = "orders") List<ResponseFromEzd> orders) {
        Result result;
        List<ResponseToEZDResult> responseToEZDResults = new ArrayList<>();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<Org> orgs = new ArrayList<>();
            List<String> groupNames = new ArrayList<>(orders.size());
            for (ResponseFromEzd responseFromEzd : orders) {
                orgs.addAll(DAOUtils.findOrgsByGuid(persistenceSession,  responseFromEzd.getGuidOrg()));
                groupNames.add(responseFromEzd.getGroupName());
            }
            logger.info("Старт начала сбора данных по производственному календарю");
            //Загружаем все данные производственного календаря
            List<ProductionCalendar> productionCalendars = DAOUtils.getAllDateFromProdactionCalendarForFutureDates(persistenceSession);
            if (productionCalendars == null) {
                productionCalendars = new ArrayList<>();
            }
            logger.info(String.format("Всего записей по производственному календарю - %d", productionCalendars.size()));


            List<Long> idOrgs = new ArrayList<>();
            for (Org org : orgs) {
                idOrgs.add(org.getIdOfOrg());
            }

            logger.info("Старт начала сбора данных по учебному календарю");
            //Загружаем все данные учебного календаря
            List<RequestsEzdSpecialDateView> requestsEzdSpecialDateViews = DAOUtils
                    .getDateFromsSpecialDatesForEZD(persistenceSession, groupNames, idOrgs);
            if (requestsEzdSpecialDateViews == null) {
                requestsEzdSpecialDateViews = new ArrayList<>();
            }
            logger.info(String.format("Всего записей по учебному календарю - %s", String.valueOf(requestsEzdSpecialDateViews.size())));

            //Настройка с АРМ для Org
            Map<Long, Integer> allIdtoSetiings = OrgSettingDAOUtils
                    .getOrgSettingItemByOrgAndType(persistenceSession, idOrgs, SETTING_TYPE);
            if (allIdtoSetiings == null) {
                allIdtoSetiings = new HashMap<>();
            }

            for (ResponseFromEzd responseFromEzd : orders) {
                result = GeneralRequestMetod
                        .requestsComplexForOne(persistenceSession, productionCalendars, requestsEzdSpecialDateViews,
                                allIdtoSetiings,
                                responseFromEzd.getGuidOrg(), responseFromEzd.getGroupName(),
                                responseFromEzd.getDate(), responseFromEzd.getUserName(),
                                responseFromEzd.getIdOfComplex(), responseFromEzd.getComplexName(),
                                responseFromEzd.getCount());
                if (result.getErrorCode().equals(ResponseCodes.RC_OK.getCode().toString()) && result.getErrorMessage()
                        .equals(ResponseCodes.RC_OK.toString())) {
                    ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
                    responseToEZDResult.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
                    responseToEZDResult.setErrorMessage(ResponseCodes.RC_OK.toString());
                    responseToEZDResults.add(responseToEZDResult);
                } else {
                    ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();

                    responseToEZDResult.setGuidOrg(responseFromEzd.getGuidOrg());
                    responseToEZDResult.setGroupName(responseFromEzd.getGroupName());
                    responseToEZDResult.setDate(responseFromEzd.getDate());
                    responseToEZDResult.setUserName(responseFromEzd.getUserName());
                    responseToEZDResult.setIdOfComplex(responseFromEzd.getIdOfComplex());
                    responseToEZDResult.setComplexName(responseFromEzd.getComplexName());
                    responseToEZDResult.setCount(responseFromEzd.getCount());
                    responseToEZDResult.setErrorCode(result.getErrorCode());
                    responseToEZDResult.setErrorMessage(result.getErrorMessage());
                    responseToEZDResults.add(responseToEZDResult);
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("", e);
            responseToEZDResults = new ArrayList<>();
            ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
            responseToEZDResult.setErrorCode(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.getCode().toString());
            responseToEZDResult.setErrorMessage(ResponseCodes.RC_BAD_ARGUMENTS_ERROR.toString());
            responseToEZDResults.add(responseToEZDResult);
            return responseToEZDResults;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return responseToEZDResults;
    }
}



