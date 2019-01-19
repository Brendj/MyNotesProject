/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import ru.axetta.ecafe.processor.web.internal.ResponseItem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuardianInfoResult")
public class GuardianInfoResult extends ResponseItem {
    @XmlElement(name="guardianDesc")
    private GuardianDesc.GuardianDescItemParamList guardianDescList;

    public GuardianInfoResult() {
        guardianDescList = new GuardianDesc.GuardianDescItemParamList();
    }

    public GuardianDesc.GuardianDescItemParamList getGuardianDescList() {
        return guardianDescList;
    }

    public void setGuardianDescList(GuardianDesc.GuardianDescItemParamList guardianDescList) {
        this.guardianDescList = guardianDescList;
    }
}
