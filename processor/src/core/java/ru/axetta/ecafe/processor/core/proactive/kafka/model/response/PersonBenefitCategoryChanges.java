package ru.axetta.ecafe.processor.core.proactive.kafka.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonBenefitCategoryChanges {
    //Идентификатор персоны
    String person_id;
    //Список изменённых льготных категорий персоны
    List<BenefitCategoryChange> benefit_category_changes;

    public PersonBenefitCategoryChanges(String person_id, List<BenefitCategoryChange> benefit_category_changes) {
        this.person_id = person_id;
        this.benefit_category_changes = benefit_category_changes;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public List<BenefitCategoryChange> getBenefit_category_changes() {
        return benefit_category_changes;
    }

    public void setBenefit_category_changes(List<BenefitCategoryChange> benefit_category_changes) {
        this.benefit_category_changes = benefit_category_changes;
    }
}
