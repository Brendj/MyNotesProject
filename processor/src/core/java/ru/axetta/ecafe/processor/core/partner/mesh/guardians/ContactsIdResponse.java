package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class ContactsIdResponse {

    private Integer contactId;
    private Integer contactType;
    private Boolean isDefault;
    private String contactValue;

    public ContactsIdResponse(Integer contactId, Integer contactType, Boolean isDefault, String contactValue) {
        this.contactId = contactId;
        this.contactType = contactType;
        this.isDefault = isDefault;
        this.contactValue = contactValue;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public Integer getContactType() {
        return contactType;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getContactValue() {
        return contactValue;
    }

    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }
}
