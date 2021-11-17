package generated.ru.mos.rnip.xsd.charge._2_4;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Информация, необходимая для осуществления исполнительного производства
 *
 *
 * <p>Java class for ExecutiveProcedureInfoType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExecutiveProcedureInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.4.0}DeedInfo"/>
 *         &lt;element name="ExecutOrgan">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="organOkogu" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;length value="7"/>
 *                       &lt;pattern value="\d{7}"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="organCode" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="10"/>
 *                       &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="organ" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="1000"/>
 *                       &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="organAdr" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AdrType" />
 *                 &lt;attribute name="organSignCodePost" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="25"/>
 *                       &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="organSign" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;minLength value="1"/>
 *                       &lt;maxLength value="255"/>
 *                       &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="organSignFIO" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}FIOFSSPType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Debtor">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="Person" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="debtorRegPlace">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;minLength value="1"/>
 *                                 &lt;maxLength value="150"/>
 *                                 &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="debtorBirthDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *                           &lt;attribute name="debtorGender" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;length value="7"/>
 *                                 &lt;enumeration value="мужской"/>
 *                                 &lt;enumeration value="женский"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="debtorBirthPlace">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;minLength value="1"/>
 *                                 &lt;maxLength value="100"/>
 *                                 &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="debtorType" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *                       &lt;enumeration value="1"/>
 *                       &lt;enumeration value="2"/>
 *                       &lt;enumeration value="3"/>
 *                       &lt;enumeration value="1700"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="debtorAdr" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AdrType" />
 *                 &lt;attribute name="debtorAdrFakt" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AdrType" />
 *                 &lt;attribute name="debtorCountryCode">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;length value="3"/>
 *                       &lt;pattern value="\d{3}"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="idDeloNo" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="25"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="deloPlace">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="150"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="idDesDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="aktDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="srokPrIsp" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="srokPrIspType" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;length value="1"/>
 *             &lt;enumeration value="1"/>
 *             &lt;enumeration value="2"/>
 *             &lt;enumeration value="3"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="claimerAdr" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AdrType" />
 *       &lt;attribute name="notifFSSPDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExecutiveProcedureInfoType", propOrder = {
        "deedInfo",
        "executOrgan",
        "debtor"
})
public class ExecutiveProcedureInfoType {

