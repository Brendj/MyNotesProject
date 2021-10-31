/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.PaymentReconciliationManager;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Scope("session")
public class ReconciliationPage extends BasicWorkspacePage implements ContragentSelectPage.CompleteHandler {
    final static String FIELD_ID_OF_PAYMENT="idOfPayment", FIELD_ID_OF_CONTRACT="idOfContract", FIELD_SUM="sum",
            FIELD_SEPARATORS="separators", FIELD_DATE="date", PAYMENT_TRANSFORM="paymentTransform", SKIP_LINE="skipLine",
            FIELD_ID_SIZE="idSize", FIELD_ID_SKIP_PATTERN="idSkipPattern";

    private Long caAgent;
    private Long caReceiver;
    private String caAgentName;
    private String caReceiverName;
    private String registryProcessingError;
    private Date dtFrom = new Date(), dtTo = new Date();
    private List<PaymentReconciliationManager.RegistryItem> registryItems = new ArrayList<PaymentReconciliationManager.RegistryItem>();
    private List<PaymentReconciliationManager.Difference> differencesList;
    private String differencesInfo;
    private String settings;
    private List<UploadedFile> fileItems = new ArrayList<UploadedFile>();
    private int exportType = 0;
    private DateFormat localDateFormat = CalendarUtils.getDateFormatLocal();
    private LineConfig defaultLineConfig;
    private boolean dateDependent = false;

    @Override
    public String getPageFilename() {
        return "contragent/reconciliation";
    }

    @Override
    public void onShow() throws Exception {
        super.onShow();
    }

    public boolean isDateDependent() {
        return dateDependent;
    }

    public void setDateDependent(boolean dateDependent) {
        this.dateDependent = dateDependent;
    }

