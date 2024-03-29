package ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian;

import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.ParentInfo;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.ParentPassportInfo;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.Errors;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.LearnerDocumentInfo;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.ResponseHeader;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.LearnerInfo;

import java.util.List;

public class RelatednessCheckingResponse extends ResponseHeader {
    private LearnerInfo learnerInfo;
    private LearnerDocumentInfo learner_document_info;
    private ParentInfo parent_info;
    private ParentPassportInfo parent_passport_info;
    private String parent_snils_info;
    private String learner_snils_info;
    private RelatednessInfo relatedness_info;
    private List<Errors> errors;

    public RelatednessCheckingResponse() {

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

    public String getParent_snils_info() {
        return parent_snils_info;
    }

    public void setParent_snils_info(String parent_snils_info) {
        this.parent_snils_info = parent_snils_info;
    }

    public List<Errors> getErrors() {
        return errors;
    }

    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }

    public String getLearner_snils_info() {
        return learner_snils_info;
    }

    public void setLearner_snils_info(String learner_snils_info) {
        this.learner_snils_info = learner_snils_info;
    }

    public RelatednessInfo getRelatedness_info() {
        return relatedness_info;
    }

    public void setRelatedness_info(RelatednessInfo relatedness_info) {
        this.relatedness_info = relatedness_info;
    }
}
