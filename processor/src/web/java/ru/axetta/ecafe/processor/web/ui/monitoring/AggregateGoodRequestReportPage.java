package ru.axetta.ecafe.processor.web.ui.monitoring;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.AggregateGoodRequestReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope("session")
public class AggregateGoodRequestReportPage extends OnlineReportPage {

    private static final Logger logger = LoggerFactory.getLogger(AggregateGoodRequestReportPage.class);

    private AggregateGoodRequestReport aggregateGoodRequestReport;

    public String getPageFilename() {
        return "monitoring/aggregate_good_request_report";
    }

    public AggregateGoodRequestReport getAggregateGoodRequestReport() {
        return aggregateGoodRequestReport;
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

            this.aggregateGoodRequestReport = new AggregateGoodRequestReport();
            AggregateGoodRequestReport.Builder reportBuilder = new AggregateGoodRequestReport.Builder();
            this.aggregateGoodRequestReport = reportBuilder.build(persistenceSession, startDate, endDate, idOfOrgList);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build aggregate good request report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета", null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

}
