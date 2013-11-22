
package generated.opc.ru.msk.schemas.uec.identification.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Описание данных подписи
 * 
 * <p>Java class for MacResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MacResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="keyIndex">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="macDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="macSignAddData" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="macSign" type="{http://www.w3.org/2001/XMLSchema}hexBinary"/>
 *         &lt;element name="macLabel" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="64"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MacResponseType", propOrder = {
    "keyIndex",
    "macDateTime",
    "macSignAddData",
    "macSign",
    "macLabel"
})
public class MacResponseType {

    protected int keyIndex;
    @XmlElement(required = true)
    protected XMLGregorianCalendar macDateTime;
    protected String macSignAddData;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] macSign;
    protected String macLabel;

    /**
     * Gets the value of the keyIndex property.
     * 
     */
    public int getKeyIndex() {
        return keyIndex;
    }

    /**
     * Sets the value of the keyIndex property.
     * 
     */
    public void setKeyIndex(int value) {
        this.keyIndex = value;
    }

    /**
     * Gets the value of the macDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMacDateTime() {
        return macDateTime;
    }

    /**
     * Sets the value of the macDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMacDateTime(XMLGregorianCalendar value) {
        this.macDateTime = value;
    }

    /**
     * Gets the value of the macSignAddData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacSignAddData() {
        return macSignAddData;
    }

    /**
     * Sets the value of the macSignAddData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacSignAddData(String value) {
        this.macSignAddData = value;
    }

    /**
     * Gets the value of the macSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getMacSign() {
        return macSign;
    }

    /**
     * Sets the value of the macSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacSign(byte[] value) {
        this.macSign = ((byte[]) value);
    }

    /**
     * Gets the value of the macLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacLabel() {
        return macLabel;
    }

    /**
     * Sets the value of the macLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacLabel(String value) {
        this.macLabel = value;
    }

}
