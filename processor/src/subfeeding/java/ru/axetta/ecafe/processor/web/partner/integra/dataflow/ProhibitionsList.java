package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProhibitionsList", propOrder = {
        "p"
})
public class ProhibitionsList {

    @XmlElement(name = "P")
    protected List<ProhibitionsListExt> p;

    public List<ProhibitionsListExt> getC() {
        if (p == null) {
            p = new ArrayList<ProhibitionsListExt>();
        }
        return this.p;
    }

}
