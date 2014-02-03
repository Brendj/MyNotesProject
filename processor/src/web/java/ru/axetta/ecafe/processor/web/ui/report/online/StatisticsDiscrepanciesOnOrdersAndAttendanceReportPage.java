/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders.DiscrepanciesOnOrdersAndAttendanceBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders.DiscrepanciesOnOrdersAndAttendanceReport;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyBuilder;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 20.12.11
 * Time: 12:56
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class StatisticsDiscrepanciesOnOrdersAndAttendanceReportPage extends OnlineReportPage implements ContragentSelectPage.CompleteHandler {

    private DiscrepanciesOnOrdersAndAttendanceReport report;
    private final CCAccountFilter contragentFilter = new CCAccountFilter();

    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }

    public Object showContractSelectPage () {
        MainPage.getSessionInstance().showContractSelectPage (this.contragentFilter.getContragent().getContragentName(),
                this.contragentFilter.getContragent().getIdOfContragent());
        return null;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        contragentFilter.completeContragentSelection(session, idOfContragent);
    }

    public String getPageFilename() {
        return "report/online/statistics_discrepancies_on_orders_and_attendance_report";
    }

    public DiscrepanciesOnOrdersAndAttendanceReport getReport() {
        return report;
    }

    public Object buildReport(){
        //CCAccountFilter.ContragentItem contragentItem = contragentFilter.getContragent();
        //if(contragentItem == null) {
        //    printError("Не указан контрагент");
        //    return null;
        //}
        if(idOfOrgList==null || idOfOrgList.isEmpty()){
            printError("Выберите хотя бы одну организацию");
            return null;
        }
        RuntimeContext runtimeContext = null;
        Session session = null;
        Transaction transaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createReportPersistenceSession();
            transaction = session.beginTransaction();
            DiscrepanciesOnOrdersAndAttendanceBuilder builder = new DiscrepanciesOnOrdersAndAttendanceBuilder();
            //Contragent contragent = (Contragent) session.load(Contragent.class, contragentItem.getIdOfContragent());
            this.report = builder.build(session,/* contragent,*/ idOfOrgList, localCalendar, startDate, endDate);
            transaction.commit();
            transaction = null;
            printMessage("Отчет успешно сгенерирован");
        } catch (Exception e){
            getLogger().error("Filed build DiscrepanciesOnOrdersAndAttendanceReport: ", e);
            printError("Ошибка при построении отчета: "+e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
        return null;
    }


}
