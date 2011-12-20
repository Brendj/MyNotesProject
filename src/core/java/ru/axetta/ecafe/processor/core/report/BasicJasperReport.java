/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.*;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 15.12.2009
 * Time: 12:10:58
 * To change this template use File | Settings | File Templates.
 */
public class BasicJasperReport extends BasicReport {

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 14.01.2010
     * Time: 13:56:17
     * To change this template use File | Settings | File Templates.
     */
    public static class PdfBuilder extends BasicDocumentBuilder {

        public PdfBuilder(String basePath, String baseFileName,
                BasicReport.DocumentBuilderCallback documentBuilderCallback, DateFormat dateFormat,
                DateFormat timeFormat) {
            super(basePath, baseFileName, "pdf", documentBuilderCallback, dateFormat, timeFormat);
        }

        protected void writeReportDocumentTo(BasicReport report, File file) throws Exception {
            BasicJasperReport jasperReport = (BasicJasperReport) report;
            JasperExportManager.exportReportToPdfFile(jasperReport.getPrint(), file.getAbsolutePath());
        }

    }

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 14.01.2010
     * Time: 13:56:17
     * To change this template use File | Settings | File Templates.
     */
    public static class XlsBuilder extends BasicDocumentBuilder {

        public XlsBuilder(String basePath, String baseFileName,
                BasicReport.DocumentBuilderCallback documentBuilderCallback, DateFormat dateFormat,
                DateFormat timeFormat) {
            super(basePath, baseFileName, "xls", documentBuilderCallback, dateFormat, timeFormat);
        }

        protected void writeReportDocumentTo(BasicReport report, File file) throws Exception {
            BasicJasperReport jasperReport = (BasicJasperReport) report;
            JRXlsExporter xlsExporter = new JRXlsExporter();
            xlsExporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperReport.getPrint());
            xlsExporter.setParameter(JRXlsExporterParameter.OUTPUT_FILE, file);
            //xlsExporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            xlsExporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsExporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
            xlsExporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            xlsExporter.exportReport();
        }

    }

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 14.01.2010
     * Time: 13:56:17
     * To change this template use File | Settings | File Templates.
     */
    public static class HtmlBuilder extends BasicDocumentBuilder {

        public HtmlBuilder(String basePath, String baseFileName,
                BasicReport.DocumentBuilderCallback documentBuilderCallback, DateFormat dateFormat,
                DateFormat timeFormat) {
            super(basePath, baseFileName, "html", documentBuilderCallback, dateFormat, timeFormat);
        }

        protected void writeReportDocumentTo(BasicReport report, File file) throws Exception {
            BasicJasperReport jasperReport = (BasicJasperReport) report;
            JRHtmlExporter htmlExporter = new JRHtmlExporter();
            htmlExporter.setParameter(JRHtmlExporterParameter.JASPER_PRINT, jasperReport.getPrint());
            htmlExporter.setParameter(JRHtmlExporterParameter.OUTPUT_FILE, file);
            htmlExporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
            htmlExporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
            htmlExporter.setParameter(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, null);
            htmlExporter.exportReport();
        }

    }

    /**
     * Created by IntelliJ IDEA.
     * User: Developer
     * Date: 14.01.2010
     * Time: 13:56:17
     * To change this template use File | Settings | File Templates.
     */
    public static class CsvBuilder extends BasicDocumentBuilder {

        public CsvBuilder(String basePath, String baseFileName,
                BasicReport.DocumentBuilderCallback documentBuilderCallback, DateFormat dateFormat,
                DateFormat timeFormat) {
            super(basePath, baseFileName, "csv", documentBuilderCallback, dateFormat, timeFormat);
        }

        protected void writeReportDocumentTo(BasicReport report, File file) throws Exception {
            BasicJasperReport jasperReport = (BasicJasperReport) report;
            JRCsvExporter csvExporter = new JRCsvExporter();
            csvExporter.setParameter(JRCsvExporterParameter.JASPER_PRINT, jasperReport.getPrint());
            csvExporter.setParameter(JRCsvExporterParameter.OUTPUT_FILE, file);
            csvExporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, ";");
            csvExporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "windows-1251");
            csvExporter.exportReport();
        }

    }

    private JasperPrint print;

    public BasicJasperReport(Date generateTime, long generateDuration, JasperPrint print) {
        super(generateTime, generateDuration);
        this.print = print;
    }

    public BasicJasperReport() {
        this.print = null;
    }

    public JasperPrint getPrint() {
        if (print==null) prepare();
        return print;
    }

    protected boolean hasPrint() {
        return print != null;
    }

    protected void setPrint(JasperPrint print) {
        this.print = print;
    }

}