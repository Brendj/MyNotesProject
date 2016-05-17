/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TaloonCreatedTypeEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonISPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.TaloonPPStatesEnum;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 15.02.16
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class TaloonApprovalItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private Long orgId;
    private Date date;
    private String name;
    private Integer soldedQty;
    private Integer requestedQty;
    private Integer shippedQty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    private TaloonISPPStatesEnum isppState;
    private TaloonPPStatesEnum ppState;
    private Long taloonNumber;
    private Long orgOwnerId;
    private Boolean deletedState;
    private String errorMessage;
    private Integer resCode;

    public static TaloonApprovalItem build(Node itemNode, Long orgOwner) {
        Long orgId = null;
        Date date = null;
        String name = null;
        Integer soldedQty = null;
        Integer requestedQty = null;
        Integer shippedQty = null;
        Long price = null;
        Integer createdType = null;
        Integer isppState = null;
        Integer ppState = null;
        Boolean deletedState = false;
        Long taloonNumber = null;
        StringBuilder errorMessage = new StringBuilder();

        //Три обязательных поля (orgId, date, name)- первичный ключ
        String strOrgId = XMLUtils.getAttributeValue(itemNode, "OrgId");
        if(StringUtils.isNotEmpty(strOrgId)){
            try {
                orgId =  Long.parseLong(strOrgId);
                Org o = DAOService.getInstance().getOrg(orgId);
                if (o == null) {
                    errorMessage.append(String.format("Org with id=%s not found", orgId));
                } else {
                    DAOService daoService = DAOService.getInstance();
                    Set<Org> fOrgs = daoService.getFriendlyOrgs(orgOwner);
                    if (!daoService.getInstance().isOrgFriendly(orgId, orgOwner)) {
                        errorMessage.append(String.format("Org id=%s is not friendly to Org id=%s", orgId, orgOwner));
                    }
                }
            } catch (NumberFormatException e){
                errorMessage.append("NumberFormatException OrgId not found");
            }
        } else {
            errorMessage.append("Attribute OrgId not found");
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
        name = XMLUtils.getAttributeValue(itemNode, "Name");
        if (StringUtils.isEmpty(name)) {
            errorMessage.append( "Attribute Name not found");
        }

        if (isOldQtyFormat(itemNode)){
            Integer qty = readIntegerValue(itemNode, "Qty", errorMessage);
            if (qty != null) {
                soldedQty = qty;
                requestedQty = qty;
                shippedQty = qty;
            }
        }
        else {
            soldedQty = readIntegerValue(itemNode, "SoldedQty", errorMessage);
            requestedQty = readIntegerValue(itemNode, "RequestedQty", errorMessage);
            shippedQty = readIntegerValue(itemNode, "ShippedQty", errorMessage);
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

        return new TaloonApprovalItem(orgId, date, name, soldedQty, requestedQty, shippedQty, price,
                TaloonCreatedTypeEnum.fromInteger(createdType), TaloonISPPStatesEnum.fromInteger(isppState), TaloonPPStatesEnum.fromInteger(ppState),
                taloonNumber, orgOwner, deletedState, errorMessage.toString());
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


    private TaloonApprovalItem(Long orgId, Date date, String name, Integer soldedQty, Integer requestedQty, Integer shippedQty,
            Long price, TaloonCreatedTypeEnum createdType, TaloonISPPStatesEnum isppState, TaloonPPStatesEnum ppState,
            Long taloonNumber, Long orgOwnerId, Boolean deletedState, String errorMessage) {
        this.setOrgId(orgId);
        this.setDate(date);
        this.setName(name);
        this.setSoldedQty(soldedQty);
        this.setRequestedQty(requestedQty);
        this.setShippedQty(shippedQty);
        this.setPrice(price);
        this.setCreatedType(createdType);
        this.setIsppState(isppState);
        this.setPpState(ppState);
        this.setTaloonNumber(taloonNumber);
        this.orgOwnerId = orgOwnerId;
        this.deletedState = deletedState;
        this.setErrorMessage(errorMessage);
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
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

    public void setPpState(TaloonPPStatesEnum ppState) {
        this.ppState = ppState;
    }
}
