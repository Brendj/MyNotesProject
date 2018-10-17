
package generated.ru.mos.rnip.xsd.services.export_quittances._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.ExportRequestType;
import generated.ru.mos.rnip.xsd.searchconditions._2_0.QuittancesExportConditions;

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
 *         &lt;element ref="{http://rnip.mos.ru/xsd/SearchConditions/2.0.1}QuittancesExportConditions"/>
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
    "quittancesExportConditions"
})
@XmlRootElement(name = "ExportQuittancesRequest")
public class ExportQuittancesRequest
    extends ExportRequestType
{

    @XmlElement(name = "QuittancesExportConditions", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.0.1", required = true)
    protected QuittancesExportConditions quittancesExportConditions;

    /**
     * ������� ��� �������������� ���������� � ����������� ������������
     * 								
     * 
     * @return
     *     possible object is
     *     {@link QuittancesExportConditions }
     *     
     */
    public QuittancesExportConditions getQuittancesExportConditions() {
        return quittancesExportConditions;
    }

    /**
     * Sets the value of the quittancesExportConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuittancesExportConditions }
     *     
     */
    public void setQuittancesExportConditions(QuittancesExportConditions value) {
        this.quittancesExportConditions = value;
    }

}
