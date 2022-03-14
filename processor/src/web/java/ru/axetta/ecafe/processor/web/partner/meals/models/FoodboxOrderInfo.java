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
    private String expiredAt = null;
    private String createdAt = null;
    private List<OrderDish> dishes = new ArrayList<OrderDish>();
    private Long totalPrice = null;

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
        this.expiredAt = expiresAt;
        return this;
    }



    /**
     * Дата и время, до которого клиент может забрать заказ
     * @return expiresAt
     **/
    public String getExpiredAt() {
        return expiredAt;
    }
    public void setExpiredAt(String expiredAt) {
        this.expiredAt = expiredAt;
    }
    public FoodboxOrderInfo timeOrder(String timeOrder) {
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
                Objects.equals(this.expiredAt, foodboxOrderInfo.expiredAt) &&
                Objects.equals(this.createdAt, foodboxOrderInfo.createdAt) &&
                Objects.equals(this.dishes, foodboxOrderInfo.dishes) &&
                Objects.equals(this.totalPrice, foodboxOrderInfo.totalPrice);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(isppIdFoodbox, status, expiredAt, createdAt, dishes, totalPrice);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FoodboxOrderInfo {\n");

        sb.append("    id: ").append(toIndentedString(isppIdFoodbox)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    expiresAt: ").append(toIndentedString(expiredAt)).append("\n");
        sb.append("    timeOrder: ").append(toIndentedString(createdAt)).append("\n");
        sb.append("    dishes: ").append(toIndentedString(dishes)).append("\n");
        sb.append("    orderPrice: ").append(toIndentedString(totalPrice)).append("\n");
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
            if (MealsController.simpleDateFormat.parse(this.getCreatedAt()).after(MealsController.simpleDateFormat.parse(o.getCreatedAt())))
                return -1;
            else
                return 1;
        } catch (Exception e)
        {
            return 0;
        }
    }
}