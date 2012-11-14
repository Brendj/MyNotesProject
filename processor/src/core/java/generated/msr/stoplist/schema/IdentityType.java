
package generated.msr.stoplist.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for identityType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="identityType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="APAN"/>
 *     &lt;enumeration value="PAN"/>
 *     &lt;enumeration value="CSN"/>
 *     &lt;enumeration value="CIN"/>
 *     &lt;enumeration value="MUID"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "identityType")
@XmlEnum
public enum IdentityType {

    APAN,
    PAN,
    CSN,
    CIN,
    MUID;

    public String value() {
        return name();
    }

    public static IdentityType fromValue(String v) {
        return valueOf(v);
    }

}
