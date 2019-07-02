
package generated.ru.mos.rnip.xsd.common._2_1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AddressKindType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AddressKindType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Zip"/>
 *     &lt;enumeration value="Country"/>
 *     &lt;enumeration value="Region"/>
 *     &lt;enumeration value="State"/>
 *     &lt;enumeration value="Town"/>
 *     &lt;enumeration value="City"/>
 *     &lt;enumeration value="Street"/>
 *     &lt;enumeration value="House"/>
 *     &lt;enumeration value="Building"/>
 *     &lt;enumeration value="Flat"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AddressKindType")
@XmlEnum
public enum AddressKindType {

    @XmlEnumValue("Zip")
    ZIP("Zip"),
    @XmlEnumValue("Country")
    COUNTRY("Country"),
    @XmlEnumValue("Region")
    REGION("Region"),
    @XmlEnumValue("State")
    STATE("State"),
    @XmlEnumValue("Town")
    TOWN("Town"),
    @XmlEnumValue("City")
    CITY("City"),
    @XmlEnumValue("Street")
    STREET("Street"),
    @XmlEnumValue("House")
    HOUSE("House"),
    @XmlEnumValue("Building")
    BUILDING("Building"),
    @XmlEnumValue("Flat")
    FLAT("Flat");
    private final String value;

    AddressKindType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AddressKindType fromValue(String v) {
        for (AddressKindType c: AddressKindType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
