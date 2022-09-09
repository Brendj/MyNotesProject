package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class GuardianshipValidationRequest extends AbstractPushData {
    private RelatednessCheckingRequest relatedness_checking_request;

    public GuardianshipValidationRequest() {

    }

    public GuardianshipValidationRequest(RequestValidationData data) {
        RelatednessCheckingRequest request = new RelatednessCheckingRequest(data);
        request.setLearnerInfo(new LearnerInfo(data));
        request.setLearner_document_info(new LearnerDocumentInfo(data));
        request.setParent_info(new ParentInfo(data));
        request.setParent_passport_info(new ParentPassportInfo(data));
        request.setLearner_snils_info(data.getChildSnilsInfo());
        request.setParent_snils_info(data.getParentSnilsInfo());

        this.relatedness_checking_request = request;
    }

    public RelatednessCheckingRequest getRelatedness_checking_request() {
        return relatedness_checking_request;
    }

    public void setRelatedness_checking_request(RelatednessCheckingRequest relatedness_checking_request) {
        this.relatedness_checking_request = relatedness_checking_request;
    }
}
