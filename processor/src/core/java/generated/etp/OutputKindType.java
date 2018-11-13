
package generated.etp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OutputKindType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="OutputKindType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Personally"/>
 *     &lt;enumeration value="Telephone"/>
 *     &lt;enumeration value="Portal"/>
 *     &lt;enumeration value="EMail"/>
 *     &lt;enumeration value="Mail"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "OutputKindType")
@XmlEnum
public enum OutputKindType {


    /**
     * ������� �����
     * 
     */
    @XmlEnumValue("Personally")
    PERSONALLY("Personally"),

    /**
     * �������� � ������ ����� �� ��������
     * 
     */
    @XmlEnumValue("Telephone")
    TELEPHONE("Telephone"),

    /**
     * ��������� ����� ������
     * 
     */
    @XmlEnumValue("Portal")
    PORTAL("Portal"),

    /**
     * ��������� �� ����������� �����
     * 
     */
    @XmlEnumValue("EMail")
    E_MAIL("EMail"),

    /**
     * ��������� �� �����
     * 
     */
    @XmlEnumValue("Mail")
    MAIL("Mail");
    private final String value;

    OutputKindType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static OutputKindType fromValue(String v) {
        for (OutputKindType c: OutputKindType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
