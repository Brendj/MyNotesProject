
package generated.emp_storage;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for catalogFlags.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="catalogFlags">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PUBLIC"/>
 *     &lt;enumeration value="SHARED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "catalogFlags", namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd")
@XmlEnum
public enum CatalogFlags {

    PUBLIC,
    SHARED;

    public String value() {
        return name();
    }

    public static CatalogFlags fromValue(String v) {
        return valueOf(v);
    }

}
