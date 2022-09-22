package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class DocValidationRequest extends AbstractPushData {
    private PassportValidityCheckingRequest passport_validity_checking_request;

    public DocValidationRequest() {

    }

    public DocValidationRequest(RequestValidationData data) {
        PassportValidityCheckingRequest request = new PassportValidityCheckingRequest(data);
        request.setPassport_info(new ParentPassportInfo(data));
        request.setPerson_info(new ParentInfo(data));
        this.passport_validity_checking_request = request;
    }

    public PassportValidityCheckingRequest getPassport_validity_checking_request() {
        return passport_validity_checking_request;
    }

    public void setPassport_validity_checking_request(PassportValidityCheckingRequest passport_validity_checking_request) {
        this.passport_validity_checking_request = passport_validity_checking_request;
    }
}
