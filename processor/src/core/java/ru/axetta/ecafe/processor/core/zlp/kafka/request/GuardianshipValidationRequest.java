package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class GuardianshipValidationRequest extends AbstractPushData {
    private RelatednessChecking2Request relatedness_checking_2_request;

    public GuardianshipValidationRequest() {

    }

    public GuardianshipValidationRequest(RequestValidationData data) {
        RelatednessChecking2Request request = new RelatednessChecking2Request(data);
        request.setLearnerInfo(new LearnerInfo(data));
        request.setLearner_document_info(new LearnerDocumentInfo(data));
        request.setParent_info(new ParentInfo(data));
        request.setParent_passport_info(new ParentPassportInfo(data));

        this.relatedness_checking_2_request = request;
    }

    public RelatednessChecking2Request getRelatedness_checking_2_request() {
        return relatedness_checking_2_request;
    }

    public void setRelatedness_checking_2_request(RelatednessChecking2Request relatedness_checking_2_request) {
        this.relatedness_checking_2_request = relatedness_checking_2_request;
    }
}
