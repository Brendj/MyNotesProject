package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class RelatednessChecking2Request extends RequestHeader {
    private LearnerInfo learnerInfo;
    private LearnerDocumentInfo learner_document_info;
    private ParentInfo parent_info;
    private ParentPassportInfo parent_passport_info;

    public RelatednessChecking2Request() {

    }

    public RelatednessChecking2Request(RequestValidationData data) {
        super(data);
    }

    public LearnerInfo getLearnerInfo() {
        return learnerInfo;
    }

    public void setLearnerInfo(LearnerInfo learnerInfo) {
        this.learnerInfo = learnerInfo;
    }

    public LearnerDocumentInfo getLearner_document_info() {
        return learner_document_info;
    }

    public void setLearner_document_info(LearnerDocumentInfo learner_document_info) {
        this.learner_document_info = learner_document_info;
    }

    public ParentInfo getParent_info() {
        return parent_info;
    }

    public void setParent_info(ParentInfo parent_info) {
        this.parent_info = parent_info;
    }

    public ParentPassportInfo getParent_passport_info() {
        return parent_passport_info;
    }

    public void setParent_passport_info(ParentPassportInfo parent_passport_info) {
        this.parent_passport_info = parent_passport_info;
    }
}
