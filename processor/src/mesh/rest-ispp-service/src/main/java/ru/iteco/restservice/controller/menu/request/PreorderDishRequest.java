package ru.iteco.restservice.controller.menu.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class PreorderDishRequest {
    @Schema(description = "Номер лицевого счета клиента", example = "13177")
    private Long contractId;

    @Schema(description = "Номер телефона представителя", example = "79033987854")
    private String guardianMobile;

    @Schema(description = "Дата предзаказа в Timestamp (ms)", example = "1623974400000")
    private Long date;

    @Schema(description = "Идентификатор комплекса", example = "854")
    private Long complexId;

    @Schema(description = "Идентификатор блюда", example = "8555")
    private Long dishId;

    @Schema(description = "Заказываемое количество блюда", example = "1")
    private Integer amount;

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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
