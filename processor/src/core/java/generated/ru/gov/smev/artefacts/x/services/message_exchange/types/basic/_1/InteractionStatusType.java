
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InteractionStatusType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="InteractionStatusType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="doesNotExist"/>
 *     &lt;enumeration value="requestIsQueued"/>
 *     &lt;enumeration value="requestIsAcceptedBySmev"/>
 *     &lt;enumeration value="requestIsRejectedBySmev"/>
 *     &lt;enumeration value="underProcessing"/>
 *     &lt;enumeration value="responseIsAcceptedBySmev"/>
 *     &lt;enumeration value="responseIsRejectedBySmev"/>
 *     &lt;enumeration value="cancelled"/>
 *     &lt;enumeration value="messageIsArchived"/>
 *     &lt;enumeration value="messageIsDelivered"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "InteractionStatusType")
@XmlEnum
public enum InteractionStatusType {


    /**
     * ������ � ����� Id �� ������ � �� ����.
     * 
     */
    @XmlEnumValue("doesNotExist")
    DOES_NOT_EXIST("doesNotExist"),

    /**
     * ������ ��������� � ������� �� ����������� ���������.
     * 
     */
    @XmlEnumValue("requestIsQueued")
    REQUEST_IS_QUEUED("requestIsQueued"),

    /**
     * ������ ������������ ����������.
     * 
     */
    @XmlEnumValue("requestIsAcceptedBySmev")
    REQUEST_IS_ACCEPTED_BY_SMEV("requestIsAcceptedBySmev"),

    /**
     * ������ �� ������ ����������� ���������.
     * 
     */
    @XmlEnumValue("requestIsRejectedBySmev")
    REQUEST_IS_REJECTED_BY_SMEV("requestIsRejectedBySmev"),

    /**
     * �������������� ����������� �������.
     * 
     */
    @XmlEnumValue("underProcessing")
    UNDER_PROCESSING("underProcessing"),

    /**
     * ������ �������� ����� ��������� � ������� ����.
     * 
     */
    @XmlEnumValue("responseIsAcceptedBySmev")
    RESPONSE_IS_ACCEPTED_BY_SMEV("responseIsAcceptedBySmev"),

    /**
     * ������ �� ������ ����������� ���������.
     * 
     */
    @XmlEnumValue("responseIsRejectedBySmev")
    RESPONSE_IS_REJECTED_BY_SMEV("responseIsRejectedBySmev"),

    /**
     * ������ ������ ��������.
     * 
     */
    @XmlEnumValue("cancelled")
    CANCELLED("cancelled"),

    /**
     * ��������� ���������� � �����.
     * 
     */
    @XmlEnumValue("messageIsArchived")
    MESSAGE_IS_ARCHIVED("messageIsArchived"),

    /**
     * ��������� �������� �����������.
     * 
     */
    @XmlEnumValue("messageIsDelivered")
    MESSAGE_IS_DELIVERED("messageIsDelivered");
    private final String value;

    InteractionStatusType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static InteractionStatusType fromValue(String v) {
        for (InteractionStatusType c: InteractionStatusType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}