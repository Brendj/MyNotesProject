
package generated.ru.mos.rnip.xsd.common._2_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Дополнительные реквизиты платежа, предусмотренные приказом Минфина России от 12 ноября
 * 				2013 г. №107н
 * 			
 * 
 * <p>Java class for BudgetIndexType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BudgetIndexType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="status" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="2"/>
 *             &lt;enumeration value="0"/>
 *             &lt;enumeration value="00"/>
 *             &lt;enumeration value="01"/>
 *             &lt;enumeration value="02"/>
 *             &lt;enumeration value="03"/>
 *             &lt;enumeration value="04"/>
 *             &lt;enumeration value="05"/>
 *             &lt;enumeration value="06"/>
 *             &lt;enumeration value="07"/>
 *             &lt;enumeration value="08"/>
 *             &lt;enumeration value="09"/>
 *             &lt;enumeration value="10"/>
 *             &lt;enumeration value="11"/>
 *             &lt;enumeration value="12"/>
 *             &lt;enumeration value="13"/>
 *             &lt;enumeration value="14"/>
 *             &lt;enumeration value="15"/>
 *             &lt;enumeration value="16"/>
 *             &lt;enumeration value="17"/>
 *             &lt;enumeration value="18"/>
 *             &lt;enumeration value="19"/>
 *             &lt;enumeration value="20"/>
 *             &lt;enumeration value="21"/>
 *             &lt;enumeration value="22"/>
 *             &lt;enumeration value="23"/>
 *             &lt;enumeration value="24"/>
 *             &lt;enumeration value="25"/>
 *             &lt;enumeration value="26"/>
 *             &lt;enumeration value="27"/>
 *             &lt;enumeration value="28"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="paytReason" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="2"/>
 *             &lt;enumeration value="ТП"/>
 *             &lt;enumeration value="ЗД"/>
 *             &lt;enumeration value="БФ"/>
 *             &lt;enumeration value="ТР"/>
 *             &lt;enumeration value="РС"/>
 *             &lt;enumeration value="ОТ"/>
 *             &lt;enumeration value="РТ"/>
 *             &lt;enumeration value="ПБ"/>
 *             &lt;enumeration value="ПР"/>
 *             &lt;enumeration value="АП"/>
 *             &lt;enumeration value="АР"/>
 *             &lt;enumeration value="ИН"/>
 *             &lt;enumeration value="ТЛ"/>
 *             &lt;enumeration value="ЗТ"/>
 *             &lt;enumeration value="ДЕ"/>
 *             &lt;enumeration value="ПО"/>
 *             &lt;enumeration value="КТ"/>
 *             &lt;enumeration value="ИД"/>
 *             &lt;enumeration value="ИП"/>
 *             &lt;enumeration value="ТУ"/>
 *             &lt;enumeration value="БД"/>
 *             &lt;enumeration value="КП"/>
 *             &lt;enumeration value="ВУ"/>
 *             &lt;enumeration value="ДК"/>
 *             &lt;enumeration value="ПК"/>
 *             &lt;enumeration value="КК"/>
 *             &lt;enumeration value="ТК"/>
 *             &lt;enumeration value="ПД"/>
 *             &lt;enumeration value="КВ"/>
 *             &lt;enumeration value="00"/>
 *             &lt;enumeration value="0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="taxPeriod" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="МС\.(0[0-9]|1[012])\.\d{4}"/>
 *             &lt;pattern value="КВ\.0[1-4]\.\d{4}"/>
 *             &lt;pattern value="ПЛ\.0[1-2]\.\d{4}"/>
 *             &lt;pattern value="ГД\.00\.\d{4}"/>
 *             &lt;pattern value="(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[012])\.\d{4}"/>
 *             &lt;pattern value="\d{8}"/>
 *             &lt;pattern value="0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="taxDocNumber" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="15"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="taxDocDate" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[012])\.\d{4}"/>
 *             &lt;pattern value="0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BudgetIndexType")
public class BudgetIndexType {

    @XmlAttribute(name = "status", required = true)
    protected String status;
    @XmlAttribute(name = "paytReason", required = true)
    protected String paytReason;
    @XmlAttribute(name = "taxPeriod", required = true)
    protected String taxPeriod;
    @XmlAttribute(name = "taxDocNumber", required = true)
    protected String taxDocNumber;
    @XmlAttribute(name = "taxDocDate", required = true)
    protected String taxDocDate;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the paytReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaytReason() {
        return paytReason;
    }

    /**
     * Sets the value of the paytReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaytReason(String value) {
        this.paytReason = value;
    }

    /**
     * Gets the value of the taxPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxPeriod() {
        return taxPeriod;
    }

    /**
     * Sets the value of the taxPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxPeriod(String value) {
        this.taxPeriod = value;
    }

    /**
     * Gets the value of the taxDocNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxDocNumber() {
        return taxDocNumber;
    }

    /**
     * Sets the value of the taxDocNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxDocNumber(String value) {
        this.taxDocNumber = value;
    }

    /**
     * Gets the value of the taxDocDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxDocDate() {
        return taxDocDate;
    }

    /**
     * Sets the value of the taxDocDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxDocDate(String value) {
        this.taxDocDate = value;
    }

}
