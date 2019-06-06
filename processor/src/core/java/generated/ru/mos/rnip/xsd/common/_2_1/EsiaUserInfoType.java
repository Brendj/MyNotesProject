
package generated.ru.mos.rnip.xsd.common._2_1;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;


/**
 * <p>Java class for EsiaUserInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EsiaUserInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Person">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="DocumentIdentity">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="code" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="1"/>
 *                                 &lt;enumeration value="2"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="series">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;maxLength value="45"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="number" use="required">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;maxLength value="45"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="snils" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;pattern value="\d{11}"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="personINN" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PersonINNType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="IndividualBusiness">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="personINN" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.1.1}PersonINNType">
 *                       &lt;pattern value="\d{12}"/>
 *                       &lt;length value="12"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attribute name="userId" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="sessionIndex">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="300"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="sessionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EsiaUserInfoType", propOrder = {
    "person",
    "individualBusiness"
})
public class EsiaUserInfoType {

    @XmlElement(name = "Person")
    protected EsiaUserInfoType.Person person;
    @XmlElement(name = "IndividualBusiness")
    protected EsiaUserInfoType.IndividualBusiness individualBusiness;
    @XmlAttribute(name = "userId", required = true)
    protected BigInteger userId;
    @XmlAttribute(name = "sessionIndex")
    protected String sessionIndex;
    @XmlAttribute(name = "sessionDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sessionDate;

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link EsiaUserInfoType.Person }
     *     
     */
    public EsiaUserInfoType.Person getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link EsiaUserInfoType.Person }
     *     
     */
    public void setPerson(EsiaUserInfoType.Person value) {
        this.person = value;
    }

    /**
     * Gets the value of the individualBusiness property.
     * 
     * @return
     *     possible object is
     *     {@link EsiaUserInfoType.IndividualBusiness }
     *     
     */
    public EsiaUserInfoType.IndividualBusiness getIndividualBusiness() {
        return individualBusiness;
    }

    /**
     * Sets the value of the individualBusiness property.
     * 
     * @param value
     *     allowed object is
     *     {@link EsiaUserInfoType.IndividualBusiness }
     *     
     */
    public void setIndividualBusiness(EsiaUserInfoType.IndividualBusiness value) {
        this.individualBusiness = value;
    }

    /**
     * Gets the value of the userId property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUserId() {
        return userId;
    }

    /**
     * Sets the value of the userId property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUserId(BigInteger value) {
        this.userId = value;
    }

    /**
     * Gets the value of the sessionIndex property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionIndex() {
        return sessionIndex;
    }

    /**
     * Sets the value of the sessionIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionIndex(String value) {
        this.sessionIndex = value;
    }

    /**
     * Gets the value of the sessionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSessionDate() {
        return sessionDate;
    }

    /**
     * Sets the value of the sessionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSessionDate(XMLGregorianCalendar value) {
        this.sessionDate = value;
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
     *       &lt;attribute name="personINN" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.1.1}PersonINNType">
     *             &lt;pattern value="\d{12}"/>
     *             &lt;length value="12"/>
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
    public static class IndividualBusiness {

        @XmlAttribute(name = "personINN", required = true)
        protected String personINN;

        /**
         * Gets the value of the personINN property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPersonINN() {
            return personINN;
        }

        /**
         * Sets the value of the personINN property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPersonINN(String value) {
            this.personINN = value;
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
     *       &lt;sequence>
     *         &lt;element name="DocumentIdentity">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="code" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="1"/>
     *                       &lt;enumeration value="2"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="series">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;maxLength value="45"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="number" use="required">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;maxLength value="45"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="snils" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;pattern value="\d{11}"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="personINN" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PersonINNType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "documentIdentity"
    })
    public static class Person {

        @XmlElement(name = "DocumentIdentity", required = true)
        protected EsiaUserInfoType.Person.DocumentIdentity documentIdentity;
        @XmlAttribute(name = "snils", required = true)
        protected String snils;
        @XmlAttribute(name = "personINN")
        protected String personINN;

        /**
         * Gets the value of the documentIdentity property.
         * 
         * @return
         *     possible object is
         *     {@link EsiaUserInfoType.Person.DocumentIdentity }
         *     
         */
        public EsiaUserInfoType.Person.DocumentIdentity getDocumentIdentity() {
            return documentIdentity;
        }

        /**
         * Sets the value of the documentIdentity property.
         * 
         * @param value
         *     allowed object is
         *     {@link EsiaUserInfoType.Person.DocumentIdentity }
         *     
         */
        public void setDocumentIdentity(EsiaUserInfoType.Person.DocumentIdentity value) {
            this.documentIdentity = value;
        }

        /**
         * Gets the value of the snils property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSnils() {
            return snils;
        }

        /**
         * Sets the value of the snils property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSnils(String value) {
            this.snils = value;
        }

        /**
         * Gets the value of the personINN property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPersonINN() {
            return personINN;
        }

        /**
         * Sets the value of the personINN property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPersonINN(String value) {
            this.personINN = value;
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
         *       &lt;attribute name="code" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="1"/>
         *             &lt;enumeration value="2"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="series">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;maxLength value="45"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="number" use="required">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;maxLength value="45"/>
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
        public static class DocumentIdentity {

            @XmlAttribute(name = "code", required = true)
            protected String code;
            @XmlAttribute(name = "series")
            protected String series;
            @XmlAttribute(name = "number", required = true)
            protected String number;

            /**
             * Gets the value of the code property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * Sets the value of the code property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCode(String value) {
                this.code = value;
            }

            /**
             * Gets the value of the series property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSeries() {
                return series;
            }

            /**
             * Sets the value of the series property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSeries(String value) {
                this.series = value;
            }

            /**
             * Gets the value of the number property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNumber() {
                return number;
            }

            /**
             * Sets the value of the number property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNumber(String value) {
                this.number = value;
            }

        }

    }

}
