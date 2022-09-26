package ru.axetta.ecafe.processor.core.partner.etpmv.enums;

public enum MessageType {
     /*0*/MOS(0,"МоС");

    private Integer identification;
    private String description;

    MessageType(Integer identification, String description) {
        this.identification = identification;
        this.description = description;
    }

    public Integer getIdentification() {
        return identification;
    }

    public void setIdentification(Integer identification) {
        this.identification = identification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
