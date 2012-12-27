/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.questionary.Answer;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryResultByOrg;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormatSymbols;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.03.12
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public class QuestionaryResultByOrgReport extends BasicReportForOrgJob {

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public static class QuestionaryReportItem {

               /* количество ответивших на ответ */
               private Integer count;
               /* Наименование анкетировния */
               private String questionary;
               /* Вариант ответа анкеты */
               private String answer;

               public QuestionaryReportItem() {

               }

            public QuestionaryReportItem(QuestionaryResultByOrg questionaryResultByOrg) {
                this.answer = questionaryResultByOrg.getAnswer().getAnswer();
                this.count = questionaryResultByOrg.getCount();
                this.questionary = questionaryResultByOrg.getQuestionary().getQuestion();
            }

            public String getAnswer() {
                   return answer;
               }

               public void setAnswer(String answer) {
                   this.answer = answer;
               }

               public Integer getCount() {
                   return count;
               }

               public void setCount(Integer count) {
                   this.count = count;
               }

               public String getQuestionary() {
                   return questionary;
               }

               public void setQuestionary(String questionary) {
                   this.questionary = questionary;
               }
           }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            int month = calendar.get(Calendar.MONTH);
            parameterMap.put("orgName", org.getOfficialName());
            parameterMap.put("day", calendar.get(Calendar.DAY_OF_MONTH));
            parameterMap.put("month", month + 1);
            parameterMap.put("monthName", new DateFormatSymbols().getMonths()[month]);
            parameterMap.put("year", calendar.get(Calendar.YEAR));
            parameterMap.put("startDate", startTime);
            parameterMap.put("endDate", endTime);

            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new QuestionaryResultByOrgReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<QuestionaryReportItem> resultRows = new LinkedList<QuestionaryReportItem>();
            Criteria criteriaQuestionaryResultByOrg = session.createCriteria(QuestionaryResultByOrg.class);
            criteriaQuestionaryResultByOrg.add(Restrictions.eq("org",org));
            List<QuestionaryResultByOrg> questionaryResultByOrgList = criteriaQuestionaryResultByOrg.list();
            for (QuestionaryResultByOrg questionaryResultByOrg: questionaryResultByOrgList){
                  resultRows.add(new QuestionaryReportItem(questionaryResultByOrg));
            }
            return new JRBeanCollectionDataSource(resultRows);
        }

    }


    public QuestionaryResultByOrgReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }
    private static final Logger logger = LoggerFactory.getLogger(QuestionaryResultByOrgReport.class);

    public QuestionaryResultByOrgReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new QuestionaryResultByOrgReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_TODAY;
    }
}


