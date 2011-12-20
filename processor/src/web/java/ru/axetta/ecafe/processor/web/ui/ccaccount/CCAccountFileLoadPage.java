/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.ccaccount;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class CCAccountFileLoadPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(CCAccountFileLoadPage.class);

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

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.orgShortName = client.getOrg().getShortName();
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.contractId = client.getContractId();
        }

        public ClientItem() {
            this.idOfClient = null;
            this.orgShortName = null;
            this.person = new PersonItem();
            this.contractPerson = new PersonItem();
            this.contractId = null;
        }

        public String getShortName() {
            if (null == this.idOfClient) {
                return null;
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

    public static class CCAccountItem {

        private final CompositeIdOfContragentClientAccount compositeIdOfContragentClientAccount;
        private final Long idOfAccount;
        private final ContragentItem contragent;
        private final ClientItem client;

        public CCAccountItem() {
            this.compositeIdOfContragentClientAccount = null;
            this.idOfAccount = null;
            this.contragent = new ContragentItem();
            this.client = new ClientItem();
        }

        public CCAccountItem(ContragentClientAccount contragentClientAccount, Contragent contragent) {
            this.compositeIdOfContragentClientAccount = contragentClientAccount
                    .getCompositeIdOfContragentClientAccount();
            this.idOfAccount = contragentClientAccount.getCompositeIdOfContragentClientAccount().getIdOfAccount();
            this.contragent = new ContragentItem(contragent);
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

    public static class CreateResult {

        private final int resultCode;
        private final String message;
        private final CCAccountItem ccAccount;

        public CreateResult(int resultCode, String message) {
            this.resultCode = resultCode;
            this.message = message;
            this.ccAccount = new CCAccountItem();
        }

        public CreateResult(int resultCode, String message, ContragentClientAccount contragentClientAccount,
                Contragent contragent) {
            this.resultCode = resultCode;
            this.message = message;
            this.ccAccount = new CCAccountItem(contragentClientAccount, contragent);
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public CCAccountItem getCcAccount() {
            return ccAccount;
        }
    }

    public static class LineResult {

        private final long line;
        private final CreateResult createResult;

        public LineResult(long line, CreateResult createResult) {
            this.line = line;
            this.createResult = createResult;
        }

        public long getLine() {
            return line;
        }

        public CreateResult getCreateResult() {
            return createResult;
        }
    }

    private List<LineResult> lineResults = Collections.emptyList();

    public String getPageFilename() {
        return "contragent/ccaccount/load_file";
    }

    public List<LineResult> getLineResults() {
        return lineResults;
    }

    public void setLineResults(List<LineResult> lineResults) {
        this.lineResults = lineResults;
    }

    public void fill(Session session) throws Exception {

    }

    public void loadCCAccounts(InputStream inputStream) throws Exception {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            List<LineResult> newLineResults = new LinkedList<LineResult>();
            long line = 1;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
            String currLine = reader.readLine();
            while (null != currLine) {
                newLineResults.add(new LineResult(line, createCCAccount(runtimeContext, currLine)));
                currLine = reader.readLine();
                ++line;
            }
            this.lineResults = newLineResults;
        } finally {
            RuntimeContext.release(runtimeContext);
        }
    }

    private CreateResult createCCAccount(RuntimeContext runtimeContext, String line) throws Exception {
        String[] tokens = line.split(";");
        if (tokens.length >= 3) {
            Long idOfContragent = Long.parseLong(tokens[0]);
            Long idOfAccount = Long.parseLong(tokens[1]);
            Long contractId = Long.parseLong(tokens[2]);
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();

                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                clientCriteria.add(Restrictions.eq("contractId", contractId));
                Client client = (Client) clientCriteria.uniqueResult();
                if (null == client) {
                    return new CreateResult(2,
                            String.format("Client not found: contractId == %s", ContractIdFormat.format(contractId)));
                }
                Contragent contragent = (Contragent) persistenceSession.get(Contragent.class, idOfContragent);
                if (null == contragent) {
                    return new CreateResult(3,
                            String.format("Contragent not found: IdOfContragent == %s", idOfContragent));
                }

                CompositeIdOfContragentClientAccount id = new CompositeIdOfContragentClientAccount(
                        contragent.getIdOfContragent(), idOfAccount);
                ContragentClientAccount account = new ContragentClientAccount(id, client);
                persistenceSession.save(account);

                CreateResult createResult = new CreateResult(0, "Ok", account, contragent);

                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
                return createResult;
            } catch (Exception e) {
                logger.debug("Failed to create contragent client account", e);
                return new CreateResult(1, e.getMessage());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
        return new CreateResult(1, "Not enogth data");
    }
}