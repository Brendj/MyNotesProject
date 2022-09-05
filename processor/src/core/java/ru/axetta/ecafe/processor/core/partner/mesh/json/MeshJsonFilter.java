
package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "and",
    "or"
})
public class MeshJsonFilter {

    @JsonProperty("and")
    private List<OpContainer> and = null;

    @JsonProperty("or")
    private List<OpContainer> or = null;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("and")
    public List<OpContainer> getAnd() {
        return and;
    }

    @JsonProperty("and")
    public void setAnd(List<OpContainer> and) {
        this.and = and;
    }

    @JsonProperty("or")
    public List<OpContainer> getOr() {
        return or;
    }

    @JsonProperty("or")
    public void setOr(List<OpContainer> or) {
        this.or = or;
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
