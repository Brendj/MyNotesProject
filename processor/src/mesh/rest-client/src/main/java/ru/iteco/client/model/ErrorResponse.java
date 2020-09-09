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

/**
 * Ответ с ошибкой
 */
@Schema(description = "Ответ с ошибкой")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-07-28T06:28:05.879Z[GMT]")
public class ErrorResponse {
  @SerializedName("error_code")
  private String errorCode = null;

  @SerializedName("error_description")
  private String errorDescription = null;

  @SerializedName("details")
  private Object details = null;

  public ErrorResponse errorCode(String errorCode) {
    this.errorCode = errorCode;
    return this;
  }

   /**
   * Код ошибки
   * @return errorCode
  **/
  @Schema(description = "Код ошибки")
  public String getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public ErrorResponse errorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
    return this;
  }

   /**
   * Описание ошибки
   * @return errorDescription
  **/
  @Schema(required = true, description = "Описание ошибки")
  public String getErrorDescription() {
    return errorDescription;
  }

  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  public ErrorResponse details(Object details) {
    this.details = details;
    return this;
  }

   /**
   * Детали ошибки
   * @return details
  **/
  @Schema(description = "Детали ошибки")
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
    ErrorResponse errorResponse = (ErrorResponse) o;
    return Objects.equals(this.errorCode, errorResponse.errorCode) &&
        Objects.equals(this.errorDescription, errorResponse.errorDescription) &&
        Objects.equals(this.details, errorResponse.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorCode, errorDescription, details);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponse {\n");
    
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
    sb.append("    errorDescription: ").append(toIndentedString(errorDescription)).append("\n");
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
