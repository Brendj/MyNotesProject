
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Тип для ответа по тарификационным расчетов
 * 
 * <p>Java class for getBillsResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBillsResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="billsList" type="{http://schemas.msk.ru/uec/TransactionService/v1}BillListType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBillsResponseType", propOrder = {
    "billsList"
})
public class GetBillsResponseType {

    @XmlElement(required = true)
    protected BillListType billsList;

    /**
     * Gets the value of the billsList property.
     * 
     * @return
     *     possible object is
     *     {@link BillListType }
     *     
     */
    public BillListType getBillsList() {
        return billsList;
    }

    /**
     * Sets the value of the billsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillListType }
     *     
     */
    public void setBillsList(BillListType value) {
        this.billsList = value;
    }

}
