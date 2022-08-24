package ru.axetta.ecafe.processor.core.zlp.kafka.response.passport;

import ru.axetta.ecafe.processor.core.zlp.kafka.response.Errors;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.LearnerDocumentInfo;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.ResponseHeader;

import java.util.List;

public class PassportBySerieNumberValidityCheckingResponse extends ResponseHeader {
    private LearnerDocumentInfo passport_info;
    private PassportValidityInfo passportValidityInfo;
    private List<Errors> errors;



    public PassportBySerieNumberValidityCheckingResponse() {

    }

    public LearnerDocumentInfo getPassport_info() {
        return passport_info;
    }

    public void setPassport_info(LearnerDocumentInfo passport_info) {
        this.passport_info = passport_info;
    }

    public PassportValidityInfo getPassportValidityInfo() {
        return passportValidityInfo;
    }

    public void setPassportValidityInfo(PassportValidityInfo passportValidityInfo) {
        this.passportValidityInfo = passportValidityInfo;
    }

    public List<Errors> getErrors() {
        return errors;
    }

    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }
}
