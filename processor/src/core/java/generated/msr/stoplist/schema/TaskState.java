
package generated.msr.stoplist.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for taskState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="taskState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ACCEPTED"/>
 *     &lt;enumeration value="REJECTED"/>
 *     &lt;enumeration value="EXECUTING"/>
 *     &lt;enumeration value="DONE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "taskState")
@XmlEnum
public enum TaskState {

    ACCEPTED,
    REJECTED,
    EXECUTING,
    DONE;

    public String value() {
        return name();
    }

    public static TaskState fromValue(String v) {
        return valueOf(v);
    }

}
