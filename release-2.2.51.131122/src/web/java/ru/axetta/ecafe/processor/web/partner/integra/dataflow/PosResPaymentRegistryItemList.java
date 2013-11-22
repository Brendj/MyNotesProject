package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.10.13
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
public class PosResPaymentRegistryItemList {

    @XmlElement(name = "I")
    protected List<PosResPaymentRegistryItem> i;

    public List<PosResPaymentRegistryItem> getI() {
        if (i == null) {
            i = new ArrayList<PosResPaymentRegistryItem>();
        }
        return this.i;
    }

}
