
package generated.ru.mos.rnip.xsd.services.export_charges._2_1;

import generated.ru.mos.rnip.xsd.charge._2_1.ChargeType;
import generated.ru.mos.rnip.xsd.common._2_1.ResponseType;

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
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}ResponseType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="ChargeInfo" maxOccurs="100">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://rnip.mos.ru/xsd/Charge/2.1.1}ChargeType">
 *                 &lt;attribute name="amountToPay" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                 &lt;attribute name="acknowledgmentStatus" type="{http://rnip.mos.ru/xsd/Common/2.1.1}AcknowledgmentStatusType" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="needReRequest" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "chargeInfo"
})
@XmlRootElement(name = "ExportChargesResponse")
public class ExportChargesResponse
    extends ResponseType
{

    @XmlElement(name = "ChargeInfo")
    protected List<ExportChargesResponse.ChargeInfo> chargeInfo;
    @XmlAttribute(name = "hasMore", required = true)
    protected boolean hasMore;
    @XmlAttribute(name = "needReRequest")
    protected Boolean needReRequest;

    /**
     * Gets the value of the chargeInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chargeInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChargeInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExportChargesResponse.ChargeInfo }
     * 
     * 
     */
    public List<ExportChargesResponse.ChargeInfo> getChargeInfo() {
        if (chargeInfo == null) {
            chargeInfo = new ArrayList<ExportChargesResponse.ChargeInfo>();
        }
        return this.chargeInfo;
    }

    /**
     * Gets the value of the hasMore property.
     * 
     */
    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * Sets the value of the hasMore property.
     * 
     */
    public void setHasMore(boolean value) {
        this.hasMore = value;
    }

    /**
     * Gets the value of the needReRequest property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isNeedReRequest() {
        if (needReRequest == null) {
            return false;
        } else {
            return needReRequest;
        }
    }

    /**
     * Sets the value of the needReRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeedReRequest(Boolean value) {
        this.needReRequest = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://rnip.mos.ru/xsd/Charge/2.1.1}ChargeType">
     *       &lt;attribute name="amountToPay" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
     *       &lt;attribute name="acknowledgmentStatus" type="{http://rnip.mos.ru/xsd/Common/2.1.1}AcknowledgmentStatusType" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class ChargeInfo
        extends ChargeType
    {

        @XmlAttribute(name = "amountToPay", required = true)
        protected long amountToPay;
        @XmlAttribute(name = "acknowledgmentStatus")
        protected String acknowledgmentStatus;

        /**
         * Gets the value of the amountToPay property.
         * 
         */
        public long getAmountToPay() {
            return amountToPay;
        }

        /**
         * Sets the value of the amountToPay property.
         * 
         */
        public void setAmountToPay(long value) {
            this.amountToPay = value;
        }

        /**
         * Gets the value of the acknowledgmentStatus property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAcknowledgmentStatus() {
            return acknowledgmentStatus;
        }

        /**
         * Sets the value of the acknowledgmentStatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAcknowledgmentStatus(String value) {
            this.acknowledgmentStatus = value;
        }

    }

}
