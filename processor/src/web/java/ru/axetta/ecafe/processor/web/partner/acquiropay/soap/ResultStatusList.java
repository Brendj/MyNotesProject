/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by i.semenov on 10.10.2019.
 */
@XmlRootElement(name = "ResultStatusList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResultStatusList {
    @XmlElement(name = "ErrorCode")
    private int errorCode;
    @XmlElement(name = "ErrorDesc")
    private String errorDesc;
    @XmlElement(name = "StatusInfo")
    private List<StatusInfo> statusList;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public List<StatusInfo> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<StatusInfo> statusList) {
        this.statusList = statusList;
    }
}
