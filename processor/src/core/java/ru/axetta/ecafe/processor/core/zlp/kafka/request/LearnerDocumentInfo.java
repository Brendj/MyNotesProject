package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.GuardianshipValidationData;

public class LearnerDocumentInfo {
    private String document_type_code;
    private String document_series;
    private String document_number;
    private String document_issue_date;

    public LearnerDocumentInfo() {

    }

    public LearnerDocumentInfo(GuardianshipValidationData data) {
        this.document_type_code = data.getChildDocumentTypeCode();
        this.document_series = data.getChildDocumentSeries();
        this.document_number = data.getChildDocumentNumber();
        this.document_issue_date = data.getChildDocumentIssueDate();
    }

    public String getDocument_type_code() {
        return document_type_code;
    }

    public void setDocument_type_code(String document_type_code) {
        this.document_type_code = document_type_code;
    }

    public String getDocument_series() {
        return document_series;
    }

    public void setDocument_series(String document_series) {
        this.document_series = document_series;
    }

    public String getDocument_number() {
        return document_number;
    }

    public void setDocument_number(String document_number) {
        this.document_number = document_number;
    }

    public String getDocument_issue_date() {
        return document_issue_date;
    }

    public void setDocument_issue_date(String document_issue_date) {
        this.document_issue_date = document_issue_date;
    }
}
