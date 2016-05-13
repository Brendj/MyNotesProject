/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for clientsInsideItem complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="clientsInsideItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="idOfClient" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "clientsInsideItem", propOrder = {
        "idOfClient"
})
public class ClientsInsideItem {

    protected long idOfClient;

    /**
     * Gets the value of the idOfClient property.
     *
     */
    public long getIdOfClient() {
        return idOfClient;
    }

    /**
     * Sets the value of the idOfClient property.
     *
     */
    public void setIdOfClient(long idOfClient) {
        this.idOfClient = idOfClient;
    }
}
