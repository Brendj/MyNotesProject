
package generated.etp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CitizenshipType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CitizenshipType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RF"/>
 *     &lt;enumeration value="Foreign"/>
 *     &lt;enumeration value="None"/>
 *     &lt;enumeration value="Both"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CitizenshipType")
@XmlEnum
public enum CitizenshipType {


    RF("RF"),

    @XmlEnumValue("Foreign")
    FOREIGN("Foreign"),

    @XmlEnumValue("None")
    NONE("None"),

    @XmlEnumValue("Both")
    BOTH("Both");
    private final String value;

    CitizenshipType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CitizenshipType fromValue(String v) {
        for (CitizenshipType c: CitizenshipType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
