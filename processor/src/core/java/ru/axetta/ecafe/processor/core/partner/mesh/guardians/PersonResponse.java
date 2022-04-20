package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import java.util.List;

public class PersonResponse {
    public static Integer OK_CODE = 0;
    public static String OK_MESSAGE = "OK";
    public static Integer NOT_ENOUGH_CLIENT_DATA_CODE = 1;
    public static String NOT_ENOUGH_CLIENT_DATA_MESSAGE = "В карточке клиента не хватает данных: ";
    public static Integer INTERNAL_ERROR_CODE = 2;
    public static String INTERNAL_ERROR_MESSAGE = "Внутренняя ошибка";

    private Integer code;
    private String message;
    private List<MeshGuardianPerson> response;

    public PersonResponse okResponse(List<MeshGuardianPerson> response) {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        this.setResponse(response);
        return this;
    }

    public PersonResponse notEnoughClientDataResponse(String detailMessage) {
        this.setCode(NOT_ENOUGH_CLIENT_DATA_CODE);
        this.setMessage(NOT_ENOUGH_CLIENT_DATA_MESSAGE);
        return this;
    }

    public PersonResponse internalErrorResponse() {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE);
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MeshGuardianPerson> getResponse() {
        return response;
    }

    public void setResponse(List<MeshGuardianPerson> response) {
        this.response = response;
    }
}
