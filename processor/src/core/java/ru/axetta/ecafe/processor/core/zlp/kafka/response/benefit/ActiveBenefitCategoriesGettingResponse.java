package ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit;

import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.*;

import java.util.List;

public class ActiveBenefitCategoriesGettingResponse extends AbstractPullData {
    private String request_id;
    private String person_id;
    private List<BenefitCategory> benefit_categories;
    private List<Errors> errors;
    public ActiveBenefitCategoriesGettingResponse() {

    }


    public List<Errors> getErrors() {
        return errors;
    }

    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }

    public List<BenefitCategory> getBenefit_categories() {
        return benefit_categories;
    }

    public void setBenefit_categories(List<BenefitCategory> benefit_categories) {
        this.benefit_categories = benefit_categories;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }
}
