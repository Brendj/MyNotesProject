/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.questionary.ClientAnswerByQuestionary;

import org.hibernate.Criteria;
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
public class ClientSelectedAnswerResultByOrgReport extends BasicReportForOrgJob {
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
    public static final String REPORT_NAME = "Отчет по анкетированию";
    public static final String[] TEMPLATE_FILE_NAMES = {"ClientSelectedAnswerResultByOrgReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{3};


    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public static class ClientSelectedAnswerReportItem {

               /* количество ответивших на ответ */
               //private Integer count;
               /* Наименование анкетировния */
               private String questionary;
               /* Вариант ответа анкеты */
               private String answer;
               /* номер контракта */
               private Long contractId;
               /* Фио клиента */
               private String clientFullName;
               /* Дата ответа */
               private Date answerDate;

               public ClientSelectedAnswerReportItem() {

               }

            public ClientSelectedAnswerReportItem(ClientAnswerByQuestionary clientAnswerByQuestionary) {
                this.answer = clientAnswerByQuestionary.getAnswer().getAnswer();
                //this.count = questionaryResultByOrg.getCount();
                this.questionary = clientAnswerByQuestionary.getAnswer().getQuestionary().getQuestion();
                Client client =   clientAnswerByQuestionary.getClient();
                this.contractId = client.getContractId();
                this.answerDate = clientAnswerByQuestionary.getCreatedDate();
                Person person = client.getPerson();
                this.clientFullName = person.getFullName();
            }

            public String getAnswer() {
                   return answer;
               }

               public void setAnswer(String answer) {
                   this.answer = answer;
               }

               //public Integer getCount() {
               //    return count;
               //}
               //
               //public void setCount(Integer count) {
               //    this.count = count;
               //}


            public Date getAnswerDate() {
                return answerDate;
            }

            public void setAnswerDate(Date answerDate) {
                this.answerDate = answerDate;
            }

            public String getClientFullName() {
                return clientFullName;
            }

            public void setClientFullName(String clientFullName) {
                this.clientFullName = clientFullName;
            }

            public Long getContractId() {
                return contractId;
            }

            public void setContractId(Long contractId) {
                this.contractId = contractId;
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
            return new ClientSelectedAnswerResultByOrgReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        /* TODO: переделать запрос используфя QuestionaryService как в отчете QuestionaryResultByOrgReport*/
        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            List<ClientSelectedAnswerReportItem> resultRows = new ArrayList<ClientSelectedAnswerReportItem>();
            Criteria clientOrgCriteria = session.createCriteria(Client.class);
            clientOrgCriteria.add(Restrictions.eq("org",org));
            List<Client> clients = clientOrgCriteria.list();
            Criteria criteriaClientAnswerByQuestionary = session.createCriteria(ClientAnswerByQuestionary.class);
            criteriaClientAnswerByQuestionary.add(Restrictions.in("client",clients));
            List<ClientAnswerByQuestionary> questionaryResultByOrgList = criteriaClientAnswerByQuestionary.list();
            for (ClientAnswerByQuestionary clientAnswerByQuestionary: questionaryResultByOrgList){
                  resultRows.add(new ClientSelectedAnswerReportItem(clientAnswerByQuestionary));
            }
            return new JRBeanCollectionDataSource(resultRows);
        }

    }


    public ClientSelectedAnswerResultByOrgReport(Date generateTime, long generateDuration, JasperPrint print,
            Date startTime, Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);
    }
    private static final Logger logger = LoggerFactory.getLogger(ClientSelectedAnswerResultByOrgReport.class);

    public ClientSelectedAnswerResultByOrgReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new ClientSelectedAnswerResultByOrgReport();
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


