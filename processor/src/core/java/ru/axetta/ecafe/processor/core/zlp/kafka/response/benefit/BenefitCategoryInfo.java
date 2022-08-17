package ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit;

public class BenefitCategoryInfo {
    private String benefit_category_id;
    private String benefit_category_name;
    private String benefit_activity_date_from; //"1997-03-04T00:00"
    private String benefit_activity_date_to;

    public BenefitCategoryInfo() {
    }

    public String getBenefit_category_id() {
        return benefit_category_id;
    }

    public void setBenefit_category_id(String benefit_category_id) {
        this.benefit_category_id = benefit_category_id;
    }

    public String getBenefit_category_name() {
        return benefit_category_name;
    }

    public void setBenefit_category_name(String benefit_category_name) {
        this.benefit_category_name = benefit_category_name;
    }

    public String getBenefit_activity_date_from() {
        return benefit_activity_date_from;
    }

    public void setBenefit_activity_date_from(String benefit_activity_date_from) {
        this.benefit_activity_date_from = benefit_activity_date_from;
    }

    public String getBenefit_activity_date_to() {
        return benefit_activity_date_to;
    }

    public void setBenefit_activity_date_to(String benefit_activity_date_to) {
        this.benefit_activity_date_to = benefit_activity_date_to;
    }
}
