
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cReps", propOrder = {"cRep"})
public class ClientRepresentativesList {

    @XmlElement(name="cRep")
    protected List<ClientRepresentatives> cRep;

    public List<ClientRepresentatives> getCRep() {
        if (cRep == null) {
            cRep = new ArrayList<ClientRepresentatives>();
        }
        return this.cRep;
    }
}
