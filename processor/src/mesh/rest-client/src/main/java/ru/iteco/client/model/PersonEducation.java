/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/*
 * API МЭШ.Контингент
 * Описание REST API МЭШ.Контингент
 *
 * OpenAPI spec version: 0.0.1
 * Contact: fixme@ktelabs.ru
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package ru.iteco.client.model;

import java.util.Objects;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
/**
 * Информация об образовании персоны
 */
@Schema(description = "Информация об образовании персоны")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-07-28T06:28:05.879Z[GMT]")
public class PersonEducation {
  @SerializedName("id")
  private Long id = null;

  @SerializedName("person_id")
  private UUID personId = null;

  @SerializedName("organization_id")
  private Long organizationId = null;

  @SerializedName("class_uid")
  private UUID classUid = null;

  @SerializedName("notes")
  private String notes = null;

  @SerializedName("education_form_id")
  private Integer educationFormId = null;

  @SerializedName("financing_type_id")
  private Integer financingTypeId = null;

  @SerializedName("service_type_id")
  private Integer serviceTypeId = null;

  @SerializedName("deduction_reason_id")
  private Integer deductionReasonId = null;

  @SerializedName("training_begin_at")
  private LocalDate trainingBeginAt = null;

  @SerializedName("training_end_at")
  private LocalDate trainingEndAt = null;

  @SerializedName("actual_from")
  private OffsetDateTime actualFrom = null;

  @SerializedName("actual_to")
  private OffsetDateTime actualTo = null;

  @SerializedName("created_by")
  private UUID createdBy = null;

  @SerializedName("updated_by")
  private UUID updatedBy = null;

  @SerializedName("created_at")
  private OffsetDateTime createdAt = null;

  @SerializedName("updated_at")
  private OffsetDateTime updatedAt = null;

  @SerializedName("class")
  private ModelClass propertyClass = null;

  @SerializedName("education_form")
  private EducationForm educationForm = null;

  @SerializedName("financing_type")
  private FinancingType financingType = null;

  @SerializedName("deduction_reason")
  private DeductionReason deductionReason = null;

  @SerializedName("service_type")
  private ServiceType serviceType = null;

  @SerializedName("organization")
  private Organization organization = null;

  public PersonEducation id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Идентификатор
   * @return id
  **/
  @Schema(required = true, description = "Идентификатор")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PersonEducation personId(UUID personId) {
    this.personId = personId;
    return this;
  }

   /**
   * Ссылка на идентификатор персоны
   * @return personId
  **/
  @Schema(required = true, description = "Ссылка на идентификатор персоны")
  public UUID getPersonId() {
    return personId;
  }

  public void setPersonId(UUID personId) {
    this.personId = personId;
  }

  public PersonEducation organizationId(Long organizationId) {
    this.organizationId = organizationId;
    return this;
  }

   /**
   * Идентификатор образовательной оранизации (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;13710039\&quot;&gt;ORGANIZATION_REGISTRY&lt;/href&gt;)
   * @return organizationId
  **/
  @Schema(description = "Идентификатор образовательной оранизации (каталог НСИ3 <a href=\"https://wiki.edu.mos.ru/pages/viewpage.action?pageId=13710039\">ORGANIZATION_REGISTRY</href>)")
  public Long getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(Long organizationId) {
    this.organizationId = organizationId;
  }

  public PersonEducation classUid(UUID classUid) {
    this.classUid = classUid;
    return this;
  }

   /**
   * Идентификатор класса/группы/кружка
   * @return classUid
  **/
  @Schema(description = "Идентификатор класса/группы/кружка")
  public UUID getClassUid() {
    return classUid;
  }

  public void setClassUid(UUID classUid) {
    this.classUid = classUid;
  }

  public PersonEducation notes(String notes) {
    this.notes = notes;
    return this;
  }

