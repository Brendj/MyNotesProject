
package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "id",
    "person_id",
    "merged_to",
    "lastname",
    "firstname",
    "patronymic",
    "birthdate",
    "birthplace",
    "snils",
    "gender_id",
    "citizenship_id",
    "validation_state_id",
    "validated_at",
    "actual_from",
    "actual_to",
    "created_by",
    "updated_by",
    "created_at",
    "updated_at",
    "addresses",
    "documents",
    "contacts",
    "preventions",
    "categories",
    "ids",
    "agents",
    "children",
    "education",
    "citizenship",
    "validation_errors"
})
public class ResponsePersons {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("person_id")
    private String personId;
    @JsonProperty("merged_to")
    private Object mergedTo;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("patronymic")
    private String patronymic;
    @JsonProperty("birthdate")
    private String birthdate;
    @JsonProperty("birthplace")
    private Object birthplace;
    @JsonProperty("snils")
    private String snils;
    @JsonProperty("gender_id")
    private Integer genderId;
    @JsonProperty("citizenship_id")
    private Integer citizenshipId;
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
    @JsonProperty("addresses")
    private Object addresses;
    @JsonProperty("documents")
    private List<PersonDocument> documents = null;
    @JsonProperty("contacts")
    private Object contacts;
    @JsonProperty("preventions")
    private Object preventions;
    @JsonProperty("categories")
    private List<Category> categories = null;
    @JsonProperty("ids")
    private Object ids;
    @JsonProperty("agents")
    private Object agents;
    @JsonProperty("children")
    private List<PersonAgent> children = null;
    @JsonProperty("education")
    private List<Education> education = null;
    @JsonProperty("citizenship")
    private Object citizenship;
    @JsonProperty("validation_errors")
    private Object validationErrors;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private String className, guidNsi, ooId, training_end_at, idIsPp;

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

    @JsonProperty("merged_to")
    public Object getMergedTo() {
        return mergedTo;
    }

    @JsonProperty("merged_to")
    public void setMergedTo(Object mergedTo) {
        this.mergedTo = mergedTo;
    }

    @JsonProperty("lastname")
    public String getLastname() {
        return lastname;
    }

    @JsonProperty("lastname")
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @JsonProperty("firstname")
    public String getFirstname() {
        return firstname;
    }

    @JsonProperty("firstname")
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @JsonProperty("patronymic")
    public String getPatronymic() {
        return patronymic;
    }

    @JsonProperty("patronymic")
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @JsonProperty("birthdate")
    public String getBirthdate() {
        return birthdate;
    }

    @JsonProperty("birthdate")
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    @JsonProperty("birthplace")
    public Object getBirthplace() {
        return birthplace;
    }

    @JsonProperty("birthplace")
    public void setBirthplace(Object birthplace) {
        this.birthplace = birthplace;
    }

    @JsonProperty("snils")
    public String getSnils() {
        return snils;
    }

    @JsonProperty("snils")
    public void setSnils(String snils) {
        this.snils = snils;
    }

    @JsonProperty("gender_id")
    public Integer getGenderId() {
        return genderId;
    }

    @JsonProperty("gender_id")
    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    @JsonProperty("citizenship_id")
    public Integer getCitizenshipId() {
        return citizenshipId;
    }

    @JsonProperty("citizenship_id")
    public void setCitizenshipId(Integer citizenshipId) {
        this.citizenshipId = citizenshipId;
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

    @JsonProperty("addresses")
    public Object getAddresses() {
        return addresses;
    }

    @JsonProperty("addresses")
    public void setAddresses(Object addresses) {
        this.addresses = addresses;
    }

    @JsonProperty("documents")
    public List<PersonDocument> getDocuments() {
        return documents;
    }

    @JsonProperty("documents")
    public void setDocuments(List<PersonDocument> documents) {
        this.documents = documents;
    }

    @JsonProperty("contacts")
    public Object getContacts() {
        return contacts;
    }

    @JsonProperty("contacts")
    public void setContacts(Object contacts) {
        this.contacts = contacts;
    }

    @JsonProperty("preventions")
    public Object getPreventions() {
        return preventions;
    }

    @JsonProperty("preventions")
    public void setPreventions(Object preventions) {
        this.preventions = preventions;
    }

    @JsonProperty("categories")
    public List<Category> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @JsonProperty("ids")
    public Object getIds() {
        return ids;
    }

    @JsonProperty("ids")
    public void setIds(Object ids) {
        this.ids = ids;
    }

    @JsonProperty("agents")
    public Object getAgents() {
        return agents;
    }

    @JsonProperty("agents")
    public void setAgents(Object agents) {
        this.agents = agents;
    }

    @JsonProperty("children")
    public List<PersonAgent> getChildren() {
        return children;
    }

    @JsonProperty("children")
    public void setChildren(List<PersonAgent> children) {
        this.children = children;
    }

    @JsonProperty("education")
    public List<Education> getEducation() {
        return education;
    }

    @JsonProperty("education")
    public void setEducation(List<Education> education) {
        this.education = education;
    }

    @JsonProperty("citizenship")
    public Object getCitizenship() {
        return citizenship;
    }

    @JsonProperty("citizenship")
    public void setCitizenship(Object citizenship) {
        this.citizenship = citizenship;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGuidNsi() {
        return guidNsi;
    }

    public void setGuidNsi(String guidNsi) {
        this.guidNsi = guidNsi;
    }

    public String getOoId() {
        return ooId;
    }

    public void setOoId(String ooId) {
        this.ooId = ooId;
    }

    public String getTraining_end_at() {
        return training_end_at;
    }

    public void setTraining_end_at(String training_end_at) {
        this.training_end_at = training_end_at;
    }

    public String getIdIsPp() {
        return idIsPp;
    }

    public void setIdIsPp(String idIsPp) {
        this.idIsPp = idIsPp;
    }
}
