/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.help.request;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.persistence.HelpRequestStatusEnumType;
import ru.axetta.ecafe.processor.core.persistence.HelpRequestThemeEnumType;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HelpRequestItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private String guid;
    private Long orgId;
    private Date requestDate;
    private String number;
    private HelpRequestThemeEnumType theme;
    private String message;
    private String declarer;
    private String phone;
    private HelpRequestStatusEnumType requestState;
    private String errorMessage;
    private Integer resCode;
    private Long version;

    private HelpRequestItem(String guid, Long orgId, Date requestDate, String number, HelpRequestThemeEnumType theme,
            String message, String declarer, String phone, HelpRequestStatusEnumType requestState, String errorMessage) {
        this.guid = guid;
        this.orgId = orgId;
        this.requestDate = requestDate;
        this.number = number;
        this.theme = theme;
        this.message = message;
        this.declarer = declarer;
        this.phone = phone;
        this.requestState = requestState;
        this.errorMessage = errorMessage;
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    public static HelpRequestItem build(Node itemNode, Long orgOwner) {
        String guid = null;
        Long orgId = null;
        Date requestDate = null;
        String number = null;
        Integer theme = null;
        String message = null;
        String declarer = null;
        String phone = null;
        Integer requestState = null;
        StringBuilder errorMessage = new StringBuilder();

        guid = XMLUtils.getAttributeValue(itemNode, "Guid");
        if (null == guid || StringUtils.isEmpty(guid)) {
            errorMessage.append("Attribute Guid not found");
        }

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

        String requestDateString = XMLUtils.getAttributeValue(itemNode, "RequestDate");
        if (StringUtils.isNotEmpty(requestDateString)) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                requestDate = simpleDateFormat.parse(requestDateString);
            } catch (Exception e){
                errorMessage.append("Attribute RequestDate not found or incorrect");
            }
        } else {
            errorMessage.append("Attribute RequestDate not found");
        }

        String themeString = XMLUtils.getAttributeValue(itemNode, "Theme");
        if (StringUtils.isNotEmpty(themeString)) {
            try {
                HelpRequestThemeEnumType helpRequestThemeEnumType = HelpRequestThemeEnumType.fromString(themeString);
                if (null == helpRequestThemeEnumType) {
                    errorMessage.append(String.format("Theme with theme description=%s not found", themeString));
                } else {
                    theme = helpRequestThemeEnumType.ordinal();
                }
            } catch (NullPointerException e) {
                errorMessage.append("NullPointerException Theme not found");
            }
        } else {
            errorMessage.append("Attribute Theme not found");
        }

        number = XMLUtils.getAttributeValue(itemNode, "Number");
        if (null == number || StringUtils.isEmpty(number)) {
            errorMessage.append("Attribute Number not found");
        }

        message = XMLUtils.getAttributeValue(itemNode, "Message");
        declarer = XMLUtils.getAttributeValue(itemNode, "ContactFIO");

        phone = XMLUtils.getAttributeValue(itemNode, "ContactPhone");
        if (null == phone || StringUtils.isEmpty(phone)) {
            errorMessage.append("Attribute ContactPhone not found");
        }

        String requestStateString = XMLUtils.getAttributeValue(itemNode, "RequestState");
        if (StringUtils.isNotEmpty(requestStateString)) {
            try {
                requestState = Integer.parseInt(requestStateString);
            } catch (NullPointerException e) {
                errorMessage.append("NumberFormatException RequestState not found");
            }
        } else {
            errorMessage.append("Attribute RequestState not found");
        }

        if (null == DAOReadonlyService.getInstance().findHelpRequest(orgId, guid)) {
            if (DAOReadonlyService.getInstance().isHelpRequestNumberExists(number)) {
                errorMessage.append("Attribute RequestNumber incorrect (same number already exists)");
            }

            if (DAOReadonlyService.getInstance().isHelpRequestGuidExists(guid)) {
                errorMessage.append("Attribute Guid incorrect (same guid already exists)");
            }
        }

        return new HelpRequestItem(guid, orgId, requestDate, number, HelpRequestThemeEnumType.fromInteger(theme),
                message, declarer, phone, HelpRequestStatusEnumType.fromInteger(requestState), errorMessage.toString());
    }


    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public HelpRequestThemeEnumType getTheme() {
        return theme;
    }

    public void setTheme(HelpRequestThemeEnumType theme) {
        this.theme = theme;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeclarer() {
        return declarer;
    }

    public void setDeclarer(String declarer) {
        this.declarer = declarer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HelpRequestStatusEnumType getRequestState() {
        return requestState;
    }

    public void setRequestState(HelpRequestStatusEnumType requestState) {
        this.requestState = requestState;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
