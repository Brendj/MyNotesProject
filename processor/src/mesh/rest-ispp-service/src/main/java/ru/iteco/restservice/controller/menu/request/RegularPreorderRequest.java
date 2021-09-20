package ru.iteco.restservice.controller.menu.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by nuc on 10.06.2021.
 */
public class RegularPreorderRequest {
    private Long regularPreorderId;

    @NotNull
    @Schema(description = "Номер лицевого счета клиента", example = "13177")
    private Long contractId;

    @NotNull
    @Schema(description = "Номер телефона представителя", example = "79033987854")
    private String guardianMobile;

    @NotNull
    @Schema(description = "Заказываемое количество в регулярном заказе", example = "1")
    private Integer amount;

    @NotNull
    @Schema(description = "Дата начала действия регуляра в Timestamp (ms)", example = "1623974400000")
    private Long startDate;

    @NotNull
    @Schema(description = "Дата окончания действия регуляра в Timestamp (ms)", example = "1623974400000")
    private Long endDate;

    @NotNull
    @Schema(description = "Идентификатор комплекса", example = "131")
    private Integer complexId;

    @NotNull
    @Schema(description = "Флаг заказа на понедельник", example = "true")
    private Boolean monday;

    @NotNull
    @Schema(description = "Флаг заказа на вторник", example = "true")
    private Boolean tuesday;

    @NotNull
    @Schema(description = "Флаг заказа на среду", example = "true")
    private Boolean wednesday;

    @NotNull
    @Schema(description = "Флаг заказа на четверг", example = "true")
    private Boolean thursday;

    @NotNull
    @Schema(description = "Флаг заказа на пятницу", example = "true")
    private Boolean friday;

    @NotNull
    @Schema(description = "Флаг заказа на субботу", example = "true")
    private Boolean saturday;

    @Schema(description = "Идентификатор блюда. Не заполняется, если создается регуляр на несоставной комплекс", example = "346")
    private Long dishId;

    public boolean enoughDataForCreate() {
        return contractId != null && guardianMobile != null
                && amount != null
                && startDate != null
                && endDate != null
                && complexId != null
                && monday != null
                && tuesday != null
                && wednesday != null
                && thursday != null
                && friday != null
                && saturday != null;
    }

    public boolean enoughDataForEdit() {
        return regularPreorderId != null
                && contractId != null
                && amount != null
                && startDate != null
                && endDate != null
                && monday != null
                && tuesday != null
                && wednesday != null
                && thursday != null
                && friday != null
                && saturday != null;
    }

    public boolean enoughDataForDelete() {
        return regularPreorderId != null
                &&contractId != null;
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

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getGuardianMobile() {
        return guardianMobile;
    }

    public void setGuardianMobile(String guardianMobile) {
        this.guardianMobile = guardianMobile;
    }

    public Long getRegularPreorderId() {
        return regularPreorderId;
    }

    public void setRegularPreorderId(Long regularPreorderId) {
        this.regularPreorderId = regularPreorderId;
    }
}
