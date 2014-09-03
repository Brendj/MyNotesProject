
package generated.emp_storage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ReceiveDataChangesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReceiveDataChangesResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://emp.mos.ru/schemas/storage/request/common.xsd}BaseResponse">
 *       &lt;sequence>
 *         &lt;element name="result" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="identifier" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
 *                                       &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="attribute" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
 *                                       &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" maxOccurs="unbounded" minOccurs="0"/>
 *                                       &lt;element name="previous" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" maxOccurs="unbounded" minOccurs="0"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="changeAuthor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="changeSequence" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="changeAction">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="create"/>
 *                                   &lt;enumeration value="update"/>
 *                                   &lt;enumeration value="delete"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="changeTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="hasMoreEntries" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReceiveDataChangesResponse", namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", propOrder = {
    "result"
})
public class ReceiveDataChangesResponse
    extends BaseResponse
{

    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
    protected ReceiveDataChangesResponse.Result result;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link ReceiveDataChangesResponse.Result }
     *     
     */
    public ReceiveDataChangesResponse.Result getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReceiveDataChangesResponse.Result }
     *     
     */
    public void setResult(ReceiveDataChangesResponse.Result value) {
        this.result = value;
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
     *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="identifier" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
     *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="attribute" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
     *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" maxOccurs="unbounded" minOccurs="0"/>
     *                             &lt;element name="previous" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" maxOccurs="unbounded" minOccurs="0"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="changeAuthor" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="changeSequence" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *                   &lt;element name="changeAction">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;enumeration value="create"/>
     *                         &lt;enumeration value="update"/>
     *                         &lt;enumeration value="delete"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="changeTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="hasMoreEntries" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        "entry",
        "hasMoreEntries"
    })
    public static class Result {

        @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
        protected List<ReceiveDataChangesResponse.Result.Entry> entry;
        @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
        protected boolean hasMoreEntries;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ReceiveDataChangesResponse.Result.Entry }
         * 
         * 
         */
        public List<ReceiveDataChangesResponse.Result.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<ReceiveDataChangesResponse.Result.Entry>();
            }
            return this.entry;
        }

        /**
         * Gets the value of the hasMoreEntries property.
         * 
         */
        public boolean isHasMoreEntries() {
            return hasMoreEntries;
        }

        /**
         * Sets the value of the hasMoreEntries property.
         * 
         */
        public void setHasMoreEntries(boolean value) {
            this.hasMoreEntries = value;
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
         *         &lt;element name="identifier" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
         *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="attribute" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
         *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" maxOccurs="unbounded" minOccurs="0"/>
         *                   &lt;element name="previous" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" maxOccurs="unbounded" minOccurs="0"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="changeAuthor" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="changeSequence" type="{http://www.w3.org/2001/XMLSchema}long"/>
         *         &lt;element name="changeAction">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;enumeration value="create"/>
         *               &lt;enumeration value="update"/>
         *               &lt;enumeration value="delete"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="changeTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
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
            "identifier",
            "attribute",
            "changeAuthor",
            "changeSequence",
            "changeAction",
            "changeTime"
        })
        public static class Entry {

            @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
            protected List<ReceiveDataChangesResponse.Result.Entry.Identifier> identifier;
            @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
            protected List<ReceiveDataChangesResponse.Result.Entry.Attribute> attribute;
            @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
            protected String changeAuthor;
            @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
            protected long changeSequence;
            @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
            protected String changeAction;
            @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
            @XmlJavaTypeAdapter(EMPDateAdapter.class)
            protected XMLGregorianCalendar changeTime;

            /**
             * Gets the value of the identifier property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the identifier property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getIdentifier().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ReceiveDataChangesResponse.Result.Entry.Identifier }
             * 
             * 
             */
            public List<ReceiveDataChangesResponse.Result.Entry.Identifier> getIdentifier() {
                if (identifier == null) {
                    identifier = new ArrayList<ReceiveDataChangesResponse.Result.Entry.Identifier>();
                }
                return this.identifier;
            }

            /**
             * Gets the value of the attribute property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the attribute property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAttribute().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link ReceiveDataChangesResponse.Result.Entry.Attribute }
             * 
             * 
             */
            public List<ReceiveDataChangesResponse.Result.Entry.Attribute> getAttribute() {
                if (attribute == null) {
                    attribute = new ArrayList<ReceiveDataChangesResponse.Result.Entry.Attribute>();
                }
                return this.attribute;
            }

            /**
             * Gets the value of the changeAuthor property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getChangeAuthor() {
                return changeAuthor;
            }

            /**
             * Sets the value of the changeAuthor property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setChangeAuthor(String value) {
                this.changeAuthor = value;
            }

            /**
             * Gets the value of the changeSequence property.
             * 
             */
            public long getChangeSequence() {
                return changeSequence;
            }

            /**
             * Sets the value of the changeSequence property.
             * 
             */
            public void setChangeSequence(long value) {
                this.changeSequence = value;
            }

            /**
             * Gets the value of the changeAction property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getChangeAction() {
                return changeAction;
            }

            /**
             * Sets the value of the changeAction property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setChangeAction(String value) {
                this.changeAction = value;
            }

            /**
             * Gets the value of the changeTime property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getChangeTime() {
                return changeTime;
            }

            /**
             * Sets the value of the changeTime property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setChangeTime(XMLGregorianCalendar value) {
                this.changeTime = value;
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
             *         &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
             *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" maxOccurs="unbounded" minOccurs="0"/>
             *         &lt;element name="previous" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" maxOccurs="unbounded" minOccurs="0"/>
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
                "name",
                "value",
                "previous"
            })
            public static class Attribute {

                @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
                protected String name;
                @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", nillable = true)
                @XmlSchemaType(name = "anySimpleType")
                protected List<Object> value;
                @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", nillable = true)
                @XmlSchemaType(name = "anySimpleType")
                protected List<Object> previous;

                /**
                 * Gets the value of the name property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getName() {
                    return name;
                }

                /**
                 * Sets the value of the name property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setName(String value) {
                    this.name = value;
                }

                /**
                 * Gets the value of the value property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the value property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getValue().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Object }
                 * 
                 * 
                 */
                public List<Object> getValue() {
                    if (value == null) {
                        value = new ArrayList<Object>();
                    }
                    return this.value;
                }

                /**
                 * Gets the value of the previous property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the previous property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getPrevious().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Object }
                 * 
                 * 
                 */
                public List<Object> getPrevious() {
                    if (previous == null) {
                        previous = new ArrayList<Object>();
                    }
                    return this.previous;
                }

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
             *         &lt;element name="name" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}attributeName"/>
             *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anySimpleType"/>
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
                "name",
                "value"
            })
            public static class Identifier {

                @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
                protected String name;
                @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
                @XmlSchemaType(name = "anySimpleType")
                protected Object value;

                /**
                 * Gets the value of the name property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getName() {
                    return name;
                }

                /**
                 * Sets the value of the name property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setName(String value) {
                    this.name = value;
                }

                /**
                 * Gets the value of the value property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Object }
                 *     
                 */
                public Object getValue() {
                    return value;
                }

                /**
                 * Sets the value of the value property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Object }
                 *     
                 */
                public void setValue(Object value) {
                    this.value = value;
                }

            }

        }

    }

}
