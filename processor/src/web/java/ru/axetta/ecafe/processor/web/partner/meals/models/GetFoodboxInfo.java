package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.Objects;

/**
 * Возвращает список разрешений по Фудбоксу для ОУ и клиента
 */
public class GetFoodboxInfo {
    private Boolean foodboxAvailable = null;
    private Boolean foodboxAllowed = null;
    public GetFoodboxInfo foodboxAvailabilityForEO(Boolean foodboxAvailabilityForEO) {
        this.foodboxAvailable = foodboxAvailabilityForEO;
        return this;
    }

    /**
     * Признак доступности использования фудбокса для образовательной организации
     * @return foodboxAvailabilityForEO
     **/
    public Boolean isFoodboxAvailabilityForEO() {
        return foodboxAvailable;
    }
    public void setFoodboxAvailable(Boolean foodboxAvailable) {
        this.foodboxAvailable = foodboxAvailable;
    }
    public GetFoodboxInfo foodboxAvailability(Boolean foodboxAvailability) {
        this.foodboxAllowed = foodboxAvailability;
        return this;
    }



    /**
     * Признак доступности использования фудбокса
     * @return foodboxAvailability
     **/
    public Boolean isFoodboxAvailability() {
        return foodboxAllowed;
    }
    public void setFoodboxAllowed(Boolean foodboxAllowed) {
        this.foodboxAllowed = foodboxAllowed;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GetFoodboxInfo getFoodboxInfo = (GetFoodboxInfo) o;
        return Objects.equals(this.foodboxAvailable, getFoodboxInfo.foodboxAvailable) &&
                Objects.equals(this.foodboxAllowed, getFoodboxInfo.foodboxAllowed);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(foodboxAvailable, foodboxAllowed);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GetFoodboxInfo {\n");

        sb.append("    foodboxAvailabilityForEO: ").append(toIndentedString(foodboxAvailable)).append("\n");
        sb.append("    foodboxAvailability: ").append(toIndentedString(foodboxAllowed)).append("\n");
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
