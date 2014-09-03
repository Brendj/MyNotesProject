
package generated.emp_storage;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for attributeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="attributeType">
 *   &lt;restriction base="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}systemName">
 *     &lt;enumeration value="integer"/>
 *     &lt;enumeration value="double"/>
 *     &lt;enumeration value="string"/>
 *     &lt;enumeration value="boolean"/>
 *     &lt;enumeration value="binary"/>
 *     &lt;enumeration value="datetime"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "attributeType", namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd")
@XmlEnum
public enum AttributeType {

    @XmlEnumValue("integer")
    INTEGER("integer"),
    @XmlEnumValue("double")
    DOUBLE("double"),
    @XmlEnumValue("string")
    STRING("string"),
    @XmlEnumValue("boolean")
    BOOLEAN("boolean"),
    @XmlEnumValue("binary")
    BINARY("binary"),
    @XmlEnumValue("datetime")
    DATETIME("datetime");
    private final String value;

    AttributeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AttributeType fromValue(String v) {
        for (AttributeType c: AttributeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
