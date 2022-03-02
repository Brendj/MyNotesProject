package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cоздание фудбокс-заказа.
 */
public class FoodboxOrder {
    private String meshIdFoodbox = null;
    private List<OrderDish> dishes = new ArrayList<OrderDish>();
    public FoodboxOrder meshIdFoodbox(String meshIdFoodbox) {
        this.meshIdFoodbox = meshIdFoodbox;
        return this;
    }



    /**
     * Идентификатор Фудбокс-заказа, генерируемый в МП.
     * @return meshIdFoodbox
     **/
    public String getMeshIdFoodbox() {
        return meshIdFoodbox;
    }
    public void setMeshIdFoodbox(String meshIdFoodbox) {
        this.meshIdFoodbox = meshIdFoodbox;
    }
    public FoodboxOrder dishes(List<OrderDish> dishes) {
        this.dishes = dishes;
        return this;
    }

    public FoodboxOrder addDishesItem(OrderDish dishesItem) {
        this.dishes.add(dishesItem);
        return this;
    }

    /**
     * Список блюд в заказе.
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
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FoodboxOrder foodboxOrder = (FoodboxOrder) o;
        return Objects.equals(this.meshIdFoodbox, foodboxOrder.meshIdFoodbox) &&
                Objects.equals(this.dishes, foodboxOrder.dishes);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(meshIdFoodbox, dishes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class FoodboxOrder {\n");

        sb.append("    meshIdFoodbox: ").append(toIndentedString(meshIdFoodbox)).append("\n");
        sb.append("    dishes: ").append(toIndentedString(dishes)).append("\n");
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