    @XmlElement(name = "DeedInfo", required = true)
    protected DeedInfo deedInfo;
    @XmlElement(name = "ExecutOrgan", required = true)
    protected ExecutiveProcedureInfoType.ExecutOrgan executOrgan;
    @XmlElement(name = "Debtor", required = true)
    protected ExecutiveProcedureInfoType.Debtor debtor;
    @XmlAttribute(name = "idDeloNo", required = true)
    protected String idDeloNo;
    @XmlAttribute(name = "deloPlace")
    protected String deloPlace;
    @XmlAttribute(name = "idDesDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar idDesDate;
    @XmlAttribute(name = "aktDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar aktDate;
    @XmlAttribute(name = "srokPrIsp", required = true)
    protected BigInteger srokPrIsp;
    @XmlAttribute(name = "srokPrIspType", required = true)
    protected String srokPrIspType;
    @XmlAttribute(name = "claimerAdr", required = true)
    protected String claimerAdr;
    @XmlAttribute(name = "notifFSSPDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar notifFSSPDate;

    /**
     * Gets the value of the deedInfo property.
     *
     * @return
     *     possible object is
     *     {@link DeedInfo }
     *
     */
    public DeedInfo getDeedInfo() {
        return deedInfo;
    }

    /**
     * Sets the value of the deedInfo property.
     *
     * @param value
     *     allowed object is
     *     {@link DeedInfo }
     *
     */
    public void setDeedInfo(DeedInfo value) {
        this.deedInfo = value;
    }

    /**
     * Gets the value of the executOrgan property.
     *
     * @return
     *     possible object is
     *     {@link ExecutiveProcedureInfoType.ExecutOrgan }
     *
     */
    public ExecutiveProcedureInfoType.ExecutOrgan getExecutOrgan() {
        return executOrgan;
    }

    /**
     * Sets the value of the executOrgan property.
     *
     * @param value
     *     allowed object is
     *     {@link ExecutiveProcedureInfoType.ExecutOrgan }
     *
     */
    public void setExecutOrgan(ExecutiveProcedureInfoType.ExecutOrgan value) {
        this.executOrgan = value;
    }

    /**
     * Gets the value of the debtor property.
     *
     * @return
     *     possible object is
     *     {@link ExecutiveProcedureInfoType.Debtor }
     *
     */
    public ExecutiveProcedureInfoType.Debtor getDebtor() {
        return debtor;
    }

    /**
     * Sets the value of the debtor property.
     *
     * @param value
     *     allowed object is
     *     {@link ExecutiveProcedureInfoType.Debtor }
     *
     */
    public void setDebtor(ExecutiveProcedureInfoType.Debtor value) {
        this.debtor = value;
    }

    /**
     * Gets the value of the idDeloNo property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdDeloNo() {
        return idDeloNo;
    }

    /**
     * Sets the value of the idDeloNo property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdDeloNo(String value) {
        this.idDeloNo = value;
    }

    /**
     * Gets the value of the deloPlace property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDeloPlace() {
        return deloPlace;
    }

    /**
     * Sets the value of the deloPlace property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDeloPlace(String value) {
        this.deloPlace = value;
    }

    /**
     * Gets the value of the idDesDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getIdDesDate() {
        return idDesDate;
    }

    /**
     * Sets the value of the idDesDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setIdDesDate(XMLGregorianCalendar value) {
        this.idDesDate = value;
    }

    /**
     * Gets the value of the aktDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getAktDate() {
        return aktDate;
    }

    /**
     * Sets the value of the aktDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setAktDate(XMLGregorianCalendar value) {
        this.aktDate = value;
    }

    /**
     * Gets the value of the srokPrIsp property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getSrokPrIsp() {
        return srokPrIsp;
    }

    /**
     * Sets the value of the srokPrIsp property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setSrokPrIsp(BigInteger value) {
        this.srokPrIsp = value;
    }

    /**
     * Gets the value of the srokPrIspType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSrokPrIspType() {
        return srokPrIspType;
    }

    /**
     * Sets the value of the srokPrIspType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSrokPrIspType(String value) {
        this.srokPrIspType = value;
    }

    /**
     * Gets the value of the claimerAdr property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getClaimerAdr() {
        return claimerAdr;
    }

    /**
     * Sets the value of the claimerAdr property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setClaimerAdr(String value) {
        this.claimerAdr = value;
    }

    /**
     * Gets the value of the notifFSSPDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getNotifFSSPDate() {
        return notifFSSPDate;
    }

    /**
     * Sets the value of the notifFSSPDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setNotifFSSPDate(XMLGregorianCalendar value) {
        this.notifFSSPDate = value;
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
     *       &lt;sequence minOccurs="0">
     *         &lt;element name="Person" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="debtorRegPlace">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;minLength value="1"/>
     *                       &lt;maxLength value="150"/>
     *                       &lt;pattern value="\S+([\S\s]*\S+)*"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="debtorBirthDate" type="{http://www.w3.org/2001/XMLSchema}date" />
     *                 &lt;attribute name="debtorGender" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;length value="7"/>
     *                       &lt;enumeration value="мужской"/>
     *                       &lt;enumeration value="женский"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="debtorBirthPlace">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;minLength value="1"/>
     *                       &lt;maxLength value="100"/>
     *                       &lt;pattern value="\S+([\S\s]*\S+)*"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="debtorType" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
     *             &lt;enumeration value="1"/>
     *             &lt;enumeration value="2"/>
     *             &lt;enumeration value="3"/>
     *             &lt;enumeration value="1700"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="debtorAdr" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AdrType" />
     *       &lt;attribute name="debtorAdrFakt" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AdrType" />
     *       &lt;attribute name="debtorCountryCode">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;length value="3"/>
     *             &lt;pattern value="\d{3}"/>
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
    @XmlType(name = "", propOrder = {
            "person"
    })
    public static class Debtor {

        @XmlElement(name = "Person")
        protected ExecutiveProcedureInfoType.Debtor.Person person;
        @XmlAttribute(name = "debtorType", required = true)
        protected BigInteger debtorType;
        @XmlAttribute(name = "debtorAdr", required = true)
        protected String debtorAdr;
        @XmlAttribute(name = "debtorAdrFakt")
        protected String debtorAdrFakt;
        @XmlAttribute(name = "debtorCountryCode")
        protected String debtorCountryCode;

        /**
         * Gets the value of the person property.
         *
         * @return
         *     possible object is
         *     {@link ExecutiveProcedureInfoType.Debtor.Person }
         *
         */
        public ExecutiveProcedureInfoType.Debtor.Person getPerson() {
            return person;
        }

        /**
         * Sets the value of the person property.
         *
         * @param value
         *     allowed object is
         *     {@link ExecutiveProcedureInfoType.Debtor.Person }
         *
         */
        public void setPerson(ExecutiveProcedureInfoType.Debtor.Person value) {
            this.person = value;
        }

        /**
         * Gets the value of the debtorType property.
         *
         * @return
         *     possible object is
         *     {@link BigInteger }
         *
         */
        public BigInteger getDebtorType() {
            return debtorType;
        }

        /**
         * Sets the value of the debtorType property.
         *
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *
         */
        public void setDebtorType(BigInteger value) {
            this.debtorType = value;
        }

        /**
         * Gets the value of the debtorAdr property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getDebtorAdr() {
            return debtorAdr;
        }

        /**
         * Sets the value of the debtorAdr property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setDebtorAdr(String value) {
            this.debtorAdr = value;
        }

        /**
         * Gets the value of the debtorAdrFakt property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getDebtorAdrFakt() {
            return debtorAdrFakt;
        }

        /**
         * Sets the value of the debtorAdrFakt property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setDebtorAdrFakt(String value) {
            this.debtorAdrFakt = value;
        }

        /**
         * Gets the value of the debtorCountryCode property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getDebtorCountryCode() {
            return debtorCountryCode;
        }

        /**
         * Sets the value of the debtorCountryCode property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setDebtorCountryCode(String value) {
            this.debtorCountryCode = value;
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
         *       &lt;attribute name="debtorRegPlace">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;minLength value="1"/>
         *             &lt;maxLength value="150"/>
         *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="debtorBirthDate" type="{http://www.w3.org/2001/XMLSchema}date" />
         *       &lt;attribute name="debtorGender" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;length value="7"/>
         *             &lt;enumeration value="мужской"/>
         *             &lt;enumeration value="женский"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="debtorBirthPlace">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;minLength value="1"/>
         *             &lt;maxLength value="100"/>
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
        public static class Person {

            @XmlAttribute(name = "debtorRegPlace")
            protected String debtorRegPlace;
            @XmlAttribute(name = "debtorBirthDate")
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar debtorBirthDate;
            @XmlAttribute(name = "debtorGender", required = true)
            protected String debtorGender;
            @XmlAttribute(name = "debtorBirthPlace")
            protected String debtorBirthPlace;

            /**
             * Gets the value of the debtorRegPlace property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getDebtorRegPlace() {
                return debtorRegPlace;
            }

            /**
             * Sets the value of the debtorRegPlace property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setDebtorRegPlace(String value) {
                this.debtorRegPlace = value;
            }

            /**
             * Gets the value of the debtorBirthDate property.
             *
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *
             */
            public XMLGregorianCalendar getDebtorBirthDate() {
                return debtorBirthDate;
            }

            /**
             * Sets the value of the debtorBirthDate property.
             *
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *
             */
            public void setDebtorBirthDate(XMLGregorianCalendar value) {
                this.debtorBirthDate = value;
            }

            /**
             * Gets the value of the debtorGender property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getDebtorGender() {
                return debtorGender;
            }

            /**
             * Sets the value of the debtorGender property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setDebtorGender(String value) {
                this.debtorGender = value;
            }

            /**
             * Gets the value of the debtorBirthPlace property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getDebtorBirthPlace() {
                return debtorBirthPlace;
            }

            /**
             * Sets the value of the debtorBirthPlace property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setDebtorBirthPlace(String value) {
                this.debtorBirthPlace = value;
            }

        }

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
     *       &lt;attribute name="organOkogu" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;length value="7"/>
     *             &lt;pattern value="\d{7}"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="organCode" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="10"/>
     *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="organ" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="1000"/>
     *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="organAdr" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AdrType" />
     *       &lt;attribute name="organSignCodePost" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="25"/>
     *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="organSign" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;minLength value="1"/>
     *             &lt;maxLength value="255"/>
     *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="organSignFIO" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}FIOFSSPType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ExecutOrgan {

        @XmlAttribute(name = "organOkogu", required = true)
        protected String organOkogu;
        @XmlAttribute(name = "organCode", required = true)
        protected String organCode;
        @XmlAttribute(name = "organ", required = true)
        protected String organ;
        @XmlAttribute(name = "organAdr", required = true)
        protected String organAdr;
        @XmlAttribute(name = "organSignCodePost", required = true)
        protected String organSignCodePost;
        @XmlAttribute(name = "organSign", required = true)
        protected String organSign;
        @XmlAttribute(name = "organSignFIO", required = true)
        protected String organSignFIO;

        /**
         * Gets the value of the organOkogu property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOrganOkogu() {
            return organOkogu;
        }

        /**
         * Sets the value of the organOkogu property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOrganOkogu(String value) {
            this.organOkogu = value;
        }

        /**
         * Gets the value of the organCode property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOrganCode() {
            return organCode;
        }

        /**
         * Sets the value of the organCode property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOrganCode(String value) {
            this.organCode = value;
        }

        /**
         * Gets the value of the organ property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOrgan() {
            return organ;
        }

        /**
         * Sets the value of the organ property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOrgan(String value) {
            this.organ = value;
        }

        /**
         * Gets the value of the organAdr property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOrganAdr() {
            return organAdr;
        }

        /**
         * Sets the value of the organAdr property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOrganAdr(String value) {
            this.organAdr = value;
        }

        /**
         * Gets the value of the organSignCodePost property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOrganSignCodePost() {
            return organSignCodePost;
        }

        /**
         * Sets the value of the organSignCodePost property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOrganSignCodePost(String value) {
            this.organSignCodePost = value;
        }

        /**
         * Gets the value of the organSign property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOrganSign() {
            return organSign;
        }

        /**
         * Sets the value of the organSign property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOrganSign(String value) {
            this.organSign = value;
        }

        /**
         * Gets the value of the organSignFIO property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getOrganSignFIO() {
            return organSignFIO;
        }

        /**
         * Sets the value of the organSignFIO property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setOrganSignFIO(String value) {
            this.organSignFIO = value;
        }

    }

}