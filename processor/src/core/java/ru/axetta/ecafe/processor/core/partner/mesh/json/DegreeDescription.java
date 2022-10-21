package ru.axetta.ecafe.processor.core.partner.mesh.json;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
        "degree",
        "method"
})
public class DegreeDescription {

    @JsonProperty("degree")
    private Integer degree;
    @JsonProperty("method")
    private String method;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("degree")
    public Integer getDegree() {
        return degree;
    }

    @JsonProperty("degree")
    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(String method) {
        this.method = method;
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
