package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 30.03.16
 * Time: 13:05
 * To change this template use File | Settings | File Templates.
 */
public class LogInfoServiceOperationType {
    private Long idOfOperationType;
    private String nameOfOperationType;

    public LogInfoServiceOperationType() {

    }

    public LogInfoServiceOperationType(String nameOfOperationType) {
        this.nameOfOperationType = nameOfOperationType;
    }

    public Long getIdOfOperationType() {
        return idOfOperationType;
    }

    public void setIdOfOperationType(Long idOfOperationType) {
        this.idOfOperationType = idOfOperationType;
    }

    public String getNameOfOperationType() {
        return nameOfOperationType;
    }

    public void setNameOfOperationType(String nameOfOperationType) {
        this.nameOfOperationType = nameOfOperationType;
    }
}
