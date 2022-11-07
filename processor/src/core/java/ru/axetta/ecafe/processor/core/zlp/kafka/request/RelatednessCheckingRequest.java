package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class RelatednessCheckingRequest extends RequestHeader {
    private LearnerInfo learnerInfo;
    private LearnerDocumentInfo learner_document_info;
    private String learner_snils_info;
    private ParentInfo parent_info;
    private ParentPassportInfo parent_passport_info;
    private String parent_snils_info;

    public RelatednessCheckingRequest() {

    }

    public RelatednessCheckingRequest(RequestValidationData data) {
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

    public String getLearner_snils_info() {
        return learner_snils_info;
    }

    public void setLearner_snils_info(String learner_snils_info) {
        this.learner_snils_info = learner_snils_info;
    }

    public String getParent_snils_info() {
        return parent_snils_info;
    }

    public void setParent_snils_info(String parent_snils_info) {
        this.parent_snils_info = parent_snils_info;
    }
}
