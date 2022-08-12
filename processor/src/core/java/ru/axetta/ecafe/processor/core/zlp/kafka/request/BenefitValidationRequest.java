package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class BenefitValidationRequest extends AbstractPushData {
    private ActiveBenefitCategoriesGettingRequest active_benefit_categories_getting_request;

    public BenefitValidationRequest() {

    }

    public BenefitValidationRequest(RequestValidationData data) {
        ActiveBenefitCategoriesGettingRequest request = new ActiveBenefitCategoriesGettingRequest(data);
        request.setPerson_info(new LearnerInfo(data));
        request.setDocument_info(new LearnerDocumentInfo(data));
        this.active_benefit_categories_getting_request = request;
    }

    public ActiveBenefitCategoriesGettingRequest getActive_benefit_categories_getting_request() {
        return active_benefit_categories_getting_request;
    }

    public void setActive_benefit_categories_getting_request(ActiveBenefitCategoriesGettingRequest active_benefit_categories_getting_request) {
        this.active_benefit_categories_getting_request = active_benefit_categories_getting_request;
    }
}
