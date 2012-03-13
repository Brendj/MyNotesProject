/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;
import ru.axetta.ecafe.processor.core.report.*;
import ru.axetta.ecafe.processor.core.report.kzn.SalesReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderCategoryReport;
import ru.axetta.ecafe.processor.core.report.maussp.ContragentOrderReport;
import ru.axetta.ecafe.processor.core.report.msc.MscSalesReport;

import org.apache.commons.lang.StringUtils;

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
            new ParamHint("groupName", "Название класса"), new ParamHint("idOfClient", "Идентификатор клиента"),
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
            new ParamHint("idOfContragent", "Идентификатор контрагента"),
            new ParamHint("contragentName", "Название контрагента")};

    public static final ReportHint[] REPORT_HINTS = {
            new ReportHint(OrgBalanceReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ClientGroupBalanceReport.class.getCanonicalName(), new int[]{3, 4, 5, 6}),
            new ReportHint(OrgBalanceJasperReport.class.getCanonicalName(), new int[]{3, 4, 5}),
            new ReportHint(ContragentOrderReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(ContragentOrderCategoryReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(OrgOrderCategoryReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(SalesReport.class.getCanonicalName(), new int[]{3}),
            new ReportHint(MscSalesReport.class.getCanonicalName(), new int[]{3})};

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

}