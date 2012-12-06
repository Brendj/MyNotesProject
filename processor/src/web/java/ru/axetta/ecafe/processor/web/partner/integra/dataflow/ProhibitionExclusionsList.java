package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProhibitionExclusionsList", propOrder = {
        "e"
})
public class ProhibitionExclusionsList {

    @XmlElement(name = "E")
    protected List<ProhibitionExclusionsListExt> e;

    public List<ProhibitionExclusionsListExt> getE() {
        if (e == null) {
            e = new ArrayList<ProhibitionExclusionsListExt>();
        }
        return this.e;
    }

}
