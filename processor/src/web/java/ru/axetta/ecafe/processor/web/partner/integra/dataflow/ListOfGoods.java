package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfGoods", propOrder = {
        "g"
})
public class ListOfGoods {

    @XmlElement(name = "G")
    protected List<ListOfGoodsExt> g;

    public List<ListOfGoodsExt> getG() {
        if (g == null) {
            g = new ArrayList<ListOfGoodsExt>();
        }
        return this.g;
    }

}
