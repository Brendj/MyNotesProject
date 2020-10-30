
package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "id",
    "person_id",
    "category_id",
    "parameter_values",
    "actual_from",
    "actual_to",
    "created_by",
    "updated_by",
    "created_at",
    "updated_at"
})
public class Category implements Comparable {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("person_id")
    private String personId;
    @JsonProperty("category_id")
    private Integer categoryId;
    @JsonProperty("parameter_values")
    private List<Object> parameterValues = new LinkedList<>();
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
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public static final int PROPER_ID = 1;

    public boolean empty(String valueActualFrom) {
        return StringUtils.isEmpty(valueActualFrom) || valueActualFrom.equalsIgnoreCase("null");
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Category)) {
            return 1;
        }

        Category item = (Category) o;
        if (item.getCategoryId() != PROPER_ID) return 1;

        if (empty(this.getCreatedAt()) && empty(item.getCreatedAt())) return 0;
        if (!empty(this.getCreatedAt()) && empty(item.getCreatedAt())) return 1;
        if (empty(this.getCreatedAt()) && !empty(item.getCreatedAt())) return -1;
        return this.getCreatedAt().compareTo(item.getCreatedAt());
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

    @JsonProperty("category_id")
    public Integer getCategoryId() {
        return categoryId;
    }

    @JsonProperty("category_id")
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @JsonProperty("parameter_values")
    public List<Object> getParameterValues() {
        return parameterValues;
    }

    @JsonProperty("parameter_values")
    public void setParameterValues(List<Object> parameterValues) {
        this.parameterValues = parameterValues;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
