/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.ReportRuleConstants;
import ru.axetta.ecafe.processor.core.report.RuleConditionItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportRuleViewPage extends BasicWorkspacePage {

    private long idOfReportHandleRule;
    private String ruleName;
    private String tag;
    private boolean enabled;
    private String reportType;
    private int documentFormat;
    private String subject;
    private List<String> routeAddresses = Collections.emptyList();
    private List<RuleConditionItem> ruleConditionItems = Collections.emptyList();
    private String shortName;
    private List<ReportRuleConstants.ParamHint> paramHints = Collections.emptyList();
    private String reportTemplateFileName;
    private long storagePeriod;
    private boolean manualReportRun = false;

    public String getPageFilename() {
        return "report/rule/view";
    }

    public long getIdOfReportHandleRule() {
        return idOfReportHandleRule;
    }

    public String getRuleName() {
        return ruleName;
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

    public String getReportType() {
        return reportType;
    }

    public int getDocumentFormat() {
        return documentFormat;
    }

    public String getSubject() {
        return subject;
    }

    public List<String> getRouteAddresses() {
        return routeAddresses;
    }

    public List<RuleConditionItem> getRuleConditionItems() {
        return ruleConditionItems;
    }

    public String getShortName() {
        return shortName;
    }
    
    public String getStoragePeriod() {
        for (ReportHandleRule.StoragePeriods per : ReportHandleRule.StoragePeriods.getPeriods()) {
            if (storagePeriod == per.getMilliseconds()) {
                return per.getName();
            }
        }
        return "";
    }

    public List<ReportRuleConstants.ParamHint> getParamHints() {
        return paramHints;
    }

    public void fill(Session session, Long idOfReportHandleRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session
                .load(ReportHandleRule.class, idOfReportHandleRule);
        this.idOfReportHandleRule = reportHandleRule.getIdOfReportHandleRule();
        this.ruleName = reportHandleRule.getRuleName();
        this.tag = reportHandleRule.getTag();
        Set<RuleCondition> actualRules = reportHandleRule.getRuleConditions();
        this.reportType = reportHandleRule.findType(session);
        if (null == this.reportType) {
            this.reportType = ReportRuleConstants.UNKNOWN_REPORT_TYPE;
        }
        this.reportTemplateFileName = reportHandleRule.getTemplateFileName();
        if (null == this.reportTemplateFileName)
            this.reportTemplateFileName = ReportRuleConstants.DEFAULT_REPORT_TEMPLATE;
        this.enabled = reportHandleRule.isEnabled();
        this.manualReportRun = reportHandleRule.isAllowManualReportRun();
        this.documentFormat = reportHandleRule.getDocumentFormat();
        this.subject = reportHandleRule.getSubject();
        this.routeAddresses = new LinkedList<String>();
        this.storagePeriod = reportHandleRule.getStoragePeriod();
        addAddress(this.routeAddresses, reportHandleRule.getRoute0());
        addAddress(this.routeAddresses, reportHandleRule.getRoute1());
        addAddress(this.routeAddresses, reportHandleRule.getRoute2());
        addAddress(this.routeAddresses, reportHandleRule.getRoute3());
        addAddress(this.routeAddresses, reportHandleRule.getRoute4());
        addAddress(this.routeAddresses, reportHandleRule.getRoute5());
        addAddress(this.routeAddresses, reportHandleRule.getRoute6());
        addAddress(this.routeAddresses, reportHandleRule.getRoute7());
        addAddress(this.routeAddresses, reportHandleRule.getRoute8());
        addAddress(this.routeAddresses, reportHandleRule.getRoute9());
        //this.ruleConditionItems = new LinkedList<RuleConditionItem>();
        this.shortName = ReportRuleConstants.createShortName(reportHandleRule, 64);

        this.paramHints = new LinkedList<ReportRuleConstants.ParamHint>();
        ReportRuleConstants.ReportHint defaultRules = ReportRuleConstants.findReportHint(this.reportType);
        for (int i : defaultRules.getParamHints()) {
            ReportRuleConstants.ParamHint hint = ReportRuleConstants.PARAM_HINTS[Math.abs(i)];
            String argument  = "";  // к чему применяется
            String operation = ""; // тип операции
            String constant  = "";  // значение
            for (RuleCondition currRuleCondition : actualRules) {
                if (currRuleCondition.getConditionArgument().equals(hint.getName())) {
                    //  Для всех остальных просто вставляем значение
                    RuleConditionItem conditionItem = new RuleConditionItem(currRuleCondition);
                    argument  = conditionItem.getConditionArgument();  // к чему применяется
                    operation = conditionItem.getConditionOperationText(); // тип операции
                    constant  = conditionItem.getConditionConstant();  // значение

                    //  Дополнительные действия для тех значений, которые необходимо грузить из БД
                    if (hint.getName().equals("idOfContragent")) {
                        constant = getContragentHintValue (getActualHintByName ("idOfContragent", actualRules));
                    } else if (hint.getName().equals("idOfContract")) {
                        constant = getContractHintValue (getActualHintByName ("idOfContract", actualRules));
                    } else if (hint.getName().equals("idOfOrg")) {
                        constant = getOrgsHintValue(getActualHintByName("idOfOrg", actualRules));
                    } else if (hint.getName().equals("idOfClient")) {
                        constant = getClientHintValue (getActualHintByName ("idOfClient", actualRules));
                    }
                }
            }
            String space = " ";
            if (constant.length() < 1) {
                constant = "{Не выбрано}";
                operation = "";
                space = "";
            }
            hint.setValue(operation + space + constant);
            this.paramHints.add(hint);
        }
    }

    public RuleCondition getActualHintByName (String name, Set<RuleCondition> actualRules) {
        Iterator<RuleCondition> hints = actualRules.iterator();
        while (hints.hasNext()) {
            RuleCondition hint = hints.next();
            if (hint.getConditionArgument().equals(name)) {
                return hint;
            }
        }
        return null;
    }

    public String getContragentHintValue (RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return "";
        }
        try {
            long idOfContragent = Long.parseLong(hint.getConditionConstant());
            Contragent contragent = DAOService.getInstance().getContragentById(idOfContragent);
            return contragent.getContragentName();
        } catch (Exception e) {
            //logger.error("Failed to parse contragent hint " + hint.getConditionConstant(), e);
        }
        return "";
    }

    public String getContractHintValue (RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return "";
        }
        try {
            long idOfContract = Long.parseLong(hint.getConditionConstant());
            String contractName = DAOService.getInstance().getContractNameById (idOfContract);
            return contractName;
        } catch (Exception e) {
            //logger.error("Failed to parse contract hint " + hint.getConditionConstant(), e);
        }
        return "";
    }

    public String getOrgsHintValue (RuleCondition hint) {
        if (hint == null || hint.getConditionConstant() == null || hint.getConditionConstant().length() < 1) {
            return "";
        }
        try {
            String ids [] = hint.getConditionConstant().split(",");
            String res = "";
            for (String id : ids) {
                long idOfOrg = Long.parseLong(id);
                Org org = DAOReadonlyService.getInstance().findOrg(idOfOrg);
                if (res.length() > 0) {
                    res = res + ", ";
                }
                res = res + org.getOfficialName();
            }
            return res;
        } catch (Exception e) {
            //logger.error("Failed to parse orgs hint " + hint.getConditionConstant(), e);
        }
    return "";
    }

    public String getClientHintValue (RuleCondition hint) {
        if (hint == null) {
            return "";
        }

        return "";
    }

    private static void addAddress(List<String> addresses, String newAddress) {
        if (StringUtils.isNotEmpty(newAddress)) {
            addresses.add(newAddress);
        }
    }

    public void setReportTemplateFileName(String reportTemplateFileName) {
        this.reportTemplateFileName = reportTemplateFileName;
    }

    public String getReportTemplateFileName() {
        return reportTemplateFileName;
    }

    public String getTag() {
        return tag;
    }
}