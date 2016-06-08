/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.telephone.number;

import ru.axetta.ecafe.processor.core.report.BasicReport;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.01.14
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class TelephoneNumberCountReport extends BasicReport {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по телефонным номерам";
    public static final String[] TEMPLATE_FILE_NAMES = {"TelephoneNumberCountJasperReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final List<TelephoneNumberCountItem> telephoneNumberCountItems;

    public TelephoneNumberCountReport() {
        super();
        this.telephoneNumberCountItems = Collections.emptyList();
    }

    public TelephoneNumberCountReport(Date generateTime, long generateDuration,
            List<TelephoneNumberCountItem> telephoneNumberCountItems) {
        super(generateTime, generateDuration);
        this.telephoneNumberCountItems = telephoneNumberCountItems;
    }

    public List<TelephoneNumberCountItem> getTelephoneNumberCountItems() {
        return telephoneNumberCountItems;
    }
}
