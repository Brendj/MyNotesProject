package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class DocValidationRequest extends AbstractPushData {
    private PassportBySerieNumberValidityCheckingRequest passportBySerieNumberValidityCheckingRequest;

    public DocValidationRequest() {

    }

    public DocValidationRequest(RequestValidationData data) {
        PassportBySerieNumberValidityCheckingRequest request = new PassportBySerieNumberValidityCheckingRequest(data);
        request.setPassport_info(new LearnerDocumentInfo(data));
        this.passportBySerieNumberValidityCheckingRequest = request;
    }

    public PassportBySerieNumberValidityCheckingRequest getPassportBySerieNumberValidityCheckingRequest() {
        return passportBySerieNumberValidityCheckingRequest;
    }

    public void setPassportBySerieNumberValidityCheckingRequest(PassportBySerieNumberValidityCheckingRequest passportBySerieNumberValidityCheckingRequest) {
        this.passportBySerieNumberValidityCheckingRequest = passportBySerieNumberValidityCheckingRequest;
    }
}
