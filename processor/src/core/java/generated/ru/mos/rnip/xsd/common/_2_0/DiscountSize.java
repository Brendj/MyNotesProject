
package generated.ru.mos.rnip.xsd.common._2_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.0.1}DiscountType">
 *       &lt;sequence>
 *         &lt;element name="Value">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.0.1}DiscountValueType">
 *               &lt;pattern value="([1-9])|(\d{2})"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="Expiry" type="{http://rnip.mos.ru/xsd/Common/2.0.1}DiscountDateType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class DiscountSize
    extends DiscountType
{


}
