
package generated.ru.mos.rnip.xsd.services.export_payments._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.ExportRequestType;
import generated.ru.mos.rnip.xsd.searchconditions._2_0.PaymentsExportConditions;

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
 *         &lt;element ref="{http://rnip.mos.ru/xsd/SearchConditions/2.0.1}PaymentsExportConditions"/>
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
    "paymentsExportConditions"
})
@XmlRootElement(name = "ExportPaymentsRequest")
public class ExportPaymentsRequest
    extends ExportRequestType
{

    @XmlElement(name = "PaymentsExportConditions", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.0.1", required = true)
    protected PaymentsExportConditions paymentsExportConditions;

    /**
     * ������� ��� �������������� ���������� �� ������
     * 
     * @return
     *     possible object is
     *     {@link PaymentsExportConditions }
     *     
     */
    public PaymentsExportConditions getPaymentsExportConditions() {
        return paymentsExportConditions;
    }

    /**
     * Sets the value of the paymentsExportConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentsExportConditions }
     *     
     */
    public void setPaymentsExportConditions(PaymentsExportConditions value) {
        this.paymentsExportConditions = value;
    }

}
