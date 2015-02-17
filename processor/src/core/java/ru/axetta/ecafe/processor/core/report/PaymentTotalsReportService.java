/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 28.10.14
 * Time: 18:00
 * This class almost similar to GoodRequestsNewReportService.
 */
public class PaymentTotalsReportService {

    final private static Logger logger = LoggerFactory.getLogger(GoodRequestsNewReportService.class);

    final private Session session;
    final private static String STR_YEAR_DATE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    final private static DateFormat YEAR_DATE_FORMAT = new SimpleDateFormat(STR_YEAR_DATE_FORMAT, new Locale("ru"));
    private Long debugLevel = 0L;


    public PaymentTotalsReportService(Session session) {
        this.session = session;
    }

    public List<Item> buildReportItems(Long idOfContragent, List<Long> idOfOrgList, Date startTime, Date endTime, boolean hideNullRows) throws Exception {

        if (idOfOrgList.size() <= 0)
            idOfOrgList = getOrgs(idOfContragent);

        List<Long> reportOrgSet = DAOUtils.complementIdOfOrgSet(session, idOfOrgList);

        Date date = new Date();
        Long iterator = 0L;

        List<Item> reportItems = new ArrayList<Item>();
        for (Long idOfOrg : reportOrgSet) {

            date = printTime(date, " ms " + iterator++ + " (idOfOrg - " + idOfOrg + "). Main cycle", 2L);

            Org org = (Org) session.load(Org.class, idOfOrg);

            String orgNumString = org.getOrgNumberInName();
            Long orgNum = orgNumString.equals("") ? null : Long.parseLong(orgNumString);

            Long orgID = org.getIdOfOrg();

            String orgName = org.getShortName();

            String lastSyncTime = YEAR_DATE_FORMAT.format(getLastSyncTime(idOfOrg));

            Long startCash = getOrgClientsBalance(idOfOrg, startTime, ClientGroupMenu.CLIENT_STUDENTS);
            // getOrgClientsBalanceOnDateBeforeRubicon(idOfOrg, startTime, ClientGroupMenu.CLIENT_STUDENTS);

            Long income = getOrgClientsIncomeOnPeriod(idOfOrg, startTime, endTime, ClientGroupMenu.CLIENT_STUDENTS);

            Long paid = getPaidOrdersSum(idOfOrg, startTime, endTime);

            //paid = getPaidOrdersSumSQL(idOfOrg, startTime, endTime); // todo must be optimized

            Long paidSnack = getPaidSnackSum(idOfOrg, startTime, endTime);

            Long paidTotal = paid + paidSnack;

            Long repayment = getRepaymentSum(idOfOrg, startTime, endTime);

            Long cashMoved = getCashMovedSum(idOfOrg, startTime, endTime);

            Long endCashControl = startCash + income - paidTotal + repayment + cashMoved;

            Long endCash = getOrgClientsBalance(idOfOrg, endTime, ClientGroupMenu.CLIENT_STUDENTS);

            String comment = getStatusDetail(idOfOrg);

            Item item = new Item(orgNum, orgID, orgName, lastSyncTime, startCash, income, paid, paidSnack, paidTotal,
                    repayment, cashMoved, endCash, comment);
            if (notZeroCash(item) || !hideNullRows)
                reportItems.add(item);
        }

        date = printTime(date, " ms - Main cycle passed.", 2L);

        return reportItems;
    }

    private String getStatusDetail(Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        String statusDetailing = org.getStatusDetailing();
        if (!statusDetailing.equals("") && !statusDetailing.equals("/"))
            return statusDetailing;
        return "";
    }

    private Long getOrgClientsBalance(Long idOfOrg, Date toTime, Long clientGroup) {
        Long result = 0L;
        Date rubicon = new Date(113, 2, 14); // 2013-03-14 00:00:00
        result = getOrgClientsBalanceOnDateBeforeRubicon(idOfOrg, rubicon, clientGroup);
        if (toTime.after(rubicon))
            result += getOrgClientsBalanceChangeOnPeriodAfterRubicon(idOfOrg, rubicon, toTime, clientGroup);
        return result;
    }

    private Long getOrgClientsBalanceChangeOnPeriodAfterRubicon(Long idOfOrg, Date rubicon, Date toTime,
            Long clientGroup) {

        Date date = new Date();
        date = printTime(date, " ms - getOrgClientsBalanceChangeOnPeriodAfterRubicon, idOfOrg - " + idOfOrg, 4L);

        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.between("transactionTime", rubicon, toTime));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.setProjection(Projections.sum("transactionSum"));
        List list = criteria.list();

