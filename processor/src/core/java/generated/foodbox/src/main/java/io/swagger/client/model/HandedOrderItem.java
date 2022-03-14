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
import io.swagger.client.model.Complex;
import io.swagger.client.model.Dish;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;

/**
 * Позиция в заказе.
 */
@Schema(description = "Позиция в заказе.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-13T21:55:36.524+03:00[Europe/Moscow]")public class HandedOrderItem {

  @SerializedName("dish")
  private Dish dish = null;

  @SerializedName("complex")
  private Complex complex = null;

  @SerializedName("amount")
  private Integer amount = null;
  public HandedOrderItem dish(Dish dish) {
    this.dish = dish;
    return this;
  }

  

  /**
  * Get dish
  * @return dish
  **/
  @Schema(description = "")
  public Dish getDish() {
    return dish;
  }
  public void setDish(Dish dish) {
    this.dish = dish;
  }
  public HandedOrderItem complex(Complex complex) {
    this.complex = complex;
    return this;
  }

  

  /**
  * Get complex
  * @return complex
  **/
  @Schema(description = "")
  public Complex getComplex() {
    return complex;
  }
  public void setComplex(Complex complex) {
    this.complex = complex;
  }
  public HandedOrderItem amount(Integer amount) {
    this.amount = amount;
    return this;
  }

  

  /**
  * Количество.
  * @return amount
  **/
  @Schema(required = true, description = "Количество.")
  public Integer getAmount() {
    return amount;
  }
  public void setAmount(Integer amount) {
    this.amount = amount;
  }
  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HandedOrderItem handedOrderItem = (HandedOrderItem) o;
    return Objects.equals(this.dish, handedOrderItem.dish) &&
        Objects.equals(this.complex, handedOrderItem.complex) &&
        Objects.equals(this.amount, handedOrderItem.amount);
  }

  @Override
  public int hashCode() {
    return java.util.Objects.hash(dish, complex, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HandedOrderItem {\n");
    
    sb.append("    dish: ").append(toIndentedString(dish)).append("\n");
    sb.append("    complex: ").append(toIndentedString(complex)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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
