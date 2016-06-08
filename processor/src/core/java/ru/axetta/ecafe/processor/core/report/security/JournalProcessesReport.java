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
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalProcess;
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
public class JournalProcessesReport extends BasicReportForAllOrgJob {
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
    public static final String REPORT_NAME = "Журнал запуска фоновых процессов";
    public static final String[] TEMPLATE_FILE_NAMES = {"JournalProcessesReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final static Logger logger = LoggerFactory.getLogger(JournalProcessesReport.class);
    private String htmlReport;

    public JournalProcessesReport() {}

    public JournalProcessesReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
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
            templateFilename = reportsTemplateFilePath + JournalProcessesReport.class.getSimpleName() + ".jasper";
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

            return new JournalProcessesReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, List<Client> clients, HashMap<Long, List<String>> mapGuardians) throws Exception {
            List<SJProcess> list = new ArrayList<SJProcess>();
            Criteria criteria = session.createCriteria(SecurityJournalProcess.class);
            criteria.add(Restrictions.ge("eventDate", startTime));
            criteria.add(Restrictions.le("eventDate", endTime));
            criteria.addOrder(Order.asc("eventDate"));
            List<SecurityJournalProcess> query = criteria.list();
            for (SecurityJournalProcess process : query) {
                SJProcess item = new SJProcess();
                item.setIdOfJournalProcess(process.getIdOfJournalProcess());
                item.setEventType(SecurityJournalProcess.EventType.parse(process.getEventType()).toString());
                item.setEventDate(process.getEventDate());
                item.setEventClass(SecurityJournalProcess.EventClass.parse(process.getEventClass()).toString());
                item.setIsSuccess(process.getSuccess() ? "Да" : "Нет");
                if (process.getUser() != null) {
                    item.setUserName(process.getUser().getUserName());
                }
                item.setServerAddress(process.getServerAddress());
                list.add(item);
            }

            return new JRBeanCollectionDataSource(list);

        }

    }

    public static class SJProcess {
        private Long idOfJournalProcess;
        private String eventType;
        private String eventClass;
        private Date eventDate;
        private String userName;
        private String isSuccess;
        private String serverAddress;


        public Long getIdOfJournalProcess() {
            return idOfJournalProcess;
        }

        public void setIdOfJournalProcess(Long idOfJournalProcess) {
            this.idOfJournalProcess = idOfJournalProcess;
        }

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public String getEventClass() {
            return eventClass;
        }

        public void setEventClass(String eventClass) {
            this.eventClass = eventClass;
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

        public String getServerAddress() {
            return serverAddress;
        }

        public void setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
        }
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new JournalProcessesReport();  //To change body of implemented methods use File | Settings | File Templates.
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
