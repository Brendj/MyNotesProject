/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

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
    
    protected ReportInfo() {

    }

    public ReportInfo(String ruleName, Integer documentFormat, String reportName, Date createdDate, Long generationTime,
            Date startDate, Date endDate, String reportFile, String orgNum, Long idOfOrg, String tag, Long idOfContragentReceiver,
            String contragentReceiver, Long idOfContragentPayer, String contragentPayer) {
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
                ", idOfContragentReceiver='" + idOfContragentReceiver + '\'' +
                ", contragentReceiver='" + contragentReceiver + '\'' +
                ", idOfContragent='" + idOfContragentPayer + '\'' +
                ", contragent='" + contragentPayer + '\'' +
                '}';
    }
}
