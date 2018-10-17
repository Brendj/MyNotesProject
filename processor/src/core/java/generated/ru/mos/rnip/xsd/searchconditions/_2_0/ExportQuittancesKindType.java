
package generated.ru.mos.rnip.xsd.searchconditions._2_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExportQuittancesKindType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExportQuittancesKindType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="QUITTANCE"/>
 *     &lt;enumeration value="ALLQUITTANCE"/>
 *     &lt;enumeration value="QUITTANCEMAINCHARGE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ExportQuittancesKindType")
@XmlEnum
public enum ExportQuittancesKindType {

    QUITTANCE,
    ALLQUITTANCE,
    QUITTANCEMAINCHARGE;

    public String value() {
        return name();
    }

    public static ExportQuittancesKindType fromValue(String v) {
        return valueOf(v);
    }

}
