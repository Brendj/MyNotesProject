package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class MeshGuardianResponse {
    public static Integer OK_CODE = 0;
    public static String OK_MESSAGE = "OK";
    public static Integer NOT_ENOUGH_CLIENT_DATA_CODE = 1;
    public static String NOT_ENOUGH_CLIENT_DATA_MESSAGE = "В карточке клиента не хватает данных: ";
    public static Integer INTERNAL_ERROR_CODE = 100;
    public static String INTERNAL_ERROR_MESSAGE = "Внутренняя ошибка";

    protected Integer code;
    protected String message;

    public MeshGuardianResponse() {
    }

    public MeshGuardianResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
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
}
