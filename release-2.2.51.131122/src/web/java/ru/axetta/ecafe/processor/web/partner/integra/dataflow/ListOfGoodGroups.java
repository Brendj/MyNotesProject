package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfGoodGroups", propOrder = {
        "gg"
})
public class ListOfGoodGroups {

    @XmlElement(name = "GG")
    protected List<ListOfGoodGroupsExt> gg;

    public List<ListOfGoodGroupsExt> getGG() {
        if (gg == null) {
            gg = new ArrayList<ListOfGoodGroupsExt>();
        }
        return this.gg;
    }

}
