package ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit;

import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;

public class BenefitResponse extends AbstractPullData {
    private ActiveBenefitCategoriesGettingResponse active_benefit_categories_getting_response;

    public ActiveBenefitCategoriesGettingResponse getActive_benefit_categories_getting_response() {
        return active_benefit_categories_getting_response;
    }

    public void setActive_benefit_categories_getting_response(ActiveBenefitCategoriesGettingResponse active_benefit_categories_getting_response) {
        this.active_benefit_categories_getting_response = active_benefit_categories_getting_response;
    }
}
