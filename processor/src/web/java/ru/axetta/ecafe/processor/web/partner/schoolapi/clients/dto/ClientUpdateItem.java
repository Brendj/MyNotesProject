/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientUpdateItem {
    private Long idOfClient;
    private Long idOfClientGroup;
    private Long idOfOrg;
    private Long idOfMiddleGroup;
    private Boolean useLastEEModeForPlan;
    private Date startExcludeDate;
    private Date endExcludedDate;
    private Integer gender;
    private Date birthDate;
    private Boolean confirmVisualRecognition;
    private String mobile;
    private List<Long> categoriesDiscounts = new ArrayList<>();

    public static ClientUpdateItem from(Client client, ClientGroup clientGroup){
        ClientUpdateItem clientUpdateItem = new ClientUpdateItem();
        clientUpdateItem.setIdOfClient(client.getIdOfClient());
        clientUpdateItem.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
        clientUpdateItem.setIdOfOrg(clientGroup.getCompositeIdOfClientGroup().getIdOfOrg());
        return clientUpdateItem;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfMiddleGroup() {
        return idOfMiddleGroup;
    }

    public void setIdOfMiddleGroup(Long idOfMiddleGroup) {
        this.idOfMiddleGroup = idOfMiddleGroup;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Date getStartExcludeDate() {
        return startExcludeDate;
    }

    public void setStartExcludeDate(Date startExcludeDate) {
        this.startExcludeDate = startExcludeDate;
    }

    public Date getEndExcludedDate() {
        return endExcludedDate;
    }

    public void setEndExcludedDate(Date endExcludedDate) {
        this.endExcludedDate = endExcludedDate;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Boolean getConfirmVisualRecognition() {
        return confirmVisualRecognition;
    }

    public void setConfirmVisualRecognition(Boolean confirmVisualRecognition) {
        this.confirmVisualRecognition = confirmVisualRecognition;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Long> getCategoriesDiscounts() {
        return categoriesDiscounts;
    }

    public void setCategoriesDiscounts(List<Long> categoriesDiscounts) {
        this.categoriesDiscounts = categoriesDiscounts;
    }

    public Boolean getUseLastEEModeForPlan() {
        return useLastEEModeForPlan;
    }

    public void setUseLastEEModeForPlan(Boolean useLastEEModeForPlan) {
        this.useLastEEModeForPlan = useLastEEModeForPlan;
    }
}
