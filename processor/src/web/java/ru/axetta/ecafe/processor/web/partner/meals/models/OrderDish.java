package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.Objects;

/**
 * Блюдо в заказе.
 */
public class OrderDish {
    private Long dishId = null;
    private Integer amount = null;
    private String name = null;
    private Long price = null;
    private Long buffetCategoryId = null;
    private String buffetCategoryName = null;
    public OrderDish dishId(Long dishId) {
        this.dishId = dishId;
        return this;
    }



    /**
     * Идентификатор блюда.
     * @return dishId
     **/
    public Long getDishId() {
        return dishId;
    }
    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }
    public OrderDish amount(Integer amount) {
        this.amount = amount;
        return this;
    }



    /**
     * Заказываемое количество блюда.
     * @return amount
     **/
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    public OrderDish name(String name) {
        this.name = name;
        return this;
    }



    /**
     * Название блюда.
     * @return name
     **/
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public OrderDish price(Long price) {
        this.price = price;
        return this;
    }



    /**
     * Цена блюда в копейках.
     * @return price
     **/
    public Long getPrice() {
        return price;
    }
    public void setPrice(Long price) {
        this.price = price;
    }
    public OrderDish buffetCategoriesId(Long buffetCategoriesId) {
        this.buffetCategoryId = buffetCategoriesId;
        return this;
    }



    /**
     * Идентификатор категории.
     * @return buffetCategoriesId
     **/
    public Long getBuffetCategoryId() {
        return buffetCategoryId;
    }
    public void setBuffetCategoryId(Long buffetCategoryId) {
        this.buffetCategoryId = buffetCategoryId;
    }
    public OrderDish buffetCategoriesName(String buffetCategoriesName) {
        this.buffetCategoryName = buffetCategoriesName;
        return this;
    }



    /**
     * Название категории.
     * @return buffetCategoriesName
     **/
    public String getBuffetCategoryName() {
        return buffetCategoryName;
    }
    public void setBuffetCategoryName(String buffetCategoryName) {
        this.buffetCategoryName = buffetCategoryName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderDish orderDish = (OrderDish) o;
        return Objects.equals(this.dishId, orderDish.dishId) &&
                Objects.equals(this.amount, orderDish.amount) &&
                Objects.equals(this.name, orderDish.name) &&
                Objects.equals(this.price, orderDish.price) &&
                Objects.equals(this.buffetCategoryId, orderDish.buffetCategoryId) &&
                Objects.equals(this.buffetCategoryName, orderDish.buffetCategoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishId, amount, name, price, buffetCategoryId, buffetCategoryName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class OrderDish {\n");

        sb.append("    dishId: ").append(toIndentedString(dishId)).append("\n");
        sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    price: ").append(toIndentedString(price)).append("\n");
        sb.append("    buffetCategoriesId: ").append(toIndentedString(buffetCategoryId)).append("\n");
        sb.append("    buffetCategoriesName: ").append(toIndentedString(buffetCategoryName)).append("\n");
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