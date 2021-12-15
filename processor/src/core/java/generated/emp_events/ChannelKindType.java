
package generated.emp_events;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChannelKind_Type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChannelKind_Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="sms"/>
 *     &lt;enumeration value="email"/>
 *     &lt;enumeration value="push"/>
 *     &lt;enumeration value="telegram"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ChannelKind_Type")
@XmlEnum
public enum ChannelKindType {

    @XmlEnumValue("sms")
    SMS("sms"),
    @XmlEnumValue("email")
    EMAIL("email"),
    @XmlEnumValue("push")
    PUSH("push"),
    @XmlEnumValue("telegram")
    TELEGRAM("telegram");
    private final String value;

    ChannelKindType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ChannelKindType fromValue(String v) {
        for (ChannelKindType c: ChannelKindType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
