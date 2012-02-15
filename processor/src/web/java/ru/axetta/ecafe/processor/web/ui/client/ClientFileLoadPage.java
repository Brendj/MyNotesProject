/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientFileLoadPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientFileLoadPage.class);
    private static final long MAX_LINE_NUMBER = 80000;

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

    public static class LineResult {

        private final long lineNo;
        private final int resultCode;
        private final String message;
        private final Long idOfClient;

        public LineResult(long lineNo, int resultCode, String message, Long idOfClient) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.message = message;
            this.idOfClient = idOfClient;
        }

        public long getLineNo() {
            return lineNo;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }
    }

    private OrgItem org = new OrgItem();
    private boolean checkFullNameUnique = true;
    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;

    public String getPageFilename() {
        return "client/load_file";
    }

    public OrgItem getOrg() {
        return org;
    }

    public boolean isCheckFullNameUnique() {
        return checkFullNameUnique;
    }

    public void setCheckFullNameUnique(boolean checkFullNameUnique) {
        this.checkFullNameUnique = checkFullNameUnique;
    }

    public List<LineResult> getLineResults() {
        return lineResults;
    }

    public int getLineResultSize() {
        return lineResults.size();
    }

    public int getSuccessLineNumber() {
        return successLineNumber;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
        }
    }

    public void fill(Session persistenceSession) throws Exception {
        // Nothing to do here
    }

    public void loadClients(InputStream inputStream, long dataSize) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            ContractIdGenerator contractIdGenerator = runtimeContext.getClientContractIdGenerator();
            TimeZone localTimeZone = runtimeContext
                    .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setTimeZone(localTimeZone);

            long lineCount = dataSize / 100;
            if (lineCount > MAX_LINE_NUMBER) {
                lineCount = MAX_LINE_NUMBER;
            }
            List<LineResult> lineResults = new ArrayList<LineResult>((int) lineCount);
            int lineNo = 0;
            int successLineNumber = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
            String currLine = reader.readLine();
            while (null != currLine) {
                LineResult result = createClient(runtimeContext, contractIdGenerator, dateFormat, this.org.getIdOfOrg(),
                        currLine, lineNo, this.checkFullNameUnique);
                if (result.getResultCode() == 0) {
                    ++successLineNumber;
                }
                lineResults.add(result);
                currLine = reader.readLine();
                if (lineNo == MAX_LINE_NUMBER) {
                    break;
                }
                ++lineNo;
            }

            this.lineResults = lineResults;
            this.successLineNumber = successLineNumber;
        } finally {
            //RuntimeContext.release(runtimeContext);
        }
    }
        /*
    public void loadClients(InputStream inputStream, long dataSize) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        ContractIdGenerator contractIdGenerator = runtimeContext.getClientContractIdGenerator();
        TimeZone localTimeZone = runtimeContext
                .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setTimeZone(localTimeZone);

        long lineCount = dataSize / 100;
        if (lineCount > MAX_LINE_NUMBER) {
            lineCount = MAX_LINE_NUMBER;
        }
        List<LineResult> lineResults = new ArrayList<LineResult>((int) lineCount);
        int lineNo = 0;
        int successLineNumber = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
        String currLine = reader.readLine();
        while (null != currLine) {
            LineResult result = createClient(runtimeContext, contractIdGenerator, dateFormat, this.org.getIdOfOrg(),
                    currLine, lineNo, this.checkFullNameUnique);
            if (result.getResultCode() == 0) {
                ++successLineNumber;
            }
            lineResults.add(result);
            currLine = reader.readLine();
            if (lineNo == MAX_LINE_NUMBER) {
                break;
            }
            ++lineNo;
        }

        this.lineResults = lineResults;
        this.successLineNumber = successLineNumber;
    }
               */
    private LineResult createClient(RuntimeContext runtimeContext, ContractIdGenerator contractIdGenerator,
            DateFormat dateFormat, Long idOfOrg, String line, int lineNo, boolean checkFullNameUnique) {
        String[] tokens = line.split(";");
        if (tokens.length < 19) {
            return new LineResult(lineNo, 1, "Not enough data", null);
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org organization = DAOUtils.findOrg(persistenceSession, idOfOrg);
            if (null == organization) {
                return new LineResult(lineNo, 2, String.format("Org ot found: idOfOrg == %s", idOfOrg), null);
            }

            String firstName = tokens[9];
            String surname = tokens[8];
            String secondName = tokens[10];

            if (checkFullNameUnique && existClient(persistenceSession, organization, firstName, surname, secondName)) {
                return new LineResult(lineNo, 10, "Duplicate client (full name of person)", null);
            }

            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
            String contractIdText = tokens[0];
            long contractId;
            if (StringUtils.equals(contractIdText, "AUTO")) {
                contractId = contractIdGenerator.generate(organization.getIdOfOrg());
            } else {
                contractId = Long.parseLong(contractIdText);
            }

            Person contractPerson = new Person(tokens[5], tokens[4], tokens[6]);
            contractPerson.setIdDocument(tokens[7]);
            persistenceSession.save(contractPerson);
            Person person = new Person(firstName, surname, secondName);
            person.setIdDocument(tokens[11]);
            persistenceSession.save(person);

            long limit = organization.getCardLimit();  /*
            if (tokens.length >= 25 && StringUtils.isNotEmpty(tokens[19])) {
                limit = CurrencyStringUtils.rublesToCopecks(tokens[19]);
            }                                            */

            Client client = new Client(organization, person, contractPerson, 0, Boolean.parseBoolean(tokens[16]),
                    Boolean.parseBoolean(tokens[15]), contractId, dateFormat.parse(tokens[18]),
                    Integer.parseInt(tokens[1]), tokens[0], Integer.parseInt(tokens[14]), clientRegistryVersion, limit,
                    20000, "");
            client.setAddress(tokens[12]);
            client.setPhone(tokens[13]);
            client.setMobile(tokens[14]);
            client.setEmail(tokens[15]);
            if (tokens.length >= 21) {
                client.setRemarks(tokens[20]);
            }
            persistenceSession.save(client);
            Long idOfClient = client.getIdOfClient();

            persistenceTransaction.commit();
            persistenceTransaction = null;

            return new LineResult(lineNo, 0, "Ok", idOfClient);
        } catch (Exception e) {
            logger.debug("Failed to create client", e);
            return new LineResult(lineNo, 3, e.getMessage(), null);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private static boolean existClient(Session persistenceSession, Org organization, String firstName, String surname,
            String secondName) throws Exception {
        if (StringUtils.isEmpty(secondName)) {
            return DAOUtils.existClient(persistenceSession, organization, firstName, surname);
        }
        return DAOUtils.existClient(persistenceSession, organization, firstName, surname, secondName);
    }

}