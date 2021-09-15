/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 10:20:25
 * To change this template use File | Settings | File Templates.
 */
public class ClientSelectPage extends BasicPage implements OrgSelectPage.CompleteHandler {

    public interface CompleteHandler {

        void completeClientSelection(Session session, Long idOfClient) throws Exception;
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String shortNameInfoService;
        private final String officialName;

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            shortNameInfoService = null;
            this.officialName = null;
        }

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.shortNameInfoService = org.getShortNameInfoService();
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

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }
    }

    public static class PersonItem {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

        public PersonItem() {
            this.firstName = null;
            this.surname = null;
            this.secondName = null;
            this.idDocument = null;
        }

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

        public String getCaption() {
            StringBuilder stringBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(surname)) {
                stringBuilder.append(surname);
            }
            if (StringUtils.isNotEmpty(firstName)) {
                if (0 != stringBuilder.length()) {
                    stringBuilder.append(' ');
                }
                stringBuilder.append(firstName);
            }
            if (StringUtils.isNotEmpty(secondName)) {
                if (0 != stringBuilder.length()) {
                    stringBuilder.append(' ');
                }
                stringBuilder.append(secondName);
            }
            return stringBuilder.toString();
        }
    }

    public static class Item {

        private Long idOfClient;
        private OrgItem org;
        private PersonItem person;
        //private PersonItem contractPerson;
        private Integer flags;
        private Long contractId;
        private Date contractTime;
        private Integer contractState;
        private Date updateTime;
        private Long balance;
        private String groupName;

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public OrgItem getOrg() {
            return org;
        }

        public void setOrg(OrgItem org) {
            this.org = org;
        }

        public PersonItem getPerson() {
            return person;
        }

        public void setPerson(PersonItem person) {
            this.person = person;
        }

        /*public PersonItem getContractPerson() {
            return contractPerson;
        }

        public void setContractPerson(PersonItem contractPerson) {
            this.contractPerson = contractPerson;
        }*/

        public Integer getFlags() {
            return flags;
        }

        public void setFlags(Integer flags) {
            this.flags = flags;
        }

        public Long getContractId() {
            return contractId;
        }

        public void setContractId(Long contractId) {
            this.contractId = contractId;
        }

        public Date getContractTime() {
            return contractTime;
        }

        public void setContractTime(Date contractTime) {
            this.contractTime = contractTime;
        }

        public Integer getContractState() {
            return contractState;
        }

        public void setContractState(Integer contractState) {
            this.contractState = contractState;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public Item(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.org = new OrgItem(client.getOrg());
            this.person = new PersonItem(client.getPerson());
            //this.contractPerson = new PersonItem(client.getContractPerson());
            this.flags = client.getFlags();
            this.contractId = client.getContractId();
            this.contractTime = client.getContractTime();
            this.contractState = client.getContractState();
            this.updateTime = client.getUpdateTime();
            this.balance = client.getBalance();
            this.groupName = client.getClientGroup() == null ? "" : client.getClientGroup().getGroupName();
        }

        public Item() {
            this.idOfClient = null;
            this.org = new OrgItem();
            this.person = new PersonItem();
            //this.contractPerson = new PersonItem();
            this.flags = null;
            this.contractId = null;
            this.contractTime = null;
            this.contractState = null;
            this.updateTime = null;
            this.balance = null;
            this.groupName = "";
        }

        public String getCaption() {
            if (null == idOfClient) {
                return "";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ContractIdFormat.format(contractId)).append(" : ").append(person.getCaption());
            return stringBuilder.toString();
        }

        public Long getBalance() {
            return balance;
        }

        public void setBalance(Long balance) {
            this.balance = balance;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private Item selectedItem = new Item();
    private final ClientFilter clientFilter = new ClientFilter();

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeClientSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeClientSelection(session, selectedItem.getIdOfClient());
            completeHandlers.pop();
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selected) {
        if (null == selected) {
            this.selectedItem = new Item();
        } else {
            this.selectedItem = selected;
        }
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
        this.selectedItem = new Item();
    }

    public void fill(Session session, Long idOfClient) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!clientFilter.isEmpty()) {
            List clients = clientFilter.retrieveClients(session);
            for (Object object : clients) {
                Client client = (Client) object;
                items.add(new Item(client));
            }
        }
        Item selectedItem = new Item();
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            selectedItem = new Item(client);
        }
        this.items = items;
        this.selectedItem = selectedItem;
    }

}