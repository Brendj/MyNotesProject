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

import org.threeten.bp.OffsetDateTime;
/**
 * Вид учета
 */
@Schema(description = "Вид учета")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-07-28T06:28:05.879Z[GMT]")
public class PreventionType {
  @SerializedName("id")
  private Integer id = null;

  @SerializedName("name")
  private String name = null;

  @SerializedName("code")
  private String code = null;

  @SerializedName("actual_from")
  private OffsetDateTime actualFrom = null;

  @SerializedName("actual_to")
  private OffsetDateTime actualTo = null;

  public PreventionType id(Integer id) {
    this.id = id;
    return this;
  }

   /**
   * Идентификатор
   * @return id
  **/
  @Schema(required = true, description = "Идентификатор")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public PreventionType name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Название
   * @return name
  **/
  @Schema(required = true, description = "Название")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PreventionType code(String code) {
    this.code = code;
    return this;
  }

   /**
   * Код
   * @return code
  **/
  @Schema(required = true, description = "Код")
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public PreventionType actualFrom(OffsetDateTime actualFrom) {
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

  public PreventionType actualTo(OffsetDateTime actualTo) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PreventionType preventionType = (PreventionType) o;
    return Objects.equals(this.id, preventionType.id) &&
        Objects.equals(this.name, preventionType.name) &&
        Objects.equals(this.code, preventionType.code) &&
        Objects.equals(this.actualFrom, preventionType.actualFrom) &&
        Objects.equals(this.actualTo, preventionType.actualTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, code, actualFrom, actualTo);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PreventionType {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    actualFrom: ").append(toIndentedString(actualFrom)).append("\n");
    sb.append("    actualTo: ").append(toIndentedString(actualTo)).append("\n");
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
