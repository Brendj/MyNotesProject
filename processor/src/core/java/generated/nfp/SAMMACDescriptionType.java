
package generated.nfp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Подписи
 * 
 * <p>Java class for SAMMACDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SAMMACDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="samID" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="samIdentityCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *               &lt;enumeration value="UECN"/>
 *               &lt;enumeration value="CHIP"/>
 *               &lt;enumeration value="IP"/>
 *               &lt;enumeration value="OTHER"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="samIdentityName" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="samCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *               &lt;enumeration value="UEC"/>
 *               &lt;enumeration value="SAM"/>
 *               &lt;enumeration value="HOST"/>
 *               &lt;enumeration value="ADM"/>
 *               &lt;enumeration value="OTHER"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="samName" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="32"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="algType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="keyIndex" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="contId" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
 *         &lt;element name="dataForSamMacSign" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="1020"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="samMACSign" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="64"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="macDetail" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SAMMACDescriptionType", namespace = "http://schemas.msk.ru/uec/transaction/v1", propOrder = {

})
public class SAMMACDescriptionType {

    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String samID;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String samIdentityCode;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String samIdentityName;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String samCode;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String samName;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String algType;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected int key;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected Integer keyIndex;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1", required = true, type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] contId;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String dataForSamMacSign;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String samMACSign;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/transaction/v1")
    protected String macDetail;

    /**
     * Gets the value of the samID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSamID() {
        return samID;
    }

    /**
     * Sets the value of the samID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSamID(String value) {
        this.samID = value;
    }

    /**
     * Gets the value of the samIdentityCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSamIdentityCode() {
        return samIdentityCode;
    }

    /**
     * Sets the value of the samIdentityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSamIdentityCode(String value) {
        this.samIdentityCode = value;
    }

    /**
     * Gets the value of the samIdentityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSamIdentityName() {
        return samIdentityName;
    }

    /**
     * Sets the value of the samIdentityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSamIdentityName(String value) {
        this.samIdentityName = value;
    }

    /**
     * Gets the value of the samCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSamCode() {
        return samCode;
    }

    /**
     * Sets the value of the samCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSamCode(String value) {
        this.samCode = value;
    }

    /**
     * Gets the value of the samName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSamName() {
        return samName;
    }

    /**
     * Sets the value of the samName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSamName(String value) {
        this.samName = value;
    }

    /**
     * Gets the value of the algType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlgType() {
        return algType;
    }

    /**
     * Sets the value of the algType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlgType(String value) {
        this.algType = value;
    }

    /**
     * Gets the value of the key property.
     * 
     */
    public int getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     */
    public void setKey(int value) {
        this.key = value;
    }

    /**
     * Gets the value of the keyIndex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getKeyIndex() {
        return keyIndex;
    }

    /**
     * Sets the value of the keyIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setKeyIndex(Integer value) {
        this.keyIndex = value;
    }

    /**
     * Gets the value of the contId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getContId() {
        return contId;
    }

    /**
     * Sets the value of the contId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContId(byte[] value) {
        this.contId = ((byte[]) value);
    }

    /**
     * Gets the value of the dataForSamMacSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataForSamMacSign() {
        return dataForSamMacSign;
    }

    /**
     * Sets the value of the dataForSamMacSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataForSamMacSign(String value) {
        this.dataForSamMacSign = value;
    }

    /**
     * Gets the value of the samMACSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSamMACSign() {
        return samMACSign;
    }

    /**
     * Sets the value of the samMACSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSamMACSign(String value) {
        this.samMACSign = value;
    }

    /**
     * Gets the value of the macDetail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacDetail() {
        return macDetail;
    }

    /**
     * Sets the value of the macDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacDetail(String value) {
        this.macDetail = value;
    }

}
