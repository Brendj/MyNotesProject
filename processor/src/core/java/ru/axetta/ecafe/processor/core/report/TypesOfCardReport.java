/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.TypesOfCardReportItem;
import ru.axetta.ecafe.processor.core.card.TypesOfCardSubreportItem;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.TypesOfCardService;
import ru.axetta.ecafe.processor.core.persistence.xmlreport.DailyFormationOfRegistries;
import ru.axetta.ecafe.processor.core.persistence.xmlreport.XmlReportGenerator;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 03.11.14
 * Time: 14:08
 */
public class TypesOfCardReport extends BasicReportForAllOrgJob {

    public static final String PARAM_WITH_OUT_SUMMARY_BY_DISTRICTS = "includeSummaryByDistrict";

    private final static Logger logger = LoggerFactory.getLogger(TypesOfCardReport.class);

    public TypesOfCardReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, null);
    }

    public TypesOfCardReport() {
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new TypesOfCardReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        String subReportDir =  RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        return new Builder(templateFilename, subReportDir);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private final String subReportDir;

        public Builder(String templateFilename, String subReportDir) {
            this.templateFilename = templateFilename;
            this.subReportDir = subReportDir;
        }

        public BasicReportJob build(Session session, Date startTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startTime);
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
            parameterMap.put("SUBREPORT_DIR", subReportDir);

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, startTime, (Calendar) calendar.clone(), parameterMap));
            long generateDuration = generateTime.getTime();
            return new TypesOfCardReport(generateTime, generateDuration, jasperPrint, startTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<TypesOfCardReportItem> result = new ArrayList<TypesOfCardReportItem>();

            int ac = Card.ACTIVE_STATE; // активная карта
            int lc = Card.LOCKED_STATE; // заблокированная карта

            TypesOfCardService service = new TypesOfCardService();
            service.setSession(session);

            String withOutSummaryByDistrictParam = (String) getReportProperties().get(PARAM_WITH_OUT_SUMMARY_BY_DISTRICTS);
            boolean withOutSummaryByDistrict = false;
            if (withOutSummaryByDistrictParam!=null) {
                withOutSummaryByDistrict = withOutSummaryByDistrictParam.trim().equalsIgnoreCase("true");
            }

            DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy");

            TypesOfCardSubreportItem typesOfCardSubreportItem1 = new TypesOfCardSubreportItem("ГОУ ЦО № 2010",
                    "109382, г. Москва, ул. Белореченская, д. 8 -- 109382, г. Москва, ул. Верхние поля, д.15, к.3",
                   0L,0L,0L,0L,0L,0L,0L,0L);
            TypesOfCardSubreportItem typesOfCardSubreportItem2 = new TypesOfCardSubreportItem("ГОУ СОШ № 1716",
                    "109451, г. Москва, ул. Верхние поля, д. 40, к.2", 0L,0L,0L,0L,0L,0L,0L,0L);

            List<TypesOfCardSubreportItem> typesOfCardSubreportItems = new ArrayList<TypesOfCardSubreportItem>();
            typesOfCardSubreportItems.add(typesOfCardSubreportItem1);
            typesOfCardSubreportItems.add(typesOfCardSubreportItem2);

            TypesOfCardReportItem typesOfCardReportItem = new TypesOfCardReportItem("САО",0L,0L,0L,0L,0L,0L,0L,0L);
            TypesOfCardReportItem typesOfCardReportItem1 = new TypesOfCardReportItem("ЮВАО",10L,20L,30L,40L,50L,60L,70L,80L);

            typesOfCardReportItem.setTypesOfCardSubeportItems(typesOfCardSubreportItems);
            typesOfCardReportItem1.setTypesOfCardSubeportItems(typesOfCardSubreportItems);

            result.add(typesOfCardReportItem);
            result.add(typesOfCardReportItem1);

            List<Contragent> some = DailyFormationOfRegistries.getContragentTSP(session);

            List<DailyFormationOfRegistries.DailyFormationOfRegistriesModel> dailyFormationOfRegistriesModelList =
                    DailyFormationOfRegistries.getGenerationReportResult(session, some, startTime);

            XmlReportGenerator xmlReportGenerator = new XmlReportGenerator();
            Document document = xmlReportGenerator.createXmlFile(dailyFormationOfRegistriesModelList);
            xmlReportGenerator.unloadXmlFile(document, CalendarUtils.dateToString(startTime));

            return new JRBeanCollectionDataSource(result);
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            return build(session, startTime, calendar);
        }
    }

}
