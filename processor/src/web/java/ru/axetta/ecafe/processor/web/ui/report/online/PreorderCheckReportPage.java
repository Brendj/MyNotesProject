/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.PreorderCheck;
import ru.axetta.ecafe.processor.core.report.PreorderCheckReportItem;
import ru.axetta.ecafe.processor.core.service.GoodRequestsChangeAsyncNotificationService;
import ru.axetta.ecafe.processor.core.service.PreorderRequestsReportService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Scope(value = "session")
public class PreorderCheckReportPage extends OnlineReportPage {
    private final static Logger logger = LoggerFactory.getLogger(PreorderCheckReportPage.class);
    private List<PreorderCheckReportItem> items = new ArrayList<>();

    public PreorderCheckReportPage() {
        super();
        Date currentDate = new Date();
        int day = CalendarUtils.getDayOfMonth(currentDate);
        startDate = CalendarUtils.getFirstDayOfMonth(currentDate);
        if (day <= PreorderRequestsReportService.DAY_PREORDER_CHECK) {
            startDate = CalendarUtils.getFirstDayOfPrevMonth(currentDate);
        }
        startDate = CalendarUtils.startOfDay(startDate);

        List<Date> weekends = GoodRequestsChangeAsyncNotificationService.getInstance().getProductionCalendarDates(currentDate);
        Integer maxDays = RuntimeContext.getAppContext().getBean(PreorderRequestsReportService.class)
                .getMaxDateToCreateRequests(currentDate, weekends, PreorderRequestsReportService.MAX_FORBIDDEN_DAYS);
        endDate = CalendarUtils.addDays(currentDate, maxDays);
        endDate = CalendarUtils.endOfDay(endDate);
    }

    public void reload() {
        items.clear();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("select pc from PreorderCheck pc where pc.date between :startDate and :endDate order by pc.date, pc.createdDate");
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            List<PreorderCheck> list = query.list();
            for (PreorderCheck preorderCheck : list) {
                PreorderCheckReportItem item = new PreorderCheckReportItem(preorderCheck);
                items.add(item);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in build preorder check report: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public String getPageFilename() {
        return "report/online/preorder_check";
    }

    public List<PreorderCheckReportItem> getItems() {
        return items;
    }

    public void setItems(List<PreorderCheckReportItem> items) {
        this.items = items;
    }
}
