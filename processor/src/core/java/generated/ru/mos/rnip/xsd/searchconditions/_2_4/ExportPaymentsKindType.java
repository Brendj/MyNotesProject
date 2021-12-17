
package generated.ru.mos.rnip.xsd.searchconditions._2_4;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
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
 *     &lt;enumeration value="PAYMENT-PART-SERVICE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ExportPaymentsKindType", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.4.0")
@XmlEnum
public enum ExportPaymentsKindType {

    PAYMENT("PAYMENT"),
    PAYMENTMODIFIED("PAYMENTMODIFIED"),
    PAYMENTUNMATCHED("PAYMENTUNMATCHED"),
    PAYMENTCANCELLED("PAYMENTCANCELLED"),
    PAYMENTMAINCHARGE("PAYMENTMAINCHARGE"),
    @XmlEnumValue("PAYMENT-PART-SERVICE")
    PAYMENT_PART_SERVICE("PAYMENT-PART-SERVICE");
    private final String value;

    ExportPaymentsKindType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExportPaymentsKindType fromValue(String v) {
        for (ExportPaymentsKindType c: ExportPaymentsKindType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}