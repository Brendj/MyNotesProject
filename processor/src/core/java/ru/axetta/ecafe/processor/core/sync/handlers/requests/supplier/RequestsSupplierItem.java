/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RequestsSupplierItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private static final Logger logger = LoggerFactory.getLogger(RequestsSupplierItem.class);

    private Long orgId;
    private String guid;
    private String number;
    private Date dateOfGoodsRequest;
    private Date doneDate;
    private RequestsSupplierTypeEnum type;
    private String staffGuid;
    private Boolean deletedState;
    private Long version;
    private List<RequestsSupplierDetail> requestsSupplierDetailList;

    private GoodRequest goodRequest;

    private String errorMessage;
    private Integer resCode;

    public RequestsSupplierItem() {
        requestsSupplierDetailList = new LinkedList<>();
    }

    public void build(Node itemNode, Long idOfOrgOwner) {
        Long orgId = null;
        String guid = null;
        String number = null;
        Date dateOfGoodsRequest = null;
        Date doneDate = null;
        Integer type = null;
        String staffGuid = null;
        Boolean deletedState = false;
        Long version = null;
        StringBuilder errorMessage = new StringBuilder();
        List<RequestsSupplierDetail> requestsSupplierDetailList = new LinkedList<>();

        String strOrgId = XMLUtils.getAttributeValue(itemNode, "OrgId");
        if (StringUtils.isNotEmpty(strOrgId)) {
            try {
                orgId = Long.parseLong(strOrgId);
                Org o = DAOReadonlyService.getInstance().findOrg(orgId);
                if (o == null) {
                    errorMessage.append(String.format("Org with id=%s was not found", orgId));
                } else if (!DAOReadonlyService.getInstance().isOrgFriendly(orgId, idOfOrgOwner)) {
                    errorMessage.append(String.format("Org id=%s is not friendly one to Org id=%s", orgId, idOfOrgOwner));
                }
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: OrgId was not found");
            }
        } else {
            errorMessage.append("Attribute OrgId was not found");
        }

        guid = XMLUtils.getAttributeValue(itemNode, "Guid");
        if (StringUtils.isEmpty(guid)) {
            errorMessage.append("Attribute Guid was not found");
        }

        number = XMLUtils.getAttributeValue(itemNode, "Number");
        if (StringUtils.isEmpty(number)) {
            errorMessage.append("Attribute Number was not found");
        }

        String strDateOfGoodsRequest = XMLUtils.getAttributeValue(itemNode, "Date");
        if (StringUtils.isNotEmpty(strDateOfGoodsRequest)) {
            try {
                dateOfGoodsRequest = CalendarUtils.parseDate(strDateOfGoodsRequest);
            } catch (Exception e) {
                errorMessage.append("Attribute Date was not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute Date was not found");
        }

        String strDoneDate = XMLUtils.getAttributeValue(itemNode, "DoneDate");
        if (StringUtils.isNotEmpty(strDoneDate)) {
            try {
                doneDate = CalendarUtils.parseDate(strDoneDate);
            } catch (Exception e) {
                errorMessage.append("Attribute DoneDate was not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute DoneDate was not found");
        }

        String strType = XMLUtils.getAttributeValue(itemNode, "Type");
        if (StringUtils.isNotEmpty(strType)) {
            try {
                type = Integer.parseInt(strType);
                if (!RequestsSupplierTypeEnum.map.containsKey(type)) {
                    errorMessage.append("Attribute Type is not valid");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute Type");
            }
        } else {
            errorMessage.append("Attribute Type was not found");
        }

        staffGuid = XMLUtils.getAttributeValue(itemNode, "StaffGuid");
        if (StringUtils.isEmpty(staffGuid)) {
            errorMessage.append("Attribute StaffGuid was not found");
        }

        String strDeletedState = XMLUtils.getAttributeValue(itemNode, "D");
        if (StringUtils.isNotEmpty(strDeletedState)) {
            try {
                deletedState = (Integer.parseInt(strDeletedState) == 1);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute DeletedState");
            }
        }

        version = XMLUtils.getLongAttributeValue(itemNode, "V");

        DistributedObject distributedObject = null;
        try {
            distributedObject = GoodRequest.class.newInstance();
            distributedObject.setIdOfSyncOrg(orgId);
            distributedObject = distributedObject.build(itemNode);
        } catch (Exception e) {
            if (distributedObject != null) {
                if (!(e instanceof DistributedObjectException)) {
                    distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
                    logger.error(distributedObject.toString(), e);
                }
            }
        }
        GoodRequest goodRequest = (GoodRequest) distributedObject;

        Node detailNode = itemNode.getFirstChild();
        while (detailNode != null) {
            if (Node.ELEMENT_NODE == detailNode.getNodeType() && detailNode.getNodeName().equals("RDI")) {

                RequestsSupplierDetail detail = new RequestsSupplierDetail();
                detail.build(detailNode, orgId, goodRequest);
                requestsSupplierDetailList.add(detail);
                errorMessage.append(detail.getErrorMessage());

            }
            detailNode = detailNode.getNextSibling();
        }

        this.setProperties(orgId, guid, number, dateOfGoodsRequest, doneDate, RequestsSupplierTypeEnum.fromInteger(type), staffGuid,
                deletedState, version, errorMessage.toString(), requestsSupplierDetailList, goodRequest);
    }

    private void setProperties(Long orgId, String guid, String number, Date dateOfGoodsRequest, Date doneDate, RequestsSupplierTypeEnum type,
            String staffGuid, Boolean deletedState, Long version, String errorMessage,
            List<RequestsSupplierDetail> requestsSupplierDetailList,
            GoodRequest goodRequest) {
        this.orgId = orgId;
        this.guid = guid;
        this.number = number;
        this.dateOfGoodsRequest = dateOfGoodsRequest;
        this.doneDate = doneDate;
        this.type = type;
        this.staffGuid = staffGuid;
        this.deletedState = deletedState;
        this.version = version;
        this.setErrorMessage(errorMessage);
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
        this.requestsSupplierDetailList = requestsSupplierDetailList;
        this.goodRequest = goodRequest;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public List<RequestsSupplierDetail> getRequestsSupplierDetailList() {
        return requestsSupplierDetailList;
    }

    public void setRequestsSupplierDetailList(List<RequestsSupplierDetail> requestsSupplierDetailList) {
        this.requestsSupplierDetailList = requestsSupplierDetailList;
    }

    public GoodRequest getGoodRequest() {
        return goodRequest;
    }

    public void setGoodRequest(GoodRequest goodRequest) {
        this.goodRequest = goodRequest;
    }

    public Date getDateOfGoodsRequest() {
        return dateOfGoodsRequest;
    }

    public void setDateOfGoodsRequest(Date dateOfGoodsRequest) {
        this.dateOfGoodsRequest = dateOfGoodsRequest;
    }
}
