
package generated.ru.mos.rnip.xsd.organization._2_1;

import javax.xml.bind.annotation.*;


/**
 * Данные организации
 * 
 * <p>Java class for OrganizationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OrganizationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{http://rnip.mos.ru/xsd/Organization/2.1.1}OrgNameType" />
 *       &lt;attribute name="inn" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}INNType" />
 *       &lt;attribute name="kpp" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}KPPType" />
 *       &lt;attribute name="ogrn" type="{http://rnip.mos.ru/xsd/Common/2.1.1}OGRNType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganizationType")
@XmlSeeAlso({
    generated.ru.mos.rnip.xsd.catalog._2_1.ServiceType.Payee.class,
    RefundPayer.class,
    generated.ru.mos.rnip.xsd.organization._2_1.Payee.class
})
public class OrganizationType {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "inn", required = true)
    protected String inn;
    @XmlAttribute(name = "kpp", required = true)
    protected String kpp;
    @XmlAttribute(name = "ogrn")
    protected String ogrn;

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

    /**
     * Gets the value of the ogrn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOgrn() {
        return ogrn;
    }

    /**
     * Sets the value of the ogrn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOgrn(String value) {
        this.ogrn = value;
    }

}
