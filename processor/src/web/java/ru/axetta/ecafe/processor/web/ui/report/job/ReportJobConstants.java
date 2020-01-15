/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.job;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.report.feeding.SubscriptionFeedingJasperReport;
import ru.axetta.ecafe.processor.core.report.kzn.SalesReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderCategoryReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderReport;
import ru.axetta.ecafe.processor.core.report.msc.BeneficiarySummaryReport;
import ru.axetta.ecafe.processor.core.report.msc.HalfYearSummaryReport;
import ru.axetta.ecafe.processor.core.report.msc.MscSalesReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ReportJobConstants {

    public static final String ELIDE_FILL = "...";
    public static final String UNKNOWN_REPORT_TYPE = "Неизвестный";

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
        private String defaultRule;

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

        public String getDefaultRule() {
            return defaultRule;
        }

        public ParamHint setDefaultRule(String defaultRule) {
            this.defaultRule = defaultRule;
            return this;
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
            new ParamHint("generateDate", "Дата генерации отчета"),  //0
            new ParamHint("generateTime", "Дата и время генерации отчета"),
            new ParamHint("generateDurationMillis", "Продолжительность генерации отчета в миллисекундах"),
            new ParamHint("idOfOrg", "Идентификатор организации"),
            new ParamHint("shortName", "Краткое название организации"),
            new ParamHint("officialName", "Официальное название организации"),//5
            new ParamHint("groupName", "Название класса"),
            new ParamHint("idOfClient", "Идентификатор клиента"),
            new ParamHint("email", "Адрес электронной почты клиента"),
            new ParamHint("contractPerson.surname", "Фамилия физического лица, заключившего контракт"),
            new ParamHint("contractPerson.firstName", "Имя физического лица, заключившего контракт"),//10
            new ParamHint("contractPerson.secondName", "Отчество физического лица, заключившего контракт"),
            new ParamHint("contractPerson.abbreviation", "Фамилия И.О. физического лица, заключившего контракт"),
            new ParamHint("person.surname", "Фамилия обслуживаемого физического лица"),
            new ParamHint("person.firstName", "Имя обслуживаемого физического лица"),
            new ParamHint("person.secondName", "Отчество обслуживаемого физического лица"),              //15
            new ParamHint("person.abbreviation", "Фамилия И.О. обслуживаемого физического лица"),
            new ParamHint("phone", "Телефонный номер клиента"),
            new ParamHint("mobile", "Номер мобильного телефона клиента"),
            new ParamHint("address", "Адрес клиента"),
            new ParamHint("idOfContragent", "Идентификатор контрагента"),//20
            new ParamHint("contragentName", "Название контрагента"),
            new ParamHint("category", "Категория организации"),
            new ParamHint("idOfMenuSourceOrg", "Идентификатор организации - источника меню"),
            new ParamHint("enterEventType", "Тип отчета по посещаемости: все/учащиеся/все_без_учащихся"),
            new ParamHint("groupByMenuGroup", "Группировка отчета по товарным группам"),//25
            new ParamHint(ReportPropertiesUtils.P_REPORT_PERIOD_TYPE, "Тип периода"),
            new ParamHint("goodName", "Наименование товара").setDefaultRule("= " + RuleProcessor.INPUT_EXPRESSION),
            new ParamHint("hideMissedColumns", "Скрывать даты с пустыми значениями").setDefaultRule("= " + RuleProcessor.CHECKBOX_EXPRESSION + "{false}Не скрывать,{true}Скрывать"),
            new ParamHint("goodsFilter", "Фильтры по заявкам").setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION + "{3}Отображать организации с отсутствием заявок за последние 2 дня,{2}Только пустые,{0}Все,{1}Только с данными"),
            new ParamHint("showDailySample", "Суточная проба").setDefaultRule("= " + RuleProcessor.CHECKBOX_EXPRESSION + "{false}Не выводить,{true}Выводить"), //30
            new ParamHint(DashboardByAllOrgReport.P_ORG_STATE, "Статус")//31
    };
    // !!!!!!!! ДЛЯ ТЕСТА !!!!!!!!!!
            /*new ParamHint("idOfContract", "Контракт"),
            new ParamHint("listValue", "Какое-то значение из списка").setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION + "{111}один,{222}два,{333}три"),
            new ParamHint("checkValue", "Какое-то значение по чекбоксу").setDefaultRule("= " + RuleProcessor.CHECKBOX_EXPRESSION + "{555}пять,{666}шесть,{777}семь"),
            new ParamHint("methodValue", "Какое-то значение из метода").setDefaultRule("= " + RuleProcessor.METHOD_EXPRESSION  + "ru.axetta.ecafe.processor.core.RuleProcessor.inputValueMethodCalling"),
            new ParamHint("methodValues", "Какие-то значения из метода").setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION + RuleProcessor.METHOD_EXPRESSION + "ru.axetta.ecafe.processor.core.RuleProcessor.testMethodCalling"),
            new ParamHint("radioValues", "Какие-то значения из радио").setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{100}сто,{200}двести,{300}триста"),
            new ParamHint("input", "Какие-то произольное значение").setDefaultRule("= " + RuleProcessor.INPUT_EXPRESSION + RuleProcessor.METHOD_EXPRESSION + "ru.axetta.ecafe.processor.core.RuleProcessor.inputValueMethodCalling"),*/

    public static final ReportHint[] REPORT_HINTS = {
            new ReportHint(ReportOnNutritionByWeekReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(OrgBalanceJasperReport.class.getCanonicalName(), new int[]{3, 4, 5, 22, 23}),
            new ReportHint(ClientGroupBalanceReport.class.getCanonicalName(), new int[]{3, 4, 5, 6, 22, 23}),
            new ReportHint(OrgBalanceReport.class.getCanonicalName(), new int[]{3, 4, 5, 22, 23}),
            new ReportHint(ContragentOrderReport.class.getCanonicalName(), new int[]{3, 22, 23}),
            new ReportHint(ContragentOrderCategoryReport.class.getCanonicalName(), new int[]{3, 22, 23}),
            new ReportHint(OrgOrderCategoryReport.class.getCanonicalName(), new int[]{3, 22, 23}),
            new ReportHint(SalesReport.class.getCanonicalName(), new int[]{3, 22, 23}),
            new ReportHint(MscSalesReport.class.getCanonicalName(), new int[]{3, 22, 23}),
            new ReportHint(RegisterReport.class.getCanonicalName(), new int[]{3, 22, 23}),
            new ReportHint(ClientsReport.class.getCanonicalName(), new int[]{3, 22, 23}),
            new ReportHint(OrgOrderByDaysReport.class.getCanonicalName(), new int[]{3, 22, 23}),
            new ReportHint(AutoEnterEventReport.class.getCanonicalName(), new int[]{3, 22, 23, 24, 26}),
            new ReportHint(AutoEnterEventByDaysReport.class.getCanonicalName(), new int[]{3, 22, 23, 24}),
            new ReportHint(DailySalesByGroupsReport.class.getCanonicalName(), new int[]{3, 22, 23, 25, 26}),
            new ReportHint(SubscriptionFeedingJasperReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ClientOrderDetailsByOneOrgReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(RegisterStampReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ComplaintCountByGoodReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ComplaintCausesReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ComplaintIterationsReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ProductPopularityReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(QuestionaryResultByOrgReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(ClientMigrationHistoryReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(ClientSelectedAnswerResultByOrgReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(MenuDetailsGroupByMenuOriginReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(ClientOrderDetailsByAllOrgReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(OrderDetailsGroupByMenuOriginReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(ContragentPaymentReport.class.getCanonicalName(), new int[]{26}),
            new ReportHint(ContragentCompletionReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(HalfYearSummaryReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(BeneficiarySummaryReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(DeliveredServicesReport.class.getCanonicalName(), new int[]{20/*, 26, 27, 28, 29, 30, 31, 32*/}),
            new ReportHint(DashboardByAllOrgReport.class.getCanonicalName(), new int[]{31}),
            new ReportHint(DetailedEnterEventReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(FeedingAndVisitReport.class.getCanonicalName(), new int[]{3})
    };


    private ReportJobConstants() {

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

}