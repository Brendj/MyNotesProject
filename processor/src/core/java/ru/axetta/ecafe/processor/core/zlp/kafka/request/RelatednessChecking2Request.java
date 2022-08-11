package ru.axetta.ecafe.processor.core.zlp.kafka.request;

//"relatedness_checking_2_request"
public class RelatednessChecking2Request {
    private String request_id;
    private String requesting_system;
    private String requested_method;
    private String state_service_variety_code;
    private LearnerInfo learnerInfo;
    private LearnerDocumentInfo learner_document_info;
    private ParentInfo parent_info;
    private ParentPassportInfo parent_passport_info;

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getRequesting_system() {
        return requesting_system;
    }

    public void setRequesting_system(String requesting_system) {
        this.requesting_system = requesting_system;
    }

    public String getRequested_method() {
        return requested_method;
    }

    public void setRequested_method(String requested_method) {
        this.requested_method = requested_method;
    }

    public String getState_service_variety_code() {
        return state_service_variety_code;
    }

    public void setState_service_variety_code(String state_service_variety_code) {
        this.state_service_variety_code = state_service_variety_code;
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
