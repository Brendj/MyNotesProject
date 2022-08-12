package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class PassportBySerieNumberValidityCheckingRequest extends RequestHeader {
    private LearnerDocumentInfo passport_info;

    public PassportBySerieNumberValidityCheckingRequest() {

    }

    public PassportBySerieNumberValidityCheckingRequest(RequestValidationData data) {
        super(data);
    }

    public LearnerDocumentInfo getPassport_info() {
        return passport_info;
    }

    public void setPassport_info(LearnerDocumentInfo passport_info) {
        this.passport_info = passport_info;
    }
}
