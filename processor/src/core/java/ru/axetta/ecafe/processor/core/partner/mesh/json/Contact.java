package ru.axetta.ecafe.processor.core.partner.mesh.json;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
        "id",
        "person_id",
        "actual_from",
        "actual_to",
        "created_by",
        "updated_by",
        "created_at",
        "updated_at",
        "type_id",
        "data",
        "default",
        "type",
        "validation_state_id",
        "validated_at",
        "validation_errors"
})
public class Contact {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("person_id")
    private String personId;
    @JsonProperty("actual_from")
    private String actualFrom;
    @JsonProperty("actual_to")
    private String actualTo;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("updated_by")
    private Object updatedBy;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private Object updatedAt;
    @JsonProperty("type_id")
    private Integer typeId;
    @JsonProperty("data")
    private String data;
    @JsonProperty("default")
    private Boolean _default;
    @JsonProperty("type")
    private ContactType type;
    @JsonProperty("validation_state_id")
    private Integer validationStateId;
    @JsonProperty("validated_at")
    private String validatedAt;
    @JsonProperty("validation_errors")
    private Object validationErrors;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonIgnore
    public boolean empty(String valueActualFrom) {
        return StringUtils.isEmpty(valueActualFrom) || valueActualFrom.equalsIgnoreCase("null");
    }

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
    public Object getUpdatedBy() {
        return updatedBy;
    }

    @JsonProperty("updated_by")
    public void setUpdatedBy(Object updatedBy) {
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
    public Object getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("updated_at")
    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonProperty("type_id")
    public Integer getTypeId() {
        return typeId;
    }

    @JsonProperty("type_id")
    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @JsonProperty("data")
    public String getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(String data) {
        this.data = data;
    }

    @JsonProperty("default")
    public Boolean getDefault() {
        return _default;
    }

    @JsonProperty("default")
    public void setDefault(Boolean _default) {
        this._default = _default;
    }

    @JsonProperty("type")
    public ContactType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(ContactType type) {
        this.type = type;
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
    public String getValidatedAt() {
        return validatedAt;
    }

    @JsonProperty("validated_at")
    public void setValidatedAt(String validatedAt) {
        this.validatedAt = validatedAt;
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
