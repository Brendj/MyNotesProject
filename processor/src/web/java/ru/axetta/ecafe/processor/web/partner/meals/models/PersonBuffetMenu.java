package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Персональное меню
 */
public class PersonBuffetMenu {
    private List<PersonBuffetMenuBuffetCategoriesItem> categories = null;
    private Boolean buffetIsOpen = null;
    private String buffetOpenAt = null;
    private String buffetCloseAt = null;


    /**
     * Get buffetCategoriesItem
     * @return buffetCategoriesItem
     **/
    public List<PersonBuffetMenuBuffetCategoriesItem> getCategories() {
        if (categories == null)
            categories = new ArrayList<>();
        return categories;
    }

    public void setCategories(List<PersonBuffetMenuBuffetCategoriesItem> categories) {
        this.categories = categories;
    }



    /**
     * Признак работы буфета, открыт или закрыт
     * @return buffetIsOpen
     **/
    public Boolean isBuffetIsOpen() {
        return buffetIsOpen;
    }
    public void setBuffetIsOpen(Boolean buffetIsOpen) {
        this.buffetIsOpen = buffetIsOpen;
    }
    public PersonBuffetMenu buffetOpenTime(String buffetOpenTime) {
        this.buffetOpenAt = buffetOpenTime;
        return this;
    }



    /**
     * Дата начала работы буфета
     * @return buffetOpenTime
     **/
    public String getBuffetOpenAt() {
        return buffetOpenAt;
    }
    public void setBuffetOpenAt(String buffetOpenAt) {
        this.buffetOpenAt = buffetOpenAt;
    }
    public PersonBuffetMenu buffetCloseTime(String buffetCloseTime) {
        this.buffetCloseAt = buffetCloseTime;
        return this;
    }



    /**
     * Дата завершения работы буфета
     * @return buffetCloseTime
     **/
    public String getBuffetCloseAt() {
        return buffetCloseAt;
    }
    public void setBuffetCloseAt(String buffetCloseAt) {
        this.buffetCloseAt = buffetCloseAt;
    }
    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PersonBuffetMenu personBuffetMenu = (PersonBuffetMenu) o;
        return Objects.equals(this.categories, personBuffetMenu.categories) &&
                Objects.equals(this.buffetIsOpen, personBuffetMenu.buffetIsOpen) &&
                Objects.equals(this.buffetOpenAt, personBuffetMenu.buffetOpenAt) &&
                Objects.equals(this.buffetCloseAt, personBuffetMenu.buffetCloseAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(categories, buffetIsOpen, buffetOpenAt, buffetCloseAt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PersonBuffetMenu {\n");

        sb.append("    buffetCategoriesItem: ").append(toIndentedString(categories)).append("\n");
        sb.append("    buffetIsOpen: ").append(toIndentedString(buffetIsOpen)).append("\n");
        sb.append("    buffetOpenTime: ").append(toIndentedString(buffetOpenAt)).append("\n");
        sb.append("    buffetCloseTime: ").append(toIndentedString(buffetCloseAt)).append("\n");
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
