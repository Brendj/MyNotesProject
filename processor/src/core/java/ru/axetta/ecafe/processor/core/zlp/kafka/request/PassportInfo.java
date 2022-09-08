package ru.axetta.ecafe.processor.core.zlp.kafka.request;

import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;

public class PassportInfo extends ParentPassportInfo {
    private String passport_issuer_code;

    public PassportInfo() {

    }

    public PassportInfo(RequestValidationData data) {
        super(data);
        this.passport_issuer_code = data.getIssuerCode();
    }

    public String getPassport_issuer_code() {
        return passport_issuer_code;
    }

    public void setPassport_issuer_code(String passport_issuer_code) {
        this.passport_issuer_code = passport_issuer_code;
    }
}
