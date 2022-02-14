package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Cоздание фудбокс-заказа.
 */
public class FoodboxOrder {
    private Long id = null;
    private Date createdAt = null;
    private List<OrderDish> dishes = null;
    private Long orderPrice = null;
    public FoodboxOrder id(Long id) {
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
    public FoodboxOrder createdAt(Date createdAt) {
        this.createdAt = createdAt;
        return this;
    }



    /**
     * Дата и время совершения заказа.
     * @return createdAt
     **/
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public FoodboxOrder dishes(List<OrderDish> dishes) {
        this.dishes = dishes;
        return this;
    }

    public FoodboxOrder addDishesItem(OrderDish dishesItem) {
        if (this.dishes == null) {
            this.dishes = new ArrayList<OrderDish>();
        }
        this.dishes.add(dishesItem);
        return this;
    }

    /**
     * Список блюд в заказе.
     * @return dishes
     **/
    public List<OrderDish> getDishes() {
        return dishes;
    }
    public void setDishes(List<OrderDish> dishes) {
        this.dishes = dishes;
    }
    public FoodboxOrder orderPrice(Long orderPrice) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FoodboxOrder foodboxOrder = (FoodboxOrder) o;
        return Objects.equals(this.id, foodboxOrder.id) &&
                Objects.equals(this.createdAt, foodboxOrder.createdAt) &&
                Objects.equals(this.dishes, foodboxOrder.dishes) &&
                Objects.equals(this.orderPrice, foodboxOrder.orderPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, dishes, orderPrice);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FoodboxOrder {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    createdAt: ").append(toIndentedString(createdAt)).append("\n");
        sb.append("    dishes: ").append(toIndentedString(dishes)).append("\n");
        sb.append("    orderPrice: ").append(toIndentedString(orderPrice)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
