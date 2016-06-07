/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Contragent;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 20.10.11
 * Time: 12:09
 * To change this template use File | Settings | File Templates.
 */
public class ClientReport extends BasicReport {
    private final List<ClientItem> clientItems;

    public static class Builder {

        public ClientReport build(String contragentIds, Long clientGroupId, Session session) throws Exception {
            Date generateTime = new Date();

            String groupWhere = "";
            if (!clientGroupId.equals(ClientGroupMenu.CLIENT_ALL)) {
                if (clientGroupId.equals(ClientGroupMenu.CLIENT_STUDENTS)) {
                    List<Long> www = ClientGroupMenu.getNotStudent();
                    for (Long v : www) {
                        groupWhere += v.toString() + ",";
                    }
                    groupWhere = groupWhere.substring(0, groupWhere.length()-1);
                } else {
                    groupWhere = String.format("%s", clientGroupId);
                }
            }

            /*Criteria clientCriteria = session.createCriteria(Client.class);

            clientCriteria.createAlias("org", "o").
                    setProjection(Projections.projectionList().
                        add(Projections.groupProperty("o.idOfOrg").as("idOfOrg")).
                        add(Projections.groupProperty("o.officialName").as("officialName")).
                        add(Projections.count("idOfClient")).
                        add(Projections.sqlProjection("sum(case when balance >= 0 then 1 else 0 end) as posbal",
                                                       new Integer[]{"posbal"},
                                                       new Type[]{Hibernate.INTEGER})).
                        add(Projections.sum("balance")));
            clientCriteria.addOrder(Order.asc("idOfOrg"));
            clientCriteria.addOrder(Order.asc("officialName"));
            List clientItems = clientCriteria.list();*/

            String preparedQuery = "select org.idOfOrg, org.officialName, count(idOfClient) as clientCount, "
                    + "       sum(case when balance > 0 then 1 else 0 end) as posbalCount, "
                    + "       sum(case when balance = 0 then 1 else 0 end) as nulbalCount, "
                    + "       sum(case when balance < 0 then 1 else 0 end) as negbalCount, "
                    + "       sum(balance) as balsum, "
                    + "       sum(case when balance > 0 then balance else 0 end) as posbalsum, "
                    + "       sum(case when balance < 0 then balance else 0 end) as negbalsum " + "  from Client "
                    + "where org.defaultSupplier.idOfContragent in ( " + contragentIds + " ) "
                    + " group by org.idOfOrg, org.officialName " + " order by org.idOfOrg";
            List resultList = null;
            Query query = session.createQuery(preparedQuery);

            resultList = query.list();

            List<ClientItem> clientItemList = new ArrayList<ClientItem>();
            for (Object obj : resultList) {
                Object[] client = (Object[]) obj;
                Long idOfOrg = (Long) client[0];
                String officialName = (String) client[1];
                Long clientCount = (Long) client[2];
                Long clientWithPositiveBalanceCount = (Long) client[3];
                Long clientWithNullBalanceCount = (Long) client[4];
                Long clientWithNegativeBalanceCount = (Long) client[5];
                Long balanceSum = (Long) client[6];
                Long posBalanceSum = (Long) client[7];
                Long negBalanceSum = (Long) client[8];
                clientItemList.add(new ClientItem(idOfOrg, officialName, clientCount, clientWithPositiveBalanceCount,
                        clientWithNullBalanceCount, clientWithNegativeBalanceCount, balanceSum, posBalanceSum,
                        negBalanceSum));
            }
            return new ClientReport(generateTime, new Date().getTime() - generateTime.getTime(), clientItemList);
        }

    }

    public ClientReport() {
        super();
        this.clientItems = Collections.emptyList();
    }

    public ClientReport(Date generateTime, long generateDuration, List<ClientItem> clientItems) {
        super(generateTime, generateDuration);
        this.clientItems = clientItems;
    }

    public List<ClientItem> getClientItems() {
        return clientItems;
    }

    public static class ClientItem {
        private Long idOfOrg; // Идентификатор школы
        private String officialName; // Название школы
        private Long clientCount; // Количество учащихся
        private Long clientWithPositiveBalanceCount; // Количество детей у которых баланс больше нуля
        private Long clientWithNullBalanceCount; // Количество детей у которых баланс равен нулю
        private Long clientWithNegativeBalanceCount; // Количество детей у которых баланс меньше нуля
        private String balanceSum; // Сумма денег на их картах
        private String posBalanceSum; // Сумма положительных балансов
        private String negBalanceSum; // Сумма отрицательных балансов

        public ClientItem(Long idOfOrg, String officialName, Long clientCount, Long clientWithPositiveBalanceCount,
                Long clientWithNullBalanceCount, Long clientWithNegativeBalanceCount, Long balanceSum,
                Long posBalanceSum, Long negBalanceSum) {
            this.idOfOrg = idOfOrg;
            this.officialName = officialName;
            this.clientCount = clientCount;
            this.clientWithPositiveBalanceCount = clientWithPositiveBalanceCount;
            this.clientWithNullBalanceCount = clientWithNullBalanceCount;
            this.clientWithNegativeBalanceCount = clientWithNegativeBalanceCount;
            this.balanceSum = longToMoney(balanceSum);
            this.posBalanceSum = longToMoney(posBalanceSum);
            this.negBalanceSum = longToMoney(negBalanceSum);
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public Long getClientCount() {
            return clientCount;
        }

        public void setClientCount(Long clientCount) {
            this.clientCount = clientCount;
        }

        public Long getClientWithPositiveBalanceCount() {
            return clientWithPositiveBalanceCount;
        }

        public void setClientWithPositiveBalanceCount(Long clientWithPositiveBalanceCount) {
            this.clientWithPositiveBalanceCount = clientWithPositiveBalanceCount;
        }

        public Long getClientWithNullBalanceCount() {
            return clientWithNullBalanceCount;
        }

        public void setClientWithNullBalanceCount(Long clientWithNullBalanceCount) {
            this.clientWithNullBalanceCount = clientWithNullBalanceCount;
        }

        public Long getClientWithNegativeBalanceCount() {
            return clientWithNegativeBalanceCount;
        }

        public void setClientWithNegativeBalanceCount(Long clientWithNegativeBalanceCount) {
            this.clientWithNegativeBalanceCount = clientWithNegativeBalanceCount;
        }

        public String getBalanceSum() {
            return balanceSum;
        }

        public void setBalanceSum(String balanceSum) {
            this.balanceSum = balanceSum;
        }

        public String getPosBalanceSum() {
            return posBalanceSum;
        }

        public void setPosBalanceSum(String posBalanceSum) {
            this.posBalanceSum = posBalanceSum;
        }

        public String getNegBalanceSum() {
            return negBalanceSum;
        }

        public void setNegBalanceSum(String negBalanceSum) {
            this.negBalanceSum = negBalanceSum;
        }
    }
}
