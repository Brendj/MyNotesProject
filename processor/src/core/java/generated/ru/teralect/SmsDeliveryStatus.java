
package generated.ru.teralect;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SmsDeliveryStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SmsDeliveryStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Delivered"/>
 *     &lt;enumeration value="SentToSmsc"/>
 *     &lt;enumeration value="Enqueued"/>
 *     &lt;enumeration value="Expired"/>
 *     &lt;enumeration value="Undeliverable"/>
 *     &lt;enumeration value="Unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SmsDeliveryStatus")
@XmlEnum
public enum SmsDeliveryStatus {

    @XmlEnumValue("Delivered")
    DELIVERED("Delivered"),
    @XmlEnumValue("SentToSmsc")
    SENT_TO_SMSC("SentToSmsc"),
    @XmlEnumValue("Enqueued")
    ENQUEUED("Enqueued"),
    @XmlEnumValue("Expired")
    EXPIRED("Expired"),
    @XmlEnumValue("Undeliverable")
    UNDELIVERABLE("Undeliverable"),
    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown");
    private final String value;

    SmsDeliveryStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SmsDeliveryStatus fromValue(String v) {
        for (SmsDeliveryStatus c: SmsDeliveryStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
