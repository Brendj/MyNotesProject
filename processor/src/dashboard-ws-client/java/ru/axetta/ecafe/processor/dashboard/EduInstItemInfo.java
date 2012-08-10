
package ru.axetta.ecafe.processor.dashboard;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for eduInstItemInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eduInstItemInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstFullSyncTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="idOfOrg" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="lastFullSyncTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastSuccessfulBalanceSyncTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastSyncErrors" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="lastUnSuccessfulBalanceSyncTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="numberOfPaidMealsPerNumOfStaff" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfPaidMealsPerNumOfStudents" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfPassagesPerNumOfStaff" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfPassagesPerNumOfStudents" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfReducedPriceMealsPerNumOfStaff" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfReducedPriceMealsPerNumOfStudents" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eduInstItemInfo", propOrder = {
    "error",
    "firstFullSyncTime",
    "idOfOrg",
    "lastFullSyncTime",
    "lastSuccessfulBalanceSyncTime",
    "lastSyncErrors",
    "lastUnSuccessfulBalanceSyncTime",
    "numberOfPaidMealsPerNumOfStaff",
    "numberOfPaidMealsPerNumOfStudents",
    "numberOfPassagesPerNumOfStaff",
    "numberOfPassagesPerNumOfStudents",
    "numberOfReducedPriceMealsPerNumOfStaff",
    "numberOfReducedPriceMealsPerNumOfStudents"
})
public class EduInstItemInfo {

    protected String error;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstFullSyncTime;
    protected long idOfOrg;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastFullSyncTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastSuccessfulBalanceSyncTime;
    protected boolean lastSyncErrors;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUnSuccessfulBalanceSyncTime;
    protected double numberOfPaidMealsPerNumOfStaff;
    protected double numberOfPaidMealsPerNumOfStudents;
    protected double numberOfPassagesPerNumOfStaff;
    protected double numberOfPassagesPerNumOfStudents;
    protected double numberOfReducedPriceMealsPerNumOfStaff;
    protected double numberOfReducedPriceMealsPerNumOfStudents;

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setError(String value) {
        this.error = value;
    }

    /**
     * Gets the value of the firstFullSyncTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFirstFullSyncTime() {
        return firstFullSyncTime;
    }

    /**
     * Sets the value of the firstFullSyncTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFirstFullSyncTime(XMLGregorianCalendar value) {
        this.firstFullSyncTime = value;
    }

    /**
     * Gets the value of the idOfOrg property.
     * 
     */
    public long getIdOfOrg() {
        return idOfOrg;
    }

    /**
     * Sets the value of the idOfOrg property.
     * 
     */
    public void setIdOfOrg(long value) {
        this.idOfOrg = value;
    }

    /**
     * Gets the value of the lastFullSyncTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastFullSyncTime() {
        return lastFullSyncTime;
    }

    /**
     * Sets the value of the lastFullSyncTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastFullSyncTime(XMLGregorianCalendar value) {
        this.lastFullSyncTime = value;
    }

    /**
     * Gets the value of the lastSuccessfulBalanceSyncTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastSuccessfulBalanceSyncTime() {
        return lastSuccessfulBalanceSyncTime;
    }

    /**
     * Sets the value of the lastSuccessfulBalanceSyncTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastSuccessfulBalanceSyncTime(XMLGregorianCalendar value) {
        this.lastSuccessfulBalanceSyncTime = value;
    }

    /**
     * Gets the value of the lastSyncErrors property.
     * 
     */
    public boolean isLastSyncErrors() {
        return lastSyncErrors;
    }

    /**
     * Sets the value of the lastSyncErrors property.
     * 
     */
    public void setLastSyncErrors(boolean value) {
        this.lastSyncErrors = value;
    }

    /**
     * Gets the value of the lastUnSuccessfulBalanceSyncTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastUnSuccessfulBalanceSyncTime() {
        return lastUnSuccessfulBalanceSyncTime;
    }

    /**
     * Sets the value of the lastUnSuccessfulBalanceSyncTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastUnSuccessfulBalanceSyncTime(XMLGregorianCalendar value) {
        this.lastUnSuccessfulBalanceSyncTime = value;
    }

    /**
     * Gets the value of the numberOfPaidMealsPerNumOfStaff property.
     * 
     */
    public double getNumberOfPaidMealsPerNumOfStaff() {
        return numberOfPaidMealsPerNumOfStaff;
    }

    /**
     * Sets the value of the numberOfPaidMealsPerNumOfStaff property.
     * 
     */
    public void setNumberOfPaidMealsPerNumOfStaff(double value) {
        this.numberOfPaidMealsPerNumOfStaff = value;
    }

    /**
     * Gets the value of the numberOfPaidMealsPerNumOfStudents property.
     * 
     */
    public double getNumberOfPaidMealsPerNumOfStudents() {
        return numberOfPaidMealsPerNumOfStudents;
    }

    /**
     * Sets the value of the numberOfPaidMealsPerNumOfStudents property.
     * 
     */
    public void setNumberOfPaidMealsPerNumOfStudents(double value) {
        this.numberOfPaidMealsPerNumOfStudents = value;
    }

    /**
     * Gets the value of the numberOfPassagesPerNumOfStaff property.
     * 
     */
    public double getNumberOfPassagesPerNumOfStaff() {
        return numberOfPassagesPerNumOfStaff;
    }

    /**
     * Sets the value of the numberOfPassagesPerNumOfStaff property.
     * 
     */
    public void setNumberOfPassagesPerNumOfStaff(double value) {
        this.numberOfPassagesPerNumOfStaff = value;
    }

    /**
     * Gets the value of the numberOfPassagesPerNumOfStudents property.
     * 
     */
    public double getNumberOfPassagesPerNumOfStudents() {
        return numberOfPassagesPerNumOfStudents;
    }

    /**
     * Sets the value of the numberOfPassagesPerNumOfStudents property.
     * 
     */
    public void setNumberOfPassagesPerNumOfStudents(double value) {
        this.numberOfPassagesPerNumOfStudents = value;
    }

    /**
     * Gets the value of the numberOfReducedPriceMealsPerNumOfStaff property.
     * 
     */
    public double getNumberOfReducedPriceMealsPerNumOfStaff() {
        return numberOfReducedPriceMealsPerNumOfStaff;
    }

    /**
     * Sets the value of the numberOfReducedPriceMealsPerNumOfStaff property.
     * 
     */
    public void setNumberOfReducedPriceMealsPerNumOfStaff(double value) {
        this.numberOfReducedPriceMealsPerNumOfStaff = value;
    }

    /**
     * Gets the value of the numberOfReducedPriceMealsPerNumOfStudents property.
     * 
     */
    public double getNumberOfReducedPriceMealsPerNumOfStudents() {
        return numberOfReducedPriceMealsPerNumOfStudents;
    }

    /**
     * Sets the value of the numberOfReducedPriceMealsPerNumOfStudents property.
     * 
     */
    public void setNumberOfReducedPriceMealsPerNumOfStudents(double value) {
        this.numberOfReducedPriceMealsPerNumOfStudents = value;
    }

}
