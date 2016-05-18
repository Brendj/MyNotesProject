/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 11:23
 */

public class CompositeIdOfVisitReqResolutionHist implements Serializable {
    private Long idOfRecord; // Идентификатор резолюции по запросу на посещение
    private Long idOfRequest; // Идентификатор запроса на посещение
    private Long idOfOrgResol; // Идентификатор организации, в которой была вынесена резолюция

    public CompositeIdOfVisitReqResolutionHist() {
    }

    public CompositeIdOfVisitReqResolutionHist(Long idOfRecord, Long idOfRequest, Long idOfOrgResol) {
        this.idOfRecord = idOfRecord;
        this.idOfRequest = idOfRequest;
        this.idOfOrgResol = idOfOrgResol;
    }

    public Long getIdOfRecord() {
        return idOfRecord;
    }

    public void setIdOfRecord(Long idOfRecord) {
        this.idOfRecord = idOfRecord;
    }

    public Long getIdOfRequest() {
        return idOfRequest;
    }

    public void setIdOfRequest(Long idOfRequest) {
        this.idOfRequest = idOfRequest;
    }

    public Long getIdOfOrgResol() {
        return idOfOrgResol;
    }

    public void setIdOfOrgResol(Long idOfOrgResol) {
        this.idOfOrgResol = idOfOrgResol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeIdOfVisitReqResolutionHist that = (CompositeIdOfVisitReqResolutionHist) o;

        if (!idOfOrgResol.equals(that.idOfOrgResol)) {
            return false;
        }
        if (!idOfRecord.equals(that.idOfRecord)) {
            return false;
        }
        if (!idOfRequest.equals(that.idOfRequest)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = idOfRecord.hashCode();
        result = 31 * result + idOfRequest.hashCode();
        result = 31 * result + idOfOrgResol.hashCode();
        return result;
    }
}
