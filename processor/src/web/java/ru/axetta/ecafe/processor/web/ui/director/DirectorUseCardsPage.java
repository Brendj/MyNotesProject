/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.director;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by i.semenov on 19.09.2017.
 */
public class DirectorUseCardsPage extends OnlineReportPage {
    private Logger logger = LoggerFactory.getLogger(DirectorUseCardsPage.class);

    public DirectorUseCardsPage() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        localCalendar = RuntimeContext.getInstance()
                .getDefaultLocalCalendar((HttpSession) facesContext.getExternalContext().getSession(false));

        localCalendar.setTime(new Date());
        this.startDate = DateUtils.truncate(localCalendar, Calendar.DAY_OF_MONTH).getTime();

        localCalendar.setTime(this.startDate);
        localCalendar.add(Calendar.DAY_OF_MONTH, 1);
        localCalendar.add(Calendar.SECOND, -1);
        this.endDate = localCalendar.getTime();
    }

    public Object buildUseCardsReport() {
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession(); //открытие сессии к отчетной БД (запросы только на чтение)
            persistenceTransaction = persistenceSession.beginTransaction();
            String ids = "";
            for (Long id : idOfOrgList) {
                ids += id.toString() + ", ";
            }
            //buildReport(persistenceSession); - здесь должна быть работа с данными, построение отчета
            persistenceTransaction.commit();
            persistenceTransaction = null;
            printMessage(String.format("Подготовка отчета завершена успешно. %s", ids));
        } catch (Exception e) {
            logger.error("Failed to build director use cards report", e);
            printError("Ошибка при подготовке отчета: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public String getPageFilename() {
        return "use_cards";
    }


}
