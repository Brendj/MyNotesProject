/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.report.ContragentPaymentReport;
import ru.axetta.ecafe.processor.core.report.kzn.SalesReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderCategoryReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderReport;
import ru.axetta.ecafe.processor.core.report.msc.BeneficiarySummaryReport;
import ru.axetta.ecafe.processor.core.report.msc.HalfYearSummaryReport;
import ru.axetta.ecafe.processor.core.report.msc.MscSalesReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;

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
public class ReportRuleConstants {

    public static final String ELIDE_FILL = "...";
    public static final String UNKNOWN_REPORT_TYPE = "Неизвестный";
    public static final String DEFAULT_REPORT_TEMPLATE = "По умолчанию";

    static public String createShortName(ReportHandleRule reportHandleRule, int maxLen) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(reportHandleRule.getIdOfReportHandleRule().toString());
        String ruleName = reportHandleRule.getRuleName();
        if (StringUtils.isNotEmpty(ruleName)) {
            stringBuilder.append(": ").append(ruleName);
        }
        int len = stringBuilder.length();
        if (len > maxLen) {
            return stringBuilder.substring(0, maxLen - ELIDE_FILL.length()) + ELIDE_FILL;
        }
        return stringBuilder.toString();
    }

    public static class ParamHint {

        private final String name;
        private final String description;

        public ParamHint(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class ReportHint {

        private final String typeName;
        private final int[] paramHints;

        public ReportHint(String typeName, int[] paramHints) {
            this.typeName = typeName;
            this.paramHints = paramHints;
        }

        public String getTypeName() {
            return typeName;
        }

        public int[] getParamHints() {
            return paramHints;
        }
    }

    public static final ParamHint[] PARAM_HINTS = {
            new ParamHint("generateDate", "Дата генерации отчета"),
            new ParamHint("generateTime", "Дата и время генерации отчета"),
            new ParamHint("generateDurationMillis", "Продолжительность генерации отчета в миллисекундах"),
            new ParamHint("idOfOrg", "Идентификатор организации"),
            new ParamHint("shortName", "Краткое название организации"),
            new ParamHint("officialName", "Официальное название организации"),
            new ParamHint("groupName", "Название класса"),
            new ParamHint("idOfClient", "Идентификатор клиента"),
            new ParamHint("email", "Адрес электронной почты клиента"),
            new ParamHint("contractPerson.surname", "Фамилия физического лица, заключившего контракт"),
            new ParamHint("contractPerson.firstName", "Имя физического лица, заключившего контракт"),
            new ParamHint("contractPerson.secondName", "Отчество физического лица, заключившего контракт"),
            new ParamHint("contractPerson.abbreviation", "Фамилия И.О. физического лица, заключившего контракт"),
            new ParamHint("person.surname", "Фамилия обслуживаемого физического лица"),
            new ParamHint("person.firstName", "Имя обслуживаемого физического лица"),
            new ParamHint("person.secondName", "Отчество обслуживаемого физического лица"),
            new ParamHint("person.abbreviation", "Фамилия И.О. обслуживаемого физического лица"),
            new ParamHint("phone", "Телефонный номер клиента"),
            new ParamHint("mobile", "Номер мобильного телефона клиента"), new ParamHint("address", "Адрес клиента"),
            new ParamHint("idOfContragent", "Идентификатор контрагента"), //20
            new ParamHint("contragentName", "Название контрагента"),
            new ParamHint("category", "Категория организации"),
            new ParamHint("idOfMenuSourceOrg", "Идентификатор организации - источника меню"),
            new ParamHint("enterEventType", "Тип отчета по посещаемости: все/учащиеся/все_без_учащихся"),
            new ParamHint("groupByMenuGroup", "Группировка отчета по товарным группам"), //25
            new ParamHint(DailySalesByGroupsReport.PARAM_MENU_GROUPS, "Группы меню"),
            new ParamHint(DailySalesByGroupsReport.PARAM_INCLUDE_COMPLEX, "Включать комплексы"),
            new ParamHint(ReportPropertiesUtils.P_REPORT_PERIOD, "Количество дней в выборке"),
            new ParamHint(ReportPropertiesUtils.P_JOB_NAME, "Название задачи"),
            new ParamHint(ContragentPaymentReport.PARAM_CONTRAGENT_RECEIVER_ID, "Идентификатор контрагента-получателя"), //30

    };

    public static final ReportHint[] REPORT_HINTS = {
            new ReportHint(OrgBalanceReport.class.getCanonicalName(), new int[]{28, 29, 3, 4, 5, 22, 23}),
            new ReportHint(ClientGroupBalanceReport.class.getCanonicalName(), new int[]{28, 29, 3, 4, 5, 6, 22, 23}),
            new ReportHint(OrgBalanceJasperReport.class.getCanonicalName(), new int[]{28, 29, 3, 4, 5, 22, 23}),
            new ReportHint(ContragentOrderReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23}),
            new ReportHint(ContragentOrderCategoryReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23}),
            new ReportHint(OrgOrderCategoryReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23}),
            new ReportHint(SalesReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23}),
            new ReportHint(MscSalesReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23}),
            new ReportHint(RegisterReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23}),
            new ReportHint(ClientsReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23}),
            new ReportHint(OrgOrderByDaysReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23}),
            new ReportHint(AutoEnterEventReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23, 24}),
            new ReportHint(AutoEnterEventByDaysReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23, 24}),
            new ReportHint(DailySalesByGroupsReport.class.getCanonicalName(), new int[]{28, 29, 3, 22, 23, 25, 26, 27}),
            new ReportHint(ClientOrderDetailsByOneOrgReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ComplaintCountByGoodReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ComplaintCausesReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ComplaintIterationsReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ProductPopularityReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(QuestionaryResultByOrgReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(ClientSelectedAnswerResultByOrgReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(MenuDetailsGroupByMenuOriginReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(ClientOrderDetailsByAllOrgReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(OrderDetailsGroupByMenuOriginReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(ContragentPaymentReport.class.getCanonicalName(), new int[]{20, 21, 30}),
            new ReportHint(HalfYearSummaryReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(BeneficiarySummaryReport.class.getCanonicalName(), new int[]{})
    };

    private ReportRuleConstants() {

    }

    public static RuleCondition buildTypeCondition(ReportHandleRule reportHandleRule, String reportType) {
        return new RuleCondition(reportHandleRule, RuleCondition.EQUAL_OPERTAION, RuleCondition.TYPE_CONDITION_ARG,
                reportType);
    }

    public static ReportHint findReportHint(String reportTypeName) {
        for (ReportHint reportHint : REPORT_HINTS) {
            if (StringUtils.equals(reportHint.getTypeName(), reportTypeName)) {
                return reportHint;
            }
        }
        return null;
    }
    
    public static class ReportParamHint {

        private final String typeName;
        private final List<ReportRuleConstants.ParamHint> paramHints;

        public ReportParamHint(ReportRuleConstants.ReportHint reportHint) {
            this.typeName = reportHint.getTypeName();
            this.paramHints = new LinkedList<ReportRuleConstants.ParamHint>();
            for (int i : reportHint.getParamHints()) {
                this.paramHints.add(ReportRuleConstants.PARAM_HINTS[i]);
            }
        }

        public String getTypeName() {
            return typeName;
        }

        public List<ReportRuleConstants.ParamHint> getParamHints() {
            return paramHints;
        }
    }
    
    public static List<ParamHint> getParamHintsForReportType(String reportType) {
        ReportHint hint = findReportHint(reportType);
        if (hint==null) return Collections.emptyList();
        else return new ReportParamHint(hint).getParamHints();
    }

}