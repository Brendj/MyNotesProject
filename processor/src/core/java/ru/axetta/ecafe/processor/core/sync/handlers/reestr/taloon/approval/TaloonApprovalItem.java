/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TaloonCreatedTypeEnum;
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
    private Integer qty;
    private Long price;
    private TaloonCreatedTypeEnum createdType;
    private Long orgOwnerId;
    private Boolean deletedState;
    private String errorMessage;
    private Integer resCode;

    public static TaloonApprovalItem build(Node itemNode, Long orgOwner) {
        Long orgId = null;
        Date date = null;
        String name = null;
        Integer qty = null;
        Long price = null;
        Integer createdType = null;
        Boolean deletedState = false;

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

        String strQty = XMLUtils.getAttributeValue(itemNode, "Qty");
        if (StringUtils.isNotEmpty(strQty)) {
            try {
                qty = Integer.parseInt(strQty);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format Qty\n";
            }
        } else {
            errorMessageComposite += "Attribute Qty not found\n";
        }

        String strPrice = XMLUtils.getAttributeValue(itemNode, "Price");
        if (StringUtils.isNotEmpty(strQty)) {
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

        String strDeletedState = XMLUtils.getAttributeValue(itemNode, "D");
        if (StringUtils.isNotEmpty(strDeletedState)) {
            try {
                deletedState = (Integer.parseInt(strDeletedState) == 1);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException incorrect format DeletedState";
            }
        }
        return new TaloonApprovalItem(orgId, date, name, qty, price, TaloonCreatedTypeEnum.fromInteger(createdType), orgOwner, deletedState, errorMessageComposite);
    }

    private TaloonApprovalItem(Long orgId, Date date, String name, Integer qty, Long price, TaloonCreatedTypeEnum createdType,
            Long orgOwnerId, Boolean deletedState, String errorMessage) {
        this.setOrgId(orgId);
        this.setDate(date);
        this.setName(name);
        this.setQty(qty);
        this.setPrice(price);
        this.setCreatedType(createdType);
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

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
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
}
