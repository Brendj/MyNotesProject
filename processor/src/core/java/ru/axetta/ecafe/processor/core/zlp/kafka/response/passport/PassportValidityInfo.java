package ru.axetta.ecafe.processor.core.zlp.kafka.response.passport;

public class PassportValidityInfo {
    private PassportStatusInfo passport_status_info;
    private PassportInvalidReasonInfo invalidity_reason_info;
    private String invalidity_since;

    public PassportStatusInfo getPassport_status_info() {
        return passport_status_info;
    }

    public void setPassport_status_info(PassportStatusInfo passport_status_info) {
        this.passport_status_info = passport_status_info;
    }

    public PassportInvalidReasonInfo getInvalidity_reason_info() {
        return invalidity_reason_info;
    }

    public void setInvalidity_reason_info(PassportInvalidReasonInfo invalidity_reason_info) {
        this.invalidity_reason_info = invalidity_reason_info;
    }

    public String getInvalidity_since() {
        return invalidity_since;
    }

    public void setInvalidity_since(String invalidity_since) {
        this.invalidity_since = invalidity_since;
    }
}
