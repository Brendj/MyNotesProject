/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.ccaccount;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CCAccountCreatePage extends BasicWorkspacePage
        implements ClientSelectPage.CompleteHandler, ContragentSelectPage.CompleteHandler {

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

        public PersonItem() {
            this.firstName = null;
            this.surname = null;
            this.secondName = null;
            this.idDocument = null;
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

        public ClientItem() {
            this.idOfClient = null;
            this.orgShortName = null;
            this.person = new PersonItem();
            this.contractPerson = new PersonItem();
            this.contractId = null;
            this.contractTime = null;
            this.contractState = null;
        }

        public String getShortName() {
            if (null == this.idOfClient) {
                return "";
            }
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

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    private ContragentItem contragent = new ContragentItem();
    private ClientItem client = new ClientItem();
    private Long idOfAccount;

    public String getPageFilename() {
        return "contragent/ccaccount/create";
    }

    public ContragentItem getContragent() {
        return contragent;
    }

    public ClientItem getClient() {
        return client;
    }

    public Long getIdOfAccount() {
        return idOfAccount;
    }

    public void setIdOfAccount(Long idOfAccount) {
        this.idOfAccount = idOfAccount;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.contragent = new ContragentItem(contragent);
        }
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            this.client = new ClientItem(client);
        }
    }

    public void fill(Session session) throws Exception {

    }

    public void createCCAccount(Session session) throws Exception {
        Contragent contragent = (Contragent) session.load(Contragent.class, this.contragent.getIdOfContragent());
        CompositeIdOfContragentClientAccount id = new CompositeIdOfContragentClientAccount(
                contragent.getIdOfContragent(), this.idOfAccount);
        Client client = (Client) session.load(Client.class, this.client.getIdOfClient());
        ContragentClientAccount contragentClientAccount = new ContragentClientAccount(id, client);
        session.save(contragentClientAccount);
    }

}