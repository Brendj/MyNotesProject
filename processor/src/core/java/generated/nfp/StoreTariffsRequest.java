
package generated.nfp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="systemCode">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="tariffList" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffListType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "systemCode",
    "tariffList"
})
@XmlRootElement(name = "storeTariffsRequest", namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
public class StoreTariffsRequest {

    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected String systemCode;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected TariffListType tariffList;

    /**
     * Gets the value of the systemCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSystemCode() {
        return systemCode;
    }

    /**
     * Sets the value of the systemCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSystemCode(String value) {
        this.systemCode = value;
    }

    /**
     * Gets the value of the tariffList property.
     * 
     * @return
     *     possible object is
     *     {@link TariffListType }
     *     
     */
    public TariffListType getTariffList() {
        return tariffList;
    }

    /**
     * Sets the value of the tariffList property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffListType }
     *     
     */
    public void setTariffList(TariffListType value) {
        this.tariffList = value;
    }

}
