
package generated.ru.mos.rnip.xsd.searchconditions._2_4;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExportChargesKindType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ExportChargesKindType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CHARGE"/>
 *     &lt;enumeration value="CHARGENOTFULLMATCHED"/>
 *     &lt;enumeration value="CHARGESTATUS"/>
 *     &lt;enumeration value="CHARGE-PRIOR"/>
 *     &lt;enumeration value="CHARGE-PRIOR-NOTFULLMATCHED"/>
 *     &lt;enumeration value="CHARGE-PRIOR-STATUS"/>
 *     &lt;enumeration value="TEMP-CHARGING"/>
 *     &lt;enumeration value="TEMP-CHARGING-NOTFULLMATCHED"/>
 *     &lt;enumeration value="TEMP-CHARGING-STATUS"/>
 *     &lt;enumeration value="MAINCHARGE"/>
 *     &lt;enumeration value="CHARGE-LIST-FULL"/>
 *     &lt;enumeration value="CHARGE-OFFENSE"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ExportChargesKindType", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.4.0")
@XmlEnum
public enum ExportChargesKindType {

    CHARGE("CHARGE"),
    CHARGENOTFULLMATCHED("CHARGENOTFULLMATCHED"),
    CHARGESTATUS("CHARGESTATUS"),
    @XmlEnumValue("CHARGE-PRIOR")
    CHARGE_PRIOR("CHARGE-PRIOR"),
    @XmlEnumValue("CHARGE-PRIOR-NOTFULLMATCHED")
    CHARGE_PRIOR_NOTFULLMATCHED("CHARGE-PRIOR-NOTFULLMATCHED"),
    @XmlEnumValue("CHARGE-PRIOR-STATUS")
    CHARGE_PRIOR_STATUS("CHARGE-PRIOR-STATUS"),
    @XmlEnumValue("TEMP-CHARGING")
    TEMP_CHARGING("TEMP-CHARGING"),
    @XmlEnumValue("TEMP-CHARGING-NOTFULLMATCHED")
    TEMP_CHARGING_NOTFULLMATCHED("TEMP-CHARGING-NOTFULLMATCHED"),
    @XmlEnumValue("TEMP-CHARGING-STATUS")
    TEMP_CHARGING_STATUS("TEMP-CHARGING-STATUS"),
    MAINCHARGE("MAINCHARGE"),
    @XmlEnumValue("CHARGE-LIST-FULL")
    CHARGE_LIST_FULL("CHARGE-LIST-FULL"),
    @XmlEnumValue("CHARGE-OFFENSE")
    CHARGE_OFFENSE("CHARGE-OFFENSE");
    private final String value;

    ExportChargesKindType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ExportChargesKindType fromValue(String v) {
        for (ExportChargesKindType c: ExportChargesKindType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}