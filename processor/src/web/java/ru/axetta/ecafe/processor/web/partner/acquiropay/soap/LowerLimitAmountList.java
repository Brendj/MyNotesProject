package ru.axetta.ecafe.processor.web.partner.acquiropay.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 09.01.14
 * Time: 12:44
 */

@XmlRootElement(name = "LowerLimitAmountList")
@XmlAccessorType(XmlAccessType.FIELD)
public class LowerLimitAmountList {

    @XmlElement(name = "LowerLimitAmount")
    private List<Long> list;

    public List<Long> getList() {
        return list;
    }

    public void setList(List<Long> list) {
        this.list = list;
    }
}
