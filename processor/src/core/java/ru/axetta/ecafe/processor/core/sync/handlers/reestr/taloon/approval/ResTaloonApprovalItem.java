/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.persistence.TaloonApproval;
import ru.axetta.ecafe.processor.core.persistence.TaloonCreatedTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonISPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 13:42
 * To change this template use File | Settings | File Templates.
 */
public class ResTaloonApprovalItem {

    private Long orgId;
    private Date date;
    private String name;
    private String goodsName;
    private String goodsGuid;
    private Integer soldedQty;
    private Integer requestedQty;
    private Integer shippedQty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    private TaloonISPPStatesEnum isppState;
    private TaloonPPStatesEnum ppState;
    private Long version;
    private Boolean deletedState;
    private Integer resultCode;
    private String errorMessage;
    private Long taloonNumber;

    public ResTaloonApprovalItem() {

    }

    public ResTaloonApprovalItem(TaloonApproval taloon) {
        this.orgId = taloon.getCompositeIdOfTaloonApproval().getIdOfOrg();
        this.date = taloon.getCompositeIdOfTaloonApproval().getTaloonDate();
        this.name = taloon.getCompositeIdOfTaloonApproval().getTaloonName();
        this.goodsGuid = taloon.getCompositeIdOfTaloonApproval().getGoodsGuid();
        this.goodsName = taloon.getGoodsName();
        this.soldedQty = taloon.getSoldedQty();
        this.setRequestedQty(taloon.getRequestedQty());
        this.setShippedQty(taloon.getShippedQty());
        this.price = taloon.getPrice();
        this.createdType = taloon.getCreatedType();
        this.isppState = taloon.getIsppState();
        this.ppState = taloon.getPpState();
        this.taloonNumber = taloon.getTaloonNumber();
        this.version = taloon.getVersion();
        this.deletedState = taloon.getDeletedState();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "OrgId", orgId);
        if (date != null) {
            XMLUtils.setAttributeIfNotNull(element, "Date", CalendarUtils.dateShortToStringFullYear(date));
        }
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        XMLUtils.setAttributeIfNotNull(element, "Res", resultCode);
        XMLUtils.setAttributeIfNotNull(element, "Name", name);
        XMLUtils.setAttributeIfNotNull(element, "SoldedQty", soldedQty);
        XMLUtils.setAttributeIfNotNull(element, "RequestedQty", requestedQty == null ? 0 : requestedQty);
        XMLUtils.setAttributeIfNotNull(element, "ShippedQty", shippedQty == null ? 0 : shippedQty);
        XMLUtils.setAttributeIfNotNull(element, "Price", price);
        XMLUtils.setAttributeIfNotNull(element, "TaloonNumber", taloonNumber);
        if (createdType != null) {
            XMLUtils.setAttributeIfNotNull(element, "CreatedType", createdType.ordinal());
        }
        if (this.goodsName != null) {
            XMLUtils.setAttributeIfNotNull(element,"GoodsName",this.goodsName);
        }
        if (this.goodsGuid != null) {
            XMLUtils.setAttributeIfNotNull(element,"GoodsGuid",this.goodsGuid);
        }
        XMLUtils.setAttributeIfNotNull(element, "ISPP_State", isppState == null ? TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED.ordinal() : isppState.ordinal());
        XMLUtils.setAttributeIfNotNull(element, "PP_State", ppState == null ? TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED.ordinal() : ppState.ordinal());
        if (resultCode != null && resultCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }
        // для поддежки старых версий
        XMLUtils.setAttributeIfNotNull(element, "Qty", soldedQty);
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSoldedQty() {
        return soldedQty;
    }

    public void setSoldedQty(Integer soldedQty) {
        this.soldedQty = soldedQty;
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
}
