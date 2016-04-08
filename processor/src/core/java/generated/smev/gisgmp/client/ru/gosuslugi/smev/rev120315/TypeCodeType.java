
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TypeCodeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TypeCodeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GSRV"/>
 *     &lt;enumeration value="GFNC"/>
 *     &lt;enumeration value="OTHR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TypeCodeType")
@XmlEnum
public enum TypeCodeType {


    /**
     * �������������� � ������ �������� ��������������� �����
     * 					
     * 
     */
    GSRV,

    /**
     * �������������� � ������ ���������� ��������������� �������
     * 					
     * 
     */
    GFNC,

    /**
     *  �������������� � ���� �����, ���������������
     * 						����������������� ���������� ��������� 
     * 
     */
    OTHR;

    public String value() {
        return name();
    }

    public static TypeCodeType fromValue(String v) {
        return valueOf(v);
    }

}
