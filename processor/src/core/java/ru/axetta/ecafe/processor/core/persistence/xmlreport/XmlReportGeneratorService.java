package ru.axetta.ecafe.processor.core.persistence.xmlreport;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 16.12.14
 * Time: 17:44
 */
@Deprecated
@Service
public class XmlReportGeneratorService {

    private final static Logger logger = LoggerFactory.getLogger(XmlReportGeneratorService.class);

    public void generateXmlReport() throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Long duration = System.currentTimeMillis();
        Date currentDate = new Date();
        Date endDate = CalendarUtils.truncateToDayOfMonth(currentDate);
        Date startDate = CalendarUtils.addDays(endDate, -1);
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            List<Contragent> some = DailyFormationOfRegistriesService.getContragentTSP(persistenceSession);

            List<DailyFormationOfRegistriesService.DailyFormationOfRegistriesModel> dailyFormationOfRegistriesModelList = DailyFormationOfRegistriesService
                    .getGenerationReportResult(persistenceSession, some, startDate);

            XmlReportGenerator xmlReportGenerator = new XmlReportGenerator();
            Document document = xmlReportGenerator.createXmlFile(dailyFormationOfRegistriesModelList);
            xmlReportGenerator.unloadXmlFile(document, CalendarUtils.dateToString(startDate));

            DailyFormationOfRegistriesDBExport dailyFormationOfRegistriesDBExport = new DailyFormationOfRegistriesDBExport();
            dailyFormationOfRegistriesDBExport
                    .reportToDatabaseExport(dailyFormationOfRegistriesModelList, persistenceSession);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

}
