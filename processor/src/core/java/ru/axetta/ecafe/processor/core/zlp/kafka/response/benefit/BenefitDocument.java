package ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit;

public class BenefitDocument {
    private String benefit_category_id;
    private String name;
    private String series;
    private String number;
    private String issue_date; //"1999-04-17T00:00"
    private String issuer;

    public String getBenefit_category_id() {
        return benefit_category_id;
    }

    public void setBenefit_category_id(String benefit_category_id) {
        this.benefit_category_id = benefit_category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
