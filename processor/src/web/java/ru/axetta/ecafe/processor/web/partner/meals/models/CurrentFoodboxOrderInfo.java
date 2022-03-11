package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Передача данных о текущем заказе
 */
public class CurrentFoodboxOrderInfo {
    private Long foodboxOrderId = null;
    private String status = null;
    private List<OrderDish> dishes = new ArrayList<OrderDish>();
    private String expiresAt = null;
    private String createdAt = null;
    private Long totalPrice = null;
    private Long balanceLimit = null;
    private Long balance = null;
    public CurrentFoodboxOrderInfo isppIdFoodbox(Long isppIdFoodbox) {
        this.foodboxOrderId = isppIdFoodbox;
        return this;
    }



    /**
     * Идентификатор заказа, передаваемый от ИС ПП.
     * @return isppIdFoodbox
     **/
    public Long getFoodboxOrderId() {
        return foodboxOrderId;
    }
    public void setFoodboxOrderId(Long foodboxOrderId) {
        this.foodboxOrderId = foodboxOrderId;
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
        this.createdAt = timeOrder;
        return this;
    }



    /**
     * Дата и время создания заказа
     * @return timeOrder
     **/
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public CurrentFoodboxOrderInfo orderPrice(Long orderPrice) {
        this.totalPrice = orderPrice;
        return this;
    }



    /**
     * Общая стоимость заказа в копейках
     * @return orderPrice
     **/
    public Long getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }
    public CurrentFoodboxOrderInfo currentBalanceLimit(Long currentBalanceLimit) {
        this.balanceLimit = currentBalanceLimit;
        return this;
    }



    /**
     * Лимит дневных трат
     * @return currentBalanceLimit
     **/
    public Long getBalanceLimit() {
        return balanceLimit;
    }
    public void setBalanceLimit(Long balanceLimit) {
        this.balanceLimit = balanceLimit;
    }
    public CurrentFoodboxOrderInfo currentBalance(Long currentBalance) {
        this.balance = currentBalance;
        return this;
    }



    /**
     * Остаток денежных средств
     * @return currentBalance
     **/
    public Long getBalance() {
        return balance;
    }
    public void setBalance(Long balance) {
        this.balance = balance;
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
        return Objects.equals(this.foodboxOrderId, currentFoodboxOrderInfo.foodboxOrderId) &&
                Objects.equals(this.status, currentFoodboxOrderInfo.status) &&
                Objects.equals(this.dishes, currentFoodboxOrderInfo.dishes) &&
                Objects.equals(this.expiresAt, currentFoodboxOrderInfo.expiresAt) &&
                Objects.equals(this.createdAt, currentFoodboxOrderInfo.createdAt) &&
                Objects.equals(this.totalPrice, currentFoodboxOrderInfo.totalPrice) &&
                Objects.equals(this.balanceLimit, currentFoodboxOrderInfo.balanceLimit) &&
                Objects.equals(this.balance, currentFoodboxOrderInfo.balance);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(foodboxOrderId, status, dishes, expiresAt, createdAt, totalPrice, balanceLimit, balance);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class CurrentFoodboxOrderInfo {\n");

        sb.append("    isppIdFoodbox: ").append(toIndentedString(foodboxOrderId)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    dishes: ").append(toIndentedString(dishes)).append("\n");
        sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
        sb.append("    timeOrder: ").append(toIndentedString(createdAt)).append("\n");
        sb.append("    orderPrice: ").append(toIndentedString(totalPrice)).append("\n");
        sb.append("    currentBalanceLimit: ").append(toIndentedString(balanceLimit)).append("\n");
        sb.append("    currentBalance: ").append(toIndentedString(balance)).append("\n");
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
