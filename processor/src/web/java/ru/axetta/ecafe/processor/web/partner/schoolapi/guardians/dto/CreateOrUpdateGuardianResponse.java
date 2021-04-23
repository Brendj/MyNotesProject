/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.BaseResponse;

public class CreateOrUpdateGuardianResponse extends BaseResponse
{
    private Long recordId;
    private Long childClientId;
    private Long guardianClientId;
    private Integer rights;
    private Integer relationType;
    private Boolean isEnabledInformationSupport;

    public Long getRecordId() {
        return recordId;
    }
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public long getChildClientId() {
        return childClientId;
    }
    public void setChildClientId(long idOfClient) {
        this.childClientId = idOfClient;
    }

    public long getGuardianClientId() { return guardianClientId; }
    public void setGuardianClientId(long idOfGuardian) {
        this.guardianClientId = idOfGuardian;
    }

    public Integer getRights() {
        return rights;
    }
    public void setRights(Integer rights) {
        this.rights = rights;
    }

    public Integer getRelationType() {
        return relationType;
    }
    public void setRelationType(Integer reletaionType) {
        this.relationType = reletaionType;
    }

    public Boolean getIsEnabledInformationSupport() {
        return isEnabledInformationSupport;
    }
    public void setIsEnabledInformationSupport(Boolean isEnabledInformationSupport) { this.isEnabledInformationSupport = isEnabledInformationSupport; }

    private CreateOrUpdateGuardianResponse() { }

    public static CreateOrUpdateGuardianResponse success(CreateOrUpdateGuardianRequest createGuardianRequest, long recordId)
    {
        CreateOrUpdateGuardianResponse result = new CreateOrUpdateGuardianResponse();
        result.result = 0;
        result.errorText = null;
        result.recordId = recordId;
        result.childClientId = createGuardianRequest.getChildClientId();
        result.guardianClientId = createGuardianRequest.getGuardianClientId();
        result.rights = createGuardianRequest.getRights();
        result.relationType = createGuardianRequest.getRelationType();
        result.isEnabledInformationSupport = createGuardianRequest.getIsEnabledInformationSupport();
        return result;
    }

    public static CreateOrUpdateGuardianResponse error(CreateOrUpdateGuardianRequest createGuardianRequest, int errorCode, String errorText)
    {
        CreateOrUpdateGuardianResponse result = new CreateOrUpdateGuardianResponse();
        result.result = errorCode;
        result.errorText = errorText;
        result.recordId = null;
        result.childClientId = createGuardianRequest.getChildClientId();
        result.guardianClientId = createGuardianRequest.getGuardianClientId();
        result.rights = createGuardianRequest.getRights();
        result.relationType = createGuardianRequest.getRelationType();
        result.isEnabledInformationSupport = createGuardianRequest.getIsEnabledInformationSupport();
        return result;
    }
}
