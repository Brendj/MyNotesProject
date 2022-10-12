package ru.axetta.ecafe.processor.core.proactive.kafka.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BenefitCategoryChange {
    //Код льготной категории
    String benefit_category_code;
    //Дата начала действия льготной категории
    String begin_date;
    //Дата окончания действия льготной категории
    String end_date;
    //Активна ли льготная категория
    Boolean is_actual;
    //Дата, когда льготная категория стала неактуальной
    String not_actual_date;

    public BenefitCategoryChange(String benefit_category_code, String begin_date, String end_date, Boolean is_actual, String not_actual_date) {
        this.benefit_category_code = benefit_category_code;
        this.begin_date = begin_date;
        this.end_date = end_date;
        this.is_actual = is_actual;
        this.not_actual_date = not_actual_date;
    }

    public String getBenefit_category_code() {
        return benefit_category_code;
    }

    public void setBenefit_category_code(String benefit_category_code) {
        this.benefit_category_code = benefit_category_code;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public Boolean getIs_actual() {
        return is_actual;
    }

    public void setIs_actual(Boolean is_actual) {
        this.is_actual = is_actual;
    }

    public String getNot_actual_date() {
        return not_actual_date;
    }

    public void setNot_actual_date(String not_actual_date) {
        this.not_actual_date = not_actual_date;
    }
}
