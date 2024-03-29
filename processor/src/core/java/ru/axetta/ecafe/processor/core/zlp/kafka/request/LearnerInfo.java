package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class LearnerInfo {
    private String last_name;
    private String first_name;
    private String middle_name;
    private String birth_date; //"2019-07-29T00:00:00"

    public LearnerInfo() {

    }

    public LearnerInfo(RequestValidationData data) {
        this.last_name = data.getChildLastName();
        this.first_name = data.getChildFirstName();
        this.middle_name = data.getChildMiddleName();
        this.birth_date = data.getChildBirthDate();
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }
}
