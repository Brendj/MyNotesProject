
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Список тарифов
 * 
 * <p>Java class for TariffListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TariffListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tariff" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffType" maxOccurs="100"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TariffListType", propOrder = {
    "tariff"
})
public class TariffListType {

    @XmlElement(required = true)
    protected List<TariffType> tariff;

    /**
     * Gets the value of the tariff property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tariff property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTariff().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TariffType }
     * 
     * 
     */
    public List<TariffType> getTariff() {
        if (tariff == null) {
            tariff = new ArrayList<TariffType>();
        }
        return this.tariff;
    }

}
