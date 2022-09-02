package ru.axetta.ecafe.processor.core.zlp.kafka.response.passport;

import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;

public class PassportResponse extends AbstractPullData {
    private PassportBySerieNumberValidityCheckingResponse passport_by_serie_number_validity_checking_response;

    public PassportBySerieNumberValidityCheckingResponse getPassport_by_serie_number_validity_checking_response() {
        return passport_by_serie_number_validity_checking_response;
    }

    public void setPassport_by_serie_number_validity_checking_response(PassportBySerieNumberValidityCheckingResponse passport_by_serie_number_validity_checking_response) {
        this.passport_by_serie_number_validity_checking_response = passport_by_serie_number_validity_checking_response;
    }
}
