/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import net.sf.jasperreports.engine.JasperPrint;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.BasicJasperReport;
import ru.axetta.ecafe.processor.core.report.BasicReportJob;
import ru.axetta.ecafe.processor.core.report.ReportDocument;
import ru.axetta.ecafe.processor.core.report.ReportsFactory;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ReportFormatMenu;
import ru.axetta.ecafe.processor.web.ui.RuleConditionItem;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contract.ContractFilter;
import ru.axetta.ecafe.processor.web.ui.contract.ContractSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportRuleConstants;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportRuleEditPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 05.06.13
 * Time: 17:42
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ManualReportRunnerPage extends OnlineReportPage
        implements ContragentSelectPage.CompleteHandler, ContractSelectPage.CompleteHandler {

    private UIComponent paramsComponent;

    Logger logger = LoggerFactory.getLogger(ReportRuleEditPage.class);
    private String reportType;

    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private final ContractFilter contractFilter = new ContractFilter();
    private Date generateStartDate = getDefaultStartDate();
    private Date generateEndDate = getDefaultEndDate();

    private List<RuleItem> items = Collections.emptyList();
    private String ruleItem;
    private Long ruleId;
    private int documentFormat;
    private ReportFormatMenu reportFormatMenu;
    private String htmlResult;
    private JasperPrint previousPrint;
    private String previousRuleName;
    private String infoMessage;
    private String errorMessage;

    private List<ReportRuleEditPage.Hint> hints = new ArrayList<ReportRuleEditPage.Hint>();

    @Autowired
    private DAOService proxy;

    @PersistenceContext
    EntityManager em;


    @Override
    //@Transactional -- здесь транзакция не будет работать нежун прокси клас
    public void onShow() throws Exception {
        fill();
    }

    public int getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(int documentFormat) {
        this.documentFormat = documentFormat;
    }

    public ReportFormatMenu getReportFormatMenu() {
        if (reportFormatMenu == null) {
            reportFormatMenu = new ReportFormatMenu();
            //  К стандартному списку добавляем вариант "В репозиторий"
            SelectItem items[] = reportFormatMenu.getItems();
            SelectItem newItems[] = new SelectItem[items.length + 1];
            System.arraycopy(items, 0, newItems, 0, items.length);
            newItems[newItems.length - 1] = new SelectItem(newItems.length - 1, "В репозиторий");
            reportFormatMenu.setItems(newItems);
        }
        return reportFormatMenu;
    }

    public List<ReportRuleEditPage.Hint> getParamHints() {
        return hints == null ? Collections.EMPTY_LIST : hints;
    }

    public void setParamHints(List<ReportRuleEditPage.Hint> hints) {
        this.hints = hints;
    }

    public JasperPrint getPreviousPrint() {
        return previousPrint;
    }

    public boolean getDisplaySettings() {
        return htmlResult == null || htmlResult.length() < 1;
    }

    public String getPageFilename() {
        return "report/online/manual_report_runner";
    }

    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }

    public ContractFilter getContractFilter() {
        return contractFilter;
    }


    public Date getGenerateStartDate() {
        return generateStartDate;
    }

    public void setGenerateStartDate(Date generateStartDate) {
        this.generateStartDate = generateStartDate;
    }

    public Date getGenerateEndDate() {
        return generateEndDate;
    }

    public void setGenerateEndDate(Date generateEndDate) {
        if (generateEndDate != null) {
            localCalendar.setTime(generateEndDate);
            localCalendar.add(Calendar.DAY_OF_MONTH, 1);
            localCalendar.add(Calendar.SECOND, -1);
            this.generateEndDate = localCalendar.getTime();
        } else {
            this.generateEndDate = generateEndDate;
        }
    }

    public UIComponent getParamsComponent() {
        return paramsComponent;
    }

    public void setParamsComponent(UIComponent paramsComponent) {
        this.paramsComponent = paramsComponent;
    }

    public void showContractSelectPage() {
        MainPage.getSessionInstance().showContractSelectPage(this.contragentFilter.getContragent().getContragentName());
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        contragentFilter.completeContragentSelection(session, idOfContragent);
    }

    public void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes)
            throws Exception {
        this.contractFilter.completeContractSelection(session, idOfContract, multiContrFlag, classTypes);
    }

    private void clear() {
        hints.clear();
        contragentFilter.clear();
        contractFilter.clear();
        filter = "";
        idOfOrgList = Collections.EMPTY_LIST;
        ruleId = null;
        /*generateStartDate = getDefaultStartDate();
        generateEndDate = getDefaultEndDate();*/
        reportType = null;
        documentFormat = ReportHandleRule.HTML_FORMAT;
        errorMessage = "";
        infoMessage = "";
    }

    //@Transactional -- необходимо вынести работу с бд в другой утилитный клсасс
    public void valueChangeListener(ValueChangeEvent event) throws Exception {
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

    /*public RuleItem [] getRuleItems () {
        return items.toArray(new RuleItem [items.size()]);
    }*/
    public List<SelectItem> getRuleItems() {
        List<SelectItem> res = new ArrayList<SelectItem>();
        for (RuleItem it : items) {
            res.add(new SelectItem(it.getRuleName(), it.getRuleName()));
        }
        return res;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleItem() {
        return ruleItem;
    }

    public void setRuleItem(String ruleItem) {
        this.ruleItem = ruleItem;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public String displayElement (ReportRuleEditPage.Hint item) {
        if (item.getHint().getParamHint().isHideOnSetup()) {
            return "display: none";
        } else {
            return "";
        }
    }

    @Transactional
    public void fill() throws Exception {
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
            this.hints.add(new ReportRuleEditPage.Hint(h));
        }
        List<RuleCondition> actualRules = proxy.getReportHandlerRules(ruleId);


        for (ReportRuleEditPage.Hint hint : this.hints) {
            RuleConditionItem defRule = null;
            try {
                if (hint.getHint().getParamHint().getName().equals("idOfContragent")) {
                    fillContragentHint(getActualHintByName("idOfContragent", actualRules));
                    continue;
                } else if (hint.getHint().getParamHint().getName().equals("idOfContract")) {
                    fillContractHint(getActualHintByName("idOfContract", actualRules));
                    continue;
                } else if (hint.getHint().getParamHint().getName().equals("idOfOrg")) {
                    fillOrgsHint(getActualHintByName("idOfOrg", actualRules));
                    continue;
                } else if (hint.getHint().getParamHint().getName().equals("idOfClient")) {
                    fillClientHint(getActualHintByName("idOfClient", actualRules));
                    continue;
                }
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

    public RuleCondition getActualHintByName(String name, List<RuleCondition> actualRules) {
        Iterator<RuleCondition> hints = actualRules.iterator();
        while (hints.hasNext()) {
            RuleCondition hint = hints.next();
            if (hint.getConditionArgument().equals(name)) {
                return hint;
            }
        }
        return null;
    }

    public void fillContragentHint(RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return;
        }
        try {
            long idOfContragent = Long.parseLong(hint.getConditionConstant());
            Contragent contragent = DAOService.getInstance().getContragentById(idOfContragent);
            contragentFilter.completeContragentSelection(contragent);
        } catch (Exception e) {
            logger.error("Failed to parse contragent hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillContractHint(RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return;
        }
        try {
            long idOfContract = Long.parseLong(hint.getConditionConstant());
            String contractName = DAOService.getInstance().getContractNameById(idOfContract);
            contractFilter.completeContractSelection(idOfContract, contractName);
        } catch (Exception e) {
            logger.error("Failed to parse contract hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillOrgsHint(RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return;
        }
        try {
            String ids[] = hint.getConditionConstant().split(",");
            Map<Long, String> res = new HashMap<Long, String>();
            for (String id : ids) {
                long idOfOrg = Long.parseLong(id);
                Org org = DAOService.getInstance().getOrg(idOfOrg);
                res.put(org.getIdOfOrg(), org.getOfficialName());
            }
            completeOrgListSelection(res);
        } catch (Exception e) {
            logger.error("Failed to parse orgs hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillClientHint(RuleCondition hint) {
        if (hint == null) {
            return;
        }

    }

    public String getHtmlResult() {
        return htmlResult;
    }

    public void setHtmlResult(String htmlResult) {
        this.htmlResult = htmlResult;
    }


    @Transactional
    public void triggerJob() throws Exception {
        if (ruleId == null) {
            errorMessage = "Необходимо выбрать тип отчета";
            return;
        }
        errorMessage = "";
        infoMessage = "";
        Map<String, List<String>> values = new HashMap<String, List<String>>();

        //  Сохраняем контракты
        for (ReportRuleEditPage.Hint hint : hints) {
            //  Проверяем выбранные значения, если пустые, то пропускаем этот параметр
            if (!hint.getHint().isRequired() &&
                    (hint.getValueItems() == null || hint.getValueItems().size() < 1) &&
                    (hint.getValue() == null || hint.getValue().length() < 1) &&
                    !hint.isSuperType()) {
                continue;
            }
            //  Проверка контрагента
            if (!hint.getHint().isRequired() &&
                    hint.getType().equals(ReportRuleEditPage.Hint.CONTRAGENT) &&
                    (contragentFilter.getContragent() == null
                            || contragentFilter.getContragent().getIdOfContragent() == null)) {
                continue;
            }
            //  Проверка контракта
            if (!hint.getHint().isRequired() &&
                    hint.getType().equals(ReportRuleEditPage.Hint.CONTRAGENT) &&
                    (contractFilter.getContract() == null || contractFilter.getContract().getIdOfContract() == null)) {
                continue;
            }
            //  Проверка орга
            if (!hint.getHint().isRequired() &&
                    hint.getType().equals(ReportRuleEditPage.Hint.ORG) &&
                    (idOfOrgList == null || idOfOrgList.size() < 1)) {
                continue;
            }
            //  Проверка клиента


            List<String> arr = new ArrayList<String>();
            if (hint.getValueItems() != null && hint.getValueItems().size() > 0) {
                List<String> items = hint.getValueItems();
                for (int i = 0; i < items.size(); i++) {
                    arr.add(items.get(i));
                }
            } else if (hint.getValue() != null && hint.getValue().length() > 0) {
                arr.add(hint.getValue());
            } else if (hint.getType().equals(ReportRuleEditPage.Hint.CONTRAGENT)
                    && contragentFilter.getContragent().getIdOfContragent() != null) {
                arr.add("" + contragentFilter.getContragent().getIdOfContragent());
            } else if (hint.getType().equals(ReportRuleEditPage.Hint.CONTRACT)
                    && contractFilter.getContract().getIdOfContract() != null) {
                arr.add("" + contractFilter.getContract().getIdOfContract());
            } else if (hint.getType().equals(ReportRuleEditPage.Hint.ORG)) {
                for (int i = 0; i < idOfOrgList.size(); i++) {
                    arr.add("" + idOfOrgList.get(i));
                }
            } else if (hint.getType().equals(ReportRuleEditPage.Hint.CLIENT)) {
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
                    errorMessage = String.format("Отсутствует значение для поля '%s'.", hint.getHint().getParamHint().getDescription());
                    return;
                }
            }
            values.put(hint.getHint().getParamHint().getName(), arr);
        }


        if (values.get("idOfOrg") != null) {
            //  Если были выбраны орги, то для каждого орга запускаем отчет..
            List<String> idOfOrgList = values.get("idOfOrg");
            for (String idOfOrg : idOfOrgList) {
                Org org = DAOService.getInstance().getOrg(Long.parseLong(idOfOrg));
                buildReport(values, org);
                //  .. но прерываем выполнение, если тип отчета не "В репозиторий" - для всех оргов выполнение только
                //  если требуется экспорт в репозитории
                if (documentFormat != ReportHandleRule.REPOSITORY_FORMAT) {
                    break;
                }
            }
        } else {
            //  Если орги вообще не присутствывали, то просто единаждый выполняем отчет
            buildReport(values, null);
        }
    }


    public void buildReport(Map<String, List<String>> values, Org org) throws Exception {
        //  Строим отчет
        ReportHandleRule rule = DAOService.getInstance().getReportHandleRule(ruleId);
        if (rule == null) {
            return;
        }
        //AutoReportGenerator.JobDetailCreator cr = RuntimeContext.getInstance().getAutoReportGenerator().getReportJobDetailCreator (ManualReportRunnerPage.class);
        BasicReportJob clearReport = ReportsFactory.craeteReportInstance(reportType);
        BasicReportJob.Builder builder = clearReport.createBuilder(rule.getTemplateFileName());
        //  Передаем все необходимые значение
        if (values.get("idOfContragent") != null) {
            List<String> idOfContragentList = values.get("idOfContragent");
            if (idOfContragentList != null && idOfContragentList.size() > 0) {
                Contragent contragent = DAOService.getInstance()
                        .getContragentById(Long.parseLong(idOfContragentList.get(0)));
                builder.setContragent(contragent);
            }
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
        BasicReportJob report = builder.build((Session) em.getDelegate(), generateStartDate, generateEndDate, cal);


        //  Получаем принтер и в зависимости от выбранного типа отчета, выполняем его
        String rootPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportPath();
        DateFormat dateFormat = new SimpleDateFormat();
        DateFormat timeFormat = new SimpleDateFormat();
        BasicJasperReport.ManualBuilder resultBuilder = new BasicJasperReport.ManualBuilder(rootPath, dateFormat, timeFormat);


        //  Сбрасываем свю информацию о предыдущих запусках
        htmlResult = null;
        previousPrint = null;
        previousRuleName = null;
        //  Далее, в зависимости от выбранного формата, обрабатываем выполненный отчет
        if (documentFormat == ReportHandleRule.REPOSITORY_FORMAT) {
            //  Если был выбран экспорт в репозитории, то сохраняем все данные туда
            ReportDocument reportDocument = resultBuilder.buildDocument(rule.getDocumentFormat(), "" + ruleId, report);
            //String subject = RuleProcessor.fillTemplate(rule.getSubject(), props);
            String subject = rule.getRuleName();
            File f = new File(RuntimeContext.getInstance().getAutoReportGenerator().getReportPath());
            String relativeReportFilePath = reportDocument.getReportFile().getAbsolutePath()
                    .substring(f.getAbsolutePath().length());
            //  Информацию о том, что отчет выполнен, добавляем в БД
            DAOService.getInstance()
                    .registerReport(rule.getRuleName(), rule.getDocumentFormat(), subject, report.getGenerateTime(),
                            report.getGenerateDuration(), report.getStartTime(), report.getEndTime(),
                            relativeReportFilePath, org.getOrgNumberInName(),
                            org.getIdOfOrg(), rule.getTag());
            infoMessage = "Отчеты успешно созданы и помещены в репозиторий";
        } else if (documentFormat == ReportHandleRule.HTML_FORMAT) {
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


    @Transactional
    public void triggerXLSRequest() {
        if (previousPrint == null) {
            return;
        }


        ReportHandleRule rule = DAOService.getInstance().getReportHandleRule(ruleId);
        DateFormat dateFormat = new SimpleDateFormat();
        DateFormat timeFormat = new SimpleDateFormat();
        BasicJasperReport.ManualBuilder resultBuilder = new BasicJasperReport.ManualBuilder("", dateFormat, timeFormat);
        exportFile(ReportHandleRule.XLS_FORMAT, null, resultBuilder, previousRuleName, previousPrint);
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
            return stringBuilder.toString();
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

    public static Date getDefaultStartDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.YEAR, 2012);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        return cal.getTime();
    }

    public static Date getDefaultEndDate() {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        return cal.getTime();
    }
}