        Long cashSum = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) cashSum = (Long) list.get(0);

        date = printTime(date,  " ms - getOrgClientsBalanceChangeOnPeriodAfterRubicon, idOfOrg - " + idOfOrg, 4L);

        return cashSum;
    }

    private boolean notZeroCash(Item item) {
        //if (item.getStartCash() == 0L
        //    && item.getIncome() == 0L
        //    && item.getPaid() == 0L
        //    && item.getPaidSnack() == 0L
        //    && item.getPaidTotal() == 0L
        //    && item.getRepayment() == 0L
        //    && item.getCashMoved() == 0L
        //    && item.getEndCash() == 0L) {
        //    return false;
        //}
        if (item.getIncome() != 0L
            || item.getPaid() != 0L
            || item.getPaidSnack() != 0L
            || item.getPaidTotal() != 0L
            || item.getRepayment() != 0L
            || item.getCashMoved() != 0L) {
            return true;
        }
        return false;
    }

    private Date printTime(Date date, String message, Long debugLevel) {
        if (debugLevel == this.debugLevel) {
            Date newDate;
            newDate = new Date();
            Long diff = (newDate.getTime() - date.getTime());
            if (diff > 1000L)
                logger.warn((newDate.getTime()-date.getTime()) + message + " debug level - " + debugLevel);
            date = newDate;
        }
        return date;
    }

    private Long getCashMovedSum(Long idOfOrg, Date startTime, Date endTime) {

        Date date = new Date();
        date = printTime(date, " ms - getCashMovedSum, idOfClient - " + idOfOrg, 4L);

        Criteria criteria = session.createCriteria(ClientMigration.class, "c");
        criteria.add(Restrictions.between("c.registrationDate", startTime, endTime));
        criteria.add(Restrictions.or(Restrictions.eq("c.org.idOfOrg", idOfOrg),
                Restrictions.eq("c.oldOrg.idOfOrg", idOfOrg)));
        List<ClientMigration> list = criteria.list();
        Long cashMovedSum = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) {
            for (ClientMigration clientMigration : list) {
                if (clientMigration.getOrg().getIdOfOrg() == idOfOrg) {
                    cashMovedSum += getClientsBalanceOnDate(clientMigration.getClient(), startTime,
                            clientMigration.getRegistrationDate(), 1L);
                } else if (clientMigration.getOldOrg().getIdOfOrg() == idOfOrg) {
                    cashMovedSum -= getClientsBalanceOnDate(clientMigration.getClient(), startTime,
                            clientMigration.getRegistrationDate(), 1L);
                } else {
                    logger.info("Clients migrations processing error");
                }
            }
        }

        date = printTime(date, " ms - getCashMovedSum, idOfClient - " + idOfOrg, 4L);

        return cashMovedSum;
    }

    private Long getClientsBalanceOnDate(Client client, Date startTime, Date registrationDate, Long debugLevel) {

        Date date = new Date();
        date = printTime(date, " ms - getClientsBalanceOnDate, idOfClient - " + client.getIdOfClient(), 4L);

        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.gt("transactionTime", startTime));    // <
        criteria.add(Restrictions.lt("transactionTime", registrationDate));    // <
        criteria.add(Restrictions.eq("client", client));
        criteria.setProjection(Projections.projectionList().add(Projections.sum("transactionSum")));
        List list = criteria.list();

        Long balance = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) balance = (Long) list.get(0);

        date = printTime(date, " ms - getClientsBalanceOnDate, idOfClient - " + client.getIdOfClient(), 4L);

        return balance;
    }

    private Long getRepaymentSum(Long idOfOrg, Date startTime, Date endTime) {

        Date date = new Date();
        date = printTime(date, " ms - getRepaymentSum, idOfOrg - " + idOfOrg, 4L);

        Criteria criteria = session.createCriteria(AccountTransaction.class, "at");
        criteria.createAlias("org", "o");
        criteria.add(Restrictions.eq("at.sourceType", AccountTransaction.ACCOUNT_REFUND_TRANSACTION_SOURCE_TYPE));
        criteria.add(Restrictions.between("at.transactionTime", startTime, endTime));

        criteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));

        criteria.setProjection(Projections.projectionList()
                .add(Projections.sqlProjection("sum(this_.transactionsum) as sum", new String[] {"sum"},
                        new Type[] {LongType.INSTANCE}))
        );
        List list = criteria.list();

        Long repaymentSum = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) repaymentSum = (Long) list.get(0);

        date = printTime(date, " ms - getRepaymentSum, idOfOrg - " + idOfOrg, 4L);

        return repaymentSum;
    }

    private Long getPaidSnackSum(Long idOfOrg, Date startTime, Date endTime) {

        Date date = new Date();
        date = printTime(date, " ms - getPaidSnackSum, idOfOrg - " + idOfOrg, 4L);

        Criteria criteria = session.createCriteria(OrderDetail.class, "od");
        criteria.createAlias("order", "o");
        criteria.add(Restrictions.eq("od.state", 0));
        criteria.add(Restrictions.eq("o.orderType", OrderTypeEnumType.DEFAULT));
        criteria.add(Restrictions.eq("od.menuType", OrderDetail.TYPE_DISH_ITEM));
        criteria.add(Restrictions.eq("o.org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.between("o.createTime", startTime, endTime));
        // не нужно - удалить
        //Org org = (Org) session.load(Org.class, idOfOrg);
        //criteria.add(Restrictions.eq("o.contragent", org.getDefaultSupplier()));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.sqlProjection("sum(this_.rprice * this_.qty) as sum", new String[]{"sum"},
                        new Type[]{LongType.INSTANCE}))
        );
        List list = criteria.list();

        Long paidSnackSum = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) paidSnackSum = (Long) list.get(0);

        date = printTime(date, " ms - getPaidSnackSum, idOfOrg - " + idOfOrg, 4L);

        return paidSnackSum;
    }

    private Long getPaidOrdersSum(Long idOfOrg, Date startTime, Date endTime) {

        Date date = new Date();
        date = printTime(date, " ms - getPaidOrdersSum, idOfOrg - " + idOfOrg, 4L);

        Criteria criteria = session.createCriteria(OrderDetail.class, "od");
        criteria.createAlias("order", "o");
        criteria.add(Restrictions.eq("od.state", 0));
        criteria.add(Restrictions.in("o.orderType", new OrderTypeEnumType[]{
                OrderTypeEnumType.DEFAULT, OrderTypeEnumType.PAY_PLAN, OrderTypeEnumType.SUBSCRIPTION_FEEDING}));
        criteria.add(Restrictions.between("od.menuType", OrderDetail.TYPE_COMPLEX_MIN, OrderDetail.TYPE_COMPLEX_MAX));
        criteria.add(Restrictions.eq("o.org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.between("o.createTime", startTime, endTime));
        // не нужно - удалить
        //Org org = (Org) session.load(Org.class, idOfOrg);
        //criteria.add(Restrictions.eq("o.contragent", org.getDefaultSupplier()));
        criteria.setProjection(Projections.projectionList()
                .add(Projections.sqlProjection("sum(this_.rprice * this_.qty) as sum", new String[]{"sum"},
                        new Type[]{LongType.INSTANCE}))
        );
        List list = criteria.list();

        Long paidOrdersSum = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) paidOrdersSum = (Long) list.get(0);

        date = printTime(date, " ms - getPaidOrdersSum, idOfOrg - " + idOfOrg, 4L);

        return paidOrdersSum;
    }

    private Long getPaidOrdersSumSQL(Long idOfOrg, Date startTime, Date endTime) {

        Date date = new Date();
        date = printTime(date, " ms - getPaidOrdersSumSQL, idOfOrg - " + idOfOrg, 4L);

        Query query = session.createSQLQuery(
                "SELECT sum(this_.rprice * this_.qty) AS sum " + "FROM " + "CF_OrderDetails this_ "
                        + "INNER JOIN CF_Orders o1_ "
                        + "ON this_.IdOfOrg = o1_.IdOfOrg AND this_.IdOfOrder = o1_.IdOfOrder " + "WHERE "
                        + "this_.State = :state " + "AND o1_.OrderType IN (:ordertype) "
                //+ "AND this_.MenuType BETWEEN :menutypemin AND :menutypemax "
                //+ "AND o1_.IdOfOrg IN (:idoforgs) "
                //+ "AND o1_.CreatedDate BETWEEN :startdate AND :enddate "
        );
        query.setParameter("state", 0);
        query.setParameterList("ordertype", new Long[]{1L, 3L, 7L});
        //query.setParameter("menutypemin", 0);
        //query.setParameter("menutypemax", 0);
        //query.setParameter("idoforgs", idOfOrg);
        //query.setParameter("startdate", startTime.getTime());
        //query.setParameter("enddate", endTime.getTime());
        List list = query.list();

        Long paidOrdersSum = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) paidOrdersSum = (Long) list.get(0);

        date = printTime(date, " ms - getPaidOrdersSumSQL, idOfOrg - " + idOfOrg, 4L);

        return paidOrdersSum;
    }

    private Long getOrgClientsIncomeOnPeriod(Long idOfOrg, Date startTime, Date endTime, Long clientGroupId) {

        Date date = new Date();
        date = printTime(date, " ms - getOrgClientsIncomeOnPeriod, idOfOrg - " + idOfOrg, 4L);

        DetachedCriteria orgClientsDetachedCriteria = DetachedCriteria.forClass(Client.class);
        orgClientsDetachedCriteria.createAlias("clientGroup", "cg", JoinType.LEFT_OUTER_JOIN);
        String cgFieldName = "cg.compositeIdOfClientGroup.idOfClientGroup";
        if (!clientGroupId.equals(ClientGroupMenu.CLIENT_ALL))
            if (clientGroupId.equals(ClientGroupMenu.CLIENT_STUDENTS))
                orgClientsDetachedCriteria.add(Restrictions.not(Restrictions.in(cgFieldName, ClientGroupMenu.getNotStudent())));
            else
                orgClientsDetachedCriteria.add(Restrictions.eq(cgFieldName, clientGroupId));
        orgClientsDetachedCriteria.createCriteria("org", "o");
        orgClientsDetachedCriteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        orgClientsDetachedCriteria.setProjection(Property.forName("idOfClient"));

        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.gt("transactionTime", startTime));
        criteria.add(Restrictions.lt("transactionTime", endTime));
        criteria.add(Restrictions.gt("transactionSum", 0L));
        criteria.add(Property.forName("client.idOfClient").in(orgClientsDetachedCriteria));
        criteria.setProjection(Projections.projectionList().add(Projections.sum("transactionSum")));
        List list = criteria.list();

        Long income = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) income = (Long) list.get(0);

        date = printTime(date, " ms - getOrgClientsIncomeOnPeriod, idOfOrg - " + idOfOrg, 4L);

        return income;
    }

    private Long getOrgClientsBalanceOnDateBeforeRubicon(Long idOfOrg, Date toTime, Long clientGroupId) {

        Date date = new Date();
        date = printTime(date, " ms - getOrgClientsBalanceOnDateBeforeRubicon, idOfOrg - " + idOfOrg, 4L);

        DetachedCriteria orgClientsDetachedCriteria = DetachedCriteria.forClass(Client.class);
        orgClientsDetachedCriteria.createAlias("clientGroup", "cg", JoinType.LEFT_OUTER_JOIN);
        String cgFieldName = "cg.compositeIdOfClientGroup.idOfClientGroup";
        if (!clientGroupId.equals(ClientGroupMenu.CLIENT_ALL))
            if (clientGroupId.equals(ClientGroupMenu.CLIENT_STUDENTS))
                orgClientsDetachedCriteria.add(Restrictions.not(Restrictions.in(cgFieldName, ClientGroupMenu.getNotStudent())));
            else
                orgClientsDetachedCriteria.add(Restrictions.eq(cgFieldName, clientGroupId));
        orgClientsDetachedCriteria.createCriteria("org", "o");
        orgClientsDetachedCriteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        orgClientsDetachedCriteria.setProjection(Property.forName("idOfClient"));

        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.lt("transactionTime", toTime));
        criteria.add(Property.forName("client.idOfClient").in(orgClientsDetachedCriteria));
        criteria.setProjection(Projections.sum("transactionSum"));
        List list = criteria.list();

        Long cashSum = 0L;
        if (list != null && list.size() > 0 && list.get(0) != null) cashSum = (Long) list.get(0);

        date = printTime(date,  " ms - getOrgClientsBalanceOnDateBeforeRubicon, idOfOrg - " + idOfOrg, 4L);

        return cashSum;
    }

    private Date getLastSyncTime(Long idOfSyncOrg) {

        Date date = new Date();
        date = printTime(date, " ms - getLastSyncTime, idOfOrg - " + idOfSyncOrg, 4L);

        Criteria orgCriteria = session.createCriteria(SyncHistory.class);
        orgCriteria.createAlias("org", "o");
        orgCriteria.add(Restrictions.eq("o.idOfOrg", idOfSyncOrg));
        orgCriteria.add(Restrictions.eq("syncResult", 0));
        orgCriteria.setProjection(Projections.projectionList().add(Projections.property("syncEndTime")));
        List<Date> syncTimes = orgCriteria.list();
        Date lastSyncTime = new Date(0L);
        if (syncTimes != null && syncTimes.size() > 0 && syncTimes.get(0) != null) {
            lastSyncTime = syncTimes.get(0);
            for (Date syncTime : syncTimes)
                if (syncTime.after(lastSyncTime))
                    lastSyncTime = syncTime;
        }

        date = printTime(date, " ms - getLastSyncTime, idOfOrg - " + idOfSyncOrg, 4L);

        return lastSyncTime;
    }

    private List<Long> getOrgs(Long idOfContragent) {
        List<Long> orgList = new ArrayList<Long>();
        Set<Org> orgs = ((Contragent) session.load(Contragent.class, idOfContragent)).getOrgs();
        for (Org org : orgs) orgList.add(org.getIdOfOrg());
        return orgList;
    }

    public static class Item implements Comparable {
        private Long orgNum;
        private Long orgID;
        private String orgName;
        private String lastSyncTime;
        private Long startCash;
        private Long income;
        private Long paid;
        private Long paidSnack;
        private Long paidTotal;
        private Long repayment;
        private Long cashMoved;
        private Long endCash;
        private String comment;

        protected Item(Long orgNum, Long orgID, String orgName, String lastSyncTime, Long startCash, Long income,
                Long paid, Long paidSnack, Long paidTotal, Long repayment, Long cashMoved, Long endCash,
                String comment) {
            this.orgNum = orgNum;
            this.orgID = orgID;
            this.orgName = orgName;
            this.lastSyncTime = lastSyncTime;
            this.startCash = startCash;
            this.income = income;
            this.paid = paid;
            this.paidSnack = paidSnack;
            this.paidTotal = paidTotal;
            this.repayment = repayment;
            this.cashMoved = cashMoved;
            this.endCash = endCash;
            this.comment = comment;
        }

        @Override
        public int compareTo(Object o) {
            return Integer.valueOf(hashCode()).compareTo(o.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Item item = (Item) o;
            return lastSyncTime.equals(item.lastSyncTime) && orgName.equals(item.orgName);
        }

        @Override
        public int hashCode() {
            int result = orgID.hashCode();
            result = 31 * result + orgName.hashCode();
            result = 31 * result + lastSyncTime.hashCode();
            return result;
        }

        public Long getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(Long orgNum) {
            this.orgNum = orgNum;
        }

        public Long getOrgID() {
            return orgID;
        }

        public void setOrgID(Long orgID) {
            this.orgID = orgID;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getLastSyncTime() {
            return lastSyncTime;
        }

        public void setLastSyncTime(String lastSyncTime) {
            this.lastSyncTime = lastSyncTime;
        }

        public Long getStartCash() {
            return startCash;
        }

        public void setStartCash(Long startCash) {
            this.startCash = startCash;
        }

        public Long getIncome() {
            return income;
        }

        public void setIncome(Long income) {
            this.income = income;
        }

        public Long getPaid() {
            return paid;
        }

        public void setPaid(Long paid) {
            this.paid = paid;
        }

        public Long getPaidSnack() {
            return paidSnack;
        }

        public void setPaidSnack(Long paidSnack) {
            this.paidSnack = paidSnack;
        }

        public Long getPaidTotal() {
            return paidTotal;
        }

        public void setPaidTotal(Long paidTotal) {
            this.paidTotal = paidTotal;
        }

        public Long getRepayment() {
            return repayment;
        }

        public void setRepayment(Long repayment) {
            this.repayment = repayment;
        }

        public Long getCashMoved() {
            return cashMoved;
        }

        public void setCashMoved(Long cashMoved) {
            this.cashMoved = cashMoved;
        }

        public Long getEndCash() {
            return endCash;
        }

        public void setEndCash(Long endCash) {
            this.endCash = endCash;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
