package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.push.model.AbstractPushData;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;

import java.util.Date;

public class AppMezhvedRequest {
    private Long idOfMezhvedRequest;
    private ApplicationForFood applicationForFood;
    private String requestId;
    private String requestPayload;
    private String responsePayload;
    private Date createdDate;
    private Date lastUpdate;
    private AppMezhvedRequestType requestType;
    private AppMezhvedResponseType responseType;
    private Date responseDate;

    public AppMezhvedRequest() {

    }

    public AppMezhvedRequest(AbstractPushData request, String jsonString, ApplicationForFood applicationForFood) {
        if (request instanceof GuardianshipValidationRequest)
        this.requestId = ((GuardianshipValidationRequest)request).getRelatedness_checking_2_request().getRequest_id();
        this.requestPayload = jsonString;
        this.createdDate = new Date();
        this.requestType = getAppMezhvedRequestTypeByMessage(request);
        this.applicationForFood = applicationForFood;
    }

    private AppMezhvedRequestType getAppMezhvedRequestTypeByMessage(AbstractPushData request) {
        if (request instanceof GuardianshipValidationRequest) return AppMezhvedRequestType.GUARDIANSHIP;
        // todo uncomment -- if (request instanceof DocValidationRequest) return AppMezhvedRequestType.DOCS;
        return null;
    }

    public Long getIdOfMezhvedRequest() {
        return idOfMezhvedRequest;
    }

    public void setIdOfMezhvedRequest(Long idOfMezhvedRequest) {
        this.idOfMezhvedRequest = idOfMezhvedRequest;
    }

    public ApplicationForFood getApplicationForFood() {
        return applicationForFood;
    }

    public void setApplicationForFood(ApplicationForFood applicationForFood) {
        this.applicationForFood = applicationForFood;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public void setResponsePayload(String responsePayload) {
        this.responsePayload = responsePayload;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public AppMezhvedRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(AppMezhvedRequestType requestType) {
        this.requestType = requestType;
    }

    public AppMezhvedResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(AppMezhvedResponseType responseType) {
        this.responseType = responseType;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }
}
