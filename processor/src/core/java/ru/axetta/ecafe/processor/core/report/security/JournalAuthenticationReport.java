/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.security;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SecurityJournalAuthenticate;
import ru.axetta.ecafe.processor.core.persistence.User;
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
public class JournalAuthenticationReport extends BasicReportForAllOrgJob {/*
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
    public static final String REPORT_NAME = "Журнал событий входа-выхода";
    public static final String[] TEMPLATE_FILE_NAMES = {"JournalAuthenticationReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final static Logger logger = LoggerFactory.getLogger(JournalAuthenticationReport.class);
    private String htmlReport;

    public JournalAuthenticationReport() {}

    public JournalAuthenticationReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime) {
        super(generateTime, generateDuration, print, startTime, endTime);
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        Long userId;

        public Builder(String templateFilename, Long userId) {
            this.templateFilename = templateFilename;
            this.userId = userId;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + JournalAuthenticationReport.class.getSimpleName() + ".jasper";
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();

            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            HashMap<Long, List<String>> mapGuardians = new HashMap<Long, List<String>>();

            User user = null;
            if (userId != null) {
                user = (User)session.get(User.class, userId);
            }

            JRDataSource dataSource = createDataSource(session, startTime, endTime, user, mapGuardians);

            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

            Date generateEndTime = new Date();

            long generateDuration = generateEndTime.getTime() - generateTime.getTime();

            return new JournalAuthenticationReport(generateTime, generateDuration, jasperPrint, startTime, endTime);
        }

        private JRDataSource createDataSource(Session session, Date startTime, Date endTime, User user, HashMap<Long, List<String>> mapGuardians) throws Exception {
            List<SJAuthentication> list = new ArrayList<SJAuthentication>();
            Criteria criteria = session.createCriteria(SecurityJournalAuthenticate.class);
            criteria.add(Restrictions.ge("eventDate", startTime));
            criteria.add(Restrictions.le("eventDate", endTime));
            if (user != null) {
                criteria.add(Restrictions.eq("user", user));
            }
            criteria.addOrder(Order.asc("eventDate"));
            List<SecurityJournalAuthenticate> query = criteria.list();
            for (SecurityJournalAuthenticate balance : query) {
                SJAuthentication item = new SJAuthentication();
                item.setIdOfJournalAuthenticate(balance.getIdOfJournalAuthenticate());
                item.setEventType(SecurityJournalAuthenticate.EventType.parse(balance.getEventType()).toString());
                item.setEventDate(balance.getEventDate());
                item.setUserName(balance.getLogin());
                item.setIsSuccess(balance.getSuccess() ? "Да" : "Нет");
                item.setComment(balance.getComment());
                if (SecurityJournalAuthenticate.ArmType.parse(balance.getIdOfArmType()) != null) {
                    item.setArmType(SecurityJournalAuthenticate.ArmType.parse(balance.getIdOfArmType()).toString());
                }
                item.setIpAddress(balance.getIpAddress());
                if (balance.getDenyCause() != null) {
                    item.setDenyCause(SecurityJournalAuthenticate.DenyCause.parse(balance.getDenyCause()).toString());
                }
                list.add(item);
            }

            return new JRBeanCollectionDataSource(list);

        }

    }

    public static class SJAuthentication {
        private Long idOfJournalAuthenticate;
        private String eventType;
        private Date eventDate;
        private String userName;
        private String isSuccess;
        private String armType;
        private String ipAddress;
        private String denyCause;
        private String comment;

        public Long getIdOfJournalAuthenticate() {
            return idOfJournalAuthenticate;
        }

        public void setIdOfJournalAuthenticate(Long idOfJournalAuthenticate) {
            this.idOfJournalAuthenticate = idOfJournalAuthenticate;
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

        public String getArmType() {
            return armType;
        }

        public void setArmType(String armType) {
            this.armType = armType;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getDenyCause() {
            return denyCause;
        }

        public void setDenyCause(String denyCause) {
            this.denyCause = denyCause;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    @Override
    public BasicReportForAllOrgJob createInstance() {
        return new JournalAuthenticationReport();  //To change body of implemented methods use File | Settings | File Templates.
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
