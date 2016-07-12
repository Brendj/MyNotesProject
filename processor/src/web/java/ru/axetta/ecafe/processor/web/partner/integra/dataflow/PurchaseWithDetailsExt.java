/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Java class for PurchaseWithDetailsExt complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="PurchaseWithDetailsExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="E" type="{}PurchaseWithDetailsElementExt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="Sum" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="SocDiscount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="TrdDiscount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Donation" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ByCash" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ByCard" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="IdOfCard" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PurchaseWithDetailsExt", propOrder = {
        "e"})
public class PurchaseWithDetailsExt {

    @XmlElement(name = "E")
    protected List<PurchaseWithDetailsElementExt> e;
    @XmlAttribute(name = "Time")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar time;
    @XmlAttribute(name = "Sum")
    protected Long sum;
    @XmlAttribute(name = "SocDiscount")
    protected Long socDiscount;
    @XmlAttribute(name = "TrdDiscount")
    protected Long trdDiscount;
    @XmlAttribute(name = "Donation")
    protected Long donation;
    @XmlAttribute(name = "ByCash")
    protected Long byCash;
    @XmlAttribute(name = "ByCard")
    protected Long byCard;
    @XmlAttribute(name = "IdOfCard")
    protected Long idOfCard;
    @XmlAttribute(name = "s")
    protected Integer state;

    @XmlAttribute(name = "LastUpdateDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdateDate;

    /**
     * Gets the value of the e property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the e property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getE().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link PurchaseWithDetailsElementExt }
     */
    public List<PurchaseWithDetailsElementExt> getE() {
        if (e == null) {
            e = new ArrayList<PurchaseWithDetailsElementExt>();
        }
        return this.e;
    }

    /**
     * Gets the value of the time property.
     *
     * @return possible object is
     *         {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getTime() {
        return time;
    }

    /**
     * Sets the value of the time property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setTime(XMLGregorianCalendar value) {
        this.time = value;
    }

    /**
     * Gets the value of the sum property.
     *
     * @return possible object is
     *         {@link Long }
     */
    public Long getSum() {
        return sum;
    }

    /**
     * Sets the value of the sum property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setSum(Long value) {
        this.sum = value;
    }

    /**
     * Gets the value of the socDiscount property.
     *
     * @return possible object is
     *         {@link Long }
     */
    public Long getSocDiscount() {
        return socDiscount;
    }

    /**
     * Sets the value of the socDiscount property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setSocDiscount(Long value) {
        this.socDiscount = value;
    }

    /**
     * Gets the value of the trdDiscount property.
     *
     * @return possible object is
     *         {@link Long }
     */
    public Long getTrdDiscount() {
        return trdDiscount;
    }

    /**
     * Sets the value of the trdDiscount property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setTrdDiscount(Long value) {
        this.trdDiscount = value;
    }

    /**
     * Gets the value of the donation property.
     *
     * @return possible object is
     *         {@link Long }
     */
    public Long getDonation() {
        return donation;
    }

    /**
     * Sets the value of the donation property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setDonation(Long value) {
        this.donation = value;
    }

    /**
     * Gets the value of the byCash property.
     *
     * @return possible object is
     *         {@link Long }
     */
    public Long getByCash() {
        return byCash;
    }

    /**
     * Sets the value of the byCash property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setByCash(Long value) {
        this.byCash = value;
    }

    /**
     * Gets the value of the byCard property.
     *
     * @return possible object is
     *         {@link Long }
     */
    public Long getByCard() {
        return byCard;
    }

    /**
     * Sets the value of the byCard property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setByCard(Long value) {
        this.byCard = value;
    }

    /**
     * Gets the value of the idOfCard property.
     *
     * @return possible object is
     *         {@link Long }
     */
    public Long getIdOfCard() {
        return idOfCard;
    }

    /**
     * Sets the value of the idOfCard property.
     *
     * @param value allowed object is
     *              {@link Long }
     */
    public void setIdOfCard(Long value) {
        this.idOfCard = value;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public XMLGregorianCalendar getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(XMLGregorianCalendar lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }
}
