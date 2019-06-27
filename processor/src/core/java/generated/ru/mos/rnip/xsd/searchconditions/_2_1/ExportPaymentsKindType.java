
package generated.ru.mos.rnip.xsd.searchconditions._2_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExportPaymentsKindType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExportPaymentsKindType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PAYMENT"/>
 *     &lt;enumeration value="PAYMENTMODIFIED"/>
 *     &lt;enumeration value="PAYMENTUNMATCHED"/>
 *     &lt;enumeration value="PAYMENTCANCELLED"/>
 *     &lt;enumeration value="PAYMENTMAINCHARGE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExportPaymentsKindType")
@XmlEnum
public enum ExportPaymentsKindType {

    PAYMENT,
    PAYMENTMODIFIED,
    PAYMENTUNMATCHED,
    PAYMENTCANCELLED,
    PAYMENTMAINCHARGE;

    public String value() {
        return name();
    }

    public static ExportPaymentsKindType fromValue(String v) {
        return valueOf(v);
    }

}
