/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 10:27
 */

public class OutcomeMigrationRequestsHistoryItem {
    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private Long idOfRecord;
    private Long idOfRequest;
    private Long idOfOrgResol;
    private Long idOfOrgRegistry;
    private Integer resolution;
    private Date resolutionDateTime;
    private String resolutionCause;
    private Long idOfClientResol;
    private String contactInfo;
    private String errorMessage;
    private Integer resCode;

    public static OutcomeMigrationRequestsHistoryItem build(Node itemNode, Long idOfOrgRegistry) {
        Long idOfRecord = null;
        Long idOfRequest = null;
        Long idOfOrgResol = null;
        Integer resolution = null;
        Date resolutionDateTime = null;
        String resolutionCause = null;
        Long idOfClientResol = null;
        String contactInfo = null;
        EMSetter emSetter = new EMSetter("");

        idOfRecord = getLongValue(itemNode, "IdOfRecord", emSetter, true);

        idOfRequest = getLongValue(itemNode, "IdOfRequest", emSetter, true);

        idOfOrgResol = getLongValue(itemNode, "IdOfOrgIssuer", emSetter, true);
        if(idOfOrgResol != null) {
            Org orgResol = DAOReadonlyService.getInstance().findOrg(idOfOrgResol);
            if (orgResol == null) {
                emSetter.setCompositeErrorMessage(String.format("OrgIssuer with id=%s not found", idOfOrgResol));
            }
        }

        try {
            resolution = getLongValue(itemNode, "Resolution", emSetter, true).intValue();
        } catch (Exception e){
            emSetter.setCompositeErrorMessage("Attribute Resolution not found or incorrect");
        }

        String date = XMLUtils.getAttributeValue(itemNode, "ResolutionDateTime");
        if(StringUtils.isNotEmpty(date)){
            try {
                resolutionDateTime = CalendarUtils.parseDateWithDayTime(date);
            } catch (Exception e){
                emSetter.setCompositeErrorMessage("Attribute ResolutionDateTime not found or incorrect");
            }
        } else {
            emSetter.setCompositeErrorMessage("Attribute VisitStartDate not found");
        }

        resolutionCause = XMLUtils.getAttributeValue(itemNode, "ResolutionCause");
        if(StringUtils.isEmpty(resolutionCause)){
            emSetter.setCompositeErrorMessage("Attribute ResolutionCause not found");
        }
        if(resolutionCause.length() > 300){
            emSetter.setCompositeErrorMessage("Attribute ResolutionCause is longer than 300 characters");
        }

        idOfClientResol = getLongValue(itemNode, "IdOfClientResol", emSetter, true);
        if(idOfClientResol != null) {
            Client client = DAOService.getInstance().findClientById(idOfClientResol);
            if (client == null) {
                emSetter.setCompositeErrorMessage(String.format("Client with id=%s not found", idOfClientResol));
            }
        }

        contactInfo = XMLUtils.getAttributeValue(itemNode, "ContactInfo");
        if(contactInfo != null){
            if(contactInfo.length() > 100){
                emSetter.setCompositeErrorMessage("Attribute ContactInfo is longer than 100 characters");
            }
        }

        return new OutcomeMigrationRequestsHistoryItem(idOfRecord, idOfRequest, idOfOrgResol, idOfOrgRegistry, resolution,
                resolutionDateTime, resolutionCause, idOfClientResol, contactInfo, emSetter.getStr());
    }

    private static Long getLongValue(Node itemNode, String nodeName, ISetErrorMessage www, boolean checkExists) {
        String str = XMLUtils.getAttributeValue(itemNode, nodeName);
        Long result = null;
        if(StringUtils.isNotEmpty(str)){
            try {
                result =  Long.parseLong(str);
            } catch (NumberFormatException e){
                www.setCompositeErrorMessage(String.format("NumberFormatException %s is incorrect", nodeName));
            }
        } else {
            if (checkExists) {
                www.setCompositeErrorMessage(String.format("Attribute %s not found", nodeName));
            }
        }
        return result;
    }

    private static class EMSetter implements ISetErrorMessage {
        private String str;
        public EMSetter(String str) {
            this.setStr(str);
        }
        @Override
        public void setCompositeErrorMessage(String message) {
            setStr(str + message + ", ");
        }

        public String getStr() {
            if(str.length() > 2){
                return str.substring(0, str.length() - 2);
            } else {
                return str;
            }
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    private interface ISetErrorMessage {
        public void setCompositeErrorMessage(String message);
    }

    public OutcomeMigrationRequestsHistoryItem() {
    }

    public OutcomeMigrationRequestsHistoryItem(Long idOfRecord, Long idOfRequest, Long idOfOrgResol, Long idOfOrgRegistry,
            Integer resolution, Date resolutionDateTime, String resolutionCause, Long idOfClientResol,
            String contactInfo, String errorMessage) {
        this.idOfRecord = idOfRecord;
        this.idOfRequest = idOfRequest;
        this.idOfOrgResol = idOfOrgResol;
        this.idOfOrgRegistry = idOfOrgRegistry;
        this.resolution = resolution;
        this.resolutionDateTime = resolutionDateTime;
        this.resolutionCause = resolutionCause;
        this.idOfClientResol = idOfClientResol;
        this.contactInfo = contactInfo;
        this.errorMessage = errorMessage;
        if(errorMessage == null || errorMessage.isEmpty() ){
            this.resCode = ERROR_CODE_ALL_OK;
        } else {
            this.resCode = ERROR_CODE_NOT_VALID_ATTRIBUTE;
        }
    }

    public Long getIdOfRecord() {
        return idOfRecord;
    }

    public void setIdOfRecord(Long idOfRecord) {
        this.idOfRecord = idOfRecord;
    }

    public Long getIdOfRequest() {
        return idOfRequest;
    }

    public void setIdOfRequest(Long idOfRequest) {
        this.idOfRequest = idOfRequest;
    }

    public Long getIdOfOrgResol() {
        return idOfOrgResol;
    }

    public void setIdOfOrgResol(Long idOfOrgResol) {
        this.idOfOrgResol = idOfOrgResol;
    }

    public Long getIdOfOrgRegistry() {
        return idOfOrgRegistry;
    }

    public void setIdOfOrgRegistry(Long idOfOrgRegistry) {
        this.idOfOrgRegistry = idOfOrgRegistry;
    }

    public Integer getResolution() {
        return resolution;
    }

    public void setResolution(Integer resolution) {
        this.resolution = resolution;
    }

    public Date getResolutionDateTime() {
        return resolutionDateTime;
    }

    public void setResolutionDateTime(Date resolutionDateTime) {
        this.resolutionDateTime = resolutionDateTime;
    }

    public String getResolutionCause() {
        return resolutionCause;
    }

    public void setResolutionCause(String resolutionCause) {
        this.resolutionCause = resolutionCause;
    }

    public Long getIdOfClientResol() {
        return idOfClientResol;
    }

    public void setIdOfClientResol(Long idOfClientResol) {
        this.idOfClientResol = idOfClientResol;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
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
}
