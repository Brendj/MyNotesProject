
package ru.axetta.ecafe.processor.core.partner.mesh.json;

import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "id",
    "uid",
    "name",
    "organization_id",
    "building_id",
    "staff_ids",
    "academic_year_id",
    "open_at",
    "close_at",
    "parallel_id",
    "education_stage_id",
    "letter",
    "age_group_id",
    "notes",
    "actual_from",
    "actual_to",
    "created_by",
    "updated_by",
    "created_at",
    "updated_at",
    "parallel",
    "organization"
})
public class Class {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("organization_id")
    private Integer organizationId;
    @JsonProperty("building_id")
    private Object buildingId;
    @JsonProperty("staff_ids")
    private Object staffIds;
    @JsonProperty("academic_year_id")
    private Integer academicYearId;
    @JsonProperty("open_at")
    private String openAt;
    @JsonProperty("close_at")
    private String closeAt;
    @JsonProperty("parallel_id")
    private Object parallelId;
    @JsonProperty("education_stage_id")
    private Integer educationStageId;
    @JsonProperty("letter")
    private Object letter;
    @JsonProperty("age_group_id")
    private Object ageGroupId;
    @JsonProperty("notes")
    private Object notes;
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
    @JsonProperty("parallel")
    private Object parallel;
    @JsonProperty("organization")
    private Organization organization;
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

    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("organization_id")
    public Integer getOrganizationId() {
        return organizationId;
    }

    @JsonProperty("organization_id")
    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    @JsonProperty("building_id")
    public Object getBuildingId() {
        return buildingId;
    }

    @JsonProperty("building_id")
    public void setBuildingId(Object buildingId) {
        this.buildingId = buildingId;
    }

    @JsonProperty("staff_ids")
    public Object getStaffIds() {
        return staffIds;
    }

    @JsonProperty("staff_ids")
    public void setStaffIds(Object staffIds) {
        this.staffIds = staffIds;
    }

    @JsonProperty("academic_year_id")
    public Integer getAcademicYearId() {
        return academicYearId;
    }

    @JsonProperty("academic_year_id")
    public void setAcademicYearId(Integer academicYearId) {
        this.academicYearId = academicYearId;
    }

    @JsonProperty("open_at")
    public String getOpenAt() {
        return openAt;
    }

    @JsonProperty("open_at")
    public void setOpenAt(String openAt) {
        this.openAt = openAt;
    }

    @JsonProperty("close_at")
    public String getCloseAt() {
        return closeAt;
    }

    @JsonProperty("close_at")
    public void setCloseAt(String closeAt) {
        this.closeAt = closeAt;
    }

    @JsonProperty("parallel_id")
    public Object getParallelId() {
        return parallelId;
    }

    @JsonProperty("parallel_id")
    public void setParallelId(Object parallelId) {
        this.parallelId = parallelId;
    }

    @JsonProperty("education_stage_id")
    public Integer getEducationStageId() {
        return educationStageId;
    }

    @JsonProperty("education_stage_id")
    public void setEducationStageId(Integer educationStageId) {
        this.educationStageId = educationStageId;
    }

    @JsonProperty("letter")
    public Object getLetter() {
        return letter;
    }

    @JsonProperty("letter")
    public void setLetter(Object letter) {
        this.letter = letter;
    }

    @JsonProperty("age_group_id")
    public Object getAgeGroupId() {
        return ageGroupId;
    }

    @JsonProperty("age_group_id")
    public void setAgeGroupId(Object ageGroupId) {
        this.ageGroupId = ageGroupId;
    }

    @JsonProperty("notes")
    public Object getNotes() {
        return notes;
    }

    @JsonProperty("notes")
    public void setNotes(Object notes) {
        this.notes = notes;
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

    @JsonProperty("parallel")
    public Object getParallel() {
        return parallel;
    }

    @JsonProperty("parallel")
    public void setParallel(Object parallel) {
        this.parallel = parallel;
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
