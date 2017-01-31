/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package generated.nsiws_delta;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Action.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Action">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADDED"/>
 *     &lt;enumeration value="MODIFIED"/>
 *     &lt;enumeration value="DELETED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Action", namespace = "http://rstyle.com/nsi/delta")
@XmlEnum
public enum Action {

    ADDED,
    MODIFIED,
    DELETED;

    public String value() {
        return name();
    }

    public static Action fromValue(String v) {
        return valueOf(v);
    }

}
