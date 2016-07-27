/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.clients;

/**
 * Created by i.semenov on 26.07.2016.
 */
public class ClientContactInfo {
    private Long idOfClient;
    private Long idOfOrg;
    private Long idOfClientGroup;
    private String mobile;
    private String email;
    private Long contractId;
    private Integer notifyViaSMS;
    private Integer notifyViaEmail;
    private Integer notifyViaPUSH;
    private String ssoid;
    private Long guardCount;

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Integer getNotifyViaSMS() {
        return notifyViaSMS;
    }

    public void setNotifyViaSMS(Integer notifyViaSms) {
        this.notifyViaSMS = notifyViaSms;
    }

    public Integer getNotifyViaEmail() {
        return notifyViaEmail;
    }

    public void setNotifyViaEmail(Integer notifyViaEmail) {
        this.notifyViaEmail = notifyViaEmail;
    }

    public Integer getNotifyViaPUSH() {
        return notifyViaPUSH;
    }

    public void setNotifyViaPUSH(Integer notifyViaPush) {
        this.notifyViaPUSH = notifyViaPush;
    }

    public String getSsoid() {
        return ssoid;
    }

    public void setSsoid(String ssoId) {
        this.ssoid = ssoId;
    }

    public Long getGuardCount() {
        return guardCount;
    }

    public void setGuardCount(Long guardCount) {
        this.guardCount = guardCount;
    }
}
