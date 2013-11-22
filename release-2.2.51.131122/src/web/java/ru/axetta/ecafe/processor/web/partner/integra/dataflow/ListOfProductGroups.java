package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfProductGroups", propOrder = {
        "pg"
})
public class ListOfProductGroups {

    @XmlElement(name = "PG")
    protected List<ListOfProductGroupsExt> pg;

    public List<ListOfProductGroupsExt> getPG() {
        if (pg == null) {
            pg = new ArrayList<ListOfProductGroupsExt>();
        }
        return this.pg;
    }

}
