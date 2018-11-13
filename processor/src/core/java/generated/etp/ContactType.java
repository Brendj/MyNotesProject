
package generated.etp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContactType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ContactType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Declarant"/>
 *     &lt;enumeration value="Trustee"/>
 *     &lt;enumeration value="LegalRepresentative"/>
 *     &lt;enumeration value="Contact"/>
 *     &lt;enumeration value="Child"/>
 *     &lt;enumeration value="Spouse"/>
 *     &lt;enumeration value="Relative"/>
 *     &lt;enumeration value="Other"/>
 *     &lt;enumeration value="Father"/>
 *     &lt;enumeration value="Mother"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ContactType")
@XmlEnum
public enum ContactType {


    @XmlEnumValue("Declarant")
    DECLARANT("Declarant"),

    @XmlEnumValue("Trustee")
    TRUSTEE("Trustee"),

    @XmlEnumValue("LegalRepresentative")
    LEGAL_REPRESENTATIVE("LegalRepresentative"),

    @XmlEnumValue("Contact")
    CONTACT("Contact"),

    @XmlEnumValue("Child")
    CHILD("Child"),

    @XmlEnumValue("Spouse")
    SPOUSE("Spouse"),

    @XmlEnumValue("Relative")
    RELATIVE("Relative"),

    @XmlEnumValue("Other")
    OTHER("Other"),

    @XmlEnumValue("Father")
    FATHER("Father"),

    @XmlEnumValue("Mother")
    MOTHER("Mother");
    private final String value;

    ContactType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ContactType fromValue(String v) {
        for (ContactType c: ContactType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
