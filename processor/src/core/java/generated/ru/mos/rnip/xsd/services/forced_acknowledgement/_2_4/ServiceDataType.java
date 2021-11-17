/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package generated.ru.mos.rnip.xsd.services.forced_acknowledgement._2_4;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;

/**
 * ƒополнительные сведени¤ о предоставлении услуги
 *
 * <p>Java class for ServiceDataType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServiceDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="personeOfficial">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="name" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="100"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="officialPosition" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="300"/>
 *                       &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="courtName" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="400"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="lawsuitInfo" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="50"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
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
@XmlType(name = "ServiceDataType", propOrder = {
        "personeOfficial"
})

public class ServiceDataType {

    @XmlElement(required = true)
    protected ServiceDataType.PersoneOfficial personeOfficial;
    @XmlAttribute(name = "amount", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger amount;
    @XmlAttribute(name = "courtName", required = true)
    protected String courtName;
    @XmlAttribute(name = "lawsuitInfo", required = true)
    protected String lawsuitInfo;

    /**
     * Gets the value of the personeOfficial property.
     *
     * @return
     *     possible object is
     *     {@link ServiceDataType.PersoneOfficial }
     *
     */
    public ServiceDataType.PersoneOfficial getPersoneOfficial() {
        return personeOfficial;
    }

    /**
     * Sets the value of the personeOfficial property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceDataType.PersoneOfficial }
     *
     */
    public void setPersoneOfficial(ServiceDataType.PersoneOfficial value) {
        this.personeOfficial = value;
    }

    /**
     * Gets the value of the amount property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setAmount(BigInteger value) {
        this.amount = value;
    }

    /**
     * Gets the value of the courtName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCourtName() {
        return courtName;
    }

    /**
     * Sets the value of the courtName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCourtName(String value) {
        this.courtName = value;
    }

    /**
     * Gets the value of the lawsuitInfo property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLawsuitInfo() {
        return lawsuitInfo;
    }

    /**
     * Sets the value of the lawsuitInfo property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLawsuitInfo(String value) {
        this.lawsuitInfo = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="name" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="100"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="officialPosition" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="300"/>
     *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
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
    @XmlType(name = "")
    public static class PersoneOfficial {

        @XmlAttribute(name = "name", required = true)
        protected String name;
        @XmlAttribute(name = "officialPosition", required = true)
        protected String officialPosition;

        /**
         * Gets the value of the name property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the officialPosition property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOfficialPosition() {
            return officialPosition;
        }

        /**
         * Sets the value of the officialPosition property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOfficialPosition(String value) {
            this.officialPosition = value;
        }

    }

}
