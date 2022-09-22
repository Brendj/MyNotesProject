package ru.axetta.ecafe.processor.core.zlp.kafka.response.passport;

public class PassportStatusInfo {
    private String passport_status;
    private String passport_status_code;

    public String getPassport_status() {
        return passport_status;
    }

    public void setPassport_status(String passport_status) {
        this.passport_status = passport_status;
    }

    public String getPassport_status_code() {
        return passport_status_code;
    }

    public void setPassport_status_code(String passport_status_code) {
        this.passport_status_code = passport_status_code;
    }
}
