package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintOrders", propOrder = {
        "o"
})
public class ListOfComplaintOrders {

    @XmlElement(name = "O")
    protected List<ListOfComplaintOrdersExt> o;

    public List<ListOfComplaintOrdersExt> getO() {
        if (o == null) {
            o = new ArrayList<ListOfComplaintOrdersExt>();
        }
        return this.o;
    }

}
