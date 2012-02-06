
package generated.nfp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Список тарификационных расчетов
 * 
 * <p>Java class for BillListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BillListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bill" type="{http://schemas.msk.ru/uec/TransactionService/v1}BillType" maxOccurs="100" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BillListType", namespace = "http://schemas.msk.ru/uec/TransactionService/v1", propOrder = {
    "bill"
})
public class BillListType {

    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected List<BillType> bill;

    /**
     * Gets the value of the bill property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bill property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBill().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BillType }
     * 
     * 
     */
    public List<BillType> getBill() {
        if (bill == null) {
            bill = new ArrayList<BillType>();
        }
        return this.bill;
    }

}
