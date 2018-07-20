/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;


import javax.xml.bind.annotation.*;
import java.math.BigInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "peopleQuantityInGroup", propOrder = {
        "groupName",
        "quantity"
})
public class PeopleQuantityInGroup {
    @XmlAttribute(name = "groupName")
    private String groupName;
    @XmlAttribute(name = "quantity")
    private BigInteger quantity;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public BigInteger getQuantity() {
        return quantity;
    }

    public void setQuantity(BigInteger quantity) {
        this.quantity = quantity;
    }
}
