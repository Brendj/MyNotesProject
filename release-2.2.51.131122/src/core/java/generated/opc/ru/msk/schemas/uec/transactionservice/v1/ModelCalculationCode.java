
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ModelCalculationCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ModelCalculationCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FC"/>
 *     &lt;enumeration value="S"/>
 *     &lt;enumeration value="VC1"/>
 *     &lt;enumeration value="FTA"/>
 *     &lt;enumeration value="M1"/>
 *     &lt;enumeration value="M2"/>
 *     &lt;enumeration value="M3"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ModelCalculationCode")
@XmlEnum
public enum ModelCalculationCode {

    FC("FC"),
    S("S"),
    @XmlEnumValue("VC1")
    VC_1("VC1"),
    FTA("FTA"),
    @XmlEnumValue("M1")
    M_1("M1"),
    @XmlEnumValue("M2")
    M_2("M2"),
    @XmlEnumValue("M3")
    M_3("M3");
    private final String value;

    ModelCalculationCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ModelCalculationCode fromValue(String v) {
        for (ModelCalculationCode c: ModelCalculationCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
