package ru.axetta.ecafe.processor.web.partner.meals.models;
import ru.axetta.ecafe.processor.web.partner.meals.MealsController;

import java.util.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Передача данных о заказе (в контексте списка).
 */
public class FoodboxOrderInfo implements Comparable<FoodboxOrderInfo> {
    private Long isppIdFoodbox = null;
    private String status = null;
    private String expiresAt = null;
    private String timeOrder = null;
    private List<OrderDish> dishes = new ArrayList<OrderDish>();
    private Long orderPrice = null;

    /**
     * Идентификатор заказа.
     * @return id
     **/
    public Long getIsppIdFoodbox() {
        return isppIdFoodbox;
    }

    public void setIsppIdFoodbox(Long isppIdFoodbox) {
        this.isppIdFoodbox = isppIdFoodbox;
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
    public FoodboxOrderInfo expiresAt(String expiresAt) {
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
    public FoodboxOrderInfo timeOrder(String timeOrder) {
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
        return Objects.equals(this.isppIdFoodbox, foodboxOrderInfo.isppIdFoodbox) &&
                Objects.equals(this.status, foodboxOrderInfo.status) &&
                Objects.equals(this.expiresAt, foodboxOrderInfo.expiresAt) &&
                Objects.equals(this.timeOrder, foodboxOrderInfo.timeOrder) &&
                Objects.equals(this.dishes, foodboxOrderInfo.dishes) &&
                Objects.equals(this.orderPrice, foodboxOrderInfo.orderPrice);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(isppIdFoodbox, status, expiresAt, timeOrder, dishes, orderPrice);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FoodboxOrderInfo {\n");

        sb.append("    id: ").append(toIndentedString(isppIdFoodbox)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    expiresAt: ").append(toIndentedString(expiresAt)).append("\n");
        sb.append("    timeOrder: ").append(toIndentedString(timeOrder)).append("\n");
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


    @Override
    public int compareTo(FoodboxOrderInfo o) {
        try {
            if (MealsController.simpleDateFormat.parse(this.getTimeOrder()).after(MealsController.simpleDateFormat.parse(o.getTimeOrder())))
                return -1;
            else
                return 1;
        } catch (Exception e)
        {
            return 0;
        }
    }
}