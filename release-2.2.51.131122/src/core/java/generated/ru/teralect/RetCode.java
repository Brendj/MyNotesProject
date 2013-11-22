
package generated.ru.teralect;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RetCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RetCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RC_OK"/>
 *     &lt;enumeration value="RC_OUTOFRANGE"/>
 *     &lt;enumeration value="RC_AUTHORIZATIONFAILED"/>
 *     &lt;enumeration value="RC_IPBLOCKED"/>
 *     &lt;enumeration value="RC_DELIVERERR"/>
 *     &lt;enumeration value="RC_TRXIDERR"/>
 *     &lt;enumeration value="RC_SRCADRERR"/>
 *     &lt;enumeration value="RC_SENDSMSERR"/>
 *     &lt;enumeration value="RC_SYSERR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RetCode")
@XmlEnum
public enum RetCode {

    RC_OK,
    RC_OUTOFRANGE,
    RC_AUTHORIZATIONFAILED,
    RC_IPBLOCKED,
    RC_DELIVERERR,
    RC_TRXIDERR,
    RC_SRCADRERR,
    RC_SENDSMSERR,
    RC_SYSERR;

    public String value() {
        return name();
    }

    public static RetCode fromValue(String v) {
        return valueOf(v);
    }

}
