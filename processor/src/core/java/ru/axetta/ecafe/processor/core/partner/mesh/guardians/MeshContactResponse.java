package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import ru.axetta.ecafe.processor.core.partner.mesh.json.Contact;
import ru.axetta.ecafe.processor.core.partner.mesh.json.PersonDocument;
import ru.axetta.ecafe.processor.core.persistence.DulDetail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MeshContactResponse extends MeshGuardianResponse {
    Integer type;
    String value;

    public MeshContactResponse() {
        super();
    }

    public MeshContactResponse(Contact contact) {
        this.type = contact.getTypeId();
        this.value = contact.getData();
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
    }

    public MeshContactResponse(Integer code, String message) {
        super(code, message);
    }

    public MeshContactResponse okResponse() {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        return this;
    }

    public MeshContactResponse internalErrorResponse() {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE);
        return this;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
