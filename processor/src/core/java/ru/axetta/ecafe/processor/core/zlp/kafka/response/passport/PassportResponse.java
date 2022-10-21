package ru.axetta.ecafe.processor.core.zlp.kafka.response.passport;

import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;

public class PassportResponse extends AbstractPullData {
    private PassportValidityCheckingResponse passport_validity_checking_response;

    public PassportValidityCheckingResponse getPassport_validity_checking_response() {
        return passport_validity_checking_response;
    }

    public void setPassport_validity_checking_response(PassportValidityCheckingResponse passport_validity_checking_response) {
        this.passport_validity_checking_response = passport_validity_checking_response;
    }
}
