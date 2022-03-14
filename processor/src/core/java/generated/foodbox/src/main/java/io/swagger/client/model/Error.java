/*
 * API по питанию в образовательных учреждениях Москвы
 * API по питанию в образовательных учреждениях Москвы
 *
 * OpenAPI spec version: 1.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;

/**
 * Ответ с ошибкой.
 */
@Schema(description = "Ответ с ошибкой.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-13T21:55:36.524+03:00[Europe/Moscow]")public class Error {

  @SerializedName("code")
  private String code = null;

  @SerializedName("description")
  private String description = null;

  @SerializedName("details")
  private Object details = null;
  public Error code(String code) {
    this.code = code;
    return this;
  }

  

  /**
  * Идентификатор ошибки.
  * @return code
  **/
  @Schema(required = true, description = "Идентификатор ошибки.")
  public String getCode() {
    return code;
  }
  public void setCode(String code) {
    this.code = code;
  }
  public Error description(String description) {
    this.description = description;
    return this;
  }

  

  /**
  * Описание ошибки.
  * @return description
  **/
  @Schema(required = true, description = "Описание ошибки.")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public Error details(Object details) {
    this.details = details;
    return this;
  }

  

  /**
  * Детализация ошибки.
  * @return details
  **/
  @Schema(description = "Детализация ошибки.")
  public Object getDetails() {
    return details;
  }
  public void setDetails(Object details) {
    this.details = details;
  }
  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Error error = (Error) o;
    return Objects.equals(this.code, error.code) &&
        Objects.equals(this.description, error.description) &&
        Objects.equals(this.details, error.details);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(code, description, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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
