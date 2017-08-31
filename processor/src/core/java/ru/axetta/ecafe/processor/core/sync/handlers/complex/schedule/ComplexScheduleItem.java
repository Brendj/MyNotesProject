/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule;

import ru.axetta.ecafe.processor.core.persistence.ComplexSchedule;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 15.02.16
 * Time: 16:22
 * To change this template use File | Settings | File Templates.
 */
public class ComplexScheduleItem {

    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private Long idOfOrg;
    private Long idOfComplex;
    private String guid;
    private Integer intervalFrom;
    private Integer intervalTo;
    private Long idOfOrgCreated;
    private String errorMessage;
    private Integer resCode;
    private Long version;

    public ComplexScheduleItem(ComplexSchedule schedule) {
        this.idOfOrg = schedule.getIdOfOrg();
        this.idOfComplex = schedule.getIdOfComplex();
        this.guid = schedule.getGuid();
        this.intervalFrom = schedule.getIntervalFrom();
        this.intervalTo = schedule.getIntervalTo();
        this.idOfOrgCreated = schedule.getIdOfOrgCreated();
        this.version = schedule.getVersion();
    }

    public ComplexScheduleItem(String guid, Integer resCode, String errorMessage) {
        this.guid = guid;
        this.resCode = resCode;
        this.errorMessage = errorMessage;
    }

    public ComplexScheduleItem(Long idOfOrg, Long idOfComplex, String guid, Integer intervalFrom, Integer intervalTo,
            Long idOfOrgCreated) {
        this.idOfOrg = idOfOrg;
        this.idOfComplex = idOfComplex;
        this.guid = guid;
        this.intervalFrom = intervalFrom;
        this.intervalTo = intervalTo;
        this.idOfOrgCreated = idOfOrgCreated;
        this.resCode = 0;
    }

    public static ComplexScheduleItem build(Node itemNode, Long orgOwner) {
        Long idOfOrg;
        Long idOfComplex;
        Integer intervalFrom;
        Integer intervalTo;
        String guid = XMLUtils.getAttributeValue(itemNode, "Guid");
        String strIdOfOrg = XMLUtils.getAttributeValue(itemNode, "IdOfOrg");
        String strIdOfComplex = XMLUtils.getAttributeValue(itemNode, "ComplexId");
        String strIntervalFrom = XMLUtils.getAttributeValue(itemNode, "IntervalFrom");
        String strIntervalTo = XMLUtils.getAttributeValue(itemNode, "IntervalTo");
            try {
                idOfOrg = Long.parseLong(strIdOfOrg);
                idOfComplex = Long.parseLong(strIdOfComplex);
                intervalFrom = Integer.parseInt(strIntervalFrom);
                intervalTo = Integer.parseInt(strIntervalTo);
                return new ComplexScheduleItem(idOfOrg, idOfComplex, guid, intervalFrom, intervalTo, orgOwner);
            } catch (Exception e) {
                return new ComplexScheduleItem(guid, 1, "Incorrect attribute values");
            }
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "IdOfOrg", idOfOrg);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        XMLUtils.setAttributeIfNotNull(element, "ComplexId", idOfComplex);
        XMLUtils.setAttributeIfNotNull(element, "IntervalFrom", intervalFrom);
        XMLUtils.setAttributeIfNotNull(element, "IntervalTo", intervalTo);
        return element;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(Long idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getIntervalFrom() {
        return intervalFrom;
    }

    public void setIntervalFrom(Integer intervalFrom) {
        this.intervalFrom = intervalFrom;
    }

    public Integer getIntervalTo() {
        return intervalTo;
    }

    public void setIntervalTo(Integer intervalTo) {
        this.intervalTo = intervalTo;
    }

    public Long getIdOfOrgCreated() {
        return idOfOrgCreated;
    }

    public void setIdOfOrgCreated(Long idOfOrgCreated) {
        this.idOfOrgCreated = idOfOrgCreated;
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
