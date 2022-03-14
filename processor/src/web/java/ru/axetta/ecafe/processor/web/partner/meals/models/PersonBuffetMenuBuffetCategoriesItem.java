package ru.axetta.ecafe.processor.web.partner.meals.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Список категорий меню
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PersonBuffetMenuBuffetCategoriesItem")
public class PersonBuffetMenuBuffetCategoriesItem {
    private Long id = null;
    private String name = null;
    private List<PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem> subcategories = null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Dish> dishes = null;
    public PersonBuffetMenuBuffetCategoriesItem id(Long id) {
        this.id = id;
        return this;
    }



    /**
     * Идентификатор категории.
     * @return id
     **/
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public PersonBuffetMenuBuffetCategoriesItem name(String name) {
        this.name = name;
        return this;
    }



    /**
     * Название категории.
     * @return name
     **/
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



    /**
     * Get buffetSubcategoriesItem
     * @return buffetSubcategoriesItem
     **/
    public List<PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem> getSubcategories() {
        if (subcategories == null)
            subcategories = new ArrayList<>();
        return subcategories;
    }

    public void setSubcategories(List<PersonBuffetMenuBuffetCategoriesItemBuffetSubcategoriesItem> subcategories) {
        this.subcategories = subcategories;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersonBuffetMenuBuffetCategoriesItem personBuffetMenuBuffetCategoriesItem = (PersonBuffetMenuBuffetCategoriesItem) o;
        return Objects.equals(this.id, personBuffetMenuBuffetCategoriesItem.id) &&
                Objects.equals(this.name, personBuffetMenuBuffetCategoriesItem.name) &&
                Objects.equals(this.subcategories, personBuffetMenuBuffetCategoriesItem.subcategories);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, subcategories);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PersonBuffetMenuBuffetCategoriesItem {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    buffetSubcategoriesItem: ").append(toIndentedString(subcategories)).append("\n");
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

    public List<Dish> getDishes() {
        if (dishes == null)
            dishes = new ArrayList<>();
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }
}

