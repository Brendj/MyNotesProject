/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
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
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.List;

@Path(value = "")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Controller
public class EzdController {

    private Logger logger = LoggerFactory.getLogger(EzdController.class);
    private static final int SETTING_TYPE = 11001;
    private static final int THRESHOLD_VALUE = 39600000;//39600000 - Это время в миллесекундах между началом для (00:00) и 11:00
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
    @Path(value = "/discountComplexList")
    public Response getComplexList(@FormParam(value="GuidOrg") String guidOrg,
             @FormParam(value="GroupName") String groupName) throws Exception {
        ResponseDiscountComplex responseDiscountComplex = new ResponseDiscountComplex();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        //Вычисление результата запроса
        try {

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = DAOUtils
                    .findOrgByGuid(persistenceSession, guidOrg);
            if (org == null) {
                throw new IllegalArgumentException("Org with guid = " + guidOrg + " is not found");
            }
            if (org.getState() == 0)
            {
                throw new IllegalArgumentException("Org with guid = " + guidOrg + " is inactive");
            }
            if (org.getType() == OrganizationType.SUPPLIER)
            {
                throw new IllegalArgumentException("Org with guid = " + guidOrg + " is supplier");
            }

            GroupNamesToOrgs groupNamesToOrgs = DAOUtils
                    .getGroupNamesToOrgsByOrgAndGroupName(persistenceSession, org, groupName);

            if (groupNamesToOrgs == null) {
                throw new Exception("Org with guid = " + guidOrg + " is havent group " + groupName);
            }

            Date date = new Date();
            Date startDay = CalendarUtils.startOfDay(date);
            Long currentTime = date.getTime() - startDay.getTime();
            if (currentTime >= THRESHOLD_VALUE)
            {
                date = CalendarUtils.addOneDay(date);
            }
            OrgSettingItem orgSettingItem = DAOUtils.getOrgSettingItemByOrgAndType(persistenceSession, org.getIdOfOrg(), SETTING_TYPE);
            if (orgSettingItem != null)
            {
                try {
                    int notEditDays = Integer.parseInt(orgSettingItem.getSettingValue());
                    date = CalendarUtils.addDays(date, notEditDays);
                }
                catch (Exception e)
                {
                    logger.error("Cannot parse value " + orgSettingItem.getSettingValue() +
                            " to Integer for OrgSettingItem = " + orgSettingItem.getIdOfOrgSettingItem());
                }
            }

            //Производственный календарь
            date = getProdaction(date);

            //Учебный календарь
            date = getSpecialDate(persistenceSession, date, groupName);

            for (int i=0;i<13;i++) {
                DiscountComplexItem discountComplexItem = new DiscountComplexItem();
                discountComplexItem.setDate(date.toString());
                boolean stateForDate = getStateforDate(date);
                discountComplexItem.setState(String.valueOf(stateForDate));
                if (!stateForDate)
                {
                    List<ComplexInfo> complexInfos = DAOUtils.getComplexInfoForDate(persistenceSession, org, date);
                    if (complexInfos != null)
                    {
                        for (ComplexInfo complexInfo: complexInfos) {
                            ComplexesItem complexesItem = new ComplexesItem();
                            Integer idComplex = complexInfo.getIdOfComplex();
                            if (idComplex != null)
                                complexesItem.setIdofcomplex(idComplex.toString());
                            complexesItem.setComplexname(complexInfo.getComplexName());
                            discountComplexItem.getComplexeslist().add(complexesItem);
                        }
                    }
                }
                responseDiscountComplex.getDays().add(discountComplexItem);
                //К следующему дню
                date = CalendarUtils.addOneDay(date);
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
