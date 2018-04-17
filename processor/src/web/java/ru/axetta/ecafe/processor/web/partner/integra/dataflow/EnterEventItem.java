package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for EnterEventItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnterEventItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="DateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="Day" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="EnterName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Direction" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="TemporaryCard" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnterEventItem")
public class EnterEventItem {

    @XmlAttribute(name = "DateTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTime;
    @XmlAttribute(name = "Day")
    protected Integer day;
    @XmlAttribute(name = "EnterName")
    protected String enterName;
    @XmlAttribute(name = "Direction")
    protected Integer direction;
    @XmlAttribute(name = "TemporaryCard")
    protected Integer temporaryCard;
    @XmlAttribute(name = "GuardianSan")
    protected String guardianSan;
    @XmlAttribute(name = "PassWithGuardian")
    protected Long passWithGuardian;

    @XmlAttribute(name = "idOfClient")
    protected Long idOfClient;
    @XmlAttribute(name = "IdOfCard")
    protected Long IdOfCard;
    @XmlAttribute(name = "TurnstileAddr")
    protected String TurnstileAddr;
    @XmlAttribute(name = "VisitorFullName")
    protected String VisitorFullName;

    @XmlAttribute(name = "LastUpdateDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdateDate;

    @XmlAttribute(name = "Address")
    protected String address;
    @XmlAttribute(name = "ShortNameInfoService")
    protected String shortNameInfoService;

    /**
     * Gets the value of the dateTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTime() {
        return dateTime;
    }

    /**
     * Sets the value of the dateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTime(XMLGregorianCalendar value) {
        this.dateTime = value;
    }

    /**
     * Gets the value of the day property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDay(Integer value) {
        this.day = value;
    }

    /**
     * Gets the value of the enterName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnterName() {
        return enterName;
    }

    /**
     * Sets the value of the enterName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnterName(String value) {
        this.enterName = value;
    }

    /**
     * Gets the value of the direction property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDirection(Integer value) {
        this.direction = value;
    }

    /**
     * Gets the value of the temporaryCard property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTemporaryCard() {
        return temporaryCard;
    }

    /**
     * Sets the value of the temporaryCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTemporaryCard(Integer value) {
        this.temporaryCard = value;
    }

    public String getGuardianSan() {
        return guardianSan;
    }

    public void setGuardianSan(String guardianSan) {
        this.guardianSan = guardianSan;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfCard() {
        return IdOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        IdOfCard = idOfCard;
    }

    public String getTurnstileAddr() {
        return TurnstileAddr;
    }

    public void setTurnstileAddr(String turnstileAddr) {
        TurnstileAddr = turnstileAddr;
    }

    public String getVisitorFullName() {
        return VisitorFullName;
    }

    public void setVisitorFullName(String visitorFullName) {
        VisitorFullName = visitorFullName;
    }

    public Long getPassWithGuardian() {
        return passWithGuardian;
    }

    public void setPassWithGuardian(Long passWithGuardian) {
        this.passWithGuardian = passWithGuardian;
    }

    public XMLGregorianCalendar getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(XMLGregorianCalendar lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }
}
