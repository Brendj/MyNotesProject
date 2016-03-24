/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 24.03.16
 * Time: 12:26
 */

public class InteractiveReportItem {
    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;

    private Long idOfRecord;
    private String value;
    private String errorMessage;
    private Integer resCode;

    public InteractiveReportItem() {
    }

    public static InteractiveReportItem build(Node itemNode) {
        Long idOfRecord = null;
        String value;

        String errorMessageComposite = "";

        String strIdOfRecord = XMLUtils.getAttributeValue(itemNode, "Id");
        if (StringUtils.isNotEmpty(strIdOfRecord)){
            try {
                idOfRecord = Long.parseLong(strIdOfRecord);
            } catch (NumberFormatException e) {
                errorMessageComposite += "NumberFormatException Id not found\n";
            }
        } else {
            errorMessageComposite += "Attribute Id not found\n";
        }

        value = XMLUtils.getAttributeValue(itemNode, "Value");
        if (StringUtils.isEmpty(value)) {
            errorMessageComposite += "Attribute Value not found\n";
        }

        return new InteractiveReportItem(idOfRecord, value, errorMessageComposite);
    }

    public InteractiveReportItem(Long idOfRecord, String value, String errorMessage) {
        this.idOfRecord = idOfRecord;
        this.value = value;
        if (errorMessage.equals("")) {
            this.setResCode(ERROR_CODE_ALL_OK);
        } else {
            this.setResCode(ERROR_CODE_NOT_VALID_ATTRIBUTE);
        }
    }

    public Long getIdOfRecord() {
        return idOfRecord;
    }

    public void setIdOfRecord(Long idOfRecord) {
        this.idOfRecord = idOfRecord;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
