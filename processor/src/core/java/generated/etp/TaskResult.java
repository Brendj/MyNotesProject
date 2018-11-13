
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package generated.etp;

import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;


/**
 * ��������� ����� ��������
 * 
 * <p>Java class for TaskResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaskResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ResultType" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}TaskResultType"/>
 *         &lt;element name="ResultCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="XmlView">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax'/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="BinaryView" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfCoordinateFileReference"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskResult", propOrder = {
    "resultType",
    "resultCode",
    "xmlView",
    "binaryView"
})
public class TaskResult {

    @XmlElement(name = "ResultType", required = true)
    protected TaskResultType resultType;
    @XmlElement(name = "ResultCode")
    protected int resultCode;
    @XmlElement(name = "XmlView", required = true, nillable = true)
    protected XmlView xmlView;
    @XmlElement(name = "BinaryView", required = true, nillable = true)
    protected ArrayOfCoordinateFileReference binaryView;

    /**
     * Gets the value of the resultType property.
     * 
     * @return
     *     possible object is
     *     {@link TaskResultType }
     *     
     */
    public TaskResultType getResultType() {
        return resultType;
    }

    /**
     * Sets the value of the resultType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskResultType }
     *     
     */
    public void setResultType(TaskResultType value) {
        this.resultType = value;
    }

    /**
     * Gets the value of the resultCode property.
     * 
     */
    public int getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     */
    public void setResultCode(int value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the xmlView property.
     * 
     * @return
     *     possible object is
     *     {@link XmlView }
     *     
     */
    public XmlView getXmlView() {
        return xmlView;
    }

    /**
     * Sets the value of the xmlView property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlView }
     *     
     */
    public void setXmlView(XmlView value) {
        this.xmlView = value;
    }

    /**
     * Gets the value of the binaryView property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCoordinateFileReference }
     *     
     */
    public ArrayOfCoordinateFileReference getBinaryView() {
        return binaryView;
    }

    /**
     * Sets the value of the binaryView property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCoordinateFileReference }
     *     
     */
    public void setBinaryView(ArrayOfCoordinateFileReference value) {
        this.binaryView = value;
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
     *         &lt;any processContents='lax'/>
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
        "any"
    })
    public static class XmlView {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Gets the value of the any property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Sets the value of the any property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

    }

}
