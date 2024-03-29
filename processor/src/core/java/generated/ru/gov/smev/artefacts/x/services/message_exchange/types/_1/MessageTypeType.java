
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MessageTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="REQUEST"/>
 *     &lt;enumeration value="BROADCAST"/>
 *     &lt;enumeration value="RESPONSE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MessageTypeType")
@XmlEnum
public enum MessageTypeType {

    REQUEST,
    BROADCAST,
    RESPONSE;

    public String value() {
        return name();
    }

    public static MessageTypeType fromValue(String v) {
        return valueOf(v);
    }

}
