/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for refreshRegistryChangeItems complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="refreshRegistryChangeItemsV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idOfOrg" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "refreshRegistryChangeItemsV2", propOrder = {
        "idOfOrg"
})
public class RefreshRegistryChangeItemsV2 {
    protected long idOfOrg;

    /**
     * Gets the value of the idOfOrg property.
     *
     */
    public long getIdOfOrg() {
        return idOfOrg;
    }

    /**
     * Sets the value of the idOfOrg property.
     *
     */
    public void setIdOfOrg(long value) {
        this.idOfOrg = value;
    }
}
