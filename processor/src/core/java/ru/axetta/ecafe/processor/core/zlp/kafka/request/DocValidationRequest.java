package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class DocValidationRequest extends AbstractPushData {
    private PassportValidityCheckingRequest passportValidityCheckingRequest;

    public DocValidationRequest() {

    }

    public DocValidationRequest(RequestValidationData data) {
        PassportValidityCheckingRequest request = new PassportValidityCheckingRequest(data);
        request.setPassport_info(new PassportInfo(data));
        request.setPerson_info(new LearnerInfo(data));
        this.passportValidityCheckingRequest = request;
    }

    public PassportValidityCheckingRequest getPassportValidityCheckingRequest() {
        return passportValidityCheckingRequest;
    }

    public void setPassportValidityCheckingRequest(PassportValidityCheckingRequest passportValidityCheckingRequest) {
        this.passportValidityCheckingRequest = passportValidityCheckingRequest;
    }
}
