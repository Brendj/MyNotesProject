
package generated.ru.mos.rnip.xsd.services.forced_acknowledgement._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ResponseType;
import generated.ru.mos.rnip.xsd.quittance._2_1.QuittanceType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}ResponseType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="ForcedAcknowledgementItemResponse" maxOccurs="100">
 *             &lt;complexType>
 *               &lt;complexContent>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                   &lt;sequence>
 *                     &lt;choice>
 *                       &lt;sequence>
 *                         &lt;element name="Quittance" type="{http://rnip.mos.ru/xsd/Quittance/2.1.1}QuittanceType" maxOccurs="100" minOccurs="0"/>
 *                       &lt;/sequence>
 *                       &lt;element name="Error">
 *                         &lt;complexType>
 *                           &lt;complexContent>
 *                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                               &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                               &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                             &lt;/restriction>
 *                           &lt;/complexContent>
 *                         &lt;/complexType>
 *                       &lt;/element>
 *                       &lt;element name="Done" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                     &lt;/choice>
 *                   &lt;/sequence>
 *                   &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *                 &lt;/restriction>
 *               &lt;/complexContent>
 *             &lt;/complexType>
 *           &lt;/element>
 *         &lt;/sequence>
 *         &lt;element name="Done" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "forcedAcknowledgementItemResponse",
    "done"
})
@XmlRootElement(name = "ForcedAcknowledgementResponse")
public class ForcedAcknowledgementResponse
    extends ResponseType
{

    @XmlElement(name = "ForcedAcknowledgementItemResponse")
    protected List<ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse> forcedAcknowledgementItemResponse;
    @XmlElement(name = "Done")
    protected Boolean done;

    /**
     * Gets the value of the forcedAcknowledgementItemResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the forcedAcknowledgementItemResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getForcedAcknowledgementItemResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse }
     * 
     * 
     */
    public List<ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse> getForcedAcknowledgementItemResponse() {
        if (forcedAcknowledgementItemResponse == null) {
            forcedAcknowledgementItemResponse = new ArrayList<ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse>();
        }
        return this.forcedAcknowledgementItemResponse;
    }

    /**
     * Gets the value of the done property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDone() {
        return done;
    }

    /**
     * Sets the value of the done property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDone(Boolean value) {
        this.done = value;
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
     *         &lt;choice>
     *           &lt;sequence>
     *             &lt;element name="Quittance" type="{http://rnip.mos.ru/xsd/Quittance/2.1.1}QuittanceType" maxOccurs="100" minOccurs="0"/>
     *           &lt;/sequence>
     *           &lt;element name="Error">
     *             &lt;complexType>
     *               &lt;complexContent>
     *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                   &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                   &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;/restriction>
     *               &lt;/complexContent>
     *             &lt;/complexType>
     *           &lt;/element>
     *           &lt;element name="Done" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *         &lt;/choice>
     *       &lt;/sequence>
     *       &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "quittance",
        "error",
        "done"
    })
    public static class ForcedAcknowledgementItemResponse {

        @XmlElement(name = "Quittance")
        protected List<QuittanceType> quittance;
        @XmlElement(name = "Error")
        protected ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse.Error error;
        @XmlElement(name = "Done")
        protected Boolean done;
        @XmlAttribute(name = "Id", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the quittance property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the quittance property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getQuittance().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link QuittanceType }
         * 
         * 
         */
        public List<QuittanceType> getQuittance() {
            if (quittance == null) {
                quittance = new ArrayList<QuittanceType>();
            }
            return this.quittance;
        }

        /**
         * Gets the value of the error property.
         * 
         * @return
         *     possible object is
         *     {@link ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse.Error }
         *     
         */
        public ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse.Error getError() {
            return error;
        }

        /**
         * Sets the value of the error property.
         * 
         * @param value
         *     allowed object is
         *     {@link ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse.Error }
         *     
         */
        public void setError(ForcedAcknowledgementResponse.ForcedAcknowledgementItemResponse.Error value) {
            this.error = value;
        }

        /**
         * Gets the value of the done property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isDone() {
            return done;
        }

        /**
         * Sets the value of the done property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setDone(Boolean value) {
            this.done = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
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
         *       &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="message" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Error {

            @XmlAttribute(name = "code")
            protected String code;
            @XmlAttribute(name = "message")
            protected String message;

            /**
             * Gets the value of the code property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCode() {
                return code;
            }

            /**
             * Sets the value of the code property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCode(String value) {
                this.code = value;
            }

            /**
             * Gets the value of the message property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMessage() {
                return message;
            }

            /**
             * Sets the value of the message property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMessage(String value) {
                this.message = value;
            }

        }

    }

}
