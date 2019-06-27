
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
 *         &lt;element name="ChargesConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}ChargesConditionsType"/>
 *         &lt;element name="PayersConditions">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}PayersConditionsType">
 *                 &lt;sequence>
 *                   &lt;sequence>
 *                     &lt;element name="PayerIdentifier" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PayerIdentifierType" maxOccurs="100"/>
 *                   &lt;/sequence>
 *                   &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.1}TimeInterval" minOccurs="0"/>
 *                   &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.1}KBKlist" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PaymentsConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}PaymentsConditionsType"/>
 *         &lt;element name="TimeConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}TimeConditionsType"/>
 *       &lt;/choice>
 *       &lt;attribute name="kind" use="required" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}ExportPaymentsKindType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "PaymentsExportConditions")
public class PaymentsExportConditions
    extends Conditions
{


}
