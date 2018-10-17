
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1;

import generated.ru.mos.rnip.xsd.services.export_catalog._2_0.ExportCatalogRequest;
import generated.ru.mos.rnip.xsd.services.export_charges._2_0.ExportChargesRequest;
import generated.ru.mos.rnip.xsd.services.export_payments._2_0.ExportPaymentsRequest;
import generated.ru.mos.rnip.xsd.services.export_quittances._2_0.ExportQuittancesRequest;
import generated.ru.mos.rnip.xsd.services.export_refunds._2_0.ExportRefundsRequest;
import generated.ru.mos.rnip.xsd.services.forced_ackmowledgement._2_0.ForcedAcknowledgementRequest;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_0.ImportCatalogRequest;
import generated.ru.mos.rnip.xsd.services.import_certificates._2_0.ImportCertificateRequest;
import generated.ru.mos.rnip.xsd.services.import_charges._2_0.ImportChargesRequest;
import generated.ru.mos.rnip.xsd.services.import_chargestemplate._2_0.ChargeCreationRequest;
import generated.ru.mos.rnip.xsd.services.import_payments._2_0.ImportPaymentsRequest;
import generated.ru.mos.rnip.xsd.services.import_refunds._2_0.ImportRefundsRequest;

import javax.xml.bind.annotation.*;


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
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-chargestemplate/2.0.1}ChargeCreationRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-catalog/2.0.1}ExportCatalogRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-charges/2.0.1}ExportChargesRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-payments/2.0.1}ExportPaymentsRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-quittances/2.0.1}ExportQuittancesRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-refunds/2.0.1}ExportRefundsRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/forced-ackmowledgement/2.0.1}ForcedAcknowledgementRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-catalog/2.0.1}ImportCatalogRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-certificates/2.0.1}ImportCertificateRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-charges/2.0.1}ImportChargesRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-payments/2.0.1}ImportPaymentsRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-refunds/2.0.1}ImportRefundsRequest"/>
 *         &lt;/choice>
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
    "chargeCreationRequest",
    "exportCatalogRequest",
    "exportChargesRequest",
    "exportPaymentsRequest",
    "exportQuittancesRequest",
    "exportRefundsRequest",
    "forcedAcknowledgementRequest",
    "importCatalogRequest",
    "importCertificateRequest",
    "importChargesRequest",
    "importPaymentsRequest",
    "importRefundsRequest"
})
@XmlRootElement(name = "MessagePrimaryContent")
public class MessagePrimaryContent {

    @XmlElement(name = "ChargeCreationRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-chargestemplate/2.0.1")
    protected ChargeCreationRequest chargeCreationRequest;
    @XmlElement(name = "ExportCatalogRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-catalog/2.0.1")
    protected ExportCatalogRequest exportCatalogRequest;
    @XmlElement(name = "ExportChargesRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-charges/2.0.1")
    protected ExportChargesRequest exportChargesRequest;
    @XmlElement(name = "ExportPaymentsRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-payments/2.0.1")
    protected ExportPaymentsRequest exportPaymentsRequest;
    @XmlElement(name = "ExportQuittancesRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-quittances/2.0.1")
    protected ExportQuittancesRequest exportQuittancesRequest;
    @XmlElement(name = "ExportRefundsRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-refunds/2.0.1")
    protected ExportRefundsRequest exportRefundsRequest;
    @XmlElement(name = "ForcedAcknowledgementRequest", namespace = "urn://rnip.mos.ru/xsd/services/forced-ackmowledgement/2.0.1")
    protected ForcedAcknowledgementRequest forcedAcknowledgementRequest;
    @XmlElement(name = "ImportCatalogRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-catalog/2.0.1")
    protected ImportCatalogRequest importCatalogRequest;
    @XmlElement(name = "ImportCertificateRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-certificates/2.0.1")
    protected ImportCertificateRequest importCertificateRequest;
    @XmlElement(name = "ImportChargesRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-charges/2.0.1")
    protected ImportChargesRequest importChargesRequest;
    @XmlElement(name = "ImportPaymentsRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-payments/2.0.1")
    protected ImportPaymentsRequest importPaymentsRequest;
    @XmlElement(name = "ImportRefundsRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-refunds/2.0.1")
    protected ImportRefundsRequest importRefundsRequest;

    /**
     * Gets the value of the chargeCreationRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeCreationRequest }
     *     
     */
    public ChargeCreationRequest getChargeCreationRequest() {
        return chargeCreationRequest;
    }

    /**
     * Sets the value of the chargeCreationRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeCreationRequest }
     *     
     */
    public void setChargeCreationRequest(ChargeCreationRequest value) {
        this.chargeCreationRequest = value;
    }

