/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 10:20:25
 * To change this template use File | Settings | File Templates.
 */
public class ClientSelectListPage extends BasicPage implements OrgSelectPage.CompleteHandler {

    private final Logger logger = LoggerFactory.getLogger(ClientSelectListPage.class);

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public interface CompleteHandler {
        void completeClientSelection(Session session, List<Item> items) throws Exception;
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

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

        public String getInitialsCaption() {
            StringBuilder stringBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(firstName)) {
                stringBuilder.append(firstName);
            }
            if (StringUtils.isNotEmpty(secondName)) {
                if (0 != stringBuilder.length()) {
                    stringBuilder.append(' ');
                }
                stringBuilder.append(secondName.charAt(0));
                stringBuilder.append('.');
            }
            if (StringUtils.isNotEmpty(surname)) {
                stringBuilder.append(surname.charAt(0));
                stringBuilder.append('.');
            }
            return stringBuilder.toString();
        }
    }

    public static class Item {

        private Long idOfClient;
        private OrgItem org;
        private PersonItem person;
        private PersonItem contractPerson;
        private Integer flags;
        private Long contractId;
        private Date contractTime;
        private Integer contractState;
        private Date updateTime;
        private Boolean selected;

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

        public PersonItem getContractPerson() {
            return contractPerson;
        }

        public void setContractPerson(PersonItem contractPerson) {
            this.contractPerson = contractPerson;
        }

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

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public Item(Client client, Boolean selected) {
            this.idOfClient = client.getIdOfClient();
            this.org = new OrgItem(client.getOrg());
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.flags = client.getFlags();
            this.contractId = client.getContractId();
            this.contractTime = client.getContractTime();
            this.contractState = client.getContractState();
            this.updateTime = client.getUpdateTime();
            this.selected = selected;
        }

        public Item() {
            this.idOfClient = null;
            this.org = new OrgItem();
            this.person = new PersonItem();
            this.contractPerson = new PersonItem();
            this.flags = null;
            this.contractId = null;
            this.contractTime = null;
            this.contractState = null;
            this.updateTime = null;
            this.selected = false;
        }

        static Item clone(Item it) {
            Item item = new Item();
            item.idOfClient = it.idOfClient;
            item.org = it.org;
            item.person = it.person;
            item.contractPerson = it.contractPerson;
            item.flags = it.flags;
            item.contractId = it.contractId;
            item.contractTime = it.contractTime;
            item.contractState = it.contractState;
            item.updateTime = it.updateTime;
            item.selected = false;
            return item;
        }

        public String getCaption() {
            if (null == idOfClient) {
                return "";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ContractIdFormat.format(contractId)).append(" (").append(contractPerson.getCaption())
                    .append("): ").append(person.getCaption());
            return stringBuilder.toString();
        }

        public String getInitialsCaption() {
            if (null == idOfClient) {
                return "";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ContractIdFormat.format(contractId)).append(" (").append(contractPerson.getCaption())
                    .append("): ").append(person.getInitialsCaption());
            return stringBuilder.toString();
        }
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Item)) {
                return false;
            }
            final Item that = (Item) o;
            if (!idOfClient.equals(that.getIdOfClient())) {
                return false;
            }
            return true;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private List<Item> selectedItems = new ArrayList<Item>(); // Collections.emptyList();
    private final ClientFilter clientFilter = new ClientFilter();
    private Integer limit = 10;
    private Integer offset = 0;
    private final int OFFSET = 10;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeClientSelection(Session session) throws Exception {
        selectedItems = getSelectedItems();
        while (!completeHandlers.empty()) {
            completeHandlers.peek().completeClientSelection(session, selectedItems);
            completeHandlers.pop();
        }
        //clientFilter.setPermanentOrgId(null);
        //clientFilter.setOrg(new ClientFilter.OrgItem());
        removeAllFromSelected();
    }

    public void cancelButtonClick() {
        removeAllFromSelected();
        items.clear();
        clientFilter.clear();
        clientFilter.setPermanentOrgId(null);
        clientFilter.setOrg(new ClientFilter.OrgItem());
        /*for (Item item : selectedItems) {
            item.setSelected(true);
        }*/
    }

    public List<Item> getItems() {
        return items;
    }

    public Boolean isItemsEmpty() {
        return items.size() == 0;
    }

    public Boolean isSelectedItemsEmpty() {
        return selectedItems.size() == 0;
    }

    public List<Item> getSelectedItems() {
        return selectedItems;
        /*List<Item> selItems = new ArrayList<Item>();
        for (Item it : items) {
            if (it != null && it.getSelected()) {
                selItems.add(it);
            }
        }
        return selItems;*/
    }

    /*public void addToSelected() {
        //selectedItems.clear();
        List<Item> its = new ArrayList<Item>();
        its.addAll(selectedItems);
        for (Item it : items) {
            if (it != null && it.getSelected() && !testItemSelectedExists(it)) {
                its.add(Item.clone(it));
            }
        }
        selectedItems = its;
    }*/

    private boolean testItemSelectedExists(Item item) {
        for (Item it : selectedItems) {
            if (it.getIdOfClient() == item.getIdOfClient()) {
                return true;
            }
        }
        return false;
    }

    /*public void removeFromSelected() {
        List<Item> its = new ArrayList<Item>();
        for (Item item : selectedItems) {
            if (!item.getSelected()) {
                its.add(item);
            }
        }
        selectedItems = its;
    }*/

    public void removeFromSelectedOne(Item item) {
        //item.setSelected(false);
        for(Item it : items) {
            if (item.getIdOfClient().equals(it.getIdOfClient())) {
                it.setSelected(false);
                break;
            }
        }
        selectedItems.remove(item);
    }

    public void removeAllFromSelected() {
        for(Item item: items) {
            item.setSelected(false);
        }
        selectedItems.clear();
    }

    public void addToSelectedOne(Item item) {
        selectedItems.add(item);
        item.setSelected(true);
    }

    public void addAllToSelected() {
        for(Item item : items) {
            if (selectedItems.contains(item)) continue;
            selectedItems.add(item);
            item.setSelected(true);
        }
    }

    public ClientFilter getClientFilter() {
        return clientFilter;
    }

    public void clearClientFilter() {
        clientFilter.clear();
        items.clear();
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.clientFilter.completeOrgSelection(session, idOfOrg);
        this.clientFilter.setIncludeFriendlyOrg(false);
    }

    public void pageBack() {
        offset -= OFFSET;
        clientFilter.setOffset(offset);
        updateClientSelectListPage();
    }

    public void pageForward() {
        offset += OFFSET;
        clientFilter.setOffset(offset);
        updateClientSelectListPage();
    }

    public void resetLimitOffset() {
        clientFilter.setLimit(limit);
        offset = 0;
        clientFilter.setOffset(offset);
    }

    public Boolean showPager() {
        return items.size() > 0;
    }

    public Boolean pageBackEnabled() {
        return offset > 0;
    }

    public Boolean pageForwardEnabled() {
        return items.size() == OFFSET;
    }

    private Object fillTable() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            fill(persistenceSession, selectedItems);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to fill client selection page", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при подготовке страницы выбора клиента: " + e.getMessage(), null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }

    public Object updateClientSelectListPage() {
        return fillTable();
    }

    public Object apply() {
        resetLimitOffset();
        return fillTable();
    }

    public void fill(Session session, List<Item> clientList) throws Exception {

        if (!clientFilter.isEmpty()) { // && this.items.isEmpty()) {
            List<Item> items = new LinkedList<Item>();
            List clients = clientFilter.retrieveClients(session);
            for (Object object : clients) {
                Client client = (Client) object;
                Item item = new Item(client, isItemSelected(client, clientList));
                items.add(item);
            }
            this.items = items;
        }
        if (clientList != null && clientList.size() != selectedItems.size()) {
            selectedItems.clear();
            for (Item item : clientList) {
                selectedItems.add(item);
            }
        }
    }

    public void updatePermanentOrg(Session session, Long idOfOrg) {
        if (null == idOfOrg)
            return;

        if (!idOfOrg.equals(clientFilter.getPermanentOrgId()) ||
                (null != clientFilter.getOrg() && clientFilter.getOrg().getIdOfOrg().equals(idOfOrg))) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            if (null != org) {
                clientFilter.setOrg(new ClientFilter.OrgItem(org));
                clientFilter.setPermanentOrgId(idOfOrg);
                removeAllFromSelected();
            }
        }
    }

    private boolean isItemSelected(Client client, List<Item> clientList) {
        if (clientList == null) {
            return false;
        }
        for (Item it : clientList) {
            if (it.getIdOfClient().equals(client.getIdOfClient())) {
                return true;
            }
        }
        return false;
    }
}