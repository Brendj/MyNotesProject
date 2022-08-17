package ru.axetta.ecafe.processor.core.zlp.kafka.response.passport;

public class PassportInvalidReasonInfo {
    private String invalidity_reason;
    private String invalidity_reason_code;
    private String invalidity_since;

    public String getInvalidity_reason() {
        return invalidity_reason;
    }

    public void setInvalidity_reason(String invalidity_reason) {
        this.invalidity_reason = invalidity_reason;
    }

    public String getInvalidity_reason_code() {
        return invalidity_reason_code;
    }

    public void setInvalidity_reason_code(String invalidity_reason_code) {
        this.invalidity_reason_code = invalidity_reason_code;
    }

    public String getInvalidity_since() {
        return invalidity_since;
    }

    public void setInvalidity_since(String invalidity_since) {
        this.invalidity_since = invalidity_since;
    }
}