    public Object processData() {
        if (caAgent == null) {
            printErrorAndClear("Не указан агент");
            return null;
        }
        defaultLineConfig = fillDefaultLineConfig();
        if (defaultLineConfig == null) {
            return null;
        }
        for (UploadedFile item : fileItems) {
            readFile(item);
        }
        if (registryProcessingError != null) {
            printErrorAndClear(registryProcessingError);
            return null;
        }
        if (registryItems.isEmpty()) {
            printErrorAndClear("Не загружен реестр платежей");
            return null;
        }
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_RECONCILIATION_SETTING, settings);
        PaymentReconciliationManager reconciliationManager = RuntimeContext.getAppContext().getBean(PaymentReconciliationManager.class);
        Date dtFrom = CalendarUtils.truncateToDayOfMonth(this.dtFrom);
        Date dtTo = CalendarUtils.addDays(CalendarUtils.truncateToDayOfMonth(this.dtTo), 1);
        this.differencesInfo = null;
        this.differencesList = null;
        DateFormat df = CalendarUtils.getDateFormatLocal();
        try {
            this.differencesList = reconciliationManager.processRegistry(caAgent, caReceiver, dtFrom, dtTo, registryItems, dateDependent,
                    this.defaultLineConfig.idSize != null);
            this.differencesInfo = "Результаты сверки реестра за период: " + df.format(this.dtFrom) + " - " + df.format(this.dtTo)
                    + ": записей в реестре - " + registryItems.size() + ", различий - " + differencesList.size();
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при обработке", e);
        } finally {
            registryItems.clear();
            fileItems.clear();
        }
        return null;
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        Contragent ca = null;
        if (idOfContragent!=null) ca = (Contragent)session.get(Contragent.class, idOfContragent);
        if (classTypes.equals(""+Contragent.PAY_AGENT)) {
            caAgent = idOfContragent;
            caAgentName = ca == null?"":ca.getContragentName();
        }
        else if (classTypes.equals(""+Contragent.TSP)) {
            caReceiver = idOfContragent;
            caReceiverName = ca == null?"":ca.getContragentName();
        }
        else {
            throw new Exception("Неправильный тип контрагента: "+classTypes);
        }
        settings = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_RECONCILIATION_SETTING);
    }

    public static class LineConfig {
        Pattern separators;
        Integer nIdOfContractField, nIdOfPaymentField, nSumField, nDateField, idSize;
        String paymentTransform, idSkipPattern;
        boolean skipFirst, skipLast;
    }

    public void uploadFileListener(FileUploadEvent event) {
        fileItems.add(event.getUploadedFile());
    }

    private void readFile(UploadedFile item) {
        InputStream inputStream = null;
        long dataSize;
        try {
            byte[] data = item.getData();
            dataSize = data.length;
            inputStream = new ByteArrayInputStream(data);
            loadRegistry(inputStream, dataSize);
            registryProcessingError = null;
        } catch (Exception e) {
            registryProcessingError = String
                    .format("Ошибка при обработке файла реестра %s: %s", item.getName(), e.getMessage());
            logAndPrintMessage("Ошибка при обработке файла реестра", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void loadRegistry(InputStream inputStream, long dataSize) throws Exception {
        DateFormat df = new SimpleDateFormat("ddMMyyyy");
        df.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
        int lineNo = 0;
        LineConfig lineConfig = defaultLineConfig;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
        String currLine;
        int infoLineCount = 0;
        boolean customLineConfig = false;
        do {
            currLine = reader.readLine();
            if (currLine != null) {
                ++lineNo;
                try {
                    // Если у файла есть свои настройки - используем их.
                    if (currLine.startsWith("!")) {
                        if (!customLineConfig) {
                            lineConfig = new LineConfig();
                            customLineConfig = true;
                        }
                        parseLineConfig(lineConfig, currLine);
                    } else {
                        infoLineCount++;
                        if ((lineConfig.skipFirst && infoLineCount == 1) || currLine.trim().isEmpty() || (
                                lineConfig.skipLast && !reader.ready())) {
                            continue;
                        }
                        if (lineConfig.nIdOfPaymentField == null) {
                            throw new Exception("Не указано позиция обязательного поля: " + FIELD_ID_OF_PAYMENT);
                        }
                        if (lineConfig.separators == null) {
                            throw new Exception("Не указаны разделители, поле: " + FIELD_SEPARATORS);
                        }
                        PaymentReconciliationManager.RegistryItem ri = parseLine(lineConfig, currLine, df);
                        registryItems.add(ri);
                    }
                } catch (Exception e) {
                    throw new Exception("Ошибка при обработке строки " + lineNo + ": " + e.getMessage(), e);
                }
            }
        } while (null != currLine);
    }

    private PaymentReconciliationManager.RegistryItem parseLine(LineConfig lineConfig, String currLine, DateFormat df)
            throws Exception {
        String[] v = lineConfig.separators.split(currLine);
        String idOfPayment; String dt=null; Long sum=null, idOfContract=null;
        if (lineConfig.nIdOfPaymentField>=v.length) throw new Exception("Позиция "+FIELD_ID_OF_PAYMENT+" больше чем полей в строке: "+v.length);
        idOfPayment = v[lineConfig.nIdOfPaymentField];
        if (lineConfig.nIdOfContractField!=null) {
            if (lineConfig.nIdOfContractField>=v.length) throw new Exception("Позиция "+FIELD_ID_OF_CONTRACT+" больше чем полей в строке: "+v.length);
            idOfContract = Long.parseLong(v[lineConfig.nIdOfContractField]);
        }
        if (lineConfig.nDateField!=null) {
            if (lineConfig.nDateField>=v.length) throw new Exception("Позиция "+FIELD_DATE+" больше чем полей в строке: "+v.length);
            dt = v[lineConfig.nDateField];
        }
        if (lineConfig.nSumField!=null) {
            if (lineConfig.nSumField>=v.length) throw new Exception("Позиция "+FIELD_SUM+" больше чем полей в строке: "+v.length);
            String strSum = v[lineConfig.nSumField];
            if (strSum.indexOf('.')!=-1) { sum = (long)(100*Float.parseFloat(v[lineConfig.nSumField])); }
            else sum = Long.parseLong(v[lineConfig.nSumField]);
        }
        if (lineConfig.paymentTransform != null) {
            idOfPayment = lineConfig.paymentTransform + idOfPayment;
        }
        if (lineConfig.idSkipPattern != null && lineConfig.idSkipPattern.length() > 0) {
            Boolean isMatch = true;
            Integer cycleSize =
                    lineConfig.idSkipPattern.length() < idOfPayment.length() ? lineConfig.idSkipPattern.length()
                            : idOfPayment.length();
            for (int i = 0; i < cycleSize; i++) {
                if (idOfPayment.charAt(i) != lineConfig.idSkipPattern.charAt(i)) {
                    isMatch = false;
                    break;
                }
            }
            if (isMatch) {
                idOfPayment = idOfPayment.substring(lineConfig.idSkipPattern.length() - 1, idOfPayment.length());
            }
        }
        if (lineConfig.idSize != null) {
            if (idOfPayment.length() > lineConfig.idSize) {
                idOfPayment = idOfPayment.substring(idOfPayment.length() - lineConfig.idSize, idOfPayment.length());
            }
        }
        String dtNormal = localDateFormat.format(df.parse(dt));
        return new PaymentReconciliationManager.RegistryItem(dt, dtNormal, sum, idOfContract, idOfPayment);
    }

    private void parseLineConfig(LineConfig lineConfig, String currLine) throws Exception {
        // !separators=|@
        // !idOfContract=13
        // !idOfPayment=6
        // !sum=8
        String l = currLine.substring(1).trim();
        int eqpos = l.indexOf('=');
        if (eqpos == -1) {
            throw new Exception("Неправильный формат строки, ожидалось <параметр>=<значение>");
        }
        String n = l.substring(0, eqpos).trim();
        String v = l.substring(eqpos + 1).trim();
        if (n.compareToIgnoreCase(FIELD_ID_OF_CONTRACT) == 0) {
            lineConfig.nIdOfContractField = Integer.parseInt(v);
        } else if (n.compareToIgnoreCase(FIELD_ID_OF_PAYMENT) == 0) {
            lineConfig.nIdOfPaymentField = Integer.parseInt(v);
        } else if (n.compareToIgnoreCase(FIELD_SUM) == 0) {
            lineConfig.nSumField = Integer.parseInt(v);
        } else if (n.compareToIgnoreCase(FIELD_DATE) == 0) {
            lineConfig.nDateField = Integer.parseInt(v);
        } else if (n.compareToIgnoreCase(FIELD_SEPARATORS) == 0) {
            lineConfig.separators = Pattern.compile(v);
        } else if (n.compareToIgnoreCase(PAYMENT_TRANSFORM) == 0) {
            lineConfig.paymentTransform = StringUtils.substringBefore(v, "#");
        } else if (n.compareToIgnoreCase(SKIP_LINE) == 0) {
            String[] linesToSkip = StringUtils.split(v, ',');
            for (String skipLine : linesToSkip) {
                if (StringUtils.trimToEmpty(skipLine).equalsIgnoreCase("first")) {
                    lineConfig.skipFirst = true;
                }
                if (StringUtils.trimToEmpty(skipLine).equalsIgnoreCase("last")) {
                    lineConfig.skipLast = true;
                }
            }
        } else if (n.compareToIgnoreCase(FIELD_ID_SIZE) == 0) {
            lineConfig.idSize = Integer.parseInt(v);
        } else if (n.compareToIgnoreCase(FIELD_ID_SKIP_PATTERN) == 0) {
            lineConfig.idSkipPattern = StringUtils.substringBefore(v, "#");
        } else {
            throw new Exception("Неизвестный параметр: " + n);
        }
    }

    public String exportToFile() {
        if (exportType == 0) {
            return "reconciliationPageDifferCsv";
        } else if (exportType == 1) {
            return "reconciliationPageMissingCsv";
        } else if (exportType == 2) {
            return "reconciliationPageMissingXml";
        } else {
            return "";
        }
    }

    private LineConfig fillDefaultLineConfig() {
        LineConfig lineConfig = new LineConfig();
        try {
            String[] rows = StringUtils.split(settings, "\n");
            for (String row : rows) {
                parseLineConfig(lineConfig, row);
            }
        } catch (Exception ex) {
            printErrorAndClear("Ошибка при обработке конфигурации по-умолчанию.");
            return null;
        }
        return lineConfig;
    }

    private void printErrorAndClear(String error) {
        printError(error);
        registryItems.clear();
        fileItems.clear();
    }

    public Long getCaAgent() {
        return caAgent;
    }

    public void setCaAgent(Long caAgent) {
        this.caAgent = caAgent;
    }

    public Long getCaReceiver() {
        return caReceiver;
    }

    public void setCaReceiver(Long caReceiver) {
        this.caReceiver = caReceiver;
    }

    public Date getDtFrom() {
        return dtFrom;
    }

    public void setDtFrom(Date dtFrom) {
        this.dtFrom = dtFrom;
    }

    public Date getDtTo() {
        return dtTo;
    }

    public void setDtTo(Date dtTo) {
        this.dtTo = dtTo;
    }

    public List<PaymentReconciliationManager.Difference> getDifferencesList() {
        return differencesList;
    }

    public String getDifferencesInfo() {
        return differencesInfo;
    }

    public String getCaAgentName() {
        return caAgentName;
    }

    public void setCaAgentName(String caAgentName) {
        this.caAgentName = caAgentName;
    }

    public String getCaReceiverName() {
        return caReceiverName;
    }

    public void setCaReceiverName(String caReceiverName) {
        this.caReceiverName = caReceiverName;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public int getExportType() {
        return exportType;
    }

    public void setExportType(int exportType) {
        this.exportType = exportType;
    }
}
