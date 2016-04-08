
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.ticket;

import generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.errinfo.ResultInfo;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 *  ����������� ���������
 * 
 * <p>Java class for TicketType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TicketType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="RequestProcessResult" type="{http://roskazna.ru/gisgmp/xsd/116/ErrInfo}ResultInfo"/>
 *         &lt;element name="PackageProcessResult">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="EntityProcessResult" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/ErrInfo}ResultInfo">
 *                           &lt;attribute name="entityId" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
 *                         &lt;/extension>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TicketType", propOrder = {
    "requestProcessResult",
    "packageProcessResult"
})
public class TicketType {

    @XmlElement(name = "RequestProcessResult")
    protected ResultInfo requestProcessResult;
    @XmlElement(name = "PackageProcessResult")
    protected PackageProcessResult packageProcessResult;

    /**
     * Gets the value of the requestProcessResult property.
     * 
     * @return
     *     possible object is
     *     {@link ResultInfo }
     *     
     */
    public ResultInfo getRequestProcessResult() {
        return requestProcessResult;
    }

    /**
     * Sets the value of the requestProcessResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultInfo }
     *     
     */
    public void setRequestProcessResult(ResultInfo value) {
        this.requestProcessResult = value;
    }

    /**
     * Gets the value of the packageProcessResult property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.ticket.TicketType.PackageProcessResult }
     *     
     */
    public PackageProcessResult getPackageProcessResult() {
        return packageProcessResult;
    }

    /**
     * Sets the value of the packageProcessResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.ticket.TicketType.PackageProcessResult }
     *     
     */
    public void setPackageProcessResult(PackageProcessResult value) {
        this.packageProcessResult = value;
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
     *       &lt;sequence>
     *         &lt;element name="EntityProcessResult" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/ErrInfo}ResultInfo">
     *                 &lt;attribute name="entityId" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
     *               &lt;/extension>
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
        "entityProcessResult"
    })
    public static class PackageProcessResult {

        @XmlElement(name = "EntityProcessResult", required = true)
        protected List<EntityProcessResult> entityProcessResult;

        /**
         * Gets the value of the entityProcessResult property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entityProcessResult property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntityProcessResult().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.ticket.TicketType.PackageProcessResult.EntityProcessResult }
         * 
         * 
         */
        public List<EntityProcessResult> getEntityProcessResult() {
            if (entityProcessResult == null) {
                entityProcessResult = new ArrayList<EntityProcessResult>();
            }
            return this.entityProcessResult;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/ErrInfo}ResultInfo">
         *       &lt;attribute name="entityId" use="required" type="{http://www.w3.org/2001/XMLSchema}token" />
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class EntityProcessResult
            extends ResultInfo
        {

            @XmlAttribute(required = true)
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            protected String entityId;

            /**
             * Gets the value of the entityId property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEntityId() {
                return entityId;
            }

            /**
             * Sets the value of the entityId property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEntityId(String value) {
                this.entityId = value;
            }

        }

    }

}
