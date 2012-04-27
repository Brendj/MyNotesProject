/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

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
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ClientUpdateFileLoadPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ClientUpdateFileLoadPage.class);
    private static final long MAX_LINE_NUMBER = 80000;

    public static class LineResult {

        private final long lineNo;
        private final int resultCode;
        private final String resultDescription;
        private final Long idOfClient;

        public LineResult(long lineNo, int resultCode, String resultDescription, Long idOfClient) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.resultDescription = resultDescription;
            this.idOfClient = idOfClient;
        }

        public long getLineNo() {
            return lineNo;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getResultDescription() {
            return resultDescription;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }
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

    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;

    public String getPageFilename() {
        return "client/load_update_file";
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

    public void fill(Session persistenceSession) throws Exception {
        //nothing to do here
    }

    public void updateClients(InputStream inputStream, long dataSize) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        try{
            TimeZone localTimeZone = runtimeContext
                    .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setTimeZone(localTimeZone);

            long lineCount = dataSize / 20;
            if (lineCount > MAX_LINE_NUMBER) {
                lineCount = MAX_LINE_NUMBER;
            }
            List<LineResult> lineResults = new ArrayList<LineResult>((int) lineCount);
            int lineNo = 0;
            int successLineNumber = 0;
            /* массив с именами колонок */
            String colums[]={}; //= {"ContractState", "MobilePhone","NotifyViaSMS",
            // "PersonFirstName","PersonSurName","PersonSecondName"};

            ClientManager.ClientFieldConfigForUpdate fieldConfig = new ClientManager.ClientFieldConfigForUpdate();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
            String currLine = reader.readLine();
            while (null != currLine) {
                if (lineNo==0) {
                    if (!currLine.startsWith("!")) currLine="!"+currLine;
                    parseLineConfig(fieldConfig, currLine);
                } else {
                    LineResult result = updateClient(fieldConfig, currLine, lineNo);
                    //result = updateClient(runtimeContext, dateFormat, currLine, lineNo, colums);
                    if (result.getResultCode() == 0) {
                        ++successLineNumber;
                    }
                    lineResults.add(result);
                }
                currLine = reader.readLine();
                if (lineNo == MAX_LINE_NUMBER) {
                    break;
                }
                ++lineNo;
            }
            this.lineResults = lineResults;
            this.successLineNumber = successLineNumber;
        } finally {

        }
    }

    private void parseLineConfig(FieldProcessor.Config fc, String currLine) throws Exception {
        String attrs[] = currLine.substring(1).split(";");
        for (int n=0;n<attrs.length;++n) {
            fc.registerField(attrs[n]);
        }
        fc.checkRequiredFields();
    }

    private LineResult updateClient(ClientManager.ClientFieldConfigForUpdate fieldConfig, String line,
            int lineNo) {
        String[] tokens = line.split(";");
        try {
            fieldConfig.setValues(tokens);
        } catch (Exception e) {
            return new LineResult(lineNo, 1, e.getMessage(), null);
        }
        try {
            long idOfClient = ClientManager.modifyClient(fieldConfig);
            return new LineResult(lineNo, 0, "Ok", idOfClient);
        } catch (Exception e) {
            return new LineResult(lineNo, -1, "Ошибка: "+e.getMessage(), -1L);
        }

    }

    /*
    private LineResult createClient(RuntimeContext runtimeContext, DateFormat dateFormat, String line, int lineNo, String[] colums) {
        String[] tokens = line.split(";");
        if (tokens.length < 2) {
            return new LineResult(lineNo, 1, "Not enough data", null);
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
            long contractId = Long.parseLong(tokens[0]);

            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                return new LineResult(lineNo, 20, "Client not found", null);
            }
            for (int i=1; i<colums.length; i++){
                if(colums[i].equalsIgnoreCase("ContractState")){
                    client.setContractState(Integer.parseInt(tokens[i].trim()));
                }
                if(colums[i].equalsIgnoreCase("MobilePhone")){
                    client.setMobile(tokens[i].trim());
                }
                if(colums[i].equalsIgnoreCase("NotifyBySMS")){
                    client.setNotifyViaSMS(Integer.parseInt(tokens[i].trim())!=0);
                }
                if(colums[i].equalsIgnoreCase("PersonFirstName")){
                    client.getPerson().setFirstName(tokens[i].trim());
                }
                if(colums[i].equalsIgnoreCase("PersonSurName")){
                    client.getPerson().setSurname(tokens[i].trim());
                }
                if(colums[i].equalsIgnoreCase("PersonSecondName")){
                    client.getPerson().setSecondName(tokens[i].trim());
                }
            }
            // "PersonFirstName","PersonSurName","PersonSecondName"};
            client.setUpdateTime(new Date());
            client.setClientRegistryVersion(clientRegistryVersion);
            persistenceSession.update(client);
            Long idOfClient = client.getIdOfClient();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            return new LineResult(lineNo, 0, "Ok", idOfClient);
        } catch (Exception e) {
            logger.debug("Failed to update client", e);
            return new LineResult(lineNo, 3, e.getMessage(), null);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
    */

}