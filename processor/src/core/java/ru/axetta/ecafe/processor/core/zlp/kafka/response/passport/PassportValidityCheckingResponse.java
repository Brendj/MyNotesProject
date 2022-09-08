package ru.axetta.ecafe.processor.core.zlp.kafka.response.passport;

import ru.axetta.ecafe.processor.core.zlp.kafka.request.PassportInfo;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.Errors;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.ResponseHeader;

import java.util.List;

public class PassportValidityCheckingResponse extends ResponseHeader {
    private PassportInfo passport_info;
    private PassportValidityInfo passport_validity_info;
    private List<Errors> errors;



    public PassportValidityCheckingResponse() {

    }

    public PassportValidityInfo getPassport_validity_info() {
        return passport_validity_info;
    }

    public void setPassport_validity_info(PassportValidityInfo passport_validity_info) {
        this.passport_validity_info = passport_validity_info;
    }

    public List<Errors> getErrors() {
        return errors;
    }

    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }

    public PassportInfo getPassport_info() {
        return passport_info;
    }

    public void setPassport_info(PassportInfo passport_info) {
        this.passport_info = passport_info;
    }
}
