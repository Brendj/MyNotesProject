
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1;

import generated.ru.mos.rnip.xsd.common._2_1.ImportPackageResponseType;
import generated.ru.mos.rnip.xsd.services.export_catalog._2_1.ExportCatalogRequest;
import generated.ru.mos.rnip.xsd.services.export_catalog._2_1.ExportCatalogResponse;
import generated.ru.mos.rnip.xsd.services.export_charges._2_1.ExportChargesRequest;
import generated.ru.mos.rnip.xsd.services.export_charges._2_1.ExportChargesResponse;
import generated.ru.mos.rnip.xsd.services.export_payments._2_1.ExportPaymentsRequest;
import generated.ru.mos.rnip.xsd.services.export_payments._2_1.ExportPaymentsResponse;
import generated.ru.mos.rnip.xsd.services.export_quittances._2_1.ExportQuittancesRequest;
import generated.ru.mos.rnip.xsd.services.export_quittances._2_1.ExportQuittancesResponse;
import generated.ru.mos.rnip.xsd.services.export_refunds._2_1.ExportRefundsRequest;
import generated.ru.mos.rnip.xsd.services.export_refunds._2_1.ExportRefundsResponse;
import generated.ru.mos.rnip.xsd.services.forced_acknowledgement._2_1.ForcedAcknowledgementRequest;
import generated.ru.mos.rnip.xsd.services.forced_acknowledgement._2_1.ForcedAcknowledgementResponse;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ImportCatalogRequest;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ImportCatalogResponse;
import generated.ru.mos.rnip.xsd.services.import_certificates._2_1.ImportCertificateRequest;
import generated.ru.mos.rnip.xsd.services.import_certificates._2_1.ImportCertificateResponse;
import generated.ru.mos.rnip.xsd.services.import_charges._2_1.ImportChargesRequest;
import generated.ru.mos.rnip.xsd.services.import_chargestemplate._2_1.ChargeCreationRequest;
import generated.ru.mos.rnip.xsd.services.import_chargestemplate._2_1.ChargeCreationResponse;
import generated.ru.mos.rnip.xsd.services.import_payments._2_1.ImportPaymentsRequest;
import generated.ru.mos.rnip.xsd.services.import_refunds._2_1.ImportRefundsRequest;

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
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-chargestemplate/2.1.1}ChargeCreationRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-catalog/2.1.1}ExportCatalogRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-charges/2.1.1}ExportChargesRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-payments/2.1.1}ExportPaymentsRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-quittances/2.1.1}ExportQuittancesRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-refunds/2.1.1}ExportRefundsRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.1.1}ForcedAcknowledgementRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-catalog/2.1.1}ImportCatalogRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-certificates/2.1.1}ImportCertificateRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-charges/2.1.1}ImportChargesRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-payments/2.1.1}ImportPaymentsRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-refunds/2.1.1}ImportRefundsRequest"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-chargestemplate/2.1.1}ChargeCreationResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-catalog/2.1.1}ExportCatalogResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-charges/2.1.1}ExportChargesResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-payments/2.1.1}ExportPaymentsResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-quittances/2.1.1}ExportQuittancesResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/export-refunds/2.1.1}ExportRefundsResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.1.1}ForcedAcknowledgementResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-catalog/2.1.1}ImportCatalogResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-certificates/2.1.1}ImportCertificateResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-charges/2.1.1}ImportChargesResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-payments/2.1.1}ImportPaymentsResponse"/>
 *           &lt;element ref="{urn://rnip.mos.ru/xsd/services/import-refunds/2.1.1}ImportRefundsResponse"/>
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
    "importRefundsRequest",
    "chargeCreationResponse",
    "exportCatalogResponse",
    "exportChargesResponse",
    "exportPaymentsResponse",
    "exportQuittancesResponse",
    "exportRefundsResponse",
    "forcedAcknowledgementResponse",
    "importCatalogResponse",
    "importCertificateResponse",
    "importChargesResponse",
    "importPaymentsResponse",
    "importRefundsResponse"
})
@XmlRootElement(name = "MessagePrimaryContent")
public class MessagePrimaryContent {