   /**
   * Примечание
   * @return notes
  **/
  @Schema(description = "Примечание")
  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public PersonEducation educationFormId(Integer educationFormId) {
    this.educationFormId = educationFormId;
    return this;
  }

   /**
   * Форма обучения (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394215\&quot;&gt;TRAINING_FORM&lt;/href&gt;)
   * @return educationFormId
  **/
  @Schema(required = true, description = "Форма обучения (каталог НСИ3 <a href=\"https://wiki.edu.mos.ru/pages/viewpage.action?pageId=18394215\">TRAINING_FORM</href>)")
  public Integer getEducationFormId() {
    return educationFormId;
  }

  public void setEducationFormId(Integer educationFormId) {
    this.educationFormId = educationFormId;
  }

  public PersonEducation financingTypeId(Integer financingTypeId) {
    this.financingTypeId = financingTypeId;
    return this;
  }

   /**
   * Вид финансирования (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18396612\&quot;&gt;FINANCING_TYPE&lt;/href&gt;)
   * @return financingTypeId
  **/
  @Schema(required = true, description = "Вид финансирования (каталог НСИ3 <a href=\"https://wiki.edu.mos.ru/pages/viewpage.action?pageId=18396612\">FINANCING_TYPE</href>)")
  public Integer getFinancingTypeId() {
    return financingTypeId;
  }

  public void setFinancingTypeId(Integer financingTypeId) {
    this.financingTypeId = financingTypeId;
  }

  public PersonEducation serviceTypeId(Integer serviceTypeId) {
    this.serviceTypeId = serviceTypeId;
    return this;
  }

   /**
   * Вид услуги (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;23727577\&quot;&gt;SERVICE_TYPE&lt;/href&gt;)
   * @return serviceTypeId
  **/
  @Schema(description = "Вид услуги (каталог НСИ3 <a href=\"https://wiki.edu.mos.ru/pages/viewpage.action?pageId=23727577\">SERVICE_TYPE</href>)")
  public Integer getServiceTypeId() {
    return serviceTypeId;
  }

  public void setServiceTypeId(Integer serviceTypeId) {
    this.serviceTypeId = serviceTypeId;
  }

  public PersonEducation deductionReasonId(Integer deductionReasonId) {
    this.deductionReasonId = deductionReasonId;
    return this;
  }

   /**
   * Причина отчисления (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18396842\&quot;&gt;DEDUCTION_REASON&lt;/href&gt;)
   * @return deductionReasonId
  **/
  @Schema(description = "Причина отчисления (каталог НСИ3 <a href=\"https://wiki.edu.mos.ru/pages/viewpage.action?pageId=18396842\">DEDUCTION_REASON</href>)")
  public Integer getDeductionReasonId() {
    return deductionReasonId;
  }

  public void setDeductionReasonId(Integer deductionReasonId) {
    this.deductionReasonId = deductionReasonId;
  }

  public PersonEducation trainingBeginAt(LocalDate trainingBeginAt) {
    this.trainingBeginAt = trainingBeginAt;
    return this;
  }

   /**
   * Дата начала обучения
   * @return trainingBeginAt
  **/
  @Schema(required = true, description = "Дата начала обучения")
  public LocalDate getTrainingBeginAt() {
    return trainingBeginAt;
  }

  public void setTrainingBeginAt(LocalDate trainingBeginAt) {
    this.trainingBeginAt = trainingBeginAt;
  }

  public PersonEducation trainingEndAt(LocalDate trainingEndAt) {
    this.trainingEndAt = trainingEndAt;
    return this;
  }

   /**
   * Дата окончания обучения
   * @return trainingEndAt
  **/
  @Schema(description = "Дата окончания обучения")
  public LocalDate getTrainingEndAt() {
    return trainingEndAt;
  }

  public void setTrainingEndAt(LocalDate trainingEndAt) {
    this.trainingEndAt = trainingEndAt;
  }

