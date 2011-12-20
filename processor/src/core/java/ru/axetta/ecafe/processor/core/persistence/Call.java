/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class Call {

    private Long IdOfCall;
    private Client client;
    private Date callTime;
    private String reason;
    private Integer callType;
    private Integer state;

    Call() {
        // For Hibernate only
    }

    Call(Client client, Date callTime, String reason, int callType, int state) throws Exception {
        this.client = client;
        this.callTime = callTime;
        this.reason = reason;
        this.callType = callType;
        this.state = state;
    }

    public Long getIdOfCall() {
        return IdOfCall;
    }

    private void setIdOfCall(Long idOfCall) {
        // For Hibernate only
        IdOfCall = idOfCall;
    }

    public Client getClient() {
        return client;
    }

    private void setClient(Client client) {
        // For Hibernate only
        this.client = client;
    }

    public Date getCallTime() {
        return callTime;
    }

    private void setCallTime(Date callTime) {
        // For Hibernate only
        this.callTime = callTime;
    }

    public String getReason() {
        return reason;
    }

    private void setReason(String reason) {
        // For Hibernate only
        this.reason = reason;
    }

    public Integer getCallType() {
        return callType;
    }

    private void setCallType(Integer callType) {
        // For Hibernate only
        this.callType = callType;
    }

    public Integer getState() {
        return state;
    }

    private void setState(Integer state) {
        // For Hibernate only
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Call)) {
            return false;
        }
        final Call call = (Call) o;
        if (!IdOfCall.equals(call.getIdOfCall())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return IdOfCall.hashCode();
    }

    @Override
    public String toString() {
        return "Call{" + "IdOfCall=" + IdOfCall + ", client=" + client + ", callTime=" + callTime + ", reason='"
                + reason + '\'' + ", callType=" + callType + ", state=" + state + '}';
    }
}
