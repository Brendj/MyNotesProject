package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Список подкатегорий меню
 */
public class PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem {
    private Long id = null;
    private String name = null;
    private List<Dish> dishes = null;
    public PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem id(Long id) {
        this.id = id;
        return this;
    }



    /**
     * Идентификатор подкатегории.
     * @return id
     **/
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem name(String name) {
        this.name = name;
        return this;
    }



    /**
     * Название подкатегории.
     * @return name
     **/
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem menuDishesItem(List<Dish> menuDishesItem) {
        this.dishes = menuDishesItem;
        return this;
    }

    public PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem addMenuDishesItemItem(Dish menuDishesItemItem) {
        if (this.dishes == null) {
            this.dishes = new ArrayList<Dish>();
        }
        this.dishes.add(menuDishesItemItem);
        return this;
    }

    /**
     * Список блюд меню.
     * @return menuDishesItem
     **/
    public List<Dish> getDishes() {
        if (dishes == null)
            dishes = new ArrayList<>();
        return dishes;
    }
    public void setDishes(List<Dish> dishes) {
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
        PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem personBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem = (PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem) o;
        return Objects.equals(this.id, personBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem.id) &&
                Objects.equals(this.name, personBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem.name) &&
                Objects.equals(this.dishes, personBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem.dishes);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, dishes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    menuDishesItem: ").append(toIndentedString(dishes)).append("\n");
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