  public PersonEducation actualFrom(OffsetDateTime actualFrom) {
    this.actualFrom = actualFrom;
    return this;
  }

   /**
   * Дата и время начала действия версии
   * @return actualFrom
  **/
  @Schema(description = "Дата и время начала действия версии")
  public OffsetDateTime getActualFrom() {
    return actualFrom;
  }

  public void setActualFrom(OffsetDateTime actualFrom) {
    this.actualFrom = actualFrom;
  }

  public PersonEducation actualTo(OffsetDateTime actualTo) {
    this.actualTo = actualTo;
    return this;
  }

   /**
   * Дата и время окончания действия версии
   * @return actualTo
  **/
  @Schema(description = "Дата и время окончания действия версии")
  public OffsetDateTime getActualTo() {
    return actualTo;
  }

  public void setActualTo(OffsetDateTime actualTo) {
    this.actualTo = actualTo;
  }

  public PersonEducation createdBy(UUID createdBy) {
    this.createdBy = createdBy;
    return this;
  }

   /**
   * Идентификатор системы-источника, создавшую запись
   * @return createdBy
  **/
  @Schema(description = "Идентификатор системы-источника, создавшую запись")
  public UUID getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(UUID createdBy) {
    this.createdBy = createdBy;
  }

  public PersonEducation updatedBy(UUID updatedBy) {
    this.updatedBy = updatedBy;
    return this;
  }

   /**
   * Идентификатор системы-источника, изменившую запись
   * @return updatedBy
  **/
  @Schema(description = "Идентификатор системы-источника, изменившую запись")
  public UUID getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(UUID updatedBy) {
    this.updatedBy = updatedBy;
  }

