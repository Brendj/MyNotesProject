package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class ContactsIdResponse {

    private Integer contactId;
    private Integer contactType;

    public ContactsIdResponse(Integer contactId, Integer contactType) {
        this.contactId = contactId;
        this.contactType = contactType;
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
}
