/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

public class ResRequestsSupplierDetail {
    private String guid;
    private Long idOfComplex;
    private Long idOfDish;
    private RequestsSupplierDetailTypeEnum fType;
    private Integer totalCount;
    private Integer dProbeCount;
    private Integer tempClientsCount;
    private Boolean deletedState;
    private Long version;
    private String data;

    public ResRequestsSupplierDetail() {
    }

    public ResRequestsSupplierDetail(GoodRequestPosition goodRequestPosition) {
        this.guid = goodRequestPosition.getGuid();
        this.idOfComplex = goodRequestPosition.getComplexId() == null ? null : goodRequestPosition.getComplexId().longValue();
        this.idOfDish = goodRequestPosition.getIdOfDish();
        this.fType = RequestsSupplierDetailTypeEnum.fromInteger(goodRequestPosition.getFeedingType());
        this.totalCount = goodRequestPosition.getTotalCount() == null ? 0 : goodRequestPosition.getTotalCount().intValue();
        this.dProbeCount = goodRequestPosition.getDailySampleCount() == null ? 0 :
                goodRequestPosition.getDailySampleCount().intValue();
        this.tempClientsCount = goodRequestPosition.getTempClientsCount() == null ? 0 :
                goodRequestPosition.getTempClientsCount().intValue();
        this.deletedState = goodRequestPosition.getDeletedState();
        this.version = goodRequestPosition.getGlobalVersion();
    }

    public ResRequestsSupplierDetail(GoodRequestPosition grp, String guid) {
        this.guid = guid;
        this.data = "TC="+ grp.getTotalCount() + ", DSC=" + grp.getDailySampleCount() + ", TCC=" + grp.getTempClientsCount();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        XMLUtils.setAttributeIfNotNull(element, "data", data);
        XMLUtils.setAttributeIfNotNull(element, "IdOfComplex", idOfComplex);
        XMLUtils.setAttributeIfNotNull(element, "IdOfDish", idOfDish);
        if (fType != null) {
            XMLUtils.setAttributeIfNotNull(element, "FType", fType.ordinal());
        }
        XMLUtils.setAttributeIfNotNull(element, "TotalCount", totalCount);
        XMLUtils.setAttributeIfNotNull(element, "DProbeCount", dProbeCount);
        XMLUtils.setAttributeIfNotNull(element, "TempClientsCount", tempClientsCount);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        return element;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public RequestsSupplierDetailTypeEnum getfType() {
        return fType;
    }

    public void setfType(RequestsSupplierDetailTypeEnum fType) {
        this.fType = fType;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getdProbeCount() {
        return dProbeCount;
    }

    public void setdProbeCount(Integer dProbeCount) {
        this.dProbeCount = dProbeCount;
    }

    public Integer getTempClientsCount() {
        return tempClientsCount;
    }

    public void setTempClientsCount(Integer tempClientsCount) {
        this.tempClientsCount = tempClientsCount;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResRequestsSupplierDetail that = (ResRequestsSupplierDetail) o;
        return guid.equals(that.guid) && fType == that.fType && Objects.equals(totalCount, that.totalCount) && Objects
                .equals(dProbeCount, that.dProbeCount) && Objects.equals(tempClientsCount, that.tempClientsCount)
                && Objects.equals(deletedState, that.deletedState) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, fType, totalCount, dProbeCount, tempClientsCount, deletedState, version);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
