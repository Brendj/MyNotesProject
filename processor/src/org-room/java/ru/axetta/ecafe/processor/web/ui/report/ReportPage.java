/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.org.Contract;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.auth.LoginBean;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 15.08.13
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ReportPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(ReportPage.class);

    @Autowired
    private ReportDAOService proxy;

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    private String errorMessages;
    private String infoMessages;
    private Calendar generateStartDate;
    private Calendar generateEndDate;
    private String report;
    private String reportHtml;

    private List<Hint> hints = new ArrayList<Hint>();
    private List<RuleItem> items = Collections.emptyList();
    private String ruleItem;
    private Long ruleId;
    private String reportType;

    private String htmlResult;
    private JasperPrint previousPrint;
    private String previousRuleName;
    private String infoMessage;
    private String errorMessage;
    private int documentFormat;




    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    @Transactional
    public void fill() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load claims data", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session) throws Exception {
        generateStartDate = new GregorianCalendar();
        generateStartDate.setTimeInMillis(System.currentTimeMillis());
        generateEndDate = new GregorianCalendar();
        generateEndDate.setTimeInMillis(System.currentTimeMillis());
        resetDate(generateStartDate);
        resetDate(generateEndDate);

        List<RuleItem> newRuleItems = new LinkedList<RuleItem>();
        List<ReportHandleRule> rules = proxy.getReportHandlerRules(true);
        for (ReportHandleRule rule : rules) {
            newRuleItems.add(new RuleItem(rule));
        }
        this.items = newRuleItems;

        //  Если заходят на страницу в первый раз, то делаем выбранным первое правило
        if ((ruleItem == null || ruleItem.length() < 1) && items.size() > 0) {
            ruleItem = items.get(0).getRuleName();
            ruleId = items.get(0).getIdOfReportHandleRule();
            reportType = proxy.getReportHandlerType(ruleId);
        }

        //  Если правило было когда-то установлено, то необходимо переформировать поля под значения по умолчанию
        if (ruleId != null && reportType != null && reportType.length() > 0) {
            this.hints.clear();
            parseExecParams();
        }
    }


    public void parseExecParams() {
        List<ReportRuleConstants.ParamHintWrapper> hints = ReportRuleConstants.getParamHintsForReportType(reportType);
        for (ReportRuleConstants.ParamHintWrapper h : hints) {
            this.hints.add(new Hint(h));
        }
        List<RuleCondition> actualRules = proxy.getReportHandlerRules(ruleId);


        //TODO: refactor with ReportRuleEditPage
        for (Hint hint : this.hints) {
            RuleConditionItem defRule = null;
            try {
                defRule = new RuleConditionItem(
                        hint.getHint().getParamHint().getName() + hint.getHint().getParamHint().getDefaultRule());
            } catch (Exception e) {
                try {
                    defRule = new RuleConditionItem(
                            hint.getHint().getParamHint().getName() + "= " + RuleProcessor.INPUT_EXPRESSION);
                } catch (Exception e2) {
                    continue;
                }
                /*if (hint.getHint().getDefaultRule() == null || hint.getHint().getDefaultRule().length() > 0) {
                    continue;
                }*/
            }
            hint.getHint().getParamHint()
                    .setValue(defRule.getConditionOperationText() + " " + defRule.getConditionConstant());

            RuleCondition actRule = null;
            Iterator<RuleCondition> iter = actualRules.iterator();
            while (iter.hasNext()) {
                RuleCondition tmpRule = iter.next();
                if (tmpRule.getConditionArgument().equals(defRule.getConditionArgument())) {
                    actRule = tmpRule;
                    break;
                }
            }
            hint.fill(defRule, actRule);
        }
    }





    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ReportPage.class).fill();
    }

    public void doChangeReport(ValueChangeEvent event) throws Exception {
        //  Удаляем все существующие элементы
        clear();

        if (event.getNewValue() != null) {
            String newVal = event.getNewValue().toString();
            for (RuleItem it : items) {
                if (it.toString().equals(newVal)) {
                    ruleId = it.getIdOfReportHandleRule();
                    reportType = proxy.getReportHandlerType(ruleId);
                    //documentFormat = proxy.getReportHandleRule(ruleId).getDocumentFormat();
                    break;
                }
            }
            parseExecParams();
        }
    }

    public void doExportToExcel () {
        RuntimeContext.getAppContext().getBean(ReportPage.class).exportToExcel();
    }

    @Transactional
    public void exportToExcel() {
        if (previousPrint == null) {
            return;
        }

        //ReportHandleRule rule = DAOService.getInstance().getReportHandleRule(ruleId);
        DateFormat dateFormat = new SimpleDateFormat();
        DateFormat timeFormat = new SimpleDateFormat();
        BasicJasperReport.ManualBuilder resultBuilder = new BasicJasperReport.ManualBuilder("", dateFormat, timeFormat);
        exportFile(ReportHandleRule.XLS_FORMAT, null, resultBuilder, previousRuleName, previousPrint);
    }

    public void doGenerateReport (){
        RuntimeContext.getAppContext().getBean(ReportPage.class).generateReport();
    }

    @Transactional
    public void generateReport () {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            Org org = entityManager.merge(RuntimeContext.getAppContext().getBean(LoginBean.class).getOrg());  //  Получаем Org от авторизованного клиента
            generateReport(session, org);
        } catch (Exception e) {
            logger.error("Failed to generate report", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void generateReport (Session session, Org org) {
        if (ruleId == null) {
            errorMessages = "Необходимо выбрать тип отчета";
            return;
        }
        errorMessages = "";
        infoMessages = "";
        Map<String, List<String>> values = new HashMap<String, List<String>>();

        //  Загружаем объекты, закрепленные за оргом
        Contragent contragent = org.getDefaultSupplier();
        Contract contract = org.getContract();


        //  Сохраняем контракты
        for (Hint hint : hints) {
            //  Проверяем выбранные значения, если пустые, то пропускаем этот параметр
            if (!hint.getHint().isRequired() &&
                    (hint.getValueItems() == null || hint.getValueItems().size() < 1) &&
                    (hint.getValue() == null || hint.getValue().length() < 1) &&
                    !hint.isSuperType()) {
                continue;
            }


            List<String> arr = new ArrayList<String>();
            if (hint.getValueItems() != null && hint.getValueItems().size() > 0) {
                List<String> items = hint.getValueItems();
                for (int i = 0; i < items.size(); i++) {
                    arr.add(items.get(i));
                }
            } else if (hint.getValue() != null && hint.getValue().length() > 0) {
                arr.add(hint.getValue());
            } else if (hint.getType().equals(Hint.CONTRAGENT)) {
                arr.add("" + contragent.getIdOfContragent());
            } else if (hint.getType().equals(Hint.CONTRAGENT_PAYAGENT)) {
                arr.add("" + contragent.getIdOfContragent());
            } else if (hint.getType().equals(Hint.CONTRACT)) {
                arr.add("" + contract.getIdOfContract());
            } else if (hint.getType().equals(Hint.ORG)) {
                arr.add("" + org.getIdOfOrg());
            } else if (hint.getType().equals(Hint.CLIENT)) {
                //  Добавить!
            }
            if (hint.getHint().isRequired()) {
                boolean valueExists = false;
                for (String v : arr) {
                    if (v != null && v.length() > 0) {
                        valueExists = true;
                        break;
                    }
                }
                if (!valueExists) {
                    errorMessage = String.format("Отсутствует значение для поля '%s'.",
                            hint.getHint().getParamHint().getDescription());
                    return;
                }
            }
            values.put(hint.getHint().getParamHint().getName(), arr);
        }


        try {
            buildReport(values, org, contragent, contract, session);
        } catch (Exception e) {
            errorMessages = String.format("Во время выполнения отчета, возникла ошибка: %s",
                    e.getMessage());
            logger.error("Filed to generate report", e);
            return;
        }
    }


    public void buildReport(Map<String, List<String>> values, Org org, Contragent contragent, Contract contract, Session session) throws Exception {
        //  Строим отчет
        ReportHandleRule rule = DAOReadonlyService.getInstance().getReportHandleRule(ruleId);
        if (rule == null) {
            return;
        }
        //AutoReportGenerator.JobDetailCreator cr = RuntimeContext.getInstance().getAutoReportGenerator().getReportJobDetailCreator (ManualReportRunnerPage.class);
        BasicReportJob clearReport = ReportsFactory.craeteReportInstance(reportType);
        String reportPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        BasicReportJob.Builder builder = clearReport.createBuilder(reportPath + rule.getTemplateFileName());
        //  Передаем все необходимые значение
        if (contragent != null) {
            builder.setContragent(contragent);
        }
        if (org != null) {
            builder.setOrg(
                    new BasicReportJob.OrgShortItem(org.getIdOfOrg(), org.getShortName(), org.getOfficialName()));
        }
        Properties props = new Properties();
        for (String k : values.keySet()) {
            List<String> vals = values.get(k);
            String v = "";
            for (String val : vals) {
                if (v.length() > 0) {
                    v = v + ",";
                }
                v = v + val;
            }
            props.setProperty(k, v);
        }
        builder.setReportProperties(props);
        Calendar cal = new GregorianCalendar();
        //  и запускаем
        BasicReportJob report = builder.build(session,
                generateStartDate.getTime(), generateEndDate.getTime(), cal);


        //  Получаем принтер и в зависимости от выбранного типа отчета, выполняем его
        //String rootPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportPath();
        DateFormat dateFormat = new SimpleDateFormat();
        DateFormat timeFormat = new SimpleDateFormat();
        BasicJasperReport.ManualBuilder resultBuilder = new BasicJasperReport.ManualBuilder("", dateFormat,
                timeFormat);


        //  Сбрасываем свю информацию о предыдущих запусках
        htmlResult = null;
        previousPrint = null;
        previousRuleName = null;
        //  Далее, в зависимости от выбранного формата, обрабатываем выполненный отчет
        if (documentFormat == ReportHandleRule.HTML_FORMAT) {
            //  Если выбран html, то необходимо выполнить отчет в строку и отобразить его на странице, а так же, добавить
            //  принтер, чтобы можно было без перезапуска отчета, выполнить его в XLS
            htmlResult = resultBuilder.generateDocument(documentFormat, report);
            previousPrint = report.getPrint();
            previousRuleName = rule.getRuleName();
        } else {
            //  Если это не HTML, то необходимо создавать собственный output stream и выводить результат в него
            exportFile(report, resultBuilder, rule);
        }
    }


    public void exportFile(BasicReportJob report, BasicJasperReport.ManualBuilder resultBuilder,
            ReportHandleRule rule) {
        exportFile(documentFormat, report, resultBuilder, rule.getRuleName(), null);
    }


    public void exportFile(int documentFormat, BasicReportJob report, BasicJasperReport.ManualBuilder resultBuilder,
            String ruleName, JasperPrint print) {
        if (report == null && print == null) {
            return;
        }

        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            // Header
            String ext = "";
            String contentType = "";
            if (documentFormat == ReportHandleRule.XLS_FORMAT) {
                ext = "xls";
                contentType = "application/vnd.ms-excel";
            } else if (documentFormat == ReportHandleRule.CSV_FORMAT) {
                ext = "csv";
                contentType = "text/csv";
            } else if (documentFormat == ReportHandleRule.PDF_FORMAT) {
                ext = "pdf";
                contentType = "application/pdf";
            }
            response.setHeader("Content-disposition", String.format("attachment; filename=%s.%s", ruleName, ext));
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setCharacterEncoding("UTF-8");
            response.setContentType(contentType);

            //Send Response
            final ServletOutputStream responseOutputStream = response.getOutputStream();
            try {
                if (report != null) {
                    resultBuilder.generateDocument(documentFormat, report, responseOutputStream);
                } else {
                    resultBuilder.generateDocument(documentFormat, print, responseOutputStream);
                }
                responseOutputStream.flush();
            } finally {
                responseOutputStream.close();
            }
            facesContext.responseComplete();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Date getGenerateEndDate() {
        return generateEndDate.getTime();
    }

    public void setGenerateEndDate(Date generateEndDate) {
        this.generateEndDate.setTimeInMillis(generateEndDate.getTime());
    }

    public Date getGenerateStartDate() {
        return generateStartDate.getTime();
    }

    public void setGenerateStartDate(Date generateStartDate) {
        this.generateStartDate.setTimeInMillis(generateStartDate.getTime());
    }
    
    public String getReport() {
        return report;
    }
    
    public void setReport(String report) {
        this.report = report;
    }
    public List<SelectItem> getReports() {
        List<SelectItem> res = new ArrayList<SelectItem>();
        for (RuleItem it : items) {
            res.add(new SelectItem(it.getRuleName(), it.getRuleName()));
        }
        return res;
    }

    public String getReportHtml() {
        return htmlResult;
    }

    public boolean getDisplaySettings() {
        return htmlResult == null || htmlResult.length() < 1;
    }

    public JasperPrint getPreviousPrint() {
        return previousPrint;
    }

    public String displayElement(Hint item) {
        if (item.getHint().getParamHint().isHideOnSetup()) {
            return "display: none";
        } else {
            return "";
        }
    }

    public boolean renderElement(Hint item) {
        if (item.getHint().getParamHint().isHideOnSetup() ||
            item.getType().equals(Hint.CONTRAGENT) ||
            item.getType().equals(Hint.CONTRAGENT_PAYAGENT) ||
            item.getType().equals(Hint.CONTRACT) ||
            item.getType().equals(Hint.ORG) ||
            item.getType().equals(Hint.CLIENT)) {
            return false;
        } else {
            return true;
        }
    }

    public boolean getRenderParamHints() {
        List<Hint> params = getParamHints();
        for (Hint p : params) {
            if (renderElement(p)) {
                return true;
            }
        }
        return false;
    }

    public List<Hint> getParamHints() {
        return hints == null ? Collections.EMPTY_LIST : hints;
    }







    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public String getPageFilename() {
        return "report/report";
    }

    public String getPageTitle() {
        return "Формирование отчета";
    }

    public void resetMessages() {
        errorMessages = "";
        infoMessages = "";
    }

    public void sendError(String message) {
        errorMessages = message;
    }

    public void sendInfo(String message) {
        infoMessages = message;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

    private void resetDate (Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    private void clear() {
        hints.clear();
        ruleId = null;
        /*generateStartDate = getDefaultStartDate();
        generateEndDate = getDefaultEndDate();*/
        reportType = null;
        documentFormat = ReportHandleRule.HTML_FORMAT;
        errorMessage = "";
        infoMessage = "";
    }



    public static class RuleItem {

        public String ruleName;
        public Long idOfReportHandleRule;
        private static final int RULE_NAME_MAX_LEN = 50;


        public RuleItem(ReportHandleRule reportHandleRule) {
            this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
            this.ruleName = cutRuleName(reportHandleRule.getRuleName());
        }

        private static String cutRuleName(String ruleName) {
            StringBuilder stringBuilder = new StringBuilder();
            if (StringUtils.isNotEmpty(ruleName)) {
                stringBuilder.append(ruleName);
            }
            int len = stringBuilder.length();
            if (len > RULE_NAME_MAX_LEN) {
                return stringBuilder.substring(0, RULE_NAME_MAX_LEN - ReportRuleConstants.ELIDE_FILL.length())
                        + ReportRuleConstants.ELIDE_FILL;
            }
            return stringBuilder.   toString();
        }

        public Long getIdOfReportHandleRule() {
            return idOfReportHandleRule;
        }

        public String getRuleName() {
            return ruleName;
        }

        @Override
        public String toString() {
            return ruleName;
        }
    }





    public static class Hint
    {
        //  Типы
        public static final String CONTRAGENT = "contragent";
        public static final String CONTRAGENT_PAYAGENT = "contragent-payagent";
        public static final String CONTRACT   = "contract";
        public static final String ORG        = "org";
        public static final String CLIENT     = "client";

        //
        private ReportRuleConstants.ParamHintWrapper hint;
        private String value;
        private List <SelectItem> listItems = new ArrayList <SelectItem> ();
        private List <String> valueItems = new ArrayList <String> ();
        private String type;

        public Hint (ReportRuleConstants.ParamHintWrapper hint) {
            this.hint = hint;
            value = "";
        }

        public void fill (RuleConditionItem defaultRule, RuleCondition actualRule) {
            Map <String, String> defParams = RuleProcessor.getParametersFromString(defaultRule.getConditionConstant());

            if (defaultRule.getConditionConstant().startsWith(RuleProcessor.CONTRAGENT_PAYAGENT_EXPRESSION)) {
                type = "contragent-payagent";
            }
            else if (defaultRule.getConditionConstant().startsWith(RuleProcessor.CONTRAGENT_EXPRESSION)) {
                type = "contragent";
            }
            else if (defaultRule.getConditionConstant().startsWith(RuleProcessor.ORG_EXPRESSION)) {
                type = "org";
            }
            else if (defaultRule.getConditionConstant().startsWith(RuleProcessor.CONTRAGENT_EXPRESSION)) {
                type = "contragent";
            }
            else if (defaultRule.getConditionConstant().indexOf(RuleProcessor.COMBOBOX_EXPRESSION) >= 0) {
                type = "combobox";
                SelectItem emptyItem = new SelectItem("", "");
                listItems.add(emptyItem);
                for (String key : defParams.keySet()) {
                    String val = defParams.get(key);
                    SelectItem item = new SelectItem(key, val);
                    listItems.add(item);
                }
                if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                    value = actualRule.getConditionConstant();
                }
            } else if (defaultRule.getConditionConstant().indexOf(RuleProcessor.CHECKBOX_EXPRESSION) >= 0) {
                type = "checkbox";
                for (String key : defParams.keySet()) {
                    String val = defParams.get(key);
                    SelectItem item = new SelectItem(key, val);
                    listItems.add(item);
                }
                if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                    String vals [] = actualRule.getConditionConstant().split(",");
                    for (String v : vals) {
                        valueItems.add(v);
                    }
                }
            } else if (defaultRule.getConditionConstant().indexOf(RuleProcessor.RADIO_EXPRESSION) >= 0) {
                type = "radio";
                for (String key : defParams.keySet()) {
                    String val = defParams.get(key);
                    SelectItem item = new SelectItem(key, val);
                    listItems.add(item);
                }
                if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                    value = actualRule.getConditionConstant();
                }
            } else if (defaultRule.getConditionConstant().indexOf(RuleProcessor.INPUT_EXPRESSION) >= 0) {
                type = "input";

                for (String key : defParams.keySet()) {
                    value = defParams.get(key);
                    break;
                }
                if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                    value = actualRule.getConditionConstant();
                }
            } else {
                type = "output";
                for (String key : defParams.keySet()) {
                    value = defParams.get(key);
                    break;
                }
                if (actualRule != null && actualRule.getConditionConstant().length() > 0) {
                    value = actualRule.getConditionConstant();
                }
            }
        }

        public ReportRuleConstants.ParamHintWrapper getHint() {
            return hint;
        }

        public void setHint(ReportRuleConstants.ParamHintWrapper hint) {
            this.hint = hint;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public List<SelectItem> getListItems() {
            return listItems;
        }

        public void setListItems(List<SelectItem> listItems) {
            this.listItems = listItems;
        }

        public List<String> getValueItems() {
            return valueItems;
        }

        public void setValueItems(List<String> valueItems) {
            this.valueItems = valueItems;
        }

        public String getType () {
            if (type != null) {
                return type;
            }
            return getType(hint.getParamHint().getName());
        }

        public boolean isSuperType () {
            return isSuperType(getType());
        }

        public static String getType (String name) {
            if (name.equals("idOfContragent")) {
                return CONTRAGENT;
            } else if (name.equals("idOfContract")) {
                return CONTRACT;
            } else if (name.equals("idOfOrg")) {
                return ORG;
            } else if (name.equals("idOfClient")) {
                return CLIENT;
            }
            return "";
        }

        public static boolean isSuperType (String type) {
            if (type.equals(CONTRAGENT) || type.equals(CONTRAGENT_PAYAGENT) || type.equals(CONTRACT) ||
                    type.equals(ORG) || type.equals(CLIENT)) {
                return true;
            }
            return false;
        }
    }
}