
package generated.etp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RegType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RegType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Constant"/>
 *     &lt;enumeration value="Temporary"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RegType")
@XmlEnum
public enum RegType {


    @XmlEnumValue("Constant")
    CONSTANT("Constant"),

    @XmlEnumValue("Temporary")
    TEMPORARY("Temporary");
    private final String value;

    RegType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RegType fromValue(String v) {
        for (RegType c: RegType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
