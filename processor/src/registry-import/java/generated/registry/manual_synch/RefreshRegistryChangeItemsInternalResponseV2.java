/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package generated.registry.manual_synch;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for refreshRegistryChangeItemsInternalResponseV2 complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="refreshRegistryChangeItemsInternalResponseV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://ru.axetta.ecafe}registryChangeItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "refreshRegistryChangeItemsInternalResponseV2", propOrder = {
        "_return"})
public class RefreshRegistryChangeItemsInternalResponseV2 {

    @XmlElement(name = "return")
    protected List<RegistryChangeItemV2> _return;

    /**
     * Gets the value of the return property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the return property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReturn().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistryChangeItem }
     */
    public List<RegistryChangeItemV2> getReturn() {
        if (_return == null) {
            _return = new ArrayList<RegistryChangeItemV2>();
        }
        return this._return;
    }
}
