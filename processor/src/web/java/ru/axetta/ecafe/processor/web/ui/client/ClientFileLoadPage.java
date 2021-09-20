/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.ClientsMobileHistory;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    public static final String UTF8_BOM = "\uFEFF";

    protected static final String[] COLUMN_NAMES = new String[]{
            "Номер л/счета", "Пароль", "Статус", "Дата договора", "Договор-фамилия", "Договор-имя", "Договор-отчество",
            "Договор-документ", "Фамилия", "Имя", "Отчество", "Пол(ж-0 м-1)", "Документ", "Адрес", "Телефон",
            "Мобильный", "E-mail", "Платный SMS", "Уведомление по e-mail", "Уведомление по SMS", "Овердрафт",
            "Уведомление через PUSH", "Группа/класс"};

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

    public void loadClients(InputStream inputStream, long dataSize, ClientsMobileHistory clientsMobileHistory)
            throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            long lineCount = dataSize / 100;
            if (lineCount > MAX_LINE_NUMBER) {
                lineCount = MAX_LINE_NUMBER;
            }
            List<LineResult> lineResults = new ArrayList<>((int) lineCount);
            int lineNo = 0;
            int successLineNumber = 0;
            ClientManager.ClientFieldConfig fieldConfig = new ClientManager.ClientFieldConfig();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            LineResult result;
            String currLine = reader.readLine();
            while (null != currLine) {
                fieldConfig.resetToDefaultValues();
                if (lineNo == 0 && currLine.startsWith("!")) {
                    parseLineConfig(fieldConfig, currLine);
                } else {
                    if (lineNo == 0) {
                        currLine = currLine.replace(UTF8_BOM, "");
                    }
                    result = checkLength(currLine, lineNo);
                    if (result != null) {
                        lineResults.add(result);
                        break;
                    }
                    if (!isTitle(currLine)) {
                        result = checkNames(currLine, lineNo);
                        if (result != null) {
                            lineResults.add(result);
                            currLine = reader.readLine();
                            if (lineNo == MAX_LINE_NUMBER) {
                                break;
                            }
                            ++lineNo;
                            continue;
                        }
                        result = createClient(fieldConfig, this.org.getIdOfOrg(), currLine, lineNo,
                                this.checkFullNameUnique, clientsMobileHistory);
                        if (result.getResultCode() == 0) {
                            ++successLineNumber;
                        }
                        lineResults.add(result);
                    }
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

    private boolean isTitle(String currLine) throws Exception {
        String[] data = currLine.split(";");
        for (int i = 0; i < data.length; i++) {
            String str = data[i].replace("\"", "");
            if (str == null || StringUtils.isBlank(str)) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(COLUMN_NAMES[i], str)) {
                return true;
            }
        }
        return false;
    }

    private LineResult checkLength(String row, int lineNo) {
        String[] data = row.split(";");
        if (data.length > 23) {
            return new LineResult(lineNo, -1,
                    "Ошибка: " + "Количество полей в строке " + lineNo + " не совпадает с заголовком", -1L);
        }
        return null;
    }

    private LineResult checkNames(String line, int lineNo) {
        String[] data = line.split(";");
        if (data[8].equals("") || data[9].equals("")) {     // SURNAME, NAME
            String value = data[8].equals("") ? COLUMN_NAMES[8] : COLUMN_NAMES[9];
            return new LineResult(lineNo, -1,
                    "Ошибка: " + "Не заполнено обязательное поле " + value + " в строке " + lineNo, -1L);
        }
        return null;
    }

    private void parseLineConfig(FieldProcessor.Config fc, String currLine) throws Exception {
        String attrs[] = currLine.substring(1).split(";");
        for (int n = 0; n < attrs.length; ++n) {
            fc.registerField(attrs[n]);
        }
        fc.checkRequiredFields();
    }

    private LineResult createClient(ClientManager.ClientFieldConfig fieldConfig, Long idOfOrg, String line, int lineNo,
            boolean checkFullNameUnique, ClientsMobileHistory clientsMobileHistory) throws Exception {
        String[] tokens = modifyData(line, lineNo);
        try {
            fieldConfig.setValues(tokens);
        } catch (Exception e) {
            return new LineResult(lineNo, 1, e.getMessage(), null);
        }

        boolean payForSmsIsNull = fieldConfig.isValueNull(ClientManager.FieldId.PAY_FOR_SMS);
        if (payForSmsIsNull) {
            String payForSms =
                    RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_SEND_PAYMENT_NOTIFY_SMS_ON) ? "1"
                            : "0";
            fieldConfig.setValue(ClientManager.FieldId.PAY_FOR_SMS, payForSms);
        }

        //если флаг установки уведомления по Push не установлен в файле загрузки, устаноавливаем значение по умолчанию в соотв. с опцией
        boolean notifyPushIsNull = fieldConfig.isValueNull(ClientManager.FieldId.NOTIFY_BY_PUSH);
        if (notifyPushIsNull) {
            String notifyByPush =
                    RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS) ? "1"
                            : "0";
            fieldConfig.setValue(ClientManager.FieldId.NOTIFY_BY_PUSH, notifyByPush);
        }

        boolean notifyEmailIsNull = fieldConfig.isValueNull(ClientManager.FieldId.NOTIFY_BY_EMAIL);
        if (notifyEmailIsNull) {
            String notifyByEmail =
                    RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS) ? "1"
                            : "0";
            fieldConfig.setValue(ClientManager.FieldId.NOTIFY_BY_EMAIL, notifyByEmail);
        }

        boolean isGenderEmpty = fieldConfig.isValueNull(ClientManager.FieldId.GENDER);
        if (isGenderEmpty) {
            fieldConfig.setValue(ClientManager.FieldId.GENDER, 0);
        }

        boolean isContractDateEmpty = fieldConfig.isValueNull(ClientManager.FieldId.CONTRACT_DATE);
        if (isContractDateEmpty) {
            fieldConfig.setValue(ClientManager.FieldId.CONTRACT_DATE, "#CURRENT_DATE");
        }

        try {
            long idOfClient = ClientManager.registerClient(idOfOrg, fieldConfig, checkFullNameUnique, true,
                    clientsMobileHistory);
            ClientManager.updateComment(idOfClient, MskNSIService.COMMENT_MANUAL_IMPORT);
            return new LineResult(lineNo, 0, "Ok", idOfClient);
        } catch (Exception e) {
            return new LineResult(lineNo, -1, "Ошибка: " + e.getMessage(), -1L);
        }

    }

    private String[] modifyData(String line, int lineNo) {
        String[] data = line.split(";", -1);
        String[] tokens = new String[34];
        for (int i = 0; i < data.length && i < 11; i++) {
            data[i] = data[i].trim();
            if (i < 11) {
                if (i == 0 && data[i].equals("")) {     // CONTRACT_ID
                    data[i] = "AUTO";
                }
                if (i == 3 && data[i].equals("")) {     // CONTRACT_DATE
                    data[i] = "#CURRENT_DATE";
                }
                if ((i == 2) && data[i].equals("")) {   // CONTRACT_STATE
                    data[i] = "0";
                }
                tokens[i] = data[i].trim();
            } /*else if (i > 11) {
                if ((i == 16 || i == 17 || i == 18) && data[i]
                        .equals("")) {        // PAY_FOR_SMS, NOTIFY_BY_PUSH, NOTIFY_BY_EMAIL
                    data[i] = "0";
                }
                tokens[i - 1] = data[i];
            }*/
        }
        tokens[11] = data[12]; //документ
        tokens[12] = data[13]; //адрес
        tokens[13] = data[14]; //телефон
        tokens[14] = data[15]; //мобильный
        tokens[15] = data[16]; //емейл
        tokens[16] = data[17].equals("") ? "0" : data[17]; //платный смс
        tokens[17] = data[19].equals("") ? "0" : data[19]; //уведомления по смс
        //tokens[18] = data[21].equals("") ? "0" : data[21]; //уведомления пуш
        tokens[18] = data[18].equals("") ? "0" : data[18]; //уведомления емейл
        tokens[19] = data[20]; //овердрафт
        tokens[22] = StringUtils.isEmpty(data[22]) ? ClientGroup.Predefined.CLIENT_OTHERS.getNameOfGroup() : data[22];
        tokens[33] = data[11].equals("0") ? "f" : "m";      // GENDER
        return tokens;
    }

    public void downloadSample() {
        String result = "\"Номер л/счета\";\"Пароль\";\"Статус\";\"Дата договора\";\"Договор-фамилия\";"
                + "\"Договор-имя\";\"Договор-отчество\";\"Договор-документ\";\"Фамилия\";\"Имя\";"
                + "\"Отчество\"; \"Пол(ж-0 м-1)\";\"Документ\";\"Адрес\";\"Телефон\";\"Мобильный\";\"E-mail\";\"Платный SMS\";"
                + "\"Уведомление по e-mail\";\"Уведомление по SMS\";\"Овердрафт\";\"Уведомление через PUSH\";"
                + "\"Группа/класс\";";
        FacesContext facesContext = FacesContext.getCurrentInstance();
        try {
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            ServletOutputStream servletOutputStream = response.getOutputStream();
            facesContext.responseComplete();
            response.setContentType("application/csv");
            response.setHeader("Content-disposition", "attachment;filename=\"clients.csv\"");
            servletOutputStream.write(result.getBytes(StandardCharsets.UTF_8));
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
            logger.error("Failed export report : ", e);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Не удалось сгенерировать пример файла для загрузки: " + e.getMessage(), null));
        }
    }


}