package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * История заказов или получение данных по конкретному заказу фудбокса
 */
public class HistoryFoodboxOrderInfo {
    private List<FoodboxOrderInfo> info = new ArrayList<FoodboxOrderInfo>();
    private Long orders = null;


    public HistoryFoodboxOrderInfo addOrdersInfoItem(FoodboxOrderInfo ordersInfoItem) {
        this.info.add(ordersInfoItem);
        return this;
    }

    /**
     * Get ordersInfo
     * @return ordersInfo
     **/
    public List<FoodboxOrderInfo> getInfo() {
        if (info == null)
            info = new ArrayList<>();
        return info;
    }
    public void setInfo(List<FoodboxOrderInfo> info) {
        this.info = info;
    }
    public HistoryFoodboxOrderInfo ordersAmount(Long ordersAmount) {
        this.orders = ordersAmount;
        return this;
    }



    /**
     * Количество заказов в списке
     * @return ordersAmount
     **/
    public Long getOrders() {
        return orders;
    }
    public void setOrders(Long orders) {
        this.orders = orders;
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
        return  Objects.equals(this.info, historyFoodboxOrderInfo.info) &&
                Objects.equals(this.orders, historyFoodboxOrderInfo.orders);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(info, orders);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class HistoryFoodboxOrderInfo {\n");

        sb.append("    ordersInfo: ").append(toIndentedString(info)).append("\n");
        sb.append("    ordersAmount: ").append(toIndentedString(orders)).append("\n");
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