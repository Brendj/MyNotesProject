package ru.axetta.ecafe.processor.core.partner.mesh.json;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
        "id",
        "person_id",
        "validation_state_id",
        "validated_at",
        "actual_from",
        "actual_to",
        "created_by",
        "updated_by",
        "created_at",
        "updated_at",
        "document_type_id",
        "series",
        "number",
        "subdivision_code",
        "issuer",
        "issued",
        "expiration",
        "attachments",
        "document_type",
        "validation_errors"
})
public class PersonDocument {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("person_id")
    private String personId;
    @JsonProperty("validation_state_id")
    private Integer validationStateId;
    @JsonProperty("validated_at")
    private Object validatedAt;
    @JsonProperty("actual_from")
    private String actualFrom;
    @JsonProperty("actual_to")
    private String actualTo;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("updated_by")
    private String updatedBy;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("document_type_id")
    private Integer documentTypeId;
    @JsonProperty("series")
    private String series;
    @JsonProperty("number")
    private String number;
    @JsonProperty("subdivision_code")
    private String subdivisionCode;
    @JsonProperty("issuer")
    private String issuer;
    @JsonProperty("issued")
    private String issued;
    @JsonProperty("expiration")
    private String expiration;
    @JsonProperty("attachments")
    private String attachments;
    @JsonProperty("document_type")
    private DocumentType documentType;
    @JsonProperty("validation_errors")
    private Object validationErrors;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("person_id")
    public String getPersonId() {
        return personId;
    }

    @JsonProperty("person_id")
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @JsonProperty("validation_state_id")
    public Integer getValidationStateId() {
        return validationStateId;
    }

    @JsonProperty("validation_state_id")
    public void setValidationStateId(Integer validationStateId) {
        this.validationStateId = validationStateId;
    }

    @JsonProperty("validated_at")
    public Object getValidatedAt() {
        return validatedAt;
    }

    @JsonProperty("validated_at")
    public void setValidatedAt(Object validatedAt) {
        this.validatedAt = validatedAt;
    }

    @JsonProperty("actual_from")
    public String getActualFrom() {
        return actualFrom;
    }

    @JsonProperty("actual_from")
    public void setActualFrom(String actualFrom) {
        this.actualFrom = actualFrom;
    }

    @JsonProperty("actual_to")
    public String getActualTo() {
        return actualTo;
    }

    @JsonProperty("actual_to")
    public void setActualTo(String actualTo) {
        this.actualTo = actualTo;
    }

    @JsonProperty("created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    @JsonProperty("created_by")
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @JsonProperty("updated_by")
    public String getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty("updated_by")
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("document_type_id")
    public Integer getDocumentTypeId() {
        return documentTypeId;
    }

    @JsonProperty("document_type_id")
    public void setDocumentTypeId(Integer documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    @JsonProperty("series")
    public String getSeries() {
        return series;
    }

    @JsonProperty("series")
    public void setSeries(String series) {
        this.series = series;
    }

    @JsonProperty("number")
    public String getNumber() {
        return number;
    }

    @JsonProperty("number")
    public void setNumber(String number) {
        this.number = number;
    }

    @JsonProperty("subdivision_code")
    public String getSubdivisionCode() {
        return subdivisionCode;
    }

    @JsonProperty("subdivision_code")
    public void setSubdivisionCode(String subdivisionCode) {
        this.subdivisionCode = subdivisionCode;
    }

    @JsonProperty("issuer")
    public String getIssuer() {
        return issuer;
    }

    @JsonProperty("issuer")
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    @JsonProperty("issued")
    public String getIssued() {
        return issued;
    }

    @JsonProperty("issued")
    public void setIssued(String issued) {
        this.issued = issued;
    }

    @JsonProperty("expiration")
    public String getExpiration() {
        return expiration;
    }

    @JsonProperty("expiration")
    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    @JsonProperty("attachments")
    public String getAttachments() {
        return attachments;
    }

    @JsonProperty("attachments")
    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    @JsonProperty("document_type")
    public DocumentType getDocumentType() {
        return documentType;
    }

    @JsonProperty("document_type")
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    @JsonProperty("validation_errors")
    public Object getValidationErrors() {
        return validationErrors;
    }

    @JsonProperty("validation_errors")
    public void setValidationErrors(Object validationErrors) {
        this.validationErrors = validationErrors;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


}
