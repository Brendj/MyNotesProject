
package generated.etp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TaskResultType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TaskResultType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PositiveAnswer"/>
 *     &lt;enumeration value="NegativeAnswer"/>
 *     &lt;enumeration value="Error"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TaskResultType")
@XmlEnum
public enum TaskResultType {


    @XmlEnumValue("PositiveAnswer")
    POSITIVE_ANSWER("PositiveAnswer"),

    @XmlEnumValue("NegativeAnswer")
    NEGATIVE_ANSWER("NegativeAnswer"),

    @XmlEnumValue("Error")
    ERROR("Error");
    private final String value;

    TaskResultType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TaskResultType fromValue(String v) {
        for (TaskResultType c: TaskResultType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
