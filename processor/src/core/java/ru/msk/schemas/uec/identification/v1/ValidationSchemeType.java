
package ru.msk.schemas.uec.identification.v1;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValidationSchemeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ValidationSchemeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="RANDOM_SIGN"/>
 *     &lt;enumeration value="OTP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ValidationSchemeType")
@XmlEnum
public enum ValidationSchemeType {


    /**
     * Схема валидации карты с использованием случайного числа от ИС УОС
     * 
     */
    RANDOM_SIGN,

    /**
     * Схема валидации с одноразовым паролем и подписью карты случайного числа от ИС УОС (в случае, если указаны данные для запроса подписи)
     * 
     */
    OTP;

    public String value() {
        return name();
    }

    public static ValidationSchemeType fromValue(String v) {
        return valueOf(v);
    }

}
