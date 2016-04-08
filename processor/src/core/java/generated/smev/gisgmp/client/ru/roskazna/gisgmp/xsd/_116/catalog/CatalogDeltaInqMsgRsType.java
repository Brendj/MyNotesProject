
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ���, ����������� ��������� � �������� ����� ���������� ��� ��������� ��������
 * 
 * <p>Java class for CatalogDeltaInqMsgRs_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CatalogDeltaInqMsgRs_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Changes" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}ServiceCatalog_Type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CatalogDeltaInqMsgRs_Type", propOrder = {
    "changes"
})
public class CatalogDeltaInqMsgRsType {

    @XmlElement(name = "Changes", required = true, nillable = true)
    protected ServiceCatalogType changes;

    /**
     * Gets the value of the changes property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceCatalogType }
     *     
     */
    public ServiceCatalogType getChanges() {
        return changes;
    }

    /**
     * Sets the value of the changes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceCatalogType }
     *     
     */
    public void setChanges(ServiceCatalogType value) {
        this.changes = value;
    }

}
