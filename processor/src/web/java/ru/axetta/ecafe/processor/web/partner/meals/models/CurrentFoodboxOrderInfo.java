package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Передача данных о текущем заказе
 */
public class CurrentFoodboxOrderInfo {
    private Long isppIdFoodbox = null;
    private String status = null;
    private List<OrderDish> dishes = new ArrayList<OrderDish>();
    private String expiresAt = null;
    private String timeOrder = null;
    private Long orderPrice = null;
    private Long currentBalanceLimit = null;
    private Long currentBalance = null;
    public CurrentFoodboxOrderInfo isppIdFoodbox(Long isppIdFoodbox) {
        this.isppIdFoodbox = isppIdFoodbox;
        return this;
    }



    /**
     * Идентификатор заказа, передаваемый от ИС ПП.
     * @return isppIdFoodbox
     **/
    public Long getIsppIdFoodbox() {
        return isppIdFoodbox;
    }
    public void setIsppIdFoodbox(Long isppIdFoodbox) {
        this.isppIdFoodbox = isppIdFoodbox;
    }
    public CurrentFoodboxOrderInfo status(String status) {
        this.status = status;
        return this;
    }



    /**
     * Статус заказа в системе
     * @return status
     **/
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public CurrentFoodboxOrderInfo dishes(List<OrderDish> dishes) {
        this.dishes = dishes;
        return this;
    }

    public CurrentFoodboxOrderInfo addDishesItem(OrderDish dishesItem) {
        this.dishes.add(dishesItem);
        return this;
    }

    /**
     * Get dishes
     * @return dishes
     **/
    public List<OrderDish> getDishes() {
        if (dishes == null)
            dishes = new ArrayList<>();
        return dishes;
    }
    public void setDishes(List<OrderDish> dishes) {
        this.dishes = dishes;
    }
    public CurrentFoodboxOrderInfo expiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }



    /**
     * Дата и время, до которого клиент может забрать заказ
     * @return expiresAt
     **/
    public String getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }
    public CurrentFoodboxOrderInfo timeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
        return this;
    }



    /**
     * Дата и время создания заказа
     * @return timeOrder
     **/
    public String getTimeOrder() {
        return timeOrder;
    }
    public void setTimeOrder(String timeOrder) {
        this.timeOrder = timeOrder;
    }
    public CurrentFoodboxOrderInfo orderPrice(Long orderPrice) {
        this.orderPrice = orderPrice;
        return this;
    }



    /**
     * Общая стоимость заказа в копейках
     * @return orderPrice
     **/
    public Long getOrderPrice() {
        return orderPrice;
    }
    public void setOrderPrice(Long orderPrice) {
        this.orderPrice = orderPrice;
    }
    public CurrentFoodboxOrderInfo currentBalanceLimit(Long currentBalanceLimit) {
        this.currentBalanceLimit = currentBalanceLimit;
        return this;
    }



    /**
     * Лимит дневных трат
     * @return currentBalanceLimit
     **/
    public Long getCurrentBalanceLimit() {
        return currentBalanceLimit;
    }
    public void setCurrentBalanceLimit(Long currentBalanceLimit) {
        this.currentBalanceLimit = currentBalanceLimit;
    }
    public CurrentFoodboxOrderInfo currentBalance(Long currentBalance) {
        this.currentBalance = currentBalance;
        return this;
    }



    /**
     * Остаток денежных средств
     * @return currentBalance
     **/
    public Long getCurrentBalance() {
        return currentBalance;
    }
    public void setCurrentBalance(Long currentBalance) {
        this.currentBalance = currentBalance;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CurrentFoodboxOrderInfo currentFoodboxOrderInfo = (CurrentFoodboxOrderInfo) o;
        return Objects.equals(this.isppIdFoodbox, currentFoodboxOrderInfo.isppIdFoodbox) &&
                Objects.equals(this.status, currentFoodboxOrderInfo.status) &&
                Objects.equals(this.dishes, currentFoodboxOrderInfo.dishes) &&
                Objects.equals(this.expiresAt, currentFoodboxOrderInfo.expiresAt) &&
                Objects.equals(this.timeOrder, currentFoodboxOrderInfo.timeOrder) &&
                Objects.equals(this.orderPrice, currentFoodboxOrderInfo.orderPrice) &&
                Objects.equals(this.currentBalanceLimit, currentFoodboxOrderInfo.currentBalanceLimit) &&
                Objects.equals(this.currentBalance, currentFoodboxOrderInfo.currentBalance);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(isppIdFoodbox, status, dishes, expiresAt, timeOrder, orderPrice, currentBalanceLimit, currentBalance);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CurrentFoodboxOrderInfo {\n");

        sb.append("    isppIdFoodbox: ").append(toIndentedString(isppIdFoodbox)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    dishes: ").append(toIndentedString(dishes)).append("\n");
        sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
        sb.append("    timeOrder: ").append(toIndentedString(timeOrder)).append("\n");
        sb.append("    orderPrice: ").append(toIndentedString(orderPrice)).append("\n");
        sb.append("    currentBalanceLimit: ").append(toIndentedString(currentBalanceLimit)).append("\n");
        sb.append("    currentBalance: ").append(toIndentedString(currentBalance)).append("\n");
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
