/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
public class ClientUpdateFileLoadPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClientUpdateFileLoadPage.class);
    private static final long MAX_LINE_NUMBER = 80000;
    private String errorText = "";

    public OrgItem getOrg() {
        return org;
    }

    public void setOrg(OrgItem org) {
        this.org = org;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public Boolean getErrorPresent() {
        return !StringUtils.isEmpty(errorText);
    }

    public static class LineResult {

        private final long lineNo;
        private final int resultCode;
        private final String resultDescription;
        private final Long idOfClient;
        private final Long contractId;

        public LineResult(long lineNo, int resultCode, String resultDescription, Long idOfClient, Long contractId) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.resultDescription = resultDescription;
            this.idOfClient = idOfClient;
            this.contractId = contractId;
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

        public Long getContractId() {
            return contractId;
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

    public static class ClientItem {
        private final Long contractId;
        private final String group;

        public ClientItem(Long contractId, String group) {
            this.contractId = contractId;
            this.group = group;
        }

        public Long getContractId() {
            return contractId;
        }

        public String getGroup() {
            return group;
        }
    }

    private List<LineResult> lineResults = Collections.emptyList();
    private int successLineNumber;
    private OrgItem org = new OrgItem();

    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
        } else {
            this.org = new OrgItem();
        }
    }

    public Boolean orgSelected() {
        return org.getIdOfOrg() != null;
    }

    public void downloadClients() {
        String clients = "\"Номер л/счета\";\"Фамилия\";\"Имя\";"
                + "\"Отчество\";\"Дата рождения\";"
                + "\"Текущая группа\";\"Новая группа\"\r\n";
        clients += getClientsByOrg();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            facesContext.responseComplete();
            response.setContentType("application/csv");
            response.setHeader("Content-disposition", "attachment;filename=\"clients.csv\"");
            servletOutputStream.write(clients.getBytes(StandardCharsets.UTF_8));
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            logger.error("Failed export clients : ", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Не удалось сгенерировать список клиентов для загрузки: " + e.getMessage(), null));
        }
    }

    private String getClientsByOrg() {
        String result = "";
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("select c from Client c join fetch c.person join fetch c.clientGroup "
                    + "where c.org.idOfOrg = :org and c.idOfClientGroup < :group order by c.clientGroup.groupName, c.person.surname, c.person.firstName, c.person.secondName");
            query.setParameter("org", org.getIdOfOrg());
            query.setParameter("group", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            List<Client> list = query.list();
            for (Client client : list) {
                StringBuilder sb = new StringBuilder();
                sb.append(client.getContractId()).append(";");
                sb.append(client.getPerson().getSurname()).append(";");
                sb.append(client.getPerson().getFirstName()).append(";");
                sb.append(client.getPerson().getSecondName()).append(";");
                sb.append(client.getBirthDate() == null ? "" : CalendarUtils.dateToString(client.getBirthDate())).append(";");
                sb.append(client.getClientGroup().getGroupName()).append(";");
                sb.append(client.getClientGroup().getGroupName()).append("\r\n");
                result += sb.toString();
            }
            transaction.commit();
            transaction = null;
            return result;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String currLine = reader.readLine();

            while (null != currLine) {
                if (lineNo==0) {
                    ++lineNo;
                    currLine = reader.readLine();
                    continue; //пропускаем заголовок
                } else {
                    LineResult result = updateClient(currLine, lineNo);
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

    private LineResult updateClient(String line, int lineNo) throws Exception {
        String[] tokens = line.split(";");
        if (tokens.length != 7) throw new Exception("Неправильная структура файла. Ошибка в строке " + lineNo);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Long contractId = new Long(tokens[0]);
            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) return new LineResult(lineNo, -1, "Клиент не найден", null, contractId);
            String prevGroup = tokens[5];
            String groupName = tokens[6];
            if (groupName.equals(prevGroup)) {
                return new LineResult(lineNo, 0, "Группа не менялась", client.getIdOfClient(), contractId);
            }
            if (client.getClientGroup() != null && client.getClientGroup().getGroupName().equals(groupName)) {
                return new LineResult(lineNo, 0, "Группа не менялась", client.getIdOfClient(), contractId);
            }
            String surname = tokens[1];
            String firstname = tokens[2];
            String secondname = tokens[3];
            ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(session,
                    client.getOrg().getIdOfOrg(), groupName);
            if (clientGroup == null) {
                clientGroup = DAOUtils.createClientGroup(session, client.getOrg().getIdOfOrg(), groupName);
            }

            ClientManager.createClientGroupMigrationHistory(session, client, client.getOrg(),
                    clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), clientGroup.getGroupName(),
                    ClientGroupMigrationHistory.MODIFY_IN_WEBAPP + FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
            client.setClientGroup(clientGroup);
            client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            Long nextClientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
            client.setClientRegistryVersion(nextClientRegistryVersion);
            Person person = client.getPerson();
            person.setSurname(surname);
            person.setFirstName(firstname);
            person.setSecondName(secondname);
            session.update(person);
            session.update(client);
            transaction.commit();
            transaction = null;
            return new LineResult(lineNo, 0, "Ok", client.getIdOfClient(), contractId);
        } catch (Exception e) {
            return new LineResult(lineNo, 1, e.getMessage(), null, null);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }


}