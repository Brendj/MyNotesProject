
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * �����
 * 
 * <p>Java class for Address complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Address">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CountryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PostalCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Locality" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Region" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="City" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Town" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Street" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="House" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Building" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Structure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Facility" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Ownership" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Flat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="POBox" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Okato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="KladrCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="KladrStreetCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BTIDistrictCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BTIRegionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BTIStreetCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BTIBuildingCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BTIAltCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BTIFlatCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FiasCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Address", propOrder = {
    "country",
    "countryCode",
    "postalCode",
    "locality",
    "region",
    "city",
    "town",
    "street",
    "house",
    "building",
    "structure",
    "facility",
    "ownership",
    "flat",
    "poBox",
    "okato",
    "kladrCode",
    "kladrStreetCode",
    "btiDistrictCode",
    "btiRegionCode",
    "btiStreetCode",
    "btiBuildingCode",
    "btiAltCode",
    "btiFlatCode",
    "fiasCode"
})
public class Address {

    @XmlElement(name = "Country")
    protected String country;
    @XmlElement(name = "CountryCode")
    protected String countryCode;
    @XmlElement(name = "PostalCode")
    protected String postalCode;
    @XmlElement(name = "Locality")
    protected String locality;
    @XmlElement(name = "Region")
    protected String region;
    @XmlElement(name = "City")
    protected String city;
    @XmlElement(name = "Town")
    protected String town;
    @XmlElement(name = "Street")
    protected String street;
    @XmlElement(name = "House")
    protected String house;
    @XmlElement(name = "Building")
    protected String building;
    @XmlElement(name = "Structure")
    protected String structure;
    @XmlElement(name = "Facility")
    protected String facility;
    @XmlElement(name = "Ownership")
    protected String ownership;
    @XmlElement(name = "Flat")
    protected String flat;
    @XmlElement(name = "POBox")
    protected String poBox;
    @XmlElement(name = "Okato")
    protected String okato;
    @XmlElement(name = "KladrCode")
    protected String kladrCode;
    @XmlElement(name = "KladrStreetCode")
    protected String kladrStreetCode;
    @XmlElement(name = "BTIDistrictCode")
    protected String btiDistrictCode;
    @XmlElement(name = "BTIRegionCode")
    protected String btiRegionCode;
    @XmlElement(name = "BTIStreetCode")
    protected String btiStreetCode;
    @XmlElement(name = "BTIBuildingCode")
    protected String btiBuildingCode;
    @XmlElement(name = "BTIAltCode")
    protected String btiAltCode;
    @XmlElement(name = "BTIFlatCode")
    protected String btiFlatCode;
    @XmlElement(name = "FiasCode")
    protected String fiasCode;

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
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
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the locality property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocality() {
        return locality;
    }

    /**
     * Sets the value of the locality property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocality(String value) {
        this.locality = value;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegion(String value) {
        this.region = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the town property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTown() {
        return town;
    }

    /**
     * Sets the value of the town property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTown(String value) {
        this.town = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
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
     *     {@link String }
     *     
     */
    public String getBuilding() {
        return building;
    }

    /**
     * Sets the value of the building property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuilding(String value) {
        this.building = value;
    }

    /**
     * Gets the value of the structure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStructure() {
        return structure;
    }

    /**
     * Sets the value of the structure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStructure(String value) {
        this.structure = value;
    }

    /**
     * Gets the value of the facility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFacility() {
        return facility;
    }

    /**
     * Sets the value of the facility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFacility(String value) {
        this.facility = value;
    }

    /**
     * Gets the value of the ownership property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnership() {
        return ownership;
    }

    /**
     * Sets the value of the ownership property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnership(String value) {
        this.ownership = value;
    }

    /**
     * Gets the value of the flat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlat() {
        return flat;
    }

    /**
     * Sets the value of the flat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlat(String value) {
        this.flat = value;
    }

    /**
     * Gets the value of the poBox property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOBox() {
        return poBox;
    }

    /**
     * Sets the value of the poBox property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOBox(String value) {
        this.poBox = value;
    }

    /**
     * Gets the value of the okato property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOkato() {
        return okato;
    }

    /**
     * Sets the value of the okato property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOkato(String value) {
        this.okato = value;
    }

    /**
     * Gets the value of the kladrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKladrCode() {
        return kladrCode;
    }

    /**
     * Sets the value of the kladrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKladrCode(String value) {
        this.kladrCode = value;
    }

    /**
     * Gets the value of the kladrStreetCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKladrStreetCode() {
        return kladrStreetCode;
    }

    /**
     * Sets the value of the kladrStreetCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKladrStreetCode(String value) {
        this.kladrStreetCode = value;
    }

    /**
     * Gets the value of the btiDistrictCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBTIDistrictCode() {
        return btiDistrictCode;
    }

    /**
     * Sets the value of the btiDistrictCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBTIDistrictCode(String value) {
        this.btiDistrictCode = value;
    }

    /**
     * Gets the value of the btiRegionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBTIRegionCode() {
        return btiRegionCode;
    }

    /**
     * Sets the value of the btiRegionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBTIRegionCode(String value) {
        this.btiRegionCode = value;
    }

    /**
     * Gets the value of the btiStreetCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBTIStreetCode() {
        return btiStreetCode;
    }

    /**
     * Sets the value of the btiStreetCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBTIStreetCode(String value) {
        this.btiStreetCode = value;
    }

    /**
     * Gets the value of the btiBuildingCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBTIBuildingCode() {
        return btiBuildingCode;
    }

    /**
     * Sets the value of the btiBuildingCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBTIBuildingCode(String value) {
        this.btiBuildingCode = value;
    }

    /**
     * Gets the value of the btiAltCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBTIAltCode() {
        return btiAltCode;
    }

    /**
     * Sets the value of the btiAltCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBTIAltCode(String value) {
        this.btiAltCode = value;
    }

    /**
     * Gets the value of the btiFlatCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBTIFlatCode() {
        return btiFlatCode;
    }

    /**
     * Sets the value of the btiFlatCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBTIFlatCode(String value) {
        this.btiFlatCode = value;
    }

    /**
     * Gets the value of the fiasCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFiasCode() {
        return fiasCode;
    }

    /**
     * Sets the value of the fiasCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFiasCode(String value) {
        this.fiasCode = value;
    }

}
