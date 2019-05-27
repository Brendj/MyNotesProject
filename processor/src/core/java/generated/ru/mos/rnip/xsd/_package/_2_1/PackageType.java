
package generated.ru.mos.rnip.xsd._package._2_1;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for PackageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ImportedCharge" type="{http://rnip.mos.ru/xsd/Package/2.1.0}ImportedChargeType" maxOccurs="100"/>
 *         &lt;element name="ImportedPayment" type="{http://rnip.mos.ru/xsd/Package/2.1.0}ImportedPaymentType" maxOccurs="100"/>
 *         &lt;element name="ImportedRefund" type="{http://rnip.mos.ru/xsd/Package/2.1.0}ImportedRefundType" maxOccurs="100"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageType", propOrder = {
    "importedCharge",
    "importedPayment",
    "importedRefund"
})
@XmlSeeAlso({
    RefundsPackage.class,
    PaymentsPackage.class,
    ChargesPackage.class
})
public class PackageType {

    @XmlElement(name = "ImportedCharge")
    protected List<ImportedChargeType> importedCharge;
    @XmlElement(name = "ImportedPayment")
    protected List<ImportedPaymentType> importedPayment;
    @XmlElement(name = "ImportedRefund")
    protected List<ImportedRefundType> importedRefund;

    /**
     * Gets the value of the importedCharge property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the importedCharge property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImportedCharge().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImportedChargeType }
     * 
     * 
     */
    public List<ImportedChargeType> getImportedCharge() {
        if (importedCharge == null) {
            importedCharge = new ArrayList<ImportedChargeType>();
        }
        return this.importedCharge;
    }

    /**
     * Gets the value of the importedPayment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the importedPayment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImportedPayment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImportedPaymentType }
     * 
     * 
     */
    public List<ImportedPaymentType> getImportedPayment() {
        if (importedPayment == null) {
            importedPayment = new ArrayList<ImportedPaymentType>();
        }
        return this.importedPayment;
    }

    /**
     * Gets the value of the importedRefund property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the importedRefund property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImportedRefund().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImportedRefundType }
     * 
     * 
     */
    public List<ImportedRefundType> getImportedRefund() {
        if (importedRefund == null) {
            importedRefund = new ArrayList<ImportedRefundType>();
        }
        return this.importedRefund;
    }

}
