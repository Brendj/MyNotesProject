
package generated.ru.mos.rnip.xsd.services.export_charges._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.EsiaUserInfoType;
import generated.ru.mos.rnip.xsd.common._2_0.ExportRequestType;
import generated.ru.mos.rnip.xsd.searchconditions._2_0.ChargesExportConditions;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.0.1}ExportRequestType">
 *       &lt;sequence>
 *         &lt;element name="EsiaUserInfo" type="{http://rnip.mos.ru/xsd/Common/2.0.1}EsiaUserInfoType" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/SearchConditions/2.0.1}ChargesExportConditions"/>
 *       &lt;/sequence>
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
    @XmlElement(name = "ChargesExportConditions", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.0.1", required = true)
    protected ChargesExportConditions chargesExportConditions;

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
     * ������� ��� �������������� ����������� ��� ������ ����������
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

}
