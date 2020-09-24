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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
/**
 * Учет персоны
 */
@Schema(description = "Учет персоны")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-07-28T06:28:05.879Z[GMT]")
public class PersonPrevention {
  @SerializedName("id")
  private Long id = null;

  @SerializedName("person_id")
  private UUID personId = null;

  @SerializedName("prevention_type_id")
  private Integer preventionTypeId = null;

  @SerializedName("started_at")
  private LocalDate startedAt = null;

  @SerializedName("started_document_issued")
  private LocalDate startedDocumentIssued = null;

  @SerializedName("started_document_number")
  private String startedDocumentNumber = null;

  @SerializedName("started_reasons")
  private List<Integer> startedReasons = null;

  @SerializedName("finished_at")
  private LocalDate finishedAt = null;

  @SerializedName("finished_document_issued")
  private LocalDate finishedDocumentIssued = null;

  @SerializedName("finished_document_number")
  private String finishedDocumentNumber = null;

  @SerializedName("finished_reasons")
  private List<Integer> finishedReasons = null;

  @SerializedName("validation_state_id")
  private Integer validationStateId = null;

  @SerializedName("validated_at")
  private OffsetDateTime validatedAt = null;

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

  @SerializedName("prevention_type")
  private PreventionType preventionType = null;

  @SerializedName("validation_errors")
  private String validationErrors = null;

  public PersonPrevention id(Long id) {
    this.id = id;
    return this;
  }

   /**
   * Идентификатор версии
   * @return id
  **/
  @Schema(required = true, description = "Идентификатор версии")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public PersonPrevention personId(UUID personId) {
    this.personId = personId;
    return this;
  }

   /**
   * Идентификатор персоны
   * @return personId
  **/
  @Schema(description = "Идентификатор персоны")
  public UUID getPersonId() {
    return personId;
  }

  public void setPersonId(UUID personId) {
    this.personId = personId;
  }

  public PersonPrevention preventionTypeId(Integer preventionTypeId) {
    this.preventionTypeId = preventionTypeId;
    return this;
  }

   /**
   * Идентификатор вида учета (каталог НСИ3 &lt;a href&#x3D;\&quot;https://wiki.edu.mos.ru/pages/viewpage.action?pageId&#x3D;18394301\&quot;&gt;ACCOUNTING_TYPE&lt;/a&gt;)
   * @return preventionTypeId
  **/
  @Schema(required = true, description = "Идентификатор вида учета (каталог НСИ3 <a href=\"https://wiki.edu.mos.ru/pages/viewpage.action?pageId=18394301\">ACCOUNTING_TYPE</a>)")
  public Integer getPreventionTypeId() {
    return preventionTypeId;
  }

  public void setPreventionTypeId(Integer preventionTypeId) {
    this.preventionTypeId = preventionTypeId;
  }

  public PersonPrevention startedAt(LocalDate startedAt) {
    this.startedAt = startedAt;
    return this;
  }

   /**
   * Дата постановки на учет
   * @return startedAt
  **/
  @Schema(required = true, description = "Дата постановки на учет")
  public LocalDate getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(LocalDate startedAt) {
    this.startedAt = startedAt;
  }

  public PersonPrevention startedDocumentIssued(LocalDate startedDocumentIssued) {
    this.startedDocumentIssued = startedDocumentIssued;
    return this;
  }

   /**
   * Дата документа постановки на учет
   * @return startedDocumentIssued
  **/
  @Schema(required = true, description = "Дата документа постановки на учет")
  public LocalDate getStartedDocumentIssued() {
    return startedDocumentIssued;
  }

  public void setStartedDocumentIssued(LocalDate startedDocumentIssued) {
    this.startedDocumentIssued = startedDocumentIssued;
  }

  public PersonPrevention startedDocumentNumber(String startedDocumentNumber) {
    this.startedDocumentNumber = startedDocumentNumber;
    return this;
  }

   /**
   * Номер документа постановки на учет
   * @return startedDocumentNumber
  **/
  @Schema(required = true, description = "Номер документа постановки на учет")
  public String getStartedDocumentNumber() {
    return startedDocumentNumber;
  }

  public void setStartedDocumentNumber(String startedDocumentNumber) {
    this.startedDocumentNumber = startedDocumentNumber;
  }

  public PersonPrevention startedReasons(List<Integer> startedReasons) {
    this.startedReasons = startedReasons;
    return this;
  }

  public PersonPrevention addStartedReasonsItem(Integer startedReasonsItem) {
    if (this.startedReasons == null) {
      this.startedReasons = new ArrayList<Integer>();
    }
    this.startedReasons.add(startedReasonsItem);
    return this;
  }

