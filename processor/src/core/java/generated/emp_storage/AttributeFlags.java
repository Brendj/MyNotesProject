
package generated.emp_storage;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for attributeFlags.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="attributeFlags">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NULL"/>
 *     &lt;enumeration value="ARRAY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "attributeFlags", namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd")
@XmlEnum
public enum AttributeFlags {

    NULL,
    ARRAY;

    public String value() {
        return name();
    }

    public static AttributeFlags fromValue(String v) {
        return valueOf(v);
    }

}
