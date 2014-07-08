
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package generated.nsiws_delta;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ContainerType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ContainerType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CATALOG"/>
 *     &lt;enumeration value="HIERARCHY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ContainerType", namespace = "http://rstyle.com/nsi/delta")
@XmlEnum
public enum ContainerType {

    CATALOG,
    HIERARCHY;

    public String value() {
        return name();
    }

    public static ContainerType fromValue(String v) {
        return valueOf(v);
    }

}
