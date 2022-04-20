package ru.axetta.ecafe.processor.core.partner.mesh.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
        "person",
        "degree",
        "degree_description"
})
public class SimilarPerson {
    @JsonProperty("person")
    private ResponsePersons person;
    @JsonProperty("degree")
    private Integer degree;
    @JsonProperty("degree_description")
    private List<DegreeDescription> degreeDescription = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("person")
    public ResponsePersons getPerson() {
        return person;
    }

    @JsonProperty("person")
    public void setPerson(ResponsePersons person) {
        this.person = person;
    }

    @JsonProperty("degree")
    public Integer getDegree() {
        return degree;
    }

    @JsonProperty("degree")
    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    @JsonProperty("degree_description")
    public List<DegreeDescription> getDegreeDescription() {
        return degreeDescription;
    }

    @JsonProperty("degree_description")
    public void setDegreeDescription(List<DegreeDescription> degreeDescription) {
        this.degreeDescription = degreeDescription;
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
