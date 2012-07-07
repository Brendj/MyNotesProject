
package ru.axetta.ecafe.processor.web.partner.integra.dataflowtest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for classStudentListResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="classStudentListResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="classStudentList" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ClassStudentList" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "classStudentListResult", propOrder = {
    "classStudentList"
})
public class ClassStudentListResult {

    protected ClassStudentList classStudentList;

    /**
     * Gets the value of the classStudentList property.
     * 
     * @return
     *     possible object is
     *     {@link ClassStudentList }
     *     
     */
    public ClassStudentList getClassStudentList() {
        return classStudentList;
    }

    /**
     * Sets the value of the classStudentList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassStudentList }
     *     
     */
    public void setClassStudentList(ClassStudentList value) {
        this.classStudentList = value;
    }

}
