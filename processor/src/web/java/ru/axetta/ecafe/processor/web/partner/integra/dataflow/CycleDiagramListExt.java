package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.14
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CycleDiagramListExt", propOrder = {
        "c"
})
public class CycleDiagramListExt {

    @XmlElement(name = "C")
    protected List<CycleDiagramExt> c;

    public List<CycleDiagramExt> getC() {
        if (c == null) {
            c = new ArrayList<CycleDiagramExt>();
        }
        return this.c;
    }

}