    @XmlElement(name = "ChargeCreationRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-chargestemplate/2.1.1")
    protected ChargeCreationRequest chargeCreationRequest;
    @XmlElement(name = "ExportCatalogRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-catalog/2.1.1")
    protected ExportCatalogRequest exportCatalogRequest;
    @XmlElement(name = "ExportChargesRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-charges/2.1.1")
    protected ExportChargesRequest exportChargesRequest;
    @XmlElement(name = "ExportPaymentsRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-payments/2.1.1")
    protected ExportPaymentsRequest exportPaymentsRequest;
    @XmlElement(name = "ExportQuittancesRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-quittances/2.1.1")
    protected ExportQuittancesRequest exportQuittancesRequest;
    @XmlElement(name = "ExportRefundsRequest", namespace = "urn://rnip.mos.ru/xsd/services/export-refunds/2.1.1")
    protected ExportRefundsRequest exportRefundsRequest;
    @XmlElement(name = "ForcedAcknowledgementRequest", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.1.1")
    protected ForcedAcknowledgementRequest forcedAcknowledgementRequest;
    @XmlElement(name = "ImportCatalogRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-catalog/2.1.1")
    protected ImportCatalogRequest importCatalogRequest;
    @XmlElement(name = "ImportCertificateRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-certificates/2.1.1")
    protected ImportCertificateRequest importCertificateRequest;
    @XmlElement(name = "ImportChargesRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-charges/2.1.1")
    protected ImportChargesRequest importChargesRequest;
    @XmlElement(name = "ImportPaymentsRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-payments/2.1.1")
    protected ImportPaymentsRequest importPaymentsRequest;
    @XmlElement(name = "ImportRefundsRequest", namespace = "urn://rnip.mos.ru/xsd/services/import-refunds/2.1.1")
    protected ImportRefundsRequest importRefundsRequest;
    @XmlElement(name = "ChargeCreationResponse", namespace = "urn://rnip.mos.ru/xsd/services/import-chargestemplate/2.1.1")
    protected ChargeCreationResponse chargeCreationResponse;
    @XmlElement(name = "ExportCatalogResponse", namespace = "urn://rnip.mos.ru/xsd/services/export-catalog/2.1.1")
    protected ExportCatalogResponse exportCatalogResponse;
    @XmlElement(name = "ExportChargesResponse", namespace = "urn://rnip.mos.ru/xsd/services/export-charges/2.1.1")
    protected ExportChargesResponse exportChargesResponse;
    @XmlElement(name = "ExportPaymentsResponse", namespace = "urn://rnip.mos.ru/xsd/services/export-payments/2.1.1")
    protected ExportPaymentsResponse exportPaymentsResponse;
    @XmlElement(name = "ExportQuittancesResponse", namespace = "urn://rnip.mos.ru/xsd/services/export-quittances/2.1.1")
    protected ExportQuittancesResponse exportQuittancesResponse;
    @XmlElement(name = "ExportRefundsResponse", namespace = "urn://rnip.mos.ru/xsd/services/export-refunds/2.1.1")
    protected ExportRefundsResponse exportRefundsResponse;
    @XmlElement(name = "ForcedAcknowledgementResponse", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.1.1")
    protected ForcedAcknowledgementResponse forcedAcknowledgementResponse;
    @XmlElement(name = "ImportCatalogResponse", namespace = "urn://rnip.mos.ru/xsd/services/import-catalog/2.1.1")
    protected ImportCatalogResponse importCatalogResponse;
    @XmlElement(name = "ImportCertificateResponse", namespace = "urn://rnip.mos.ru/xsd/services/import-certificates/2.1.1")
    protected ImportCertificateResponse importCertificateResponse;
    @XmlElement(name = "ImportChargesResponse", namespace = "urn://rnip.mos.ru/xsd/services/import-charges/2.1.1")
    protected ImportPackageResponseType importChargesResponse;
    @XmlElement(name = "ImportPaymentsResponse", namespace = "urn://rnip.mos.ru/xsd/services/import-payments/2.1.1")
    protected ImportPackageResponseType importPaymentsResponse;
    @XmlElement(name = "ImportRefundsResponse", namespace = "urn://rnip.mos.ru/xsd/services/import-refunds/2.1.1")
    protected ImportPackageResponseType importRefundsResponse;

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

    /**
     * Gets the value of the chargeCreationResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ChargeCreationResponse }
     *     
     */
    public ChargeCreationResponse getChargeCreationResponse() {
        return chargeCreationResponse;
    }

    /**
     * Sets the value of the chargeCreationResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargeCreationResponse }
     *     
     */
    public void setChargeCreationResponse(ChargeCreationResponse value) {
        this.chargeCreationResponse = value;
    }

    /**
     * Gets the value of the exportCatalogResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ExportCatalogResponse }
     *     
     */
    public ExportCatalogResponse getExportCatalogResponse() {
        return exportCatalogResponse;
    }

    /**
     * Sets the value of the exportCatalogResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportCatalogResponse }
     *     
     */
    public void setExportCatalogResponse(ExportCatalogResponse value) {
        this.exportCatalogResponse = value;
    }

    /**
     * Gets the value of the exportChargesResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ExportChargesResponse }
     *     
     */
    public ExportChargesResponse getExportChargesResponse() {
        return exportChargesResponse;
    }

    /**
     * Sets the value of the exportChargesResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportChargesResponse }
     *     
     */
    public void setExportChargesResponse(ExportChargesResponse value) {
        this.exportChargesResponse = value;
    }

    /**
     * Gets the value of the exportPaymentsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ExportPaymentsResponse }
     *     
     */
    public ExportPaymentsResponse getExportPaymentsResponse() {
        return exportPaymentsResponse;
    }

    /**
     * Sets the value of the exportPaymentsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportPaymentsResponse }
     *     
     */
    public void setExportPaymentsResponse(ExportPaymentsResponse value) {
        this.exportPaymentsResponse = value;
    }

    /**
     * Gets the value of the exportQuittancesResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ExportQuittancesResponse }
     *     
     */
    public ExportQuittancesResponse getExportQuittancesResponse() {
        return exportQuittancesResponse;
    }

    /**
     * Sets the value of the exportQuittancesResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportQuittancesResponse }
     *     
     */
    public void setExportQuittancesResponse(ExportQuittancesResponse value) {
        this.exportQuittancesResponse = value;
    }

    /**
     * Gets the value of the exportRefundsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ExportRefundsResponse }
     *     
     */
    public ExportRefundsResponse getExportRefundsResponse() {
        return exportRefundsResponse;
    }

    /**
     * Sets the value of the exportRefundsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportRefundsResponse }
     *     
     */
    public void setExportRefundsResponse(ExportRefundsResponse value) {
        this.exportRefundsResponse = value;
    }

    /**
     * Gets the value of the forcedAcknowledgementResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ForcedAcknowledgementResponse }
     *     
     */
    public ForcedAcknowledgementResponse getForcedAcknowledgementResponse() {
        return forcedAcknowledgementResponse;
    }

    /**
     * Sets the value of the forcedAcknowledgementResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForcedAcknowledgementResponse }
     *     
     */
    public void setForcedAcknowledgementResponse(ForcedAcknowledgementResponse value) {
        this.forcedAcknowledgementResponse = value;
    }

    /**
     * Gets the value of the importCatalogResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ImportCatalogResponse }
     *     
     */
    public ImportCatalogResponse getImportCatalogResponse() {
        return importCatalogResponse;
    }

    /**
     * Sets the value of the importCatalogResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportCatalogResponse }
     *     
     */
    public void setImportCatalogResponse(ImportCatalogResponse value) {
        this.importCatalogResponse = value;
    }

    /**
     * Gets the value of the importCertificateResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ImportCertificateResponse }
     *     
     */
    public ImportCertificateResponse getImportCertificateResponse() {
        return importCertificateResponse;
    }

    /**
     * Sets the value of the importCertificateResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportCertificateResponse }
     *     
     */
    public void setImportCertificateResponse(ImportCertificateResponse value) {
        this.importCertificateResponse = value;
    }

    /**
     * Gets the value of the importChargesResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ImportPackageResponseType }
     *     
     */
    public ImportPackageResponseType getImportChargesResponse() {
        return importChargesResponse;
    }

    /**
     * Sets the value of the importChargesResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportPackageResponseType }
     *     
     */
    public void setImportChargesResponse(ImportPackageResponseType value) {
        this.importChargesResponse = value;
    }

    /**
     * Gets the value of the importPaymentsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ImportPackageResponseType }
     *     
     */
    public ImportPackageResponseType getImportPaymentsResponse() {
        return importPaymentsResponse;
    }

    /**
     * Sets the value of the importPaymentsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportPackageResponseType }
     *     
     */
    public void setImportPaymentsResponse(ImportPackageResponseType value) {
        this.importPaymentsResponse = value;
    }

    /**
     * Gets the value of the importRefundsResponse property.
     * 
     * @return
     *     possible object is
     *     {@link ImportPackageResponseType }
     *     
     */
    public ImportPackageResponseType getImportRefundsResponse() {
        return importRefundsResponse;
    }

    /**
     * Sets the value of the importRefundsResponse property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportPackageResponseType }
     *     
     */
    public void setImportRefundsResponse(ImportPackageResponseType value) {
        this.importRefundsResponse = value;
    }

}
