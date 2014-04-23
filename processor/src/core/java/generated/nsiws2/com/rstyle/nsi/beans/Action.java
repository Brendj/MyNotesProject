
package generated.nsiws2.com.rstyle.nsi.beans;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Action.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Action">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SET"/>
 *     &lt;enumeration value="DELETE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Action")
@XmlEnum
public enum Action {

    SET,
    DELETE;

    public String value() {
        return name();
    }

    public static Action fromValue(String v) {
        return valueOf(v);
    }

}
