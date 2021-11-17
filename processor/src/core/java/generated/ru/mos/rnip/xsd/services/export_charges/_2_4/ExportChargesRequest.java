
package generated.ru.mos.rnip.xsd.services.export_charges._2_4;

import generated.ru.mos.rnip.xsd.common._2_1.EsiaUserInfoType;
import generated.ru.mos.rnip.xsd.common._2_1.ExportRequestType;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.ChargesExportConditions;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}ExportRequestType">
 *       &lt;sequence>
 *         &lt;element name="EsiaUserInfo" type="{http://rnip.mos.ru/xsd/Common/2.1.1}EsiaUserInfoType" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}ChargesExportConditions"/>
 *       &lt;/sequence>
 *       &lt;attribute name="external" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="includeTaxes">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;length value="1"/>
 *             &lt;enumeration value="0"/>
 *             &lt;enumeration value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "esiaUserInfo",
    "chargesExportConditions"
})
@XmlRootElement(name = "ExportChargesRequest")
public class ExportChargesRequest
    extends ExportRequestType
{

    @XmlElement(name = "EsiaUserInfo")
    protected EsiaUserInfoType esiaUserInfo;
    @XmlElement(name = "ChargesExportConditions", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.1.1", required = true)
    protected ChargesExportConditions chargesExportConditions;
    @XmlAttribute(name = "external")
    protected Boolean external;
    @XmlAttribute(name = "includeTaxes")
    protected String includeTaxes;

    /**
     * Gets the value of the esiaUserInfo property.
     * 
     * @return
     *     possible object is
     *     {@link EsiaUserInfoType }
     *     
     */
    public EsiaUserInfoType getEsiaUserInfo() {
        return esiaUserInfo;
    }

    /**
     * Sets the value of the esiaUserInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link EsiaUserInfoType }
     *     
     */
    public void setEsiaUserInfo(EsiaUserInfoType value) {
        this.esiaUserInfo = value;
    }

    /**
     * Условия для предоставления необходимой для уплаты информации
     * 								
     * 
     * @return
     *     possible object is
     *     {@link ChargesExportConditions }
     *     
     */
    public ChargesExportConditions getChargesExportConditions() {
        return chargesExportConditions;
    }

    /**
     * Sets the value of the chargesExportConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargesExportConditions }
     *     
     */
    public void setChargesExportConditions(ChargesExportConditions value) {
        this.chargesExportConditions = value;
    }

    /**
     * Gets the value of the external property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExternal() {
        return external;
    }

    /**
     * Sets the value of the external property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExternal(Boolean value) {
        this.external = value;
    }

    /**
     * Gets the value of the includeTaxes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncludeTaxes() {
        return includeTaxes;
    }

    /**
     * Sets the value of the includeTaxes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncludeTaxes(String value) {
        this.includeTaxes = value;
    }

}
