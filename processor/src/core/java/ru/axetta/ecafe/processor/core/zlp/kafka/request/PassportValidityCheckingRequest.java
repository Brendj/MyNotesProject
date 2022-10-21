package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class PassportValidityCheckingRequest extends RequestHeader {
    private ParentInfo person_info;
    private ParentPassportInfo passport_info;

    public PassportValidityCheckingRequest() {

    }

    public PassportValidityCheckingRequest(RequestValidationData data) {
        super(data);
    }


    public ParentInfo getPerson_info() {
        return person_info;
    }

    public void setPerson_info(ParentInfo person_info) {
        this.person_info = person_info;
    }

    public ParentPassportInfo getPassport_info() {
        return passport_info;
    }

    public void setPassport_info(ParentPassportInfo passport_info) {
        this.passport_info = passport_info;
    }
}
