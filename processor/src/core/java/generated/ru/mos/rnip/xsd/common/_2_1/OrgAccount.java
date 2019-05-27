
package generated.ru.mos.rnip.xsd.common._2_1;

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
 *     &lt;restriction base="{http://rnip.mos.ru/xsd/Common/2.1.0}AccountType">
 *       &lt;sequence>
 *         &lt;element name="Bank" type="{http://rnip.mos.ru/xsd/Common/2.1.0}BankType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="accountNumber" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.0}AccountNumType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "OrgAccount")
public class OrgAccount
    extends AccountType
{


}
