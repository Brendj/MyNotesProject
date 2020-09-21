/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
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
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
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
    private String errorTextGroups = "";

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

    public Boolean getErrorGroupsPresent() {
        return !StringUtils.isEmpty(errorTextGroups);
    }

    public List<LineResult> getLineGroupsResults() {
        return lineGroupsResults;
    }

    public void setLineGroupsResults(List<LineResult> lineGroupsResults) {
        this.lineGroupsResults = lineGroupsResults;
    }

    public String getErrorTextGroups() {
        return errorTextGroups;
    }

    public void setErrorTextGroups(String errorTextGroups) {
        this.errorTextGroups = errorTextGroups;
    }

    public static class LineResult {

        private final long lineNo;
        private final int resultCode;
        private final String resultDescription;
        private final Long idOfClient;
        private final Long contractId;
        private String fio;
        private final List<Long> involvedClients;

        public LineResult(long lineNo, int resultCode, String resultDescription, Long idOfClient, Long contractId) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.resultDescription = resultDescription;
            this.idOfClient = idOfClient;
            this.contractId = contractId;
            this.involvedClients = new ArrayList<>();
        }

        public LineResult(long lineNo, int resultCode, String resultDescription, String fio, Long idOfClient, List<Long> involvedClients) {
            this.lineNo = lineNo;
            this.resultCode = resultCode;
            this.resultDescription = resultDescription;
            this.idOfClient = idOfClient;
            this.contractId = null;
            this.fio = fio;
            this.involvedClients = involvedClients;
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

        public String getFio() {
            return fio;
        }

        public void setFio(String fio) {
            this.fio = fio;
        }

        public List<Long> getInvolvedClients() {
            return involvedClients;
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
    private List<LineResult> lineGroupsResults = Collections.emptyList();
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

    public int getLineGroupsResultSize() {
        return lineGroupsResults.size();
    }

    public int getSuccessLineNumber() {
        return successLineNumber;
    }

    public void uploadGroupChange(UploadEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        errorTextGroups = "";
        if (org.getIdOfOrg() == null) {
            errorTextGroups = "Выберите организацию";
            return;
        }
        UploadItem item = event.getUploadItem();
        InputStream inputStream = null;
        long dataSize = 0;
        try {
            if (item.isTempFile()) {
                File file = item.getFile();
                dataSize = file.length();
                inputStream = new FileInputStream(file);
            } else {
                byte[] data = item.getData();
                dataSize = data.length;
                inputStream = new ByteArrayInputStream(data);
            }
            updateGroupChanges(inputStream, dataSize);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Клиенты загружены и зарегистрированы успешно", null));
            setErrorTextGroups("");
        } catch (Exception e) {
            logger.error("Failed to update clients from file", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка при загрузке/регистрации данных по клиентам: " + e.getMessage(), null));
            setErrorTextGroups(e.getMessage());
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.error("failed to close input stream", e);
                }
            }
        }
    }

    private void updateGroupChanges(InputStream inputStream, long dataSize) throws Exception {
        lineGroupsResults.clear();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "Windows-1251"));
        String currLine = reader.readLine();

        int lineNo = 1;
        int successLineNumber = 0;
        List<LineResult> lineResults = new ArrayList<LineResult>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Org orga = DAOService.getInstance().findOrgById(org.getIdOfOrg());
            while (null != currLine) {
                LineResult result = updateClientGroup(session, currLine, lineNo, orga);
                if (result.getResultCode() == 0) {
                    ++successLineNumber;
                }
                lineResults.add(result);
                currLine = reader.readLine();
                ++lineNo;
            }

            this.successLineNumber = successLineNumber;

            List<LineResult> lineResults2 = moveToLeaving(session, orga, lineResults);
            lineResults.addAll(lineResults2);
            lineGroupsResults = lineResults;

            transaction.commit();
            transaction = null;
        } catch (Exception e) {

        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

    }

    private List<LineResult> moveToLeaving(Session session, Org org, List<LineResult> lineResults) throws Exception {
        List<Long> ids = new ArrayList<>();
        for (LineResult lineResult : lineResults) {
            if (lineResult.getInvolvedClients() == null) continue;
            for (Long id : lineResult.getInvolvedClients()) {
                ids.add(id);
            }
        }
        List<LineResult> result = new ArrayList<>();
        List<Client> clients = ClientManager.getStudentsByOrg(session, org);
        Long nextClientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
        for (Client client : clients) {
            if (clientProcessed(client, ids)) continue;
            ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(session, org.getIdOfOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
            if (clientGroup == null) {
                clientGroup = DAOUtils.createClientGroup(session, org.getIdOfOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup());
            }

            ClientManager.createClientGroupMigrationHistory(session, client, client.getOrg(),
                    clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), clientGroup.getGroupName(),
                    ClientGroupMigrationHistory.MODIFY_IN_WEBAPP + FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
            client.setClientGroup(clientGroup);
            client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());

            client.setClientRegistryVersion(nextClientRegistryVersion);
            session.update(client);
            result.add(new LineResult(-1L, 3, "Переведен в выбывшие", client.getPerson().getFullName(), client.getIdOfClient(), null));
        }
        return result;
    }

    private boolean clientProcessed(Client client, List<Long> clientIds) {
        for (Long idOfClient : clientIds) {
            if (idOfClient.equals(client.getIdOfClient())) return true;
        }
        return false;
    }

    private LineResult updateClientGroup(Session session, String line, int lineNo, Org orga) throws Exception {
        String[] tokens = line.split(";");
        if (tokens.length != 4 && tokens.length != 5) throw new Exception("Неправильная структура файла. Ошибка в строке " + lineNo);
        try {
            String surname = tokens[0];
            String firstname = tokens[1];
            String secondname = tokens[2];
            String groupName = tokens[3];
            if (tokens.length == 5) groupName += tokens[4];
            String fio = surname + " " + firstname + " " + secondname;

            List<Long> idOfClientList = ClientManager.findClientByFullName(session, orga, surname, firstname, secondname, true);
            if (idOfClientList.isEmpty()) return new LineResult(lineNo, -1, "Клиент не найден", fio, null, null);
            if (idOfClientList.size()> 1) {
                return new LineResult(lineNo, -2, "По ФИО найдено более одного клиента", fio, -2L, idOfClientList);
            }
            Long idOfClient = idOfClientList.get(0);
            Client client = DAOUtils.findClient(session, idOfClient);
            if (client.getClientGroup() != null && client.getClientGroup().getGroupName().equals(groupName)) {
                return new LineResult(lineNo, 0, "Группа не менялась", fio, client.getIdOfClient(), idOfClientList);
            }

            ClientGroup clientGroup = DAOUtils.findClientGroupByGroupNameAndIdOfOrg(session, orga.getIdOfOrg(), groupName);
            if (clientGroup == null) {
                clientGroup = DAOUtils.createClientGroup(session, orga.getIdOfOrg(), groupName);
            }

            ClientManager.createClientGroupMigrationHistory(session, client, client.getOrg(),
                    clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup(), clientGroup.getGroupName(),
                    ClientGroupMigrationHistory.MODIFY_IN_WEBAPP + FacesContext.getCurrentInstance().getExternalContext().getRemoteUser());
            client.setClientGroup(clientGroup);
            client.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
            Long nextClientRegistryVersion = DAOUtils.updateClientRegistryVersion(session);
            client.setClientRegistryVersion(nextClientRegistryVersion);
            session.update(client);
            return new LineResult(lineNo, 0, "Ok", fio, client.getIdOfClient(), idOfClientList);
        } catch (Exception e) {
            return new LineResult(lineNo, 1, e.getMessage(), null, null, null);
        }
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