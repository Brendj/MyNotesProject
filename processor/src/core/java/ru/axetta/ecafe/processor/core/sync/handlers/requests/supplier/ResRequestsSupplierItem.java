/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ResRequestsSupplierItem {

    private String guid;
    private String number;
    private Date doneDate;
    private Date dateOfGoodsRequest;
    private RequestsSupplierTypeEnum type;
    private Long orgId;
    private String staffGuid;
    private Boolean deletedState;
    private Long version;
    private List<ResRequestsSupplierDetail> resRequestsSupplierDetailList;

    private Integer resultCode;
    private String errorMessage;

    public ResRequestsSupplierItem() {
    }

    public ResRequestsSupplierItem(GoodRequest goodRequest) {
        this.guid = goodRequest.getGuid();
        this.number = goodRequest.getNumber();
        this.dateOfGoodsRequest = goodRequest.getDateOfGoodsRequest();
        this.doneDate = goodRequest.getDoneDate();
        this.type = RequestsSupplierTypeEnum.fromInteger(goodRequest.getRequestType());
        this.orgId = goodRequest.getOrgOwner();
        this.staffGuid = goodRequest.getGuidOfStaff();
        this.deletedState = goodRequest.getDeletedState();
        this.version = goodRequest.getGlobalVersion();
        if (goodRequest.getStaff() != null) {
            this.staffGuid = goodRequest.getStaff().getGuid();
        }
    }

    public ResRequestsSupplierItem(String guid, Long version) {
        this.guid = guid;
        this.version = version;
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        XMLUtils.setAttributeIfNotNull(element, "Number", number);
        if (dateOfGoodsRequest != null) {
            XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(dateOfGoodsRequest));
        }
        if (doneDate != null) {
            XMLUtils.setAttributeIfNotNull(element, "DoneDate", CalendarUtils.toStringFullDateTimeWithLocalTimeZone(doneDate));
        }
        if (type != null) {
            XMLUtils.setAttributeIfNotNull(element, "Type", type.ordinal());
        }
        XMLUtils.setAttributeIfNotNull(element, "OrgId", orgId);
        XMLUtils.setAttributeIfNotNull(element, "StaffGuid", staffGuid);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        XMLUtils.setAttributeIfNotNull(element, "Code", resultCode);

        if (resRequestsSupplierDetailList != null && resRequestsSupplierDetailList.size() > 0) {
            for (ResRequestsSupplierDetail detail : resRequestsSupplierDetailList) {
                element.appendChild(detail.toElement(document, "RDI"));
            }
        }
        return element;
    }

    public void addDetails(List<GoodRequestPosition> details) {
        if (details == null || details.isEmpty()) {
            return;
        }
        if (this.resRequestsSupplierDetailList == null) {
           this.resRequestsSupplierDetailList = new LinkedList<>();
        }
        for (GoodRequestPosition detail : details) {
            this.getResRequestsSupplierDetailList().add(new ResRequestsSupplierDetail(detail));
        }
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public RequestsSupplierTypeEnum getType() {
        return type;
    }

    public void setType(RequestsSupplierTypeEnum type) {
        this.type = type;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getStaffGuid() {
        return staffGuid;
    }

    public void setStaffGuid(String staffGuid) {
        this.staffGuid = staffGuid;
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

    public List<ResRequestsSupplierDetail> getResRequestsSupplierDetailList() {
        return resRequestsSupplierDetailList;
    }

    public void setResRequestsSupplierDetailList(List<ResRequestsSupplierDetail> resRequestsSupplierDetailList) {
        this.resRequestsSupplierDetailList = resRequestsSupplierDetailList;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getDateOfGoodsRequest() {
        return dateOfGoodsRequest;
    }

    public void setDateOfGoodsRequest(Date dateOfGoodsRequest) {
        this.dateOfGoodsRequest = dateOfGoodsRequest;
    }
}
