/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SochiClient;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.util.ParseUtils;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class SochiClientsLoadPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(SochiClientsLoadPage.class);
    private static final long MAX_LINE_NUMBER = 80000;

    public static class ClientInfo {

        private Long contractId;
        private String fullName;
        private String address;

        public ClientInfo() {
            this.contractId = null;
            this.fullName = "";
            this.address = "";
        }

        public ClientInfo(SochiClient sochiClient) {
            this.contractId = sochiClient.getContractId();
            this.fullName = sochiClient.getFullName();
            this.address = sochiClient.getAddress();
        }

        public long getContractId() {
            return contractId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getAddress() {
            return address;
        }
    }

    public static class LineResult {

        private int lineNo;
        private int resultCode;
        private String resultDescription;
        private ClientInfo client;

        public LineResult(int lineNo, int resultCode, String resultDescription, ClientInfo client) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.resultDescription = resultDescription;
            this.client = client;
        }

        public LineResult(int lineNo, int resultCode, String resultDescription) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.resultDescription = resultDescription;
            this.client = new ClientInfo();
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

        public ClientInfo getClient() {
            return client;
        }
    }

    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;

    public String getPageFilename() {
        return "service/load_sochi_clients";
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

    public void loadClients(InputStream inputStream, long dataSize) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();

            TimeZone localTimeZone = runtimeContext
                    .getDefaultLocalTimeZone((HttpSession) facesContext.getExternalContext().getSession(false));
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setTimeZone(localTimeZone);

            NumberFormat contractIdFormat = new DecimalFormat("#########0");

            long lineCount = dataSize / 50;
            if (lineCount > MAX_LINE_NUMBER) {
                lineCount = MAX_LINE_NUMBER;
            }
            List<LineResult> lineResults = new ArrayList<LineResult>((int) lineCount);
            int lineNo = 0;
            int successLineNumber = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));

            String currLine = reader.readLine();
            while (null != currLine) {
                LineResult result = createUpdateSochiClient(runtimeContext, contractIdFormat, currLine, lineNo);
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
            RuntimeContext.release(runtimeContext);
        }
    }

    private static LineResult createUpdateSochiClient(RuntimeContext runtimeContext, NumberFormat contractIdFormat,
            String line, int lineNo) throws Exception {
        String[] tokens = StringUtils.split(line, ';');
        if (tokens.length < 3) {
            return new LineResult(lineNo, 100, "Not enough data");
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            String fullName = tokens[0];
            String address = tokens[1];
            String contractIdText = tokens[2];
            long contractId;
            try {
                contractId = ParseUtils.parseLong(contractIdFormat, contractIdText);
            } catch (Exception e) {
                return new LineResult(lineNo, 200,
                        String.format("Failed to parse contractId. Value: %s", contractIdText));
            }
            SochiClient client = DAOUtils.findSochiClient(persistenceSession, contractId);
            if (client == null) {
                client = new SochiClient(contractId, fullName);
                client.setAddress(address);
                persistenceSession.save(client);
            } else {
                client.setFullName(fullName);
                client.setAddress(address);
                client.setUpdateTime(new Date());
                persistenceSession.update(client);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;

            return new LineResult(lineNo, 0, "Ok", new ClientInfo(client));
        } catch (Exception e) {
            logger.debug("Failed to create sochi client", e);
            return new LineResult(lineNo, 3, e.getMessage(), new ClientInfo());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

}