/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.LatePaymentByOneDayCountType;
import ru.axetta.ecafe.processor.core.persistence.LatePaymentDaysCountType;
import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.complianceWithOrderAndConsumption.CWOACReport;
import ru.axetta.ecafe.processor.core.report.feeding.SubscriptionFeedingJasperReport;
import ru.axetta.ecafe.processor.core.report.feeding.SubscriptionFeedingReport;
import ru.axetta.ecafe.processor.core.report.financialControlReports.AdjustmentPaymentReport;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentDetailedReport;
import ru.axetta.ecafe.processor.core.report.financialControlReports.LatePaymentReport;
import ru.axetta.ecafe.processor.core.report.kzn.BeneficiaryByAllOrgReport;
import ru.axetta.ecafe.processor.core.report.kzn.SalesReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderCategoryReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderReport;
import ru.axetta.ecafe.processor.core.report.msc.*;
import ru.axetta.ecafe.processor.core.report.security.JournalAuthenticationReport;
import ru.axetta.ecafe.processor.core.report.security.JournalBalancesReport;
import ru.axetta.ecafe.processor.core.report.security.JournalProcessesReport;
import ru.axetta.ecafe.processor.core.report.security.JournalReportsReport;
import ru.axetta.ecafe.processor.core.report.statistics.discounts.OrgDiscountsReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.payment.DetailedDeviationsPaymentOrReducedPriceMealsJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps.DetailedDeviationsWithoutCorpsIntervalJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps.DetailedDeviationsWithoutCorpsJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps.DetailedDeviationsWithoutCorpsNewIntervalJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.discrepancies.deviations.without.corps.DetailedDeviationsWithoutCorpsNewJasperReport;
import ru.axetta.ecafe.processor.core.report.statistics.payment.preferential.supply.StatisticsPaymentPreferentialSupplyReport;
import ru.axetta.ecafe.processor.core.report.statistics.telephone.number.TelephoneNumberCountReport;
import ru.axetta.ecafe.processor.core.report.summarySalesToSchools.SSTSReport;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;

