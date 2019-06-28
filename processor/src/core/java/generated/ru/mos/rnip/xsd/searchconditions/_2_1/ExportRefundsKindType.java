
package generated.ru.mos.rnip.xsd.searchconditions._2_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExportRefundsKindType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExportRefundsKindType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REFUND"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExportRefundsKindType")
@XmlEnum
public enum ExportRefundsKindType {

    REFUND;

    public String value() {
        return name();
    }

    public static ExportRefundsKindType fromValue(String v) {
        return valueOf(v);
    }

}