   /**
   * Причины постановки на учет
   * @return startedReasons
  **/
  @Schema(description = "Причины постановки на учет")
  public List<Integer> getStartedReasons() {
    return startedReasons;
  }

  public void setStartedReasons(List<Integer> startedReasons) {
    this.startedReasons = startedReasons;
  }

  public PersonPrevention finishedAt(LocalDate finishedAt) {
    this.finishedAt = finishedAt;
    return this;
  }

   /**
   * Дата снятия с учета
   * @return finishedAt
  **/
  @Schema(description = "Дата снятия с учета")
  public LocalDate getFinishedAt() {
    return finishedAt;
  }

  public void setFinishedAt(LocalDate finishedAt) {
    this.finishedAt = finishedAt;
  }

  public PersonPrevention finishedDocumentIssued(LocalDate finishedDocumentIssued) {
    this.finishedDocumentIssued = finishedDocumentIssued;
    return this;
  }

   /**
   * Дата документа о снятии с учета
   * @return finishedDocumentIssued
  **/
  @Schema(description = "Дата документа о снятии с учета")
  public LocalDate getFinishedDocumentIssued() {
    return finishedDocumentIssued;
  }

  public void setFinishedDocumentIssued(LocalDate finishedDocumentIssued) {
    this.finishedDocumentIssued = finishedDocumentIssued;
  }

  public PersonPrevention finishedDocumentNumber(String finishedDocumentNumber) {
    this.finishedDocumentNumber = finishedDocumentNumber;
    return this;
  }

   /**
   * Номер документа о снятии с учета
   * @return finishedDocumentNumber
  **/
  @Schema(description = "Номер документа о снятии с учета")
  public String getFinishedDocumentNumber() {
    return finishedDocumentNumber;
  }

  public void setFinishedDocumentNumber(String finishedDocumentNumber) {
    this.finishedDocumentNumber = finishedDocumentNumber;
  }

  public PersonPrevention finishedReasons(List<Integer> finishedReasons) {
    this.finishedReasons = finishedReasons;
    return this;
  }

  public PersonPrevention addFinishedReasonsItem(Integer finishedReasonsItem) {
    if (this.finishedReasons == null) {
      this.finishedReasons = new ArrayList<Integer>();
    }
    this.finishedReasons.add(finishedReasonsItem);
    return this;
  }

   /**
   * Причины снятия с учета
   * @return finishedReasons
  **/
  @Schema(description = "Причины снятия с учета")
  public List<Integer> getFinishedReasons() {
    return finishedReasons;
  }

  public void setFinishedReasons(List<Integer> finishedReasons) {
    this.finishedReasons = finishedReasons;
  }

  public PersonPrevention validationStateId(Integer validationStateId) {
    this.validationStateId = validationStateId;
    return this;
  }

   /**
   * Статус проверки
   * @return validationStateId
  **/
  @Schema(description = "Статус проверки")
  public Integer getValidationStateId() {
    return validationStateId;
  }

  public void setValidationStateId(Integer validationStateId) {
    this.validationStateId = validationStateId;
  }

  public PersonPrevention validatedAt(OffsetDateTime validatedAt) {
    this.validatedAt = validatedAt;
    return this;
  }

   /**
   * Дата проверки
   * @return validatedAt
  **/
  @Schema(description = "Дата проверки")
  public OffsetDateTime getValidatedAt() {
    return validatedAt;
  }

  public void setValidatedAt(OffsetDateTime validatedAt) {
    this.validatedAt = validatedAt;
  }

  public PersonPrevention actualFrom(OffsetDateTime actualFrom) {
    this.actualFrom = actualFrom;
    return this;
  }

   /**
   * Дата и время начала действия связи
   * @return actualFrom
  **/
  @Schema(description = "Дата и время начала действия связи")
  public OffsetDateTime getActualFrom() {
    return actualFrom;
  }

  public void setActualFrom(OffsetDateTime actualFrom) {
    this.actualFrom = actualFrom;
  }

  public PersonPrevention actualTo(OffsetDateTime actualTo) {
    this.actualTo = actualTo;
    return this;
  }

   /**
   * Дата и время окончания действия связи
   * @return actualTo
  **/
  @Schema(description = "Дата и время окончания действия связи")
  public OffsetDateTime getActualTo() {
    return actualTo;
  }

  public void setActualTo(OffsetDateTime actualTo) {
    this.actualTo = actualTo;
  }

