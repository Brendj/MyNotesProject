package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintBookEntries", propOrder = {
        "e"
})
public class ListOfComplaintBookEntries {

    @XmlElement(name = "E")
    protected List<ListOfComplaintBookEntriesExt> e;

    public List<ListOfComplaintBookEntriesExt> getE() {
        if (e == null) {
            e = new ArrayList<ListOfComplaintBookEntriesExt>();
        }
        return this.e;
    }

}