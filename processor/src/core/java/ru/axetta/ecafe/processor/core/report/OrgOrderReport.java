/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.10.2009
 * Time: 14:09:39
 * To change this template use File | Settings | File Templates.
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

        public PersonItem(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }
    }

    public static class ClientItem {

        private final Long contractId;
        private final PersonItem person;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalDiscount;
        private final long totalGrantSum;

        public Long getContractId() {
            return contractId;
        }

        public PersonItem getPerson() {
            return person;
        }

        public long getTotalDiscount() {
            return totalDiscount;
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

        public ClientItem(Client client, long totalOrderSumByCard, long totalOrderSumByCash, long totalDiscount,
                long totalGrantSum) {
            this.contractId = client.getContractId();
            this.person = new PersonItem(client.getPerson());
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalDiscount = totalDiscount;
            this.totalGrantSum = totalGrantSum;
        }
    }

    public static class ClientGroupItem {

        private final Long idOfClientGroup;
        private final String groupName;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalDiscount;
        private final long totalGrantSum;
        private final List<ClientItem> clients;

        public Long getIdOfClientGroup() {
            return idOfClientGroup;
        }

        public String getGroupName() {
            return groupName;
        }

        public long getTotalDiscount() {
            return totalDiscount;
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

        public ClientGroupItem(ClientGroup clientGroup, long totalOrderSumByCard, long totalOrderSumByCash,
                long totalDiscount, long totalGrantSum, List<ClientItem> clients) {
            if (null == clientGroup) {
                this.idOfClientGroup = null;
                this.groupName = null;
            } else {
                this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
                this.groupName = clientGroup.getGroupName();
            }
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalDiscount = totalDiscount;
            this.totalGrantSum = totalGrantSum;
            this.clients = clients;
        }

        public ClientGroupItem() {
            this.idOfClientGroup = null;
            this.groupName = null;
            this.totalOrderSumByCard = 0;
            this.totalOrderSumByCash = 0;
            this.totalDiscount = 0;
            this.totalGrantSum = 0;
            this.clients = Collections.emptyList();
        }
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;
        private final long totalOrderSumByCard;
        private final long totalOrderSumByCash;
        private final long totalDiscount;
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

        public long getTotalDiscount() {
            return totalDiscount;
        }

        public long getTotalGrantSum() {
            return totalGrantSum;
        }

        public List<ClientGroupItem> getClientGroups() {
            return clientGroups;
        }

        public OrgItem(Org org, long totalOrderSumByCard, long totalOrderSumByCash, long totalDiscount,
                long totalGrantSum, List<ClientGroupItem> clientGroups) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
            this.totalOrderSumByCard = totalOrderSumByCard;
            this.totalOrderSumByCash = totalOrderSumByCash;
            this.totalDiscount = totalDiscount;
            this.totalGrantSum = totalGrantSum;
            this.clientGroups = clientGroups;
        }

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
            this.totalOrderSumByCard = 0;
            this.totalOrderSumByCash = 0;
            this.totalDiscount = 0;
            this.totalGrantSum = 0;
            this.clientGroups = Collections.emptyList();
        }

        @Override
        public String toString() {
            return "OrgItem{" + "idOfOrg=" + idOfOrg + ", shortName='" + shortName + '\'' + ", officialName='"
                    + officialName + '\'' + ", totalOrderSumByCard=" + totalOrderSumByCard + ", totalOrderSumByCash="
                    + totalOrderSumByCash + ", totalDiscount=" + totalDiscount + ", totalGrantSum=" + totalGrantSum
                    + '}';
        }
    }

    public static class Builder {

        private static class GrouppedClientsData {

            private final long totalOrderSumByCard;
            private final long totalOrderSumByCash;
            private final long totalDiscount;
            private final long totalGrantSum;
            private final List<ClientGroupItem> clientGroupItems;

            private GrouppedClientsData(long totalOrderSumByCard, long totalOrderSumByCash, long totalDiscount,
                    long totalGrantSum, List<ClientGroupItem> clientGroupItems) {
                this.totalOrderSumByCard = totalOrderSumByCard;
                this.totalOrderSumByCash = totalOrderSumByCash;
                this.totalDiscount = totalDiscount;
                this.totalGrantSum = totalGrantSum;
                this.clientGroupItems = clientGroupItems;
            }

            public long getTotalOrderSumByCard() {
                return totalOrderSumByCard;
            }

            public long getTotalOrderSumByCash() {
                return totalOrderSumByCash;
            }

            public long getTotalDiscount() {
                return totalDiscount;
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
            private final long discount;
            private final long totalGrantSum;

            private TotalSums(long sumByCard, long sumByCash, long discount, long totalGrantSum) {
                this.sumByCard = sumByCard;
                this.sumByCash = sumByCash;
                this.discount = discount;
                this.totalGrantSum = totalGrantSum;
            }

            public long getSumByCard() {
                return sumByCard;
            }

            public long getSumByCash() {
                return sumByCash;
            }

            public long getDiscount() {
                return discount;
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

                if (discount != totalSums.discount) {
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
                result = 31 * result + (int) (discount ^ (discount >>> 32));
                result = 31 * result + (int) (totalGrantSum ^ (totalGrantSum >>> 32));
                return result;
            }
        }

        public OrgOrderReport build(Session session, Date startTime, Date endTime, Org org) throws Exception {
            Date generateTime = new Date();
            GrouppedClientsData grouppedClientsData = buildGrouppedClientsData(session, startTime, endTime, org);
            ClientGroupItem unGrouppedClientsData = buildUnGrouppedClientsData(session, startTime, endTime, org);
            List<ClientGroupItem> clientGroupItems = grouppedClientsData.getClientGroupItems();
            clientGroupItems.add(unGrouppedClientsData);
            return new OrgOrderReport(generateTime, new Date().getTime() - generateTime.getTime(), startTime, endTime,
                    new OrgItem(org, grouppedClientsData.getTotalOrderSumByCard() + unGrouppedClientsData
                            .getTotalOrderSumByCard(),
                            grouppedClientsData.getTotalOrderSumByCash() + unGrouppedClientsData
                                    .getTotalOrderSumByCash(),
                            grouppedClientsData.getTotalDiscount() + unGrouppedClientsData.getTotalDiscount(),
                            grouppedClientsData.getTotalGrantSum() + unGrouppedClientsData.getTotalGrantSum(),
                            clientGroupItems));
        }

        private static GrouppedClientsData buildGrouppedClientsData(Session session, Date startTime, Date endTime,
                Org org) throws Exception {
            Criteria clientGroupsCriteria = session.createCriteria(ClientGroup.class);
            clientGroupsCriteria.add(Restrictions.eq("org", org)).setFetchMode("org", FetchMode.JOIN);
            HibernateUtils.addAscOrder(clientGroupsCriteria, "groupName");
            List<ClientGroupItem> clientGroupItems = new LinkedList<ClientGroupItem>();
            List clientGroups = clientGroupsCriteria.list();
            long totalOrderSumByCard = 0;
            long totalOrderSumByCash = 0;
            long totalDiscount = 0;
            long totalGrantSum = 0;
            for (Object currObject : clientGroups) {
                ClientGroup currClientGroup = (ClientGroup) currObject;
                Criteria clientsCriteria = session.createCriteria(Client.class);
                clientsCriteria.add(Restrictions.eq("clientGroup", currClientGroup));
                clientsCriteria = clientsCriteria.createCriteria("person");
                HibernateUtils.addAscOrder(clientsCriteria, "surname");
                HibernateUtils.addAscOrder(clientsCriteria, "firstName");
                HibernateUtils.addAscOrder(clientsCriteria, "secondName");
                ClientGroupItem clientGroupItem = buildClientGroupItem(currClientGroup, session, startTime, endTime,
                        clientsCriteria.list());
                totalOrderSumByCard += clientGroupItem.getTotalOrderSumByCard();
                totalOrderSumByCash += clientGroupItem.getTotalOrderSumByCash();
                totalDiscount += clientGroupItem.getTotalDiscount();
                totalGrantSum += clientGroupItem.getTotalGrantSum();
                clientGroupItems.add(clientGroupItem);
            }
            return new GrouppedClientsData(totalOrderSumByCard, totalOrderSumByCash, totalDiscount, totalGrantSum,
                    clientGroupItems);
        }

        private static ClientGroupItem buildUnGrouppedClientsData(Session session, Date startTime, Date endTime,
                Org org) throws Exception {
            Criteria clientsCriteria = session.createCriteria(Client.class);
            clientsCriteria.add(Restrictions.eq("org", org)).setFetchMode("org", FetchMode.JOIN);
            clientsCriteria.add(Restrictions.isNull("idOfClientGroup"));
            clientsCriteria = clientsCriteria.createCriteria("person");
            HibernateUtils.addAscOrder(clientsCriteria, "surname");
            HibernateUtils.addAscOrder(clientsCriteria, "firstName");
            HibernateUtils.addAscOrder(clientsCriteria, "secondName");
            return buildClientGroupItem(null, session, startTime, endTime, clientsCriteria.list());
        }

        private static ClientGroupItem buildClientGroupItem(ClientGroup clientGroup, Session session, Date startTime,
                Date endTime, Collection clients) throws Exception {
            List<ClientItem> clientItems = new LinkedList<ClientItem>();
            long totalOrderSumByCard = 0;
            long totalOrderSumByCash = 0;
            long totalDiscount = 0;
            long totalGrantSum = 0;
            for (Object currObject : clients) {
                Client currClient = (Client) currObject;
                ClientItem clientItem = buildClientItem(session, startTime, endTime, currClient);
                clientItems.add(clientItem);
                totalOrderSumByCard += clientItem.getTotalOrderSumByCard();
                totalOrderSumByCash += clientItem.getTotalOrderSumByCash();
                totalDiscount += clientItem.getTotalDiscount();
                totalGrantSum += clientItem.getTotalGrantSum();
            }
            return new ClientGroupItem(clientGroup, totalOrderSumByCard, totalOrderSumByCash, totalDiscount,
                    totalGrantSum, clientItems);
        }

        private static ClientItem buildClientItem(Session session, Date startTime, Date endTime, Client client)
                throws Exception {
            TotalSums totalSums = getTotalOrderSums(session, startTime, endTime, client);
            return new ClientItem(client, totalSums.getSumByCard(), totalSums.getSumByCash(), totalSums.getDiscount(),
                    totalSums.getTotalGrantSum());
        }

        private static TotalSums getTotalOrderSums(Session session, Date startTime, Date endTime, Client client)
                throws Exception {
            Query query = session.createQuery(
                    "select sum(sumByCard), sum(sumByCash), sum(discount), sum(grantSum) from Order where client = ? and createTime >= ? and createTime < ?");
            query.setParameter(0, client);
            query.setParameter(1, startTime);
            query.setParameter(2, endTime);
            Object[] result = (Object[]) query.uniqueResult();
            if (null == result) {
                return new TotalSums(0, 0, 0, 0);
            } else {
                return new TotalSums(defaultValue((Long) result[0]), defaultValue((Long) result[1]),
                        defaultValue((Long) result[2]), defaultValue((Long) result[3]));
            }
        }

        private static long defaultValue(Long value) {
            if (null == value) {
                return 0L;
            }
            return value;
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