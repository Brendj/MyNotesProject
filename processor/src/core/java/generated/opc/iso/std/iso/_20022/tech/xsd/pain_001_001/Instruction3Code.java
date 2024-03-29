
package generated.opc.iso.std.iso._20022.tech.xsd.pain_001_001;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Instruction3Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Instruction3Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CHQB"/>
 *     &lt;enumeration value="HOLD"/>
 *     &lt;enumeration value="PHOB"/>
 *     &lt;enumeration value="TELB"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Instruction3Code")
@XmlEnum
public enum Instruction3Code {

    CHQB,
    HOLD,
    PHOB,
    TELB;

    public String value() {
        return name();
    }

    public static Instruction3Code fromValue(String v) {
        return valueOf(v);
    }

}
