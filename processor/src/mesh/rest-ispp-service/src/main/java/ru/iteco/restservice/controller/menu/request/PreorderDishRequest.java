package ru.iteco.restservice.controller.menu.request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;

public class PreorderDishRequest {
    @NotNull
    @Schema(description = "Номер лицевого счета клиента", example = "13177")
    private Long contractId;

    @Schema(description = "Номер телефона представителя", example = "79033987854")
    private String guardianMobile;

    @Schema(description = "Дата предзаказа в Timestamp (ms)", example = "1623974400000")
    private Long date;

    @Schema(description = "Идентификатор комплекса", example = "854")
    private Long complexId;

    @Schema(description = "Идентификатор предзаказа на комплекса. " +
            "Не заполняется в запросе на редактирование блюд ранее созданного предзаказа", example = "8542")
    private Long preorderId;

    @Schema(description = "Заказываемые блюда с количествами")
    private List<PreorderDishInfo> dishes;

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

    public List<PreorderDishInfo> getDishes() {
        return dishes;
    }

    public void setDishes(List<PreorderDishInfo> dishes) {
        this.dishes = dishes;
    }

    public Long getPreorderId() {
        return preorderId;
    }

    public void setPreorderId(Long preorderId) {
        this.preorderId = preorderId;
    }
}
