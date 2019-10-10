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
 * Created by nuc on 25.09.2019.
 */
@XmlRootElement(name = "StatusList")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatusList {
    @XmlElement(name = "StatusInfo")
    private List<StatusInfo> statusList;

    public List<StatusInfo> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<StatusInfo> statusList) {
        this.statusList = statusList;
    }
}
