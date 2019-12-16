/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder;

import ru.axetta.ecafe.processor.core.persistence.TaloonCreatedTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonISPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonPreorder;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 16.12.19
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
public class ResTaloonPreorderItem {

    private Long orgId;
    private Long orgIdCreated;
    private Date date;
    private Long complexId;
    private String complexName;
    private String goodsName;
    private String goodsGuid;
    private Integer soldQty;
    private Integer ordersCount;
    private Integer requestedQty;
    private Integer shippedQty;
    private Integer reservedQty;
    private Integer blockedQty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    private TaloonISPPStatesEnum isppState;
    private TaloonPPStatesEnum ppState;
    private Long version;
    private Boolean deletedState;
    private Integer resultCode;
    private String errorMessage;
    private Long taloonNumber;
    private String comments;

    public ResTaloonPreorderItem() {

    }

    public ResTaloonPreorderItem(TaloonPreorder taloon) {
        this.orgId = taloon.getIdOfOrg();
        this.orgIdCreated = taloon.getIdOfOrgCreated();
        this.date = taloon.getTaloonDate();
        this.complexId = taloon.getComplexId();
        this.complexName = taloon.getComplexName() == null ? "" : taloon.getComplexName();
        this.goodsGuid = taloon.getGoodsGuid();
        this.goodsName = taloon.getGoodsName() == null ? "" : taloon.getGoodsName();
        this.soldQty = taloon.getSoldQty();
        this.requestedQty = taloon.getRequestedQty() == null ? 0 : taloon.getRequestedQty();
        this.shippedQty = taloon.getShippedQty() == null ? 0 : taloon.getShippedQty();
        this.reservedQty = taloon.getReservedQty() == null ? 0 : taloon.getReservedQty();
        this.blockedQty = taloon.getBlockedQty() == null ? 0 : taloon.getBlockedQty();
        this.price = taloon.getPrice();
        this.createdType = taloon.getCreatedType();
        this.isppState = taloon.getIsppState() == null ? TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED : taloon.getIsppState();
        this.ppState = taloon.getPpState() == null ? TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED : taloon.getPpState();
        this.taloonNumber = taloon.getTaloonNumber();
        this.version = taloon.getVersion();
        this.deletedState = taloon.getDeletedState();
        this.goodsName = taloon.getGoodsName() == null ? "" : taloon.getGoodsName();
        this.comments = taloon.getComments() == null ? "" : taloon.getComments();
    }

    public ResTaloonPreorderItem(TaloonPreorder taloon, Integer ordersCount, Integer resCode) {
        this.orgId = taloon.getIdOfOrg();
        this.orgIdCreated = taloon.getIdOfOrgCreated();
        this.date = taloon.getTaloonDate();
        this.complexId = taloon.getComplexId();
        this.complexName = taloon.getComplexName();
        this.goodsGuid = taloon.getGoodsGuid();
        this.ordersCount = ordersCount;
        this.taloonNumber = taloon.getTaloonNumber();
        this.version = taloon.getVersion();
        this.deletedState = taloon.getDeletedState();
        this.resultCode = resCode;
        this.price = taloon.getPrice();
        this.comments = taloon.getComments();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "OrgId", orgId);
        XMLUtils.setAttributeIfNotNull(element, "OrgIdCreated", orgIdCreated);
        if (date != null) {
            XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.dateShortToStringFullYear(date));
        }
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);
        XMLUtils.setAttributeIfNotNull(element, "ComplexId", complexId);
        XMLUtils.setAttributeIfNotNull(element, "ComplexName", complexName);
        XMLUtils.setAttributeIfNotNull(element, "SoldedQtyISPP", soldQty);
        XMLUtils.setAttributeIfNotNull(element, "OrdersCount", ordersCount);
        XMLUtils.setAttributeIfNotNull(element, "RequestedQtyISPP", requestedQty);
        XMLUtils.setAttributeIfNotNull(element, "ShippedQtyPP", shippedQty);
        XMLUtils.setAttributeIfNotNull(element, "StornoInPlan", reservedQty);
        XMLUtils.setAttributeIfNotNull(element, "BlockInPlan", blockedQty);
        XMLUtils.setAttributeIfNotNull(element, "Price", price);
        //XMLUtils.setAttributeIfNotNull(element, "TaloonNumber", taloonNumber);
        if (createdType != null) {
            XMLUtils.setAttributeIfNotNull(element, "CreatedType", createdType.ordinal());
        }
        XMLUtils.setAttributeIfNotNull(element,"GoodsName",this.goodsName);
        XMLUtils.setAttributeIfNotNull(element,"GoodsGuid",this.goodsGuid == null ? "" : this.goodsGuid);
        if(isppState != null) XMLUtils.setAttributeIfNotNull(element, "ISPP_State", isppState.ordinal());
        if(ppState != null) XMLUtils.setAttributeIfNotNull(element, "PP_State", ppState.ordinal());
        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        XMLUtils.setAttributeIfNotNull(element,"Comments",this.comments);
        // для поддежки старых версий
        XMLUtils.setAttributeIfNotNull(element, "Qty", soldQty);
        return element;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsGuid() {
        return goodsGuid;
    }

    public void setGoodsGuid(String goodsGuid) {
        this.goodsGuid = goodsGuid;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getOrgIdCreated() {
        return orgIdCreated;
    }

    public void setOrgIdCreated(Long orgIdCreated) {
        this.orgIdCreated = orgIdCreated;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(Integer ordersCount) {
        this.ordersCount = ordersCount;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public TaloonCreatedTypeEnum getCreatedType() {
        return createdType;
    }

    public void setCreatedType(TaloonCreatedTypeEnum createdType) {
        this.createdType = createdType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Long getTaloonNumber() {
        return taloonNumber;
    }

    public void setTaloonNumber(Long taloonNumber) {
        this.taloonNumber = taloonNumber;
    }

    public Integer getRequestedQty() {
        return requestedQty;
    }

    public void setRequestedQty(Integer requestedQty) {
        this.requestedQty = requestedQty;
    }

    public Integer getShippedQty() {
        return shippedQty;
    }

    public void setShippedQty(Integer shippedQty) {
        this.shippedQty = shippedQty;
    }

    public TaloonISPPStatesEnum getIsppState() {
        return isppState;
    }

    public void setIsppState(TaloonISPPStatesEnum isppState) {
        this.isppState = isppState;
    }

    public TaloonPPStatesEnum getPpState() {
        return ppState;
    }

    public void setPpState(TaloonPPStatesEnum ppState) {
        this.ppState = ppState;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public String getComplexName() {
        return complexName;
    }

    public void setComplexName(String complexName) {
        this.complexName = complexName;
    }

    public Integer getSoldQty() {
        return soldQty;
    }

    public void setSoldQty(Integer soldQty) {
        this.soldQty = soldQty;
    }

    public Integer getReservedQty() {
        return reservedQty;
    }

    public void setReservedQty(Integer reservedQty) {
        this.reservedQty = reservedQty;
    }

    public Integer getBlockedQty() {
        return blockedQty;
    }

    public void setBlockedQty(Integer blockedQty) {
        this.blockedQty = blockedQty;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
