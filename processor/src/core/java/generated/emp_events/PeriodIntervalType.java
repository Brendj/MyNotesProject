
package generated.emp_events;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PeriodInterval_Type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PeriodInterval_Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MINUTE"/>
 *     &lt;enumeration value="HOUR"/>
 *     &lt;enumeration value="DAY"/>
 *     &lt;enumeration value="WEEK"/>
 *     &lt;enumeration value="QUARTER"/>
 *     &lt;enumeration value="YEAR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PeriodInterval_Type")
@XmlEnum
public enum PeriodIntervalType {

    MINUTE,
    HOUR,
    DAY,
    WEEK,
    QUARTER,
    YEAR;

    public String value() {
        return name();
    }

    public static PeriodIntervalType fromValue(String v) {
        return valueOf(v);
    }

}
