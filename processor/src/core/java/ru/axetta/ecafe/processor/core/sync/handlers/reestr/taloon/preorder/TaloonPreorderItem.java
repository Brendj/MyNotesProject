/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.preorder;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TaloonCreatedTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonISPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: o.petrova
 * Date: 16.12.19
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class TaloonPreorderItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private String guid;
    private Long orgId;
    private Long orgIdCreated;
    private Date date;
    private Long complexId;
    private String complexName;
    private String goodsName;
    private String goodsGuid;
    private Integer soldQty;
    private Integer requestedQty;
    private Integer shippedQty;
    private Integer reservedQty;
    private Integer blockedQty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    private TaloonISPPStatesEnum isppState;
    private TaloonPPStatesEnum ppState;
    private Long taloonNumber;
    private Long orgOwnerId;
    private Boolean deletedState;
    private String errorMessage;
    private Integer resCode;
    private Long version;
    private String comments;
    private Long idOfDish;
    private Boolean byWebSupplier;

    public static TaloonPreorderItem build(Node itemNode, Long orgOwner) {
        String guid = null;
        Long orgId = null;
        Long orgIdCreated = null;
        Date date = null;
        Long complexId = null;
        String complexName = null;
        String goodsName = null;
        String goodsGuid = null;
        Integer soldQty = null;
        Integer requestedQty = null;
        Integer shippedQty = null;
        Integer reservedQty = null;
        Integer blockedQty = null;
        Long price = null;
        Integer createdType = null;
        Integer isppState = null;
        Integer ppState = null;
        Boolean deletedState = false;
        Long taloonNumber = null;
        Long version = null;
        StringBuilder errorMessage = new StringBuilder();
        String comments = null;
        Long idOfDish = null;
        Boolean byWebSupplier = false;

        guid = XMLUtils.getAttributeValue(itemNode, "Guid");
        if (StringUtils.isEmpty(guid)) {
            errorMessage.append( "Attribute Guid not found");
        }

        // Четыре обязательных поля (orgId, date, complexId, goodsGuid)- первичный ключ
        String strOrgId = XMLUtils.getAttributeValue(itemNode, "OrgId");
        if(StringUtils.isNotEmpty(strOrgId)){
            try {
                orgId =  Long.parseLong(strOrgId);
                Org o = DAOReadonlyService.getInstance().findOrg(orgId);
                if (o == null) {
                    errorMessage.append(String.format("Org with id=%s not found", orgId));
                } else {
                    if (!DAOReadonlyService.getInstance().isOrgFriendly(orgId, orgOwner)) {
                        errorMessage.append(String.format("Org id=%s is not friendly to Org id=%s", orgId, orgOwner));
                    }
                }
            } catch (NumberFormatException e){
                errorMessage.append("NumberFormatException OrgId not found");
            }
        } else {
            errorMessage.append("Attribute OrgId not found");
        }

        String strOrgIdCreated = XMLUtils.getAttributeValue(itemNode, "OrgIdCreated");
        if(StringUtils.isNotEmpty(strOrgIdCreated)){
            try {
                orgIdCreated =  Long.parseLong(strOrgIdCreated);
                Org o = DAOReadonlyService.getInstance().findOrg(orgIdCreated);
                if (o == null) {
                    errorMessage.append(String.format("OrgCreated with id=%s not found", orgIdCreated));
                } else {
                    if (!DAOReadonlyService.getInstance().isOrgFriendly(orgIdCreated, orgOwner)) {
                        errorMessage.append(String.format("OrgCreated id=%s is not friendly to Org id=%s", orgIdCreated, orgOwner));
                    }
                }
            } catch (NumberFormatException e){
                errorMessage.append("NumberFormatException OrgIdCreated is not parsed");
            }
        }

        String strDate = XMLUtils.getAttributeValue(itemNode,"Date");
        if(StringUtils.isNotEmpty(strDate)){
            try {
                date = CalendarUtils.parseDate(strDate);
            } catch (Exception e){
                errorMessage.append("Attribute Date not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute Date not found");
        }

        String strComplexId = XMLUtils.getAttributeValue(itemNode, "ComplexId");
        if(StringUtils.isNotEmpty(strComplexId)){
            try {
                complexId =  Long.parseLong(strComplexId);
            } catch (NumberFormatException e){
                errorMessage.append("Attribute ComplexId not found or incorrect");
            }
        }

        goodsGuid = XMLUtils.getAttributeValue(itemNode, "GoodsGuid");
        if (StringUtils.isEmpty(goodsGuid)) {
            errorMessage.append( "Attribute GoodsGuid not found");
        }

        complexName = XMLUtils.getAttributeValue(itemNode,"ComplexName");
        goodsName = XMLUtils.getAttributeValue(itemNode,"GoodsName");
        comments = XMLUtils.getAttributeValue(itemNode,"Comments");
        version = XMLUtils.getLongAttributeValue(itemNode, "V");

        if (isOldQtyFormat(itemNode)){
            Integer qty = readIntegerValue(itemNode, "Qty", errorMessage);
            if (qty != null) {
                soldQty = qty;
                requestedQty = qty;
                shippedQty = qty;
                reservedQty = qty;
                blockedQty = qty;
            }
        }
        else {
            soldQty = readIntegerValue(itemNode, "SoldedQtyISPP", errorMessage);
            requestedQty = readIntegerValue(itemNode, "RequestedQtyISPP", errorMessage);
            shippedQty = readIntegerValue(itemNode, "ShippedQtyPP", errorMessage);
            reservedQty = readIntegerValue(itemNode, "StornoInPlan", errorMessage);
            blockedQty = readIntegerValue(itemNode, "BlockInPlan", errorMessage);
        }

        String strPrice = XMLUtils.getAttributeValue(itemNode, "Price");
        if (StringUtils.isNotEmpty(strPrice)) {
            try {
                price = Long.parseLong(strPrice);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException incorrect format Price");
            }
        } else {
            errorMessage.append("Attribute Price not found");
        }

        String strCreatedType = XMLUtils.getAttributeValue(itemNode, "CreatedType");
        if (StringUtils.isNotEmpty(strCreatedType)) {
            try {
                createdType = Integer.parseInt(strCreatedType);
                if (!createdType.equals(TaloonCreatedTypeEnum.TALOON_CREATED_TYPE_AUTO.ordinal()) && !createdType.equals(TaloonCreatedTypeEnum.TALOON_CREATED_TYPE_MANUAL.ordinal())) {
                    errorMessage.append("Attribute CreatedType not valid\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException incorrect format CreatedType\n");
            }
        } else {
            errorMessage.append("Attribute CreatedType not found");
        }

        String strIsppState = XMLUtils.getAttributeValue(itemNode, "ISPP_State");
        if (StringUtils.isNotEmpty(strIsppState)) {
            try {
                isppState = Integer.parseInt(strIsppState);
                if (!isppState.equals(TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED.ordinal()) && !isppState.equals(TaloonISPPStatesEnum.TALOON_ISPP_STATE_CONFIRMED.ordinal())) {
                    errorMessage.append("Attribute ISPP_State not valid");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException incorrect format ISPP_State");
            }
        } else {
            isppState = TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED.ordinal();
        }

        String strPpState = XMLUtils.getAttributeValue(itemNode, "PP_State");
        if (StringUtils.isNotEmpty(strPpState)) {
            try {
                ppState = Integer.parseInt(strPpState);
                if (!ppState.equals(TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED.ordinal())
                        && !ppState.equals(TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED.ordinal())
                        && !ppState.equals(TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED.ordinal())) {
                    errorMessage.append("Attribute PP_State not valid");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException incorrect format PP_State");
            }
        } else {
            ppState = TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED.ordinal();
        }

        String strWebSupplier = XMLUtils.getAttributeValue(itemNode, "ByWebSupplier");
        if (StringUtils.isNotEmpty(strWebSupplier)) {
            try {
                byWebSupplier = (Integer.parseInt(strWebSupplier) == 1);
            } catch (NumberFormatException e) {
                errorMessage.append( "NumberFormatException incorrect format ByWebSupplier");
            }
        }

        String strDishId = XMLUtils.getAttributeValue(itemNode, "DishId");
        if(StringUtils.isNotEmpty(strDishId)){
            try {
                idOfDish =  Long.parseLong(strDishId);
            } catch (NumberFormatException e){
                errorMessage.append("NumberFormatException DishId is not parsed");
            }
        }

        String strDeletedState = XMLUtils.getAttributeValue(itemNode, "D");
        if (StringUtils.isNotEmpty(strDeletedState)) {
            try {
                deletedState = (Integer.parseInt(strDeletedState) == 1);
            } catch (NumberFormatException e) {
                errorMessage.append( "NumberFormatException incorrect format DeletedState");
            }
        }

        String strTaloonNumber = XMLUtils.getAttributeValue(itemNode, "TaloonNumber");
        if (StringUtils.isNotEmpty(strTaloonNumber)) {
            try {
                taloonNumber = Long.parseLong(strTaloonNumber);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException incorrect format TaloonNumber");
            }
        }

        return new TaloonPreorderItem(guid, orgId, orgIdCreated, date, complexId, complexName, goodsName,goodsGuid, soldQty, requestedQty, shippedQty,
                reservedQty, blockedQty, price,
                TaloonCreatedTypeEnum.fromInteger(createdType), TaloonISPPStatesEnum.fromInteger(isppState), TaloonPPStatesEnum.fromInteger(ppState),
                taloonNumber, orgOwner, idOfDish, byWebSupplier, deletedState, version, errorMessage.toString(), comments);
    }

    private static boolean isOldQtyFormat(Node itemNode) {
        String strValue = XMLUtils.getAttributeValue(itemNode, "Qty");
        return StringUtils.isNotEmpty(strValue);
    }

    private static Integer readIntegerValue(Node itemNode, String nameAttr, StringBuilder errorMessage) {
        String strValue = XMLUtils.getAttributeValue(itemNode, nameAttr);
        if (StringUtils.isNotEmpty(strValue)) {
            try {
                return Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                errorMessage.append(String.format("NumberFormatException incorrect format %s", nameAttr));
            }
        } else {
            errorMessage.append(String.format("Attribute %s not found", nameAttr));
        }
        return null;
    }


    private TaloonPreorderItem(String guid, Long orgId, Long orgIdCreated, Date date, Long complexId, String complexName, String goodsName, String goodsGuid, Integer soldQty,
            Integer requestedQty, Integer shippedQty, Integer reservedQty, Integer blockedQty,
            Long price, TaloonCreatedTypeEnum createdType, TaloonISPPStatesEnum isppState, TaloonPPStatesEnum ppState,
            Long taloonNumber, Long orgOwnerId, Long idOfDish, Boolean byWebSupplier, Boolean deletedState, Long version, String errorMessage, String comments) {
        this.setGuid(guid);
        this.setOrgId(orgId);
        this.setOrgIdCreated(orgIdCreated);
        this.setDate(date);
        this.setComplexId(complexId);
        this.setComplexName(complexName);
        this.setSoldQty(soldQty);
        this.setRequestedQty(requestedQty);
        this.setShippedQty(shippedQty);
        this.setReservedQty(reservedQty);
        this.setBlockedQty(blockedQty);
        this.setPrice(price);
        this.setCreatedType(createdType);
        this.setIsppState(isppState);
        this.setPpState(ppState);
        this.setTaloonNumber(taloonNumber);
        this.setOrgOwnerId(orgOwnerId);
        this.setDeletedState(deletedState);
        this.setGoodsName(goodsName);
        this.setGoodsGuid(goodsGuid);
        this.setIdOfDish(idOfDish);
        this.setByWebSupplier(byWebSupplier);
        this.version = version;
        this.setErrorMessage(errorMessage);
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
        this.setComments(comments);
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

    public Long getOrgOwnerId() {
        return orgOwnerId;
    }

    public void setOrgOwnerId(Long orgOwnerId) {
        this.orgOwnerId = orgOwnerId;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
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

    public void setPpState(TaloonPPStatesEnum ppState) {
        this.ppState = ppState;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getSoldQty() {
        return soldQty;
    }

    public void setSoldQty(Integer soldQty) {
        this.soldQty = soldQty;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getIdOfDish() {
        return idOfDish;
    }

    public void setIdOfDish(Long idOfDish) {
        this.idOfDish = idOfDish;
    }

    public Boolean getByWebSupplier() {
        return byWebSupplier;
    }

    public void setByWebSupplier(Boolean byWebSupplier) {
        this.byWebSupplier = byWebSupplier;
    }
}
