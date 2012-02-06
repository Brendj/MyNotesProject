
package generated.nfp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValidationSchemeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ValidationSchemeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RANDOM_SIGN"/>
 *     &lt;enumeration value="OTP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ValidationSchemeType")
@XmlEnum
public enum ValidationSchemeType {

    RANDOM_SIGN,
    OTP;

    public String value() {
        return name();
    }

    public static ValidationSchemeType fromValue(String v) {
        return valueOf(v);
    }

}