  public PersonPrevention createdBy(UUID createdBy) {
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

  public PersonPrevention updatedBy(UUID updatedBy) {
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

  public PersonPrevention createdAt(OffsetDateTime createdAt) {
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

  public PersonPrevention updatedAt(OffsetDateTime updatedAt) {
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

  public PersonPrevention preventionType(PreventionType preventionType) {
    this.preventionType = preventionType;
    return this;
  }

   /**
   * Get preventionType
   * @return preventionType
  **/
  @Schema(description = "")
  public PreventionType getPreventionType() {
    return preventionType;
  }

  public void setPreventionType(PreventionType preventionType) {
    this.preventionType = preventionType;
  }

  public PersonPrevention validationErrors(String validationErrors) {
    this.validationErrors = validationErrors;
    return this;
  }

   /**
   * Ошибки валидации из внешних систем
   * @return validationErrors
  **/
  @Schema(description = "Ошибки валидации из внешних систем")
  public String getValidationErrors() {
    return validationErrors;
  }

  public void setValidationErrors(String validationErrors) {
    this.validationErrors = validationErrors;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PersonPrevention personPrevention = (PersonPrevention) o;
    return Objects.equals(this.id, personPrevention.id) &&
        Objects.equals(this.personId, personPrevention.personId) &&
        Objects.equals(this.preventionTypeId, personPrevention.preventionTypeId) &&
        Objects.equals(this.startedAt, personPrevention.startedAt) &&
        Objects.equals(this.startedDocumentIssued, personPrevention.startedDocumentIssued) &&
        Objects.equals(this.startedDocumentNumber, personPrevention.startedDocumentNumber) &&
        Objects.equals(this.startedReasons, personPrevention.startedReasons) &&
        Objects.equals(this.finishedAt, personPrevention.finishedAt) &&
        Objects.equals(this.finishedDocumentIssued, personPrevention.finishedDocumentIssued) &&
        Objects.equals(this.finishedDocumentNumber, personPrevention.finishedDocumentNumber) &&
        Objects.equals(this.finishedReasons, personPrevention.finishedReasons) &&
        Objects.equals(this.validationStateId, personPrevention.validationStateId) &&
        Objects.equals(this.validatedAt, personPrevention.validatedAt) &&
        Objects.equals(this.actualFrom, personPrevention.actualFrom) &&
        Objects.equals(this.actualTo, personPrevention.actualTo) &&
        Objects.equals(this.createdBy, personPrevention.createdBy) &&
        Objects.equals(this.updatedBy, personPrevention.updatedBy) &&
        Objects.equals(this.createdAt, personPrevention.createdAt) &&
        Objects.equals(this.updatedAt, personPrevention.updatedAt) &&
        Objects.equals(this.preventionType, personPrevention.preventionType) &&
        Objects.equals(this.validationErrors, personPrevention.validationErrors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, personId, preventionTypeId, startedAt, startedDocumentIssued, startedDocumentNumber, startedReasons, finishedAt, finishedDocumentIssued, finishedDocumentNumber, finishedReasons, validationStateId, validatedAt, actualFrom, actualTo, createdBy, updatedBy, createdAt, updatedAt, preventionType, validationErrors);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PersonPrevention {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    personId: ").append(toIndentedString(personId)).append("\n");
    sb.append("    preventionTypeId: ").append(toIndentedString(preventionTypeId)).append("\n");
    sb.append("    startedAt: ").append(toIndentedString(startedAt)).append("\n");
    sb.append("    startedDocumentIssued: ").append(toIndentedString(startedDocumentIssued)).append("\n");
    sb.append("    startedDocumentNumber: ").append(toIndentedString(startedDocumentNumber)).append("\n");
    sb.append("    startedReasons: ").append(toIndentedString(startedReasons)).append("\n");
    sb.append("    finishedAt: ").append(toIndentedString(finishedAt)).append("\n");
    sb.append("    finishedDocumentIssued: ").append(toIndentedString(finishedDocumentIssued)).append("\n");
    sb.append("    finishedDocumentNumber: ").append(toIndentedString(finishedDocumentNumber)).append("\n");
    sb.append("    finishedReasons: ").append(toIndentedString(finishedReasons)).append("\n");
    sb.append("    validationStateId: ").append(toIndentedString(validationStateId)).append("\n");
    sb.append("    validatedAt: ").append(toIndentedString(validatedAt)).append("\n");
    sb.append("    actualFrom: ").append(toIndentedString(actualFrom)).append("\n");
    sb.append("    actualTo: ").append(toIndentedString(actualTo)).append("\n");
    sb.append("    createdBy: ").append(toIndentedString(createdBy)).append("\n");
    sb.append("    updatedBy: ").append(toIndentedString(updatedBy)).append("\n");
    sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
    sb.append("    updatedAt: ").append(toIndentedString(updatedAt)).append("\n");
    sb.append("    preventionType: ").append(toIndentedString(preventionType)).append("\n");
    sb.append("    validationErrors: ").append(toIndentedString(validationErrors)).append("\n");
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