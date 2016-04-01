
package ru.axetta.ecafe.processor.web.istkservice.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for plainSchool complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="plainSchool">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="schoolId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="schoolName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="permitGame" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="districtId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="areaId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="isppId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "plainSchool", propOrder = {
    "schoolId",
    "schoolName",
    "permitGame",
    "districtId",
    "areaId",
    "isppId"
})
public class PlainSchool {

    protected long schoolId;
    protected String schoolName;
    protected boolean permitGame;
    protected long districtId;
    protected long areaId;
    protected long isppId;

    /**
     * Gets the value of the schoolId property.
     * 
     */
    public long getSchoolId() {
        return schoolId;
    }

    /**
     * Sets the value of the schoolId property.
     * 
     */
    public void setSchoolId(long value) {
        this.schoolId = value;
    }

    /**
     * Gets the value of the schoolName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchoolName() {
        return schoolName;
    }

    /**
     * Sets the value of the schoolName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchoolName(String value) {
        this.schoolName = value;
    }

    /**
     * Gets the value of the permitGame property.
     * 
     */
    public boolean isPermitGame() {
        return permitGame;
    }

    /**
     * Sets the value of the permitGame property.
     * 
     */
    public void setPermitGame(boolean value) {
        this.permitGame = value;
    }

    /**
     * Gets the value of the districtId property.
     * 
     */
    public long getDistrictId() {
        return districtId;
    }

    /**
     * Sets the value of the districtId property.
     * 
     */
    public void setDistrictId(long value) {
        this.districtId = value;
    }

    /**
     * Gets the value of the areaId property.
     * 
     */
    public long getAreaId() {
        return areaId;
    }

    /**
     * Sets the value of the areaId property.
     * 
     */
    public void setAreaId(long value) {
        this.areaId = value;
    }

    /**
     * Gets the value of the isppId property.
     * 
     */
    public long getIsppId() {
        return isppId;
    }

    /**
     * Sets the value of the isppId property.
     * 
     */
    public void setIsppId(long value) {
        this.isppId = value;
    }

}
