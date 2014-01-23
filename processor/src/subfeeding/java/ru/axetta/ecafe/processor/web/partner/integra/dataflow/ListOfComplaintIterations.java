package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintIterations", propOrder = {
        "i"
})
public class ListOfComplaintIterations {

    @XmlElement(name = "I")
    protected List<ListOfComplaintIterationsExt> i;

    public List<ListOfComplaintIterationsExt> getI() {
        if (i == null) {
            i = new ArrayList<ListOfComplaintIterationsExt>();
        }
        return this.i;
    }

}
