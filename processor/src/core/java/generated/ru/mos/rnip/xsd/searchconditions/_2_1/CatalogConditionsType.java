
package generated.ru.mos.rnip.xsd.searchconditions._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.TimeIntervalType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for CatalogConditionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CatalogConditionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="100" minOccurs="0">
 *           &lt;element name="BeneficiaryWithInnKpp">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;extension base="{http://rnip.mos.ru/xsd/SearchConditions/2.1.0}AbstractBeneficiaryType">
 *                   &lt;attribute name="inn" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.0}INNType" />
 *                   &lt;attribute name="kpp" type="{http://rnip.mos.ru/xsd/Common/2.1.0}KPPType" />
 *                 &lt;/extension>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *           &lt;element name="BeneficiaryWithPayeeId">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;extension base="{http://rnip.mos.ru/xsd/SearchConditions/2.1.0}AbstractBeneficiaryType">
 *                   &lt;attribute name="payeeId" use="required" type="{http://rnip.mos.ru/xsd/Catalog/2.1.0}PayeeIDType" />
 *                 &lt;/extension>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/choice>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.0}TimeInterval" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="allDateCatalog" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CatalogConditionsType", propOrder = {
    "beneficiaryWithInnKppOrBeneficiaryWithPayeeId",
    "timeInterval"
})
public class CatalogConditionsType {

    @XmlElements({
        @XmlElement(name = "BeneficiaryWithInnKpp", type = CatalogConditionsType.BeneficiaryWithInnKpp.class),
        @XmlElement(name = "BeneficiaryWithPayeeId", type = CatalogConditionsType.BeneficiaryWithPayeeId.class)
    })
    protected List<AbstractBeneficiaryType> beneficiaryWithInnKppOrBeneficiaryWithPayeeId;
    @XmlElement(name = "TimeInterval", namespace = "http://rnip.mos.ru/xsd/Common/2.1.0")
    protected TimeIntervalType timeInterval;
    @XmlAttribute(name = "allDateCatalog")
    protected Boolean allDateCatalog;

    /**
     * Gets the value of the beneficiaryWithInnKppOrBeneficiaryWithPayeeId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the beneficiaryWithInnKppOrBeneficiaryWithPayeeId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBeneficiaryWithInnKppOrBeneficiaryWithPayeeId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CatalogConditionsType.BeneficiaryWithInnKpp }
     * {@link CatalogConditionsType.BeneficiaryWithPayeeId }
     * 
     * 
     */
    public List<AbstractBeneficiaryType> getBeneficiaryWithInnKppOrBeneficiaryWithPayeeId() {
        if (beneficiaryWithInnKppOrBeneficiaryWithPayeeId == null) {
            beneficiaryWithInnKppOrBeneficiaryWithPayeeId = new ArrayList<AbstractBeneficiaryType>();
        }
        return this.beneficiaryWithInnKppOrBeneficiaryWithPayeeId;
    }

    /**
     * Временной интервал, за который запрашивается информация из ГИС ГМП
     * 					
     * 
     * @return
     *     possible object is
     *     {@link TimeIntervalType }
     *     
     */
    public TimeIntervalType getTimeInterval() {
        return timeInterval;
    }

    /**
     * Sets the value of the timeInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeIntervalType }
     *     
     */
    public void setTimeInterval(TimeIntervalType value) {
        this.timeInterval = value;
    }

    /**
     * Gets the value of the allDateCatalog property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllDateCatalog() {
        return allDateCatalog;
    }

    /**
     * Sets the value of the allDateCatalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllDateCatalog(Boolean value) {
        this.allDateCatalog = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://rnip.mos.ru/xsd/SearchConditions/2.1.0}AbstractBeneficiaryType">
     *       &lt;attribute name="inn" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.0}INNType" />
     *       &lt;attribute name="kpp" type="{http://rnip.mos.ru/xsd/Common/2.1.0}KPPType" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BeneficiaryWithInnKpp
        extends AbstractBeneficiaryType
    {

        @XmlAttribute(name = "inn", required = true)
        protected String inn;
        @XmlAttribute(name = "kpp")
        protected String kpp;

        /**
         * Gets the value of the inn property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInn() {
            return inn;
        }

        /**
         * Sets the value of the inn property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInn(String value) {
            this.inn = value;
        }

        /**
         * Gets the value of the kpp property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKpp() {
            return kpp;
        }

        /**
         * Sets the value of the kpp property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKpp(String value) {
            this.kpp = value;
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
     *     &lt;extension base="{http://rnip.mos.ru/xsd/SearchConditions/2.1.0}AbstractBeneficiaryType">
     *       &lt;attribute name="payeeId" use="required" type="{http://rnip.mos.ru/xsd/Catalog/2.1.0}PayeeIDType" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class BeneficiaryWithPayeeId
        extends AbstractBeneficiaryType
    {

        @XmlAttribute(name = "payeeId", required = true)
        protected String payeeId;

        /**
         * Gets the value of the payeeId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPayeeId() {
            return payeeId;
        }

        /**
         * Sets the value of the payeeId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPayeeId(String value) {
            this.payeeId = value;
        }

    }

}
