/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import ru.axetta.ecafe.processor.core.report.BasicReportForContragentJob;
import ru.axetta.ecafe.processor.core.report.ContragentPaymentReport;
import ru.axetta.ecafe.processor.core.report.DailySalesByGroupsReport;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 22.04.12
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class RuleExpressionUtil {
    public static final String ARGUMENT_NAMES[] = {
        "generateDate",
        "generateTime",
        "generateDurationMillis",
        "idOfOrg",
        "shortName",
        "officialName",
        "groupName",
        "idOfClient",
        "email",
        "contractPerson.surname",
        "contractPerson.firstName",
        "contractPerson.secondName",
        "contractPerson.abbreviation",
        "person.surname",
        "person.firstName",
        "person.secondName",
        "person.abbreviation",
        "phone",
        "mobile",
        BasicReportForContragentJob.PARAM_CONTRAGENT_ID, //"idOfContragent",
        "contragentName",
        "category",
        "idOfMenuSourceOrg",
        "orgTag",
        null, // далее идут параметры для передачи значений
        "enterEventType", // используется в AutoEnterEventByDaysReport и AutoEnterEventReport
        DailySalesByGroupsReport.PARAM_GROUP_BY_MENU_GROUP,
        DailySalesByGroupsReport.PARAM_INCLUDE_COMPLEX,
        DailySalesByGroupsReport.PARAM_MENU_GROUPS,
        ReportPropertiesUtils.P_REPORT_PERIOD,
        ReportPropertiesUtils.P_REPORT_PERIOD_TYPE,
        BasicReportForContragentJob.PARAM_CONTRAGENT_RECEIVER_ID,
        BasicReportForContragentJob.PARAM_CONTRAGENT_PAYER_ID
    };

    public static boolean isPostArgument(String argName) {
        boolean isPostArg=false;
        for (int i = 0; i < ARGUMENT_NAMES.length; i++) {
            if (ARGUMENT_NAMES[i]==null) { isPostArg=true; continue; }
            if (ARGUMENT_NAMES[i].equals(argName)) {
                return isPostArg;
            }
        }
        return false;
    }
}
