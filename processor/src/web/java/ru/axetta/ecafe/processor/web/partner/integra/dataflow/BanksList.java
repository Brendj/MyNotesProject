package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 07.08.12
 * Time: 17:16
 * To change this template use File | Settings | File Templates.
 */
public class BanksList {
    @XmlElement(name = "Banks")
    protected List<BankItem> banks;

    public List<BankItem> getBanks() {
        if (banks == null)
            banks = new ArrayList<BankItem>();
        return this.banks;
    }
}