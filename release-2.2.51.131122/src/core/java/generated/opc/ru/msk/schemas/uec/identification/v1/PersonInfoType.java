
package generated.opc.ru.msk.schemas.uec.identification.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Данные персоны
 * 
 * <p>Java class for PersonInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PersonInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://schemas.msk.ru/uec/identification/v1}NameType" minOccurs="0"/>
 *         &lt;element name="sex" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="citizenship" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="birthCityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthCityCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDistrictName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthDistrictCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthRegionName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthRegionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthCountryName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="birthCountryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="photo" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonInfoType", propOrder = {
    "name",
    "sex",
    "citizenship",
    "dateOfBirth",
    "birthCityName",
    "birthCityCode",
    "birthDistrictName",
    "birthDistrictCode",
    "birthRegionName",
    "birthRegionCode",
    "birthCountryName",
    "birthCountryCode",
    "photo"
})
public class PersonInfoType {

    protected NameType name;
    protected Integer sex;
    @XmlElementRef(name = "citizenship", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> citizenship;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateOfBirth;
    protected String birthCityName;
    protected String birthCityCode;
    @XmlElementRef(name = "birthDistrictName", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> birthDistrictName;
    @XmlElementRef(name = "birthDistrictCode", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> birthDistrictCode;
    @XmlElementRef(name = "birthRegionName", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> birthRegionName;
    @XmlElementRef(name = "birthRegionCode", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> birthRegionCode;
    protected String birthCountryName;
    protected String birthCountryCode;
    @XmlElementRef(name = "photo", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<byte[]> photo;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link NameType }
     *     
     */
    public NameType getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link NameType }
     *     
     */
    public void setName(NameType value) {
        this.name = value;
    }

    /**
     * Gets the value of the sex property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSex() {
        return sex;
    }

    /**
     * Sets the value of the sex property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSex(Integer value) {
        this.sex = value;
    }

    /**
     * Gets the value of the citizenship property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCitizenship() {
        return citizenship;
    }

    /**
     * Sets the value of the citizenship property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCitizenship(JAXBElement<String> value) {
        this.citizenship = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateOfBirth(XMLGregorianCalendar value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the birthCityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthCityName() {
        return birthCityName;
    }

    /**
     * Sets the value of the birthCityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthCityName(String value) {
        this.birthCityName = value;
    }

    /**
     * Gets the value of the birthCityCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthCityCode() {
        return birthCityCode;
    }

    /**
     * Sets the value of the birthCityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthCityCode(String value) {
        this.birthCityCode = value;
    }

    /**
     * Gets the value of the birthDistrictName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBirthDistrictName() {
        return birthDistrictName;
    }

    /**
     * Sets the value of the birthDistrictName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBirthDistrictName(JAXBElement<String> value) {
        this.birthDistrictName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the birthDistrictCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBirthDistrictCode() {
        return birthDistrictCode;
    }

    /**
     * Sets the value of the birthDistrictCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBirthDistrictCode(JAXBElement<String> value) {
        this.birthDistrictCode = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the birthRegionName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBirthRegionName() {
        return birthRegionName;
    }

    /**
     * Sets the value of the birthRegionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBirthRegionName(JAXBElement<String> value) {
        this.birthRegionName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the birthRegionCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBirthRegionCode() {
        return birthRegionCode;
    }

    /**
     * Sets the value of the birthRegionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBirthRegionCode(JAXBElement<String> value) {
        this.birthRegionCode = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the birthCountryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthCountryName() {
        return birthCountryName;
    }

    /**
     * Sets the value of the birthCountryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthCountryName(String value) {
        this.birthCountryName = value;
    }

    /**
     * Gets the value of the birthCountryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthCountryCode() {
        return birthCountryCode;
    }

    /**
     * Sets the value of the birthCountryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthCountryCode(String value) {
        this.birthCountryCode = value;
    }

    /**
     * Gets the value of the photo property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getPhoto() {
        return photo;
    }

    /**
     * Sets the value of the photo property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setPhoto(JAXBElement<byte[]> value) {
        this.photo = ((JAXBElement<byte[]> ) value);
    }

}