    /**
     * Gets the value of the exportCatalogRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ExportCatalogRequest }
     *     
     */
    public ExportCatalogRequest getExportCatalogRequest() {
        return exportCatalogRequest;
    }

    /**
     * Sets the value of the exportCatalogRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportCatalogRequest }
     *     
     */
    public void setExportCatalogRequest(ExportCatalogRequest value) {
        this.exportCatalogRequest = value;
    }

    /**
     * Gets the value of the exportChargesRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ExportChargesRequest }
     *     
     */
    public ExportChargesRequest getExportChargesRequest() {
        return exportChargesRequest;
    }

    /**
     * Sets the value of the exportChargesRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportChargesRequest }
     *     
     */
    public void setExportChargesRequest(ExportChargesRequest value) {
        this.exportChargesRequest = value;
    }

    /**
     * Gets the value of the exportPaymentsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ExportPaymentsRequest }
     *     
     */
    public ExportPaymentsRequest getExportPaymentsRequest() {
        return exportPaymentsRequest;
    }

    /**
     * Sets the value of the exportPaymentsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportPaymentsRequest }
     *     
     */
    public void setExportPaymentsRequest(ExportPaymentsRequest value) {
        this.exportPaymentsRequest = value;
    }

    /**
     * Gets the value of the exportQuittancesRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ExportQuittancesRequest }
     *     
     */
    public ExportQuittancesRequest getExportQuittancesRequest() {
        return exportQuittancesRequest;
    }

    /**
     * Sets the value of the exportQuittancesRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportQuittancesRequest }
     *     
     */
    public void setExportQuittancesRequest(ExportQuittancesRequest value) {
        this.exportQuittancesRequest = value;
    }

    /**
     * Gets the value of the exportRefundsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ExportRefundsRequest }
     *     
     */
    public ExportRefundsRequest getExportRefundsRequest() {
        return exportRefundsRequest;
    }

    /**
     * Sets the value of the exportRefundsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportRefundsRequest }
     *     
     */
    public void setExportRefundsRequest(ExportRefundsRequest value) {
        this.exportRefundsRequest = value;
    }

    /**
     * Gets the value of the forcedAcknowledgementRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ForcedAcknowledgementRequest }
     *     
     */
    public ForcedAcknowledgementRequest getForcedAcknowledgementRequest() {
        return forcedAcknowledgementRequest;
    }

    /**
     * Sets the value of the forcedAcknowledgementRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForcedAcknowledgementRequest }
     *     
     */
    public void setForcedAcknowledgementRequest(ForcedAcknowledgementRequest value) {
        this.forcedAcknowledgementRequest = value;
    }

    /**
     * Gets the value of the importCatalogRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ImportCatalogRequest }
     *     
     */
    public ImportCatalogRequest getImportCatalogRequest() {
        return importCatalogRequest;
    }

    /**
     * Sets the value of the importCatalogRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportCatalogRequest }
     *     
     */
    public void setImportCatalogRequest(ImportCatalogRequest value) {
        this.importCatalogRequest = value;
    }

    /**
     * Gets the value of the importCertificateRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ImportCertificateRequest }
     *     
     */
    public ImportCertificateRequest getImportCertificateRequest() {
        return importCertificateRequest;
    }

    /**
     * Sets the value of the importCertificateRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportCertificateRequest }
     *     
     */
    public void setImportCertificateRequest(ImportCertificateRequest value) {
        this.importCertificateRequest = value;
    }

    /**
     * Gets the value of the importChargesRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ImportChargesRequest }
     *     
     */
    public ImportChargesRequest getImportChargesRequest() {
        return importChargesRequest;
    }

    /**
     * Sets the value of the importChargesRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportChargesRequest }
     *     
     */
    public void setImportChargesRequest(ImportChargesRequest value) {
        this.importChargesRequest = value;
    }

    /**
     * Gets the value of the importPaymentsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ImportPaymentsRequest }
     *     
     */
    public ImportPaymentsRequest getImportPaymentsRequest() {
        return importPaymentsRequest;
    }

    /**
     * Sets the value of the importPaymentsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportPaymentsRequest }
     *     
     */
    public void setImportPaymentsRequest(ImportPaymentsRequest value) {
        this.importPaymentsRequest = value;
    }

    /**
     * Gets the value of the importRefundsRequest property.
     * 
     * @return
     *     possible object is
     *     {@link ImportRefundsRequest }
     *     
     */
    public ImportRefundsRequest getImportRefundsRequest() {
        return importRefundsRequest;
    }

    /**
     * Sets the value of the importRefundsRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportRefundsRequest }
     *     
     */
    public void setImportRefundsRequest(ImportRefundsRequest value) {
        this.importRefundsRequest = value;
    }

}
