
package generated.nfp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * Описание адреса
 * 
 * <p>Java class for AddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cityName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="cityCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="districtName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="districtCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="regionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="countryName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="countryCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="streetName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="streetCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="house" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="building" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="corp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="flat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressType", propOrder = {
    "cityName",
    "cityCode",
    "districtName",
    "districtCode",
    "regionName",
    "regionCode",
    "countryName",
    "countryCode",
    "streetName",
    "streetCode",
    "index",
    "house",
    "building",
    "corp",
    "flat"
})
public class AddressType {

    @XmlElement(required = true)
    protected String cityName;
    @XmlElement(required = true)
    protected String cityCode;
    @XmlElementRef(name = "districtName", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> districtName;
    @XmlElementRef(name = "districtCode", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> districtCode;
    @XmlElementRef(name = "regionName", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> regionName;
    @XmlElementRef(name = "regionCode", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> regionCode;
    @XmlElement(required = true)
    protected String countryName;
    @XmlElement(required = true)
    protected String countryCode;
    @XmlElement(required = true)
    protected String streetName;
    @XmlElement(required = true)
    protected String streetCode;
    @XmlElementRef(name = "index", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> index;
    @XmlElement(required = true)
    protected String house;
    @XmlElementRef(name = "building", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> building;
    @XmlElementRef(name = "corp", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> corp;
    @XmlElementRef(name = "flat", namespace = "http://schemas.msk.ru/uec/identification/v1", type = JAXBElement.class)
    protected JAXBElement<String> flat;

    /**
     * Gets the value of the cityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Sets the value of the cityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCityName(String value) {
        this.cityName = value;
    }

    /**
     * Gets the value of the cityCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCityCode() {
        return cityCode;
    }

    /**
     * Sets the value of the cityCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCityCode(String value) {
        this.cityCode = value;
    }

    /**
     * Gets the value of the districtName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDistrictName() {
        return districtName;
    }

    /**
     * Sets the value of the districtName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDistrictName(JAXBElement<String> value) {
        this.districtName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the districtCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getDistrictCode() {
        return districtCode;
    }

    /**
     * Sets the value of the districtCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setDistrictCode(JAXBElement<String> value) {
        this.districtCode = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the regionName property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRegionName() {
        return regionName;
    }

    /**
     * Sets the value of the regionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRegionName(JAXBElement<String> value) {
        this.regionName = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the regionCode property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getRegionCode() {
        return regionCode;
    }

    /**
     * Sets the value of the regionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setRegionCode(JAXBElement<String> value) {
        this.regionCode = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the countryName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Sets the value of the countryName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryName(String value) {
        this.countryName = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the streetName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Sets the value of the streetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreetName(String value) {
        this.streetName = value;
    }

    /**
     * Gets the value of the streetCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreetCode() {
        return streetCode;
    }

    /**
     * Sets the value of the streetCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreetCode(String value) {
        this.streetCode = value;
    }

    /**
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIndex(JAXBElement<String> value) {
        this.index = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the house property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHouse() {
        return house;
    }

    /**
     * Sets the value of the house property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHouse(String value) {
        this.house = value;
    }

    /**
     * Gets the value of the building property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBuilding() {
        return building;
    }

    /**
     * Sets the value of the building property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBuilding(JAXBElement<String> value) {
        this.building = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the corp property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCorp() {
        return corp;
    }

    /**
     * Sets the value of the corp property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCorp(JAXBElement<String> value) {
        this.corp = ((JAXBElement<String> ) value);
    }

    /**
     * Gets the value of the flat property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getFlat() {
        return flat;
    }

    /**
     * Sets the value of the flat property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setFlat(JAXBElement<String> value) {
        this.flat = ((JAXBElement<String> ) value);
    }

}
