
package generated.nfp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrganizationType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OrganizationType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="INTERNAL"/>
 *     &lt;enumeration value="GOV"/>
 *     &lt;enumeration value="FED"/>
 *     &lt;enumeration value="REG"/>
 *     &lt;enumeration value="BANK"/>
 *     &lt;enumeration value="MED"/>
 *     &lt;enumeration value="SCHOOL"/>
 *     &lt;enumeration value="TRANSPORT"/>
 *     &lt;enumeration value="PERSONAL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OrganizationType")
@XmlEnum
public enum OrganizationType {

    INTERNAL,
    GOV,
    FED,
    REG,
    BANK,
    MED,
    SCHOOL,
    TRANSPORT,
    PERSONAL;

    public String value() {
        return name();
    }

    public static OrganizationType fromValue(String v) {
        return valueOf(v);
    }

}
