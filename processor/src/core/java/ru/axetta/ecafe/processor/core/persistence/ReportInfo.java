/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashMap;

public class ReportInfo {

    private Long idOfReportInfo;
    private String ruleName;
    private Integer documentFormat;
    private String reportName;
    private Date createdDate;
    private Long generationTime;
    private Date startDate;
    private Date endDate;
    private String reportFile;
    private String orgNum;
    private Long idOfOrg;
    private String tag;
    private Long idOfContragentReceiver;
    private String contragentReceiver;
    private Long idOfContragentPayer;
    private String contragentPayer;
    private Integer createState;
    private String errorString;

    //статусы записей в репозитории отчетов
    public static final Integer QUARTZ_JOB_TRIGGERED = 100; // запущена org.quartz.Job (записывается до генерации отчета)
    public static final Integer REPORT_GENERATED = 200; // отчет успешно сгенерирован (записывается после генерации отчета)
    public static final Integer MANUAL_REPORT_GENERATION = 300; // отчет сгенерирован вручную (вне org.quartz.Job)
    public static final Integer ERROR_DURING_REPORT_GENERATION = 400; //ошибка при генерации отчета
    public static final Integer ERROR_DURING_MAILING = 500; //ошибка при отправке отчета по почте
    public static final Integer MAIL_SENT = 600; //почтовая рассылка прошла успешно
    public static final Integer UNDEFINED = 0;

    public static final HashMap<Integer, String> REPORT_INFO_STATUS = new HashMap<Integer, String>();
    static {
        REPORT_INFO_STATUS.put(QUARTZ_JOB_TRIGGERED, "Задача стартовала");
        REPORT_INFO_STATUS.put(REPORT_GENERATED, "Отчет сгенерирован");
        REPORT_INFO_STATUS.put(MANUAL_REPORT_GENERATION, "Ручной запуск");
        REPORT_INFO_STATUS.put(ERROR_DURING_REPORT_GENERATION, "Ошибка генерации отчета");
        REPORT_INFO_STATUS.put(ERROR_DURING_MAILING, "Ошибка отправки почты");
        REPORT_INFO_STATUS.put(MAIL_SENT, "Почта отправлена");
        REPORT_INFO_STATUS.put(UNDEFINED, "Статус не определен");
    }

    public static String getStatusDescription(Integer statusCode) {
        String description = REPORT_INFO_STATUS.get(statusCode);
        return StringUtils.trimToEmpty(description);
    }

    protected ReportInfo() {

    }

    public ReportInfo(String ruleName, Integer documentFormat, String reportName, Date createdDate, Long generationTime,
            Date startDate, Date endDate, String reportFile, String orgNum, Long idOfOrg, String tag, Long idOfContragentReceiver,
            String contragentReceiver, Long idOfContragentPayer, String contragentPayer, Integer createState) {
        this.ruleName = ruleName;
        this.documentFormat = documentFormat;
        this.reportName = reportName;
        this.createdDate = createdDate;
        this.generationTime = generationTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportFile = reportFile;
        this.orgNum = orgNum;
        if (this.orgNum!=null && orgNum.length()>12) orgNum=orgNum.substring(0, 11);
        this.idOfOrg = idOfOrg;
        this.tag = tag;
        this.idOfContragentReceiver = idOfContragentReceiver;
        this.contragentReceiver = contragentReceiver;
        this.idOfContragentPayer = idOfContragentPayer;
        this.contragentPayer = contragentPayer;
        this.createState = createState;
    }

    public ReportInfo(String ruleName, Integer documentFormat, String reportName, Date createdDate, Long generationTime,
            Date startDate, Date endDate, String reportFile, Long idOfOrg, Integer createState) {
        this.ruleName = ruleName;
        this.documentFormat = documentFormat;
        this.reportName = reportName;
        this.createdDate = createdDate;
        this.generationTime = generationTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportFile = reportFile;
        this.idOfOrg = idOfOrg;
        this.createState = createState;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Integer getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(Integer documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Long getIdOfReportInfo() {
        return idOfReportInfo;
    }

    public void setIdOfReportInfo(Long idOfReportInfo) {
        this.idOfReportInfo = idOfReportInfo;
    }

    public Long getGenerationTime() {
        return generationTime;
    }

    public void setGenerationTime(Long generationTime) {
        this.generationTime = generationTime;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getIdOfContragentReceiver() {
        return idOfContragentReceiver;
    }

    public void setIdOfContragentReceiver(Long idOfContragentReceiver) {
        this.idOfContragentReceiver = idOfContragentReceiver;
    }

    public String getContragentReceiver() {
        return contragentReceiver;
    }

    public void setContragentReceiver(String contragentReceiver) {
        this.contragentReceiver = contragentReceiver;
    }

    public Long getIdOfContragentPayer() {
        return idOfContragentPayer;
    }

    public void setIdOfContragentPayer(Long idOfContragentPayer) {
        this.idOfContragentPayer = idOfContragentPayer;
    }

    public String getContragentPayer() {
        return contragentPayer;
    }

    public void setContragentPayer(String contragentPayer) {
        this.contragentPayer = contragentPayer;
    }

    public Integer getCreateState() {
        return createState;
    }

    public void setCreateState(Integer createState) {
        this.createState = createState;
    }

    public String getErrorString() {
        return errorString;
    }

    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    @Override
    public String toString() {
        return "ReportInfo{" +
                "idOfReportInfo=" + idOfReportInfo +
                ", ruleName='" + ruleName + '\'' +
                ", documentFormat=" + documentFormat +
                ", reportName='" + reportName + '\'' +
                ", createdDate=" + createdDate +
                ", generationTime=" + generationTime +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reportFile='" + reportFile + '\'' +
                ", orgNum='" + orgNum + '\'' +
                ", idOfOrg=" + idOfOrg +
                ", tag='" + tag + '\'' +
                ", idOfContragentReceiver=" + idOfContragentReceiver +
                ", contragentReceiver='" + contragentReceiver + '\'' +
                ", idOfContragentPayer=" + idOfContragentPayer +
                ", contragentPayer='" + contragentPayer + '\'' +
                ", createState=" + createState +
                ", errorString='" + errorString + '\'' +
                '}';
    }

    public static class Updater {
        public ReportInfo update(ReportInfo reportInfo, String ruleName, int documentFormat,
                String reportName, Date createdDate, Long generationTime,
                Date startDate, Date endDate, String reportFile,
                String orgNum, Long idOfOrg, String tag,
                Long idOfContragentReceiver, String contragentReceiver, Long idOfContragent,
                String contragent, Integer createState) {
            reportInfo.setRuleName(ruleName);
            reportInfo.setDocumentFormat(documentFormat);
            reportInfo.setReportName(reportName);
            reportInfo.setCreatedDate(createdDate);
            reportInfo.setGenerationTime(generationTime);
            reportInfo.setStartDate(startDate);
            reportInfo.setEndDate(endDate);
            reportInfo.setReportFile(reportFile);
            reportInfo.setOrgNum(orgNum);
            reportInfo.setIdOfOrg(idOfOrg);
            reportInfo.setTag(tag);
            reportInfo.setIdOfContragentReceiver(idOfContragentReceiver);
            reportInfo.setContragentReceiver(contragentReceiver);
            reportInfo.setIdOfContragentPayer(idOfContragent);
            reportInfo.setContragentPayer(contragent);
            reportInfo.setCreateState(createState);
            return reportInfo;
        }
    }
}
