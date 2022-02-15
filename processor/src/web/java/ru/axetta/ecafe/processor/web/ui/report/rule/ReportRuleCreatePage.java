/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import org.apache.commons.collections4.CollectionUtils;
import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRuleRoute;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.persistence.User;
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

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportRuleCreatePage  extends OnlineReportPage
        implements ContragentSelectPage.CompleteHandler, ContractSelectPage.CompleteHandler {

    private String ruleName;
    private boolean enabled;
    private String reportType;
    private int documentFormat;
    private String subject;
    private String routeAddresses;
    private String ruleConditionItems;
    private String shortName;
    private final ReportTypeMenu reportTypeMenu = new ReportTypeMenu();
    private final ReportFormatMenu reportFormatMenu = new ReportFormatMenu();
    private String reportTemplateFileName;
    private final ReportTemplateFileNameMenu reportTemplateFileNameMenu = new ReportTemplateFileNameMenu();
    private String tag;
    private boolean manualReportRun = false;
    private long storagePeriod;

    private List<Hint> hints = new LinkedList<>();
    private final CCAccountFilter contragentFilter = new CCAccountFilter();
    private final CCAccountFilter contragentReceiverFilter = new CCAccountFilter();
    private final CCAccountFilter contragentPayAgentFilter = new CCAccountFilter();
    private int receiverSelection; // 0-все контрагенты, 1-Агент по приему платежей, 2-ТСП
    private final ContractFilter contractFilter= new ContractFilter();

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
        return "report/rule/create";
    }

    public boolean isManualReportRun() {
        return manualReportRun;
    }

    public void setManualReportRun(boolean manualReportRun) {
        this.manualReportRun = manualReportRun;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ReportRuleCreatePage() {
        this.documentFormat = 0;
        this.enabled = true;
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


    public CCAccountFilter getContragentFilter() {
        return contragentFilter;
    }

    public CCAccountFilter getContragentReceiverFilter() {
        return contragentReceiverFilter;
    }

    public CCAccountFilter getContragentPayAgentFilter() {
        return contragentPayAgentFilter;
    }

    public ContractFilter getContractFilter() {
        return contractFilter;
    }

    public void showContractSelectPage () {
        MainPage.getSessionInstance().showContractSelectPage(this.contragentFilter.getContragent().getContragentName());
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
            case 0:
                contragentFilter.completeContragentSelection(session, idOfContragent);
                break;
            case 1:
                contragentPayAgentFilter.completeContragentSelection(session, idOfContragent);
                break;
            case 2:
                contragentReceiverFilter.completeContragentSelection(session, idOfContragent);
                break;
        }
        //contragentFilter.completeContragentSelection(session, idOfContragent);
    }

    public void completeContractSelection(Session session, Long idOfContract, int multiContrFlag, String classTypes) throws Exception {
        this.contractFilter.completeContractSelection(session, idOfContract, multiContrFlag, classTypes);
    }

    public void valueChangeListener(ValueChangeEvent event) throws Exception {
        //  Удаляем все существующие элементы
        reportType = event.getNewValue().toString();
        clear();
        parseExecParams ();
    }



    public void fill(Session session, User currentUser) throws Exception {
        this.documentFormat = 0;
        this.ruleName = null;
        this.tag = null;
        this.enabled = false;
        parseExecParams();
    }

    private void clear () {
        hints.clear();
        contragentFilter.clear();
        contragentReceiverFilter.clear();
        contragentPayAgentFilter.clear();
        contractFilter.clear();
        filter = "";
        idOfOrgList = Collections.EMPTY_LIST;
    }

    public void parseExecParams () {
        //  Если заходят на страницу в первый раз, то делаем выбранным первое правило
        if (StringUtils.isEmpty(reportType)) {
            reportType = reportTypeMenu.getItems() [0].getValue().toString();
        }

        clear();
        List <ReportRuleConstants.ParamHintWrapper> hints = ReportRuleConstants.getParamHintsForReportType(reportType);
        for (ReportRuleConstants.ParamHintWrapper h : hints) {
            this.hints.add(new Hint (h));
        }


        for (Hint hint : this.hints) {
            RuleConditionItem defRule = null;
            try {
                defRule = new RuleConditionItem (hint.getHint().getParamHint().getName() + hint.getHint().getParamHint().getDefaultRule());
            } catch (Exception e) {
                if (Hint.isSuperType(Hint.getType(hint.getHint().getParamHint().getName()))) {
                    continue;
                }
                try {
                    defRule = new RuleConditionItem (hint.getHint().getParamHint().getName() + "= " + RuleProcessor.INPUT_EXPRESSION);
                } catch (Exception e2) {
                    continue;
                }
            }
            hint.getHint().getParamHint().setValue(defRule.getConditionOperationText() + " " + defRule.getConditionConstant());

            hint.fill (defRule, null);
        }
    }

    public void createReportRule(Session session) throws Exception {
        String[] addressList = this.routeAddresses.split(ReportHandleRule.DELIMETER);

        ReportHandleRule reportHandleRule = new ReportHandleRule(this.documentFormat, this.subject, this.enabled);
        reportHandleRule.setRuleName(this.ruleName);

        for(String addr : addressList){
            String processedAddress = StringUtils.trim(addr);

            ReportHandleRuleRoute route = new ReportHandleRuleRoute(processedAddress, reportHandleRule);
            reportHandleRule.getRoutes().add(route);
        }

        reportHandleRule.setTag(tag);

        // Собираем строку с условием
        StringBuilder newCondition = new StringBuilder("");
        for (Hint hint : hints) {

            //  Проверяем выбранные значения, если пустые, то пропускаем этот параметр
            if (!hint.getHint().isRequired() &&
                CollectionUtils.isEmpty(hint.getValueItems()) &&
                StringUtils.isEmpty(hint.getValue()) &&
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
            } else if (hint.getType().equals(Hint.CONTRAGENT_RECEIVER) &&
                    contragentReceiverFilter.getContragent().getIdOfContragent() != null) {
                newValue.append(contragentReceiverFilter.getContragent().getIdOfContragent());
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
                if(newValue.length()> RuleCondition.SIZE_OF_CONFITIONCONSTANT_FIELD - 100){
                    newValue.delete(RuleCondition.SIZE_OF_CONFITIONCONSTANT_FIELD - 100, newValue.length() );
                    int i = newValue.lastIndexOf(",");
                    if(i >  0){
                        newValue.delete(i, newValue.length());
                    }
                }
            } else if (hint.getType().equals(Hint.CLIENT)) {
                //  TODO: Добавить!
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
        reportHandleRule.addRuleCondition(ReportRuleConstants.buildTypeCondition(reportHandleRule, this.reportType));
        String[] textRuleConditions = this.ruleConditionItems.split(ReportHandleRule.DELIMETER);
        for (String textRuleCondition : textRuleConditions) {
            String trimmedTextRuleCondition = StringUtils.trim(textRuleCondition);
            if (StringUtils.isNotEmpty(trimmedTextRuleCondition)) {
                RuleConditionItem conditionItem = new RuleConditionItem(trimmedTextRuleCondition);
                reportHandleRule.addRuleCondition(
                        new RuleCondition(reportHandleRule, conditionItem.getConditionOperation(),
                                conditionItem.getConditionArgument(), conditionItem.getConditionConstant()));
            }
        }
        String reportPath = RuntimeContext.getInstance().getAutoReportGenerator().getReportsTemplateFilePath();
        reportHandleRule.setTemplateFileName(this.reportTemplateFileName.substring(reportPath.length()));
        reportHandleRule.setAllowManualReportRun(manualReportRun);
        reportHandleRule.setStoragePeriod(storagePeriod);

        session.save(reportHandleRule);
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public List<Hint> getParamHints() {
        return hints;
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

}