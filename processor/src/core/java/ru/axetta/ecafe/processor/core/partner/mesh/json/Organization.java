
package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "global_id",
    "constituent_entity_id",
    "status_id",
    "actual_from",
    "actual_to"
})
public class Organization {

    @JsonProperty("global_id")
    private Integer globalId;
    @JsonProperty("constituent_entity_id")
    private Integer constituentEntityId;
    @JsonProperty("status_id")
    private Integer statusId;
    @JsonProperty("actual_from")
    private String actualFrom;
    @JsonProperty("actual_to")
    private String actualTo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("global_id")
    public Integer getGlobalId() {
        return globalId;
    }

    @JsonProperty("global_id")
    public void setGlobalId(Integer globalId) {
        this.globalId = globalId;
    }

    @JsonProperty("constituent_entity_id")
    public Integer getConstituentEntityId() {
        return constituentEntityId;
    }

    @JsonProperty("constituent_entity_id")
    public void setConstituentEntityId(Integer constituentEntityId) {
        this.constituentEntityId = constituentEntityId;
    }

    @JsonProperty("status_id")
    public Integer getStatusId() {
        return statusId;
    }

    @JsonProperty("status_id")
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
