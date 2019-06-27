/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Shamil
 * Date: 21.05.15
 * Time: 18:41
 */
@Component
@Scope("session")
public class ServiceRNIPPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRNIPPage.class);

    //private Date startDate;// = new Date();
    //private Date endDate;// = new Date();

    private String startDate;
    private String endDate;

    public ServiceRNIPPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Calendar localCalendar = RuntimeContext.getInstance()
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        Date sDate = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();
        this.setStartDate(CalendarUtils.dateTimeToString(sDate));

        localCalendar.setTime(sDate);
        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.setEndDate(CalendarUtils.dateTimeToString(localCalendar.getTime()));
    }

    public void run(){
        //endDate = CalendarUtils.endOfDay(endDate);
        Date sDate = null, eDate = null;
        try {
            sDate = CalendarUtils.parseFullDateTimeWithLocalTimeZone(getStartDate());
            eDate = CalendarUtils.parseFullDateTimeWithLocalTimeZone(getEndDate());
        } catch (Exception e) {
            printError(String.format("При запуске операции произошла ошибка: %s", e.getMessage()));
            logger.error("Error run manual RNIPLoadPayment", e);
        }
        logger.error("Manual launch RNIPService startDate:" + getStartDate() + ", endDate:" + getEndDate());

        RNIPLoadPaymentsService rnipLoadPaymentsService = RNIPLoadPaymentsService.getRNIPServiceBean();
        rnipLoadPaymentsService.run(sDate,eDate);
    }

    public String getPageFilename() {
        return "service/rnip_service";
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /*public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }*/
}
