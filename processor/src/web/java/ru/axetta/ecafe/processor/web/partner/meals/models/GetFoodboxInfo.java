package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.Objects;

/**
 * Возвращает список разрешений по Фудбоксу для ОУ и клиента
 */
public class GetFoodboxInfo {
    private Boolean foodboxAvailabilityForEO = null;
    private Boolean foodboxAvailability = null;
    public GetFoodboxInfo foodboxAvailabilityForEO(Boolean foodboxAvailabilityForEO) {
        this.foodboxAvailabilityForEO = foodboxAvailabilityForEO;
        return this;
    }

    /**
     * Признак доступности использования фудбокса для образовательной организации
     * @return foodboxAvailabilityForEO
     **/
    public Boolean isFoodboxAvailabilityForEO() {
        return foodboxAvailabilityForEO;
    }
    public void setFoodboxAvailabilityForEO(Boolean foodboxAvailabilityForEO) {
        this.foodboxAvailabilityForEO = foodboxAvailabilityForEO;
    }
    public GetFoodboxInfo foodboxAvailability(Boolean foodboxAvailability) {
        this.foodboxAvailability = foodboxAvailability;
        return this;
    }



    /**
     * Признак доступности использования фудбокса
     * @return foodboxAvailability
     **/
    public Boolean isFoodboxAvailability() {
        return foodboxAvailability;
    }
    public void setFoodboxAvailability(Boolean foodboxAvailability) {
        this.foodboxAvailability = foodboxAvailability;
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
        return Objects.equals(this.foodboxAvailabilityForEO, getFoodboxInfo.foodboxAvailabilityForEO) &&
                Objects.equals(this.foodboxAvailability, getFoodboxInfo.foodboxAvailability);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(foodboxAvailabilityForEO, foodboxAvailability);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GetFoodboxInfo {\n");

        sb.append("    foodboxAvailabilityForEO: ").append(toIndentedString(foodboxAvailabilityForEO)).append("\n");
        sb.append("    foodboxAvailability: ").append(toIndentedString(foodboxAvailability)).append("\n");
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
