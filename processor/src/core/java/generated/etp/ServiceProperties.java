
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="IsLegalRepresentative" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Validity" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="ValidationGuardianship" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="EduName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IDLink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PreferentialCategory">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LargeFamily" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="LowIncomeFamily" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="WithoutParentalCare" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="DisabledChild" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="UnemployedPersons" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="Recipient" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="ChildrenWithDisabilities" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "ServiceProperties", namespace = "http://mos.ru/gu/service/063101/")
public class ServiceProperties {

    @XmlElement(name = "IsLegalRepresentative", namespace = "http://mos.ru/gu/service/063101/")
    protected boolean isLegalRepresentative;
    @XmlElement(name = "Validity", namespace = "http://mos.ru/gu/service/063101/")
    protected Boolean validity;
    @XmlElement(name = "ValidationGuardianship", namespace = "http://mos.ru/gu/service/063101/")
    protected boolean validationGuardianship;
    @XmlElement(name = "EduName", namespace = "http://mos.ru/gu/service/063101/")
    protected String eduName;
    @XmlElement(name = "IDLink", namespace = "http://mos.ru/gu/service/063101/")
    protected String idLink;
    @XmlElement(name = "PreferentialCategory", namespace = "http://mos.ru/gu/service/063101/", required = true)
    protected ServiceProperties.PreferentialCategory preferentialCategory;

    /**
     * Gets the value of the isLegalRepresentative property.
     * 
     */
    public boolean isIsLegalRepresentative() {
        return isLegalRepresentative;
    }

    /**
     * Sets the value of the isLegalRepresentative property.
     * 
     */
    public void setIsLegalRepresentative(boolean value) {
        this.isLegalRepresentative = value;
    }

    /**
     * Gets the value of the validity property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isValidity() {
        return validity;
    }

    /**
     * Sets the value of the validity property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setValidity(Boolean value) {
        this.validity = value;
    }

    /**
     * Gets the value of the validationGuardianship property.
     * 
     */
    public boolean isValidationGuardianship() {
        return validationGuardianship;
    }

    /**
     * Sets the value of the validationGuardianship property.
     * 
     */
    public void setValidationGuardianship(boolean value) {
        this.validationGuardianship = value;
    }

    /**
     * Gets the value of the eduName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEduName() {
        return eduName;
    }

    /**
     * Sets the value of the eduName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEduName(String value) {
        this.eduName = value;
    }

    /**
     * Gets the value of the idLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDLink() {
        return idLink;
    }

    /**
     * Sets the value of the idLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDLink(String value) {
        this.idLink = value;
    }

    /**
     * Gets the value of the preferentialCategory property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceProperties.PreferentialCategory }
     *     
     */
    public ServiceProperties.PreferentialCategory getPreferentialCategory() {
        return preferentialCategory;
    }

    /**
     * Sets the value of the preferentialCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceProperties.PreferentialCategory }
     *     
     */
    public void setPreferentialCategory(ServiceProperties.PreferentialCategory value) {
        this.preferentialCategory = value;
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
     *         &lt;element name="LargeFamily" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="LowIncomeFamily" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="WithoutParentalCare" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="DisabledChild" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="UnemployedPersons" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="Recipient" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="ChildrenWithDisabilities" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "largeFamily",
        "lowIncomeFamily",
        "withoutParentalCare",
        "disabledChild",
        "unemployedPersons",
        "recipient",
        "childrenWithDisabilities"
    })
    public static class PreferentialCategory {

        @XmlElement(name = "LargeFamily", namespace = "http://mos.ru/gu/service/063101/")
        protected Boolean largeFamily;
        @XmlElement(name = "LowIncomeFamily", namespace = "http://mos.ru/gu/service/063101/")
        protected Boolean lowIncomeFamily;
        @XmlElement(name = "WithoutParentalCare", namespace = "http://mos.ru/gu/service/063101/")
        protected Boolean withoutParentalCare;
        @XmlElement(name = "DisabledChild", namespace = "http://mos.ru/gu/service/063101/")
        protected Boolean disabledChild;
        @XmlElement(name = "UnemployedPersons", namespace = "http://mos.ru/gu/service/063101/")
        protected Boolean unemployedPersons;
        @XmlElement(name = "Recipient", namespace = "http://mos.ru/gu/service/063101/")
        protected Boolean recipient;
        @XmlElement(name = "ChildrenWithDisabilities", namespace = "http://mos.ru/gu/service/063101/")
        protected Boolean childrenWithDisabilities;

        /**
         * Gets the value of the largeFamily property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isLargeFamily() {
            return largeFamily;
        }

        /**
         * Sets the value of the largeFamily property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setLargeFamily(Boolean value) {
            this.largeFamily = value;
        }

        /**
         * Gets the value of the lowIncomeFamily property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isLowIncomeFamily() {
            return lowIncomeFamily;
        }

        /**
         * Sets the value of the lowIncomeFamily property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setLowIncomeFamily(Boolean value) {
            this.lowIncomeFamily = value;
        }

        /**
         * Gets the value of the withoutParentalCare property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isWithoutParentalCare() {
            return withoutParentalCare;
        }

        /**
         * Sets the value of the withoutParentalCare property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setWithoutParentalCare(Boolean value) {
            this.withoutParentalCare = value;
        }

        /**
         * Gets the value of the disabledChild property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isDisabledChild() {
            return disabledChild;
        }

        /**
         * Sets the value of the disabledChild property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setDisabledChild(Boolean value) {
            this.disabledChild = value;
        }

        /**
         * Gets the value of the unemployedPersons property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isUnemployedPersons() {
            return unemployedPersons;
        }

        /**
         * Sets the value of the unemployedPersons property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setUnemployedPersons(Boolean value) {
            this.unemployedPersons = value;
        }

        /**
         * Gets the value of the recipient property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isRecipient() {
            return recipient;
        }

        /**
         * Sets the value of the recipient property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setRecipient(Boolean value) {
            this.recipient = value;
        }

        /**
         * Gets the value of the childrenWithDisabilities property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isChildrenWithDisabilities() {
            return childrenWithDisabilities;
        }

        /**
         * Sets the value of the childrenWithDisabilities property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setChildrenWithDisabilities(Boolean value) {
            this.childrenWithDisabilities = value;
        }

    }

}
