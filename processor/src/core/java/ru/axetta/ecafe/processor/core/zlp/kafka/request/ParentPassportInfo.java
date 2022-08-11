package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.GuardianshipValidationData;

public class ParentPassportInfo {
    private String passport_series;
    private String passport_number;
    private String passport_issue_date;

    public ParentPassportInfo() {

    }

    public ParentPassportInfo(GuardianshipValidationData data) {
        this.passport_series = data.getParentPassportSeries();
        this.passport_number = data.getParentPassportNumber();
        this.passport_issue_date = data.getParentPassportIssueDate();
    }

    public String getPassport_series() {
        return passport_series;
    }

    public void setPassport_series(String passport_series) {
        this.passport_series = passport_series;
    }

    public String getPassport_number() {
        return passport_number;
    }

    public void setPassport_number(String passport_number) {
        this.passport_number = passport_number;
    }

    public String getPassport_issue_date() {
        return passport_issue_date;
    }

    public void setPassport_issue_date(String passport_issue_date) {
        this.passport_issue_date = passport_issue_date;
    }
}
