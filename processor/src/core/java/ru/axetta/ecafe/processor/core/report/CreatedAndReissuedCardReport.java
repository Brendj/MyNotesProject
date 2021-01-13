/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.HistoryCard;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
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
    public static DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    protected List<CreatedAndReissuedCardReportItem> items;

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    private CreatedAndReissuedCardReport(){
        this.items = new LinkedList<CreatedAndReissuedCardReportItem>();
    }

    private CreatedAndReissuedCardReport(Date startDate, Date endDate, List<CreatedAndReissuedCardReportItem> items, JasperPrint jasperPrint){
        this.startTime = startDate;
        this.endTime = endDate;
        this.items = items;
        this.setPrint(jasperPrint);
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {
        private final String templateFilename;
        private List<User> userList = new ArrayList<User>();

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
            if(!userList.isEmpty()){
                Criteria criteriaHistoryCard = session.createCriteria(HistoryCard.class);
                criteriaHistoryCard
                        .add(Restrictions.in("user", userList))
                        .add(Restrictions.like("informationAboutCard", "Регистрация%", MatchMode.ANYWHERE))
                        .add(Restrictions.between("upDatetime", startTime, endTime));
                criteriaHistoryCard.addOrder(Order.asc("upDatetime"));
                listOfHistoryCard = criteriaHistoryCard.list();
            }else{
                Criteria criteriaHistoryCard = session.createCriteria(HistoryCard.class);
                criteriaHistoryCard
                        .add(Restrictions.between("upDatetime", startTime, endTime))
                        .add(Restrictions.like("informationAboutCard", "Регистрация%", MatchMode.ANYWHERE))
                        .add(Restrictions.isNotNull("user"));
                criteriaHistoryCard.addOrder(Order.asc("upDatetime"));
                listOfHistoryCard = criteriaHistoryCard.list();
            }
            long number = 1;
            for(HistoryCard el : listOfHistoryCard){
                if(el.getCard().getClient() == null){
                    logger.error(String.format("Card ID %s cardNo %s does not have owner", el.getCard().getIdOfCard(), el.getCard().getCardNo()));
                    continue;
                }
                Long printNo = el.getCard().getCardPrintedNo();
                String lockReason  = stringNotNull(getLockReasonPenultimateCard(session, el.getCard())); // Причина перевыпуска есть причиа блокировки старой карты
                Date createDate = el.getCard().getCreateTime();
                String shortName, department;
                Long cost = el.getTransaction() == null? 0: longNotNull(el.getTransaction().getTransactionSum());
                if(el.getUser().getPerson() != null){
                    shortName = stringNotNull(el.getUser().getPerson().getSurnameAndFirstLetters());
                } else {
                    shortName =  "";
                }
                department = stringNotNull(el.getUser().getDepartment());
                CreatedAndReissuedCardReportItem item = new CreatedAndReissuedCardReportItem(shortName, cost,
                        lockReason, department, printNo, createDate, number);
                items.add(item);
                number++;
            }

            if (items.isEmpty())
                throw new Exception("Недостаточно данных для построения отчета");

            JRDataSource dataSource = createDataSource(items);
            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, dataSource);

            return new CreatedAndReissuedCardReport(startTime, endTime, items, jasperPrint);

        }

        private String getLockReasonPenultimateCard(Session session, Card card) {
            List<Card> allClientCard = DAOUtils.getAllCardByClient(session, card.getClient());
            if(allClientCard.isEmpty()){
                logger.error(String.format("Client ID %s have card, but Hibernate return empty CardList", card.getClient().getIdOfClient()));
                return "";
            }
            int index = allClientCard.indexOf(card);
            if(index == allClientCard.size() -1){
                return "Новая карта";
            }
            return allClientCard.get(index+1).getLockReason();
        }

        private JRDataSource createDataSource(List<CreatedAndReissuedCardReportItem> items){
            return new JRBeanCollectionDataSource(items);
        }

        public List<User> getUserList() {
            return userList;
        }

        public void setUserList(List<User> userList) {
            this.userList = userList;
        }

        public void addUser(User user) {
            this.userList.clear();
            this.userList.add(user);
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
        return new CreatedAndReissuedCardReport.Builder(templateFilename);
    }

    public static final class CreatedAndReissuedCardReportItem {
        private Long number;
        private String employeeName;
        private Long cost;
        private String reason;
        private String department;
        private Long cardNo;
        private Date issueDate;

        CreatedAndReissuedCardReportItem(String shortName, Long cost,
                String reason, String department, Long cardNo, Date issueDate, Long number){
            this.employeeName = shortName;
            this.cost = cost /100;
            this.reason = getCheckedReason(reason);
            this.department = department;
            this.cardNo = cardNo;
            this.issueDate = issueDate;
            this.number = number;
        }

        private String getCheckedReason(String reason) {
            return reason.isEmpty()? "Причина перевыпуска не указана" : reason;
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
