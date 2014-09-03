
package generated.emp_storage;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indexAttributeSort.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="indexAttributeSort">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ASC"/>
 *     &lt;enumeration value="DESC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "indexAttributeSort", namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd")
@XmlEnum
public enum IndexAttributeSort {

    ASC,
    DESC;

    public String value() {
        return name();
    }

    public static IndexAttributeSort fromValue(String v) {
        return valueOf(v);
    }

}
