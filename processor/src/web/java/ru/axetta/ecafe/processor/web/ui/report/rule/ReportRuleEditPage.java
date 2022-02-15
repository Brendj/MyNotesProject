/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;


import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.report.ReportRuleConstants;
import ru.axetta.ecafe.processor.core.report.RuleConditionItem;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ReportFormatMenu;
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

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private final CCAccountFilter contragentPayAgentFilter = new CCAccountFilter();
    private final CCAccountFilter contragentReceiverFilter = new CCAccountFilter();
    private int receiverSelection; // 0-все контрагенты, 1-Агент по приему платежей, 2-ТСП
    private final ContractFilter contractFilter= new ContractFilter();

    private long idOfReportHandleRule;
    private String ruleName;
    private String tag;
    private String reportType;
    private int documentFormat;
    private String subject;
    private String routeAddresses;
    private Map<String, ReportHandleRuleRoute> routeMap;
    private String ruleConditionItems;
    private String shortName;
    private boolean enabled;
    private final ReportTypeMenu reportTypeMenu = new ReportTypeMenu();
    private final ReportFormatMenu reportFormatMenu = new ReportFormatMenu();
    private String reportTemplateFileName;
    private final ReportTemplateFileNameMenu reportTemplateFileNameMenu = new ReportTemplateFileNameMenu();
    private List<Hint> hints = new ArrayList <> ();
    private boolean manualReportRun = false;
    private long storagePeriod;


    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }

    public CCAccountFilter getContragentPayAgentFilter() {
        return contragentPayAgentFilter;
    }

    public CCAccountFilter getContragentReceiverFilter() {
        return contragentReceiverFilter;
    }

    public ContractFilter getContractFilter() {
        return contractFilter;
    }

    public Object showContragentSelectPage(){
        receiverSelection=0;
        return MainPage.getSessionInstance().showContragentSelectPage();
    }

    public Object showContragentPayAgentSelectPage(){
        receiverSelection=1;
        return MainPage.getSessionInstance().showContragentSelectPage();
    }

    public Object showContragentReceiverSelectPage(){
        receiverSelection=2;
        return MainPage.getSessionInstance().showContragentSelectPage();
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception {
        switch (receiverSelection){
            case 0: contragentFilter.completeContragentSelection(session, idOfContragent); break;
            case 1: contragentPayAgentFilter.completeContragentSelection(session, idOfContragent);break;
            case 2: contragentReceiverFilter.completeContragentSelection(session, idOfContragent);break;
        }
        //contragentFilter.completeContragentSelection(session, idOfContragent);
    }

    public void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes) throws Exception {
        this.contractFilter.completeContractSelection(session, idOfContract, multiContrFlag, classTypes);
    }

    public SelectItem[] getReportTemplatesFiles() {
        if (StringUtils.isEmpty(reportType)) return reportTemplateFileNameMenu.getStringItems();
        return reportTemplateFileNameMenu.getStrItemsForReportType(reportType);
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
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .get(ReportHandleRule.class, idOfReportHandleRule);
        reportHandleRule.setRuleName(this.ruleName);
        reportHandleRule.setTag(this.tag);
        reportHandleRule.setDocumentFormat(this.documentFormat);
        reportHandleRule.setSubject(this.subject);
        reportHandleRule.setEnabled(this.enabled);
        reportHandleRule.setAllowManualReportRun (manualReportRun);
        reportHandleRule.setStoragePeriod(storagePeriod);

        reportHandleRule.setTemplateFileName(this.reportTemplateFileName);

        String[] addressList = this.routeAddresses.split(ReportHandleRule.DELIMETER);

        for (String addr : addressList) {
            if (addr.trim().startsWith("{") && addr.trim().endsWith("}")) {
                boolean ok = false;
                for (String mailListName : ReportHandleRule.MAIL_LIST_NAMES) {
                    if (mailListName.equals(addr)) {
                        ok = true;
                        break;
                    }
                }
                if (!ok) {
                    throw new Exception("Некорректное имя рассылки.");
                }
            }

        }

        for(String addr : addressList){
            String processedAddress = StringUtils.trim(addr);
            ReportHandleRuleRoute route = this.routeMap.get(processedAddress);
            if(route == null){
                route = new ReportHandleRuleRoute(processedAddress, reportHandleRule);
                reportHandleRule.getRoutes().add(route);
                session.save(route);
            }
            this.routeMap.remove(processedAddress);
        }

        for(ReportHandleRuleRoute deleted : this.routeMap.values()){
            reportHandleRule.getRoutes().remove(deleted);
            deleted = (ReportHandleRuleRoute) session.merge(deleted);
            session.delete(deleted);
        }

        Set<RuleCondition> newRuleConditions = new HashSet<>();
        newRuleConditions.add(ReportRuleConstants.buildTypeCondition(reportHandleRule, this.reportType));

        // Собираем строку с условием
        StringBuilder newCondition = new StringBuilder();
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

            //  Проверка контрагента "Агент по приему платежей"
            if (!hint.getHint().isRequired() &&
                    hint.getType().equals(Hint.CONTRAGENT_PAYAGENT) &&
                    (contragentPayAgentFilter.getContragent() == null ||
                            contragentPayAgentFilter.getContragent().getIdOfContragent() == null)) {
                continue;
            }
            //  Проверка контрагента "ТСП"
            if (!hint.getHint().isRequired() &&
                    hint.getType().equals(Hint.CONTRAGENT_RECEIVER) &&
                    (contragentReceiverFilter.getContragent() == null ||
                            contragentReceiverFilter.getContragent().getIdOfContragent() == null)) {
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
            } if (hint.getType().equals(Hint.CONTRAGENT_RECEIVER) &&
                    contragentReceiverFilter.getContragent().getIdOfContragent() != null) {
                newValue.append(contragentReceiverFilter.getContragent().getIdOfContragent());
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
            }

            if (hint.getHint().isRequired() && newValue.length() < 1) {
                throw new Exception(String.format("Отсутствует значение для поля '%s'.",
                        hint.getHint().getParamHint().getDescription()));
            }

            if (newCondition.length() > 0) {
                newCondition.append(";");
            }
            newCondition.append(hint.getHint().getParamHint().getName()).append("=").append(newValue);
        }

        this.ruleConditionItems = newCondition.toString();
        String[] textRuleConditions = this.ruleConditionItems.split(ReportHandleRule.DELIMETER);
        for (String textRuleCondition : textRuleConditions) {
            String trimmedTextRuleCondition = StringUtils.trim(textRuleCondition);
            if (StringUtils.isNotEmpty(trimmedTextRuleCondition)) {
                RuleConditionItem conditionItem = new RuleConditionItem(trimmedTextRuleCondition);
                newRuleConditions.add(new RuleCondition(reportHandleRule, conditionItem.getConditionOperation(),
                        conditionItem.getConditionArgument(), conditionItem.getConditionConstant()));
            }
        }

        Set<RuleCondition> superfluousRuleConditions = new HashSet<>();
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

    private void fill(Session session, ReportHandleRule reportHandleRule) throws Exception {
        this.routeMap = new HashMap<>();
        this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
        this.ruleName = reportHandleRule.getRuleName();
        this.tag = reportHandleRule.getTag();
        storagePeriod = reportHandleRule.getStoragePeriod();

        Set<RuleCondition> ruleConditions = reportHandleRule.getRuleConditions();
        this.reportType = reportHandleRule.findType(session);
        if (null == this.reportType) {
            this.reportType = ReportRuleConstants.UNKNOWN_REPORT_TYPE;
        }

        this.reportTemplateFileName = reportHandleRule.getTemplateFileName();
        this.documentFormat = reportHandleRule.getDocumentFormat();
        this.subject = reportHandleRule.getSubject();

        StringBuilder routeAddresses = new StringBuilder();

        for(ReportHandleRuleRoute r : reportHandleRule.getRoutes()){
            this.routeMap.put(r.getRoute(), r);
            appendNotEmpty(routeAddresses, r.getRoute());
        }

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
                } else if (hint.getHint().getParamHint().getDefaultRule().contains(RuleProcessor.CONTRAGENT_RECEIVER_EXPRESSION)) {
                    fillContragentReceiverHint(getActualHintByName(hint.getHint().getParamHint().getName(), actualRules));
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
            for (RuleCondition tmpRule : actualRules) {
                if (tmpRule.getConditionArgument().equals(defRule.getConditionArgument())) {
                    actRule = tmpRule;
                    break;
                }
            }
            hint.fill (defRule, actRule);
        }
    }

    public RuleCondition getActualHintByName (String name, Set<RuleCondition> actualRules) {
        for (RuleCondition hint : actualRules) {
            if (hint.getConditionArgument().equals(name)) {
                return hint;
            }
        }
        return null;
    }

    public void fillContragentHint (RuleCondition hint) {
        if (hint == null || StringUtils.isEmpty(hint.getConditionConstant())) {
            return;
        }
        try {
            long idOfContragent = Long.parseLong(hint.getConditionConstant());
            Contragent contragent = DAOReadonlyService.getInstance().getContragentById(idOfContragent);
            contragentFilter.completeContragentSelection(contragent);
        } catch (Exception e) {
            logger.error("Failed to parse contragent hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillContragentPayAgentHint (RuleCondition hint) {
        if (hint == null || StringUtils.isEmpty(hint.getConditionConstant())) {
            return;
        }
        try {
            long idOfContragent = Long.parseLong(hint.getConditionConstant());
            Contragent contragent = DAOReadonlyService.getInstance().getContragentById(idOfContragent);
            contragentPayAgentFilter.completeContragentSelection(contragent);
        } catch (Exception e) {
            logger.error("Failed to parse contragent hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillContragentReceiverHint (RuleCondition hint) {
        if (hint == null || StringUtils.isEmpty(hint.getConditionConstant())) {
            return;
        }
        try {
            long idOfContragent = Long.parseLong(hint.getConditionConstant());
            Contragent contragent = DAOReadonlyService.getInstance().getContragentById(idOfContragent);
            contragentReceiverFilter.completeContragentSelection(contragent);
        } catch (Exception e) {
            logger.error("Failed to parse contragent hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillContractHint (RuleCondition hint) {
        if (hint == null || StringUtils.isEmpty(hint.getConditionConstant())) {
            return;
        }
        try {
            long idOfContract = Long.parseLong(hint.getConditionConstant());
            String contractName = DAOReadonlyService.getInstance().getContractNameById (idOfContract);
            contractFilter.completeContractSelection(idOfContract, contractName);
        } catch (Exception e) {
            logger.error("Failed to parse contract hint " + hint.getConditionConstant(), e);
        }
    }

    public void fillOrgsHint (RuleCondition hint) {
        if (hint == null || StringUtils.isEmpty(hint.getConditionConstant())) {
            return;
        }
        try {
            String[] ids = hint.getConditionConstant().split(",");
            Map <Long, String> res = new HashMap<>();
            for (String id : ids) {
                long idOfOrg = Long.parseLong(id);
                Org org = DAOReadonlyService.getInstance().findOrg(idOfOrg);
                res.put(org.getIdOfOrg(), org.getOfficialName());
            }
            completeOrgListSelection(res);
        } catch (Exception e) {
            logger.error("Failed to parse orgs hint " + hint.getConditionConstant(), e);
        }
    }

    private static void appendNotEmpty(StringBuilder stringBuilder, String value) {
        if (StringUtils.isNotEmpty(value)) {
            if (0 != stringBuilder.length()) {
                stringBuilder.append(ReportHandleRule.DELIMETER).append(' ');
            }
            stringBuilder.append(value);
        }
    }
    
    public List<SelectItem> getStoragePeriods() {
        List<SelectItem> items = new LinkedList<>();
        for (ReportHandleRule.StoragePeriods per : ReportHandleRule.StoragePeriods.getPeriods()) {
            items.add(new SelectItem(per.getMilliseconds(), per.getName()));
        }
        return items;
    }

    public long getStoragePeriod() {
        return storagePeriod;
    }

    public void setStoragePeriod(long storagePeriod) {
        this.storagePeriod = storagePeriod;
    }

    public String getMailListNames() {
        return ReportHandleRule.getMailListNames();
    }
}