package ru.iteco.restservice.controller.menu.responsedto;

import ru.iteco.restservice.model.preorder.RegularPreorder;

import java.util.Date;

public class RegularPreorderDTO {
    private Long regularPreorderId;
    private Long startDate;
    private Long endDate;
    private Integer complexId;
    private Integer amount;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Long dishId;

    public static RegularPreorderDTO build(RegularPreorder regularPreorder) {
        RegularPreorderDTO result = new RegularPreorderDTO();

        result.setRegularPreorderId(regularPreorder.getIdOfRegularPreorder());
        result.setStartDate(regularPreorder.getStartDate().getTime());
        result.setEndDate(regularPreorder.getEndDate().getTime());
        result.setComplexId(regularPreorder.getIdOfComplex());
        result.setAmount(regularPreorder.getAmount());
        result.setMonday(regularPreorder.getMonday().equals(1));
        result.setTuesday(regularPreorder.getTuesday().equals(1));
        result.setWednesday(regularPreorder.getWednesday().equals(1));
        result.setThursday(regularPreorder.getThursday().equals(1));
        result.setFriday(regularPreorder.getFriday().equals(1));
        result.setSaturday(regularPreorder.getSaturday().equals(1));
        result.setDishId(regularPreorder.getIdOfDish());

        return result;
    }

    public Long getRegularPreorderId() {
        return regularPreorderId;
    }

    public void setRegularPreorderId(Long regularPreorderId) {
        this.regularPreorderId = regularPreorderId;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public Integer getComplexId() {
        return complexId;
    }

    public void setComplexId(Integer complexId) {
        this.complexId = complexId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }
}
