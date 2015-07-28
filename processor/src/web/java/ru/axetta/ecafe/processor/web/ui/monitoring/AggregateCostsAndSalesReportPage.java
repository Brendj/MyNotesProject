/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AggregateCostsAndSalesReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

@Component
@Scope("session")
public class AggregateCostsAndSalesReportPage extends OnlineReportPage {

    private static final Logger logger = LoggerFactory.getLogger(AggregateCostsAndSalesReportPage.class);

    private AggregateCostsAndSalesReport aggregateCostsAndSalesReport;

    public String getPageFilename() {
        return "monitoring/aggregate_costs_and_sales_report";
    }

    public AggregateCostsAndSalesReport getAggregateCostsAndSalesReport() {
        return aggregateCostsAndSalesReport;
    }

    public Object buildReport() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            this.aggregateCostsAndSalesReport = new AggregateCostsAndSalesReport();
            AggregateCostsAndSalesReport.Builder reportBuilder = new AggregateCostsAndSalesReport.Builder();
            if (!idOfOrgList.isEmpty()) {
                this.aggregateCostsAndSalesReport = reportBuilder
                        .build(persistenceSession, startDate, endDate, idOfOrgList);

                persistenceTransaction.commit();
                persistenceTransaction = null;
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
            } else {
                facesContext.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Укажите список организаций", null));
            }
        } catch (Exception e) {
            logger.error("Failed to build costs and sales report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }


}