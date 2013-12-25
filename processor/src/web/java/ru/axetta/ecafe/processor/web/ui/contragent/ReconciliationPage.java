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
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Component
@Scope("session")
public class ReconciliationPage extends BasicWorkspacePage implements ContragentSelectPage.CompleteHandler {
    final static String FIELD_ID_OF_PAYMENT="idOfPayment", FIELD_ID_OF_CONTRACT="idOfContract", FIELD_SUM="sum",
        FIELD_SEPARATORS="separators", FIELD_DATE="date", PAYMENT_TRANSFORM="paymentTransform", SKIP_LINE="skipLine";
    
    private Long caAgent;
    private Long caReceiver;
    private String caAgentName;
    private String caReceiverName;
    private String registryProcessingError;
    private Date dtFrom = new Date(), dtTo = new Date();
    private List<PaymentReconciliationManager.RegistryItem> registryItems;
    private List<PaymentReconciliationManager.Difference> differencesList;
    private String differencesInfo;
    private String settings;
    private UploadItem item;

    @Override
    public String getPageFilename() {
        return "contragent/reconciliation";
    }

    @Override
    public void onShow() throws Exception {
        super.onShow();
    }
    
    public Object processData() {
        readFile();
        if (registryProcessingError != null) {
            printError(registryProcessingError);
            return null;
        }
        if (registryItems == null) {
            printError("Не загружен реестр платежей");
            return null;
        }
        if (caAgent == null) {
            printError("Не указан агент");
            return null;
        }
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_RECONCILIATION_SETTING, settings);
        PaymentReconciliationManager reconciliationManager = RuntimeContext.getAppContext().getBean(PaymentReconciliationManager.class);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dtFrom);
        resetTime(gc);
        Date dtFrom = gc.getTime();
        gc.setTime(dtTo);
        gc.add(Calendar.HOUR_OF_DAY, 24);
        resetTime(gc);
        Date dtTo = gc.getTime();
        this.differencesInfo = null;
        this.differencesList = null;
        DateFormat df = CalendarUtils.getDateFormatLocal();
        try {
            this.differencesList = reconciliationManager.processRegistry(caAgent, caReceiver, dtFrom, dtTo, registryItems);
            this.differencesInfo = "Результаты сверки реестра за период: " + df.format(this.dtFrom) + " - " + df.format(this.dtTo)
                    + ": записей в реестре - " + registryItems.size() + ", различий - " + differencesList.size();
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при обработке", e);
        }
        return null;
    }

    private void resetTime(GregorianCalendar gc) {
        gc.set(Calendar.MILLISECOND, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.HOUR_OF_DAY, 0);
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
        Integer nIdOfContractField, nIdOfPaymentField, nSumField, nDateField;
        String paymentTransform;
        boolean skipFirst, skipLast;
    }

    public void uploadFileListener(UploadEvent event) {
        item = event.getUploadItem();
    }

    private void readFile() {
        InputStream inputStream = null;
        long dataSize;
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
            loadRegistry(inputStream, dataSize);
            registryProcessingError = null;
        } catch (Exception e) {
            registryProcessingError = "Ошибка при обработке файла реестра: " + e.getMessage();
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
        List<PaymentReconciliationManager.RegistryItem> registryItems = new ArrayList<PaymentReconciliationManager.RegistryItem>();

        int lineNo = 0;
        LineConfig lineConfig = new LineConfig();
        String[] rows = StringUtils.split(settings, "\n");
        for (String row : rows) {
            parseLineConfig(lineConfig, row);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
        String currLine;
        int infoLineCount = 0;
        do {
            currLine = reader.readLine();
            if (currLine != null) {
                ++lineNo;
                try {
                    if (currLine.startsWith("!")) {
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
                        PaymentReconciliationManager.RegistryItem ri = parseLine(lineConfig, currLine, lineNo);
                        registryItems.add(ri);
                    }
                } catch (Exception e) {
                    throw new Exception("Ошибка при обработке строки " + lineNo + ": " + e.getMessage(), e);
                }
            }
        } while (null != currLine);
        this.registryItems = registryItems;
    }

    private PaymentReconciliationManager.RegistryItem parseLine(LineConfig lineConfig, String currLine, int lineNo)
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
        return new PaymentReconciliationManager.RegistryItem(dt, sum, idOfContract, idOfPayment);
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
        } else {
            throw new Exception("Неизвестный параметр: " + n);
        }
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
}
