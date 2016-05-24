/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
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
    private Long debugLevel = 2L;


    public PaymentTotalsReportService(Session session) {
        this.session = session;
    }

    public List<Item> buildReportItems(Long idOfContragent, List<Long> idOfOrgList, Date startTime, Date endTime,
            boolean hideNullRows) throws Exception {

        if (idOfOrgList.size() <= 0) {
            idOfOrgList = getOrgs(idOfContragent);
        }

        List<Long> reportOrgSet = DAOUtils.complementIdOfOrgSet(session, idOfOrgList);

        List<Item> reportItems = new ArrayList<Item>();
        for (Long idOfOrg : reportOrgSet) {

            Org org = (Org) session.load(Org.class, idOfOrg);

            String orgNumString = org.getOrgNumberInName();
            Long orgNum = orgNumString.equals("") ? null : Long.parseLong(orgNumString);

            Long orgID = org.getIdOfOrg();

            String orgName = org.getShortName();

            Date lastSyncTimeDate = getLastSyncTime(idOfOrg);
            if (lastSyncTimeDate == null) {
                lastSyncTimeDate = CalendarUtils.addDays(endTime, -1);
            }
            String lastSyncTime = YEAR_DATE_FORMAT.format(lastSyncTimeDate);

            Long startCash = getOrgStudentsBalanceSumToDate(idOfOrg, startTime);

            Long income = getOrgClientsIncomeOnPeriod(idOfOrg, startTime, endTime);

            Long paid = getPaidComplexOrdersSum(idOfOrg, startTime, endTime);

            Long paidSnack = getPaidSnackOrdersSum(idOfOrg, startTime, endTime);

            Long paidTotal = paid + paidSnack;

            Long repayment = getRepaymentSum(idOfOrg, startTime, endTime);

            Long cashMoved = getCashMovedSum(idOfOrg, startTime, endTime);

            Long endCash = getOrgStudentsBalanceSumToDate(idOfOrg, endTime);

            Long endCash1 = startCash + income - paidTotal - repayment + cashMoved;

            Long endCash2 = getAllOrdersSum(idOfOrg, startTime, endTime);

            String comment = getStatusDetail(idOfOrg);

            TreeMap<Long, AccountTransaction> transactions = getTransactions(idOfOrg, startTime, endTime);
            TreeMap<CompositeIdOfOrder, Order> orders = getOrders(idOfOrg, startTime, endTime);
            Long transactionsSum = 0L;
            Long ordersSum = 0L;


            Item item = new Item(orgNum, orgID, orgName, lastSyncTime, startCash, income, paid, paidSnack, paidTotal,
                    repayment, cashMoved, endCash, endCash1, endCash2, comment);
            if (!zeroCash(item) || !hideNullRows) {
                reportItems.add(item);
            }
        }

        return reportItems;
    }

    /**
     * Разница между
     * 1) суммой текущих балансов клиентов (принадлежащих данной ОО на дату toDate)
     * 2) и суммой транзакций этих клиентов с даты toDate по текущий момент.
     *
     * @param idOfOrg
     * @param toDate
     * @return
     */

    private Long getOrgStudentsBalanceSumToDate(Long idOfOrg, Date toDate) {

        Set<Client> clientMap = getOrgClientsToDate(idOfOrg, toDate);

        List<Long> clientIdList = new ArrayList<Long>();
        clientIdList.add(-1L);
        for (Client client : clientMap) {
            clientIdList.add(client.getIdOfClient());
        }

        Criteria orgClientsToDateCriteria = session.createCriteria(Client.class);
        orgClientsToDateCriteria.add(Restrictions.in("idOfClient", clientIdList));
        orgClientsToDateCriteria.setProjection(Projections.sum("balance"));
        Long orgClientsBalanceToDate = (Long) orgClientsToDateCriteria.uniqueResult();
        if (orgClientsBalanceToDate == null) {
            orgClientsBalanceToDate = 0L;
        }

        Criteria transactionsFromDateCriteria = session.createCriteria(AccountTransaction.class);
        transactionsFromDateCriteria.createAlias("client", "c");
        transactionsFromDateCriteria.add(Restrictions.in("c.idOfClient", clientIdList));
        transactionsFromDateCriteria.add(Restrictions.gt("transactionTime", toDate));
        transactionsFromDateCriteria.setProjection(Projections.sum("transactionSum"));
        Long orgClientsTransactionsSumFromDate = (Long) transactionsFromDateCriteria.uniqueResult();
        if (orgClientsTransactionsSumFromDate == null) {
            orgClientsTransactionsSumFromDate = 0L;
        }

        return orgClientsBalanceToDate - orgClientsTransactionsSumFromDate;
    }

    /**
     * Клиенты ОО на дату toDate
     *
     * @param idOfOrg
     * @param toDate
     * @return
     */

    private Set<Client>
    getOrgClientsToDate(Long idOfOrg, Date toDate) {

        HashMap<Client, List<ClientMigration>> clientMigrationListsMap = getClientMigrationsListHashMap(idOfOrg,
                toDate);

        Criteria orgClientsCriteria = session.createCriteria(Client.class);
        orgClientsCriteria.createAlias("org", "o");
        orgClientsCriteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        List<Client> todayClientList = orgClientsCriteria.list();
        Set<Client> clientMap = new HashSet<Client>();
        clientMap.addAll(todayClientList);

        for (Client client : clientMigrationListsMap.keySet()) {
            ClientMigration firstClientMigration = null;
            Date registrationDate = new Date();
            for (ClientMigration clientMigration : clientMigrationListsMap.get(client)) {
                if (clientMigration.getRegistrationDate().before(registrationDate)) {
                    firstClientMigration = clientMigration;
                    registrationDate = clientMigration.getRegistrationDate();
                }
            }
            if (firstClientMigration != null && firstClientMigration.getOldOrg() != null
                    && firstClientMigration.getOldOrg().getIdOfOrg().equals(idOfOrg) && !clientMap
                    .contains(firstClientMigration.getClient())) {
                clientMap.add(client);
            }
            if (firstClientMigration != null && firstClientMigration.getOrg() != null
                    && firstClientMigration.getOrg().getIdOfOrg().equals(idOfOrg) && clientMap
                    .contains(firstClientMigration.getClient())) {
                clientMap.remove(client);
            }
        }

        return clientMap;
    }

    private HashMap<Client, List<ClientMigration>> getClientMigrationsListHashMap(Long idOfOrg, Date startDate) {
        return getClientMigrationsListHashMap(idOfOrg, startDate, new Date());
    }

    private HashMap<Client, List<ClientMigration>> getClientMigrationsListHashMap(Long idOfOrg, Date toDate,
            Date endDate) {
        Org org = null;
        try {
            org = DAOUtils.findOrg(session, idOfOrg);
        } catch (Exception e) {
            logger.warn("Org by idOfOrg not found. ", e);
        }
        List<ClientMigration> clientMigrations =
                org != null ? getClientMigrationsForOrg(org, toDate, endDate) : new ArrayList<ClientMigration>();

        HashMap<Client, List<ClientMigration>> clientMigrationListsMap = new HashMap<Client, List<ClientMigration>>();
        for (ClientMigration clientMigration : clientMigrations) {
            if (clientMigrationListsMap.containsKey(clientMigration.getClient())) {
                clientMigrationListsMap.get(clientMigration.getClient()).add(clientMigration);
            } else {
                List<ClientMigration> clientMigrationList = new ArrayList<ClientMigration>();
                clientMigrationList.add(clientMigration);
                clientMigrationListsMap.put(clientMigration.getClient(), clientMigrationList);
            }
        }
        return clientMigrationListsMap;
    }

    private List<ClientMigration> getClientMigrationsForOrg(Org org, Date start, Date end) {
        Criteria clientMigrationCriteria = session.createCriteria(ClientMigration.class);
        clientMigrationCriteria.add(Restrictions.or(Restrictions.eq("org", org), Restrictions.eq("oldOrg", org)));
        clientMigrationCriteria.add(Restrictions.between("registrationDate", start, end));
        List<ClientMigration> clientMigrations = clientMigrationCriteria.list();
        return clientMigrations != null ? clientMigrations : new ArrayList<ClientMigration>();
    }

    /**
     * Поступления на счета клиентов ОО за период (транзакции ОО с положительной суммой за период)
     *
     * @param idOfOrg
     * @param startTime
     * @param endTime
     * @return
     */

    private Long getOrgClientsIncomeOnPeriod(Long idOfOrg, Date startTime, Date endTime) {
        Object[] objects = {AccountTransaction.CASHBOX_TRANSACTION_SOURCE_TYPE, AccountTransaction.PAYMENT_SYSTEM_TRANSACTION_SOURCE_TYPE,
        AccountTransaction.ACCOUNT_TRANSFER_TRANSACTION_SOURCE_TYPE};
        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.gt("transactionTime", startTime));
        criteria.add(Restrictions.lt("transactionTime", endTime));
        criteria.add(Restrictions.gt("transactionSum", 0L));
        criteria.add(Restrictions.in("sourceType", objects));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        criteria.setProjection(Projections.projectionList().add(Projections.sum("transactionSum")));
        Long income = (Long) criteria.uniqueResult();
        if (income == null) {
            income = 0L;
        }
        return income;
    }

    private TreeMap<Long, AccountTransaction> getTransactions(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.gt("transactionTime", startTime));
        criteria.add(Restrictions.lt("transactionTime", endTime));
        criteria.add(Restrictions.gt("transactionSum", 0L));
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        List<AccountTransaction> transactions = criteria.list();
        TreeMap<Long, AccountTransaction> transactionsTreeMap = new TreeMap<Long, AccountTransaction>();
        if (transactions != null) {
            for (AccountTransaction accountTransaction : transactions) {
                transactionsTreeMap.put(accountTransaction.getIdOfTransaction(), accountTransaction);
            }
        }
        return transactionsTreeMap;
    }

    private TreeMap<CompositeIdOfOrder, Order> getOrders(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = session.createCriteria(OrderDetail.class, "od");
        criteria.createAlias("order", "o");
        criteria.add(Restrictions.eq("od.state", 0));
        criteria.add(Restrictions.eq("o.org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.between("o.createTime", startTime, endTime));
        List<OrderDetail> orderDetails = criteria.list();
        TreeMap<CompositeIdOfOrder, Order> orders = new TreeMap<CompositeIdOfOrder, Order>();
        if (orderDetails == null) {
            for (OrderDetail orderDetail : orderDetails) {
                orders.put(orderDetail.getOrder().getCompositeIdOfOrder(), orderDetail.getOrder());
            }
        }
        return orders;
    }

    private String getStatusDetail(Long idOfOrg) {
        Org org = (Org) session.load(Org.class, idOfOrg);
        String statusDetailing = org.getStatusDetailing();
        if (!statusDetailing.equals("") && !statusDetailing.equals("/")) {
            return statusDetailing;
        }
        return "";
    }

    private boolean zeroCash(Item item) {
        if (item.getIncome() != 0L || item.getPaid() != 0L || item.getPaidSnack() != 0L || item.getPaidTotal() != 0L
                || item.getRepayment() != 0L || item.getCashMoved() != 0L) {
            return false;
        } else {
            return true;
        }
    }

    private Date printTime(Date date, String message, Long debugLevel) {
        if (debugLevel == this.debugLevel) {
            Date newDate;
            newDate = new Date();
            Long diff = (newDate.getTime() - date.getTime());
            if (diff > 1000L) {
                logger.warn((newDate.getTime() - date.getTime()) + message + " debug level - " + debugLevel);
            }
            date = newDate;
        }
        return date;
    }

    /**
     * Сумма балансов клиентов на момент перемещения из или в ОО
     *
     * @param idOfOrg
     * @param startTime
     * @param endTime
     * @return
     */

    private Long getCashMovedSum(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = session.createCriteria(ClientMigration.class, "c");
        criteria.add(Restrictions.between("c.registrationDate", startTime, endTime));
        criteria.add(Restrictions
                .or(Restrictions.eq("c.org.idOfOrg", idOfOrg), Restrictions.eq("c.oldOrg.idOfOrg", idOfOrg)));
        List<ClientMigration> list = criteria.list();
        Long cashMovedSum = 0L;
        if (list != null) {
            for (ClientMigration clientMigration : list) {
                if (clientMigration.getOrg().getIdOfOrg() == idOfOrg) {
                    cashMovedSum += getClientBalanceOnDate(clientMigration.getClient(),
                            clientMigration.getRegistrationDate());
                } else if (clientMigration.getOldOrg().getIdOfOrg() == idOfOrg) {
                    cashMovedSum -= getClientBalanceOnDate(clientMigration.getClient(),
                            clientMigration.getRegistrationDate());
                }
            }
        }
        return cashMovedSum;
    }

    /**
     * Вспомогательная функция для getCashMovedSum
     *
     * @param client
     * @param registrationDate
     * @return
     */

    private Long getClientBalanceOnDate(Client client, Date registrationDate) {
        Long balance = client.getBalance();
        Criteria criteria = session.createCriteria(AccountTransaction.class);
        criteria.add(Restrictions.gt("transactionTime", registrationDate));
        criteria.add(Restrictions.eq("client", client));
        criteria.setProjection(Projections.projectionList().add(Projections.sum("transactionSum")));
        try {
            balance -= (Long) criteria.uniqueResult();
        } catch (Exception e) {
        }
        return balance;
    }

    /**
     * Сумма возвратов клиентам ОО за период
     *
     * @param idOfOrg
     * @param startTime
     * @param endTime
     * @return
     */

    private Long getRepaymentSum(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = session.createCriteria(AccountTransaction.class, "at");
        criteria.createAlias("org", "o");
        criteria.add(Restrictions.eq("at.sourceType", AccountTransaction.ACCOUNT_REFUND_TRANSACTION_SOURCE_TYPE));
        criteria.add(Restrictions.between("at.transactionTime", startTime, endTime));
        criteria.add(Restrictions.eq("o.idOfOrg", idOfOrg));
        criteria.setProjection(Projections.projectionList().add(Projections
                .sqlProjection("sum(this_.transactionsum) as sum", new String[]{"sum"},
                        new Type[]{LongType.INSTANCE})));
        Long repaymentSum = (Long) criteria.uniqueResult();
        if (repaymentSum == null) {
            repaymentSum = 0L;
        }
        return repaymentSum * -1L;
    }

    /**
     * Сумма всех оплат в ОО за период
     *
     * @param idOfOrg
     * @param startTime
     * @param endTime
     * @return
     */

    private Long getAllOrdersSum(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = session.createCriteria(OrderDetail.class, "od");
        criteria.createAlias("order", "o");
        criteria.add(Restrictions.eq("od.state", 0));
        criteria.add(Restrictions.eq("o.org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.between("o.createTime", startTime, endTime));
        criteria.setProjection(Projections.projectionList().add(Projections
                .sqlProjection("sum(this_.rprice * this_.qty) as sum", new String[]{"sum"},
                        new Type[]{LongType.INSTANCE})));
        Long allOrdersSum = (Long) criteria.uniqueResult();
        if (allOrdersSum == null) {
            allOrdersSum = 0L;
        }
        return allOrdersSum;
    }

    /**
     * Сумма оплат за буфетную продукцию в ОО за период
     *
     * @param idOfOrg
     * @param startTime
     * @param endTime
     * @return
     */

    private Long getPaidSnackOrdersSum(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = session.createCriteria(OrderDetail.class, "od");
        criteria.createAlias("order", "o");
        criteria.add(Restrictions.eq("od.state", 0));
        criteria.add(Restrictions.eq("o.orderType", OrderTypeEnumType.DEFAULT));
        criteria.add(Restrictions.eq("od.menuType", OrderDetail.TYPE_DISH_ITEM));
        criteria.add(Restrictions.eq("o.org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.between("o.createTime", startTime, endTime));
        criteria.setProjection(Projections.projectionList().add(Projections
                .sqlProjection("sum(this_.rprice * this_.qty) as sum", new String[]{"sum"},
                        new Type[]{LongType.INSTANCE})));
        Long paidSnackSum = (Long) criteria.uniqueResult();
        if (paidSnackSum == null) {
            paidSnackSum = 0L;
        }
        return paidSnackSum;
    }

    /**
     * Сумма оплат за комплексы в ОО за период
     *
     * @param idOfOrg
     * @param startTime
     * @param endTime
     * @return
     */

    private Long getPaidComplexOrdersSum(Long idOfOrg, Date startTime, Date endTime) {
        Criteria criteria = session.createCriteria(OrderDetail.class, "od");
        criteria.createAlias("order", "o");
        criteria.add(Restrictions.eq("od.state", 0));
        criteria.add(Restrictions.in("o.orderType", new OrderTypeEnumType[]{
                OrderTypeEnumType.DEFAULT, OrderTypeEnumType.PAY_PLAN, OrderTypeEnumType.SUBSCRIPTION_FEEDING}));
        criteria.add(Restrictions.between("od.menuType", OrderDetail.TYPE_COMPLEX_MIN, OrderDetail.TYPE_COMPLEX_MAX));
        criteria.add(Restrictions.eq("o.org.idOfOrg", idOfOrg));
        criteria.add(Restrictions.between("o.createTime", startTime, endTime));
        criteria.setProjection(Projections.projectionList().add(Projections
                .sqlProjection("sum(this_.rprice * this_.qty) as sum", new String[]{"sum"},
                        new Type[]{LongType.INSTANCE})));
        Long paidOrdersSum = (Long) criteria.uniqueResult();
        if (paidOrdersSum == null) {
            paidOrdersSum = 0L;
        }
        return paidOrdersSum;
    }

    /**
     * Дата последней синхронизации
     *
     * @param idOfSyncOrg
     * @return
     */

    private Date getLastSyncTime(Long idOfSyncOrg) {
        Criteria syncHistoryCriteria = session.createCriteria(SyncHistory.class);
        syncHistoryCriteria.createAlias("org", "o");
        syncHistoryCriteria.add(Restrictions.eq("o.idOfOrg", idOfSyncOrg));
        syncHistoryCriteria.add(Restrictions.eq("syncResult", 0));
        syncHistoryCriteria.setProjection(Projections.projectionList().add(Projections.max("syncEndTime")));
        Date lastSyncTime = (Date) syncHistoryCriteria.uniqueResult();
        return lastSyncTime;
    }

    private List<Long> getOrgs(Long idOfContragent) {
        List<Long> orgList = new ArrayList<Long>();
        Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
        Set<Org> orgs = new HashSet<Org>();
        if (contragent != null) {
            orgs = contragent.getOrgs();
        }
        for (Org org : orgs) {
            orgList.add(org.getIdOfOrg());
        }
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
        private Long endCash1;
        private Long endCash2;
        private String comment;

        protected Item(Long orgNum, Long orgID, String orgName, String lastSyncTime, Long startCash, Long income,
                Long paid, Long paidSnack, Long paidTotal, Long repayment, Long cashMoved, Long endCash, Long endCash1,
                Long endCash2, String comment) {
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
            this.endCash1 = endCash1;
            this.endCash2 = endCash2;
            this.comment = comment;
        }

        @Override
        public int compareTo(Object o) {
            return Integer.valueOf(hashCode()).compareTo(o.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
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

        public Long getEndCash1() {
            return endCash1;
        }

        public void setEndCash1(Long endCash1) {
            this.endCash1 = endCash1;
        }

        public Long getEndCash2() {
            return endCash2;
        }

        public void setEndCash2(Long endCash2) {
            this.endCash2 = endCash2;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
