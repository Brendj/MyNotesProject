/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

            long lineCount = dataSize / 100;
            if (lineCount > MAX_LINE_NUMBER) {
                lineCount = MAX_LINE_NUMBER;
            }
            List<LineResult> lineResults = new ArrayList<LineResult>((int) lineCount);
            int lineNo = 0;
            int successLineNumber = 0;
            ClientManager.ClientFieldConfig fieldConfig = new ClientManager.ClientFieldConfig();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
            String currLine = reader.readLine();
            while (null != currLine) {
                fieldConfig.resetToDefaultValues();
                if (lineNo==0 && currLine.startsWith("!")) {
                    parseLineConfig(fieldConfig, currLine);
                } else {
                    LineResult result = createClient(fieldConfig, this.org.getIdOfOrg(),
                            currLine, lineNo, this.checkFullNameUnique);
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
            //RuntimeContext.release(runtimeContext);
        }
    }

    private void parseLineConfig(FieldProcessor.Config fc, String currLine) throws Exception {
        String attrs[] = currLine.substring(1).split(";");
        for (int n=0;n<attrs.length;++n) {
            fc.registerField(attrs[n]);
        }
        fc.checkRequiredFields();
    }

    private LineResult createClient(ClientManager.ClientFieldConfig fieldConfig,
            Long idOfOrg, String line, int lineNo, boolean checkFullNameUnique) throws Exception {
        String[] tokens = line.split(";", -1);
        try {
            fieldConfig.setValues(tokens);
        } catch (Exception e) {
            return new LineResult(lineNo, 1, e.getMessage(), null);
        }
        //если флаг установки уведомления по Push не установлен в файле загрузки, устаноавливаем значение по умолчанию в соотв. с опцией
        Boolean notifyPushIsNull = fieldConfig.isValueNull(ClientManager.FieldId.NOTIFY_BY_PUSH);
        if (notifyPushIsNull) {
            String notifyByPush = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1" : "0";
            fieldConfig.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
        }

        Boolean notifyEmailIsNull = fieldConfig.isValueNull(ClientManager.FieldId.NOTIFY_BY_EMAIL);
        if (notifyEmailIsNull) {
            String notifyByEmail = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS) ? "1" : "0";
            fieldConfig.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, notifyByEmail);
        }

        Boolean isGroupsFieldEmpty = fieldConfig.isValueNull(ClientManager.FieldId.GROUP);
        if (isGroupsFieldEmpty) {
            fieldConfig.setValue(ClientManager.FieldId.GROUP, ClientGroup.Predefined.CLIENT_OTHERS.getNameOfGroup());
        }

        try {
            long idOfClient = ClientManager.registerClient(idOfOrg, fieldConfig, checkFullNameUnique, false);
            return new LineResult(lineNo, 0, "Ok", idOfClient);
        } catch (Exception e) {
            return new LineResult(lineNo, -1, "Ошибка: "+e.getMessage(), -1L);
        }

    }


}