package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 30.03.16
 * Time: 11:52
 * To change this template use File | Settings | File Templates.
 */
public class LogInfoService {
    private Long idOfLogInfoService;
    private String idOfSystem;
    private Date createdDate;
    private String ssoId;
    private Client client;
    private Long idOfClient;
    private LogInfoServiceOperationType operationType;

    public LogInfoService() {

    }

    public LogInfoService(String idOfSystem, Date createdDate,
            String ssoId, Long idOfClient, LogInfoServiceOperationType operationType) {
        this.idOfSystem = idOfSystem;
        this.createdDate = createdDate;
        this.ssoId = ssoId;
        this.idOfClient = idOfClient;
        this.operationType = operationType;
    }

    public Long getIdOfLogInfoService() {
        return idOfLogInfoService;
    }

    public void setIdOfLogInfoService(Long idOfLogInfoService) {
        this.idOfLogInfoService = idOfLogInfoService;
    }

    public String getIdOfSystem() {
        return idOfSystem;
    }

    public void setIdOfSystem(String idOfSystem) {
        this.idOfSystem = idOfSystem;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LogInfoServiceOperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(LogInfoServiceOperationType operationType) {
        this.operationType = operationType;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }
}
