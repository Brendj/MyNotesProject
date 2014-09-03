
package generated.emp_storage;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indexFlags.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="indexFlags">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PK"/>
 *     &lt;enumeration value="UNIQUE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "indexFlags", namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd")
@XmlEnum
public enum IndexFlags {

    PK,
    UNIQUE;

    public String value() {
        return name();
    }

    public static IndexFlags fromValue(String v) {
        return valueOf(v);
    }

}
