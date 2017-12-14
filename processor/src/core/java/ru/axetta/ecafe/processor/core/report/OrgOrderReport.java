/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.10.2009
 * Time: 14:09:39
 * Продажи по продукции
 * Организации -> {Выбранная организация} -> Отчет по покупкам
 */
public class OrgOrderReport extends BasicReport {

    public static class PersonItem {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

        public String getFirstName() {
            return firstName;
        }

        public String getSurname() {
            return surname;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getIdDocument() {
            return idDocument;
        }

        public PersonItem(String surname, String firstName, String secondName) {
            this.firstName = firstName;
            this.surname = surname;
            this.secondName = secondName;
            this.idDocument = "";
        }
    }

    public static class ClientItem {

        private final Long contractId;
        private final PersonItem person;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalSocDiscount;
        private final long totalTrdDiscount;
        private final long totalGrantSum;

        public Long getContractId() {
            return contractId;
        }

        public PersonItem getPerson() {
            return person;
        }

        public long getTotalSocDiscount() {
            return totalSocDiscount;
        }

        public long getTotalTrdDiscount() {
            return totalTrdDiscount;
        }

        public long getTotalGrantSum() {
            return totalGrantSum;
        }

        public long getTotalOrderSumByCard() {
            return totalOrderSumByCard;
        }

        public long getTotalOrderSumByCash() {
            return totalOrderSumByCash;
        }

        public ClientItem(Long contractID, String surname, String firstName, String secondName, long totalOrderSumByCard,
                long totalOrderSumByCash, long totalSocDiscount, long totalTrdDiscount, long totalGrantSum) {
            this.contractId = contractID;
            this.person = new PersonItem(surname, firstName, secondName);
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalSocDiscount = totalSocDiscount;
            this.totalTrdDiscount = totalTrdDiscount;
            this.totalGrantSum = totalGrantSum;
        }
    }

    public static class ClientGroupItem implements Comparable<ClientGroupItem> {

        private final Long idOfClientGroup;
        private final String groupName;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalSocDiscount, totalTrdDiscount;
        private final long totalGrantSum;
        private final List<ClientItem> clients;

        public Long getIdOfClientGroup() {
            return idOfClientGroup;
        }

        public String getGroupName() {
            return groupName;
        }

        public long getTotalSocDiscount() {
            return totalSocDiscount;
        }

        public long getTotalTrdDiscount() {
            return totalTrdDiscount;
        }

        public long getTotalGrantSum() {
            return totalGrantSum;
        }

        public long getTotalOrderSumByCard() {
            return totalOrderSumByCard;
        }

        public long getTotalOrderSumByCash() {
            return totalOrderSumByCash;
        }

        public List<ClientItem> getClients() {
            return clients;
        }

        public ClientGroupItem(Long goupID, String groupName, long totalOrderSumByCard, long totalOrderSumByCash,
                long totalSocDiscount, long totalTrdDiscount, long totalGrantSum, List<ClientItem> clients) {
            this.idOfClientGroup = goupID;
            this.groupName = groupName;
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalSocDiscount = totalSocDiscount;
            this.totalTrdDiscount = totalTrdDiscount;
            this.totalGrantSum = totalGrantSum;
            this.clients = clients;
        }

        public ClientGroupItem() {
            this.idOfClientGroup = null;
            this.groupName = null;
            this.totalOrderSumByCard = 0;
            this.totalOrderSumByCash = 0;
            this.totalSocDiscount = 0;
            this.totalTrdDiscount = 0;
            this.totalGrantSum = 0;
            this.clients = Collections.emptyList();
        }

        @Override
        public int compareTo(ClientGroupItem o) {
            return this.getGroupName().compareTo(o.getGroupName());
        }
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalSocDiscount, totalTrdDiscount;
        private final long totalGrantSum;
        private final List<ClientGroupItem> clientGroups;

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }

