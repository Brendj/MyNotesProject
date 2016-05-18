/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.migrants;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 11.05.16
 * Time: 10:27
 */

public class OutcomeMigrationRequestsItem {
    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private Long idOfRequest;
    private Long idOfOrgRegistry;
    private Long idOfClient;
    private Long idOfOrgVisit;
    private Date visitStartDate;
    private Date visitEndDate;
    private String errorMessage;
    private Integer resCode;

    public static OutcomeMigrationRequestsItem build(Node itemNode, Long idOfOrgRegistry) {
        Long idOfRequest = null;
        Long idOfClient = null;
        Long idOfOrgVisit = null;
        Date visitStartDate = null;
        Date visitEndDate = null;
        EMSetter emSetter = new EMSetter("");

        idOfRequest = getLongValue(itemNode, "IdOfRequest", emSetter, true);
        if(idOfRequest != null) {
            Org orgRequest = DAOService.getInstance().getOrg(idOfOrgRegistry);
            if (orgRequest == null) {
                emSetter.setCompositeErrorMessage(String.format("OrgRegistry with id=%s not found", idOfOrgRegistry));
            }
        }

        idOfClient = getLongValue(itemNode, "IdOfClient", emSetter, true);
        if(idOfClient != null) {
            Client client = DAOService.getInstance().findClientById(idOfClient);
            if (client == null) {
                emSetter.setCompositeErrorMessage(String.format("Client with id=%s not found", idOfClient));
            }
        }

        idOfOrgVisit = getLongValue(itemNode, "IdOfOrgVisit", emSetter, true);
        if(idOfOrgVisit != null) {
            Org orgVisit = DAOService.getInstance().getOrg(idOfOrgVisit);
            if (orgVisit == null) {
                emSetter.setCompositeErrorMessage(String.format("OrgVisit with id=%s not found", idOfOrgVisit));
            }
        }

        String startDate = XMLUtils.getAttributeValue(itemNode, "VisitStartDate");
        if(StringUtils.isNotEmpty(startDate)){
            try {
                visitStartDate = CalendarUtils.parseDateWithDayTime(startDate);
            } catch (Exception e){
                emSetter.setCompositeErrorMessage("Attribute VisitStartDate not found or incorrect");
            }
        } else {
            emSetter.setCompositeErrorMessage("Attribute VisitStartDate not found");
        }

        String endDate = XMLUtils.getAttributeValue(itemNode, "VisitEndDate");
        if(StringUtils.isNotEmpty(endDate)){
            try {
                visitEndDate = CalendarUtils.parseDateWithDayTime(endDate);
            } catch (Exception e){
                emSetter.setCompositeErrorMessage("Attribute VisitEndDate not found or incorrect");
            }
        } else {
            emSetter.setCompositeErrorMessage("Attribute VisitEndDate not found");
        }

        return new OutcomeMigrationRequestsItem(idOfRequest, idOfOrgRegistry, idOfClient, idOfOrgVisit, visitStartDate, visitEndDate, emSetter.getStr());
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

    public OutcomeMigrationRequestsItem() {
    }

    public OutcomeMigrationRequestsItem(Long idOfRequest, Long idOfOrgRegistry, Long idOfClient, Long idOfOrgVisit,
            Date visitStartDate, Date visitEndDate, String errorMessage) {
        this.idOfRequest = idOfRequest;
        this.idOfOrgRegistry = idOfOrgRegistry;
        this.idOfClient = idOfClient;
        this.idOfOrgVisit = idOfOrgVisit;
        this.visitStartDate = visitStartDate;
        this.visitEndDate = visitEndDate;
        this.errorMessage = errorMessage;
        if(errorMessage == null || errorMessage.isEmpty() ){
            this.resCode = ERROR_CODE_ALL_OK;
        } else {
            this.resCode = ERROR_CODE_NOT_VALID_ATTRIBUTE;
        }
    }

    public Long getIdOfRequest() {
        return idOfRequest;
    }

    public void setIdOfRequest(Long idOfRequest) {
        this.idOfRequest = idOfRequest;
    }

    public Long getIdOfOrgRegistry() {
        return idOfOrgRegistry;
    }

    public void setIdOfOrgRegistry(Long idOfOrgRegistry) {
        this.idOfOrgRegistry = idOfOrgRegistry;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrgVisit() {
        return idOfOrgVisit;
    }

    public void setIdOfOrgVisit(Long idOfOrgVisit) {
        this.idOfOrgVisit = idOfOrgVisit;
    }

    public Date getVisitStartDate() {
        return visitStartDate;
    }

    public void setVisitStartDate(Date visitStartDate) {
        this.visitStartDate = visitStartDate;
    }

    public Date getVisitEndDate() {
        return visitEndDate;
    }

    public void setVisitEndDate(Date visitEndDate) {
        this.visitEndDate = visitEndDate;
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
