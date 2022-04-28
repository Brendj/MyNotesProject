package ru.axetta.ecafe.processor.core.partner.mesh.json;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
        "id",
        "person_id",
        "agent_person_id",
        "agent_type_id",
        "validation_state_id",
        "validated_at",
        "actual_from",
        "actual_to",
        "created_by",
        "updated_by",
        "created_at",
        "updated_at",
        "agent_type",
        "agent_person",
        "validation_errors"
})
public class PersonAgent {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("person_id")
    private String personId;
    @JsonProperty("agent_person_id")
    private String agentPersonId;
    @JsonProperty("agent_type_id")
    private Integer agentTypeId;
    @JsonProperty("validation_state_id")
    private Object validationStateId;
    @JsonProperty("validated_at")
    private Object validatedAt;
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
    @JsonProperty("agent_type")
    private AgentType agentType;
    @JsonProperty("agent_person")
    private ResponsePersons agentPerson;
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

    @JsonProperty("agent_person_id")
    public String getAgentPersonId() {
        return agentPersonId;
    }

    @JsonProperty("agent_person_id")
    public void setAgentPersonId(String agentPersonId) {
        this.agentPersonId = agentPersonId;
    }

    @JsonProperty("agent_type_id")
    public Integer getAgentTypeId() {
        return agentTypeId;
    }

    @JsonProperty("agent_type_id")
    public void setAgentTypeId(Integer agentTypeId) {
        this.agentTypeId = agentTypeId;
    }

    @JsonProperty("validation_state_id")
    public Object getValidationStateId() {
        return validationStateId;
    }

    @JsonProperty("validation_state_id")
    public void setValidationStateId(Object validationStateId) {
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

    @JsonProperty("agent_type")
    public AgentType getAgentType() {
        return agentType;
    }

    @JsonProperty("agent_type")
    public void setAgentType(AgentType agentType) {
        this.agentType = agentType;
    }

    @JsonProperty("agent_person")
    public ResponsePersons getAgentPerson() {
        return agentPerson;
    }

    @JsonProperty("agent_person")
    public void setAgentPerson(ResponsePersons agentPerson) {
        this.agentPerson = agentPerson;
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