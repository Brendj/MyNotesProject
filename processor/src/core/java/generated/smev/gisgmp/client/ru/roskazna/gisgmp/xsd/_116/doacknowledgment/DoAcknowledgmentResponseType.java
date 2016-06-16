
package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.doacknowledgment;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.quittance.QuittanceType;


/**
 * <p>Java class for DoAcknowledgmentResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DoAcknowledgmentResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="Quittances">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Quittance" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/Quittance}QuittanceType">
 *                         &lt;/extension>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PaymentsNotFound">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/DoAcknowledgment}PaymentSystemIdentifier" maxOccurs="unbounded"/>
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
@XmlType(name = "DoAcknowledgmentResponseType", propOrder = {
    "quittances",
    "paymentsNotFound"
})
public class DoAcknowledgmentResponseType {

    @XmlElement(name = "Quittances")
    protected DoAcknowledgmentResponseType.Quittances quittances;
    @XmlElement(name = "PaymentsNotFound")
    protected DoAcknowledgmentResponseType.PaymentsNotFound paymentsNotFound;

    /**
     * Gets the value of the quittances property.
     * 
     * @return
     *     possible object is
     *     {@link DoAcknowledgmentResponseType.Quittances }
     *     
     */
    public DoAcknowledgmentResponseType.Quittances getQuittances() {
        return quittances;
    }

    /**
     * Sets the value of the quittances property.
     * 
     * @param value
     *     allowed object is
     *     {@link DoAcknowledgmentResponseType.Quittances }
     *     
     */
    public void setQuittances(DoAcknowledgmentResponseType.Quittances value) {
        this.quittances = value;
    }

    /**
     * Gets the value of the paymentsNotFound property.
     * 
     * @return
     *     possible object is
     *     {@link DoAcknowledgmentResponseType.PaymentsNotFound }
     *     
     */
    public DoAcknowledgmentResponseType.PaymentsNotFound getPaymentsNotFound() {
        return paymentsNotFound;
    }

    /**
     * Sets the value of the paymentsNotFound property.
     * 
     * @param value
     *     allowed object is
     *     {@link DoAcknowledgmentResponseType.PaymentsNotFound }
     *     
     */
    public void setPaymentsNotFound(DoAcknowledgmentResponseType.PaymentsNotFound value) {
        this.paymentsNotFound = value;
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
     *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/DoAcknowledgment}PaymentSystemIdentifier" maxOccurs="unbounded"/>
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
        "paymentSystemIdentifier"
    })
    public static class PaymentsNotFound {

        @XmlElement(name = "PaymentSystemIdentifier", required = true)
        protected List<String> paymentSystemIdentifier;

        /**
         * ��� Gets the value of the paymentSystemIdentifier property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the paymentSystemIdentifier property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPaymentSystemIdentifier().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getPaymentSystemIdentifier() {
            if (paymentSystemIdentifier == null) {
                paymentSystemIdentifier = new ArrayList<String>();
            }
            return this.paymentSystemIdentifier;
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
     *         &lt;element name="Quittance" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/Quittance}QuittanceType">
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
        "quittance"
    })
    public static class Quittances {

        @XmlElement(name = "Quittance", required = true)
        protected List<DoAcknowledgmentResponseType.Quittances.Quittance> quittance;

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
         * {@link DoAcknowledgmentResponseType.Quittances.Quittance }
         * 
         * 
         */
        public List<DoAcknowledgmentResponseType.Quittances.Quittance> getQuittance() {
            if (quittance == null) {
                quittance = new ArrayList<DoAcknowledgmentResponseType.Quittances.Quittance>();
            }
            return this.quittance;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/Quittance}QuittanceType">
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Quittance
            extends QuittanceType
        {


        }

    }

}