import java.util.*;

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

    /*
    * ВСЕ классы отчетов должны добавляться сюда, чтобы их параметры проинициализировались
    */
    public static final Class[] ALL_REPORT_CLASSES = {
            ActiveClientsReport.class,
            ActiveDiscountClientsReport.class,
            AdjustmentPaymentReport.class,
            AutoEnterEventByDaysReport.class,
            AutoEnterEventReport.class,
            DetailedEnterEventReport.class,
            BalanceLeavingReport.class,
            BeneficiaryByAllOrgReport.class,
            BeneficiarySummaryReport.class,
            BudgetMealsShippingReport.class,
            ClientBalanceByDayReport.class,
            ClientBalanceByOrgReport.class,
            ClientMigrationHistoryReport.class,
            ClientOrderDetailsByAllOrgReport.class,
            ClientOrderDetailsByOneOrgReport.class,
            ClientPaymentsReport.class,
            ClientSelectedAnswerResultByOrgReport.class,
            ClientsReport.class,
            ComplaintCausesReport.class,
            ComplaintCountByGoodReport.class,
            ComplaintIterationsReport.class,
            ContragentCompletionReport.class,
            ContragentOrderCategoryReport.class,
            ContragentOrderReport.class,
            ContragentPaymentReport.class,
            CWOACReport.class,
            DailyReferReport.class,
            DailySalesByGroupsReport.class,
            DashboardByAllOrgReport.class,
            DeliveredServicesElectronicCollationReport.class,
            DeliveredServicesReport.class,
            DetailedDeviationsPaymentOrReducedPriceMealsIntervalJasperReport.class,
            DetailedDeviationsPaymentOrReducedPriceMealsJasperReport.class,
            DetailedDeviationsWithoutCorpsIntervalJasperReport.class,
            DetailedDeviationsWithoutCorpsJasperReport.class,
            DetailedDeviationsWithoutCorpsNewIntervalJasperReport.class,
            DetailedDeviationsWithoutCorpsNewJasperReport.class,
            DiscrepanciesDataOnOrdersAndPaymentJasperReport.class,
            DiscrepanciesOnOrdersAndAttendanceJasperReport.class,
            FeedingAndVisitReport.class,
            FeedingAndVisitSReport.class,
            GoodRequestsNewReport.class,
            HalfYearSummaryReport.class,
            InteractiveCardDataReport.class,
            JournalAuthenticationReport.class,
            JournalBalancesReport.class,
            JournalProcessesReport.class,
            JournalReportsReport.class,
            LatePaymentDetailedReport.class,
            LatePaymentReport.class,
            MenuDetailsGroupByMenuOriginReport.class,
            OrderDetailsGroupByMenuOriginReport.class,
            OrdersByManufacturerReport.class,
            OrgBalanceReport.class,
            OrgBalanceJasperReport.class,
            OrgDiscountsReport.class,
            OrgOrderByDaysReport.class,
            OrgOrderCategoryReport.class,
            OrgSmsStatsReport.class,
            OutOfSynchronizationReport.class,
            PaymentTotalsReport.class,
            ProductPopularityReport.class,
            QuestionaryResultByOrgReport.class,
            ReferReport.class,
            RegisterReport.class,
            RegisterStampPaidReport.class,
            RegisterStampReport.class,
            RegisterStampSubscriptionFeedingReport.class,
            RegularPaymentsReport.class,
            ReportOnNutritionByWeekReport.class,
            RequestsAndOrdersReport.class,
            SalesReport.class,
            ru.axetta.ecafe.processor.core.report.SalesReport.class,
            MscSalesReport.class,
            SentSmsReport.class,
            SmsAddressesReport.class,
            SMSDeliveryReport.class,
            SpecialDatesReport.class,
            SSTSReport.class,
            StatisticsPaymentPreferentialSupplyReport.class,
            StatisticsPaymentPreferentialSupplyJasperReport.class,
            SubscriptionFeedingReport.class,
            SubscriptionFeedingJasperReport.class,
            TelephoneNumberCountReport.class,
            TelephoneNumberCountJasperReport.class,
            TotalBenefFeedReport.class,
            TotalSalesReport.class,
            TransactionsReport.class,
            TypesOfCardReport.class,
            ZeroTransactionsReport.class,
            MonitoringOfReport.class,
    };

    public static final ReportHint[] REPORT_HINTS = initReportHitns();
    public static final Map<String, String> REPORT_NAME_MAP = initReportNameMap();
    public static final Map<String, String> REPORT_NAME_CLASS_MAP = initReportNameClassMap();

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
        private String value;
        private String defaultRule;
        private boolean hideOnSetup;

        public ParamHint(String name, String description) {
            this.name = name;
            this.description = description;
            hideOnSetup = false;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public ParamHint setDefaultRule(String defaultRule) {
            this.defaultRule = defaultRule;
            return this;
        }

        public String getDefaultRule() {
            return defaultRule;
        }

        public boolean isHideOnSetup() {
            return hideOnSetup;
        }

        public ParamHint setHideOnSetup(boolean hideOnSetup) {
            this.hideOnSetup = hideOnSetup;
            return this;
        }

    }

    public static class ReportHint {

        private final String typeName;
        private final String reportName;
        private final int[] paramHints;

        public ReportHint(String typeName, String reportName, int[] paramHints) {
            this.typeName = typeName;
            this.reportName = reportName;
            this.paramHints = paramHints;
        }

        public String getTypeName() {
            return typeName;
        }

        public String getReportName() {
            return reportName;
        }

        public int[] getParamHints() {
            return paramHints;
        }
    }

    public static final ParamHint[] PARAM_HINTS = {
            new ParamHint("generateDate", "Дата генерации отчета"), //0
            new ParamHint("generateTime", "Дата и время генерации отчета"), //1
            new ParamHint("generateDurationMillis", "Продолжительность генерации отчета в миллисекундах"), //2
            new ParamHint("idOfOrg", "Организации").setDefaultRule("= org: "), //3
            new ParamHint("shortName", "Краткое название организации"), //4
            new ParamHint("officialName", "Официальное название организации"),   //5
            new ParamHint("groupName", "Название класса"), //6
            new ParamHint("idOfClient", "Идентификатор клиента"), //7
            new ParamHint("email", "Адрес электронной почты клиента"), //8
            new ParamHint("contractPerson.surname", "Фамилия физического лица, заключившего контракт"), //9
            new ParamHint("contractPerson.firstName", "Имя физического лица, заключившего контракт"),   //10
            new ParamHint("contractPerson.secondName", "Отчество физического лица, заключившего контракт"), //11
            new ParamHint("contractPerson.abbreviation", "Фамилия И.О. физического лица, заключившего контракт"), //12
            new ParamHint("person.surname", "Фамилия обслуживаемого физического лица"), //13
            new ParamHint("person.firstName", "Имя обслуживаемого физического лица"), //14
            new ParamHint("person.secondName", "Отчество обслуживаемого физического лица"), //15
            new ParamHint("person.abbreviation", "Фамилия И.О. обслуживаемого физического лица"), //16
            new ParamHint("phone", "Телефонный номер клиента"), //17
            new ParamHint("mobile", "Номер мобильного телефона клиента"), //18
            new ParamHint("address", "Адрес клиента"), //19
            new ParamHint(BasicReportForContragentJob.PARAM_CONTRAGENT_ID, "Идентификатор контрагента")
                    .setDefaultRule("= contragent: "),    //20
            new ParamHint("contragentName", "Наименование контрагента"),  //21
            new ParamHint("category", "Категория организации"), //22
            new ParamHint("idOfMenuSourceOrg", "Организация - источник меню"), //23
            new ParamHint("enterEventType", "Тип отчета по посещаемости: ").setDefaultRule(
                    "= " + RuleProcessor.RADIO_EXPRESSION
                            + "{все}Все,{учащиеся}Учащиеся,{все_без_учащихся}Все без учащихся"), //24
            new ParamHint("groupByMenuGroup", "Группировка отчета").setDefaultRule(
                    "= " + RuleProcessor.COMBOBOX_EXPRESSION
                            + "{false}По типам производства,{true}По товарным группам"), //25
            new ParamHint(DailySalesByGroupsReport.PARAM_MENU_GROUPS, "Группы меню"), //26
            new ParamHint(DailySalesByGroupsReport.PARAM_INCLUDE_COMPLEX, "Включать комплексы"), //27
            new ParamHint(ReportPropertiesUtils.P_REPORT_PERIOD, "Количество дней в выборке").setHideOnSetup(true), //28
            //  Период отображать не надо, он устанавливается автоматически
            new ParamHint(ReportPropertiesUtils.P_JOB_NAME, "Название задачи"), //29
            new ParamHint(BasicReportForContragentJob.PARAM_CONTRAGENT_RECEIVER_ID, "Контрагент-получатель")
                    .setDefaultRule("= contragent-receiver:"), //30,
            new ParamHint(BasicReportForContragentJob.PARAM_CONTRAGENT_PAYER_ID, "Агент по приему платежей")
                    .setDefaultRule("= contragent-payagent:"), //31
            new ParamHint(ReportPropertiesUtils.P_REPORT_PERIOD_TYPE, "Период").setHideOnSetup(true)
                    .setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION +
                            "{" + BasicReportJob.REPORT_PERIOD_PREV_MONTH + "}прошлый месяц," +
                            "{" + BasicReportJob.REPORT_PERIOD_PREV_DAY + "}-1 день," +
                            "{" + BasicReportJob.REPORT_PERIOD_TODAY+ "}сегодня," +
                            "{" + BasicReportJob.REPORT_PERIOD_PREV_PREV_DAY + "}-2 дня," +
                            "{" + BasicReportJob.REPORT_PERIOD_PREV_PREV_PREV_DAY + "}-3 дня," +
                            "{" + BasicReportJob.REPORT_PERIOD_PREV_WEEK + "}прошлая неделя," +
                            "{" + BasicReportJob.REPORT_PERIOD_CURRENT_MONTH + "}текущий месяц," +
                            "{" + BasicReportJob.REPORT_PERIOD_LAST_WEEK + "}текущая неделя"), //32
            new ParamHint("referCategory", "Категория").setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION +
                    "{Шк Здоровья 1-4 кл.(завтрак+обед)}Шк Здоровья 1-4 кл.(завтрак+обед),"
                    + "{Шк Здоровья 5-11 кл.(завтрак+обед)}Шк Здоровья 5-11 кл.(завтрак+обед),"
                    + "{Многодетные 1-4 кл.(завтрак+обед)}Многодетные 1-4 кл.(завтрак+обед),"
                    + "{Многодетные 5-11 кл.(завтрак+обед)}Многодетные 5-11 кл.(завтрак+обед),"
                    + "{Соц./незащищ. 5-11 кл.(завтрак+обед)}Соц./незащищ. 5-11 кл.(завтрак+обед),"
                    + "{Соц./незащищ. 1-4 кл.(завтрак+обед)}Соц./незащищ. 1-4 кл.(завтрак+обед)"), //33
            new ParamHint("goodName", "Наименование товара").setDefaultRule("= " + RuleProcessor.INPUT_EXPRESSION), //34
            new ParamHint("hideMissedColumns", "Скрывать даты с пустыми значениями")
                    .setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{false}Не скрывать,{true}Скрывать"), //35
            new ParamHint("goodsFilter", "Фильтры по заявкам").setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION
                    + "{3}Отображать организации с отсутствием заявок за последние 2 дня,{2}Только пустые,{0}Все,{1}Только с данными"), //36
            new ParamHint("dailySample", "Суточная проба")
                    .setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{false}Не выводить,{true}Выводить"), //37
            new ParamHint(RegisterStampReport.PARAM_WITH_OUT_ACT_DISCREPANCIES, "Показывать без расхождений")
                    .setDefaultRule("= " + RuleProcessor.CHECKBOX_EXPRESSION + " {true}"), //38
            new ParamHint(DashboardByAllOrgReport.P_ORG_STATE, "Статус").setDefaultRule(
                    String.format("= %s{0}Не обслуживается,{1}Обслуживается,{2}Все,",
                            RuleProcessor.COMBOBOX_EXPRESSION)),//39
            new ParamHint(RequestsAndOrdersReport.P_HIDE_MISSED_COLUMNS, "Скрывать даты с пустыми значениями")
                    .setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{false}Не скрывать,{true}Скрывать"),//40
            new ParamHint(RequestsAndOrdersReport.P_USE_COLOR_ACCENT, "Включить цветовую индикацию")
                    .setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{false}Не использовать,{true}Включить"),//41
            new ParamHint(RequestsAndOrdersReport.P_SHOW_ONLY_DIVERGENCE, "Режим вывода данных")
                    .setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{false}Все данные,{true}Только расхождения"),//42
            new ParamHint(RequestsAndOrdersReport.P_FEEDING_PLAN_TYPE, "Тип питания")
                    .setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION
                            + "{Все}Все,"
                            + "{Платное питание}Платное питание,"
                            + "{Льготное питание}Льготное питание,"
                            + "{Абонементное питание}Абонементное питание"), //43
            new ParamHint(RequestsAndOrdersReport.P_NO_NULL_REPORT, "При отстуствии данных для отчета:")
                    .setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{false}ничего не создавать,{true}создать отчет с пустой строкой"), //44
            new ParamHint(ClientBalanceByDayReport.P_CLIENT_BALANCE_CONDITION_TYPE, "Текущий баланс")
                    .setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION
                            +"{Не задано}Не задано,"
                            +"{Меньше 0}Меньше 0,"
                            +"{Равен 0}Равен 0,"
                            +"{Больше}Больше 0,"), //45
            new ParamHint(TypesOfCardReport.PARAM_WITH_OUT_SUMMARY_BY_DISTRICTS, "Итоговые данные по округу")
                    .setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{false}Нет,{true}Да"), //46
            new ParamHint(TypesOfCardReport.PARAM_CLIENT_GROUP, "ИД группы"), //47
            new ParamHint(TypesOfCardReport.PARAM_GROUP_NAME, "Название группы"), //48
            new ParamHint("showReserve", "Показывать резервников")
                    .setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{false}Нет,{true}Да"), //49
            new ParamHint(LatePaymentReport.LATE_PAYMENT_DAYS_COUNT_TYPE, "Количество дней несвоевременной оплаты")
                    .setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION
                    + "{" + LatePaymentDaysCountType.EMPTY.toString() +"}" + LatePaymentDaysCountType.EMPTY.toString() + ","
                    + "{" + LatePaymentDaysCountType.MORE_TEN.toString() +"}" + LatePaymentDaysCountType.MORE_TEN.toString()), //50
            new ParamHint(LatePaymentReport.LATE_PAYMENT_DAYS_COUNT_TYPE, "Количество дней несвоевременной оплаты")
                    .setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION
                    + "{" + LatePaymentByOneDayCountType.EMPTY.toString() +"}" + LatePaymentByOneDayCountType.EMPTY.toString() + ","
                    + "{" + LatePaymentByOneDayCountType.MORE_FIVE.toString() +"}" + LatePaymentByOneDayCountType.MORE_FIVE.toString()), //51

    };
    // !!!!!!!! ДЛЯ ТЕСТА !!!!!!!!!!
            /*new ParamHint("idOfContract", "Контракт"),
            new ParamHint("listValue", "Какое-то значение из списка").setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION + "{111}один,{222}два,{333}три"),
            new ParamHint("checkValue", "Какое-то значение по чекбоксу").setDefaultRule("= " + RuleProcessor.CHECKBOX_EXPRESSION + "{555}пять,{666}шесть,{777}семь"),
            new ParamHint("methodValue", "Какое-то значение из метода").setDefaultRule("= " + RuleProcessor.METHOD_EXPRESSION  + "ru.axetta.ecafe.processor.core.RuleProcessor.inputValueMethodCalling"),
            new ParamHint("methodValues", "Какие-то значения из метода").setDefaultRule("= " + RuleProcessor.COMBOBOX_EXPRESSION + RuleProcessor.METHOD_EXPRESSION + "ru.axetta.ecafe.processor.core.RuleProcessor.testMethodCalling"),
            new ParamHint("radioValues", "Какие-то значения из радио").setDefaultRule("= " + RuleProcessor.RADIO_EXPRESSION + "{100}сто,{200}двести,{300}триста"),
            new ParamHint("input", "Какие-то произольное значение").setDefaultRule("= " + RuleProcessor.INPUT_EXPRESSION + RuleProcessor.METHOD_EXPRESSION + "ru.axetta.ecafe.processor.core.RuleProcessor.inputValueMethodCalling"),*/


            /*
            new ReportHint(ReportOnNutritionByWeekReport.class.getCanonicalName(), new int[]{3, 4, 5}),
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
            new ReportHint(AutoEnterEventReport.class.getCanonicalName(), new int[]{28, 29, -3, 22, 23, 24, 32}),
            new ReportHint(AutoEnterEventByDaysReport.class.getCanonicalName(), new int[]{28, 29, -3, 22, 23, 24}),
            new ReportHint(DailySalesByGroupsReport.class.getCanonicalName(), new int[]{3, 23, 29, 25, 26, 27, 32}),
            new ReportHint(SubscriptionFeedingJasperReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ClientOrderDetailsByOneOrgReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(RegisterStampReport.class.getCanonicalName(), new int[]{3, 4, 5, 38}),
            new ReportHint(ComplaintCountByGoodReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ComplaintCausesReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ComplaintIterationsReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ProductPopularityReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(QuestionaryResultByOrgReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(ClientSelectedAnswerResultByOrgReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(ClientMigrationHistoryReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(MenuDetailsGroupByMenuOriginReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(ClientOrderDetailsByAllOrgReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(DashboardByAllOrgReport.class.getCanonicalName(), new int[]{39}),
            new ReportHint(OrderDetailsGroupByMenuOriginReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(ContragentPaymentReport.class.getCanonicalName(), new int[]{31, 21, 30, 32}),
            new ReportHint(ContragentCompletionReport.class.getCanonicalName(), new int[]{20, 21}),
            new ReportHint(HalfYearSummaryReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(BeneficiarySummaryReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(DeliveredServicesReport.class.getCanonicalName(),  new int[]{3, 32}),
            new ReportHint(CWOACReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(SSTSReport.class.getCanonicalName(), new int[]{30}),
            new ReportHint(ClientBalanceByDayReport.class.getCanonicalName(), new int[]{20, 3, 45}),
            new ReportHint(BudgetMealsShippingReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(ActiveClientsReport.class.getCanonicalName(), new int[]{32}),
            new ReportHint(StatisticsPaymentPreferentialSupplyJasperReport.class.getCanonicalName(), new int[]{3, -20}),
            new ReportHint(TelephoneNumberCountJasperReport.class.getCanonicalName(), new int[]{3, -20}),
            new ReportHint(ActiveDiscountClientsReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(DiscrepanciesDataOnOrdersAndPaymentJasperReport.class.getCanonicalName(), new int[]{3, -23}),
            new ReportHint(TransactionsReport.class.getCanonicalName(), new int[]{}),
            //new ReportHint(ReferReport.class.getCanonicalName(), new int[]{-3}),
            //new ReportHint(DailyReferReport.class.getCanonicalName(), new int[]{-3, -33})
            // отрицательное значение - обязательное
            new ReportHint(AutoEnterEventV2Report.class.getCanonicalName(), new int[]{3}),
            new ReportHint(TotalSalesReport.class.getCanonicalName(), new int[]{20}),
            new ReportHint(FeedingAndVisitReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(FeedingAndVisitSReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(SMSDeliveryReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(RequestsAndOrdersReport.class.getCanonicalName(), new int[]{3, 32, 40, 41, 42, 43, 44}),
            new ReportHint(PaymentTotalsReport.class.getCanonicalName(), new int[]{20, 3, 35}),
            new ReportHint(BeneficiaryByAllOrgReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(DetailedDeviationsWithoutCorpsNewJasperReport.class.getCanonicalName(), new int[]{0,3}),
            new ReportHint(BalanceLeavingReport.class.getCanonicalName(), new int[]{}),
            new ReportHint(OutOfSynchronizationReport.class.getCanonicalName(), new int[]{-3})
    };
    */

    private static ReportHint[] initReportHitns(){
        List<ReportHint> list = new ArrayList<ReportHint>();
        for(Class c : ALL_REPORT_CLASSES){
            try {
                String reportName = (String) c.getField("REPORT_NAME").get(null);
                int[] paramHints = (int[]) c.getField("PARAM_HINTS").get(null);
                Boolean isTemplateReport = (Boolean) c.getField("IS_TEMPLATE_REPORT").get(null);
                if(isTemplateReport) {
                    list.add(new ReportHint(c.getCanonicalName(), reportName, paramHints));
                }
            } catch (Exception ignore) {}
        }
        ReportHint[] result = new ReportHint[list.size()];
        result = list.toArray(result);
        return result;
    }

    private static Map<String, String> initReportNameMap(){
        Map<String, String>  map = new HashMap<String, String>();
        for(Class c : ALL_REPORT_CLASSES){
            try {
                String reportName = (String) c.getField("REPORT_NAME").get(null);
                String[] templateFilenames = (String[]) c.getField("TEMPLATE_FILE_NAMES").get(null);
                for(String template : templateFilenames){
                    map.put(template, reportName);
                }
            } catch (Exception ignore) {}
        }
        return map;
    }

    private static Map<String, String> initReportNameClassMap(){
        Map<String, String>  map = new HashMap<String, String>();
        for(Class c : ALL_REPORT_CLASSES){
            try {
                String reportName = (String) c.getField("REPORT_NAME").get(null);
                map.put(reportName, c.getCanonicalName());
            } catch (Exception ignore) {}
        }
        return map;
    }

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

    public static class ParamHintWrapper {

        private ParamHint hint;
        private boolean required;

        public ParamHintWrapper(ParamHint hint) {
            this.hint = hint;
        }

        public ParamHint getParamHint() {
            return hint;
        }

        public boolean isRequired() {
            return required;
        }

        public ParamHintWrapper setRequired(boolean required) {
            this.required = required;
            return this;
        }
    }

    public static class ReportParamHint {

        private final String typeName;
        private final List<ParamHintWrapper> paramHints;

        public ReportParamHint(ReportRuleConstants.ReportHint reportHint) {
            this.typeName = reportHint.getTypeName();
            this.paramHints = new LinkedList<ParamHintWrapper>();
            for (int i : reportHint.getParamHints()) {
                //  Изменяем i, делаем его положительным всегда, но если изначально было отрицательным, то указываем, что
                //  поле является обязательным
                ParamHintWrapper newParam = new ParamHintWrapper(ReportRuleConstants.PARAM_HINTS[Math.abs(i)]);
                this.paramHints.add(newParam.setRequired(i < 0));
            }
        }

        public String getTypeName() {
            return typeName;
        }

        public List<ParamHintWrapper> getParamHints() {
            return paramHints;
        }
    }

    public static List<ParamHintWrapper> getParamHintsForReportType(String reportType) {
        ReportHint hint = findReportHint(reportType);
        if (hint == null) {
            return Collections.emptyList();
        } else {
            return new ReportParamHint(hint).getParamHints();
        }
    }
}