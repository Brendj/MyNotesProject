
package generated.etp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DigestAlghoritm.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DigestAlghoritm">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GOST3411-2001"/>
 *     &lt;enumeration value="GOST3411-2012-256"/>
 *     &lt;enumeration value="GOST3411-2012-512"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DigestAlghoritm")
@XmlEnum
public enum DigestAlghoritm {

    @XmlEnumValue("GOST3411-2001")
    GOST_3411_2001("GOST3411-2001"),
    @XmlEnumValue("GOST3411-2012-256")
    GOST_3411_2012_256("GOST3411-2012-256"),
    @XmlEnumValue("GOST3411-2012-512")
    GOST_3411_2012_512("GOST3411-2012-512");
    private final String value;

    DigestAlghoritm(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DigestAlghoritm fromValue(String v) {
        for (DigestAlghoritm c: DigestAlghoritm.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
