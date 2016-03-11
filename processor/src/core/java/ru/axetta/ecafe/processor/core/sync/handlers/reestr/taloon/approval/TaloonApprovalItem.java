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

        String errorMessageComposite = "";
        //Три обязательных поля (orgId, date, name)- первичный ключ
        String strOrgId = XMLUtils.getAttributeValue(itemNode, "OrgId");
        if(StringUtils.isNotEmpty(strOrgId)){
            try {
                orgId =  Long.parseLong(strOrgId);
                Org o = DAOService.getInstance().getOrg(orgId);
                if (o == null) {
                    errorMessageComposite += String.format("Org with id=%s not found\n", orgId);
                } else {
                    DAOService daoService = DAOService.getInstance();
                    Set<Org> fOrgs = daoService.getFriendlyOrgs(orgOwner);
                    if (!daoService.getInstance().isOrgFriendly(orgId, orgOwner)) {
                        errorMessageComposite += String.format("Org id=%s is not friendly to Org id=%s", orgId, orgOwner);
                    }
                }
            } catch (NumberFormatException e){
                errorMessageComposite += "NumberFormatException OrgId not found\n";
            }
        } else {
            errorMessageComposite += "Attribute OrgId not found\n";
        }
        String strDate = XMLUtils.getAttributeValue(itemNode,"Date");
        if(StringUtils.isNotEmpty(strDate)){
            try {
                date = CalendarUtils.parseDate(strDate);
            } catch (Exception e){
                errorMessageComposite += "Attribute Date not found or incorrect\n";
            }
        } else {
            errorMessageComposite += "Attribute Date not found\n";
        }
        name = XMLUtils.getAttributeValue(itemNode, "Name");
        if (StringUtils.isEmpty(name)) {
            errorMessageComposite += "Attribute Name not found\n";
        }

        String strSoldedQty = XMLUtils.getAttributeValue(itemNode, "SoldedQty");
        if (StringUtils.isNotEmpty(strSoldedQty)) {
            try {
                soldedQty = Integer.parseInt(strSoldedQty);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format SoldedQty\n";
            }
        } else {
            errorMessageComposite += "Attribute SoldedQty not found\n";
        }

        String strRequestedQty = XMLUtils.getAttributeValue(itemNode, "RequestedQty");
        if (StringUtils.isNotEmpty(strRequestedQty)) {
            try {
                requestedQty = Integer.parseInt(strRequestedQty);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format RequestedQty\n";
            }
        } else {
            errorMessageComposite += "Attribute RequestedQty not found\n";
        }

        String strShippedQty = XMLUtils.getAttributeValue(itemNode, "ShippedQty");
        if (StringUtils.isNotEmpty(strShippedQty)) {
            try {
                shippedQty = Integer.parseInt(strShippedQty);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format ShippedQty\n";
            }
        } else {
            errorMessageComposite += "Attribute ShippedQty not found\n";
        }

        String strPrice = XMLUtils.getAttributeValue(itemNode, "Price");
        if (StringUtils.isNotEmpty(strPrice)) {
            try {
                price = Long.parseLong(strPrice);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format Price\n";
            }
        } else {
            errorMessageComposite += "Attribute Price not found\n";
        }

        String strCreatedType = XMLUtils.getAttributeValue(itemNode, "CreatedType");
        if (StringUtils.isNotEmpty(strCreatedType)) {
            try {
                createdType = Integer.parseInt(strCreatedType);
                if (!createdType.equals(TaloonCreatedTypeEnum.TALOON_CREATED_TYPE_AUTO.ordinal()) && !createdType.equals(TaloonCreatedTypeEnum.TALOON_CREATED_TYPE_MANUAL.ordinal())) {
                    errorMessageComposite += "Attribute CreatedType not valid\n";
                }
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format CreatedType\n";
            }
        } else {
            errorMessageComposite += "Attribute CreatedType not found\n";
        }

        String strIsppState = XMLUtils.getAttributeValue(itemNode, "ISPP_State");
        if (StringUtils.isNotEmpty(strIsppState)) {
            try {
                isppState = Integer.parseInt(strIsppState);
                if (!isppState.equals(TaloonISPPStatesEnum.TALOON_ISPP_STATE_NOT_SELECTED.ordinal()) && !isppState.equals(TaloonISPPStatesEnum.TALOON_ISPP_STATE_CONFIRMED.ordinal())) {
                    errorMessageComposite += "Attribute ISPP_State not valid\n";
                }
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format ISPP_State\n";
            }
        } else {
            errorMessageComposite += "Attribute ISPP_State not found\n";
        }

        String strPpState = XMLUtils.getAttributeValue(itemNode, "PP_State");
        if (StringUtils.isNotEmpty(strPpState)) {
            try {
                ppState = Integer.parseInt(strPpState);
                if (!ppState.equals(TaloonPPStatesEnum.TALOON_PP_STATE_NOT_SELECTED.ordinal())
                        && !ppState.equals(TaloonPPStatesEnum.TALOON_PP_STATE_CONFIRMED.ordinal())
                        && !ppState.equals(TaloonPPStatesEnum.TALOON_PP_STATE_CANCELED.ordinal())) {
                    errorMessageComposite += "Attribute PP_State not valid\n";
                }
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format PP_State\n";
            }
        } else {
            errorMessageComposite += "Attribute PP_State not found\n";
        }

        String strDeletedState = XMLUtils.getAttributeValue(itemNode, "D");
        if (StringUtils.isNotEmpty(strDeletedState)) {
            try {
                deletedState = (Integer.parseInt(strDeletedState) == 1);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format DeletedState";
            }
        }

        String strTaloonNumber = XMLUtils.getAttributeValue(itemNode, "TaloonNumber");
        if (StringUtils.isNotEmpty(strTaloonNumber)) {
            try {
                taloonNumber = Long.parseLong(strTaloonNumber);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format TaloonNumber";
            }
        }

        return new TaloonApprovalItem(orgId, date, name, soldedQty, requestedQty, shippedQty, price,
                TaloonCreatedTypeEnum.fromInteger(createdType), TaloonISPPStatesEnum.fromInteger(isppState), TaloonPPStatesEnum.fromInteger(ppState),
                taloonNumber, orgOwner, deletedState, errorMessageComposite);
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