  public PersonEducation createdAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
    return this;
  }

   /**
   * Дата и время создания
   * @return createdAt
  **/
  @Schema(description = "Дата и время создания")
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public PersonEducation updatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

   /**
   * Дата и время изменения
   * @return updatedAt
  **/
  @Schema(description = "Дата и время изменения")
  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public PersonEducation propertyClass(ModelClass propertyClass) {
    this.propertyClass = propertyClass;
    return this;
  }

   /**
   * Get propertyClass
   * @return propertyClass
  **/
  @Schema(description = "")
  public ModelClass getPropertyClass() {
    return propertyClass;
  }

  public void setPropertyClass(ModelClass propertyClass) {
    this.propertyClass = propertyClass;
  }

  public PersonEducation educationForm(EducationForm educationForm) {
    this.educationForm = educationForm;
    return this;
  }

   /**
   * Get educationForm
   * @return educationForm
  **/
  @Schema(description = "")
  public EducationForm getEducationForm() {
    return educationForm;
  }

  public void setEducationForm(EducationForm educationForm) {
    this.educationForm = educationForm;
  }

  public PersonEducation financingType(FinancingType financingType) {
    this.financingType = financingType;
    return this;
  }

   /**
   * Get financingType
   * @return financingType
  **/
  @Schema(description = "")
  public FinancingType getFinancingType() {
    return financingType;
  }

  public void setFinancingType(FinancingType financingType) {
    this.financingType = financingType;
  }

  public PersonEducation deductionReason(DeductionReason deductionReason) {
    this.deductionReason = deductionReason;
    return this;
  }

   /**
   * Get deductionReason
   * @return deductionReason
  **/
  @Schema(description = "")
  public DeductionReason getDeductionReason() {
    return deductionReason;
  }

  public void setDeductionReason(DeductionReason deductionReason) {
    this.deductionReason = deductionReason;
  }

  public PersonEducation serviceType(ServiceType serviceType) {
    this.serviceType = serviceType;
    return this;
  }

   /**
   * Get serviceType
   * @return serviceType
  **/
  @Schema(description = "")
  public ServiceType getServiceType() {
    return serviceType;
  }

  public void setServiceType(ServiceType serviceType) {
    this.serviceType = serviceType;
  }

  public PersonEducation organization(Organization organization) {
    this.organization = organization;
    return this;
  }

   /**
   * Get organization
   * @return organization
  **/
  @Schema(description = "")
  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PersonEducation personEducation = (PersonEducation) o;
    return Objects.equals(this.id, personEducation.id) &&
        Objects.equals(this.personId, personEducation.personId) &&
        Objects.equals(this.organizationId, personEducation.organizationId) &&
        Objects.equals(this.classUid, personEducation.classUid) &&
        Objects.equals(this.notes, personEducation.notes) &&
        Objects.equals(this.educationFormId, personEducation.educationFormId) &&
        Objects.equals(this.financingTypeId, personEducation.financingTypeId) &&
        Objects.equals(this.serviceTypeId, personEducation.serviceTypeId) &&
        Objects.equals(this.deductionReasonId, personEducation.deductionReasonId) &&
        Objects.equals(this.trainingBeginAt, personEducation.trainingBeginAt) &&
        Objects.equals(this.trainingEndAt, personEducation.trainingEndAt) &&
        Objects.equals(this.actualFrom, personEducation.actualFrom) &&
        Objects.equals(this.actualTo, personEducation.actualTo) &&
        Objects.equals(this.createdBy, personEducation.createdBy) &&
        Objects.equals(this.updatedBy, personEducation.updatedBy) &&
        Objects.equals(this.createdAt, personEducation.createdAt) &&
        Objects.equals(this.updatedAt, personEducation.updatedAt) &&
        Objects.equals(this.propertyClass, personEducation.propertyClass) &&
        Objects.equals(this.educationForm, personEducation.educationForm) &&
        Objects.equals(this.financingType, personEducation.financingType) &&
        Objects.equals(this.deductionReason, personEducation.deductionReason) &&
        Objects.equals(this.serviceType, personEducation.serviceType) &&
        Objects.equals(this.organization, personEducation.organization);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, personId, organizationId, classUid, notes, educationFormId, financingTypeId, serviceTypeId, deductionReasonId, trainingBeginAt, trainingEndAt, actualFrom, actualTo, createdBy, updatedBy, createdAt, updatedAt, propertyClass, educationForm, financingType, deductionReason, serviceType, organization);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PersonEducation {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    personId: ").append(toIndentedString(personId)).append("\n");
    sb.append("    organizationId: ").append(toIndentedString(organizationId)).append("\n");
    sb.append("    classUid: ").append(toIndentedString(classUid)).append("\n");
    sb.append("    notes: ").append(toIndentedString(notes)).append("\n");
    sb.append("    educationFormId: ").append(toIndentedString(educationFormId)).append("\n");
    sb.append("    financingTypeId: ").append(toIndentedString(financingTypeId)).append("\n");
    sb.append("    serviceTypeId: ").append(toIndentedString(serviceTypeId)).append("\n");
    sb.append("    deductionReasonId: ").append(toIndentedString(deductionReasonId)).append("\n");
    sb.append("    trainingBeginAt: ").append(toIndentedString(trainingBeginAt)).append("\n");
    sb.append("    trainingEndAt: ").append(toIndentedString(trainingEndAt)).append("\n");
    sb.append("    actualFrom: ").append(toIndentedString(actualFrom)).append("\n");
    sb.append("    actualTo: ").append(toIndentedString(actualTo)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    updatedBy: ").append(toIndentedString(updatedBy)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    propertyClass: ").append(toIndentedString(propertyClass)).append("\n");
    sb.append("    educationForm: ").append(toIndentedString(educationForm)).append("\n");
    sb.append("    financingType: ").append(toIndentedString(financingType)).append("\n");
    sb.append("    deductionReason: ").append(toIndentedString(deductionReason)).append("\n");
    sb.append("    serviceType: ").append(toIndentedString(serviceType)).append("\n");
    sb.append("    organization: ").append(toIndentedString(organization)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}