/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;


import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.ReportRuleConstants;
import ru.axetta.ecafe.processor.web.ui.ReportFormatMenu;
import ru.axetta.ecafe.processor.web.ui.RuleConditionItem;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.contract.ContractSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportRuleEditPage  extends OnlineReportPage
        implements ContragentSelectPage.CompleteHandler, ContractSelectPage.CompleteHandler {

    Logger logger = LoggerFactory.getLogger(ReportRuleEditPage.class);
    public static String DELIMETER = ";";

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private final CCAccountFilter contragentPayAgentFilter = new CCAccountFilter();
    private final ContractFilter contractFilter= new ContractFilter();

    private long idOfReportHandleRule;
    private String ruleName;
    private String tag;
    private String reportType;
    private int documentFormat;
    private String subject;
    private String routeAddresses;
    private String ruleConditionItems;
    private String shortName;
    private boolean enabled;
    private final ReportTypeMenu reportTypeMenu = new ReportTypeMenu();
    private final ReportFormatMenu reportFormatMenu = new ReportFormatMenu();
    private String reportTemplateFileName;
    private final ReportTemplateFileNameMenu reportTemplateFileNameMenu = new ReportTemplateFileNameMenu();
    private List<Hint> hints = new ArrayList <Hint> ();
    private boolean manualReportRun = false;


    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }

    public CCAccountFilter getContragentPayAgentFilter() {
        return contragentPayAgentFilter;
    }

    public ContractFilter getContractFilter() {
        return contractFilter;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        if (classTypes.equals(Contragent.PAY_AGENT+"")) {
            contragentPayAgentFilter.completeContragentSelection(session, idOfContragent);
        } else {
            contragentFilter.completeContragentSelection(session, idOfContragent);
        }
    }

    public void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes) throws Exception {
        this.contractFilter.completeContractSelection(session, idOfContract, multiContrFlag, classTypes);
    }

    public SelectItem[] getReportTemplatesFiles() {
        if (StringUtils.isEmpty(reportType)) return reportTemplateFileNameMenu.getItems();
        return reportTemplateFileNameMenu.getItemsForReportType(reportType);
    }

    public void setReportTemplateFileName(String reportTemplateFileName) {
        this.reportTemplateFileName = reportTemplateFileName;
    }

    public String getReportTemplateFileName() {
        return reportTemplateFileName;
    }

    public String getPageFilename() {
        return "report/rule/edit";
    }

    public ReportRuleEditPage() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isManualReportRun() {
        return manualReportRun;
    }

    public void setManualReportRun(boolean manualReportRun) {
        this.manualReportRun = manualReportRun;
    }

    public List<Hint> getParamHints() {
        return hints==null ? Collections.EMPTY_LIST : hints;
    }

    public ReportTypeMenu getReportTypeMenu() {
        return reportTypeMenu;
    }

    public ReportFormatMenu getReportFormatMenu() {
        return reportFormatMenu;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public long getIdOfReportHandleRule() {
        return idOfReportHandleRule;
    }

    public void setIdOfReportHandleRule(long idOfReportHandleRule) {
        this.idOfReportHandleRule = idOfReportHandleRule;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(int documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRouteAddresses() {
        return routeAddresses;
    }

    public void setRouteAddresses(String routeAdresses) {
        this.routeAddresses = routeAdresses;
    }

    public String getRuleConditionItems() {
        return ruleConditionItems;
    }

    public void setRuleConditionItems(String ruleConditionItems) {
        this.ruleConditionItems = ruleConditionItems;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void fill(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        fill(session, reportHandleRule);
    }

    public void updateReportRule(Session session, Long idOfReportHandleRule) throws Exception {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        String componentId = "in";
        UIComponent c = findComponent(root, componentId);

        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        reportHandleRule.setRuleName(this.ruleName);
        reportHandleRule.setTag(this.tag);
        reportHandleRule.setDocumentFormat(this.documentFormat);
        reportHandleRule.setSubject(this.subject);
        reportHandleRule.setEnabled(this.enabled);
        reportHandleRule.setAllowManualReportRun (manualReportRun);

        reportHandleRule.setTemplateFileName(this.reportTemplateFileName);

        String[] addressList = this.routeAddresses.split(DELIMETER);

        for (String addr : addressList) {
            if (addr.trim().startsWith("{") && addr.trim().endsWith("}")) {
                boolean ok = false;
                for (String mailListName : ReportHandleRule.MAIL_LIST_NAMES) {
                    if (mailListName.equals(addr)) {
                        ok = true;
                        break;
                    }
                }
                if (ok == false) {
                    throw new Exception("Некорректное имя рассылки.");
                }
            }

        }

        reportHandleRule.setRoute0(StringUtils.trim(getString(addressList, 0)));
        reportHandleRule.setRoute1(StringUtils.trim(getString(addressList, 1)));
        reportHandleRule.setRoute2(StringUtils.trim(getString(addressList, 2)));
        reportHandleRule.setRoute3(StringUtils.trim(getString(addressList, 3)));
        reportHandleRule.setRoute4(StringUtils.trim(getString(addressList, 4)));
        reportHandleRule.setRoute5(StringUtils.trim(getString(addressList, 5)));
        reportHandleRule.setRoute6(StringUtils.trim(getString(addressList, 6)));
        reportHandleRule.setRoute7(StringUtils.trim(getString(addressList, 7)));
        reportHandleRule.setRoute8(StringUtils.trim(getString(addressList, 8)));
        reportHandleRule.setRoute9(StringUtils.trim(getString(addressList, 9)));

        Set<RuleCondition> newRuleConditions = new HashSet<RuleCondition>();
        newRuleConditions.add(ReportRuleConstants.buildTypeCondition(reportHandleRule, this.reportType));


        // Собираем строку с условием
        StringBuilder newCondition = new StringBuilder("");
        for (Hint hint : hints) {
            //  Проверяем выбранные значения, если пустые, то пропускаем этот параметр
            if (!hint.getHint().isRequired() &&
                (hint.getValueItems() == null || hint.getValueItems().size() < 1) &&
                (hint.getValue() == null || hint.getValue().length() < 1) &&
                !hint.isSuperType ()) {
                continue;
            }
            //  Проверка контрагента
            if (!hint.getHint().isRequired() &&
                hint.getType().equals(Hint.CONTRAGENT) &&
                (contragentFilter.getContragent() == null ||
                contragentFilter.getContragent().getIdOfContragent() == null)) {
                continue;
            }
            //  Проверка контрагента
            if (!hint.getHint().isRequired() &&
                hint.getType().equals(Hint.CONTRAGENT_PAYAGENT) &&
                (contragentPayAgentFilter.getContragent() == null ||
                        contragentPayAgentFilter.getContragent().getIdOfContragent() == null)) {
                continue;
            }
            //  Проверка контракта
            if (!hint.getHint().isRequired() &&
                hint.getType().equals(Hint.CONTRACT) &&
                (contractFilter.getContract() == null ||
                contractFilter.getContract().getIdOfContract() == null)) {
                continue;
            }
            //  Проверка орга
            if (!hint.getHint().isRequired() &&
                hint.getType().equals(Hint.ORG) &&
                (idOfOrgList == null || idOfOrgList.size() < 1)) {
                continue;
            }
            //  Проверка клиента


            StringBuilder newValue = new StringBuilder();
            if (hint.getValueItems() != null && hint.getValueItems().size() > 0) {
                List <String> items = hint.getValueItems();
                for (int i=0; i<items.size(); i++) {
                    if (i != 0) {
                        newValue.append(",");
                    }
                    newValue.append(items.get(i));
                }
            } else if (hint.getValue() != null && hint.getValue().length() > 0) {
                newValue.append(hint.getValue());
            } else if (hint.getType().equals(Hint.CONTRAGENT) &&
                       contragentFilter.getContragent().getIdOfContragent() != null) {
                newValue.append(contragentFilter.getContragent().getIdOfContragent());
            } else if (hint.getType().equals(Hint.CONTRAGENT_PAYAGENT) &&
                    contragentPayAgentFilter.getContragent().getIdOfContragent() != null) {
                newValue.append(contragentPayAgentFilter.getContragent().getIdOfContragent());
            } else if (hint.getType().equals(Hint.CONTRACT) &&
                    contractFilter.getContract().getIdOfContract() != null) {
                newValue.append(contractFilter.getContract().getIdOfContract());
            } else if (hint.getType().equals(Hint.ORG)) {
                for (int i=0; i<idOfOrgList.size(); i++) {
                    if (i != 0) {
                        newValue.append(",");
                    }
                    newValue.append(idOfOrgList.get(i));
                }
            } else if (hint.getType().equals(Hint.CLIENT)) {
                //  Добавить!
            }

            if (hint.getHint().isRequired() && newValue.length() < 1) {
                throw new Exception(String.format("Отсутствует значение для поля '%s'.",
                        hint.getHint().getParamHint().getDescription()));
            }

            if (newCondition.length() > 0) {
                newCondition.append(";");
            }
            newCondition.append(hint.getHint().getParamHint().getName()).append("=").append(newValue.toString());
        }


        this.ruleConditionItems = newCondition.toString();
        String[] textRuleConditions = this.ruleConditionItems.split(DELIMETER);
        for (String textRuleCondition : textRuleConditions) {
            String trimmedTextRuleCondition = StringUtils.trim(textRuleCondition);
            if (StringUtils.isNotEmpty(trimmedTextRuleCondition)) {
                RuleConditionItem conditionItem = new RuleConditionItem(trimmedTextRuleCondition);
                newRuleConditions.add(new RuleCondition(reportHandleRule, conditionItem.getConditionOperation(),
                        conditionItem.getConditionArgument(), conditionItem.getConditionConstant()));
            }
        }

        Set<RuleCondition> superfluousRuleConditions = new HashSet<RuleCondition>();
        for (RuleCondition ruleCondition : reportHandleRule.getRuleConditions()) {
            if (!newRuleConditions.contains(ruleCondition)) {
                superfluousRuleConditions.add(ruleCondition);
            }
        }

        for (RuleCondition ruleCondition : superfluousRuleConditions) {
            reportHandleRule.removeRuleCondition(ruleCondition);
            session.delete(ruleCondition);
        }

        for (RuleCondition ruleCondition : newRuleConditions) {
            if (!reportHandleRule.getRuleConditions().contains(ruleCondition)) {
                session.save(ruleCondition);
                reportHandleRule.addRuleCondition(ruleCondition);
            }
        }
        session.update(reportHandleRule);
        fill(session, reportHandleRule);
    }

    private static String getString(String[] strings, int i) {
        if (0 <= i && i < strings.length) {
            return strings[i];
        }
        return null;
    }

    private void fill(Session session, ReportHandleRule reportHandleRule) throws Exception {
        this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
        this.ruleName = reportHandleRule.getRuleName();
        this.tag = reportHandleRule.getTag();

        Set<RuleCondition> ruleConditions = reportHandleRule.getRuleConditions();
        this.reportType = reportHandleRule.findType(session);
        if (null == this.reportType) {
            this.reportType = ReportRuleConstants.UNKNOWN_REPORT_TYPE;
        }
        this.reportTemplateFileName = reportHandleRule.getTemplateFileName();
        this.documentFormat = reportHandleRule.getDocumentFormat();
        this.subject = reportHandleRule.getSubject();

        StringBuilder routeAddresses = new StringBuilder();
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute0());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute1());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute2());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute3());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute4());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute5());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute6());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute7());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute8());
        appendNotEmpty(routeAddresses, reportHandleRule.getRoute9());
        this.routeAddresses = routeAddresses.toString();

        StringBuilder ruleConditionItems = new StringBuilder();
        for (RuleCondition currRuleCondition : ruleConditions) {
            if (!currRuleCondition.isTypeCondition()) {
                appendNotEmpty(ruleConditionItems, new RuleConditionItem(currRuleCondition).buildText());
            }
        }
        this.ruleConditionItems = ruleConditionItems.toString();
        this.enabled = reportHandleRule.isEnabled();
        this.shortName = ReportRuleConstants.createShortName(reportHandleRule, 64);
        this.manualReportRun = reportHandleRule.isAllowManualReportRun ();
        fillHints (ruleConditions);
    }

    private void clear () {
        hints.clear();
        contragentFilter.clear();
        contragentPayAgentFilter.clear();
        contractFilter.clear();
        filter = "";
        idOfOrgList = Collections.EMPTY_LIST;
    }

    private void fillHints (Set<RuleCondition> actualRules) {
        //  Если заходят на страницу в первый раз, то делаем выбранным первое правило
        if (reportType == null || reportType.length() < 1) {
            reportType = reportTypeMenu.getItems() [0].getValue().toString();
        }

        clear();
        List <ReportRuleConstants.ParamHintWrapper> hints = ReportRuleConstants.getParamHintsForReportType(reportType);
        for (ReportRuleConstants.ParamHintWrapper h : hints) {
            this.hints.add(new Hint(h));
        }


        //TODO: refactor with ManualReportPage
        for (Hint hint : this.hints) {
            RuleConditionItem defRule = null;
            try {
                if (hint.getHint().getParamHint().getDefaultRule().contains(RuleProcessor.CONTRAGENT_PAYAGENT_EXPRESSION)) {
                    fillContragentPayAgentHint(getActualHintByName(hint.getHint().getParamHint().getName(), actualRules));
                } else if (hint.getHint().getParamHint().getDefaultRule().contains(RuleProcessor.CONTRAGENT_EXPRESSION)) {
                    fillContragentHint(getActualHintByName(hint.getHint().getParamHint().getName(), actualRules));
                } else if (hint.getHint().getParamHint().getName().equals("idOfContragent")) {
                    fillContragentHint(getActualHintByName("idOfContragent", actualRules));
                    continue;
                } else if (hint.getHint().getParamHint().getName().equals("idOfContract")) {
                    fillContractHint(getActualHintByName("idOfContract", actualRules));
                    continue;
                } else if (hint.getHint().getParamHint().getName().equals("idOfOrg")) {
                    fillOrgsHint(getActualHintByName("idOfOrg", actualRules));
                    continue;
                } else if (hint.getHint().getParamHint().getName().equals("idOfClient")) {
                    fillClientHint (getActualHintByName ("idOfClient", actualRules));
                    continue;
                }
                defRule = new RuleConditionItem (hint.getHint().getParamHint().getName() + hint.getHint().getParamHint().getDefaultRule());
            } catch (Exception e) {
                try {
                    defRule = new RuleConditionItem (hint.getHint().getParamHint().getName() + "= " + RuleProcessor.INPUT_EXPRESSION);
                } catch (Exception e2) {
                    continue;
                }
                /*if (hint.getHint().getDefaultRule() == null || hint.getHint().getDefaultRule().length() > 0) {
                    continue;
                }*/
            }
            hint.getHint().getParamHint().setValue(defRule.getConditionOperationText() + " " + defRule.getConditionConstant());

            RuleCondition actRule = null;
            Iterator <RuleCondition> iter = actualRules.iterator();
            while (iter.hasNext()) {
                RuleCondition tmpRule = iter.next();
                if (tmpRule.getConditionArgument().equals(defRule.getConditionArgument())) {
                    actRule = tmpRule;
                    break;
                }
            }
            hint.fill (defRule, actRule);
        }
    }

    public RuleCondition getActualHintByName (String name, Set<RuleCondition> actualRules) {
        Iterator <RuleCondition> hints = actualRules.iterator();
        while (hints.hasNext()) {
            RuleCondition hint = hints.next();
            if (hint.getConditionArgument().equals(name)) {
                return hint;
            }
        }
        return null;
    }

    public void fillContragentHint (RuleCondition hint) {
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

    public void fillContragentPayAgentHint (RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return;
        }
        try {
            long idOfContragent = Long.parseLong(hint.getConditionConstant());
            Contragent contragent = DAOService.getInstance().getContragentById(idOfContragent);
            contragentPayAgentFilter.completeContragentSelection(contragent);
        } catch (Exception e) {
            logger.error("Failed to parse contragent hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillContractHint (RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return;
        }
        try {
            long idOfContract = Long.parseLong(hint.getConditionConstant());
            String contractName = DAOService.getInstance().getContractNameById (idOfContract);
            contractFilter.completeContractSelection(idOfContract, contractName);
        } catch (Exception e) {
            logger.error("Failed to parse contract hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillOrgsHint (RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return;
        }
        try {
            String ids [] = hint.getConditionConstant().split(",");
            Map <Long, String> res = new HashMap <Long, String> ();
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

    public void fillClientHint (RuleCondition hint) {
        if (hint == null) {
            return;
        }

    }

    private static void appendNotEmpty(StringBuilder stringBuilder, String value) {
        if (StringUtils.isNotEmpty(value)) {
            if (0 != stringBuilder.length()) {
                stringBuilder.append(DELIMETER).append(' ');
            }
            stringBuilder.append(value);
        }
    }

    public String getMailListNames() {
        return ReportHandleRule.getMailListNames();
    }

    private UIComponent findComponent(UIComponent c, String id) {
        if (id.equals(c.getId())) {
            return c;
        }
        Iterator<UIComponent> kids = c.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent found = findComponent(kids.next(), id);
            if (found != null) {
                return found;
            }
        }
        return null;
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