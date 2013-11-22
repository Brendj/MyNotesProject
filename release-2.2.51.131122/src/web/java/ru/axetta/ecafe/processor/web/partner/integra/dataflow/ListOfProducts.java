package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfProducts", propOrder = {
        "p"
})
public class ListOfProducts {

    @XmlElement(name = "P")
    protected List<ListOfProductsExt> p;

    public List<ListOfProductsExt> getP() {
        if (p == null) {
            p = new ArrayList<ListOfProductsExt>();
        }
        return this.p;
    }

}
