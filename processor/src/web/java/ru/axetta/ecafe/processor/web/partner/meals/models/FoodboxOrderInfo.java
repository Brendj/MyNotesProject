package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.Date;
import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Передача данных о заказе (в контексте списка).
 */
public class FoodboxOrderInfo {
    private Long id = null;
    private String status = null;
    private Date expiresAt = null;
    private Date timeOrder = null;
    private Date issueTime = null;
    private Long foodboxOrderNumber = null;
    private List<OrderDish> dishes = new ArrayList<OrderDish>();
    private Long orderPrice = null;
    public FoodboxOrderInfo id(Long id) {
        this.id = id;
        return this;
    }



    /**
     * Идентификатор заказа.
     * @return id
     **/
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public FoodboxOrderInfo status(String status) {
        this.status = status;
        return this;
    }



    /**
     * Статус заказа в системе. При запросе списка заказов передавать статусы всех заказов, в т.ч. и текущих
     * @return status
     **/
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public FoodboxOrderInfo expiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }



    /**
     * Дата и время, до которого клиент может забрать заказ
     * @return expiresAt
     **/
    public Date getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
    public FoodboxOrderInfo timeOrder(Date timeOrder) {
        this.timeOrder = timeOrder;
        return this;
    }



    /**
     * Дата и время создания заказа
     * @return timeOrder
     **/
    public Date getTimeOrder() {
        return timeOrder;
    }
    public void setTimeOrder(Date timeOrder) {
        this.timeOrder = timeOrder;
    }
    public FoodboxOrderInfo issueTime(Date issueTime) {
        this.issueTime = issueTime;
        return this;
    }



    /**
     * Дата и время получения заказа. Параметр обязателен для выданных заказов
     * @return issueTime
     **/
    public Date getIssueTime() {
        return issueTime;
    }
    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }
    public FoodboxOrderInfo foodboxOrderNumber(Long foodboxOrderNumber) {
        this.foodboxOrderNumber = foodboxOrderNumber;
        return this;
    }



    /**
     * Номер фудбокс-заказа
     * @return foodboxOrderNumber
     **/
    public Long getFoodboxOrderNumber() {
        return foodboxOrderNumber;
    }
    public void setFoodboxOrderNumber(Long foodboxOrderNumber) {
        this.foodboxOrderNumber = foodboxOrderNumber;
    }
    public FoodboxOrderInfo dishes(List<OrderDish> dishes) {
        this.dishes = dishes;
        return this;
    }

    public FoodboxOrderInfo addDishesItem(OrderDish dishesItem) {
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
    public FoodboxOrderInfo orderPrice(Long orderPrice) {
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
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FoodboxOrderInfo foodboxOrderInfo = (FoodboxOrderInfo) o;
        return Objects.equals(this.id, foodboxOrderInfo.id) &&
                Objects.equals(this.status, foodboxOrderInfo.status) &&
                Objects.equals(this.expiresAt, foodboxOrderInfo.expiresAt) &&
                Objects.equals(this.timeOrder, foodboxOrderInfo.timeOrder) &&
                Objects.equals(this.issueTime, foodboxOrderInfo.issueTime) &&
                Objects.equals(this.foodboxOrderNumber, foodboxOrderInfo.foodboxOrderNumber) &&
                Objects.equals(this.dishes, foodboxOrderInfo.dishes) &&
                Objects.equals(this.orderPrice, foodboxOrderInfo.orderPrice);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, status, expiresAt, timeOrder, issueTime, foodboxOrderNumber, dishes, orderPrice);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FoodboxOrderInfo {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
        sb.append("    timeOrder: ").append(toIndentedString(timeOrder)).append("\n");
        sb.append("    issueTime: ").append(toIndentedString(issueTime)).append("\n");
        sb.append("    foodboxOrderNumber: ").append(toIndentedString(foodboxOrderNumber)).append("\n");
        sb.append("    dishes: ").append(toIndentedString(dishes)).append("\n");
        sb.append("    orderPrice: ").append(toIndentedString(orderPrice)).append("\n");
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
