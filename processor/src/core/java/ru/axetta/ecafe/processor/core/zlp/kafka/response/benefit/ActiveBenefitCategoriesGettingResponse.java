package ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit;

import ru.axetta.ecafe.processor.core.zlp.kafka.response.*;

import java.util.List;

public class ActiveBenefitCategoriesGettingResponse extends ResponseHeader {
    private List<BenefitCategoryInfo> active_benefit_categories_info;
    private List<BenefitDocument> benefit_activity_starting_reason_documents;
    private List<BenefitDocument> benefit_activity_ending_reason_documents;
    private List<Errors> errors;
    public ActiveBenefitCategoriesGettingResponse() {

    }

    public List<BenefitDocument> getBenefit_activity_starting_reason_documents() {
        return benefit_activity_starting_reason_documents;
    }

    public void setBenefit_activity_starting_reason_documents(List<BenefitDocument> benefit_activity_starting_reason_documents) {
        this.benefit_activity_starting_reason_documents = benefit_activity_starting_reason_documents;
    }

    public List<BenefitDocument> getBenefit_activity_ending_reason_documents() {
        return benefit_activity_ending_reason_documents;
    }

    public void setBenefit_activity_ending_reason_documents(List<BenefitDocument> benefit_activity_ending_reason_documents) {
        this.benefit_activity_ending_reason_documents = benefit_activity_ending_reason_documents;
    }

    public List<Errors> getErrors() {
        return errors;
    }

    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }

    public List<BenefitCategoryInfo> getActive_benefit_categories_info() {
        return active_benefit_categories_info;
    }

    public void setActive_benefit_categories_info(List<BenefitCategoryInfo> active_benefit_categories_info) {
        this.active_benefit_categories_info = active_benefit_categories_info;
    }
}
