
package generated.emp_storage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
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
 *                         &lt;extension base="{http://emp.mos.ru/schemas/storage/entity/entry.xsd}Entry">
 *                           &lt;sequence>
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
 *                         &lt;/extension>
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
@XmlType(name = "ReceiveDataChangesResponse", propOrder = {
    "result"
})
public class ReceiveDataChangesResponse
    extends BaseResponse
{

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
     *               &lt;extension base="{http://emp.mos.ru/schemas/storage/entity/entry.xsd}Entry">
     *                 &lt;sequence>
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
     *               &lt;/extension>
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

        protected List<ReceiveDataChangesResponse.Result.Entry> entry;
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
         *     &lt;extension base="{http://emp.mos.ru/schemas/storage/entity/entry.xsd}Entry">
         *       &lt;sequence>
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
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "changeSequence",
            "changeAction",
            "changeTime"
        })
        public static class Entry
            extends generated.emp_storage.Entry
        {

            protected long changeSequence;
            @XmlElement(required = true)
            protected String changeAction;
            @XmlElement(required = true)
            protected XMLGregorianCalendar changeTime;

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

        }

    }

}
