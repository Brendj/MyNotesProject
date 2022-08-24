package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class ActiveBenefitCategoriesGettingRequest extends RequestHeader {
    private LearnerInfo person_info;
    private LearnerDocumentInfo document_info;

    public ActiveBenefitCategoriesGettingRequest() {

    }

    public ActiveBenefitCategoriesGettingRequest(RequestValidationData data) {
        super(data);
    }

    public LearnerInfo getPerson_info() {
        return person_info;
    }

    public void setPerson_info(LearnerInfo person_info) {
        this.person_info = person_info;
    }

    public LearnerDocumentInfo getDocument_info() {
        return document_info;
    }

    public void setDocument_info(LearnerDocumentInfo document_info) {
        this.document_info = document_info;
    }
}
