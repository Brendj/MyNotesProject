
package generated.etp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;


/**
 * ������ �� ���������
 * 
 * <p>Java class for RequestService complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RegNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RegDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="ServiceNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ServicePrice" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="PrepareTargetDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="OutputTargetDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="Responsible" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Person"/>
 *         &lt;element name="Department" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Department"/>
 *         &lt;element name="DeclineReasons" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfDictionaryItem" minOccurs="0"/>
 *         &lt;element name="CreatedByDepartment" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Department" minOccurs="0"/>
 *         &lt;element name="PrepareFactDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="OutputFactDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="OutputKind" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}OutputKindType"/>
 *         &lt;element name="PortalNum" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ParentServiceNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestService", propOrder = {
    "regNum",
    "regDate",
    "serviceNumber",
    "servicePrice",
    "prepareTargetDate",
    "outputTargetDate",
    "responsible",
    "department",
    "declineReasons",
    "createdByDepartment",
    "prepareFactDate",
    "outputFactDate",
    "outputKind",
    "portalNum",
    "parentServiceNumber"
})
public class RequestService {

    @XmlElement(name = "RegNum")
    protected String regNum;
    @XmlElement(name = "RegDate", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar regDate;
    @XmlElement(name = "ServiceNumber", required = true)
    protected String serviceNumber;
    @XmlElementRef(name = "ServicePrice", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<BigDecimal> servicePrice;
    @XmlElementRef(name = "PrepareTargetDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> prepareTargetDate;
    @XmlElementRef(name = "OutputTargetDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> outputTargetDate;
    @XmlElement(name = "Responsible", required = true, nillable = true)
    protected Person responsible;
    @XmlElement(name = "Department", required = true, nillable = true)
    protected Department department;
    @XmlElement(name = "DeclineReasons")
    protected ArrayOfDictionaryItem declineReasons;
    @XmlElement(name = "CreatedByDepartment")
    protected Department createdByDepartment;
    @XmlElementRef(name = "PrepareFactDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> prepareFactDate;
    @XmlElementRef(name = "OutputFactDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> outputFactDate;
    @XmlElement(name = "OutputKind", required = true, nillable = true)
    protected OutputKindType outputKind;
    @XmlElement(name = "PortalNum")
    protected String portalNum;
    @XmlElement(name = "ParentServiceNumber")
    protected String parentServiceNumber;

    /**
     * Gets the value of the regNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegNum() {
        return regNum;
    }

    /**
     * Sets the value of the regNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegNum(String value) {
        this.regNum = value;
    }

    /**
     * Gets the value of the regDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRegDate() {
        return regDate;
    }

    /**
     * Sets the value of the regDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRegDate(XMLGregorianCalendar value) {
        this.regDate = value;
    }

    /**
     * Gets the value of the serviceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceNumber() {
        return serviceNumber;
    }

    /**
     * Sets the value of the serviceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceNumber(String value) {
        this.serviceNumber = value;
    }

    /**
     * Gets the value of the servicePrice property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public JAXBElement<BigDecimal> getServicePrice() {
        return servicePrice;
    }

    /**
     * Sets the value of the servicePrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}
     *     
     */
    public void setServicePrice(JAXBElement<BigDecimal> value) {
        this.servicePrice = value;
    }

    /**
     * Gets the value of the prepareTargetDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getPrepareTargetDate() {
        return prepareTargetDate;
    }

    /**
     * Sets the value of the prepareTargetDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setPrepareTargetDate(JAXBElement<XMLGregorianCalendar> value) {
        this.prepareTargetDate = value;
    }

    /**
     * Gets the value of the outputTargetDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getOutputTargetDate() {
        return outputTargetDate;
    }

    /**
     * Sets the value of the outputTargetDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setOutputTargetDate(JAXBElement<XMLGregorianCalendar> value) {
        this.outputTargetDate = value;
    }

    /**
     * Gets the value of the responsible property.
     * 
     * @return
     *     possible object is
     *     {@link Person }
     *     
     */
    public Person getResponsible() {
        return responsible;
    }

    /**
     * Sets the value of the responsible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Person }
     *     
     */
    public void setResponsible(Person value) {
        this.responsible = value;
    }

    /**
     * Gets the value of the department property.
     * 
     * @return
     *     possible object is
     *     {@link Department }
     *     
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * Sets the value of the department property.
     * 
     * @param value
     *     allowed object is
     *     {@link Department }
     *     
     */
    public void setDepartment(Department value) {
        this.department = value;
    }

    /**
     * Gets the value of the declineReasons property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDictionaryItem }
     *     
     */
    public ArrayOfDictionaryItem getDeclineReasons() {
        return declineReasons;
    }

    /**
     * Sets the value of the declineReasons property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDictionaryItem }
     *     
     */
    public void setDeclineReasons(ArrayOfDictionaryItem value) {
        this.declineReasons = value;
    }

    /**
     * Gets the value of the createdByDepartment property.
     * 
     * @return
     *     possible object is
     *     {@link Department }
     *     
     */
    public Department getCreatedByDepartment() {
        return createdByDepartment;
    }

    /**
     * Sets the value of the createdByDepartment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Department }
     *     
     */
    public void setCreatedByDepartment(Department value) {
        this.createdByDepartment = value;
    }

    /**
     * Gets the value of the prepareFactDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getPrepareFactDate() {
        return prepareFactDate;
    }

    /**
     * Sets the value of the prepareFactDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setPrepareFactDate(JAXBElement<XMLGregorianCalendar> value) {
        this.prepareFactDate = value;
    }

    /**
     * Gets the value of the outputFactDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getOutputFactDate() {
        return outputFactDate;
    }

    /**
     * Sets the value of the outputFactDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setOutputFactDate(JAXBElement<XMLGregorianCalendar> value) {
        this.outputFactDate = value;
    }

    /**
     * Gets the value of the outputKind property.
     * 
     * @return
     *     possible object is
     *     {@link OutputKindType }
     *     
     */
    public OutputKindType getOutputKind() {
        return outputKind;
    }

    /**
     * Sets the value of the outputKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutputKindType }
     *     
     */
    public void setOutputKind(OutputKindType value) {
        this.outputKind = value;
    }

    /**
     * Gets the value of the portalNum property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPortalNum() {
        return portalNum;
    }

    /**
     * Sets the value of the portalNum property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPortalNum(String value) {
        this.portalNum = value;
    }

    /**
     * Gets the value of the parentServiceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentServiceNumber() {
        return parentServiceNumber;
    }

    /**
     * Sets the value of the parentServiceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentServiceNumber(String value) {
        this.parentServiceNumber = value;
    }

}
