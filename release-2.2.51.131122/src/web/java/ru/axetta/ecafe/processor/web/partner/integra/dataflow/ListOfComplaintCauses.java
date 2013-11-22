package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintCauses", propOrder = {
        "c"
})
public class ListOfComplaintCauses {

    @XmlElement(name = "C")
    protected List<ListOfComplaintCausesExt> c;

    public List<ListOfComplaintCausesExt> getC() {
        if (c == null) {
            c = new ArrayList<ListOfComplaintCausesExt>();
        }
        return this.c;
    }

}
