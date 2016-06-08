/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.security;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalReport;
import ru.axetta.ecafe.processor.core.report.BasicReportForAllOrgJob;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.10.15
 * Time: 18:05
 * To change this template use File | Settings | File Templates.
 */
public class JournalReportsReport extends BasicReportForAllOrgJob {
    /*
   * Параметры отчета для добавления в правила и шаблоны
   *
   * При создании любого отчета необходимо добавить параметры:
   * REPORT_NAME - название отчета на русском
   * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
   * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
   * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
   * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
   *
   * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
   */
    public static final String REPORT_NAME = "Журнал сохранения файлов отчетов";
    public static final String[] TEMPLATE_FILE_NAMES = {"JournalReportsReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final static Logger logger = LoggerFactory.getLogger(JournalReportsReport.class);
    private String htmlReport;

    public JournalReportsReport() {}

    public JournalReportsReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + JournalReportsReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();

            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            HashMap<Long, List<String>> mapGuardians = new HashMap<Long, List<String>>();
            List<Client> clients = new ArrayList<Client>();

            JRDataSource dataSource = createDataSource(session, startTime, endTime, clients, mapGuardians);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new JournalReportsReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, List<Client> clients, HashMap<Long, List<String>> mapGuardians) throws Exception {
            List<SJReport> list = new ArrayList<SJReport>();
            Criteria criteria = session.createCriteria(SecurityJournalReport.class);
            criteria.add(Restrictions.ge("eventDate", startTime));
            criteria.add(Restrictions.le("eventDate", endTime));
            criteria.addOrder(Order.asc("eventDate"));
            List<SecurityJournalReport> query = criteria.list();
            for (SecurityJournalReport report : query) {
                SJReport item = new SJReport();
                item.setIdOfJournalReport(report.getIdOfJournalReport());
                item.setEventType(report.getEventType());
                item.setEventDate(report.getEventDate());
                item.setIsSuccess(report.getSuccess() ? "Да" : "Нет");
                if (report.getUser() != null) {
                    item.setUserName(report.getUser().getUserName());
                }
                list.add(item);
            }

            return new JRBeanCollectionDataSource(list);

        }

    }

    public static class SJReport {
        private Long idOfJournalReport;
        private String eventType;
        private Date eventDate;
        private String userName;
        private String isSuccess;

        public Long getIdOfJournalReport() {
            return idOfJournalReport;
        }

        public void setIdOfJournalReport(Long idOfJournalReport) {
            this.idOfJournalReport = idOfJournalReport;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public Date getEventDate() {
            return eventDate;
        }

        public void setEventDate(Date eventDate) {
            this.eventDate = eventDate;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getIsSuccess() {
            return isSuccess;
        }

        public void setIsSuccess(String success) {
            isSuccess = success;
        }
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new JournalReportsReport();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new Builder(); // Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_MONTH;
    }
}
