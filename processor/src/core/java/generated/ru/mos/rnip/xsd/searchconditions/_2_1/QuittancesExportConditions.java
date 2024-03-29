
package generated.ru.mos.rnip.xsd.searchconditions._2_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *     &lt;restriction base="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}Conditions">
 *       &lt;choice>
 *         &lt;element name="ChargesConditions">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}ChargesConditionsType">
 *                 &lt;sequence>
 *                   &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType" maxOccurs="100"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TimeConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}TimeConditionsType"/>
 *       &lt;/choice>
 *       &lt;attribute name="kind" use="required" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}ExportQuittancesKindType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "QuittancesExportConditions")
public class QuittancesExportConditions
    extends Conditions
{


}
