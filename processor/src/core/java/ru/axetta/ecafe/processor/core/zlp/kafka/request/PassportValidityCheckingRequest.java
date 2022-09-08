package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class PassportValidityCheckingRequest extends RequestHeader {
    private LearnerInfo person_info;
    private PassportInfo passport_info;

    public PassportValidityCheckingRequest() {

    }

    public PassportValidityCheckingRequest(RequestValidationData data) {
        super(data);
    }


    public LearnerInfo getPerson_info() {
        return person_info;
    }

    public void setPerson_info(LearnerInfo person_info) {
        this.person_info = person_info;
    }

    public PassportInfo getPassport_info() {
        return passport_info;
    }

    public void setPassport_info(PassportInfo passport_info) {
        this.passport_info = passport_info;
    }
}
