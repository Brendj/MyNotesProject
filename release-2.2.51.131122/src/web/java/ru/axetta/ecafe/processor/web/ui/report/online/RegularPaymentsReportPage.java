/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.regularPaymentsReport.RPRDataLoader;
import ru.axetta.ecafe.processor.core.report.regularPaymentsReport.ReportItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 11.11.13
 * Time: 15:05
 * Онлайн-отчет "Отчет по регулярным платежам"
 */

@Component
@Scope(value = "session")
public class RegularPaymentsReportPage extends OnlineReportPage {

    private List<ReportItem> items;

    public Object showReport() {
        Session session = RuntimeContext.getInstance().createReportPersistenceSession();
        RPRDataLoader dl = new RPRDataLoader(session);
        items = dl.getReportData(getStartDate(), getEndDate());
        HibernateUtils.close(session, null);
        return null;
    }

    public List<ReportItem> getItems() {
        return items;
    }

    public void setItems(List<ReportItem> items) {
        this.items = items;
    }

    @Override
    public String getPageFilename() {
        return "report/online/regular_payments_report";
    }
}
