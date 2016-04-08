
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata;

import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Catalog">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="Service" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}Service_Type" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="hasMore" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "catalog"
})
public class ExportCatalogResponse {

    @XmlElement(name = "Catalog", namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", required = true)
    protected Catalog catalog;

    /**
     * Gets the value of the catalog property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ExportCatalogResponse.Catalog }
     *     
     */
    public Catalog getCatalog() {
        return catalog;
    }

    /**
     * Sets the value of the catalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ExportCatalogResponse.Catalog }
     *     
     */
    public void setCatalog(Catalog value) {
        this.catalog = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence minOccurs="0">
     *         &lt;element name="Service" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}Service_Type" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *       &lt;attribute name="hasMore" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "service"
    })
    public static class Catalog {

        @XmlElement(name = "Service", namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData")
        protected List<ServiceType> service;
        @XmlAttribute
        protected Boolean hasMore;

        /**
         * Gets the value of the service property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the service property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getService().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ServiceType }
         * 
         * 
         */
        public List<ServiceType> getService() {
            if (service == null) {
                service = new ArrayList<ServiceType>();
            }
            return this.service;
        }

        /**
         * Gets the value of the hasMore property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isHasMore() {
            return hasMore;
        }

        /**
         * Sets the value of the hasMore property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setHasMore(Boolean value) {
            this.hasMore = value;
        }

    }

}
