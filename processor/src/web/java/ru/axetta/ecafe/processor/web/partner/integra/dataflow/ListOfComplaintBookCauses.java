package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintBookCauses", propOrder = {
        "c"
})
public class ListOfComplaintBookCauses {

    @XmlElement(name = "C")
    protected List<ListOfComplaintBookCausesExt> c;

    public List<ListOfComplaintBookCausesExt> getC() {
        if (c == null) {
            c = new ArrayList<ListOfComplaintBookCausesExt>();
        }
        return this.c;
    }

}