        public long getTotalOrderSumByCard() {
            return totalOrderSumByCard;
        }

        public long getTotalOrderSumByCash() {
            return totalOrderSumByCash;
        }

        public long getTotalSocDiscount() {
            return totalSocDiscount;
        }
        public long getTotalTrdDiscount() {
            return totalTrdDiscount;
        }

        public long getTotalGrantSum() {
            return totalGrantSum;
        }

        public List<ClientGroupItem> getClientGroups() {
            return clientGroups;
        }

        public OrgItem(Org org, long totalOrderSumByCard, long totalOrderSumByCash, long totalSocDiscount, long totalTrdDiscount,
                long totalGrantSum, List<ClientGroupItem> clientGroups) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalSocDiscount = totalSocDiscount;
            this.totalTrdDiscount = totalTrdDiscount;
            this.totalGrantSum = totalGrantSum;
            this.clientGroups = clientGroups;
        }

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
            this.totalOrderSumByCard = 0;
            this.totalOrderSumByCash = 0;
            this.totalSocDiscount = 0;
            this.totalTrdDiscount = 0;
            this.totalGrantSum = 0;
            this.clientGroups = Collections.emptyList();
        }

        @Override
        public String toString() {
            return "OrgItem{" + "idOfOrg=" + idOfOrg + ", shortName='" + shortName + '\'' + ", officialName='"
                    + officialName + '\'' + ", totalOrderSumByCard=" + totalOrderSumByCard + ", totalOrderSumByCash="
                    + totalOrderSumByCash + ", totalSocDiscount=" + totalSocDiscount + ", totalTrdDiscount="+totalTrdDiscount+", totalGrantSum=" + totalGrantSum
                    + '}';
        }
    }

    public static class Builder {

        private static class GroupedClientsData {

            private final long totalOrderSumByCard;
            private final long totalOrderSumByCash;
            private final long totalSocDiscount, totalTrdDiscount;
            private final long totalGrantSum;
            private final List<ClientGroupItem> clientGroupItems;

            private GroupedClientsData(long totalOrderSumByCard, long totalOrderSumByCash, long totalSocDiscount,
                    long totalTrdDiscount, long totalGrantSum, List<ClientGroupItem> clientGroupItems) {
                this.totalOrderSumByCard = totalOrderSumByCard;
                this.totalOrderSumByCash = totalOrderSumByCash;
                this.totalSocDiscount = totalSocDiscount;
                this.totalTrdDiscount = totalTrdDiscount;
                this.totalGrantSum = totalGrantSum;
                this.clientGroupItems = clientGroupItems;
            }

            public long getTotalOrderSumByCard() {
                return totalOrderSumByCard;
            }

            public long getTotalOrderSumByCash() {
                return totalOrderSumByCash;
            }

            public long getTotalSocDiscount() {
                return totalSocDiscount;
            }

            public long getTotalTrdDiscount() {
                return totalTrdDiscount;
            }

            public long getTotalGrantSum() {
                return totalGrantSum;
            }

            public List<ClientGroupItem> getClientGroupItems() {
                return clientGroupItems;
            }
        }

        private static class TotalSums {

            private final long sumByCard;
            private final long sumByCash;
            private final long socDiscount;
            private final long trdDiscount;
            private final long totalGrantSum;

            private TotalSums(long sumByCard, long sumByCash, long socDiscount, long trdDiscount, long totalGrantSum) {
                this.sumByCard = sumByCard;
                this.sumByCash = sumByCash;
                this.socDiscount = socDiscount;
                this.trdDiscount = trdDiscount;
                this.totalGrantSum = totalGrantSum;
            }

            public long getSumByCard() {
                return sumByCard;
            }

            public long getSumByCash() {
                return sumByCash;
            }

            public long getSocDiscount() {
                return socDiscount;
            }

            public long getTrdDiscount() {
                return trdDiscount;
            }

            public long getTotalGrantSum() {
                return totalGrantSum;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || getClass() != o.getClass()) {
                    return false;
                }

                TotalSums totalSums = (TotalSums) o;

                if (trdDiscount != totalSums.trdDiscount) {
                    return false;
                }
                if (socDiscount != totalSums.socDiscount) {
                    return false;
                }
                if (sumByCard != totalSums.sumByCard) {
                    return false;
                }
                if (sumByCash != totalSums.sumByCash) {
                    return false;
                }
                if (totalGrantSum != totalSums.totalGrantSum) {
                    return false;
                }

                return true;
            }

            @Override
            public int hashCode() {
                int result = (int) (sumByCard ^ (sumByCard >>> 32));
                result = 31 * result + (int) (sumByCash ^ (sumByCash >>> 32));
                result = 31 * result + (int) (socDiscount ^ (socDiscount >>> 32));
                result = 31 * result + (int) (trdDiscount ^ (trdDiscount >>> 32));
                result = 31 * result + (int) (totalGrantSum ^ (totalGrantSum >>> 32));
                return result;
            }
        }

        public OrgOrderReport build(Session session, Date startTime, Date endTime, Org org, Boolean hideEmptyClients) throws Exception {
            Date generateTime = new Date();
            GroupedClientsData groupedClientsData = buildGroupedClientsData(session, startTime, endTime, org, hideEmptyClients);
            ClientGroupItem unGroupedClientsData = buildUnGroupedClientsData(session, startTime, endTime, org, hideEmptyClients);
            List<ClientGroupItem> clientGroupItems = groupedClientsData.getClientGroupItems();
            clientGroupItems.add(unGroupedClientsData);
            return new OrgOrderReport(generateTime, new Date().getTime() - generateTime.getTime(), startTime, endTime,
                    new OrgItem(org, groupedClientsData.getTotalOrderSumByCard() + unGroupedClientsData
                            .getTotalOrderSumByCard(),
                            groupedClientsData.getTotalOrderSumByCash() + unGroupedClientsData
                                    .getTotalOrderSumByCash(),
                            groupedClientsData.getTotalSocDiscount() + unGroupedClientsData.getTotalSocDiscount(),
                            groupedClientsData.getTotalTrdDiscount() + unGroupedClientsData.getTotalTrdDiscount(),
                            groupedClientsData.getTotalGrantSum() + unGroupedClientsData.getTotalGrantSum(),
                            clientGroupItems));
        }

        private static GroupedClientsData buildGroupedClientsData(Session session, Date startTime, Date endTime,
                Org org, Boolean hideEmptyClients) throws Exception {
              String sql =
                    "SELECT c.contractid, g.idofclientgroup, g.groupname, p.surname, p.firstname, p.secondname, "
                  + "   CASE WHEN sum(o.sumbycard) IS NULL THEN 0 ELSE sum(o.sumbycard) END AS sumbycard, "
                  + "   CASE WHEN sum(o.sumbycash) IS NULL THEN 0 ELSE sum(o.sumbycash) END AS sumbycash, "
                  + "   CASE WHEN sum(o.socdiscount) IS NULL THEN 0 ELSE sum(o.socdiscount) END AS socdisocunt, "
                  + "   CASE WHEN sum(o.trddiscount) IS NULL THEN 0 ELSE sum(o.trddiscount) END AS trddiscount, "
                  + "   CASE WHEN sum(o.grantsum) IS NULL THEN 0 ELSE sum(o.grantsum) END AS grantsum "
                  + "FROM cf_clientgroups g "
                  + "INNER JOIN cf_clients c ON c.idoforg=g.idoforg AND c.idofclientgroup=g.idofclientgroup "
                  + "INNER JOIN cf_persons p ON p.idofperson=c.idofperson "
                  + "LEFT JOIN cf_orders o ON o.idofclient=c.idofclient AND o.state=0 AND o.createddate >= :startDate AND o.createddate < :endDate "
                  + "WHERE g.idoforg=:idOfOrg " + (hideEmptyClients ? "AND o.idoforder IS NOT NULL " : "")
                  + "GROUP BY c.contractid, g.idofclientgroup, g.groupname, p.surname, p.firstname, p.secondname "
                  + "ORDER BY g.groupname, p.surname, p.firstname, p.secondname;";

            Query sqlQuery = session.createSQLQuery(sql);
            sqlQuery.setParameter("idOfOrg", org.getIdOfOrg());
            sqlQuery.setParameter("startDate", startTime.getTime());
            sqlQuery.setParameter("endDate", endTime.getTime());

            List clients = sqlQuery.list();
            HashMap<Long, List<GroupedClientItem>> clientItems = new HashMap<Long, List<GroupedClientItem>>();

            for (Object o : clients) {
                Object[] vals = (Object[])o;

                GroupedClientItem groupedClientItem = new GroupedClientItem(((BigInteger)vals[0]).longValue(),
                        ((BigInteger)vals[1]).longValue(), (String)vals[2], (String)vals[3], (String)vals[4],
                        (String)vals[5], ((BigDecimal)vals[6]).longValue(), ((BigDecimal)vals[7]).longValue(),
                        ((BigDecimal)vals[8]).longValue(), ((BigDecimal)vals[9]).longValue(),
                        ((BigDecimal)vals[10]).longValue());

                if (clientItems.containsKey(groupedClientItem.getIdOfGroup())) {
                    List<GroupedClientItem> clientItemList = clientItems.get(groupedClientItem.getIdOfGroup());
                    clientItemList.add(groupedClientItem);
                } else {
                    List<GroupedClientItem> clientItemList = new ArrayList<GroupedClientItem>();
                    clientItemList.add(groupedClientItem);
                    clientItems.put(groupedClientItem.getIdOfGroup(), clientItemList);
                }
            }

            long totalOrderSumByCard = 0;
            long totalOrderSumByCash = 0;
            long totalSocDiscount = 0, totalTrdDiscount = 0;
            long totalGrantSum = 0;
            List<ClientGroupItem> clientGroupItems = new LinkedList<ClientGroupItem>();
            for (Long idOfClientGroup : clientItems.keySet()) {
                List<GroupedClientItem> groupedClientItemList = clientItems.get(idOfClientGroup);
                ClientGroupItem groupItem = buildClientGroupItem(groupedClientItemList);
                totalOrderSumByCard += groupItem.getTotalOrderSumByCard();
                totalOrderSumByCash += groupItem.getTotalOrderSumByCash();
                totalSocDiscount += groupItem.getTotalSocDiscount();
                totalTrdDiscount += groupItem.getTotalTrdDiscount();
                totalGrantSum += groupItem.getTotalGrantSum();
                clientGroupItems.add(groupItem);
            }

            Collections.sort(clientGroupItems);

            return new GroupedClientsData(totalOrderSumByCard, totalOrderSumByCash, totalSocDiscount, totalTrdDiscount, totalGrantSum,
                    clientGroupItems);
        }

        private static ClientGroupItem buildUnGroupedClientsData(Session session, Date startTime, Date endTime, Org org,
                Boolean hideEmptyClients) throws Exception {
            String sql =
                    "SELECT c.contractid, p.surname, p.firstname, p.secondname, "
                  + "   CASE WHEN sum(o.sumbycard) IS NULL THEN 0 ELSE sum(o.sumbycard) END AS sumbycard, "
                  + "   CASE WHEN sum(o.sumbycash) IS NULL THEN 0 ELSE sum(o.sumbycash) END AS sumbycash, "
                  + "   CASE WHEN sum(o.socdiscount) IS NULL THEN 0 ELSE sum(o.socdiscount) END AS socdisocunt, "
                  + "   CASE WHEN sum(o.trddiscount) IS NULL THEN 0 ELSE sum(o.trddiscount) END AS trddiscount, "
                  + "   CASE WHEN sum(o.grantsum) IS NULL THEN 0 ELSE sum(o.grantsum) END AS grantsum "
                  + "FROM cf_clients c "
                  + "INNER JOIN cf_persons p ON p.idofperson=c.idofperson "
                  + "LEFT JOIN cf_orders o ON o.idofclient=c.idofclient AND o.state=0 AND o.createddate >= :startDate AND o.createddate < :endDate "
                  + "WHERE c.idoforg = :idOfOrg AND c.idofclientgroup IS NULL " + (hideEmptyClients ? "AND o.idoforder IS NOT NULL " : "")
                  + "GROUP BY c.contractid, p.surname, p.firstname, p.secondname "
                  + "ORDER BY p.surname, p.firstname, p.secondname;";

            Query sqlQuery = session.createSQLQuery(sql);
            sqlQuery.setParameter("idOfOrg", org.getIdOfOrg());
            sqlQuery.setParameter("startDate", startTime.getTime());
            sqlQuery.setParameter("endDate", endTime.getTime());

            List clients = sqlQuery.list();
            List<GroupedClientItem> clientItems = new ArrayList<GroupedClientItem>();

            for (Object o : clients) {
                Object[] vals = (Object[])o;

                GroupedClientItem groupedClientItem = new GroupedClientItem(((BigInteger)vals[0]).longValue(),
                        null, null, (String)vals[1], (String)vals[2],
                        (String)vals[3], ((BigDecimal)vals[4]).longValue(), ((BigDecimal)vals[5]).longValue(),
                        ((BigDecimal)vals[6]).longValue(), ((BigDecimal)vals[7]).longValue(),
                        ((BigDecimal)vals[8]).longValue());

                clientItems.add(groupedClientItem);
            }

            return buildClientGroupItem(clientItems);
        }

        private static ClientGroupItem buildClientGroupItem(List<GroupedClientItem> groupedClientItemList) throws Exception {
            List<ClientItem> clientItems = new LinkedList<ClientItem>();
            long totalOrderSumByCard = 0;
            long totalOrderSumByCash = 0;
            long totalSocDiscount = 0, totalTrdDiscount = 0;
            long totalGrantSum = 0;
            Long groupId = -1L;
            String groupName = "";
            for (GroupedClientItem item : groupedClientItemList) {
                ClientItem clientItem = buildClientItem(item);
                clientItems.add(clientItem);
                totalOrderSumByCard += clientItem.getTotalOrderSumByCard();
                totalOrderSumByCash += clientItem.getTotalOrderSumByCash();
                totalSocDiscount += clientItem.getTotalSocDiscount();
                totalTrdDiscount += clientItem.getTotalTrdDiscount();
                totalGrantSum += clientItem.getTotalGrantSum();
                if (groupId < 0 || groupName.isEmpty()) {
                    groupId = item.getIdOfGroup();
                    groupName = item.getGroupName();
                }
            }
            return new ClientGroupItem(groupId, groupName, totalOrderSumByCard, totalOrderSumByCash, totalSocDiscount, totalTrdDiscount,
                    totalGrantSum, clientItems);
        }

        private static ClientItem buildClientItem(GroupedClientItem item) throws Exception {
            return new ClientItem(item.getContractID(), item.getSurname(), item.getFirstName(), item.getSecondName(),
                    item.getSumByCard(), item.getSumByCash(), item.getSocDiscount(), item.getTrdDiscount(), item.getGrantSum());
        }

        private static TotalSums getTotalOrderSums(Session session, Date startTime, Date endTime, Client client)
                throws Exception {
            Query query = session.createQuery(
                    "select sum(sumByCard), sum(sumByCash), sum(socDiscount), sum(trdDiscount), sum(grantSum) from Order where state=0 and client = ? and createTime >= ? and createTime < ?");
            query.setParameter(0, client);
            query.setParameter(1, startTime);
            query.setParameter(2, endTime);
            Object[] result = (Object[]) query.uniqueResult();
            if (null == result) {
                return new TotalSums(0, 0, 0, 0, 0);
            } else {
                return new TotalSums(defaultValue((Long) result[0]), defaultValue((Long) result[1]),
                        defaultValue((Long) result[2]), defaultValue((Long) result[3]), defaultValue((Long) result[4]));
            }
        }

        private static long defaultValue(Long value) {
            if (null == value) {
                return 0L;
            }
            return value;
        }

        private static class GroupedClientItem {
            private Long contractID;
            private Long idOfGroup;
            private String groupName;
            private String surname;
            private String firstName;
            private String secondName;
            private Long sumByCard;
            private Long sumByCash;
            private Long socDiscount;
            private Long trdDiscount;
            private Long grantSum;

            private GroupedClientItem(Long contractID, Long idOfGroup, String groupName, String surname, String firstName, String secondName,
                    Long sumByCard, Long sumByCash, Long socDiscount, Long trdDiscount, Long grantSum) {
                this.contractID = contractID;
                this.idOfGroup = idOfGroup;
                this.groupName = groupName;
                this.surname = surname;
                this.firstName = firstName;
                this.secondName = secondName;
                this.sumByCard = sumByCard;
                this.sumByCash = sumByCash;
                this.socDiscount = socDiscount;
                this.trdDiscount = trdDiscount;
                this.grantSum = grantSum;
            }

            public Long getContractID() {
                return contractID;
            }

            public void setContractID(Long contractID) {
                this.contractID = contractID;
            }

            public Long getIdOfGroup() {
                return idOfGroup;
            }

            public void setIdOfGroup(Long idOfGroup) {
                this.idOfGroup = idOfGroup;
            }

            public String getGroupName() {
                return groupName;
            }

            public void setGroupName(String groupName) {
                this.groupName = groupName;
            }

            public String getSurname() {
                return surname;
            }

            public void setSurname(String surname) {
                this.surname = surname;
            }

            public String getFirstName() {
                return firstName;
            }

            public void setFirstName(String firstName) {
                this.firstName = firstName;
            }

            public String getSecondName() {
                return secondName;
            }

            public void setSecondName(String secondName) {
                this.secondName = secondName;
            }

            public Long getSumByCard() {
                return sumByCard;
            }

            public void setSumByCard(Long sumByCard) {
                this.sumByCard = sumByCard;
            }

            public Long getSumByCash() {
                return sumByCash;
            }

            public void setSumByCash(Long sumByCash) {
                this.sumByCash = sumByCash;
            }

            public Long getSocDiscount() {
                return socDiscount;
            }

            public void setSocDiscount(Long socDiscount) {
                this.socDiscount = socDiscount;
            }

            public Long getTrdDiscount() {
                return trdDiscount;
            }

            public void setTrdDiscount(Long trdDiscount) {
                this.trdDiscount = trdDiscount;
            }

            public Long getGrantSum() {
                return grantSum;
            }

            public void setGrantSum(Long grantSum) {
                this.grantSum = grantSum;
            }
        }

    }

    private final Date startTime;
    private final Date endTime;
    private final OrgItem org;

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public OrgItem getOrg() {
        return org;
    }

    public OrgOrderReport(Date generateTime, long generateDuration, Date startTime, Date endTime, OrgItem org) {
        super(generateTime, generateDuration);
        this.startTime = startTime;
        this.endTime = endTime;
        this.org = org;
    }

    public OrgOrderReport(Date startTime, Date endTime) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.org = new OrgItem();
    }

    @Override
    public String toString() {
        return "OrgOrderReport{" + "startTime=" + startTime + ", endTime=" + endTime + ", org=" + org + "} " + super
                .toString();
    }
}