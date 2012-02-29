/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientListPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }
    }

    public static class PersonItem {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

        public PersonItem(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }

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
    }

    public static class Item {

        private final Long idOfClient;
        private OrgItem org;
        private final PersonItem person;
        private final PersonItem contractPerson;
        private final Integer flags;
        private final Long contractId;
        private final Date contractTime;
        private final Integer contractState;
        private final Date updateTime;
        private final String address;
        private final String phone;
        private final String mobile;
        private final String email;
        private final Integer payForSMS;
        private final boolean notifyViaEmail;
        private final boolean notifyViaSMS;
        private final long cardCount;
        private final String clientGroupName;
        private final Long balance;
        private Long limit;
        private Long expenditureLimit;
        private final Integer discountMode;

        public void setExpenditureLimit(Long expenditureLimit) {
            this.expenditureLimit = expenditureLimit;
        }

        public void setOrg(OrgItem org) {
            this.org = org;
        }

        public void setLimit(Long limit) {
            this.limit = limit;
        }

        public Long getBalance() {
            return balance;
        }

        public Long getLimit() {
            return limit;
        }

        public Long getExpenditureLimit() {
            return expenditureLimit;
        }

        public Integer getDiscountMode() {
            return discountMode;
        }

        public Item(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.org = new OrgItem(client.getOrg());
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.flags = client.getFlags();
            this.contractId = client.getContractId();
            this.contractTime = client.getContractTime();
            this.contractState = client.getContractState();
            this.updateTime = client.getUpdateTime();
            this.address = client.getAddress();
            this.phone = client.getPhone();
            this.mobile = client.getMobile();
            this.email = client.getEmail();
            this.payForSMS = client.getPayForSMS();
            this.notifyViaSMS = client.isNotifyViaSMS();
            this.notifyViaEmail = client.isNotifyViaEmail();
            this.cardCount = client.getCards().size();
            this.balance = client.getBalance();
            this.limit = client.getLimit();
            this.expenditureLimit = client.getExpenditureLimit();
            this.discountMode = client.getDiscountMode();
            ClientGroup clientGroup = client.getClientGroup();
            if (null == clientGroup) {
                this.clientGroupName = null;
            } else {
                this.clientGroupName = clientGroup.getGroupName();
            }
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public OrgItem getOrg() {
            return org;
        }

        public PersonItem getPerson() {
            return person;
        }

        public PersonItem getContractPerson() {
            return contractPerson;
        }

        public Integer getFlags() {
            return flags;
        }

        public Long getContractId() {
            return contractId;
        }

        public Date getContractTime() {
            return contractTime;
        }

        public Integer getContractState() {
            return contractState;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public String getAddress() {
            return address;
        }

        public String getPhone() {
            return phone;
        }

        public String getMobile() {
            return mobile;
        }

        public String getEmail() {
            return email;
        }

        public Integer getPayForSMS() {
            return payForSMS;
        }

        public boolean isNotifyViaEmail() {
            return notifyViaEmail;
        }

        public boolean isNotifyViaSMS() {
            return notifyViaSMS;
        }

        public long getCardCount() {
            return cardCount;
        }

        public String getClientGroupName() {
            return clientGroupName;
        }
    }

    private List<Item> items = Collections.emptyList();
    private final ClientFilter clientFilter = new ClientFilter();
    private Long limit = 0L;
    private Long expenditureLimit = 0L;

    public Long getExpenditureLimit() {
        return expenditureLimit;
    }

    public void setExpenditureLimit(Long expenditureLimit) {
        this.expenditureLimit = expenditureLimit;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public String getPageFilename() {
        return "client/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.clientFilter.completeOrgSelection(session, idOfOrg);
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!clientFilter.isEmpty()) {
            List clients = clientFilter.retrieveClients(session);
            for (Object object : clients) {
                Client client = (Client) object;
                items.add(new Item(client));
            }
        }
        this.items = items;
    }

    public void removeClientFromList(Long clientId) {
        for (Item item : this.getItems()) {
            if (item.getIdOfClient().equals(clientId)) {
                this.getItems().remove(item);
                break;
            }
        }
    }

    /**
     * Установить лимит овердрафта для выбранного списка клиентов
     * @param session
     * @throws Exception
     */
    public void setLimit(Session session) throws Exception {
        if (this.items.isEmpty())
            return;
        // создаем множество id клиентов
        List<Long> clientsId = new ArrayList<Long>();
        for (Item item : this.items) {
            clientsId.add(item.getIdOfClient());
        }
        org.hibernate.Query q = session.createQuery("update Client set limit = :newLimit where idOfClient in :clientsId");
        q.setLong("newLimit", limit);
        q.setParameterList("clientsId", clientsId);
        if (q.executeUpdate() != clientsId.size())
            throw new Exception("Ошибка при установлении лимита овердрафта.");
        for (Item item : this.getItems()) {
            item.setLimit(limit);
        }
    }


    /**
     * Устанавливаем организацию для выбранного списка клиентов
     * @param session
     */
    public void setOrg(Session session) throws Exception {
        if (this.items.isEmpty())
            return;
        Org org = null;
        OrgItem orgItem = null;
        if (this.getClientFilter().getOrg().getIdOfOrg() != null) {
            org = (Org) session.load(Org.class, this.getClientFilter().getOrg().getIdOfOrg());
        }
        if (org == null)
            return;
        else
            orgItem = new OrgItem(org);
        // создаем множество id клиентов
        Set<Long> clientsId = new HashSet<Long>();
        for (Item item : this.items) {
            clientsId.add(item.getIdOfClient());
        }
        org.hibernate.Query q = session.createQuery("update Client set org.idOfOrg = :newOrg where idOfClient in :clientsId");
        q.setLong("newOrg", org.getIdOfOrg());
        q.setParameterList("clientsId", clientsId);
        if (q.executeUpdate() != clientsId.size())
            throw new Exception("Ошибка при установлении лимита овердрафта.");
        // устанавливаем лорганизацию в бинах страницы
        for (Item item : this.getItems()) {
            item.setOrg(orgItem);
        }
    }

    public void setExpenditureLimit(Session session) throws Exception {
        if (this.items.isEmpty())
            return;
        List<Long> clientsId = new ArrayList<Long>();
        for (Item item : this.items) {
            clientsId.add(item.getIdOfClient());
        }
        org.hibernate.Query q = session.createQuery("update Client set expenditureLimit = :newExpenditureLimit where idOfClient in :clientsId");
        q.setLong("newExpenditureLimit", expenditureLimit);
        q.setParameterList("clientsId", clientsId);
        if (q.executeUpdate() != clientsId.size())
            throw new Exception("Ошибка при установлении лимита дневныъ трат овердрафта.");
        for (Item item : this.getItems()) {
            item.setExpenditureLimit(expenditureLimit);
        }
    }



}