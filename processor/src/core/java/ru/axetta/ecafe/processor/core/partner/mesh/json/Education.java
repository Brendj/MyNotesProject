
package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "id",
    "person_id",
    "organization_id",
    "class_uid",
    "notes",
    "education_form_id",
    "financing_type_id",
    "service_type_id",
    "deduction_reason_id",
    "training_begin_at",
    "training_end_at",
    "actual_from",
    "actual_to",
    "created_by",
    "updated_by",
    "created_at",
    "updated_at",
    "class",
    "education_form",
    "financing_type",
    "deduction_reason",
    "service_type",
    "organization"
})
public class Education implements Comparable {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("person_id")
    private String personId;
    @JsonProperty("organization_id")
    private Integer organizationId;
    @JsonProperty("class_uid")
    private String classUid;
    @JsonProperty("notes")
    private Object notes;
    @JsonProperty("education_form_id")
    private Integer educationFormId;
    @JsonProperty("financing_type_id")
    private Integer financingTypeId;
    @JsonProperty("service_type_id")
    private Integer serviceTypeId;
    @JsonProperty("deduction_reason_id")
    private Object deductionReasonId;
    @JsonProperty("training_begin_at")
    private String trainingBeginAt;
    @JsonProperty("training_end_at")
    private String trainingEndAt;
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
    @JsonProperty("class")
    private Class _class;
    @JsonProperty("education_form")
    private EducationForm educationForm;
    @JsonProperty("financing_type")
    private FinancingType financingType;
    @JsonProperty("deduction_reason")
    private Object deductionReason;
    @JsonProperty("service_type")
    private ServiceType serviceType;
    @JsonProperty("organization")
    private Organization organization;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public boolean empty(String valueActualFrom) {
        return StringUtils.isEmpty(valueActualFrom) || valueActualFrom.equalsIgnoreCase("null");
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Education)) {
            return 1;
        }

        Education item = (Education) o;
        if (empty(this.getTrainingEndAt()) && empty(item.getTrainingEndAt())) return 0;
        if (!empty(this.getTrainingEndAt()) && empty(item.getTrainingEndAt())) return 1;
        if (empty(this.getTrainingEndAt()) && !empty(item.getTrainingEndAt())) return -1;
        if (this.getTrainingEndAt().equals(item.getTrainingEndAt())) return 0;
        return this.getTrainingEndAt().compareTo(item.getTrainingEndAt());

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

    @JsonProperty("organization_id")
    public Integer getOrganizationId() {
        return organizationId;
    }

    @JsonProperty("organization_id")
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty("class_uid")
    public String getClassUid() {
        return classUid;
    }

    @JsonProperty("class_uid")
    public void setClassUid(String classUid) {
        this.classUid = classUid;
    }

    @JsonProperty("notes")
    public Object getNotes() {
        return notes;
    }

    @JsonProperty("notes")
    public void setNotes(Object notes) {
        this.notes = notes;
    }

    @JsonProperty("education_form_id")
    public Integer getEducationFormId() {
        return educationFormId;
    }

    @JsonProperty("education_form_id")
    public void setEducationFormId(Integer educationFormId) {
        this.educationFormId = educationFormId;
    }

    @JsonProperty("financing_type_id")
    public Integer getFinancingTypeId() {
        return financingTypeId;
    }

    @JsonProperty("financing_type_id")
    public void setFinancingTypeId(Integer financingTypeId) {
        this.financingTypeId = financingTypeId;
    }

    @JsonProperty("service_type_id")
    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    @JsonProperty("service_type_id")
    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    @JsonProperty("deduction_reason_id")
    public Object getDeductionReasonId() {
        return deductionReasonId;
    }

    @JsonProperty("deduction_reason_id")
    public void setDeductionReasonId(Object deductionReasonId) {
        this.deductionReasonId = deductionReasonId;
    }

    @JsonProperty("training_begin_at")
    public String getTrainingBeginAt() {
        return trainingBeginAt;
    }

    @JsonProperty("training_begin_at")
    public void setTrainingBeginAt(String trainingBeginAt) {
        this.trainingBeginAt = trainingBeginAt;
    }

    @JsonProperty("training_end_at")
    public String getTrainingEndAt() {
        return trainingEndAt;
    }

    @JsonProperty("training_end_at")
    public void setTrainingEndAt(String trainingEndAt) {
        this.trainingEndAt = trainingEndAt;
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

    @JsonProperty("class")
    public Class getClass_() {
        return _class;
    }

    @JsonProperty("class")
    public void setClass_(Class _class) {
        this._class = _class;
    }

    @JsonProperty("education_form")
    public EducationForm getEducationForm() {
        return educationForm;
    }

    @JsonProperty("education_form")
    public void setEducationForm(EducationForm educationForm) {
        this.educationForm = educationForm;
    }

    @JsonProperty("financing_type")
    public FinancingType getFinancingType() {
        return financingType;
    }

    @JsonProperty("financing_type")
    public void setFinancingType(FinancingType financingType) {
        this.financingType = financingType;
    }

    @JsonProperty("deduction_reason")
    public Object getDeductionReason() {
        return deductionReason;
    }

    @JsonProperty("deduction_reason")
    public void setDeductionReason(Object deductionReason) {
        this.deductionReason = deductionReason;
    }

    @JsonProperty("service_type")
    public ServiceType getServiceType() {
        return serviceType;
    }

    @JsonProperty("service_type")
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    @JsonProperty("organization")
    public Organization getOrganization() {
        return organization;
    }

    @JsonProperty("organization")
    public void setOrganization(Organization organization) {
        this.organization = organization;
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
