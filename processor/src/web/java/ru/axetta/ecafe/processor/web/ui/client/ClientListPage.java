/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;

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
        private boolean notifyViaSMS;
        private final String clientGroupName;
        private final Long balance;
        private Long limit;
        private Long expenditureLimit;
        private final Integer discountMode;
        private final String discountAsString;
        private final String guid;

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

        public String getDiscountAsString() {
            return discountAsString;
        }

        public Item(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.guid = client.getClientGUID();
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
            if (null == discountMode) {
                this.discountAsString = Client.DISCOUNT_MODE_NAMES[0];
            } else {
                this.discountAsString = Client.DISCOUNT_MODE_NAMES[discountMode];
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

        public String getClientGroupName() {
            return clientGroupName;
        }


        public String getIdOfClientString() {
            return this.getIdOfClient().toString();
        }

        public void setNotifyViaSMS(boolean notifyViaSMS) {
            this.notifyViaSMS = notifyViaSMS;
        }

        public String getGuid() {
            return guid;
        }
    }

    private List<Item> items = Collections.emptyList();
    private final ClientFilter clientFilter = new ClientFilter();
    private Long limit = 0L;
    private Long expenditureLimit = 0L;
    private boolean notifyViaSMS = false;


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

    public boolean isNotifyViaSMS() {
        return notifyViaSMS;
    }

    public void setNotifyViaSMS(boolean notifyViaSMS) {
        this.notifyViaSMS = notifyViaSMS;
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

    /**
     * удаление клиента из списка
     * @param clientId
     */
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
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
        org.hibernate.Query q = session.createQuery("update Client set limit = :newLimit, clientRegistryVersion=:clientRegistryVersion where idOfClient in :clientsId");
        q.setLong("newLimit", limit);
        q.setLong("clientRegistryVersion", clientRegistryVersion);
        q.setParameterList("clientsId", clientsId);
        if (q.executeUpdate() != clientsId.size())
            throw new Exception("Ошибка при установлении лимита овердрафта.");
        for (Item item : this.getItems()) {
            item.setLimit(limit);
        }
        printMessage("Данные обновлены.");
    }


    /**
     * Устанавливаем организацию для выбранного списка клиентов
     * @param session
     * @throws Exception
     */
    public void setNewOrg(Session session) throws Exception {
        if (this.items.isEmpty())
            return;
        Org org = null;
        OrgItem orgItem = null;
        if (this.getClientFilter().getOrg().getIdOfOrg() != null) {
            org = (Org) session.load(Org.class, this.getClientFilter().getOrg().getIdOfOrg());
        }
        if (org == null)
            return;
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
        for (Item item : this.items) {
            //clientsId.add(item.getIdOfClient());
            ClientGroup clientGroup = null;
            org.hibernate.Query q;
            if (item.getClientGroupName()!=null) {
                DAOUtils.findClientGroupByGroupNameAndIdOfOrg(session, org.getIdOfOrg(), item.getClientGroupName());
                if (clientGroup == null) clientGroup = DAOUtils.createNewClientGroup(session, org.getIdOfOrg(), item.getClientGroupName());
                q = session.createQuery("update Client set org.idOfOrg = :newOrg, clientRegistryVersion=:clientRegistryVersion, idOfClientGroup=:idOfClientGroup where idOfClient=:idOfClient");
            } else {
                q = session.createQuery("update Client set org.idOfOrg = :newOrg, clientRegistryVersion=:clientRegistryVersion where idOfClient=:idOfClient");
            }
            q.setLong("newOrg", org.getIdOfOrg());
            if (clientGroup!=null) q.setLong("idOfClientGroup", clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            q.setLong("idOfClient", item.getIdOfClient());
            q.setLong("clientRegistryVersion", clientRegistryVersion);
            q.executeUpdate();
        }
        printMessage("Данные обновлены.");
    }

    public void setOrg(Session session) throws Exception{
        Org org = null;
        if (this.getClientFilter().getOrg().getIdOfOrg() != null) {
            org = (Org) session.load(Org.class, this.getClientFilter().getOrg().getIdOfOrg());
        }
        if (!(this.items.isEmpty() || org==null)){
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
            ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(session, org.getIdOfOrg(), ClientGroup.Predefined.CLIENT_DISPLACED.getNameOfGroup());
            if(clientGroup==null) clientGroup = DAOUtils.createClientGroup(session, org.getIdOfOrg(), ClientGroup.Predefined.CLIENT_DISPLACED);
            for (Item item : this.items) {
                org.hibernate.Query query = session.createQuery("update Client set org.idOfOrg = :newOrg, clientRegistryVersion=:clientRegistryVersion, idOfClientGroup=:idOfClientGroup where idOfClient=:idOfClient");
                query.setLong("newOrg", org.getIdOfOrg());
                query.setLong("idOfClientGroup", clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                query.setLong("idOfClient", item.getIdOfClient());
                query.setLong("clientRegistryVersion", clientRegistryVersion);
                query.executeUpdate();
            }
            printMessage("Данные обновлены.");
        }
    }

    /**
     * Установка лимита дневных трат
     * @param session
     * @throws Exception
     */
    public void setExpenditureLimit(Session session) throws Exception {
        if (this.items.isEmpty())
            return;
        List<Long> clientsId = new ArrayList<Long>();
        for (Item item : this.items) {
            clientsId.add(item.getIdOfClient());
        }
        org.hibernate.Query q = session.createQuery("update Client set expenditureLimit = :newExpenditureLimit, clientRegistryVersion=:clientRegistryVersion where idOfClient in :clientsId");
        long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
        q.setLong("clientRegistryVersion", clientRegistryVersion);
        q.setLong("newExpenditureLimit", expenditureLimit);
        q.setParameterList("clientsId", clientsId);
        if (q.executeUpdate() != clientsId.size())
            throw new Exception("Ошибка при установлении лимита дневных трат овердрафта.");
        for (Item item : this.getItems()) {
            item.setExpenditureLimit(expenditureLimit);
        }
        printMessage("Данные обновлены.");
    }

    public void setNotifyViaSMS(Session session) throws Exception {
        if (this.items.isEmpty())
            return;
        List<Long> clientsId = new ArrayList<Long>();
        for (Item item : this.items) {
            clientsId.add(item.getIdOfClient());
        }
        DAOUtils.changeClientGroupNotifyViaSMS(session, notifyViaSMS, clientsId);
        for (Item item : this.getItems()) {
            item.setNotifyViaSMS(notifyViaSMS);
        }
        printMessage("Данные обновлены.");
    }
}