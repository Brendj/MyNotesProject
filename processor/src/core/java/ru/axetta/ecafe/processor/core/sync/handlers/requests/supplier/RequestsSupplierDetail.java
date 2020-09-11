/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.requests.supplier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import java.util.Objects;

public class RequestsSupplierDetail {

    private static final Logger logger = LoggerFactory.getLogger(RequestsSupplierDetail.class);

    private String guid;
    private String guidRequest;
    private Long idOfComplex;
    private Long idOfDish;
    private RequestsSupplierDetailTypeEnum fType;
    private Integer totalCount;
    private Integer dProbeCount;
    private Integer tempClientsCount;
    private Boolean deletedState;
    private Long version;
    private String errorMessage;

    private GoodRequestPosition goodRequestPosition;

    public RequestsSupplierDetail() {
    }

    public void build(Node detailNode, Long orgId, GoodRequest goodRequest) {
        String guid = null;
        String guidRequest = null;
        Long idOfComplex = null;
        Long idOfDish = null;
        Integer fType = null;
        Integer totalCount = null;
        Integer dProbeCount = null;
        Integer tempClientsCount = null;
        Boolean deletedState = false;
        Long version = null;

        StringBuilder errorMessage = new StringBuilder();

        guid = XMLUtils.getAttributeValue(detailNode, "Guid");
        if (StringUtils.isEmpty(guid)) {
            errorMessage.append("Attribute Guid was not found");
        }

        guidRequest = XMLUtils.getAttributeValue(detailNode, "GuidRequest");
        if (StringUtils.isEmpty(guidRequest)) {
            errorMessage.append("Attribute GuidRequest was not found");
        }

        String strIdOfComplex = XMLUtils.getAttributeValue(detailNode, "IdOfComplex");
        if (StringUtils.isNotEmpty(strIdOfComplex)) {
            try {
                idOfComplex = Long.parseLong(strIdOfComplex);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute IdOfComplex");
            }
        }

        String strIdOfDish = XMLUtils.getAttributeValue(detailNode, "IdOfDish");
        if (StringUtils.isNotEmpty(strIdOfDish)) {
            try {
                idOfDish = Long.parseLong(strIdOfDish);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute IdOfDish");
            }
        }

        String strFType = XMLUtils.getAttributeValue(detailNode, "FType");
        if (StringUtils.isNotEmpty(strFType)) {
            try {
                fType = Integer.parseInt(strFType);
                if (!RequestsSupplierDetailTypeEnum.map.containsKey(fType)) {
                    errorMessage.append("Attribute FType is not valid");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute FType");
            }
        }

        String strTotalCount = XMLUtils.getAttributeValue(detailNode, "TotalCount");
        if (StringUtils.isNotEmpty(strTotalCount)) {
            try {
                totalCount = Integer.parseInt(strTotalCount);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute TotalCount");
            }
        } else {
            errorMessage.append("Attribute TotalCount was not found");
        }

        String strDProbeCount = XMLUtils.getAttributeValue(detailNode, "DProbeCount");
        if (StringUtils.isNotEmpty(strDProbeCount)) {
            try {
                dProbeCount = Integer.parseInt(strDProbeCount);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute DProbeCount");
            }
        } else {
            errorMessage.append("Attribute DProbeCount was not found");
        }

        String strTempClientsCount = XMLUtils.getAttributeValue(detailNode, "TempClientsCount");
        if (StringUtils.isNotEmpty(strTempClientsCount)) {
            try {
                tempClientsCount = Integer.parseInt(strTempClientsCount);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute TempClientsCount");
            }
        } else {
            errorMessage.append("Attribute TempClientsCount was not found");
        }

        String strDeletedState = XMLUtils.getAttributeValue(detailNode, "D");
        if (StringUtils.isNotEmpty(strDeletedState)) {
            try {
                deletedState = (Integer.parseInt(strDeletedState) == 1);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException: incorrect format of the attribute DeletedState");
            }
        }

        version = XMLUtils.getLongAttributeValue(detailNode, "V");

        DistributedObject distributedObject = null;
        try {
            distributedObject = GoodRequestPosition.class.newInstance();
            distributedObject.setIdOfSyncOrg(orgId);
            distributedObject = distributedObject.build(detailNode);
        } catch (Exception e) {
            if (distributedObject != null) {
                if (!(e instanceof DistributedObjectException)) {
                    distributedObject.setDistributedObjectException(new DistributedObjectException("Internal Error"));
                    logger.error(distributedObject.toString(), e);
                }
            }
        }

        //GoodRequestPosition goodRequestPosition = (GoodRequestPosition) distributedObject;

        this.setProperties(guid, guidRequest, idOfComplex, idOfDish, RequestsSupplierDetailTypeEnum.fromInteger(fType),
                totalCount, dProbeCount, tempClientsCount, deletedState, version, errorMessage.toString(),
                (GoodRequestPosition) distributedObject);
    }

    private void setProperties(String guid, String guidRequest, Long idOfComplex, Long idOfDish,
            RequestsSupplierDetailTypeEnum fType, Integer totalCount, Integer dProbeCount, Integer tempClientsCount,
            Boolean deletedState, Long version, String errorMessage, GoodRequestPosition goodRequestPosition) {
        this.setGuid(guid);
        this.setGuidRequest(guidRequest);
        this.setIdOfComplex(idOfComplex);
        this.setIdOfDish(idOfDish);
        this.setfType(fType);
        this.setTotalCount(totalCount);
        this.setdProbeCount(dProbeCount);
        this.setTempClientsCount(tempClientsCount);
        this.setDeletedState(deletedState);
        this.setVersion(version);
        this.setErrorMessage(errorMessage);
        this.goodRequestPosition = goodRequestPosition;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGuidRequest() {
        return guidRequest;
    }

    public void setGuidRequest(String guidRequest) {
        this.guidRequest = guidRequest;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public GoodRequestPosition getGoodRequestPosition() {
        return goodRequestPosition;
    }

    public void setGoodRequestPosition(GoodRequestPosition goodRequestPosition) {
        this.goodRequestPosition = goodRequestPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestsSupplierDetail that = (RequestsSupplierDetail) o;
        return guid.equals(that.guid) && guidRequest.equals(that.guidRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, guidRequest);
    }
}

