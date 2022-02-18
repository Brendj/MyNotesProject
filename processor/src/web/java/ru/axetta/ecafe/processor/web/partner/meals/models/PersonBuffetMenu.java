package ru.axetta.ecafe.processor.web.partner.meals.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Персональное меню
 */
public class PersonBuffetMenu {
    private List<PersonBuffetMenuBuffetCategoriesItem> buffetCategoriesItem = null;
    private Long dishesAmount = null;
    private Boolean buffetIsOpen = null;
    private String buffetOpenTime = null;
    private String buffetCloseTime = null;


    /**
     * Get buffetCategoriesItem
     * @return buffetCategoriesItem
     **/
    public List<PersonBuffetMenuBuffetCategoriesItem> getBuffetCategoriesItem() {
        if (buffetCategoriesItem == null)
            buffetCategoriesItem = new ArrayList<>();
        return buffetCategoriesItem;
    }

    public void setBuffetCategoriesItem(List<PersonBuffetMenuBuffetCategoriesItem> buffetCategoriesItem) {
        this.buffetCategoriesItem = buffetCategoriesItem;
    }

    /**
     * Количество блюд
     * @return dishesAmount
     **/
    public Long getDishesAmount() {
        return dishesAmount;
    }
    public void setDishesAmount(Long dishesAmount) {
        this.dishesAmount = dishesAmount;
    }
    public PersonBuffetMenu buffetIsOpen(Boolean buffetIsOpen) {
        this.buffetIsOpen = buffetIsOpen;
        return this;
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
        this.buffetOpenTime = buffetOpenTime;
        return this;
    }



    /**
     * Дата начала работы буфета
     * @return buffetOpenTime
     **/
    public String getBuffetOpenTime() {
        return buffetOpenTime;
    }
    public void setBuffetOpenTime(String buffetOpenTime) {
        this.buffetOpenTime = buffetOpenTime;
    }
    public PersonBuffetMenu buffetCloseTime(String buffetCloseTime) {
        this.buffetCloseTime = buffetCloseTime;
        return this;
    }



    /**
     * Дата завершения работы буфета
     * @return buffetCloseTime
     **/
    public String getBuffetCloseTime() {
        return buffetCloseTime;
    }
    public void setBuffetCloseTime(String buffetCloseTime) {
        this.buffetCloseTime = buffetCloseTime;
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
        return Objects.equals(this.buffetCategoriesItem, personBuffetMenu.buffetCategoriesItem) &&
                Objects.equals(this.dishesAmount, personBuffetMenu.dishesAmount) &&
                Objects.equals(this.buffetIsOpen, personBuffetMenu.buffetIsOpen) &&
                Objects.equals(this.buffetOpenTime, personBuffetMenu.buffetOpenTime) &&
                Objects.equals(this.buffetCloseTime, personBuffetMenu.buffetCloseTime);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(buffetCategoriesItem, dishesAmount, buffetIsOpen, buffetOpenTime, buffetCloseTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PersonBuffetMenu {\n");

        sb.append("    buffetCategoriesItem: ").append(toIndentedString(buffetCategoriesItem)).append("\n");
        sb.append("    dishesAmount: ").append(toIndentedString(dishesAmount)).append("\n");
        sb.append("    buffetIsOpen: ").append(toIndentedString(buffetIsOpen)).append("\n");
        sb.append("    buffetOpenTime: ").append(toIndentedString(buffetOpenTime)).append("\n");
        sb.append("    buffetCloseTime: ").append(toIndentedString(buffetCloseTime)).append("\n");
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
