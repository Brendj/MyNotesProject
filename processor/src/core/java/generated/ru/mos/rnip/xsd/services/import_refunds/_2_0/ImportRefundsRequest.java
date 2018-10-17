
package generated.ru.mos.rnip.xsd.services.import_refunds._2_0;

import generated.ru.mos.rnip.xsd._package._2_0.RefundsPackage;
import generated.ru.mos.rnip.xsd.common._2_0.RequestType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.0.1}RequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Package/2.0.1}RefundsPackage"/>
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
    "refundsPackage"
})
@XmlRootElement(name = "ImportRefundsRequest")
public class ImportRefundsRequest
    extends RequestType
{

    @XmlElement(name = "RefundsPackage", namespace = "http://rnip.mos.ru/xsd/Package/2.0.1", required = true)
    protected RefundsPackage refundsPackage;

    /**
     * ����� ���������� ������������� ��������
     * 
     * @return
     *     possible object is
     *     {@link RefundsPackage }
     *     
     */
    public RefundsPackage getRefundsPackage() {
        return refundsPackage;
    }

    /**
     * Sets the value of the refundsPackage property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefundsPackage }
     *     
     */
    public void setRefundsPackage(RefundsPackage value) {
        this.refundsPackage = value;
    }

}
