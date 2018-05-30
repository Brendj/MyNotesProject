/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.HistoryCard;
import ru.axetta.ecafe.processor.core.persistence.User;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CreatedAndReissuedCardReport extends BasicReportForAllOrgJob {

    public static final String REPORT_NAME = "Отчет о выданных и перевыпущенных картах";
    public static final String TEMPLATE_FILE_NAME = "IssuedCardsReport.jasper";
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{};


    private final static Logger logger = LoggerFactory.getLogger(CreatedAndReissuedCardReport.class);
    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat dailyItemsFormat = new SimpleDateFormat("dd.MM.yyyy");

    private Date startDate;
    private Date endDate;
    protected List<CreatedAndReissuedCardReportItem> items;


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    private CreatedAndReissuedCardReport(){
        this.items = new LinkedList<CreatedAndReissuedCardReportItem>();
    }

    private CreatedAndReissuedCardReport(Date startDate, Date endDate, List<CreatedAndReissuedCardReportItem> items, JasperPrint jasperPrint){
        this.startDate = startDate;
        this.endDate = endDate;
        this.items = items;
        this.setPrint(jasperPrint);
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {
        private final String templateFilename;
        private User user = null;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public Builder() {
            String reportsTemplateFilePath = RuntimeContext.getInstance().getAutoReportGenerator()
                    .getReportsTemplateFilePath();
            templateFilename = reportsTemplateFilePath + TEMPLATE_FILE_NAME;
        }

        private String stringNotNull(String str){
            return str == null? "" : str;
        }

        private Long longNotNull(Long var){
            return var == null? 0: var;
        }

        @Override
        public CreatedAndReissuedCardReport build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            if(startTime==null){
                throw new Exception("Не указано дата выборки от");
            }
            if(endTime==null) {
                throw new Exception("Не указано дата выборки до");
            }
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("startDate", format.format(startTime));
            parameterMap.put("endDate", format.format(endTime));
            List<HistoryCard> listOfHistoryCard = Collections.emptyList();
            List<CreatedAndReissuedCardReportItem> items = new LinkedList<CreatedAndReissuedCardReportItem>();
            if(user != null){
                Criteria criteriaHistoryCard = session.createCriteria(HistoryCard.class);
                criteriaHistoryCard
                        .add(Restrictions.eq("user", user))
                        .add(Restrictions.between("upDatetime", startTime, endTime));
                listOfHistoryCard = criteriaHistoryCard.list();
            }else{
                Criteria criteriaHistoryCard = session.createCriteria(HistoryCard.class);
                criteriaHistoryCard
                        .add(Restrictions.between("upDatetime", startTime, endTime))
                        .add(Restrictions.isNotNull("user"));
                listOfHistoryCard = criteriaHistoryCard.list();
            }
            long number = 1;
            for(HistoryCard el : listOfHistoryCard){
                Long printNo = el.getCard().getCardPrintedNo();
                String loscReason  = el.getCard().getLockReason();
                Date createDate = el.getCard().getCreateTime();
                String firstname, surname, secondname, department;
                Long cost = el.getTransaction() == null? 0: longNotNull(el.getTransaction().getTransactionSum());
                if(el.getUser().getPerson() != null){
                    firstname = stringNotNull(el.getUser().getPerson().getFirstName());
                    surname = stringNotNull(el.getUser().getPerson().getSurname());
                    secondname = stringNotNull(el.getUser().getPerson().getSecondName());
                } else {
                    firstname = surname = secondname = "";
                }
                department = stringNotNull(el.getUser().getDepartment());
                CreatedAndReissuedCardReportItem item = new CreatedAndReissuedCardReportItem(firstname, surname, secondname, cost,
                        loscReason, department, printNo, createDate, number);
                items.add(item);
                number++;
            }

            JRDataSource dataSource = createDataSource(items);
            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, dataSource);

            return new CreatedAndReissuedCardReport(startTime, endTime, items, jasperPrint);

        }

        private JRDataSource createDataSource(List<CreatedAndReissuedCardReportItem> items){
            return new JRBeanCollectionDataSource(items);
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    @Override
    public Logger getLogger(){
        return logger;
    }

    @Override
    public BasicReportForAllOrgJob createInstance(){
        return new CreatedAndReissuedCardReport();
    }

    @Override
    public BasicReportForAllOrgJob.Builder createBuilder(String templateFilename) {
        return new TransactionsReport.Builder(templateFilename);
    }

    public static final class CreatedAndReissuedCardReportItem {
        private Long number;
        private String employeeName;
        private Long cost;
        private String reason;
        private String department;
        private Long cardNo;
        private Date issueDate;

        CreatedAndReissuedCardReportItem(String firstname, String surname, String secondname, Long cost,
                String reason, String department, Long cardNo, Date issueDate, Long number){
            this.employeeName = firstname + " " + surname + " " + secondname;
            this.cost = cost /100;
            this.reason = reason;
            this.department = department;
            this.cardNo = cardNo;
            this.issueDate = issueDate;
            this.number = number;
        }


        public Long getCost() {
            return cost;
        }

        public void setCost(Long cost) {
            this.cost = cost;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public Long getCardNo() {
            return cardNo;
        }

        public void setCardNo(Long cardNo) {
            this.cardNo = cardNo;
        }

        public Date getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(Date issueDate) {
            this.issueDate = issueDate;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public Long getNumber() {
            return number;
        }

        public void setNumber(Long number) {
            this.number = number;
        }
    }
}
