package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * История заказов или получение данных по конкретному заказу фудбокса
 */
public class HistoryFoodboxOrderInfo {
    private Boolean foodboxAvailabilityForEO = null;
    private Boolean foodboxAvailability = null;
    private List<FoodboxOrderInfo> ordersInfo = new ArrayList<FoodboxOrderInfo>();
    private Long ordersAmount = null;
    public HistoryFoodboxOrderInfo foodboxAvailabilityForEO(Boolean foodboxAvailabilityForEO) {
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
    public HistoryFoodboxOrderInfo foodboxAvailability(Boolean foodboxAvailability) {
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
    public HistoryFoodboxOrderInfo ordersInfo(List<FoodboxOrderInfo> ordersInfo) {
        this.ordersInfo = ordersInfo;
        return this;
    }

    public HistoryFoodboxOrderInfo addOrdersInfoItem(FoodboxOrderInfo ordersInfoItem) {
        this.ordersInfo.add(ordersInfoItem);
        return this;
    }

    /**
     * Get ordersInfo
     * @return ordersInfo
     **/
    public List<FoodboxOrderInfo> getOrdersInfo() {
        return ordersInfo;
    }
    public void setOrdersInfo(List<FoodboxOrderInfo> ordersInfo) {
        this.ordersInfo = ordersInfo;
    }
    public HistoryFoodboxOrderInfo ordersAmount(Long ordersAmount) {
        this.ordersAmount = ordersAmount;
        return this;
    }



    /**
     * Количество заказов в списке
     * @return ordersAmount
     **/
    public Long getOrdersAmount() {
        return ordersAmount;
    }
    public void setOrdersAmount(Long ordersAmount) {
        this.ordersAmount = ordersAmount;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HistoryFoodboxOrderInfo historyFoodboxOrderInfo = (HistoryFoodboxOrderInfo) o;
        return Objects.equals(this.foodboxAvailabilityForEO, historyFoodboxOrderInfo.foodboxAvailabilityForEO) &&
                Objects.equals(this.foodboxAvailability, historyFoodboxOrderInfo.foodboxAvailability) &&
                Objects.equals(this.ordersInfo, historyFoodboxOrderInfo.ordersInfo) &&
                Objects.equals(this.ordersAmount, historyFoodboxOrderInfo.ordersAmount);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(foodboxAvailabilityForEO, foodboxAvailability, ordersInfo, ordersAmount);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class HistoryFoodboxOrderInfo {\n");

        sb.append("    foodboxAvailabilityForEO: ").append(toIndentedString(foodboxAvailabilityForEO)).append("\n");
        sb.append("    foodboxAvailability: ").append(toIndentedString(foodboxAvailability)).append("\n");
        sb.append("    ordersInfo: ").append(toIndentedString(ordersInfo)).append("\n");
        sb.append("    ordersAmount: ").append(toIndentedString(ordersAmount)).append("\n");
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