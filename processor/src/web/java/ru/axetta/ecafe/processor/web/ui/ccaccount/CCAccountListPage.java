/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.ccaccount;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CCAccountListPage extends BasicWorkspacePage
        implements OrgSelectPage.CompleteHandler, ContragentSelectPage.CompleteHandler {

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

    public static class ClientItem {

        private final Long idOfClient;
        private final String orgShortName;
        private final PersonItem person;
        private final PersonItem contractPerson;
        private final Long contractId;
        private final Date contractTime;
        private final Integer contractState;

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.orgShortName = client.getOrg().getShortName();
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.contractId = client.getContractId();
            this.contractTime = client.getContractTime();
            this.contractState = client.getContractState();
        }

        public String getShortName() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ContractIdFormat.format(contractId)).append(" (")
                    .append(AbbreviationUtils.buildAbbreviation(contractPerson.getFirstName(),
                            contractPerson.getSurname(), contractPerson.getSecondName())).append("): ")
                    .append(AbbreviationUtils.buildAbbreviation(person.getFirstName(), person.getSurname(),
                            person.getSecondName()));
            return stringBuilder.toString();
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public String getOrgShortName() {
            return orgShortName;
        }

        public PersonItem getPerson() {
            return person;
        }

        public PersonItem getContractPerson() {
            return contractPerson;
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
    }

    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    public static class Item {

        private final CompositeIdOfContragentClientAccount compositeIdOfContragentClientAccount;
        private final Long idOfAccount;
        private final ContragentItem contragent;
        private final ClientItem client;

        public Item(ContragentClientAccount contragentClientAccount) {
            this.compositeIdOfContragentClientAccount = contragentClientAccount
                    .getCompositeIdOfContragentClientAccount();
            this.idOfAccount = contragentClientAccount.getCompositeIdOfContragentClientAccount().getIdOfAccount();
            this.contragent = new ContragentItem(contragentClientAccount.getContragent());
            this.client = new ClientItem(contragentClientAccount.getClient());
        }

        public CompositeIdOfContragentClientAccount getCompositeIdOfContragentClientAccount() {
            return compositeIdOfContragentClientAccount;
        }

        public Long getIdOfAccount() {
            return idOfAccount;
        }

        public ContragentItem getContragent() {
            return contragent;
        }

        public ClientItem getClient() {
            return client;
        }
    }

    private List<Item> items = Collections.emptyList();
    private final CCAccountFilter filter = new CCAccountFilter();

    public String getPageFilename() {
        return "contragent/ccaccount/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public CCAccountFilter getFilter() {
        return filter;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        this.filter.completeContragentSelection(session, idOfContragent);
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.filter.completeOrgSelection(session, idOfOrg);
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        if (!filter.isEmpty()) {
            List accounts = filter.retrieveCCAccounts(session);
            for (Object object : accounts) {
                ContragentClientAccount account = (ContragentClientAccount) object;
                items.add(new Item(account));
            }
        }
        this.items = items;
    }

}